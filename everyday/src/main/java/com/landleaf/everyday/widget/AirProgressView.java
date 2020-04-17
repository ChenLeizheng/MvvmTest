package com.landleaf.everyday.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.OvershootInterpolator;

import androidx.annotation.Nullable;

import com.landleaf.everyday.R;

/**
 * beetle项目空气质量进度显示
 */
public class AirProgressView extends View {

    private Paint progressTextPaint;
    private Paint progressPaint;
    private Paint textPaint;
    private int progressBgColor;
    private int progressColor;
    private String titleText;

    public static final String defaultTitle = "[室内二氧化碳]";
    public static final String defaultStrLeft = "[";
    public static final String defaultStrRight = "]";
    public static final String defaultStrLevel = "9.99";
    public static final String defaultStrProgress = "优";
    private int progressWidth;
    private int progressHeight;
    private int defaultTitleWidth;
    private int defaultTitleHeight;
    private int defaultProgressHeight;
    private int progressTextSize;
    private ValueAnimator valueAnimator;

    public AirProgressView(Context context) {
        this(context, null);
    }

    public AirProgressView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        Typeface pixFontLite = Typeface.createFromAsset(getContext().getAssets(), "fonts/pixFontLite.ttf");
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.AirProgressView);
        progressColor = typedArray.getColor(R.styleable.AirProgressView_progressColor, 0xFFE4CC54);
        progressBgColor = typedArray.getColor(R.styleable.AirProgressView_progressBgColor, 0xFF47AADF);
        int progressTextColor = typedArray.getColor(R.styleable.AirProgressView_progressTextColor, Color.WHITE);
        int titleTextColor = typedArray.getColor(R.styleable.AirProgressView_titleTextColor, Color.LTGRAY);
        progressTextSize = typedArray.getDimensionPixelOffset(R.styleable.AirProgressView_progressTextSize, 24);
        int titleTextSize = typedArray.getDimensionPixelOffset(R.styleable.AirProgressView_titleTextSize, 20);
        progressWidth = typedArray.getDimensionPixelOffset(R.styleable.AirProgressView_progressWidth, 100);
        progressHeight = typedArray.getDimensionPixelOffset(R.styleable.AirProgressView_progressHeight, 5);
        titleText = typedArray.getString(R.styleable.AirProgressView_titleText);
        if (TextUtils.isEmpty(titleText)) {
            titleText = defaultTitle;
        }

        progressTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        progressTextPaint.setTextSize(progressTextSize);
        progressTextPaint.setColor(progressTextColor);
        progressTextPaint.setTypeface(pixFontLite);

        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setTextSize(titleTextSize);
        textPaint.setColor(titleTextColor);

        progressPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        progressPaint.setStrokeWidth(progressHeight);

        //标题文字的边界矩形
        Rect textBoundRect = new Rect();
        textPaint.getTextBounds(defaultTitle, 0, defaultTitle.length(), textBoundRect);
        defaultTitleWidth = textBoundRect.width();
        defaultTitleHeight = textBoundRect.height();
        //当前数值
        Rect textValueRect = new Rect();
        progressTextPaint.getTextBounds(defaultStrProgress, 0, defaultStrProgress.length(), textValueRect);
        defaultProgressHeight = textValueRect.height();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int width, height;
        if (widthMode == MeasureSpec.EXACTLY) {
            width = widthSize;
            progressWidth = width - defaultTitleWidth - progressHeight * 2;
        } else {
            width = defaultTitleWidth + progressHeight * 2 + progressWidth;
        }
        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        } else {
            height = defaultTitleHeight + defaultProgressHeight / 2 + progressHeight;
        }
        setMeasuredDimension(width, height);
    }

    private String dataUnit = "";
    private boolean isFloat = false;
    private float currentValue;
    private int max = 100;

    public void setProgress(final float value, String dataUnit, int max, boolean isFloat) {
        this.dataUnit = dataUnit;
        this.isFloat = isFloat;
        this.max = max;
        valueAnimator = ValueAnimator.ofFloat(0, value);
        valueAnimator.setInterpolator(new OvershootInterpolator());
        valueAnimator.setDuration(3000);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                currentValue = (float) Math.round(value * 100) / 100;
                Log.d("AirProgressView", "value:" + value);
                invalidate();
            }
        });
        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationCancel(Animator animation) {
                super.onAnimationCancel(animation);
                Log.d("AirProgressView", "onAnimationCancel");
                currentValue = value;
                invalidate();
            }
        });
        valueAnimator.start();
    }

    public void onStop() {
        if (valueAnimator != null)
            valueAnimator.cancel();
    }

    private String level;

    public void setProgressAndLevel(float value, String dataUnit, int max, boolean isFloat, String level) {
        this.level = level;
        setProgress(value, dataUnit, max, isFloat);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //绘制标题
        float widthLeft = getStringWidth(defaultStrLeft, textPaint);
        float titleWidth = getStringWidth(titleText, textPaint);
        canvas.drawText(defaultStrLeft, 0, defaultProgressHeight / 2 + defaultTitleHeight, textPaint);
        canvas.drawText(titleText, (defaultTitleWidth - titleWidth) / 2, defaultProgressHeight / 2 + defaultTitleHeight, textPaint);
        canvas.drawText(defaultStrRight, defaultTitleWidth - widthLeft, defaultProgressHeight / 2 + defaultTitleHeight, textPaint);

        //绘制进度
        progressPaint.setColor(progressColor);
        float progress = currentValue * progressWidth / max;
        canvas.drawLine(defaultTitleWidth + progressHeight * 2, getHeight() - progressHeight * 3, defaultTitleWidth + progressHeight * 2 + progress, getHeight() - progressHeight * 3, progressPaint);
        progressPaint.setColor(progressBgColor);
        canvas.drawLine(defaultTitleWidth + progressHeight * 2, getHeight() - progressHeight, defaultTitleWidth + progressHeight * 2 + progressWidth, getHeight() - progressHeight, progressPaint);

        //绘制当前值
        int value = (int) currentValue;
        String data = isFloat ? String.valueOf(currentValue) : String.valueOf(value);
        progressTextPaint.setTextSize(progressTextSize / 2);
        float dataUnitWidth = getStringWidth(dataUnit, progressTextPaint);
        canvas.drawText(dataUnit, getWidth() - dataUnitWidth, defaultProgressHeight, progressTextPaint);
        progressTextPaint.setTextSize(progressTextSize);
        canvas.drawText(data, getWidth() - getStringWidth(data, progressTextPaint) - dataUnitWidth, defaultProgressHeight, progressTextPaint);
        //绘制优良中等级文字
        if (!TextUtils.isEmpty(level)) {
            float dataWidth = getStringWidth(defaultStrLevel, progressTextPaint);
            canvas.drawText(level, getWidth() - getStringWidth(level, progressTextPaint) - dataUnitWidth - dataWidth * 3 / 2, defaultProgressHeight, progressTextPaint);
        }
    }

    //获取文字宽度
    private float getStringWidth(String str, Paint paint) {
        return paint.measureText(str);
    }
}
