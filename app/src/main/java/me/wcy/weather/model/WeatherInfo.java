/**
 * 2015-3-26
 */
package me.wcy.weather.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * @author wcy
 * 
 */
@DatabaseTable(tableName = "WeatherInfo")
public class WeatherInfo {

	@DatabaseField(generatedId = true)
	int id;

	@DatabaseField
	String date;

	@DatabaseField
	String weather;

	@DatabaseField
	String wind;

	@DatabaseField
	String temperature;

	public WeatherInfo() {
		super();
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getWeather() {
		return weather;
	}

	public void setWeather(String weather) {
		this.weather = weather;
	}

	public String getWind() {
		return wind;
	}

	public void setWind(String wind) {
		this.wind = wind;
	}

	public String getTemperature() {
		return temperature;
	}

	public void setTemperature(String temperature) {
		this.temperature = temperature;
	}

}
