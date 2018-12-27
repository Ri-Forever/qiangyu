package com.qiangyu.home.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.qiangyu.R;
import com.qiangyu.home.adapter.TypeLeftAdapter;
import com.qiangyu.home.adapter.TypeRightAdapter;
import com.qiangyu.home.bean.Category;
import com.qiangyu.home.bean.JsonCategoryBeanData;
import com.qiangyu.home.bean.TypeBean;
import com.qiangyu.utils.Constants;
import com.qiangyu.utils.MD5Util;
import com.qiangyu.utils.SPUtils;
import com.qiangyu.utils.ToastUtil;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import okhttp3.Call;

public class ListActivity extends Activity {

    private ListView lv_left;
    private ListView lv_right;
    private TextView tv_list_back;
    private List<TypeBean.ResultBean> result;

    private TypeLeftAdapter leftAdapter;
    private boolean isFirst = true;
    private JsonCategoryBeanData.Result mCategoryResult;
    private List<Category> mCategorys;
    private List<Category> mCategorys1;
    private List<Category> mCategorys2;
    private Context mContext;
    private ImageButton mIb_back;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        this.mContext = this;
        initView();
    }

    private void initView() {
        lv_left = findViewById(R.id.lv_left);
        lv_right = findViewById(R.id.lv_right);
        mIb_back = findViewById(R.id.ib_back);
        tv_list_back = findViewById(R.id.tv_list_back);
        mIb_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        tv_list_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        getDataFromNav();
    }

    private void initListener(final TypeLeftAdapter adapter) {
        //点击监听
        lv_left.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                adapter.changeSelected(position);//刷新
                if (position != 0) {
                    isFirst = false;
                }
                Log.d("setOnItemClickListener", "点击的是 ===> " + position);
                //解析右边数据
                List<Category> categoryList = new ArrayList<>();
                int id1 = mCategorys.get(position).getId();
                for (Category category : mCategorys1) {
                    if (id1 == Integer.parseInt(category.getParentId())) {
                        categoryList.add(category);
                    }
                }
                TypeRightAdapter rightAdapter = new TypeRightAdapter(mContext, categoryList, mCategorys2);
                lv_right.setAdapter(rightAdapter);
                leftAdapter.notifyDataSetChanged();
            }
        });

        //选中监听
        lv_left.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                adapter.changeSelected(position);//刷新

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    //=================================================首页全部导航=================================================\\
    void getDataFromNav() {
        String cityId = SPUtils.getInstance().getString("CityId");
        SortedMap<Object, Object> parameters = new TreeMap<>();
        String randomStr = MD5Util.getRandomStr();
        String timeStamp = MD5Util.getTimeStamp();
        String characterEncoding = "UTF-8";
        parameters.put("shopId", cityId);
        parameters.put("isNav", "0");
        parameters.put("nonceStr", randomStr);
        parameters.put("timeStamp", timeStamp);
        String mySign = MD5Util.createSign(characterEncoding, parameters);

        OkHttpUtils
                .post()
                .url(Constants.QIANGYU_URL + "GetCategory")
                .addParams("shopId", cityId)
                .addParams("isNav", "0")
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
                        Log.d("getDataFromNet", "首页导航请求失败 === >" + e.getMessage());
                        ToastUtil.toastCenter(mContext,"网络加载异常,请稍后再试...");
                        //getDataFromNav();
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
        List<Category> categoryList = mCategoryResult.getCategory();
        mCategorys = new ArrayList();
        mCategorys1 = new ArrayList<>();
        mCategorys2 = new ArrayList<>();
        for (Category category : categoryList) {
            if (category.getLayer() == 1) {
                mCategorys.add(category);
            } else if (category.getLayer() == 2) {
                mCategorys1.add(category);
            } else if (category.getLayer() == 3) {
                mCategorys2.add(category);
            }
        }
        //解析左边数据
        leftAdapter = new TypeLeftAdapter(mContext, mCategorys);
        lv_left.setAdapter(leftAdapter);
        //解析右边数据
        List<Category> listCategory = new ArrayList<>();
        int id1 = mCategorys.get(0).getId();
        for (Category category : mCategorys1) {
            if (id1 == Integer.parseInt(category.getParentId())) {
                listCategory.add(category);
            }
        }
        TypeRightAdapter rightAdapter = new TypeRightAdapter(mContext, listCategory, mCategorys2);
        lv_right.setAdapter(rightAdapter);
        initListener(leftAdapter);
    }


}
