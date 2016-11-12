package me.wcy.weather.api;

import android.content.Context;

import java.lang.reflect.Field;

/**
 * Created by hzwangchenyan on 2016/11/12.
 */
public class Key {
    public static final String FIR_KEY = "FIR_KEY";
    public static final String BMOB_KEY = "BMOB_KEY";
    public static final String HE_KEY = "HE_KEY";
    public static final String BD_TTS_API_KEY = "BD_TTS_API_KEY";
    public static final String BD_TTS_SECRET_KEY = "BD_TTS_SECRET_KEY";

    public static String get(Context context, String keyName) {
        String pkgName = context.getPackageName();
        String className = pkgName + ".api.KeyStore";
        try {
            Class apiKey = Class.forName(className);
            Field field = apiKey.getField(keyName);
            field.setAccessible(true);
            return (String) field.get(null);
        } catch (Exception ignored) {
        }
        return "";
    }
}
