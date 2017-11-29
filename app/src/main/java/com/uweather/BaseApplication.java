package com.uweather;

import android.app.Application;

import net.youmi.android.AdManager;

import org.litepal.LitePal;

/**
 * 作者:candy
 * 创建时间:2017/11/28 18:35
 * 邮箱:1601796593@qq.com
 * 功能描述:
 */
public class BaseApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        LitePal.initialize(this);

        AdManager.getInstance(this)
                .init("9890f096893d2bc8","ac4b471748975581",
                        false);
    }
}
