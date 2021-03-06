package com.uweather.util;

/**
 * 作者:candy
 * 创建时间:2017/11/29 09:27
 * 邮箱:1601796593@qq.com
 * 功能描述:
 * API接口常量类
 */
public interface Constaint {


    interface API{

        /**APP key**/
        String APP_KEY="13a8db227b3143e2a660970097cdc891";
        /**HOST**/
        String BASE_HOST="http://guolin.tech/api/";
        /**省份列表**/
        String API_PROVINCE_LIST=BASE_HOST+"china";
        /**地区天气?cityid=?&key=?**/
        String API_WARTHER="http://guolin.tech/api/weather";
    }

}
