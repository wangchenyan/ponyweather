package me.wcy.weather.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import cn.bmob.v3.listener.UpdateListener;
import me.wcy.weather.R;
import me.wcy.weather.constants.Extras;
import me.wcy.weather.constants.RequestCode;
import me.wcy.weather.model.bmob.ImageWeather;
import me.wcy.weather.utils.ScreenUtils;
import me.wcy.weather.utils.SystemUtils;
import me.wcy.weather.utils.binding.Bind;

public class ViewImageActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "ViewImageActivity";
    @Bind(R.id.iv_weather_image)
    private ImageView ivWeatherImage;
    @Bind(R.id.tv_location)
    private TextView tvLocation;
    @Bind(R.id.tv_user_name)
    private TextView tvUserName;
    @Bind(R.id.tv_say)
    private TextView tvSay;
    @Bind(R.id.tv_time)
    private TextView tvTime;
    @Bind(R.id.tv_tag)
    private TextView tvTag;
    @Bind(R.id.tv_praise)
    private TextView tvPraise;
    private ImageWeather mImageWeather;
    private ProgressDialog mProgressDialog;

    public static void start(Activity context, ImageWeather imageWeather) {
        Intent intent = new Intent(context, ViewImageActivity.class);
        intent.putExtra(Extras.IMAGE_WEATHER, imageWeather);
        context.startActivityForResult(intent, RequestCode.REQUEST_VIEW_IMAGE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_image);

        mImageWeather = (ImageWeather) getIntent().getSerializableExtra(Extras.IMAGE_WEATHER);
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage(getString(R.string.loading));
        mProgressDialog.setCancelable(false);

        ImageLoader.getInstance().loadImage(mImageWeather.getImageUrl(), SystemUtils.getDefaultDisplayOption()
                , new SimpleImageLoadingListener() {
                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        super.onLoadingComplete(imageUri, view, loadedImage);
                        int imageWidth = ScreenUtils.getScreenWidth() - ScreenUtils.dp2px(12) * 2;
                        int imageHeight = (int) ((float) loadedImage.getHeight() / (float) loadedImage.getWidth() * (float) imageWidth);
                        ivWeatherImage.setMinimumHeight(imageHeight);
                        ivWeatherImage.setImageBitmap(loadedImage);
                    }
                });

        tvLocation.setText(mImageWeather.getLocation().getAddress());
        tvUserName.setText(mImageWeather.getUserName());
        tvSay.setText(mImageWeather.getSay());
        tvSay.setVisibility(TextUtils.isEmpty(mImageWeather.getSay()) ? View.GONE : View.VISIBLE);
        tvTag.setText(getTagText(mImageWeather.getTag()));
        tvTag.setMovementMethod(LinkMovementMethod.getInstance());
        initTimeAndPraise();
    }

    @Override
    protected void setListener() {
        tvPraise.setOnClickListener(this);
    }

    private void initTimeAndPraise() {
        String time = SystemUtils.timeFormat(mImageWeather.getCreatedAt());
        tvTime.setText(getString(R.string.image_time_praise, time, mImageWeather.getPraise()));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_praise:
                praise();
                break;
        }
    }

    private void praise() {
        mProgressDialog.show();
        mImageWeather.increment("praise");
        mImageWeather.update(this, new UpdateListener() {
            @Override
            public void onSuccess() {
                mProgressDialog.cancel();
                mImageWeather.setPraise(mImageWeather.getPraise() + 1);
                initTimeAndPraise();

                Intent data = new Intent();
                data.putExtra(Extras.IMAGE_WEATHER, mImageWeather);
                setResult(RESULT_OK, data);
            }

            @Override
            public void onFailure(int i, String s) {
                Log.e(TAG, "praise fail. code:" + i + ",msg:" + s);
                mProgressDialog.cancel();
            }
        });
    }

    private Spanned getTagText(String tag) {
        int intColor = R.color.blue_300;
        switch (tag) {
            case "植物":
                intColor = R.color.green_300;
                break;
            case "人物":
                intColor = R.color.orange_300;
                break;
            case "天气":
                intColor = R.color.blue_300;
                break;
            case "建筑":
                intColor = R.color.cyan_300;
                break;
            case "动物":
                intColor = R.color.pink_300;
                break;
        }
        String strColor = String.format("#%06X", 0xFFFFFF & getResources().getColor(intColor));
        String html = "<font color='%1$s'>%2$s</font>";
        return Html.fromHtml(getString(R.string.image_tag, String.format(html, strColor, tag)));
    }
}
