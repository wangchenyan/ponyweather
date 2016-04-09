package me.wcy.weather.utils;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;

import me.wcy.weather.R;

/**
 * Created by hzwangchenyan on 2016/3/28.
 */
public class ImageUtils {

    public static int getIconByCode(Context context, String code) {
        return context.getResources().getIdentifier("ic_weather_icon_" + code, "drawable", context.getPackageName());
    }

    public static int getWeatherImage(String weather) {
        if (weather.contains("转")) {
            weather = weather.substring(0, weather.indexOf("转"));
        }
        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        if (hour >= 7 && hour < 19) {
            if (weather.contains("晴")) {
                return R.drawable.header_weather_day_sunny;
            }
            if (weather.contains("云") || weather.contains("阴")) {
                return R.drawable.header_weather_day_cloudy;
            }
            if (weather.contains("雨")) {
                return R.drawable.header_weather_day_rain;
            }
            if (weather.contains("雪") || weather.contains("冰雹")) {
                return R.drawable.header_weather_day_snow;
            }
            if (weather.contains("雾") || weather.contains("霾") || weather.contains("沙") || weather.contains("浮尘")) {
                return R.drawable.header_weather_day_fog;
            }
            return R.drawable.header_sunrise;
        } else {
            if (weather.contains("晴")) {
                return R.drawable.header_weather_night_sunny;
            }
            if (weather.contains("云") || weather.contains("阴")) {
                return R.drawable.header_weather_night_cloudy;
            }
            if (weather.contains("雨")) {
                return R.drawable.header_weather_night_rain;
            }
            if (weather.contains("雪") || weather.contains("冰雹")) {
                return R.drawable.header_weather_night_snow;
            }
            if (weather.contains("雾") || weather.contains("霾") || weather.contains("沙") || weather.contains("浮尘")) {
                return R.drawable.header_weather_day_fog;
            }
            return R.drawable.header_sunset;
        }
    }

    public static void pickImage(final Activity activity) {
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            SnackbarUtils.show(activity, "请确认已插入SD卡");
            return;
        }
        String[] items = new String[]{"拍照", "从相册选择"};
        new AlertDialog.Builder(activity)
                .setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                startCamera(activity);
                                break;
                            case 1:
                                startAlbum(activity);
                                break;
                        }
                    }
                })
                .show();
    }

    private static void startCamera(Activity activity) {
        String imagePath = FileUtils.getCameraImagePath(activity);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(imagePath)));
        activity.startActivityForResult(intent, RequestCode.REQUEST_CAMERA);
    }

    private static void startAlbum(Activity activity) {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        activity.startActivityForResult(intent, RequestCode.REQUEST_ALBUM);
    }

    /**
     * 图片自动旋转
     */
    public static Bitmap autoRotate(String path, Bitmap source) {
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (exif == null) {
            return source;
        }
        int ori = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
        if (ori == ExifInterface.ORIENTATION_NORMAL) {
            return source;
        }
        int degree = 0;
        switch (ori) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                degree = 90;
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                degree = 180;
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                degree = 270;
                break;
        }
        // 旋转图片
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    public static String save2File(Context context, Bitmap bitmap) {
        String path = FileUtils.getCutImagePath(context);
        FileOutputStream stream = null;
        Bitmap.CompressFormat format = Bitmap.CompressFormat.JPEG;
        int quality = 90;
        try {
            stream = new FileOutputStream(path);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        if (bitmap.compress(format, quality, stream)) {
            return path;
        }
        return null;
    }
}
