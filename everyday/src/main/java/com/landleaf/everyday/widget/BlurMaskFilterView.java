package com.landleaf.everyday.widget;

import android.content.Context;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Region;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

public class BlurMaskFilterView extends View {

    private BlurMaskFilter maskfilterOuter;
    private BlurMaskFilter maskfilterNormal;

    public BlurMaskFilterView(Context context) {
        this(context, null);
    }

    public BlurMaskFilterView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    Paint mPaint;
    Paint textPaint;

    private void init() {
        setLayerType(LAYER_TYPE_SOFTWARE, null);
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        maskfilterOuter = new BlurMaskFilter(5, BlurMaskFilter.Blur.OUTER);
        maskfilterNormal = new BlurMaskFilter(5, BlurMaskFilter.Blur.NORMAL);
        mPaint.setStrokeWidth(2);
        mPaint.setColor(0xFF45AAF5);

        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setStyle(Paint.Style.STROKE);
        textPaint.setStrokeWidth(1);
        textPaint.setColor(0xFF45AAF5);

    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.save();
//        canvas.drawArc(new RectF(5,5,205,205),90,360,false,textPaint);

        canvas.drawCircle(100,100,90,textPaint);
        mPaint.setMaskFilter(maskfilterOuter);
        canvas.drawCircle(100,100,85,mPaint);

//        canvas.drawCircle(60,60,25,textPaint);
//        canvas.drawCircle(35,35,25,textPaint);
//        canvas.drawCircle(120,120,50,textPaint);
//        canvas.drawCircle(100,100,70,textPaint);

        Path path = new Path();
        path.addCircle(60,60,25,Path.Direction.CW);
        Path path1 = new Path();
        path1.addCircle(35,35,25,Path.Direction.CW);
        path1.op(path, Path.Op.INTERSECT);
        mPaint.setMaskFilter(maskfilterNormal);
        canvas.drawPath(path1,mPaint);

//        canvas.drawArc(new RectF(5,5,205,205),90,180,false,mPaint);

        path.addCircle(100,100,70,Path.Direction.CW);
        path1.addCircle(120,120,50,Path.Direction.CW);
        path1.op(path, Path.Op.DIFFERENCE);
        canvas.drawPath(path1,mPaint);

//        mPaint.setShader(new LinearGradient(200,100,10,100,0x00000000,0x6645AAF5, Shader.TileMode.CLAMP));
//        canvas.drawLine(190,100,10,100,mPaint);
//        canvas.restore();
//        canvas.drawText("lao da la la la la la...",100,100,textPaint);
    }
}
