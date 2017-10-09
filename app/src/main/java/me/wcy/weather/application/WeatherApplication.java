package me.wcy.weather.application;

import android.app.Application;

import cn.bmob.v3.Bmob;
import me.wcy.weather.BuildConfig;
import me.wcy.weather.utils.Preferences;
import me.wcy.weather.utils.ScreenUtils;

public class WeatherApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Preferences.init(this);
        ScreenUtils.init(this);
        Bmob.initialize(this, BuildConfig.BMOB_KEY);
    }
}
