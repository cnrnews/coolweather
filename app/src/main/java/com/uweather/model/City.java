package com.uweather.model;

import org.litepal.crud.DataSupport;

/**
 * 作者:candy
 * 创建时间:2017/11/29 08:37
 * 邮箱:1601796593@qq.com
 * 功能描述:
 * 城市
 */
public class City extends DataSupport {

    private int id;
    private int provinceId;
    private int code;
    private String name;

    public int getProvinceId() {
        return provinceId;
    }

    public void setProvinceId(int provinceId) {
        this.provinceId = provinceId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
