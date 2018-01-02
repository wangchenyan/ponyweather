package me.wcy.weather.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;

import me.wcy.weather.R;
import me.wcy.weather.adapter.ManageCityAdapter;
import me.wcy.weather.adapter.OnItemClickListener;
import me.wcy.weather.adapter.OnItemLongClickListener;
import me.wcy.weather.addcity.AddCityActivity;
import me.wcy.weather.constants.Extras;
import me.wcy.weather.constants.RequestCode;
import me.wcy.weather.model.CityInfo;
import me.wcy.weather.utils.ACache;
import me.wcy.weather.utils.SnackbarUtils;
import me.wcy.weather.utils.binding.Bind;

public class ManageCityActivity extends BaseActivity implements View.OnClickListener, OnItemClickListener, OnItemLongClickListener {
    @Bind(R.id.rv_city)
    private RecyclerView rvCity;
    @Bind(R.id.fab_add)
    private FloatingActionButton fabAdd;
    private ACache mACache;
    private ArrayList<CityInfo> mCityList;
    private ManageCityAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_city);

        mACache = ACache.get(getApplicationContext());
        mCityList = (ArrayList<CityInfo>) mACache.getAsObject(Extras.CITY_LIST);

        mAdapter = new ManageCityAdapter(mCityList);
        rvCity.setLayoutManager(new LinearLayoutManager(rvCity.getContext()));
        rvCity.setAdapter(mAdapter);
    }

    @Override
    protected void setListener() {
        fabAdd.setOnClickListener(this);
        mAdapter.setOnItemClickListener(this);
        mAdapter.setOnItemLongClickListener(this);
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
    public void onItemClick(View view, Object data) {
        CityInfo city = (CityInfo) data;
        CityInfo currentCity = (CityInfo) mACache.getAsObject(Extras.CITY);
        if (!currentCity.equals(city)) {
            mACache.put(Extras.CITY, city);
        }
        Intent intent = new Intent();
        intent.putExtra(Extras.CITY, city);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void onItemLongClick(View view, Object data) {
        if (mCityList.size() <= 1) {
            return;
        }
        final CityInfo city = (CityInfo) data;
        new AlertDialog.Builder(this)
                .setTitle(city.name)
                .setMessage(R.string.whether_delete)
                .setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteCity(city);
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    private void deleteCity(CityInfo city) {
        mCityList.remove(city);
        mACache.put(Extras.CITY_LIST, mCityList);
        CityInfo currentCity = (CityInfo) mACache.getAsObject(Extras.CITY);
        if (!mCityList.contains(currentCity)) {
            currentCity = mCityList.get(0);
            mACache.put(Extras.CITY, currentCity);
            Intent data = new Intent();
            data.putExtra(Extras.CITY, currentCity);
            setResult(RESULT_OK, data);
        }
        mAdapter.notifyDataSetChanged();
        SnackbarUtils.show(fabAdd, R.string.delete_success);
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
