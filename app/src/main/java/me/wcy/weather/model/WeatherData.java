package me.wcy.weather.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by hzwangchenyan on 2016/3/24.
 */
public class WeatherData {
    @SerializedName("HeWeather data service 3.0")
    public List<Weather> weathers;
}
