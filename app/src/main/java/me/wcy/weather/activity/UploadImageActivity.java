package me.wcy.weather.activity;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;

import java.io.File;

import butterknife.Bind;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UploadFileListener;
import me.wcy.weather.R;
import me.wcy.weather.model.ImageWeather;
import me.wcy.weather.model.Location;
import me.wcy.weather.utils.Extras;
import me.wcy.weather.utils.SnackbarUtils;

public class UploadImageActivity extends BaseActivity implements View.OnClickListener, AMapLocationListener {
    @Bind(R.id.iv_weather_image)
    ImageView ivWeatherImage;
    @Bind(R.id.et_say)
    EditText etSay;
    @Bind(R.id.tv_location)
    TextView tvLocation;
    @Bind(R.id.btn_upload)
    Button btnUpload;
    private ProgressDialog mProgressDialog;
    private String path;
    private AMapLocationClient mLocationClient;
    private ImageWeather imageWeather;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_image);

        path = getIntent().getStringExtra(Extras.IMAGE_PATH);
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setCancelable(false);
        imageWeather = new ImageWeather();

        Bitmap bitmap = BitmapFactory.decodeFile(path);
        ivWeatherImage.setImageBitmap(bitmap);
        initAMapLocation();
        mLocationClient.startLocation();
        mProgressDialog.show();
    }

    @Override
    protected void setListener() {
        btnUpload.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_upload:
                upload();
                break;
        }
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
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (aMapLocation != null) {
            mProgressDialog.cancel();
            mLocationClient.stopLocation();
            if (aMapLocation.getErrorCode() == 0) {
                // 定位成功回调信息，设置相关消息
                imageWeather.setCity(aMapLocation.getCity().replace("市", ""));
                Location location = new Location();
                location.setAddress(aMapLocation.getAddress());
                location.setCountry(aMapLocation.getCountry());
                location.setProvince(aMapLocation.getProvince());
                location.setCity(aMapLocation.getCity());
                location.setDistrict(aMapLocation.getDistrict());
                location.setStreet(aMapLocation.getStreet());
                location.setStreetNum(aMapLocation.getStreetNum());
                imageWeather.setLocation(location);
                imageWeather.setUserName(aMapLocation.getProvince().replace("省", "") + aMapLocation.getCity().replace("市", "") + "网友");
                tvLocation.setText(aMapLocation.getAddress());
            } else {
                // 定位失败
                SnackbarUtils.show(this, R.string.locate_fail);
                // 显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
                Log.e("AmapError", "location Error, ErrCode:"
                        + aMapLocation.getErrorCode() + ", errInfo:"
                        + aMapLocation.getErrorInfo());
            }
        }
    }

    private void upload() {
        mProgressDialog.show();
        final BmobFile file = new BmobFile(new File(path));
        file.upload(this, new UploadFileListener() {
            @Override
            public void onSuccess() {
                imageWeather.setImageUrl(file.getFileUrl(UploadImageActivity.this));
                imageWeather.setSay(etSay.getText().toString());
                imageWeather.setTag("天气");
                imageWeather.setPraise(0L);
                imageWeather.save(UploadImageActivity.this, new SaveListener() {
                    @Override
                    public void onSuccess() {
                        mProgressDialog.cancel();
                        Toast.makeText(UploadImageActivity.this, "成功发布到" + imageWeather.getLocation().getCity(), Toast.LENGTH_SHORT).show();
                        setResult(RESULT_OK);
                        finish();
                    }

                    @Override
                    public void onFailure(int i, String s) {
                        Log.e("upload object fail", "code:" + i + ",msg:" + s);
                        mProgressDialog.cancel();
                        SnackbarUtils.show(UploadImageActivity.this, "发布失败：" + i + "," + s);
                    }
                });
            }

            @Override
            public void onFailure(int i, String s) {
                mProgressDialog.cancel();
                Log.e("upload image fail", "code:" + i + ",msg:" + s);
                SnackbarUtils.show(UploadImageActivity.this, "发布失败：" + i + "," + s);
            }
        });
    }
}
