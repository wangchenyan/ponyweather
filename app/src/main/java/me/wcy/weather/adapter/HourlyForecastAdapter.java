package me.wcy.weather.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import me.wcy.weather.R;
import me.wcy.weather.model.Weather;
import me.wcy.weather.utils.binding.Bind;
import me.wcy.weather.utils.binding.ViewBinder;

public class HourlyForecastAdapter extends BaseAdapter {
    private List<Weather.HourlyForecastEntity> mData;

    public HourlyForecastAdapter(List<Weather.HourlyForecastEntity> data) {
        mData = data;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Context context = parent.getContext();
        HourlyForecastAdapter.ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.view_holder_hourly_forecast, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.tvTime.setText(timeFormat(mData.get(position).date));
        holder.tvWeather.setText(mData.get(position).cond.txt);
        holder.tvTemp.setText(context.getString(R.string.temp, mData.get(position).tmp));
        holder.tvWind.setText(windFormat(context, mData.get(position).wind.sc));
        return convertView;
    }

    private static class ViewHolder {
        @Bind(R.id.tv_time)
        public TextView tvTime;
        @Bind(R.id.tv_weather)
        public TextView tvWeather;
        @Bind(R.id.tv_temp)
        public TextView tvTemp;
        @Bind(R.id.tv_wind)
        public TextView tvWind;

        public ViewHolder(View itemView) {
            ViewBinder.bind(this, itemView);
        }
    }

    private String timeFormat(String time) {
        SimpleDateFormat fromSdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        SimpleDateFormat toSdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        try {
            return toSdf.format(fromSdf.parse(time));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return toSdf.format(new Date());
    }

    private String windFormat(Context context, String wind) {
        if (wind.contains("风")) {
            return wind;
        } else {
            return context.getString(R.string.hourly_forecast_wind, wind);
        }
    }
}
