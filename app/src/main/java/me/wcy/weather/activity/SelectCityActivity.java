package me.wcy.weather.activity;

import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import butterknife.Bind;
import me.wcy.weather.R;
import me.wcy.weather.adapter.CityListAdapter;
import me.wcy.weather.model.CityListEntity;
import me.wcy.weather.utils.Extras;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class SelectCityActivity extends BaseActivity implements View.OnClickListener, AMapLocationListener, CityListAdapter.OnItemClickListener {
    @Bind(R.id.rv_city)
    RecyclerView rvCity;
    @Bind(R.id.swipe_refresh_layout)
    SwipeRefreshLayout mRefreshLayout;
    @Bind(R.id.fab_location)
    FloatingActionButton fabLocation;
    private List<CityListEntity.CityInfoEntity> mCityList;
    private CityListAdapter mCityListAdapter;
    private CityListAdapter.Type currentType = CityListAdapter.Type.PROVINCE;
    private AMapLocationClient mLocationClient;
    private String currentProvince;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_city);

        mCityListAdapter = new CityListAdapter();
        rvCity.setLayoutManager(new LinearLayoutManager(rvCity.getContext()));
        rvCity.setAdapter(mCityListAdapter);

        initAMapLocation();
        fetchCityList();
    }

    @Override
    protected void setListener() {
        fabLocation.setOnClickListener(this);
        mCityListAdapter.setOnItemClickListener(this);
    }

    private void initAMapLocation() {
        mLocationClient = new AMapLocationClient(getApplicationContext());
        mLocationClient.setLocationListener(this);
        // 初始化定位参数
        AMapLocationClientOption mLocationOption = new AMapLocationClientOption();
        // 设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        // 设置是否返回地址信息（默认返回地址信息）
        mLocationOption.setNeedAddress(true);
        // 设置是否只定位一次,默认为false
        mLocationOption.setOnceLocation(false);
        // 设置是否强制刷新WIFI，默认为强制刷新
        mLocationOption.setWifiActiveScan(true);
        // 设置是否允许模拟位置,默认为false，不允许模拟位置
        mLocationOption.setMockEnable(true);
        // 设置定位间隔,单位毫秒,默认为2000ms
        mLocationOption.setInterval(2000);
        // 给定位客户端对象设置定位参数
        mLocationClient.setLocationOption(mLocationOption);
    }

    private void fetchCityList() {
        AssetManager assetManager = getAssets();
        Observable.just(assetManager)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(new Func1<AssetManager, String>() {
                    @Override
                    public String call(AssetManager assetManager) {
                        return readJsonFromAssets(assetManager);
                    }
                })
                .filter(new Func1<String, Boolean>() {
                    @Override
                    public Boolean call(String s) {
                        return !TextUtils.isEmpty(s);
                    }
                })
                .map(new Func1<String, CityListEntity>() {
                    @Override
                    public CityListEntity call(String s) {
                        return new Gson().fromJson(s, CityListEntity.class);
                    }
                })
                .map(new Func1<CityListEntity, List<CityListEntity.CityInfoEntity>>() {
                    @Override
                    public List<CityListEntity.CityInfoEntity> call(CityListEntity cityListEntity) {
                        return cityListEntity.city;
                    }
                })
                .subscribe(new Subscriber<List<CityListEntity.CityInfoEntity>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("fetchCityList", "onError:" + e.getMessage());
                    }

                    @Override
                    public void onNext(List<CityListEntity.CityInfoEntity> cityInfoEntities) {
                        mCityList = cityInfoEntities;
                        showProvList();
                    }
                });
    }

    private void showProvList() {
        Observable.from(mCityList)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .distinct(new Func1<CityListEntity.CityInfoEntity, String>() {
                    @Override
                    public String call(CityListEntity.CityInfoEntity cityInfoEntity) {
                        return cityInfoEntity.province;
                    }
                })
                .toSortedList()
                .subscribe(new Subscriber<List<CityListEntity.CityInfoEntity>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("showProvList", "onError:" + e.getMessage());
                    }

                    @Override
                    public void onNext(List<CityListEntity.CityInfoEntity> cityInfoEntities) {
                        mCityListAdapter.setData(cityInfoEntities, CityListAdapter.Type.PROVINCE);
                        mCityListAdapter.notifyDataSetChanged();
                        rvCity.scrollToPosition(0);
                        currentType = CityListAdapter.Type.PROVINCE;
                    }
                });
    }

    private void showCityList(final String province) {
        Observable.from(mCityList)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .filter(new Func1<CityListEntity.CityInfoEntity, Boolean>() {
                    @Override
                    public Boolean call(CityListEntity.CityInfoEntity cityInfoEntity) {
                        return cityInfoEntity.province.equals(province);
                    }
                })
                .distinct(new Func1<CityListEntity.CityInfoEntity, String>() {
                    @Override
                    public String call(CityListEntity.CityInfoEntity cityInfoEntity) {
                        return cityInfoEntity.city;
                    }
                })
                .toSortedList()
                .subscribe(new Subscriber<List<CityListEntity.CityInfoEntity>>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("showCityList", "onError:" + e.getMessage());
                    }

                    @Override
                    public void onNext(List<CityListEntity.CityInfoEntity> cityInfoEntities) {
                        mCityListAdapter.setData(cityInfoEntities, CityListAdapter.Type.CITY);
                        mCityListAdapter.notifyDataSetChanged();
                        rvCity.scrollToPosition(0);
                        currentType = CityListAdapter.Type.CITY;
                    }
                });
    }

    private void showAreaList(final String city) {
        Observable.from(mCityList)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .filter(new Func1<CityListEntity.CityInfoEntity, Boolean>() {
                    @Override
                    public Boolean call(CityListEntity.CityInfoEntity cityInfoEntity) {
                        return cityInfoEntity.city.equals(city);
                    }
                })
                .toSortedList()
                .subscribe(new Subscriber<List<CityListEntity.CityInfoEntity>>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("showAreaList", "onError:" + e.getMessage());
                    }

                    @Override
                    public void onNext(List<CityListEntity.CityInfoEntity> cityInfoEntities) {
                        mCityListAdapter.setData(cityInfoEntities, CityListAdapter.Type.AREA);
                        mCityListAdapter.notifyDataSetChanged();
                        rvCity.scrollToPosition(0);
                        currentType = CityListAdapter.Type.AREA;
                    }
                });
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (aMapLocation != null) {
            mLocationClient.stopLocation();
            if (aMapLocation.getErrorCode() == 0) {
                // 定位成功回调信息，设置相关消息
                String area = aMapLocation.getDistrict();
                if (area.endsWith("市") || area.endsWith("县")) {
                    if (area.length() > 2) {
                        area = area.replace("市", "").replace("县", "");
                    }
                    backToWeather(area);
                } else {
                    String city = aMapLocation.getCity().replace("市", "");
                    backToWeather(city);
                }
            } else {
                // 定位失败
                Toast.makeText(this, R.string.locate_fail, Toast.LENGTH_SHORT).show();
                // 显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
                Log.e("AmapError", "location Error, ErrCode:"
                        + aMapLocation.getErrorCode() + ", errInfo:"
                        + aMapLocation.getErrorInfo());
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab_location:
                // 启动定位
                mLocationClient.startLocation();
                break;
        }
    }

    @Override
    public void onItemClick(View view, Object data) {
        CityListEntity.CityInfoEntity cityInfo = (CityListEntity.CityInfoEntity) data;
        if (currentType == CityListAdapter.Type.PROVINCE) {
            currentProvince = cityInfo.province;
            showCityList(currentProvince);
        } else if (currentType == CityListAdapter.Type.CITY) {
            showAreaList(cityInfo.city);
        } else if (currentType == CityListAdapter.Type.AREA) {
            backToWeather(cityInfo.area);
        }
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

    private void backToWeather(String city) {
        Intent intent = new Intent();
        intent.putExtra(Extras.CITY, city);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        if (currentType == CityListAdapter.Type.PROVINCE) {
            super.onBackPressed();
        } else if (currentType == CityListAdapter.Type.CITY) {
            showProvList();
        } else if (currentType == CityListAdapter.Type.AREA) {
            showCityList(currentProvince);
        }
    }
}
