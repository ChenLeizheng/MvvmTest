package com.landleaf.everyday.utils;

import android.graphics.Paint;

/**
 * Created by chenyifei on 2018/1/17.
 */

public class ViewUtil {

    public static float getStringWidth(String str, Paint paint) {
        return paint.measureText(str);
    }

    public static float getStringHeight(Paint paint) {
        Paint.FontMetrics fr = paint.getFontMetrics();
        return (float) (Math.ceil(fr.descent - fr.top) + 2);  //ceil() 函数向上舍入为最接近的整数。
    }
}
