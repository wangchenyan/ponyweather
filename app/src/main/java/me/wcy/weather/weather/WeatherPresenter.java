package me.wcy.weather.weather;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.amap.api.location.AMapLocation;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import me.wcy.weather.R;
import me.wcy.weather.activity.AboutActivity;
import me.wcy.weather.activity.ImageWeatherActivity;
import me.wcy.weather.activity.ManageCityActivity;
import me.wcy.weather.activity.SettingActivity;
import me.wcy.weather.application.Callback;
import me.wcy.weather.application.LocationManager;
import me.wcy.weather.constants.Extras;
import me.wcy.weather.constants.RequestCode;
import me.wcy.weather.model.CityInfo;
import me.wcy.weather.model.Weather;
import me.wcy.weather.utils.NetworkUtils;
import me.wcy.weather.utils.PermissionReq;
import me.wcy.weather.utils.Utils;

/**
 * Created by hzwangchenyan on 2018/1/8.
 */
public class WeatherPresenter implements WeatherContract.Presenter {
    private static final String TAG = "WeatherPresenter";
    private WeatherContract.Model model;
    private WeatherContract.View view;
    private CityInfo mCity;

    public WeatherPresenter(WeatherContract.Model model, WeatherContract.View view) {
        this.model = model;
        this.view = view;
    }

    @Override
    public void onCreate() {
        mCity = model.getCurrentCity();
        // 首次进入
        if (mCity == null) {
            mCity = new CityInfo("正在定位", true);
        }
        view.setTitle(mCity.name);
        view.setSpeechFabAnimation(false);

        Weather weather = model.getWeatherFromCache(mCity);
        if (weather != null) {
            view.showWeather(weather);
        } else {
            view.hideWeatherView();
        }
        if (weather == null || Utils.shouldRefresh(view.getContext())) {
            view.setRefreshing(true);
            onRefresh();
        }
    }

    @Override
    public void onDestroy() {
    }

    @Override
    public void onRefresh() {
        if (mCity.isAutoLocate) {
            locate();
        } else {
            getWeatherFromNet();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK || data == null) {
            return;
        }

        CityInfo city = (CityInfo) data.getSerializableExtra(Extras.CITY);
        if (mCity.equals(city)) {
            return;
        }

        mCity = city;
        view.setTitle(mCity.name);
        view.scrollToTopAndExpand();
        view.hideWeatherView();
        view.setRefreshing(true);
        onRefresh();
    }

    @Override
    public void startImageWeather() {
        PermissionReq.with(view.getActivity())
                .permissions(Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION)
                .result(new PermissionReq.Result() {
                    @Override
                    public void onGranted() {
                        Intent intent = new Intent(view.getActivity(), ImageWeatherActivity.class);
                        view.getActivity().startActivity(intent);
                    }

                    @Override
                    public void onDenied() {
                        view.showSnack("没有位置信息权限，无法打开实景天气！");
                    }
                })
                .request();
    }

    @Override
    public void startManage() {
        Intent intent = new Intent(view.getActivity(), ManageCityActivity.class);
        view.getActivity().startActivityForResult(intent, RequestCode.REQUEST_CODE);
    }

    @Override
    public void startSetting() {
        Intent intent = new Intent(view.getActivity(), SettingActivity.class);
        view.getActivity().startActivity(intent);
    }

    @Override
    public void startAbout() {
        Intent intent = new Intent(view.getActivity(), AboutActivity.class);
        view.getActivity().startActivity(intent);
    }

    @Override
    public void share() {
        Context context = view.getContext();
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, context.getString(R.string.share_content, context.getString(R.string.app_name), context.getString(R.string.about_project_url)));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        view.getContext().startActivity(Intent.createChooser(intent, view.getContext().getString(R.string.share)));
    }

    private void locate() {
        PermissionReq.with(view.getActivity())
                .permissions(Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION)
                .result(new PermissionReq.Result() {
                    @Override
                    public void onGranted() {
                        LocationManager.get().addLocationObserver(mLocationObserver);
                        LocationManager.get().start();
                    }

                    @Override
                    public void onDenied() {
                        onLocated(null);
                        view.showSnack("没有位置信息权限，无法获取当前位置！");
                    }
                })
                .request();
    }

    private Callback<AMapLocation> mLocationObserver = new Callback<AMapLocation>() {
        @Override
        public void onEvent(AMapLocation aMapLocation) {
            LocationManager.get().removeLocationObserver(mLocationObserver);
            String city = null;
            if (aMapLocation.getErrorCode() == 0) {
                city = Utils.formatCity(aMapLocation.getCity(), aMapLocation.getDistrict());
            } else {
                view.showSnack(view.getContext().getString(R.string.locate_fail));
            }
            onLocated(city);
        }
    };

    private void onLocated(String city) {
        if (TextUtils.isEmpty(city) && TextUtils.equals(mCity.name, "正在定位")) {
            mCity.name = "北京";
        } else {
            mCity.name = city;
        }
        model.cacheCity(mCity);
        view.setTitle(mCity.name);
        getWeatherFromNet();
    }

    private void getWeatherFromNet() {
        model.getWeatherFromNet(mCity)
                .subscribe(new Observer<Weather>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(Weather weather) {
                        view.setRefreshing(false);
                        view.showWeather(weather);
                        view.showSnack(view.getContext().getString(R.string.update_tips));
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "update weather fail", e);
                        view.setRefreshing(false);
                        if (NetworkUtils.errorByNetwork(e)) {
                            view.showSnack(view.getContext().getString(R.string.network_error));
                        } else {
                            view.showSnack("update weather error: " + e.getMessage());
                        }
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }
}
