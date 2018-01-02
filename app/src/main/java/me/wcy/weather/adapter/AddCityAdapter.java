package me.wcy.weather.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import me.wcy.weather.R;
import me.wcy.weather.addcity.AddCityContract;
import me.wcy.weather.constants.Extras;
import me.wcy.weather.model.CityEntity;
import me.wcy.weather.model.CityInfo;
import me.wcy.weather.utils.ACache;

public class AddCityAdapter extends RecyclerView.Adapter<CityViewHolder> implements View.OnClickListener {
    private List<CityEntity> mCityList = new ArrayList<>();
    private List<String> mAddedCityList = new ArrayList<>();
    private AddCityContract.Presenter presenter;

    public AddCityAdapter(AddCityContract.Presenter presenter) {
        this.presenter = presenter;
    }

    public void setCityList(List<CityEntity> cityList) {
        mCityList = cityList;
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
        CityEntity cityEntity = mCityList.get(position);
        holder.item.setTag(cityEntity);
        String name = null;
        String remark = null;
        switch (presenter.getType()) {
            case PROVINCE:
                name = cityEntity.getProvince();
                break;
            case CITY:
                name = cityEntity.getCity();
                break;
            case AREA:
                name = cityEntity.getArea();
                remark = mAddedCityList.contains(cityEntity.getArea()) ? "已添加" : null;
                break;
            case SEARCH:
                name = cityEntity.getArea() + " - " + cityEntity.getCity() + ", " + cityEntity.getProvince();
                break;
        }
        holder.tvCity.setText(name);
        holder.tvRemark.setText(remark);
    }

    @Override
    public int getItemCount() {
        return mCityList.size();
    }

    @Override
    public void onClick(View v) {
        presenter.onItemClick((CityEntity) v.getTag());
    }
}
