package com.qiangyu.home.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.qiangyu.R;
import com.qiangyu.app.GoodsInfoActivity;
import com.qiangyu.base.BaseFragment;
import com.qiangyu.home.adapter.GoodsListAdapter;
import com.qiangyu.home.bean.GoodsList;
import com.qiangyu.home.bean.Hot;
import com.qiangyu.home.bean.JsonGoodsListBeanData;
import com.qiangyu.home.bean.JsonHotBeanData;
import com.qiangyu.utils.Constants;
import com.qiangyu.utils.MD5Util;
import com.qiangyu.utils.SPUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import okhttp3.Call;

@SuppressLint("ValidFragment")
public class SearchFragment extends BaseFragment {

    private final String search;
    private String TAG = "SearchFragment";
    private int i = 0;
    private ListView mListView;
    private String GOODS_BEAN = "goodsBean";
    private JsonHotBeanData.Result mHotResult;
    private List<GoodsList> mGoodsList;

    public SearchFragment(String search) {
        this.search = search;
    }

    @Override
    protected View initView() {
        if (i == 0) {
            Log.d(TAG, "initView: " + search);
            //getDataFromGetGoodsList(String shopId, String cid, String keys)
            String cityId = SPUtils.getInstance().getString("CityId");
            getDataFromGetGoodsList(cityId, "0", search);
        }
        View v = View.inflate(mContext, R.layout.list_view_more_selling, null);
        mListView = v.findViewById(R.id.lv_more_selling);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                GoodsList goodsList = mGoodsList.get(i);
                Toast.makeText(mContext, "点击的商品是<" + goodsList.getName() + ">", Toast.LENGTH_SHORT).show();
                String mChanpinId = goodsList.getId() + "";
                Intent intent = new Intent(mContext, GoodsInfoActivity.class);
                intent.putExtra(GOODS_BEAN, mChanpinId);
                mContext.startActivity(intent);
            }
        });
        return v;
    }

    //=================================================搜索商品=================================================\\
    /*
    /// <summary>
    ///   根据商品分类获取商品。
    ///    获取某个分类商品  Cid 填写对应cid。
    ///    获取全部商品   cid  填写0。
    ///    搜索时 填写 关键字 ； 不填写 返回 全部。
    /// </summary>
    /// <param name="shopId"> 门店标识</param>
    /// <param name="cid">商品分类标识</param>
    /// <param name="keys">搜索关键字(商品名称或商品代码)</param>
    */
    void getDataFromGetGoodsList(String shopId, String cid, String keys) {
        SortedMap<Object, Object> parameters = new TreeMap<>();
        String randomStr = MD5Util.getRandomStr();
        String timeStamp = MD5Util.getTimeStamp();
        String characterEncoding = "UTF-8";
        parameters.put("shopId", shopId);
        parameters.put("cid", cid);
        parameters.put("keys", keys);
        parameters.put("nonceStr", randomStr);
        parameters.put("timeStamp", timeStamp);
        String mySign = MD5Util.createSign(characterEncoding, parameters);

        OkHttpUtils
                .post()
                .url(Constants.QIANGYU_URL + "GetGoodsList")
                .addParams("shopId", shopId)
                .addParams("cid", cid)
                .addParams("keys", keys)
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
                        GetGoodsList(response);
                    }
                });
    }

    private void GetGoodsList(String json) {
        Log.d(TAG, "GetGoodsList: ");
        JsonGoodsListBeanData jsonGoodsListBeanData = JSON.parseObject(json, JsonGoodsListBeanData.class);
        mGoodsList = jsonGoodsListBeanData.getResult().getGoodsList();

        mListView.setAdapter(new GoodsListAdapter(mContext, mGoodsList));
    }

}