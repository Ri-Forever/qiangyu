package com.qiangyu.my.activity;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.qiangyu.R;

public class OfferDialog extends Dialog implements View.OnClickListener {

    private Activity mContext;
    private EditText mEt_offer;
    private TextView mTv_sure;
    private TextView mTv_cancel;

    public OfferDialog(@NonNull Activity context) {
        super(context);
        this.mContext = context;
    }

    public OfferDialog(@NonNull Activity context, int themeResId) {
        super(context, themeResId);
        this.mContext = context;
    }

    protected OfferDialog(@NonNull Activity context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        this.mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }

    private void initView() {
        setContentView(R.layout.activity_offer);
        mEt_offer = findViewById(R.id.et_offer);
        mTv_sure = findViewById(R.id.tv_sure);
        mTv_cancel = findViewById(R.id.tv_cancel);
        Window dialogWindow = this.getWindow();
        WindowManager windowManager = mContext.getWindowManager();
        Display defaultDisplay = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams attributes = dialogWindow.getAttributes();
        attributes.width=(int)(defaultDisplay.getWidth()*0.8);
        dialogWindow.setAttributes(attributes);
        this.setCancelable(true);
        initListener();
    }

    private void initListener() {
        mTv_sure.setOnClickListener(this);
        mTv_cancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view == mTv_sure) {
            String value = mEt_offer.getText().toString().trim();
            Toast.makeText(mContext,value,Toast.LENGTH_SHORT).show();
        }
        if (view == mTv_cancel) {
            mContext.finish();
        }
    }
}
