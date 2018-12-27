package com.qiangyu.utils;

import android.app.ProgressDialog;
import android.content.Context;

import com.qiangyu.app.MainActivity;
import com.qiangyu.app.MyApplication;

public class Loading {
    private static ProgressDialog mDialog;

    public static void loading(Context context, String content) {
        mDialog = new ProgressDialog(context);
        mDialog.setMessage(content);
        mDialog.show();
    }

    public static void endLoad() {
        mDialog.dismiss();
    }
}
