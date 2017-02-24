package me.wcy.weather.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class WeatherData {
    @SerializedName("HeWeather5")
    public List<Weather> weathers;
}
