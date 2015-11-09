package me.wcy.weather.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import me.wcy.weather.R;

@SuppressLint("InlinedApi")
public class Welcome extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {

            @Override
            public void run() {
                Intent intent = new Intent();
                intent.setClass(Welcome.this, WeatherActivity.class);
                Welcome.this.startActivity(intent);
                Welcome.this.finish();
            }
        }, 500);
    }

}
