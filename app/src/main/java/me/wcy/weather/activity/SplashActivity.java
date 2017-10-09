package me.wcy.weather.activity;

import android.content.Intent;
import android.os.Bundle;

import java.util.Calendar;

import me.wcy.weather.R;
import me.wcy.weather.utils.Preferences;

public class SplashActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        checkNightMode();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startWeather();
            }
        }, 1000);
    }

    @Override
    protected boolean supportNightMode() {
        return false;
    }

    private void checkNightMode() {
        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        Preferences.setNightMode(hour < 7 || hour >= 19);
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
