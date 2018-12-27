package com.qiangyu.service;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.RemoteException;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.qiangyu.R;
import com.qiangyu.StrongService;
import com.qiangyu.im.enity.KFList;
import com.qiangyu.im.ui.activity.ChatActivity;
import com.qiangyu.im.ui.activity.ContactCustomerServiceActivity;
import com.qiangyu.im.ui.fragment.MessageListFragment;
import com.qiangyu.my.bean.Designer;
import com.qiangyu.my.bean.JsonDesignerData;
import com.qiangyu.utils.Constants;
import com.qiangyu.utils.JudgeServiceUtil;
import com.qiangyu.utils.MD5Util;
import com.qiangyu.utils.SPUtils;
import com.qiangyu.utils.ToastUtil;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.app.Notification.VISIBILITY_SECRET;

public class MyService extends Service {

    private String TAG = "MyService";
    private Context mContext;
    private String mContentText;
    private int id = 1;
    private static PowerManager.WakeLock mWakeLock = null;
    private List<KFList> mKfList;
    private Intent mIntent;
    private String mCustomer;
    private List<EMMessage> mEMMessageList;
    private List<Designer> mDesignerList;
    private String mDisplayName;
    private String mHeadPic;

    @Override
    public void onCreate() {
        super.onCreate();
        this.mContext = this;

        //startScreenService();
        /*
         * 此线程用监听Service2的状态
         */
        /*new Thread() {
            public void run() {
                while (true) {
                    boolean isRun = JudgeServiceUtil.isServiceWork(mContext, "com.qiangyu.service.ScreenService");
                    if (!isRun) {
                        Message msg = Message.obtain();
                        msg.what = 1;
                        handler.sendMessage(msg);
                    }
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
        }.start();*/

        acquireWakeLock(mContext);
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, MyService.class.getName());
        mWakeLock.acquire();

        /*new ScreenListener(mContext).begin(new ScreenListener.ScreenStateListener() {
            @Override
            public void onScreenOn() {
                // 亮屏，finish一个像素的Activity
                Intent intent = new Intent(mContext,MyService.class);
                startService(intent);
                //KeepLiveActivityManager.getInstance(mContext).finishKeepLiveActivity();
            }

            @Override
            public void onScreenOff() {
                Intent intent = new Intent(mContext,MyService.class);
                startService(intent);
                // 灭屏，启动一个像素的Activity
                //KeepLiveActivityManager.getInstance(mContext).startKeepLiveActivity();
            }
        });*/

    }

    //申请设备电源锁
    @SuppressLint("InvalidWakeLockTag")
    public static void acquireWakeLock(Context context) {
        if (null == mWakeLock) {
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK | PowerManager.ON_AFTER_RELEASE, "WakeLock");
            if (null != mWakeLock) {
                mWakeLock.acquire();
            }
        }
    }

    //释放设备电源锁
    public static void releaseWakeLock() {
        if (null != mWakeLock) {
            mWakeLock.release();
            mWakeLock = null;
        }
    }

    @SuppressLint("WrongConstant")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        this.mIntent = intent;
        //开启环信消息监听
        EMClient.getInstance().chatManager().addMessageListener(MessageListener);
        flags = START_STICKY;
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //注销环信消息监听
        EMClient.getInstance().chatManager().removeMessageListener(MessageListener);

        releaseWakeLock();
    }


    /*@Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }*/

    //================================================环信消息监听================================================


    EMMessageListener MessageListener = new EMMessageListener() {
        @Override
        public void onMessageReceived(List<EMMessage> messages) {
            mEMMessageList = messages;

            for (EMMessage message : mEMMessageList) {
                //EMLog.d(TAG, "onMessageReceived id : " + message.getMsgId());
                if (message.getMsgId() != null) {
                    mCustomer = SPUtils.getInstance().getString("Customer");
                    Log.d(TAG, "onMessageReceived: " + mCustomer);
                    if (StringUtils.isNotEmpty(mCustomer)) {
                        //获取会员
                        //getDataFromGetHeaderPic(yonghu, "0");//type=0获取会员
                        SortedMap<Object, Object> parameters = new TreeMap<>();
                        String randomStr = MD5Util.getRandomStr();
                        String timeStamp = MD5Util.getTimeStamp();
                        String characterEncoding = "UTF-8";
                        parameters.put("name", message.getUserName());
                        parameters.put("type", "0");
                        parameters.put("nonceStr", randomStr);
                        parameters.put("timeStamp", timeStamp);
                        String mySign = MD5Util.createSign(characterEncoding, parameters);

                        RequestBody requestBody = new MultipartBody.Builder()
                                .setType(MultipartBody.FORM)
                                .addFormDataPart("name", message.getUserName())
                                .addFormDataPart("type", "0")
                                .addFormDataPart("nonceStr", randomStr)
                                .addFormDataPart("timeStamp", timeStamp)
                                .addFormDataPart("sign", mySign)
                                .build();

                        OkHttpClient client = new OkHttpClient
                                .Builder()
                                .readTimeout(5, TimeUnit.SECONDS).build();
                        Request request = new Request.Builder().url(Constants.QIANGYU_URL + "GetHeaderPic")
                                .post(requestBody)
                                .build();
                        Call call = client.newCall(request);
                        try {
                            Response response = call.execute();
                            if (response != null) {
                                String json = response.body().string();

                                json = json.replace("\\", "");
                                json = json.replace("\"[", "[");
                                json = json.replace("]\"", "]");

                                String code = JSON.parseObject(json).get("Code").toString().trim();
                                if ("OK".equals(code)) {
                                    mDesignerList = null;
                                    JsonDesignerData jsonDesignerData = JSON.parseObject(json, JsonDesignerData.class);
                                    mDesignerList = jsonDesignerData.getResult().getDesigner();
                                    Log.d(TAG, "onMessageReceived: " + mDesignerList.size());
                                    mDisplayName = mDesignerList.get(0).getDisplayName();
                                    mHeadPic = mDesignerList.get(0).getHeadPic();
                                }
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        //获取用户
                        //getDataFromGetHeaderPic(yonghu, "1");//type=1获取客服
                        SortedMap<Object, Object> parameters = new TreeMap<>();
                        String randomStr = MD5Util.getRandomStr();
                        String timeStamp = MD5Util.getTimeStamp();
                        String characterEncoding = "UTF-8";
                        parameters.put("name", message.getUserName());
                        parameters.put("type", "1");
                        parameters.put("nonceStr", randomStr);
                        parameters.put("timeStamp", timeStamp);
                        String mySign = MD5Util.createSign(characterEncoding, parameters);

                        RequestBody requestBody = new MultipartBody.Builder()
                                .setType(MultipartBody.FORM)
                                .addFormDataPart("name", message.getUserName())
                                .addFormDataPart("type", "1")
                                .addFormDataPart("nonceStr", randomStr)
                                .addFormDataPart("timeStamp", timeStamp)
                                .addFormDataPart("sign", mySign)
                                .build();

                        OkHttpClient client = new OkHttpClient.Builder()
                                .readTimeout(5, TimeUnit.SECONDS).build();
                        Request request = new Request.Builder().url(Constants.QIANGYU_URL + "GetHeaderPic")
                                .post(requestBody)
                                .build();
                        Call call = client.newCall(request);
                        try {
                            Response response = call.execute();
                            if (response != null) {
                                String json = response.body().string();

                                json = json.replace("\\", "");
                                json = json.replace("\"[", "[");
                                json = json.replace("]\"", "]");

                                String code = JSON.parseObject(json).get("Code").toString().trim();
                                if ("OK".equals(code)) {
                                    mDesignerList = null;
                                    JsonDesignerData jsonDesignerData = JSON.parseObject(json, JsonDesignerData.class);
                                    mDesignerList = jsonDesignerData.getResult().getDesigner();
                                    Log.d(TAG, "onMessageReceived: " + mDesignerList.size());
                                    mDisplayName = mDesignerList.get(0).getDisplayName();
                                    mHeadPic = mDesignerList.get(0).getHeadPic();
                                }
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    String type = message.getType().toString();
                    if ("TXT".equals(type)) {
                        EMTextMessageBody body = (EMTextMessageBody) message.getBody();
                        mContentText = body.getMessage();
                    }
                    if ("VOICE".equals(type)) {
                        mContentText = "[语音消息]";
                    }
                    if ("IMAGE".equals(type)) {
                        mContentText = "[图片]";
                    }

                    //设置跳转的页面
                    PendingIntent intent = PendingIntent.getActivity(mContext, 100, new Intent(mContext, ChatActivity.class)
                            .putExtra("kefu", message.getUserName())
                            .putExtra("headPic", mHeadPic)
                            .putExtra("displayName", mDisplayName), PendingIntent.FLAG_CANCEL_CURRENT);

                    if (Build.VERSION.SDK_INT >= 26) {
                        NotificationManager manager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
                        NotificationChannel channel = new NotificationChannel("即时通讯通知", "即时通讯通知", NotificationManager.IMPORTANCE_HIGH);
                        Uri uri = Uri.parse("android.resource://" + mContext.getPackageName() + "/" + R.raw.wumiaoshengyin);
                        AudioAttributes.Builder audioBuilder = new AudioAttributes.Builder();
                        audioBuilder.setUsage(AudioAttributes.USAGE_NOTIFICATION_COMMUNICATION_INSTANT);
                        AudioAttributes audioAttributes = audioBuilder.build();
                        channel.setSound(uri, audioAttributes);

                        channel.enableLights(true);//闪光灯
                        channel.setLockscreenVisibility(VISIBILITY_SECRET);//锁屏显示通知
                        channel.setLightColor(Color.RED);//闪关灯的灯光颜色

                        manager.createNotificationChannel(channel);


                        Notification.Builder builder = new Notification.Builder(mContext, "即时通讯通知");
                        builder.setSmallIcon(R.mipmap.ic_launcher)
                                //.setTicker("通知")
                                .setContentTitle(mDisplayName)
                                .setContentText(mContentText)
                                .setWhen(System.currentTimeMillis())
                                .setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.ic_launcher))
                                .setContentIntent(intent)
                                .setAutoCancel(true);
                        manager.notify(id++, builder.build());

                        //直接播放一段声音
                        //Ringtone r = RingtoneManager.getRingtone(mContext.getApplicationContext(), uri);
                        //r.play();

                        return;
                    }

                    NotificationManager mNotificationManager = (NotificationManager) mContext.getSystemService(NOTIFICATION_SERVICE);
                    Notification notification = new NotificationCompat.Builder(mContext, "default")
                            .setPriority(Notification.PRIORITY_DEFAULT)// 设置该通知优先级
                            //.setTicker("通知")
                            .setContentTitle(mDisplayName)
                            .setContentText(mContentText)
                            .setWhen(System.currentTimeMillis())
                            .setContentIntent(intent)
                            .setSmallIcon(R.mipmap.ic_launcher)
                            .setSound(Uri.parse("android.resource://" + mContext.getPackageName() + "/" + R.raw.wumiaoshengyin))
                            .setPriority(NotificationCompat.PRIORITY_HIGH)
                            .setChannelId(mContext.getPackageName())
                            .setAutoCancel(true)
                            .build();
                    mNotificationManager.notify(id++, notification);
                }
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

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 1:
                    //startScreenService();
                    break;

                default:
                    break;
            }

        }

        ;
    };

    /**
     * 使用aidl 启动Service2
     */
    private StrongService mStrongService = new StrongService.Stub() {

        @Override
        public void startService() throws RemoteException {
            Intent i = new Intent(getBaseContext(), ScreenService.class);
            getBaseContext().startService(i);
        }

        @Override
        public void stopService() throws RemoteException {
            Intent i = new Intent(getBaseContext(), ScreenService.class);
            getBaseContext().stopService(i);
        }

        @Override
        public void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat, double aDouble, String aString) throws RemoteException {

        }

    };

    /**
     * 在内存紧张的时候，系统回收内存时，会回调OnTrimMemory， 重写onTrimMemory当系统清理内存时从新启动Service2
     */
    @Override
    public void onTrimMemory(int level) {
        /*
         * 启动service2
         */
        //startScreenService();

    }

    /**
     * 判断Service2是否还在运行，如果不是则启动Service2
     */
    /*private void startScreenService() {
        boolean isRun = JudgeServiceUtil.isServiceWork(mContext, "com.qiangyu.service.ScreenService");
        if (!isRun) {
            try {
                mStrongService.startService();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }*/
    @Override
    public IBinder onBind(Intent intent) {
        return (IBinder) mStrongService;
    }

}
