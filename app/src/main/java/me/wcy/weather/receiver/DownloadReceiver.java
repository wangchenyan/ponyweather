package me.wcy.weather.receiver;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.widget.Toast;

import java.io.File;

import me.wcy.weather.utils.FileUtils;

public class DownloadReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action.equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE)) {
            try {
                onDownloadSuccess(context, intent);
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(context, "自动安装失败，请进入/Download/目录下手动安装", Toast.LENGTH_SHORT).show();
            }
        } else if (action.equals(DownloadManager.ACTION_NOTIFICATION_CLICKED)) {
            // 点击通知取消下载
            DownloadManager manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
            long[] ids = intent.getLongArrayExtra(DownloadManager.EXTRA_NOTIFICATION_CLICK_DOWNLOAD_IDS);
            manager.remove(ids);
            Toast.makeText(context, "已取消下载", Toast.LENGTH_SHORT).show();
        }
    }

    private void onDownloadSuccess(Context context, Intent intent) {
        DownloadManager manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0);
        DownloadManager.Query query = new DownloadManager.Query();
        query.setFilterById(id);
        Cursor cursor = manager.query(query);
        if (cursor == null) {
            return;
        }

        if (cursor.moveToFirst()) {
            String fileUri = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
            if (fileUri != null) {
                File file = new File(Uri.parse(fileUri).getPath());
                if (file.exists()) {
                    install(context, file);
                }
            }
        }
        cursor.close();
    }

    private void install(Context context, File file) {
        Uri uri = FileUtils.getUriForFile(context, file);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(uri, "application/vnd.android.package-archive");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        context.startActivity(intent);
    }
}
