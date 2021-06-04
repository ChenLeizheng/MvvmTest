package com.landleaf.everyday.widget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RadialGradient;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;

import androidx.annotation.Nullable;

import com.landleaf.everyday.R;

import java.text.DecimalFormat;

/**
 * 温湿度圆盘自定义控件
 */
public class HumitureCircleView extends View  {

    private int hvCircleRadius;
    private Paint excirclePaint;
    private Paint incirclePaint;
    private int hvCircleMargin;
    private RectF progressRectF;
    private Paint progressPaint;
    private int hvProgressWidth;
    private Paint ripplePaint;
    private int mWaterTop = -20;
    private Path wavePath1;
    private Path wavePath2;
    private RadialGradient radialGradient;
    private Paint paintText;
    private Path clipPath;
    private int hvTempTextSize;
    private int hvHumidityTextSize;
    private Rect textBoundRect;
    private String tempUnit = "℃";
    private String humidityUnit = "%";
    private int MAX_TEMP = 50;
    private int MIN_TEMP = 0;
    private int MIN_HUMIDITY = 0;
    private int MAX_HUMIDITY = 100;

    /**
     * 在Java代码中直接new一个CustomView实例的时候，会调用该构造函数
     */
    public HumitureCircleView(Context context) {
        this(context,null);
    }

    /**
     * 在xml中引用CustomView标签的时候，会调用2参数的构造函数。
     * 这种方式通常是我们需要自定义View的属性的时候，使用2参数的构造函数。
     */
    public HumitureCircleView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public HumitureCircleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.HumitureCircleView);
        Typeface pixFontLite = Typeface.createFromAsset(getContext().getAssets(), "fonts/pixFontLite.ttf");
        hvCircleRadius = typedArray.getDimensionPixelOffset(R.styleable.HumitureCircleView_hvCircleRadius, 182);
        hvCircleMargin = typedArray.getDimensionPixelOffset(R.styleable.HumitureCircleView_hvCircleMargin, 10);
        hvProgressWidth = typedArray.getDimensionPixelOffset(R.styleable.HumitureCircleView_hvProgressWidth, 4);
        hvTempTextSize = typedArray.getDimensionPixelOffset(R.styleable.HumitureCircleView_hvTempTextSize, 90);
        hvHumidityTextSize = typedArray.getDimensionPixelOffset(R.styleable.HumitureCircleView_hvHumidityTextSize, 30);


        typedArray.recycle();
        excirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        excirclePaint.setStyle(Paint.Style.FILL);
        BlurMaskFilter maskfilterOuter = new BlurMaskFilter(hvCircleMargin, BlurMaskFilter.Blur.OUTER);
        excirclePaint.setColor(0xFFFDFDFD);
        excirclePaint.setMaskFilter(maskfilterOuter);
        setLayerType(LAYER_TYPE_SOFTWARE, excirclePaint);

        incirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        incirclePaint.setStyle(Paint.Style.FILL);

        progressPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        progressPaint.setStyle(Paint.Style.STROKE);
        progressPaint.setStrokeWidth(hvProgressWidth);
        progressRectF = new RectF(hvCircleMargin * 3.5f + hvProgressWidth, hvCircleMargin * 3.5f + hvProgressWidth, hvCircleRadius * 2 - hvCircleMargin * 3.5f - hvProgressWidth, hvCircleRadius * 2 - hvCircleMargin * 3.5f - hvProgressWidth);


        ripplePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        ripplePaint.setStyle(Paint.Style.FILL);
        ripplePaint.setColor(0x33FFFFFF);

        paintText = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintText.setStyle(Paint.Style.FILL);
        paintText.setTypeface(pixFontLite);
        paintText.setColor(Color.WHITE);

        wavePath1 = new Path();
        wavePath2 = new Path();
        clipPath = new Path();

        textBoundRect = new Rect();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(measureDimension(widthMeasureSpec),measureDimension(heightMeasureSpec));
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        int[] colors = {0x22FFFFFF,0x22FFFFFF,0x88FFFFFF};
        float[] stops = {0,9/10f,1};
        radialGradient = new RadialGradient(w/2, h/2, hvCircleRadius-hvCircleMargin*2, colors, stops, Shader.TileMode.CLAMP);
    }

    private int measureDimension(int measureSpec) {
        int result;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
            hvCircleRadius = result/2;
        } else {
            result = hvCircleRadius * 2;
            if (specMode == MeasureSpec.AT_MOST) {
                result = Math.min(result, specSize);
            }
        }
        return result;
    }

    //每节波浪的宽度
    private int mItemWidth = 150;
    int dy1 = 20;
    int dy2 = 30;
    int mOffsetX1 = 0;

    public void setProgress(float temp,float humidity){
        ValueAnimator valueAnimator = ValueAnimator.ofInt(0, mItemWidth);
        valueAnimator.setDuration(2000);
        valueAnimator.setRepeatCount(2);
        valueAnimator.setInterpolator(new LinearInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                mOffsetX1 = (int) valueAnimator.getAnimatedValue();
                postInvalidate();
            }
        });
        ValueAnimator humidityAnim = ValueAnimator.ofFloat(0,humidity);
        humidityAnim.setDuration(3000);
        humidityAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                currentHumidity = (float) animation.getAnimatedValue();
                if (currentHumidity > MAX_HUMIDITY){
                    currentHumidity = MAX_HUMIDITY;
                }
                if (currentHumidity < MIN_HUMIDITY){
                    currentHumidity = MIN_HUMIDITY;
                }
                mWaterTop = (int) (getWidth()* (1- currentHumidity/100f));
                Log.d("HumitureCircleView", "currentHumidity:" + currentHumidity+"+++++++++"+mWaterTop);
            }
        });
        ValueAnimator tempAnim = ValueAnimator.ofFloat(0, temp);
        tempAnim.setDuration(3000);
        tempAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                currentTemp = (float) animation.getAnimatedValue();
                if (currentTemp > MAX_TEMP){
                    currentTemp = MAX_TEMP;
                }
                if (currentTemp < MIN_TEMP){
                    currentTemp = MIN_TEMP;
                }
                Log.d("HumitureCircleView", "currentTemp:++++++++++" + currentTemp);
            }
        });

        valueAnimator.start();
        humidityAnim.start();
        tempAnim.start();
        tempAnim.start();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (changed){
            clipPath.addCircle(getWidth()/2,getWidth()/2,hvCircleRadius-hvCircleMargin + 1,Path.Direction.CW);
        }
    }

    float currentTemp = 26.2f;
    float currentHumidity = 39.5f;


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //添加抗锯齿
        canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));

        //绘制内部边缘发光圆
        incirclePaint.setShader(radialGradient);
        canvas.drawCircle(getWidth()/2,getWidth()/2,hvCircleRadius-hvCircleMargin*2,incirclePaint);

        //裁剪画布，处理水波圆外部分
        canvas.save();
        canvas.clipPath(clipPath);

        //绘制波纹,处理初始水波遮挡
        if (mWaterTop != -20){
            createWavePath(wavePath1,mItemWidth,dy1,mOffsetX1);
            createWavePath(wavePath2,mItemWidth*5/4,dy2,mOffsetX1*5/4);
            canvas.drawPath(wavePath1,ripplePaint);
            canvas.drawPath(wavePath2,ripplePaint);
        }
        canvas.restore();

        //绘制最外层外发光圆
        canvas.drawCircle(getWidth()/2,getWidth()/2,hvCircleRadius-hvCircleMargin,excirclePaint);

        //绘制进度
        progressPaint.setColor(0xFF14171A);
        canvas.drawArc(progressRectF,150,240,false,progressPaint);
        progressPaint.setColor(0xFF00ADA2);
        canvas.drawArc(progressRectF,150,currentTemp/(MAX_TEMP - MIN_TEMP) * 240,false,progressPaint);

        //绘制文字
        String strTemp = roundOneDecimal(currentTemp);
        int[] tempSize = getValueAndUnitSize(strTemp,tempUnit,hvTempTextSize);
        canvas.drawText(strTemp,(getWidth() - tempSize[VALUE_WIDTH_INDEX] - tempSize[UNIT_WIDTH_INDEX])/2f,(getHeight()+tempSize[VALUE_HEIGHT_INDEX])/2f,paintText);
        paintText.setTextSize(hvTempTextSize/2);
        canvas.drawText(tempUnit,(getWidth() + tempSize[VALUE_WIDTH_INDEX] - tempSize[UNIT_WIDTH_INDEX])/2f,(getHeight()+tempSize[VALUE_HEIGHT_INDEX])/2f,paintText);

        String strHumidity = roundOneDecimal(currentHumidity);
        int[] humiditySize = getValueAndUnitSize(strHumidity, humidityUnit, hvHumidityTextSize);
        canvas.drawText(strHumidity,(getWidth() - humiditySize[VALUE_WIDTH_INDEX] - humiditySize[UNIT_WIDTH_INDEX])/2f,getHeight()- humiditySize[VALUE_HEIGHT_INDEX] - hvCircleMargin*3,paintText);
        paintText.setTextSize(hvHumidityTextSize/2);
        canvas.drawText(humidityUnit,(getWidth() + humiditySize[VALUE_WIDTH_INDEX] - humiditySize[UNIT_WIDTH_INDEX])/2f,getHeight()- humiditySize[VALUE_HEIGHT_INDEX] - hvCircleMargin*3,paintText);

        canvas.drawLine(getWidth()/2,0,getWidth()/2,getHeight(),paintText);
        canvas.drawLine(0,getHeight()/2,getWidth(),getHeight()/2,paintText);
    }

    private int UNIT_HEIGHT_INDEX = 0;
    private int UNIT_WIDTH_INDEX = 1;
    private int VALUE_HEIGHT_INDEX = 2;
    private int VALUE_WIDTH_INDEX = 3;
    private int[] getValueAndUnitSize (String strValue,String unit,int textSize){
        int[] size = new int[4];
        paintText.setTextSize(textSize/2);
        paintText.getTextBounds(unit,0,unit.length(),textBoundRect);
        size[UNIT_HEIGHT_INDEX] = textBoundRect.height();
        size[UNIT_WIDTH_INDEX] = textBoundRect.width();
        paintText.setTextSize(textSize);
        paintText.getTextBounds(strValue,0,strValue.length(),textBoundRect);
        size[VALUE_HEIGHT_INDEX] = textBoundRect.height();
        size[VALUE_WIDTH_INDEX] = textBoundRect.width();
        return size;
    }

    private void createWavePath(Path path,int dx,int dy,int mOffsetX){
        path.reset();
        path.moveTo(-dx+mOffsetX,mWaterTop);
        for (int i = -dx; i < dx + getWidth(); i += dx) {
            path.rQuadTo(dx / 4, -dy, dx / 2, 0);
            path.rQuadTo(dx / 4, dy, dx / 2, 0);
        }
        path.lineTo(getWidth(),getWidth());
        path.lineTo(0,getWidth());
        path.close();
    }

    DecimalFormat decimalFormat = new DecimalFormat("0.0");
    public String roundOneDecimal(float value){
        String data = decimalFormat.format(value);
        if ("0.0".equals(data) || "-0.0".equals(data)){
            return "0";
        }
        return data;
    }
}
