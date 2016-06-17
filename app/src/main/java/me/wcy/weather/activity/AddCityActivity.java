package me.wcy.weather.activity;

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

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import me.wcy.weather.R;
import me.wcy.weather.adapter.AddCityAdapter;
import me.wcy.weather.adapter.OnItemClickListener;
import me.wcy.weather.model.CityEntity;
import me.wcy.weather.model.CityListEntity;
import me.wcy.weather.utils.ACache;
import me.wcy.weather.utils.Extras;
import me.wcy.weather.utils.SnackbarUtils;
import me.wcy.weather.utils.SystemUtils;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class AddCityActivity extends BaseActivity implements View.OnClickListener
        , AMapLocationListener, OnItemClickListener, SearchView.OnQueryTextListener {
    private static final String TAG = "AddCityActivity";
    @Bind(R.id.rv_city)
    RecyclerView rvCity;
    @Bind(R.id.fab_location)
    FloatingActionButton fabLocation;
    @Bind(R.id.tv_search_tips)
    TextView tvSearchTips;
    private SearchView mSearchView;
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

        mLocationClient = SystemUtils.initAMapLocation(this, this);
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
        Observable.from(mCityList)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        tvSearchTips.setText("正在搜索…");
                        tvSearchTips.setVisibility(View.VISIBLE);
                        rvCity.setVisibility(View.GONE);
                        currentType = AddCityAdapter.Type.SEARCH;
                    }
                })
                .subscribeOn(AndroidSchedulers.mainThread())
                .filter(new Func1<CityListEntity.CityInfoEntity, Boolean>() {
                    @Override
                    public Boolean call(CityListEntity.CityInfoEntity cityInfoEntity) {
                        return cityInfoEntity.area.contains(text);
                    }
                })
                .toSortedList()
                .subscribe(new Subscriber<List<CityListEntity.CityInfoEntity>>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "search city error", e);
                        tvSearchTips.setText("无匹配城市");
                    }

                    @Override
                    public void onNext(List<CityListEntity.CityInfoEntity> cityInfoEntities) {
                        if (!mSearchView.getTag().equals(text)) {
                            return;
                        }
                        if (cityInfoEntities.isEmpty()) {
                            tvSearchTips.setText("无匹配城市");
                        } else {
                            tvSearchTips.setVisibility(View.GONE);
                            rvCity.setVisibility(View.VISIBLE);
                            rvCity.scrollToPosition(0);
                            mAddCityAdapter.setDataAndType(cityInfoEntities, AddCityAdapter.Type.SEARCH);
                            mAddCityAdapter.notifyDataSetChanged();
                        }
                    }
                });
    }

    private void fetchCityList() {
        AssetManager assetManager = getAssets();
        Observable.just(assetManager)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        mProgressDialog.setMessage(getString(R.string.loading));
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
                        Log.e(TAG, "fetchCityList", e);
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
                        Log.e(TAG, "showProvinceList" + e.getMessage());
                    }

                    @Override
                    public void onNext(List<CityListEntity.CityInfoEntity> cityInfoEntities) {
                        rvCity.scrollToPosition(0);
                        mToolbar.setTitle(getString(R.string.add_city));
                        mAddCityAdapter.setDataAndType(cityInfoEntities, AddCityAdapter.Type.PROVINCE);
                        mAddCityAdapter.notifyDataSetChanged();
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
                        Log.e(TAG, "showCityList", e);
                    }

                    @Override
                    public void onNext(List<CityListEntity.CityInfoEntity> cityInfoEntities) {
                        rvCity.scrollToPosition(0);
                        mToolbar.setTitle(province);
                        mAddCityAdapter.setDataAndType(cityInfoEntities, AddCityAdapter.Type.CITY);
                        mAddCityAdapter.notifyDataSetChanged();
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
                        Log.e(TAG, "showAreaList", e);
                    }

                    @Override
                    public void onNext(List<CityListEntity.CityInfoEntity> cityInfoEntities) {
                        rvCity.scrollToPosition(0);
                        mToolbar.setTitle(city);
                        mAddCityAdapter.setDataAndType(cityInfoEntities, AddCityAdapter.Type.AREA);
                        mAddCityAdapter.notifyDataSetChanged();
                        currentType = AddCityAdapter.Type.AREA;
                    }
                });
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (aMapLocation != null) {
            mProgressDialog.cancel();
            mLocationClient.stopLocation();
            if (aMapLocation.getErrorCode() == 0 && !TextUtils.isEmpty(aMapLocation.getCity())) {
                // 定位成功回调信息，设置相关消息
                backToWeather(SystemUtils.formatCity(aMapLocation.getCity(), aMapLocation.getDistrict()), true);
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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab_location:
                if (!hasAutoLocate()) {
                    mProgressDialog.setMessage(getString(R.string.locating));
                    mProgressDialog.show();
                    mLocationClient.startLocation();
                } else {
                    SnackbarUtils.show(this, "已添加自动定位");
                }
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
        } else if (currentType == AddCityAdapter.Type.AREA || currentType == AddCityAdapter.Type.SEARCH) {
            backToWeather(cityInfo.area, false);
        }
    }

    private boolean hasAutoLocate() {
        ACache aCache = ACache.get(getApplicationContext());
        ArrayList<CityEntity> cityList = (ArrayList<CityEntity>) aCache.getAsObject(Extras.CITY_LIST);
        for (CityEntity entity : cityList) {
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

    private void backToWeather(String name, boolean isAutoLocate) {
        CityEntity city = new CityEntity(name, isAutoLocate);
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
        mLocationClient.onDestroy();
        super.onDestroy();
    }
}
