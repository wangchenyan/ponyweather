package me.wcy.weather.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import butterknife.Bind;
import me.wcy.weather.R;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_weather);

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setCancelable(false);
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
        String savePath = Utils.getCutImagePath(ImageWeatherActivity.this);
        FileOutputStream stream = null;
        Bitmap.CompressFormat format = Bitmap.CompressFormat.JPEG;
        int quality = 90;
        try {
            stream = new FileOutputStream(savePath);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        boolean success = bitmap.compress(format, quality, stream);
        if (success) {
            Intent intent = new Intent(ImageWeatherActivity.this, UploadImageActivity.class);
            intent.putExtra(Extras.IMAGE_PATH, savePath);
            startActivity(intent);
        }
    }
}
