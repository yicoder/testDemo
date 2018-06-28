package com.yicoder.mybatis;

import java.io.Serializable;

public class PubProvincesDO implements Serializable{

    private Double id;
    private String provinceid;
    private String province;

    public Double getId() {
        return id;
    }

    public void setId(Double id) {
        this.id = id;
    }

    public String getProvinceid() {
        return provinceid;
    }

    public void setProvinceid(String provinceid) {
        this.provinceid = provinceid;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }
}
