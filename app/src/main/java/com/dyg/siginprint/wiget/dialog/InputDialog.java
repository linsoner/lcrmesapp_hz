package com.dyg.siginprint.wiget.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.dyg.siginprint.R;
import com.dyg.siginprint.wiget.textview.MyEditText;

public class InputDialog implements View.OnClickListener{
    private AlertDialog dialog;
    private LayoutInflater inflater;
    private Activity activity;
    private View dialogView;
    private Callback callback;
    private TextView title_txt;
    private MyEditText input_et;
    private Button cancelBtn;
    private Button saveBtn;

    public InputDialog(Activity activity , Callback callback){
        this.activity = activity;
        this.callback = callback;
        inflater = LayoutInflater.from(activity);
        dialogView = inflater.inflate(R.layout.view_dialog_input, null);
        title_txt = dialogView.findViewById(R.id.title_txt);
        input_et = dialogView.findViewById(R.id.input_et);
        input_et.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        cancelBtn = dialogView.findViewById(R.id.cancel);
        saveBtn = dialogView.findViewById(R.id.save);
        setButtonListener(dialogView,R.id.cancel,"取消",this);
        setButtonListener(dialogView,R.id.save,"确定",this);
        dialog = new AlertDialog.Builder(activity, R.style.my_style_dialog).create();
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
    }

    public void show(String title,String inputContent){
        dialog.show();
        dialog.getWindow().setContentView(dialogView);
        dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        WindowManager windowManager = (WindowManager) activity.getSystemService(activity.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
        lp.width = display.getWidth(); //设置宽度
        lp.height = (int)(display.getHeight() * 0.6);
        dialog.getWindow().setAttributes(lp);
        title_txt.setText(title);
        input_et.setText(inputContent);
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

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.cancel){
            dismiss();
            if(callback != null){
                callback.finish(false,input_et.getText());
            }
        }else if(view.getId() == R.id.save){
            dismiss();
            if(callback != null){
                callback.finish(true,input_et.getText());
            }
        }
    }

    public static Button setButtonListener(View view, int btnId, String btnText, View.OnClickListener listener) {
        Button button = (Button) view.findViewById(btnId);
        button.setText(btnText);
        button.setOnClickListener(listener);
        return button;
    }


    public interface Callback{
        void finish(Boolean isConfirm , String inputContent);
    }
}
