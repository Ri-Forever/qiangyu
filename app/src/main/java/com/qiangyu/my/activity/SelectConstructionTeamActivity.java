package com.qiangyu.my.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import com.alibaba.fastjson.JSON;
import com.qiangyu.R;
import com.qiangyu.my.adapter.ConstructionTeamAdapter;
import com.qiangyu.my.bean.JsonWantTeamsData;
import com.qiangyu.my.bean.WantTeams;
import com.qiangyu.utils.Constants;
import com.qiangyu.utils.Loading;
import com.qiangyu.utils.MD5Util;
import com.qiangyu.utils.MyListView;
import com.qiangyu.utils.SPUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

public class SelectConstructionTeamActivity extends Activity {

    private String TAG = "SelectConstructionTeamActivity";
    private Context mContext;
    private ImageButton mIb_back;
    private MyListView mLv_construction_team;
    //private Button mTv_queding;
    private String mWantNo;
    private int mMid;
    private String mSignInPwd;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mContext = this;
        setContentView(R.layout.activity_select_construction_team);
        //GetWantTeam(int shopId, string nonceStr, string timeStamp, string sign, int mid, string wantNo)
        mWantNo = getIntent().getStringExtra("wantNo");
        mMid = SPUtils.getInstance().getInt("mid");
        mSignInPwd = SPUtils.getInstance().getString("signInPwd");
        String cityId = SPUtils.getInstance().getString("CityId");
        getDataFromGetWantTeam(String.valueOf(mMid), cityId, mWantNo, mSignInPwd);
        initView();
    }

    private void initView() {
        mIb_back = findViewById(R.id.ib_back);
        //mTv_queding = findViewById(R.id.tv_queding);
        mLv_construction_team = findViewById(R.id.lv_construction_team);

        mIb_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
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
        List<WantTeams> wantTeams = jsonWantTeamsData.getResult().getWantTeams();
        mLv_construction_team.setAdapter(new ConstructionTeamAdapter(mContext,wantTeams));
    }
}
