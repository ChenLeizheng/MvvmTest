package com.landleaf.normal.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * 时间工具类
 */
public class TimeUtil {
    /**
     * 获取当前日期是星期几<br>
     *
     * @param date date
     * @return 当前日期是星期几
     */
    public static String getWeekOfDate(Date date) {
        String[] weekDays = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (w < 0)
            w = 0;
        return weekDays[w];
    }

    private static String timestampToDate(long time, String pattern) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern, Locale.CHINA);
        Date date = new Date(time);
        return simpleDateFormat.format(date);
    }

    //时间戳转换成时间
    public static String timestampToDate(long time) {
        return timestampToDate(time, "yyyy-MM-dd HH:mm:ss");
    }

    //时间戳显示的文件名
    public static String timestampToFileName(long time) {
        return timestampToDate(time, "yyyy_MM_dd_HH_mm_ss");
    }

    //生成以日期为名的文件夹
    public static String timestampToDirName(long time) {
        return timestampToDate(time, "yyyy_MM_dd");
    }
}
