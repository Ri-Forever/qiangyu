package com.qiangyu.my.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hyphenate.EMCallBack;
import com.hyphenate.EMError;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;
import com.qiangyu.R;
import com.qiangyu.app.MainActivity;
import com.qiangyu.home.bean.JsonShopListData;
import com.qiangyu.home.bean.ShopList;
import com.qiangyu.my.bean.JsonMemberInfoData;
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

public class UserLoginActivity extends Activity implements View.OnClickListener {

    private String TAG = "UserLoginActivity";
    private ImageButton ibLoginBack;
    private EditText etLoginPhone;
    private EditText etLoginPwd;
    private ImageButton ibLoginVisible;
    private Button btnLogin;
    private TextView tvLoginRegister;
    private TextView tvLoginForgetPwd;
    private String mUsername;
    private String mPassword;
    //弹出框
    private AlertDialog alertDialog;
    private ProgressDialog mDialog;
    private Context mContext;
    private String mCId;
    private String mCity;
    private String mRegistrationID;
    private JsonMemberInfoData.Result mResult;

    //其他登录方式,暂时用不到
    //private ImageButton ibWeibo;
    //private ImageButton ibQq;
    //private ImageButton ibWechat;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRegistrationID = SPUtils.getInstance().getString("registrationID");
        setContentView(R.layout.activity_login);
        this.mContext = this;
        findViews();
    }

    private void findViews() {
        ibLoginBack = findViewById(R.id.ib_login_back);
        etLoginPhone = findViewById(R.id.et_login_phone);
        etLoginPwd = findViewById(R.id.et_login_pwd);
        ibLoginVisible = findViewById(R.id.ib_login_visible);
        btnLogin = findViewById(R.id.btn_login);
        tvLoginRegister = findViewById(R.id.tv_login_register);
        tvLoginForgetPwd = findViewById(R.id.tv_login_forget_pwd);
        //ibWeibo = findViewById(R.id.ib_weibo);
        //ibQq = findViewById(R.id.ib_qq);
        //ibWechat = findViewById(R.id.ib_wechat);

        //ibWeibo.setOnClickListener(this);
        //ibQq.setOnClickListener(this);
        //ibWechat.setOnClickListener(this);
        ibLoginBack.setOnClickListener(this);
        ibLoginVisible.setOnClickListener(this);
        btnLogin.setOnClickListener(this);
        tvLoginRegister.setOnClickListener(this);
        tvLoginForgetPwd.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == ibLoginBack) {
            finish();
        } else if (v == ibLoginVisible) {
            //隐藏显示密码
            if (etLoginPwd.getInputType() == (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD)) {
                etLoginPwd.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                Toast.makeText(mContext, "隐藏密码", Toast.LENGTH_SHORT).show();
            } else {
                etLoginPwd.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                Toast.makeText(mContext, "显示密码", Toast.LENGTH_SHORT).show();
            }

        } else if (v == btnLogin) {
            // Handle clicks for btnLogin 登录
            mUsername = etLoginPhone.getText().toString().trim();
            mPassword = etLoginPwd.getText().toString().trim();
            //商城用户登陆
            if (!StringUtils.isNotEmpty(mUsername)) {
                ToastUtil.toastCenter(mContext, "账号不能为空...");
                return;
            }
            if (mUsername.length() != 11) {
                ToastUtil.toastCenter(mContext, "请输入正确的账号...");
                return;
            }
            if (!StringUtils.isNotEmpty(mPassword)) {
                ToastUtil.toastCenter(mContext, "密码不能为空...");
                return;
            }
            String registrationID = JPushInterface.getRegistrationID(mContext);
            Log.d(TAG, "registrationID: " + registrationID);
            getDataFromNet(mUsername, mPassword, registrationID);
        } else if (v == tvLoginRegister) {
            //注册账号
            Intent intent = new Intent(this, RegisterActivity.class);
            startActivity(intent);
        } else if (v == tvLoginForgetPwd) {
            Intent intent = new Intent(mContext,RetrievePasswordActivity.class);
            startActivity(intent);
        }
        /*else if (v == ibWeibo) {
            // Handle clicks for ibWeibo
        } else if (v == ibQq) {
            // Handle clicks for ibQq
        } else if (v == ibWechat) {
            // Handle clicks for ibWechat
        }*/
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    //=================================================用户登录=================================================\\
    void getDataFromNet(final String username, final String password, String appToken) {
        Loading.loading(mContext, "正在登陆请稍后...");
        SortedMap<Object, Object> parameters = new TreeMap<>();
        String randomStr = MD5Util.getRandomStr();
        String timeStamp = MD5Util.getTimeStamp();
        String characterEncoding = "UTF-8";
        parameters.put("name", username);
        parameters.put("password", password);
        parameters.put("appToken", appToken);
        parameters.put("nonceStr", randomStr);
        parameters.put("timeStamp", timeStamp);
        String mySign = MD5Util.createSign(characterEncoding, parameters);

        OkHttpUtils
                .post()
                .url(Constants.QIANGYU_URL + "SignIn")
                .addParams("name", username)
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
                    public void onError(okhttp3.Call call, Exception e, int id) {
                        Loading.endLoad();
                        ToastUtil.toastCenter(mContext, "网络异常请稍后再试...");
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
                        Log.d("getDataFromNet", " 更改过 === >" + response);
                        processData(response, username);
                    }
                });

    }

    private void processData(String json, String username) {
        Loading.endLoad();
        JSONObject jsonObject = JSON.parseObject(json);
        String code = jsonObject.get("Code").toString();
        String message = jsonObject.get("Message").toString();
        if (code.equals("OK")) {
            SPUtils.getInstance().put("username", mUsername);
            //环信IM注册
            signUp(username, username);
            //Toast.makeText(mContext, message, Toast.LENGTH_LONG).show();
        } else {
            Loading.endLoad();
            Toast.makeText(mContext, message, Toast.LENGTH_LONG).show();
        }
    }

    /**
     * 环信登录
     */
    private void signIn(final String username, final String password) {

        Loading.loading(mContext, "正在登陆请稍后...");
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
                        JPushInterface.setAlias(getApplicationContext(), 1, username);
                        // 加载所有会话到内存
                        EMClient.getInstance().groupManager().loadAllGroups();
                        EMClient.getInstance().chatManager().loadAllConversations();

                        getDataFromGetUserInfoByMobile(username);
                        startActivity(new Intent(mContext, MainActivity.class));
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
                                Loading.endLoad();
                                signUp(username, password);
                                break;
                            default:
                                Toast.makeText(UserLoginActivity.this,
                                        "ml_sign_in_failed code: " + i + ", message:" + s,
                                        Toast.LENGTH_LONG).show();
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

        Loading.loading(mContext, "正在登陆请稍后...");
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
                            if (!UserLoginActivity.this.isFinishing()) {
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
                                // 用户已存在
                                case EMError.USER_ALREADY_EXIST:
                                    Loading.endLoad();
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

    //=================================================获取用户信息=================================================\\
    void getDataFromGetUserInfoByMobile(final String username) {
        SortedMap<Object, Object> parameters = new TreeMap<>();
        String randomStr = MD5Util.getRandomStr();
        String timeStamp = MD5Util.getTimeStamp();
        String characterEncoding = "UTF-8";
        parameters.put("mobile", username);
        parameters.put("nonceStr", randomStr);
        parameters.put("timeStamp", timeStamp);
        String mySign = MD5Util.createSign(characterEncoding, parameters);

        OkHttpUtils
                .post()
                .url(Constants.QIANGYU_URL + "GetUserInfoByMobile")
                .addParams("mobile", username)
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
                        Log.d("getDataFromNet", " 更改过 === >" + response);
                        GetUserInfoByMobile(response);
                    }
                });

    }

    private void GetUserInfoByMobile(String json) {
        JsonMemberInfoData jsonMemberInfoData = JSON.parseObject(json, JsonMemberInfoData.class);
        mResult = jsonMemberInfoData.getResult();
        //获取会员类型
        int type = mResult.getMemberInfo().get(0).getType();//0:施工队,1:普通会员
        SPUtils.getInstance().put("type", String.valueOf(type));
        //获取头像
        String headPic = mResult.getMemberInfo().get(0).getHeadPic();
        SPUtils.getInstance().put("userHeadPic", headPic);
        //获取名字
        String name = mResult.getMemberInfo().get(0).getName();
        //获取会员id
        int mid = mResult.getMemberInfo().get(0).getId();
        SPUtils.getInstance().put("mid", mid);
        //获取用户加密后的密码
        String signInPwd = mResult.getMemberInfo().get(0).getSignInPwd();
        SPUtils.getInstance().put("signInPwd", signInPwd);
    }
}
