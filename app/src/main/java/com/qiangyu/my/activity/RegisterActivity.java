package com.qiangyu.my.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hyphenate.EMError;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;
import com.qiangyu.R;
import com.qiangyu.utils.Constants;
import com.qiangyu.utils.CountDownButton;
import com.qiangyu.utils.Loading;
import com.qiangyu.utils.MD5Util;
import com.qiangyu.utils.SPUtils;
import com.qiangyu.utils.ToastUtil;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.apache.commons.lang3.StringUtils;

import java.util.SortedMap;
import java.util.TreeMap;

public class RegisterActivity extends Activity implements View.OnClickListener {

    private String TAG = "RegisterActivity";
    private EditText mEt_user;
    private EditText mEt_password;
    private Button mBtn_submit;
    private Context mContext;
    private EditText mEt_yanzhengma;
    private CountDownButton mCountDownButton;
    private String mUser;
    private String mPassword;
    private String mYanzhengma;
    private ImageView mIb_back;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mContext = this;
        initView();
    }

    private void initView() {
        setContentView(R.layout.activity_register);
        mIb_back = findViewById(R.id.ib_back);
        mEt_user = findViewById(R.id.et_user);
        mEt_password = findViewById(R.id.et_password);
        mEt_yanzhengma = findViewById(R.id.et_yanzhengma);
        mCountDownButton = findViewById(R.id.CountDownButton);
        mBtn_submit = findViewById(R.id.btn_submit);

        initListener();
    }

    private void initListener() {
        mIb_back.setOnClickListener(this);
        mBtn_submit.setOnClickListener(this);
        mCountDownButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view == mIb_back) {
            finish();
        }
        if (view == mBtn_submit) {
            mUser = mEt_user.getText().toString().trim();
            mPassword = mEt_password.getText().toString().trim();
            mYanzhengma = mEt_yanzhengma.getText().toString().trim();
            if (!StringUtils.isNotEmpty(mUser)) {
                ToastUtil.toastCenter(mContext, "账号不能为空...");
                return;
            }
            if (mUser.length() != 11) {
                ToastUtil.toastCenter(mContext, "请输入正确的手机号码...");
                return;
            }
            if (!StringUtils.isNotEmpty(mPassword)) {
                ToastUtil.toastCenter(mContext, "密码不能为空...");
                return;
            }
            if (!StringUtils.isNotEmpty(mYanzhengma)) {
                ToastUtil.toastCenter(mContext, "验证码不能为空...");
                return;
            }
            //注册提交
            getDataFromRegister(mUser, mYanzhengma, mPassword, "");
        }
        if (view == mCountDownButton) {
            //这里判断是否倒计时结束，避免在倒计时时多次点击导致重复请求接口
            mUser = mEt_user.getText().toString();
            Log.d(TAG, "onClick: " + mUser);
            if (!StringUtils.isNotEmpty(mUser)) {
                ToastUtil.toastCenter(mContext, "手机号不能为空...");
                return;
            }
            if (mUser.length() != 11) {
                ToastUtil.toastCenter(mContext, "请输入正确的手机号码...");
                return;
            }
            if (mCountDownButton.isFinish()) {
                //发送验证码请求成功后调用
                Log.d("mCountDownButton", "调用发送验证码");
                getDataFromGetSMSCode(mEt_user.getText().toString().trim(), "1");
                mCountDownButton.start();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!mCountDownButton.isFinish()) {
            mCountDownButton.cancel();
        }
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

    //=================================================用户注册=================================================\\
    /*
    /// <param name="mobile"></param>

    /// <param name="checkCode"></param>

    /// <param name="signInPwd"></param>

    /// <param name="recommender"></param>

    /// <param name="nonceStr"></param>

    /// <param name="timeStamp"></param>

    /// <param name="sign"></param>
    */
    void getDataFromRegister(final String mobile, String checkCode, String signInPwd, String recommender) {
        Loading.loading(mContext, "正在注册请稍后...");
        SortedMap<Object, Object> parameters = new TreeMap<>();
        String randomStr = MD5Util.getRandomStr();
        String timeStamp = MD5Util.getTimeStamp();
        String characterEncoding = "UTF-8";
        parameters.put("mobile", mobile);
        parameters.put("checkCode", checkCode);
        parameters.put("signInPwd", signInPwd);
        parameters.put("recommender", recommender);
        parameters.put("nonceStr", randomStr);
        parameters.put("timeStamp", timeStamp);
        String mySign = MD5Util.createSign(characterEncoding, parameters);

        OkHttpUtils
                .post()
                .url(Constants.QIANGYU_URL + "Register")
                .addParams("mobile", mobile)
                .addParams("checkCode", checkCode)
                .addParams("signInPwd", signInPwd)
                .addParams("recommender", recommender)
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
                        Loading.endLoad();
                        ToastUtil.toastCenter(mContext, "网络连接异常请稍后再试...");
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
                        Register(response, mobile);
                    }
                });

    }

    private void Register(String json, String username) {
        Loading.endLoad();
        Log.d("banner", "json === " + json);
        JSONObject jsonObject = JSON.parseObject(json);
        String code = jsonObject.get("Code").toString();
        if ("OK".equals(code)) {
            ToastUtil.toastCenter(mContext,"注册成功");
            finish();
            //signUp(username, username);
        } else {
            String Message = jsonObject.get("Message").toString();
            ToastUtil.toastCenter(mContext, Message);
        }
    }

    //=================================================环信IM注册=================================================\\
    private void signUp(final String username, final String password) {
        Loading.loading(mContext, "正在注册请稍后...");
        // 注册是耗时过程，所以要显示一个dialog来提示下用户
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    EMClient.getInstance().createAccount(username, password);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(mContext, "注册成功", Toast.LENGTH_LONG).show();
                            Loading.endLoad();
                            finish();
                        }
                    });
                } catch (final HyphenateException e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (!RegisterActivity.this.isFinishing()) {
                            }
                            Loading.endLoad();
                            /**
                             * 关于错误码可以参考官方api详细说明
                             * http://www.easemob.com/apidoc/android/chat3.0/classcom_1_1hyphenate_1_1_e_m_error.html
                             */
                            int errorCode = e.getErrorCode();
                            String message = e.getMessage();
                            Log.d("lzan13",
                                    String.format("sign up - errorCode:%d, errorMsg:%s", errorCode,
                                            e.getMessage()));
                            switch (errorCode) {
                                // 网络错误
                                case EMError.NETWORK_ERROR:
                                    Loading.endLoad();
                                    Toast.makeText(mContext, "网络错误 code: " + errorCode + ", message:" + message, Toast.LENGTH_LONG).show();
                                    break;
                                // 用户已存在
                                case EMError.USER_ALREADY_EXIST:
                                    Loading.endLoad();
                                    //Toast.makeText(mContext, "用户已存在 code: " + errorCode + ", message:" + message, Toast.LENGTH_LONG).show();
                                    break;
                                // 参数不合法，一般情况是username 使用了uuid导致，不能使用uuid注册
                                case EMError.USER_ILLEGAL_ARGUMENT:
                                    Loading.endLoad();
                                    Toast.makeText(mContext, "参数不合法，一般情况是username 使用了uuid导致，不能使用uuid注册 code: " + errorCode + ", message:" + message, Toast.LENGTH_LONG).show();
                                    break;
                                // 服务器未知错误
                                case EMError.SERVER_UNKNOWN_ERROR:
                                    Loading.endLoad();
                                    Toast.makeText(mContext, "服务器未知错误 code: " + errorCode + ", message:" + message, Toast.LENGTH_LONG).show();
                                    break;
                                case EMError.USER_REG_FAILED:
                                    Loading.endLoad();
                                    Toast.makeText(mContext, "账户注册失败 code: " + errorCode + ", message:" + message, Toast.LENGTH_LONG).show();
                                    break;
                                default:
                                    Loading.endLoad();
                                    Toast.makeText(mContext, "ml_sign_up_failed code: " + errorCode + ", message:" + message, Toast.LENGTH_LONG).show();
                                    break;
                            }
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
