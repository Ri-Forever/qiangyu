package com.qiangyu.my.activity;

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
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.qiangyu.R;
import com.qiangyu.home.adapter.PhotoAdapter;
import com.qiangyu.my.bean.Details;
import com.qiangyu.utils.Constants;
import com.qiangyu.utils.MD5Util;
import com.qiangyu.utils.MyGridView;
import com.qiangyu.utils.SPUtils;
import com.qiangyu.utils.ToastUtil;
import com.scrat.app.selectorlibrary.ImageSelector;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

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

import okhttp3.Call;

public class CommodityEvaluationActivity extends Activity {

    private String TAG = "CommodityEvaluationActivity";
    private ImageButton mIb_back;
    private ImageView mIv_commodity_picture;
    private TextView mTv_commodity_name;
    private TextView mTv_commodity_parameters;
    private TextView mTv_number;
    private TextView mTv_commodity_price;
    private RatingBar mRb_xingji;
    private EditText mEt_content;
    private Button mBtn_pingjia;
    private CheckBox mCheckbox_niming;
    private Details mDetails;
    private Context mContext;
    private int i = 0;
    //相机需要
    private static final int REQUEST_CODE_SELECT_IMG = 1;
    private static int MAX_SELECT_COUNT;
    private static final int REQ_GALLERY = 33;
    private String mPublicPhotoPath;
    private Uri uri;
    private String path;
    private LinearLayout ll_fabuxuqiu;
    private List<String> mPaths;
    private String[] mBase64;
    private String mResult;
    private Button btn_qingkong;
    private MyGridView mGv_tupian;
    private Button mBtn_xiangce;
    private Button mBtn_paizhao;
    private Button mBtn_qingkong;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mContext = this;
        mDetails = (Details) getIntent().getExtras().get("Details");
        setContentView(R.layout.activity_commodity_evaluation);
        initView();
    }

    private void initView() {
        mIb_back = findViewById(R.id.ib_back);
        //图片
        mIv_commodity_picture = findViewById(R.id.iv_commodity_picture);
        //商品名
        mTv_commodity_name = findViewById(R.id.tv_commodity_name);
        //规格参数
        mTv_commodity_parameters = findViewById(R.id.tv_commodity_parameters);
        //数量
        mTv_number = findViewById(R.id.tv_number);
        //价钱
        mTv_commodity_price = findViewById(R.id.tv_commodity_price);
        //评星
        mRb_xingji = findViewById(R.id.rb_xingji);
        //评价
        mEt_content = findViewById(R.id.et_content);
        //发布评价
        mBtn_pingjia = findViewById(R.id.btn_pingjia);
        //是否匿名
        mCheckbox_niming = findViewById(R.id.checkbox_niming);
        //展示图片
        mGv_tupian = findViewById(R.id.gv_tupian);
        //相册选择
        mBtn_xiangce = findViewById(R.id.btn_xiangce);
        //相机拍照
        mBtn_paizhao = findViewById(R.id.btn_paizhao);
        //清空图片
        mBtn_qingkong = findViewById(R.id.btn_qingkong);

        Glide.with(mContext).load(Constants.QIANGYU_PIC_URL + mDetails.getPicture()).diskCacheStrategy(DiskCacheStrategy.ALL).centerCrop().into(mIv_commodity_picture);
        mTv_commodity_name.setText(mDetails.getGoodsName());
        mTv_commodity_parameters.setText(mDetails.getSpecValues_Desc());
        mTv_number.setText("X " + mDetails.getNum());
        mTv_commodity_price.setText("¥ " + mDetails.getPrice());

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
                String pingjia = mEt_content.getText().toString();
                String rating = String.valueOf((int) mRb_xingji.getRating());
                String orderNo = mDetails.getOrderNo();
                int sku = mDetails.getSKU();
                Log.d(TAG, "rating: " + rating + " --- " + i);

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

                if (mBase64 != null) {
                    //Log.d(TAG, "有图片");
                    getDataFromCommentSKU(String.valueOf(mid), orderNo, rating, pingjia, Arrays.toString(mBase64), String.valueOf(i), String.valueOf(sku), signInPwd);
                } else {
                    //Log.d(TAG, "没有图片");
                    getDataFromCommentSKU(String.valueOf(mid), orderNo, rating, pingjia, "", String.valueOf(i), String.valueOf(sku), signInPwd);
                }

                //getDataFromCommentSKU(String mid, String orderNo, String rate, String comment, String picstr, String isShow,String skuId, String signInPwd)
                //getDataFromCommentSKU(String.valueOf(mid), orderNo, rating, pingjia, "", String.valueOf(i), String.valueOf(sku), signInPwd);
            }
        });

        //展示图片
        //mGv_tupian = findViewById(R.id.gv_tupian);
        //相册选择
        mBtn_xiangce.setOnClickListener(new View.OnClickListener() {
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
                ImageSelector.show(CommodityEvaluationActivity.this, REQUEST_CODE_SELECT_IMG, MAX_SELECT_COUNT);
            }
        });
        //相机拍照
        mBtn_paizhao.setOnClickListener(new View.OnClickListener() {
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
        //清空图片
        mBtn_qingkong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //清空图片
                pathList.clear();
                mBase64 = null;
                mGv_tupian.setAdapter(new PhotoAdapter(mContext, pathList));
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
            mGv_tupian.setAdapter(new PhotoAdapter(mContext, pathList));
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
        mGv_tupian.setAdapter(new PhotoAdapter(mContext, pathList));
    }

    //=================================================发布评价=================================================\\
    /*
    /// <summary>
    /// 评价商品   （根据sku评价的）
    /// </summary>
    /// <param name="mid">会员标识</param>
    /// <param name="orderNo">订单号</param>
    /// <param name="skuId">sku</param>
    /// <param name="rate">星级</param>
    /// <param name="comment">评论内容</param>
    /// <param name="isHidden">是否匿名（0显示 1 匿名）</param>
    /// <param name="picstr">评论图片列表（由逗号与图片地址组成的字符串）</param>
     */
    void getDataFromCommentSKU(String mid, String orderNo, String rate, String comment, String picstr, String isShow, String skuId, String signInPwd) {
        SortedMap<Object, Object> parameters = new TreeMap<>();
        String randomStr = MD5Util.getRandomStr();
        String timeStamp = MD5Util.getTimeStamp();
        String characterEncoding = "UTF-8";
        parameters.put("mid", mid);
        parameters.put("rate", rate);
        parameters.put("comment", comment);
        parameters.put("picstr", picstr);
        parameters.put("isHidden", isShow);
        parameters.put("skuId", skuId);
        parameters.put("orderNo", orderNo);
        parameters.put("nonceStr", randomStr);
        parameters.put("timeStamp", timeStamp);
        String mySign = MD5Util.createSign(characterEncoding, parameters, signInPwd);

        Log.d(TAG, "randomStr === " + randomStr + "---timeStamp === " + timeStamp + " ---- mySign ===" + mySign);
        OkHttpUtils
                .post()
                .url(Constants.QIANGYU_URL + "CommentSKU")
                .addParams("mid", mid)
                .addParams("rate", rate)
                .addParams("comment", comment)
                .addParams("picstr", picstr)
                .addParams("isHidden", isShow)
                .addParams("skuId", skuId)
                .addParams("orderNo", orderNo)
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
                        ToastUtil.toastCenter(mContext, "网络请求失败请稍后再试...");
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
                        processDataCommentSKU(response);
                    }
                });
    }

    private void processDataCommentSKU(String json) {
        Log.d(TAG, "processDataCartList: " + json);
        JSONObject jsonObject = JSON.parseObject(json);
        String code = jsonObject.get("Code").toString();
        if ("OK".equals(code)) {
            Toast toast = Toast.makeText(mContext, "评价完成,非常感谢您的评价...", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            finish();
        } else {
            String errmsg = jsonObject.get("errmsg").toString();
            Toast toast = Toast.makeText(mContext, "评价失败: " + errmsg, Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }
    }
}
