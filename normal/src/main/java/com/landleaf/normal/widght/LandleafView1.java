package com.landleaf.normal.widght;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.util.AttributeSet;

import com.landleaf.normal.R;

/**
 * 自定义View,显示刻度盘，外带指针显示位置
 * <p>
 * 2017-10-19  新版朗绿监测转盘
 *
 * @author Chenyifei
 */
public class LandleafView1 extends LandleafView {

    protected String attrChineseName;

    protected String attrEnglishName;

    protected int attrChineseSize;

    protected int attrEnglishSize;

    protected Typeface ttfRegular;

    protected Typeface ttfMedium;

    public LandleafView1(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LandleafView1(Context context) {
        super(context);
    }

    public LandleafView1(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void initAttrs(Context context, AttributeSet attrs) {
        super.initAttrs(context, attrs);
        @SuppressLint("CustomViewStyleable")
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.LandleafView);
        attrChineseName = ta.getString(R.styleable.LandleafView_cChineseAttrName);
        attrEnglishName = ta.getString(R.styleable.LandleafView_cEnglishAttrName);

        attrChineseSize = ta.getDimensionPixelSize(R.styleable.LandleafView_cChineseAttrSize, 28);
        attrEnglishSize = ta.getDimensionPixelSize(R.styleable.LandleafView_cEnglishAttrSize, 20);

        ttfRegular = Typeface.createFromAsset(getContext().getAssets(), "fonts/pingfangRegularLite.ttf");
        ttfMedium = Typeface.createFromAsset(getContext().getAssets(), "fonts/pingfangMediumLite.ttf");
        ta.recycle();
    }

    public void setAttrName(String attrChineseName, String attrEnglishName) {
        this.attrChineseName = attrChineseName;
        this.attrEnglishName = attrEnglishName;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //绘制透明背景
        canvas.drawColor(Color.TRANSPARENT);
        // 画虚线，背景线
        progressPaint.setColor(cBgColor);
        drawArc(canvas, maxSweep);
        // 画虚线，实际进度线
        progressPaint.setColor(cProgressColor);
        drawArc(canvas, cSweepAngle);
        // 画等边三角形
        trianglePaint.setColor(Color.WHITE);
        drawTriangle(canvas, cSweepAngle);
        //等级背景（矩形，以此为坐标计算),画等级背景
        drawLevelBackground(canvas);
        //画范围-此处的坐标需要固定
        drawRange(canvas);
        //画中文属性名
        drawAttrText(canvas, attrChineseName, Color.WHITE, attrChineseSize, drawLevel(canvas) + sRadius / 4.3f, txtPaint);
        //画英文属性名
        drawAttrText(canvas, attrEnglishName, Color.parseColor("#72718e"), attrEnglishSize, drawLevel(canvas) + sRadius / 2.4f, txtPaint);
        //画中间显示的数据
        showCenterNumber(canvas, ttfMedium);
    }

    protected void drawLevelBackground(Canvas canvas) {
        //// TODO: 2017/7/15 新加入的属性,设置字体属性，包括中文等级，中文数据类型，英文数据类型
        txtPaint.setStyle(Paint.Style.FILL);
        txtPaint.setTypeface(ttfRegular);
        //抗锯齿
        txtPaint.setAntiAlias(true);
        //// TODO: 2017/7/15 计算等级的宽高和x，y坐标
        txtPaint.setColor(levelBg);
        txtPaint.setTextSize(levelTextSize);
        float strHeight = getStringHeight(txtPaint);
        float strWidth = getStringWidth(levelText, txtPaint);
        float levelMargin = cTriangleMargin / 2;
        bounds.set(xPoint - txtPaint.measureText(levelText) / 2f - levelMargin, yPoint + sRadius / 2 - strHeight - levelMargin - sRadius / 4f,
                xPoint - txtPaint.measureText(levelText) / 2f + strWidth + levelMargin, yPoint + sRadius / 2 + levelMargin - sRadius / 4f);
        canvas.drawRoundRect(bounds, boundSize, boundSize, txtPaint);



    }

    protected float drawLevel(Canvas canvas) {
        txtPaint.setTextAlign(Paint.Align.CENTER);
        txtPaint.setColor(Color.WHITE);
        Paint.FontMetricsInt fontMetrics = txtPaint.getFontMetricsInt();
        float baseline = (bounds.bottom + bounds.top - fontMetrics.bottom - fontMetrics.top) / 2;
        canvas.drawText(levelText, bounds.centerX(), baseline, txtPaint);


        return baseline;
    }

    private void drawRange(Canvas canvas) {
        txtPaint.setTextSize(rangeSize);
        txtPaint.setColor(Color.WHITE);
        canvas.drawText(minText, leftX, leftY, txtPaint);
        canvas.drawText(maxText, rightX, rightY, txtPaint);
        txtPaint.setStrokeWidth(2);
    }

    protected void drawAttrText(Canvas canvas, String attrEnglishName, int color, int attrEnglishSize, float y, Paint txtPaint) {
        if (!TextUtils.isEmpty(attrEnglishName)) {
            txtPaint.setColor(color);
            txtPaint.setTextSize(attrEnglishSize);
            canvas.drawText(attrEnglishName, bounds.centerX(), y, txtPaint);
        }
    }

    protected void showCenterNumber(Canvas canvas, Typeface typeface) {
        txtPaint.setTypeface(typeface);
        txtPaint.setColor(Color.WHITE);
        txtPaint.setTextSize(progressTextSize);
        txtPaint.setShadowLayer(3, 3, 4, Color.BLACK);
        txtPaint.setStrokeWidth(5);
        canvas.drawText(progressText, bounds.centerX(), bounds.centerY() - sRadius / 4.3f, txtPaint);
        txtPaint.reset();
    }
}