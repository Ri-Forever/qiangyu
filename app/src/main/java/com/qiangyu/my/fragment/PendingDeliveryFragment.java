package com.qiangyu.my.fragment;

import android.annotation.SuppressLint;
import android.view.View;
import android.widget.TextView;

import com.qiangyu.R;
import com.qiangyu.base.BaseFragment;

@SuppressLint("ValidFragment")
public class PendingDeliveryFragment extends BaseFragment {

    @Override
    protected View initView() {
        View v = View.inflate(mContext, R.layout.activity_empty, null);
        return v;
    }
}