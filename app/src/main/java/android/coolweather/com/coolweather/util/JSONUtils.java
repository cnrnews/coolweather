package android.coolweather.com.coolweather.util;

import android.coolweather.com.coolweather.model.City;
import android.coolweather.com.coolweather.model.Country;
import android.coolweather.com.coolweather.model.Province;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;

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
     * 解析省份集合
     * @param jsonStr
     * @return
     */
    public static List<Province> paraseJsonArray(String jsonStr)
    {
        Gson gson=new Gson();
        List<Province>provinces=new ArrayList<>();
        try {
            JSONArray jsonArray=new JSONArray(jsonStr);
            int length=jsonArray.length();
            for (int i = 0; i <length ; i++) {
                provinces.add(gson.fromJson(jsonArray.getString(i),Province.class));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return provinces;
    }
    /***
     * 解析城市集合
     * @param jsonStr
     * @return
     */
    public static List<City> paraseCitys(String jsonStr)
    {
        Gson gson=new Gson();
        List<City>provinces=new ArrayList<>();
        try {
            JSONArray jsonArray=new JSONArray(jsonStr);
            int length=jsonArray.length();
            for (int i = 0; i <length ; i++) {
                provinces.add(gson.fromJson(jsonArray.getString(i),City.class));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return provinces;
    }
    /***
     * 解析城市集合
     * @param jsonStr
     * @return
     */
    public static List<Country> paraseCountrys(String jsonStr)
    {
        Gson gson=new Gson();
        List<Country>provinces=new ArrayList<>();
        try {
            JSONArray jsonArray=new JSONArray(jsonStr);
            int length=jsonArray.length();
            for (int i = 0; i <length ; i++) {
                provinces.add(gson.fromJson(jsonArray.getString(i),Country.class));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return provinces;
    }
}
