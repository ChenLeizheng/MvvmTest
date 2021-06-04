package com.landleaf.everyday.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.landleaf.everyday.R;

/**
 * Author：Lei on 2020/12/10
 */
public class SensorLinearLayout extends LinearLayout {

    public SensorLinearLayout(Context context) {
        this(context,null);
    }

    public SensorLinearLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.SensorLinearLayout);
        String type = typedArray.getString(R.styleable.SensorLinearLayout_sensorTvType);
        String data = typedArray.getString(R.styleable.SensorLinearLayout_sensorTvData);
        String unit = typedArray.getString(R.styleable.SensorLinearLayout_sensorTvUnit);
        LinearLayout view = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.layout_sensor_view, this,true);
        view.setBackgroundResource(R.drawable.img_tips_handler);
        view.setGravity(Gravity.CENTER_VERTICAL);
        TextView tvDataType = view.findViewById(R.id.tvDataType);
        TextView tvData = view.findViewById(R.id.tvData);
        TextView tvDataUnit = view.findViewById(R.id.tvDataUnit);
        if (!TextUtils.isEmpty(type)){
            tvDataType.setText(type);
        }
        if (!TextUtils.isEmpty(data)){
            tvData.setText(data);
        }
        if (!TextUtils.isEmpty(unit)){
            tvDataUnit.setText(unit);
        }
    }
}
