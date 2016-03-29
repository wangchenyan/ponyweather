package me.wcy.weather.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import me.wcy.weather.R;
import me.wcy.weather.model.CityListEntity;

/**
 * Created by hzwangchenyan on 2016/3/29.
 */
public class CityListAdapter extends RecyclerView.Adapter<CityListAdapter.ViewHolder> implements View.OnClickListener {
    private List<CityListEntity.CityInfoEntity> mCityList = new ArrayList<>();
    private Type mType;
    private OnItemClickListener mListener;

    public void setData(List<CityListEntity.CityInfoEntity> data, Type type) {
        mCityList = data;
        mType = type;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_holder_city, parent, false);
        view.setOnClickListener(this);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.tvCity.setTag(mCityList.get(position));
        switch (mType) {
            case PROVINCE:
                holder.tvCity.setText(mCityList.get(position).province);
                break;
            case CITY:
                holder.tvCity.setText(mCityList.get(position).city);
                break;
            case AREA:
                holder.tvCity.setText(mCityList.get(position).area);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return mCityList.size();
    }

    @Override
    public void onClick(View v) {
        mListener.onItemClick(v, v.getTag());
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.tv_city)
        public TextView tvCity;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public enum Type {
        PROVINCE,
        CITY,
        AREA
    }

    public interface OnItemClickListener {
        void onItemClick(View view, Object data);
    }
}
