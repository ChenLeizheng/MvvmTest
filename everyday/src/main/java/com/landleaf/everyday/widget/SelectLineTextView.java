package com.landleaf.everyday.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.landleaf.everyday.R;

public class SelectLineTextView extends View {

    private String title;
    private Paint paint;
    private float lineTvLineMarginTop;
    private float lineTvLineMarginLeft;
    private int normalColor;
    private int selectColor;
    private int selectLineColor;
    private float lineWidth;
    private Rect textRect;

    private boolean select = false;

    public SelectLineTextView(Context context) {
        this(context, null);
    }

    public SelectLineTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.SelectLineTextView);
        title = typedArray.getString(R.styleable.SelectLineTextView_lineTvText);
        if (TextUtils.isEmpty(title)){
            title = "室内温/湿度";
        }
        float textSize = typedArray.getDimension(R.styleable.SelectLineTextView_lineTvTextSize, 24);
        lineWidth = typedArray.getDimension(R.styleable.SelectLineTextView_lineTvLineWidth, 4);
        lineTvLineMarginTop = typedArray.getDimension(R.styleable.SelectLineTextView_lineTvLineMarginTop, 4);
        lineTvLineMarginLeft = typedArray.getDimension(R.styleable.SelectLineTextView_lineTvLineMarginLeft, 4);
        normalColor = typedArray.getColor(R.styleable.SelectLineTextView_lineTvTextColor, 0xFF859BA7);
        selectColor = typedArray.getColor(R.styleable.SelectLineTextView_lineTvSelectTextColor, 0xFFFFFFFF);
        selectLineColor = typedArray.getColor(R.styleable.SelectLineTextView_lineTvSelectLineColor, 0xFF00ADA2);

        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setTextSize(textSize);
        paint.setStrokeWidth(lineWidth);
        textRect = new Rect();
        // textRect.bottom 3  textRect.top -20 基线位置 0
        paint.getTextBounds(title,0,title.length(), textRect);
        typedArray.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int height = (int) (textRect.height() + lineWidth*2 + lineTvLineMarginTop);
        setMeasuredDimension(textRect.width(),height);
    }

    public void setSelect(boolean select){
        this.select = select;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        paint.setColor(select ? selectColor:normalColor);
        canvas.drawText(title,0,textRect.height() - textRect.bottom,paint);
        paint.setColor(select? selectLineColor:Color.TRANSPARENT);
        canvas.drawLine(lineTvLineMarginLeft,getHeight()-lineWidth,getWidth()-lineTvLineMarginLeft,getHeight()-lineWidth,paint);
    }
}
