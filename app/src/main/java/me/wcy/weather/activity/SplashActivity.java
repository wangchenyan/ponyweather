package me.wcy.weather.activity;

import android.content.Intent;
import android.os.Bundle;

import me.wcy.weather.R;

public class SplashActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

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
}
