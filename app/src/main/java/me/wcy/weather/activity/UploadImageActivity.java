package me.wcy.weather.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
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
import me.wcy.weather.utils.SystemUtils;
import me.wcy.weather.widget.TagLayout;

public class UploadImageActivity extends BaseActivity implements View.OnClickListener, AMapLocationListener {
    @Bind(R.id.iv_weather_image)
    ImageView ivWeatherImage;
    @Bind(R.id.tv_location)
    TextView tvLocation;
    @Bind(R.id.tag)
    TagLayout tagLayout;
    @Bind(R.id.et_say)
    EditText etSay;
    @Bind(R.id.btn_upload)
    Button btnUpload;
    private ImageWeather imageWeather = new ImageWeather();
    private AMapLocationClient mLocationClient;
    private ProgressDialog mProgressDialog;
    private String path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_image);

        path = getIntent().getStringExtra(Extras.IMAGE_PATH);
        Bitmap bitmap = BitmapFactory.decodeFile(path);
        ivWeatherImage.setImageBitmap(bitmap);

        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        String deviceId = telephonyManager.getDeviceId();
        String userName = "马儿" + deviceId.substring(7);
        imageWeather.setUserName(userName);
        imageWeather.setPraise(0L);

        mLocationClient = SystemUtils.initAMapLocation(this, this);
        mLocationClient.startLocation();
        showProgress("正在定位…");
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

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (aMapLocation != null) {
            mLocationClient.stopLocation();
            cancelProgress();
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
                tvLocation.setText(aMapLocation.getAddress());
            } else {
                // 定位失败
                // 显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
                Log.e("AmapError", "location Error, ErrCode:"
                        + aMapLocation.getErrorCode() + ", errInfo:"
                        + aMapLocation.getErrorInfo());
                Toast.makeText(this, R.string.locate_fail, Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    private void upload() {
        showProgress("正在上传图片…");
        final BmobFile file = new BmobFile(new File(path));
        file.upload(this, new UploadFileListener() {
            @Override
            public void onSuccess() {
                cancelProgress();
                showProgress("正在发布…");
                imageWeather.setImageUrl(file.getFileUrl(UploadImageActivity.this));
                imageWeather.setSay(etSay.getText().toString());
                imageWeather.setTag(tagLayout.getTag());
                imageWeather.save(UploadImageActivity.this, new SaveListener() {
                    @Override
                    public void onSuccess() {
                        cancelProgress();
                        Toast.makeText(UploadImageActivity.this, "成功发布到" + imageWeather.getLocation().getCity(), Toast.LENGTH_SHORT).show();
                        setResult(RESULT_OK);
                        finish();
                    }

                    @Override
                    public void onFailure(int i, String s) {
                        Log.e("upload object fail", "code:" + i + ",msg:" + s);
                        cancelProgress();
                        SnackbarUtils.show(UploadImageActivity.this, "发布失败：" + s);
                    }
                });
            }

            @Override
            public void onFailure(int i, String s) {
                Log.e("upload image fail", "code:" + i + ",msg:" + s);
                cancelProgress();
                SnackbarUtils.show(UploadImageActivity.this, "图片上传失败：" + s);
            }
        });
    }

    private void showProgress(String msg) {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setCancelable(false);
        }
        mProgressDialog.setMessage(msg);
        mProgressDialog.show();
    }

    private void cancelProgress() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.cancel();
        }
    }

    @Override
    protected void onDestroy() {
        mLocationClient.onDestroy();
        super.onDestroy();
    }
}
