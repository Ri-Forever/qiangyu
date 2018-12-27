package com.qiangyu.home.activity;

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
import android.widget.TextView;
import android.widget.Toast;

import com.flyco.tablayout.SlidingTabLayout;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.qiangyu.R;
import com.qiangyu.home.fragment.MoreSellingFragment;
import com.qiangyu.home.utils.ViewFindUtils;

import java.util.ArrayList;

public class MoreSellingActivity extends FragmentActivity implements View.OnClickListener {

    private static final String TAG = "MoreSellingActivity";
    private ImageButton mIb_more_selling_back;
    private TextView mTv_search_more_selling;
    private ArrayList<Fragment> mFragments = new ArrayList<>();
    private MyPagerAdapter mAdapter;
    private SlidingTabLayout mStl_selling_option;
    private ViewPager mVp_selling_content;
    private View mDecorView;
    private final String[] mTitles = {
            "最新", "销量", "价格"
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_selling);

        initView();
    }

    private void initView() {
        mIb_more_selling_back = findViewById(R.id.ib_more_selling_back);
        mTv_search_more_selling = findViewById(R.id.tv_search_more_selling);

        mDecorView = getWindow().getDecorView();
        mStl_selling_option = ViewFindUtils.find(mDecorView, R.id.stl_selling_option);
        mVp_selling_content = ViewFindUtils.find(mDecorView, R.id.vp_selling_content);
        mStl_selling_option.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int position) {
                Log.d(TAG, "选择的是 --- > " + position);
            }

            @Override
            public void onTabReselect(int position) {
                Log.d(TAG, "onTabReselect --- > " + position);
            }
        });
        mIb_more_selling_back.setOnClickListener(this);
        mTv_search_more_selling.setOnClickListener(this);
        for (int i = 0; i < mTitles.length; i++) {
            mFragments.add(new MoreSellingFragment());
        }
        mAdapter = new MyPagerAdapter(getSupportFragmentManager());
        mVp_selling_content.setAdapter(mAdapter);
        mStl_selling_option.setViewPager(mVp_selling_content);
    }

    @Override
    public void onClick(View v) {
        if (v == mIb_more_selling_back) {
            System.gc();
            finish();
        }
        if (v == mTv_search_more_selling) {
            Toast.makeText(getApplicationContext(), "搜索!", Toast.LENGTH_SHORT).show();
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
