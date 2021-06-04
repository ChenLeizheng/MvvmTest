package com.landleaf.everyday.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;

import com.landleaf.everyday.R;

/**
 * 轮播图下方小点
 */
public class BannerIndicator extends View {

    private int number = 1;
    private int position = 0;
    private Paint paint;
    private int selectColor;
    private int unSelectColor;
    private float radius;
    private float space;

    public BannerIndicator(Context context) {
        this(context, null);
    }

    public BannerIndicator(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.BannerIndicator);
        this.selectColor = typedArray.getColor(R.styleable.BannerIndicator_biSelectDotColor, 0xFFEEEEEE);
        this.unSelectColor = typedArray.getColor(R.styleable.BannerIndicator_biUnSelectDotColor, 0x50EEEEEE);
        this.radius = typedArray.getDimension(R.styleable.BannerIndicator_biDotRadius, 10);
        this.space = typedArray.getDimension(R.styleable.BannerIndicator_biDotSpace, 10);
        typedArray.recycle();

        paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = (int) (2*radius*number + (number-1)*space + 1);
        int height = (int) (2*radius + 1);
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (int i = 0; i < number; i++) {
            paint.setColor(position == i ? selectColor : unSelectColor);
            canvas.drawCircle(radius * (2 * i + 1) + i * space, radius, radius, paint);
        }
    }

    public void setNumber(int number) {
        this.number = number;
        //调用这个方法要求parent view重新调用他的onMeasure onLayout来对重新设置自己位置
        requestLayout();
    }

    public void setPosition(int position) {
        this.position = position;
        //调onDraw()重绘
        invalidate();
    }
}
