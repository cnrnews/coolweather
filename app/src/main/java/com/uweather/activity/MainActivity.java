package com.uweather.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.util.ArrayMap;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.tamic.novate.Novate;
import com.tamic.novate.Throwable;
import com.tamic.novate.callback.RxStringCallback;
import com.uweather.R;
import com.uweather.model.weather.Forecast;
import com.uweather.model.weather.Weather;
import com.uweather.service.AutoUpdateService;
import com.uweather.util.Constaint;
import com.uweather.util.JSONUtils;

import net.youmi.android.nm.bn.BannerManager;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author:candy 创建时间:2017/11/29 10:56
 * 邮箱:1601796593@qq.com
 * 功能描述:
 * 天气首页
 */
public class MainActivity extends BaseActivity {

    private static final String WEATHER_ID = "weather_id";
    public DrawerLayout drawerLayout;

    public SwipeRefreshLayout swipeRefresh;
    @BindView(R.id.bing_pic_img)
    ImageView mBingPicImg;
    @BindView(R.id.nav_button)
    Button mNavButton;
    @BindView(R.id.title_city)
    TextView mTitleCity;
    @BindView(R.id.title_update_time)
    TextView mTitleUpdateTime;
    @BindView(R.id.degree_text)
    TextView mDegreeText;
    @BindView(R.id.weather_info_text)
    TextView mWeatherInfoText;
    @BindView(R.id.forecast_layout)
    LinearLayout mForecastLayout;
    @BindView(R.id.aqi_text)
    TextView mAqiText;
    @BindView(R.id.pm25_text)
    TextView mPm25Text;
    @BindView(R.id.comfort_text)
    TextView mComfortText;
    @BindView(R.id.car_wash_text)
    TextView mCarWashText;
    @BindView(R.id.sport_text)
    TextView mSportText;
    @BindView(R.id.weather_layout)
    ScrollView mWeatherLayout;
    @BindView(R.id.swipe_refresh)
    SwipeRefreshLayout mSwipeRefresh;
    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;

//    private ScrollView weatherLayout;
//
//    private Button navButton;
//
//    private TextView titleCity;
//
//    private TextView titleUpdateTime;
//
//    private TextView degreeText;
//
//    private TextView weatherInfoText;
//
//    private LinearLayout forecastLayout;
//
//    private TextView aqiText;
//
//    private TextView pm25Text;
//
//    private TextView comfortText;
//
//    private TextView carWashText;
//
//    private TextView sportText;
//
//    private ImageView bingPicImg;

    private String mWeatherId;

    private Context mContext;

    public static void actionStart(Context context, String weatherID) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra(WEATHER_ID, weatherID);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        mContext = this;
        // 初始化各控件
//        bingPicImg = (ImageView) findViewById(R.id.bing_pic_img);
//        weatherLayout = (ScrollView) findViewById(R.id.weather_layout);
//        titleCity = (TextView) findViewById(R.id.title_city);
//        titleUpdateTime = (TextView) findViewById(R.id.title_update_time);
//        degreeText = (TextView) findViewById(R.id.degree_text);
//        weatherInfoText = (TextView) findViewById(R.id.weather_info_text);
//        forecastLayout = (LinearLayout) findViewById(R.id.forecast_layout);
//        aqiText = (TextView) findViewById(R.id.aqi_text);
//        pm25Text = (TextView) findViewById(R.id.pm25_text);
//        comfortText = (TextView) findViewById(R.id.comfort_text);
//        carWashText = (TextView) findViewById(R.id.car_wash_text);
//        sportText = (TextView) findViewById(R.id.sport_text);
        swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        swipeRefresh.setColorSchemeResources(R.color.colorPrimary);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
//        navButton = (Button) findViewById(R.id.nav_button);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        // 无缓存时去服务器查询天气
        mWeatherId = getIntent().getStringExtra(WEATHER_ID);
        mWeatherLayout.setVisibility(View.INVISIBLE);
        requestWeather(mWeatherId);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestWeather(mWeatherId);
            }
        });
        mNavButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
        String bingPic = prefs.getString("bing_pic", null);
        if (bingPic != null) {
            Glide.with(this).load(bingPic).into(mBingPicImg);
        } else {
            loadBingPic();
        }
        setupBannerAd();
    }

    /**
     * 根据天气id请求城市天气信息。
     */
    public void requestWeather(final String weatherId) {
        String weatherUrl = Constaint.API.API_WARTHER + "?cityid=" + weatherId + "&key=" + Constaint.API.APP_KEY;
        new Novate.Builder(this)
                .baseUrl(Constaint.API.BASE_HOST)
                .build()
                .rxGet(weatherUrl, new ArrayMap<String, Object>(1), new RxStringCallback() {
                    @Override
                    public void onNext(Object tag, String response) {
                        final Weather weather = JSONUtils.handleWeatherResponse(response);
                        if (weather != null && "ok".equals(weather.status)) {
                            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(MainActivity.this).edit();
                            editor.putString("weather", response);
                            editor.apply();
                            mWeatherId = weather.basic.weatherId;
                            showWeatherInfo(weather);
                        } else {
                            Toast.makeText(MainActivity.this, "获取天气信息失败", Toast.LENGTH_SHORT).show();
                        }
                        swipeRefresh.setRefreshing(false);
                    }

                    @Override
                    public void onError(Object tag, Throwable e) {
                        Toast.makeText(MainActivity.this, "获取天气信息失败", Toast.LENGTH_SHORT).show();
                        swipeRefresh.setRefreshing(false);
                    }

                    @Override
                    public void onCancel(Object tag, Throwable e) {

                    }
                });

        loadBingPic();
    }

    /**
     * 加载必应每日一图
     */
    private void loadBingPic() {
        String requestBingPic = "http://guolin.tech/api/bing_pic";
        new Novate.Builder(this)
                .baseUrl(Constaint.API.BASE_HOST)
                .build()
                .rxGet(requestBingPic, new ArrayMap<String, Object>(1), new RxStringCallback() {

                    @Override
                    public void onNext(Object tag, String response) {
                        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(mContext).edit();
                        editor.putString("bing_pic", response);
                        editor.apply();
                        Glide.with(mContext).load(response).into(mBingPicImg);
                    }

                    @Override
                    public void onError(Object tag, Throwable e) {

                    }

                    @Override
                    public void onCancel(Object tag, Throwable e) {

                    }
                });
    }

    /**
     * 处理并展示Weather实体类中的数据。
     */
    private void showWeatherInfo(Weather weather) {
        String cityName = weather.basic.cityName;
        String updateTime = weather.basic.update.updateTime.split(" ")[1];
        String degree = weather.now.temperature + "℃";
        String weatherInfo = weather.now.more.info;
        mTitleCity.setText(cityName);
        mTitleUpdateTime.setText(updateTime);
        mDegreeText.setText(degree);
        mWeatherInfoText.setText(weatherInfo);
        mForecastLayout.removeAllViews();
        for (Forecast forecast : weather.forecastList) {
            View view = LayoutInflater.from(this).inflate(R.layout.forecast_item, mForecastLayout, false);
            TextView dateText = (TextView) view.findViewById(R.id.date_text);
            TextView infoText = (TextView) view.findViewById(R.id.info_text);
            TextView maxText = (TextView) view.findViewById(R.id.max_text);
            TextView minText = (TextView) view.findViewById(R.id.min_text);
            dateText.setText(forecast.date);
            infoText.setText(forecast.more.info);
            maxText.setText(forecast.temperature.max);
            minText.setText(forecast.temperature.min);
            mForecastLayout.addView(view);
        }
        if (weather.aqi != null) {
            mAqiText.setText(weather.aqi.city.aqi);
            mPm25Text.setText(weather.aqi.city.pm25);
        }
        String comfort = "舒适度：" + weather.suggestion.comfort.info;
        String carWash = "洗车指数：" + weather.suggestion.carWash.info;
        String sport = "运行建议：" + weather.suggestion.sport.info;
        mComfortText.setText(comfort);
        mCarWashText.setText(carWash);
        mSportText.setText(sport);
        mWeatherLayout.setVisibility(View.VISIBLE);
        Intent intent = new Intent(this, AutoUpdateService.class);
        startService(intent);
    }

    /**
     * 设置广告条广告
     */
    private void setupBannerAd() {
        FrameLayout.LayoutParams layoutParams =
                new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        // 设置广告条的悬浮位置，这里示例为右下角
        layoutParams.gravity = Gravity.BOTTOM | Gravity.RIGHT;
        // 获取广告条
        final View bannerView = BannerManager.getInstance(mContext)
                .getBannerView(mContext, null);
        // 添加广告条到窗口中
        ((Activity) mContext).addContentView(bannerView, layoutParams);
    }
}
