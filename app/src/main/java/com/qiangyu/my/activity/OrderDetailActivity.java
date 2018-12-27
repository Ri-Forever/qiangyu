package com.qiangyu.my.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.qiangyu.R;
import com.qiangyu.im.ui.activity.ChatActivity;
import com.qiangyu.my.adapter.SingleOrderAdapter;
import com.qiangyu.my.bean.Designer;
import com.qiangyu.my.bean.JsonDesignerData;
import com.qiangyu.my.bean.JsonOrderDetailData;
import com.qiangyu.my.bean.OrderList;
import com.qiangyu.my.bean.PayInfo;
import com.qiangyu.utils.Constants;
import com.qiangyu.utils.Loading;
import com.qiangyu.utils.MD5Util;
import com.qiangyu.utils.MyListView;
import com.qiangyu.utils.SPUtils;
import com.qiangyu.utils.ToastUtil;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.apache.commons.lang3.StringUtils;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import okhttp3.Call;

public class OrderDetailActivity extends Activity {

    private String TAG = "OrderDetailActivity";
    private TextView mTv_dingdanhao;
    private TextView mTv_shijian;
    private TextView mTv_zhuangtai;
    private TextView mTv_lianxiren;
    private TextView mTv_dianhua;
    private TextView mTv_beizhu;
    private TextView mTv_zongjia;
    private TextView mTv_zhifu;
    private MyListView mMyListView;
    private ImageButton mIb_all_my_order_back;
    private Context mContext;
    private TextView mTv_dizhi;
    private LinearLayout mLl_daifenpei;
    private LinearLayout mLl_pingjia;
    private Button mBtn_pingjia;
    private LinearLayout mLl_shejishi;
    private TextView mTv_xingming;
    private TextView mTv_shejishidianhua;
    private TextView mTv_shejishiqq;
    private LinearLayout mLl_shouhuo;
    private Button mBtn_shouhuo;
    private OrderList mOrderList;
    private ImageView mIv_touxiang;
    private Designer mDesigner;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);
        mContext = this;
        String orderNo = getIntent().getStringExtra("orderNo");
        Log.d(TAG, "onCreate: " + orderNo);
        initView();
        getDataFromOrderDetail(orderNo);
    }

    private void initView() {
        mIb_all_my_order_back = findViewById(R.id.ib_all_my_order_back);
        mTv_dingdanhao = findViewById(R.id.tv_dingdanhao);
        mTv_shijian = findViewById(R.id.tv_shijian);
        mTv_zhuangtai = findViewById(R.id.tv_zhuangtai);
        mTv_lianxiren = findViewById(R.id.tv_lianxiren);
        mTv_dianhua = findViewById(R.id.tv_dianhua);
        mTv_dizhi = findViewById(R.id.tv_dizhi);
        mTv_beizhu = findViewById(R.id.tv_beizhu);
        mTv_zongjia = findViewById(R.id.tv_zongjia);
        mTv_zhifu = findViewById(R.id.tv_zhifu);
        mMyListView = findViewById(R.id.lv_order_list);
        mLl_daifenpei = findViewById(R.id.ll_daifenpei);
        mLl_pingjia = findViewById(R.id.ll_pingjia);
        mBtn_pingjia = findViewById(R.id.btn_pingjia);
        mLl_shejishi = findViewById(R.id.ll_shejishi);
        mTv_xingming = findViewById(R.id.tv_xingming);
        mTv_shejishidianhua = findViewById(R.id.tv_shejishidianhua);
        mTv_shejishiqq = findViewById(R.id.tv_shejishiqq);
        mLl_shouhuo = findViewById(R.id.ll_shouhuo);
        mBtn_shouhuo = findViewById(R.id.btn_shouhuo);
        mIv_touxiang = findViewById(R.id.iv_touxiang);

        mIb_all_my_order_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mBtn_pingjia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, EvaluateActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("mOrderList", mOrderList);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        mBtn_shouhuo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String orderNo = mOrderList.getOrderNo();
                int mid = SPUtils.getInstance().getInt("mid");
                String signInPwd = SPUtils.getInstance().getString("signInPwd");
                getDataFromGotoSign(String.valueOf(mid), orderNo, signInPwd);
            }
        });

        mIv_touxiang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //联系客服
                String code = mDesigner.getCode();
                if (StringUtils.isNotEmpty(code)) {
                    Intent intent = new Intent(mContext, ChatActivity.class);
                    intent.putExtra("kefu", code);
                    startActivity(intent);
                }
            }
        });
    }

    //=================================================获取订单详情=================================================\\
    void getDataFromOrderDetail(String orderNo) {
        SortedMap<Object, Object> parameters = new TreeMap<>();
        String randomStr = MD5Util.getRandomStr();
        String timeStamp = MD5Util.getTimeStamp();
        String characterEncoding = "UTF-8";
        parameters.put("orderNo", orderNo);
        parameters.put("nonceStr", randomStr);
        parameters.put("timeStamp", timeStamp);
        String mySign = MD5Util.createSign(characterEncoding, parameters);

        Log.d(TAG, "randomStr === " + randomStr + "---timeStamp === " + timeStamp + " ---- mySign ===" + mySign);
        OkHttpUtils
                .post()
                .url(Constants.QIANGYU_URL + "GetOrderDetail")
                .addParams("orderNo", orderNo)
                .addParams("nonceStr", randomStr)
                .addParams("timeStamp", timeStamp)
                .addParams("sign", mySign)
                .build()
                .readTimeOut(10000)
                .writeTimeOut(10000)
                .connTimeOut(10000)
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Log.d("getDataFromNet", "请求失败 === >" + e.getMessage());
                        Loading.endLoad();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Log.d("getDataFromNet", " 未更改 === >" + response);
                        //解析数据
                        //response = response.replace("", "");
                        response = response.replace("\\", "");
                        response = response.replace("\"[", "[");
                        response = response.replace("]\"", "]");
                        Log.d("getDataFromNet", " 更改过 === >" + response);
                        processDataOrderDetail(response);
                    }
                });
    }

    private void processDataOrderDetail(String json) {
        Log.d(TAG, "processDataCartList: " + json);
        JsonOrderDetailData jsonOrderDetailData = JSON.parseObject(json, JsonOrderDetailData.class);
        JsonOrderDetailData.Result orderDetailDataResult = jsonOrderDetailData.getResult();
        List<OrderList> orderListList = orderDetailDataResult.getOrderList();
        mOrderList = orderListList.get(0);
        List<PayInfo> payInfoList = orderDetailDataResult.getPayInfo();
        Log.d(TAG, "payInfoList: " + payInfoList.size());
        mMyListView.setAdapter(new SingleOrderAdapter(mContext, mOrderList));

        SimpleDateFormat SFDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String riqi = SFDate.format(mOrderList.getOrderDT());

        mTv_dingdanhao.setText("订单编号:   " + mOrderList.getOrderNo());
        mTv_shijian.setText("下单时间:   " + riqi);
        mTv_zhuangtai.setText("订单状态:   " + mOrderList.getStatusValue());
        mTv_lianxiren.setText("联系人:   " + mOrderList.getReceiver());
        mTv_dianhua.setText("电话:   " + mOrderList.getMobile());
        mTv_dizhi.setText("地址:   " + mOrderList.getAddress());
        mTv_beizhu.setText("备注:   " + mOrderList.getRemark());
        if (!payInfoList.isEmpty()) {
            mTv_zongjia.setText("订单总价: " + payInfoList.get(0).getTradeMoney_desc());
            mTv_zhifu.setText("支付信息: " + payInfoList.get(0).getTradeType_desc() + " --- " + payInfoList.get(0).getTradeMoney_desc());
        }
        switch (mOrderList.getStatus()) {
            case 0:
                //orderStatusDesc = "待支付";
                break;
            case 1:
                //orderStatusDesc = "已支付";
                break;
            case 2:
                //orderStatusDesc = "退款中";
                break;
            case 3:
                //orderStatusDesc = "退款中";
                break;
            case 4:
                //orderStatusDesc = "正在派送";
                break;
            case 5:
                //orderStatusDesc = "已签收";
                mLl_pingjia.setVisibility(View.VISIBLE);
                break;
            case 6:
                //orderStatusDesc = "已评价";
                mTv_zongjia.setText("订单结束");
                mTv_zhifu.setVisibility(View.GONE);
                break;
            case 7:
                //orderStatusDesc = "超时";
                mTv_zongjia.setText("订单超时");
                mTv_zhifu.setVisibility(View.GONE);
                break;
            case 8:
                //orderStatusDesc = "已取消";
                mTv_zongjia.setText("订单已取消...");
                mTv_zhifu.setVisibility(View.GONE);
                break;
            case 9:
                //orderStatusDesc = "已退款";
                break;
            case 10:
                //orderStatusDesc = "已接单";
                break;
            case 11:
                //orderStatusDesc = "异常订单";
                break;
            case 12:
                //orderStatusDesc = "已发货";
                mLl_shouhuo.setVisibility(View.VISIBLE);
                break;
            case 14:
                //orderStatusDesc = "待分配设计师";
                mLl_daifenpei.setVisibility(View.VISIBLE);
                break;
            case 16:
                //orderStatusDesc = "已分配设计师";
                mLl_shejishi.setVisibility(View.VISIBLE);
                mTv_xingming.setText(mOrderList.getDesignerName());
                mTv_shejishidianhua.setText("设计师电话:  " + mOrderList.getDesignMobile());
                mTv_shejishiqq.setText("设计师QQ:  " + mOrderList.getDesignQQ());
                String designerId = mOrderList.getDesignerId();
                //获取设计师信息
                getDataFromGetDesigner(designerId);
                break;
            case 17:
                //orderStatusDesc = "稿件设计完成";
                mTv_zongjia.setText("订单处理中...");
                mTv_zhifu.setVisibility(View.GONE);
                break;
            case 18:
                //orderStatusDesc = "制作中";
                mTv_zongjia.setText("订单制作中...");
                mTv_zhifu.setVisibility(View.GONE);
                break;
        }
    }

    //=================================================获取设计师信息=================================================\\
    /*
    /// <param name="designerId">设计师用户标识</param>
    /// <param name="nonceStr">随机字符串</param>
    /// <param name="timeStamp">时间戳</param>
    /// <param name="sign">签名</param>
    */
    void getDataFromGetDesigner(String designerId) {
        SortedMap<Object, Object> parameters = new TreeMap<>();
        String randomStr = MD5Util.getRandomStr();
        String timeStamp = MD5Util.getTimeStamp();
        String characterEncoding = "UTF-8";
        parameters.put("designerId", designerId);
        parameters.put("nonceStr", randomStr);
        parameters.put("timeStamp", timeStamp);
        String mySign = MD5Util.createSign(characterEncoding, parameters);

        Log.d(TAG, "randomStr === " + randomStr + "---timeStamp === " + timeStamp + " ---- mySign ===" + mySign);
        OkHttpUtils
                .post()
                .url(Constants.QIANGYU_URL + "GetDesigner")
                .addParams("designerId", designerId)
                .addParams("nonceStr", randomStr)
                .addParams("timeStamp", timeStamp)
                .addParams("sign", mySign)
                .build()
                .readTimeOut(10000)
                .writeTimeOut(10000)
                .connTimeOut(10000)
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Log.d("getDataFromNet", "请求失败 === >" + e.getMessage());
                        Loading.endLoad();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        //解析数据
                        response = response.replace("\\", "");
                        response = response.replace("\"[", "[");
                        response = response.replace("]\"", "]");
                        GetDesigner(response);
                    }
                });
    }

    private void GetDesigner(String json) {
        Log.d(TAG, "processDataCartList: " + json);
        JsonDesignerData jsonDesignerData = JSON.parseObject(json, JsonDesignerData.class);
        mDesigner = jsonDesignerData.getResult().getDesigner().get(0);

        String headPic = mDesigner.getHeadPic();
        if (StringUtils.isNotEmpty(headPic)) {
            Glide.with(mContext).load(Constants.QIANGYU_PIC_URL + headPic).diskCacheStrategy(DiskCacheStrategy.ALL).centerCrop().into(mIv_touxiang);
        }
    }

    //=================================================确认收货=================================================\\
    /*
    /// <param name="nonceStr"></param>
    /// <param name="timeStamp"></param>
    /// <param name="sign"></param>
    /// <param name="mid">会员标识</param>
    /// <param name="orderNo">订单号</param>
     */
    void getDataFromGotoSign(String mid, String orderNo, String signInPwd) {
        SortedMap<Object, Object> parameters = new TreeMap<>();
        String randomStr = MD5Util.getRandomStr();
        String timeStamp = MD5Util.getTimeStamp();
        String characterEncoding = "UTF-8";
        parameters.put("mid", mid);
        parameters.put("orderNo", orderNo);
        parameters.put("nonceStr", randomStr);
        parameters.put("timeStamp", timeStamp);
        String mySign = MD5Util.createSign(characterEncoding, parameters, signInPwd);

        Log.d(TAG, "randomStr === " + randomStr + "---timeStamp === " + timeStamp + " ---- mySign ===" + mySign);
        OkHttpUtils
                .post()
                .url(Constants.QIANGYU_URL + "GotoSign")
                .addParams("mid", mid)
                .addParams("orderNo", orderNo)
                .addParams("nonceStr", randomStr)
                .addParams("timeStamp", timeStamp)
                .addParams("sign", mySign)
                .build()
                .readTimeOut(10000)
                .writeTimeOut(10000)
                .connTimeOut(10000)
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Log.d("getDataFromNet", "请求失败 === >" + e.getMessage());
                        ToastUtil.toastCenter(mContext,"网络请求失败,请稍后再试...");
                        Loading.endLoad();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Log.d("getDataFromNet", " 未更改 === >" + response);
                        //解析数据
                        //response = response.replace("", "");
                        response = response.replace("\\", "");
                        response = response.replace("\"[", "[");
                        response = response.replace("]\"", "]");
                        Log.d("getDataFromNet", " 更改过 === >" + response);
                        processDataGotoSign(response);
                    }
                });
    }

    private void processDataGotoSign(String json) {
        Log.d(TAG, "processDataCartList: " + json);
        JSONObject jsonObject = JSON.parseObject(json);
        String code = jsonObject.get("Code").toString();
        if ("OK".equals(code)) {
            Toast toast = Toast.makeText(mContext, "您已签收...", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            finish();
        } else {
            String errmsg = jsonObject.get("errmsg").toString();
            Toast toast = Toast.makeText(mContext, "签收失败,失败原因: " + errmsg, Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }
    }
}

