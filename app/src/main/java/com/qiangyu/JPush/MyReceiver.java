package com.qiangyu.JPush;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;

import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.qiangyu.R;
import com.qiangyu.im.ui.activity.ContactCustomerServiceActivity;
import com.qiangyu.my.activity.ConstructionSheetActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;
import java.util.List;

import cn.jpush.android.api.JPushInterface;

import static android.app.Notification.VISIBILITY_SECRET;
import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * 自定义接收器
 * <p>
 * 如果不定义这个 Receiver，则：
 * 1) 默认用户会打开主界面
 * 2) 接收不到自定义消息
 */
public class MyReceiver extends BroadcastReceiver {

    private static final String TAG = "JIGUANG-Example";
    private String mContentText;
    private Context mContext;
    private int id = 1;

    @Override
    public void onReceive(Context context, Intent intent) {
        this.mContext = context;

        //开启环信消息监听
        //EMClient.getInstance().chatManager().addMessageListener(MessageListener);

        try {
            Bundle bundle = intent.getExtras();
            Log.d(TAG, "[MyReceiver] onReceive - " + intent.getAction() + ", extras: " + printBundle(bundle));

            if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
                String regId = bundle.getString(JPushInterface.EXTRA_REGISTRATION_ID);
                Log.d(TAG, "[MyReceiver] 接收Registration Id : " + regId);
                //send the Registration Id to your server...

            } else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
                Log.d(TAG, "[MyReceiver] 接收到推送下来的自定义消息: " + bundle.getString(JPushInterface.EXTRA_MESSAGE));
                // 自定义消息不会展示在通知栏，完全要开发者写代码去处理
                String extra = bundle.getString(JPushInterface.EXTRA_EXTRA);
                String title = bundle.getString(JPushInterface.EXTRA_TITLE);
                String message = bundle.getString(JPushInterface.EXTRA_MESSAGE);

                Log.d(TAG, "收到了自定义消息@@消息extra内容是: " + extra);
                Log.d(TAG, "收到了自定义消息@@消息title内容是: " + title);
                Log.d(TAG, "收到了自定义消息@@消息message内容是: " + message);
                //ToastUtil.toastCenter(context, "title: " + title + " --- message: " + message);
                if (extra != null) {
                    int wantNo = extra.indexOf("WantNo");
                    int orderNo = extra.indexOf("OrderNo");

                    Log.d(TAG, "wantNo: " + wantNo + " -- orderNo: " + orderNo);
                    if (wantNo != -1) {
                        //收到需求订单/对消息进行推送处理
                        demandOrder(context, bundle);
                    }
                    if (orderNo != -1) {
                        //收到商品订单/对消息进行推送处理
                        commodityOrder(context, bundle);
                    }
                    //测试开启
                    //commodityOrder(context, bundle);
                }
            } else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
                Log.d(TAG, "[MyReceiver] 接收到推送下来的通知");
                int notifactionId = bundle.getInt(JPushInterface.EXTRA_NOTIFICATION_ID);
                Log.d(TAG, "[MyReceiver] 接收到推送下来的通知的ID: " + notifactionId);

            } else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
                Log.d(TAG, "[MyReceiver] 用户点击打开了通知");

                //打开自定义的Activity
                /*Intent i = new Intent(context, TestActivity.class);
                i.putExtras(bundle);
                //i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                context.startActivity(i);*/

            } else if (JPushInterface.ACTION_RICHPUSH_CALLBACK.equals(intent.getAction())) {
                Log.d(TAG, "[MyReceiver] 用户收到到RICH PUSH CALLBACK: " + bundle.getString(JPushInterface.EXTRA_EXTRA));
                //在这里根据 JPushInterface.EXTRA_EXTRA 的内容处理代码，比如打开新的Activity， 打开一个网页等..

            } else if (JPushInterface.ACTION_CONNECTION_CHANGE.equals(intent.getAction())) {
                boolean connected = intent.getBooleanExtra(JPushInterface.EXTRA_CONNECTION_CHANGE, false);
                Log.w(TAG, "[MyReceiver]" + intent.getAction() + " connected state change to " + connected);
            } else {
                Log.d(TAG, "[MyReceiver] Unhandled intent - " + intent.getAction());
            }
        } catch (Exception e) {

        }

    }


    // 打印所有的 intent extra 数据
    private static String printBundle(Bundle bundle) {
        StringBuilder sb = new StringBuilder();
        for (String key : bundle.keySet()) {
            if (key.equals(JPushInterface.EXTRA_NOTIFICATION_ID)) {
                sb.append("\nkey:" + key + ", value:" + bundle.getInt(key));
            } else if (key.equals(JPushInterface.EXTRA_CONNECTION_CHANGE)) {
                sb.append("\nkey:" + key + ", value:" + bundle.getBoolean(key));
            } else if (key.equals(JPushInterface.EXTRA_EXTRA)) {
                if (TextUtils.isEmpty(bundle.getString(JPushInterface.EXTRA_EXTRA))) {
                    Log.i(TAG, "This message has no Extra data");
                    continue;
                }
                try {
                    JSONObject json = new JSONObject(bundle.getString(JPushInterface.EXTRA_EXTRA));
                    Iterator<String> it = json.keys();

                    while (it.hasNext()) {
                        String myKey = it.next();
                        sb.append("\nkey:" + key + ", value: [" +
                                myKey + " - " + json.optString(myKey) + "]");
                    }
                } catch (JSONException e) {
                    Log.e(TAG, "Get message extra JSON error!");
                }

            } else {
                sb.append("\nkey:" + key + ", value:" + bundle.get(key));
            }
        }
        return sb.toString();
    }

    //send msg to ProcessCustomMessageActivity
    private void demandOrder(Context context, Bundle bundle) {
        //收到需求订单/对消息进行推送处理
        Log.d(TAG, "收到需求订单/对消息进行推送处理");
        String title = bundle.getString(JPushInterface.EXTRA_TITLE);
        String message = bundle.getString(JPushInterface.EXTRA_MESSAGE);
        //设置跳转的页面
        PendingIntent intent = PendingIntent.getActivity(context, 100, new Intent(context, ConstructionSheetActivity.class), PendingIntent.FLAG_CANCEL_CURRENT);

        if (Build.VERSION.SDK_INT >= 26) {
            NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationChannel channel = new NotificationChannel("需求订单通知", "需求订单通知", NotificationManager.IMPORTANCE_HIGH);
            Uri uri = Uri.parse("android.resource://" + mContext.getPackageName() + "/" + R.raw.tixing);
            AudioAttributes.Builder audioBuilder = new AudioAttributes.Builder();
            audioBuilder.setUsage(AudioAttributes.USAGE_NOTIFICATION_COMMUNICATION_INSTANT);
            AudioAttributes audioAttributes = audioBuilder.build();
            channel.setSound(uri, audioAttributes);
            channel.enableLights(true);//闪光灯
            channel.setLockscreenVisibility(VISIBILITY_SECRET);//锁屏显示通知
            channel.setLightColor(Color.RED);//闪关灯的灯光颜色
            manager.createNotificationChannel(channel);

            Notification.Builder builder = new Notification.Builder(context, "需求订单通知");
            builder.setSmallIcon(R.mipmap.ic_launcher)
                    .setWhen(System.currentTimeMillis())
                    .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher))
                    .setContentTitle(title)
                    .setContentText(message)
                    .setContentIntent(intent)
                    .setAutoCancel(true);
            manager.notify(id++, builder.build());
        } else {
            NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
            Notification notification = new NotificationCompat.Builder(context, "default")
                    .setContentTitle(title)
                    .setContentText(message)
                    .setWhen(System.currentTimeMillis())
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setChannelId(context.getPackageName())
                    .setSound(Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.tixing))
                    .setContentIntent(intent)
                    .setAutoCancel(true)
                    .build();
            mNotificationManager.notify(id++, notification);
        }
    }

    private void commodityOrder(Context context, Bundle bundle) {
        //收到商品订单/对消息进行推送处理
        Log.d(TAG, "收到商品订单/对消息进行推送处理");
        //设置跳转的页面
        //PendingIntent intent = PendingIntent.getActivity(context, 100, new Intent(context, ConstructionSheetActivity.class), PendingIntent.FLAG_CANCEL_CURRENT);
        String title = bundle.getString(JPushInterface.EXTRA_TITLE);
        String message = bundle.getString(JPushInterface.EXTRA_MESSAGE);
        if (Build.VERSION.SDK_INT >= 26) {
            NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationChannel channel = new NotificationChannel("商品订单通知", "商品订单通知", NotificationManager.IMPORTANCE_HIGH);
            Uri uri = Uri.parse("android.resource://" + mContext.getPackageName() + "/" + R.raw.dingdanshengyin);
            AudioAttributes.Builder audioBuilder = new AudioAttributes.Builder();
            audioBuilder.setUsage(AudioAttributes.USAGE_NOTIFICATION_COMMUNICATION_INSTANT);
            AudioAttributes audioAttributes = audioBuilder.build();
            channel.setSound(uri, audioAttributes);
            channel.enableLights(true);//闪光灯
            channel.setLockscreenVisibility(VISIBILITY_SECRET);//锁屏显示通知
            channel.setLightColor(Color.RED);//闪关灯的灯光颜色
            manager.createNotificationChannel(channel);

            Notification.Builder builder = new Notification.Builder(context, "商品订单通知");
            builder.setSmallIcon(R.mipmap.ic_launcher)
                    .setWhen(System.currentTimeMillis())
                    .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher))
                    .setContentTitle(title)
                    .setContentText(message)
                    .setAutoCancel(true);
            manager.notify(id++, builder.build());
        } else {
            NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
            Notification notification = new NotificationCompat.Builder(context, "default")
                    .setContentTitle(title)
                    .setContentText(message)
                    .setWhen(System.currentTimeMillis())
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setChannelId(context.getPackageName())
                    .setSound(Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.dingdanshengyin))
                    .setAutoCancel(true)
                    .build();
            mNotificationManager.notify(id++, notification);
        }
    }

    //================================================环信消息监听================================================


    EMMessageListener MessageListener = new EMMessageListener() {
        @Override
        public void onMessageReceived(List<EMMessage> messages) {
            for (EMMessage message : messages) {
                //EMLog.d(TAG, "onMessageReceived id : " + message.getMsgId());
                if (message.getMsgId() != null) {
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
                    PendingIntent intent = PendingIntent.getActivity(mContext, 100, new Intent(mContext, ContactCustomerServiceActivity.class), PendingIntent.FLAG_CANCEL_CURRENT);

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
                                .setContentTitle(message.getUserName())
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
                            .setContentTitle(message.getUserName())
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

}
