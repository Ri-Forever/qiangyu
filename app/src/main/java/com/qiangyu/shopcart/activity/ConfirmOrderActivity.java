package com.qiangyu.shopcart.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.qiangyu.R;
import com.qiangyu.home.bean.GoodsBean;
import com.qiangyu.payment.PayActivity;
import com.qiangyu.shopcart.adapter.ConfirmOrderViewAdapter;
import com.qiangyu.shopcart.bean.AddrList;
import com.qiangyu.shopcart.bean.CartList;
import com.qiangyu.shopcart.bean.JsonAddressListData;
import com.qiangyu.shopcart.bean.JsonCartBeanData;
import com.qiangyu.utils.Constants;
import com.qiangyu.utils.Loading;
import com.qiangyu.utils.MD5Util;
import com.qiangyu.utils.SPUtils;
import com.qiangyu.utils.ToastUtil;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

public class ConfirmOrderActivity extends Activity implements View.OnClickListener {
    //private TextView tvShangpinming;
    //private TextView tvGuigecanshu1;
    //private TextView tvGuigecanshu2;
    //private TextView tvJiage;
    //private TextView tvShuliang;
    private EditText etContent;
    private ImageButton ibDemandReleaseBack;
    private EditText etName;
    private EditText etPhone;
    private EditText etAddress;
    private Button btnSubmit;
    private List<GoodsBean> mGoodsBeanList;
    private ListView mLv_confirm_order;
    private Context mContext;
    private TextView mTv_zongjia;
    private double mZongjine;
    private JsonCartBeanData.Result mResult;
    private GoodsBean mGoodsBean;
    private String mOrderNumber;
    private String mZongjia;
    private TextView mTvShouhuodizhi;
    private LinearLayout mLl_moren;
    private TextView mTv_shouhuoren;
    private TextView mTv_shoujihao;
    private TextView mTv_dizhi;
    private String mContacts;
    private String mMobile;
    private String mLocation;
    private String mAddress;
    private String mType;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        mType = SPUtils.getInstance().getString("type");
        findViews();
    }

    @Override
    protected void onResume() {
        super.onResume();
        String signInPwd = SPUtils.getInstance().getString("signInPwd");
        String mid = String.valueOf(SPUtils.getInstance().getInt("mid"));
        String shopId = SPUtils.getInstance().getString("CityId");
        Log.d("shopId", "onCreate: " + shopId);
        getDataFromGetUserDeliveryAddress(shopId, mid, signInPwd);
    }

    /**
     * Find the Views in the layout<br />
     * <br />
     * Auto-created on 2018-08-26 14:27:02 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    private void findViews() {
        setContentView(R.layout.activity_confirm_order);
        ibDemandReleaseBack = findViewById(R.id.ib_demand_release_back);
        mTvShouhuodizhi = findViewById(R.id.tv_shouhuodizhi);
        etName = findViewById(R.id.et_name);
        etPhone = findViewById(R.id.et_phone);
        etAddress = findViewById(R.id.et_address);
        etContent = findViewById(R.id.et_content);
        btnSubmit = findViewById(R.id.btn_submit);
        mTv_zongjia = findViewById(R.id.tv_zongjia);
        mLv_confirm_order = findViewById(R.id.lv_confirm_order);
        mLl_moren = findViewById(R.id.ll_moren);
        mTv_shouhuoren = findViewById(R.id.tv_shouhuoren);
        mTv_shoujihao = findViewById(R.id.tv_shoujihao);
        mTv_dizhi = findViewById(R.id.tv_dizhi);
        //tvShangpinming = findViewById(R.id.tv_shangpinming);
        //tvGuigecanshu1 = findViewById(R.id.tv_guigecanshu1);
        //tvGuigecanshu2 = findViewById(R.id.tv_guigecanshu2);
        //tvJiage = findViewById(R.id.tv_jiage);
        //tvShuliang = findViewById(R.id.tv_shuliang);
        setData();
        ibDemandReleaseBack.setOnClickListener(this);
        mTvShouhuodizhi.setOnClickListener(this);
        btnSubmit.setOnClickListener(this);
        mLl_moren.setOnClickListener(this);
    }

    private void setData() {
        mGoodsBean = new GoodsBean();
        String signInPwd = SPUtils.getInstance().getString("signInPwd");
        String mid = String.valueOf(SPUtils.getInstance().getInt("mid"));
        String shopId = SPUtils.getInstance().getString("CityId");
        getDataFromCartList(shopId, mid, signInPwd);

        /*mGoodsBeanList = CartStorage.getInstance().getAllData();
        if (!mGoodsBeanList.isEmpty()) {
            mZongjine = getTotalPrice();
            mTv_zongjia.setText("￥ " + mZongjine);
            mLv_confirm_order.setAdapter(new ConfirmOrderViewAdapter(mContext, mGoodsBeanList));
        }*/
    }

    @Override
    public void onClick(View v) {
        if (v == ibDemandReleaseBack) {
            // Handle clicks for ibDemandReleaseBack
            finish();
        }
        if (v == mTvShouhuodizhi) {
            //添加或选择收货地址
            Intent intent = new Intent(mContext, ManagingReceiptAddressActivity.class);
            startActivity(intent);
        }
        if (v == btnSubmit) {
            // Handle clicks for btnSubmit
            List<CartList> cartList = mGoodsBean.getCartResult().getCartList();
            String[] skus = new String[cartList.size()];
            for (int i = 0; i < cartList.size(); i++) {
                skus[i] = String.valueOf(cartList.get(i).getSKU());
            }
            String remark = etContent.getText().toString();
            mZongjia = mTv_zongjia.getText().toString();
            String sku = Arrays.toString(skus);
            String signInPwd = SPUtils.getInstance().getString("signInPwd");
            String mid = String.valueOf(SPUtils.getInstance().getInt("mid"));
            String shopId = SPUtils.getInstance().getString("CityId");

            if (!StringUtils.isNotEmpty(mContacts)) {
                ToastUtil.toastCenter(mContext, "请先选择收货地址再提交订单!");
                return;
            }
            //getDataFromPlaceOrder(  shopId,   mid,  skus,  remark,  receiver,  mobile,  address,   signInPwd)
            if (!StringUtils.isNotEmpty(remark)) {
                getDataFromPlaceOrder(shopId, mid, sku, "", mContacts, mMobile, mLocation + mAddress, signInPwd);
            } else {
                getDataFromPlaceOrder(shopId, mid, sku, remark, mContacts, mMobile, mLocation + mAddress, signInPwd);
            }
        }
        if (v == mLl_moren) {
            //添加或选择收货地址
            Intent intent = new Intent(mContext, ManagingReceiptAddressActivity.class);
            startActivity(intent);
        }
    }

    private double getTotalPrice() {
        double totalPrice = 0.0;
        if (mGoodsBean != null && mGoodsBean.getCartResult().getCartList().size() > 0) {
            for (int i = 0; i < mGoodsBean.getCartResult().getCartList().size(); i++) {
                //GoodsBean goodsBean = mGoodsBeanList.get(i);
                CartList cartList = mGoodsBean.getCartResult().getCartList().get(i);
                if (cartList.isSelected()) {
                    if ("0".equals(mType)) {
                        totalPrice = totalPrice + Double.valueOf(cartList.getNum()) * Double.valueOf(cartList.getSalePrice2());
                    } else {
                        totalPrice = totalPrice + Double.valueOf(cartList.getNum()) * Double.valueOf(cartList.getSalePrice1());
                    }
                }
            }
        }
        return totalPrice;
    }

    //=================================================获取购物车=================================================\\
    /*
    int mid
    int shopId,
    string nonceStr,
    string timeStamp,
    string sign,
     */
    void getDataFromCartList(final String shopId, final String mid, final String signInPwd) {
        Loading.loading(mContext, "正在加载商品...");
        SortedMap<Object, Object> parameters = new TreeMap<>();
        String randomStr = MD5Util.getRandomStr();
        String timeStamp = MD5Util.getTimeStamp();
        String characterEncoding = "UTF-8";
        parameters.put("shopId", shopId);
        parameters.put("mid", mid);
        parameters.put("nonceStr", randomStr);
        parameters.put("timeStamp", timeStamp);
        String mySign = MD5Util.createSign(characterEncoding, parameters, signInPwd);

        Log.d("qianming", "randomStr === " + randomStr + "---timeStamp === " + timeStamp + " ---- mySign ===" + mySign + " --- mid --- " + mid);
        OkHttpUtils
                .post()
                .url(Constants.QIANGYU_URL + "GetCartList")
                .addParams("shopId", shopId)
                .addParams("mid", mid)
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
                        processDataCartList(response);
                        Loading.endLoad();
                    }
                });

    }

    private void processDataCartList(String json) {
        Log.d("processDataCartList", "processDataCartList: " + json);
        JsonCartBeanData jsonCartBeanData = JSON.parseObject(json, JsonCartBeanData.class);
        mResult = jsonCartBeanData.getResult();
        mGoodsBean.setCartResult(mResult);
        if (mGoodsBean.getCartResult().getCartList() != null) {
            mZongjine = getTotalPrice();
            mTv_zongjia.setText("总计:￥ " + mZongjine);
            mLv_confirm_order.setAdapter(new ConfirmOrderViewAdapter(mContext, mGoodsBean));
        }
    }

    //=================================================提交订单=================================================\\
    /*
    string nonceStr,
    string timeStamp,
    string sign,
    int shopId,
    int mid,
    string skus,
    string remark, 客户备注
    string receiver, 收货人
    string mobile, 收货人联系方式
    string address 收货地址
     */
    //getDataFromPlaceOrder(shopId, mid, sku, remark, mContacts, mMobile, mLocation + mAddress, signInPwd);
    void getDataFromPlaceOrder(final String shopId, final String mid, String skus, String remark, String receiver, String mobile, String address, final String signInPwd) {
        Loading.loading(mContext, "正在提交订单...");
        SortedMap<Object, Object> parameters = new TreeMap<>();
        String randomStr = MD5Util.getRandomStr();
        String timeStamp = MD5Util.getTimeStamp();
        String characterEncoding = "UTF-8";
        parameters.put("shopId", shopId);
        parameters.put("mid", mid);
        parameters.put("skus", skus);
        parameters.put("remark", remark);
        parameters.put("receiver", receiver);
        parameters.put("mobile", mobile);
        parameters.put("address", address);
        parameters.put("nonceStr", randomStr);
        parameters.put("timeStamp", timeStamp);
        String mySign = MD5Util.createSign(characterEncoding, parameters, signInPwd);

        Log.d("qianming", "randomStr === " + randomStr + "---timeStamp === " + timeStamp + " ---- mySign ===" + mySign + " --- mid --- " + mid);
        OkHttpUtils
                .post()
                .url(Constants.QIANGYU_URL + "PlaceOrder")
                .addParams("shopId", shopId)
                .addParams("mid", mid)
                .addParams("skus", skus)
                .addParams("remark", remark)
                .addParams("receiver", receiver)
                .addParams("mobile", mobile)
                .addParams("address", address)
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
                        processDataPlaceOrder(response);
                        Loading.endLoad();
                    }
                });

    }

    private void processDataPlaceOrder(String json) {
        Log.d("processDataPlaceOrder", "processDataPlaceOrder 提交订单 : " + json);
        JSONObject jsonObject = JSON.parseObject(json);
        String code = jsonObject.get("Code").toString().trim();
        if ("OK".equals(code)) {
            ToastUtil.toastCenter(mContext, "订单提交成功!");
            mOrderNumber = jsonObject.get("Result").toString();
            Intent intent = new Intent(mContext, PayActivity.class);
            intent.putExtra("orderNo", mOrderNumber);
            startActivity(intent);
            finish();
        } else {
            ToastUtil.toastCenter(mContext, "订单提交失败!");
        }
    }

    //=================================================获取用户的收货地址=================================================\\
    /*
    /// <param name="nonceStr"></param>
    /// <param name="timeStamp"></param>
    /// <param name="sign"></param>
    /// <param name="mid"></param>
    /// <param name="shopId"></param>
    */
    void getDataFromGetUserDeliveryAddress(final String shopId, final String mid, final String signInPwd) {
        SortedMap<Object, Object> parameters = new TreeMap<>();
        String randomStr = MD5Util.getRandomStr();
        String timeStamp = MD5Util.getTimeStamp();
        String characterEncoding = "UTF-8";
        parameters.put("shopId", shopId);
        parameters.put("mid", mid);
        parameters.put("nonceStr", randomStr);
        parameters.put("timeStamp", timeStamp);
        String mySign = MD5Util.createSign(characterEncoding, parameters, signInPwd);

        Log.d("qianming", "randomStr === " + randomStr + "---timeStamp === " + timeStamp + " ---- mySign ===" + mySign + " --- mid --- " + mid);
        OkHttpUtils
                .post()
                .url(Constants.QIANGYU_URL + "GetUserDeliveryAddress")
                .addParams("shopId", shopId)
                .addParams("mid", mid)
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
                        GetUserDeliveryAddress(response);
                    }
                });

    }

    private void GetUserDeliveryAddress(String json) {
        Log.d("processDataCartList", "processDataCartList: " + json);
        JsonAddressListData jsonAddressListData = JSON.parseObject(json, JsonAddressListData.class);
        final List<AddrList> addrLists = jsonAddressListData.getResult().getAddrList();
        //添加默认的收货地址
        for (AddrList addrList : addrLists) {
            String isDefault = addrList.getIsDefault();
            if ("true".equals(isDefault)) {
                mContacts = addrList.getContacts();
                mMobile = addrList.getMobile();
                mLocation = addrList.getLocation();
                mAddress = addrList.getAddress();
                mTv_shouhuoren.setText("收货人: " + mContacts);
                mTv_shoujihao.setText("手机号: " + mMobile);
                mTv_dizhi.setText("收货地址: " + mLocation + mAddress);
                mLl_moren.setVisibility(View.VISIBLE);
                mTvShouhuodizhi.setVisibility(View.GONE);
            }
        }
    }

    /*//=================================================获取配送地址待选列表=================================================\\
     *//*
    /// <param name="nonceStr"></param>
    /// <param name="timeStamp"></param>
    /// <param name="sign"></param>
    /// <param name="shopId"></param>
    *//*
    void getDataFromGetDeliveryAddress(final String shopId) {
        SortedMap<Object, Object> parameters = new TreeMap<>();
        String randomStr = MD5Util.getRandomStr();
        String timeStamp = MD5Util.getTimeStamp();
        String characterEncoding = "UTF-8";
        parameters.put("shopId", shopId);
        parameters.put("nonceStr", randomStr);
        parameters.put("timeStamp", timeStamp);
        String mySign = MD5Util.createSign(characterEncoding, parameters);

        OkHttpUtils
                .post()
                .url(Constants.QIANGYU_URL + "GetDeliveryAddress")
                .addParams("shopId", shopId)
                .addParams("nonceStr", randomStr)
                .addParams("timeStamp", timeStamp)
                .addParams("sign", mySign)
                .build()
                .readTimeOut(10000)
                .writeTimeOut(10000)
                .connTimeOut(10000)
                *//**
     * 请求失败的时候回调
     *//*
                .execute(new StringCallback() {
                    @Override
                    public void onError(okhttp3.Call call, Exception e, int id) {
                        Log.d("getDataFromNet", "请求失败 === >" + e.getMessage());
                        //getDataFromCartList(shopId, mid, signInPwd);
                    }

                    *//**
     * 当联网成功时回调
     * @param response
     * @param id
     *//*
                    @Override
                    public void onResponse(String response, int id) {
                        Log.d("getDataFromNet", " 未更改 === >" + response);
                        //解析数据
                        //response = response.replace("", "");
                        response = response.replace("\\", "");
                        response = response.replace("\"[", "[");
                        response = response.replace("]\"", "]");
                        Log.d("getDataFromNet", " 更改过 === >" + response);
                        GetDeliveryAddress(response);
                        Loading.endLoad();
                    }
                });

    }

    private void GetDeliveryAddress(String json) {
        i("GetDeliveryAddress", json);
        JsonAddrListData jsonAddrListData = JSON.parseObject(json, JsonAddrListData.class);
        List<AddrList> addrList = jsonAddrListData.getResult().getAddrList();
        //Log.d("GetDeliveryAddress", "GetDeliveryAddress: " + json);
    }

    public static void i(String tag, String msg) {  //信息太长,分段打印
        //因为String的length是字符数量不是字节数量所以为了防止中文字符过多，
        //  把4*1024的MAX字节打印长度改为2001字符数
        int max_str_length = 2001 - tag.length();
        //大于4000时
        while (msg.length() > max_str_length) {
            Log.i(tag, msg.substring(0, max_str_length));
            msg = msg.substring(max_str_length);
        }
        //剩余部分
        Log.i(tag, msg);
    }*/
}
