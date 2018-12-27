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

import com.alibaba.fastjson.JSON;
import com.qiangyu.R;
import com.qiangyu.my.adapter.UserWantAdapter;
import com.qiangyu.my.bean.JsonNeedListData;
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

public class UserWantsActivity extends Activity {

    private String TAG = "UserWantsActivity";
    private Context mContext;
    private ImageButton ibBack;
    private ListView lvRequirment;
    private int mMid;
    private int page;
    private int pageSize;
    private String mSignInPwd;
    private UserWantAdapter mUserWantAdapter;
    private boolean loadFinishFlag = true;
    private int mSize;
    private String mCityId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mContext = this;
        page = 1;
        pageSize = 30;
        mMid = SPUtils.getInstance().getInt("mid");
        mSignInPwd = SPUtils.getInstance().getString("signInPwd");
        mCityId = SPUtils.getInstance().getString("CityId");
        getDataFromGetNeedList(String.valueOf(mMid), mCityId, String.valueOf(page), String.valueOf(pageSize), mSignInPwd);
        setContentView(R.layout.activity_user_wants);
        initView();
    }

    private void initView() {
        ibBack = findViewById(R.id.ib_back);
        lvRequirment = findViewById(R.id.lv_requirment);

        lvRequirment.setOnScrollListener(new ScrollListener());
        ibBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    //=================================================获取需求列表=================================================\\
    /*
    /// <param name="shopId"></param>
    /// <param name="mid"></param>
    /// <param name="page">页码</param>
    /// <param name="pageSize">数量</param>
     */
    void getDataFromGetNeedList(String mid, String shopId, String page, String pageSize, String signInPwd) {
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
                .url(Constants.QIANGYU_URL + "GetNeedList")
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
                        processDataGetNeedList(response);
                    }
                });

    }

    private void processDataGetNeedList(String json) {
        Log.d(TAG, "processDataCartList: " + json);
        JsonNeedListData jsonNeedListData = JSON.parseObject(json, JsonNeedListData.class);
        List<NeedList> needList = jsonNeedListData.getResult().getNeedList();
        if (mUserWantAdapter == null) {
            mSize = needList.size();
            mUserWantAdapter = new UserWantAdapter(mContext, needList);
            lvRequirment.setAdapter(mUserWantAdapter);
        } else {
            mSize = needList.size();
            mUserWantAdapter.onDateChange(needList);
        }
    }

    private class ScrollListener implements AbsListView.OnScrollListener {

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            if (scrollState == SCROLL_STATE_IDLE && loadFinishFlag) {
                page = page + 1;
                if (mSize != 0) {
                    getDataFromGetNeedList(String.valueOf(mMid), mCityId, String.valueOf(page), String.valueOf(pageSize), mSignInPwd);
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
