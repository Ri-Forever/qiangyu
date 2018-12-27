package com.qiangyu.my.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.qiangyu.R;
import com.qiangyu.my.adapter.CommentAdapter;
import com.qiangyu.my.adapter.PictureAdapter;
import com.qiangyu.my.bean.CommentList;
import com.qiangyu.my.bean.JsonCommentListData;
import com.qiangyu.my.bean.JsonWantsData;
import com.qiangyu.my.bean.PictList;
import com.qiangyu.my.bean.WantDetail;
import com.qiangyu.utils.Constants;
import com.qiangyu.utils.Loading;
import com.qiangyu.utils.MD5Util;
import com.qiangyu.utils.MyListView;
import com.qiangyu.utils.SPUtils;
import com.qiangyu.utils.ToastUtil;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

public class MyWantsActivity extends Activity implements View.OnClickListener {

    private String TAG = "MyWantsActivity";
    private Context mContext;
    private ImageButton ibAllMyOrderBack;
    private TextView tvDanhao;
    private TextView tvXingming;
    private TextView tvShouJihao;
    private TextView tvDizhi;
    private TextView tvFabushijian;
    private TextView tvBiaoti;
    private TextView tvZhuangtai;
    private TextView tvNeirong;
    private TextView tvPinglun;
    private MyListView lvTupian;
    private MyListView lvComments;
    private Button tvQuxiao;
    private Button tvXuanze;
    private Button tvQueren;
    private Button tvYanshou;
    private Button tvPingjia;
    private String mWantNo;
    private int mMid;
    private String mSignInPwd;
    private WantDetail mWantDetail;

    private LinearLayout llQueren;
    private TextView tvShigongdui;
    private TextView tvDianhua;
    private TextView tvFuzeren;
    private TextView tvBianhao;
    private TextView tvJine;
    private TextView tvRiqi;
    private List<PictList> mPictList;
    private String mManagerId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mContext = this;
        setContentView(R.layout.activity_my_wants);
        mWantNo = getIntent().getStringExtra("wantNo");
        mMid = SPUtils.getInstance().getInt("mid");
        mSignInPwd = SPUtils.getInstance().getString("signInPwd");
        //getDataFromGetWantDetail(final String mid, String shopId, String wantNo, final String signInPwd)
        String cityId = SPUtils.getInstance().getString("CityId");
        getDataFromGetWantDetail(String.valueOf(mMid), cityId, mWantNo, mSignInPwd);
        initView();
    }

    private void initView() {
        ibAllMyOrderBack = findViewById(R.id.ib_back);
        tvDanhao = findViewById(R.id.tv_danhao);
        tvXingming = findViewById(R.id.tv_xingming);
        tvShouJihao = findViewById(R.id.tv_shoujihao);
        tvDizhi = findViewById(R.id.tv_dizhi);
        tvFabushijian = findViewById(R.id.tv_fabushijian);
        tvBiaoti = findViewById(R.id.tv_biaoti);
        tvZhuangtai = findViewById(R.id.tv_zhuangtai);
        tvNeirong = findViewById(R.id.tv_neirong);
        lvTupian = findViewById(R.id.lv_tupian);
        tvPinglun = findViewById(R.id.tv_pinglun);
        lvComments = findViewById(R.id.lv_comments);

        llQueren = findViewById(R.id.ll_queren);
        tvShigongdui = findViewById(R.id.tv_shigongdui);
        tvDianhua = findViewById(R.id.tv_dianhua);
        tvFuzeren = findViewById(R.id.tv_fuzeren);
        tvBianhao = findViewById(R.id.tv_bianhao);
        tvJine = findViewById(R.id.tv_jine);
        tvRiqi = findViewById(R.id.tv_riqi);

        tvQuxiao = findViewById(R.id.tv_quxiao);
        tvXuanze = findViewById(R.id.tv_xuanze);
        tvQueren = findViewById(R.id.tv_queren);
        tvYanshou = findViewById(R.id.tv_yanshou);
        tvPingjia = findViewById(R.id.tv_pingjia);

        ibAllMyOrderBack.setOnClickListener(this);
        tvShigongdui.setOnClickListener(this);
        tvQuxiao.setOnClickListener(this);
        tvXuanze.setOnClickListener(this);
        tvQueren.setOnClickListener(this);
        tvYanshou.setOnClickListener(this);
        tvPingjia.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == ibAllMyOrderBack) {
            finish();
        } else if (v == tvShigongdui) {
            mManagerId = mWantDetail.getManagerId();
            Intent intent = new Intent(mContext, TeamInfoActivity.class);
            intent.putExtra("managerId", mManagerId);
            startActivity(intent);
        } else if (v == tvQuxiao) {
            String cityId = SPUtils.getInstance().getString("CityId");
            getDataFromCancelWant(String.valueOf(mMid), cityId, mWantNo, mSignInPwd);
        } else if (v == tvXuanze) {
            Intent intent = new Intent(mContext, SelectConstructionTeamActivity.class);
            intent.putExtra("wantNo", mWantDetail.getWantNo());
            startActivity(intent);
        } else if (v == tvQueren) {
            String cityId = SPUtils.getInstance().getString("CityId");
            getDataFromConfirContract(String.valueOf(mMid), cityId, mWantNo, mSignInPwd);
        } else if (v == tvYanshou) {
            String cityId = SPUtils.getInstance().getString("CityId");
            getDataFromConfirmWant(String.valueOf(mMid), cityId, mWantNo, mSignInPwd);
        } else if (v == tvPingjia) {
            //评价
            Intent intent = new Intent(mContext, DemandEvaluateActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("mWantDetail", mWantDetail);
            intent.putExtras(bundle);
            startActivity(intent);
        }
    }

    //=================================================需求明细=================================================\\
    void getDataFromGetWantDetail(String mid, String shopId, String wantNo, String signInPwd) {
        SortedMap<Object, Object> parameters = new TreeMap<>();
        String randomStr = MD5Util.getRandomStr();
        String timeStamp = MD5Util.getTimeStamp();
        String characterEncoding = "UTF-8";
        parameters.put("mid", mid);
        parameters.put("shopId", shopId);
        parameters.put("wantNo", wantNo);
        parameters.put("nonceStr", randomStr);
        parameters.put("timeStamp", timeStamp);
        String mySign = MD5Util.createSign(characterEncoding, parameters, signInPwd);

        Log.d(TAG, "randomStr === " + randomStr + "---timeStamp === " + timeStamp + " ---- mySign ===" + mySign + " --- mid --- " + mid);
        OkHttpUtils
                .post()
                .url(Constants.QIANGYU_URL + "GetWantDetail")
                .addParams("mid", mid)
                .addParams("shopId", shopId)
                .addParams("wantNo", wantNo)
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
                        processDataGetWantDetail(response);
                    }
                });

    }

    private void processDataGetWantDetail(String json) {
        Log.d(TAG, "processDataCartList: " + json);
        JsonWantsData jsonWantsData = JSON.parseObject(json, JsonWantsData.class);
        mWantDetail = jsonWantsData.getResult().getWantDetail().get(0);
        mPictList = jsonWantsData.getResult().getPictList();

        SimpleDateFormat SFDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String riqi = SFDate.format(mWantDetail.getPublicDT());
        String hetong = "";
        if (mWantDetail.getContractDT() != null) {
            hetong = SFDate.format(mWantDetail.getPublicDT());
        }

        tvDanhao.setText("单号:  " + mWantDetail.getWantNo());
        tvXingming.setText("姓名:  " + mWantDetail.getReceiver());
        tvDizhi.setText("地址:  " + mWantDetail.getAddress());
        tvShouJihao.setText("手机号:  " + mWantDetail.getMobile());
        tvFabushijian.setText("发布时间:  " + riqi);
        tvBiaoti.setText("需求标题:  " + mWantDetail.getTitle());
        tvZhuangtai.setText("当前状态:  " + mWantDetail.getStatus_Desc());
        tvNeirong.setText("需求内容:  " + mWantDetail.getWantInfo());


        switch (mWantDetail.getStatus()) {
            case 0:
                //等待审核
                break;
            case 1:
                //待确定施工队
                break;
            case 2:
                //审核失败
                break;
            case 3:
                //已确定施工队
                tvXuanze.setVisibility(View.GONE);
                break;
            case 4:
                //生成合同
                tvXuanze.setVisibility(View.GONE);
                llQueren.setVisibility(View.VISIBLE);
                tvQueren.setVisibility(View.VISIBLE);
                tvShigongdui.setText("施工队:  " + mWantDetail.getManagerName() + "  [点击查看施工队详细信息]");
                tvDianhua.setText("联系电话:  " + mWantDetail.getManagerMobile());
                tvFuzeren.setText("负责人:  " + mWantDetail.getManagerTeamManager());
                tvBianhao.setText("合同编号:  " + mWantDetail.getContractNo());
                tvJine.setText("合同金额:  " + mWantDetail.getContractBill());
                tvRiqi.setText("合同日期:  " + hetong);
                break;
            case 5:
                //已完成
                llQueren.setVisibility(View.VISIBLE);
                tvPingjia.setVisibility(View.VISIBLE);
                tvXuanze.setVisibility(View.GONE);
                tvQueren.setVisibility(View.GONE);
                tvQuxiao.setVisibility(View.GONE);
                tvShigongdui.setText("施工队:  " + mWantDetail.getManagerName() + "  [点击查看施工队详细信息]");
                tvDianhua.setText("联系电话:  " + mWantDetail.getManagerMobile());
                tvFuzeren.setText("负责人:  " + mWantDetail.getManagerTeamManager());
                tvBianhao.setText("合同编号:  " + mWantDetail.getContractNo());
                tvJine.setText("合同金额:  " + mWantDetail.getContractBill());
                tvRiqi.setText("合同日期:  " + hetong);
                break;
            case 6:
                //已评价
                llQueren.setVisibility(View.VISIBLE);
                tvPinglun.setVisibility(View.VISIBLE);
                tvPingjia.setVisibility(View.GONE);
                tvXuanze.setVisibility(View.GONE);
                tvQueren.setVisibility(View.GONE);
                tvQuxiao.setVisibility(View.GONE);
                tvShigongdui.setText("施工队:  " + mWantDetail.getManagerName() + "  [点击查看施工队详细信息]");
                tvDianhua.setText("联系电话:  " + mWantDetail.getManagerMobile());
                tvFuzeren.setText("负责人:  " + mWantDetail.getManagerTeamManager());
                tvBianhao.setText("合同编号:  " + mWantDetail.getContractNo());
                tvJine.setText("合同金额:  " + mWantDetail.getContractBill());
                tvRiqi.setText("合同日期:  " + hetong);
                getDataFromGetTeamComments(mWantDetail.getManagerId(), "1", "30");
                break;
            case 7:

                break;
            case 8:
                //已取消
                tvQuxiao.setVisibility(View.GONE);
                tvXuanze.setVisibility(View.GONE);
                break;
            case 9:
                //异常
                break;
            case 10:
                //用户确认合同
                llQueren.setVisibility(View.VISIBLE);
                tvXuanze.setVisibility(View.GONE);
                tvQueren.setVisibility(View.GONE);
                tvQuxiao.setVisibility(View.GONE);
                tvShigongdui.setText("施工队:  " + mWantDetail.getManagerName() + "  [点击查看施工队详细信息]");
                tvDianhua.setText("联系电话:  " + mWantDetail.getManagerMobile());
                tvFuzeren.setText("负责人:  " + mWantDetail.getManagerTeamManager());
                tvBianhao.setText("合同编号:  " + mWantDetail.getContractNo());
                tvJine.setText("合同金额:  " + mWantDetail.getContractBill());
                tvRiqi.setText("合同日期:  " + hetong);
                break;
            case 11:
                //待验收
                llQueren.setVisibility(View.VISIBLE);
                tvYanshou.setVisibility(View.VISIBLE);
                tvXuanze.setVisibility(View.GONE);
                tvQueren.setVisibility(View.GONE);
                tvQuxiao.setVisibility(View.GONE);
                tvShigongdui.setText("施工队:  " + mWantDetail.getManagerName() + "  [点击查看施工队详细信息]");
                tvDianhua.setText("联系电话:  " + mWantDetail.getManagerMobile());
                tvFuzeren.setText("负责人:  " + mWantDetail.getManagerTeamManager());
                tvBianhao.setText("合同编号:  " + mWantDetail.getContractNo());
                tvJine.setText("合同金额:  " + mWantDetail.getContractBill());
                tvRiqi.setText("合同日期:  " + hetong);
                break;
            case 12:
                break;
            case 13:
                //合同确认,施工中";//对应 施工队  待支付
                llQueren.setVisibility(View.VISIBLE);
                tvXuanze.setVisibility(View.GONE);
                tvQueren.setVisibility(View.GONE);
                tvQuxiao.setVisibility(View.GONE);
                tvShigongdui.setText("施工队:  " + mWantDetail.getManagerName() + "  [点击查看施工队详细信息]");
                tvDianhua.setText("联系电话:  " + mWantDetail.getManagerMobile());
                tvFuzeren.setText("负责人:  " + mWantDetail.getManagerTeamManager());
                tvBianhao.setText("合同编号:  " + mWantDetail.getContractNo());
                tvJine.setText("合同金额:  " + mWantDetail.getContractBill());
                tvRiqi.setText("合同日期:  " + hetong);
                break;
        }
        lvTupian.setAdapter(new PictureAdapter(mContext, mPictList));
    }

    //=================================================取消需求订单订单=================================================\\
    void getDataFromCancelWant(String mid, String shopId, String wantNo, String signInPwd) {
        SortedMap<Object, Object> parameters = new TreeMap<>();
        String randomStr = MD5Util.getRandomStr();
        String timeStamp = MD5Util.getTimeStamp();
        String characterEncoding = "UTF-8";
        parameters.put("mid", mid);
        parameters.put("shopId", shopId);
        parameters.put("wantNo", wantNo);
        parameters.put("nonceStr", randomStr);
        parameters.put("timeStamp", timeStamp);
        String mySign = MD5Util.createSign(characterEncoding, parameters, signInPwd);

        Log.d(TAG, "randomStr === " + randomStr + "---timeStamp === " + timeStamp + " ---- mySign ===" + mySign + " --- mid --- " + mid);
        OkHttpUtils
                .post()
                .url(Constants.QIANGYU_URL + "CancelWant")
                .addParams("mid", mid)
                .addParams("shopId", shopId)
                .addParams("wantNo", wantNo)
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
                        processDataCancelWant(response);
                    }
                });

    }

    private void processDataCancelWant(String json) {
        Log.d(TAG, "processDataCartList: " + json);
        JSONObject jsonObject = JSON.parseObject(json);
        String code = jsonObject.get("Code").toString();
        if ("OK".equals(code)) {
            ToastUtil.toastCenter(mContext, "需求订单取消成功...");
            finish();
        } else {
            String errmsg = jsonObject.get("errmsg").toString();
            ToastUtil.toastCenter(mContext, "需求订单取消失败,失败原因: " + errmsg);
        }
    }

    //=================================================确认合同=================================================\\
    void getDataFromConfirContract(String mid, String shopId, String wantNo, String signInPwd) {
        SortedMap<Object, Object> parameters = new TreeMap<>();
        String randomStr = MD5Util.getRandomStr();
        String timeStamp = MD5Util.getTimeStamp();
        String characterEncoding = "UTF-8";
        parameters.put("mid", mid);
        parameters.put("shopId", shopId);
        parameters.put("wantNo", wantNo);
        parameters.put("nonceStr", randomStr);
        parameters.put("timeStamp", timeStamp);
        String mySign = MD5Util.createSign(characterEncoding, parameters, signInPwd);

        Log.d(TAG, "randomStr === " + randomStr + "---timeStamp === " + timeStamp + " ---- mySign ===" + mySign + " --- mid --- " + mid);
        OkHttpUtils
                .post()
                .url(Constants.QIANGYU_URL + "ConfirContract")
                .addParams("mid", mid)
                .addParams("shopId", shopId)
                .addParams("wantNo", wantNo)
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
                        processDataConfirContract(response);
                    }
                });

    }

    private void processDataConfirContract(String json) {
        Log.d(TAG, "processDataCartList: " + json);
        JSONObject jsonObject = JSON.parseObject(json);
        String code = jsonObject.get("Code").toString();
        if ("OK".equals(code)) {
            ToastUtil.toastCenter(mContext, "提交合同成功...");
            finish();
        } else {
            String errmsg = jsonObject.get("errmsg").toString();
            ToastUtil.toastCenter(mContext, "提交合同失败,失败原因: " + errmsg);
        }
    }

    //=================================================施工验收确认=================================================\\
    /*
    /// <param name="shopId"></param>
    /// <param name="mid"></param>
    /// <param name="wantNo"></param>
     */
    void getDataFromConfirmWant(String mid, String shopId, String wantNo, String signInPwd) {
        SortedMap<Object, Object> parameters = new TreeMap<>();
        String randomStr = MD5Util.getRandomStr();
        String timeStamp = MD5Util.getTimeStamp();
        String characterEncoding = "UTF-8";
        parameters.put("mid", mid);
        parameters.put("shopId", shopId);
        parameters.put("wantNo", wantNo);
        parameters.put("nonceStr", randomStr);
        parameters.put("timeStamp", timeStamp);
        String mySign = MD5Util.createSign(characterEncoding, parameters, signInPwd);

        Log.d(TAG, "randomStr === " + randomStr + "---timeStamp === " + timeStamp + " ---- mySign ===" + mySign + " --- mid --- " + mid);
        OkHttpUtils
                .post()
                .url(Constants.QIANGYU_URL + "ConfirmWant")
                .addParams("mid", mid)
                .addParams("shopId", shopId)
                .addParams("wantNo", wantNo)
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
                        processDataConfirmWant(response);
                    }
                });

    }

    private void processDataConfirmWant(String json) {
        Log.d(TAG, "processDataCartList: " + json);
        JSONObject jsonObject = JSON.parseObject(json);
        String code = jsonObject.get("Code").toString();
        if ("OK".equals(code)) {
            ToastUtil.toastCenter(mContext, "施工验收成功...");
            finish();
        } else {
            String errmsg = jsonObject.get("errmsg").toString();
            ToastUtil.toastCenter(mContext, "验收失败,失败原因: " + errmsg);
        }
    }

    //=================================================施工队评价=================================================\\
    void getDataFromGetTeamComments(String tid, String page, String pageSize) {
        SortedMap<Object, Object> parameters = new TreeMap<>();
        String randomStr = MD5Util.getRandomStr();
        String timeStamp = MD5Util.getTimeStamp();
        String characterEncoding = "UTF-8";
        parameters.put("tid", tid);
        parameters.put("page", page);
        parameters.put("pageSize", pageSize);
        parameters.put("nonceStr", randomStr);
        parameters.put("timeStamp", timeStamp);
        String mySign = MD5Util.createSign(characterEncoding, parameters);

        Log.d(TAG, "randomStr === " + randomStr + "---timeStamp === " + timeStamp + " ---- mySign ===" + mySign);
        OkHttpUtils
                .post()
                .url(Constants.QIANGYU_URL + "GetTeamComments")
                .addParams("tid", tid)
                .addParams("page", page)
                .addParams("pageSize", pageSize)
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
                        processDataGetTeamComments(response);
                    }
                });

    }

    private void processDataGetTeamComments(String json) {
        Log.d(TAG, "ChooseTeam: " + json);
        JsonCommentListData jsonCommentListData = JSON.parseObject(json, JsonCommentListData.class);
        List<CommentList> commentList = jsonCommentListData.getResult().getCommentList();
        if (commentList != null && commentList.size() > 0) {
            lvComments.setAdapter(new CommentAdapter(mContext, commentList));
        }
    }
}
