package me.wcy.weather.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationListener;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.exceptions.Exceptions;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;
import me.wcy.weather.R;
import me.wcy.weather.adapter.AddCityAdapter;
import me.wcy.weather.adapter.OnItemClickListener;
import me.wcy.weather.constants.Extras;
import me.wcy.weather.model.CityEntity;
import me.wcy.weather.model.CityInfo;
import me.wcy.weather.utils.ACache;
import me.wcy.weather.utils.PermissionReq;
import me.wcy.weather.utils.SnackbarUtils;
import me.wcy.weather.utils.Utils;
import me.wcy.weather.utils.binding.Bind;

public class AddCityActivity extends BaseActivity implements View.OnClickListener
        , AMapLocationListener, OnItemClickListener, SearchView.OnQueryTextListener {
    private static final String TAG = "AddCityActivity";
    @Bind(R.id.rv_city)
    private RecyclerView rvCity;
    @Bind(R.id.fab_location)
    private FloatingActionButton fabLocation;
    @Bind(R.id.tv_search_tips)
    private TextView tvSearchTips;
    private SearchView mSearchView;
    private ProgressDialog mProgressDialog;
    private List<CityEntity> mCityList;
    private AddCityAdapter mAddCityAdapter;
    private AMapLocationClient mLocationClient;
    private AddCityAdapter.Type currentType = AddCityAdapter.Type.PROVINCE;
    private String currentProvince;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_city);

        mAddCityAdapter = new AddCityAdapter();
        rvCity.setLayoutManager(new LinearLayoutManager(rvCity.getContext()));
        rvCity.setAdapter(mAddCityAdapter);
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setCancelable(false);

        fetchCityList();
    }

    @Override
    protected void setListener() {
        fabLocation.setOnClickListener(this);
        mAddCityAdapter.setOnItemClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_city, menu);
        mSearchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        mSearchView.setMaxWidth(Integer.MAX_VALUE);
        mSearchView.setQueryHint("城市名");
        mSearchView.setOnQueryTextListener(this);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(final String newText) {
        String text = newText.replace(" ", "");
        mSearchView.setTag(text);
        if (TextUtils.isEmpty(text)) {
            tvSearchTips.setVisibility(View.GONE);
            rvCity.setVisibility(View.VISIBLE);
            showProvinceList();
            return true;
        }
        searchCity(text);
        return true;
    }

    private void searchCity(final String text) {
        Observable.fromIterable(mCityList)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        tvSearchTips.setText("正在搜索…");
                        tvSearchTips.setVisibility(View.VISIBLE);
                        rvCity.setVisibility(View.GONE);
                        currentType = AddCityAdapter.Type.SEARCH;
                    }
                })
                .observeOn(Schedulers.io())
                .filter(new Predicate<CityEntity>() {
                    @Override
                    public boolean test(@NonNull CityEntity cityEntity) throws Exception {
                        return cityEntity.getArea().contains(text);
                    }
                })
                .toSortedList()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<List<CityEntity>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                    }

                    @Override
                    public void onSuccess(@NonNull List<CityEntity> cityEntityList) {
                        if (!mSearchView.getTag().equals(text)) {
                            return;
                        }

                        if (cityEntityList.isEmpty()) {
                            tvSearchTips.setText("无匹配城市");
                        } else {
                            tvSearchTips.setVisibility(View.GONE);
                            rvCity.setVisibility(View.VISIBLE);
                            rvCity.scrollToPosition(0);
                            mAddCityAdapter.setDataAndType(cityEntityList, AddCityAdapter.Type.SEARCH);
                            mAddCityAdapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Log.e(TAG, "searchCity error", e);
                        if (!mSearchView.getTag().equals(text)) {
                            return;
                        }
                        tvSearchTips.setText("无匹配城市");
                    }
                });
    }

    private void fetchCityList() {
        AssetManager assetManager = getAssets();
        Observable.just(assetManager)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        mProgressDialog.setMessage(getString(R.string.loading));
                        mProgressDialog.show();
                    }
                })
                .observeOn(Schedulers.io())
                .map(new Function<AssetManager, String>() {
                    @Override
                    public String apply(@NonNull AssetManager assetManager) throws Exception {
                        return readJsonFromAssets(assetManager);
                    }
                })
                .doOnNext(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        if (TextUtils.isEmpty(s)) {
                            throw Exceptions.propagate(new Throwable("read city list failed"));
                        }
                    }
                })
                .map(new Function<String, List<CityEntity>>() {
                    @Override
                    public List<CityEntity> apply(@NonNull String s) throws Exception {
                        return parseCityList(s);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<CityEntity>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                    }

                    @Override
                    public void onNext(@NonNull List<CityEntity> cityEntities) {
                        mCityList = cityEntities;
                        showProvinceList();
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Log.e(TAG, "fetchCityList error", e);
                        mProgressDialog.cancel();
                        finish();
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }

    private void showProvinceList() {
        Observable.fromIterable(mCityList)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .distinct(new Function<CityEntity, String>() {
                    @Override
                    public String apply(@NonNull CityEntity cityEntity) throws Exception {
                        return cityEntity.getProvince();
                    }
                })
                .toSortedList()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<List<CityEntity>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                    }

                    @Override
                    public void onSuccess(@NonNull List<CityEntity> cityEntityList) {
                        if (mProgressDialog.isShowing()) {
                            mProgressDialog.cancel();
                        }
                        rvCity.scrollToPosition(0);
                        mToolbar.setTitle(getString(R.string.add_city));
                        mAddCityAdapter.setDataAndType(cityEntityList, AddCityAdapter.Type.PROVINCE);
                        mAddCityAdapter.notifyDataSetChanged();
                        currentType = AddCityAdapter.Type.PROVINCE;
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        if (mProgressDialog.isShowing()) {
                            mProgressDialog.cancel();
                        }
                        Log.e(TAG, "showProvinceList error" + e.getMessage());
                        finish();
                    }
                });
    }

    private void showCityList(final String province) {
        Observable.fromIterable(mCityList)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .filter(new Predicate<CityEntity>() {
                    @Override
                    public boolean test(@NonNull CityEntity cityEntity) throws Exception {
                        return cityEntity.getProvince().equals(province);
                    }
                })
                .distinct(new Function<CityEntity, String>() {
                    @Override
                    public String apply(@NonNull CityEntity cityEntity) throws Exception {
                        return cityEntity.getCity();
                    }
                })
                .toSortedList()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<List<CityEntity>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                    }

                    @Override
                    public void onSuccess(@NonNull List<CityEntity> cityEntityList) {
                        rvCity.scrollToPosition(0);
                        mToolbar.setTitle(province);
                        mAddCityAdapter.setDataAndType(cityEntityList, AddCityAdapter.Type.CITY);
                        mAddCityAdapter.notifyDataSetChanged();
                        currentType = AddCityAdapter.Type.CITY;
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Log.e(TAG, "showCityList error", e);
                        finish();
                    }
                });
    }

    private void showAreaList(final String city) {
        Observable.fromIterable(mCityList)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .filter(new Predicate<CityEntity>() {
                    @Override
                    public boolean test(@NonNull CityEntity cityEntity) throws Exception {
                        return cityEntity.getCity().equals(city);
                    }
                })
                .toSortedList()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<List<CityEntity>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onSuccess(@NonNull List<CityEntity> cityEntityList) {
                        rvCity.scrollToPosition(0);
                        mToolbar.setTitle(city);
                        mAddCityAdapter.setDataAndType(cityEntityList, AddCityAdapter.Type.AREA);
                        mAddCityAdapter.notifyDataSetChanged();
                        currentType = AddCityAdapter.Type.AREA;
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Log.e(TAG, "showAreaList error", e);
                        finish();
                    }
                });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab_location:
                if (!hasAutoLocate()) {
                    locate();
                } else {
                    SnackbarUtils.show(this, "已添加自动定位");
                }
                break;
        }
    }

    private void locate() {
        mProgressDialog.setMessage(getString(R.string.locating));
        mProgressDialog.show();
        PermissionReq.with(this)
                .permissions(Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION)
                .result(new PermissionReq.Result() {
                    @Override
                    public void onGranted() {
                        if (mLocationClient == null) {
                            mLocationClient = Utils.initAMapLocation(AddCityActivity.this, AddCityActivity.this);
                        }
                        mLocationClient.startLocation();
                    }

                    @Override
                    public void onDenied() {
                        mProgressDialog.cancel();
                        SnackbarUtils.show(AddCityActivity.this, getString(R.string.no_permission, "位置信息", "获取当前位置"));
                    }
                })
                .request();
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (aMapLocation != null) {
            mProgressDialog.cancel();
            mLocationClient.stopLocation();
            if (aMapLocation.getErrorCode() == 0 && !TextUtils.isEmpty(aMapLocation.getCity())) {
                // 定位成功回调信息，设置相关消息
                backToWeather(Utils.formatCity(aMapLocation.getCity(), aMapLocation.getDistrict()), true);
            } else {
                // 定位失败
                // 显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
                Log.e("AmapError", "location Error, ErrCode:"
                        + aMapLocation.getErrorCode() + ", errInfo:"
                        + aMapLocation.getErrorInfo());
                SnackbarUtils.show(this, R.string.locate_fail);
            }
        }
    }

    @Override
    public void onItemClick(View view, Object data) {
        CityEntity cityInfo = (CityEntity) data;
        if (currentType == AddCityAdapter.Type.PROVINCE) {
            currentProvince = cityInfo.getProvince();
            showCityList(currentProvince);
        } else if (currentType == AddCityAdapter.Type.CITY) {
            showAreaList(cityInfo.getCity());
        } else if (currentType == AddCityAdapter.Type.AREA || currentType == AddCityAdapter.Type.SEARCH) {
            backToWeather(cityInfo.getArea(), false);
        }
    }

    private boolean hasAutoLocate() {
        ACache aCache = ACache.get(getApplicationContext());
        ArrayList<CityInfo> cityList = (ArrayList<CityInfo>) aCache.getAsObject(Extras.CITY_LIST);
        for (CityInfo entity : cityList) {
            if (entity.isAutoLocate) {
                return true;
            }
        }
        return false;
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

    private void backToWeather(String name, boolean isAutoLocate) {
        CityInfo city = new CityInfo(name, isAutoLocate);
        Intent data = new Intent();
        data.putExtra(Extras.CITY, city);
        setResult(RESULT_OK, data);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (currentType == AddCityAdapter.Type.PROVINCE || currentType == AddCityAdapter.Type.SEARCH) {
            super.onBackPressed();
        } else if (currentType == AddCityAdapter.Type.CITY) {
            showProvinceList();
        } else if (currentType == AddCityAdapter.Type.AREA) {
            showCityList(currentProvince);
        }
    }

    @Override
    protected void onDestroy() {
        if (mLocationClient != null) {
            mLocationClient.onDestroy();
        }
        super.onDestroy();
    }
}
