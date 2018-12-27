package com.qiangyu.my.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.qiangyu.R;

public class RequirementDetailsActivity extends Activity implements View.OnClickListener {

    private ImageButton ibBack;
    private TextView tvNumber;
    private TextView tvDate;
    private TextView tvContacts;
    private TextView tvContactInformation;
    private TextView tvAddress;
    private TextView tvTitle;
    private TextView tvDemand;
    private Button btnSubmit;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initView();
    }

    private void initView() {
        setContentView(R.layout.activity_requirement_details);

        ibBack = findViewById(R.id.ib_back);
        tvNumber = findViewById(R.id.tv_number);
        tvDate = findViewById(R.id.tv_date);
        tvContacts = findViewById(R.id.tv_contacts);
        tvContactInformation = findViewById(R.id.tv_contact_information);
        tvAddress = findViewById(R.id.tv_address);
        tvTitle = findViewById(R.id.tv_title);
        tvDemand = findViewById(R.id.tv_demand);
        btnSubmit = findViewById(R.id.btn_submit);

        /*tvNumber.setText(getNumber);
        tvDate.setText(getDate);
        tvContacts.setText("联系人:啊哈");
        tvContactInformation.setText("联系方式:1234567");
        tvAddress.setText("地址:啊哈");
        tvTitle.setText(getTitle);
        tvDemand.setText(getContent);*/

        initListener();
    }

    private void initListener() {
        ibBack.setOnClickListener(this);
        btnSubmit.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view == ibBack) {
            finish();
        }
        if (view == btnSubmit) {
        }
    }


}