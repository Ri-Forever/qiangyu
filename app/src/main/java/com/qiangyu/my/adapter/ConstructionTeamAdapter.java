package com.qiangyu.my.adapter;

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
import com.qiangyu.my.activity.SelectConstructionTeamActivity;
import com.qiangyu.my.activity.TeamInfoActivity;
import com.qiangyu.my.bean.WantTeams;
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

public class ConstructionTeamAdapter extends BaseAdapter {

    private String TAG = "ConstructionTeamAdapter";
    private Context mContext;
    private final List<WantTeams> data;

    public ConstructionTeamAdapter(Context context, List<WantTeams> wantTeams) {
        this.mContext = context;
        this.data = wantTeams;
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
        final ViewHolder viewHolder;
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.activity_construction_team, null);
            viewHolder = new ViewHolder();
            //viewHolder.checkboxAll = convertView.findViewById(R.id.checkbox_all);
            viewHolder.tvMingzi = convertView.findViewById(R.id.tv_mingzi);
            viewHolder.tvDianhua = convertView.findViewById(R.id.tv_dianhua);
            viewHolder.tvBaojia = convertView.findViewById(R.id.tv_baojia);
            viewHolder.tvRiqi = convertView.findViewById(R.id.tv_riqi);
            viewHolder.tvChakan = convertView.findViewById(R.id.tv_chakan);
            viewHolder.tvXuanze = convertView.findViewById(R.id.tv_xuanze);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        final WantTeams wantTeams = data.get(position);

        viewHolder.tvMingzi.setText("施工队:  " + wantTeams.getManager());
        viewHolder.tvDianhua.setText("联系电话:  " + wantTeams.getManagerMobile());
        viewHolder.tvBaojia.setText("订单报价:  " + wantTeams.getInfo());
        viewHolder.tvRiqi.setText("报价日期:  " + wantTeams.getJoinDT());

        viewHolder.tvChakan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int managerId = data.get(position).getManagerId();
                Log.d(TAG, "onClick: " + managerId);
                Intent intent = new Intent(mContext, TeamInfoActivity.class);
                intent.putExtra("managerId", String.valueOf(managerId));
                mContext.startActivity(intent);
            }
        });
        viewHolder.tvXuanze.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String wantNo = data.get(position).getWantNo();
                int mid = SPUtils.getInstance().getInt("mid");
                String signInPwd = SPUtils.getInstance().getString("signInPwd");
                int tid = data.get(position).getManagerId();
                String cityId = SPUtils.getInstance().getString("CityId");
                getDataFromChooseTeam(String.valueOf(mid), cityId, wantNo, String.valueOf(tid), signInPwd);
            }
        });
        return convertView;
    }

    static class ViewHolder {
        //private CheckBox checkboxAll;
        private TextView tvMingzi;
        private TextView tvDianhua;
        private TextView tvBaojia;
        private TextView tvRiqi;
        private TextView tvChakan;
        private TextView tvXuanze;
    }

    //=================================================选择施工队=================================================\\
    void getDataFromChooseTeam(final String mid, String shopId, String wantNo, String tid, final String signInPwd) {
        SortedMap<Object, Object> parameters = new TreeMap<>();
        String randomStr = MD5Util.getRandomStr();
        String timeStamp = MD5Util.getTimeStamp();
        String characterEncoding = "UTF-8";
        parameters.put("mid", mid);
        parameters.put("shopId", shopId);
        parameters.put("wantNo", wantNo);
        parameters.put("tid", tid);
        parameters.put("nonceStr", randomStr);
        parameters.put("timeStamp", timeStamp);
        String mySign = MD5Util.createSign(characterEncoding, parameters, signInPwd);

        Log.d(TAG, "randomStr === " + randomStr + "---timeStamp === " + timeStamp + " ---- mySign ===" + mySign + " --- mid --- " + mid);
        OkHttpUtils
                .post()
                .url(Constants.QIANGYU_URL + "ChooseTeam")
                .addParams("mid", mid)
                .addParams("shopId", shopId)
                .addParams("wantNo", wantNo)
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
                        processDataChooseTeam(response);
                    }
                });

    }

    private void processDataChooseTeam(String json) {
        Log.d(TAG, "ChooseTeam: " + json);
        JSONObject jsonObject = JSON.parseObject(json);
        String code = jsonObject.get("Code").toString();
        if ("OK".equals(code)) {
            ToastUtil.toastCenter(mContext, "选择成功...");
            SelectConstructionTeamActivity selectConstructionTeamActivity = new SelectConstructionTeamActivity();
            selectConstructionTeamActivity.finish();
        } else {
            String errmsg = jsonObject.get("errmsg").toString();
            ToastUtil.toastCenter(mContext, "选择失败,失败原因: " + errmsg);
        }
    }

}
