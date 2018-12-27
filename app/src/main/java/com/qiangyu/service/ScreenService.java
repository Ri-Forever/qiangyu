package com.qiangyu.service;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;

import com.qiangyu.StrongService;
import com.qiangyu.utils.JudgeServiceUtil;

public class ScreenService extends Service {

    private Context mContext;

    public void onCreate() {
        this.mContext = this;
        super.onCreate();

        //startMyService();
        /*
         * 此线程用监听Service2的状态
         */
        /*new Thread() {
            public void run() {
                while (true) {
                    boolean isRun = JudgeServiceUtil.isServiceWork(mContext, "com.qiangyu.service.MyService");
                    if (!isRun) {
                        Message msg = Message.obtain();
                        msg.what = 1;
                        handler.sendMessage(msg);
                    }
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();*/

        /*new ScreenListener(mContext).begin(new ScreenListener.ScreenStateListener() {

            @Override
            public void onScreenOn() {
                Intent intent = new Intent(mContext,MyService.class);
                startService(intent);
                // 亮屏，finish一个像素的Activity
                //KeepLiveActivityManager.getInstance(ScreenService.this).finishKeepLiveActivity();
            }

            @Override
            public void onScreenOff() {
                Intent intent = new Intent(mContext,MyService.class);
                startService(intent);
                // 灭屏，启动一个像素的Activity
                //KeepLiveActivityManager.getInstance(ScreenService.this).startKeepLiveActivity();
            }
        });*/
    }

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 1:
                    //startMyService();
                    break;

                default:
                    break;
            }

        }

        ;
    };

    /**
     * 使用aidl 启动Service1
     */
    private StrongService mStrongService = new StrongService.Stub() {

        @Override
        public void stopService() throws RemoteException {
            Intent i = new Intent(getBaseContext(), MyService.class);
            getBaseContext().stopService(i);
        }

        @Override
        public void startService() throws RemoteException {
            Intent i = new Intent(getBaseContext(), MyService.class);
            getBaseContext().startService(i);

        }

        @Override
        public void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat, double aDouble, String aString) throws RemoteException {

        }

    };

    /**
     * 在内存紧张的时候，系统回收内存时，会回调OnTrimMemory， 重写onTrimMemory当系统清理内存时从新启动Service1
     */
    @Override
    public void onTrimMemory(int level) {
        //startMyService();
    }

    /**
     * 判断Service1是否还在运行，如果不是则启动Service1
     */
    /*private void startMyService() {
        boolean isRun = JudgeServiceUtil.isServiceWork(mContext, "com.qiangyu.service.MyService");
        if (!isRun) {
            try {
                mStrongService.startService();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }*/

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return (IBinder) mStrongService;
    }

}
