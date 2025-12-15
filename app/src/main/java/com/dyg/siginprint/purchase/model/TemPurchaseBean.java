package com.dyg.siginprint.purchase.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class TemPurchaseBean implements Serializable {
    private String supplierCode;//供应商
    private String supplierName;
    private String loc;//调出库位 注意 调拨才会用到调入库位，其他都是调出库位
    private String toLoc;//调入库位
    private int salesReturn;//是否退货 1退货
    private String SourceNo;//采购单号
    private List<PuechaseModel> scanFinishList = new ArrayList<>();//已扫描的条码

    public String getSupplierCode() {
        return supplierCode;
    }

    public void setSupplierCode(String supplierCode) {
        this.supplierCode = supplierCode;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public String getLoc() {
        return loc;
    }

    public void setLoc(String loc) {
        this.loc = loc;
    }

    public String getToLoc() {
        return toLoc;
    }

    public void setToLoc(String toLoc) {
        this.toLoc = toLoc;
    }

    public int getSalesReturn() {
        return salesReturn;
    }

    public void setSalesReturn(int salesReturn) {
        this.salesReturn = salesReturn;
    }

    public List<PuechaseModel> getScanFinishList() {
        return scanFinishList;
    }

    public void setScanFinishList(List<PuechaseModel> scanFinishList) {
        this.scanFinishList = scanFinishList;
    }

    public String getSourceNo() {
        return SourceNo;
    }

    public void setSourceNo(String sourceNo) {
        SourceNo = sourceNo;
    }
}
