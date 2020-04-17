package com.landleaf.everyday.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;

import androidx.annotation.Nullable;

import com.landleaf.everyday.R;

public class CircleTempView extends View {

    private Paint excirclePaint;
    private int tvExcircleRadius;
    private int tvExcircleWidth;
    private String strType;
    private String strUnit;
    private Paint incirclePaint;
    private int tvIncircleWidth;
    private int tvIncircleBgColor;
    private int tvIncircleColor;
    private Paint textPaint;
    private RectF arcRectF;
    private Rect textBoundRect;
    private float mCurData;
    private ValueAnimator valueAnimator;
    private Paint textPaintUnit;

    public CircleTempView(Context context) {
        this(context, null);
    }

    public CircleTempView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CircleTempView);
        tvExcircleRadius = typedArray.getDimensionPixelOffset(R.styleable.CircleTempView_tvExcircleRadius, 120);
        tvExcircleWidth = typedArray.getDimensionPixelOffset(R.styleable.CircleTempView_tvExcircleWidth, 5);
        tvIncircleWidth = typedArray.getDimensionPixelOffset(R.styleable.CircleTempView_tvIncircleWidth, 15);
        int tvTempTextSize = typedArray.getDimensionPixelOffset(R.styleable.CircleTempView_tvTempTextSize, 32);
        int tvExcircleColor = typedArray.getColor(R.styleable.CircleTempView_tvExcircleColor, 0xFF054A87);
        tvIncircleBgColor = typedArray.getColor(R.styleable.CircleTempView_tvIncircleBgColor, 0xFF235B8F);
        tvIncircleColor = typedArray.getColor(R.styleable.CircleTempView_tvIncircleColor, 0xFF895FF4);
        int tvTempColor = typedArray.getColor(R.styleable.CircleTempView_tvTempColor, Color.GRAY);
        strType = typedArray.getString(R.styleable.CircleTempView_tvTextType);
        strUnit = typedArray.getString(R.styleable.CircleTempView_tvTextUnit);
        if (TextUtils.isEmpty(strType)) {
            strType = "室外温度";
        }
        if (TextUtils.isEmpty(strUnit)) {
            strUnit = "℃";
        }

        typedArray.recycle();

        excirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        excirclePaint.setStyle(Paint.Style.FILL);
        excirclePaint.setColor(tvExcircleColor);
        BlurMaskFilter maskfilterOuter = new BlurMaskFilter(tvExcircleWidth, BlurMaskFilter.Blur.OUTER);
        excirclePaint.setMaskFilter(maskfilterOuter);
        setLayerType(LAYER_TYPE_SOFTWARE, excirclePaint);

        incirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        incirclePaint.setStyle(Paint.Style.STROKE);
        incirclePaint.setStrokeWidth(tvIncircleWidth);
        incirclePaint.setStrokeCap(Paint.Cap.ROUND);

        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setColor(tvTempColor);
        textPaint.setTextSize(tvTempTextSize);
        textPaint.setTypeface(Typeface.DEFAULT_BOLD);

        textPaintUnit = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaintUnit.setStyle(Paint.Style.FILL);
        textPaintUnit.setColor(tvTempColor);
        textPaintUnit.setTextSize(tvTempTextSize/2);
        textPaintUnit.setTypeface(Typeface.DEFAULT_BOLD);

        //圓弧的外接矩形
        arcRectF = new RectF();
        //文字的边界矩形
        textBoundRect = new Rect();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(measureDimension(widthMeasureSpec), measureDimension(heightMeasureSpec));
    }

    private int measureDimension(int measureSpec) {
        int result;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        } else {
            result = tvExcircleRadius * 2;
            if (specMode == MeasureSpec.AT_MOST) {
                result = Math.min(result, specSize);
            }
        }
        return result;
    }

    boolean isFloat;
    public void setProgress(final float value, boolean isFloat) {
        this.isFloat = isFloat;
        valueAnimator = ValueAnimator.ofFloat(0, value);
        valueAnimator.setInterpolator(new OvershootInterpolator());
        valueAnimator.setDuration(3000);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float value = (float) valueAnimator.getAnimatedValue();
                mCurData = (float) (Math.round(value * 10)) / 10;
                if (mCurData>100){
                    mCurData = 100;
                    valueAnimator.end();
                }
                invalidate();
            }
        });
        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationCancel(Animator animation) {
                super.onAnimationCancel(animation);
                mCurData = value;
                invalidate();
            }
        });
        valueAnimator.start();
    }

    public void onStop() {
        if (valueAnimator != null)
            valueAnimator.cancel();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //绘制外圆
        canvas.drawCircle(getHeight() / 2, getWidth() / 2, tvExcircleRadius - tvExcircleWidth, excirclePaint);

        //绘制内圆及进度
        arcRectF.set(2 * tvIncircleWidth, 2 * tvIncircleWidth, getWidth() - 2 * tvIncircleWidth, getHeight() - 2 * tvIncircleWidth);
        incirclePaint.setColor(tvIncircleBgColor);
        canvas.drawArc(arcRectF, 0, 360, false, incirclePaint);
        incirclePaint.setColor(tvIncircleColor);
        canvas.drawArc(arcRectF, 90, mCurData*360/100, false, incirclePaint);

        //绘制内部文字
        int value = (int) mCurData;
        String data = isFloat? String.valueOf(mCurData):String.valueOf(value);
        textPaint.getTextBounds(data, 0, data.length(), textBoundRect);
        int height = textBoundRect.height();
        int width = textBoundRect.width();
        textPaintUnit.getTextBounds(strUnit,0,strUnit.length(),textBoundRect);
        canvas.drawText(data, getWidth() / 2 - (textBoundRect.width()+width) / 2, getHeight() / 2, textPaint);
        canvas.drawText(strUnit,getWidth()/2+textBoundRect.width()/2,getHeight()/2,textPaintUnit);
        textPaintUnit.getTextBounds(strType, 0, strType.length(), textBoundRect);
        canvas.drawText(strType, getWidth() / 2 - textBoundRect.width() / 2, getHeight() / 2 + textBoundRect.height()*2, textPaintUnit);
    }
}
