package com.landleaf.normal.widght;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.OvershootInterpolator;

import com.landleaf.normal.R;

import java.text.DecimalFormat;


/**
 * 自定义View,显示刻度盘，外带指针显示位置
 *
 * @author Chenyifei
 */
public class LandleafView extends View {

    protected static final String TAG = LandleafView.class.getSimpleName();

    protected Paint progressPaint = new Paint(Paint.ANTI_ALIAS_FLAG);// 画除进度的画笔

    protected Paint trianglePaint = new Paint(Paint.ANTI_ALIAS_FLAG);//画三角形的画笔

    protected Paint txtPaint = new Paint(Paint.ANTI_ALIAS_FLAG);//绘制文本

    protected Path path = new Path();

    protected static final int ANIMATION_DURATION = 5000;

    protected float cCurProgress;
    protected float cStartAngle;
    protected float cSweepAngle;
    protected float cLineWidth;
    protected int cBgColor;

    protected void setProgressColor(int cProgressColor) {
        this.cProgressColor = cProgressColor;
    }

    protected int cProgressColor;
    protected float sRadius, bRadius;
    protected float xPoint, yPoint;
    protected float maxSweep;
    protected RectF arcRect;
    protected float angle;

    public String progressText;//中间显示进度值
    protected String minText;//范围最小值
    protected String maxText;//范围最大值
    protected String levelText;//等级值
    protected int strUnitSize;//单位文本大小

    protected String strUnit;//单位

    protected int progressTextSize;//中间显示进度文字大小
    protected int rangeSize;//范围文字大小
    protected int levelBg;//等级背景
    protected int levelTextSize;//等级字体大小

    protected int boundSize;//等级的弧度

    protected RectF bounds = new RectF();//绘制等级字体的背景

    protected float startNum;//初始值

    public void setProgressText(float value) {
        this.progressText = String.valueOf(value);
        invalidate();
    }

    /**
     * 构造函数
     *
     * @param context 上下文
     *
     * @param attrs   属性
     */
    public LandleafView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttrs(context, attrs);
        initPaint();
    }

    protected float cTriangleMargin;

    protected float cRadius;

    protected float mCenter;

    /**
     * 初始化参数
     *
     * @param context 上下文对象
     * @param attrs   属性值
     */
    protected void initAttrs(Context context, AttributeSet attrs) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.LandleafView);
        cBgColor = ta.getInteger(R.styleable.LandleafView_cBgColor, Color.GRAY);
        cProgressColor = ta.getInteger(R.styleable.LandleafView_cProgressColor, Color.GREEN);
        cCurProgress = ta.getInteger(R.styleable.LandleafView_cCurProgress, 0);
        cStartAngle = ta.getInteger(R.styleable.LandleafView_cStartAngle, 120);
        cRadius = ta.getDimensionPixelOffset(R.styleable.LandleafView_cRadius, 200);
        cLineWidth = ta.getDimensionPixelOffset(R.styleable.LandleafView_cLineWidth, 20);
        cTriangleMargin = ta.getDimensionPixelOffset(R.styleable.LandleafView_cTriangleMargin, 10);
        float cTriangleWidth = ta.getDimensionPixelOffset(R.styleable.LandleafView_cTriangleWidth, 20);
        //新加入的属性-大小
        progressTextSize = ta.getDimensionPixelOffset(R.styleable.LandleafView_cProgressTextSize, 110);

        rangeSize = ta.getDimensionPixelOffset(R.styleable.LandleafView_cRangeTextSize, 28);
        txtPaint.setTextSize(rangeSize);

        levelBg = ta.getInteger(R.styleable.LandleafView_cLevelTextBg, Color.parseColor("#39b54a"));

        levelTextSize = ta.getDimensionPixelOffset(R.styleable.LandleafView_cLevelTextSize, 28);

        boundSize = ta.getDimensionPixelOffset(R.styleable.LandleafView_cBoundSize, 5);

        //默认文本
        progressText = ta.getString(R.styleable.LandleafView_cProgressText);
        startNum = Float.parseFloat(progressText);

        minText = ta.getString(R.styleable.LandleafView_cMinText);
        maxText = ta.getString(R.styleable.LandleafView_cMaxText);
        levelText = ta.getString(R.styleable.LandleafView_cLevelText);

        strUnit = ta.getString(R.styleable.LandleafView_strUnit);
        strUnitSize = ta.getDimensionPixelOffset(R.styleable.LandleafView_strUnitSize, 30);

        ta.recycle();
        //初始化绘制参数
        new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
        sRadius = cTriangleMargin + cRadius + cLineWidth;// 小圆半径
        Log.d(TAG, "小圆半径:" + sRadius);
        // 根据余弦公式求出圆的半径
        bRadius = (float) Math.sqrt(Math.pow(sRadius, 2) + Math.pow(cTriangleWidth, 2)
                - 2 * sRadius * cTriangleWidth * Math.cos(150 * Math.PI / 180));//大圆半径hhuu
        maxSweep = 540 - 2 * cStartAngle;
        //圆半径+线宽+三角形距离圆+三角形的高
        mCenter = (float) (cRadius + cLineWidth + cTriangleMargin + cTriangleWidth * Math.sin(60 * Math.PI / 180));// 360
//        LLog.d(TAG, "最大滑动:" + maxSweep);
//        LLog.d(TAG, "距离圆心" + mCenter);
        arcRect = new RectF(mCenter - cRadius, mCenter - cRadius, mCenter + cRadius, mCenter + cRadius);
        xPoint = arcRect.centerX();
        yPoint = arcRect.centerY();
        //角度
        angle = Math.round((float) (Math.asin(cTriangleWidth * Math.sin(150 * Math.PI / 180) / bRadius) / Math.PI * 180));
//        LLog.d(TAG, "大圆半径：" + bRadius);
//        LLog.d(TAG, "小圆半径：" + sRadius);
        // 根据进度计算弧形的角度
        float atAngle = cSweepAngle + cStartAngle;

        //根据余弦定理，求出第三边长度
        float rangeMargin = (float) Math.sqrt(bRadius * bRadius * 2 - 2 * bRadius * bRadius * Math.cos((360 - maxSweep) * Math.PI / 180));
        // Log.d(TAG, "angle:" + (360 - maxSweep));
        // Log.d(TAG, "rangeMargin:" + rangeMargin);

        x1 = (float) (xPoint + sRadius * Math.cos(atAngle * Math.PI / 180));
        y1 = (float) (yPoint + sRadius * Math.sin(atAngle * Math.PI / 180));

        leftX = x1 + cTriangleMargin;
        leftY = y1 + cTriangleMargin;

        rightX = (x1 + rangeMargin) - txtPaint.measureText(minText);
        rightY = y1 + cTriangleMargin;

        calRadian();
    }

    /**
     * 计算滑动的弧度值
     */
    protected void calRadian() {
        if (cCurProgress < 0 || cCurProgress > 100) {
            throw new IllegalArgumentException("progress must >=0 && <=100，now progress is " + cCurProgress);
        }
        cSweepAngle = (cCurProgress / 100f) * maxSweep;
//        LLog.d(TAG, "滑动弧度:" + cSweepAngle);
    }

    /**
     * 绘制扇形
     *
     * @param canvas     画布
     * @param sweepAngle 滑动角度
     */
    public void drawArc(Canvas canvas, float sweepAngle) {
//        LLog.d(TAG, "绘制弧形!!!");
        canvas.drawArc(arcRect, cStartAngle, sweepAngle, false, progressPaint);
    }

    public LandleafView(Context context) {
        super(context);
        initAttrs(context, null);
        initPaint();
    }

    /**
     * 初始化画笔
     */
    protected void initPaint() {
        progressPaint.setAntiAlias(true);
        progressPaint.setStyle(Paint.Style.STROKE);
        PathEffect mPathEffect = new DashPathEffect(new float[]{3, 9, 3, 9}, 1);
        progressPaint.setPathEffect(mPathEffect);
        progressPaint.setStrokeWidth(cLineWidth);
        trianglePaint.setAntiAlias(true);
        trianglePaint.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        /* 计算控件宽度与高度 */
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int width;
        int height;
        if (widthMode == MeasureSpec.EXACTLY) {
            width = widthSize;
        } else {
            width = (int) (getPaddingLeft() + mCenter * 2 + getPaddingRight());
        }
        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        } else {
            height = (int) (getPaddingTop() + mCenter * 2 + getPaddingBottom());
        }
        //Log.d(TAG, "重置宽高：" + width + ":" + height);
        setMeasuredDimension(width, height);
    }

    /**
     * 构造函数
     *
     * @param context      上下文
     * @param attrs        属性
     * @param defStyleAttr 自定义
     */
    public LandleafView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(context, attrs);
        initPaint();
    }

    protected float leftX;
    protected float leftY;

    protected float rightX;
    protected float rightY;

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(Color.TRANSPARENT);
        // 画虚线背景
        progressPaint.setColor(cBgColor);
        //overive
        drawArc(canvas, maxSweep);
        // 画进度线
        progressPaint.setColor(cProgressColor);
        drawArc(canvas, cSweepAngle);
        // 画等边三角形
        trianglePaint.setColor(Color.WHITE);
        drawTriangle(canvas, cSweepAngle);
        //// TODO: 2017/7/15 新加入的属性
        txtPaint.setStyle(Paint.Style.FILL);
        //// TODO: 2017/7/15 计算出的x轴向左偏移值
//        float shiftLen = (mCenter - cRadius - cLineWidth) / 2;
//        Log.d(TAG, "ShiftLen:" + shiftLen);
        txtPaint.setAntiAlias(true);
        //// TODO: 2017/7/15 计算等级的宽高和x，y坐标
        txtPaint.setColor(levelBg);
        txtPaint.setTextSize(levelTextSize);
        float strHeight = getStringHeight(txtPaint);
        float strWidth = getStringWidth(levelText, txtPaint);
        float levelMargin = cTriangleMargin / 2;
        //等级背景（矩形，以此为坐标计算）
        bounds.set(xPoint - txtPaint.measureText(levelText) / 2f - levelMargin, yPoint + sRadius / 2 - strHeight - levelMargin,

                xPoint - txtPaint.measureText(levelText) / 2f + strWidth + levelMargin, yPoint + sRadius / 2 + levelMargin);
        //画等级背景
        canvas.drawRoundRect(bounds, boundSize, boundSize, txtPaint);
        //画等级文本
        txtPaint.setTextAlign(Paint.Align.CENTER);
        txtPaint.setColor(Color.WHITE);
        Paint.FontMetricsInt fontMetrics = txtPaint.getFontMetricsInt();
        float baseline = (bounds.bottom + bounds.top - fontMetrics.bottom - fontMetrics.top) / 2;
        //下面这行是实现水平居中，drawText对应改为传入targetRect.centerX()
        canvas.drawText(levelText, bounds.centerX(), baseline, txtPaint);


        if (strUnit != null && !strUnit.equals("")) {
            txtPaint.setTextSize(strUnitSize);
            txtPaint.setColor(Color.WHITE);
            canvas.drawText(strUnit, bounds.centerX(), baseline - 40, txtPaint);
        }

        //画范围-此处的坐标需要固定
        txtPaint.setTextSize(rangeSize);
        canvas.drawText(minText, leftX, leftY, txtPaint);
        canvas.drawText(maxText, rightX, rightY, txtPaint);
        //画中间显示的数据
        txtPaint.setColor(Color.WHITE);
        txtPaint.setTextSize(progressTextSize);
        txtPaint.setShadowLayer(3, 3, 4, Color.BLACK);
        canvas.drawText(progressText, bounds.centerX(), yPoint + sRadius / 5 - cTriangleMargin * 2, txtPaint);
        txtPaint.reset();
    }

    protected float x1, y1;

    /**
     * 绘制圆外的三角形指示器
     * <p>
     * 绘制规则：找到三个顶点的坐标
     * <p>
     * 都在圆的路径上,圆形坐标为：(xPoint,yPort)/(200,200)
     * <p>
     * 圆点坐标：(x0,y0) 半径：r 角度：a0
     * <p>
     * 则圆上任一点为：（x1,y1）,x1 = x0 + r * cos(ao * Math.PI /180 ),y1 = y0 + r *
     * sin(ao *Math.PI /180 )
     * <p>
     * 余弦定理公式:c=Math.sqrt(Math.pow(a,2)+Math.pow（b,2)-2*a*b*Math.cos(C))
     * 正弦定理公式:A/sin(a)=B/sin(b)=C/sin(c);
     * <p>
     * 计算大圆和小圆的半径
     *
     * @param canvas     画布
     * @param sweepAngle 滑动角度
     */
    protected void drawTriangle(Canvas canvas, float sweepAngle) {
//        LLog.d(TAG, "画三角形");
        float atAngle = sweepAngle + cStartAngle;
//        LLog.d(TAG, "所处角度：" + atAngle);

        // 根据公式求出圆上点的坐标
        x1 = (float) (xPoint + sRadius * Math.cos(atAngle * Math.PI / 180));
        y1 = (float) (yPoint + sRadius * Math.sin(atAngle * Math.PI / 180));

        // 根据余弦定理求出角度
//        LLog.d(TAG, "角度：" + angle);

        float x2 = (float) (xPoint + bRadius * Math.cos((atAngle + angle) * Math.PI / 180));
        float y2 = (float) (yPoint + bRadius * Math.sin((atAngle + angle) * Math.PI / 180));

        float x3 = (float) (xPoint + bRadius * Math.cos((atAngle - angle) * Math.PI / 180));
        float y3 = (float) (yPoint + bRadius * Math.sin((atAngle - angle) * Math.PI / 180));


        path.setFillType(Path.FillType.EVEN_ODD);
        path.moveTo(x1, y1);
        path.lineTo(x2, y2);
        path.lineTo(x3, y3);
        path.lineTo(x1, y1);
        // 画三角形
        path.close();
        canvas.drawPath(path, trianglePaint);
        path.reset();
//        LLog.d(TAG, "点1：(" + x1 + "," + y1 + "),点2：(" + x2 + "," + y2 + "),点3：(" + x3 + "," + y3 + ")");
    }

    /**
     * 设置朗绿view属性
     *
     * @param value     当前值
     * @param minValue  最小值
     * @param maxValue  最大值
     * @param color     当前值颜色
     * @param levelText 等级文字
     */
    public void setProgress(float value, float minValue, float maxValue, int color, String levelText) {
        float tempValue = value;
        if (value >= maxValue) {
            tempValue = maxValue;
        }
        if (value <= minValue) {
            tempValue = minValue;
        }
        int progress = (int) ((tempValue - minValue) / (maxValue - minValue) * 100.0F);
        this.levelText = levelText;
        this.levelBg = color;
        this.minText = String.valueOf(minValue);
        this.maxText = String.valueOf(maxValue);
        if (progress < 0 || progress > 100)
            throw new IllegalArgumentException("progress must >=0 && <=100，now progress is " + progress);
        //圆环动画
        runCircleAnimator(cCurProgress, progress, color);
        //文字动画
        //显示最小，最大值
        runTextAnimator(tempValue);
    }

    public void setProgress(float value, float minValue, float maxValue, int color, String levelText, String strUnit) {
        this.strUnit = strUnit;
        setProgress(value, minValue, maxValue, color, levelText);
    }

    public void setLevel(int color, String levelText) {
        this.levelText = levelText;
        this.levelBg = color;
        setProgressColor(levelBg);
        invalidate();
    }

    public void setRange(float min, float max) {
        this.minText = String.valueOf(min);
        this.maxText = String.valueOf(max);
        invalidate();
    }

    public void setRange(float min, float max, int color) {
        this.minText = String.valueOf(min);
        this.maxText = String.valueOf(max);
        this.levelBg = color;
        invalidate();
    }

    protected ValueAnimator progressAnim;

    /**
     * 设置进度
     *
     * @param oldProgress 原始进度
     * @param endProgress 现在的进度
     */
    protected void runCircleAnimator(final float oldProgress, final float endProgress, final int color) {
        if (progressAnim != null) {
            progressAnim.cancel();
        }
        progressAnim = ValueAnimator.ofInt(1);
        progressAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator mAnim) {
                float fraction = mAnim.getAnimatedFraction();
                cCurProgress = evaluate(fraction, (int) oldProgress, (int) endProgress);
                // 防止超越最大值
                if (cCurProgress >= 0 && cCurProgress <= 100) {
                    // 根据进度计算角度
                    calRadian();
                    //设置颜色
                    setProgressColor(color);
                    //调用onDraw绘制
                    invalidate();
                }
            }

            private Integer evaluate(float fraction, Integer startValue, Integer endValue) {
                int startInt = startValue;
                return (int) (startInt + fraction * (endValue - startInt));
            }
        });
        progressAnim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                progressAnim = null;
                //更新当前进度
                cCurProgress = endProgress;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                progressAnim = null;
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        progressAnim.setInterpolator(new OvershootInterpolator());
        progressAnim.setDuration((int) (ANIMATION_DURATION * (Math.abs((endProgress - oldProgress)) / 100)));
        progressAnim.start();
    }

    protected DecimalFormat decimalFormat;

    protected DecimalFormat getDecimalFormat(float number) {
        int len;
        if (!(number + "").contains(".")) {
            len = 0;
        } else {
            len = (number + "").length() - (number + "").indexOf(".") - 1;
        }
        StringBuilder str = new StringBuilder("##0");
        if (len >= 1) {
            str.append(".");
            for (int i = 0; i < len; i++) {
                str.append("0");
            }
        }
        // Log.d(TAG, "Format:" + str);
        decimalFormat = new DecimalFormat(str.toString());
        return decimalFormat;
    }

    protected ValueAnimator txtAnim;

    protected void runTextAnimator(float number) {
        int duration = 1000;
        decimalFormat = getDecimalFormat(number);
        if (txtAnim != null) {
            txtAnim.cancel();
        }
        txtAnim = ValueAnimator.ofFloat(startNum, number);
        txtAnim.setDuration(duration);
        txtAnim.addUpdateListener(animation -> {
            progressText = decimalFormat.format(Float.parseFloat(animation.getAnimatedValue().toString()));
            invalidate();
        });
        txtAnim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                txtAnim = null;
                startNum = number;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                txtAnim = null;
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        txtAnim.start();
    }

    public float getStringWidth(String str, Paint paint) {
        return paint.measureText(str);
    }

    public float getStringHeight(Paint paint) {
        Paint.FontMetrics fr = paint.getFontMetrics();
        return (float) (Math.ceil(fr.descent - fr.top) + 2);  //ceil() 函数向上舍入为最接近的整数。
    }
}