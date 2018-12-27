package com.qiangyu.my.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.qiangyu.R;
import com.qiangyu.my.adapter.PictureAdapter;
import com.qiangyu.my.bean.JsonWantTeamsData;
import com.qiangyu.my.bean.JsonWantsData;
import com.qiangyu.my.bean.PictList;
import com.qiangyu.my.bean.WantDetail;
import com.qiangyu.my.bean.WantTeams;
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

public class DemandDetailedActivity extends Activity {

    private String TAG = "DemandDetailedActivity";
    private Context mContext;
    private ImageButton ibBack;
    private TextView tvBianhao;
    private TextView tvXingming;
    private TextView tvShoujihao;
    private TextView tvDizhi;
    private TextView tvFabushijian;
    private TextView tvBiaoti;
    private TextView tvNeirong;
    private MyListView lvTupian;
    private Button tvBaojia;
    private Button tvYibaojia;
    private WantDetail mWantDetail;
    private String mWantNo;
    private int mMid;
    private String mSignInPwd;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mContext = this;
        mWantNo = getIntent().getStringExtra("wantNo");
        mMid = SPUtils.getInstance().getInt("mid");
        mSignInPwd = SPUtils.getInstance().getString("signInPwd");
        //getDataFromGetWantDetail(final String mid, String shopId, String wantNo, final String signInPwd)
        String cityId = SPUtils.getInstance().getString("CityId");
        getDataFromGetWantDetail(String.valueOf(mMid), cityId, mWantNo, mSignInPwd);
        setContentView(R.layout.activity_demand_detailed);
        initView();
    }

    private void initView() {
        ibBack = findViewById(R.id.ib_back);
        tvBianhao = findViewById(R.id.tv_bianhao);
        tvXingming = findViewById(R.id.tv_xingming);
        tvShoujihao = findViewById(R.id.tv_shoujihao);
        tvDizhi = findViewById(R.id.tv_dizhi);
        tvFabushijian = findViewById(R.id.tv_fabushijian);
        tvBiaoti = findViewById(R.id.tv_biaoti);
        tvNeirong = findViewById(R.id.tv_neirong);
        lvTupian = findViewById(R.id.lv_tupian);
        tvBaojia = findViewById(R.id.tv_baojia);
        tvYibaojia = findViewById(R.id.tv_yibaojia);

        ibBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        tvBaojia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //报价
                final EditText inputServer = new EditText(mContext);
                inputServer.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle("请输入您的报价").setIcon(android.R.drawable.ic_dialog_info).setView(inputServer)
                        .setNegativeButton("取消", null);
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String info = inputServer.getText().toString().trim();
                        String cityId = SPUtils.getInstance().getString("CityId");
                        getDataFromOfferWant(String.valueOf(mMid), cityId, mWantNo, info, mSignInPwd);
                    }
                });
                builder.show();
            }
        });
        tvYibaojia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //已报价
                finish();
            }
        });
    }

    //=================================================需求明细=================================================\\
    void getDataFromGetWantDetail(final String mid, String shopId, String wantNo, final String signInPwd) {
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
        String cityId = SPUtils.getInstance().getString("CityId");
        getDataFromGetWantTeam(String.valueOf(mMid), cityId, mWantNo, mSignInPwd);
        JsonWantsData jsonWantsData = JSON.parseObject(json, JsonWantsData.class);
        mWantDetail = jsonWantsData.getResult().getWantDetail().get(0);
        List<PictList> pictList = jsonWantsData.getResult().getPictList();

        SimpleDateFormat SFDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String riqi = SFDate.format(mWantDetail.getPublicDT());//entity.getTranDate();

        tvBianhao.setText("编号:  " + mWantDetail.getWantNo());
        tvXingming.setText("姓名:  " + mWantDetail.getReceiver());
        tvShoujihao.setText("手机号:  " + mWantDetail.getMobile());
        tvDizhi.setText("地址:  " + mWantDetail.getAddress());
        tvFabushijian.setText("发布时间:  " + riqi);
        tvBiaoti.setText("需求标题:  " + mWantDetail.getTitle());
        tvNeirong.setText("需求内容:  " + mWantDetail.getWantInfo());

        lvTupian.setAdapter(new PictureAdapter(mContext, pictList));
    }

    //=================================================需求明细=================================================\\
    /*
    /// <param name="shopId"></param>
    /// <param name="mid">会员标识</param>
    /// <param name="wantNo">需求单号</param>
    /// <param name="info">报价信息</param>
     */
    void getDataFromOfferWant(String mid, String shopId, String wantNo, String info, String signInPwd) {
        SortedMap<Object, Object> parameters = new TreeMap<>();
        String randomStr = MD5Util.getRandomStr();
        String timeStamp = MD5Util.getTimeStamp();
        String characterEncoding = "UTF-8";
        parameters.put("mid", mid);
        parameters.put("shopId", shopId);
        parameters.put("wantNo", wantNo);
        parameters.put("info", info);
        parameters.put("nonceStr", randomStr);
        parameters.put("timeStamp", timeStamp);
        String mySign = MD5Util.createSign(characterEncoding, parameters, signInPwd);

        Log.d(TAG, "randomStr === " + randomStr + "---timeStamp === " + timeStamp + " ---- mySign ===" + mySign + " --- mid --- " + mid);
        OkHttpUtils
                .post()
                .url(Constants.QIANGYU_URL + "OfferWant")
                .addParams("mid", mid)
                .addParams("shopId", shopId)
                .addParams("wantNo", wantNo)
                .addParams("info", info)
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
                        response = response.replace("\\", "");
                        response = response.replace("\"[", "[");
                        response = response.replace("]\"", "]");
                        Log.d("getDataFromNet", " 更改过 === >" + response);
                        processDataOfferWant(response);
                    }
                });

    }

    private void processDataOfferWant(String json) {
        Log.d(TAG, "processDataCartList: " + json);
        JSONObject jsonObject = JSON.parseObject(json);
        String code = jsonObject.get("Code").toString();
        if ("OK".equals(code)) {
            ToastUtil.toastCenter(mContext, "报价成功!");
            finish();
        } else {
            String errmsg = jsonObject.get("errmsg").toString();
            ToastUtil.toastCenter(mContext, "报价失败,失败原因: " + errmsg);
        }
    }

    //=================================================获取报价施工队=================================================\\
    //GetWantTeam(int shopId, string nonceStr, string timeStamp, string sign, int mid, string wantNo)
    void getDataFromGetWantTeam(final String mid, String shopId, String wantNo, final String signInPwd) {
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
                .url(Constants.QIANGYU_URL + "GetWantTeam")
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
                        processDataGetWantTeam(response);
                    }
                });

    }

    private void processDataGetWantTeam(String json) {
        Log.d(TAG, "processDataCartList: " + json);
        JsonWantTeamsData jsonWantTeamsData = JSON.parseObject(json, JsonWantTeamsData.class);
        List<WantTeams> wantTeamsList = jsonWantTeamsData.getResult().getWantTeams();
        for (WantTeams wantTeams : wantTeamsList) {
            int managerId = wantTeams.getManagerId();
            if (mMid == managerId) {
                tvBaojia.setVisibility(View.GONE);
                tvYibaojia.setVisibility(View.VISIBLE);
                tvYibaojia.setText("已有 " + wantTeamsList.size() + " 人报价,您的报价: " + wantTeams.getInfo());
                return;
            }
        }
    }

}
