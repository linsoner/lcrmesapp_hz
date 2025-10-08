package com.dyg.siginprint.sigin.model;

import java.io.Serializable;

/**
 * author : DC-DingYG
 * e-mail : dingyg012655@126.com
 * time : 2021/07/06
 * desc : *条码信息
 */
public class BarcodeBean implements Serializable {
    /**
     * "sn": "A00001", //标签流水号，可能为空, 有值时可用于判断是否重复扫描
     *     "pn": "RL102M6R3F08003", //公司料号
     *     "customerPn": "10.05.100500998R",//客户料号
     *     "qty": 4000, //数量
     *     "lotNo": "Lo2021001",//批号
     *     "dateCode": "AL125", //周期
     *     "scanData": "A00001;RL102M6R3F08003;10.05.100500998R;4000;Lo2021001;AL125" //扫描内容
     */
    private String sn;//标签流水号
    private String pn;//公司料号
    private String customerPn;//客户料号
    private double qty;//数量
    private String lotNo;//批号
    private String dateCode;//周期
    private String scanData;//扫描内容



    public BarcodeBean(String sn, String pn, String customerPn, double qty, String lotNo, String dateCode, String scanData) {
        this.sn = sn;
        this.pn = pn;
        this.customerPn = customerPn;
        this.qty = qty;
        this.lotNo = lotNo;
        this.dateCode = dateCode;
        this.scanData = scanData;
    }

    public String getSn() {
        return sn == null ? "" : sn;
    }

    public String getPn() {
        return pn == null ? "" : pn;
    }

    public String getCustomerPn() {
        return customerPn == null ? "" : customerPn;
    }

    public double getQty() {
        return qty;
    }

    public void setQty(double qty) {
        this.qty = qty;
    }

    public String getLotNo() {
        return lotNo == null ? "" : lotNo;
    }

    public String getDateCode() {
        return dateCode == null ? "" : dateCode;
    }

    public String getScanData() {
        return scanData == null ? "" : scanData;
    }

    public void setScanData(String scanData) {
        this.scanData = scanData;
    }
}
