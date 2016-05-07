package me.wcy.weather.utils;

import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

public class NetworkUtils {

    public static boolean errorByNetwork(Throwable e) {
        return e instanceof SocketTimeoutException || e instanceof UnknownHostException;
    }
}
