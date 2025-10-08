package com.dyg.siginprint.base.tools;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.widget.TextView;

import com.dyg.siginprint.app.App;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;


/**
 * Created by ljc on 2018/1/16.
 */

public class CommUtil {
    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 获取屏幕宽度
     */
    public static int getScreenWidth(Context context) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        return dm.widthPixels;
    }

    /**
     * 大陆号码或香港号码均可
     */
    public static boolean isPhoneLegal(String str) throws PatternSyntaxException {
        return isChinaPhoneLegal(str) || isHKPhoneLegal(str);
    }

    /**
     * 大陆手机号码11位数，匹配格式：前三位固定格式+后8位任意数
     * 此方法中前三位格式有：
     * 13+任意数
     * 15+除4的任意数
     * 18+除1和4的任意数
     * 17+除9的任意数
     * 147
     */
    public static boolean isChinaPhoneLegal(String str) throws PatternSyntaxException {
        String regExp = "^((13[0-9])|(15[^4])|(18[0,2,3,5-9])|(17[0-8])|(147))\\d{8}$";
        Pattern p = Pattern.compile(regExp);
        Matcher m = p.matcher(str);
        return m.matches();
    }

    /**
     * 香港手机号码8位数，5|6|8|9开头+7位任意数
     */
    public static boolean isHKPhoneLegal(String str) throws PatternSyntaxException {
        String regExp = "^(5|6|8|9)\\d{7}$";
        Pattern p = Pattern.compile(regExp);
        Matcher m = p.matcher(str);
        return m.matches();
    }

    /**
     * 判断是否内网
     * 10.0.0.0~10.255.255.255
     * 172.16.0.0~172.31.255.255
     * 192.168.0.0~192.168.255.255
     **/
    public static boolean isInner(String ip) {
        String reg = "(10|172|192)\\.([0-1][0-9]{0,2}|[2][0-5]{0,2}|[3-9][0-9]{0,1})\\.([0-1][0-9]{0,2}|[2][0-5]{0,2}|[3-9][0-9]{0,1})\\.([0-1][0-9]{0,2}|[2][0-5]{0,2}|[3-9][0-9]{0,1})";
        Pattern p = Pattern.compile(reg);
        Matcher matcher = p.matcher(ip);
        return matcher.find();
    }

    public static String formatSecond(int second) {
        int result = second / 60;
        if (result > 0) {
            return String.format("%d'%d\'", result, second % 60);
        } else {
            return second + "\"";
        }
    }


    public static String formatStr(String string) {
        if (string == null || string.equals("null")) {
            return "";
        } else {
            return string.trim();
        }
    }
    /**
     * 获取VersionName
     * @param context
     * @return pi.versionName
     */
    public static int getVersionNCode(Context context){
        PackageManager pm = context.getPackageManager();
        PackageInfo pi = null;
        try {
            pi = pm.getPackageInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if(pi==null){
            return 1000;
        }
        return pi.versionCode;
    }
    /**
     * 获取VersionName
     * @param context
     * @return pi.versionName
     */
    public static String getVersionName(Context context){
        PackageManager pm = context.getPackageManager();
        PackageInfo pi = null;
        try {
            pi = pm.getPackageInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if(pi==null){
            return "";
        }
        return pi.versionName;
    }
    public static void setRightDrawable(Context context, TextView textView, String text, int drawableId) {
        textView.setText(text);
        Drawable drawable = context.getResources().getDrawable(drawableId);
        drawable.setBounds(0, 0, 23, 13);
        textView.setCompoundDrawables(null, null, drawable, null);
    }

    /**
     * @param mContext      上下文
     * @param TLength       标题的长度
     * @param b_count       最后一列多少个按钮
     * @param isFirstButton 第一列是否为按钮
     * @return
     */
    public static int[] SetComWidth(Context mContext, int TLength, int b_count, boolean isFirstButton) {
        int[] tableWith = new int[TLength];
        int operationWidth = 0;
        operationWidth = CommUtil.dip2px(mContext, 100);//一个按钮所占的父窗体宽度

        int ScrrentWith = getScreenWidth(mContext);
        for (int i = 0; i < tableWith.length; i++) {
            if (isFirstButton) {
                if (i == 0) {
                    tableWith[i] = CommUtil.dip2px(mContext, 50);
                } else {
                    int www = (ScrrentWith - tableWith[0] - operationWidth * b_count) / (TLength - 1);
                    tableWith[i] = www < 120 ? 120 : www;
                }
            } else {
                int www = (ScrrentWith - operationWidth * b_count) / (TLength - 1);
                tableWith[i] = www < 120 ? 120 : www;
            }

        }
        /*int ScrrentWith = getScreenWidth(mContext);
        int row = ScrrentWith / 12;//固定一屏最多显示12列
        if (TLength > 11) {
            for (int i = 0; i < TLength; i++) {
                if (i == TLength - 1) {
                    tableWith[i] = row * b_count;
                } else {
                    tableWith[i] = row;
                }
            }
        } else {
            for (int i = 0; i < tableWith.length; i++) {
                if (isFirstButton) {
                    if (i == 0) {
                        tableWith[i] = operationWidth;
                    } else if (i == tableWith.length - 1) {
                        tableWith[i] = operationWidth * b_count;
                    } else {
                        tableWith[i] = (ScrrentWith - operationWidth * (b_count + 1)) / (TLength - 2);
                    }
                } else {
                    if (i == tableWith.length - 1) {
                        tableWith[i] = operationWidth * b_count;
                    } else {
                        tableWith[i] = (ScrrentWith - operationWidth * b_count) / (TLength - 1);
                    }
                }

            }
        }*/
        return tableWith;
    }

    public static TextWatcher watcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable edt) {
            try {
                String temp = edt.toString();
                String tem = temp.substring(temp.length() - 1, temp.length());
                char[] temC = tem.toCharArray();
                int mid = temC[0];
                if (mid >= 48 && mid <= 57) {//数字
                    return;
                }
                if (mid >= 65 && mid <= 90) {//大写字母
                    return;
                }
                if (mid >= 97 && mid <= 122) {//小写字母
                    return;
                }
                if(mid == 46 || mid == 64 || mid == 35 || mid == 47 || mid == 92 || mid == 45
                        || mid == 95 || mid == 42)//46. 64@ 35# 47/ 92\ 45- 95_ 42*
                    return;
                edt.delete(temp.length() - 1, temp.length());
            } catch (Exception e) {
            }
        }
    };

    public static TextWatcher watcherStore = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable edt) {
            try {
                String temp = edt.toString();
                String tem = temp.substring(temp.length() - 1, temp.length());
                char[] temC = tem.toCharArray();
                int mid = temC[0];
                if (mid >= 48 && mid <= 57) {//数字
                    return;
                }
                if (mid >= 65 && mid <= 90) {//大写字母
                    return;
                }
                if (mid >= 97 && mid <= 122) {//小写字母
                    return;
                }
                if(mid == 58 || mid == 62 || mid == 46 || mid == 64 || mid == 35 || mid == 47 || mid == 92 || mid == 45
                        || mid == 95 || mid == 42)//58 : 62>
                    return;
                edt.delete(temp.length() - 1, temp.length());
            } catch (Exception e) {
            }
        }
    };

    /**
     * 重复某个字符串重复显示多少次
     * @param str 需显示的字符串
     * @param n 重复字次
     * @return 返回字符串
     */
    public static String repeatString(String str, int n) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < n; i++) {
            sb.append(str);
        }
        return sb.toString();
    }


    public static String formatInteger(String value){
        if(value == null || value.equals("")){
            return "0";
        }else{
            return value;
        }
    }

    public static int formatStrToInteger(String value){
        if(value == null){
            return 0;
        }else{
            return Integer.parseInt(value);
        }
    }
    // 判断一个字符是否是中文
    public static boolean isChinese(char c) {
        return c >= 0x4E00 && c <= 0x9FA5;// 根据字节码判断
    }

    // 判断一个字符串是否含有中文
    public static boolean isChinese(String str) {
        if (str == null)
            return false;
        for (char c : str.toCharArray()) {
            if (isChinese(c))
                return true;// 有一个中文字符就返回
        }
        return false;
    }

    //创建圆角drawale fillColor填充颜色 strokeColor 边框颜色 broadWidth 边框大小 radius 圆角
    public static GradientDrawable createDrawale(int fillColor , int strokeColor , int broadWidth , float radius)
    {
        GradientDrawable gd = new GradientDrawable();
        if(fillColor!=0)
            gd.setColor(fillColor);
        gd.setCornerRadius(radius);
        if(strokeColor!=0)
            gd.setStroke(broadWidth, strokeColor);
        return gd;
    }

    /**
     * 判断字符串是否为纯数字
     * */
    public static boolean isNumeric(String str){
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str);
        if( !isNum.matches() ){
            return false;
        }
        return true;
    }



    //解析网络请求错误信息
    public static String getErrorMsg(JSONObject jsonObject)
    {
        try {
            String showMessage = "";
            if(TextUtils.isEmpty(jsonObject.getString("data")) || jsonObject.getString("data").equals("null"))
            {
                showMessage = jsonObject.getString("errormsg");
            }
            else {
                JSONArray jsonArray = jsonObject.getJSONObject("data").getJSONArray("error");
                if (jsonArray != null && jsonArray.length() > 0) {
                    JSONObject object = jsonArray.getJSONObject(0);
                    showMessage = object.getString("fRemark");
                } else {
                    if (!jsonObject.getJSONObject("data").getString("error").equals("[]") && !TextUtils.isEmpty(jsonObject.getJSONObject("data").getString("error")))
                        showMessage = jsonObject.getJSONObject("data").getString("error");
                    else
                        showMessage = jsonObject.getString("errormsg");
                }
            }
            return showMessage;
        }
        catch (JSONException e)
        {
            e.printStackTrace();
            return "";
        }
    }

    //键盘按下弹起事件
    public static Boolean onKeyUp(int keyCode , KeyEvent event)
    {
        if (keyCode == 102 || keyCode == 103 || keyCode == 110 || keyCode == 120 || keyCode == 520) {
            return true;
        }
        return false;
    }

    //键盘按下事件
    public static Boolean onKeyDown(int keyCode , KeyEvent event)
    {
        if (keyCode == 102 || keyCode == 103 || keyCode == 110 || keyCode == 120 || keyCode == 520) {
            return true;
        }
        return false;
    }


    //获取MAC地址
    public static String getMacAddress() {

        String macAddress =null;
        WifiManager wifiManager =
                (WifiManager) App.appContext.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = (null== wifiManager ?null: wifiManager.getConnectionInfo());

        if(!wifiManager.isWifiEnabled())
        {
            //必须先打开，才能获取到MAC地址
            wifiManager.setWifiEnabled(true);
            wifiManager.setWifiEnabled(false);
        }
        if(null!= info) {
            macAddress = info.getMacAddress();
        }
        return macAddress;
    }
    // 两个String数字之和
    public static String getAddStrNum(String a, String b) {
        try {
            BigDecimal bigA = new BigDecimal(a);
            BigDecimal bigB = new BigDecimal(b);
            BigDecimal bigC = bigA.add(bigB);
            String result = bigC.stripTrailingZeros().toPlainString();
            if (result.contains(".")) {
                String tou = result.substring(0, result.indexOf("."));
                if (tou.equals("0")
                        && result.charAt(result.length() - 1) == '0') {
                    result = "0";
                }
            }
            return result;
        }catch (Exception e){
            return "0";
        }
    }
}
