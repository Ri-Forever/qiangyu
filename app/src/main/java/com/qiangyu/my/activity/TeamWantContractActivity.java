package com.qiangyu.my.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.qiangyu.R;
import com.qiangyu.utils.Constants;
import com.qiangyu.utils.Loading;
import com.qiangyu.utils.MD5Util;
import com.qiangyu.utils.SPUtils;
import com.qiangyu.utils.ToastUtil;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.SortedMap;
import java.util.TreeMap;

public class TeamWantContractActivity extends Activity {

    private String TAG = "TeamWantContractActivity";
    private Context mContext;
    private ImageButton ibBack;
    private TextView tvDanhao;
    private EditText etBianhao;
    private EditText etJine;
    private Button btnTijiao;
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
        setContentView(R.layout.activity_team_want_contract);
        initView();
    }

    private void initView() {
        ibBack = findViewById(R.id.ib_back);
        tvDanhao = findViewById(R.id.tv_danhao);
        etBianhao = findViewById(R.id.et_bianhao);
        etJine = findViewById(R.id.et_jine);
        btnTijiao = findViewById(R.id.btn_tijiao);

        tvDanhao.setText(mWantNo);

        ibBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btnTijiao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                /// <param n!ame="shopId"></param>
                /// <param name="mid"> 施工队</param>
                /// <param name="wantNo">需求单号</param>
                /// <param name="contractNo">合同编号</param>
                /// <param name="bill">合同金额</param>
                /// <param name="bDT">合同开始日期</param>
                /// <param name="eDT">合同结束日期</param>
                 */

                String cityId = SPUtils.getInstance().getString("CityId");
                getDataFromSetTeamWantContract(String.valueOf(mMid), cityId, mWantNo, etBianhao.getText().toString().trim(), etJine.getText().toString().trim(), "", "", mSignInPwd);
            }
        });
    }

    //=================================================填写合同=================================================\\
    /*
    /// <param n!ame="shopId"></param>
    /// <param name="mid"> 施工队</param>
    /// <param name="wantNo">需求单号</param>
    /// <param name="contractNo">合同编号</param>
    /// <param name="bill">合同金额</param>
    /// <param name="bDT">合同开始日期</param>
    /// <param name="eDT">合同结束日期</param>
     */
    void getDataFromSetTeamWantContract(String mid, String shopId, String wantNo, String contractNo, String bill, String bDT, String eDT, String signInPwd) {
        SortedMap<Object, Object> parameters = new TreeMap<>();
        String randomStr = MD5Util.getRandomStr();
        String timeStamp = MD5Util.getTimeStamp();
        String characterEncoding = "UTF-8";
        parameters.put("mid", mid);
        parameters.put("shopId", shopId);
        parameters.put("wantNo", wantNo);
        parameters.put("contractNo", contractNo);
        parameters.put("bill", bill);
        parameters.put("bDT", bDT);
        parameters.put("eDT", eDT);
        parameters.put("nonceStr", randomStr);
        parameters.put("timeStamp", timeStamp);
        String mySign = MD5Util.createSign(characterEncoding, parameters, signInPwd);

        Log.d(TAG, "randomStr === " + randomStr + "---timeStamp === " + timeStamp + " ---- mySign ===" + mySign + " --- mid --- " + mid);
        OkHttpUtils
                .post()
                .url(Constants.QIANGYU_URL + "SetTeamWantContract")
                .addParams("mid", mid)
                .addParams("shopId", shopId)
                .addParams("wantNo", wantNo)
                .addParams("contractNo", contractNo)
                .addParams("bill", bill)
                .addParams("bDT", bDT)
                .addParams("eDT", eDT)
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
                        processDataSetTeamWantContract(response);
                    }
                });

    }

    private void processDataSetTeamWantContract(String json) {
        Log.d(TAG, "processDataCartList: " + json);
        JSONObject jsonObject = JSON.parseObject(json);
        Object code = jsonObject.get("Code");
        if ("OK".equals(code)) {
            ToastUtil.toastCenter(mContext, "提交成功...");
            finish();
        } else {
            String errmsg = jsonObject.get("errmsg").toString();
            ToastUtil.toastCenter(mContext, "提交失败,失败原因: " + errmsg);
        }
    }

}
