package me.wcy.weather.addcity;

import android.content.Context;
import android.content.res.AssetManager;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import me.wcy.weather.constants.Extras;
import me.wcy.weather.model.CityEntity;
import me.wcy.weather.model.CityInfo;
import me.wcy.weather.utils.ACache;

/**
 * Created by wcy on 2017/12/30.
 */
public class AddCityRepository implements AddCityContract.Model {
    private List<CityEntity> mCityList;

    @Override
    public Observable<CityEntity> getAllCity(Context context) {
        return Observable.just(context)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .map(context1 -> {
                    if (mCityList == null) {
                        String json = readJsonFromAssets(context1.getAssets());
                        mCityList = parseCityList(json);
                    }
                    return mCityList;
                })
                .flatMap(Observable::fromIterable);
    }

    @Override
    public Observable<List<CityEntity>> getProvince(Context context) {
        return getAllCity(context)
                .distinct(CityEntity::getProvince)
                .toSortedList()
                .toObservable()
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<List<CityEntity>> getCity(Context context, final String province) {
        return getAllCity(context)
                .filter(cityEntity -> cityEntity.getProvince().equals(province))
                .distinct(CityEntity::getCity)
                .toSortedList()
                .toObservable()
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<List<CityEntity>> getArea(Context context, final String city) {
        return getAllCity(context)
                .filter(cityEntity -> cityEntity.getCity().equals(city))
                .toSortedList()
                .toObservable()
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<List<CityEntity>> search(Context context, final String keyword) {
        return getAllCity(context)
                .filter(cityEntity -> cityEntity.getArea().contains(keyword))
                .toSortedList()
                .toObservable()
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public List<String> getAddedCity(Context context) {
        List<String> addedCityList = new ArrayList<>();
        ACache cache = ACache.get(context);
        List<CityInfo> cityList = (List<CityInfo>) cache.getAsObject(Extras.CITY_LIST);
        for (CityInfo cityInfo : cityList) {
            if (!cityInfo.isAutoLocate) {
                addedCityList.add(cityInfo.name);
            }
        }
        return addedCityList;
    }

    private String readJsonFromAssets(AssetManager assetManager) {
        try {
            InputStream is = assetManager.open("city.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();

            return new String(buffer);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private List<CityEntity> parseCityList(String json) {
        Gson gson = new Gson();
        JsonParser parser = new JsonParser();
        List<CityEntity> cityList = new ArrayList<>();
        JsonArray jArray = parser.parse(json).getAsJsonArray();
        for (JsonElement obj : jArray) {
            CityEntity cityEntity = gson.fromJson(obj, CityEntity.class);
            cityList.add(cityEntity);
        }
        return cityList;
    }
}
