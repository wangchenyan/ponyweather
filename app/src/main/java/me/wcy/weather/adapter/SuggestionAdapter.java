package me.wcy.weather.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import me.wcy.weather.R;
import me.wcy.weather.model.Weather;
import me.wcy.weather.utils.binding.Bind;
import me.wcy.weather.utils.binding.ViewBinder;

public class SuggestionAdapter extends BaseAdapter {
    private List<Integer> mIcon = new ArrayList<>();
    private List<String> mTitle = new ArrayList<>();
    private List<Weather.SuggestionEntity.Entity> mData = new ArrayList<>();

    public SuggestionAdapter(Weather.SuggestionEntity data) {
        mIcon.add(R.drawable.ic_suggestion_comfort);
        mTitle.add("舒适度");
        mData.add(data.comf);
        mIcon.add(R.drawable.ic_suggestion_clothe);
        mTitle.add("穿衣");
        mData.add(data.drsg);
        mIcon.add(R.drawable.ic_suggestion_flu);
        mTitle.add("感冒");
        mData.add(data.flu);
        mIcon.add(R.drawable.ic_suggestion_car);
        mTitle.add("洗车");
        mData.add(data.cw);
        mIcon.add(R.drawable.ic_suggestion_sport);
        mTitle.add("运动");
        mData.add(data.sport);
        mIcon.add(R.drawable.ic_suggestion_travel);
        mTitle.add("旅游");
        mData.add(data.trav);
        mIcon.add(R.drawable.ic_suggestion_uv);
        mTitle.add("紫外线");
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
        Context context = parent.getContext();
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.view_holder_suggestion, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.ivIcon.setImageResource(mIcon.get(position));
        holder.tvLabel.setText(mTitle.get(position));
        holder.tvTitle.setText(mData.get(position).brf);
        holder.tvDesc.setText(mData.get(position).txt);
        return convertView;
    }

    public static class ViewHolder {
        @Bind(R.id.iv_icon)
        public ImageView ivIcon;
        @Bind(R.id.tv_label)
        public TextView tvLabel;
        @Bind(R.id.tv_title)
        public TextView tvTitle;
        @Bind(R.id.tv_desc)
        public TextView tvDesc;

        public ViewHolder(View view) {
            ViewBinder.bind(this, view);
        }
    }
}
