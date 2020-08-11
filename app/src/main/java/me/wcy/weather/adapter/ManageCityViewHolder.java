package me.wcy.weather.adapter;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import me.wcy.weather.R;
import me.wcy.weather.constants.Extras;
import me.wcy.weather.model.CityInfo;
import me.wcy.weather.utils.ACache;
import me.wcy.weather.utils.SnackbarUtils;
import me.wcy.weather.utils.binding.Bind;
import me.wcy.weather.utils.binding.ViewBinder;
import me.wcy.weather.widget.radapter.RLayout;
import me.wcy.weather.widget.radapter.RViewHolder;

/**
 * Created by wcy on 2018/1/20.
 */
@RLayout(R.layout.view_holder_city)
public class ManageCityViewHolder extends RViewHolder<CityInfo> implements View.OnClickListener, View.OnLongClickListener {
    @Bind(R.id.view_holder_city)
    public LinearLayout item;
    @Bind(R.id.tv_city)
    public TextView tvCity;
    @Bind(R.id.iv_locate)
    public ImageView ivLocate;
    @Bind(R.id.tv_remark)
    public TextView tvRemark;

    private ACache aCache;

    public ManageCityViewHolder(View itemView) {
        super(itemView);
        ViewBinder.bind(this, itemView);
        itemView.setOnClickListener(this);
        itemView.setOnLongClickListener(this);
    }

    @Override
    public void refresh() {
        aCache = (ACache) adapter.getTag();
        tvCity.setText(data.name);
        ivLocate.setVisibility(data.isAutoLocate ? View.VISIBLE : View.GONE);
        CityInfo currentCity = (CityInfo) aCache.getAsObject(Extras.CITY);
        tvRemark.setText(data.equals(currentCity) ? "当前城市" : "");
    }

    @Override
    public void onClick(View v) {
        CityInfo currentCity = (CityInfo) aCache.getAsObject(Extras.CITY);
        if (!currentCity.equals(data)) {
            aCache.put(Extras.CITY, data);
        }
        Activity activity = (Activity) context;
        Intent intent = new Intent();
        intent.putExtra(Extras.CITY, data);
        activity.setResult(Activity.RESULT_OK, intent);
        activity.finish();
    }

    @Override
    public boolean onLongClick(View v) {
        if (adapter.getDataList().size() <= 1) {
            return false;
        }
        new AlertDialog.Builder(context)
                .setTitle(data.name)
                .setMessage(R.string.whether_delete)
                .setPositiveButton(R.string.delete, (dialog, which) -> deleteCity())
                .setNegativeButton(R.string.cancel, null)
                .show();
        return true;
    }

    private void deleteCity() {
        adapter.removeItem(position);
        aCache.put(Extras.CITY_LIST, (ArrayList) adapter.getDataList());
        CityInfo currentCity = (CityInfo) aCache.getAsObject(Extras.CITY);
        Activity activity = (Activity) context;
        if (!adapter.getDataList().contains(currentCity)) {
            currentCity = adapter.getDataList().get(0);
            aCache.put(Extras.CITY, currentCity);
            Intent data = new Intent();
            data.putExtra(Extras.CITY, currentCity);
            activity.setResult(Activity.RESULT_OK, data);
        }
        SnackbarUtils.show(activity, R.string.delete_success);
    }
}
