package me.wcy.weather.activity;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
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
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import me.wcy.weather.R;
import me.wcy.weather.adapter.ImageWeatherAdapter;
import me.wcy.weather.adapter.OnItemClickListener;
import me.wcy.weather.adapter.StaggeredGridSpacingItemDecoration;
import me.wcy.weather.application.LoadMoreListener;
import me.wcy.weather.constants.Extras;
import me.wcy.weather.constants.RequestCode;
import me.wcy.weather.model.ImageWeather;
import me.wcy.weather.model.Location;
import me.wcy.weather.utils.FileUtils;
import me.wcy.weather.utils.ImageUtils;
import me.wcy.weather.utils.PermissionReq;
import me.wcy.weather.utils.ScreenUtils;
import me.wcy.weather.utils.SnackbarUtils;
import me.wcy.weather.utils.Utils;
import me.wcy.weather.utils.binding.Bind;

public class ImageWeatherActivity extends BaseActivity implements View.OnClickListener
        , SwipeRefreshLayout.OnRefreshListener, AMapLocationListener, LoadMoreListener.Listener
        , OnItemClickListener {
    private static final String TAG = "ImageWeatherActivity";
    private static final int QUERY_LIMIT = 20;
    @Bind(R.id.appbar)
    private AppBarLayout mAppBar;
    @Bind(R.id.swipe_refresh_layout)
    private SwipeRefreshLayout mRefreshLayout;
    @Bind(R.id.rv_image)
    private RecyclerView rvImage;
    @Bind(R.id.fam_add_photo)
    private FloatingActionsMenu famAddPhoto;
    @Bind(R.id.fab_camera)
    private FloatingActionButton fabCamera;
    @Bind(R.id.fab_album)
    private FloatingActionButton fabAlbum;
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
        rvImage.addItemDecoration(new StaggeredGridSpacingItemDecoration(ScreenUtils.dp2px(8)));
        rvImage.setAdapter(mAdapter);

        mQuery.setLimit(QUERY_LIMIT);
        mQuery.order("-createdAt");

        mLocationClient = Utils.initAMapLocation(this, this);
        mLocationClient.startLocation();

        Utils.setRefreshingOnCreate(mRefreshLayout);
    }

    @Override
    protected void setListener() {
        mRefreshLayout.setOnRefreshListener(this);
        rvImage.addOnScrollListener(mLoadMoreListener);
        mAdapter.setOnItemClickListener(this);
        fabCamera.setOnClickListener(this);
        fabAlbum.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        famAddPhoto.collapse();
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

                String city = Utils.formatCity(mLocation.getCity());
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
        mQuery.findObjects(new FindListener<ImageWeather>() {
            @Override
            public void done(List<ImageWeather> list, BmobException e) {
                if (e == null) {
                    mImageList.clear();
                    mImageList.addAll(list);
                    mAdapter.notifyDataSetChanged();
                    mRefreshLayout.setRefreshing(false);
                    mLoadMoreListener.setEnableLoadMore(true);
                    famAddPhoto.setVisibility(View.VISIBLE);
                } else {
                    Log.e(TAG, "query image fail", e);
                    mRefreshLayout.setRefreshing(false);
                    SnackbarUtils.show(ImageWeatherActivity.this, R.string.refresh_fail);
                }
            }
        });
    }

    @Override
    public void onLoadMore() {
        mQuery.setSkip(mImageList.size());
        mQuery.findObjects(new FindListener<ImageWeather>() {
            @Override
            public void done(List<ImageWeather> list, BmobException e) {
                if (e == null) {
                    mLoadMoreListener.onLoadComplete();
                    if (!list.isEmpty()) {
                        mImageList.addAll(list);
                        mAdapter.notifyDataSetChanged();
                    } else {
                        mLoadMoreListener.setEnableLoadMore(false);
                        SnackbarUtils.show(ImageWeatherActivity.this, R.string.no_more);
                    }
                } else {
                    Log.e(TAG, "query image fail", e);
                    mLoadMoreListener.onLoadComplete();
                    SnackbarUtils.show(ImageWeatherActivity.this, R.string.load_fail);
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab_camera:
                pickImage(ImageUtils.PickType.CAMERA);
                break;
            case R.id.fab_album:
                pickImage(ImageUtils.PickType.ALBUM);
                break;
        }
    }

    private void pickImage(final ImageUtils.PickType type) {
        if (!FileUtils.hasSDCard()) {
            SnackbarUtils.show(this, R.string.no_sdcard);
            return;
        }

        PermissionReq.with(this)
                .permissions(Manifest.permission.READ_PHONE_STATE,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .result(new PermissionReq.Result() {
                    @Override
                    public void onGranted() {
                        ImageUtils.pickImage(ImageWeatherActivity.this, type);
                    }

                    @Override
                    public void onDenied() {
                        SnackbarUtils.show(ImageWeatherActivity.this, getString(R.string.no_permission, "获取手机信息", "上传实景"));
                    }
                })
                .request();
    }

    @Override
    public void onItemClick(View view, Object data) {
        ImageWeather imageWeather = (ImageWeather) data;
        ActivityOptionsCompat activityOptions = ActivityOptionsCompat.makeSceneTransitionAnimation(this,
                new Pair<>(view.findViewById(R.id.iv_image), Extras.VIEW_NAME_WEATHER_IMAGE));
        ViewImageActivity.start(this, imageWeather, activityOptions);
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
                compressImage(FileUtils.uriToPath(this, uri));
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
            SnackbarUtils.show(this, R.string.image_open_fail);
            return;
        }

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        int inSampleSize;
        int max = Math.max(options.outWidth, options.outHeight);
        inSampleSize = max / 800;
        options.inJustDecodeBounds = false;
        options.inSampleSize = inSampleSize;
        Bitmap bitmap = BitmapFactory.decodeFile(path, options);
        bitmap = ImageUtils.autoRotate(path, bitmap);
        String savePath = ImageUtils.save2File(this, bitmap);
        if (!TextUtils.isEmpty(savePath)) {
            UploadImageActivity.start(this, mLocation, savePath);
        } else {
            SnackbarUtils.show(this, R.string.image_save_fail);
        }
    }

    @Override
    protected void onDestroy() {
        mLocationClient.onDestroy();
        super.onDestroy();
    }
}
