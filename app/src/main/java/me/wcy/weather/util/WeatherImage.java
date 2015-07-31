/**
 * 2015-3-27
 */
package me.wcy.weather.util;

import java.util.Calendar;

import me.wcy.weather.R;

/**
 * @author wcy
 */
public class WeatherImage {
    private String weather;
    private int hour;

    public WeatherImage(String weather) {
        super();
        this.weather = weather;
        init();
    }

    private void init() {
        if (weather.contains("转")) {
            weather = weather.substring(0, weather.indexOf("转"));
        }
        hour = Calendar.HOUR_OF_DAY;
    }

    public int getWeatherBg() {
        int bg = R.drawable.ic_weather_bg_na;
        if (weather.contains("晴")) {
            if (hour >= 7 && hour < 19) {
                bg = R.drawable.ic_weather_bg_fine_day;
            } else {
                bg = R.drawable.ic_weather_bg_fine_night;
            }
        } else if (weather.contains("多云")) {
            if (hour >= 7 && hour < 19) {
                bg = R.drawable.ic_weather_bg_cloudy_day;
            } else {
                bg = R.drawable.ic_weather_bg_cloudy_night;
            }
        } else if (weather.contains("阴")) {
            bg = R.drawable.ic_weather_bg_overcast;
        } else if (weather.contains("雷")) {
            bg = R.drawable.ic_weather_bg_thunder_storm;
        } else if (weather.contains("雨")) {
            bg = R.drawable.ic_weather_bg_rain;
        } else if (weather.contains("雪") || weather.contains("冰雹")) {
            bg = R.drawable.ic_weather_bg_snow;
        } else if (weather.contains("雾")) {
            bg = R.drawable.ic_weather_bg_fog;
        } else if (weather.contains("霾")) {
            bg = R.drawable.ic_weather_bg_haze;
        } else if (weather.contains("沙") || weather.contains("浮尘")) {
            bg = R.drawable.ic_weather_bg_sand_storm;
        }
        return bg;
    }

    public int getWeatherIcon() {
        int icon = R.drawable.ic_weather_icon_fine;
        if (weather.contains("多云")) {
            icon = R.drawable.ic_weather_icon_cloudy;
        } else if (weather.contains("阴")) {
            icon = R.drawable.ic_weather_icon_overcast;
        } else if (weather.contains("雷")) {
            icon = R.drawable.ic_weather_icon_thunder_storm;
        } else if (weather.contains("小雨")) {
            icon = R.drawable.ic_weather_icon_rain_small;
        } else if (weather.contains("中雨")) {
            icon = R.drawable.ic_weather_icon_rain_middle;
        } else if (weather.contains("大雨")) {
            icon = R.drawable.ic_weather_icon_rain_big;
        } else if (weather.contains("暴雨")) {
            icon = R.drawable.ic_weather_icon_rain_storm;
        } else if (weather.contains("雨夹雪")) {
            icon = R.drawable.ic_weather_icon_rain_snow;
        } else if (weather.contains("冻雨")) {
            icon = R.drawable.ic_weather_icon_sleet;
        } else if (weather.contains("小雪")) {
            icon = R.drawable.ic_weather_icon_snow_small;
        } else if (weather.contains("中雪")) {
            icon = R.drawable.ic_weather_icon_snow_middle;
        } else if (weather.contains("大雪")) {
            icon = R.drawable.ic_weather_icon_snow_big;
        } else if (weather.contains("暴雪")) {
            icon = R.drawable.ic_weather_icon_snow_storm;
        } else if (weather.contains("冰雹")) {
            icon = R.drawable.ic_weather_icon_hail;
        } else if (weather.contains("雾") || weather.contains("霾")) {
            icon = R.drawable.ic_weather_icon_fog;
        } else if (weather.contains("沙") || weather.contains("浮尘")) {
            icon = R.drawable.ic_weather_icon_sand_storm;
        }
        return icon;
    }
}
