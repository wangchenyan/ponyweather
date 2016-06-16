package me.wcy.weather.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.text.format.DateUtils;

import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.nostra13.universalimageloader.core.DisplayImageOptions;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import me.wcy.weather.R;
import me.wcy.weather.model.Weather;

@SuppressLint("SimpleDateFormat")
public class SystemUtils {

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

    public static void voiceAnimation(FloatingActionButton fab, boolean start) {
        AnimationDrawable animation = (AnimationDrawable) fab.getDrawable();
        if (start) {
            animation.start();
        } else {
            animation.stop();
            animation.selectDrawable(animation.getNumberOfFrames() - 1);
        }
    }

    public static String voiceText(Context context, Weather weather) {
        StringBuilder sb = new StringBuilder();
        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        if (hour >= 7 && hour < 12) {
            sb.append("上午好");
        } else if (hour < 19) {
            sb.append("下午好");
        } else {
            sb.append("晚上好");
        }
        sb.append("，");
        sb.append(context.getString(R.string.app_name))
                .append("为您播报")
                .append("，");
        sb.append("今天白天到夜间")
                .append(weather.daily_forecast.get(0).cond.txt_d)
                .append("转")
                .append(weather.daily_forecast.get(0).cond.txt_n)
                .append("，");
        sb.append("温度")
                .append(weather.daily_forecast.get(0).tmp.min)
                .append("~")
                .append(weather.daily_forecast.get(0).tmp.max)
                .append("℃")
                .append("，");
        sb.append(weather.daily_forecast.get(0).wind.dir)
                .append(weather.daily_forecast.get(0).wind.sc)
                .append(weather.daily_forecast.get(0).wind.sc.contains("风") ? "" : "级")
                .append("。");
        return sb.toString();
    }

    public static String timeFormat(String source) {
        SimpleDateFormat sourceSdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date now = new Date();
        try {
            Date date = sourceSdf.parse(source);
            if (date.getYear() != now.getYear()) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                return sdf.format(date);
            } else if (date.getMonth() != now.getMonth()) {
                SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm");
                return sdf.format(date);
            } else if (date.getDay() != now.getDay()) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                if (sdf.parse(sdf.format(now)).getTime() - sdf.parse(sdf.format(date)).getTime() == DateUtils.DAY_IN_MILLIS) {
                    SimpleDateFormat sdf2 = new SimpleDateFormat("HH:mm");
                    return "昨天 " + sdf2.format(date);
                } else {
                    SimpleDateFormat sdf2 = new SimpleDateFormat("MM-dd HH:mm");
                    return sdf2.format(date);
                }
            } else {
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                return sdf.format(date);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return source;
    }

    public static String formatCity(String city) {
        return formatCity(city, null);
    }

    public static String formatCity(String city, String area) {
        if (!TextUtils.isEmpty(area) && (area.endsWith("市") || area.endsWith("县"))) {
            if (area.length() > 2) {
                if (area.endsWith("市")) {
                    area = area.substring(0, area.lastIndexOf('市'));
                } else if (area.endsWith("县")) {
                    area = area.substring(0, area.lastIndexOf('县'));
                }
            }
            return area;
        } else {
            return city.replace("市", "")
                    .replace("盟", "");
        }
    }

    public static boolean shouldRefresh(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        int refreshInterval = Integer.valueOf(sp.getString(Extras.KEY_REFRESH_INTERVAL, "1"));
        if (refreshInterval == 0) {
            return false;
        }
        long lastRefreshTime = sp.getLong(Extras.KEY_LAST_REFRESH_TIME, 0);
        long nowTime = System.currentTimeMillis();
        return nowTime - lastRefreshTime >= refreshInterval * DateUtils.HOUR_IN_MILLIS;
    }

    public static void saveRefreshTime(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putLong(Extras.KEY_LAST_REFRESH_TIME, System.currentTimeMillis()).apply();
    }
}
