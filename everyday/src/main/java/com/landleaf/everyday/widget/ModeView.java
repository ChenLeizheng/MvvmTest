package com.landleaf.everyday.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.landleaf.everyday.R;

public class ModeView extends LinearLayout {

    private LinearLayout llMode;
    private ImageView ivMode;
    private TextView tvMode;
    private String modeName;
    private int selectResourceId;
    private int unselectedResourceId;

    public ModeView(Context context) {
        this(context, null);
    }

    public ModeView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setGravity(Gravity.CENTER);
        setOrientation(LinearLayout.VERTICAL);
        View view = LayoutInflater.from(context).inflate(R.layout.layout_mode_view, this, true);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ModeView);
        selectResourceId = typedArray.getResourceId(R.styleable.ModeView_modeImageSelect, R.drawable.img_nt_mode_zhileng_on);
        unselectedResourceId = typedArray.getResourceId(R.styleable.ModeView_modeImageUnSelected, R.drawable.img_nt_mode_zhileng_off);
        modeName = typedArray.getString(R.styleable.ModeView_modeName);
        Log.d("ModeView", "aa"+modeName);
        if (TextUtils.isEmpty(modeName)){
            modeName = "制冷";
        }
        llMode = view.findViewById(R.id.llMode);
        ivMode = view.findViewById(R.id.ivMode);
        tvMode = view.findViewById(R.id.tvMode);
        tvMode.setText(modeName);
        ivMode.setImageResource(selectResourceId);
    }

    public void setSelect(boolean select){
        llMode.setBackgroundResource(select ? R.drawable.img_nt_mode_bg:0);
        ivMode.setImageResource(select?selectResourceId:unselectedResourceId);
        tvMode.setTextColor(select?0xFFFFFFFF:0xFF859BA7);
    }

}
