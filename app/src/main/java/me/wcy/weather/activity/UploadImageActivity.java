package me.wcy.weather.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.File;

import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UploadFileListener;
import me.wcy.weather.R;
import me.wcy.weather.constants.Extras;
import me.wcy.weather.constants.RequestCode;
import me.wcy.weather.model.ImageWeather;
import me.wcy.weather.model.Location;
import me.wcy.weather.utils.KeyboardUtils;
import me.wcy.weather.utils.SnackbarUtils;
import me.wcy.weather.utils.Utils;
import me.wcy.weather.utils.binding.Bind;
import me.wcy.weather.widget.TagLayout;

public class UploadImageActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "UploadImageActivity";
    @Bind(R.id.iv_weather_image)
    private ImageView ivWeatherImage;
    @Bind(R.id.tv_location)
    private TextView tvLocation;
    @Bind(R.id.tag)
    private TagLayout tagLayout;
    @Bind(R.id.et_say)
    private EditText etSay;
    @Bind(R.id.btn_upload)
    private Button btnUpload;
    private ImageWeather imageWeather = new ImageWeather();
    private String path;

    public static void start(Activity activity, Location location, String path) {
        Intent intent = new Intent(activity, UploadImageActivity.class);
        intent.putExtra(Extras.IMAGE_PATH, path);
        intent.putExtra(Extras.LOCATION, location);
        activity.startActivityForResult(intent, RequestCode.REQUEST_UPLOAD);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_image);

        path = getIntent().getStringExtra(Extras.IMAGE_PATH);
        Glide.with(this)
                .load(new File(path))
                .placeholder(R.drawable.image_weather_placeholder)
                .error(R.drawable.image_weather_placeholder)
                .into(ivWeatherImage);
        btnUpload.setOnClickListener(this);

        Location location = (Location) getIntent().getSerializableExtra(Extras.LOCATION);
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        @SuppressLint({"MissingPermission", "HardwareIds"})
        String deviceId = telephonyManager.getDeviceId();
        String userName;
        if (!TextUtils.isEmpty(deviceId) && deviceId.length() == 15) {
            userName = getString(R.string.user_name, deviceId.substring(7));
        } else {
            userName = "马儿";
        }
        imageWeather.setLocation(location);
        imageWeather.setCity(Utils.formatCity(location.getCity()));
        imageWeather.setUserName(userName);
        imageWeather.setPraise(0L);
        tvLocation.setText(location.getAddress());

        KeyboardUtils.showKeyboard(etSay);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_upload:
                upload();
                break;
        }
    }

    private void upload() {
        showProgress(getString(R.string.uploading_image));
        BmobFile file = new BmobFile(new File(path));
        file.uploadblock(new UploadFileListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    showProgress(getString(R.string.publishing));
                    imageWeather.setImageUrl(file.getFileUrl());
                    imageWeather.setSay(etSay.getText().toString());
                    imageWeather.setTag(tagLayout.getTag());
                    imageWeather.save(new SaveListener<String>() {
                        @Override
                        public void done(String objectId, BmobException e) {
                            if (e == null) {
                                cancelProgress();
                                Toast.makeText(UploadImageActivity.this, getString(R.string.publish_success,
                                        imageWeather.getLocation().getCity()), Toast.LENGTH_SHORT).show();
                                setResult(RESULT_OK);
                                finish();
                            } else {
                                Log.e(TAG, "upload object fail", e);
                                cancelProgress();
                                SnackbarUtils.show(UploadImageActivity.this, getString(R.string.publish_fail, e.getMessage()));
                            }
                        }
                    });
                } else {
                    Log.e(TAG, "upload image fail", e);
                    cancelProgress();
                    SnackbarUtils.show(UploadImageActivity.this, getString(R.string.upload_image_fail, e.getMessage()));
                }
            }
        });
    }
}
