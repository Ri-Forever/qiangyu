package com.qiangyu.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {

    //13位时间戳转换日期
    public static String timeStampDate(long timeStamp) {
        SimpleDateFormat sdr = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
        @SuppressWarnings("unused")
        String times = sdr.format(new Date((timeStamp / 1000) * 1000L));
        return times;
    }
}
