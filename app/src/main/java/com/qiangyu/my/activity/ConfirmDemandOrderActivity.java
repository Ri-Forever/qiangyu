package com.qiangyu.my.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.qiangyu.R;
import com.qiangyu.my.bean.JsonConfirmDemandOrderData;
import com.qiangyu.my.bean.WantInfo;
import com.qiangyu.payment.PayActivity;
import com.qiangyu.utils.Constants;
import com.qiangyu.utils.Loading;
import com.qiangyu.utils.MD5Util;
import com.qiangyu.utils.SPUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.SortedMap;
import java.util.TreeMap;

public class ConfirmDemandOrderActivity extends Activity {

    private String TAG = "ConfirmDemandOrderActivity";
    private Context mContext;
    private ImageButton ibBack;
    private TextView tvBianhao;
    private TextView tvHetongbianhao;
    private TextView tvJine;
    private TextView tvBilv;
    private TextView tvFuwufei;
    private TextView tvZongji;
    private TextView tvTijiao;
    private String mWantNo;
    private int mMid;
    private String mSignInPwd;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mContext = this;
        mWantNo = getIntent().getStringExtra("wantNo");
        mMid = SPUtils.getInstance().getInt("mid");
        mSignInPwd = SPUtils.getInstance().getString("signInPwd");
        String cityId = SPUtils.getInstance().getString("CityId");
        getDataFromGetWantPayInfo(String.valueOf(mMid), cityId, mWantNo, mSignInPwd);
        setContentView(R.layout.activity_confirm_demand_order);
        initView();
    }

    private void initView() {
        ibBack = findViewById(R.id.ib_back);
        tvBianhao = findViewById(R.id.tv_bianhao);
        tvHetongbianhao = findViewById(R.id.tv_hetongbianhao);
        tvJine = findViewById(R.id.tv_jine);
        tvBilv = findViewById(R.id.tv_bilv);
        tvFuwufei = findViewById(R.id.tv_fuwufei);
        tvZongji = findViewById(R.id.tv_zongji);
        tvTijiao = findViewById(R.id.tv_tijiao);

        ibBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        tvTijiao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String cityId = SPUtils.getInstance().getString("CityId");
                getDataFromConfirmWantOrder(String.valueOf(mMid), cityId, mWantNo, mSignInPwd);
            }
        });
    }

    //=================================================确认订单=================================================\\
    /*
    /// <param name="shopId"></param>
    /// <param name="mid">会员标识</param>
    /// <param name="wantNo">需求标识</param>
     */
    void getDataFromGetWantPayInfo(String mid, String shopId, String wantNo, String signInPwd) {
        SortedMap<Object, Object> parameters = new TreeMap<>();
        String randomStr = MD5Util.getRandomStr();
        String timeStamp = MD5Util.getTimeStamp();
        String characterEncoding = "UTF-8";
        parameters.put("mid", mid);
        parameters.put("shopId", shopId);
        parameters.put("wantNo", wantNo);
        parameters.put("nonceStr", randomStr);
        parameters.put("timeStamp", timeStamp);
        String mySign = MD5Util.createSign(characterEncoding, parameters, signInPwd);

        Log.d(TAG, "randomStr === " + randomStr + "---timeStamp === " + timeStamp + " ---- mySign ===" + mySign + " --- mid --- " + mid);
        OkHttpUtils
                .post()
                .url(Constants.QIANGYU_URL + "GetWantPayInfo")
                .addParams("mid", mid)
                .addParams("shopId", shopId)
                .addParams("wantNo", wantNo)
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
                        processDataGetWantPayInfo(response);
                    }
                });

    }

    private void processDataGetWantPayInfo(String json) {
        Log.d(TAG, "processDataCartList: " + json);
        JsonConfirmDemandOrderData jsonConfirmDemandOrderData = JSON.parseObject(json, JsonConfirmDemandOrderData.class);
        WantInfo wantInfo = jsonConfirmDemandOrderData.getResult().getWantInfo().get(0);

        tvBianhao.setText("编号 :  " + wantInfo.getWantNo());
        tvHetongbianhao.setText("合同编号 :  " + wantInfo.getContractNo());
        tvJine.setText("合同金额 :  " + wantInfo.getContractBill());
        tvBilv.setText("比率:  " + wantInfo.getPercent() + " %");
        tvFuwufei.setText("服务费:  " + wantInfo.getTotal());
        tvZongji.setText("总计 : ¥ " + wantInfo.getTotal() + " 元");

    }

    //=================================================提交支付订单=================================================\\
    /*
    /// <param name="shopId"></param>
    /// <param name="mid"></param>
    /// <param name="wantNo"></param>
    /// <returns> OrderNo 返回订单编号 去继续支付</returns>
     */
    void getDataFromConfirmWantOrder(String mid, String shopId, String wantNo, String signInPwd) {
        SortedMap<Object, Object> parameters = new TreeMap<>();
        String randomStr = MD5Util.getRandomStr();
        String timeStamp = MD5Util.getTimeStamp();
        String characterEncoding = "UTF-8";
        parameters.put("mid", mid);
        parameters.put("shopId", shopId);
        parameters.put("wantNo", wantNo);
        parameters.put("nonceStr", randomStr);
        parameters.put("timeStamp", timeStamp);
        String mySign = MD5Util.createSign(characterEncoding, parameters, signInPwd);

        Log.d(TAG, "randomStr === " + randomStr + "---timeStamp === " + timeStamp + " ---- mySign ===" + mySign + " --- mid --- " + mid);
        OkHttpUtils
                .post()
                .url(Constants.QIANGYU_URL + "ConfirmWantOrder")
                .addParams("mid", mid)
                .addParams("shopId", shopId)
                .addParams("wantNo", wantNo)
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
                        processDataConfirmWantOrder(response);
                    }
                });

    }

    private void processDataConfirmWantOrder(String json) {
        Log.d(TAG, "processDataCartList: " + json);
        JSONObject jsonObject = JSON.parseObject(json);
        String code = jsonObject.get("Code").toString();
        if ("OK".equals(code)) {
            String orderNo = jsonObject.get("OrderNo").toString();
            Intent intent = new Intent(mContext, PayActivity.class);
            intent.putExtra("mPayOrderNo", orderNo);
            startActivity(intent);
            finish();
        }

    }

}
