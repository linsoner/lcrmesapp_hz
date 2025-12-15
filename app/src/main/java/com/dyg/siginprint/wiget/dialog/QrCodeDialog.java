package com.dyg.siginprint.wiget.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.dingyg.qrcodelib.common.QrManager;
import com.dyg.siginprint.R;

/**
 * Created by Administrator on 2018/1/11 0011.
 */

public class QrCodeDialog implements View.OnClickListener {
    private AlertDialog dialog;
    private LayoutInflater inflater;
    private Activity activity;
    private View dialogView;
    private QrManager.Callback callback;
    public static final int REQUEST_PERMISSION_CAMERA = 3999; //拍照返回
    public static final int REQUEST_PERMISSION_PHOTO = 4000;//选择照片返回

    public QrCodeDialog(Activity activity, QrManager.Callback callback) {
        this.activity = activity;
        this.callback = callback;
        inflater = LayoutInflater.from(activity);
        dialogView = inflater.inflate(R.layout.view_dialog_qr_code, null);
        setButtonListener(dialogView, R.id.camera, "相机扫描外部二维码", this);
        setButtonListener(dialogView, R.id.choose_from_album, "本地二维码图片识别", this);
        setButtonListener(dialogView, R.id.cancel, "取消", this);
        dialog = new AlertDialog.Builder(activity, R.style.my_style_dialog).create();
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
    }


    /**
     * 显示对话框
     */
    public void show() {
        dialog.show();
        dialog.getWindow().setContentView(dialogView);
        dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        WindowManager windowManager = (WindowManager) activity.getSystemService(activity.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
        lp.width = display.getWidth(); //设置宽度
        dialog.getWindow().setAttributes(lp);
    }

    public void setGravityBottom() {
        dialog.getWindow().setGravity(Gravity.BOTTOM);
    }

    /**
     * 隐藏对话框
     */
    public void dismiss() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    /**
     * 判断对话框是否显示
     */
    public boolean isShowing() {
        if (dialog != null) {
            return dialog.isShowing();
        }
        return false;
    }

    /**
     * 设置对话框点击对话框以外的范围是否可以关闭对话框
     */
    public void setCancelable(boolean bool) {
        dialog.setCancelable(bool);
    }

    @Override
    public void onClick(View v) {
        dialog.dismiss();
        int id = v.getId();
        if (id == R.id.camera) {
            //打开摄像头
            QrManager.getInstance().openCamera(activity, 0);
        } else if (id == R.id.choose_from_album) {
            //打开相册
            QrManager.getInstance().openGallery(activity);
        }
    }

    public static Button setButtonListener(View view, int btnId, String btnText, View.OnClickListener listener) {
        Button button = (Button) view.findViewById(btnId);
        button.setText(btnText);
        button.setOnClickListener(listener);
        return button;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        QrManager.getInstance().onActivityResult(activity, requestCode, resultCode, data, callback);
    }

}