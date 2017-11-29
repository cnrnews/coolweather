package com.uweather.activity;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.tamic.novate.Novate;
import com.uweather.util.Constaint;

/**
 * @author:candy
 * 创建时间:2017/11/29 14:29
 * 邮箱:1601796593@qq.com
 * 功能描述:
 * Activity基类
 */
public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
    public Novate getNovate()
    {
        return new Novate.Builder(this)
                .baseUrl(Constaint.API.BASE_HOST)
                .build();
    }
    public void showLog(String msg)
    {
        Log.i("TAG", "showLog: "+msg);
    }
}
