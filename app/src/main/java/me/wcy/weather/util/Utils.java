package me.wcy.weather.util;

import java.lang.reflect.Field;
import java.net.URLEncoder;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.WindowManager;

/**
 * @author wcy
 * 
 */
@SuppressWarnings("deprecation")
public class Utils {
	private static final String URL = "http://api.map.baidu.com/telematics/v3/weather";
	private static final String BAIDU_APP_KEY = "MEMK39Gs9RS2jXyiG3He4VUB";
	private static final String OUTPUT_JSON = "json";
	public static final String STATUS_SUCCESS = "success";

	/**
	 * 检查网络连接
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isNetworkAvailable(Context context) {
		ConnectivityManager localConnectivityManager = (ConnectivityManager) context
				.getSystemService("connectivity");
		int k;
		if (localConnectivityManager != null) {
			NetworkInfo[] arrayOfNetworkInfo = localConnectivityManager
					.getAllNetworkInfo();
			if (arrayOfNetworkInfo != null) {
				int j = arrayOfNetworkInfo.length;
				for (k = 0; k < j; k++) {
					if (arrayOfNetworkInfo[k].getState() == NetworkInfo.State.CONNECTED)
						return true;
				}
			}
		}
		return false;
	}

	/**
	 * 获取版本号
	 * 
	 * @param context
	 *            context
	 * @return 版本号
	 */
	public static String getVersion(Context context) {
		PackageManager manager = context.getPackageManager();
		PackageInfo info = null;
		try {
			info = manager.getPackageInfo(context.getPackageName(), 0);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return info.versionName;
	}

	public static String getUpdateUrl(String city) {
		city = URLEncoder.encode(city);
		return URL + "?location=" + city + "&ak=" + BAIDU_APP_KEY + "&output="
				+ OUTPUT_JSON;
	}

	/**
	 * 获取屏幕高度（除去状态栏）
	 * 
	 * @param context
	 * @return
	 * @throws Exception
	 */
	public static int getDisplayHeight(Context context) throws Exception {
		int displayHeight, statusBarHeight;
		// 屏幕高度
		WindowManager manager = ((Activity) context).getWindowManager();
		displayHeight = manager.getDefaultDisplay().getHeight();
		// 通知栏高度
		Class<?> c = Class.forName("com.android.internal.R$dimen");
		Object obj = c.newInstance();
		Field field = c.getField("status_bar_height");
		int x = Integer.parseInt(field.get(obj).toString());
		statusBarHeight = context.getResources().getDimensionPixelSize(x);
		return displayHeight - statusBarHeight;
	}
}
