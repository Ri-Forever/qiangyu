package com.qiangyu.base;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alibaba.fastjson.JSON;
import com.qiangyu.home.bean.JsonShopListData;
import com.qiangyu.home.bean.ShopList;
import com.qiangyu.utils.Constants;
import com.qiangyu.utils.MD5Util;
import com.qiangyu.utils.SPUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import okhttp3.Call;

public abstract class BaseFragment extends Fragment {

    private String TAG = "BaseFragment";
    protected Context mContext;
    //弹出框
    private AlertDialog alertDialog;
    private String mCId;
    private String mCity;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return initView();
    }


    protected abstract View initView();

    //当activity被创建时回调这个方法
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initData();
    }

    @Override
    public void onStart() {
        super.onStart();
        String cityId = SPUtils.getInstance().getString("CityId");
        if (!StringUtils.isNotEmpty(cityId)) {
            //getDataFromGetShopList();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    //当子类需要联网请求数据的时候可以重写该方法
    public void initData() {
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
        List<ShopList> shopListList = jsonShopListData.getResult().getShopList();
        final String[] items = new String[shopListList.size()];
        final String[] cityId = new String[shopListList.size()];

        for (int j = 0; j < shopListList.size(); j++) {
            int id = shopListList.get(j).getId();
            String name = shopListList.get(j).getName();
            items[j] = name;
            cityId[j] = String.valueOf(id);
        }
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(mContext);
        alertBuilder.setTitle("请选择您的所在城市");
        alertBuilder.setSingleChoiceItems(items, 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mCId = cityId[i];//城市id
                mCity = items[i];//城市名称
            }
        });

        alertBuilder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Log.d(TAG, "onClick: " + mCId);
                if (StringUtils.isNotEmpty(mCity) && StringUtils.isNotEmpty(mCId)) {
                    SPUtils.getInstance().put("CityId", String.valueOf(mCId));
                    SPUtils.getInstance().put("City", mCity);
                } else {
                    mCId = cityId[0];//城市id
                    mCity = items[0];//城市名称
                    SPUtils.getInstance().put("CityId", String.valueOf(mCId));
                    SPUtils.getInstance().put("City", mCity);
                }
                alertDialog.dismiss();
            }
        });
        alertBuilder.setCancelable(false);
        alertDialog = alertBuilder.create();
        alertDialog.show();
    }
}
