package com.qiangyu.im.ui.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.qiangyu.R;
import com.qiangyu.im.adapter.ContactCustomerServiceAdapter;
import com.qiangyu.im.enity.JsonKFListData;
import com.qiangyu.im.enity.KFList;
import com.qiangyu.im.ui.activity.ChatActivity;
import com.qiangyu.utils.Constants;
import com.qiangyu.utils.MD5Util;
import com.qiangyu.utils.SPUtils;
import com.qiangyu.utils.ToastUtil;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import okhttp3.Call;

@SuppressLint("ValidFragment")
public class ContactCustomerServiceFragment extends BaseFragment {

    private String TAG = "ContactCustomerServiceFragment";
    private ListView mListView;
    private String GOODS_BEAN = "goodsBean";
    private List<KFList> mKfList;

    //http://192.168.1.104:8021/QYApi/
    @Override
    protected View initView() {

        String cityId = SPUtils.getInstance().getString("CityId");
        Log.d(TAG, "cityId: " + cityId);
        if (StringUtils.isNotEmpty(cityId)) {
            getDataFromGetKF(cityId);
        }

        View v = View.inflate(mContext, R.layout.im_list_view_more_selling, null);
        mListView = v.findViewById(R.id.lv_more_selling);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //Toast.makeText(mContext, "点击的是 --- > " + i, Toast.LENGTH_SHORT).show();
                String value = mKfList.get(i).getCode();
                String headPic = mKfList.get(i).getHeadPic();
                String displayName = mKfList.get(i).getDisplayName();
                Intent intent = new Intent(mContext, ChatActivity.class);
                intent.putExtra("kefu", value);
                intent.putExtra("headPic", headPic);
                intent.putExtra("displayName", displayName);
                startActivity(intent);
            }
        });
        return v;
    }

    @Override
    protected void setUpView() {

    }

    //=================================================获取客服列表=================================================\\
    /*
        /// <summary>
        /// 获取 客服列表
        /// </summary>
        /// <param name="shopId"></param>
        /// <param name="nonceStr"></param>
        /// <param name="timeStamp"></param>
        /// <param name="sign"></param>
        /// <returns></returns>
        [HttpPost]
        public JsonResult GetKF(int shopId, string nonceStr, string timeStamp, string sign)
    */
    void getDataFromGetKF(String shopId) {
        SortedMap<Object, Object> parameters = new TreeMap<>();
        String randomStr = MD5Util.getRandomStr();
        String timeStamp = MD5Util.getTimeStamp();
        String characterEncoding = "UTF-8";
        parameters.put("shopId", shopId);
        parameters.put("nonceStr", randomStr);
        parameters.put("timeStamp", timeStamp);
        String mySign = MD5Util.createSign(characterEncoding, parameters);

        //Constants.QIANGYU_URL +
        OkHttpUtils
                .post()
                .url(Constants.QIANGYU_URL + "GetKF")
                .addParams("shopId", shopId)
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
                    public void onError(Call call, Exception e, int id) {
                        ToastUtil.toastCenter(mContext, "网络连接错误请稍后再试...");
                        Log.d("getDataFromNet", "网络连接错误请稍后再试 === >" + e.getMessage());
                    }

                    /**
                     * 当联网成功时回调
                     * @param response
                     * @param id
                     */
                    @Override
                    public void onResponse(String response, int id) {
                        //解析数据
                        response = response.replace("\\", "");
                        response = response.replace("\"[", "[");
                        response = response.replace("]\"", "]");
                        Log.d("getDataFromNet", "客服列表获取客服 === >" + response);
                        GetKF(response);
                    }
                });
    }

    private void GetKF(String json) {
        Log.d(TAG, "GetKF: " + json);
        JSONObject jsonObject = JSON.parseObject(json);
        String code = jsonObject.get("Code").toString().trim();
        if ("OK".equals(code)) {
            JsonKFListData jsonKFListData = JSON.parseObject(json, JsonKFListData.class);
            mKfList = jsonKFListData.getResult().getKFList();
            mListView.setAdapter(new ContactCustomerServiceAdapter(mContext, mKfList));
        } else {
            String errmsg = jsonObject.get("errmsg").toString().trim();
            ToastUtil.toastCenter(mContext, "暂无客服...");
        }
    }

}