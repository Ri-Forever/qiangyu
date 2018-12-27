package com.qiangyu.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.qiangyu.utils.Constants;
import com.qiangyu.utils.FragmentCreater;

public class MainContentPagerAdapter extends FragmentPagerAdapter {

    public MainContentPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {
        return FragmentCreater.getFragmentByPosition(i);
    }

    @Override
    public int getCount() {
        return Constants.TABS_COUNT;
    }
}
