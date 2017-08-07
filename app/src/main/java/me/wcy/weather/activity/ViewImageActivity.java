package me.wcy.weather.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import cn.bmob.v3.listener.UpdateListener;
import me.wcy.weather.R;
import me.wcy.weather.constants.Extras;
import me.wcy.weather.constants.RequestCode;
import me.wcy.weather.model.ImageWeather;
import me.wcy.weather.utils.ScreenUtils;
import me.wcy.weather.utils.Utils;
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

    public static void start(Activity activity, ImageWeather imageWeather, ActivityOptionsCompat activityOptions) {
        Intent intent = new Intent(activity, ViewImageActivity.class);
        intent.putExtra(Extras.IMAGE_WEATHER, imageWeather);
        ActivityCompat.startActivityForResult(activity, intent, RequestCode.REQUEST_VIEW_IMAGE, activityOptions.toBundle());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_image);

        mImageWeather = (ImageWeather) getIntent().getSerializableExtra(Extras.IMAGE_WEATHER);
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage(getString(R.string.loading));
        mProgressDialog.setCancelable(false);

        ViewCompat.setTransitionName(ivWeatherImage, Extras.VIEW_NAME_WEATHER_IMAGE);

        tvLocation.setText(mImageWeather.getLocation().getAddress());
        tvUserName.setText(mImageWeather.getUserName());
        tvSay.setText(mImageWeather.getSay());
        tvSay.setVisibility(TextUtils.isEmpty(mImageWeather.getSay()) ? View.GONE : View.VISIBLE);
        tvTag.setText(getTagText(mImageWeather.getTag()));
        tvTag.setMovementMethod(LinkMovementMethod.getInstance());
        setTimeAndPraise();

        final int imageWidth = ScreenUtils.getScreenWidth() - ScreenUtils.dp2px(12) * 2;
        ivWeatherImage.setMinimumHeight(imageWidth);
        Glide.with(this)
                .load(mImageWeather.getImageUrl())
                .asBitmap()
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        int imageHeight = (int) ((float) resource.getHeight() / resource.getWidth() * imageWidth);
                        ivWeatherImage.setMinimumHeight(imageHeight);
                        ivWeatherImage.setImageBitmap(resource);
                    }
                });
    }

    @Override
    protected void setListener() {
        tvPraise.setOnClickListener(this);
    }

    private void setTimeAndPraise() {
        String time = Utils.timeFormat(mImageWeather.getCreatedAt());
        TypedValue typedValue = new TypedValue();
        getTheme().resolveAttribute(R.attr.colorAccent, typedValue, true);
        int color = ContextCompat.getColor(this, typedValue.resourceId);
        ForegroundColorSpan colorSpan = new ForegroundColorSpan(color);
        SpannableStringBuilder ssb = new SpannableStringBuilder();
        ssb.append(time).append("拍摄").append("  ");
        int start = ssb.length();
        ssb.append(String.valueOf(mImageWeather.getPraise()));
        ssb.setSpan(colorSpan, start, ssb.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ssb.append("个赞");
        tvTime.setText(ssb);
        tvTime.setMovementMethod(LinkMovementMethod.getInstance());
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
                setTimeAndPraise();

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

    private CharSequence getTagText(String tag) {
        int color = R.color.blue_300;
        switch (tag) {
            case "植物":
                color = R.color.green_300;
                break;
            case "人物":
                color = R.color.orange_300;
                break;
            case "天气":
                color = R.color.blue_300;
                break;
            case "建筑":
                color = R.color.cyan_300;
                break;
            case "动物":
                color = R.color.pink_300;
                break;
        }
        ForegroundColorSpan colorSpan = new ForegroundColorSpan(ContextCompat.getColor(this, color));
        SpannableStringBuilder ssb = new SpannableStringBuilder();
        ssb.append("标签  ");
        int start = ssb.length();
        ssb.append(tag);
        ssb.setSpan(colorSpan, start, ssb.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return ssb;
    }
}
