package com.dyg.siginprint.sigin.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.List;

/**
 * author : DC-DingYG
 * e-mail : dingyg012655@126.com
 * time : 2021/07/07
 * desc : *出库单信息
 */
public class BillInfoBean implements Serializable {

    /**
     * "interID": 315049,//出库单内码
     * "date": "2020-09-01T00:00:00", //出库单日期
     * "billNo": "PT2020090001", //出库单号
     * "customerName": "金威源科技", //客户名称
     * "customerCode": "25.0115",//客户代号
     * "scanCount": 1,//扫描多少次才提交一次解析
     * "dtls":[]  //出库单细表
     */

    private int interID;
    private String date;
    private String billNo;
    private String customerName;
    private String customerCode;
    private int scanCount;
    private List<DtlsBean> dtls;

    public int getInterID() {
        return interID;
    }

    public void setInterID(int interID) {
        this.interID = interID;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getBillNo() {
        return billNo;
    }

    public void setBillNo(String billNo) {
        this.billNo = billNo;
    }

    public String getCustomerName() {
        return customerName == null || customerName.equals("null") ? "" : customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerCode() {
        return customerCode;
    }

    public void setCustomerCode(String customerCode) {
        this.customerCode = customerCode;
    }

    public int getScanCount() {
        return scanCount;
    }

    public void setScanCount(int scanCount) {
        this.scanCount = scanCount;
    }

    public List<DtlsBean> getDtls() {
        return dtls;
    }

    //获取出库单某客户料号对应的总数数量
    public double getCustPnQty(String custPn) {
        if (dtls != null && dtls.size() > 0) {
            for (DtlsBean bean : dtls) {
                if (bean.getCustomerPn().equals(custPn)) {
                    return bean.getQty();
                }
            }
        }
        return 0.00;
    }


    public static class DtlsBean implements Serializable {
        /**
         * "sn": "A00001", //标签流水号，可能为空, 有值时可用于判断是否重复扫描
         * "pn": "RL102M6R3F08003", //公司料号
         * "customerPn": "10.05.100500998R",//客户料号
         * "qty": 4000, //数量
         * "lotNo": "Lo2021001",//批号
         * "dateCode": "AL125", //周期
         * "scanData":
         */

        private double qty;
        private String pn;
        private String customerPn;

        public double getQty() {
            return qty;
        }

        public String getPn() {
            return pn == null ? "" : pn;
        }

        public String getCustomerPn() {
            return customerPn == null ? "" : customerPn;
        }

    }

}
