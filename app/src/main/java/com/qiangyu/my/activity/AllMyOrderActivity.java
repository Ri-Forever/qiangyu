package com.qiangyu.my.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.flyco.tablayout.SlidingTabLayout;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.qiangyu.R;
import com.qiangyu.home.utils.ViewFindUtils;
import com.qiangyu.my.fragment.FullOrderFragment;
import com.qiangyu.my.fragment.GoodsToBeReceivedFragment;
import com.qiangyu.my.fragment.PendingDeliveryFragment;
import com.qiangyu.my.fragment.PendingPaymentFragment;
import com.qiangyu.my.fragment.ToBeEvaluatedFragment;

import java.util.ArrayList;

public class AllMyOrderActivity extends FragmentActivity implements View.OnClickListener {

    private static final String TAG = "MoreSellingActivity";
    private ImageButton ib_all_my_order_back;
    private ArrayList<Fragment> mFragments = new ArrayList<>();
    private MyPagerAdapter mAdapter;
    private SlidingTabLayout stl_all_my_order;
    private ViewPager vp_all_my_order_content;
    private View mDecorView;
    private final String[] mTitles = {
            "全部", "待付款", "待发货",
            "待收货", "待评价"
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_my_order);

        initView();
    }

    private void initView() {
        ib_all_my_order_back = findViewById(R.id.ib_all_my_order_back);

        mDecorView = getWindow().getDecorView();
        stl_all_my_order = ViewFindUtils.find(mDecorView, R.id.stl_all_my_order);
        vp_all_my_order_content = ViewFindUtils.find(mDecorView, R.id.vp_all_my_order_content);
        ib_all_my_order_back.setOnClickListener(this);

        for (int i = 0; i < mTitles.length; i++) {
            if (i == 0) {
                //"全部"
                mFragments.add(new FullOrderFragment());
            } else if (i == 1) {
                //"待付款"
                mFragments.add(new PendingPaymentFragment());
            } else if (i == 2) {
                //"待发货"
                mFragments.add(new PendingDeliveryFragment());
            } else if (i == 3) {
                //"待收货"
                mFragments.add(new GoodsToBeReceivedFragment());
            } else if (i == 4) {
                //"待评价"
                mFragments.add(new ToBeEvaluatedFragment());
            }
        }

        mAdapter = new MyPagerAdapter(getSupportFragmentManager());
        vp_all_my_order_content.setAdapter(mAdapter);
        stl_all_my_order.setViewPager(vp_all_my_order_content);

        Intent intent = getIntent();
        String intentValue = intent.getStringExtra("panduan");
        if ("daifukuan".equals(intentValue)) {
            vp_all_my_order_content.setCurrentItem(1);
        } else if ("daishouhuo".equals(intentValue)) {
            vp_all_my_order_content.setCurrentItem(3);
        } else if ("quanbudingdan".equals(intentValue)) {
            vp_all_my_order_content.setCurrentItem(0);
        }
        stl_all_my_order.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int position) {
                Log.d(TAG, "选择的是 --- > " + position);
            }

            @Override
            public void onTabReselect(int position) {
                Log.d(TAG, "onTabReselect --- > " + position);
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v == ib_all_my_order_back) {
            System.gc();
            finish();
        }
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    public class MyPagerAdapter extends FragmentPagerAdapter {
        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mTitles[position];
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            return super.instantiateItem(container, position);

        }
    }

}
