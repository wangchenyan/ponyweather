package me.wcy.weather.weather;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import me.wcy.weather.R;
import me.wcy.weather.activity.BaseActivity;
import me.wcy.weather.adapter.DailyForecastAdapter;
import me.wcy.weather.adapter.HourlyForecastAdapter;
import me.wcy.weather.adapter.SuggestionAdapter;
import me.wcy.weather.model.Weather;
import me.wcy.weather.utils.ImageUtils;
import me.wcy.weather.utils.SnackbarUtils;
import me.wcy.weather.utils.Utils;
import me.wcy.weather.utils.binding.Bind;

public class WeatherActivity extends BaseActivity implements WeatherContract.View, NavigationView.OnNavigationItemSelectedListener,
        SwipeRefreshLayout.OnRefreshListener, View.OnClickListener {
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

    private WeatherContract.Presenter presenter;
    private WeatherContract.SpeechPresenter speechPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu);
        }

        WeatherContract.Model model = new WeatherRepository(this);
        presenter = new WeatherPresenter(model, this);
        speechPresenter = new WeatherSpeechPresenter(model, this);

        presenter.onCreate();
        speechPresenter.onCreate();
    }

    @Override
    protected void onDestroy() {
        presenter.onDestroy();
        speechPresenter.onDestroy();
        super.onDestroy();
    }

    @Override
    public void setTitle(CharSequence title) {
        super.setTitle(title);
        collapsingToolbar.setTitle(title);
    }

    @Override
    protected void setListener() {
        mNavigationView.setNavigationItemSelectedListener(this);
        fabSpeech.setOnClickListener(this);
        mRefreshLayout.setOnRefreshListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab_speech:
                speechPresenter.speech();
                break;
        }
    }

    @Override
    public void onRefresh() {
        presenter.onRefresh();
    }

    @Override
    public boolean isDestroy() {
        return isDestroyedCompat();
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public Activity getActivity() {
        return this;
    }

    @Override
    public void showWeather(Weather weather) {
        llWeatherContainer.setVisibility(View.VISIBLE);
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
                .append(weather.now.wind.sc.endsWith("风") ? "" : "级");
        tvMoreInfo.setText(sb.toString());
        lvHourlyForecast.setAdapter(new HourlyForecastAdapter(weather.hourly_forecast));
        lvDailyForecast.setAdapter(new DailyForecastAdapter(weather.daily_forecast));
        lvSuggestion.setAdapter(new SuggestionAdapter(weather.suggestion));
    }

    @Override
    public void hideWeatherView() {
        llWeatherContainer.setVisibility(View.GONE);
    }

    @Override
    public void setRefreshing(boolean refreshing) {
        mRefreshLayout.post(() -> mRefreshLayout.setRefreshing(refreshing));
    }

    @Override
    public void showSnack(CharSequence message) {
        SnackbarUtils.show(fabSpeech, message);
    }

    @Override
    public void setSpeechFabEnable(boolean enable) {
        fabSpeech.setEnabled(enable);
    }

    @Override
    public void setSpeechFabAnimation(boolean start) {
        Utils.voiceAnimation(fabSpeech, start);
    }

    @Override
    public void scrollToTopAndExpand() {
        mScrollView.scrollTo(0, 0);
        mAppBar.setExpanded(true, false);
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
        mHandler.postDelayed(() -> item.setChecked(false), 500);
        switch (item.getItemId()) {
            case R.id.action_image_weather:
                presenter.startImageWeather();
                return true;
            case R.id.action_location:
                presenter.startManage();
                return true;
            case R.id.action_setting:
                presenter.startSetting();
                break;
            case R.id.action_share:
                presenter.share();
                return true;
            case R.id.action_about:
                presenter.startAbout();
                return true;
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        presenter.onActivityResult(requestCode, resultCode, data);
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
