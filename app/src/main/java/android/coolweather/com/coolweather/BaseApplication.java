package android.coolweather.com.coolweather;

import android.app.Application;

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
    }
}
