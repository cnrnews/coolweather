package com.uweather.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.v4.util.ArrayMap;

import com.tamic.novate.Novate;
import com.tamic.novate.Throwable;
import com.tamic.novate.callback.RxStringCallback;
import com.uweather.model.weather.Weather;
import com.uweather.util.Constaint;
import com.uweather.util.JSONUtils;

/**
 * @author:candy 创建时间:2017/11/29 11:45
 * 邮箱:1601796593@qq.com
 * 功能描述:
 * 自动天气更新服务
 */
public class AutoUpdateService extends Service {
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        updateWeather();
        updateBingPic();
        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        // 这是8小时的毫秒数
        int anHour = 8 * 60 * 60 * 1000;
        long triggerAtTime = SystemClock.elapsedRealtime() + anHour;
        Intent i = new Intent(this, AutoUpdateService.class);
        PendingIntent pi = PendingIntent.getService(this, 0, i, 0);
        manager.cancel(pi);
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pi);
        return super.onStartCommand(intent, flags, startId);
    }
    /**
     * 更新天气信息。
     */
    private void updateWeather() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherString = prefs.getString("weather", null);
        if (weatherString != null) {
            // 有缓存时直接解析天气数据
            Weather weather = JSONUtils.handleWeatherResponse(weatherString);
            String weatherId = weather.basic.weatherId;
            String weatherUrl = "http://guolin.tech/api/weather?cityid=" + weatherId + "&key=bc0418b57b2d4918819d3974ac1285d9";
            new Novate.Builder(this)
                    .baseUrl(Constaint.API.BASE_HOST)
                    .build()
                    .rxGet(weatherUrl, new ArrayMap<String, Object>(1), new RxStringCallback() {
                        @Override
                        public void onNext(Object tag, String response) {
                            Weather weather = JSONUtils.handleWeatherResponse(response);
                            if (weather != null && "ok".equals(weather.status)) {
                                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(AutoUpdateService.this).edit();
                                editor.putString("weather", response);
                                editor.apply();
                            }
                        }
                        @Override
                        public void onError(Object tag, Throwable e) {
                        }
                        @Override
                        public void onCancel(Object tag, Throwable e) {

                        }
                    });
        }
    }
    /**
     * 更新必应每日一图
     */
    private void updateBingPic() {
        String requestBingPic = "http://guolin.tech/api/bing_pic";
        new Novate.Builder(this)
                .baseUrl(Constaint.API.BASE_HOST)
                .build()
                .rxGet(requestBingPic, new ArrayMap<String, Object>(1), new RxStringCallback() {
                    @Override
                    public void onNext(Object tag, String bingPic) {
                        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(AutoUpdateService.this).edit();
                        editor.putString("bing_pic", bingPic);
                        editor.apply();
                    }

                    @Override
                    public void onError(Object tag, Throwable e) {

                    }

                    @Override
                    public void onCancel(Object tag, Throwable e) {

                    }
                });
    }

}
