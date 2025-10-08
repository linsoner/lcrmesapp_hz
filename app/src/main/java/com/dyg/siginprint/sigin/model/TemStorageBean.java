package com.dyg.siginprint.sigin.model;

import java.io.Serializable;
import java.util.List;

/**
 * author : DC-DingYG
 * e-mail : dingyg012655@126.com
 * time : 2021/07/18
 * desc : *
 */
public class TemStorageBean implements Serializable{
    private BillInfoBean bill;//出库单信息
    private List<BarcodeBean> codeList;//解析成功后的数据，用于计算总张数和保存是提交
    private List<BarcodeBean> bList;//显示在网格的数据
    private List<String> ones;//扫描了但未解析的一维码
    private int isInterSigin;//是否内部标签
    private String count;//扫描总数量

    public BillInfoBean getBill() {
        return bill;
    }

    public void setBill(BillInfoBean bill) {
        this.bill = bill;
    }

    public List<BarcodeBean> getbList() {
        return bList;
    }

    public void setbList(List<BarcodeBean> bList) {
        this.bList = bList;
    }

    public List<String> getOnes() {
        return ones;
    }

    public void setOnes(List<String> ones) {
        this.ones = ones;
    }

    public int getIsInterSigin() {
        return isInterSigin;
    }

    public void setIsInterSigin(int isInterSigin) {
        this.isInterSigin = isInterSigin;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public List<BarcodeBean> getCodeList() {
        return codeList;
    }

    public void setCodeList(List<BarcodeBean> codeList) {
        this.codeList = codeList;
    }
}
