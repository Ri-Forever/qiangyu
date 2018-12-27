package com.qiangyu.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler {

    private static String TAG = "WXPayEntryActivity";

    private IWXAPI api;

    private String APP_ID = "wxff5ae1ee22dc0c95"; //这里需要替换你的APP_ID

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        api = WXAPIFactory.createWXAPI(this, APP_ID);
        api.handleIntent(getIntent(), this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        api.handleIntent(intent, this);
    }

    @Override
    public void onReq(BaseReq baseReq) {
        Log.d(TAG, "onReq == " + baseReq.openId);
    }

    @Override
    public void onResp(BaseResp baseResp) {
        Log.d(TAG, "onResp == " + baseResp.errCode);
        if (baseResp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
            if (baseResp.errCode == 0) {
                Toast.makeText(this, "支付成功!", Toast.LENGTH_LONG).show();
            } else if (baseResp.errCode == -1) {
                Toast.makeText(this, "支付失败!", Toast.LENGTH_LONG).show();
            } else if (baseResp.errCode == -2) {
                Toast.makeText(this, "取消支付!", Toast.LENGTH_LONG).show();
            }
            finish();
        }
    }
}
