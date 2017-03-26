package me.wcy.weather.activity;

import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationListener;
import com.baidu.speechsynthesizer.SpeechSynthesizer;

import java.util.ArrayList;

import me.wcy.weather.R;
import me.wcy.weather.adapter.DailyForecastAdapter;
import me.wcy.weather.adapter.HourlyForecastAdapter;
import me.wcy.weather.adapter.SuggestionAdapter;
import me.wcy.weather.api.Api;
import me.wcy.weather.api.KeyStore;
import me.wcy.weather.application.SpeechListener;
import me.wcy.weather.constants.Extras;
import me.wcy.weather.constants.RequestCode;
import me.wcy.weather.model.CityInfo;
import me.wcy.weather.model.Weather;
import me.wcy.weather.model.WeatherData;
import me.wcy.weather.utils.ACache;
import me.wcy.weather.utils.ImageUtils;
import me.wcy.weather.utils.NetworkUtils;
import me.wcy.weather.utils.SnackbarUtils;
import me.wcy.weather.utils.UpdateUtils;
import me.wcy.weather.utils.Utils;
import me.wcy.weather.utils.binding.Bind;
import me.wcy.weather.utils.permission.PermissionReq;
import me.wcy.weather.utils.permission.PermissionResult;
import me.wcy.weather.utils.permission.Permissions;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.exceptions.Exceptions;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class WeatherActivity extends BaseActivity implements AMapLocationListener
        , NavigationView.OnNavigationItemSelectedListener, SwipeRefreshLayout.OnRefreshListener
        , View.OnClickListener {
    private static final String TAG = "WeatherActivity";
    @Bind(R.id.drawer_layout)
    private DrawerLayout mDrawerLayout;
    @Bind(R.id.navigation_view)
    private NavigationView mNavigationView;
    @Bind(R.id.appbar)
    private AppBarLayout mAppBar;
    @Bind(R.id.collapsing_toolbar)
    private CollapsingToolbarLayout collapsingToolbar;
    @Bind(R.id.iv_weather_image)
    private ImageView ivWeatherImage;
    @Bind(R.id.swipe_refresh_layout)
    private SwipeRefreshLayout mRefreshLayout;
    @Bind(R.id.nested_scroll_view)
    private NestedScrollView mScrollView;
    @Bind(R.id.ll_weather_container)
    private LinearLayout llWeatherContainer;
    @Bind(R.id.iv_icon)
    private ImageView ivWeatherIcon;
    @Bind(R.id.tv_temp)
    private TextView tvTemp;
    @Bind(R.id.tv_max_temp)
    private TextView tvMaxTemp;
    @Bind(R.id.tv_min_temp)
    private TextView tvMinTemp;
    @Bind(R.id.tv_more_info)
    private TextView tvMoreInfo;
    @Bind(R.id.lv_hourly_forecast)
    private ListView lvHourlyForecast;
    @Bind(R.id.lv_daily_forecast)
    private ListView lvDailyForecast;
    @Bind(R.id.lv_suggestion)
    private ListView lvSuggestion;
    @Bind(R.id.fab_speech)
    private FloatingActionButton fabSpeech;
    private ACache mACache;
    private AMapLocationClient mLocationClient;
    private SpeechSynthesizer mSpeechSynthesizer;
    private SpeechListener mSpeechListener;
    private CityInfo mCity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu);
        }

        mACache = ACache.get(getApplicationContext());
        mCity = (CityInfo) mACache.getAsObject(Extras.CITY);

        Utils.voiceAnimation(fabSpeech, false);

        // 首次进入
        if (mCity == null) {
            mCity = new CityInfo("正在定位", true);
        }

        collapsingToolbar.setTitle(mCity.name);
        checkIfRefresh(mCity);
        UpdateUtils.checkUpdate(this);
    }

    @Override
    protected void setListener() {
        mNavigationView.setNavigationItemSelectedListener(this);
        fabSpeech.setOnClickListener(this);
        mRefreshLayout.setOnRefreshListener(this);
    }

    private void checkIfRefresh(CityInfo city) {
        Weather weather = (Weather) mACache.getAsObject(city.name);
        if (weather != null) {
            updateView(weather);
        } else {
            llWeatherContainer.setVisibility(View.GONE);
        }
        if (weather == null || Utils.shouldRefresh(this)) {
            Utils.setRefreshingOnCreate(mRefreshLayout);
            onRefresh();
        }
    }

    private void updateView(Weather weather) {
        ivWeatherImage.setImageResource(ImageUtils.getWeatherImage(weather.now.cond.txt));
        ivWeatherIcon.setImageResource(ImageUtils.getIconByCode(this, weather.now.cond.code));
        tvTemp.setText(getString(R.string.tempC, weather.now.tmp));
        tvMaxTemp.setText(getString(R.string.now_max_temp, weather.daily_forecast.get(0).tmp.max));
        tvMinTemp.setText(getString(R.string.now_min_temp, weather.daily_forecast.get(0).tmp.min));
        StringBuilder sb = new StringBuilder();
        sb.append("体感")
                .append(weather.now.fl)
                .append("°");
        if (weather.aqi != null && !TextUtils.isEmpty(weather.aqi.city.qlty)) {
            sb.append("  ")
                    .append(weather.aqi.city.qlty.contains("污染") ? "" : "空气")
                    .append(weather.aqi.city.qlty);
        }
        sb.append("  ")
                .append(weather.now.wind.dir)
                .append(weather.now.wind.sc)
                .append(weather.now.wind.sc.contains("风") ? "" : "级");
        tvMoreInfo.setText(sb.toString());
        lvHourlyForecast.setAdapter(new HourlyForecastAdapter(weather.hourly_forecast));
        lvDailyForecast.setAdapter(new DailyForecastAdapter(weather.daily_forecast));
        lvSuggestion.setAdapter(new SuggestionAdapter(weather.suggestion));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab_speech:
                speech();
                break;
        }
    }

    @Override
    public void onRefresh() {
        if (mCity.isAutoLocate) {
            locate();
        } else {
            fetchDataFromNetWork(mCity);
        }
    }

    private void locate() {
        PermissionReq.with(this)
                .permissions(Permissions.LOCATION)
                .result(new PermissionResult() {
                    @Override
                    public void onGranted() {
                        if (mLocationClient == null) {
                            mLocationClient = Utils.initAMapLocation(WeatherActivity.this, WeatherActivity.this);
                        }
                        mLocationClient.startLocation();
                    }

                    @Override
                    public void onDenied() {
                        onLocated(null);
                        SnackbarUtils.show(WeatherActivity.this, getString(R.string.no_permission, Permissions.LOCATION_DESC, "获取当前位置"));
                    }
                })
                .request();
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (aMapLocation != null) {
            mLocationClient.stopLocation();
            if (aMapLocation.getErrorCode() == 0 && !TextUtils.isEmpty(aMapLocation.getCity())) {
                // 定位成功回调信息，设置相关消息
                onLocated(Utils.formatCity(aMapLocation.getCity(), aMapLocation.getDistrict()));
            } else {
                // 定位失败
                // 显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
                Log.e("AmapError", "location Error, ErrCode:"
                        + aMapLocation.getErrorCode() + ", errInfo:"
                        + aMapLocation.getErrorInfo());
                onLocated(null);
                SnackbarUtils.show(fabSpeech, R.string.locate_fail);
            }
        }
    }

    private void onLocated(String city) {
        mCity.name = TextUtils.isEmpty(city) ? (TextUtils.equals(mCity.name, "正在定位") ? "北京" : mCity.name) : city;
        cache(mCity);

        collapsingToolbar.setTitle(mCity.name);
        fetchDataFromNetWork(mCity);
    }

    private void fetchDataFromNetWork(final CityInfo city) {
        // HE_KEY是更新天气需要的key，需要从和风天气官网申请后方能更新天气
        Api.getIApi().getWeather(city.name, KeyStore.getKey(KeyStore.HE_KEY))
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .doOnNext(new Action1<WeatherData>() {
                    @Override
                    public void call(WeatherData weatherData) {
                        boolean success = weatherData.weathers.get(0).status.equals("ok");
                        if (!success) {
                            throw Exceptions.propagate(new Throwable(weatherData.weathers.get(0).status));
                        }
                    }
                })
                .map(new Func1<WeatherData, Weather>() {
                    @Override
                    public Weather call(WeatherData weatherData) {
                        return weatherData.weathers.get(0);
                    }
                })
                .doOnNext(new Action1<Weather>() {
                    @Override
                    public void call(Weather weather) {
                        mACache.put(city.name, weather);
                        Utils.saveRefreshTime(WeatherActivity.this);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Weather>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "update weather fail", e);
                        if (NetworkUtils.errorByNetwork(e)) {
                            SnackbarUtils.show(fabSpeech, R.string.network_error);
                        } else {
                            SnackbarUtils.show(fabSpeech, TextUtils.isEmpty(e.getMessage()) ?
                                    "加载失败" : e.getMessage());
                        }
                        mRefreshLayout.setRefreshing(false);
                    }

                    @Override
                    public void onNext(Weather weather) {
                        updateView(weather);
                        llWeatherContainer.setVisibility(View.VISIBLE);
                        SnackbarUtils.show(fabSpeech, R.string.update_tips);
                        mRefreshLayout.setRefreshing(false);
                    }
                });
    }

    private void cache(CityInfo city) {
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

    private void speech() {
        Weather weather = (Weather) mACache.getAsObject(mCity.name);
        if (weather == null) {
            return;
        }
        if (mSpeechSynthesizer == null) {
            mSpeechListener = new SpeechListener(this);
            mSpeechSynthesizer = new SpeechSynthesizer(this, "holder", mSpeechListener);
            mSpeechSynthesizer.setApiKey(KeyStore.getKey(KeyStore.BD_TTS_API_KEY), KeyStore.getKey(KeyStore.BD_TTS_SECRET_KEY));
            mSpeechSynthesizer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        }
        String text = Utils.voiceText(this, weather);
        mSpeechSynthesizer.speak(text);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            mDrawerLayout.openDrawer(GravityCompat.START);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(final MenuItem item) {
        mDrawerLayout.closeDrawers();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                item.setChecked(false);
            }
        }, 500);
        switch (item.getItemId()) {
            case R.id.action_image_weather:
                startImageWeather();
                return true;
            case R.id.action_location:
                startActivityForResult(new Intent(this, ManageCityActivity.class), RequestCode.REQUEST_CODE);
                return true;
            case R.id.action_setting:
                startActivity(new Intent(this, SettingActivity.class));
                break;
            case R.id.action_share:
                share();
                return true;
            case R.id.action_about:
                startActivity(new Intent(this, AboutActivity.class));
                return true;
        }
        return false;
    }

    private void startImageWeather() {
        PermissionReq.with(this)
                .permissions(Permissions.LOCATION)
                .result(new PermissionResult() {
                    @Override
                    public void onGranted() {
                        startActivity(new Intent(WeatherActivity.this, ImageWeatherActivity.class));
                    }

                    @Override
                    public void onDenied() {
                        SnackbarUtils.show(WeatherActivity.this, getString(R.string.no_permission, Permissions.LOCATION_DESC, "打开实景天气"));
                    }
                })
                .request();
    }

    private void share() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_content));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(Intent.createChooser(intent, getString(R.string.share)));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK || data == null) {
            return;
        }

        CityInfo city = (CityInfo) data.getSerializableExtra(Extras.CITY);
        if (mCity.equals(city)) {
            return;
        }

        mCity = city;
        collapsingToolbar.setTitle(mCity.name);
        mScrollView.scrollTo(0, 0);
        mAppBar.setExpanded(true, false);
        llWeatherContainer.setVisibility(View.GONE);
        mRefreshLayout.setRefreshing(true);
        onRefresh();
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawers();
            return;
        }
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        if (mLocationClient != null) {
            mLocationClient.onDestroy();
        }
        if (mSpeechSynthesizer != null) {
            mSpeechSynthesizer.cancel();
            mSpeechListener.release();
        }
        super.onDestroy();
    }
}
