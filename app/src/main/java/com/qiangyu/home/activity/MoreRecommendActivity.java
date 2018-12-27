package com.qiangyu.home.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.flyco.tablayout.SlidingTabLayout;
import com.qiangyu.R;
import com.qiangyu.home.fragment.MoreRecommendFragment;
import com.qiangyu.home.utils.ViewFindUtils;

import java.util.ArrayList;

public class MoreRecommendActivity extends FragmentActivity implements View.OnClickListener {

    private ImageButton ibMoreRecommendBack;
    private TextView tvSearchMoreRecommend;
    private ArrayList<Fragment> mFragments = new ArrayList<>();
    private MyPagerAdapter mAdapter;
    private ViewPager mVp_recommend_content;
    private SlidingTabLayout mStl_recommend_option;
    private View mDecorView;
    private final String[] mTitles = {
            "最新", "销量", "价格"
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_recommend);
        initView();
    }

    private void initView() {
        ibMoreRecommendBack = findViewById(R.id.ib_more_selling_back);
        tvSearchMoreRecommend = findViewById(R.id.tv_search_more_selling);
        mDecorView = getWindow().getDecorView();
        mVp_recommend_content = ViewFindUtils.find(mDecorView, R.id.vp_recommend_content);
        ibMoreRecommendBack.setOnClickListener(this);
        tvSearchMoreRecommend.setOnClickListener(this);
        for (int i = 0; i < mTitles.length; i++) {
            mFragments.add(new MoreRecommendFragment());
        }
        mAdapter = new MyPagerAdapter(getSupportFragmentManager());
        mVp_recommend_content.setAdapter(mAdapter);
        mStl_recommend_option = ViewFindUtils.find(mDecorView, R.id.stl_recommend_option);
        mStl_recommend_option.setViewPager(mVp_recommend_content);
    }

    @Override
    public void onClick(View v) {
        if (v == ibMoreRecommendBack) {
            System.gc();
            finish();
        }
        if (v == tvSearchMoreRecommend) {
            Toast.makeText(getApplicationContext(), "搜索!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    private class MyPagerAdapter extends FragmentPagerAdapter {
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
    }

}
