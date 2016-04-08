package me.wcy.weather.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import me.wcy.weather.R;
import me.wcy.weather.model.ImageWeather;
import me.wcy.weather.utils.ScreenUtils;

/**
 * Created by hzwangchenyan on 2016/4/7.
 */
public class ImageWeatherAdapter extends RecyclerView.Adapter<ImageWeatherAdapter.ViewHolder> {
    private Context mContext;
    private List<ImageWeather> mImageList;

    public ImageWeatherAdapter(List<ImageWeather> imageList) {
        mImageList = imageList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        View view = LayoutInflater.from(mContext).inflate(R.layout.view_holder_image_weather, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();
        ImageLoader.getInstance().displayImage(mImageList.get(position).getImageUrl(), holder.ivImage, options);
        holder.tvLocation.setText(mImageList.get(position).getLocation().getDistrict() + mImageList.get(position).getLocation().getStreet());
    }

    @Override
    public int getItemCount() {
        return mImageList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.item)
        public CardView item;
        @Bind(R.id.iv_image)
        public ImageView ivImage;
        @Bind(R.id.tv_location)
        public TextView tvLocation;
        @Bind(R.id.iv_praise)
        public ImageView ivPraise;
        @Bind(R.id.tv_praise_num)
        public TextView tvPraiseNum;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            int minHeight = ScreenUtils.getScreenWidth() / 2 - ScreenUtils.dp2px(4) * 2;
            ivImage.setMinimumHeight(minHeight);
        }
    }
}
