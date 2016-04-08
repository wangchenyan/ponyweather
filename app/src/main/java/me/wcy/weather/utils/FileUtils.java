package me.wcy.weather.utils;

import android.content.Context;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by hzwangchenyan on 2016/4/8.
 */
public class FileUtils {

    private static String getCachePath(Context context) {
        return context.getExternalCacheDir().getPath() + File.separator;
    }

    public static String getCameraImagePath(Context context) {
        return getCachePath(context) + "temp.jpg";
    }

    public static String getCutImagePath(Context context) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
        return getCachePath(context) + "image_" + sdf.format(new Date()) + ".jpg";
    }
}
