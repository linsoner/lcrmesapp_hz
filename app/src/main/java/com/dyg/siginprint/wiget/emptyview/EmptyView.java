package com.dyg.siginprint.wiget.emptyview;

import android.content.Context;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dyg.siginprint.R;


/**
 * author : DC-DingYG
 * e-mail : dingyg012655@126.com
 * time : 2021/07/06
 * desc : * 错误提示界面
 */
public class EmptyView extends LinearLayout {

    LinearLayout ll_empty;
    TextView tv_error_title;//错误标题
    TextView tv_error_desc;//错误详细描述

    public EmptyView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        View view = View.inflate(context, R.layout.view_empty, this);
        ll_empty = view.findViewById(R.id.ll_empty);
        tv_error_title = view.findViewById(R.id.tv_error_title);
        tv_error_desc = view.findViewById(R.id.tv_error_desc);
    }

    /**
     * @param errorTitle 错误标题
     * @param errorDesc 错误详细描述
     */
    public void setEmptyView(String errorTitle, String errorDesc) {
       if (TextUtils.isEmpty(errorTitle)) {
           ll_empty.setVisibility(GONE);
       }else {
           tv_error_title.setText(errorTitle);
           if (TextUtils.isEmpty(errorDesc)) {
               tv_error_desc.setVisibility(GONE);
           }else {
               tv_error_desc.setVisibility(VISIBLE);
               tv_error_desc.setText(errorDesc);
           }
       }
    }
}
