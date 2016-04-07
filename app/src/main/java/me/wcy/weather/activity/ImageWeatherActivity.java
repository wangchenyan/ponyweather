package me.wcy.weather.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;
import me.wcy.weather.R;
import me.wcy.weather.adapter.ImageWeatherAdapter;
import me.wcy.weather.model.ImageWeather;
import me.wcy.weather.utils.Extras;
import me.wcy.weather.utils.ImageUtils;
import me.wcy.weather.utils.RequestCode;
import me.wcy.weather.utils.SnackbarUtils;
import me.wcy.weather.utils.Utils;

public class ImageWeatherActivity extends BaseActivity implements View.OnClickListener {
    @Bind(R.id.rv_image)
    RecyclerView rvImage;
    @Bind(R.id.fab_add_photo)
    FloatingActionButton fabAddPhoto;
    private ProgressDialog mProgressDialog;
    private ImageWeatherAdapter mAdapter;
    private List<ImageWeather> mImageList = new ArrayList<>();
    private BmobQuery<ImageWeather> mQuery = new BmobQuery<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_weather);

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setCancelable(false);

        rvImage.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        mAdapter = new ImageWeatherAdapter(mImageList);
        rvImage.setAdapter(mAdapter);

        mQuery.addWhereEqualTo("city", "杭州");
        mQuery.setLimit(20);
        mQuery.order("-createdAt");
        mQuery.findObjects(this, new FindListener<ImageWeather>() {
            @Override
            public void onSuccess(List<ImageWeather> list) {
                mImageList.addAll(list);
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onError(int i, String s) {
                Log.e("query image fail", "code:" + i + ",msg:" + s);
            }
        });
    }

    @Override
    protected void setListener() {
        fabAddPhoto.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab_add_photo:
                if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                    SnackbarUtils.show(fabAddPhoto, "请确认已插入SD卡");
                    return;
                }
                ImageUtils.pickImage(this);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case RequestCode.REQUEST_CAMERA:
                compressImage(Utils.getCameraImagePath(this));
                break;
            case RequestCode.REQUEST_ALBUM:
                Uri uri = data.getData();
                compressImage(uri.getPath());
                break;
        }
    }

    private void compressImage(final String path) {
        File file = new File(path);
        if (!file.exists()) {
            SnackbarUtils.show(fabAddPhoto, "打开失败，请重试");
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
        // 自动旋转方向
        bitmap = autoRotate(path, bitmap);
        String savePath = save2File(bitmap);
        if (!TextUtils.isEmpty(savePath)) {
            Intent intent = new Intent(ImageWeatherActivity.this, UploadImageActivity.class);
            intent.putExtra(Extras.IMAGE_PATH, savePath);
            startActivityForResult(intent, RequestCode.REQUEST_CODE);
        }
    }

    /**
     * 图片自动旋转
     */
    private Bitmap autoRotate(String path, Bitmap source) {
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (exif == null) {
            return source;
        }
        int ori = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
        if (ori == ExifInterface.ORIENTATION_NORMAL) {
            return source;
        }
        int degree = 0;
        switch (ori) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                degree = 90;
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                degree = 180;
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                degree = 270;
                break;
        }
        // 旋转图片
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    private String save2File(Bitmap bitmap) {
        String path = Utils.getCutImagePath(ImageWeatherActivity.this);
        FileOutputStream stream = null;
        Bitmap.CompressFormat format = Bitmap.CompressFormat.JPEG;
        int quality = 90;
        try {
            stream = new FileOutputStream(path);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        if (bitmap.compress(format, quality, stream)) {
            return path;
        } else {
            return null;
        }
    }
}
