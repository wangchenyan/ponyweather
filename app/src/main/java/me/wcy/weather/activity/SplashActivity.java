package me.wcy.weather.activity;

import android.content.Intent;
import android.os.Bundle;

import com.amap.api.location.AMapLocationClient;

import me.wcy.weather.R;
import me.wcy.weather.utils.SystemUtils;

public class SplashActivity extends BaseActivity {
    private AMapLocationClient mLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // 申请定位权限
        mLocationClient = SystemUtils.initAMapLocation(this, null);
        startWeather();
    }

    @Override
    protected void setListener() {
    }

    private void startWeather() {
        Intent intent = new Intent(this, WeatherActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        mLocationClient.onDestroy();
        super.onDestroy();
    }
}
