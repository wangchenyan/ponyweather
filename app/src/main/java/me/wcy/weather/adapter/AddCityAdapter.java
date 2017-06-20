package me.wcy.weather.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import me.wcy.weather.R;
import me.wcy.weather.constants.Extras;
import me.wcy.weather.model.CityEntity;
import me.wcy.weather.model.CityInfo;
import me.wcy.weather.utils.ACache;

public class AddCityAdapter extends RecyclerView.Adapter<CityViewHolder> implements View.OnClickListener {
    private List<CityEntity> mCityList = new ArrayList<>();
    private List<String> mAddedCityList = new ArrayList<>();
    private OnItemClickListener mListener;
    private Type mType;

    public void setDataAndType(List<CityEntity> data, Type type) {
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
        List<CityInfo> cityList = (List<CityInfo>) cache.getAsObject(Extras.CITY_LIST);
        for (CityInfo cityInfo : cityList) {
            if (!cityInfo.isAutoLocate) {
                mAddedCityList.add(cityInfo.name);
            }
        }
        return new CityViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CityViewHolder holder, int position) {
        holder.item.setTag(mCityList.get(position));
        switch (mType) {
            case PROVINCE:
                holder.tvCity.setText(mCityList.get(position).getProvince());
                break;
            case CITY:
                holder.tvCity.setText(mCityList.get(position).getCity());
                break;
            case AREA:
                holder.tvCity.setText(mCityList.get(position).getArea());
                holder.tvRemark.setText(mAddedCityList.contains(mCityList.get(position).getArea()) ? "已添加" : "");
                break;
            case SEARCH:
                String result = mCityList.get(position).getArea()
                        + " - " + mCityList.get(position).getCity()
                        + ", " + mCityList.get(position).getProvince();
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
