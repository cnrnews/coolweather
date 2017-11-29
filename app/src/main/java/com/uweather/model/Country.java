package com.uweather.model;

import org.litepal.crud.DataSupport;

/**
 * 作者:candy
 * 创建时间:2017/11/29 08:40
 * 邮箱:1601796593@qq.com
 * 功能描述:
 * 区县
 */
public class Country extends DataSupport {
    private int id;
    private int cityId;
    private int code;
    private String name;
    private String weather_id;

    public int getCityId() {
        return cityId;
    }

    public void setCityId(int cityId) {
        this.cityId = cityId;
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

    public String getWeather_id() {
        return weather_id;
    }

    public void setWeather_id(String weather_id) {
        this.weather_id = weather_id;
    }
}
