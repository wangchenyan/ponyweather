package me.wcy.weather.utils;

import android.content.Context;
import android.view.WindowManager;

/**
 * Created by hzwangchenyan on 2016/4/8.
 */
public class ScreenUtils {
    private static Context sContext;

    public static void init(Context context) {
        sContext = context.getApplicationContext();
    }

    public static int getScreenWidth() {
        WindowManager wm = (WindowManager) sContext.getSystemService(Context.WINDOW_SERVICE);
        return wm.getDefaultDisplay().getWidth();
    }

    public static int dp2px(float dpValue) {
        final float scale = sContext.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
