package com.qiangyu.shopcart.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.qiangyu.R;
import com.qiangyu.utils.Constants;
import com.qiangyu.utils.Loading;
import com.qiangyu.utils.MD5Util;
import com.qiangyu.utils.SPUtils;
import com.qiangyu.utils.ToastUtil;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.apache.commons.lang3.StringUtils;

import java.util.SortedMap;
import java.util.TreeMap;

public class NewAddressActivity extends Activity {

    private String TAG = "NewAddressActivity";
    private Context mContext;
    private ImageButton ibBack;
    private EditText etShouhuoren;
    private EditText etDianhua;
    private TextView tvQuyu;
    private EditText etDizhi;
    private RadioButton rbXuanze;
    private TextView tvBaocun;
    private String mAddress;
    private String mShopId;
    private String mSignInPwd;
    private String mMid;
    private String CityId;
    private String mDizhiId;
    private String isDefault = "0";//0是false,1是true

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mContext = this;
        mSignInPwd = SPUtils.getInstance().getString("signInPwd");
        mMid = String.valueOf(SPUtils.getInstance().getInt("mid"));
        CityId = SPUtils.getInstance().getString("CityId");
        setContentView(R.layout.new_address_activity);
        initView();
    }

    private void initView() {
        ibBack = findViewById(R.id.ib_back);
        etShouhuoren = findViewById(R.id.et_shouhuoren);
        etDianhua = findViewById(R.id.et_dianhua);
        tvQuyu = findViewById(R.id.tv_quyu);
        etDizhi = findViewById(R.id.et_dizhi);
        rbXuanze = findViewById(R.id.rb_xuanze);
        tvBaocun = findViewById(R.id.tv_baocun);

        ibBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        tvQuyu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //选择区域
                Intent intent = new Intent(mContext, RegionActivity.class);
                intent.putExtra("Address", "NewAddressActivity");
                startActivityForResult(intent, 1);
            }
        });
        rbXuanze.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //提交
                if (rbXuanze.isSelected()) {
                    rbXuanze.setSelected(false);
                    rbXuanze.setChecked(false);
                    isDefault = "0";//0是false,1是true
                    return;
                }
                if (!rbXuanze.isSelected()) {
                    rbXuanze.setSelected(true);
                    rbXuanze.setChecked(true);
                    isDefault = "1";//0是false,1是true
                }
            }
        });

        tvBaocun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                /// <param name="mid">会员标识</param>
                /// <param name="shopId">县区标识</param>
                /// <param name="daId">用户地址标识</param>
                /// <param name="contacts">联系人</param>
                /// <param name="title">称呼</param>
                /// <param name="mobile">手机号</param>
                /// <param name="locationIds">所属区域标识列表</param>
                /// <param name="location">所属区域文字</param>
                /// <param name="address">详细地址</param>
                /// <param name="isDefault">是否默认地址</param>
                /// <param name="sort">排序</param>
                新增时， daId 传 0，修改时，daId传当前修改的地址项的标识
                */
                String shouhuoren = etShouhuoren.getText().toString().trim();
                String dianhua = etDianhua.getText().toString().trim();
                String dizhi = etDizhi.getText().toString().trim();
                if (!StringUtils.isNotEmpty(shouhuoren)) {
                    ToastUtil.toastCenter(mContext, "姓名不能为空!");
                    return;
                }
                if (!StringUtils.isNotEmpty(dianhua)) {
                    ToastUtil.toastCenter(mContext, "电话不能为空!");
                    return;
                }
                if (dianhua.length() != 11) {
                    ToastUtil.toastCenter(mContext, "电话号码不正确!");
                    return;
                }
                if (!StringUtils.isNotEmpty(mAddress)) {
                    ToastUtil.toastCenter(mContext, "区域不能为空!");
                    return;
                }
                if (!StringUtils.isNotEmpty(dizhi)) {
                    ToastUtil.toastCenter(mContext, "地址不能为空!");
                    return;
                }
                Log.d(TAG, "mAddress: " + mAddress + " -- mShopId: " + mShopId + " -- mDizhiId: " + mDizhiId);
                getDataFromEditUserAddress(CityId, mMid, "0", shouhuoren, dianhua, mDizhiId, mAddress, dizhi, isDefault, mSignInPwd);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mAddress = data.getStringExtra("address");
        mShopId = data.getStringExtra("shopId");
        mDizhiId = data.getStringExtra("dizhiId");

        if (StringUtils.isNotEmpty(mAddress)) {
            tvQuyu.setText("所在区域: " + mAddress);
        } else {
            tvQuyu.setText("点击选择所在区域");
        }
    }

    //=================================================新增收货地址=================================================\\
    /*
    /// <param name="mid">会员标识</param>
    /// <param name="shopId">县区标识</param>
    /// <param name="daId">用户地址标识</param>
    /// <param name="contacts">联系人</param>
    /// <param name="mobile">手机号</param>
    /// <param name="locationIds">所属区域标识列表</param>
    /// <param name="location">所属区域文字</param>
    /// <param name="address">详细地址</param>
    /// <param name="isDefault">是否默认地址</param>
    */
    void getDataFromEditUserAddress(final String shopId, final String mid, String daId, String contacts, String mobile, String locationIds, String location, String address, String isDefault, final String signInPwd) {
        Loading.loading(mContext, "正在添加收货地址...");
        SortedMap<Object, Object> parameters = new TreeMap<>();
        String randomStr = MD5Util.getRandomStr();
        String timeStamp = MD5Util.getTimeStamp();
        String characterEncoding = "UTF-8";
        parameters.put("shopId", shopId);
        parameters.put("mid", mid);
        parameters.put("daId", daId);
        parameters.put("contacts", contacts);
        parameters.put("mobile", mobile);
        parameters.put("locationIds", locationIds);
        parameters.put("location", location);
        parameters.put("address", address);
        parameters.put("isDefault", isDefault);
        parameters.put("nonceStr", randomStr);
        parameters.put("timeStamp", timeStamp);
        String mySign = MD5Util.createSign(characterEncoding, parameters, signInPwd);

        Log.d("qianming", "randomStr === " + randomStr + "---timeStamp === " + timeStamp + " ---- mySign ===" + mySign + " --- mid --- " + mid);
        OkHttpUtils
                .post()
                .url(Constants.QIANGYU_URL + "EditUserAddress")
                .addParams("shopId", shopId)
                .addParams("mid", mid)
                .addParams("daId", daId)
                .addParams("contacts", contacts)
                .addParams("mobile", mobile)
                .addParams("locationIds", locationIds)
                .addParams("location", location)
                .addParams("address", address)
                .addParams("isDefault", isDefault)
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
                        ToastUtil.toastCenter(mContext,"网络连接失败,请稍后再试");
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
                        EditUserAddress(response);
                        Loading.endLoad();
                    }
                });

    }

    private void EditUserAddress(String json) {
        Log.d("processDataCartList", "processDataCartList: " + json);
        JSONObject jsonObject = JSON.parseObject(json);
        String code = jsonObject.get("Code").toString().trim();
        if ("OK".equals(code)) {
            ToastUtil.toastCenter(mContext, "新增地址成功!");
            finish();
        } else {
            ToastUtil.toastCenter(mContext, "新增地址失败!");
        }
    }

}
