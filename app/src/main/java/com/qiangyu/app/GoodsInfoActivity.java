package com.qiangyu.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.hyphenate.chat.EMClient;
import com.qiangyu.R;
import com.qiangyu.adapter.SpecificationsAdapter;
import com.qiangyu.home.adapter.HomeCommentAdapter;
import com.qiangyu.home.bean.Comments;
import com.qiangyu.home.bean.GoodsBean;
import com.qiangyu.home.bean.GoodsDetail;
import com.qiangyu.home.bean.GoodsPics;
import com.qiangyu.home.bean.JsonCommodityBeanData;
import com.qiangyu.home.bean.SKUs;
import com.qiangyu.im.ui.activity.ContactCustomerServiceActivity;
import com.qiangyu.shopcart.view.AddSubView;
import com.qiangyu.utils.Constants;
import com.qiangyu.utils.Loading;
import com.qiangyu.utils.MD5Util;
import com.qiangyu.utils.MyListView;
import com.qiangyu.utils.SPUtils;
import com.qiangyu.utils.ToastUtil;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.Transformer;
import com.youth.banner.listener.OnBannerClickListener;
import com.youth.banner.listener.OnLoadImageListener;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

public class GoodsInfoActivity extends Activity implements View.OnClickListener, Serializable {

    //private ImageButton ibGoodInfoMore;
    //private TextView tvGoodInfoCollection;收藏,暂时用不到
    private TextView tvGoodInfoCart;
    private ImageView ivGoodInfoImage;
    private ImageButton ibGoodInfoBack;
    private TextView tvGoodInfoName;
    private TextView tvGoodInfoDesc;
    private static TextView tvGoodInfoPrice;
    private TextView tvGoodInfoStore;
    private TextView tvGoodInfoStyle;
    private WebView wbGoodInfoMore;
    private LinearLayout llGoodsRoot;
    private TextView tvGoodInfoCallcenter;
    private Button btnGoodInfoAddcart;
    private TextView tv_more_share;
    private TextView tv_more_search;
    private TextView tv_more_home;
    private TextView tvGoodInfoRemark;
    private Button btn_more;
    private static GoodsBean mGoodsBean;
    private MyListView lv_specifications;
    private TextView tv_chanpinid;
    private Context mContext;
    private JsonCommodityBeanData.Result mResult;
    private JsonCommodityBeanData mJsonCommodityBeanData;
    private String mGid;
    private static List<SKUs> mSkUsList;
    private String mMoren;
    private static String spvs;
    private String mMid;
    private String mSignInPwd;
    private String mCityId;
    private AddSubView mAdd_sub_view;
    private static String mType;
    private EditText mEt_value;
    private ImageView mAdd;
    private ImageView mSub;

    private double value = 1;
    private double maxValue = 999999;
    private double minValue = 1;
    private LinearLayout mLl_xiangqing;
    private int mDivisive;
    private Banner mDetails_banner;
    private MyListView mLv_comment;
    private List<Comments> mComments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(R.layout.activity_goods_info);

        this.mContext = this;
        mType = SPUtils.getInstance().getString("type");
        mMid = String.valueOf(SPUtils.getInstance().getInt("mid"));
        mSignInPwd = SPUtils.getInstance().getString("signInPwd");
        mCityId = SPUtils.getInstance().getString("CityId");
        mGoodsBean = new GoodsBean();
        findViews();

        //接收数据
        Intent intent = getIntent();
        mGid = (String) intent.getSerializableExtra("goodsBean");
        getDataFromGoodsDetail(mGid);
    }

    public static void SetPrice(int[] value) {
        int[] specValues = new int[value.length];
        int[] values = new int[value.length];
        String s = Arrays.toString(value);
        s = s.replace("[", "");
        s = s.replace("]", "");
        String[] values1 = s.split(",");
        for (int i = 0; i < values1.length; i++) {
            values[i] = Integer.parseInt(values1[i].trim());
        }
        for (int i = 0; i < values.length / 2; i++) {
            int temp = values[i];
            values[i] = values[values.length - i - 1];
            values[values.length - i - 1] = temp;
        }
        for (SKUs skUs : mSkUsList) {
            String[] split = skUs.getSpecValues().split(",");
            for (int i = 0; i < split.length; i++) {
                specValues[i] = Integer.parseInt(split[i]);
            }
            for (int i = 0; i < specValues.length / 2; i++) {
                int temp = specValues[i];
                specValues[i] = specValues[specValues.length - i - 1];
                specValues[specValues.length - i - 1] = temp;
            }
            if (Arrays.toString(values).equals(Arrays.toString(specValues))) {
                spvs = Arrays.toString(specValues);
                if ("0".equals(mType)) {
                    tvGoodInfoPrice.setText("会员价 ￥" + skUs.getSalePrice2());
                    mGoodsBean.setCover_price(skUs.getSalePrice2() + "");
                } else {
                    tvGoodInfoPrice.setText("￥" + skUs.getSalePrice1());
                    mGoodsBean.setCover_price(skUs.getSalePrice1() + "");
                }
                //设置属性
                mGoodsBean.setSpecValues(skUs.getSpecValues());
                return;
            } else {
            }
        }

    }

    private void setDataForView() {
        //.getDivisive()).setDivisive(mResult.getGoodsDetail().get(0).getDivisive()
        new AddSubView(mContext, mResult.getGoodsDetail().get(0).getDivisive());
        mDivisive = mResult.getGoodsDetail().get(0).getDivisive();
        mComments = mResult.getComments();
        Log.d("divisive", "divisive: " + mDivisive);
        mAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String format = "";
                if (value < maxValue) {
                    //value = value - 0.10;
                    if (mDivisive == 0) {
                        value++;
                    } else {
                        value += 0.1;
                    }
                }
                if (mDivisive == 1) {
                    format = String.format("%.1f", value);
                } else {
                    format = String.format("%.0f", value);
                }
                mEt_value.setText(format);
            }
        });

        mSub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String format = "";
                if (value > minValue) {
                    //value = value - 0.10;
                    if (mDivisive == 0) {
                        value--;
                    } else {
                        value -= 0.1;
                    }
                }
                if (mDivisive == 1) {
                    format = String.format("%.1f", value);
                } else {
                    format = String.format("%.0f", value);
                }
                mEt_value.setText(format);
            }
        });

        List<GoodsDetail> goodsDetailList = mResult.getGoodsDetail();
        GoodsDetail goodsDetail = goodsDetailList.get(0);
        List<GoodsPics> goodsPics = mResult.getGoodsPics();
        mSkUsList = mResult.getSKUs();
        List<Double> ints = new ArrayList<>();
        for (SKUs skUs : mSkUsList) {
            if ("0".equals(mType)) {
                double salePrice1 = skUs.getSalePrice2();
                ints.add(salePrice1);
            } else {
                double salePrice1 = skUs.getSalePrice1();
                ints.add(salePrice1);
            }

        }

        double max = Collections.max(ints);
        double min = Collections.min(ints);
        //设置图片
        //mDetails_banner
        //轮播时间
        List<String> listPic = new ArrayList<>();
        for (int i = 0; i < goodsPics.size(); i++) {
            listPic.add(goodsPics.get(i).getPicture());
        }
        mDetails_banner.setDelayTime(2000);
        //设置循环指示点
        mDetails_banner.setBannerStyle(BannerConfig.NUM_INDICATOR);
        //设置图片轮播效果
        mDetails_banner.setBannerAnimation(Transformer.DepthPage);//Flip Horizontal
        //OnLoadImageListener

        mDetails_banner.setImages(listPic, new OnLoadImageListener() {
            @Override
            public void OnLoadImage(final ImageView view, Object url) {
                //联网请求图片,Glide
                Glide.with(mContext).load(Constants.QIANGYU_PIC_URL + url).diskCacheStrategy(DiskCacheStrategy.ALL).centerCrop().into(view);
            }
        });
        mDetails_banner.setOnBannerClickListener(new OnBannerClickListener() {
            @Override
            public void OnBannerClick(int position) {
            }
        });

        //Glide.with(this).load(Constants.BASE_URL_IMAGE + goodsBean.getFigure()).into(ivGoodInfoImage);
        //Glide.with(this).load(Constants.QIANGYU_PIC_URL + goodsPics.get(0).getPicture()).diskCacheStrategy(DiskCacheStrategy.ALL).centerCrop().into(ivGoodInfoImage);
        mGoodsBean.setFigure(goodsPics.get(0).getPicture());
        //设置文本
        tvGoodInfoName.setText(goodsDetail.getName());
        mGoodsBean.setName(goodsDetail.getName());
        //设置价格
        tvGoodInfoPrice.setText("￥" + min + " - " + max);
        mGoodsBean.setCover_price("￥" + min + " - " + max);
        mMoren = "￥" + min + " - " + max;
        //设置销量
        tvGoodInfoStyle.setText("销量: " + goodsDetail.getSoldNum2());
        //产品号
        //setWebViewData(goodsBean.getProduct_id());
        //备注
        tvGoodInfoRemark.setText(goodsDetail.getRemark());
        mGoodsBean.setRemark(goodsDetail.getRemark());
        //产品id
        tv_chanpinid.setText(goodsDetail.getId() + "");
        mGoodsBean.setProduct_id(goodsDetail.getId() + "");
        //商品详情
        //商品规格属性
        lv_specifications.setAdapter(new SpecificationsAdapter(mContext, mResult));
        //商品评论
        //mComments
        mLv_comment.setAdapter(new HomeCommentAdapter(mContext, mComments));
    }

    /**
     * 设置数据
     */
    /*private void setDataForView(GoodsBean goodsBean) {

        //设置图片
        //Glide.with(this).load(Constants.BASE_URL_IMAGE + goodsBean.getFigure()).into(ivGoodInfoImage);
        Glide.with(this).load(Constants.QIANGYU_URL + goodsBean.getFigure()).into(ivGoodInfoImage);
        //设置文本
        tvGoodInfoName.setText(goodsBean.getName());
        //设置价格
        tvGoodInfoPrice.setText("￥" + goodsBean.getCover_price());
        //产品号
        //setWebViewData(goodsBean.getProduct_id());
        //备注
        tvGoodInfoRemark.setText(goodsBean.getRemark());
        //产品id
        tv_chanpinid.setText(goodsBean.getProduct_id());
        //商品详情
        mResult = goodsBean.getResult();
        Log.d("setDataForView", "setDataForView: " + mResult);
        //商品规格属性
//        List<SpecTypes> specTypes = mResult.getSpecTypes();
//        Log.d("specTypes", "specTypes: "+specTypes.size());
        lv_specifications.setAdapter(new SpecificationsAdapter(mContext, mResult));
    }*/
    private void setWebViewData(String product_id) {
        if (product_id != null) {
            wbGoodInfoMore.loadUrl("www.baidu.com");
        }
    }

    /**
     * Find the Views in the layout<br />
     * <br />
     * Auto-created on 2018-07-17 09:59:50 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    private void findViews() {
        //ibGoodInfoMore = findViewById(R.id.ib_good_info_more);
        mLl_xiangqing = findViewById(R.id.ll_xiangqing);
        tv_more_share = findViewById(R.id.tv_more_share);
        tv_more_search = findViewById(R.id.tv_more_search);
        tv_more_home = findViewById(R.id.tv_more_home);
        btn_more = findViewById(R.id.btn_more);
        tvGoodInfoRemark = findViewById(R.id.tv_good_info_remark);
        ibGoodInfoBack = findViewById(R.id.ib_good_info_back);
        ivGoodInfoImage = findViewById(R.id.iv_good_info_image);
        tvGoodInfoName = findViewById(R.id.tv_good_info_name);
        tvGoodInfoDesc = findViewById(R.id.tv_good_info_desc);
        tvGoodInfoPrice = findViewById(R.id.tv_good_info_price);
        tvGoodInfoStore = findViewById(R.id.tv_good_info_store);
        tvGoodInfoStyle = findViewById(R.id.tv_good_info_style);
        //wbGoodInfoMore = findViewById(R.id.wb_good_info_more);
        llGoodsRoot = findViewById(R.id.ll_goods_root);
        tv_chanpinid = findViewById(R.id.tv_chanpinid);
        lv_specifications = findViewById(R.id.lv_specifications);
        mAdd_sub_view = findViewById(R.id.add_sub_view);
        mDetails_banner = findViewById(R.id.details_banner);
        mLv_comment = findViewById(R.id.lv_comment);

        mEt_value = findViewById(R.id.et_value);
        mAdd = findViewById(R.id.iv_add);
        mSub = findViewById(R.id.iv_sub);

        mEt_value.setText(value + "");

        tvGoodInfoCallcenter = findViewById(R.id.tv_good_info_callcenter);
        //tvGoodInfoCollection = findViewById(R.id.tv_good_info_collection);
        tvGoodInfoCart = findViewById(R.id.tv_good_info_cart);
        btnGoodInfoAddcart = findViewById(R.id.btn_good_info_addcart);
        /*private void initView() {
            attachmentListView = (ListView) findViewById(R.id.attachmentList);
            if (attachmentList.size() > 0) {
                attachmentListView.setVisibility(View.VISIBLE);
                attachmentAdapter = new OAAttachmentAdapter(this, attachmentList);
                attachmentListView.setAdapter(attachmentAdapter);
                setListViewHeightByItem(attachmentListView);
            }
        }*/


        //ibGoodInfoMore.setOnClickListener(this);
        ibGoodInfoBack.setOnClickListener(this);
        btnGoodInfoAddcart.setOnClickListener(this);
        tvGoodInfoCallcenter.setOnClickListener(this);
        //tvGoodInfoCollection.setOnClickListener(this);
        tvGoodInfoCart.setOnClickListener(this);

        tv_more_share.setOnClickListener(this);
        tv_more_search.setOnClickListener(this);
        tv_more_home.setOnClickListener(this);
        mEt_value.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                mEt_value.clearFocus();
                return true;
            }
        });
        mEt_value.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    //获取焦点
                } else {
                    //失去焦点
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
                    String etvalue = mEt_value.getText().toString().trim();
                    String format = null;
                    if (StringUtils.isNotEmpty(etvalue)) {
                        if (mDivisive == 0) {
                            format = String.format("%.0f", Double.valueOf(etvalue));
                        } else {
                            format = String.format("%.1f", Double.valueOf(etvalue));
                        }
                        //Log.d("onFocusChange", "失去焦点: " + format);
                        value = Double.valueOf(format);
                        if (value < 1) {
                            value = 1;
                        }
                        mEt_value.setText(value + "");
                    } else {
                        value = 1;
                        mEt_value.setText(value + "");
                    }

                }
            }
        });

    }
    //dispatchTouchEvent


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (isShouldHideInput(v, ev)) {//点击editText控件外部
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    assert v != null;
                    //hideInput();
                    if (mEt_value != null) {
                        mEt_value.clearFocus();
                        //mEt_value.setFocusable(false);
                    }
                }
            }
            return super.dispatchTouchEvent(ev);
        }
        return super.dispatchTouchEvent(ev);
    }

    public boolean isShouldHideInput(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {
            mEt_value = (EditText) v;
            int[] leftTop = {0, 0};
            //获取输入框当前的location位置
            v.getLocationInWindow(leftTop);
            int left = leftTop[0];
            int top = leftTop[1];
            int bottom = top + v.getHeight();
            int right = left + v.getWidth();
            return !(event.getX() > left && event.getX() < right
                    && event.getY() > top && event.getY() < bottom);
        }
        return false;
    }

    private void setListViewHeightByItem(ListView listView) {
        if (listView == null) {
            return;
        }
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }
        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View item = listAdapter.getView(i, null, listView);
            //item的布局要求是linearLayout，否则measure(0,0)会报错。
            item.measure(0, 0);
            //计算出所有item高度的总和
            totalHeight += item.getMeasuredHeight();
        }
        //获取ListView的LayoutParams,只需要修改高度就可以。
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        //修改ListView高度为item总高度和所有分割线的高度总和。
        //这里的分隔线是指ListView自带的divider
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        //将修改过的参数，重新设置给ListView
        listView.setLayoutParams(params);
    }

    /**
     * Handle button click events<br />
     * <br />
     * Auto-created on 2018-07-17 09:59:50 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    @Override
    public void onClick(View v) {
        if (v == ibGoodInfoBack) {
            // Handle clicks for ibGoodInfoBack
            finish();
        } /*else if (v == ibGoodInfoMore) {
            //商品详情右上角更多选项暂时用不到
            Toast.makeText(this, "更多", Toast.LENGTH_SHORT).show();
        }*/ else if (v == btnGoodInfoAddcart) {
            String username = SPUtils.getInstance().getString("username");
            if (username.isEmpty()) {
                Toast toast = Toast.makeText(mContext, "请先登录...", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                return;
            }
            if (!mMoren.equals(mGoodsBean.getCover_price())) {
                //double value = mAdd_sub_view.getValue();
                getDataFromAddToCart(mGid, mCityId, mMid, spvs, String.valueOf(value), mSignInPwd);

            } else {
                Toast toast = Toast.makeText(mContext, "请先选择商品规格...", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }
        } else if (v == tvGoodInfoCallcenter) {
            // TODO: 2018/8/6 联系客服
            // 判断sdk是否登录成功过，并没有退出和被踢，否则跳转到登陆界面
            if (!EMClient.getInstance().isLoggedInBefore()) {
                /*Intent intent = new Intent(this, UserLoginActivity.class);
                startActivity(intent);
                finish();*/
                Toast.makeText(this, "请先登录!", Toast.LENGTH_SHORT).show();
                return;
            }
            Toast.makeText(this, "联系客服", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, ContactCustomerServiceActivity.class);
            startActivity(intent);
        } /*else if (v == tvGoodInfoCollection) {
            //商品收藏暂时用不到
            Toast.makeText(this, "商品已收藏", Toast.LENGTH_SHORT).show();
        }*/ else if (v == tvGoodInfoCart) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("ShopCartFragment", 2);
            intent.putExtra("keyCart", "valueCart");
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            Toast.makeText(this, "购物车！", Toast.LENGTH_SHORT).show();
            finish();
        }/* else if (v == tv_more_share) {
            Toast.makeText(this, "分享", Toast.LENGTH_SHORT).show();
        } else if (v == tv_more_search) {
            Toast.makeText(this, "搜索", Toast.LENGTH_SHORT).show();
        } else if (v == tv_more_home) {
            Toast.makeText(this, "主页面", Toast.LENGTH_SHORT).show();
        }*/
    }


    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    //=================================================商品详情=================================================\\
    void getDataFromGoodsDetail(String gid) {
        String cityId = SPUtils.getInstance().getString("CityId");
        SortedMap<Object, Object> parameters = new TreeMap<>();
        String randomStr = MD5Util.getRandomStr();
        String timeStamp = MD5Util.getTimeStamp();
        String characterEncoding = "UTF-8";
        parameters.put("shopId", cityId);
        parameters.put("gid", gid);
        parameters.put("nonceStr", randomStr);
        parameters.put("timeStamp", timeStamp);
        String mySign = MD5Util.createSign(characterEncoding, parameters);

        Log.d("qianming", "randomStr === " + randomStr + "---timeStamp === " + timeStamp + " ---- mySign ===" + mySign + " --- gid --- " + gid);
        OkHttpUtils
                .post()
                .url(Constants.QIANGYU_URL + "GetGoodsDetail")
                .addParams("shopId", cityId)
                .addParams("gid", gid)
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
                        getDataFromGoodsDetail(mGid);
                    }

                    /**
                     * 当联网成功时回调
                     * @param response
                     * @param id
                     */
                    @Override
                    public void onResponse(String response, int id) {
                        Log.d("getDataFromNet", " 未更改 === >" + response);
                        //Log.d("getDataFromNet", " --- >" + response);
                        //解析数据
                        //response = response.replace("", "");
                        response = response.replace("\\", "");
                        response = response.replace("\"[", "[");
                        response = response.replace("]\"", "]");
                        Log.d("getDataFromNet", " 更改过 === >" + response);
                        processDataGoodsDetail(response);
                    }
                });

    }

    private void processDataGoodsDetail(String json) {
        Log.d("banner", "json === " + json);
        mJsonCommodityBeanData = JSON.parseObject(json, JsonCommodityBeanData.class);
        mResult = mJsonCommodityBeanData.getResult();

        setDataForView();
    }

    //=================================================加入购物车=================================================\\
    void getDataFromAddToCart(final String gid, final String shopId, final String mid, final String SpecValues, String num, final String signInPwd) {
        Loading.loading(mContext, "正在加入购物车...");
        SortedMap<Object, Object> parameters = new TreeMap<>();
        String randomStr = MD5Util.getRandomStr();
        String timeStamp = MD5Util.getTimeStamp();
        String characterEncoding = "UTF-8";
        parameters.put("shopId", shopId);
        parameters.put("gid", gid);
        parameters.put("mid", mid);
        parameters.put("spvs", SpecValues);
        parameters.put("num", num);
        parameters.put("nonceStr", randomStr);
        parameters.put("timeStamp", timeStamp);
        String mySign = MD5Util.createSign(characterEncoding, parameters, signInPwd);

        Log.d("qianming", "randomStr === " + randomStr + "---timeStamp === " + timeStamp + " ---- mySign ===" + mySign + " --- gid --- " + gid + " --- mid --- " + mid + " --- SpecValues --- " + SpecValues);
        OkHttpUtils
                .post()
                .url(Constants.QIANGYU_URL + "AddToCart")
                .addParams("shopId", shopId)
                .addParams("gid", gid)
                .addParams("mid", mid)
                .addParams("spvs", SpecValues)
                .addParams("num", num)
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
                        Loading.endLoad();
                        Log.d("getDataFromNet", "请求失败 === >" + e.getMessage());
                        ToastUtil.toastCenter(mContext, "网络连接失败请稍后再试...");
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
                        processDataAddToCart(response);
                    }
                });

    }

    private void processDataAddToCart(String json) {
        Loading.endLoad();
        JSONObject jsonObject = JSON.parseObject(json);
        Object code = jsonObject.get("Code");
        if ("OK".equals(code)) {
            Log.d("processDataAddToCart", "processDataAddToCart: " + mGoodsBean.getSpecValues());
            Toast toast = Toast.makeText(mContext, "商品已加入购物车...", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        } else {
            Toast toast = Toast.makeText(mContext, "商品加入失败,请联系客服询问商品状态...", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }

    }

}
