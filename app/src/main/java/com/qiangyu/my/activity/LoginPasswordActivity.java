package com.qiangyu.my.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.qiangyu.R;
import com.qiangyu.my.bean.HuanXinTokenBean;
import com.qiangyu.utils.Constants;
import com.qiangyu.utils.MD5Util;
import com.qiangyu.utils.SPUtils;
import com.qiangyu.utils.ToastUtil;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.SortedMap;
import java.util.TreeMap;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginPasswordActivity extends Activity implements View.OnClickListener {

    private String TAG = "LoginPasswordActivity";
    private Context mContext;
    private ImageButton ibBack;
    private EditText etOldPassword;
    private EditText etNewPassword;
    private EditText etConfirmPassword;
    private TextView tvSubmit;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mContext = this;
        initView();
    }

    private void initView() {
        setContentView(R.layout.activity_change_password);

        ibBack = findViewById(R.id.ib_back);
        etOldPassword = findViewById(R.id.et_old_password);
        etNewPassword = findViewById(R.id.et_new_password);
        etConfirmPassword = findViewById(R.id.et_confirm_password);
        tvSubmit = findViewById(R.id.tv_submit);

        ibBack.setOnClickListener(this);
        tvSubmit.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view == ibBack) {
            Toast.makeText(this, "取消修改登录密码!", Toast.LENGTH_SHORT).show();
            finish();
        }
        if (view == tvSubmit) {
            String oldPassword = etOldPassword.getText().toString().trim();
            String newPassword = etNewPassword.getText().toString().trim();
            String confirmPassword = etConfirmPassword.getText().toString().trim();

            if (StringUtils.isNotEmpty(oldPassword) && StringUtils.isNotEmpty(newPassword) && StringUtils.isNotEmpty(confirmPassword)) {
                if (confirmPassword.equals(newPassword)) {
                    if (confirmPassword.length() >= 6 && confirmPassword.length() <= 32) {
                        int mid = SPUtils.getInstance().getInt("mid");
                        String signInPwd = SPUtils.getInstance().getString("signInPwd");
                        getDataFromSetPwd(String.valueOf(mid), "1", oldPassword, confirmPassword, signInPwd);
                    } else {
                        ToastUtil.toastCenter(mContext, "新密码不能低于6位或大于32位");
                    }

                } else {
                    ToastUtil.toastCenter(mContext, "第二次密码输入与第一次不一致,请重新检查");
                }
            } else {
                ToastUtil.toastCenter(mContext, "密码不能为空");
            }
        }
    }

    //=================================================修改登录密码=================================================\\
    /*
    /// <param name="mid">会员标识</param>
    /// <param name="type">资料类型；  type=1：登录密码 ;2：支付密码 </param>
    /// <param name="oldvalue">旧值：若无原密码：传空字符串</param>
    /// <param name="newvalue">新值</param>

    int mid = SPUtils.getInstance().getInt("mid");
    String signInPwd = SPUtils.getInstance().getString("signInPwd");
     */
    void getDataFromSetPwd(String mid, String type, String oldvalue, String newvalue, String signInPwd) {
        SortedMap<Object, Object> parameters = new TreeMap<>();
        String randomStr = MD5Util.getRandomStr();
        String timeStamp = MD5Util.getTimeStamp();
        String characterEncoding = "UTF-8";
        parameters.put("mid", mid);
        parameters.put("type", type);
        parameters.put("oldvalue", oldvalue);
        parameters.put("newvalue", newvalue);
        parameters.put("nonceStr", randomStr);
        parameters.put("timeStamp", timeStamp);
        String mySign = MD5Util.createSign(characterEncoding, parameters, signInPwd);

        OkHttpUtils
                .post()
                .url(Constants.QIANGYU_URL + "SetPwd")
                .addParams("mid", mid)
                .addParams("type", type)
                .addParams("oldvalue", oldvalue)
                .addParams("newvalue", newvalue)
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
                        processDataSetPwd(response);
                    }
                });

    }

    private void processDataSetPwd(String json) {
        Log.d(TAG, "processData1: " + json);
        JSONObject jsonObject = JSON.parseObject(json);
        String code = jsonObject.get("Code").toString();
        if ("OK".equals(code)) {
            ToastUtil.toastCenter(mContext, "登录密码修改成功...");
            finish();
        } else {
            String errmsg = jsonObject.get("errmsg").toString();
            ToastUtil.toastCenter(mContext, "登录密码修改失败,失败原因: " + errmsg);
        }
    }

    private HuanXinTokenBean tokenBean;
    private Gson gson;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (null != msg) {
                String result = msg.obj.toString();
                Log.e("xxx", result);
                tokenBean = gson.fromJson(result, HuanXinTokenBean.class);
            }
        }
    };

    private void getToken() {
        OkHttpClient client = new OkHttpClient();
        String gson = "{\"grant_type\":\"client_credentials\",\"client_id\":\"YourClientKey\",\"client_secret\":\"YourClientSecret\"}";
        String baseUrl = "https://a1.easemob.com/YourOrgName/YourAppName/token";
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), gson);
        Request request = new Request.Builder()
                .header("content-type", "application/json")
                .post(requestBody)
                .url(baseUrl)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {

            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                Message message = mHandler.obtainMessage();
                message.obj = response.body().string();
                message.sendToTarget();
            }
        });

    }

    public void updatePass(String user) {
        OkHttpClient client = new OkHttpClient();
        String baseUrl = "https://a1.easemob.com/YourOrgName/YourAppName/users/" +
                user + "/password";
        String gson = "{\"newpassword\":\"123456\"}";
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), gson);
        String access_token = tokenBean.getAccess_token();
        Log.e("xxx", access_token);
        Request request = new Request.Builder()
                .addHeader("Authorization", "Bearer " + access_token)
                .url(baseUrl)
                .put(requestBody)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e("xxx", "fail");
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                Log.e("xxx", response.body().string());
            }
        });
    }
}
