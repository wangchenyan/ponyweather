package me.wcy.weather.weather;

import android.content.Context;

import java.util.ArrayList;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.exceptions.Exceptions;
import io.reactivex.schedulers.Schedulers;
import me.wcy.weather.BuildConfig;
import me.wcy.weather.api.Api;
import me.wcy.weather.constants.Extras;
import me.wcy.weather.model.CityInfo;
import me.wcy.weather.model.Weather;
import me.wcy.weather.utils.ACache;
import me.wcy.weather.utils.Utils;

/**
 * Created by hzwangchenyan on 2018/1/8.
 */
public class WeatherRepository implements WeatherContract.Model {
    private Context context;
    private ACache mACache;

    public WeatherRepository(Context context) {
        this.context = context.getApplicationContext();
        mACache = ACache.get(this.context);
    }

    @Override
    public CityInfo getCurrentCity() {
        return (CityInfo) mACache.getAsObject(Extras.CITY);
    }

    @Override
    public Weather getCurrentWeatherFromCache() {
        CityInfo cityInfo = (CityInfo) mACache.getAsObject(Extras.CITY);
        if (cityInfo == null) {
            return null;
        }
        return (Weather) mACache.getAsObject(cityInfo.name);
    }

    @Override
    public Weather getWeatherFromCache(CityInfo cityInfo) {
        return (Weather) mACache.getAsObject(cityInfo.name);
    }

    @Override
    public Observable<Weather> getWeatherFromNet(CityInfo cityInfo) {
        // HE_KEY是更新天气需要的key，需要从和风天气官网申请后方能更新天气
        return Api.getIApi().getWeather(cityInfo.name, BuildConfig.HE_KEY)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .doOnNext(weatherData -> {
                    boolean success = weatherData.weathers.get(0).status.equals("ok");
                    if (!success) {
                        throw Exceptions.propagate(new Throwable(weatherData.weathers.get(0).status));
                    }
                })
                .map(weatherData -> weatherData.weathers.get(0))
                .doOnNext(weather -> {
                    cacheWeather(cityInfo, weather);
                    Utils.saveRefreshTime(context);
                })
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public void cacheCity(CityInfo city) {
        ArrayList<CityInfo> cityList = (ArrayList<CityInfo>) mACache.getAsObject(Extras.CITY_LIST);
        if (cityList == null) {
            cityList = new ArrayList<>();
        }
        CityInfo oldAutoLocate = null;
        for (CityInfo cityInfo : cityList) {
            if (cityInfo.isAutoLocate) {
                oldAutoLocate = cityInfo;
                break;
            }
        }
        if (oldAutoLocate != null) {
            oldAutoLocate.name = city.name;
        } else {
            cityList.add(city);
        }
        mACache.put(Extras.CITY, city);
        mACache.put(Extras.CITY_LIST, cityList);
    }

    @Override
    public void cacheWeather(CityInfo cityInfo, Weather weather) {
        mACache.put(cityInfo.name, weather);
    }
}
