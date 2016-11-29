package me.wcy.weather.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

import cn.bmob.v3.listener.UpdateListener;
import me.wcy.weather.R;
import me.wcy.weather.model.ImageWeather;
import me.wcy.weather.utils.ScreenUtils;
import me.wcy.weather.utils.Utils;
import me.wcy.weather.utils.binding.Bind;
import me.wcy.weather.utils.binding.ViewBinder;

public class ImageWeatherAdapter extends RecyclerView.Adapter<ImageWeatherAdapter.ViewHolder> implements View.OnClickListener {
    private static final String TAG = "ImageWeatherAdapter";
    private Context mContext;
    private List<ImageWeather> mImageList;
    private OnItemClickListener mClickListener;

    public ImageWeatherAdapter(List<ImageWeather> imageList) {
        mImageList = imageList;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mClickListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        View view = LayoutInflater.from(mContext).inflate(R.layout.view_holder_image_weather, parent, false);
        view.setOnClickListener(this);
        ViewHolder holder = new ViewHolder(view);
        holder.llPraiseContainer.setOnClickListener(this);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.item.setTag(mImageList.get(position));
        holder.llPraiseContainer.setTag(mImageList.get(position));
        holder.tvLocation.setText(mImageList.get(position).getLocation().getDistrict() + mImageList.get(position).getLocation().getStreet());
        holder.tvPraiseNum.setText(mImageList.get(position).getPraise() == 0L ? "" : String.valueOf(mImageList.get(position).getPraise()));
        ImageLoader.getInstance().displayImage(mImageList.get(position).getImageUrl(), holder.ivImage, Utils.getDefaultDisplayOption());
    }

    @Override
    public int getItemCount() {
        return mImageList.size();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.item:
                mClickListener.onItemClick(v, v.getTag());
                break;
            case R.id.ll_praise_container:
                ImageWeather imageWeather = (ImageWeather) v.getTag();
                praise(v, imageWeather);
                break;
        }
    }

    private void praise(final View v, final ImageWeather imageWeather) {
        imageWeather.increment("praise");
        imageWeather.update(mContext, new UpdateListener() {
            @Override
            public void onSuccess() {
                imageWeather.setPraise(imageWeather.getPraise() + 1);
                TextView tvPraiseNum = (TextView) v.findViewById(R.id.tv_praise_num);
                tvPraiseNum.setText(String.valueOf(imageWeather.getPraise()));
            }

            @Override
            public void onFailure(int i, String s) {
                Log.e(TAG, "praise fail. code:" + i + ",msg:" + s);
            }
        });
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.item)
        public CardView item;
        @Bind(R.id.iv_image)
        public ImageView ivImage;
        @Bind(R.id.tv_location)
        public TextView tvLocation;
        @Bind(R.id.ll_praise_container)
        public LinearLayout llPraiseContainer;
        @Bind(R.id.tv_praise_num)
        public TextView tvPraiseNum;

        public ViewHolder(View itemView) {
            super(itemView);
            ViewBinder.bind(this, itemView);
            int minHeight = ScreenUtils.getScreenWidth() / 2 - ScreenUtils.dp2px(4) * 2;
            ivImage.setMinimumHeight(minHeight);
        }
    }
}
