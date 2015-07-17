/**
 * 2015-3-25
 */
package me.wcy.weather.adapter;

import me.wcy.weather.R;
import me.wcy.weather.model.WeatherInfo;
import me.wcy.weather.util.WeatherImage;
import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * @author wcy
 * 
 */
public class WeatherForecastAdapter extends BaseAdapter {
	private Context context;
	private WeatherInfo weatherInfos[];

	public WeatherForecastAdapter(Context context, WeatherInfo weatherInfos[]) {
		super();
		this.context = context;
		this.weatherInfos = weatherInfos;
	}

	@Override
	public int getCount() {
		return weatherInfos.length;
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@SuppressLint("InflateParams")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(
					R.layout.weather_forecast_item, null);
			holder = new ViewHolder();
			holder.date = (TextView) convertView
					.findViewById(R.id.weather_forecast_date);
			holder.icon = (ImageView) convertView
					.findViewById(R.id.weather_forecast_icon);
			holder.weather = (TextView) convertView
					.findViewById(R.id.weather_forecast_weather);
			holder.temperature = (TextView) convertView
					.findViewById(R.id.weather_forecast_temperature);
			holder.wind = (TextView) convertView
					.findViewById(R.id.weather_forecast_wind);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		WeatherImage weatherImage = new WeatherImage(
				weatherInfos[position].getWeather());
		holder.date.setText(weatherInfos[position].getDate());
		holder.icon.setImageResource(weatherImage.getWeatherIcon());
		holder.weather.setText(weatherInfos[position].getWeather());
		holder.temperature.setText(weatherInfos[position].getTemperature());
		holder.wind.setText(weatherInfos[position].getWind());
		return convertView;
	}

	class ViewHolder {
		TextView date;
		ImageView icon;
		TextView weather;
		TextView temperature;
		TextView wind;
	}

}
