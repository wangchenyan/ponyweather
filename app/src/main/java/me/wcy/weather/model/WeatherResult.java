/**
 * 2015-3-25
 */
package me.wcy.weather.model;

/**
 * @author wcy
 * 
 */
public class WeatherResult {
	String status;
	String date;
	Weather results[];

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public Weather[] getResults() {
		return results;
	}

	public void setResults(Weather[] results) {
		this.results = results;
	}
}
