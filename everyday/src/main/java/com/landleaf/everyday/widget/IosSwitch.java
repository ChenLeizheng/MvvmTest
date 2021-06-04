package com.landleaf.everyday.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.CheckBox;

import androidx.appcompat.widget.AppCompatCheckBox;

/**
 * Author：Lei on 2020/12/23
 */
public class IosSwitch extends AppCompatCheckBox {
    public IosSwitch(Context context) {
        super(context);
    }

    public IosSwitch(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean performClick() {
        return callOnClick();
    }
}
