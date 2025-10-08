package com.dyg.siginprint.HRecycleView.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.dyg.siginprint.HRecycleView.data.HRecycleViewData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by m088524 on 2022/9/9
 * Describe:
 */
public class utils {

    /**
     * 将dp转换为px
     * @param context
     * @param dpValue
     * @return
     */
    public static int dip2px(Context context, float dpValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 计算出数据中TextView中文字的长度(即宽度，单位是像素)并返回数据集合
     * @param textView 测量的根据textView
     * @param list 数据集合
     * @return
     */
    public static ArrayList<Integer> getItemWidthList(TextView textView, List<HRecycleViewData> list) {
        ArrayList<Integer> itemViewWidthList = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) { ///50
            HRecycleViewData hRecycleViewData = list.get(i);
            for (int j = 0; j < hRecycleViewData.getHRecycleViewData().size(); j++) { //5
                int viewWidth = (int)textView.getPaint().measureText(hRecycleViewData.getHRecycleViewData().get(j));
                if(itemViewWidthList == null || itemViewWidthList.size() < hRecycleViewData.getHRecycleViewData().size()){
                    itemViewWidthList.add(viewWidth);
                }else {
                    if(itemViewWidthList.get(j) < viewWidth){
                        itemViewWidthList.set(j, viewWidth);
                    }
                }
            }
        }
        return itemViewWidthList;
    }

    /**
     * 计算出标题中TextView中文字的长度(即宽度，单位是像素)并返回数据集合
     * @param textView 测量的根据textView
     * @param list 数据集合
     * @return
     */
    public static ArrayList<Integer> getHeadWidthList(TextView textView, ArrayList<String> list) {
        ArrayList<Integer> headViewWidthList = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            int viewWidth = (int)textView.getPaint().measureText(list.get(i));
            headViewWidthList.add(viewWidth);
        }
        return headViewWidthList;
    }

    /**
     *
     * 获取同一列中标题和数据的最大宽度，返回最大宽度的数据集合
     * @param head 标题的长度集合
     * @param item 数据的长度集合
     * @return
     */
    public static ArrayList<Integer> getMaxWidthList(ArrayList<Integer> head, ArrayList<Integer> item) {
        ArrayList<Integer> maxTextViewWidthList = new ArrayList<>();
        for (int i = 0; i < head.size(); i++) {
            if(item.size() > i){
                //item数据可能不存在
                if(head.get(i) > item.get(i)){
                    maxTextViewWidthList.add(head.get(i));
                }else {
                    maxTextViewWidthList.add(item.get(i));
                }
            }else {
                maxTextViewWidthList.add(head.get(i));
            }
        }
        return maxTextViewWidthList;
    }

    /**
     * 获取布局中目标控件所设置的最大宽度
     * @param context
     * @param layout 布局
     * @param textViewList 目标控件集合
     * @return
     */
    public static ArrayList<Integer> getSetMaxWidthList(Context context, int layout, ArrayList<Integer> textViewList) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View inflate = inflater.inflate(layout, null);
        ArrayList<Integer> setMaxTextViewWidthList = new ArrayList<>();
        for (int i = 0; i < textViewList.size(); i++) {
            TextView textView = inflate.findViewById(textViewList.get(i));
            setMaxTextViewWidthList.add(textView.getMaxWidth());
        }
        return setMaxTextViewWidthList;
    }

    /**
     * 获取理想的控件宽度(如果设置的最大宽度大于文本的最大宽度，则显示文本的最大宽度，否则显示设置的最大宽度，文本将换行显示)
     * @param maxWidthList 文本最大宽度的集合
     * @param setMaxWidth 设置的控件最大宽度集合
     * @return
     */
    public static ArrayList<Integer> getIdealWidthList(ArrayList<Integer> maxWidthList, ArrayList<Integer> setMaxWidth) {
        ArrayList<Integer> finalWidthList = new ArrayList<>();
        for (int i = 0; i < maxWidthList.size(); i++) {
            if(maxWidthList.get(i) > setMaxWidth.get(i)){
                finalWidthList.add(setMaxWidth.get(i));
            }else {
                finalWidthList.add(maxWidthList.get(i));
            }
        }
        return finalWidthList;
    }

}
