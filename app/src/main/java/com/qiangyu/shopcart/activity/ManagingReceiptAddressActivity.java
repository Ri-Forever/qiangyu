package com.qiangyu.shopcart.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.qiangyu.R;
import com.qiangyu.shopcart.adapter.ReceivingAddressAdapter;
import com.qiangyu.shopcart.bean.AddrList;
import com.qiangyu.shopcart.bean.JsonAddressListData;
import com.qiangyu.utils.Constants;
import com.qiangyu.utils.Loading;
import com.qiangyu.utils.MD5Util;
import com.qiangyu.utils.MyListView;
import com.qiangyu.utils.SPUtils;
import com.qiangyu.utils.ToastUtil;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

public class ManagingReceiptAddressActivity extends Activity {

    private Context mContext;
    private ImageButton ibBack;
    private MyListView mlvAddressList;
    private TextView tvSubmit;
    private String mSignInPwd;
    private String mMid;
    private String mShopId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mContext = this;
        mSignInPwd = SPUtils.getInstance().getString("signInPwd");
        mMid = String.valueOf(SPUtils.getInstance().getInt("mid"));
        mShopId = SPUtils.getInstance().getString("CityId");
        setContentView(R.layout.activity_managing_receipt_address);
        initView();

    }

    private void initView() {
        ibBack = findViewById(R.id.ib_back);
        mlvAddressList = findViewById(R.id.mlv_address_list);
        tvSubmit = findViewById(R.id.tv_submit);

        ibBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        tvSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, NewAddressActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        getDataFromGetUserDeliveryAddress(mShopId, mMid, mSignInPwd);
    }

    //=================================================获取用户的收货地址=================================================\\
    /*
    /// <param name="nonceStr"></param>
    /// <param name="timeStamp"></param>
    /// <param name="sign"></param>
    /// <param name="mid"></param>
    /// <param name="shopId"></param>
    */
    void getDataFromGetUserDeliveryAddress(final String shopId, final String mid, final String signInPwd) {
        Loading.loading(mContext, "正在加载...");
        SortedMap<Object, Object> parameters = new TreeMap<>();
        String randomStr = MD5Util.getRandomStr();
        String timeStamp = MD5Util.getTimeStamp();
        String characterEncoding = "UTF-8";
        parameters.put("shopId", shopId);
        parameters.put("mid", mid);
        parameters.put("nonceStr", randomStr);
        parameters.put("timeStamp", timeStamp);
        String mySign = MD5Util.createSign(characterEncoding, parameters, signInPwd);

        Log.d("qianming", "randomStr === " + randomStr + "---timeStamp === " + timeStamp + " ---- mySign ===" + mySign + " --- mid --- " + mid);
        OkHttpUtils
                .post()
                .url(Constants.QIANGYU_URL + "GetUserDeliveryAddress")
                .addParams("shopId", shopId)
                .addParams("mid", mid)
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
                    public void onError(okhttp3.Call call, Exception e, int id) {
                        Log.d("getDataFromNet", "请求失败 === >" + e.getMessage());
                        Loading.endLoad();
                        //getDataFromCartList(shopId, mid, signInPwd);
                    }

                    /**
                     * 当联网成功时回调
                     * @param response
                     * @param id
                     */
                    @Override
                    public void onResponse(String response, int id) {
                        Log.d("getDataFromNet", " 未更改 === >" + response);
                        //解析数据
                        //response = response.replace("", "");
                        response = response.replace("\\", "");
                        response = response.replace("\"[", "[");
                        response = response.replace("]\"", "]");
                        Log.d("getDataFromNet", " 更改过 === >" + response);
                        GetUserDeliveryAddress(response);
                        Loading.endLoad();
                    }
                });

    }

    private void GetUserDeliveryAddress(String json) {
        Log.d("processDataCartList", "processDataCartList: " + json);
        JsonAddressListData jsonAddressListData = JSON.parseObject(json, JsonAddressListData.class);
        final List<AddrList> addrLists = jsonAddressListData.getResult().getAddrList();
        mlvAddressList.setAdapter(new ReceivingAddressAdapter(mContext, addrLists));

    }

}
