package android.coolweather.com.coolweather.activity;

import android.app.ProgressDialog;
import android.coolweather.com.coolweather.R;
import android.coolweather.com.coolweather.model.City;
import android.coolweather.com.coolweather.model.Country;
import android.coolweather.com.coolweather.model.Province;
import android.coolweather.com.coolweather.util.Constaint;
import android.coolweather.com.coolweather.util.JSONUtils;
import android.os.Bundle;
import android.support.v4.util.ArrayMap;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.tamic.novate.Novate;
import com.tamic.novate.Throwable;
import com.tamic.novate.callback.RxStringCallback;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

/**
 * @author:candy 创建时间:2017/11/29 8:57
 * 邮箱:1601796593@qq.com
 * 功能描述:
 * 省市选择
 */
public class ChooseAreaActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {


    private static final int LEVEL_PROVINCE = 0;
    private static final int LEVEL_CITY = 1;
    private static final int LEVEL_COUNTRY = 2;


    private TextView mTvAreName;
    private ListView mListview;
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

    private int mCurrentLevel = LEVEL_PROVINCE;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_area);
        initView();
        setListener();
        mAreaAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, mDatas);
        mListview.setAdapter(mAreaAdapter);
        queryProvince();
    }

    private void initView() {
        mTvAreName = findViewById(R.id.tv_are_name);
        mListview = findViewById(R.id.listview);


        mProgressDialog=new ProgressDialog(this);
        mProgressDialog.setMessage("loading...");
    }

    /***
     * 获取省份列表
     * 先从数据库获取如果为空则从网络获取
     */
    private void queryProvince() {
        mProvinces = DataSupport.findAll(Province.class);
        if (mProvinces.size() > 0) {
            mDatas.clear();
            for (Province province : mProvinces) {
                mDatas.add(province.getName());
            }
            mAreaAdapter.notifyDataSetChanged();
            mListview.setSelection(0);
            mTvAreName.setText("中国");
            mCurrentLevel = LEVEL_PROVINCE;
        } else {
            queryProvinceFromNet();
        }
    }

    /***
     * 获取城市列表
     * 先从数据库获取如果为空则从网络获取
     */
    private void queryCitys() {
        mCities = DataSupport.where("provinceId=?", mSelectedProvince.getId() + "").find(City.class);
        if (mCities.size() > 0) {
            mDatas.clear();
            for (City city : mCities) {
                mDatas.add(city.getName());
            }
            mAreaAdapter.notifyDataSetChanged();
            mListview.setSelection(0);
            mTvAreName.setText(mSelectedProvince.getName());
            mCurrentLevel = LEVEL_CITY;
        } else {
            queryCitysFromNet();
        }
    }

    /***
     * 获取区县列表
     * 先从数据库获取如果为空则从网络获取
     */
    private void queryCountry() {
        mCountries = DataSupport.where("cityId=?", mSelectedCity.getId() + "").find(Country.class);
        if (mCountries.size() > 0) {
            mDatas.clear();
            for (Country country : mCountries) {
                mDatas.add(country.getName());
            }
            mAreaAdapter.notifyDataSetChanged();
            mListview.setSelection(0);
            mTvAreName.setText(mSelectedCity.getName());
            mCurrentLevel = LEVEL_COUNTRY;
        } else {
            queryCountrysFromNet();
        }
    }

    /***
     * 网络获取所有省份列表
     */
    private void queryProvinceFromNet() {
        mProgressDialog.show();
        new Novate.Builder(this)
                .baseUrl(Constaint.API.BASE_HOST)
                .build()
                .rxGet(Constaint.API.API_PROVINCE_LIST, new ArrayMap<String, Object>(1), new RxStringCallback() {


                    @Override
                    public void onNext(Object tag, String response) {
                        if (!TextUtils.isEmpty(response)) {
                            List<Province> provinces = JSONUtils.paraseJsonArray(response);
                            for (Province province : provinces) {
                                province.save();
                            }
                            queryProvince();
                        }
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

    /***
     * 网络获取所有城市列表
     */
    private void queryCitysFromNet() {

        mProgressDialog.show();
        new Novate.Builder(this)
                .baseUrl(Constaint.API.BASE_HOST)
                .build()
                .rxGet(Constaint.API.API_PROVINCE_LIST + "/" + mSelectedProvince.getId(), new ArrayMap<String, Object>(1), new RxStringCallback() {
                    @Override
                    public void onNext(Object tag, String response) {
                        if (!TextUtils.isEmpty(response)) {
                            List<City> cities = JSONUtils.paraseCitys(response);
                            for (City city : cities) {
                                city.setProvinceId(mSelectedProvince.getId());
                                city.save();
                            }
                            queryCitys();
                        }
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

    /***
     * 网络获取所有区县列表
     */
    private void queryCountrysFromNet() {
        mProgressDialog.show();
        ArrayMap<String, Object> param = new ArrayMap<>(1);
        String url = Constaint.API.API_PROVINCE_LIST + "/" + mSelectedProvince.getId() + "/" + mSelectedCity.getId();
        new Novate.Builder(this)
                .baseUrl(Constaint.API.BASE_HOST)
                .build()
                .rxGet(url, param, new RxStringCallback() {
                    @Override
                    public void onNext(Object tag, String response) {
                        if (!TextUtils.isEmpty(response)) {
                            List<Country> countrys = JSONUtils.paraseCountrys(response);
                            for (Country country : countrys) {
                                country.setCityId(mSelectedCity.getId());
                                country.save();
                            }
                            queryCountry();
                        }
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
    private void setListener() {
        mListview.setOnItemClickListener(this);
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
