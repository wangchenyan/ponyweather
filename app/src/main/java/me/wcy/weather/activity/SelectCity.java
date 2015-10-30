package me.wcy.weather.activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;

import java.sql.SQLException;

import butterknife.Bind;
import me.wcy.weather.R;
import me.wcy.weather.adapter.CityAdapter;
import me.wcy.weather.util.Utils;
import me.wcy.weather.util.WeatherManager;

@SuppressLint("InlinedApi")
public class SelectCity extends BaseActivity implements OnClickListener, TextWatcher, OnItemClickListener, OnEditorActionListener, BDLocationListener {
    @Bind(R.id.back)
    ImageView back;
    @Bind(R.id.city_list)
    GridView cityGridView;
    @Bind(R.id.input_city)
    EditText inputCity;
    @Bind(R.id.search)
    ImageView search;

    private String[] cities;
    private Intent intent;
    private ProgressDialog dialog;
    private String city;
    private LocationClient mLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_city);

        back.setOnClickListener(this);
        search.setOnClickListener(this);
        inputCity.addTextChangedListener(this);
        inputCity.setOnEditorActionListener(this);
        cities = getResources().getStringArray(R.array.citys);
        cityGridView.setAdapter(new CityAdapter(this, cities));
        cityGridView.setOnItemClickListener(this);

        dialog = new ProgressDialog(this);
        dialog.setMessage(getResources().getString(R.string.locating));
        dialog.setCanceledOnTouchOutside(false);

        initBaiduLocation();
    }

    private void initBaiduLocation() {
        // 声明LocationClient类
        mLocationClient = new LocationClient(getApplicationContext());
        // 注册监听函数
        mLocationClient.registerLocationListener(this);
        // 设置定位参数
        setLocationOption();
    }

    /**
     * 设置定位参数。 定位模式（单次定位，定时定位），返回坐标类型，是否打开GPS等等。
     */
    private void setLocationOption() {
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationMode.Hight_Accuracy);// 设置定位模式
        option.setCoorType("bd09ll");// 返回的定位结果是百度经纬度,默认值gcj02
        option.setScanSpan(24 * 60 * 60 * 1000);// 设置发起定位请求的间隔时间为5000ms
        option.setIsNeedAddress(true);// 返回的定位结果包含地址信息
        option.setNeedDeviceDirect(true);// 返回的定位结果包含手机机头的方向
        mLocationClient.setLocOption(option);
    }

    /**
     * 请求位置信息
     */
    private void requestLocation() {
        if (mLocationClient != null) {
            if (!mLocationClient.isStarted()) {
                mLocationClient.start();
            } else {
                mLocationClient.requestLocation();
            }
        } else {
            Log.d("LocSDK5", "locClient is null or not started");
        }
    }

    @Override
    public void onReceiveLocation(BDLocation location) {
        dialog.cancel();
        if (location == null) {
            Toast.makeText(this, R.string.locate_fail, Toast.LENGTH_SHORT)
                    .show();
            return;
        }
        int code = location.getLocType();
        city = location.getCity();
        if (code == 161 && city != null) {
            // 定位成功
            intent = new Intent();
            intent.putExtra(WeatherActivity.CITY, city);
            setResult(RESULT_OK, intent);
            finish();
        } else {
            // 定位失败
            Toast.makeText(this, R.string.locate_fail, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
        city = cities[position];
        if (cities[0].equals(city)) {
            if (!Utils.isNetworkAvailable(this)) {
                Toast.makeText(this, R.string.network_error, Toast.LENGTH_SHORT).show();
                return;
            }
            dialog.show();
            requestLocation();
        } else {
            intent = new Intent();
            intent.putExtra(WeatherActivity.CITY, city);
            setResult(RESULT_OK, intent);
            finish();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                back();
                break;
            case R.id.search:
                city = inputCity.getText().toString();
                intent = new Intent();
                intent.putExtra(WeatherActivity.CITY, city);
                setResult(RESULT_OK, intent);
                finish();
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            if (inputCity.length() == 0) {
                return true;
            }
            search.performClick();
        }
        return false;
    }

    @Override
    public void afterTextChanged(Editable s) {
        if (inputCity.getText().toString().length() == 0) {
            search.setVisibility(View.GONE);
        } else {
            search.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count,
                                  int after) {
    }

    @Override
    protected void onStop() {
        mLocationClient.stop();
        super.onStop();
    }

    @Override
    public void onBackPressed() {
        back();
    }

    private void back() {
        WeatherManager storageManager = new WeatherManager(this);
        try {
            if (storageManager.getData() == null) {
                WeatherActivity.context.finish();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        finish();
    }

}
