package me.wcy.weather.application;

import android.app.Application;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

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
        ImageLoader.getInstance().init(new ImageLoaderConfiguration.Builder(this)
                .memoryCacheSize(2 * 1024 * 1024)
                .diskCacheSize(50 * 1024 * 1024)
                .build());
    }
}
