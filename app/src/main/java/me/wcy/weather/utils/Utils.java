package me.wcy.weather.utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.WindowManager;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URLEncoder;

/**
 * @author wcy
 */
public class Utils {
    private static final String URL = "http://api.map.baidu.com/telematics/v3/weather?location=%1$s&ak=%2$s&output=%3$s";
    private static final String BAIDU_APP_KEY = "MEMK39Gs9RS2jXyiG3He4VUB";
    private static final String OUTPUT_JSON = "json";
    public static final String STATUS_SUCCESS = "success";

    /**
     * 获取版本号
     *
     * @param context context
     * @return 版本号
     */
    public static String getVersion(Context context) {
        String versionName = "1.0.0";
        try {
            versionName = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return "V " + versionName;
    }

    public static String getUpdateUrl(String city) {
        city = URLEncoder.encode(city);
        return String.format(URL, city, BAIDU_APP_KEY, OUTPUT_JSON);
    }

    /**
     * 获取屏幕高度（除去状态栏）
     */
    public static int getDisplayHeight(Context context) throws Exception {
        // 屏幕高度
        WindowManager manager = ((Activity) context).getWindowManager();
        int displayHeight = manager.getDefaultDisplay().getHeight();
        // 通知栏高度
        Class<?> c = Class.forName("com.android.internal.R$dimen");
        Object obj = c.newInstance();
        Field field = c.getField("status_bar_height");
        int x = Integer.parseInt(field.get(obj).toString());
        int statusBarHeight = context.getResources().getDimensionPixelSize(x);
        return displayHeight - statusBarHeight;
    }

    public static void setRefreshing(SwipeRefreshLayout refreshLayout, boolean refreshing, boolean notify) {
        Class<? extends SwipeRefreshLayout> refreshLayoutClass = refreshLayout.getClass();
        if (refreshLayoutClass != null) {
            try {
                Method setRefreshing = refreshLayoutClass.getDeclaredMethod("setRefreshing", boolean.class, boolean.class);
                setRefreshing.setAccessible(true);
                setRefreshing.invoke(refreshLayout, refreshing, notify);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }
}
