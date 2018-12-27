package com.qiangyu.utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.qiangyu.R;

import cn.jpush.android.api.JPushInterface;

import static android.app.Notification.VISIBILITY_SECRET;
import static android.content.Context.NOTIFICATION_SERVICE;

public class NotificationUtil {

    private static String TAG = "NotificationUtil";
    private static int id = 1;

    public static void notification(Context context, Bundle bundle) {
        //对收到的消息进行通知
        Log.d(TAG, "收到商品订单/对消息进行推送处理");
        //设置跳转的页面
        //PendingIntent intent = PendingIntent.getActivity(context, 100, new Intent(context, ConstructionSheetActivity.class), PendingIntent.FLAG_CANCEL_CURRENT);
        String title = bundle.getString(JPushInterface.EXTRA_TITLE);
        String message = bundle.getString(JPushInterface.EXTRA_MESSAGE);
        if (Build.VERSION.SDK_INT >= 26) {
            NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationChannel channel = new NotificationChannel("channel_id", "channel_name", NotificationManager.IMPORTANCE_HIGH);
            channel.enableLights(true);//闪光灯
            channel.setLockscreenVisibility(VISIBILITY_SECRET);//锁屏显示通知
            channel.setLightColor(Color.RED);//闪关灯的灯光颜色
            manager.createNotificationChannel(channel);

            //播放声音
            Uri uri = Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.shuishengchang);
            Ringtone r = RingtoneManager.getRingtone(context.getApplicationContext(), uri);
            r.play();

            Notification.Builder builder = new Notification.Builder(context, "channel_id");
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
                    .setSound(Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.shuishengchang))
                    .build();
            mNotificationManager.notify(id++, notification);
        }
    }
}
