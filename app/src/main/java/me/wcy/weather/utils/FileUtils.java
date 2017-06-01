package me.wcy.weather.utils;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

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

    public static String uriToPath(Context context, Uri uri) {
        String path = null;
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                path = cursor.getString(columnIndex);
            }
            cursor.close();
        }
        return path;
    }
}
