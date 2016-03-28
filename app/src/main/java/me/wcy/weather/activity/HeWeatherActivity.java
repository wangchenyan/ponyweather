package me.wcy.weather.activity;

import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import butterknife.Bind;
import me.wcy.weather.R;
import me.wcy.weather.adapter.DailyForecastAdapter;
import me.wcy.weather.adapter.HourlyForecastAdapter;
import me.wcy.weather.adapter.SuggestionAdapter;
import me.wcy.weather.api.Api;
import me.wcy.weather.model.HeWeather;
import me.wcy.weather.model.HeWeatherData;
import me.wcy.weather.utils.ACache;
import me.wcy.weather.utils.Extras;
import me.wcy.weather.utils.ImageUtils;
import me.wcy.weather.utils.NetworkUtils;
import me.wcy.weather.utils.SnackbarUtils;
import me.wcy.weather.utils.Utils;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class HeWeatherActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener, SwipeRefreshLayout.OnRefreshListener {
    @Bind(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;
    @Bind(R.id.navigation_view)
    NavigationView mNavigationView;
    @Bind(R.id.iv_weather_image)
    ImageView ivWeatherImage;
    @Bind(R.id.swipe_refresh_layout)
    SwipeRefreshLayout mRefreshLayout;
    @Bind(R.id.ll_weather_container)
    LinearLayout llWeatherContainer;
    @Bind(R.id.iv_icon)
    ImageView ivWeatherIcon;
    @Bind(R.id.tv_temp)
    TextView tvTemp;
    @Bind(R.id.tv_max_temp)
    TextView tvMaxTemp;
    @Bind(R.id.tv_min_temp)
    TextView tvMinTemp;
    @Bind(R.id.tv_more_info)
    TextView tvMoreInfo;
    @Bind(R.id.lv_hourly_forecast)
    ListView lvHourlyForecast;
    @Bind(R.id.lv_daily_forecast)
    ListView lvDailyForecast;
    @Bind(R.id.lv_suggestion)
    ListView lvSuggestion;
    private ACache mACache;
    private String mCity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_he_weather);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu);
        }

        mACache = ACache.get(getApplicationContext());
        mCity = mACache.getAsString(Extras.KEY_CITY);
        mCity = TextUtils.isEmpty(mCity) ? "北京" : mCity;
        setTitle(mCity);

        fetchDataFromCache(mCity);
    }

    @Override
    protected void setListener() {
        mNavigationView.setNavigationItemSelectedListener(this);
        mRefreshLayout.setOnRefreshListener(this);
    }

    private void fetchDataFromCache(final String city) {
        HeWeather heWeather = (HeWeather) mACache.getAsObject(city);
        if (heWeather == null) {
            Utils.setRefreshing(mRefreshLayout, true, true);
            llWeatherContainer.setVisibility(View.GONE);
            fetchDataFromNetWork(city);
        } else {
            updateView(heWeather);
        }
    }

    private void fetchDataFromNetWork(final String city) {
        Api.getIApi().getWeather(city, Api.HE_KEY)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(AndroidSchedulers.mainThread())
                .filter(new Func1<HeWeatherData, Boolean>() {
                    @Override
                    public Boolean call(HeWeatherData heWeatherData) {
                        return heWeatherData.heWeathers != null
                                && !heWeatherData.heWeathers.isEmpty()
                                && heWeatherData.heWeathers.get(0).status.equals("ok");
                    }
                })
                .map(new Func1<HeWeatherData, HeWeather>() {
                    @Override
                    public HeWeather call(HeWeatherData heWeatherData) {
                        return heWeatherData.heWeathers.get(0);
                    }
                })
                .doOnNext(new Action1<HeWeather>() {
                    @Override
                    public void call(HeWeather heWeather) {
                        mACache.put(Extras.KEY_CITY, city);
                        mACache.put(city, heWeather, ACache.TIME_HOUR);
                    }
                })
                .subscribe(new Subscriber<HeWeather>() {
                    @Override
                    public void onCompleted() {
                        if (mRefreshLayout.isRefreshing()) {
                            mRefreshLayout.setRefreshing(false);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("fetchDataFromNetWork", "onError:" + e.getMessage());
                        if (NetworkUtils.errorByNetwork(e)) {
                            SnackbarUtils.show(getWindow().getDecorView().findViewById(android.R.id.content), R.string.network_error);
                        } else {
                            SnackbarUtils.show(getWindow().getDecorView().findViewById(android.R.id.content), e.getMessage());
                        }
                        if (mRefreshLayout.isRefreshing()) {
                            mRefreshLayout.setRefreshing(false);
                        }
                    }

                    @Override
                    public void onNext(HeWeather heWeather) {
                        updateView(heWeather);
                        if (llWeatherContainer.getVisibility() == View.GONE) {
                            llWeatherContainer.setVisibility(View.VISIBLE);
                        }
                        SnackbarUtils.show(getWindow().getDecorView().findViewById(android.R.id.content), R.string.update_tips);
                    }
                });
    }

    private void updateView(HeWeather heWeather) {
        ivWeatherImage.setImageResource(ImageUtils.getWeatherImage(heWeather.now.cond.txt));
        ivWeatherIcon.setImageResource(ImageUtils.getIconByCode(this, heWeather.now.cond.code));
        tvTemp.setText(getString(R.string.tempC, heWeather.now.tmp));
        tvMaxTemp.setText(getString(R.string.now_max_temp, heWeather.daily_forecast.get(0).tmp.max));
        tvMinTemp.setText(getString(R.string.now_min_temp, heWeather.daily_forecast.get(0).tmp.min));
        int moreInfoStr = heWeather.aqi.city.qlty.contains("污染") ? R.string.now_more_info : R.string.now_more_info_with_air;
        tvMoreInfo.setText(getString(moreInfoStr, heWeather.now.fl, heWeather.aqi.city.qlty, heWeather.now.wind.dir, heWeather.now.wind.sc));
        lvHourlyForecast.setAdapter(new HourlyForecastAdapter(heWeather.hourly_forecast));
        lvDailyForecast.setAdapter(new DailyForecastAdapter(heWeather.daily_forecast));
        lvSuggestion.setAdapter(new SuggestionAdapter(heWeather.suggestion));
    }

    @Override
    public void onRefresh() {
        fetchDataFromNetWork(mCity);
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
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                item.setChecked(false);
            }
        }, 500);
        return false;
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawers();
            return;
        }
        super.onBackPressed();
    }
}
