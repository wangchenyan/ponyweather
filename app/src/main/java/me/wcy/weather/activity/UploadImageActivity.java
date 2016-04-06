package me.wcy.weather.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.File;

import butterknife.Bind;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UploadFileListener;
import me.wcy.weather.R;
import me.wcy.weather.model.ImageWeather;
import me.wcy.weather.utils.Extras;
import me.wcy.weather.utils.SnackbarUtils;

public class UploadImageActivity extends BaseActivity implements View.OnClickListener {
    @Bind(R.id.iv_weather_image)
    ImageView ivWeatherImage;
    @Bind(R.id.btn_upload)
    Button btnUpload;
    private String path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_image);

        path = getIntent().getStringExtra(Extras.IMAGE_PATH);
        Bitmap bitmap = BitmapFactory.decodeFile(path);
        ivWeatherImage.setImageBitmap(bitmap);
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

    private void upload() {
        final BmobFile file = new BmobFile(new File(path));
        file.upload(this, new UploadFileListener() {
            @Override
            public void onSuccess() {
                ImageWeather imageWeather = new ImageWeather();
                imageWeather.setUserName("王晨彦");
                imageWeather.setImageUrl(file.getFileUrl(UploadImageActivity.this));
                imageWeather.setLocation("浙江杭州");
                imageWeather.setTag("天气");
                imageWeather.setPraise(0L);
                imageWeather.save(UploadImageActivity.this, new SaveListener() {
                    @Override
                    public void onSuccess() {
                        SnackbarUtils.show(UploadImageActivity.this, "发布成功");
                    }

                    @Override
                    public void onFailure(int i, String s) {
                        SnackbarUtils.show(UploadImageActivity.this, "发布失败：" + i + "," + s);
                    }
                });
            }

            @Override
            public void onFailure(int i, String s) {
                SnackbarUtils.show(UploadImageActivity.this, "发布失败：" + i + "," + s);
            }
        });
    }
}
