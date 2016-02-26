package me.wcy.weather.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.text.method.LinkMovementMethod;
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
import me.wcy.weather.utils.DataManager;
import me.wcy.weather.utils.Utils;
import me.wcy.weather.utils.WeatherImage;
import me.wcy.weather.widget.ScrollListView;

@SuppressLint({"SimpleDateFormat", "InflateParams"})
public class WeatherActivity extends BaseActivity implements OnClickListener, OnItemClickListener, OnRefreshListener<ScrollView> {
    public static final String CITY = "city";
    public static WeatherActivity mContext;

    @Bind(R.id.ll_weather_bg)
    LinearLayout llWeatherBg;
    @Bind(R.id.ll_title_bar)
    LinearLayout llTitleBar;
    @Bind(R.id.ll_change_city)
    LinearLayout llChangeCity;
    @Bind(R.id.sv_weather)
    PullToRefreshScrollView svWeather;
    @Bind(R.id.ll_weather)
    LinearLayout llWeather;
    @Bind(R.id.ll_current_weather)
    LinearLayout llCurrentWeather;
    @Bind(R.id.tv_city)
    TextView tvCity;
    @Bind(R.id.iv_share)
    ImageView ivShare;
    @Bind(R.id.iv_about)
    ImageView ivAbout;
    @Bind(R.id.tv_update_time)
    TextView tvUpdateTime;
    @Bind(R.id.tv_date)
    TextView tvDate;
    @Bind(R.id.tv_air_quality_num)
    TextView tvAirQualityNum;
    @Bind(R.id.tv_air_quality)
    TextView tvAirQuality;
    @Bind(R.id.tv_current_temp)
    TextView tvCurrentTemp;
    @Bind(R.id.tv_current_weather)
    TextView tvCurrentWeather;
    @Bind(R.id.tv_temp)
    TextView tvTemp;
    @Bind(R.id.tv_wind)
    TextView tvWind;
    @Bind(R.id.tv_weekday)
    TextView tvWeekday;
    @Bind(R.id.lv_weather_forecast)
    ScrollListView lvWeatherForecast;
    @Bind(R.id.lv_life_index)
    ScrollListView lvLifeIndex;
    @Bind(R.id.ll_life_index)
    LinearLayout llLifeIndex;

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
        setContentView(R.layout.activity_weather);

        svWeather.getRefreshableView().setOverScrollMode(View.OVER_SCROLL_NEVER);
        svWeather.setOnRefreshListener(this);
        svWeather.setMode(Mode.PULL_FROM_START);
        llChangeCity.setOnClickListener(this);
        ivShare.setOnClickListener(this);
        ivAbout.setOnClickListener(this);
        lvLifeIndex.setOnItemClickListener(this);

        mContext = this;
        mRequestQueue = Volley.newRequestQueue(this);
        mDataManager = DataManager.getInstance().setContext(this);
        mHandler = new Handler();
        try {
            mWeather = mDataManager.getData();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        setWeatherLayoutHeight();
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
        mIntent.setClass(this, SelectCityActivity.class);
        startActivityForResult(mIntent, 0);
    }

    /**
     * 更新界面
     */
    private void updateView() {
        llWeather.setVisibility(View.VISIBLE);
        svWeather.getRefreshableView().fullScroll(ScrollView.FOCUS_UP);
        WeatherImage weatherImage = new WeatherImage(mWeather.getWeather_data()[0].getWeather());
        llWeatherBg.setBackgroundResource(weatherImage.getWeatherBg());
        tvCity.setText(mCity);
        tvUpdateTime.setText(mDataManager.getUpdateTime(mWeather));
        tvDate.setText(mDataManager.getDate());
        tvAirQualityNum.setText(mWeather.getPm25());
        Map<String, Object> airQualityMap = mDataManager.getAirQuality(mWeather);
        tvAirQuality.setText((String) airQualityMap.get(DataManager.AIR_QUALITY));
        tvAirQuality.setBackgroundResource((Integer) airQualityMap.get(DataManager.AIR_QUALITY_BG));
        tvCurrentTemp.setText(mWeather.getCurrentTemp());
        tvCurrentWeather.setText(mDataManager.getCurrentWeather(mWeather));
        tvTemp.setText(mWeather.getWeather_data()[0].getTemperature());
        tvWind.setText(mWeather.getWeather_data()[0].getWind());
        tvWeekday.setText(mWeather.getWeather_data()[0].getDate());
        WeatherForecastAdapter weatherAdapter = new WeatherForecastAdapter(this, mWeather.getWeather_data());
        lvWeatherForecast.setAdapter(weatherAdapter);
        lvWeatherForecast.setFocusable(false);
        if (mWeather.getIndex().length > 0) {
            llLifeIndex.setVisibility(View.VISIBLE);
            mLifeAdapter = new LifeIndexAdapter(this, mWeather.getIndex());
            lvLifeIndex.setAdapter(mLifeAdapter);
            lvLifeIndex.setFocusable(false);
        } else {
            llLifeIndex.setVisibility(View.GONE);
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
    private void setWeatherLayoutHeight() {
        // TitleBar高度
        llTitleBar.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        int titleBarHeight = llTitleBar.getMeasuredHeight();
        LayoutParams linearParams = (LayoutParams) llCurrentWeather.getLayoutParams();
        try {
            linearParams.height = Utils.getDisplayHeight(this) - titleBarHeight;
        } catch (Exception e) {
            e.printStackTrace();
        }
        llCurrentWeather.setLayoutParams(linearParams);
    }

    private void updateWeather() {
        tvUpdateTime.setText(getString(R.string.updating));
        JSONRequest<WeatherResult> request = new JSONRequest<>(Utils.getUpdateUrl(mCity),
                WeatherResult.class, new Listener<WeatherResult>() {
            @Override
            public void onResponse(WeatherResult weatherResult) {
                Log.i("UpdateWeather", weatherResult.getStatus());
                if (Utils.STATUS_SUCCESS.equals(weatherResult.getStatus())) {
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
        svWeather.onRefreshComplete();
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
        svWeather.onRefreshComplete();
        tvUpdateTime.setText(getString(R.string.update_fail));
        mDialog = new AlertDialog.Builder(this);
        mDialog.setTitle(R.string.tips);
        mDialog.setMessage(R.string.update_failed);
        mDialog.setPositiveButton(R.string.retry, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                mIntent = new Intent();
                mIntent.setClass(WeatherActivity.this, SelectCityActivity.class);
                WeatherActivity.this.startActivityForResult(mIntent, 0);
            }
        });
        mDialog.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {

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
        Dialog dialog = mDialog.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    private void share() {
        mIntent = new Intent(Intent.ACTION_SEND);
        mIntent.setType("text/plain");
        mIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_content));
        mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(Intent.createChooser(mIntent, getString(R.string.share)));
    }

    private void about() {
        View view = getLayoutInflater().inflate(R.layout.about_dialog, null);
        TextView tvVersion = (TextView) view.findViewById(R.id.tv_version);
        TextView tvSource = (TextView) view.findViewById(R.id.tv_source);
        tvVersion.setText(Utils.getVersion(this));
        tvSource.setText(Html.fromHtml(getString(R.string.source_link)));
        tvSource.setMovementMethod(LinkMovementMethod.getInstance());
        mDialog = new AlertDialog.Builder(this);
        mDialog.setTitle(R.string.about);
        mDialog.setView(view);
        mDialog.setPositiveButton(R.string.sure, null);
        Dialog dialog = mDialog.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    private void refresh() {
        mHandler.postDelayed(new Runnable() {

            @Override
            public void run() {
                svWeather.setRefreshing();
            }
        }, 500);
    }

    @Override
    public void onRefresh(PullToRefreshBase<ScrollView> refreshView) {
        if (!Utils.isNetworkAvailable(this)) {
            svWeather.onRefreshComplete();
            Toast.makeText(this, R.string.network_error, Toast.LENGTH_SHORT).show();
            return;
        }
        updateWeather();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_change_city:
                mIntent = new Intent();
                mIntent.setClass(this, SelectCityActivity.class);
                startActivityForResult(mIntent, 0);
                break;
            case R.id.iv_share:
                share();
                break;
            case R.id.iv_about:
                about();
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
        if (mWeather == null || !data.getStringExtra(CITY).equals(mWeather.getCurrentCity())) {
            mCity = data.getStringExtra(CITY);
            tvCity.setText(mCity);
            llWeatherBg.setBackgroundResource(R.drawable.ic_weather_bg_na);
            llWeather.setVisibility(View.GONE);
            if (!Utils.isNetworkAvailable(this)) {
                Toast.makeText(this, R.string.network_error, Toast.LENGTH_SHORT).show();
                tvUpdateTime.setText(getString(
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
            Toast.makeText(this, R.string.click2exit, Toast.LENGTH_SHORT).show();
        } else {
            finish();
        }
    }
}
