package me.wcy.weather.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import me.wcy.weather.R;
import me.wcy.weather.model.Weather;
import me.wcy.weather.utils.ImageUtils;

/**
 * Created by hzwangchenyan on 2016/3/25.
 */
public class DailyForecastAdapter extends BaseAdapter {
    private List<Weather.DailyForecastEntity> mData;

    public DailyForecastAdapter(List<Weather.DailyForecastEntity> data) {
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
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.view_holder_dialy_forecast, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.ivIcon.setImageResource(ImageUtils.getIconByCode(context, mData.get(position).cond.code_d));
        holder.tvDate.setText(dateFormat(mData.get(position).date));
        holder.tvTemp.setText(context.getString(R.string.daily_forecast_temp, mData.get(position).tmp.min, mData.get(position).tmp.max));
        StringBuilder sb = new StringBuilder();
        sb.append(mData.get(position).cond.txt_d)
                .append("，").append(mData.get(position).wind.dir)
                .append(mData.get(position).wind.sc).append(mData.get(position).wind.sc.contains("风") ? "" : "级")
                .append("，").append("湿度").append(mData.get(position).hum).append("%")
                .append("，").append("降水几率").append(mData.get(position).pop).append("%。");
        holder.tvDetail.setText(sb.toString());
        return convertView;
    }

    public static class ViewHolder {
        @Bind(R.id.iv_icon)
        public ImageView ivIcon;
        @Bind(R.id.tv_date)
        public TextView tvDate;
        @Bind(R.id.tv_temp)
        public TextView tvTemp;
        @Bind(R.id.tv_detail)
        public TextView tvDetail;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    private String dateFormat(String date) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date today = sdf.parse(sdf.format(new Date()));
            Date source = sdf.parse(date);
            if (today.equals(source)) {
                return "今天";
            } else if (source.getTime() - today.getTime() == 24 * 60 * 60 * 1000) {
                return "明天";
            } else {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(source);
                return weekdayFormat(calendar.get(Calendar.DAY_OF_WEEK));
            }
        } catch (ParseException e) {
            e.printStackTrace();
            return date;
        }
    }

    private String weekdayFormat(int weekday) {
        switch (weekday) {
            case 1:
                return "星期日";
            case 2:
                return "星期一";
            case 3:
                return "星期二";
            case 4:
                return "星期三";
            case 5:
                return "星期四";
            case 6:
                return "星期五";
            case 7:
                return "星期六";
            default:
                return "";
        }
    }
}
