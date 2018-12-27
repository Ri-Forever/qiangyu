package com.qiangyu.home.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.qiangyu.R;
import com.qiangyu.app.GoodsInfoActivity;
import com.qiangyu.base.BaseFragment;
import com.qiangyu.home.adapter.MoreSellingHotAdapter;
import com.qiangyu.home.bean.Hot;
import com.qiangyu.home.bean.JsonHotBeanData;
import com.qiangyu.utils.Constants;
import com.qiangyu.utils.MD5Util;
import com.qiangyu.utils.SPUtils;
import com.qiangyu.utils.ToastUtil;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.SortedMap;
import java.util.TreeMap;

import okhttp3.Call;

@SuppressLint("ValidFragment")
public class MoreSellingFragment extends BaseFragment {

    private int i = 0;
    private ListView mListView;
    private String GOODS_BEAN = "goodsBean";
    private JsonHotBeanData.Result mHotResult;
    private MoreSellingHotAdapter mMoreSellingHotAdapter;
    private int mPage;
    private int mPageSize;
    private boolean loadFinishFlag = true;
    private int mSize;

    @Override
    protected View initView() {
        if (i == 0) {
            getDataFromHot();
        }
        View v = View.inflate(mContext, R.layout.list_view_more_selling, null);
        mListView = v.findViewById(R.id.lv_more_selling);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Hot hot = mHotResult.getHot().get(i);
                Toast.makeText(mContext, "点击的商品是<" + hot.getName() + ">", Toast.LENGTH_SHORT).show();
                String mChanpinId = hot.getId() + "";
                Intent intent = new Intent(mContext, GoodsInfoActivity.class);
                intent.putExtra(GOODS_BEAN, mChanpinId);
                mContext.startActivity(intent);
            }
        });
        return v;
    }

    void getDataFromHot() {
        String cityId = SPUtils.getInstance().getString("CityId");
        SortedMap<Object, Object> parameters = new TreeMap<>();
        String randomStr = MD5Util.getRandomStr();
        String timeStamp = MD5Util.getTimeStamp();
        String characterEncoding = "UTF-8";
        parameters.put("shopId", cityId);
        parameters.put("nonceStr", randomStr);
        parameters.put("timeStamp", timeStamp);
        String mySign = MD5Util.createSign(characterEncoding, parameters);

        OkHttpUtils
                .post()
                .url(Constants.QIANGYU_URL + "GetHomeHot")
                .addParams("shopId", cityId)
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
                    public void onError(Call call, Exception e, int id) {
                        Log.d("getDataFromNet", "更多热卖请求失败 === >" + e.getMessage());
                        //getDataFromHot();
                    }

                    /**
                     * 当联网成功时回调
                     * @param response
                     * @param id
                     */
                    @Override
                    public void onResponse(String response, int id) {
                        //解析数据
                        response = response.replace("\\", "");
                        response = response.replace("\"[", "[");
                        response = response.replace("]\"", "]");
                        Log.d("getDataFromNet", " 更多热卖 === >" + response);
                        processHotData(response);
                    }
                });
    }

    private void processHotData(String json) {
        JsonHotBeanData jsonHotBeanData = JSON.parseObject(json, JsonHotBeanData.class);
        mHotResult = jsonHotBeanData.getResult();
        if (mHotResult != null) {
            //有数据
            if (mMoreSellingHotAdapter == null) {
                mMoreSellingHotAdapter = new MoreSellingHotAdapter(mContext, mHotResult.getHot());
                mListView.setAdapter(mMoreSellingHotAdapter);
            } else {
                mMoreSellingHotAdapter.onDateChange(mHotResult.getHot());
            }
            i++;
        } else {
            //没有数据
            Toast.makeText(mContext, "暂无数据!", Toast.LENGTH_SHORT).show();
        }
    }

    private class ScrollListener implements AbsListView.OnScrollListener {

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            if (scrollState == SCROLL_STATE_IDLE && loadFinishFlag) {
                mPage = mPage + 1;
                if (mSize != 0) {
                    //getDataFromGetNeedList(String.valueOf(mMid), mCityId, String.valueOf(page), String.valueOf(pageSize), mSignInPwd);
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