package com.dyg.siginprint.HRecycleView.data;

import java.util.List;

/**
 * Created by m088524 on 2022/9/5
 * Describe: item数据类型
 */
public class HRecycleViewData {

    private List<String> mData;

    public HRecycleViewData(List<String> mData) {
        this.mData = mData;
    }

    public void setHRecycleViewData(List<String> data){
        this.mData = data;
    }

    public List<String> getHRecycleViewData(){
        return mData;
    }

    public String get(int i){
        return mData.get(i);
    }
}
