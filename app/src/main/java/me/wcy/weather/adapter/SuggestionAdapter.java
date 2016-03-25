package me.wcy.weather.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import me.wcy.weather.R;
import me.wcy.weather.model.HeWeather;

/**
 * Created by hzwangchenyan on 2016/3/25.
 */
public class SuggestionAdapter extends BaseAdapter {
    private List<HeWeather.SuggestionEntity.Entity> mData = new ArrayList<>();

    public SuggestionAdapter(HeWeather.SuggestionEntity data) {
        mData.add(data.comf);
        mData.add(data.cw);
        mData.add(data.drsg);
        mData.add(data.flu);
        mData.add(data.sport);
        mData.add(data.trav);
        mData.add(data.uv);
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
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_holder_suggestion, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.tvTitle.setText(mData.get(position).brf);
        holder.tvDesc.setText(mData.get(position).txt);
        return convertView;
    }

    public static class ViewHolder {
        @Bind(R.id.iv_icon)
        public ImageView ivIcon;
        @Bind(R.id.tv_title)
        public TextView tvTitle;
        @Bind(R.id.tv_desc)
        public TextView tvDesc;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
