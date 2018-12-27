package com.qiangyu.home.fragment;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.alibaba.fastjson.JSON;
import com.qiangyu.R;
import com.qiangyu.app.GoodsInfoActivity;
import com.qiangyu.base.BaseFragment;
import com.qiangyu.home.adapter.GoodsListAdapter;
import com.qiangyu.home.bean.GoodsBean;
import com.qiangyu.home.bean.GoodsList;
import com.qiangyu.home.bean.JsonGoodsListBeanData;
import com.qiangyu.utils.Constants;
import com.qiangyu.utils.MD5Util;
import com.qiangyu.utils.SPUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.SortedMap;
import java.util.TreeMap;

import okhttp3.Call;

public class GoodsListFragment extends BaseFragment {
    private String cid;
    private ListView mLv_goods_list;
    private JsonGoodsListBeanData mJsonGoodsListBeanData;
    private JsonGoodsListBeanData.Result mResult;
    private String GOODS_BEAN = "goodsBean";

    public GoodsListFragment() {
    }

    @Override
    protected View initView() {
        cid = (String) getActivity().getIntent().getSerializableExtra("cid");
        Log.d("cid", "cid: " + cid);
        getDataFromHot(cid);
        View v = View.inflate(mContext, R.layout.activity_goodslist, null);
        mLv_goods_list = v.findViewById(R.id.lv_goods_list);
        mLv_goods_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                GoodsList goodsList = mResult.getGoodsList().get(position);

                String mChanpinId = goodsList.getId() + "";
                Intent intent = new Intent(mContext, GoodsInfoActivity.class);
                intent.putExtra(GOODS_BEAN, mChanpinId);
                mContext.startActivity(intent);

            }
        });
        return v;
    }


    //=================================================根据商品分类获取商品=================================================\\
    void getDataFromHot(String cid) {
        String cityId = SPUtils.getInstance().getString("CityId");
        final String cid1 = cid;
        SortedMap<Object, Object> parameters = new TreeMap<>();
        String randomStr = MD5Util.getRandomStr();
        String timeStamp = MD5Util.getTimeStamp();
        String characterEncoding = "UTF-8";
        parameters.put("shopId", cityId);
        parameters.put("cid", cid);
        parameters.put("nonceStr", randomStr);
        parameters.put("timeStamp", timeStamp);
        String mySign = MD5Util.createSign(characterEncoding, parameters);

        OkHttpUtils
                .post()
                .url(Constants.QIANGYU_URL + "GetGoodsList")
                .addParams("shopId", cityId)
                .addParams("cid", cid)
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
                        Log.d("getDataFromNet", "首页热卖请求失败 === >" + e.getMessage());
                        getDataFromHot(cid1);
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
                        Log.d("getDataFromNet", " 首页热卖 === >" + response);
                        processGoodsListData(response);
                    }
                });
    }

    private void processGoodsListData(String json) {
        Log.d("processGoodsListData", "processGoodsListData: " + json);
        mJsonGoodsListBeanData = JSON.parseObject(json, JsonGoodsListBeanData.class);
        mResult = mJsonGoodsListBeanData.getResult();
        mLv_goods_list.setAdapter(new GoodsListAdapter(mContext, mResult.getGoodsList()));
    }

}
