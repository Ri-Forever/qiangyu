package com.qiangyu.shopcart.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.qiangyu.R;
import com.qiangyu.home.bean.GoodsBean;
import com.qiangyu.shopcart.bean.CartList;
import com.qiangyu.shopcart.bean.PicList;
import com.qiangyu.shopcart.fragment.ShopCartFragment;
import com.qiangyu.shopcart.view.AddSubView;
import com.qiangyu.utils.Constants;
import com.qiangyu.utils.Loading;
import com.qiangyu.utils.MD5Util;
import com.qiangyu.utils.SPUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * 适配器的构造方法
 */
public class ShopCartAdapter extends RecyclerView.Adapter<ShopCartAdapter.ViewHolder> {

    private Activity mActivity;
    private final Context mContext;
    private final List<CartList> datas;
    private final TextView tvShopcartTotal;
    private final CheckBox checkboxAll;
    //完成状态下的删除CheckBox
    private final CheckBox cbAll;
    private final List<PicList> mPicList;
    private String TAG = "ShopCartAdapter";
    private final GoodsBean mGoodsBean;
    private AddSubView mAdd_sub_view;
    private CartList mCartList;
    private final String mType;

    private double mValue = 1;
    private double maxValue = 999999;
    private double minValue = 1;
    private int mDivisive;
    private int mScreenHeight;
    private boolean panduan = false;//判断软键盘状态

    public ShopCartAdapter(Context context, FragmentActivity activity, GoodsBean goodsBean, TextView tvShopcartTotal, CheckBox checkboxAll, CheckBox cbAll) {
        this.mActivity = activity;
        this.mContext = context;
        this.datas = goodsBean.getCartResult().getCartList();
        this.mPicList = goodsBean.getCartResult().getPicList();
        this.tvShopcartTotal = tvShopcartTotal;
        this.checkboxAll = checkboxAll;
        this.mGoodsBean = goodsBean;
        this.cbAll = cbAll;

        //获取是否是会员
        mType = SPUtils.getInstance().getString("type");

        //总价格
        showTotalPrice();

        //设置点击事件
        setListener();

        //效验当前状态是否是全选/刚进入是否是全选
        checkAll();
    }

    /**
     * 设置点击事件
     */
    private void setListener() {
        setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                //1.根据位置找到对应的bean对象
                CartList cartList = datas.get(position);
                //2.设置取反状态
                cartList.setSelected(!cartList.isSelected());
                //3.刷新状态
                notifyItemChanged(position);
                //4.效验当前状态是否是全选
                checkAll();
                //5.重新计算总价格
                showTotalPrice();
            }
        });

        //设置CheckBox的点击事件
        checkboxAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //1.得到当前的状态
                boolean isCheck = checkboxAll.isChecked();
                //2.根据状态设置全选和非全选
                checkAllNone(isCheck);
                //3.计算总价
                showTotalPrice();
            }
        });

        //设置CheckBox的删除点击事件
        cbAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //1.得到当前的状态
                boolean isCheck = cbAll.isChecked();
                //2.根据状态设置全选和非全选
                checkAllNone(isCheck);
            }
        });
    }

    /**
     * 设置全选和非全选
     *
     * @param isCheck
     */
    public void checkAllNone(boolean isCheck) {
        if (datas != null && datas.size() > 0) {
            for (int i = 0; i < datas.size(); i++) {
                CartList cartList = datas.get(i);
                cartList.setSelected(isCheck);
                notifyItemChanged(i);
            }
        }
    }

    /**
     * 效验当前状态是否是全选/刚进入是否是全选
     */
    public void checkAll() {
        if (datas != null && datas.size() > 0) {
            int number = 0;
            for (int i = 0; i < datas.size(); i++) {
                CartList cartList = datas.get(i);
                if (!cartList.isSelected()) {
                    //非全选
                    checkboxAll.setChecked(false);
                    cbAll.setChecked(false);
                } else {
                    //选中的
                    number++;
                }
            }
            if (number == datas.size()) {
                //全选
                checkboxAll.setChecked(true);
                cbAll.setChecked(true);
            }
        } else {
            checkboxAll.setChecked(false);
            cbAll.setChecked(false);
        }
    }

    /**
     * 总价格
     */
    public void showTotalPrice() {
        // TODO: 2018/12/17  
        tvShopcartTotal.setText("￥" + getTotalPrice());
    }

    /**
     * 计算总价格
     *
     * @return
     */
    private double getTotalPrice() {
        double totalPrice = 0.0;
        if (datas != null && datas.size() > 0) {
            for (int i = 0; i < datas.size(); i++) {
                CartList cartList = datas.get(i);
                if (cartList.isSelected()) {
                    if ("0".equals(mType)) {
                        String format = String.format("%.1f", cartList.getNum() * cartList.getSalePrice2());
                        totalPrice = totalPrice + Double.valueOf(format);//Double.valueOf(cartList.getNum()) * Double.valueOf(cartList.getSalePrice2());
                    } else {
                        String format = String.format("%.1f", cartList.getNum() * cartList.getSalePrice1());
                        totalPrice = totalPrice + Double.valueOf(format);//Double.valueOf(cartList.getNum()) * Double.valueOf(cartList.getSalePrice1());
                    }
                }
            }
        }
        return totalPrice;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = View.inflate(mContext, R.layout.item_shop_cart, null);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int i) {
        //1.根据位置得到对应的Bean对象
        mCartList = datas.get(i);
        PicList picList = mPicList.get(i);
        //2.设置数据
        viewHolder.cb_gov.setChecked(mCartList.isSelected());
        Glide.with(mContext).load(Constants.QIANGYU_PIC_URL + picList.getPicture()).diskCacheStrategy(DiskCacheStrategy.ALL).centerCrop().into(viewHolder.iv_gov);
        viewHolder.tv_desc_gov.setText(mCartList.getGoodsName());

        //mDivisive = mCartList.getDivisive();
        //mValue = mCartList.getNum();
        if (mCartList.getDivisive() == 0) {
            String num = String.format("%.0f", mCartList.getNum());
            viewHolder.edit_value.setText(num);
        } else {
            String num = String.format("%.1f", mCartList.getNum());
            viewHolder.edit_value.setText(num);
        }
        if ("0".equals(mType)) {
            viewHolder.tv_price_gov.setText("会员价 ￥" + mCartList.getSalePrice2());
        } else {
            viewHolder.tv_price_gov.setText("￥" + mCartList.getSalePrice1());
        }

        //删除商品
        viewHolder.mBtn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
                Log.d(TAG, "onClick: " + mCartList.getSKU());
                String mid = String.valueOf(SPUtils.getInstance().getInt("mid"));
                String signInPwd = SPUtils.getInstance().getString("signInPwd");
                String shopId = SPUtils.getInstance().getString("CityId");
                getDataFromDelCart(shopId, mid, String.valueOf(datas.get(i).getSKU()), signInPwd, i);
            }
        });

        /*viewHolder.add_sub_view.setValue(mCartList.getNum());
        if (mCartList.getDivisive() == 1) {
            viewHolder.add_sub_view.setMinValue(0.1);
        } else {
            viewHolder.add_sub_view.setMinValue(1);
        }
        viewHolder.add_sub_view.setMaxValue(999999);
        mAdd_sub_view = viewHolder.add_sub_view;
        //设置商品数量的变化
        viewHolder.add_sub_view.setOnNumberChangeListener(new AddSubView.OnNumberChangeListener() {

            @Override
            public void onNumberChange(double value) {
                String mid = String.valueOf(SPUtils.getInstance().getInt("mid"));
                String signInPwd = SPUtils.getInstance().getString("signInPwd");
                String shopId = SPUtils.getInstance().getString("CityId");
                Log.d(TAG, "onNumberChange: " + value);
                getDataFromEditCart(shopId, mid, String.valueOf(value), String.valueOf(datas.get(i).getSKU()), signInPwd, i);
                //1.当前列表内存中的数量
                //goodsBean.setNumber(value);
                //2.本地要更新的数量
                //CartStorage.getInstance().updateData(goodsBean);
                //3.刷新适配器
                //notifyItemChanged(i);
                //4.再次计算总价格
                //showTotalPrice();
            }
        });*/

        //判断键盘状态
        mScreenHeight = mActivity.getWindow().getDecorView().getHeight();
        mActivity.getWindow().getDecorView().addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                //获取View可见区域的bottom
                Rect rect = new Rect();
                mActivity.getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
                if (bottom != 0 && oldBottom != 0 && bottom - rect.bottom <= 0) {
                    //Toast.makeText(mContext, "隐藏", Toast.LENGTH_SHORT).show();
                } else {
                    //Toast.makeText(mContext, "弹出", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //增加商品数量
        viewHolder.iv_addition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
                if (viewHolder.edit_value != null) {
                    viewHolder.edit_value.clearFocus();
                    //mEt_value.setFocusable(false);
                }

                mDivisive = datas.get(i).getDivisive();
                mValue = datas.get(i).getNum();
                String format = "";
                if (mValue < maxValue) {
                    //value = value - 0.10;
                    Log.d("onClick", "onClick: " + mDivisive);
                    if (mDivisive == 0) {
                        mValue++;
                    } else {
                        mValue += 0.1;
                    }
                }
                if (mDivisive == 1) {
                    format = String.format("%.1f", mValue);

                } else {
                    format = String.format("%.0f", mValue);
                }
                viewHolder.edit_value.setText(format);
                String mid = String.valueOf(SPUtils.getInstance().getInt("mid"));
                String signInPwd = SPUtils.getInstance().getString("signInPwd");
                String shopId = SPUtils.getInstance().getString("CityId");
                Log.d(TAG, "onNumberChange: " + format);
                getDataFromEditCart(shopId, mid, String.valueOf(format), String.valueOf(datas.get(i).getSKU()), signInPwd, i);
            }
        });

        //减少商品数量
        viewHolder.iv_subtraction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
                if (viewHolder.edit_value != null) {
                    viewHolder.edit_value.clearFocus();
                    //mEt_value.setFocusable(false);
                }

                mDivisive = datas.get(i).getDivisive();
                mValue = datas.get(i).getNum();
                String format = "";
                if (mValue > minValue) {
                    //value = value - 0.10;
                    //Log.d("onClick", "onClick: " + mDivisive);
                    if (mDivisive == 0) {
                        mValue--;
                    } else {
                        mValue -= 0.1;
                    }
                }
                if (mDivisive == 1) {
                    format = String.format("%.1f", mValue);
                } else {
                    format = String.format("%.0f", mValue);
                }
                viewHolder.edit_value.setText(format);
                String mid = String.valueOf(SPUtils.getInstance().getInt("mid"));
                String signInPwd = SPUtils.getInstance().getString("signInPwd");
                String shopId = SPUtils.getInstance().getString("CityId");
                //Log.d(TAG, "onNumberChange: " + format);
                getDataFromEditCart(shopId, mid, String.valueOf(format), String.valueOf(datas.get(i).getSKU()), signInPwd, i);
            }
        });
        viewHolder.edit_value.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    //Log.d(TAG, "点击了完成");
                    InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(mActivity.getWindow().getDecorView().getWindowToken(), 0);
                    String etvalue = viewHolder.edit_value.getText().toString();
                    if (!StringUtils.isNotEmpty(etvalue)) {
                        etvalue = "1";
                    }
                    if (Double.valueOf(etvalue) < 1) {
                        etvalue = "1";
                    }
                    String format = null;
                    if (StringUtils.isNotEmpty(etvalue)) {
                        //Log.d(TAG, "mDivisive: " + mDivisive);
                        mDivisive = datas.get(i).getDivisive();
                        if (mDivisive == 0) {
                            format = String.format("%.0f", Double.valueOf(etvalue));
                        } else {
                            format = String.format("%.1f", Double.valueOf(etvalue));
                        }

                        //Log.d("onFocusChange", "失去焦点: " + format);
                        mValue = Double.valueOf(format);
                        viewHolder.edit_value.setText(mValue + "");
                        String mid = String.valueOf(SPUtils.getInstance().getInt("mid"));
                        String signInPwd = SPUtils.getInstance().getString("signInPwd");
                        String shopId = SPUtils.getInstance().getString("CityId");
                        //Log.d(TAG, "onNumberChange: " + format);
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        getDataFromEditCart(shopId, mid, String.valueOf(format), String.valueOf(datas.get(i).getSKU()), signInPwd, i);

                    } else {
                        mValue = 1;
                        viewHolder.edit_value.setText(mValue + "");
                    }
                }
                if (viewHolder.edit_value != null) {
                    viewHolder.edit_value.clearFocus();
                    //mEt_value.setFocusable(false);
                }
                return true;
            }
        });
        viewHolder.edit_value.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    //失去焦点
                    InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(mActivity.getWindow().getDecorView().getWindowToken(), 0);
                    viewHolder.edit_value.clearFocus();
                    mDivisive = datas.get(i).getDivisive();
                    mValue = datas.get(i).getNum();
                    String format = "";
                    if (mDivisive == 1) {
                        format = String.format("%.1f", mValue);
                    } else {
                        format = String.format("%.0f", mValue);
                    }
                    viewHolder.edit_value.setText(format + "");
                    viewHolder.mBtn_delete.setEnabled(true);
                    viewHolder.iv_addition.setEnabled(true);
                    viewHolder.iv_subtraction.setEnabled(true);
                    //showTotalPrice();
                    /*InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(mActivity.getWindow().getDecorView().getWindowToken(), 0);
                    String etvalue = viewHolder.edit_value.getText().toString();
                    if (Double.valueOf(etvalue) < 1) {
                        etvalue = "1";
                    }
                    String format = null;
                    if (StringUtils.isNotEmpty(etvalue)) {
                        Log.d(TAG, "mDivisive: " + mDivisive);
                        if (mDivisive == 0) {
                            format = String.format("%.0f", Double.valueOf(etvalue));
                        } else {
                            format = String.format("%.1f", Double.valueOf(etvalue));
                        }

                        //Log.d("onFocusChange", "失去焦点: " + format);
                        mValue = Double.valueOf(format);
                        viewHolder.edit_value.setText(mValue + "");
                        String mid = String.valueOf(SPUtils.getInstance().getInt("mid"));
                        String signInPwd = SPUtils.getInstance().getString("signInPwd");
                        String shopId = SPUtils.getInstance().getString("CityId");
                        //Log.d(TAG, "onNumberChange: " + format);
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        getDataFromEditCart(shopId, mid, String.valueOf(format), String.valueOf(datas.get(i).getSKU()), signInPwd, i);

                    } else {
                        mValue = 1;
                        viewHolder.edit_value.setText(mValue + "");
                    }*/

                } else {
                    viewHolder.mBtn_delete.setEnabled(false);
                    viewHolder.iv_addition.setEnabled(false);
                    viewHolder.iv_subtraction.setEnabled(false);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    /**
     * 删除选中的商品
     */
    public void deleteData() {
        if (datas != null && datas.size() > 0) {
            List<String> skuList = new ArrayList<>();
            for (int i = 0; i < datas.size(); i++) {
                //删除选中的
                CartList cartList = datas.get(i);
                if (cartList.isSelected()) {
                    skuList.add(String.valueOf(cartList.getSKU()));
                    //内存--移除
                    //datas.remove(cartList);
                    //保存到本地
                    //CartStorage.getInstance().deleteData(cartList);
                    //刷新
                    //notifyItemRemoved(i);
                    i--;
                }
            }
            for (String sku : skuList) {
                String mid = String.valueOf(SPUtils.getInstance().getInt("mid"));
                String signInPwd = SPUtils.getInstance().getString("signInPwd");
                String cityId = SPUtils.getInstance().getString("CityId");
                //getDataFromDelCart(cityId, mid, sku, signInPwd);
            }

        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private CheckBox cb_gov;
        private ImageView iv_gov;
        private TextView tv_desc_gov;
        private TextView tv_price_gov;
        private AddSubView add_sub_view;
        private final Button mBtn_delete;
        private EditText edit_value;
        private ImageView iv_addition;
        private ImageView iv_subtraction;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cb_gov = itemView.findViewById(R.id.cb_gov);
            iv_gov = itemView.findViewById(R.id.iv_gov);
            mBtn_delete = itemView.findViewById(R.id.btn_delete);
            tv_desc_gov = itemView.findViewById(R.id.tv_desc_gov);
            tv_price_gov = itemView.findViewById(R.id.tv_price_gov);
            add_sub_view = itemView.findViewById(R.id.add_sub_view);
            edit_value = itemView.findViewById(R.id.edit_value);
            iv_addition = itemView.findViewById(R.id.iv_addition);
            iv_subtraction = itemView.findViewById(R.id.iv_subtraction);

            //设置item的点击事件
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onItemClick(getLayoutPosition());
                    }
                }
            });
        }
    }

    /**
     * 监听点击的item
     */
    public interface OnItemClickListener {
        /**
         * 当点击某条时回调
         *
         * @param position
         */
        void onItemClick(int position);

    }

    /**
     * 设置item的监听
     *
     * @param onItemClickListener
     */
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    private OnItemClickListener mOnItemClickListener;

    //=================================================加减商品数量=================================================\\
    /*
    int mid
    int skuId
    decimal num
    int shopId
    string nonceStr
    string timeStamp
    string sign
     */
    void getDataFromEditCart(final String shopId, final String mid, final String num, String skuId, String signInPwd, final int i) {
        //Log.d(TAG, "shopId: " + shopId + " -- mid: " + mid + " -- num: " + num + " -- skuId: " + skuId + " -- signInPwd: " + signInPwd);
        //Loading.loading(mContext, "正在修改商品数量...");
        SortedMap<Object, Object> parameters = new TreeMap<>();
        String randomStr = MD5Util.getRandomStr();
        String timeStamp = MD5Util.getTimeStamp();
        String characterEncoding = "UTF-8";
        parameters.put("shopId", shopId);
        parameters.put("mid", mid);
        parameters.put("num", num);
        parameters.put("skuId", skuId);
        parameters.put("nonceStr", randomStr);
        parameters.put("timeStamp", timeStamp);
        String mySign = MD5Util.createSign(characterEncoding, parameters, signInPwd);

        Log.d(TAG, "getDataFromEditCart: " + mySign);
        OkHttpUtils
                .post()
                .url(Constants.QIANGYU_URL + "EditCart")
                .addParams("shopId", shopId)
                .addParams("mid", mid)
                .addParams("num", num)
                .addParams("skuId", skuId)
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
                        notifyDataSetChanged();
                        Toast toast = Toast.makeText(mContext, "网络可能出现问题,请稍后重试...", Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
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
                        processDataEditCart(response, num, i);
                        Loading.endLoad();
                    }
                });

    }

    private void processDataEditCart(String json, String num, int i) {
        Log.d(TAG, "processDataEditCart: " + json);
        JSONObject jsonObject = JSON.parseObject(json);
        String code = jsonObject.get("Code").toString();
        if ("OK".equals(code)) {
            Log.d(TAG, "processDataEditCart: " + i);
            datas.get(i).setNum(Double.valueOf(num));//Integer.valueOf(num)
            //notifyDataSetChanged();
            notifyItemChanged(i);
            notifyItemRangeChanged(i, datas.size());
            //mAdd_sub_view.setValue(Double.valueOf(num));//Integer.valueOf(num)
            showTotalPrice();
        } else {
            Toast toast = Toast.makeText(mContext, "未知错误,请稍后重试...", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }

    }

    //=================================================删除商品=================================================\\
    /*
    int shopId,
    int mid,
    int skuId,
    string nonceStr,
    string timeStamp,
    string sign,
     */
    void getDataFromDelCart(final String shopId, final String mid, String skuId, String signInPwd, final int i) {
        Loading.loading(mContext, "正在删除商品...");
        SortedMap<Object, Object> parameters = new TreeMap<>();
        String randomStr = MD5Util.getRandomStr();
        String timeStamp = MD5Util.getTimeStamp();
        String characterEncoding = "UTF-8";
        parameters.put("shopId", shopId);
        parameters.put("mid", mid);
        parameters.put("skuId", skuId);
        parameters.put("nonceStr", randomStr);
        parameters.put("timeStamp", timeStamp);
        String mySign = MD5Util.createSign(characterEncoding, parameters, signInPwd);

        Log.d(TAG, "getDataFromEditCart: " + mySign);
        OkHttpUtils
                .post()
                .url(Constants.QIANGYU_URL + "DelFromCart")
                .addParams("shopId", shopId)
                .addParams("mid", mid)
                .addParams("skuId", skuId)
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
                        Toast toast = Toast.makeText(mContext, "网络可能出现问题,请稍后重试...", Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
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
                        processDataDelCart(response, i);
                        Loading.endLoad();
                    }
                });

    }

    private void processDataDelCart(String json, int i) {
        Log.d(TAG, "processDataEditCart: " + json);
        JSONObject jsonObject = JSON.parseObject(json);
        String code = jsonObject.get("Code").toString();
        if ("OK".equals(code)) {
            Log.d(TAG, "processDataDelCart: " + i);
            datas.remove(i);
            notifyItemRemoved(i);
            notifyItemRangeChanged(i, datas.size());
            showTotalPrice();
            if (datas.size() == 0) {
                ShopCartFragment.emptyShopcart();
            }
        } else {
            Toast toast = Toast.makeText(mContext, "未知错误,请稍后重试...", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }

    }
}
