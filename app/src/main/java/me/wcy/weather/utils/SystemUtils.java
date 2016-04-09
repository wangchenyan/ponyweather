package me.wcy.weather.utils;

import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.widget.SwipeRefreshLayout;

import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.nostra13.universalimageloader.core.DisplayImageOptions;

import me.wcy.weather.R;

/**
 * @author wcy
 */
public class SystemUtils {

    public static String getVersionName(Context context) {
        String versionName = "1.0";
        try {
            versionName = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionName;
    }

    public static int getVersionCode(Context context) {
        int versionCode = 1;
        try {
            versionCode = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionCode;
    }

    public static void setRefreshingOnCreate(final SwipeRefreshLayout refreshLayout) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                refreshLayout.setRefreshing(true);
            }
        }, 200);
    }

    public static AMapLocationClient initAMapLocation(Context context, AMapLocationListener aMapLocationListener) {
        AMapLocationClient aMapLocationClient = new AMapLocationClient(context.getApplicationContext());
        aMapLocationClient.setLocationListener(aMapLocationListener);
        // 初始化定位参数
        AMapLocationClientOption mLocationOption = new AMapLocationClientOption();
        // 设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        // 设置是否返回地址信息（默认返回地址信息）
        mLocationOption.setNeedAddress(true);
        // 设置是否只定位一次,默认为false
        mLocationOption.setOnceLocation(false);
        // 设置是否强制刷新WIFI，默认为强制刷新
        mLocationOption.setWifiActiveScan(true);
        // 设置是否允许模拟位置,默认为false，不允许模拟位置
        mLocationOption.setMockEnable(true);
        // 设置定位间隔,单位毫秒,默认为2000ms
        mLocationOption.setInterval(2000);
        // 给定位客户端对象设置定位参数
        aMapLocationClient.setLocationOption(mLocationOption);
        return aMapLocationClient;
    }

    public static DisplayImageOptions getDefaultDisplayOption() {
        return new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .showImageForEmptyUri(R.drawable.image_weather_placeholder_small)
                .showImageOnFail(R.drawable.image_weather_placeholder_small)
                .showImageOnLoading(R.drawable.image_weather_placeholder_small)
                .build();
    }
}
