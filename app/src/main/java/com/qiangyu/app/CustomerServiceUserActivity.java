package com.qiangyu.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;

import com.qiangyu.R;
import com.qiangyu.customer.CustomerLoginActivity;
import com.qiangyu.my.activity.UserLoginActivity;

public class CustomerServiceUserActivity extends Activity {

    private Context mContext;
    private Button btnYonghu;
    private Button btnKefu;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mContext = this;

        setContentView(R.layout.activity_customer_service_user);
        initView();
    }

    private void initView() {
        btnYonghu = findViewById(R.id.btn_yonghu);
        btnKefu = findViewById(R.id.btn_kefu);

        btnYonghu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //用户登录
                Intent intent = new Intent(mContext, UserLoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        btnKefu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //客服登录CustomerLoginActivity
                Intent intent = new Intent(mContext, CustomerLoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

}