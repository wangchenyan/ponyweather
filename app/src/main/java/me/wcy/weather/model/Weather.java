/**
 * 2015-3-26
 */
package me.wcy.weather.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * @author wcy
 */
@DatabaseTable(tableName = "Weather")
public class Weather {
    @DatabaseField(generatedId = true)
    int id;
    @DatabaseField
    String currentCity;
    @DatabaseField
    String pm25;
    @DatabaseField
    String currentTemp;
    @DatabaseField
    long date;
    @DatabaseField
    long updateTime;

    LifeIndex index[];
    WeatherInfo weather_data[];

    public Weather() {
    }

    public String getCurrentCity() {
        return currentCity;
    }

    public void setCurrentCity(String currentCity) {
        this.currentCity = currentCity;
    }

    public String getPm25() {
        return pm25;
    }

    public void setPm25(String pm25) {
        this.pm25 = pm25;
    }

    public String getCurrentTemp() {
        return currentTemp;
    }

    public void setCurrentTemp(String currentTemp) {
        this.currentTemp = currentTemp;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
    }

    public LifeIndex[] getIndex() {
        return index;
    }

    public void setIndex(LifeIndex[] index) {
        this.index = index;
    }

    public WeatherInfo[] getWeather_data() {
        return weather_data;
    }

    public void setWeather_data(WeatherInfo[] weather_data) {
        this.weather_data = weather_data;
    }
}
