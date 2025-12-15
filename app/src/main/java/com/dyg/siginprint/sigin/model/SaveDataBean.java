package com.dyg.siginprint.sigin.model;

import java.util.List;

/**
 * author : DC-DingYG
 * e-mail : dingyg012655@126.com
 * time : 2021/07/18
 * desc : * 保存扫描结果数据
 */
public class SaveDataBean {

    /**
     * formName : FormScan
     * userCode : Admin
     * password :
     * onlineId : be0baa3f-3d25-44e9-80d9-4c9b99d9a38b
     * data : {"SourceNo":"PT2020090001","CustomerCode":"25.0115","CustomerName":"深圳金威源","Labels":[{"sn":"A00001","pn":"RL102M6R3F08003","customerPn":"10.05.100500998R","qty":4000,"lotNo":"Lo2021001","dateCode":"AL125","scanData":"A00001;RL102M6R3F08003;10.05.100500998R;4000;Lo2021001;AL125"},{"sn":"A00002","pn":"RL102M6R3F08003","customerPn":"10.05.100500998R","qty":1000,"lotNo":"Lo2021001","dateCode":"AL125","scanData":"A00001;RL102M6R3F08003;10.05.100500998R;1000;Lo2021001;AL125"}]}
     */

    /**
     * SourceNo : PT2020090001
     * CustomerCode : 25.0115
     * CustomerName : 深圳金威源
     * Labels : [{"sn":"A00001","pn":"RL102M6R3F08003","customerPn":"10.05.100500998R","qty":4000,"lotNo":"Lo2021001","dateCode":"AL125","scanData":"A00001;RL102M6R3F08003;10.05.100500998R;4000;Lo2021001;AL125"},{"sn":"A00002","pn":"RL102M6R3F08003","customerPn":"10.05.100500998R","qty":1000,"lotNo":"Lo2021001","dateCode":"AL125","scanData":"A00001;RL102M6R3F08003;10.05.100500998R;1000;Lo2021001;AL125"}]
     */

    private DataBean data;

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        private String SourceNo;
        private String CustomerCode;
        private String CustomerName;
        private List<BarcodeBean> Labels;

        public String getSourceNo() {
            return SourceNo;
        }

        public void setSourceNo(String SourceNo) {
            this.SourceNo = SourceNo;
        }

        public String getCustomerCode() {
            return CustomerCode;
        }

        public void setCustomerCode(String CustomerCode) {
            this.CustomerCode = CustomerCode;
        }

        public String getCustomerName() {
            return CustomerName;
        }

        public void setCustomerName(String CustomerName) {
            this.CustomerName = CustomerName;
        }

        public List<BarcodeBean> getLabels() {
            return Labels;
        }

        public void setLabels(List<BarcodeBean> Labels) {
            this.Labels = Labels;
        }
    }
}
