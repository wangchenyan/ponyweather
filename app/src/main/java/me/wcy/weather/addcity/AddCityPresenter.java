package me.wcy.weather.addcity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;

import com.amap.api.location.AMapLocation;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import me.wcy.weather.R;
import me.wcy.weather.application.Callback;
import me.wcy.weather.application.LocationManager;
import me.wcy.weather.constants.CityType;
import me.wcy.weather.constants.Extras;
import me.wcy.weather.model.CityEntity;
import me.wcy.weather.model.CityInfo;
import me.wcy.weather.utils.ACache;
import me.wcy.weather.utils.PermissionReq;
import me.wcy.weather.utils.Utils;

/**
 * Created by hzwangchenyan on 2017/12/28.
 */
public class AddCityPresenter implements AddCityContract.Presenter {
    private AddCityContract.Model model;
    private AddCityContract.View view;
    private CityType currentType;
    private String currentProvince;
    private String keyword;

    public AddCityPresenter(AddCityContract.Model model, AddCityContract.View view) {
        this.model = model;
        this.view = view;
    }

    @Override
    public void onCreate() {
        LocationManager.get().addLocationObserver(mLocationObserver);
    }

    @Override
    public void onDestroy() {
        LocationManager.get().removeLocationObserver(mLocationObserver);
    }

    @Override
    public void showProvince() {
        view.showProgress("加载中…");
        model.getProvince(view.getContext())
                .subscribe(new Observer<List<CityEntity>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(List<CityEntity> cityEntities) {
                        view.cancelProgress();
                        view.setTitle(view.getContext().getString(R.string.add_city));
                        view.showProvince(cityEntities);
                        currentType = CityType.PROVINCE;
                    }

                    @Override
                    public void onError(Throwable e) {
                        view.cancelProgress();
                        view.finish();
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }

    @Override
    public void showCity(final String province) {
        view.showProgress("加载中…");
        model.getCity(view.getContext(), province)
                .subscribe(new Observer<List<CityEntity>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(List<CityEntity> cityEntities) {
                        view.cancelProgress();
                        view.setTitle(province);
                        view.showCity(cityEntities);
                        currentType = CityType.CITY;
                    }

                    @Override
                    public void onError(Throwable e) {
                        view.cancelProgress();
                        view.finish();
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }

    @Override
    public void showArea(final String city) {
        view.showProgress("加载中…");
        model.getArea(view.getContext(), city)
                .subscribe(new Observer<List<CityEntity>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(List<CityEntity> cityEntities) {
                        view.cancelProgress();
                        view.setTitle(city);
                        view.showArea(cityEntities);
                        currentType = CityType.AREA;
                    }

                    @Override
                    public void onError(Throwable e) {
                        view.cancelProgress();
                        view.finish();
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }

    @Override
    public void search(String keyword) {
        keyword = keyword.replace(" ", "");
        this.keyword = keyword;
        if (TextUtils.isEmpty(keyword)) {
            view.cancelSearch();
            showProvince();
            return;
        }

        view.showSearching();
        String finalKeyword = keyword;
        model.search(view.getContext(), keyword)
                .subscribe(new Observer<List<CityEntity>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(List<CityEntity> cityEntities) {
                        if (!AddCityPresenter.this.keyword.equals(finalKeyword)) {
                            return;
                        }
                        if (cityEntities.isEmpty()) {
                            view.showSearchError();
                        } else {
                            view.showSearchSuccess(cityEntities);
                            currentType = CityType.SEARCH;
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (!AddCityPresenter.this.keyword.equals(finalKeyword)) {
                            return;
                        }
                        view.showSearchError();
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }

    @Override
    public void locate() {
        if (hasAutoLocate()) {
            view.showSnack("已添加自动定位");
            return;
        }

        PermissionReq.with(view.getActivity())
                .permissions(Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION)
                .result(new PermissionReq.Result() {
                    @Override
                    public void onGranted() {
                        LocationManager.get().start();
                        view.showProgress(view.getContext().getString(R.string.locating));
                    }

                    @Override
                    public void onDenied() {
                        view.showSnack("没有位置信息权限，无法获取当前位置！");
                    }
                })
                .request();
    }

    @Override
    public void onItemClick(CityEntity cityEntity) {
        if (currentType == CityType.PROVINCE) {
            currentProvince = cityEntity.getProvince();
            showCity(currentProvince);
        } else if (currentType == CityType.CITY) {
            showArea(cityEntity.getCity());
        } else if (currentType == CityType.AREA || currentType == CityType.SEARCH) {
            backToWeather(cityEntity.getArea(), false);
        }
    }

    @Override
    public void onBackPressed() {
        if (currentType == CityType.PROVINCE || currentType == CityType.SEARCH) {
            view.finish();
        } else if (currentType == CityType.CITY) {
            showProvince();
        } else if (currentType == CityType.AREA) {
            showCity(currentProvince);
        }
    }

    @Override
    public CityType getType() {
        return currentType;
    }

    private Callback<AMapLocation> mLocationObserver = new Callback<AMapLocation>() {
        @Override
        public void onEvent(AMapLocation aMapLocation) {
            view.cancelProgress();
            if (aMapLocation.getErrorCode() == 0) {
                backToWeather(Utils.formatCity(aMapLocation.getCity(), aMapLocation.getDistrict()), true);
            } else {
                view.showSnack(view.getContext().getString(R.string.locate_fail));
            }
        }
    };

    private void backToWeather(String name, boolean isAutoLocate) {
        CityInfo city = new CityInfo(name, isAutoLocate);
        Intent data = new Intent();
        data.putExtra(Extras.CITY, city);
        view.getActivity().setResult(Activity.RESULT_OK, data);
        view.finish();
    }

    private boolean hasAutoLocate() {
        ACache aCache = ACache.get(view.getContext());
        ArrayList<CityInfo> cityList = (ArrayList<CityInfo>) aCache.getAsObject(Extras.CITY_LIST);
        for (CityInfo entity : cityList) {
            if (entity.isAutoLocate) {
                return true;
            }
        }
        return false;
    }
}
