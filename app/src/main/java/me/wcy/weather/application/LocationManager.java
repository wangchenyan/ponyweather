package me.wcy.weather.application;

import android.content.Context;
import android.util.Log;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hzwangchenyan on 2017/11/29.
 */
public class LocationManager {
    private final List<Callback<AMapLocation>> mObserverList = new ArrayList<>();
    private AMapLocationClient mLocationClient = null;

    private static class SingletonHolder {
        private static LocationManager sInstance = new LocationManager();
    }

    public static LocationManager get() {
        return SingletonHolder.sInstance;
    }

    private LocationManager() {
    }

    public void init(Context context) {
        mLocationClient = new AMapLocationClient(context.getApplicationContext());
        mLocationClient.setLocationListener(mLocationListener);
        AMapLocationClientOption mLocationOption = new AMapLocationClientOption();
        //设置定位模式为AMapLocationMode.Hight_Accuracy，高精度模式。
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //获取一次定位结果，该方法默认为false。
        mLocationOption.setOnceLocation(true);
        //获取最近3s内精度最高的一次定位结果，设置setOnceLocationLatest(boolean b)接口为true，启动定位时SDK会返回最近3s内精度最高的一次定位结果。
        // 如果设置其为true，setOnceLocation(boolean b)接口也会被设置为true，反之不会，默认为false。
        mLocationOption.setOnceLocationLatest(true);
        //设置是否返回地址信息（默认返回地址信息）
        mLocationOption.setNeedAddress(true);
        //设置是否允许模拟位置,默认为true，允许模拟位置
        mLocationOption.setMockEnable(false);
        //给定位客户端对象设置定位参数
        mLocationClient.setLocationOption(mLocationOption);
    }

    public void start() {
        mLocationClient.startLocation();
    }

    public void addLocationObserver(Callback<AMapLocation> observer) {
        if (!mObserverList.contains(observer)) {
            mObserverList.add(observer);
        }
    }

    public void removeLocationObserver(Callback<AMapLocation> observer) {
        mObserverList.remove(observer);
    }

    private AMapLocationListener mLocationListener = amapLocation -> {
        if (amapLocation == null) {
            Log.e("AmapError", "amapLocation is null");
            return;
        }

        if (amapLocation.getErrorCode() != 0) {
            //定位失败时，可通过ErrCode（错误码）信息来确定失败的原因，errInfo是错误信息，详见错误码表。
            Log.e("AmapError", "location Error, ErrCode:"
                    + amapLocation.getErrorCode() + ", errInfo:"
                    + amapLocation.getErrorInfo());
        }

        for (Callback<AMapLocation> observer : mObserverList) {
            observer.onEvent(amapLocation);
        }
    };
}
