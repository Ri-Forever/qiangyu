package com.qiangyu.home.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.qiangyu.R;
import com.qiangyu.app.GoodsInfoActivity;
import com.qiangyu.base.BaseFragment;
import com.qiangyu.home.adapter.MoreSellingTjAdapter;
import com.qiangyu.home.bean.JsonTjBeanData;
import com.qiangyu.home.bean.Tj;
import com.qiangyu.utils.Constants;
import com.qiangyu.utils.SPUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import okhttp3.Call;

@SuppressLint("ValidFragment")
public class MoreRecommendFragment extends BaseFragment {

    private int i = 0;
    private ListView mListView;
    private String GOODS_BEAN = "goodsBean";
    private JsonTjBeanData.Result mTjResult;

    @Override
    protected View initView() {
        if (i == 0) {
            getDataFromHot();
        }
        View v1 = View.inflate(mContext, R.layout.list_view_more_selling, null);
        mListView = v1.findViewById(R.id.lv_more_selling);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Tj tj = mTjResult.getTj().get(i);
                String mChanpinId = tj.getId() + "";
                Intent intent = new Intent(mContext, GoodsInfoActivity.class);
                intent.putExtra(GOODS_BEAN, mChanpinId);
                mContext.startActivity(intent);
            }
        });
        return v1;
    }


    //推荐
    void getDataFromHot() {
        String cityId = SPUtils.getInstance().getString("CityId");
        SortedMap<Object, Object> parameters = new TreeMap<>();
        String randomStr = getRandomStr();
        String timeStamp = timeStamp();
        String characterEncoding = "UTF-8";
        parameters.put("shopId", cityId);
        parameters.put("nonceStr", randomStr);
        parameters.put("timeStamp", timeStamp);
        String mySign = createSign(characterEncoding, parameters);

        OkHttpUtils
                .post()
                .url(Constants.QIANGYU_URL + "GetHomeTj")
                .addParams("shopId", cityId)
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
                        Log.d("getDataFromNet", "更多推荐请求失败 === >" + e.getMessage());
                        getDataFromHot();
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
                        Log.d("getDataFromNet", " 更多推荐 === >" + response);
                        processHotData(response);
                    }
                });
    }

    private void processHotData(String json) {
        JsonTjBeanData jsonTjBeanData = JSON.parseObject(json, JsonTjBeanData.class);
        mTjResult = jsonTjBeanData.getResult();
        if (mTjResult != null) {
            //有数据
            mListView.setAdapter(new MoreSellingTjAdapter(mContext, mTjResult.getTj()));
            i++;
        } else {
            //没有数据
            Toast.makeText(mContext, "暂无数据!", Toast.LENGTH_SHORT).show();
        }
    }

    //=================================================MD5加密=================================================\\
    //32位随机数
    public static String getRandomStr() {
        StringBuffer sb = new StringBuffer();
        Random r = new Random();
        String str = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        for (int i = 0; i < 32; i++) {
            sb.append(str.charAt(r.nextInt(str.length())));
        }
        return sb.toString();
    }

    //时间戳
    public static String timeStamp() {
        long time = System.currentTimeMillis();
        String timeStamp = String.valueOf(time / 1000);
        return timeStamp;
    }

    static class MD5Util {
        private static final char HEX_DIGITS[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                'A', 'B', 'C', 'D', 'E', 'F'};

        public static String toHexString(byte[] b) {
            //String to byte
            StringBuilder sb = new StringBuilder(b.length * 2);
            for (int i = 0; i < b.length; i++) {
                sb.append(HEX_DIGITS[(b[i] & 0xf0) >>> 4]);
                sb.append(HEX_DIGITS[b[i] & 0x0f]);
            }
            return sb.toString();
        }

        public static String md5(String s) {
            try {
                // Create MD5 Hash
                MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
                digest.update(s.getBytes());
                byte messageDigest[] = digest.digest();
                return toHexString(messageDigest);
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
            return "";
        }
    }

    @SuppressWarnings("unchecked")
    public static String createSign(String characterEncoding, SortedMap<Object, Object> parameters) {
        StringBuffer sb = new StringBuffer();
        Set es = parameters.entrySet();//所有参与传参的参数按照accsii排序（升序）
        Iterator it = es.iterator();
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            String k = (String) entry.getKey();
            Object v = entry.getValue();
            if (null != v && !"".equals(v)
                    && !"sign".equals(k) && !"key".equals(k)) {
                sb.append(k + "=" + v);// + "&"
            }
        }
        sb.append("key=" + Constants.Key);
        String sign = MD5Util.md5(sb.toString());
        return sign;
    }

}