package com.dyg.siginprint.CutInventory.model;

import com.dyg.siginprint.purchase.model.PuechaseModel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CutCacheBean implements Serializable {
    private String billType;//单据类型
    private String billTypeName;
    private String loc;//调出库位 注意 调拨才会用到调入库位，其他都是调出库位
    private String SourceNo;//分切单号
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

    public String getSourceNo() {
        return SourceNo;
    }

    public void setSourceNo(String sourceNo) {
        SourceNo = sourceNo;
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
