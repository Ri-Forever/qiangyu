package com.qiangyu.my.fragment;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.hyphenate.EMConnectionListener;
import com.hyphenate.EMError;
import com.hyphenate.chat.EMClient;
import com.hyphenate.util.NetUtils;
import com.qiangyu.R;
import com.qiangyu.app.CustomerServiceUserActivity;
import com.qiangyu.base.BaseFragment;
import com.qiangyu.my.activity.AllMyOrderActivity;
import com.qiangyu.my.activity.ConstructionSheetActivity;
import com.qiangyu.my.activity.RequirementActivity;
import com.qiangyu.my.activity.SetHeadImageActivity;
import com.qiangyu.my.activity.SettingActivity;
import com.qiangyu.my.activity.UserLoginActivity;
import com.qiangyu.my.activity.WalletActivity;
import com.qiangyu.my.bean.JsonMemberInfoData;
import com.qiangyu.update.CheckVersionInfoTask;
import com.qiangyu.utils.Constants;
import com.qiangyu.utils.Loading;
import com.qiangyu.utils.MD5Util;
import com.qiangyu.utils.SPUtils;
import com.qiangyu.utils.ToastUtil;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.apache.commons.lang3.StringUtils;

import java.util.SortedMap;
import java.util.TreeMap;

import cn.jpush.android.api.JPushInterface;

import static android.app.Notification.VISIBILITY_SECRET;
import static android.content.Context.NOTIFICATION_SERVICE;

public class MyFragment extends BaseFragment implements View.OnClickListener {

    private static final String TAG = "MyFragment";
    private ScrollView scrollview;
    private ImageButton ibLoginRegistration;
    private TextView tvLoginRegistration;
    private TextView tvUserPay;
    private TextView tvUserReceive;
    private TextView tvAllOrder;
    private TextView tvUserWallet;
    private TextView tvUserDemand;
    private TextView tvUserConstructionSheet;
    private TextView tvUserQrCode;
    private TextView tvUserSetting;
    private TextView tvUserCancellation;
    private ImageButton mIbUserHeadImage;
    private TextView mTvUsername;
    private JsonMemberInfoData.Result mResult;
    private TextView mTv_gengxin;
    private boolean whetherSuccess = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String username = SPUtils.getInstance().getString("username");
        getDataFromNet(username);
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onResume() {
        super.onResume();
        /*String username = SPUtils.getInstance().getString("username");
        if (StringUtils.isNotEmpty(username)) {
            String type = SPUtils.getInstance().getString("type");
            if (!StringUtils.isNotEmpty(type)) {
                getDataFromNet(username);
            }
        }*/
    }

    @Override
    protected View initView() {
        Log.d(TAG, "initView: ");
        View view = View.inflate(mContext, R.layout.fragment_user, null);
        scrollview = view.findViewById(R.id.scrollview);
        ibLoginRegistration = view.findViewById(R.id.ib_login_registration);
        tvLoginRegistration = view.findViewById(R.id.tv_login_registration);
        tvUserPay = view.findViewById(R.id.tv_user_pay);
        tvUserReceive = view.findViewById(R.id.tv_user_receive);
        tvAllOrder = view.findViewById(R.id.tv_all_order);
        tvUserWallet = view.findViewById(R.id.tv_user_wallet);
        mTv_gengxin = view.findViewById(R.id.tv_gengxin);
        tvUserDemand = view.findViewById(R.id.tv_user_demand);
        tvUserConstructionSheet = view.findViewById(R.id.tv_user_construction_sheet);
        tvUserQrCode = view.findViewById(R.id.tv_user_qr_code);
        tvUserSetting = view.findViewById(R.id.tv_user_setting);
        tvUserCancellation = view.findViewById(R.id.tv_user_cancellation);
        mIbUserHeadImage = view.findViewById(R.id.ib_user_head_image);
        mTvUsername = view.findViewById(R.id.tv_username);


        ibLoginRegistration.setOnClickListener(this);
        tvLoginRegistration.setOnClickListener(this);
        tvUserPay.setOnClickListener(this);
        tvUserReceive.setOnClickListener(this);
        tvAllOrder.setOnClickListener(this);
        tvUserWallet.setOnClickListener(this);
        tvUserDemand.setOnClickListener(this);
        tvUserConstructionSheet.setOnClickListener(this);
        tvUserQrCode.setOnClickListener(this);
        tvUserSetting.setOnClickListener(this);
        tvUserCancellation.setOnClickListener(this);
        mIbUserHeadImage.setOnClickListener(this);
        mTvUsername.setOnClickListener(this);
        mTv_gengxin.setOnClickListener(this);
        return view;
    }


    @Override
    public void onClick(View v) {
        if (v == ibLoginRegistration) {
            //登录注册
            Intent intent = new Intent(getContext(), UserLoginActivity.class);
            startActivity(intent);
        } else if (v == tvLoginRegistration) {
            //登录注册
            Intent intent = new Intent(getContext(), UserLoginActivity.class);
            startActivity(intent);
        } else if (v == mIbUserHeadImage) {
            //用户头像
            Intent intent = new Intent(mContext, SetHeadImageActivity.class);
            startActivity(intent);
        } else if (v == mTvUsername) {
            //用户名称
        } else if (v == tvUserPay) {
            String username = SPUtils.getInstance().getString("username");
            if (!StringUtils.isNotEmpty(username)) {
                Toast toast = Toast.makeText(mContext, "请先登录...", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                return;
            }
            Intent intent = new Intent(mContext, AllMyOrderActivity.class);
            intent.putExtra("panduan", "daifukuan");
            startActivity(intent);
        } else if (v == tvUserReceive) {
            String username = SPUtils.getInstance().getString("username");
            if (!StringUtils.isNotEmpty(username)) {
                Toast toast = Toast.makeText(mContext, "请先登录...", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                return;
            }

            Intent intent = new Intent(mContext, AllMyOrderActivity.class);
            intent.putExtra("panduan", "daishouhuo");
            startActivity(intent);
        } else if (v == tvAllOrder) {
            String username = SPUtils.getInstance().getString("username");
            if (!StringUtils.isNotEmpty(username)) {
                Toast toast = Toast.makeText(mContext, "请先登录...", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                return;
            }

            Intent intent = new Intent(mContext, AllMyOrderActivity.class);
            intent.putExtra("panduan", "quanbudingdan");
            startActivity(intent);
        } else if (v == tvUserWallet) {
            String username = SPUtils.getInstance().getString("username");
            if (!StringUtils.isNotEmpty(username)) {
                Toast toast = Toast.makeText(mContext, "请先登录...", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                return;
            }

            //钱包
            Intent intent = new Intent(mContext, WalletActivity.class);
            startActivity(intent);
        } else if (v == tvUserDemand) {
            String username = SPUtils.getInstance().getString("username");
            if (!StringUtils.isNotEmpty(username)) {
                Toast toast = Toast.makeText(mContext, "请先登录...", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                return;
            }

            //我发布的需求
            Intent intent = new Intent(mContext, RequirementActivity.class);
            startActivity(intent);
        } else if (v == tvUserConstructionSheet) {
            String username = SPUtils.getInstance().getString("username");
            if (!StringUtils.isNotEmpty(username)) {
                Toast toast = Toast.makeText(mContext, "请先登录...", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                return;
            }

            //我的施工单
            Intent intent = new Intent(mContext, ConstructionSheetActivity.class);
            startActivity(intent);
        } else if (v == tvUserQrCode) {
            String username = SPUtils.getInstance().getString("username");
            if (!StringUtils.isNotEmpty(username)) {
                Toast toast = Toast.makeText(mContext, "请先登录...", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                return;
            }

            Toast.makeText(mContext, "我的二维码", Toast.LENGTH_SHORT).show();
        } else if (v == tvUserSetting) {
            String username = SPUtils.getInstance().getString("username");
            if (!StringUtils.isNotEmpty(username)) {
                Toast toast = Toast.makeText(mContext, "请先登录...", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                return;
            }

            //设置
            Intent intent = new Intent(mContext, SettingActivity.class);
            startActivity(intent);
        } else if (v == tvUserCancellation) {
            String username = SPUtils.getInstance().getString("username");
            if (!StringUtils.isNotEmpty(username)) {
                Toast toast = Toast.makeText(mContext, "请登录后操作...", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                return;
            }

            String registrationID = SPUtils.getInstance().getString("registrationID");
            getDataFromSignOut(registrationID);

        } else if (v == mTv_gengxin) {
            //检测更新
            new CheckVersionInfoTask(mContext, true).execute();

        }
    }

    @Override
    public void initData() {
        super.initData();
        Log.d(TAG, "initData: --- initData");
    }

    //=================================================获取用户信息=================================================\\
    void getDataFromNet(final String username) {
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
                        processData1(response);
                    }
                });

    }

    private void processData1(String json) {
        JsonMemberInfoData jsonMemberInfoData = JSON.parseObject(json, JsonMemberInfoData.class);
        mResult = jsonMemberInfoData.getResult();
        ibLoginRegistration.setVisibility(View.GONE);
        tvLoginRegistration.setVisibility(View.GONE);
        mIbUserHeadImage.setVisibility(View.VISIBLE);
        mTvUsername.setVisibility(View.VISIBLE);
        //获取会员类型
        int type = mResult.getMemberInfo().get(0).getType();//0:施工队,1:普通会员
        SPUtils.getInstance().put("type", String.valueOf(type));
        if (type == 0) {
            tvUserConstructionSheet.setVisibility(View.VISIBLE);
        } else {
            tvUserConstructionSheet.setVisibility(View.GONE);
        }
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
        if (StringUtils.isNotEmpty(headPic)) {
            Glide.with(mContext).load(Constants.QIANGYU_PIC_URL + headPic).diskCacheStrategy(DiskCacheStrategy.ALL).centerCrop().into(mIbUserHeadImage);
        }
        mTvUsername.setText(name);
        Log.d(TAG, "processData1: " + headPic);
    }

    //=================================================用户注销=================================================\\
    void getDataFromSignOut(final String appToken) {
        Loading.loading(mContext, "正在注销,请稍后...");
        SortedMap<Object, Object> parameters = new TreeMap<>();
        String randomStr = MD5Util.getRandomStr();
        String timeStamp = MD5Util.getTimeStamp();
        String characterEncoding = "UTF-8";
        parameters.put("appToken", appToken);
        parameters.put("nonceStr", randomStr);
        parameters.put("timeStamp", timeStamp);
        String mySign = MD5Util.createSign(characterEncoding, parameters);

        OkHttpUtils
                .post()
                .url(Constants.QIANGYU_URL + "SignOut")
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
                        Log.d("getDataFromNet", "请求失败 === >" + e.getMessage());
                        Loading.endLoad();
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
                        SignOut(response);
                    }
                });

    }

    private void SignOut(String json) {
        Loading.endLoad();
        Log.d(TAG, "SignOut: " + json);
        JSONObject jsonObject = JSON.parseObject(json);
        String code = jsonObject.get("Code").toString();
        if ("OK".equals(code)) {
            //环信IM退出
            EMClient.getInstance().logout(false);
            //清空数据库
            SPUtils.getInstance().clear();
            tvUserConstructionSheet.setVisibility(View.GONE);
            mIbUserHeadImage.setVisibility(View.GONE);
            mTvUsername.setVisibility(View.GONE);
            ibLoginRegistration.setVisibility(View.VISIBLE);
            tvLoginRegistration.setVisibility(View.VISIBLE);
            Toast.makeText(mContext, "注销登录成功...", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(mContext, CustomerServiceUserActivity.class));
            getActivity().onBackPressed();
        } else {

        }
    }

}
