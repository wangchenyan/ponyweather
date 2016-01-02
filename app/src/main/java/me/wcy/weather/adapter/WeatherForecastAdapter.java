/**
 * 2015-3-25
 */
package me.wcy.weather.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import me.wcy.weather.R;
import me.wcy.weather.model.WeatherInfo;
import me.wcy.weather.util.WeatherImage;

/**
 * @author wcy
 */
public class WeatherForecastAdapter extends BaseAdapter {
    private Context mContext;
    private WeatherInfo mWeatherInfos[];

    public WeatherForecastAdapter(Context context, WeatherInfo weatherInfos[]) {
        this.mContext = context;
        this.mWeatherInfos = weatherInfos;
    }

    @Override
    public int getCount() {
        return mWeatherInfos.length;
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.activity_weather_forecast_list_item, null);
            holder = new ViewHolder();
            holder.tvDate = (TextView) convertView.findViewById(R.id.tv_date);
            holder.ivIcon = (ImageView) convertView.findViewById(R.id.iv_icon);
            holder.tvWeather = (TextView) convertView.findViewById(R.id.tv_weather);
            holder.tvTemp = (TextView) convertView.findViewById(R.id.tv_temp);
            holder.tvWind = (TextView) convertView.findViewById(R.id.tv_wind);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        WeatherImage weatherImage = new WeatherImage(mWeatherInfos[position].getWeather());
        holder.tvDate.setText(mWeatherInfos[position].getDate());
        holder.ivIcon.setImageResource(weatherImage.getWeatherIcon());
        holder.tvWeather.setText(mWeatherInfos[position].getWeather());
        holder.tvTemp.setText(mWeatherInfos[position].getTemperature());
        holder.tvWind.setText(mWeatherInfos[position].getWind());
        return convertView;
    }

    class ViewHolder {
        TextView tvDate;
        ImageView ivIcon;
        TextView tvWeather;
        TextView tvTemp;
        TextView tvWind;
    }
}
