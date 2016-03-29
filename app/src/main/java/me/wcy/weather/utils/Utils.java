package me.wcy.weather.utils;

import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.support.v4.widget.SwipeRefreshLayout;

import java.lang.reflect.Method;

/**
 * @author wcy
 */
public class Utils {

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

    public static void setRefreshing(SwipeRefreshLayout refreshLayout, boolean refreshing, boolean notify) {
        Class<? extends SwipeRefreshLayout> refreshLayoutClass = refreshLayout.getClass();
        if (refreshLayoutClass != null) {
            try {
                Method setRefreshing = refreshLayoutClass.getDeclaredMethod("setRefreshing", boolean.class, boolean.class);
                setRefreshing.setAccessible(true);
                setRefreshing.invoke(refreshLayout, refreshing, notify);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
