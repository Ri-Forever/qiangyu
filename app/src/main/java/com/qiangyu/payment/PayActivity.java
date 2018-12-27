package com.qiangyu.payment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.qiangyu.R;
import com.qiangyu.my.activity.PayPasswordActivity;
import com.qiangyu.my.bean.JsonMemberInfoData;
import com.qiangyu.my.bean.JsonOrderInfoData;
import com.qiangyu.my.bean.MemberInfo;
import com.qiangyu.my.bean.OrderInfo;
import com.qiangyu.utils.Constants;
import com.qiangyu.utils.Loading;
import com.qiangyu.utils.MD5Util;
import com.qiangyu.utils.SPUtils;
import com.qiangyu.utils.ToastUtil;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import io.github.mayubao.pay_library.PayAPI;
import io.github.mayubao.pay_library.WechatPayReq;

public class PayActivity extends Activity implements View.OnClickListener {

    private Context mContext;
    private StringBuffer xml;
    private final static String TAG = "PayActivity";
    private final static String PACKAGE = "Sign=WXPay";
    private final static String PARTNERID = "1513704831";
    private final static String APP_ID = "wxff5ae1ee22dc0c95";
    private final static String API_KEY = "1A4730879A90938FC85F0AD471A47308";

    private ImageButton ibDemandReleaseBack;
    private TextView tvDaojishi;
    private TextView tvBianhao;
    private TextView tvDingdanjine;
    private TextView tvYingfujine;
    private TextView tvHaixuzhifu;
    private TextView tvShengyuyue;
    private RadioButton rbYuezhifu;
    private RadioButton rbWeixinzhifu;
    private Button btnSubmit;
    private String mPayOrderNo;
    private TextView mTvYue;
    private int mMid;
    private String mSignInPwd;
    private double mOrderBill;
    private double mBalance;
    private String mOrderNo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mContext = this;

        //获取需求订单号
        mPayOrderNo = getIntent().getStringExtra("mPayOrderNo");
        //获取订单号
        mOrderNo = getIntent().getStringExtra("orderNo");

        String username = SPUtils.getInstance().getString("username");
        if (StringUtils.isNotEmpty(username)) {
            getDataFromNet(username);
        }

        Log.d(TAG, "mPayOrderNo: " + mPayOrderNo + " -- mOrderNumber: " + mOrderNo + " -- mZhifu: ");
        if (StringUtils.isNotEmpty(mOrderNo)) {
            //提取订单内容
            getDataGetOrderPayInfo(mOrderNo);
        }

        if (StringUtils.isNotEmpty(mPayOrderNo)) {
            //提取订单内容
            getDataGetOrderPayInfo(mPayOrderNo);
        }

        initView();
    }

    private void initView() {
        setContentView(R.layout.activity_pay);

        ibDemandReleaseBack = findViewById(R.id.ib_demand_release_back);
        tvDaojishi = findViewById(R.id.tv_daojishi);
        tvBianhao = findViewById(R.id.tv_bianhao);
        tvDingdanjine = findViewById(R.id.tv_dingdanjine);
        tvYingfujine = findViewById(R.id.tv_yingfujine);
        tvHaixuzhifu = findViewById(R.id.tv_haixuzhifu);
        tvShengyuyue = findViewById(R.id.tv_shengyuyue);
        rbYuezhifu = findViewById(R.id.rb_yuezhifu);
        rbWeixinzhifu = findViewById(R.id.rb_weixinzhifu);
        mTvYue = findViewById(R.id.tv_yue);
        btnSubmit = findViewById(R.id.btn_submit);

        ibDemandReleaseBack.setOnClickListener(this);
        rbYuezhifu.setOnClickListener(this);
        rbWeixinzhifu.setOnClickListener(this);
        btnSubmit.setOnClickListener(this);
    }

    /**
     * 判断微信客户端是否存在
     *
     * @return true安装, false未安装
     */
    public static boolean isWeChatAppInstalled(Context context) {

        IWXAPI wxapi = WXAPIFactory.createWXAPI(context, APP_ID);
        if (wxapi.isWXAppInstalled() && wxapi.isWXAppSupportAPI()) {
            return true;
        } else {
            final PackageManager packageManager = context.getPackageManager();// 获取packagemanager
            List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);// 获取所有已安装程序的包信息
            if (pinfo != null) {
                for (int i = 0; i < pinfo.size(); i++) {
                    String pn = pinfo.get(i).packageName;
                    if (pn.equalsIgnoreCase("com.tencent.mm")) {
                        return true;
                    }
                }
            }
            return false;
        }
    }

    @Override
    public void onClick(View v) {
        if (v == ibDemandReleaseBack) {
            // Handle clicks for ibDemandReleaseBack
            finish();
        } else if (v == rbYuezhifu) {
            // Handle clicks for rbYuezhifu
            rbWeixinzhifu.setChecked(false);
            //Toast.makeText(this, "余额支付!", Toast.LENGTH_SHORT).show();
        } else if (v == rbWeixinzhifu) {
            // Handle clicks for rbWeixinzhifu
            rbYuezhifu.setChecked(false);
            //Toast.makeText(this, "微信支付!", Toast.LENGTH_SHORT).show();
        } else if (v == btnSubmit) {
            // Handle clicks for btnSubmit
            if (rbYuezhifu.isChecked()) {
                Toast.makeText(this, "余额支付!", Toast.LENGTH_SHORT).show();
                /*
                /// <param name="mid">会员标识</param>
                /// <param name="orderNo">订单号</param>
                /// <param name="payType">支付方式； wxpay代表微信,bankpay 代表银行卡；alipay:代表支付宝; 单纯余额支付传空字符串；</param>
                /// <param name="balflag">是否使用余额支付；1:使用；0未使用
                ///   如果不使用余额支付：余额支付传空字符串，金额传 0；</param>
                /// <param name="paypwd">余额支付密码</param>
                /// <param name="costbal">支付金额</param>
                /// <returns>
                ///  prepayId 不为0 跳转到微信支付；
                ///</returns>
                */
                if (mBalance < mOrderBill) {
                    ToastUtil.toastCenter(mContext, "余额不足请选择其他支付方式");
                    return;
                }
                if (mBalance < mOrderBill) {
                    ToastUtil.toastCenter(mContext, "余额不足请选择其他支付方式");
                    return;
                }
                final EditText inputServer = new EditText(mContext);
                inputServer.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle("请输入支付密码完成支付").setIcon(android.R.drawable.ic_dialog_info).setView(inputServer)
                        .setNegativeButton("取消", null);
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        String paypwd = inputServer.getText().toString();
                        if (StringUtils.isNotEmpty(mOrderNo)) {
                            mMid = SPUtils.getInstance().getInt("mid");
                            mSignInPwd = SPUtils.getInstance().getString("signInPwd");
                            getDataFromPayOrder(String.valueOf(mMid), mOrderNo, "", "1", paypwd, String.valueOf(mOrderBill), mSignInPwd);
                        }
                        if (StringUtils.isNotEmpty(mPayOrderNo)) {
                            mMid = SPUtils.getInstance().getInt("mid");
                            mSignInPwd = SPUtils.getInstance().getString("signInPwd");
                            getDataFromPayOrder(String.valueOf(mMid), mPayOrderNo, "", "1", paypwd, String.valueOf(mOrderBill), mSignInPwd);
                        }
                    }
                });
                builder.show();
            }
            if (rbWeixinzhifu.isChecked()) {
                boolean weChatAppInstalled = isWeChatAppInstalled(mContext);
                if (!weChatAppInstalled) {
                    ToastUtil.toastCenter(mContext, "检测到您为安装微信,请安装微信后使用微信支付!");
                    return;
                }
                Toast.makeText(this, "微信支付!", Toast.LENGTH_SHORT).show();
                getDataFromWxPay(mOrderNo, String.valueOf((int) (mOrderBill * 100)), "抢鱼商城商品");
            }

        }
    }

    private void wxPayMent(String prepayId) {
        Log.d(TAG, "调用微信支付");
        SortedMap<Object, Object> parameters = new TreeMap<>();
        String randomStr = MD5Util.getRandomStr();
        String timeStamp = MD5Util.getTimeStamp();
        parameters.put("appid", APP_ID);
        parameters.put("partnerid", PARTNERID);
        parameters.put("prepayid", prepayId);
        parameters.put("package", PACKAGE);
        parameters.put("noncestr", randomStr);
        parameters.put("timestamp", timeStamp);
        String mySign = MD5Util.createSign(parameters, API_KEY);

        /*IWXAPI api = WXAPIFactory.createWXAPI(this, APP_ID);
        PayReq request = new PayReq();
        request.appId = APP_ID;
        request.partnerId = PARTNERID;
        request.prepayId = prepayId;
        request.packageValue = PACKAGE;
        request.nonceStr = randomStr;
        request.timeStamp = timeStamp;
        request.sign = mySign;
        api.sendReq(request);*/
        //1.创建微信支付请求
        WechatPayReq wechatPayReq = new WechatPayReq.Builder()
                .with(this) //activity实例
                .setAppId(APP_ID) //微信支付AppID
                .setPartnerId(PARTNERID)//微信支付商户号
                .setPrepayId(prepayId)//预支付码
                .setPackageValue(PACKAGE)//"Sign=WXPay"
                .setNonceStr(randomStr)//随机数
                .setTimeStamp(timeStamp)//时间戳
                .setSign(mySign)//签名
                .create();
        //2.发送微信支付请求
        PayAPI.getInstance().sendPayRequest(wechatPayReq);

    }

    //=================================================提交订单=================================================\\
    /*
    /// <param name="orderNo">订单编号</param>
    /// <returns>
    /// 订单支付信息
    /// </returns>
     */
    void getDataGetOrderPayInfo(String orderNo) {
        SortedMap<Object, Object> parameters = new TreeMap<>();
        String randomStr = MD5Util.getRandomStr();
        String timeStamp = MD5Util.getTimeStamp();
        String characterEncoding = "UTF-8";
        parameters.put("orderNo", orderNo);
        parameters.put("nonceStr", randomStr);
        parameters.put("timeStamp", timeStamp);
        String mySign = MD5Util.createSign(characterEncoding, parameters);

        Log.d("qianming", "randomStr === " + randomStr + "---timeStamp === " + timeStamp + " ---- mySign ===" + mySign);
        OkHttpUtils
                .post()
                .url(Constants.QIANGYU_URL + "GetOrderPayInfo")
                .addParams("orderNo", orderNo)
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
                        processDataGetOrderPayInfo(response);
                        //Loading.endLoad();
                    }
                });

    }

    private void processDataGetOrderPayInfo(String json) {
        Log.d(TAG, "processDataCartList: " + json);
        String code = JSON.parseObject(json).get("Code").toString();
        if ("OK".equals(code)) {
            JsonOrderInfoData jsonOrderInfoData = JSON.parseObject(json, JsonOrderInfoData.class);
            OrderInfo orderInfo = jsonOrderInfoData.getResult().getOrderInfo().get(0);
            mOrderBill = orderInfo.getOrderBill();
            String orderNo = orderInfo.getOrderNo();
            String payOrderNo = orderInfo.getPayOrderNo();
            if (StringUtils.isNotEmpty(orderNo)) {
                tvBianhao.setText(orderNo);
            }
            if (StringUtils.isNotEmpty(payOrderNo)) {
                tvBianhao.setText(payOrderNo);
            }
            tvDingdanjine.setText("￥ " + String.valueOf(mOrderBill));
            tvYingfujine.setText("￥ " + String.valueOf(mOrderBill));
            tvHaixuzhifu.setText("￥ " + String.valueOf(mOrderBill));
        } else {
            String errmsg = JSON.parseObject(json).get("errmsg").toString();
            ToastUtil.toastCenter(mContext, "支付信息错误,原因: " + errmsg);
            finish();
        }
    }

    //=================================================获取用户信息=================================================\\
    void getDataFromNet(final String username) {
        SortedMap<Object, Object> parameters = new TreeMap<>();
        String randomStr = MD5Util.getRandomStr();
        String timeStamp = MD5Util.getTimeStamp();
        String characterEncoding = "UTF-8";
        parameters.put("mobile", username);
        parameters.put("nonceStr", randomStr);
        parameters.put("timeStamp", timeStamp);
        String mySign = MD5Util.createSign(characterEncoding, parameters);

        OkHttpUtils
                .post()
                .url(Constants.QIANGYU_URL + "GetUserInfoByMobile")
                .addParams("mobile", username)
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
                        getDataFromNet(username);
                    }

                    /**
                     * 当联网成功时回调
                     * @param response
                     * @param id
                     */
                    @Override
                    public void onResponse(String response, int id) {
                        //解析数据
                        Log.d("getDataFromNet", " 未改过 === >" + response);
                        response = response.replace("\\", "");
                        response = response.replace("\"[", "[");
                        response = response.replace("]\"", "]");
                        Log.d("getDataFromNet", " 更改过 === >" + response);
                        processData1(response);
                    }
                });

    }

    private void processData1(String json) {
        JsonMemberInfoData jsonMemberInfoData = JSON.parseObject(json, JsonMemberInfoData.class);
        MemberInfo memberInfo = jsonMemberInfoData.getResult().getMemberInfo().get(0);
        mBalance = memberInfo.getBalance();
        mTvYue.setText(String.valueOf(mBalance));
    }

    //=================================================余额支付=================================================\\
    /*
    /// <param name="mid">会员标识</param>
    /// <param name="orderNo">订单号</param>
    /// <param name="payType">支付方式； wxpay代表微信,bankpay 代表银行卡；alipay:代表支付宝; 单纯余额支付传空字符串；</param>
    /// <param name="balflag">是否使用余额支付；1:使用；0未使用
    ///   如果不使用余额支付：余额支付传空字符串，金额传 0；</param>
    /// <param name="paypwd">余额支付密码</param>
    /// <param name="costbal">支付金额</param>
    /// <returns>
    ///  prepayId 不为0 跳转到微信支付；
    ///</returns>
     */
    void getDataFromPayOrder(String mid, String orderNo, String payType, String balflag, String paypwd, String costbal, String signInPwd) {
        SortedMap<Object, Object> parameters = new TreeMap<>();
        String randomStr = MD5Util.getRandomStr();
        String timeStamp = MD5Util.getTimeStamp();
        String characterEncoding = "UTF-8";
        parameters.put("mid", mid);
        parameters.put("orderNo", orderNo);
        parameters.put("payType", payType);
        parameters.put("balflag", balflag);
        parameters.put("paypwd", paypwd);
        parameters.put("costbal", costbal);
        parameters.put("nonceStr", randomStr);
        parameters.put("timeStamp", timeStamp);
        String mySign = MD5Util.createSign(characterEncoding, parameters, signInPwd);

        OkHttpUtils
                .post()
                .url(Constants.QIANGYU_URL + "PayOrder")
                .addParams("mid", mid)
                .addParams("orderNo", orderNo)
                .addParams("payType", payType)
                .addParams("balflag", balflag)
                .addParams("paypwd", paypwd)
                .addParams("costbal", costbal)
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
                    }

                    /**
                     * 当联网成功时回调
                     * @param response
                     * @param id
                     */
                    @Override
                    public void onResponse(String response, int id) {
                        //解析数据
                        Log.d("getDataFromNet", " 未改过 === >" + response);
                        response = response.replace("\\", "");
                        response = response.replace("\"[", "[");
                        response = response.replace("]\"", "]");
                        Log.d("getDataFromNet", " 更改过 === >" + response);
                        processDataPayOrder(response);
                    }
                });

    }

    private void processDataPayOrder(String json) {
        Log.d(TAG, "processDataPayOrder: " + json);
        JSONObject jsonObject = JSON.parseObject(json);
        String code = jsonObject.get("Code").toString();
        if ("OK".equals(code)) {
            ToastUtil.toastCenter(mContext, "支付成功...");
            finish();
        } else if ("401".equals(code)) {
            ToastUtil.toastCenter(mContext, "尚未设置支付密码,请先设置支付密码再支付...");
            Intent intent = new Intent(mContext,PayPasswordActivity.class);
            intent.putExtra("shezhimima","shezhimima");
            startActivity(intent);
        } else {
            String errmsg = jsonObject.get("errmsg").toString();
            ToastUtil.toastCenter(mContext, "支付失败,失败原因: " + errmsg);
        }
    }

    //=================================================微信支付获取prepayId=================================================\\
    /*
    WxPay(string orderNo, string bill, string orderDesc, string nonceStr, string timeStamp, string sign)
    */
    void getDataFromWxPay(String orderNo, String bill, String orderDesc) {
        SortedMap<Object, Object> parameters = new TreeMap<>();
        String randomStr = MD5Util.getRandomStr();
        String timeStamp = MD5Util.getTimeStamp();
        String characterEncoding = "UTF-8";
        parameters.put("orderNo", orderNo);
        parameters.put("bill", bill);
        parameters.put("orderDesc", orderDesc);
        parameters.put("nonceStr", randomStr);
        parameters.put("timeStamp", timeStamp);
        String mySign = MD5Util.createSign(characterEncoding, parameters);

        OkHttpUtils
                .post()
                .url(Constants.QIANGYU_URL + "WxPay")
                .addParams("orderNo", orderNo)
                .addParams("bill", bill)
                .addParams("orderDesc", orderDesc)
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
                    }

                    /**
                     * 当联网成功时回调
                     * @param response
                     * @param id
                     */
                    @Override
                    public void onResponse(String response, int id) {
                        //解析数据
                        Log.d("getDataFromNet", " 未改过 === >" + response);
                        response = response.replace("\\", "");
                        response = response.replace("\"[", "[");
                        response = response.replace("]\"", "]");
                        Log.d("getDataFromNet", " 更改过 === >" + response);
                        processDataWxPay(response);
                    }
                });

    }

    private void processDataWxPay(String json) {
        Log.d(TAG, "processDataPayOrder: " + json);
        JSONObject jsonObject = JSON.parseObject(json);
        String code = jsonObject.get("Code").toString();
        if ("OK".equals(code)) {
            JsonRootBean jsonRootBean = JSON.parseObject(json, JsonRootBean.class);
            String prepayId = jsonRootBean.getResult().getPrepayId();
            Log.d(TAG, "processDataWxPay: " + prepayId);
            wxPayMent(prepayId);
        } else {
            ToastUtil.toastCenter(mContext, "微信支付出错,请联系管理员或选择其他支付方式...");
        }
    }
}
