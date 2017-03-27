package me.wcy.weather.activity;

import android.content.Intent;
import android.os.Bundle;

import java.util.Calendar;

import me.wcy.weather.R;
import me.wcy.weather.application.WeatherApplication;

public class SplashActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        initNightMode();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startWeather();
            }
        }, 1000);
    }

    private void initNightMode() {
        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        WeatherApplication.updateNightMode(hour >= 19 || hour < 7);
    }

    private void startWeather() {
        Intent intent = new Intent(this, WeatherActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
    }
}
