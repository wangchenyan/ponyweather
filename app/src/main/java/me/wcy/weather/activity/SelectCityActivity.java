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

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;

import java.sql.SQLException;

import butterknife.Bind;
import me.wcy.weather.R;
import me.wcy.weather.adapter.CityAdapter;
import me.wcy.weather.utils.DataManager;
import me.wcy.weather.utils.Utils;

@SuppressLint("InlinedApi")
public class SelectCityActivity extends BaseActivity implements OnClickListener, TextWatcher,
        OnItemClickListener, OnEditorActionListener, AMapLocationListener {
    @Bind(R.id.iv_back)
    ImageView ivBack;
    @Bind(R.id.gv_city_list)
    GridView gvCity;
    @Bind(R.id.et_input_city)
    EditText etInputCity;
    @Bind(R.id.iv_search)
    ImageView ivSearch;

    private String[] mCities;
    private ProgressDialog mDialog;
    private String mCity;
    private AMapLocationClient mLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_city);

        ivBack.setOnClickListener(this);
        ivSearch.setOnClickListener(this);
        etInputCity.addTextChangedListener(this);
        etInputCity.setOnEditorActionListener(this);
        mCities = getResources().getStringArray(R.array.citys);
        gvCity.setAdapter(new CityAdapter(this, mCities));
        gvCity.setOnItemClickListener(this);

        mDialog = new ProgressDialog(this);
        mDialog.setMessage(getResources().getString(R.string.locating));
        mDialog.setCanceledOnTouchOutside(false);

        initAMapLocation();
    }

    private void initAMapLocation() {
        mLocationClient = new AMapLocationClient(getApplicationContext());
        mLocationClient.setLocationListener(this);
        // 初始化定位参数
        AMapLocationClientOption mLocationOption = new AMapLocationClientOption();
        // 设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        // 设置是否返回地址信息（默认返回地址信息）
        mLocationOption.setNeedAddress(true);
        // 设置是否只定位一次,默认为false
        mLocationOption.setOnceLocation(false);
        // 设置是否强制刷新WIFI，默认为强制刷新
        mLocationOption.setWifiActiveScan(true);
        // 设置是否允许模拟位置,默认为false，不允许模拟位置
        mLocationOption.setMockEnable(true);
        // 设置定位间隔,单位毫秒,默认为2000ms
        mLocationOption.setInterval(2000);
        // 给定位客户端对象设置定位参数
        mLocationClient.setLocationOption(mLocationOption);
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
        mCity = mCities[position];
        if (mCities[0].equals(mCity)) {
            if (!Utils.isNetworkAvailable(this)) {
                Toast.makeText(this, R.string.network_error, Toast.LENGTH_SHORT).show();
                return;
            }
            mDialog.show();
            // 启动定位
            mLocationClient.startLocation();
        } else {
            Intent intent = new Intent();
            intent.putExtra(WeatherActivity.CITY, mCity);
            setResult(RESULT_OK, intent);
            finish();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                back();
                break;
            case R.id.iv_search:
                mCity = etInputCity.getText().toString();
                Intent intent = new Intent();
                intent.putExtra(WeatherActivity.CITY, mCity);
                setResult(RESULT_OK, intent);
                finish();
                break;
            default:
                break;
        }
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (aMapLocation != null) {
            mDialog.cancel();
            mLocationClient.stopLocation();
            if (aMapLocation.getErrorCode() == 0) {
                // 定位成功回调信息，设置相关消息
                mCity = aMapLocation.getCity();
                Intent intent = new Intent();
                intent.putExtra(WeatherActivity.CITY, mCity);
                setResult(RESULT_OK, intent);
                finish();
            } else {
                // 定位失败
                Toast.makeText(this, R.string.locate_fail, Toast.LENGTH_SHORT).show();
                // 显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
                Log.e("AmapError", "location Error, ErrCode:"
                        + aMapLocation.getErrorCode() + ", errInfo:"
                        + aMapLocation.getErrorInfo());
            }
        }
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            if (etInputCity.length() > 0) {
                ivSearch.performClick();
            }
            return true;
        }
        return false;
    }

    @Override
    public void afterTextChanged(Editable s) {
        if (etInputCity.getText().toString().length() == 0) {
            ivSearch.setVisibility(View.GONE);
        } else {
            ivSearch.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    protected void onDestroy() {
        mLocationClient.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        back();
    }

    private void back() {
        DataManager storageManager = DataManager.getInstance().setContext(this);
        try {
            if (storageManager.getData() == null) {
                WeatherActivity.mContext.finish();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        finish();
    }
}
