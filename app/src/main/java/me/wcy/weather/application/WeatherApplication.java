package me.wcy.weather.application;

import android.app.Application;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.DisplayMetrics;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import cn.bmob.v3.Bmob;
import im.fir.sdk.FIR;
import me.wcy.weather.api.ApiKey;
import me.wcy.weather.utils.ScreenUtils;

/**
 * Created by wcy on 2016/4/3.
 */
public class WeatherApplication extends Application {
    private static Resources sRes;

    @Override
    public void onCreate() {
        super.onCreate();

        sRes = getResources();
        ScreenUtils.init(this);
        Bmob.initialize(this, ApiKey.BMOB_KEY);
        FIR.init(this);
        ImageLoader.getInstance().init(new ImageLoaderConfiguration.Builder(this)
                .memoryCacheSize(2 * 1024 * 1024)
                .diskCacheSize(50 * 1024 * 1024)
                .build());
    }

    public static void updateNightMode(boolean on) {
        DisplayMetrics dm = sRes.getDisplayMetrics();
        Configuration config = sRes.getConfiguration();
        config.uiMode &= ~Configuration.UI_MODE_NIGHT_MASK;
        config.uiMode |= on ? Configuration.UI_MODE_NIGHT_YES : Configuration.UI_MODE_NIGHT_NO;
        sRes.updateConfiguration(config, dm);
    }
}
