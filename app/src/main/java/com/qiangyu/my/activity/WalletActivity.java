package com.qiangyu.my.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.qiangyu.R;
import com.qiangyu.my.adapter.RecordsConsumptionAdapter;
import com.qiangyu.my.bean.JsonWalletData;
import com.qiangyu.my.bean.Record;
import com.qiangyu.utils.Constants;
import com.qiangyu.utils.Loading;
import com.qiangyu.utils.MD5Util;
import com.qiangyu.utils.SPUtils;
import com.qiangyu.utils.ToastUtil;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

public class WalletActivity extends Activity implements View.OnClickListener {

    private Context mContext;
    private TextView mTv_balance;
    private ImageButton mIb_back;
    private int mPage;
    private int mPageSize;
    private ListView mMlv_record;
    private boolean loadFinishFlag = true;
    private int mSize;
    private String mMid;
    private String mSignInPwd;
    private RecordsConsumptionAdapter mRecordsConsumptionAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mContext = this;
        mPage = 1;
        mPageSize = 30;
        String username = SPUtils.getInstance().getString("username");
        mMid = String.valueOf(SPUtils.getInstance().getInt("mid"));
        mSignInPwd = SPUtils.getInstance().getString("signInPwd");
        getDataFromGetBalance(mMid, String.valueOf(mPage), String.valueOf(mPageSize), mSignInPwd);
        initView();
    }

    private void initView() {
        setContentView(R.layout.activity_wallet);
        mIb_back = findViewById(R.id.ib_back);
        mTv_balance = findViewById(R.id.tv_balance);
        mMlv_record = findViewById(R.id.mlv_record);

        mMlv_record.setOnScrollListener(new ScrollListener());
        initListener();
    }

    private void initListener() {
        mIb_back.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view == mIb_back) {
            finish();
        }
    }

    //=================================================获取钱包信息=================================================\\
    /*
    /// <param name="mid">会员标识</param>
    /// <param name="page">当前页码</param>
    /// <param name="pageSize">一页条数</param>
     */
    void getDataFromGetBalance(final String mid, String page, String pageSize, String signInPwd) {
        SortedMap<Object, Object> parameters = new TreeMap<>();
        String randomStr = MD5Util.getRandomStr();
        String timeStamp = MD5Util.getTimeStamp();
        String characterEncoding = "UTF-8";
        parameters.put("mid", mid);
        parameters.put("page", page);
        parameters.put("pageSize", pageSize);
        parameters.put("nonceStr", randomStr);
        parameters.put("timeStamp", timeStamp);
        String mySign = MD5Util.createSign(characterEncoding, parameters, signInPwd);

        OkHttpUtils
                .post()
                .url(Constants.QIANGYU_URL + "GetBalance")
                .addParams("mid", mid)
                .addParams("page", page)
                .addParams("pageSize", pageSize)
                .addParams("nonceStr", randomStr)
                .addParams("timeStamp", timeStamp)
                .addParams("sign", mySign)
                .build()
                .readTimeOut(10000)
                .writeTimeOut(10000)
                .connTimeOut(10000)
                /**
                 * 请求失败的时候回调
                 */
                .execute(new StringCallback() {
                    @Override
                    public void onError(okhttp3.Call call, Exception e, int id) {
                        Log.d("getDataFromNet", "请求失败 === >" + e.getMessage());
                    }

                    /**
                     * 当联网成功时回调
                     * @param response
                     * @param id
                     */
                    @Override
                    public void onResponse(String response, int id) {
                        //解析数据
                        Log.d("getDataFromNet", " 未改过 === >" + response);
                        response = response.replace("\\", "");
                        response = response.replace("\"[", "[");
                        response = response.replace("]\"", "]");
                        Log.d("getDataFromNet", " 更改过 === >" + response);
                        GetBalance(response);
                    }
                });

    }

    private void GetBalance(String json) {
        Loading.endLoad();
        JsonWalletData jsonWalletData = JSON.parseObject(json, JsonWalletData.class);
        List<Record> record = jsonWalletData.getResult().getRecord();
        String balance = jsonWalletData.getResult().getBalance();
        mTv_balance.setText(balance);
        if (mRecordsConsumptionAdapter == null) {
            mSize = record.size();
            mRecordsConsumptionAdapter = new RecordsConsumptionAdapter(mContext, record);
            mMlv_record.setAdapter(mRecordsConsumptionAdapter);
        } else {
            mSize = record.size();
            mRecordsConsumptionAdapter.onDateChange(record);
        }
    }

    private class ScrollListener implements AbsListView.OnScrollListener {

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            if (scrollState == SCROLL_STATE_IDLE && loadFinishFlag) {
                mPage = mPage + 1;
                if (mSize != 0) {
                    //getDataFromGetBalance(mMid, String.valueOf(mPage), String.valueOf(mPageSize), mSignInPwd);
                } else {
                    ToastUtil.toastCenter(mContext, "亲,已经到底啦,没有数据了哦!");
                }
            }
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            loadFinishFlag = ((firstVisibleItem + visibleItemCount) == totalItemCount);
        }
    }
}
