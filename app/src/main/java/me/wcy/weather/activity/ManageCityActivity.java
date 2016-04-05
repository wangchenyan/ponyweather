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

import butterknife.Bind;
import me.wcy.weather.R;
import me.wcy.weather.adapter.ManageCityAdapter;
import me.wcy.weather.adapter.OnItemClickListener;
import me.wcy.weather.adapter.OnItemLongClickListener;
import me.wcy.weather.utils.ACache;
import me.wcy.weather.utils.Extras;
import me.wcy.weather.utils.RequestCode;
import me.wcy.weather.utils.SnackbarUtils;

public class ManageCityActivity extends BaseActivity implements View.OnClickListener, OnItemClickListener, OnItemLongClickListener {
    @Bind(R.id.rv_city)
    RecyclerView rvCity;
    @Bind(R.id.fab_add)
    FloatingActionButton fabAdd;
    private ACache mACache;
    private ArrayList<String> mCityList;
    private ManageCityAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_city);

        mACache = ACache.get(getApplicationContext());
        mCityList = (ArrayList<String>) mACache.getAsObject(Extras.CITY_LIST);

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
        String city = (String) data;
        String currentCity = mACache.getAsString(Extras.CITY);
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
        final String city = (String) data;
        new AlertDialog.Builder(this)
                .setTitle(city)
                .setMessage("是否删除该城市？")
                .setPositiveButton("删除", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteCity(city);
                    }
                })
                .setNegativeButton("取消", null)
                .show();
    }

    private void deleteCity(String city) {
        if (mCityList.size() <= 1) {
            SnackbarUtils.show(fabAdd, "请至少保留一个城市");
            return;
        }
        mCityList.remove(city);
        mACache.put(Extras.CITY_LIST, mCityList);
        String currentCity = mACache.getAsString(Extras.CITY);
        if (!mCityList.contains(currentCity)) {
            currentCity = mCityList.get(0);
            mACache.put(Extras.CITY, currentCity);
            Intent data = new Intent();
            data.putExtra(Extras.CITY, currentCity);
            setResult(RESULT_OK, data);
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK || data == null) {
            return;
        }
        String city = data.getStringExtra(Extras.CITY);
        String currentCity = mACache.getAsString(Extras.CITY);
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
