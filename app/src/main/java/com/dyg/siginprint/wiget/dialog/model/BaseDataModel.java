package com.dyg.siginprint.wiget.dialog.model;

import java.io.Serializable;

public class BaseDataModel implements Serializable {
    private String name;
    private String code;
    private String typeCode;

    private String id;//设备保养不是code,是id

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    //类别
    public String getTypeCode()  {
        return typeCode;
    }
    public void setTypeCode(String typeCode) {
        this.typeCode = typeCode;
    }
}
