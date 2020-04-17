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
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.OvershootInterpolator;


import androidx.annotation.Nullable;

import com.landleaf.everyday.R;

import org.w3c.dom.Text;

/**
 * Created by Lei on 2018/1/17.
 */

public class RulerView extends View {

    private float oneGrid;
    private int lineHeight;
    private Paint paint;
    private float currentValue;
    private Paint paintProgress;
    private final int marginStart;
    private final int paintStrokeWidth;
    private final int paintTextSize;
    private final int colorPaint;
    private final int colorPaintProgress;
    private int textHeight;
    private float stringMaxWidth;
    private float stringMinWidth;
    private int currentValueTextSize;
    private int marginValue;
    private float currentValueWidth;
    private ValueAnimator progressAnimator;


    public RulerView(Context context) {
        this(context, null);
    }

    public RulerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.RulerView);
        //绘制控件的起点
        marginStart = typedArray.getDimensionPixelOffset(R.styleable.RulerView_rvMarginStart, 20);
        //画笔的宽度
        paintStrokeWidth = typedArray.getInteger(R.styleable.RulerView_rvPaintStrokeWidth, 5);
        //xml设置最大最小值   int
        max = typedArray.getInteger(R.styleable.RulerView_rvMax, 50);
        min = typedArray.getInteger(R.styleable.RulerView_rvMin, 0);
        //一共绘制多少格刻度
        totalGrids = typedArray.getInteger(R.styleable.RulerView_rvTotalGrids, 50);
        //刻度下方文字的大小
        paintTextSize = typedArray.getDimensionPixelOffset(R.styleable.RulerView_rvPaintTextSize, totalGrids);
        //基础线高   节点处线高为2*lineHeight
        lineHeight = typedArray.getDimensionPixelOffset(R.styleable.RulerView_rvLineHeight, 5);
        //每一格刻度的宽度
        oneGrid = typedArray.getDimensionPixelOffset(R.styleable.RulerView_rvOneGrideWidth, 5);
        //当前刻度值
        currentValue = typedArray.getInteger(R.styleable.RulerView_rvCurrentValue, 22);
        currentValueTextSize = typedArray.getDimensionPixelOffset(R.styleable.RulerView_rvCurrentValueTextSize, 32);
        marginValue = typedArray.getDimensionPixelOffset(R.styleable.RulerView_rvCurrentValueMargin, 30);
        //画笔颜色  Color.parseColor("#72718e")
        colorPaint = typedArray.getColor(R.styleable.RulerView_rvPaintColor, 0xFF72718e);
        //进度画笔颜色
        colorPaintProgress = typedArray.getColor(R.styleable.RulerView_rvPaintProgressColor, 0xFF08ca08);
        initView(context);
    }

    private void initView(Context context) {
        paint = new Paint();
        paint.setStrokeWidth(paintStrokeWidth);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(colorPaint);
        //设置抗锯齿，如果不设置，加载位图的时候可能会出现锯齿状的边界，如果设置，边界就会变的稍微有点模糊，锯齿就看不到了
        paint.setAntiAlias(true);
        //设置是否抖动，如果不设置感觉就会有一些僵硬的线条，如果设置图像就会看的更柔和一些
        paint.setDither(true);
        paint.setTextSize(paintTextSize);
        paintProgress = new Paint();
        paintProgress.setStrokeWidth(paintStrokeWidth);
        paintProgress.setTextSize(currentValueTextSize);
        paintProgress.setAntiAlias(true);
        paintProgress.setDither(true);
        paintProgress.setStyle(Paint.Style.FILL);
        paintProgress.setColor(colorPaintProgress);

        strMax = max + "";
        strMin = min + "";
        Rect rect = new Rect();
        paint.getTextBounds(strMax, 0, strMax.length(), rect);
        textHeight = rect.height();
        stringMaxWidth = getStringWidth(strMax, paint);
        stringMinWidth = getStringWidth(strMin, paint);
        currentValueWidth = getStringWidth(currentValue + "", paintProgress);
    }


    float endValue;
    int max;
    int min;
    int totalGrids;
    String strMin;
    String strMax;

    public void setProgress(float progress) {
        currentValue =  progress;
        float value = progress * totalGrids/(max - min);
        progressAnimator = ValueAnimator.ofFloat(0, value);
        progressAnimator.setDuration(3000);
        progressAnimator.setInterpolator(new OvershootInterpolator());
        progressAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                endValue = (float) animation.getAnimatedValue();
//                Log.d("RulerView", "endValue:" + animation.getAnimatedValue());
                invalidate();
            }
        });
        progressAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                //处理越界
                if (endValue > totalGrids) {
                    endValue = totalGrids;
                    invalidate();
                }
            }
        });
        progressAnimator.start();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int width = (int) (marginStart + oneGrid * totalGrids + paint.getStrokeWidth() + stringMaxWidth / 2);
        int height = (int) (lineHeight * 3 + textHeight +oneGrid*2+ marginValue + currentValueWidth);
        if (widthMode == MeasureSpec.EXACTLY) {
            width = widthSize;
            oneGrid = (width-marginStart-paint.getStrokeWidth()-stringMaxWidth/2)/totalGrids;
        } else {
            width = (int) (marginStart + oneGrid * totalGrids + paint.getStrokeWidth() + stringMaxWidth / 2);
        }
        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        } else {
            height = (int) (lineHeight * 3 + textHeight + oneGrid * 2 + marginValue + currentValueWidth);
        }
        setMeasuredDimension(width, height);
    }

    private int getMeasuredHeight(int widthMeasureSpec, int heightMeasureSpec, int width, int height) {
        //AT_MOST->Math.min(width,MeasureSpec.getSize(widthMeasureSpec));EXACTLY->MeasureSpec.getSize(widthMeasureSpec);UNSPECIFIED->width
        int measuredWidth = resolveSizeAndState(width, widthMeasureSpec, 0);
        if (measuredWidth!=width){
            oneGrid = (int) ((measuredWidth-marginStart-paint.getStrokeWidth()-stringMaxWidth/2)/totalGrids);
        }

        return resolveSizeAndState(height, heightMeasureSpec, 0);
    }

    private int measureWidth(int widthMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);

        int width;
        int defaultWidth = (int) (marginStart + oneGrid * totalGrids + paint.getStrokeWidth() + stringMaxWidth / 2);
        if (widthMode == MeasureSpec.EXACTLY) {
            width = widthSize;
            Log.d("RulerView", "EXACTLY,width:" + width + ",default:" + defaultWidth);
        } else if (widthMode == MeasureSpec.AT_MOST) {
            width = Math.min(defaultWidth, widthSize);//防止控件超出父类范围
            Log.d("RulerView", "AT_MOST,width:" + width + ",default:" + defaultWidth);
        } else {
            width = defaultWidth;
            Log.d("RulerView", "UNSPECIFIED,width:" + width + ",default:" + defaultWidth);
        }
        if (defaultWidth != width) {
            oneGrid = (width - marginStart - paint.getStrokeWidth() - stringMaxWidth / 2) / totalGrids;
        }
        return width;
    }


    private int measureHeight(int heightMeasureSpec) {
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int height;
        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        } else {
            height = (int) (lineHeight * 3 + textHeight);
        }
        return height;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawText(currentValue+"",marginStart + oneGrid*totalGrids/2 - currentValueWidth/2,lineHeight * 3 + textHeight,paintProgress);
        paint.setColor(colorPaint);
        //绘制基本刻度
        drawProgressLine(canvas,totalGrids,paint);
        //绘制刻度值
        canvas.drawText(strMin, marginStart - paint.getStrokeWidth() / 2 - stringMinWidth / 2, lineHeight * 3 + textHeight + oneGrid+marginValue + currentValueWidth, paint);
        canvas.drawText(strMax, marginStart + oneGrid * totalGrids + paint.getStrokeWidth() / 2 - stringMaxWidth / 2, lineHeight * 3 + textHeight + oneGrid+marginValue + currentValueWidth, paint);

        if (currentValue != 0) {
            paint.setColor(colorPaintProgress);
            //绘制进度
            drawProgressLine(canvas,endValue,paint);
        }
    }

    private void drawProgressLine(Canvas canvas,float maxValue,Paint paint) {
        for (int i = 0; i <= maxValue; i++) {
            if (i % 5 == 0) {
                canvas.drawLine(marginStart + i * oneGrid, lineHeight * 3  + marginValue + currentValueWidth, marginStart + i * oneGrid, lineHeight * 1 + marginValue + currentValueWidth, paint);
            } else {
                canvas.drawLine(marginStart + i * oneGrid, lineHeight * 3 + marginValue + currentValueWidth, marginStart + i * oneGrid, lineHeight * 2 + marginValue + currentValueWidth, paint);
            }
        }
        canvas.drawLine(marginStart - paint.getStrokeWidth() / 2, lineHeight * 3 + marginValue + currentValueWidth, marginStart + maxValue * oneGrid + paint.getStrokeWidth() / 2, lineHeight * 3 + marginValue + currentValueWidth, paint);
    }

    //获取文字宽度
    private float getStringWidth(String str, Paint paint) {
        return paint.measureText(str);
    }
}
