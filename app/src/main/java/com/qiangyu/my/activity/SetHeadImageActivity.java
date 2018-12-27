package com.qiangyu.my.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.qiangyu.R;
import com.qiangyu.app.MainActivity;
import com.qiangyu.utils.Constants;
import com.qiangyu.utils.Loading;
import com.qiangyu.utils.MD5Util;
import com.qiangyu.utils.SPUtils;
import com.qiangyu.utils.ToastUtil;
import com.scrat.app.selectorlibrary.ImageSelector;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

public class SetHeadImageActivity extends Activity {

    private static final int REQUEST_CODE_SELECT_IMG = 1;
    private static final int MAX_SELECT_COUNT = 1;
    private String TAG = "SetHeadImageActivity";
    private Context mContext;
    private ImageView ivTouxiang;
    private Button btnXuanze;
    private Button btnShangchuan;
    private List<String> mPaths;
    private String mResult;
    private ImageView mIbBack;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mContext = this;
        setContentView(R.layout.activity_set_head_image);
        initView();
    }

    private void initView() {
        ivTouxiang = findViewById(R.id.iv_touxiang);
        btnXuanze = findViewById(R.id.btn_xuanze);
        mIbBack = findViewById(R.id.ib_back);
        btnShangchuan = findViewById(R.id.btn_shangchuan);

        mIbBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btnXuanze.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageSelector.show(SetHeadImageActivity.this, REQUEST_CODE_SELECT_IMG, MAX_SELECT_COUNT);
            }
        });
        btnShangchuan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String mid = String.valueOf(SPUtils.getInstance().getInt("mid"));
                String signInPwd = SPUtils.getInstance().getString("signInPwd");
                String path = null;
                if (mPaths != null) {
                    Loading.loading(mContext, "正在上传...");

                    for (int i = 0; i < mPaths.size(); i++) {
                        path = mPaths.get(i);
                        InputStream is = null;
                        byte[] datas = null;
                        mResult = null;
                        try {
                            is = new FileInputStream(path);
                            //创建一个字符流大小的数组。
                            datas = new byte[is.available()];
                            //写入数组
                            is.read(datas);
                            //用默认的编码格式进行编码
                            mResult = Base64.encodeToString(datas, Base64.DEFAULT);
                        } catch (IOException e) {
                            e.printStackTrace();
                        } finally {
                            if (null != is) {
                                try {
                                    is.close();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                    getDataFromSetUserPic(mid, mResult, signInPwd);
                } else {
                    ToastUtil.toastCenter(mContext, "您还未选择头像无法上传");
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_SELECT_IMG) {
            showContent(data);
            return;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void showContent(Intent data) {
        mPaths = ImageSelector.getImagePaths(data);
        if (!mPaths.isEmpty()) {
            Glide.with(mContext).load(mPaths.get(0)).into(ivTouxiang);
        }
    }

    //=================================================设置头像=================================================\\
    /*
        /// <param name="nonceStr"></param>
        /// <param name="timeStamp"></param>
        /// <param name="sign"></param>
        /// <param name="mid"></param>
        /// <param name="value">base64</param>
        /// <returns></returns>
        [HttpPost]
        // [APINoCheck]
        public JsonResult SetUserPic(string nonceStr, string timeStamp, string sign, int mid, string value)
     */
    void getDataFromSetUserPic(String mid, String value, String signInPwd) {
        SortedMap<Object, Object> parameters = new TreeMap<>();
        String randomStr = MD5Util.getRandomStr();
        String timeStamp = MD5Util.getTimeStamp();
        String characterEncoding = "UTF-8";
        parameters.put("mid", mid);
        parameters.put("value", value);
        parameters.put("nonceStr", randomStr);
        parameters.put("timeStamp", timeStamp);
        String mySign = MD5Util.createSign(characterEncoding, parameters, signInPwd);

        OkHttpUtils
                .post()
                .url(Constants.QIANGYU_URL + "SetUserPic")
                .addParams("mid", mid)
                .addParams("value", value)
                .addParams("nonceStr", randomStr)
                .addParams("timeStamp", timeStamp)
                .addParams("sign", mySign)
                .build()
                .readTimeOut(300000)
                .writeTimeOut(300000)
                .connTimeOut(300000)
                /**
                 * 请求失败的时候回调
                 */
                .execute(new StringCallback() {
                    @Override
                    public void onError(okhttp3.Call call, Exception e, int id) {
                        Log.d("getDataFromNet", "请求失败 === >" + e.getMessage());
                        ToastUtil.toastCenter(mContext, "网络连接失败,请检查您的网络...");
                        Loading.endLoad();
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
                        processDataSetUserPic(response);
                    }
                });

    }

    private void processDataSetUserPic(String json) {
        Loading.endLoad();
        Log.d(TAG, "processData1: " + json);
        JSONObject jsonObject = JSON.parseObject(json);
        String code = jsonObject.get("Code").toString();
        if ("OK".equals(code)) {
            ToastUtil.toastCenter(mContext, "上传成功...");
            finish();
        } else {
            String errmsg = jsonObject.get("errmsg").toString();
            ToastUtil.toastCenter(mContext, "上传失败,失败原因: " + errmsg);
        }
    }
}
