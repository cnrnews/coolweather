package com.uweather.model;

import org.litepal.crud.DataSupport;

/**
 * 作者:candy
 * 创建时间:2017/11/29 08:35
 * 邮箱:1601796593@qq.com
 * 功能描述:
 * 省份
 */
public class Province extends DataSupport {
    private int id;
    private int code;
    private String name;
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
