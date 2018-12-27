package com.qiangyu.home.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.qiangyu.R;
import com.qiangyu.home.adapter.PhotoAdapter;
import com.qiangyu.utils.Constants;
import com.qiangyu.utils.Loading;
import com.qiangyu.utils.MD5Util;
import com.qiangyu.utils.MyGridView;
import com.qiangyu.utils.SPUtils;
import com.qiangyu.utils.ToastUtil;
import com.scrat.app.selectorlibrary.ImageSelector;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.SortedMap;
import java.util.TreeMap;

public class DemandReleaseActivity extends Activity implements View.OnClickListener {

    private static final String TAG = "DemandReleaseActivity";
    private static final int REQUEST_CODE_SELECT_IMG = 1;
    private static int MAX_SELECT_COUNT;
    private Context mContext;
    private ImageButton ibDemandReleaseBack;
    private EditText etName;
    private EditText etPhone;
    private EditText etAddress;
    private EditText etTitle;
    private EditText etContent;
    private Button mShangchuan;
    private MyGridView mTupian;
    private List<String> mPaths;
    private String[] mBase64;
    private String mResult;
    private Button btn_paizhao;

    //相机需要
    private static final int REQ_GALLERY = 33;
    private String mPublicPhotoPath;
    private Uri uri;
    private String path;
    private LinearLayout ll_fabuxuqiu;
    private Button btn_qingkong;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mContext = this;
        setContentView(R.layout.activity_demand_release);
        findViews();
    }

    private void findViews() {
        ibDemandReleaseBack = findViewById(R.id.ib_demand_release_back);
        ll_fabuxuqiu = findViewById(R.id.ll_fabuxuqiu);
        etName = findViewById(R.id.et_name);
        etPhone = findViewById(R.id.et_phone);
        etAddress = findViewById(R.id.et_address);
        etTitle = findViewById(R.id.et_title);
        etContent = findViewById(R.id.et_content);

        //上传图片/展示图片
        mShangchuan = findViewById(R.id.btn_shangchuan);
        mTupian = findViewById(R.id.gv_tupian);
        //拍照
        btn_paizhao = findViewById(R.id.btn_paizhao);
        //清空图片
        btn_qingkong = findViewById(R.id.btn_qingkong);

        ibDemandReleaseBack.setOnClickListener(this);
        ll_fabuxuqiu.setOnClickListener(this);
        mShangchuan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //调用相册
                if (pathList.size() > 0) {
                    MAX_SELECT_COUNT = 8 - pathList.size();
                } else {
                    MAX_SELECT_COUNT = 8;
                }
                if (pathList.size() > 7) {
                    ToastUtil.toastCenter(mContext, "只可以上传8张图片!");
                    return;
                }
                ImageSelector.show(DemandReleaseActivity.this, REQUEST_CODE_SELECT_IMG, MAX_SELECT_COUNT);
            }
        });
        btn_paizhao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //调用相机
                if (pathList.size() > 7) {
                    ToastUtil.toastCenter(mContext, "只可以上传8张图片!");
                    return;
                }
                startTake();
            }
        });
        btn_qingkong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //清空图片
                pathList.clear();
                mBase64 = null;
                mTupian.setAdapter(new PhotoAdapter(mContext, pathList));
                ToastUtil.toastCenter(mContext, "图片已清空...");
            }
        });
    }

    //====================================================7.0以上相机权限====================================================\\

    private void startTake() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //判断是否有相机应用
        if (takePictureIntent.resolveActivity(mContext.getPackageManager()) != null) {
            //创建临时图片文件
            File photoFile = null;
            try {
                photoFile = createPublicImageFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            //设置Action为拍照
            if (photoFile != null) {
                takePictureIntent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
                //这里加入flag
                takePictureIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                Uri photoURI;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {//7.0及以上
                    photoURI = FileProvider.getUriForFile(mContext, mContext.getPackageName() + ".updateFileProvider", photoFile);
                } else {
                    photoURI = Uri.fromFile(photoFile);
                }
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQ_GALLERY);

            }
        }
        //将照片添加到相册中
        galleryAddPic(mPublicPhotoPath, mContext);
    }

    /**
     * 创建临时图片文件
     *
     * @return
     * @throws IOException
     */
    private File createPublicImageFile() throws IOException {
        File path = null;
        if (hasSdcard()) {
            path = Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DCIM);
        }
        Date date = new Date();
        String timeStamp = getTime(date, "yyyyMMdd_HHmmss", Locale.CHINA);
        String imageFileName = "Camera/" + "IMG_" + timeStamp + ".jpg";
        File image = new File(path, imageFileName);
        mPublicPhotoPath = image.getAbsolutePath();
        return image;
    }

    /**
     * 判断sdcard是否被挂载
     *
     * @return
     */
    public static boolean hasSdcard() {
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 获取时间的方法
     *
     * @param date
     * @param mode
     * @param locale
     * @return
     */
    private String getTime(Date date, String mode, Locale locale) {
        SimpleDateFormat format = new SimpleDateFormat(mode, locale);
        return format.format(date);
    }

    /**
     * 将照片添加到相册中
     */
    public static void galleryAddPic(String mPublicPhotoPath, Context context) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mPublicPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        context.sendBroadcast(mediaScanIntent);
    }

    /**
     * 根据路径获得突破并压缩返回bitmap用于显示
     */
    /*public static Bitmap getSmallBitmap(String filePath, int reqWidth, int reqHeight) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        //只返回图片的大小信息
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(filePath, options);
    }*/

    //====================================================7.0以上相机权限====================================================\\
    List<String> pathList = new ArrayList<>();

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQ_GALLERY) {
            if (resultCode != Activity.RESULT_OK) return;
            uri = Uri.parse(mPublicPhotoPath);
            path = uri.getPath();
            pathList.add(path);
            mTupian.setAdapter(new PhotoAdapter(mContext, pathList));
            return;
        }
        if (requestCode == REQUEST_CODE_SELECT_IMG) {
            showContent(data);
            return;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void showContent(Intent data) {
        mPaths = ImageSelector.getImagePaths(data);
        for (int i = 0; i < mPaths.size(); i++) {
            pathList.add(mPaths.get(i));
        }
        mTupian.setAdapter(new PhotoAdapter(mContext, pathList));
    }

    @Override
    public void onClick(View v) {
        if (v == ibDemandReleaseBack) {
            finish();
        } else if (v == ll_fabuxuqiu) {

            String strName = etName.getText().toString().trim();
            String strPhone = etPhone.getText().toString().trim();
            String strAddress = etAddress.getText().toString().trim();
            String strTitle = etTitle.getText().toString().trim();
            String strContent = etContent.getText().toString().trim();
            Log.d(TAG, "strName----" + strName);
            Log.d(TAG, "strPhone----" + strPhone);
            Log.d(TAG, "strAddress----" + strAddress);
            Log.d(TAG, "strTitle----" + strTitle);
            Log.d(TAG, "strContent----" + strContent);
            if (!StringUtils.isNotEmpty(strName)) {
                ToastUtil.toastCenter(mContext, "姓名是必填项,请填写后再发布...");
                return;
            }
            if (!StringUtils.isNotEmpty(strPhone)) {
                ToastUtil.toastCenter(mContext, "手机号是必填项,请填写后再发布...");
                return;
            }
            if (strPhone.length() != 11) {
                ToastUtil.toastCenter(mContext, "手机号不符合,请查看后修改...");
                return;
            }
            if (!StringUtils.isNotEmpty(strAddress)) {
                ToastUtil.toastCenter(mContext, "地址是必填项,请填写后再发布...");
                return;
            }
            if (!StringUtils.isNotEmpty(strTitle)) {
                ToastUtil.toastCenter(mContext, "标题是必填项,请填写后再发布...");
                return;
            }
            if (!StringUtils.isNotEmpty(strContent)) {
                ToastUtil.toastCenter(mContext, "内容是必填项,请填写后再发布...");
                return;
            }

            Loading.loading(mContext, "发布需求图片越多耗时越长,请您耐心等待...");
            if (pathList.size() > 0) {
                mBase64 = new String[pathList.size()];
                String path = null;
                for (int i = 0; i < pathList.size(); i++) {
                    path = pathList.get(i);
                    InputStream is = null;
                    byte[] data = null;
                    mResult = null;
                    try {
                        is = new FileInputStream(path);
                        //创建一个字符流大小的数组。
                        data = new byte[is.available()];
                        //写入数组
                        is.read(data);
                        //用默认的编码格式进行编码
                        mResult = Base64.encodeToString(data, Base64.DEFAULT);
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
                    mBase64[i] = mResult;
                    Log.d(TAG, "base64: " + mResult.trim());
                }
            }

            String mid = String.valueOf(SPUtils.getInstance().getInt("mid"));
            String signInPwd = SPUtils.getInstance().getString("signInPwd");
            String shopId = SPUtils.getInstance().getString("CityId");
            if (mBase64 != null) {
                //Log.d(TAG, "有图片");
                getDataFromPublishWant(mid, shopId, strTitle, strContent, strName, strPhone, strAddress, Arrays.toString(mBase64), signInPwd);
            } else {
                //Log.d(TAG, "没有图片");
                getDataFromPublishWant(mid, shopId, strTitle, strContent, strName, strPhone, strAddress, "", signInPwd);
            }
        }
    }

    //=================================================发布需求=================================================\\
    /*
    /// <param name="mid">会员标识</param>
    /// <param name="shopId"> </param>
    /// <param name="title">标题</param>
    /// <param name="info">信息</param>
    /// <param name="receiver">联系人</param>
    /// <param name="mobile">联系方式</param>
    /// <param name="address">地址</param>
    /// <param name="picStr">图片列表（由逗号与图片路径 组成的）</param>
    */
    void getDataFromPublishWant(String mid, String shopId, String title, String info, String receiver, String mobile, String address, String picStr, String signInPwd) {
        Log.d(TAG, "mid: " + mid + " -- shopId: " + shopId + " -- title: " + title + " -- info: " + info + " -- receiver: " + receiver + " -- mobile: " + mobile + " -- address: " + address + " -- picStr: " + picStr + " -- signInPwd: " + signInPwd);
        SortedMap<Object, Object> parameters = new TreeMap<>();
        String randomStr = MD5Util.getRandomStr();
        String timeStamp = MD5Util.getTimeStamp();
        String characterEncoding = "UTF-8";
        parameters.put("mid", mid);
        parameters.put("shopId", shopId);
        parameters.put("title", title);
        parameters.put("info", info);
        parameters.put("receiver", receiver);
        parameters.put("mobile", mobile);
        parameters.put("address", address);
        parameters.put("picStr", picStr);
        parameters.put("nonceStr", randomStr);
        parameters.put("timeStamp", timeStamp);
        String mySign = MD5Util.createSign(characterEncoding, parameters, signInPwd);

        Log.d(TAG, "getDataFromPublishWant: " + mySign);
        OkHttpUtils
                .post()
                .url(Constants.QIANGYU_URL + "PublishWant")
                .addParams("mid", mid)
                .addParams("shopId", shopId)
                .addParams("title", title)
                .addParams("info", info)
                .addParams("receiver", receiver)
                .addParams("mobile", mobile)
                .addParams("address", address)
                .addParams("picStr", picStr)
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
                        PublishWant(response);
                    }
                });

    }

    private void PublishWant(String json) {
        Loading.endLoad();
        Log.d(TAG, "processData1: " + json);
        JSONObject jsonObject = JSON.parseObject(json);
        String code = jsonObject.get("Code").toString();
        if ("OK".equals(code)) {
            ToastUtil.toastCenter(mContext, "发布成功...");
            finish();
        } else {
            //String errmsg = jsonObject.get("errmsg").toString();
            ToastUtil.toastCenter(mContext, "发布失败...");
        }
    }
}
