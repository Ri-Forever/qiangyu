package com.qiangyu.customer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hyphenate.EMCallBack;
import com.hyphenate.EMError;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;
import com.qiangyu.R;
import com.qiangyu.im.ui.activity.ContactCustomerServiceActivity;
import com.qiangyu.my.bean.Designer;
import com.qiangyu.my.bean.JsonDesignerData;
import com.qiangyu.utils.Constants;
import com.qiangyu.utils.Loading;
import com.qiangyu.utils.MD5Util;
import com.qiangyu.utils.SPUtils;
import com.qiangyu.utils.ToastUtil;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import cn.jpush.android.api.JPushInterface;
import okhttp3.Call;

public class CustomerLoginActivity extends Activity {

    private String TAG = "CustomerLoginActivity";
    private static Context mContext;
    private ImageButton ibLoginBack;
    private EditText etLoginPhone;
    private EditText etLoginPwd;
    private ImageButton ibLoginVisible;
    private Button btnLogin;
    private String mRegistrationID;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mContext = this;
        mRegistrationID = JPushInterface.getRegistrationID(mContext);
        Log.d(TAG, "onCreate: " + mRegistrationID);
        setContentView(R.layout.activity_customer_login);
        initView();
    }

    private void initView() {
        ibLoginBack = findViewById(R.id.ib_login_back);
        etLoginPhone = findViewById(R.id.et_login_phone);
        etLoginPwd = findViewById(R.id.et_login_pwd);
        ibLoginVisible = findViewById(R.id.ib_login_visible);
        btnLogin = findViewById(R.id.btn_login);

        ibLoginBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //返回
                finish();
            }
        });
        ibLoginVisible.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //显示隐藏密码
                if (etLoginPwd.getInputType() == (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD)) {
                    etLoginPwd.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    Toast.makeText(mContext, "隐藏密码...", Toast.LENGTH_SHORT).show();
                } else {
                    etLoginPwd.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    Toast.makeText(mContext, "显示密码...", Toast.LENGTH_SHORT).show();
                }
            }
        });
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //登录
                String zhanghao = etLoginPhone.getText().toString().trim();
                String mima = etLoginPwd.getText().toString().trim();
                if (!StringUtils.isNotEmpty(zhanghao)) {
                    ToastUtil.toastCenter(mContext, "账号不能为空...");
                    return;
                }
                if (!StringUtils.isNotEmpty(mima)) {
                    ToastUtil.toastCenter(mContext, "密码不能为空...");
                    return;
                }
                String registrationID = JPushInterface.getRegistrationID(mContext);
                getDataFromKFSignIn(zhanghao, mima, registrationID);
            }
        });
    }
    /*
    /// <summary>
    /// 设计师登录
    /// </summary>
    /// <param name="name">设计师用户名（不是手机号）</param>
    /// <param name="password">登录密码</param>
    /// <param name="appToken">极光推送RegId</param>
    /// <param name="nonceStr">随机字符串</param>
    /// <param name="timeStamp">时间戳</param>
    /// <param name="sign">签名</param>
    /// <returns></returns>
    [HttpPost]
    public JsonResult KFSignIn(string name, string password, string appToken, string nonceStr, string timeStamp, string sign)
    */

    //=================================================客服登录=================================================\\
    void getDataFromKFSignIn(final String name, String password, String appToken) {
        Loading.loading(mContext, "正在登录，请稍后...");
        SortedMap<Object, Object> parameters = new TreeMap<>();
        String randomStr = MD5Util.getRandomStr();
        String timeStamp = MD5Util.getTimeStamp();
        String characterEncoding = "UTF-8";
        parameters.put("name", name);
        parameters.put("password", password);
        parameters.put("appToken", appToken);
        parameters.put("nonceStr", randomStr);
        parameters.put("timeStamp", timeStamp);
        String mySign = MD5Util.createSign(characterEncoding, parameters);

        OkHttpUtils
                .post()
                .url(Constants.QIANGYU_URL + "KFSignIn")
                .addParams("name", name)
                .addParams("password", password)
                .addParams("appToken", appToken)
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
                        ToastUtil.toastCenter(mContext, "网络连接错误请稍后再试...");
                        Loading.endLoad();
                        Log.d("getDataFromNet", "首页轮播请求失败 === >" + e.getMessage());
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
                        KFSignIn(response, name);
                    }
                });
    }

    private void KFSignIn(String json, String username) {
        Log.d(TAG, "KFSignIn: " + json);
        JSONObject jsonObject = JSON.parseObject(json);
        String code = jsonObject.get("Code").toString().trim();
        if ("OK".equals(code)) {
            //记录登录账户
            SPUtils.getInstance().put("CustomerService", username);
            //注册环信账号
            signUp(username, username);
        } else {
            Loading.endLoad();
            String Message = jsonObject.get("Message").toString().trim();
            ToastUtil.toastCenter(mContext, Message);
        }
    }

    /**
     * 环信登录
     */
    private void signIn(final String username, final String password) {
        Loading.loading(mContext, "正在登录，请稍后...");
        Log.d("signIn", "username: " + username + " --- password:" + password);
        EMClient.getInstance().login(username, password, new EMCallBack() {
            /**
             * 登陆成功的回调
             */
            @Override
            public void onSuccess() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Loading.endLoad();
                        getDataFromGetHeaderPic(username, "1");
                        JPushInterface.setAlias(getApplicationContext(), 1, username);
                        // 加载所有会话到内存
                        EMClient.getInstance().groupManager().loadAllGroups();
                        EMClient.getInstance().chatManager().loadAllConversations();

                        SPUtils.getInstance().put("Customer", "Customer");
                        Intent intent = new Intent(mContext, ContactCustomerServiceActivity.class);
                        intent.putExtra("Customer", "Customer");
                        startActivity(intent);
                        finish();
                    }
                });
            }

            /**
             * 登陆错误的回调
             * @param i
             * @param s
             */
            @Override
            public void onError(final int i, final String s) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Loading.endLoad();
                        Log.d("lzan13", "登录失败 Error code:" + i + ", message:" + s);
                        /**
                         * 关于错误码可以参考官方api详细说明
                         * http://www.easemob.com/apidoc/android/chat3.0/classcom_1_1hyphenate_1_1_e_m_error.html
                         */
                        switch (i) {
                            // 用户不存在 204
                            case EMError.USER_NOT_FOUND:
                                //Toast.makeText(UserLoginActivity.this, "用户不存在 code: " + i + ", message:" + s, Toast.LENGTH_LONG).show();
                                signUp(username, password);
                                break;
                            default:
                                Toast.makeText(mContext, "ml_sign_in_failed code: " + i + ", message:" + s, Toast.LENGTH_LONG).show();
                                break;
                        }
                    }
                });
            }

            @Override
            public void onProgress(int i, String s) {

            }
        });
    }

    //环信IM注册
    private void signUp(final String username, final String password) {
        Loading.loading(mContext, "正在登录，请稍后...");
        Log.d("signUp", "username: " + username + " --- password:" + password);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    EMClient.getInstance().createAccount(username, password);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Loading.endLoad();
                            //Toast.makeText(mContext, "注册成功", Toast.LENGTH_LONG).show();
                            signIn(username, password);
                        }
                    });
                } catch (final HyphenateException e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
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
                                // 用户已存在
                                case EMError.USER_ALREADY_EXIST:
                                    signIn(username, password);
                                    break;
                                default:
                                    Loading.endLoad();
                                    signIn(username, password);
                                    //Toast.makeText(mContext, "ml_sign_up_failed code: " + errorCode + ", message:" + message, Toast.LENGTH_LONG).show();
                                    //Log.d(TAG, "ml_sign_up_failed code: " + errorCode + ", message:" + message);
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

    //=================================================获取客服/用户头像=================================================\\
    //(string name, string nonceStr, string timeStamp, string sign)
    /// <param name="type">类型 0会员（会员、施工队） 1用户（设计师）</param>
    void getDataFromGetHeaderPic(final String name, String type) {
        SortedMap<Object, Object> parameters = new TreeMap<>();
        String randomStr = MD5Util.getRandomStr();
        String timeStamp = MD5Util.getTimeStamp();
        String characterEncoding = "UTF-8";
        parameters.put("name", name);
        parameters.put("type", type);
        parameters.put("nonceStr", randomStr);
        parameters.put("timeStamp", timeStamp);
        String mySign = MD5Util.createSign(characterEncoding, parameters);

        OkHttpUtils
                .post()
                .url(com.qiangyu.utils.Constants.QIANGYU_URL + "GetHeaderPic")
                .addParams("name", name)
                .addParams("type", type)
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
                        ToastUtil.toastCenter(mContext, "网络连接失败,请稍后再试...");
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
                        Log.d("getDataFromNet", " 消息列表获取客服 === >" + response);
                        GetHeaderPic(response);
                    }
                });

    }

    private void GetHeaderPic(String json) {
        Log.d("GetHeaderPic", "GetHeaderPic: " + json);
        String code = JSON.parseObject(json).get("Code").toString().trim();
        if ("OK".equals(code)) {
            JsonDesignerData jsonDesignerData = JSON.parseObject(json, JsonDesignerData.class);
            List<Designer> designerList = jsonDesignerData.getResult().getDesigner();
            String headPic = designerList.get(0).getHeadPic();
            SPUtils.getInstance().put("userHeadPic", headPic);
        }
    }
}
