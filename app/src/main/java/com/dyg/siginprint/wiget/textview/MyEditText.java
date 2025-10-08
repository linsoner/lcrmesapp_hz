package com.dyg.siginprint.wiget.textview;

import android.content.Context;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dyg.siginprint.R;
import com.dyg.siginprint.base.tools.LogUtils;

public class MyEditText extends LinearLayout {
    EditText editText;
    Listener listener;
    int edId;//给输入框唯一标识
    private Boolean isMustNumberAndPoint = false;//只能输入数字和一个小数点
    private int pointNum = 4;//可输入小数点位数，默认四位

    public MyEditText(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        View view = View.inflate(context, R.layout.view_my_edittext, this);
        editText = view.findViewById(R.id.et_myInput);


        editText.setFilters(new InputFilter[]{new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                if (isMustNumberAndPoint) {
                    // 允许空字符串、数字、负号（作为第一个字符）、和小数点
                    String sourceStr = source.toString();
                    String destStr = dest.toString();

                    // 检查是否尝试输入负号，并且负号不是第一个字符
                    if (sourceStr.equals("-") && dstart != 0) {
                        return ""; // 如果负号不是输入在第一个位置，则拒绝输入
                    }

                    // 使用正则表达式检查其他情况
                    // 正则表达式解释：
                    // -?：表示字符串可以以负号开头（可选）
                    // [0-9]*：表示任意数量的数字（包括零个）
                    // \\.?：表示可选的一个小数点
                    // [0-9]*$：表示小数点后跟随任意数量的数字，并且确保字符串在这里结束
                    if (!sourceStr.matches("-?[0-9]*\\.?[0-9]*$")) {
                        return ""; // 非法字符，拒绝输入
                    }

                    // 检查是否已经有小数点，并且尝试再输入一个小数点
                    if (destStr.contains(".") && sourceStr.contains(".")) {
                        return "";
                    }

                    if (destStr.contains(".") &&
                            (destStr.length() - destStr.indexOf('.') - 1 + (end - start)) > pointNum) {
                        return ""; // 如果小数点后超过两位，则拒绝输入
                    }


                    // 如果所有检查都通过，则返回源字符序列
                    return source;
                }
                return source;
            }
        }});

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (listener != null) {
                    listener.changed(edId, editText.getText().toString().trim());
                }
            }
        });

        // 设置失去焦点监听器
        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    // EditText失去焦点时的处理逻辑
                    LogUtils.e("-----------失去焦点");
                    if (safeTime(50)) {
                        LogUtils.e("-----------失去焦点--安全时间");
                        EditText edt = (EditText) v;
                        String s = edt.getText().toString().trim();
                        if (listener != null) {
                            listener.confirm(edId, s);
                        }
                    }
                }
            }
        });

        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                try {
                    if (actionId == EditorInfo.IME_ACTION_UNSPECIFIED || actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_GO || actionId == EditorInfo.IME_ACTION_NEXT) {
                        if (safeTime(50)) {
                            EditText edt = (EditText) v;
                            String s = edt.getText().toString().trim();
                            if (listener != null) {
                                listener.confirm(edId, s);
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return false;
            }
        });
    }

    long lastscanTime = 0;//处理部分pda扫描时出现重复回车现象

    private Boolean safeTime(int spaceTime) {
        if (spaceTime == -1) {
            return true;
        }
        long thisTime = System.currentTimeMillis();
        if (thisTime - lastscanTime > 50) {
            LogUtils.e("-----------时间间隔：" + (thisTime - lastscanTime));
            lastscanTime = thisTime;
            return true;
        }
        return false;
    }

    public void setText(String text) {
        if (text == null) {
            text = "";
        }
        editText.setText(text);
    }

    public String getText() {
        if (editText != null)
            return editText.getText().toString().trim();
        else {
            return "";
        }
    }

    public void setMustNumberAndPoint(Boolean mustNumberAndPoint) {
        isMustNumberAndPoint = mustNumberAndPoint;
        if (editText == null) return;
        if (isMustNumberAndPoint) {
            editText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        } else {
            editText.setInputType(InputType.TYPE_CLASS_TEXT);
        }
    }

    public int getEdId() {
        return edId;
    }

    public void setEdId(int edId) {
        this.edId = edId;
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public interface Listener {
        /**
         * 输入内容改变后
         *
         * @param etId   输入框的id(在同界面的多个输入框中使用，便于区别)
         * @param result
         */
        void changed(int etId, String result);

        void confirm(int etId, String result);
    }
}
