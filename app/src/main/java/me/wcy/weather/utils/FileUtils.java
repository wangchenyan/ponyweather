package me.wcy.weather.utils;

import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import me.wcy.weather.R;

public class FileUtils {

    private static String getCachePath(Context context) {
        return context.getExternalCacheDir().getPath() + File.separator;
    }

    public static String getCameraImagePath(Context context) {
        return getCachePath(context) + context.getString(R.string.camera_file_name);
    }

    public static String getCompressImagePath(Context context) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        return getCachePath(context) + context.getString(R.string.compress_file_name, sdf.format(new Date()));
    }

    public static boolean hasSDCard() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }
}
