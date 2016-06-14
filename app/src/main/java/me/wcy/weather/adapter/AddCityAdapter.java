package me.wcy.weather.adapter;

import android.annotation.SuppressLint;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import me.wcy.weather.R;
import me.wcy.weather.model.CityEntity;
import me.wcy.weather.model.CityListEntity;
import me.wcy.weather.utils.ACache;
import me.wcy.weather.utils.Extras;

public class AddCityAdapter extends RecyclerView.Adapter<CityViewHolder> implements View.OnClickListener {
    private List<CityListEntity.CityInfoEntity> mCityList = new ArrayList<>();
    private List<String> mAddedCityList = new ArrayList<>();
    private OnItemClickListener mListener;
    private Type mType;

    public void setDataAndType(List<CityListEntity.CityInfoEntity> data, Type type) {
        mCityList = data;
        mType = type;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    @Override
    public CityViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_holder_city, parent, false);
        view.setOnClickListener(this);
        ACache cache = ACache.get(parent.getContext());
        List<CityEntity> cityList = (List<CityEntity>) cache.getAsObject(Extras.CITY_LIST);
        for (CityEntity cityEntity : cityList) {
            if (!cityEntity.isAutoLocate) {
                mAddedCityList.add(cityEntity.name);
            }
        }
        return new CityViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(CityViewHolder holder, int position) {
        holder.item.setTag(mCityList.get(position));
        switch (mType) {
            case PROVINCE:
                holder.tvCity.setText(mCityList.get(position).province);
                break;
            case CITY:
                holder.tvCity.setText(mCityList.get(position).city);
                break;
            case AREA:
                holder.tvCity.setText(mCityList.get(position).area);
                holder.tvRemark.setText(mAddedCityList.contains(mCityList.get(position).area) ? "已添加" : "");
                break;
            case SEARCH:
                String result = mCityList.get(position).area + " - " + mCityList.get(position).city +
                        ", " + mCityList.get(position).province;
                holder.tvCity.setText(result);
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

    public enum Type {
        PROVINCE,
        CITY,
        AREA,
        SEARCH
    }
}
