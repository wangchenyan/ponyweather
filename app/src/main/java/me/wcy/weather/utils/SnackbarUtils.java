package me.wcy.weather.utils;

import android.support.design.widget.Snackbar;
import android.view.View;

/**
 * Created by hzwangchenyan on 2016/3/28.
 */
public class SnackbarUtils {

    public static void show(View view, int resId) {
        Snackbar.make(view, resId, Snackbar.LENGTH_SHORT).show();
    }

    public static void show(View view, CharSequence text) {
        Snackbar.make(view, text, Snackbar.LENGTH_SHORT).show();
    }
}
