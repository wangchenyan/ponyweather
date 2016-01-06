/**
 * 2015-3-27
 */
package me.wcy.weather.utils;

import java.util.Calendar;

import me.wcy.weather.R;

/**
 * @author wcy
 */
public class WeatherImage {
    private String mWeather;
    private int mHour;

    public WeatherImage(String weather) {
        super();
        this.mWeather = weather;
        init();
    }

    private void init() {
        if (mWeather.contains("转")) {
            mWeather = mWeather.substring(0, mWeather.indexOf("转"));
        }
        mHour = Calendar.HOUR_OF_DAY;
    }

    public int getWeatherBg() {
        int bg = R.drawable.ic_weather_bg_na;
        if (mWeather.contains("晴")) {
            if (mHour >= 7 && mHour < 19) {
                bg = R.drawable.ic_weather_bg_fine_day;
            } else {
                bg = R.drawable.ic_weather_bg_fine_night;
            }
        } else if (mWeather.contains("多云")) {
            if (mHour >= 7 && mHour < 19) {
                bg = R.drawable.ic_weather_bg_cloudy_day;
            } else {
                bg = R.drawable.ic_weather_bg_cloudy_night;
            }
        } else if (mWeather.contains("阴")) {
            bg = R.drawable.ic_weather_bg_overcast;
        } else if (mWeather.contains("雷")) {
            bg = R.drawable.ic_weather_bg_thunder_storm;
        } else if (mWeather.contains("雨")) {
            bg = R.drawable.ic_weather_bg_rain;
        } else if (mWeather.contains("雪") || mWeather.contains("冰雹")) {
            bg = R.drawable.ic_weather_bg_snow;
        } else if (mWeather.contains("雾")) {
            bg = R.drawable.ic_weather_bg_fog;
        } else if (mWeather.contains("霾")) {
            bg = R.drawable.ic_weather_bg_haze;
        } else if (mWeather.contains("沙") || mWeather.contains("浮尘")) {
            bg = R.drawable.ic_weather_bg_sand_storm;
        }
        return bg;
    }

    public int getWeatherIcon() {
        int icon = R.drawable.ic_weather_icon_fine;
        if (mWeather.contains("多云")) {
            icon = R.drawable.ic_weather_icon_cloudy;
        } else if (mWeather.contains("阴")) {
            icon = R.drawable.ic_weather_icon_overcast;
        } else if (mWeather.contains("雷")) {
            icon = R.drawable.ic_weather_icon_thunder_storm;
        } else if (mWeather.contains("小雨")) {
            icon = R.drawable.ic_weather_icon_rain_small;
        } else if (mWeather.contains("中雨")) {
            icon = R.drawable.ic_weather_icon_rain_middle;
        } else if (mWeather.contains("大雨")) {
            icon = R.drawable.ic_weather_icon_rain_big;
        } else if (mWeather.contains("暴雨")) {
            icon = R.drawable.ic_weather_icon_rain_storm;
        } else if (mWeather.contains("雨夹雪")) {
            icon = R.drawable.ic_weather_icon_rain_snow;
        } else if (mWeather.contains("冻雨")) {
            icon = R.drawable.ic_weather_icon_sleet;
        } else if (mWeather.contains("小雪")) {
            icon = R.drawable.ic_weather_icon_snow_small;
        } else if (mWeather.contains("中雪")) {
            icon = R.drawable.ic_weather_icon_snow_middle;
        } else if (mWeather.contains("大雪")) {
            icon = R.drawable.ic_weather_icon_snow_big;
        } else if (mWeather.contains("暴雪")) {
            icon = R.drawable.ic_weather_icon_snow_storm;
        } else if (mWeather.contains("冰雹")) {
            icon = R.drawable.ic_weather_icon_hail;
        } else if (mWeather.contains("雾") || mWeather.contains("霾")) {
            icon = R.drawable.ic_weather_icon_fog;
        } else if (mWeather.contains("沙") || mWeather.contains("浮尘")) {
            icon = R.drawable.ic_weather_icon_sand_storm;
        }
        return icon;
    }
}
