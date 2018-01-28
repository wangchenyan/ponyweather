package me.wcy.weather.addcity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import me.wcy.weather.R;
import me.wcy.weather.activity.BaseActivity;
import me.wcy.weather.adapter.AddCityViewHolder;
import me.wcy.weather.model.CityEntity;
import me.wcy.weather.utils.SnackbarUtils;
import me.wcy.weather.utils.binding.Bind;
import me.wcy.weather.widget.radapter.RAdapter;
import me.wcy.weather.widget.radapter.RSingleDelegate;

public class AddCityActivity extends BaseActivity implements AddCityContract.View, View.OnClickListener, SearchView.OnQueryTextListener {
    @Bind(R.id.rv_city)
    private RecyclerView rvCity;
    @Bind(R.id.fab_location)
    private FloatingActionButton fabLocation;
    @Bind(R.id.tv_search_tips)
    private TextView tvSearchTips;

    private List<CityEntity> cityEntityList = new ArrayList<>();
    private RAdapter<CityEntity> adapter;
    private AddCityContract.Presenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_city);

        presenter = new AddCityPresenter(new AddCityRepository(), this);
        adapter = new RAdapter<>(cityEntityList, new RSingleDelegate<>(AddCityViewHolder.class));
        adapter.setTag(presenter);
        rvCity.setLayoutManager(new LinearLayoutManager(rvCity.getContext()));
        rvCity.setAdapter(adapter);

        fabLocation.setOnClickListener(this);

        presenter.onCreate();
        presenter.showProvince();
    }

    @Override
    public boolean isDestroy() {
        return isDestroyedCompat();
    }

    @Override
    public Activity getActivity() {
        return this;
    }

    @Override
    public void showSnack(CharSequence message) {
        SnackbarUtils.show(this, message);
    }

    @Override
    public void showProvince(List<CityEntity> provinceList) {
        rvCity.scrollToPosition(0);
        cityEntityList.clear();
        cityEntityList.addAll(provinceList);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void showCity(List<CityEntity> cityList) {
        rvCity.scrollToPosition(0);
        cityEntityList.clear();
        cityEntityList.addAll(cityList);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void showArea(List<CityEntity> areaList) {
        rvCity.scrollToPosition(0);
        cityEntityList.clear();
        cityEntityList.addAll(areaList);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void showSearching() {
        tvSearchTips.setText("正在搜索…");
        tvSearchTips.setVisibility(View.VISIBLE);
        rvCity.setVisibility(View.GONE);
    }

    @Override
    public void cancelSearch() {
        tvSearchTips.setVisibility(View.GONE);
        rvCity.setVisibility(View.VISIBLE);
    }

    @Override
    public void showSearchSuccess(List<CityEntity> searchList) {
        tvSearchTips.setVisibility(View.GONE);
        rvCity.setVisibility(View.VISIBLE);
        rvCity.scrollToPosition(0);
        cityEntityList.clear();
        cityEntityList.addAll(searchList);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void showSearchError() {
        tvSearchTips.setText("无匹配城市");
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_city, menu);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.setQueryHint("城市名");
        searchView.setOnQueryTextListener(this);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(final String newText) {
        presenter.search(newText);
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab_location:
                presenter.locate();
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        presenter.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        presenter.onDestroy();
        super.onDestroy();
    }
}
