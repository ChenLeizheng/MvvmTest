package com.landleaf.everyday.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;


import com.landleaf.everyday.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * 时间显示  2020.05.20 星期三 10:30
 */
public class TimeView extends LinearLayout {

    private SimpleDateFormat HOUR_FORMAT = new SimpleDateFormat("HH:mm", Locale.CHINESE);
    private SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy.MM.dd", Locale.CHINESE);
    private static final int REFRESH_DELAY = 5000;
    private TextView hourView;
    private TextView dateView;
    private TextView weekView;

    public TimeView(Context context) {
        this(context,null);
    }

    public TimeView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_time_view, this,true);

        dateView = view.findViewById(R.id.dateView);
        weekView = view.findViewById(R.id.weekView);
        hourView = view.findViewById(R.id.hourView);
    }

    private final Runnable mTimeRefresher = new Runnable() {
        @Override
        public void run() {
            Date date = new Date();
            Log.d("TimeView", "dateView:" + dateView+","+date);
            dateView.setText(DATE_FORMAT.format(date));
            String strWeek = getWeekOfDate(date);
            weekView.setText(strWeek);
            hourView.setText(HOUR_FORMAT.format(date));
            postDelayed(this,REFRESH_DELAY);
        }
    };

    public void start() {
        post(mTimeRefresher);
    }


    public void stop() {
        removeCallbacks(mTimeRefresher);
    }

    /**
     * 获取当前日期是星期几<br>
     * @param date date
     * @return 当前日期是星期几
     */
    public String getWeekOfDate(Date date) {
        String[] weekDays = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (w < 0)
            w = 0;
        return weekDays[w];
    }
}
