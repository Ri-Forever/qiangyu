package com.qiangyu.home.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.qiangyu.R;
import com.qiangyu.home.adapter.CityAdapter;
import com.qiangyu.home.bean.JsonShopListData;
import com.qiangyu.home.bean.ShopList;
import com.qiangyu.utils.Constants;
import com.qiangyu.utils.MD5Util;
import com.qiangyu.utils.MyListView;
import com.qiangyu.utils.SPUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.apache.commons.lang3.StringUtils;

import java.text.Collator;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.SortedMap;
import java.util.TreeMap;

import okhttp3.Call;

public class CityListActivity extends Activity {

    private Context mContext;
    private MyListView mCityList;
    private TextView mTv_dangqian;
    private String mCity;
    private List<ShopList> mShopListList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mContext = this;
        mCity = SPUtils.getInstance().getString("City");
        setContentView(R.layout.activity_city_list);
        initView();
        getDataFromGetShopList();
    }

    private void initView() {
        mTv_dangqian = findViewById(R.id.tv_dangqian);
        mCityList = findViewById(R.id.lv_city_list);

        if (StringUtils.isNotEmpty(mCity)) {
            mTv_dangqian.setText("您当前选择的城市是: " + mCity + "  点击下列城市列表切换城市!");
        } else {
            mTv_dangqian.setText("您当前尚未选择城市,请点击下列城市列表选择!");
        }
    }

    //=================================================获取城市列表=================================================\\
    //public ActionResult GetShopList(string nonceStr, string timeStamp, string sign)
    void getDataFromGetShopList() {
        SortedMap<Object, Object> parameters = new TreeMap<>();
        String randomStr = MD5Util.getRandomStr();
        String timeStamp = MD5Util.getTimeStamp();
        String characterEncoding = "UTF-8";
        parameters.put("nonceStr", randomStr);
        parameters.put("timeStamp", timeStamp);
        String mySign = MD5Util.createSign(characterEncoding, parameters);

        OkHttpUtils
                .post()
                .url(Constants.QIANGYU_URL + "GetShopList")
                .addParams("nonceStr", randomStr)
                .addParams("timeStamp", timeStamp)
                .addParams("sign", mySign)
                .build()
                .readTimeOut(10000)
                .writeTimeOut(10000)
                .connTimeOut(10000)
                /**
                 * 请求失败的时候回调
                 */
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                    }

                    /**
                     * 当联网成功时回调
                     * @param response
                     * @param id
                     */
                    @Override
                    public void onResponse(String response, int id) {
                        //解析数据
                        response = response.replace("\\", "");
                        response = response.replace("\"[", "[");
                        response = response.replace("]\"", "]");
                        Log.d("getDataFromNet", " 首页轮播 === >" + response);
                        getShopList(response);
                    }
                });
    }

    private void getShopList(String json) {
        Log.d("getShopList", "获取城市信息: " + json);
        JsonShopListData jsonShopListData = JSON.parseObject(json, JsonShopListData.class);
        mShopListList = jsonShopListData.getResult().getShopList();
        Collections.sort(mShopListList, new Comparator<ShopList>() {
            public int compare(ShopList o1, ShopList o2) {
                String s1 = o1.getName();
                String s2 = o2.getName();
                return Collator.getInstance(Locale.CHINESE).compare(s1, s2);
            }
        });
        mCityList.setAdapter(new CityAdapter(mContext, mShopListList));
        mCityList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String name = mShopListList.get(position).getName();
                int mCId = mShopListList.get(position).getId();
                SPUtils.getInstance().put("CityId", String.valueOf(mCId));
                SPUtils.getInstance().put("City", name);
                Intent result = new Intent();
                setResult(1, result);
                finish();
            }
        });
    }
}
