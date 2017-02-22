package me.wcy.weather.api;

import java.lang.reflect.Field;

/**
 * Created by hzwangchenyan on 2016/11/12.
 */
public class KeyStore {
    public static final String FIR_KEY = "FIR_KEY";
    public static final String BMOB_KEY = "BMOB_KEY";
    public static final String HE_KEY = "HE_KEY";
    public static final String BD_TTS_API_KEY = "BD_TTS_API_KEY";
    public static final String BD_TTS_SECRET_KEY = "BD_TTS_SECRET_KEY";

    public static String getKey(String keyName) {
        try {
            String className = KeyStore.class.getPackage().getName() + ".Keys";
            Class apiKey = Class.forName(className);
            Field field = apiKey.getField(keyName);
            field.setAccessible(true);
            return (String) field.get(null);
        } catch (Exception ignored) {
        }
        return "";
    }
}
