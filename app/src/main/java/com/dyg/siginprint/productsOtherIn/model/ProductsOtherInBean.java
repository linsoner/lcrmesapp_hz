package com.dyg.siginprint.productsOtherIn.model;

import com.dyg.siginprint.purchase.model.PuechaseModel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ProductsOtherInBean implements Serializable {
    private String billType;//单据类型
    private String billTypeName;
    private String loc;//库位
    private String customerCode;//车间
    private String Remark;//备注
    private List<PuechaseModel> scanFinishList = new ArrayList<>();//已扫描的条码

    public String getBillType() {
        return billType;
    }

    public void setBillType(String billType) {
        this.billType = billType;
    }

    public String getBillTypeName() {
        return billTypeName;
    }

    public void setBillTypeName(String billTypeName) {
        this.billTypeName = billTypeName;
    }

    public String getLoc() {
        return loc;
    }

    public void setLoc(String loc) {
        this.loc = loc;
    }

    public String getCustomerCode() {
        return customerCode;
    }

    public void setCustomerCode(String customerCode) {
         this.customerCode = customerCode;
    }

    public String getRemark() {
        return Remark;
    }

    public void setRemark(String remark) {
        Remark = remark;
    }

    public List<PuechaseModel> getScanFinishList() {
        return scanFinishList;
    }

    public void setScanFinishList(List<PuechaseModel> scanFinishList) {
        this.scanFinishList = scanFinishList;
    }
}
