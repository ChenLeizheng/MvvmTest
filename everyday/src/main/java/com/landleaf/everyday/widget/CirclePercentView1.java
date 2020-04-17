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
import android.view.View;
import android.view.animation.OvershootInterpolator;

import androidx.annotation.Nullable;

import com.landleaf.everyday.R;

public class CirclePercentView1 extends View {

    private Paint arcCirclePaint;
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
    private boolean layoutLeft;
    private RectF circleRectF;
    private BlurMaskFilter maskfilterOuter;
    private BlurMaskFilter maskfilterNormal;
    private Paint circlePaint;

    public CirclePercentView1(Context context) {
        this(context,null);
    }

    public CirclePercentView1(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CirclePercentView);
        initView(typedArray);
    }

    private void initView(TypedArray typedArray) {
        cvCircleRadius = typedArray.getDimensionPixelOffset(R.styleable.CirclePercentView_cvCircleRadius, 100);
        cvPaintWidth = typedArray.getDimensionPixelOffset(R.styleable.CirclePercentView_cvPaintWidth, 10);
        currentValueTextSize = typedArray.getDimensionPixelOffset(R.styleable.CirclePercentView_cvCurrentValueTextSize, 28);
        centerTextSize = typedArray.getDimensionPixelOffset(R.styleable.CirclePercentView_cvCenterTextSize, 12);
        startColor = typedArray.getColor(R.styleable.CirclePercentView_cvStartColor, 0xFF45AAF5);
        endColor = typedArray.getColor(R.styleable.CirclePercentView_cvEndColor, 0x990000FF);
        centerTextColor = typedArray.getColor(R.styleable.CirclePercentView_cvCenterTextColor, Color.LTGRAY);
        currentValeColor = typedArray.getColor(R.styleable.CirclePercentView_cvCurrentValueTextColor, Color.WHITE);
        int bgCircleColor = typedArray.getColor(R.styleable.CirclePercentView_cvBackgroundColor, Color.LTGRAY);
        currentValue = typedArray.getFloat(R.styleable.CirclePercentView_cvCurrentValue,22f);
        cvMax = typedArray.getInteger(R.styleable.CirclePercentView_cvMax,50);
        cvMin = typedArray.getInteger(R.styleable.CirclePercentView_cvMin,0);
        centerText = typedArray.getString(R.styleable.CirclePercentView_cvCenterText);
        if (TextUtils.isEmpty(centerText)){
            centerText = "室内温度";
        }

        arcCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        arcCirclePaint.setStyle(Paint.Style.STROKE);
        arcCirclePaint.setStrokeWidth(cvPaintWidth);
        arcCirclePaint.setColor(bgCircleColor);

        arcProgressPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        arcProgressPaint.setStyle(Paint.Style.STROKE);
        arcProgressPaint.setStrokeWidth(cvPaintWidth);

        mCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mCirclePaint.setStyle(Paint.Style.FILL);
        mCirclePaint.setColor(startColor);
        maskfilterOuter = new BlurMaskFilter(cvPaintWidth, BlurMaskFilter.Blur.OUTER);
        maskfilterNormal = new BlurMaskFilter(cvPaintWidth, BlurMaskFilter.Blur.NORMAL);
        setLayerType(LAYER_TYPE_SOFTWARE, mCirclePaint);

        circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        circlePaint.setStyle(Paint.Style.STROKE);
        circlePaint.setStrokeWidth(1);
        circlePaint.setColor(startColor);

        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setStyle(Paint.Style.FILL);

        //圓弧的外接矩形
        arcRectF = new RectF();
        //文字的边界矩形
        textBoundRect = new Rect();
        //最外层的圆形气泡
        circleRectF = new RectF();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(measureDimension(widthMeasureSpec),measureDimension(heightMeasureSpec));
    }

    private int measureDimension(int measureSpec) {
        int result;
        int specMode=MeasureSpec.getMode(measureSpec);
        int specSize=MeasureSpec.getSize(measureSpec);
        if(specMode==MeasureSpec.EXACTLY){
            result=specSize;
        }else{
            result=cvCircleRadius*3;
            if(specMode==MeasureSpec.AT_MOST){
                result=Math.min(result,specSize);
            }
        }
        return result;
    }

    public void setProgress(float progress,String dataUnit){
        this.dataUnit = dataUnit;
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(cvMin, progress);
        valueAnimator.setInterpolator(new OvershootInterpolator());
        valueAnimator.setDuration(3000);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                currentValue = (float) Math.round(value*10)/10;
                currentDegrees = currentValue * 180 /(cvMax - cvMin);
                invalidate();
            }
        });
        valueAnimator.start();
    }

    boolean first = false;

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.save();
//        circleRectF.set(getWidth()/2 - cvCircleRadius*3/2 + cvPaintWidth/2,getHeight()/2-cvCircleRadius*3/2+cvPaintWidth/2
//                ,getWidth()/2+cvCircleRadius*3/2-cvPaintWidth/2,getHeight()/2+cvCircleRadius*3/2-cvPaintWidth/2);
        drawBgCircle(canvas);
        canvas.restore();
        arcRectF.set(getWidth()/2-cvCircleRadius+cvPaintWidth/2,getHeight()/2-cvCircleRadius+cvPaintWidth/2
                ,getWidth()/2+cvCircleRadius-cvPaintWidth/2,getHeight()/2+cvCircleRadius-cvPaintWidth/2);
        //绘制背景圆环
        canvas.drawArc(arcRectF,90,layoutLeft? 180 : -180,false,arcCirclePaint);
        //绘制进度圆环
        arcProgressPaint.setShader(new LinearGradient(getWidth()/2,getHeight()-cvCircleRadius/2,getWidth()/2,cvCircleRadius/2,startColor, endColor, Shader.TileMode.CLAMP));
        canvas.drawArc(arcRectF,90,layoutLeft? currentDegrees : -currentDegrees,false,arcProgressPaint);
        //TODO  test delete 渐变色显示
        canvas.drawLine(getWidth()/2+10,0,getWidth()/2+10,getHeight(),arcProgressPaint);

        //绘制文字
        textPaint.setTextSize(currentValueTextSize);
        textPaint.setColor(currentValeColor);
        String data = String.valueOf(currentValue) + dataUnit;
        textPaint.getTextBounds(data,0,data.length(),textBoundRect);
        canvas.drawText(data,layoutLeft ? getWidth()/2 - textBoundRect.width():getWidth()/2,getHeight()/2,textPaint);
        textPaint.setColor(centerTextColor);
        textPaint.setTextSize(centerTextSize);
        canvas.drawText(centerText,layoutLeft? getWidth()/2 - textBoundRect.width():getWidth()/2,getHeight()/2 + textBoundRect.height(),textPaint);

        //绘制最小值
        canvas.drawLine(getWidth()/2,getHeight() - cvCircleRadius/2 - cvPaintWidth*3/2,getWidth()/2,getHeight() - cvCircleRadius/2- cvPaintWidth*2,textPaint);
        String minStr = String.valueOf(cvMin);
        textPaint.getTextBounds(minStr,0,minStr.length(),textBoundRect);
        canvas.drawText(minStr,getWidth()/2 - textBoundRect.width()/2,getHeight() - cvCircleRadius/2- cvPaintWidth*2 - textBoundRect.height()/2,textPaint);
        //绘制最大值
        canvas.drawLine(getWidth()/2,cvPaintWidth*3/2 + cvCircleRadius/2,getWidth()/2,cvPaintWidth*2+ cvCircleRadius/2,textPaint);
        String maxStr = String.valueOf(cvMax);
        textPaint.getTextBounds(maxStr,0,maxStr.length(),textBoundRect);
        canvas.drawText(maxStr,getWidth()/2 - textBoundRect.width()/2,cvPaintWidth*2 + cvCircleRadius/2+ textBoundRect.height()*3/2,textPaint);
    }

    private void drawBgCircle(Canvas canvas){
        //绘制气泡
        mCirclePaint.setMaskFilter(maskfilterOuter);
        canvas.drawCircle(getWidth()/2,getHeight()/2,cvCircleRadius*3/2,circlePaint);
        canvas.drawCircle(getWidth()/2,getHeight()/2,cvCircleRadius*3/2 - cvPaintWidth,mCirclePaint);
//        canvas.drawCircle(getWidth()/2 - cvCircleRadius*4/5,getWidth()/2 - cvCircleRadius*4/5,cvCircleRadius/5,circlePaint);
//        canvas.drawCircle(getWidth()/2 - cvCircleRadius,getWidth()/2 - cvCircleRadius,cvCircleRadius/5,circlePaint);
//        canvas.drawCircle(getWidth()/2,getWidth()/2,cvCircleRadius*5/4,circlePaint);
//        canvas.drawCircle(getWidth()/2 + cvCircleRadius/4,getWidth()/2 +cvCircleRadius/4,cvCircleRadius*3/4,circlePaint);

        //绘制亮点
        Path path = new Path();
        path.addCircle(getWidth()/2 - cvCircleRadius*4/5,getWidth()/2 - cvCircleRadius*4/5,cvCircleRadius/5,Path.Direction.CW);
        Path path1 = new Path();
        path1.addCircle(getWidth()/2 - cvCircleRadius,getWidth()/2 - cvCircleRadius,cvCircleRadius/5,Path.Direction.CW);
        path1.op(path, Path.Op.INTERSECT);
        mCirclePaint.setMaskFilter(maskfilterNormal);
        canvas.drawPath(path1,mCirclePaint);
        path1.addCircle(getWidth()/2 + cvCircleRadius/2,getWidth()/2 + cvCircleRadius/2,cvCircleRadius/2,Path.Direction.CW);
        path.addCircle(getWidth()/2 + cvCircleRadius/4,getWidth()/2 + cvCircleRadius/4,cvCircleRadius*3/4,Path.Direction.CW);
        path1.op(path, Path.Op.DIFFERENCE);
        canvas.drawPath(path1,mCirclePaint);
    }
}
