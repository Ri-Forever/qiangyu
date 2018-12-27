package com.qiangyu.app;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.RadioGroup;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hyphenate.EMConnectionListener;
import com.hyphenate.EMError;
import com.hyphenate.chat.EMClient;
import com.hyphenate.util.NetUtils;
import com.qiangyu.R;
import com.qiangyu.base.BaseFragment;
import com.qiangyu.home.fragment.HomeFragment;
import com.qiangyu.my.fragment.MyFragment;
import com.qiangyu.shopcart.fragment.ShopCartFragment;
import com.qiangyu.utils.Constants;
import com.qiangyu.utils.Loading;
import com.qiangyu.utils.MD5Util;
import com.qiangyu.utils.SPUtils;
import com.qiangyu.utils.ToastUtil;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.SortedMap;
import java.util.TreeMap;

import cn.jpush.android.api.JPushInterface;

import static android.app.Notification.VISIBILITY_SECRET;

public class MainActivity extends FragmentActivity {

    private static final String TAG = "IMMainActivity";
    private static Context mContext;
    private RadioGroup mRadioGroup;
    private int position;
    private ArrayList<BaseFragment> mFragments;
    private Fragment tempFragment;
    private FrameLayout mFrameLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        mContext = this;
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        //初始化fragment
        initFragment();
        //设置
        initListener();

        //注册一个监听连接状态的listener
        MyConnectionListener myConnectionListener = new MyConnectionListener();
        EMClient.getInstance().addConnectionListener(myConnectionListener);
    }

    private void initListener() {
        //设置点击事件判断选中的是哪项菜单
        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                //点击处理
                switch (i) {
                    case R.id.tab_home://主页
                        //Log.d(TAG, "主页");
                        position = 0;
                        break;
                    case R.id.tab_shop_cart://购物车
                        //Log.d(TAG, "购物车");
                        position = 1;
                        break;
                    case R.id.tab_my://我的
                        //Log.d(TAG, "我的");
                        position = 2;
                        break;
                    /*default:
                        position = 0;
                        break;*/
                }
                //根据位置不同取个子的fragment
                BaseFragment baseFragment = getFragment(position);
                //第一个参数是上次显示的,第二个是当前要显示的
                switchFragment(tempFragment, baseFragment);
            }
        });

        Intent intent = getIntent();
        String value = intent.getStringExtra("keyCart");
        if (!TextUtils.isEmpty(value) && "valueCart".equals(value)) {
            mRadioGroup.check(R.id.tab_shop_cart);
        } else {
            //进入默认选中首页菜单
            mRadioGroup.check(R.id.tab_home);
        }
    }

    private void initFragment() {
        mRadioGroup = findViewById(R.id.tab_group);
        mFrameLayout = findViewById(R.id.frameLayout);
        mFragments = new ArrayList<>();
        mFragments.add(new HomeFragment());
        mFragments.add(new ShopCartFragment());
        mFragments.add(new MyFragment());
    }

    @Nullable
    private BaseFragment getFragment(int position) {
        if (mFragments != null && mFragments.size() > 0) {
            BaseFragment baseFragment = mFragments.get(position);
            return baseFragment;
        }
        return null;
    }

    //切换Fragment
    private void switchFragment(Fragment fromFragment, BaseFragment nextFragment) {
        if (tempFragment != nextFragment) {
            tempFragment = nextFragment;
            if (nextFragment != null) {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                //判断nextFragment 是否添加
                if (!nextFragment.isAdded()) {
                    //隐藏当前Fragment
                    if (fromFragment != null) {
                        transaction.hide(fromFragment);
                    }
                    transaction.add(R.id.frameLayout, nextFragment).commit();
                } else {
                    //隐藏当前Fragment
                    if (fromFragment != null) {
                        transaction.hide(fromFragment);
                    }
                    transaction.show(nextFragment).commit();
                }
            }
        }
    }

    //实现ConnectionListener接口
    private class MyConnectionListener implements EMConnectionListener {

        @Override
        public void onConnected() {

        }

        @Override
        public void onDisconnected(final int errorCode) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (errorCode == EMError.USER_REMOVED) {
                        //账号已被移除
                    } else if (errorCode == EMError.USER_LOGIN_ANOTHER_DEVICE) {

                        //设置跳转的页面
                        PendingIntent intent = PendingIntent.getActivity(mContext, 100, new Intent(mContext, CustomerServiceUserActivity.class), PendingIntent.FLAG_CANCEL_CURRENT);

                        if (Build.VERSION.SDK_INT >= 26) {
                            NotificationManager manager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
                            NotificationChannel channel = new NotificationChannel("用户被踢", "用户被踢", NotificationManager.IMPORTANCE_HIGH);
                            Uri uri = Uri.parse("android.resource://" + mContext.getPackageName() + "/" + R.raw.shuishengchang);
                            AudioAttributes.Builder audioBuilder = new AudioAttributes.Builder();
                            audioBuilder.setUsage(AudioAttributes.USAGE_NOTIFICATION_COMMUNICATION_INSTANT);
                            AudioAttributes audioAttributes = audioBuilder.build();
                            channel.setSound(uri, audioAttributes);
                            channel.enableLights(true);//闪光灯
                            channel.setLockscreenVisibility(VISIBILITY_SECRET);//锁屏显示通知
                            channel.setLightColor(Color.RED);//闪关灯的灯光颜色
                            manager.createNotificationChannel(channel);

                            Notification.Builder builder = new Notification.Builder(mContext, "用户被踢");
                            builder.setSmallIcon(R.mipmap.ic_launcher)
                                    .setTicker("通知")
                                    .setContentTitle("账号在其他设备登录!")
                                    .setContentText("如果不是您本人登录的请立即登录账号更改密码!")
                                    .setWhen(System.currentTimeMillis())
                                    .setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.ic_launcher))
                                    .setContentIntent(intent)
                                    .setAutoCancel(true);
                            manager.notify(1, builder.build());
                        } else {
                            NotificationManager mNotificationManager = (NotificationManager) mContext.getSystemService(NOTIFICATION_SERVICE);
                            Notification notification = new NotificationCompat.Builder(mContext, "default")
                                    .setPriority(Notification.PRIORITY_DEFAULT)// 设置该通知优先级
                                    .setTicker("通知")
                                    .setContentTitle("账号在其他设备登录")
                                    .setContentText("如果不是您本人登录的请立即登录账号更改密码!")
                                    .setWhen(System.currentTimeMillis())
                                    .setContentIntent(intent)
                                    .setSmallIcon(R.mipmap.ic_launcher)
                                    .setSound(Uri.parse("android.resource://" + mContext.getPackageName() + "/" + R.raw.shuishengchang))
                                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                                    .setChannelId(mContext.getPackageName())
                                    .setAutoCancel(true)
                                    .build();
                            mNotificationManager.notify(1, notification);
                        }

                        String registrationID = JPushInterface.getRegistrationID(mContext);
                        getDataFromSignOut(registrationID);

                    } else {
                        if (NetUtils.hasNetwork(mContext)) {
                            //连接不到聊天服务器
                        } else {
                            //当前网络不可用,请检测网络设置
                        }
                    }
                }
            });

        }
    }

    //=================================================用户注销=================================================\\
    void getDataFromSignOut(final String appToken) {
        Loading.loading(mContext, "正在注销...");
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
        JSONObject jsonObject = JSON.parseObject(json);
        String code = jsonObject.get("Code").toString();
        if ("OK".equals(code)) {
            //环信IM退出
            EMClient.getInstance().logout(false);
            //清空数据库
            SPUtils.getInstance().clear();
            ToastUtil.toastCenter(mContext, "退出成功...");
            finish();

        } else {
            ToastUtil.toastCenter(mContext,"退出失败!");
        }
    }
}
