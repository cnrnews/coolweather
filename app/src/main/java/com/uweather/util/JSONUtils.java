package com.uweather.util;

import com.google.gson.Gson;
import com.uweather.model.weather.Weather;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 作者:candy
 * 创建时间:2017/11/29 10:15
 * 邮箱:1601796593@qq.com
 * 功能描述:
 */
public class JSONUtils {

    /***
     * 解析数据集合
     * @param jsonStr
     * @return
     */
    public static List paraseDataList(String jsonStr, Class cls)
    {
        Gson gson=new Gson();
        List provinces=new ArrayList<>();
        try {
            JSONArray jsonArray=new JSONArray(jsonStr);
            int length=jsonArray.length();
            for (int i = 0; i <length ; i++) {
                provinces.add(gson.fromJson(jsonArray.getString(i),cls));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return provinces;
    }
    /**
     * 将返回的JSON数据解析成Weather实体类
     */
    public static Weather handleWeatherResponse(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray jsonArray = jsonObject.getJSONArray("HeWeather");
            String weatherContent = jsonArray.getJSONObject(0).toString();
            return new Gson().fromJson(weatherContent, Weather.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
