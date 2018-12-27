package com.qiangyu.my.fragment;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.alibaba.fastjson.JSON;
import com.qiangyu.R;
import com.qiangyu.base.BaseFragment;
import com.qiangyu.my.adapter.FullOrderListAdapter;
import com.qiangyu.my.bean.JsonOrderListData;
import com.qiangyu.utils.Constants;
import com.qiangyu.utils.Loading;
import com.qiangyu.utils.MD5Util;
import com.qiangyu.utils.SPUtils;
import com.qiangyu.utils.ToastUtil;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.SortedMap;
import java.util.TreeMap;

@SuppressLint("ValidFragment")
public class FullOrderFragment extends BaseFragment {

    private String TAG = "FullOrderFragment";
    private JsonOrderListData.Result mOrderListResult;
    private ListView mLv_order_list;
    private boolean loadFinishFlag = true;
    private int startIndex;
    private int endIndex;
    private final int pageSize = 30;
    private View mInflateFooter;
    private FullOrderListAdapter mAdapter;
    private String mSignInPwd;
    private int mMid;
    private View mInflate;
    private String mOrderNo;
    private int mSize;
    private LinearLayout mLl_qingqiushibai;
    private LinearLayout mLl_empty;
    private int i = 1;//用来标记是否是第一次进入加载数据

    @Override
    protected View initView() {
        //getDataFromPlaceOrder(final String mid, final String type, String page, String pageSize,  final String signInPwd)
        mMid = SPUtils.getInstance().getInt("mid");
        mSignInPwd = SPUtils.getInstance().getString("signInPwd");
        startIndex = 1;
        endIndex = pageSize;
        getDataFromPlaceOrder(String.valueOf(mMid), "-1", String.valueOf(startIndex), String.valueOf(endIndex), mSignInPwd);

        View v = View.inflate(mContext, R.layout.order_list_activiy, null);
        mLl_qingqiushibai = v.findViewById(R.id.ll_qingqiushibai);
        mLl_empty = v.findViewById(R.id.ll_empty);
        mLv_order_list = v.findViewById(R.id.lv_order_list);

        LayoutInflater inflater = LayoutInflater.from(mContext);
        mInflate = inflater.inflate(R.layout.footer, null);
        mInflate.findViewById(R.id.load_layout).setVisibility(View.VISIBLE);
        mInflateFooter = getLayoutInflater().inflate(R.layout.footer, null);
        mLv_order_list.addFooterView(mInflate);
        mLv_order_list.setOnScrollListener(new ScrollListener());

        return v;
    }

    //=================================================获取订单=================================================\\
    void getDataFromPlaceOrder(final String mid, final String type, String page, String pageSize, final String signInPwd) {
        SortedMap<Object, Object> parameters = new TreeMap<>();
        String randomStr = MD5Util.getRandomStr();
        String timeStamp = MD5Util.getTimeStamp();
        String characterEncoding = "UTF-8";
        parameters.put("mid", mid);
        parameters.put("type", type);
        parameters.put("page", page);
        parameters.put("pageSize", pageSize);
        parameters.put("nonceStr", randomStr);
        parameters.put("timeStamp", timeStamp);
        String mySign = MD5Util.createSign(characterEncoding, parameters, signInPwd);

        Log.d(TAG, "randomStr === " + randomStr + "---timeStamp === " + timeStamp + " ---- mySign ===" + mySign + " --- mid --- " + mid);
        OkHttpUtils
                .post()
                .url(Constants.QIANGYU_URL + "GetOrderList")
                .addParams("mid", mid)
                .addParams("type", type)
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
                        mLl_qingqiushibai.setVisibility(View.VISIBLE);
                        mLv_order_list.setVisibility(View.GONE);
                        Loading.endLoad();
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
                        processDataPlaceOrder(response);
                    }
                });

    }

    private void processDataPlaceOrder(String json) {
        Log.d(TAG, "processDataCartList: " + json);
        JsonOrderListData jsonOrderListData = JSON.parseObject(json, JsonOrderListData.class);
        mOrderListResult = jsonOrderListData.getResult();

        if (i == 1) {
            if (mOrderListResult.getOrderList().size() == 0) {
                mLl_empty.setVisibility(View.VISIBLE);
                mLv_order_list.setVisibility(View.GONE);
            }
            i++;
        }

        if (mAdapter == null) {
            mSize = mOrderListResult.getOrderList().size();
            mAdapter = new FullOrderListAdapter(mContext, mOrderListResult);
            mLv_order_list.setAdapter(mAdapter);
        } else {
            mSize = mOrderListResult.getOrderList().size();
            mAdapter.onDateChange(mOrderListResult);
        }
        mLv_order_list.removeFooterView(mInflate);
    }

    private class ScrollListener implements AbsListView.OnScrollListener {
        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            if (scrollState == SCROLL_STATE_IDLE && loadFinishFlag) {
                startIndex = startIndex + 1;
                if (mSize != 0) {
                    getDataFromPlaceOrder(String.valueOf(mMid), "-1", String.valueOf(startIndex), String.valueOf(endIndex), mSignInPwd);
                }else {
                    ToastUtil.toastCenter(mContext,"亲,已经到底啦,没有数据了哦!");
                }
            }
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            loadFinishFlag = ((firstVisibleItem + visibleItemCount) == totalItemCount);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        initView();
    }

    @Override
    public void onStart() {
        super.onStart();
        mAdapter = null;
        i = 1;
    }
}