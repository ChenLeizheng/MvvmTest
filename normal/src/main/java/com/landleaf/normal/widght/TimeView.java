package com.landleaf.normal.widght;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.landleaf.normal.R;
import com.landleaf.normal.utils.TimeUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class TimeView extends RelativeLayout {

    private static final String TAG = "TimeView";

    private static final int REFRESH_DELAY = 1000;

    private SimpleDateFormat HOUR_FORMAT = new SimpleDateFormat("HH: mm", Locale.CHINESE);

    private SimpleDateFormat MONTH_FORMAT = new SimpleDateFormat("yyyy.MM.dd", Locale.CHINESE);

    private TextView hourView;
    private TextView monthView;
    private TextView weekView;
    private Disposable disposable;

    public TimeView(Context context) {
        this(context,null);
    }

    public TimeView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        if (!isInEditMode()) {
            // 造成错误的代码段
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.TimeView);

            LayoutInflater layoutInflater = LayoutInflater.from(context);
            View view = layoutInflater.inflate(R.layout.layout_view_time, this);
            hourView = view.findViewById(R.id.myHourView);
            monthView = view.findViewById(R.id.myMonthView);
            weekView = view.findViewById(R.id.myWeekView);
            //size
            int hourSize = typedArray.getDimensionPixelOffset(R.styleable.TimeView_hourSize, 34);
            int monthSize = typedArray.getDimensionPixelOffset(R.styleable.TimeView_monthSize, 24);
            int weekSize = typedArray.getDimensionPixelOffset(R.styleable.TimeView_weekSize, 22);
            hourView.setTextSize(TypedValue.COMPLEX_UNIT_PX, hourSize);
            monthView.setTextSize(TypedValue.COMPLEX_UNIT_PX, monthSize);
            weekView.setTextSize(TypedValue.COMPLEX_UNIT_PX, weekSize);

            typedArray.recycle();
        }
    }

    public void start(){
        disposable = Observable.interval(1, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        Date date = new Date();
                        hourView.setText(HOUR_FORMAT.format(date));
                        monthView.setText(MONTH_FORMAT.format(date));
                        weekView.setText(TimeUtil.getWeekOfDate(date));
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.d(TAG, throwable.toString());
                    }
                });
    }

    public void stop(){
        if (disposable != null){
            disposable.dispose();
        }
    }
}
