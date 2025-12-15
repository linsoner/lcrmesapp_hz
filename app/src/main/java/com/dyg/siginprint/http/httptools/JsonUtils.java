package com.dyg.siginprint.http.httptools;

import android.text.TextUtils;

import com.dyg.siginprint.base.model.BaseBean;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by Administrator on 2018-02-20.
 */

public class JsonUtils {

    /**
     * 修改人：DC-ZhuSuiBo on 2018/8/13 0013 8:59
     * 邮箱：suibozhu@139.com
     * 修改目的：
     */
    public static BaseBean getDataBean(String jsonStr) {
        BaseBean dataBean = new BaseBean();
        if (jsonStr != null && !jsonStr.equals("")) {
            try {
                JSONObject json = new JSONObject(jsonStr);
                dataBean.setCode(json.getInt("code"));
                dataBean.setMsg(json.getString("msg"));
                dataBean.setData(json.getString("data"));
            } catch (Exception e) {
            }
        }
        return dataBean;
    }

    public interface OperateData {
        void doingInSuccess(String data);
    }


    /**
     * 返回表格标题和key
     **/
    public static Map<Integer, String[]> getKeyAndTitle(String hasCheckBoxStr, JSONObject jo) {
        try {
            Map<Integer, String[]> sarrList = new HashMap<>();
            Iterator iterator = jo.keys();
            String[] tkey = new String[!TextUtils.isEmpty(hasCheckBoxStr) ? jo.names().length() + 1 : jo.names().length()];
            String[] title = new String[!TextUtils.isEmpty(hasCheckBoxStr) ? jo.names().length() + 1 : jo.names().length()];
            int index = 0;
            if (!TextUtils.isEmpty(hasCheckBoxStr)) {
                index = 1;
                tkey[0] = hasCheckBoxStr;
                title[0] = hasCheckBoxStr;
            }
            while (iterator.hasNext()) {
                String key = (String) iterator.next();
                String value = jo.getString(key);
                tkey[index] = key;
                title[index] = value;
                index++;
            }
            sarrList.put(0, tkey);
            sarrList.put(1, title);
            return sarrList;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 返回表格标题和key
     **/
    public static String getValue(Map<Integer, String[]> sMap, int position, int index) {
        if (sMap.containsKey(position)) {
            if (index < sMap.get(position).length) {
                return sMap.get(position)[index];
            } else {
                return "";
            }
        }
        return "";
    }
}
