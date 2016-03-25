package me.wcy.weather.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import me.wcy.weather.R;
import me.wcy.weather.model.HeWeather;

/**
 * Created by hzwangchenyan on 2016/3/25.
 */
public class DailyForecastAdapter extends BaseAdapter {
    private List<HeWeather.DailyForecastEntity> mData;

    public DailyForecastAdapter(List<HeWeather.DailyForecastEntity> data) {
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
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_holder_dialy_forecast, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.tvDate.setText(mData.get(position).date);
        holder.tvTemp.setText(mData.get(position).tmp.min + "~" + mData.get(position).tmp.max);
        holder.tvDesc.setText(mData.get(position).cond.txt_d);
        return convertView;
    }

    public static class ViewHolder {
        @Bind(R.id.iv_icon)
        public ImageView ivIcon;
        @Bind(R.id.tv_date)
        public TextView tvDate;
        @Bind(R.id.tv_temp)
        public TextView tvTemp;
        @Bind(R.id.tv_desc)
        public TextView tvDesc;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
