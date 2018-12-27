package com.qiangyu.shopcart.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;

import com.alibaba.fastjson.JSON;
import com.qiangyu.R;
import com.qiangyu.shopcart.adapter.RegionAdapter;
import com.qiangyu.shopcart.bean.AddrList;
import com.qiangyu.shopcart.bean.JsonAddrListData;
import com.qiangyu.utils.MD5Util;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

public class RegionActivity extends Activity {

    private String TAG = "MainActivity";
    private Context mContext;
    private ListView mLv_region1;
    private ListView mLv_region2;
    private ListView mLv_region3;
    private ListView mLv_region4;
    private String address;
    private String mAddress1;
    private String mAddress2;
    private String mAddress3;
    private String mAddress4;
    private ImageButton mIbBack;
    private String mAddress;
    private int mAddressId1;
    private int mAddressId2;
    private int mAddressId3;
    private int mAddressId4;
    private String dizhiId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAddress = getIntent().getStringExtra("Address");
        this.mContext = this;
        getDataFromGetDeliveryAddress("1");
        setContentView(R.layout.activity_region);
        initView();
    }

    private void initView() {
        mIbBack = findViewById(R.id.ib_back);
        mLv_region1 = findViewById(R.id.lv_region1);
        mLv_region2 = findViewById(R.id.lv_region2);
        mLv_region3 = findViewById(R.id.lv_region3);
        mLv_region4 = findViewById(R.id.lv_region4);

        mIbBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //返回
                Intent result = new Intent();
                result.putExtra("address", "");
                result.putExtra("shopId", "");
                setResult(1, result);
                finish();
                /*if ("NewAddressActivity".equals(mAddress)) {
                    Intent result = new Intent();
                    result.putExtra("address", "");
                    result.putExtra("shopId", "");
                    setResult(1, result);
                    finish();
                }
                if ("ModifyAddressActivity".equals(mAddress)) {
                    Intent result = new Intent();
                    result.putExtra("address", "");
                    result.putExtra("shopId", "");
                    setResult(1, result);
                    finish();
                }*/
            }
        });
    }

    //=================================================获取配送地址待选列表=================================================\\
    /*
    /// <param name="nonceStr"></param>
    /// <param name="timeStamp"></param>
    /// <param name="sign"></param>
    /// <param name="shopId"></param>
    */
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
                .url("http://www.51qiangyu.com.cn/QYApi/GetDeliveryAddress")
                .addParams("shopId", shopId)
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
                        GetDeliveryAddress(response);
                    }
                });

    }

    private void GetDeliveryAddress(String json) {
        //i("GetDeliveryAddress", json);
        JsonAddrListData jsonAddrListData = JSON.parseObject(json, JsonAddrListData.class);
        final List<AddrList> addrLists = jsonAddrListData.getResult().getAddrList();
        final List<AddrList> addrLists1 = new ArrayList<>();
        final List<AddrList> addrLists2 = new ArrayList<>();
        final List<AddrList> addrLists3 = new ArrayList<>();
        final List<AddrList> addrLists4 = new ArrayList<>();
        for (AddrList addrList : addrLists) {
            if (0 == addrList.getParentId()) {
                addrLists1.add(addrList);
            }
        }
        mLv_region1.setAdapter(new RegionAdapter(mContext, addrLists1));

        mLv_region1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mAddressId1 = addrLists1.get(position).getId();
                mAddress1 = addrLists1.get(position).getAddress();
                int shopId = addrLists1.get(position).getShopId();
                addrLists2.clear();
                for (AddrList addrList : addrLists) {
                    if (addrList.getParentId() == mAddressId1) {
                        addrLists2.add(addrList);
                    }
                }
                if (addrLists2.size() < 1) {
                    addrLists3.clear();
                    addrLists4.clear();
                    mLv_region3.setAdapter(new RegionAdapter(mContext, addrLists3));
                    mLv_region4.setAdapter(new RegionAdapter(mContext, addrLists4));
                    dizhiId = mAddressId1 + "";
                    Intent result = new Intent();
                    result.putExtra("dizhiId", dizhiId);
                    result.putExtra("address", address);
                    result.putExtra("shopId", String.valueOf(shopId));
                    setResult(1, result);
                    finish();
                    /*if ("NewAddressActivity".equals(mAddress)) {
                        Intent result = new Intent();
                        result.putExtra("dizhiId", dizhiId);
                        result.putExtra("address", address);
                        result.putExtra("shopId", String.valueOf(shopId));
                        setResult(1, result);
                        finish();
                    }
                    if ("ModifyAddressActivity".equals(mAddress)) {
                        Intent result = new Intent(mContext, NewAddressActivity.class);
                        result.putExtra("dizhiId", dizhiId);
                        result.putExtra("address", address);
                        result.putExtra("shopId", String.valueOf(shopId));
                        startActivityForResult(result, 5);
                        finish();
                    }*/
                }
                mLv_region2.setAdapter(new RegionAdapter(mContext, addrLists2));
            }
        });

        mLv_region2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mAddressId2 = addrLists2.get(position).getId();
                int shopId = addrLists2.get(position).getShopId();
                mAddress2 = addrLists2.get(position).getAddress();
                addrLists3.clear();
                for (AddrList addrList : addrLists) {
                    if (addrList.getParentId() == mAddressId2) {
                        addrLists3.add(addrList);
                    }
                }
                if (addrLists3.size() < 1) {
                    addrLists4.clear();
                    mLv_region4.setAdapter(new RegionAdapter(mContext, addrLists4));
                    address = mAddress1 + mAddress2;
                    dizhiId = mAddressId1 + "," + mAddressId2;
                    Intent result = new Intent();
                    result.putExtra("dizhiId", dizhiId);
                    result.putExtra("address", address);
                    result.putExtra("shopId", String.valueOf(shopId));
                    setResult(1, result);
                    finish();
                    /*if ("NewAddressActivity".equals(mAddress)) {
                        Intent result = new Intent(mContext, NewAddressActivity.class);
                        result.putExtra("dizhiId", dizhiId);
                        result.putExtra("address", address);
                        result.putExtra("shopId", String.valueOf(shopId));
                        startActivityForResult(result, 5);
                        finish();
                    }
                    if ("ModifyAddressActivity".equals(mAddress)) {
                        Intent result = new Intent(mContext, NewAddressActivity.class);
                        result.putExtra("dizhiId", dizhiId);
                        result.putExtra("address", address);
                        result.putExtra("shopId", String.valueOf(shopId));
                        startActivityForResult(result, 5);
                        finish();
                    }*/
                }
                mLv_region3.setAdapter(new RegionAdapter(mContext, addrLists3));
            }
        });

        mLv_region3.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mAddressId3 = addrLists3.get(position).getId();
                int shopId = addrLists3.get(position).getShopId();
                mAddress3 = addrLists3.get(position).getAddress();
                addrLists4.clear();
                for (AddrList addrList : addrLists) {
                    if (addrList.getParentId() == mAddressId3) {
                        addrLists4.add(addrList);
                    }
                }
                if (addrLists4.size() < 1) {
                    address = mAddress1 + mAddress2 + mAddress3;
                    dizhiId = mAddressId1 + "," + mAddressId2 + "," + mAddressId3;

                    Intent result = new Intent();
                    result.putExtra("dizhiId", dizhiId);
                    result.putExtra("address", address);
                    result.putExtra("shopId", String.valueOf(shopId));
                    setResult(1, result);
                    finish();
                    /*if ("NewAddressActivity".equals(mAddress)) {
                        Intent result = new Intent(mContext, NewAddressActivity.class);
                        result.putExtra("dizhiId", dizhiId);
                        result.putExtra("address", address);
                        result.putExtra("shopId", String.valueOf(shopId));
                        startActivityForResult(result, 5);
                        finish();
                    }
                    if ("ModifyAddressActivity".equals(mAddress)) {
                        Intent result = new Intent(mContext, NewAddressActivity.class);
                        result.putExtra("dizhiId", dizhiId);
                        result.putExtra("address", address);
                        result.putExtra("shopId", String.valueOf(shopId));
                        startActivityForResult(result, 5);
                        finish();
                    }*/
                }
                mLv_region4.setAdapter(new RegionAdapter(mContext, addrLists4));
            }
        });

        mLv_region4.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mAddressId4 = addrLists4.get(position).getId();
                int shopId = addrLists4.get(position).getShopId();
                mAddress4 = addrLists4.get(position).getAddress();
                address = mAddress1 + mAddress2 + mAddress3 + mAddress4;
                dizhiId = mAddressId1 + "," + mAddressId2 + "," + mAddressId3 + "," + mAddressId4;

                Intent result = new Intent();
                result.putExtra("dizhiId", dizhiId);
                result.putExtra("address", address);
                result.putExtra("shopId", String.valueOf(shopId));
                setResult(1, result);
                finish();
                /*if ("NewAddressActivity".equals(mAddress)) {
                    Intent result = new Intent(mContext, NewAddressActivity.class);
                    result.putExtra("dizhiId", dizhiId);
                    result.putExtra("address", address);
                    result.putExtra("shopId", String.valueOf(shopId));
                    startActivityForResult(result, 5);
                    finish();
                }
                if ("ModifyAddressActivity".equals(mAddress)) {
                    Intent result = new Intent(mContext, NewAddressActivity.class);
                    result.putExtra("dizhiId", dizhiId);
                    result.putExtra("address", address);
                    result.putExtra("shopId", String.valueOf(shopId));
                    startActivityForResult(result, 5);
                    finish();
                }*/
            }
        });
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
    }


}
