package me.wcy.weather.utils;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;

import java.io.File;
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
                return R.drawable.ic_weather_image_day_sunny;
            }
            if (weather.contains("云") || weather.contains("阴")) {
                return R.drawable.ic_weather_image_day_cloudy;
            }
            if (weather.contains("雨")) {
                return R.drawable.ic_weather_image_day_rain;
            }
            if (weather.contains("雪") || weather.contains("冰雹")) {
                return R.drawable.ic_weather_image_day_snow;
            }
            if (weather.contains("雾") || weather.contains("霾") || weather.contains("沙") || weather.contains("浮尘")) {
                return R.drawable.ic_weather_image_day_fog;
            }
            return R.drawable.ic_sunrise;
        } else {
            if (weather.contains("晴")) {
                return R.drawable.ic_weather_image_night_sunny;
            }
            if (weather.contains("云") || weather.contains("阴")) {
                return R.drawable.ic_weather_image_night_cloudy;
            }
            if (weather.contains("雨")) {
                return R.drawable.ic_weather_image_night_rain;
            }
            if (weather.contains("雪") || weather.contains("冰雹")) {
                return R.drawable.ic_weather_image_night_snow;
            }
            if (weather.contains("雾") || weather.contains("霾") || weather.contains("沙") || weather.contains("浮尘")) {
                return R.drawable.ic_weather_image_day_fog;
            }
            return R.drawable.ic_sunset;
        }
    }

    public static void pickImage(final Activity activity) {
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
        String imagePath = Utils.getCameraImagePath(activity);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(imagePath)));
        activity.startActivityForResult(intent, RequestCode.REQUEST_CAMERA);
    }

    private static void startAlbum(Activity activity) {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        activity.startActivityForResult(intent, RequestCode.REQUEST_ALBUM);
    }
}
