package com.qiangyu.my.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.qiangyu.R;
import com.qiangyu.my.adapter.CommodityAdapter;
import com.qiangyu.my.bean.OrderList;
import com.qiangyu.my.bean.WantDetail;
import com.qiangyu.utils.Constants;
import com.qiangyu.utils.MD5Util;
import com.qiangyu.utils.MyListView;
import com.qiangyu.utils.SPUtils;
import com.qiangyu.utils.ToastUtil;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.SortedMap;
import java.util.TreeMap;

import okhttp3.Call;

public class DemandEvaluateActivity extends Activity {

    private String TAG = "EvaluateActivity";
    private Context mContext;
    private ImageButton mIb_back;
    private MyListView mLl_commodity_evaluation;
    private RatingBar mRb_xingji;
    private EditText mEt_content;
    private Button mBtn_pingjia;
    private CheckBox mCheckbox_niming;
    private int i = 0;
    private WantDetail mWantDetail;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mContext = this;
        setContentView(R.layout.activity_evaluate);
        mWantDetail = (WantDetail) getIntent().getExtras().get("mWantDetail");
        initView();
    }

    private void initView() {
        mIb_back = findViewById(R.id.ib_back);
        mLl_commodity_evaluation = findViewById(R.id.ll_commodity_evaluation);
        mRb_xingji = findViewById(R.id.rb_xingji);
        mEt_content = findViewById(R.id.et_content);
        mBtn_pingjia = findViewById(R.id.btn_pingjia);
        mCheckbox_niming = findViewById(R.id.checkbox_niming);

        //mLl_commodity_evaluation.setAdapter(new CommodityAdapter(mContext, mOrderList));

        mCheckbox_niming.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    i = 1;
                } else {
                    i = 0;
                }
            }
        });
        mIb_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mBtn_pingjia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int mid = SPUtils.getInstance().getInt("mid");
                String signInPwd = SPUtils.getInstance().getString("signInPwd");
                String rating = String.valueOf((int) mRb_xingji.getRating());
                String wantNo = mWantDetail.getWantNo();
                String pinglun = mEt_content.getText().toString();
                getDataFromCommentWant(String.valueOf(mid), wantNo, rating, pinglun, "", String.valueOf(i), signInPwd);
            }
        });
    }

    //=================================================发布需求评价=================================================\\
    /*
    /// <param name="mid">会员 标识</param>
    /// <param name="wantNo">需求单</param>
    /// <param name="rate">星级</param>
    /// <param name="comment">评论</param>
    /// <param name="isShow">是否显示 1: 显示 0：不显示</param>
    /// <param name="picstr">图片列表，逗号与地址拼接成的</param>
     */
    void getDataFromCommentWant(String mid, String wantNo, String rate, String comment, String picstr, String isShow, String signInPwd) {
        SortedMap<Object, Object> parameters = new TreeMap<>();
        String randomStr = MD5Util.getRandomStr();
        String timeStamp = MD5Util.getTimeStamp();
        String characterEncoding = "UTF-8";
        parameters.put("mid", mid);
        parameters.put("rate", rate);
        parameters.put("comment", comment);
        parameters.put("picstr", picstr);
        parameters.put("isShow", isShow);
        parameters.put("wantNo", wantNo);
        parameters.put("nonceStr", randomStr);
        parameters.put("timeStamp", timeStamp);
        String mySign = MD5Util.createSign(characterEncoding, parameters, signInPwd);

        Log.d(TAG, "randomStr === " + randomStr + "---timeStamp === " + timeStamp + " ---- mySign ===" + mySign);
        OkHttpUtils
                .post()
                .url(Constants.QIANGYU_URL + "CommentWant")
                .addParams("mid", mid)
                .addParams("rate", rate)
                .addParams("comment", comment)
                .addParams("picstr", picstr)
                .addParams("isShow", isShow)
                .addParams("wantNo", wantNo)
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
                        ToastUtil.toastCenter(mContext,"网络请求失败请稍后再试...");
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
                        processDataCommentWant(response);
                    }
                });
    }

    private void processDataCommentWant(String json) {
        Log.d(TAG, "processDataCartList: " + json);
        JSONObject jsonObject = JSON.parseObject(json);
        String code = jsonObject.get("Code").toString();
        if ("OK".equals(code)) {
            Toast toast = Toast.makeText(mContext, "评价完成,非常感谢您的评价...", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            finish();
        } else {
            Toast toast = Toast.makeText(mContext, "评价失败: " + code, Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }
    }
}
