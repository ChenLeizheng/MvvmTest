package com.landleaf.everyday.widget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

import androidx.annotation.Nullable;

import com.landleaf.everyday.R;

/**
 * 温湿度圆盘自定义控件
 */
public class HumitureCircleView2 extends View  {

    private int hvCircleRadius;
    private Paint excirclePaint;
    private Paint incirclePaint;
    private int hvCircleMargin;
    private RectF progressRectF;
    private Paint progressPaint;
    private int hvProgressWidth;
    private Paint ripplePaint;
    private Canvas bitmapCanvas;
    private Bitmap mBallBitmap;
    private BlurMaskFilter maskfilterOuter;
    private int mWaterTop;
    private Path wavePath1;
    private Path wavePath2;
    private Bitmap bitmap;
    private Bitmap bitmapOutCircle;

    /**
     * 在Java代码中直接new一个CustomView实例的时候，会调用该构造函数
     */
    public HumitureCircleView2(Context context) {
        this(context,null);
    }

    /**
     * 在xml中引用CustomView标签的时候，会调用2参数的构造函数。
     * 这种方式通常是我们需要自定义View的属性的时候，使用2参数的构造函数。
     */
    public HumitureCircleView2(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public HumitureCircleView2(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.HumitureCircleView);
        hvCircleRadius = typedArray.getDimensionPixelOffset(R.styleable.HumitureCircleView_hvCircleRadius, 182);
        hvCircleMargin = typedArray.getDimensionPixelOffset(R.styleable.HumitureCircleView_hvCircleMargin, 10);
        hvProgressWidth = typedArray.getDimensionPixelOffset(R.styleable.HumitureCircleView_hvProgressWidth, 4);

        typedArray.recycle();
        excirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        excirclePaint.setStyle(Paint.Style.FILL);

        progressPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        progressPaint.setStyle(Paint.Style.STROKE);
        progressPaint.setStrokeWidth(hvProgressWidth);



        ripplePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        ripplePaint.setStyle(Paint.Style.FILL);
        setLayerType(LAYER_TYPE_SOFTWARE, ripplePaint);
//        PorterDuffXfermode mode = new PorterDuffXfermode(PorterDuff.Mode.DST_IN);
//        ripplePaint.setXfermode(mode);


        wavePath1 = new Path();
        wavePath2 = new Path();

        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.img_circle_bg);
        bitmapOutCircle = BitmapFactory.decodeResource(getResources(), R.drawable.img_out_circle_bg);
        mBallBitmap = Bitmap.createBitmap(bitmapOutCircle.getWidth(),bitmapOutCircle.getHeight(), Bitmap.Config.ARGB_8888);
        bitmapCanvas = new Canvas(mBallBitmap);

        progressRectF = new RectF(bitmapOutCircle.getWidth() - bitmap.getWidth()  + hvProgressWidth*5, bitmapOutCircle.getWidth() - bitmap.getWidth()  + hvProgressWidth*5, bitmap.getWidth()  - hvProgressWidth*5, bitmap.getWidth()  - hvProgressWidth*5);
        setProgress(30);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(bitmapOutCircle.getWidth(),bitmapOutCircle.getHeight());
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

    public void setProgress(int progress){
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
        ValueAnimator mProgressAnim = ValueAnimator.ofInt(bitmap.getHeight(),30);
        mProgressAnim.setDuration(3000);
        mProgressAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mWaterTop = (int) animation.getAnimatedValue();
                postInvalidate();
            }
        });


        valueAnimator.start();
        mProgressAnim.start();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //绘制最外层、内层圆
        canvas.drawBitmap(bitmapOutCircle,0,0,excirclePaint);
        canvas.drawBitmap(bitmap,(bitmapOutCircle.getWidth()-bitmap.getWidth())/2f,(bitmapOutCircle.getHeight()-bitmap.getHeight())/2f,excirclePaint);

        int layerID = canvas.saveLayer(0,0,bitmapOutCircle.getWidth(),bitmapOutCircle.getHeight(),ripplePaint,Canvas.ALL_SAVE_FLAG);
        //绘制波纹
        createWavePath(wavePath1,mItemWidth,dy1,mOffsetX1);
        createWavePath(wavePath2,mItemWidth*5/4,dy2,mOffsetX1*5/4);
        ripplePaint.setColor(0xBBFFFFFF);
        bitmapCanvas.drawPath(wavePath1,ripplePaint);
        ripplePaint.setColor(0xEEFFFFFF);
        bitmapCanvas.drawPath(wavePath2,ripplePaint);
        //模式合成
        canvas.drawBitmap(bitmapOutCircle,0,0,ripplePaint);
        ripplePaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        canvas.drawBitmap(mBallBitmap,0,0,ripplePaint);
        ripplePaint.setXfermode(null);
        canvas.restoreToCount(layerID);

//        canvas.drawBitmap(bitmapOutCircle,0,0,excirclePaint);
//        canvas.drawBitmap(bitmap,(bitmapOutCircle.getWidth()-bitmap.getWidth())/2f,(bitmapOutCircle.getHeight()-bitmap.getHeight())/2f,excirclePaint);
//        progressPaint.setColor(0xFF14171A);
//        canvas.drawArc(progressRectF,150,240,false,progressPaint);
//        progressPaint.setColor(0xFF00ADA2);
//        canvas.drawArc(progressRectF,150,100,false,progressPaint);
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
}
