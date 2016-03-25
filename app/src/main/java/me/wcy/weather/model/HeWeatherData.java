package me.wcy.weather.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by hzwangchenyan on 2016/3/24.
 */
public class HeWeatherData implements Serializable {
    @SerializedName("HeWeather data service 3.0")
    public List<HeWeather> heWeathers;
}
