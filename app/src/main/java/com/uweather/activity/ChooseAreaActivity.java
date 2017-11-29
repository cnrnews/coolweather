package com.uweather.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.util.ArrayMap;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.tamic.novate.Throwable;
import com.tamic.novate.callback.RxStringCallback;
import com.uweather.R;
import com.uweather.model.City;
import com.uweather.model.Country;
import com.uweather.model.Province;
import com.uweather.util.Constaint;
import com.uweather.util.JSONUtils;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author:candy 创建时间:2017/11/29 8:57
 * 邮箱:1601796593@qq.com
 * 功能描述:
 * 省市选择
 */
public class ChooseAreaActivity extends BaseActivity implements AdapterView.OnItemClickListener {


    private static final int LEVEL_PROVINCE = 0;
    private static final int LEVEL_CITY = 1;
    private static final int LEVEL_COUNTRY = 2;
    @BindView(R.id.tv_are_name)
    TextView mTvAreName;
    @BindView(R.id.listview)
    ListView mListview;

    private ArrayAdapter<String> mAreaAdapter;
    private List<String> mDatas = new ArrayList<>();


    private ProgressDialog mProgressDialog;

    /**
     * 省份
     **/
    private List<Province> mProvinces = new ArrayList<>();
    /**
     * 城市
     **/
    private List<City> mCities = new ArrayList<>();
    /**
     * 区县
     **/
    private List<Country> mCountries = new ArrayList<>();

    /**
     * 当前选中省份
     **/
    private Province mSelectedProvince;
    /**
     * 当前选中城市
     **/
    private City mSelectedCity;

    private int mCurrentLevel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_area);
        ButterKnife.bind(this);
        initView();
        setListener();
        mAreaAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, mDatas);
        mListview.setAdapter(mAreaAdapter);
        queryProvince();
    }

    private void initView() {
//        mTvAreName = findViewById(R.id.tv_are_name);
//        mListview = findViewById(R.id.listview);
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("loading...");
    }

    private void setListener() {
        mListview.setOnItemClickListener(this);
    }

    /***
     * 获取省份列表
     * 先从数据库获取如果为空则从网络获取
     */
    private void queryProvince() {
        mCurrentLevel = LEVEL_PROVINCE;
        mProvinces = DataSupport.findAll(Province.class);
        if (mProvinces.size() > 0) {
            mDatas.clear();
            for (Province province : mProvinces) {
                mDatas.add(province.getName());
            }
            mAreaAdapter.notifyDataSetChanged();
            mListview.setSelection(0);
            mTvAreName.setText("中国");
        } else {
            queryListFromNet();
        }
    }

    /***
     * 获取城市列表
     * 先从数据库获取如果为空则从网络获取
     */
    private void queryCitys() {
        mCurrentLevel = LEVEL_CITY;
        mCities = DataSupport.where("provinceId=?", mSelectedProvince.getId() + "").find(City.class);
        if (mCities.size() > 0) {
            mDatas.clear();
            for (City city : mCities) {
                mDatas.add(city.getName());
            }
            mAreaAdapter.notifyDataSetChanged();
            mListview.setSelection(0);
            mTvAreName.setText(mSelectedProvince.getName());
        } else {
            queryListFromNet();
        }
    }

    /***
     * 获取区县列表
     * 先从数据库获取如果为空则从网络获取
     */
    private void queryCountry() {
        mCurrentLevel = LEVEL_COUNTRY;
        mCountries = DataSupport.where("cityId=?", mSelectedCity.getId() + "").find(Country.class);
        if (mCountries.size() > 0) {
            mDatas.clear();
            for (Country country : mCountries) {
                mDatas.add(country.getName());
            }
            mAreaAdapter.notifyDataSetChanged();
            mListview.setSelection(0);
            mTvAreName.setText(mSelectedCity.getName());
        } else {
            queryListFromNet();
        }
    }

    /***
     * 网络获取列表数据
     */
    private void queryListFromNet() {
        mProgressDialog.show();
        getNovate()
                .rxGet(buildUrl(), new ArrayMap<String, Object>(1), new RxStringCallback() {
                    @Override
                    public void onNext(Object tag, String response) {
                        parseDataJson(response);
                        mProgressDialog.dismiss();
                    }

                    @Override
                    public void onError(Object tag, Throwable e) {
                        mProgressDialog.dismiss();
                    }

                    @Override
                    public void onCancel(Object tag, Throwable e) {
                        mProgressDialog.dismiss();
                    }
                });
    }

    private void parseDataJson(String response) {
        if (!TextUtils.isEmpty(response)) {
            switch (mCurrentLevel) {
                case LEVEL_PROVINCE:
                    List<Province> provinces = JSONUtils.paraseDataList(response, Province.class);
                    for (Province province : provinces) {
                        province.save();
                    }
                    queryProvince();
                    break;
                case LEVEL_CITY:
                    List<City> cities = JSONUtils.paraseDataList(response, City.class);
                    for (City city : cities) {
                        city.setProvinceId(mSelectedProvince.getId());
                        city.save();
                    }
                    queryCitys();
                    break;
                case LEVEL_COUNTRY:
                    List<Country> countrys = JSONUtils.paraseDataList(response, Country.class);
                    for (Country country : countrys) {
                        country.setCityId(mSelectedCity.getId());
                        country.save();
                    }
                    queryCountry();
                    break;
                default:
                    break;
            }
        }
    }

    /***
     * 生成请求的url地址
     * @return
     */
    private String buildUrl() {
        switch (mCurrentLevel) {
            case LEVEL_PROVINCE:
                return Constaint.API.API_PROVINCE_LIST;
            case LEVEL_CITY:
                return Constaint.API.API_PROVINCE_LIST + "/" + mSelectedProvince.getId();
            case LEVEL_COUNTRY:
                return Constaint.API.API_PROVINCE_LIST + "/" + mSelectedProvince.getId() + "/" + mSelectedCity.getId();
            default:
                break;
        }
        return null;
    }

    /****
     * 列表单击事件
     * @param adapterView
     * @param view
     * @param position
     * @param l
     */
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        switch (mCurrentLevel) {
            case LEVEL_PROVINCE:
                mSelectedProvince = mProvinces.get(position);
                queryCitys();
                break;
            case LEVEL_CITY:
                mSelectedCity = mCities.get(position);
                queryCountry();
                break;
            case LEVEL_COUNTRY:
                MainActivity.actionStart(this, mCountries.get(position).getWeather_id());
                break;
            default:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (mCurrentLevel == LEVEL_COUNTRY) {
            queryCitys();
        } else if (mCurrentLevel == LEVEL_CITY) {
            queryProvince();
        } else {
            finish();
        }
    }
}
