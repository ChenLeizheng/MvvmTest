package com.landleaf.everyday.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import com.landleaf.everyday.R;

/**
 * 户式化项目 风量调节 梯形每个挡位中间隔开
 */
public class WindSpeedView extends View {

    private int windViewHeight;
    private int windViewWidth;
    private int windViewSpace;
    private Paint mPaint;
    private int initLevelNum = 4;
    private Rect clipRect;
    private Path path;

    public WindSpeedView(Context context) {
        this(context,null);
    }

    public WindSpeedView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.WindSpeedView);
        windViewHeight = typedArray.getDimensionPixelOffset(R.styleable.WindSpeedView_windViewHeight, 40);
        windViewWidth = typedArray.getDimensionPixelOffset(R.styleable.WindSpeedView_windViewWidth, 312);
        windViewSpace = typedArray.getDimensionPixelOffset(R.styleable.WindSpeedView_windSpeedSpace, 8);

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        setClickable(true);
        clipRect = new Rect();
        path = new Path();
        typedArray.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        windViewWidth = measureDimension(widthMeasureSpec, true);
        windViewHeight = measureDimension(heightMeasureSpec, false);
        setMeasuredDimension(windViewWidth,windViewHeight);
    }

    private int measureDimension(int measureSpec,boolean width){
        int result = 0;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        if (specMode == MeasureSpec.EXACTLY){
            result = specSize;
        }else {
            result = width ? windViewWidth : windViewHeight;
            if (specMode == MeasureSpec.AT_MOST) {
                result = Math.min(result, specSize);
            }
        }
        return result;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        if (event.getAction() == MotionEvent.ACTION_UP){
            for (int i = 0; i < initLevelNum; i++) {
                if (x <= (i+1)*getWidth()/initLevelNum){
                    currentWind = i;
                    postInvalidate();
                    if (changeListener!=null){
                        changeListener.windChange(currentWind);
                    }
                    return true;
                }
            }
        }
        return super.onTouchEvent(event);
    }

    private OnWindChangeListener changeListener;
    public interface OnWindChangeListener{
        void windChange(int currentWind);
    }

    public void setOnWindChangeListener(OnWindChangeListener changeListener){
        this.changeListener = changeListener;
        Log.d("WindSpeedView", "this.changeListener:" + this.changeListener);
    }

    int currentWind = -1;
    public void setProgress(int windSpeed){
        currentWind = windSpeed;
        invalidate();
    }

    public void setWindLevel(int levelNum){
        initLevelNum = levelNum;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //绘制三角形
        int oneGridWidth = getWidth()/initLevelNum;
        path.moveTo(-oneGridWidth/2f,windViewHeight);
        path.lineTo(windViewWidth,windViewHeight);
        path.lineTo(windViewWidth,0);
        path.close();

        //裁剪画布得到每一格的梯形 currentWind = -1默认值  0/1/2相应等级风量
        for (int i = 0; i < initLevelNum; i++) {
            mPaint.setColor((currentWind >= i)? 0x8800ADA2 : 0x20FFFFFF);
            canvas.save();
            clipRect.set(i*oneGridWidth+windViewSpace,0,(i+1)*oneGridWidth,getHeight());
            canvas.clipRect(clipRect);
            canvas.drawPath(path,mPaint);
            canvas.restore();
        }
    }
}
