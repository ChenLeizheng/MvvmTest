package com.landleaf.everyday.widget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.OvershootInterpolator;

import androidx.annotation.Nullable;

import com.landleaf.everyday.R;

/**
 * beetle项目半圆形进度条
 */
public class CirclePercentView extends View {

    private Paint arcProgressPaint;
    private Paint mCirclePaint;
    private Paint textPaint;
    private int cvCircleRadius;
    private int cvPaintWidth;
    private RectF arcRectF;
    private Rect textBoundRect;
    private int cvMax;
    private int cvMin;
    private String centerText;
    private float currentValue;
    private int currentValeColor;
    private int centerTextColor;
    private int endColor;
    private int startColor;
    private int centerTextSize;
    private int currentValueTextSize;
    private float currentDegrees;
    private String dataUnit = "";
    private Paint circlePaint;
    private ValueAnimator valueAnimator;
    public static final int LAYOUT_TOP = 0;
    public static final int LAYOUT_BOTTOM = 1;
    public static final int LAYOUT_LEFT = 2;
    public static final int LAYOUT_RIGHT = 3;
    private int cvLayout;


    public CirclePercentView(Context context) {
        this(context, null);
    }

    public CirclePercentView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CirclePercentView);
        initView(typedArray);
    }

    private void initView(TypedArray typedArray) {
        cvCircleRadius = typedArray.getDimensionPixelOffset(R.styleable.CirclePercentView_cvCircleRadius, 100);
        cvPaintWidth = typedArray.getDimensionPixelOffset(R.styleable.CirclePercentView_cvPaintWidth, 10);
        currentValueTextSize = typedArray.getDimensionPixelOffset(R.styleable.CirclePercentView_cvCurrentValueTextSize, 28);
        centerTextSize = typedArray.getDimensionPixelOffset(R.styleable.CirclePercentView_cvCenterTextSize, 12);
        startColor = typedArray.getColor(R.styleable.CirclePercentView_cvStartColor, 0xFF4BB1FF);
        endColor = typedArray.getColor(R.styleable.CirclePercentView_cvEndColor, 0x990000FF);
        centerTextColor = typedArray.getColor(R.styleable.CirclePercentView_cvCenterTextColor, Color.LTGRAY);
        currentValeColor = typedArray.getColor(R.styleable.CirclePercentView_cvCurrentValueTextColor, Color.WHITE);
        int bgCircleColor = typedArray.getColor(R.styleable.CirclePercentView_cvBackgroundColor, 0xFF45AAF5);
        currentValue = typedArray.getFloat(R.styleable.CirclePercentView_cvCurrentValue, 22f);
        cvMax = typedArray.getInteger(R.styleable.CirclePercentView_cvMax, 50);
        cvMin = typedArray.getInteger(R.styleable.CirclePercentView_cvMin, 0);
        centerText = typedArray.getString(R.styleable.CirclePercentView_cvCenterText);
        if (TextUtils.isEmpty(centerText)) {
            centerText = "室内温度";
        }
        cvLayout = typedArray.getInt(R.styleable.CirclePercentView_cvLayout, 0);
        arcProgressPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        arcProgressPaint.setStyle(Paint.Style.STROKE);
        arcProgressPaint.setStrokeWidth(cvPaintWidth);

        mCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mCirclePaint.setStyle(Paint.Style.FILL);
        mCirclePaint.setColor(bgCircleColor);
        BlurMaskFilter maskfilterOuter = new BlurMaskFilter(cvPaintWidth, BlurMaskFilter.Blur.OUTER);
        mCirclePaint.setMaskFilter(maskfilterOuter);
        setLayerType(LAYER_TYPE_SOFTWARE, mCirclePaint);

        circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        circlePaint.setStyle(Paint.Style.STROKE);
        circlePaint.setStrokeWidth(1);
        circlePaint.setColor(bgCircleColor);

        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setStyle(Paint.Style.FILL);

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
            result = cvCircleRadius * 2;
            if (specMode == MeasureSpec.AT_MOST) {
                result = Math.min(result, specSize);
            }
        }
        return result;
    }

    public void setProgress(float progress, String dataUnit) {
        this.dataUnit = dataUnit;
        valueAnimator = ValueAnimator.ofFloat(cvMin, progress);
        valueAnimator.setInterpolator(new OvershootInterpolator());
        valueAnimator.setDuration(3000);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                currentValue = (float) Math.round(value * 10) / 10;
                if (currentValue > cvMax){
                    currentValue = cvMax;
                    animation.end();
                }
                currentDegrees = currentValue * 180 / (cvMax - cvMin);
                Log.d("CirclePercentView", "currentValue:" + currentValue);
                invalidate();
            }
        });
        valueAnimator.start();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //上下方向
        if (cvLayout < 2) {
            drawLayoutTop(canvas);
            return;
        }
        //左右方向
        drawLayoutLeft(canvas);
    }

    private void drawLayoutTop(Canvas canvas) {
        canvas.save();
        boolean layoutTop = (cvLayout == LAYOUT_TOP);
        //绘制背景圆环
        arcRectF.set(0, layoutTop ? 0 : getWidth() / 2, getWidth(), layoutTop ? getWidth() / 2 : getWidth());
        canvas.clipRect(arcRectF);
        canvas.drawCircle(getWidth() / 2f, getWidth() / 2f, cvCircleRadius, circlePaint);
        canvas.drawCircle(getWidth() / 2f, getWidth() / 2f, cvCircleRadius - cvPaintWidth, mCirclePaint);
        canvas.restore();
        arcRectF.set(getWidth() / 2f - cvCircleRadius + cvPaintWidth / 2f, getHeight() / 2f - cvCircleRadius + cvPaintWidth / 2f
                , getWidth() / 2f + cvCircleRadius - cvPaintWidth / 2f, getHeight() / 2f + cvCircleRadius - cvPaintWidth / 2f);

        //绘制进度圆环
        arcProgressPaint.setShader(new LinearGradient(0, getHeight() / 2, getWidth(), getHeight() / 2, startColor, endColor, Shader.TileMode.CLAMP));
        canvas.drawArc(arcRectF, 180, layoutTop ? currentDegrees : -currentDegrees, false, arcProgressPaint);
        //TODO  test delete 渐变色显示
        canvas.drawLine(0, getHeight() / 2 - 10, getWidth(), getHeight() / 2 - 10, arcProgressPaint);

        //绘制最小值
        textPaint.setColor(currentValeColor);
        textPaint.setTextSize(centerTextSize);
        String minStr = String.valueOf(cvMin);
        textPaint.getTextBounds(minStr, 0, minStr.length(), textBoundRect);
        canvas.drawLine(cvPaintWidth * 3 / 2f, layoutTop ? getHeight() / 2 - textBoundRect.height() / 2 : getHeight() / 2 + textBoundRect.height() / 2, cvPaintWidth * 2, layoutTop ? getHeight() / 2 - textBoundRect.height() / 2 : getHeight() / 2 + textBoundRect.height() / 2, textPaint);
        float textMinWidth = textBoundRect.width();
        float textMinHeight = textBoundRect.height();
        canvas.drawText(minStr, cvPaintWidth * 2 + textMinWidth / 2f, layoutTop ? getHeight() / 2 : getHeight() / 2 + textBoundRect.height(), textPaint);
        //绘制最大值
        String maxStr = String.valueOf(cvMax);
        textPaint.getTextBounds(maxStr, 0, maxStr.length(), textBoundRect);
        canvas.drawLine(getWidth() - cvPaintWidth * 3 / 2f, layoutTop ? getHeight() / 2 - textBoundRect.height() / 2 : getHeight() / 2 + textBoundRect.height() / 2, getWidth() - cvPaintWidth * 2, layoutTop ? getHeight() / 2 - textBoundRect.height() / 2 : getHeight() / 2 + textBoundRect.height() / 2, textPaint);
        canvas.drawText(maxStr, getWidth() - cvPaintWidth * 2 - textBoundRect.width() - textMinWidth / 2, layoutTop ? getHeight() / 2 : getHeight() / 2 + textBoundRect.height(), textPaint);

        textPaint.setTextSize(currentValueTextSize);
        textPaint.setColor(currentValeColor);
        String data = String.valueOf(currentValue) + dataUnit;
        textPaint.getTextBounds(data, 0, data.length(), textBoundRect);
        int heightData = textBoundRect.height();
        canvas.drawText(data, getWidth() / 2f - textBoundRect.width() / 2, layoutTop ? getHeight() / 2f - textBoundRect.height() - textMinHeight : getHeight() / 2f + textBoundRect.height(), textPaint);

        //绘制文字
        textPaint.setTextSize(centerTextSize);
        textPaint.setColor(centerTextColor);
        textPaint.getTextBounds(centerText, 0, centerText.length(), textBoundRect);
        canvas.drawText(centerText, getWidth() / 2f - textBoundRect.width() / 2, layoutTop ? getHeight() / 2f - textMinHeight : getHeight() / 2f + textBoundRect.height() + textMinHeight + heightData, textPaint);
    }

    private void drawLayoutLeft(Canvas canvas) {
        canvas.save();
        boolean layoutLeft = (cvLayout == LAYOUT_LEFT);
        //绘制背景圆环
        arcRectF.set(layoutLeft ? 0 : getWidth() / 2f, 0, layoutLeft ? getWidth() / 2f : getWidth(), getWidth());
        canvas.clipRect(arcRectF);
        canvas.drawCircle(getWidth() / 2f, getWidth() / 2f, cvCircleRadius, circlePaint);
        canvas.drawCircle(getWidth() / 2f, getWidth() / 2f, cvCircleRadius - cvPaintWidth, mCirclePaint);
        canvas.restore();
        arcRectF.set(getWidth() / 2f - cvCircleRadius + cvPaintWidth / 2f, getHeight() / 2f - cvCircleRadius + cvPaintWidth / 2f
                , getWidth() / 2f + cvCircleRadius - cvPaintWidth / 2f, getHeight() / 2f + cvCircleRadius - cvPaintWidth / 2f);

        //绘制进度圆环
        arcProgressPaint.setShader(new LinearGradient(getWidth() / 2f, getHeight() - cvCircleRadius / 2f, getWidth() / 2f, cvCircleRadius / 2f, startColor, endColor, Shader.TileMode.CLAMP));
        canvas.drawArc(arcRectF, 90, layoutLeft ? currentDegrees : -currentDegrees, false, arcProgressPaint);
        //TODO  test delete 渐变色显示
        canvas.drawLine(getWidth() / 2f + 10, 0, getWidth() / 2f + 10, getHeight(), arcProgressPaint);

        //绘制文字
        textPaint.setTextSize(currentValueTextSize);
        textPaint.setColor(currentValeColor);
        String data = String.valueOf(currentValue) + dataUnit;
        textPaint.getTextBounds(data, 0, data.length(), textBoundRect);
        canvas.drawText(data, layoutLeft ? getWidth() / 2f - textBoundRect.width() : getWidth() / 2f, getHeight() / 2f, textPaint);
        textPaint.setColor(centerTextColor);
        textPaint.setTextSize(centerTextSize);
        canvas.drawText(centerText, layoutLeft ? getWidth() / 2f - textBoundRect.width() : getWidth() / 2f, getHeight() / 2f + textBoundRect.height(), textPaint);

        //绘制最小值
        canvas.drawLine(getWidth() / 2f, getHeight() - cvPaintWidth * 3 / 2f, getWidth() / 2f, getHeight() - cvPaintWidth * 2, textPaint);
        String minStr = String.valueOf(cvMin);
        textPaint.getTextBounds(minStr, 0, minStr.length(), textBoundRect);
        canvas.drawText(minStr, getWidth() / 2f - textBoundRect.width() / 2f, getHeight() - cvPaintWidth * 2 - textBoundRect.height() / 2, textPaint);
        //绘制最大值
        canvas.drawLine(getWidth() / 2f, cvPaintWidth * 3 / 2f, getWidth() / 2f, cvPaintWidth * 2, textPaint);
        String maxStr = String.valueOf(cvMax);
        textPaint.getTextBounds(maxStr, 0, maxStr.length(), textBoundRect);
        canvas.drawText(maxStr, getWidth() / 2f - textBoundRect.width() / 2f, cvPaintWidth * 2 + textBoundRect.height() * 3 / 2f, textPaint);
    }
}
