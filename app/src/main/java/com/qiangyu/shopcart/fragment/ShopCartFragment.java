package com.qiangyu.shopcart.fragment;

import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.qiangyu.R;
import com.qiangyu.app.MainActivity;
import com.qiangyu.base.BaseFragment;
import com.qiangyu.home.bean.GoodsBean;
import com.qiangyu.shopcart.activity.ConfirmOrderActivity;
import com.qiangyu.shopcart.adapter.ShopCartAdapter;
import com.qiangyu.shopcart.bean.JsonCartBeanData;
import com.qiangyu.utils.Constants;
import com.qiangyu.utils.Loading;
import com.qiangyu.utils.MD5Util;
import com.qiangyu.utils.SPUtils;
import com.qiangyu.utils.ToastUtil;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.SortedMap;
import java.util.TreeMap;

public class ShopCartFragment extends BaseFragment implements View.OnClickListener {

    private static final String TAG = "ShopCartFragment";

    //private Button btnCollection;
    private ImageView ivEmpty;
    private RecyclerView recyclerview;
    private LinearLayout llCheckAll;
    private CheckBox checkboxAll;
    private TextView tvShopcartTotal;
    private Button btnCheckOut;
    private CheckBox cbAll;
    private Button btnDelete;
    private TextView tvEmptyCartTobuy;
    private static LinearLayout llDelete;
    private static TextView tvShopcartEdit;
    private static LinearLayout ll_empty_shopcart;
    private ShopCartAdapter adapter;
    //编辑状态
    private static final int ACTION_EDIT = 1;
    //完成状态
    private static final int ACTION_COMPLETE = 2;
    private GoodsBean mGoodsBean;
    //记录刷新状态
    private boolean isGetData = false;
    private JsonCartBeanData.Result mResult;

    @Override
    public void onHiddenChanged(boolean hidden) {
        // TODO Auto-generated method stub
        super.onHiddenChanged(hidden);
        if (hidden) {
        }
    }

    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        Log.d("onCreateAnimation", "onCreateAnimation: 进入");
        if (enter && !isGetData) {
            isGetData = true;
            //   这里可以做网络请求或者需要的数据刷新操作
            showData();
        } else {
            isGetData = false;
        }
        return super.onCreateAnimation(transit, enter, nextAnim);
    }

    @Override
    public void onResume() {
        //showData();
        super.onResume();
    }

    @Override
    protected View initView() {
        View view = View.inflate(mContext, R.layout.fragment_shopping_cart, null);
        tvShopcartEdit = view.findViewById(R.id.tv_shopcart_edit);
        recyclerview = view.findViewById(R.id.recyclerview);
        llCheckAll = view.findViewById(R.id.ll_check_all);
        checkboxAll = view.findViewById(R.id.checkbox_all);
        tvShopcartTotal = view.findViewById(R.id.tv_shopcart_total);
        btnCheckOut = view.findViewById(R.id.btn_check_out);
        llDelete = view.findViewById(R.id.ll_delete);
        cbAll = view.findViewById(R.id.cb_all);
        btnDelete = view.findViewById(R.id.btn_delete);
        ll_empty_shopcart = view.findViewById(R.id.ll_empty_shopcart);
        ivEmpty = view.findViewById(R.id.iv_empty);
        tvEmptyCartTobuy = view.findViewById(R.id.tv_empty_cart_tobuy);
        //btnCollection = view.findViewById(R.id.btn_collection);

        //btnCollection.setOnClickListener(this);
        btnCheckOut.setOnClickListener(this);
        btnDelete.setOnClickListener(this);
        tvEmptyCartTobuy.setOnClickListener(this);
        //设置右上角编辑/完成状态
        //initListener();

        return view;
    }

    private void initListener() {
        //设置默认的编辑状态
        tvShopcartEdit.setTag(ACTION_EDIT);
        tvShopcartEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int action = (int) view.getTag();
                if (action == ACTION_EDIT) {
                    //切换完成状态
                    showDelete();
                } else {
                    //切换编辑状态
                    hideDelete();
                }
            }
        });
    }

    /**
     * 完成状态
     */
    private void showDelete() {
        //1.设置状态和文本=完成
        tvShopcartEdit.setTag(ACTION_COMPLETE);
        tvShopcartEdit.setText("完成");
        //2.变成非选中状态
        if (adapter != null) {
            adapter.checkAllNone(false);
            adapter.checkAll();
        }
        //3.删除/收藏视图显示
        llDelete.setVisibility(View.VISIBLE);
        //4.结算视图隐藏
        llCheckAll.setVisibility(View.GONE);
    }

    /**
     * 编辑状态
     */
    private void hideDelete() {
        //1.设置状态和文本=完成
        tvShopcartEdit.setTag(ACTION_EDIT);
        tvShopcartEdit.setText("编辑");
        //2.变成非选中状态
        if (adapter != null) {
            adapter.checkAllNone(true);
            adapter.checkAll();
            adapter.showTotalPrice();
        }
        //3.删除/收藏视图显示
        llDelete.setVisibility(View.GONE);
        //4.结算视图隐藏
        llCheckAll.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(View v) {
        if (v == btnCheckOut) {
            //结算
            Intent intent = new Intent(mContext, ConfirmOrderActivity.class);
            startActivity(intent);
        } else if (v == btnDelete) {
            //删除选中的
            adapter.deleteData();
            //效验状态
            adapter.checkAll();
            //没有数据时显示空的界面
            if (adapter.getItemCount() == 0) {
                emptyShopcart();
            }
        }/* else if (v == btnCollection) {
            //收藏暂时用不到
        } */ else if (v == tvEmptyCartTobuy) {
            //购物车为空时点击去逛逛跳转的页面
            Intent intent = new Intent(mContext, MainActivity.class);
            startActivity(intent);
            getActivity().finish();
        }
    }

    @Override
    public void initData() {
        super.initData();
        Log.d(TAG, "ShopCartFragment---initData");
        //initView();
        //showData();
    }


    /**
     * 显示数据
     */
    public void showData() {
        mGoodsBean = new GoodsBean();
        Log.d(TAG, "showData: ");
        String signInPwd = SPUtils.getInstance().getString("signInPwd");
        String mid = String.valueOf(SPUtils.getInstance().getInt("mid"));
        String cityId = SPUtils.getInstance().getString("CityId");
        if (!signInPwd.isEmpty()) {
            Log.d(TAG, "进入更新购物车: ");
            getDataFromCartList(cityId, mid, signInPwd);
            return;
        }
        //如果没有账号登录加载显示空购物车
        emptyShopcart();
    }

    public static void emptyShopcart() {
        ll_empty_shopcart.setVisibility(View.VISIBLE);
        tvShopcartEdit.setVisibility(View.GONE);
        llDelete.setVisibility(View.GONE);
    }

    //=================================================获取购物车=================================================\\
    /*
    int mid
    int shopId,
    string nonceStr,
    string timeStamp,
    string sign,
     */
    void getDataFromCartList(final String shopId, final String mid, final String signInPwd) {
        Log.d(TAG, "shopId: " + shopId + " -- mid: " + mid + " -- signInPwd: " + signInPwd);
        Loading.loading(mContext, "正在加载购物车...");
        SortedMap<Object, Object> parameters = new TreeMap<>();
        String randomStr = MD5Util.getRandomStr();
        String timeStamp = MD5Util.getTimeStamp();
        String characterEncoding = "UTF-8";
        parameters.put("shopId", shopId);
        parameters.put("mid", mid);
        parameters.put("nonceStr", randomStr);
        parameters.put("timeStamp", timeStamp);
        String mySign = MD5Util.createSign(characterEncoding, parameters, signInPwd);

        Log.d("qianming", "randomStr === " + randomStr + "---timeStamp === " + timeStamp + " ---- mySign ===" + mySign + " --- mid --- " + mid);
        OkHttpUtils
                .post()
                .url(Constants.QIANGYU_URL + "GetCartList")
                .addParams("shopId", shopId)
                .addParams("mid", mid)
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
                        Log.d("getDataFromCartList", "购物车请求失败 === >" + e.getMessage());
                        ToastUtil.toastCenter(mContext, "网络异常请稍后再试...");
                        emptyShopcart();
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
                        processDataCartList(response);
                        //Loading.endLoad();
                    }
                });

    }

    private void processDataCartList(String json) {
        Loading.endLoad();
        Log.d(TAG, "processDataCartList: " + json);
        JsonCartBeanData jsonCartBeanData = JSON.parseObject(json, JsonCartBeanData.class);
        mResult = jsonCartBeanData.getResult();
        mGoodsBean.setCartResult(mResult);
        if (!mGoodsBean.getCartResult().getCartList().isEmpty() && !mGoodsBean.getCartResult().getPicList().isEmpty()) {
            tvShopcartEdit.setVisibility(View.GONE);
            llCheckAll.setVisibility(View.VISIBLE);
            //有数据
            //把当没有数据显示的布局-隐藏
            ll_empty_shopcart.setVisibility(View.GONE);
            //设置适配器
            adapter = new ShopCartAdapter(mContext, getActivity(), mGoodsBean, tvShopcartTotal, checkboxAll, cbAll);
            recyclerview.setAdapter(adapter);
            //设置布局管理
            recyclerview.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        } else {
            //没有数据
            //显示数据为空的布局
            emptyShopcart();
        }
    }
}
