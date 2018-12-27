package com.qiangyu.utils;

import com.qiangyu.base.BaseFragment;
import com.qiangyu.home.fragment.HomeFragment;
import com.qiangyu.my.fragment.MyFragment;
import com.qiangyu.shopcart.fragment.ShopCartFragment;

import java.util.HashMap;
import java.util.Map;

public class FragmentCreater {

    public static Map<Integer, BaseFragment> sCashes = new HashMap<>();


    public static BaseFragment getFragmentByPosition(int position) {
        BaseFragment baseFragment = sCashes.get(position);
        if (baseFragment != null) {
            return baseFragment;
        }
        switch (position) {
            case Constants.PAGER_HOME:
                baseFragment = new HomeFragment();
                break;
            case Constants.PAGER_SHOPCART:
                baseFragment = new ShopCartFragment();
                break;
            case Constants.PAGER_MY:
                baseFragment = new MyFragment();
                break;
        }
        sCashes.put(position, baseFragment);
        return baseFragment;
    }
}
