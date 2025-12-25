package com.dyg.siginprint.wiget.textview;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dyg.siginprint.R;
import com.dyg.siginprint.base.tools.CommUtil;
import com.dyg.siginprint.base.tools.DoubleClickU;
import com.dyg.siginprint.base.tools.LogUtils;

/**
 * author : DC-DingYG
 * e-mail : dingyg012655@126.com
 * time : 2018/06/07
 * desc : * 带清空按钮的单行输入框
 */
public class ClearEditText extends LinearLayout {

    LinearLayout ll_input;
    ImageView iv_left_img;//左侧图标
    EditText et_input;//输入框
    RelativeLayout rl_clear, rl_scan;//清空
    Listener listener;
    int etId;
    long lastscanTime = 0;//处理部分pda扫描时出现重复回车现象
    boolean ifCanScan = false;//是否可以扫码
    boolean showClear = true;//是否需要一键清空

    // 添加设置输入类型的方法
    public void setInputType(int type) {
        if (et_input != null) {
            et_input.setInputType(type);
        }
    }

    public ClearEditText(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        View view = View.inflate(context, R.layout.view_clear_edittext, this);
        ll_input = view.findViewById(R.id.ll_input);
        iv_left_img = view.findViewById(R.id.iv_left_img);
        et_input = view.findViewById(R.id.et_input);
        rl_clear = view.findViewById(R.id.rl_clear);
        rl_scan = view.findViewById(R.id.rl_scan);
        et_input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (showClear) {
                    try {
                        if (s.toString().trim().length() == 0) {
                            rl_clear.setVisibility(GONE);
                        } else {
                            rl_clear.setVisibility(VISIBLE);
                        }
                    } catch (Exception e1) {
                    }
                }

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (listener != null) {
                    listener.changed(etId, et_input.getText().toString().trim());
                }
            }
        });
        et_input.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                try {
                    if (actionId == EditorInfo.IME_ACTION_UNSPECIFIED || actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_GO || actionId == EditorInfo.IME_ACTION_NEXT) {
                        long thisTime = System.currentTimeMillis();
                        if (thisTime - lastscanTime > 50) {
                            LogUtils.e("-----------时间间隔：" +( thisTime - lastscanTime));
                            lastscanTime = thisTime;
                            EditText edt = (EditText) v;
                            String s = edt.getText().toString().trim();
                            if (!s.equals("")) {
                                if (listener != null) {
                                    listener.confirm(etId, s);
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return false;
            }
        });
        rl_clear.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (et_input != null) et_input.setText("");
                if (listener != null) {
                    listener.clear(etId);
                }
            }
        });
    }

    /**
     *
     * @param leftImgRes 左侧图标的资源id
     * @param hintText 提示语
     * @param etId 输入框的id(在同界面的多个输入框中使用，便于区别)
     * @param intType 输入类 0-普通的文字 1-数字 2-密码
     * @param ifCanScan 是否可以扫码
     * @param listener
     */
    public void initEditText(int leftImgRes, String hintText, int intType, final int etId, boolean ifCanScan, final Listener listener) {
        this.etId = etId;
        this.listener = listener;
        if (intType == 2) {
            et_input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        }else if (intType == 1) {
            et_input.setInputType(InputType.TYPE_CLASS_NUMBER);
        }else {
            et_input.setInputType(InputType.TYPE_CLASS_TEXT);
        }
        et_input.setImeOptions(EditorInfo.IME_ACTION_DONE);
        if (!TextUtils.isEmpty(hintText)) {
            et_input.setHint(hintText);
        }else {
            et_input.setHint("");
        }
        et_input.setHintTextColor(Color.LTGRAY);
        if (leftImgRes > 0) {
            iv_left_img.setImageResource(leftImgRes);
            iv_left_img.setVisibility(VISIBLE);
        }else {
            iv_left_img.setVisibility(GONE);
        }
        if (ifCanScan) {
            rl_scan.setVisibility(VISIBLE);
            rl_scan.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!DoubleClickU.isFastDoubleClick(R.id.rl_scan)) {
                        if(listener != null) listener.scan(etId);
                    }
                }
            });
        } else rl_scan.setVisibility(GONE);
    }

    public void setShowClear(boolean ifShowClear) {
        this.showClear = ifShowClear;
    }

    //设置最小高度
    public void setMinHeigth(Context context, int minHeight) {
        ll_input.setMinimumHeight(CommUtil.dip2px(context, minHeight));
    }

    //设置背景
    public void setBackground(int bgRes) {
        ll_input.setBackgroundResource(bgRes);
    }

    /**
     * @param text 内容
     * @param ifSetSelection 是否将光标聚焦在文字末尾
     */
    public void setTextCt(String text, boolean ifSetSelection) {
        if (et_input != null) {
            et_input.setText(text);
            if (ifSetSelection) et_input.setSelection(et_input.getText().length());
        }
    }

    public String getTextCt(){
        if (et_input != null)
            return et_input.getText().toString().trim();
        else return "";
    }

    public void setTextHint(String txt) {
        if (et_input != null) et_input.setHint(txt);
    }

    public String getTextHint() {
        if (et_input != null) return et_input.getHint().toString();
        return "";
    }

    public void requestEditFoucs() {
        if (et_input != null) et_input.requestFocus();
    }


    public interface Listener {
        /**
         * 输入内容改变后
         * @param etId 输入框的id(在同界面的多个输入框中使用，便于区别)
         * @param result
         */
        void changed(int etId, String result);
        void confirm(int etId, String result);
        void clear(int etId);
        void scan(int etId);
    }
}
