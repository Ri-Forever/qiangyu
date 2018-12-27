package com.qiangyu.my.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.qiangyu.R;

public class SettingActivity extends Activity implements View.OnClickListener {

    private TextView mTv_nickname;
    private TextView mTv_brief_introduction;
    private TextView mTv_login_password;
    private TextView mTv_pay_password;
    private ImageButton mIb_back;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }

    private void initView() {
        setContentView(R.layout.activity_setting);
        mIb_back = findViewById(R.id.ib_back);
        mTv_nickname = findViewById(R.id.tv_nickname);
        mTv_brief_introduction = findViewById(R.id.tv_brief_introduction);
        mTv_login_password = findViewById(R.id.tv_login_password);
        mTv_pay_password = findViewById(R.id.tv_pay_password);

        mIb_back.setOnClickListener(this);
        mTv_nickname.setOnClickListener(this);
        mTv_brief_introduction.setOnClickListener(this);
        mTv_login_password.setOnClickListener(this);
        mTv_pay_password.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view == mIb_back) {
            finish();
        } else if (view == mTv_nickname) {
            Intent intent = new Intent(this, NickNameActivity.class);
            startActivity(intent);
        } else if (view == mTv_brief_introduction) {
            Intent intent = new Intent(this, BriefIntroductionActivity.class);
            startActivity(intent);
        } else if (view == mTv_login_password) {
            Intent intent = new Intent(this, LoginPasswordActivity.class);
            startActivity(intent);
        } else if (view == mTv_pay_password) {
            Intent intent = new Intent(this, PayPasswordActivity.class);
            startActivity(intent);
        }
    }
}
