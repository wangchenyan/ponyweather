package me.wcy.weather.activity;

import android.content.Intent;
import android.os.Bundle;

import me.wcy.weather.R;

public class SplashActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        mHandler.postDelayed(new Runnable() {

            @Override
            public void run() {
                Intent intent = new Intent();
                intent.setClass(SplashActivity.this, WeatherActivity.class);
                SplashActivity.this.startActivity(intent);
                SplashActivity.this.finish();
            }
        }, 500);
    }

    @Override
    protected void setListener() {
    }
}
