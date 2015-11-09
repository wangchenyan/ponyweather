package me.wcy.weather.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.Map;

import butterknife.Bind;
import me.wcy.weather.R;
import me.wcy.weather.adapter.LifeIndexAdapter;
import me.wcy.weather.adapter.WeatherForecastAdapter;
import me.wcy.weather.model.Weather;
import me.wcy.weather.model.WeatherResult;
import me.wcy.weather.request.JSONRequest;
import me.wcy.weather.util.DataManager;
import me.wcy.weather.util.Utils;
import me.wcy.weather.util.WeatherImage;
import me.wcy.weather.widget.MyListView;

@SuppressLint({"SimpleDateFormat", "InflateParams"})
public class WeatherActivity extends BaseActivity implements OnClickListener,
        OnItemClickListener, OnRefreshListener<ScrollView> {
    public static final String CITY = "city";
    public static WeatherActivity mContext;

    @Bind(R.id.weather_bg)
    LinearLayout weatherBg;
    @Bind(R.id.titlebar_layout)
    LinearLayout titlebarLayout;
    @Bind(R.id.change_city_layout)
    LinearLayout changeCity;
    @Bind(R.id.scroll_view)
    PullToRefreshScrollView scrollView;
    @Bind(R.id.weather_layout)
    LinearLayout weatherLayout;
    @Bind(R.id.current_weather_layout)
    LinearLayout currentWeatherLayout;
    @Bind(R.id.city)
    TextView cityText;
    @Bind(R.id.share)
    ImageView share;
    @Bind(R.id.about)
    ImageView about;
    @Bind(R.id.update_time)
    TextView updateTime;
    @Bind(R.id.date)
    TextView date;
    @Bind(R.id.air_quality_num)
    TextView airQualityNum;
    @Bind(R.id.air_quality)
    TextView airQuality;
    @Bind(R.id.current_temperature)
    TextView currentTemp;
    @Bind(R.id.current_weather)
    TextView currentWeather;
    @Bind(R.id.temperature)
    TextView temperature;
    @Bind(R.id.wind)
    TextView wind;
    @Bind(R.id.weekday)
    TextView weekday;
    @Bind(R.id.weather_forecast_listview)
    MyListView weatherForecast;
    @Bind(R.id.life_index_listview)
    MyListView lifeIndex;
    @Bind(R.id.life_layout)
    LinearLayout lifeLayout;

    private Intent mIntent;
    private AlertDialog.Builder mDialog;
    private String mCity;
    private RequestQueue mRequestQueue;
    private DataManager mDataManager;
    private Weather mWeather;
    private Handler mHandler;
    private LifeIndexAdapter mLifeAdapter;
    private long mExitTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weather);

        scrollView.getRefreshableView().setOverScrollMode(View.OVER_SCROLL_NEVER);
        scrollView.setOnRefreshListener(this);
        scrollView.setMode(Mode.PULL_FROM_START);
        changeCity.setOnClickListener(this);
        share.setOnClickListener(this);
        about.setOnClickListener(this);
        lifeIndex.setOnItemClickListener(this);

        mContext = this;
        mRequestQueue = Volley.newRequestQueue(this);
        mDataManager = DataManager.getInstance().setContext(this);
        mHandler = new Handler();
        try {
            mWeather = mDataManager.getData();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        setWeatherLayoutHight();
        if (mWeather == null) {
            getCity();
        } else {
            mCity = mWeather.getCurrentCity();
            updateView();
            autoUpdate();
        }
    }

    private void getCity() {
        mIntent = new Intent();
        mIntent.setClass(this, SelectCity.class);
        startActivityForResult(mIntent, 0);
    }

    /**
     * 更新界面
     */
    private void updateView() {
        weatherLayout.setVisibility(View.VISIBLE);
        scrollView.getRefreshableView().fullScroll(ScrollView.FOCUS_UP);
        WeatherImage weatherImage = new WeatherImage(
                mWeather.getWeather_data()[0].getWeather());
        weatherBg.setBackgroundResource(weatherImage.getWeatherBg());
        cityText.setText(mCity);
        updateTime.setText(mDataManager.getUpdateTime(mWeather));
        date.setText(mDataManager.getDate());
        airQualityNum.setText(mWeather.getPm25());
        Map<String, Object> airQualityMap = mDataManager.getAirQuality(mWeather);
        airQuality.setText((String) airQualityMap.get(DataManager.AIR_QUALITY));
        airQuality.setBackgroundResource((Integer) airQualityMap.get(DataManager.AIR_QUALITY_BG));
        currentTemp.setText(mWeather.getCurrentTemp());
        currentWeather.setText(mDataManager.getCurrentWeather(mWeather));
        temperature.setText(mWeather.getWeather_data()[0].getTemperature());
        wind.setText(mWeather.getWeather_data()[0].getWind());
        weekday.setText(mWeather.getWeather_data()[0].getDate());
        WeatherForecastAdapter weatherAdapter = new WeatherForecastAdapter(
                this, mWeather.getWeather_data());
        weatherForecast.setAdapter(weatherAdapter);
        weatherForecast.setFocusable(false);
        if (mWeather.getIndex().length > 0) {
            lifeLayout.setVisibility(View.VISIBLE);
            mLifeAdapter = new LifeIndexAdapter(this, mWeather.getIndex());
            lifeIndex.setAdapter(mLifeAdapter);
            lifeIndex.setFocusable(false);
        } else {
            lifeLayout.setVisibility(View.GONE);
        }
    }

    /**
     * 超过2小时未更新自动更新
     */
    private void autoUpdate() {
        try {
            if (mDataManager.autoUpdate(mWeather)) {
                if (Utils.isNetworkAvailable(this)) {
                    refresh();
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置布局高度铺满屏幕
     */
    private void setWeatherLayoutHight() {
        // TitleBar高度
        titlebarLayout.measure(View.MeasureSpec.makeMeasureSpec(0,
                View.MeasureSpec.UNSPECIFIED), View.MeasureSpec
                .makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        int titleBarHeight = titlebarLayout.getMeasuredHeight();
        LayoutParams linearParams = (LayoutParams) currentWeatherLayout
                .getLayoutParams();
        try {
            linearParams.height = Utils.getDisplayHeight(this) - titleBarHeight;
        } catch (Exception e) {
            e.printStackTrace();
        }
        currentWeatherLayout.setLayoutParams(linearParams);
    }

    private void updateWeather() {
        updateTime.setText(getString(R.string.updating));
        JSONRequest<WeatherResult> request = new JSONRequest<>(
                Utils.getUpdateUrl(mCity), WeatherResult.class,
                new Listener<WeatherResult>() {
                    @Override
                    public void onResponse(WeatherResult weatherResult) {
                        Log.i("UpdateWeather", weatherResult.getStatus());
                        if (Utils.STATUS_SUCCESS.equals(weatherResult
                                .getStatus())) {
                            onUpdateSuccess(weatherResult);
                        } else {
                            onUpdateFail();
                        }
                    }
                }, new ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("UpdateWeather", error.getMessage(), error);
                onUpdateFail();
            }
        });
        request.setShouldCache(false);
        mRequestQueue.add(request);
    }

    private void onUpdateSuccess(WeatherResult weatherResult) {
        scrollView.onRefreshComplete();
        Toast.makeText(this, R.string.update_tips, Toast.LENGTH_SHORT).show();
        try {
            mDataManager.storeData(weatherResult);
            this.mWeather = mDataManager.getData();
        } catch (SQLException | ParseException e) {
            e.printStackTrace();
        }
        updateView();
    }

    private void onUpdateFail() {
        scrollView.onRefreshComplete();
        updateTime.setText(getString(R.string.update_fail));
        mDialog = new AlertDialog.Builder(this);
        mDialog.setTitle(R.string.tips);
        mDialog.setMessage(R.string.update_failed);
        mDialog.setPositiveButton(R.string.retry,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mIntent = new Intent();
                        mIntent.setClass(WeatherActivity.this, SelectCity.class);
                        WeatherActivity.this.startActivityForResult(mIntent, 0);
                    }
                });
        mDialog.setNegativeButton(R.string.cancel,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (mWeather != null) {
                            mCity = mWeather.getCurrentCity();
                            updateView();
                        } else {
                            finish();
                        }
                    }
                });
        mDialog.setCancelable(false);
        mDialog.show();
    }

    private void share() {
        mIntent = new Intent(Intent.ACTION_SEND);
        mIntent.setType("text/plain");
        mIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_content));
        mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(Intent.createChooser(mIntent, getString(R.string.share)));
    }

    private void about() {
        View dialogView = getLayoutInflater().inflate(R.layout.about_dialog, null);
        TextView version = (TextView) dialogView.findViewById(R.id.version);
        version.setText("V " + Utils.getVersion(this));
        mDialog = new AlertDialog.Builder(this);
        mDialog.setTitle(R.string.about);
        mDialog.setView(dialogView);
        mDialog.setPositiveButton(R.string.sure, null);
        mDialog.setCancelable(false);
        mDialog.show();
    }

    private void refresh() {
        mHandler.postDelayed(new Runnable() {

            @Override
            public void run() {
                scrollView.setRefreshing();
            }
        }, 500);
    }

    @Override
    public void onRefresh(PullToRefreshBase<ScrollView> refreshView) {
        if (!Utils.isNetworkAvailable(this)) {
            scrollView.onRefreshComplete();
            Toast.makeText(this, R.string.network_error, Toast.LENGTH_SHORT)
                    .show();
            return;
        }
        updateWeather();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.change_city_layout:
                mIntent = new Intent();
                mIntent.setClass(this, SelectCity.class);
                startActivityForResult(mIntent, 0);
                break;
            case R.id.share:
                share();
                break;
            case R.id.about:
                about();
                break;
            default:
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        mLifeAdapter.setSelection(position);
        mLifeAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        if (mWeather == null || !data.getStringExtra(CITY).equals(
                mWeather.getCurrentCity())) {
            mCity = data.getStringExtra(CITY);
            cityText.setText(mCity);
            weatherBg.setBackgroundResource(R.drawable.ic_weather_bg_na);
            weatherLayout.setVisibility(View.GONE);
            if (!Utils.isNetworkAvailable(this)) {
                Toast.makeText(this, R.string.network_error, Toast.LENGTH_SHORT)
                        .show();
                updateTime.setText(getString(
                        R.string.update_fail));
                return;
            }
            refresh();
        }
    }

    @Override
    public void onBackPressed() {
        if (System.currentTimeMillis() - mExitTime > 2000) {
            mExitTime = System.currentTimeMillis();
            Toast.makeText(this, R.string.click2exit, Toast.LENGTH_SHORT)
                    .show();
        } else {
            finish();
        }
    }

}
