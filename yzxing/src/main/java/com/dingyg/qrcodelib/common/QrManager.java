package com.dingyg.qrcodelib.common;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.provider.MediaStore.Images.Media;
import android.support.annotation.NonNull;

import com.dingyg.qrcodelib.zxing.CaptureActivity;
import com.github.dfqin.grantor.PermissionListener;
import com.github.dfqin.grantor.PermissionsUtil;
import com.soonfor.repository.R;

/**
 * 条码/二维码 管理工具类
 */
public class QrManager {
    private static final String TAG = QrManager.class.getSimpleName();
    public static final int PHOTO_REQUEST_GALLERY = 1000;
    public static final int PHOTO_REQUEST_CAMERA = 1001;
    public static final int PHOTO_REQUEST_CUT = 1002;
    private static final int CAMERA_PERMISSION = 110;//相机权限
    private int requestCode = 0;//当前请求view的代号

    private static QrManager instance;

    public static QrManager getInstance() {
        if (instance == null) {
            synchronized (QrManager.class) {
                if (instance == null) {
                    instance = new QrManager();
                }
            }
        }
        return instance;
    }

    //打开摄像头
    public void openCamera(Activity activity, int requestCode) {
        PermissionsUtil.requestPermission(activity, new PermissionListener() {
                    @Override
                    public void permissionGranted(@NonNull String[] permissions) {
                        QrManager.getInstance().requestCode = requestCode;
                        CaptureActivity.startCaptureActivity(activity, CaptureActivity.REQUEST_PERMISSION_CAMERA);
                    }

                    @Override
                    public void permissionDenied(@NonNull String[] permissions) {
                        AlertDialog dialog = new AlertDialog.Builder(activity)
                                .setTitle("提示")
                                .setMessage("您拒绝了相机必要权限，无法使用扫码功能！")
                                .setPositiveButton("关闭", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                }).create();
                        dialog.show();
                    }
                }, Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }

    //打开相册
    public void openGallery(Activity activity) {
        PermissionsUtil.requestPermission(activity, new PermissionListener() {
                    @Override
                    public void permissionGranted(@NonNull String[] permissions) {
                        Intent i = new Intent("android.intent.action.PICK", Media.EXTERNAL_CONTENT_URI);
                        activity.startActivityForResult(i, CaptureActivity.REQUEST_PERMISSION_PHOTO);
                    }

                    @Override
                    public void permissionDenied(@NonNull String[] permissions) {
                        AlertDialog dialog = new AlertDialog.Builder(activity)
                                .setTitle("提示")
                                .setMessage("您拒绝了读取权限，无法使用相册功能！")
                                .setPositiveButton("关闭", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                }).create();
                        dialog.show();
                    }
                },
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }

    //获取扫码结果
    public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data, Callback callback) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == CaptureActivity.REQUEST_PERMISSION_PHOTO) {
                ProgressDialog mProgress = new ProgressDialog(activity, R.style.theme_customer_progress_dialog);
                mProgress.setMessage("正在扫描...");
                mProgress.setCancelable(false);
                mProgress.show();
                String imgResult = QrUtils.getImgResult(activity, data);
                if (mProgress != null && mProgress.isShowing()){mProgress.dismiss();}
                if (imgResult == null || imgResult.trim().equals("")) {
                    if (callback != null) callback.resultFail("无法识别该图片内容！");
                } else {
                    if (callback != null) callback.resultSuccess(imgResult.trim());
                }
            }else if (requestCode == CaptureActivity.REQUEST_PERMISSION_CAMERA) {
                String scanResult = QrUtils.getScanResult(data);
                if (scanResult == null || scanResult.trim().equals("")) {
                    if (callback != null) callback.resultFail("未识别到二维码/条码内容！");
                } else {
                    if (callback != null) callback.resultSuccess(scanResult.trim());
                }
            }
        }
    }

    public int getRequestCode() {
        return requestCode;
    }

    public interface Callback{
        //扫码成功
        void resultSuccess(String barcode);
        //扫码失败
        void resultFail(String msg);
    }
}
