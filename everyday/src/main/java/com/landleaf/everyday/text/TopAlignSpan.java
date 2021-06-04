package com.landleaf.everyday.text;

import android.graphics.Rect;
import android.text.TextPaint;
import android.text.style.SuperscriptSpan;

public class TopAlignSpan extends SuperscriptSpan {

    /** 字体大小 */
    private float fontSizePx;

    //shift value, 0 to 1.0
    protected float shiftPercentage = 0;
    private Rect textRect = new Rect();
    private String textNormal = "2";

    public TopAlignSpan() {
    }

    //doesn't shift
    public TopAlignSpan(float fontSizePx) {
        this(fontSizePx, 0);
    }

    //sets the shift percentage
    public TopAlignSpan(float fontSizePx, float shiftPercentage) {
        if (shiftPercentage > 0.0 && shiftPercentage < 1.0) {
            this.shiftPercentage = shiftPercentage;
        }
        this.fontSizePx = fontSizePx;
    }

    @Override
    public void updateDrawState(TextPaint tp) {
        tp.getTextBounds(textNormal,0,1,textRect);
        //original ascent
        float ascent = textRect.top;

        //scale down the font
        tp.setTextSize(tp.getTextSize()/2f);
        tp.getTextBounds(textNormal,0,1,textRect);
        //get the new font ascent
        float newAscent = textRect.top;

        //move baseline to top of old font, then move down size of new font
        //adjust for errors with shift percentage
        //   tp.baselineShift += (ascent - ascent * shiftPercentage)
        //            - (newAscent - newAscent * shiftPercentage);
        //计算基线偏移量
        tp.baselineShift += (ascent - newAscent) * (1 - shiftPercentage);
    }

    @Override
    public void updateMeasureState(TextPaint tp) {
        updateDrawState(tp);
    }
}