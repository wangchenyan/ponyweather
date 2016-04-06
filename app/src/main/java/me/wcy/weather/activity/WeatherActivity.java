package me.wcy.weather.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.Bind;
import me.wcy.weather.R;
import me.wcy.weather.adapter.DailyForecastAdapter;
import me.wcy.weather.adapter.HourlyForecastAdapter;
import me.wcy.weather.adapter.SuggestionAdapter;
import me.wcy.weather.api.Api;
import me.wcy.weather.api.ApiKey;
import me.wcy.weather.model.Weather;
import me.wcy.weather.model.WeatherData;
import me.wcy.weather.utils.ACache;
import me.wcy.weather.utils.Extras;
import me.wcy.weather.utils.ImageUtils;
import me.wcy.weather.utils.NetworkUtils;
import me.wcy.weather.utils.RequestCode;
import me.wcy.weather.utils.SnackbarUtils;
import me.wcy.weather.utils.UpdateUtils;
import me.wcy.weather.utils.Utils;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class WeatherActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener, SwipeRefreshLayout.OnRefreshListener {
    @Bind(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;
    @Bind(R.id.navigation_view)
    NavigationView mNavigationView;
    @Bind(R.id.appbar)
    AppBarLayout mAppBar;
    @Bind(R.id.collapsing_toolbar)
    CollapsingToolbarLayout collapsingToolbar;
    @Bind(R.id.iv_weather_image)
    ImageView ivWeatherImage;
    @Bind(R.id.swipe_refresh_layout)
    SwipeRefreshLayout mRefreshLayout;
    @Bind(R.id.nested_scroll_view)
    NestedScrollView mScrollView;
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
        setContentView(R.layout.activity_weather);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu);
        }

        mACache = ACache.get(getApplicationContext());
        mCity = mACache.getAsString(Extras.CITY);
        if (TextUtils.isEmpty(mCity)) {
            mCity = "北京";
            initCache(mCity);
        }
        collapsingToolbar.setTitle(mCity);

        fetchDataFromCache(mCity);
        UpdateUtils.checkUpdate(this);
    }

    @Override
    protected void setListener() {
        mNavigationView.setNavigationItemSelectedListener(this);
        mRefreshLayout.setOnRefreshListener(this);
    }

    private void fetchDataFromCache(final String city) {
        Weather weather = (Weather) mACache.getAsObject(city);
        if (weather == null) {
            fetchDataFromNetWork(city, false);
        } else {
            updateView(weather);
        }
    }

    private void fetchDataFromNetWork(final String city, final boolean isRefresh) {
        Api.getIApi().getWeather(city, ApiKey.HE_KEY)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        if (!isRefresh) {
                            Utils.setRefreshing(mRefreshLayout, true, false);
                            llWeatherContainer.setVisibility(View.GONE);
                        }
                    }
                })
                .subscribeOn(AndroidSchedulers.mainThread())
                .filter(new Func1<WeatherData, Boolean>() {
                    @Override
                    public Boolean call(final WeatherData weatherData) {
                        boolean success = weatherData.weathers.get(0).status.equals("ok");
                        if (!success) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    SnackbarUtils.show(WeatherActivity.this, weatherData.weathers.get(0).status);
                                }
                            });
                        }
                        return success;
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
                        mACache.put(city, weather, ACache.TIME_HOUR);
                    }
                })
                .subscribe(new Subscriber<Weather>() {
                    @Override
                    public void onCompleted() {
                        mRefreshLayout.setRefreshing(false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (NetworkUtils.errorByNetwork(e)) {
                            SnackbarUtils.show(WeatherActivity.this, R.string.network_error);
                        } else {
                            SnackbarUtils.show(WeatherActivity.this, e.getMessage());
                        }
                        mRefreshLayout.setRefreshing(false);
                    }

                    @Override
                    public void onNext(Weather weather) {
                        updateView(weather);
                        if (llWeatherContainer.getVisibility() == View.GONE) {
                            llWeatherContainer.setVisibility(View.VISIBLE);
                        }
                        SnackbarUtils.show(WeatherActivity.this, R.string.update_tips);
                    }
                });
    }

    private void updateView(Weather weather) {
        ivWeatherImage.setImageResource(ImageUtils.getWeatherImage(weather.now.cond.txt));
        ivWeatherIcon.setImageResource(ImageUtils.getIconByCode(this, weather.now.cond.code));
        tvTemp.setText(getString(R.string.tempC, weather.now.tmp));
        tvMaxTemp.setText(getString(R.string.now_max_temp, weather.daily_forecast.get(0).tmp.max));
        tvMinTemp.setText(getString(R.string.now_min_temp, weather.daily_forecast.get(0).tmp.min));
        StringBuilder sb = new StringBuilder();
        sb.append("体感").append(weather.now.fl).append("°");
        if (weather.aqi != null) {
            sb.append("  ").append(weather.aqi.city.qlty.contains("污染") ? "" : "空气")
                    .append(weather.aqi.city.qlty);
        }
        sb.append("  ").append(weather.now.wind.dir).append(weather.now.wind.sc)
                .append(weather.now.wind.sc.contains("风") ? "" : "级");
        tvMoreInfo.setText(sb.toString());
        lvHourlyForecast.setAdapter(new HourlyForecastAdapter(weather.hourly_forecast));
        lvDailyForecast.setAdapter(new DailyForecastAdapter(weather.daily_forecast));
        lvSuggestion.setAdapter(new SuggestionAdapter(weather.suggestion));
    }

    private void initCache(String city) {
        ArrayList<String> cityList = new ArrayList<>();
        cityList.add(0, city);
        mACache.put(Extras.CITY, city);
        mACache.put(Extras.CITY_LIST, cityList);
    }

    @Override
    public void onRefresh() {
        fetchDataFromNetWork(mCity, true);
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
                startActivity(new Intent(this, ImageWeatherActivity.class));
                return true;
            case R.id.action_location:
                startActivityForResult(new Intent(this, ManageCityActivity.class), RequestCode.REQUEST_CODE);
                return true;
            case R.id.action_share:
                share();
                return true;
            case R.id.action_about:
                startActivity(new Intent(this, AboutActivity.class));
                return true;
        }
        return false;
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
        String city = data.getStringExtra(Extras.CITY);
        if (mCity.equals(city)) {
            return;
        }
        mCity = city;
        collapsingToolbar.setTitle(mCity);
        mScrollView.scrollTo(0, 0);
        mAppBar.setExpanded(true, false);
        fetchDataFromNetWork(mCity, false);
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
