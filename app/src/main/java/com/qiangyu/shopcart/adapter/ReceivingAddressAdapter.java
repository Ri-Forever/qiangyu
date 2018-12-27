package com.qiangyu.shopcart.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.qiangyu.R;
import com.qiangyu.shopcart.activity.ModifyAddressActivity;
import com.qiangyu.shopcart.bean.AddrList;
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

public class ReceivingAddressAdapter extends BaseAdapter {

    private Context mContext;
    private List<AddrList> data;
    private final String mSignInPwd;
    private final String mMid;
    private int mXiabiao;
    private int mMorenxiabiao;
    private int mShezhimorenxiabiao;

    public ReceivingAddressAdapter(Context context, List<AddrList> addrList) {
        this.mContext = context;
        this.data = addrList;
        mSignInPwd = SPUtils.getInstance().getString("signInPwd");
        mMid = String.valueOf(SPUtils.getInstance().getInt("mid"));
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.activity_receiving_address, null);
            viewHolder = new ViewHolder();
            viewHolder.tvShouhuoren = convertView.findViewById(R.id.tv_shouhuoren);
            viewHolder.tvShoujihao = convertView.findViewById(R.id.tv_shoujihao);
            viewHolder.tvDizhi = convertView.findViewById(R.id.tv_dizhi);
            viewHolder.tvSheweimoren = convertView.findViewById(R.id.tv_sheweimoren);
            viewHolder.tvMoren = convertView.findViewById(R.id.tv_moren);
            viewHolder.tvBianji = convertView.findViewById(R.id.tv_bianji);
            viewHolder.tvShangchu = convertView.findViewById(R.id.tv_shangchu);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        final AddrList addrList = data.get(position);

        String isDefault = addrList.getIsDefault();
        if ("true".equals(isDefault)) {
            mMorenxiabiao = position;
            viewHolder.tvMoren.setVisibility(View.VISIBLE);
            viewHolder.tvSheweimoren.setVisibility(View.GONE);
        } else {
            viewHolder.tvMoren.setVisibility(View.GONE);
            viewHolder.tvSheweimoren.setVisibility(View.VISIBLE);
        }

        viewHolder.tvShouhuoren.setText("收货人: " + addrList.getContacts());
        viewHolder.tvShoujihao.setText("手机号: " + addrList.getMobile());
        viewHolder.tvDizhi.setText("收货地址: " + addrList.getLocation() + addrList.getAddress());

        viewHolder.tvSheweimoren.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //设置默认地址
                mShezhimorenxiabiao = position;
                int shopId = addrList.getShopId();
                int id1 = addrList.getId();
                String contacts = addrList.getContacts();
                String mobile = addrList.getMobile();
                String locationIds = addrList.getLocationIds();
                String location = addrList.getLocation();
                String address = addrList.getAddress();
                getDataFromEditUserAddress(String.valueOf(shopId), mMid, String.valueOf(id1), contacts, mobile, locationIds, location, address, "1", mSignInPwd);
            }
        });
        viewHolder.tvBianji.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //编辑地址
                Intent intent = new Intent(mContext, ModifyAddressActivity.class);
                String id = String.valueOf(addrList.getId());
                String contacts = addrList.getContacts();
                String mobile = addrList.getMobile();
                String location = addrList.getLocation();
                String address = addrList.getAddress();
                String Default = addrList.getIsDefault();
                String locationIds = addrList.getLocationIds();
                intent.putExtra("id", id);
                intent.putExtra("contacts", contacts);
                intent.putExtra("mobile", mobile);
                intent.putExtra("location", location);
                intent.putExtra("address", address);
                intent.putExtra("Default", Default);
                intent.putExtra("locationIds", locationIds);
                mContext.startActivity(intent);
            }
        });

        viewHolder.tvShangchu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //删除地址
                mXiabiao = position;
                String id = String.valueOf(addrList.getId());
                String shopId = String.valueOf(addrList.getShopId());
                getDataFromDeleteAddress(shopId, mMid, id, mSignInPwd);
            }
        });
        return convertView;
    }

    static class ViewHolder {
        private TextView tvSheweimoren;
        private TextView tvMoren;
        private TextView tvBianji;
        private TextView tvShangchu;
        private TextView tvShouhuoren;
        private TextView tvShoujihao;
        private TextView tvDizhi;
    }

    //=================================================删除收货地址=================================================\\
    /*
    /// <param name="shopId">县区标识。</param>
    /// <param name="mid">会员标识。</param>
    /// <param name="id">地址标识。</param>
    */
    void getDataFromDeleteAddress(final String shopId, final String mid, String id, final String signInPwd) {
        Loading.loading(mContext, "正在删除商品...");
        SortedMap<Object, Object> parameters = new TreeMap<>();
        String randomStr = MD5Util.getRandomStr();
        String timeStamp = MD5Util.getTimeStamp();
        String characterEncoding = "UTF-8";
        parameters.put("shopId", shopId);
        parameters.put("mid", mid);
        parameters.put("id", id);
        parameters.put("nonceStr", randomStr);
        parameters.put("timeStamp", timeStamp);
        String mySign = MD5Util.createSign(characterEncoding, parameters, signInPwd);

        Log.d("qianming", "randomStr === " + randomStr + "---timeStamp === " + timeStamp + " ---- mySign ===" + mySign + " --- mid --- " + mid);
        OkHttpUtils
                .post()
                .url(Constants.QIANGYU_URL + "DeleteAddress")
                .addParams("shopId", shopId)
                .addParams("mid", mid)
                .addParams("id", id)
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
                        ToastUtil.toastCenter(mContext, "网络连接失败,请稍后再试");
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
                        DeleteAddress(response);
                        Loading.endLoad();
                    }
                });

    }

    private void DeleteAddress(String json) {
        Log.d("processDataCartList", "processDataCartList: " + json);
        JSONObject jsonObject = JSON.parseObject(json);
        String code = jsonObject.get("Code").toString().trim();
        if ("OK".equals(code)) {
            data.remove(mXiabiao);
            notifyDataSetChanged();
            ToastUtil.toastCenter(mContext, "删除成功...");
        }
    }

    //=================================================设置默认收货收货地址=================================================\\
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
        Loading.loading(mContext, "正在修改收货地址...");
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
                        ToastUtil.toastCenter(mContext, "网络连接失败,请稍后再试");
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
            ToastUtil.toastCenter(mContext, "设置默认收货地址成功!");
            data.get(mShezhimorenxiabiao).setIsDefault("true");
            data.get(mMorenxiabiao).setIsDefault("false");
            Log.d("EditUserAddress", "mShezhimorenxiabiao: " + mShezhimorenxiabiao + " -- mMorenxiabiao: " + mMorenxiabiao);
            notifyDataSetChanged();
        } else {
            ToastUtil.toastCenter(mContext, "设置默认收货地址失败!");
        }
    }
}
