package com.qiangyu.shopcart.utils;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.qiangyu.app.MyApplication;
import com.qiangyu.home.bean.GoodsBean;
import com.qiangyu.utils.CacheUtils;
import com.qiangyu.utils.Constants;

import java.util.ArrayList;
import java.util.List;

public class CartStorage {

    public static final String JSON_CART = "json_cart";
    private static final String TAG = "CartStorage";
    private static CartStorage instance;
    private Context mContext;
    //SparseArray的性能优于HashMap
    private SparseArray<GoodsBean> mSparseArray;

    private CartStorage(Context context) {
        this.mContext = context;
        //把之前存储的数据读取出来
        mSparseArray = new SparseArray<>(100);
        listToSparseArray();
    }

    /**
     * 从本地读取到数据加入到SparseArray中
     */
    private void listToSparseArray() {
        List<GoodsBean> goodsBeansList = getAllData();
        //把List数据转换成SparseArray
        for (int i = 0; i < goodsBeansList.size(); i++) {
            GoodsBean goodsBean = goodsBeansList.get(i);
            mSparseArray.put(Integer.parseInt(goodsBean.getProduct_id()), goodsBean);
        }
    }

    /**
     * 获取本地所有的数据
     *
     * @return
     */
    public List<GoodsBean> getAllData() {
        List<GoodsBean> goodsBeanList = new ArrayList<>();
        //1.从本地获取
        String json = CacheUtils.getString(mContext, JSON_CART);
        //2.使用Gson转换成列表
        if (!TextUtils.isEmpty(json)) {
            //把String转换成List
            goodsBeanList = new Gson().fromJson(json, new TypeToken<List<GoodsBean>>() {
            }.getType());
        }
        return goodsBeanList;
    }

    //得到购物车示例
    // TODO: 2018/7/28
    public static CartStorage getInstance() {
        if (instance == null) {
            //MyApplication.getmContext()
            instance = new CartStorage(MyApplication.getmContext());
        }
        return instance;
    }

    /**
     * 添加数据
     *
     * @param goodsBean
     */
    public void addData(GoodsBean goodsBean) {
        //1.添加到内存中SparseArray,如果当前数据已存在,就修改number递增
        GoodsBean tempData = mSparseArray.get(Integer.parseInt(goodsBean.getSpecValues()));
        Log.d(TAG, "addData: " + tempData.getSpecValues());
        if (tempData.getSpecValues() != null) {
            tempData.setNumber(tempData.getNumber() + 1);
        } else {
            tempData = goodsBean;
            tempData.setNumber(1);
        }
        //同步到内存中
        mSparseArray.put(Integer.parseInt(goodsBean.getProduct_id()), tempData);
        //2.同步到本地
        saveLocal();
    }

    /**
     * 删除数据
     *
     * @param goodsBean
     */
    public void deleteData(GoodsBean goodsBean) {
        //1.内存中删除
        mSparseArray.delete(Integer.parseInt(goodsBean.getProduct_id()));
        //2.把内存的保存到本地
        saveLocal();
    }

    /**
     * 更新数据
     *
     * @param goodsBean
     */
    public void updateData(GoodsBean goodsBean) {
        //1.内存中更新
        mSparseArray.put(Integer.parseInt(goodsBean.getProduct_id()), goodsBean);
        //2.同步到本地
        saveLocal();
    }

    /**
     * 保存数据到本地
     */
    private void saveLocal() {
        //1.SparseArray转换成List
        List<GoodsBean> goodsBeanList = sparseToList();
        //2.把列表转换成String类型
        String json = new Gson().toJson(goodsBeanList);
        //3.把String数据保存
        CacheUtils.saveString(mContext, JSON_CART, json);
    }

    private List<GoodsBean> sparseToList() {
        List<GoodsBean> goodsBeanList = new ArrayList<>();
        for (int i = 0; i < mSparseArray.size(); i++) {
            GoodsBean goodsBean = mSparseArray.valueAt(i);
            goodsBeanList.add(goodsBean);
        }
        return goodsBeanList;
    }

}
