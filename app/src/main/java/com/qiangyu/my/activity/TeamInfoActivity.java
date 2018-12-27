package com.qiangyu.my.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.qiangyu.R;
import com.qiangyu.my.adapter.CommentAdapter;
import com.qiangyu.my.bean.CommentList;
import com.qiangyu.my.bean.JsonCommentListData;
import com.qiangyu.my.bean.JsonTeamData;
import com.qiangyu.my.bean.MemberInfo;
import com.qiangyu.utils.Constants;
import com.qiangyu.utils.Loading;
import com.qiangyu.utils.MD5Util;
import com.qiangyu.utils.MyListView;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

public class TeamInfoActivity extends Activity {

    private String TAG = "TeamInfoActivity";
    private Context mContext;
    private ImageButton ibBack;
    private TextView tvMingcheng;
    private TextView tvFuzeren;
    private TextView tvDianhua;
    private TextView tvJianjie;
    TextView tvPinglun;
    private MyListView lvComments;
    private int startIndex;
    private int endIndex;
    private final int pageSize = 30;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mContext = this;
        setContentView(R.layout.activity_team_info);
        String tid = getIntent().getStringExtra("managerId");
        startIndex = 1;
        getDataFromGetTeamInfo(tid);
        getDataFromGetTeamComments(tid, String.valueOf(startIndex), String.valueOf(pageSize));
        initView();
    }

    private void initView() {
        ibBack = findViewById(R.id.ib_back);
        tvMingcheng = findViewById(R.id.tv_mingcheng);
        tvFuzeren = findViewById(R.id.tv_fuzeren);
        tvDianhua = findViewById(R.id.tv_dianhua);
        tvJianjie = findViewById(R.id.tv_jianjie);
        tvPinglun = findViewById(R.id.tv_pinglun);
        lvComments = findViewById(R.id.lv_comments);

        ibBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    //=================================================施工队详情=================================================\\
    void getDataFromGetTeamInfo(String tid) {
        SortedMap<Object, Object> parameters = new TreeMap<>();
        String randomStr = MD5Util.getRandomStr();
        String timeStamp = MD5Util.getTimeStamp();
        String characterEncoding = "UTF-8";
        parameters.put("tid", tid);
        parameters.put("nonceStr", randomStr);
        parameters.put("timeStamp", timeStamp);
        String mySign = MD5Util.createSign(characterEncoding, parameters);

        Log.d(TAG, "randomStr === " + randomStr + "---timeStamp === " + timeStamp + " ---- mySign ===" + mySign);
        OkHttpUtils
                .post()
                .url(Constants.QIANGYU_URL + "GetTeamInfo")
                .addParams("tid", tid)
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
                        processDataGetTeamInfo(response);
                    }
                });

    }

    private void processDataGetTeamInfo(String json) {
        Log.d(TAG, "ChooseTeam: " + json);
        JsonTeamData jsonTeamData = JSON.parseObject(json, JsonTeamData.class);
        MemberInfo memberInfo = jsonTeamData.getResult().getMemberInfo().get(0);
        tvMingcheng.setText("施工队名称:  " + memberInfo.getName());
        tvFuzeren.setText("负责人:  " + memberInfo.getTeamManager());
        tvDianhua.setText("电话:  " + memberInfo.getMobile());
        tvJianjie.setText("简介:  " + memberInfo.getDescription());

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
            tvPinglun.setVisibility(View.GONE);
            lvComments.setAdapter(new CommentAdapter(mContext, commentList));
        }
    }
}
