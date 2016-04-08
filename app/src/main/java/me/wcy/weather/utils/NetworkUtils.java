package me.wcy.weather.utils;

import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

/**
 * Created by hzwangchenyan on 2016/3/28.
 */
public class NetworkUtils {

    public static boolean errorByNetwork(Throwable e) {
        return e instanceof SocketTimeoutException || e instanceof UnknownHostException;
    }
}
