package com.dyg.siginprint.base.tools;

import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.dyg.siginprint.R;

/**
 * author : DC-DingYG
 * e-mail : dingyg012655@126.com
 * time : 2021/07/17
 * desc : *
 */
public class ToastUtil {
    /**
     * 提示框
     *
     * @param activity
     * @return
     */
    public static void show(Context activity, String text) {
        try {
            View view = LayoutInflater.from(activity).inflate(R.layout.view_dialog, null);
            final Dialog dialog = new Dialog(activity, R.style.dialog);
            dialog.setContentView(view);
            dialog.setCancelable(false);
            TextView textView = view.findViewById(R.id.message_content);
            textView.setText(text);
            Button okButton = (Button) view.findViewById(R.id.save);
            okButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (dialog != null) dialog.dismiss();
                }
            });
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (dialog != null) dialog.dismiss();
                }
            }, 2500);
            dialog.show();
        }catch (Exception e){
            e.fillInStackTrace();
        }
    }

    /**
     * 提示框
     *
     * @param activity
     * @param isAutoClose 是否自动关闭
     * @return
     */
    public static void show(Context activity, String text, boolean isAutoClose) {
        try{
            SoundUtil.getInstense(activity).pay(R.raw.qmeserror);
        }catch (Exception e){}
        try {
            View view = LayoutInflater.from(activity).inflate(
                    R.layout.view_dialog, null);
            final Dialog dialog = new Dialog(activity, R.style.dialog);
            dialog.setContentView(view);
            dialog.setCancelable(false);
            TextView textView = view.findViewById(R.id.message_content);
            textView.setText(text);
            Button okButton = (Button) view.findViewById(R.id.save);
            okButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (dialog != null) dialog.dismiss();
                }
            });
            if(isAutoClose){
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (dialog != null) dialog.dismiss();
                    }
                }, 2000);
            }
            dialog.show();
        }catch (Exception e){
            e.fillInStackTrace();
        }
    }
    /**
     * 提示框
     *
     * @param activity
     * @return
     */
    public static void showToast(Context activity, String text) {
        try {
            if (text != null) Toast.makeText(activity, text, Toast.LENGTH_SHORT).show();
        }catch (Exception e){
            e.fillInStackTrace();
        }
    }
}
