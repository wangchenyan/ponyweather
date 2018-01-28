package me.wcy.weather.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;

import me.wcy.weather.R;
import me.wcy.weather.adapter.ManageCityViewHolder;
import me.wcy.weather.addcity.AddCityActivity;
import me.wcy.weather.constants.Extras;
import me.wcy.weather.constants.RequestCode;
import me.wcy.weather.model.CityInfo;
import me.wcy.weather.utils.ACache;
import me.wcy.weather.utils.binding.Bind;
import me.wcy.weather.widget.radapter.RAdapter;
import me.wcy.weather.widget.radapter.RSingleDelegate;

public class ManageCityActivity extends BaseActivity implements View.OnClickListener {
    @Bind(R.id.rv_city)
    private RecyclerView rvCity;
    @Bind(R.id.fab_add)
    private FloatingActionButton fabAdd;

    private ACache mACache;
    private ArrayList<CityInfo> mCityList;
    private RAdapter<CityInfo> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_city);

        mACache = ACache.get(getApplicationContext());
        mCityList = (ArrayList<CityInfo>) mACache.getAsObject(Extras.CITY_LIST);
        adapter = new RAdapter<>(mCityList, new RSingleDelegate<>(ManageCityViewHolder.class));
        adapter.setTag(mACache);
        rvCity.setLayoutManager(new LinearLayoutManager(rvCity.getContext()));
        rvCity.setAdapter(adapter);
        fabAdd.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab_add:
                startActivityForResult(new Intent(this, AddCityActivity.class), RequestCode.REQUEST_CODE);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK || data == null) {
            return;
        }
        CityInfo city = (CityInfo) data.getSerializableExtra(Extras.CITY);
        CityInfo currentCity = (CityInfo) mACache.getAsObject(Extras.CITY);
        if (!currentCity.equals(city)) {
            mACache.put(Extras.CITY, city);
        }
        if (!mCityList.contains(city)) {
            mCityList.add(0, city);
            mACache.put(Extras.CITY_LIST, mCityList);
        }
        setResult(RESULT_OK, data);
        finish();
    }
}
