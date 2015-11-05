package me.wcy.weather.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog.Builder;
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
import me.wcy.weather.util.Utils;
import me.wcy.weather.util.WeatherImage;
import me.wcy.weather.util.WeatherManager;
import me.wcy.weather.widget.MyListView;

@SuppressLint({"SimpleDateFormat", "InflateParams"})
public class WeatherActivity extends BaseActivity implements OnClickListener,
        OnItemClickListener, OnRefreshListener<ScrollView> {
    public static final String CITY = "city";
    public static WeatherActivity context;

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

    private Intent intent;
    private Builder builder;
    private String city;
    private RequestQueue queue;
    private WeatherManager weatherManager;
    private Weather weather;
    private Handler handler;
    private LifeIndexAdapter lifeAdapter;
    private long exitTime = 0;

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

        context = this;
        queue = Volley.newRequestQueue(this);
        weatherManager = new WeatherManager(this);
        handler = new Handler();
        try {
            weather = weatherManager.getData();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        setWeatherLayoutHight();
        if (weather == null) {
            getCity();
        } else {
            city = weather.getCurrentCity();
            updateView();
            autoUpdate();
        }
    }

    private void getCity() {
        intent = new Intent();
        intent.setClass(this, SelectCity.class);
        startActivityForResult(intent, 0);
    }

    /**
     * 更新界面
     */
    private void updateView() {
        weatherLayout.setVisibility(View.VISIBLE);
        scrollView.getRefreshableView().fullScroll(ScrollView.FOCUS_UP);
        WeatherImage weatherImage = new WeatherImage(
                weather.getWeather_data()[0].getWeather());
        weatherBg.setBackgroundResource(weatherImage.getWeatherBg());
        cityText.setText(city);
        updateTime.setText(weatherManager.getUpdateTime(weather));
        date.setText(weatherManager.getDate());
        airQualityNum.setText(weather.getPm25());
        Map<String, Object> airQualityMap = weatherManager.getAirQuality(weather);
        airQuality.setText((String) airQualityMap.get(WeatherManager.AIR_QULITY));
        airQuality.setBackgroundResource((Integer) airQualityMap.get(WeatherManager.AIR_QULITY_BG));
        currentTemp.setText(weather.getCurrentTemp());
        currentWeather.setText(weatherManager.getCurrentWeather(weather));
        temperature.setText(weather.getWeather_data()[0].getTemperature());
        wind.setText(weather.getWeather_data()[0].getWind());
        weekday.setText(weather.getWeather_data()[0].getDate());
        WeatherForecastAdapter weatherAdapter = new WeatherForecastAdapter(
                this, weather.getWeather_data());
        weatherForecast.setAdapter(weatherAdapter);
        weatherForecast.setFocusable(false);
        if (weather.getIndex().length > 0) {
            lifeLayout.setVisibility(View.VISIBLE);
            lifeAdapter = new LifeIndexAdapter(this, weather.getIndex());
            lifeIndex.setAdapter(lifeAdapter);
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
            if (weatherManager.autoUpdate(weather)) {
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
                Utils.getUpdateUrl(city), WeatherResult.class,
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
        queue.add(request);
    }

    private void onUpdateSuccess(WeatherResult weatherResult) {
        scrollView.onRefreshComplete();
        Toast.makeText(this, R.string.update_tips, Toast.LENGTH_SHORT).show();
        try {
            weatherManager.storeData(weatherResult);
            this.weather = weatherManager.getData();
        } catch (SQLException | ParseException e) {
            e.printStackTrace();
        }
        updateView();
    }

    private void onUpdateFail() {
        scrollView.onRefreshComplete();
        updateTime.setText(getString(R.string.update_fail));
        builder = new Builder(this);
        builder.setTitle(R.string.tips);
        builder.setMessage(R.string.update_failed);
        builder.setPositiveButton(R.string.retry,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        intent = new Intent();
                        intent.setClass(WeatherActivity.this, SelectCity.class);
                        WeatherActivity.this.startActivityForResult(intent, 0);
                    }
                });
        builder.setNegativeButton(R.string.cancel,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (weather != null) {
                            city = weather.getCurrentCity();
                            updateView();
                        } else {
                            finish();
                        }
                    }
                });
        builder.setCancelable(false);
        builder.show();
    }

    private void share() {
        intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_content));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(Intent.createChooser(intent, getString(R.string.share)));
    }

    private void about() {
        View dialogView = getLayoutInflater().inflate(R.layout.about_dialog, null);
        TextView version = (TextView) dialogView.findViewById(R.id.version);
        version.setText("V " + Utils.getVersion(this));
        builder = new Builder(this);
        builder.setTitle(R.string.about);
        builder.setView(dialogView);
        builder.setPositiveButton(R.string.sure, null);
        builder.setCancelable(false);
        builder.show();
    }

    private void refresh() {
        handler.postDelayed(new Runnable() {

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
                intent = new Intent();
                intent.setClass(this, SelectCity.class);
                startActivityForResult(intent, 0);
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
        lifeAdapter.setSelection(position);
        lifeAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        if (weather == null || !data.getStringExtra(CITY).equals(
                weather.getCurrentCity())) {
            city = data.getStringExtra(CITY);
            cityText.setText(city);
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
        if (System.currentTimeMillis() - exitTime > 2000) {
            exitTime = System.currentTimeMillis();
            Toast.makeText(this, R.string.click2exit, Toast.LENGTH_SHORT)
                    .show();
        } else {
            finish();
        }
    }

}
