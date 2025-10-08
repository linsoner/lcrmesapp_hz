package com.dyg.siginprint.purchase.model;

import java.io.Serializable;

public class PuechaseModel implements Serializable {

    private String sn;//流水号
    private String pn; //料号
    private String supplierCode;//供应商代号
    private String customerCode;//[调出]客户代号
    private String toCustomerCode;//调入客户代号
    private double qty;//数量
    private String unit;//单位
    private double area;//面积
    private double cap;//容量
    private String storage;//[调出]仓库代号
    private String loc;//[调出]库位
    private String toStorage;//调入仓库代号
    private String toLoc;//调入库位
    private double pcsQty;//片数
    private double width;//宽度
    private String proDdate;//生产日期
    private String lotNo;//批号
    private String remark;//备注
    private String qrCode;//二维码信息
    private String oriPn;//原料号
    private String dateCode;//周期
    private String customerOrder;//客户订单号
    private String makeType;//成型方式

    //拆标签
    private String string1;
    private String string2;
    private String string3;
    private double qty1;//新箱1
    private double qty2;//新箱2

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public String getPn() {
        return pn;
    }

    public void setPn(String pn) {
        this.pn = pn;
    }

    public String getSupplierCode() {
        return supplierCode;
    }

    public void setSupplierCode(String supplierCode) {
        this.supplierCode = supplierCode;
    }

    public String getCustomerCode() {
        return customerCode;
    }

    public void setCustomerCode(String customerCode) {
        this.customerCode = customerCode;
    }

    public String getToCustomerCode() {
        return toCustomerCode;
    }

    public void setToCustomerCode(String toCustomerCode) {
        this.toCustomerCode = toCustomerCode;
    }

    public double getQty() {
        return qty;
    }

    public void setQty(double qty) {
        this.qty = qty;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public double getArea() {
        return area;
    }

    public void setArea(double area) {
        this.area = area;
    }

    public double getCap() {
        return cap;
    }

    public void setCap(double cap) {
        this.cap = cap;
    }

    public String getStorage() {
        return storage;
    }

    public void setStorage(String storage) {
        this.storage = storage;
    }

    public String getLoc() {
        return loc;
    }

    public void setLoc(String loc) {
        this.loc = loc;
    }

    public String getToStorage() {
        return toStorage;
    }

    public void setToStorage(String toStorage) {
        this.toStorage = toStorage;
    }

    public String getToLoc() {
        return toLoc;
    }

    public void setToLoc(String toLoc) {
        this.toLoc = toLoc;
    }

    public double getPcsQty() {
        return pcsQty;
    }

    public void setPcsQty(double pcsQty) {
        this.pcsQty = pcsQty;
    }

    public double getWidth() {
        return width;
    }

    public void setWidth(double width) {
        this.width = width;
    }

    public String getProDdate() {
        return proDdate;
    }

    public void setProDdate(String proDdate) {
        this.proDdate = proDdate;
    }

    public String getLotNo() {
        return lotNo;
    }

    public void setLotNo(String lotNo) {
        this.lotNo = lotNo;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getQrCode() {
        return qrCode;
    }

    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }

    public String getOriPn() {
        return oriPn;
    }

    public void setOriPn(String oriPn) {
        this.oriPn = oriPn;
    }

    public String getDateCode() {
        return dateCode;
    }

    public void setDateCode(String dateCode) {
        this.dateCode = dateCode;
    }

    public String getCustomerOrder() {
        return customerOrder;
    }

    public void setCustomerOrder(String customerOrder) {
        this.customerOrder = customerOrder;
    }

    public String getMakeType() {
        return makeType;
    }

    public void setMakeType(String makeType) {
        this.makeType = makeType;
    }

    public String getString1() {
        return string1;
    }

    public void setString1(String string1) {
        this.string1 = string1;
    }

    public String getString2() {
        return string2;
    }

    public void setString2(String string2) {
        this.string2 = string2;
    }

    public String getString3() {
        return string3;
    }

    public void setString3(String string3) {
        this.string3 = string3;
    }

    public double getQty1() {
        return qty1;
    }

    public void setQty1(double qty1) {
        this.qty1 = qty1;
    }

    public double getQty2() {
        return qty2;
    }

    public void setQty2(double qty2) {
        this.qty2 = qty2;
    }
}

