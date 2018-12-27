package com.qiangyu.my.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.qiangyu.R;
import com.qiangyu.utils.Constants;
import com.qiangyu.utils.Loading;
import com.qiangyu.utils.MD5Util;
import com.qiangyu.utils.ToastUtil;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.apache.commons.lang3.StringUtils;

import java.util.SortedMap;
import java.util.TreeMap;

public class RetrievePasswordActivity extends Activity {

    private Context mContext;
    private ImageButton ibBack;
    private EditText etUsername;
    private EditText etNewPassword;
    private EditText etConfirmPassword;
    private EditText etYanzhengma;
    private com.qiangyu.utils.CountDownButton mCountDownButton;
    private TextView tvSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.mContext = this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retrieve_password);
        initView();
    }

    private void initView() {
        ibBack = findViewById(R.id.ib_back);
        etUsername = findViewById(R.id.et_username);
        etNewPassword = findViewById(R.id.et_new_password);
        etConfirmPassword = findViewById(R.id.et_confirm_password);
        etYanzhengma = findViewById(R.id.et_yanzhengma);
        mCountDownButton = findViewById(R.id.CountDownButton);
        tvSubmit = findViewById(R.id.tv_submit);

        ibBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mCountDownButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //获取验证码
                //这里判断是否倒计时结束，避免在倒计时时多次点击导致重复请求接口
                String username = etUsername.getText().toString();
                if (!StringUtils.isNotEmpty(username)) {
                    ToastUtil.toastCenter(mContext, "手机号不能为空...");
                    return;
                }
                if (username.length() != 11) {
                    ToastUtil.toastCenter(mContext, "请输入正确的手机号码...");
                    return;
                }
                if (mCountDownButton.isFinish()) {
                    //发送验证码请求成功后调用
                    Log.d("mCountDownButton", "调用发送验证码");
                    getDataFromGetSMSCode(username, "2");
                    mCountDownButton.start();
                }
            }
        });
        tvSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //提交找回密码
                String username = etUsername.getText().toString();
                String newPassword = etNewPassword.getText().toString();
                String confirmPassword = etConfirmPassword.getText().toString();
                String yanzhengma = etYanzhengma.getText().toString();
                if (!StringUtils.isNotEmpty(username)) {
                    ToastUtil.toastCenter(mContext, "账号尚未填写,请填写后再次提交!");
                    return;
                }
                if (username.length() != 11) {
                    ToastUtil.toastCenter(mContext, "手机号码不正确,请重新填写后再次提交!");
                    return;
                }
                if (!StringUtils.isNotEmpty(newPassword)) {
                    ToastUtil.toastCenter(mContext, "密码尚未填写,请填写后再次提交!");
                    return;
                }
                if (newPassword.length() < 6 | newPassword.length() > 32) {
                    ToastUtil.toastCenter(mContext, "密码格式不正确,请填写6-32个字符之间!");
                    return;
                }
                if (!StringUtils.isNotEmpty(confirmPassword)) {
                    ToastUtil.toastCenter(mContext, "确认密码尚未填写,请填写后再次提交!");
                    return;
                }
                if (!newPassword.equals(confirmPassword)) {
                    ToastUtil.toastCenter(mContext, "确认密码与密码不一致,请确认后再次提交!");
                    return;
                }
                if (!StringUtils.isNotEmpty(yanzhengma)) {
                    ToastUtil.toastCenter(mContext, "验证码尚未填写,请填写后再次提交!");
                    return;
                }
                //找回密码
                getDataFromFindpassword(username, confirmPassword, yanzhengma);
            }
        });
    }

    //=================================================获取验证码=================================================\\
    void getDataFromGetSMSCode(String mobile, String sendType) {
        Loading.loading(mContext, "正在获取验证码请稍后...");
        SortedMap<Object, Object> parameters = new TreeMap<>();
        String randomStr = MD5Util.getRandomStr();
        String timeStamp = MD5Util.getTimeStamp();
        String characterEncoding = "UTF-8";
        parameters.put("mobile", mobile);
        parameters.put("sendType", sendType);
        parameters.put("nonceStr", randomStr);
        parameters.put("timeStamp", timeStamp);
        String mySign = MD5Util.createSign(characterEncoding, parameters);

        OkHttpUtils
                .post()
                .url(Constants.QIANGYU_URL + "GetSMSCode")
                .addParams("mobile", mobile)
                .addParams("sendType", sendType)
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
                        ToastUtil.toastCenter(mContext, "网络连接失败请稍后再试...");
                    }

                    /**
                     * 当联网成功时回调
                     * @param response
                     * @param id
                     */
                    @Override
                    public void onResponse(String response, int id) {
                        Log.d("getDataFromNet", " 未更改 === >" + response);
                        //Log.d("getDataFromNet", " --- >" + response);
                        //解析数据
                        //response = response.replace("", "");
                        response = response.replace("\\", "");
                        response = response.replace("\"[", "[");
                        response = response.replace("]\"", "]");
                        Log.d("getDataFromNet", " 更改过 === >" + response);
                        GetSMSCode(response);
                    }
                });

    }

    private void GetSMSCode(String json) {
        Loading.endLoad();
        Log.d("banner", "json === " + json);
        JSONObject jsonObject = JSON.parseObject(json);
        String code = jsonObject.get("Code").toString();
        if (!"OK".equals(code)) {
            String errmsg = jsonObject.get("errmsg").toString();
            ToastUtil.toastCenter(mContext, errmsg);
        } else {
            ToastUtil.toastCenter(mContext, "请填写收到的验证码");
        }
    }

    //=================================================找回密码=================================================\\
    /*
    /// <summary>
    /// 找回会员密码。
    /// </summary>
    /// <param name="mobile">会员手机号。</param>
    /// <param name="checkcode">短信验证码。</param>
    /// <param name="newpwd">新密码。</param>
     */
    void getDataFromFindpassword(String mobile, String newpwd, String checkcode) {
        Loading.loading(mContext, "正在获取验证码请稍后...");
        SortedMap<Object, Object> parameters = new TreeMap<>();
        String randomStr = MD5Util.getRandomStr();
        String timeStamp = MD5Util.getTimeStamp();
        String characterEncoding = "UTF-8";
        parameters.put("mobile", mobile);
        parameters.put("newpwd", newpwd);
        parameters.put("checkcode", checkcode);
        parameters.put("nonceStr", randomStr);
        parameters.put("timeStamp", timeStamp);
        String mySign = MD5Util.createSign(characterEncoding, parameters);

        OkHttpUtils
                .post()
                .url(Constants.QIANGYU_URL + "Findpassword")
                .addParams("mobile", mobile)
                .addParams("newpwd", newpwd)
                .addParams("checkcode", checkcode)
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
                        ToastUtil.toastCenter(mContext, "网络连接失败请稍后再试...");
                    }

                    /**
                     * 当联网成功时回调
                     * @param response
                     * @param id
                     */
                    @Override
                    public void onResponse(String response, int id) {
                        Log.d("getDataFromNet", " 未更改 === >" + response);
                        //Log.d("getDataFromNet", " --- >" + response);
                        //解析数据
                        //response = response.replace("", "");
                        response = response.replace("\\", "");
                        response = response.replace("\"[", "[");
                        response = response.replace("]\"", "]");
                        Log.d("getDataFromNet", " 更改过 === >" + response);
                        Findpassword(response);
                    }
                });

    }

    private void Findpassword(String json) {
        Loading.endLoad();
        Log.d("banner", "json === " + json);
        JSONObject jsonObject = JSON.parseObject(json);
        String code = jsonObject.get("Code").toString();
        if (!"OK".equals(code)) {
            String errmsg = jsonObject.get("Message").toString();
            ToastUtil.toastCenter(mContext, "密码重置失败,原因: " + errmsg);
        } else {
            ToastUtil.toastCenter(mContext, "密码重置成功,可以用新密码直接登录了!");
            finish();
        }
    }

}
