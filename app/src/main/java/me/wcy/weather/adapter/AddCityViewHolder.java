package me.wcy.weather.adapter;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import me.wcy.weather.R;
import me.wcy.weather.addcity.AddCityContract;
import me.wcy.weather.model.CityEntity;
import me.wcy.weather.utils.binding.Bind;
import me.wcy.weather.widget.radapter.RLayout;
import me.wcy.weather.widget.radapter.RViewHolder;

/**
 * Created by wcy on 2018/1/20.
 */
@RLayout(R.layout.view_holder_city)
public class AddCityViewHolder extends RViewHolder<CityEntity> implements View.OnClickListener {
    @Bind(R.id.view_holder_city)
    private LinearLayout item;
    @Bind(R.id.tv_city)
    private TextView tvCity;
    @Bind(R.id.iv_locate)
    private ImageView ivLocate;
    @Bind(R.id.tv_remark)
    private TextView tvRemark;

    private AddCityContract.Presenter presenter;

    public AddCityViewHolder(View itemView) {
        super(itemView);
        itemView.setOnClickListener(this);
    }

    @Override
    public void refresh() {
        presenter = (AddCityContract.Presenter) adapter.getTag();
        String name = null;
        String remark = null;
        switch (presenter.getType()) {
            case PROVINCE:
                name = data.getProvince();
                break;
            case CITY:
                name = data.getCity();
                break;
            case AREA:
                name = data.getArea();
                remark = presenter.getAddedCity().contains(data.getArea()) ? "已添加" : null;
                break;
            case SEARCH:
                name = data.getArea().concat(" - ").concat(data.getCity()).concat(", ").concat(data.getProvince());
                break;
        }
        tvCity.setText(name);
        tvRemark.setText(remark);
    }

    @Override
    public void onClick(View v) {
        presenter.onItemClick(data);
    }
}
