package me.wcy.weather.application;

import android.app.Application;

import cn.bmob.v3.Bmob;
import im.fir.sdk.FIR;
import me.wcy.weather.api.ApiKey;

/**
 * Created by wcy on 2016/4/3.
 */
public class WeatherApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Bmob.initialize(this, ApiKey.BMOB_KEY);
        FIR.init(this);
    }
}
