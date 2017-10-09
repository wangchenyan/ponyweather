package me.wcy.weather.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by hzwangchenyan on 2017/10/9.
 */
public class Preferences {
    private static final String KEY_NIGHT_MODE = "key_night_mode";

    private static Context sContext;

    public static void init(Context context) {
        sContext = context.getApplicationContext();
    }

    public static boolean isNightMode() {
        return getSharedPreferences().getBoolean(KEY_NIGHT_MODE, false);
    }

    public static void setNightMode(boolean nightMode) {
        getSharedPreferences().edit().putBoolean(KEY_NIGHT_MODE, nightMode).apply();
    }

    private static SharedPreferences getSharedPreferences() {
        return PreferenceManager.getDefaultSharedPreferences(sContext);
    }
}
