package me.wcy.weather.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import me.wcy.weather.R;
import me.wcy.weather.model.HeWeather;

/**
 * Created by hzwangchenyan on 2016/3/25.
 */
public class HourlyForecastAdapter extends BaseAdapter {
    private List<HeWeather.HourlyForecastEntity> mData;

    public HourlyForecastAdapter(List<HeWeather.HourlyForecastEntity> data) {
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
        HourlyForecastAdapter.ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_holder_hourly_forecast, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.tvTime.setText(mData.get(position).date);
        holder.tvTemp.setText(mData.get(position).tmp);
        holder.tvHum.setText(mData.get(position).hum);
        holder.tvWind.setText(mData.get(position).wind.dir);
        return convertView;
    }

    public static class ViewHolder {
        @Bind(R.id.tv_time)
        public TextView tvTime;
        @Bind(R.id.tv_temp)
        public TextView tvTemp;
        @Bind(R.id.tv_hum)
        public TextView tvHum;
        @Bind(R.id.tv_wind)
        public TextView tvWind;

        public ViewHolder(View itemView) {
            ButterKnife.bind(this, itemView);
        }
    }
}
