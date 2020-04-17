package com.landleaf.everyday.widget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ViewAnimator;

import androidx.annotation.Nullable;

public class AliPayView extends View {

    private Path mCirclePath, mDstPath;
    private Paint mPaint;
    private PathMeasure mPathMeasure;
    private Float mCurAnimValue;
    private int mCentX = 100;
    private int mCentY = 100;
    private int mRadius = 50;

    public AliPayView(Context context) {
        this(context,null);
    }

    public AliPayView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        setLayerType(LAYER_TYPE_SOFTWARE, null);

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(4);
        mPaint.setColor(Color.BLACK);

        mDstPath = new Path();
        mCirclePath = new Path();

        mCirclePath.addCircle(mCentX, mCentY, mRadius, Path.Direction.CW);

        mCirclePath.moveTo(mCentX - mRadius / 2, mCentY);
        mCirclePath.lineTo(mCentX, mCentY + mRadius / 2);
        mCirclePath.lineTo(mCentX + mRadius / 2, mCentY - mRadius / 3);

        mPathMeasure = new PathMeasure();
        //forceClose 是否闭合，对于绘制的path不影响，但是为true会将闭合部分计算到Path的长度中 比如绘制一个三角形的ab边 false geeLength = a+b,true = a+b+c
        mPathMeasure.setPath(mCirclePath,false);

        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 2);
        valueAnimator.setDuration(4000);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mCurAnimValue = (Float) animation.getAnimatedValue();
                invalidate();
            }
        });
        valueAnimator.start();
    }

    boolean isNext = false;
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(Color.WHITE);
        if (mCurAnimValue<1){
            float stop = mPathMeasure.getLength() * mCurAnimValue;
            mPathMeasure.getSegment(0,stop,mDstPath,true);
        }else {
            if (!isNext){
                mPathMeasure.getSegment(0,mPathMeasure.getLength(),mDstPath,true);
                //切换到折线路径
                mPathMeasure.nextContour();
                isNext = true;
            }
            float stop = mPathMeasure.getLength() * (mCurAnimValue - 1);
            mPathMeasure.getSegment(0,stop,mDstPath,true);
        }
        canvas.drawPath(mDstPath,mPaint);


    }
}
