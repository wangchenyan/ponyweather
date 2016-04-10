package me.wcy.weather.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationListener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;
import me.wcy.weather.R;
import me.wcy.weather.adapter.ImageWeatherAdapter;
import me.wcy.weather.adapter.LoadMoreListener;
import me.wcy.weather.adapter.OnItemClickListener;
import me.wcy.weather.model.ImageWeather;
import me.wcy.weather.model.Location;
import me.wcy.weather.utils.Extras;
import me.wcy.weather.utils.FileUtils;
import me.wcy.weather.utils.ImageUtils;
import me.wcy.weather.utils.RequestCode;
import me.wcy.weather.utils.SnackbarUtils;
import me.wcy.weather.utils.SystemUtils;

public class ImageWeatherActivity extends BaseActivity implements View.OnClickListener
        , SwipeRefreshLayout.OnRefreshListener, AMapLocationListener, LoadMoreListener.Listener
        , OnItemClickListener {
    private static final String TAG = "ImageWeatherActivity";
    private static final int LIMIT = 20;
    @Bind(R.id.appbar)
    AppBarLayout mAppBar;
    @Bind(R.id.swipe_refresh_layout)
    SwipeRefreshLayout mRefreshLayout;
    @Bind(R.id.rv_image)
    RecyclerView rvImage;
    @Bind(R.id.fab_add_photo)
    FloatingActionButton fabAddPhoto;
    private ImageWeatherAdapter mAdapter;
    private LoadMoreListener mLoadMoreListener;
    private List<ImageWeather> mImageList = new ArrayList<>();
    private BmobQuery<ImageWeather> mQuery = new BmobQuery<>();
    private AMapLocationClient mLocationClient;
    private Location mLocation = new Location();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_weather);

        mAdapter = new ImageWeatherAdapter(mImageList);
        mLoadMoreListener = new LoadMoreListener(this);
        rvImage.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        rvImage.setAdapter(mAdapter);

        mQuery.setLimit(LIMIT);
        mQuery.order("-createdAt");

        mLocationClient = SystemUtils.initAMapLocation(this, this);
        mLocationClient.startLocation();

        SystemUtils.setRefreshingOnCreate(mRefreshLayout);
    }

    @Override
    protected void setListener() {
        mRefreshLayout.setOnRefreshListener(this);
        rvImage.setOnScrollListener(mLoadMoreListener);
        fabAddPhoto.setOnClickListener(this);
        mAdapter.setOnItemClickListener(this);
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (aMapLocation != null) {
            mLocationClient.stopLocation();
            if (aMapLocation.getErrorCode() == 0 && !TextUtils.isEmpty(aMapLocation.getCity())) {
                // 定位成功回调信息，设置相关消息
                mLocation.setAddress(aMapLocation.getAddress());
                mLocation.setCountry(aMapLocation.getCountry());
                mLocation.setProvince(aMapLocation.getProvince());
                mLocation.setCity(aMapLocation.getCity());
                mLocation.setDistrict(aMapLocation.getDistrict());
                mLocation.setStreet(aMapLocation.getStreet());
                mLocation.setStreetNum(aMapLocation.getStreetNum());

                String city = mLocation.getCity().replace("市", "");
                mQuery.addWhereEqualTo("city", city);
                onRefresh();
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

    @Override
    public void onRefresh() {
        mQuery.setSkip(0);
        mQuery.findObjects(this, new FindListener<ImageWeather>() {
            @Override
            public void onSuccess(List<ImageWeather> list) {
                mImageList.clear();
                mImageList.addAll(list);
                mAdapter.notifyDataSetChanged();
                mRefreshLayout.setRefreshing(false);
                mLoadMoreListener.setEnableLoadMore(true);
            }

            @Override
            public void onError(int i, String s) {
                Log.e(TAG, "query image fail. code:" + i + ",msg:" + s);
                mRefreshLayout.setRefreshing(false);
                SnackbarUtils.show(fabAddPhoto, R.string.refresh_fail);
            }
        });
    }

    @Override
    public void onLoadMore() {
        mQuery.setSkip(mImageList.size());
        mQuery.findObjects(this, new FindListener<ImageWeather>() {
            @Override
            public void onSuccess(List<ImageWeather> list) {
                mLoadMoreListener.onLoadComplete();
                if (!list.isEmpty()) {
                    mImageList.addAll(list);
                    mAdapter.notifyDataSetChanged();
                } else {
                    mLoadMoreListener.setEnableLoadMore(false);
                    SnackbarUtils.show(fabAddPhoto, R.string.no_more);
                }
            }

            @Override
            public void onError(int i, String s) {
                Log.e(TAG, "query image fail. code:" + i + ",msg:" + s);
                mLoadMoreListener.onLoadComplete();
                SnackbarUtils.show(fabAddPhoto, R.string.load_fail);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab_add_photo:
                ImageUtils.pickImage(this);
                break;
        }
    }

    @Override
    public void onItemClick(View view, Object data) {
        ImageWeather imageWeather = (ImageWeather) data;
        ViewImageActivity.start(this, imageWeather);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case RequestCode.REQUEST_CAMERA:
                compressImage(FileUtils.getCameraImagePath(this));
                break;
            case RequestCode.REQUEST_ALBUM:
                Uri uri = data.getData();
                compressImage(uri.getPath());
                break;
            case RequestCode.REQUEST_UPLOAD:
                rvImage.scrollToPosition(0);
                mAppBar.setExpanded(true, false);
                mRefreshLayout.setRefreshing(true);
                onRefresh();
                break;
            case RequestCode.REQUEST_VIEW_IMAGE:
                ImageWeather imageWeather = (ImageWeather) data.getSerializableExtra(Extras.IMAGE_WEATHER);
                for (ImageWeather weather : mImageList) {
                    if (weather.getObjectId().equals(imageWeather.getObjectId())) {
                        weather.setPraise(imageWeather.getPraise());
                        break;
                    }
                }
                mAdapter.notifyDataSetChanged();
                break;
        }
    }

    private void compressImage(final String path) {
        File file = new File(path);
        if (!file.exists()) {
            SnackbarUtils.show(fabAddPhoto, R.string.image_open_fail);
            return;
        }
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        int width = options.outWidth;
        int height = options.outHeight;
        int inSampleSize;
        int max = width > height ? width : height;
        inSampleSize = max / 800;
        options.inJustDecodeBounds = false;
        options.inSampleSize = inSampleSize;
        Bitmap bitmap = BitmapFactory.decodeFile(path, options);
        bitmap = ImageUtils.autoRotate(path, bitmap);
        String savePath = ImageUtils.save2File(this, bitmap);
        if (!TextUtils.isEmpty(savePath)) {
            UploadImageActivity.start(this, mLocation, savePath);
        } else {
            SnackbarUtils.show(fabAddPhoto, R.string.image_save_fail);
        }
    }

    @Override
    protected void onDestroy() {
        mLocationClient.onDestroy();
        super.onDestroy();
    }
}
