package com.qiangyu.my.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.qiangyu.R;
import com.qiangyu.home.activity.DemandReleaseActivity;
import com.qiangyu.my.adapter.RequirementAdapter;
import com.qiangyu.my.bean.JsonRequirementData;
import com.qiangyu.my.bean.WantList;
import com.qiangyu.utils.Constants;
import com.qiangyu.utils.Loading;
import com.qiangyu.utils.MD5Util;
import com.qiangyu.utils.MyListView;
import com.qiangyu.utils.SPUtils;
import com.qiangyu.utils.ToastUtil;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

public class RequirementActivity extends Activity implements View.OnClickListener {

    private String TAG = "RequirementActivity";
    private Context mContext;
    private RelativeLayout mRl_gone_requirement;
    private TextView mTv_requirement_release;
    private ImageButton mIb_all_my_order_back;
    private int startIndex;
    private int endIndex;
    private final int pageSize = 30;
    private ListView mLv_requirement;
    private RequirementAdapter mRequirementAdapter;
    private int mMid;
    private String mCityId;
    private String mSignInPwd;
    private boolean loadFinishFlag = true;
    private int mSize;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mContext = this;
        mMid = SPUtils.getInstance().getInt("mid");
        mSignInPwd = SPUtils.getInstance().getString("signInPwd");
        startIndex = 1;
        mCityId = SPUtils.getInstance().getString("CityId");

        initView();
    }

    private void initView() {
        setContentView(R.layout.activity_release_requirement);
        mRl_gone_requirement = findViewById(R.id.rl_gone_requirement);
        mTv_requirement_release = findViewById(R.id.tv_requirement_release);
        mIb_all_my_order_back = findViewById(R.id.ib_all_my_order_back);
        mLv_requirement = findViewById(R.id.lv_requirement);

        mLv_requirement.setOnScrollListener(new ScrollListener());
        getDataFromGetUserWants(String.valueOf(mMid), mCityId, String.valueOf(startIndex), String.valueOf(pageSize), mSignInPwd);
        mIb_all_my_order_back.setOnClickListener(this);
        mTv_requirement_release.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view == mIb_all_my_order_back) {
            finish();
        }
        if (view == mTv_requirement_release) {
            Intent intent = new Intent(this, DemandReleaseActivity.class);
            startActivity(intent);
        }
    }

    //=================================================我发布的需求=================================================\\
    /*
    /// <param name="shopId"></param>
    /// <param name="nonceStr"></param>
    /// <param name="timeStamp"></param>
    /// <param name="sign"></param>
    /// <param name="mid">会员</param>
    /// <param name="page">页码 ，要求大于等于1 ,其他值无效</param>
    /// <param name="pageSize">数量，可以传入自定义数值，默认30；</param>
     */
    void getDataFromGetUserWants(String mid, String shopId, String page, String pageSize, String signInPwd) {
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
                .url(Constants.QIANGYU_URL + "GetUserWants")
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
                        processDataGetUserWants(response);
                    }
                });

    }

    private void processDataGetUserWants(String json) {
        Log.d(TAG, "processDataCartList: " + json);
        JsonRequirementData jsonRequirementData = JSON.parseObject(json, JsonRequirementData.class);
        List<WantList> wantList = jsonRequirementData.getResult().getWantList();
        if (mRequirementAdapter == null) {
            mSize = wantList.size();
            mRequirementAdapter = new RequirementAdapter(mContext, wantList);
            mLv_requirement.setAdapter(mRequirementAdapter);
        } else {
            mSize = wantList.size();
            mRequirementAdapter.onDateChange(wantList);
        }
    }

    private class ScrollListener implements AbsListView.OnScrollListener {

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            if (scrollState == SCROLL_STATE_IDLE && loadFinishFlag) {
                startIndex = startIndex + 1;
                Log.d(TAG, "onScrollStateChanged: " + startIndex);
                if (mSize != 0) {
                    getDataFromGetUserWants(String.valueOf(mMid), mCityId, String.valueOf(startIndex), String.valueOf(pageSize), mSignInPwd);
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
