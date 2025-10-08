package com.dyg.siginprint.wiget.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.dyg.siginprint.R;
import com.dyg.siginprint.wiget.dialog.adapter.ListDialogAdapter;
import com.dyg.siginprint.wiget.dialog.model.BaseDataModel;
import com.dyg.siginprint.wiget.textview.ClearEditText;

import java.util.ArrayList;
import java.util.List;

public class ListDialog implements View.OnClickListener,ClearEditText.Listener {
    private AlertDialog dialog;
    private LayoutInflater inflater;
    private Activity activity;
    private View dialogView;
    private RecyclerView recyclerView;
    private TextView titleTv;
    private ClearEditText searchEt;
    private ListDialogAdapter adapter;
    private Callback callback;
    private List dataLists;
    private List searchLists;
    private String analysisKey;
    private Button btnCancel;
    public ListDialog(Activity activity, Callback callback){
        this.activity = activity;
        this.callback = callback;
        inflater = LayoutInflater.from(activity);
        dialogView = inflater.inflate(R.layout.view_dialog_list, null);
        recyclerView = dialogView.findViewById(R.id.recyclerView);
        titleTv = dialogView.findViewById(R.id.titleTv);
        searchEt = dialogView.findViewById(R.id.searchEt);
        btnCancel = dialogView.findViewById(R.id.cancel);
        setButtonListener(dialogView, R.id.cancel, "取消", this);
        dialog = new AlertDialog.Builder(activity, R.style.my_style_dialog).create();
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);

        searchEt.initEditText(0, "请输入名称搜索", 0, 3, false, this);
    }

    /**
     * 显示对话框
     */
    public void show(List datas, String title ,String analysisKey, String typeCode) {
        if (dialog != null && dialog.isShowing()) {  // 避免重复显示
            return;
        }

        if (activity.isFinishing() || activity.isDestroyed()) {
            return;
        }

        List ls = new ArrayList<>();
        if(typeCode != null && !typeCode.equals("")) {
            for(int i=0;i<datas.size();i++) {
                if(datas.get(i) instanceof BaseDataModel){
                    BaseDataModel bean = (BaseDataModel)datas.get(i);
                    if(bean.getTypeCode().equals(typeCode)){
                        ls.add(bean);
                    }
                }
            }
        }

        this.analysisKey = analysisKey;

        if(ls.size() >0)
        {
            this.dataLists = ls;
            this.searchLists = ls;
        }
        else
        {
            this.dataLists = datas;
            this.searchLists = datas;
        }

        dialog.show();
        dialog.getWindow().setContentView(dialogView);
        dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        WindowManager windowManager = (WindowManager) activity.getSystemService(activity.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
        lp.width = display.getWidth(); //设置宽度
        lp.height = (int)(display.getHeight() * 0.6);
        dialog.getWindow().setAttributes(lp);
        titleTv.setText(title);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.activity, LinearLayoutManager.VERTICAL, false));
        adapter = new ListDialogAdapter(this.activity, analysisKey, new ListDialogAdapter.Callback() {
            @Override
            public void cellAction(Object obj, int position) {
                if(callback != null){
                    callback.selectFinish(obj,position);
                }
                dismiss();
            }
        });
        recyclerView.setAdapter(adapter);

        if(ls.size() >0)
        {
            adapter.notifyDataSetChanged(ls);
        }
        else
        {
            adapter.notifyDataSetChanged(datas);
        }
    }

    /**
     * 显示对话框
     */
    public void show(List datas, String title ,String analysisKey) {
        if (dialog != null && dialog.isShowing()) {  // 避免重复显示
            return;
        }

        if (!activity.isFinishing() && !activity.isDestroyed()) {
            show(datas, title,analysisKey,"");
        }
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

    /*
    * 搜索
    * */
    private List searchInDataListByText(String text){
        if(text == null || text.equals("")){
            return dataLists;
        }else {
            List list = new ArrayList();
            for(int i = 0 ; i < dataLists.size(); i++){
                if(dataLists.get(i) instanceof BaseDataModel){
                    BaseDataModel bean = (BaseDataModel)dataLists.get(i);
                    if(bean.getName().contains(text)){
                         list.add(bean);
                    }
                }
            }
            return list;
        }
    }

    /**
     * 设置对话框点击对话框以外的范围是否可以关闭对话框
     */
    public void setCancelable(boolean bool) {
        dialog.setCancelable(bool);
    }

    @Override
    public void changed(int etId, String result) {
        searchLists = searchInDataListByText(result);
        adapter.notifyDataSetChanged(searchLists);
    }

    @Override
    public void confirm(int etId, String result) {

    }

    @Override
    public void scan(int etId) {

    }

    @Override
    public void clear(int etId) {

    }

    @Override
    public void onClick(View v) {
        dialog.dismiss();
        int id = v.getId();
        if (id == R.id.cancel) {

        }
    }

    public static Button setButtonListener(View view, int btnId, String btnText, View.OnClickListener listener) {
        Button button = (Button) view.findViewById(btnId);
        button.setText(btnText);
        button.setOnClickListener(listener);
        return button;
    }

    public interface Callback{
        void selectFinish(Object obj, int position);
    }
}
