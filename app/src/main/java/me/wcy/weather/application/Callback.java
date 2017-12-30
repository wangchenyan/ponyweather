package me.wcy.weather.application;

/**
 * Created by hzwangchenyan on 2017/10/23.
 */
public interface Callback<T> {
    void onEvent(T t);
}
