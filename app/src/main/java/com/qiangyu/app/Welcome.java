package com.qiangyu.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;

import com.qiangyu.R;
import com.qiangyu.im.ui.activity.ContactCustomerServiceActivity;
import com.qiangyu.utils.DeviceUtils;
import com.qiangyu.utils.SPUtils;

import org.apache.commons.lang3.StringUtils;

public class Welcome extends Activity {

    private Activity mActivity;
    private Context mContext;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mContext = this;
        this.mActivity = this;
        setContentView(R.layout.activity_welcome);
        //两秒后进入主页面
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //执行主线程,启动主页面
                String customerService = SPUtils.getInstance().getString("CustomerService");
                String username = SPUtils.getInstance().getString("username");
                //判断之前是否登录过,如果登录过不在跳转选择登录界面
                if (StringUtils.isNotEmpty(customerService)) {
                    //客服界面
                    Intent intent = new Intent(mContext, ContactCustomerServiceActivity.class);
                    intent.putExtra("Customer", "Customer");
                    startActivity(intent);
                    finish();
                    return;
                }
                if (StringUtils.isNotEmpty(username)) {
                    //用户界面
                    startActivity(new Intent(mContext, MainActivity.class));
                    finish();
                    return;
                }

                //startActivity(new Intent(mContext, MainActivity.class));
                startActivity(new Intent(mContext, CustomerServiceUserActivity.class));
                finish();
            }
        }, 2000);
    }

}
