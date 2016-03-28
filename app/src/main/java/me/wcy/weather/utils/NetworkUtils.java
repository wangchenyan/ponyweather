package me.wcy.weather.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

/**
 * Created by hzwangchenyan on 2016/3/28.
 */
public class NetworkUtils {
    /**
     * 检查网络连接
     */
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager localConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        int k;
        if (localConnectivityManager != null) {
            NetworkInfo[] arrayOfNetworkInfo = localConnectivityManager.getAllNetworkInfo();
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

    public static boolean errorByNetwork(Throwable e) {
        return e instanceof SocketTimeoutException || e instanceof UnknownHostException;
    }
}
