package com.qiangyu.home.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.qiangyu.R;
import com.qiangyu.base.BaseFragment;
import com.qiangyu.home.activity.CityListActivity;
import com.qiangyu.home.activity.SearchActivity;
import com.qiangyu.home.adapter.HomeFragmentAdapter;
import com.qiangyu.home.bean.JsonBannerBeanData;
import com.qiangyu.home.bean.JsonCategoryBeanData;
import com.qiangyu.home.bean.JsonHotBeanData;
import com.qiangyu.home.bean.JsonNavBeanData;
import com.qiangyu.home.bean.JsonShopListData;
import com.qiangyu.home.bean.JsonTjBeanData;
import com.qiangyu.home.bean.ResultBeanData;
import com.qiangyu.home.bean.ShopList;
import com.qiangyu.my.bean.JsonMemberInfoData;
import com.qiangyu.my.bean.MemberInfo;
import com.qiangyu.utils.Constants;
import com.qiangyu.utils.Loading;
import com.qiangyu.utils.SPUtils;
import com.qiangyu.utils.ToastUtil;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.apache.commons.lang3.StringUtils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import okhttp3.Call;

public class HomeFragment extends BaseFragment {

    private int i = 0;
    private ImageView mIbTop;
    private EditText mSearchHome;
    private RecyclerView mRvHome;
    private HomeFragmentAdapter mAdapter;
    //返回的数据
    private ResultBeanData.ResultBean mResultBean;
    private static final String TAG = "HomeFragment";
    private JsonBannerBeanData.Result mBannerResult;
    private JsonNavBeanData.Result mNavResult;
    private JsonHotBeanData.Result mHotResult;
    private JsonTjBeanData.Result mTjResult;
    private JsonCategoryBeanData.Result mCategoryResult;
    //弹出框
    private AlertDialog alertDialog;
    private String mCId;
    private TextView mTvChengshi;
    private String mCity;
    //记录刷新状态
    private boolean isGetData = false;
    private TextView mTv_network_anomaly;

    @Override
    protected View initView() {

        Log.d(TAG, "HomeFragment---initView");
        View view = View.inflate(mContext, R.layout.fragment_home, null);
        mTv_network_anomaly = view.findViewById(R.id.tv_network_anomaly);
        mSearchHome = view.findViewById(R.id.tv_search_home);
        mTvChengshi = view.findViewById(R.id.tv_chengshi);
        mRvHome = view.findViewById(R.id.rv_home);
        mIbTop = view.findViewById(R.id.ib_top);
        mTvChengshi.setVisibility(View.VISIBLE);

        //设置点击事件
        initListener();
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        getDataFromBanner();
    }

    @Override
    public void onResume() {
        super.onResume();
        /*String city = SPUtils.getInstance().getString("City");
        if (StringUtils.isNotEmpty(city)) {
            mTvChengshi.setText(city);
        }
        String username = SPUtils.getInstance().getString("username");
        if (StringUtils.isNotEmpty(username)) {
            String type = SPUtils.getInstance().getString("type");
            if (!StringUtils.isNotEmpty(type)) {
                getDataFromNet(username);
            }
        }*/
    }

/*    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        Log.d("onCreateAnimation", "onCreateAnimation: 进入");
        if (enter && !isGetData) {
            isGetData = true;
            //   这里可以做网络请求或者需要的数据刷新操作
            String city = SPUtils.getInstance().getString("City");
            if (StringUtils.isNotEmpty(city)) {
                String username = SPUtils.getInstance().getString("username");
                if (StringUtils.isNotEmpty(username)) {
                    String type = SPUtils.getInstance().getString("type");
                    if (!StringUtils.isNotEmpty(type)) {
                        getDataFromNet(username);
                    }
                }
                mTvChengshi.setText(city);
                getDataFromBanner();
            } else {
                String username = SPUtils.getInstance().getString("username");
                if (StringUtils.isNotEmpty(username)) {
                    String type = SPUtils.getInstance().getString("type");
                    if (!StringUtils.isNotEmpty(type)) {
                        getDataFromNet(username);
                    }
                }
                //getDataFromGetShopList();
                //String city = SPUtils.getInstance().getString("City");
                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(mContext);
                if (StringUtils.isNotEmpty(city)) {
                    alertBuilder.setTitle("您当前选择的城市是: " + city);
                } else {
                    alertBuilder.setTitle("您当前还未选择城市");
                }

                alertBuilder.setPositiveButton("选择或切换城市", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(mContext, CityListActivity.class);
                        startActivityForResult(intent, 1);
                    }
                });
                alertBuilder.setCancelable(false);
                alertDialog = alertBuilder.create();
                alertDialog.show();
            }
        } else {
            isGetData = false;
        }
        return super.onCreateAnimation(transit, enter, nextAnim);
    }*/

    @Override
    public void initData() {
        super.initData();
        //联网请求主页数据
        /*String city = SPUtils.getInstance().getString("City");
        if (StringUtils.isNotEmpty(city)) {
            mTvChengshi.setText(city);
        }
        String username = SPUtils.getInstance().getString("username");
        if (StringUtils.isNotEmpty(username)) {
            String type = SPUtils.getInstance().getString("type");
            if (!StringUtils.isNotEmpty(type)) {
                getDataFromNet(username);
            }
        }*/

        //   这里可以做网络请求或者需要的数据刷新操作
        String city = SPUtils.getInstance().getString("City");
        if (StringUtils.isNotEmpty(city)) {
            String username = SPUtils.getInstance().getString("username");
            if (StringUtils.isNotEmpty(username)) {
                String type = SPUtils.getInstance().getString("type");
                if (!StringUtils.isNotEmpty(type)) {
                    getDataFromNet(username);
                }
            }
            mTvChengshi.setText(city);
            getDataFromBanner();
        } else {
            String username = SPUtils.getInstance().getString("username");
            if (StringUtils.isNotEmpty(username)) {
                String type = SPUtils.getInstance().getString("type");
                if (!StringUtils.isNotEmpty(type)) {
                    getDataFromNet(username);
                }
            }
            //getDataFromGetShopList();
            //String city = SPUtils.getInstance().getString("City");
            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(mContext);
            if (StringUtils.isNotEmpty(city)) {
                alertBuilder.setTitle("您当前选择的城市是: " + city);
            } else {
                alertBuilder.setTitle("您当前还未选择城市");
            }

            alertBuilder.setPositiveButton("选择或切换城市", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Intent intent = new Intent(mContext, CityListActivity.class);
                    startActivityForResult(intent, 1);
                }
            });
            alertBuilder.setCancelable(false);
            alertDialog = alertBuilder.create();
            alertDialog.show();
        }

    }

    //=================================================首页轮播=================================================\\
    void getDataFromBanner() {
        String city = SPUtils.getInstance().getString("City");
        if (StringUtils.isNotEmpty(city)) {
            mTvChengshi.setText(city);
        }
        Loading.loading(mContext, "正在加载页面数据，请稍后...");
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
                .url(Constants.QIANGYU_URL + "GetHomeBanner")
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
                        //getBannerResult(), getCategoryResult(), getHotResult(), getTjResult()
                        if (getBannerResult() == null) {
                            mTv_network_anomaly.setVisibility(View.VISIBLE);
                        }
                        ToastUtil.toastCenter(mContext, "网络连接错误,请等待有网络时重新加载...");
                        Loading.endLoad();
                        Log.d("getDataFromNet", "首页轮播请求失败 === >" + e.getMessage());
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
                        Log.d("getDataFromNet", " 首页轮播 === >" + response);
                        processBannerData(response);
                    }
                });
    }

    private void processBannerData(String json) {
        JsonBannerBeanData jsonBannerBeanData = JSON.parseObject(json, JsonBannerBeanData.class);
        mBannerResult = jsonBannerBeanData.getResult();
        setBannerResult(mBannerResult);
        //请求成功调用导航
        getDataFromNav();
    }

    //=================================================首页导航=================================================\\
    void getDataFromNav() {
        String cityId = SPUtils.getInstance().getString("CityId");
        SortedMap<Object, Object> parameters = new TreeMap<>();
        String randomStr = getRandomStr();
        String timeStamp = timeStamp();
        String characterEncoding = "UTF-8";
        parameters.put("shopId", cityId);
        parameters.put("isNav", "1");
        parameters.put("nonceStr", randomStr);
        parameters.put("timeStamp", timeStamp);
        String mySign = createSign(characterEncoding, parameters);

        OkHttpUtils
                .post()
                .url(Constants.QIANGYU_URL + "GetCategory")
                .addParams("shopId", cityId)
                .addParams("isNav", "1")
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
                        //getBannerResult(), getCategoryResult(), getHotResult(), getTjResult()
                        if (getCategoryResult() == null) {
                            mTv_network_anomaly.setVisibility(View.VISIBLE);
                        }
                        ToastUtil.toastCenter(mContext, "网络连接错误,请等待有网络时重新加载...");
                        Loading.endLoad();
                        Log.d("getDataFromNet", "首页导航请求失败 === >" + e.getMessage());
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
                        Log.d("getDataFromNet", " 首页导航 === >" + response);
                        processNavData(response);
                    }
                });
    }

    private void processNavData(String json) {
        JsonCategoryBeanData jsonCategoryBeanData = JSON.parseObject(json, JsonCategoryBeanData.class);
        mCategoryResult = jsonCategoryBeanData.getResult();
        setCategoryResult(mCategoryResult);
        //请求成功调用热卖
        getDataFromHot();
    }

    //=================================================首页热卖=================================================\\
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
                .url(Constants.QIANGYU_URL + "GetHomeHot")
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
                        //getBannerResult(), getCategoryResult(), getHotResult(), getTjResult()
                        if (getHotResult() == null) {
                            mTv_network_anomaly.setVisibility(View.VISIBLE);
                        }
                        ToastUtil.toastCenter(mContext, "网络连接错误,请等待有网络时重新加载...");
                        Loading.endLoad();
                        Log.d("getDataFromNet", "首页热卖请求失败 === >" + e.getMessage());
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
                        Log.d("getDataFromNet", " 首页热卖 === >" + response);
                        processHotData(response);
                    }
                });
    }

    private void processHotData(String json) {
        JsonHotBeanData jsonHotBeanData = JSON.parseObject(json, JsonHotBeanData.class);
        mHotResult = jsonHotBeanData.getResult();
        setHotResult(mHotResult);
        //请求成功调用推荐
        getDataFromTj();
    }

    //=================================================首页推荐=================================================\\
    void getDataFromTj() {
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
                        //getBannerResult(), getCategoryResult(), getHotResult(), getTjResult()
                        if (getTjResult() == null) {
                            mTv_network_anomaly.setVisibility(View.VISIBLE);
                        }
                        ToastUtil.toastCenter(mContext, "网络连接错误,请等待有网络时重新加载...");
                        Loading.endLoad();
                        Log.d("getDataFromNet", "首页推荐请求失败 === >" + e.getMessage());
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
                        Log.d("getDataFromTj", " 首页推荐 === >" + response);
                        processTjData(response);
                    }
                });
    }

    private void processTjData(String json) {
        Loading.endLoad();
        JsonTjBeanData jsonTjBeanData = JSON.parseObject(json, JsonTjBeanData.class);
        mTjResult = jsonTjBeanData.getResult();
        setTjResult(mTjResult);

        if (mTjResult != null) {
            //有数据,设置适配器
            mAdapter = new HomeFragmentAdapter(mContext, getBannerResult(), getCategoryResult(), getHotResult(), getTjResult());
            mRvHome.setAdapter(mAdapter);
            //设置跨度大小监听
            GridLayoutManager manager = new GridLayoutManager(mContext, 1);
            i++;
            manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int i) {
                    if (i <= 1.5) {
                        //隐藏
                        mIbTop.setVisibility(View.GONE);
                    } else {
                        //显示
                        mIbTop.setVisibility(View.VISIBLE);
                    }
                    return 1;
                }
            });
            //设置布局管理者
            mRvHome.setLayoutManager(manager);
        } else {
            //没有数据
        }
    }

    //=================================================获取城市列表=================================================\\
    //public ActionResult GetShopList(string nonceStr, string timeStamp, string sign)
    void getDataFromGetShopList() {
        SortedMap<Object, Object> parameters = new TreeMap<>();
        String randomStr = getRandomStr();
        String timeStamp = timeStamp();
        String characterEncoding = "UTF-8";
        parameters.put("nonceStr", randomStr);
        parameters.put("timeStamp", timeStamp);
        String mySign = createSign(characterEncoding, parameters);

        OkHttpUtils
                .post()
                .url(Constants.QIANGYU_URL + "GetShopList")
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
                        Log.d("getDataFromNet", "首页轮播请求失败 === >" + e.getMessage());
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
                        Log.d("getDataFromNet", " 首页轮播 === >" + response);
                        getShopList(response);
                    }
                });
    }

    private void getShopList(String json) {
        Log.d(TAG, "获取城市信息: " + json);
        JsonShopListData jsonShopListData = JSON.parseObject(json, JsonShopListData.class);
        List<ShopList> shopListList = jsonShopListData.getResult().getShopList();
        final String[] items = new String[shopListList.size()];
        final String[] cityId = new String[shopListList.size()];

        for (int j = 0; j < shopListList.size(); j++) {
            int id = shopListList.get(j).getId();
            String name = shopListList.get(j).getName();
            items[j] = name;
            cityId[j] = String.valueOf(id);
        }
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(mContext);
        alertBuilder.setTitle("请选择您的所在城市");
        alertBuilder.setSingleChoiceItems(items, 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mCId = cityId[i];//城市id
                mCity = items[i];//城市名称
            }
        });

        alertBuilder.setPositiveButton("选择城市", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (StringUtils.isNotEmpty(mCity)) {
                    SPUtils.getInstance().put("CityId", String.valueOf(mCId));
                    SPUtils.getInstance().put("City", mCity);
                    mTvChengshi.setText(mCity);
                    getDataFromBanner();
                } else {
                    mCId = cityId[0];//城市id
                    mCity = items[0];//城市名称
                    SPUtils.getInstance().put("CityId", String.valueOf(mCId));
                    SPUtils.getInstance().put("City", mCity);
                    mTvChengshi.setText(mCity);
                    getDataFromBanner();
                }
                alertDialog.dismiss();
            }
        });
        alertBuilder.setCancelable(false);
        alertDialog = alertBuilder.create();
        alertDialog.show();
    }

    //=================================================获取用户信息=================================================\\
    void getDataFromNet(final String username) {
        SortedMap<Object, Object> parameters = new TreeMap<>();
        String randomStr = com.qiangyu.utils.MD5Util.getRandomStr();
        String timeStamp = com.qiangyu.utils.MD5Util.getTimeStamp();
        String characterEncoding = "UTF-8";
        parameters.put("mobile", username);
        parameters.put("nonceStr", randomStr);
        parameters.put("timeStamp", timeStamp);
        String mySign = com.qiangyu.utils.MD5Util.createSign(characterEncoding, parameters);

        OkHttpUtils
                .post()
                .url(Constants.QIANGYU_URL + "GetUserInfoByMobile")
                .addParams("mobile", username)
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
                        ToastUtil.toastCenter(mContext, "网络连接失败,请稍后再试...");
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
                        processData1(response);
                    }
                });

    }

    private void processData1(String json) {
        Loading.endLoad();
        JsonMemberInfoData jsonMemberInfoData = JSON.parseObject(json, JsonMemberInfoData.class);
        MemberInfo memberInfo = jsonMemberInfoData.getResult().getMemberInfo().get(0);
        //获取会员类型
        int type = memberInfo.getType();//0:施工队,1:普通会员
        SPUtils.getInstance().put("type", String.valueOf(type));
        //获取会员id
        int mid = memberInfo.getId();
        SPUtils.getInstance().put("mid", mid);
        //获取用户加密后的密码
        String signInPwd = memberInfo.getSignInPwd();
        SPUtils.getInstance().put("signInPwd", signInPwd);
    }

    private void initListener() {

        //置顶的监听
        mIbTop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //回到顶部
                mRvHome.scrollToPosition(0);
            }
        });

        /*
        et_earch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
         if(actionId == EditorInfo.IME_ACTION_SEARCH){
        //如果actionId是搜索的id，则进行下一步的操作
          doSomething() }
         return false; }
        });
        */

        //搜索的监听
        mSearchHome.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    //如果actionId是搜索的id，则进行下一步的操作
                    String Search = mSearchHome.getText().toString().trim();
                    if (!StringUtils.isNotEmpty(Search)) {
                        ToastUtil.toastCenter(mContext, "搜索内容您还没有输入哦");
                    } else {
                        Intent intent = new Intent(mContext, SearchActivity.class);
                        intent.putExtra("Search", Search);
                        startActivity(intent);
                    }
                }
                return false;
            }
        });

        //切换城市
        mTvChengshi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String city = SPUtils.getInstance().getString("City");
                String qihuanhuoxuanze;
                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(mContext);
                if (StringUtils.isNotEmpty(city)) {
                    qihuanhuoxuanze = "切换城市";
                    alertBuilder.setTitle("您当前选择的城市是: " + city);
                } else {
                    qihuanhuoxuanze = "选择城市";
                    alertBuilder.setTitle("您当前还未选择城市");
                }

                alertBuilder.setPositiveButton(qihuanhuoxuanze, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(mContext, CityListActivity.class);
                        startActivityForResult(intent, 1);
                    }
                });
                alertBuilder.setCancelable(false);
                alertDialog = alertBuilder.create();
                alertDialog.show();
            }
        });

        mTv_network_anomaly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //网络异常重新加载
                String cityId = SPUtils.getInstance().getString("CityId");
                mTv_network_anomaly.setVisibility(View.GONE);
                if (StringUtils.isNotEmpty(cityId)) {
                    getDataFromBanner();
                } else {
                    String city = SPUtils.getInstance().getString("City");
                    String qihuanhuoxuanze;
                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(mContext);
                    if (StringUtils.isNotEmpty(city)) {
                        qihuanhuoxuanze = "切换城市";
                        alertBuilder.setTitle("您当前选择的城市是: " + city);
                    } else {
                        qihuanhuoxuanze = "选择城市";
                        alertBuilder.setTitle("您当前还未选择城市");
                    }

                    alertBuilder.setPositiveButton(qihuanhuoxuanze, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent intent = new Intent(mContext, CityListActivity.class);
                            startActivityForResult(intent, 1);
                        }
                    });
                    alertBuilder.setCancelable(false);
                    alertDialog = alertBuilder.create();
                    alertDialog.show();
                }
            }
        });
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

    //轮播
    public JsonBannerBeanData.Result getBannerResult() {
        return mBannerResult;
    }

    public void setBannerResult(JsonBannerBeanData.Result bannerResult) {
        mBannerResult = bannerResult;
    }

    //导航
    public JsonCategoryBeanData.Result getCategoryResult() {
        return mCategoryResult;
    }

    public void setCategoryResult(JsonCategoryBeanData.Result categoryResult) {
        mCategoryResult = categoryResult;
    }

    //热卖
    public JsonHotBeanData.Result getHotResult() {
        return mHotResult;
    }

    public void setHotResult(JsonHotBeanData.Result hotResult) {
        mHotResult = hotResult;
    }

    //推荐
    public JsonTjBeanData.Result getTjResult() {
        return mTjResult;
    }

    public void setTjResult(JsonTjBeanData.Result tjResult) {
        mTjResult = tjResult;
    }
}
