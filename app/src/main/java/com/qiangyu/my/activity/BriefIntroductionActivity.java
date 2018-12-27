package com.qiangyu.my.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.qiangyu.R;
import com.qiangyu.utils.Constants;
import com.qiangyu.utils.MD5Util;
import com.qiangyu.utils.SPUtils;
import com.qiangyu.utils.ToastUtil;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.apache.commons.lang3.StringUtils;

import java.util.SortedMap;
import java.util.TreeMap;

public class BriefIntroductionActivity extends Activity implements View.OnClickListener {

    private String TAG = "BriefIntroductionActivity";
    private Context mContext;
    private ImageButton ibBack;
    private EditText etBriefIntroduction;
    private TextView tvSubmit;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mContext = this;
        initView();
    }

    private void initView() {
        setContentView(R.layout.activity_brief_introduction);

        ibBack = findViewById(R.id.ib_back);
        etBriefIntroduction = findViewById(R.id.et_brief_introduction);
        tvSubmit = findViewById(R.id.tv_submit);

        ibBack.setOnClickListener(this);
        tvSubmit.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view == ibBack) {
            Toast.makeText(this, "取消设置简介!", Toast.LENGTH_SHORT).show();
            finish();
        }
        if (view == tvSubmit) {
            Toast.makeText(this, "提交", Toast.LENGTH_SHORT).show();
            String jianjie = etBriefIntroduction.getText().toString().trim();
            if (StringUtils.isNotEmpty(jianjie)) {
                int mid = SPUtils.getInstance().getInt("mid");
                String signInPwd = SPUtils.getInstance().getString("signInPwd");
                getDataFromSetUserInfo(String.valueOf(mid), "2", jianjie, signInPwd);
                return;
            }
            ToastUtil.toastCenter(mContext, "您还没有填写简介哦");
        }
    }

    //=================================================修改简介=================================================\\
    /*
    /// <param name="mid">会员标识</param>
    /// <param name="type">资料类型；  type=1：昵称;2：描述   </param>
    int mid = SPUtils.getInstance().getInt("mid");
    String signInPwd = SPUtils.getInstance().getString("signInPwd");
    getDataFromSetUserInfo(String.valueOf(mid), "1", name, signInPwd);
     */
    void getDataFromSetUserInfo(String mid, String type, String value, String signInPwd) {
        SortedMap<Object, Object> parameters = new TreeMap<>();
        String randomStr = MD5Util.getRandomStr();
        String timeStamp = MD5Util.getTimeStamp();
        String characterEncoding = "UTF-8";
        parameters.put("mid", mid);
        parameters.put("type", type);
        parameters.put("value", value);
        parameters.put("nonceStr", randomStr);
        parameters.put("timeStamp", timeStamp);
        String mySign = MD5Util.createSign(characterEncoding, parameters, signInPwd);

        OkHttpUtils
                .post()
                .url(Constants.QIANGYU_URL + "SetUserInfo")
                .addParams("mid", mid)
                .addParams("type", type)
                .addParams("value", value)
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
                        ToastUtil.toastCenter(mContext,"网络请求失败,请稍后再试...");
                    }

                    /**
                     * 当联网成功时回调
                     * @param response
                     * @param id
                     */
                    @Override
                    public void onResponse(String response, int id) {
                        //解析数据
                        Log.d("getDataFromNet", " 未改过 === >" + response);
                        response = response.replace("\\", "");
                        response = response.replace("\"[", "[");
                        response = response.replace("]\"", "]");
                        Log.d("getDataFromNet", " 更改过 === >" + response);
                        processDataSetUserInfo(response);
                    }
                });

    }

    private void processDataSetUserInfo(String json) {
        Log.d(TAG, "processData1: " + json);
        JSONObject jsonObject = JSON.parseObject(json);
        String code = jsonObject.get("Code").toString();
        if ("OK".equals(code)) {
            ToastUtil.toastCenter(mContext, "昵称修改成功...");
            finish();
        } else {
            ToastUtil.toastCenter(mContext, "昵称修改失败");
        }
    }
}
