package me.wcy.weather.utils;

import android.content.Context;
import android.content.SharedPreferences;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import me.wcy.weather.R;
import me.wcy.weather.constants.Extras;
import me.wcy.weather.model.Weather;

public class Utils {

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

    public static void voiceAnimation(FloatingActionButton fab, boolean start) {
        AnimationDrawable animation = (AnimationDrawable) fab.getDrawable();
        if (start) {
            animation.start();
        } else {
            animation.stop();
            animation.selectDrawable(animation.getNumberOfFrames() - 1);
        }
    }

    public static String voiceText(Context context, Weather.DailyForecastEntity forecast) {
        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        String time;
        if (hour >= 7 && hour < 12) {
            time = "上午";
        } else if (hour >= 12 && hour < 19) {
            time = "下午";
        } else {
            time = "晚上";
        }

        String weather = forecast.cond.txt_d;
        if (!TextUtils.equals(forecast.cond.txt_d, forecast.cond.txt_n)) {
            weather += "转" + forecast.cond.txt_n;
        }

        String temperature = forecast.tmp.min;
        if (!TextUtils.equals(forecast.tmp.min, forecast.tmp.max)) {
            temperature += "~" + forecast.tmp.max;
        }

        StringBuilder sb = new StringBuilder();
        sb.append(time)
                .append("好")
                .append("，")
                .append(context.getString(R.string.app_name))
                .append("为您播报")
                .append("，")
                .append("今天白天到夜间")
                .append(weather)
                .append("，")
                .append("温度")
                .append(temperature)
                .append("℃")
                .append("，")
                .append(forecast.wind.dir)
                .append(forecast.wind.sc)
                .append(forecast.wind.sc.endsWith("风") ? "" : "级")
                .append("。");
        return sb.toString();
    }

    public static String timeFormat(String source) {
        SimpleDateFormat sourceSdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date now = new Date();
        try {
            Date date = sourceSdf.parse(source);
            if (date.getYear() != now.getYear()) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
                return sdf.format(date);
            } else if (date.getMonth() != now.getMonth()) {
                SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm", Locale.getDefault());
                return sdf.format(date);
            } else if (date.getDay() != now.getDay()) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                if (sdf.parse(sdf.format(now)).getTime() - sdf.parse(sdf.format(date)).getTime() == DateUtils.DAY_IN_MILLIS) {
                    SimpleDateFormat sdf2 = new SimpleDateFormat("HH:mm", Locale.getDefault());
                    return "昨天 " + sdf2.format(date);
                } else {
                    SimpleDateFormat sdf2 = new SimpleDateFormat("MM-dd HH:mm", Locale.getDefault());
                    return sdf2.format(date);
                }
            } else {
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
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
                area = area.substring(0, area.length() - 1);
            }
            return area;
        } else {
            return city.replace("市", "").replace("盟", "");
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
