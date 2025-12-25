package com.dyg.siginprint.login.view;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.support.v4.content.FileProvider;
import android.widget.Toast;

import java.io.File;

public class ApkDownloadInstallUtils {
    // 权限请求码
    public static final int REQUEST_INSTALL_PERMISSION = 1001;
    public static final int REQUEST_STORAGE_PERMISSION = 1002;
    // FileProvider授权（和项目配置一致）
    private static final String FILE_PROVIDER_AUTHORITY = "com.dyg.siginprint.updatefileprovider";
    // APK文件名
    private static final String APK_FILE_NAME = "app-release.apk";

    private final Context mContext;
    private final DownloadManager mDownloadManager;
    private long mDownloadId = -1;
    private OnDownloadListener mOnDownloadListener;

    // 下载完成广播接收器
    private final BroadcastReceiver mDownloadCompleteReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            long completedId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            if (completedId == mDownloadId && mDownloadId != -1) {
                installApk();
                if (mOnDownloadListener != null) {
                    mOnDownloadListener.onDownloadComplete();
                }
            }
        }
    };

    public ApkDownloadInstallUtils(Context context) {
        this.mContext = context.getApplicationContext();
        this.mDownloadManager = (DownloadManager) mContext.getSystemService(Context.DOWNLOAD_SERVICE);
        // 注册广播
        mContext.registerReceiver(mDownloadCompleteReceiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }

    /**
     * 开始下载APK（自动检查权限）
     * @param downloadUrl APK下载地址
     */
    public void startDownload(String downloadUrl) {
        // 检查6.0+存储权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (mContext.checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                if (mOnDownloadListener != null) {
                    mOnDownloadListener.onNeedStoragePermission();
                }
                return;
            }
        }

        // 检查8.0+安装权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            boolean hasInstallPerm = mContext.getPackageManager().canRequestPackageInstalls();
            if (!hasInstallPerm) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES,
                        Uri.parse("package:" + mContext.getPackageName()));
                if (mOnDownloadListener != null) {
                    mOnDownloadListener.onNeedInstallPermission(intent);
                }
                return;
            }
        }

        // 权限全部通过，发起下载
        realStartDownload(downloadUrl);
    }

    /**
     * 实际发起下载请求
     */
    private void realStartDownload(String downloadUrl) {
        if (mDownloadManager == null) {
            Toast.makeText(mContext, "下载服务初始化失败", Toast.LENGTH_SHORT).show();
            return;
        }

        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(downloadUrl));
        // 保存路径：外部存储Download目录
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, APK_FILE_NAME);
        // 通知栏显示下载进度
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        // 文件类型
        request.setMimeType("application/vnd.android.package-archive");
        // 下载标题/描述
        request.setTitle("APP更新");
        request.setDescription("正在下载最新版本...");
        // 允许的网络类型（可选：仅WiFi/任意网络）
        // request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI);

        // 执行下载
        mDownloadId = mDownloadManager.enqueue(request);
        Toast.makeText(mContext, "开始下载，请稍等", Toast.LENGTH_SHORT).show();
        if (mOnDownloadListener != null) {
            mOnDownloadListener.onDownloadStart();
        }
    }

    /**
     * 安装下载完成的APK
     */
    private void installApk() {
        File apkFile = new File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                APK_FILE_NAME
        );

        if (!apkFile.exists()) {
            Toast.makeText(mContext, "APK文件不存在", Toast.LENGTH_SHORT).show();
            if (mOnDownloadListener != null) {
                mOnDownloadListener.onInstallFailed("文件不存在");
            }
            return;
        }

        Intent installIntent = new Intent(Intent.ACTION_VIEW);
        Uri apkUri;

        // 7.0+适配FileProvider
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            apkUri = FileProvider.getUriForFile(mContext, FILE_PROVIDER_AUTHORITY, apkFile);
            installIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } else {
            apkUri = Uri.fromFile(apkFile);
        }

        installIntent.setDataAndType(apkUri, "application/vnd.android.package-archive");
        installIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(installIntent);

        if (mOnDownloadListener != null) {
            mOnDownloadListener.onInstallStart();
        }
    }

    /**
     * 释放资源（必须调用）
     */
    public void release() {
        try {
            mContext.unregisterReceiver(mDownloadCompleteReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
        mOnDownloadListener = null;
    }

    /**
     * 设置下载监听
     */
    public void setOnDownloadListener(OnDownloadListener listener) {
        this.mOnDownloadListener = listener;
    }

    /**
     * 下载监听接口
     */
    public interface OnDownloadListener {
        void onDownloadStart(); // 下载开始
        void onDownloadComplete(); // 下载完成
        void onInstallStart(); // 安装开始
        void onInstallFailed(String reason); // 安装失败
        void onNeedStoragePermission(); // 需要存储权限
        void onNeedInstallPermission(Intent intent); // 需要安装权限
    }
}
