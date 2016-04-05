package me.wcy.weather.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;

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
import me.wcy.weather.adapter.OnItemClickListener;
import me.wcy.weather.adapter.AddCityAdapter;
import me.wcy.weather.model.CityListEntity;
import me.wcy.weather.utils.Extras;
import me.wcy.weather.utils.SnackbarUtils;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class AddCityActivity extends BaseActivity implements View.OnClickListener, AMapLocationListener,
        OnItemClickListener {
    @Bind(R.id.collapsing_toolbar)
    CollapsingToolbarLayout collapsingToolbar;
    @Bind(R.id.rv_city)
    RecyclerView rvCity;
    @Bind(R.id.fab_location)
    FloatingActionButton fabLocation;
    @Bind(R.id.fab_top)
    FloatingActionButton fabTop;
    private ProgressDialog mProgressDialog;
    private List<CityListEntity.CityInfoEntity> mCityList;
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

        initAMapLocation();
        fetchCityList();
    }

    @Override
    protected void setListener() {
        fabLocation.setOnClickListener(this);
        fabTop.setOnClickListener(this);
        mAddCityAdapter.setOnItemClickListener(this);
        rvCity.setOnScrollListener(mScrollListener);
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
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        mProgressDialog.show();
                    }
                })
                .subscribeOn(AndroidSchedulers.mainThread())
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
                        Gson gson = new Gson();
                        return gson.fromJson(s, CityListEntity.class);
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
                        mProgressDialog.cancel();
                        Log.e("fetchCityList", "onError:" + e.getMessage());
                    }

                    @Override
                    public void onNext(List<CityListEntity.CityInfoEntity> cityInfoEntities) {
                        mCityList = cityInfoEntities;
                        showProvinceList();
                    }
                });
    }

    private void showProvinceList() {
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
                        if (mProgressDialog.isShowing()) {
                            mProgressDialog.cancel();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (mProgressDialog.isShowing()) {
                            mProgressDialog.cancel();
                        }
                        Log.e("showProvinceList", "onError:" + e.getMessage());
                    }

                    @Override
                    public void onNext(List<CityListEntity.CityInfoEntity> cityInfoEntities) {
                        mAddCityAdapter.setData(cityInfoEntities, AddCityAdapter.Type.PROVINCE);
                        mAddCityAdapter.notifyDataSetChanged();
                        rvCity.scrollToPosition(0);
                        collapsingToolbar.setTitle(getString(R.string.add_city));
                        currentType = AddCityAdapter.Type.PROVINCE;
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
                        mAddCityAdapter.setData(cityInfoEntities, AddCityAdapter.Type.CITY);
                        mAddCityAdapter.notifyDataSetChanged();
                        rvCity.scrollToPosition(0);
                        collapsingToolbar.setTitle(province);
                        currentType = AddCityAdapter.Type.CITY;
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
                        mAddCityAdapter.setData(cityInfoEntities, AddCityAdapter.Type.AREA);
                        mAddCityAdapter.notifyDataSetChanged();
                        rvCity.scrollToPosition(0);
                        collapsingToolbar.setTitle(city);
                        currentType = AddCityAdapter.Type.AREA;
                    }
                });
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (aMapLocation != null) {
            mProgressDialog.cancel();
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
                SnackbarUtils.show(this, R.string.locate_fail);
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
                mProgressDialog.show();
                // 启动定位
                mLocationClient.startLocation();
                break;
            case R.id.fab_top:
                rvCity.scrollToPosition(0);
                break;
        }
    }

    @Override
    public void onItemClick(View view, Object data) {
        CityListEntity.CityInfoEntity cityInfo = (CityListEntity.CityInfoEntity) data;
        if (currentType == AddCityAdapter.Type.PROVINCE) {
            currentProvince = cityInfo.province;
            showCityList(currentProvince);
        } else if (currentType == AddCityAdapter.Type.CITY) {
            showAreaList(cityInfo.city);
        } else if (currentType == AddCityAdapter.Type.AREA) {
            backToWeather(cityInfo.area);
        }
    }

    private RecyclerView.OnScrollListener mScrollListener = new RecyclerView.OnScrollListener() {
        private boolean isShow = false;

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            int offsetY = recyclerView.computeVerticalScrollOffset();
            int itemHeight = recyclerView.getChildAt(0).getHeight();
            if (offsetY > itemHeight && !isShow) {
                if (fabTop.getVisibility() != View.VISIBLE) {
                    fabTop.setVisibility(View.VISIBLE);
                }
                fabTop.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2)).start();
                isShow = true;
            } else if (offsetY <= itemHeight && isShow) {
                int bottomMargin = ((CoordinatorLayout.LayoutParams) fabTop.getLayoutParams()).bottomMargin;
                fabTop.animate().translationY(fabTop.getHeight() + bottomMargin)
                        .setInterpolator(new AccelerateInterpolator(2))
                        .start();
                isShow = false;
            }
        }
    };

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
        Intent data = new Intent();
        data.putExtra(Extras.CITY, city);
        setResult(RESULT_OK, data);
        finish();
    }

    @Override
    public void onBackPressed() {
        if (currentType == AddCityAdapter.Type.PROVINCE) {
            super.onBackPressed();
        } else if (currentType == AddCityAdapter.Type.CITY) {
            showProvinceList();
        } else if (currentType == AddCityAdapter.Type.AREA) {
            showCityList(currentProvince);
        }
    }

    @Override
    protected void onDestroy() {
        mLocationClient.onDestroy();
        super.onDestroy();
    }
}
