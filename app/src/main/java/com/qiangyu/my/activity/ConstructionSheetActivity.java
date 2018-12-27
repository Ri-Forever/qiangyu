package com.qiangyu.my.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;

import com.alibaba.fastjson.JSON;
import com.qiangyu.R;
import com.qiangyu.my.adapter.ConstructionSheetListAdapter;
import com.qiangyu.my.bean.JsonConstructionSheetData;
import com.qiangyu.my.bean.NeedList;
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

public class ConstructionSheetActivity extends Activity implements View.OnClickListener {

    private String TAG = "ConstructionSheetActivity";
    private Context mContext;
    private ImageButton mIb_back;
    private ListView mLv_requirment;
    private Button mBtn_chakan;
    private int page;
    private int pageSize = 30;
    private int mMid;
    private String mSignInPwd;
    private String mCityId;
    private boolean loadFinishFlag = true;
    private int mSize;
    private ConstructionSheetListAdapter mConstructionSheetListAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mContext = this;
        page = 1;
        mMid = SPUtils.getInstance().getInt("mid");
        mSignInPwd = SPUtils.getInstance().getString("signInPwd");
        //getDataFromGetTeamNeedList(String mid, String shopId, String page, String pageSize, String signInPwd)
        mCityId = SPUtils.getInstance().getString("CityId");
        getDataFromGetTeamNeedList(String.valueOf(mMid), mCityId, String.valueOf(page), String.valueOf(pageSize), mSignInPwd);
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        String cityId = SPUtils.getInstance().getString("CityId");
        getDataFromGetTeamNeedList(String.valueOf(mMid), cityId, String.valueOf(page), String.valueOf(pageSize), mSignInPwd);
    }

    private void initView() {
        setContentView(R.layout.activity_construction_sheet);

        mIb_back = findViewById(R.id.ib_back);
        mBtn_chakan = findViewById(R.id.btn_chakan);
        mLv_requirment = findViewById(R.id.lv_requirment);

        mLv_requirment.setOnScrollListener(new ScrollListener());
        initListener();
    }

    private void initListener() {
        mIb_back.setOnClickListener(this);
        mBtn_chakan.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view == mIb_back) {
            finish();
        }
        if (view == mBtn_chakan) {
            Intent intent = new Intent(this, UserWantsActivity.class);
            startActivity(intent);
        }
    }

    //=================================================获取我的施工单=================================================\\
    void getDataFromGetTeamNeedList(String mid, String shopId, String page, String pageSize, String signInPwd) {
        SortedMap<Object, Object> parameters = new TreeMap<>();
        String randomStr = MD5Util.getRandomStr();
        String timeStamp = MD5Util.getTimeStamp();
        String characterEncoding = "UTF-8";
        parameters.put("mid", mid);
        parameters.put("shopId", shopId);
        parameters.put("page", page);
        parameters.put("pageSize", pageSize);
        parameters.put("nonceStr", randomStr);
        parameters.put("timeStamp", timeStamp);
        String mySign = MD5Util.createSign(characterEncoding, parameters, signInPwd);

        Log.d(TAG, "randomStr === " + randomStr + "---timeStamp === " + timeStamp + " ---- mySign ===" + mySign + " --- mid --- " + mid);
        OkHttpUtils
                .post()
                .url(Constants.QIANGYU_URL + "GetTeamNeedList")
                .addParams("mid", mid)
                .addParams("shopId", shopId)
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
                        Loading.endLoad();
                        //getDataFromCartList(shopId, mid, signInPwd);
                    }

                    /**
                     * 当联网成功时回调
                     * @param response
                     * @param id
                     */
                    @Override
                    public void onResponse(String response, int id) {
                        Log.d("getDataFromNet", " 未更改 === >" + response);
                        //解析数据
                        //response = response.replace("", "");
                        response = response.replace("\\", "");
                        response = response.replace("\"[", "[");
                        response = response.replace("]\"", "]");
                        Log.d("getDataFromNet", " 更改过 === >" + response);
                        processDataGetTeamNeedList(response);
                    }
                });

    }

    private void processDataGetTeamNeedList(String json) {
        Log.d(TAG, "processDataCartList: " + json);
        JsonConstructionSheetData jsonConstructionSheetData = JSON.parseObject(json, JsonConstructionSheetData.class);
        List<NeedList> needList = jsonConstructionSheetData.getResult().getNeedList();
        if (mConstructionSheetListAdapter == null) {
            mSize = needList.size();
            mConstructionSheetListAdapter = new ConstructionSheetListAdapter(mContext, needList);
            mLv_requirment.setAdapter(mConstructionSheetListAdapter);
        } else {
            mSize = needList.size();
            mConstructionSheetListAdapter.onDateChange(needList);
        }
    }

    private class ScrollListener implements AbsListView.OnScrollListener {

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            if (scrollState == SCROLL_STATE_IDLE && loadFinishFlag) {
                page = page + 1;
                if (mSize != 0) {
                    getDataFromGetTeamNeedList(String.valueOf(mMid), mCityId, String.valueOf(page), String.valueOf(pageSize), mSignInPwd);
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
