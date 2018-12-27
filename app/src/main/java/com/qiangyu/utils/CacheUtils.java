package com.qiangyu.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import static android.support.constraint.Constraints.TAG;


/**
 * 缓存工具类
 */
public class CacheUtils {

    public static final String QIANGYU = "qiangyu";

    /**
     * 得到保存的String类型的数据
     * @param context
     * @param key
     * @return
     */
    public static String getString(Context context, String key) {
        SharedPreferences sp = context.getSharedPreferences(QIANGYU, Context.MODE_PRIVATE);
        return sp.getString(key,"");
    }

    /**
     * 保存String类型数据
     * @param context   上下文
     * @param key       键
     * @param value     值
     */
    public static void saveString(Context context, String key,String value) {
        SharedPreferences sp = context.getSharedPreferences(QIANGYU, Context.MODE_PRIVATE);
        sp.edit().putString(key,value).commit();
    }
}
