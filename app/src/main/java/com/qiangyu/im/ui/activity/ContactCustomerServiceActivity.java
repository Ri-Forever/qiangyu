package com.qiangyu.im.ui.activity;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.NotificationCompat;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.flyco.tablayout.SlidingTabLayout;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.hyphenate.EMConnectionListener;
import com.hyphenate.EMError;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.util.NetUtils;
import com.qiangyu.R;
import com.qiangyu.app.CustomerServiceUserActivity;
import com.qiangyu.im.ui.fragment.ContactCustomerServiceFragment;
import com.qiangyu.im.ui.fragment.MessageListFragment;
import com.qiangyu.im.util.ViewFindUtils;
import com.qiangyu.update.CheckVersionInfoTask;
import com.qiangyu.utils.Constants;
import com.qiangyu.utils.Loading;
import com.qiangyu.utils.MD5Util;
import com.qiangyu.utils.SPUtils;
import com.qiangyu.utils.ToastUtil;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import cn.jpush.android.api.JPushInterface;

import static android.app.Notification.VISIBILITY_SECRET;

public class ContactCustomerServiceActivity extends FragmentActivity implements View.OnClickListener {

    /**
     * 屏幕宽度
     */
    public static int screenWidth;
    /**
     * 屏幕高度
     */
    public static int screenHeight;
    /**
     * 屏幕密度
     */
    public static float screenDensity;
    //弹出框
    //private AlertDialog alertDialog;

    private static final String TAG = "ContactCustomerServiceActivity";
    private ImageButton mIb_more_selling_back;
    private TextView mTv_search_more_selling;
    private ArrayList<Fragment> mFragments = new ArrayList<>();
    private MyPagerAdapter mAdapter;
    private SlidingTabLayout mStl_selling_option;
    private ViewPager mVp_selling_content;
    private View mDecorView;
    private static Context mContext;
    private String[] mTitles = {"消息列表", "客服列表"};
    private String mCustomer;
    private TextView mTv_tuichu;
    private boolean whetherSuccess = false;
    private TextView mTv_gengxin;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.im_activity_more_selling);
        this.mContext = this;
        //获取是不是客服登录
        mCustomer = getIntent().getStringExtra("Customer");

        //初始化屏幕宽高
        initScreenSize();

        //初始化布局
        initView();

    }

    /**
     * 初始化当前设备屏幕宽高
     */
    private void initScreenSize() {
        DisplayMetrics curMetrics = getApplicationContext().getResources().getDisplayMetrics();
        screenWidth = curMetrics.widthPixels;
        screenHeight = curMetrics.heightPixels;
        screenDensity = curMetrics.density;
    }

    @Override
    protected void onResume() {
        mAdapter.notifyDataSetChanged();
        EMClient.getInstance().chatManager().addMessageListener(eMMessageListener);
        super.onResume();
    }

    @Override
    protected void onPause() {
        EMClient.getInstance().chatManager().removeMessageListener(eMMessageListener);
        super.onPause();
    }

    private void initView() {

        mIb_more_selling_back = findViewById(R.id.ib_more_selling_back);
        mTv_search_more_selling = findViewById(R.id.tv_search_more_selling);
        mTv_tuichu = findViewById(R.id.tv_tuichu);
        mTv_gengxin = findViewById(R.id.tv_gengxin);
        mDecorView = getWindow().getDecorView();
        mStl_selling_option = ViewFindUtils.find(mDecorView, R.id.stl_selling_option);
        mVp_selling_content = ViewFindUtils.find(mDecorView, R.id.vp_selling_content);

        mIb_more_selling_back.setOnClickListener(this);
        mTv_tuichu.setOnClickListener(this);
        mTv_gengxin.setOnClickListener(this);

        if (StringUtils.isNotEmpty(mCustomer)) {
            //注册一个监听连接状态的listener
            MyConnectionListener myConnectionListener = new MyConnectionListener();
            EMClient.getInstance().addConnectionListener(myConnectionListener);
            mTitles = new String[1];
            mTitles[0] = "消息列表";
            mIb_more_selling_back.setVisibility(View.GONE);
            mTv_tuichu.setVisibility(View.VISIBLE);
            mTv_gengxin.setVisibility(View.VISIBLE);
            mTv_search_more_selling.setText("客服中心");
        }

        Log.d("initView", "initView: " + mTitles.length);
        for (int i = 0; i < mTitles.length; i++) {
            if (i == 0) {
                mFragments.add(new MessageListFragment());
            }
            if (i == 1) {
                mFragments.add(new ContactCustomerServiceFragment());
            }
        }

        mStl_selling_option.setOnTabSelectListener(new OnTabSelectListener() {
            @SuppressLint("LongLogTag")
            @Override
            public void onTabSelect(int position) {
            }

            @SuppressLint("LongLogTag")
            @Override
            public void onTabReselect(int position) {
                mAdapter.notifyDataSetChanged();
            }
        });

        mAdapter = new MyPagerAdapter(getSupportFragmentManager());
        mVp_selling_content.setAdapter(mAdapter);
        mVp_selling_content.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {
            }

            @Override
            public void onPageSelected(int i) {
            }

            @Override
            public void onPageScrollStateChanged(int i) {
                if (i == 0) {
                    mAdapter.notifyDataSetChanged();
                }
            }
        });
        mStl_selling_option.setViewPager(mVp_selling_content);
    }

    @SuppressLint("HandlerLeak")
    Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    EMMessage emMessage = (EMMessage) msg.obj;
                    String msgType = emMessage.getType().toString();
                    if (!msgType.isEmpty()) {
                        mAdapter.notifyDataSetChanged();
                        Log.d("emMessageListener", "emMessageListener viewpager刷新");
                    }
                    break;
            }
        }
    };
    EMMessageListener eMMessageListener = new EMMessageListener() {
        @Override
        public void onMessageReceived(List<EMMessage> messages) {
            for (EMMessage emMessage : messages) {
                Message msg = mHandler.obtainMessage();
                msg.what = 0;
                msg.obj = emMessage;
                mHandler.sendMessage(msg);
            }
        }

        @Override
        public void onCmdMessageReceived(List<EMMessage> messages) {

        }

        @Override
        public void onMessageRead(List<EMMessage> messages) {

        }

        @Override
        public void onMessageDelivered(List<EMMessage> messages) {

        }

        @Override
        public void onMessageRecalled(List<EMMessage> messages) {

        }

        @Override
        public void onMessageChanged(EMMessage message, Object change) {

        }
    };

    @Override
    public void onClick(View v) {
        if (v == mTv_gengxin) {
            //检测更新
            new CheckVersionInfoTask(mContext, true).execute();
        }
        if (v == mTv_tuichu) {
            String registrationID = SPUtils.getInstance().getString("registrationID");
            getDataFromSignOut(registrationID);
        }
        if (v == mIb_more_selling_back) {
            System.gc();
            finish();
        }
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    public class MyPagerAdapter extends FragmentPagerAdapter {

        private FragmentManager fragmentManager;
        private int mChildCount = 0;

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
            this.fragmentManager = fm;
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mTitles[position];
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            Fragment fragment = (Fragment) super.instantiateItem(container, position);
            this.fragmentManager.beginTransaction().show(fragment).commit();
            return fragment;
        }

        @Override
        public int getItemPosition(Object object) {
            if (mChildCount > 0) {
                mChildCount--;
                return POSITION_NONE;
            }
            return super.getItemPosition(object);
        }

        @Override
        public void notifyDataSetChanged() {
            mChildCount = getCount();
            super.notifyDataSetChanged();
        }
    }

    public MyPagerAdapter getAdapter() {
        return mAdapter;
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
            startActivity(new Intent(mContext, CustomerServiceUserActivity.class));
            finish();

        } else {

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
                            NotificationChannel channel = new NotificationChannel("客服被踢", "客服被踢", NotificationManager.IMPORTANCE_HIGH);
                            Uri uri = Uri.parse("android.resource://" + mContext.getPackageName() + "/" + R.raw.shuishengchang);
                            AudioAttributes.Builder audioBuilder = new AudioAttributes.Builder();
                            audioBuilder.setUsage(AudioAttributes.USAGE_NOTIFICATION_COMMUNICATION_INSTANT);
                            AudioAttributes audioAttributes = audioBuilder.build();
                            channel.setSound(uri, audioAttributes);
                            channel.enableLights(true);//闪光灯
                            channel.setLockscreenVisibility(VISIBILITY_SECRET);//锁屏显示通知
                            channel.setLightColor(Color.RED);//闪关灯的灯光颜色
                            manager.createNotificationChannel(channel);

                            Notification.Builder builder = new Notification.Builder(mContext, "客服被踢");
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
}
