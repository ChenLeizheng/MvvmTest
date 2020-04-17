package com.landleaf.everyday.widget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.OvershootInterpolator;

import androidx.annotation.Nullable;

import com.landleaf.everyday.R;

/**
 * 刻度盘控件
 */
public class DialView extends View {

    private int dvOutCircleRadius;
    private int dvProgressWidth;
    private int dvMin;
    private int dvMax;
    private String dvTitle;
    private Paint mCirclePaint;
    private RectF arcRectF;
    private Paint arcProgressPaint;
    private int startColor;
    private int endColor;
    private Paint mBgPaint;
    private int backgroundColor;
    private int dialColor;
    private int dvTitleTextSize;
    private int dvProgressTextSize;
    private Rect textBoundRect;

    public DialView(Context context) {
        this(context, null);
    }

    public DialView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        Typeface pixFontLite = Typeface.createFromAsset(getContext().getAssets(), "fonts/pixFontLite.ttf");
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.DialView);
        dvOutCircleRadius = typedArray.getDimensionPixelOffset(R.styleable.DialView_dvOutCircleRadius, 180);
        int dvInCircleRadius = typedArray.getDimensionPixelOffset(R.styleable.DialView_dvInCircleRadius, 120);
        dvProgressWidth = typedArray.getDimensionPixelOffset(R.styleable.DialView_dvProgressWidth, 30);
        dvProgressTextSize = typedArray.getDimensionPixelOffset(R.styleable.DialView_dvProgressTextSize, 64);
        dvTitleTextSize = typedArray.getDimensionPixelOffset(R.styleable.DialView_dvTitleTextSize, 15);
        startColor = typedArray.getColor(R.styleable.DialView_dvStartColor, 0xFF2467E4);
//        startColor = typedArray.getColor(R.styleable.DialView_dvStartColor, 0xFF00A8E2);
        endColor = typedArray.getColor(R.styleable.DialView_dvEndColor, 0xFF27E2EE);
//        endColor = typedArray.getColor(R.styleable.DialView_dvEndColor, 0xFF00F2F8);
        backgroundColor = typedArray.getColor(R.styleable.DialView_dvBackgroundColor, 0xff0d3d6c);
        dialColor = typedArray.getColor(R.styleable.DialView_dvBackgroundColor, 0xFF45aaf5);
        dvMin = typedArray.getInteger(R.styleable.DialView_dvMin, 0);
        dvMax = typedArray.getInteger(R.styleable.DialView_dvMax, 270);
        dvTitle = typedArray.getString(R.styleable.DialView_dvTitle);
        if (TextUtils.isEmpty(dvTitle)) {
            dvTitle = "风量调节";
        }

        mCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mCirclePaint.setStyle(Paint.Style.STROKE);
        mCirclePaint.setColor(dialColor);

        arcProgressPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        arcProgressPaint.setStyle(Paint.Style.STROKE);
        arcProgressPaint.setStrokeWidth(dvProgressWidth - 4);
        setLayerType(LAYER_TYPE_SOFTWARE, arcProgressPaint);

        mBgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBgPaint.setStyle(Paint.Style.FILL);
        mBgPaint.setTypeface(pixFontLite);

        //圆弧的外接矩形
        arcRectF = new RectF();

        textBoundRect = new Rect();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(measureDimension(widthMeasureSpec), measureDimension(heightMeasureSpec));
    }

    private int measureDimension(int measureSpec) {
        int result;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
            dvOutCircleRadius = result / 2;
        } else {
            result = dvOutCircleRadius * 2;
            if (specMode == MeasureSpec.AT_MOST) {
                result = Math.min(result, specSize);
            }
        }
        return result;
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Log.d("DialView", "ACTION_DOWN:" + x + "," + y);
                if (checkOnProgressArc(x, y)) {
                    float degree = calculateDegree(x, y, getDistance(x, y));
                    currentValue = degree - 45;
                    invalidate();
                    return true;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (checkOnProgressArc(x, y)) {
                    float moveDegree = calculateDegree(x, y, getDistance(x, y));
                    Log.d("DialView", "moveDegree:" + moveDegree);
                    currentValue = Math.round(moveDegree) - 45;
                    invalidate();
                    return true;
                }
                break;
            case MotionEvent.ACTION_UP:
                Log.d("DialView", "ACTION_UP:" + x + "," + y + "," + currentValue);
                break;
            default:
                break;
        }
        return super.onTouchEvent(event);
    }

    private boolean checkOnProgressArc(float x, float y) {
        double distance = getDistance(x, y);
        float degree = calculateDegree(x, y, distance);
        return distance >= dvOutCircleRadius - 2 * dvProgressWidth && distance <= dvOutCircleRadius - dvProgressWidth && degree >= 45 && degree <= 315;
    }

    private double getDistance(float x, float y) {
        return Math.sqrt(Math.pow(x - dvOutCircleRadius, 2) + Math.pow(y - dvOutCircleRadius, 2));
    }

    private float calculateDegree(float x, float y, double distance) {
        float degree = 0;
        int index = 0;
        //0-180
        if (x < dvOutCircleRadius) {
            //[0-90]
            if (y >= dvOutCircleRadius) {
                degree = (float) Math.toDegrees(Math.asin((dvOutCircleRadius - x) / distance));
            } else {
                degree = (float) Math.toDegrees(Math.acos((dvOutCircleRadius - x) / distance)) + 90;
            }

        } else {
            //[180,270]
            if (y <= dvOutCircleRadius) {
                degree = (float) Math.toDegrees(Math.asin((x - dvOutCircleRadius) / distance)) + 180;
            } else {
                degree = (float) Math.toDegrees(Math.acos((x - dvOutCircleRadius) / distance)) + 270;
            }

        }
        return degree;
    }

    String dataUnit = "m³/h";
    boolean isFloat;
    float currentValue;

    public void setProgress(float value, String dataUnit, boolean isFloat) {
        if (!TextUtils.isEmpty(dataUnit)){
            this.dataUnit = dataUnit;
        }
        this.isFloat = isFloat;
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, value);
        valueAnimator.setInterpolator(new OvershootInterpolator());
        valueAnimator.setDuration(3000);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                currentValue = (float) Math.round(value * 100) / 100;
                Log.d("DialView", "currentValue:" + currentValue);
                if (currentValue > dvMax) {
                    currentValue = dvMax;
                }
                invalidate();
            }
        });
        valueAnimator.start();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //绘制最外层的虚线圆
        DashPathEffect dashPathEffect = new DashPathEffect(new float[]{5, 3}, 0);
        mCirclePaint.setPathEffect(dashPathEffect);
        mCirclePaint.setStrokeWidth(2);
        arcRectF.set(0, 0, getWidth(), getWidth());
        canvas.drawCircle(getWidth() / 2f, getWidth() / 2f, getWidth() / 2f-1, mCirclePaint);

        mBgPaint.setColor(backgroundColor);
        canvas.drawCircle(getWidth() / 2f, getWidth() / 2f, dvOutCircleRadius - dvProgressWidth / 3, mBgPaint);

        canvas.save(); //保存之前绘制的内容
        canvas.rotate(90, getWidth() / 2f, getWidth() / 2f);
        //清除虚线绘制内部圆弧
        mCirclePaint.setPathEffect(null);
        mCirclePaint.setStrokeWidth(1);
        arcRectF.set(dvProgressWidth, dvProgressWidth, getWidth() - dvProgressWidth, getWidth() - dvProgressWidth);
        canvas.drawArc(arcRectF, 45, 270, false, mCirclePaint);
        arcRectF.set(2 * dvProgressWidth, 2 * dvProgressWidth, getWidth() - 2 * dvProgressWidth, getWidth() - 2 * dvProgressWidth);
        canvas.drawArc(arcRectF, 45, 270, false, mCirclePaint);

        //绘制进度  起始点 3点钟方向，旋转后起始点6点钟方向
        SweepGradient shader = new SweepGradient(getWidth() / 2f, getWidth() / 2f, startColor, endColor);
        arcProgressPaint.setShader(shader);
        arcRectF.set(dvProgressWidth * 3 / 2f, dvProgressWidth * 3 / 2f, getWidth() - dvProgressWidth * 3 / 2f, getWidth() - dvProgressWidth * 3 / 2f);
        canvas.drawArc(arcRectF, 45, currentValue * 270 / (dvMax - dvMin), false, arcProgressPaint);
        canvas.restore(); //将save前和save后的内容合并到画布

        //绘制刻度
        canvas.save();
        canvas.translate(getWidth() / 2, getHeight() / 2);
        for (int i = 0; i < 19; i++) {
            int angle = 45 + i * 15;
            float[] outStartPoint = calculatePoint(angle, dvOutCircleRadius - dvProgressWidth / 3);
            float[] outEndPoint = calculatePoint(angle, dvOutCircleRadius - dvProgressWidth);
            canvas.drawLine(outStartPoint[0], outStartPoint[1], outEndPoint[0], outEndPoint[1], mCirclePaint);
            float[] inStartPoint = calculatePoint(angle, dvOutCircleRadius - 2 * dvProgressWidth);
            float[] inEndPoint = calculatePoint(angle, dvOutCircleRadius - 5 * dvProgressWidth / 2f);
            canvas.drawLine(inStartPoint[0], inStartPoint[1], inEndPoint[0], inEndPoint[1], mCirclePaint);
            if (i % 2 == 0) {
                String text = String.valueOf(i * (dvMax - dvMin) / 18);
                mCirclePaint.getTextBounds(text, 0, text.length(), textBoundRect);
                float x = 0, y = 0;
                if (angle >= 45 && angle <= 135) {
                    mCirclePaint.setTextAlign(Paint.Align.LEFT);
                    y = i == 0 ? 0 : textBoundRect.height() / 2;
                } else if (angle > 225 && angle <= 315) {
                    mCirclePaint.setTextAlign(Paint.Align.RIGHT);
                } else {
                    mCirclePaint.setTextAlign(Paint.Align.CENTER);
                    y = textBoundRect.height();
                }
                canvas.drawText(text, inEndPoint[0] + x, inEndPoint[1] + y, mCirclePaint);
            }
        }

        //绘制进度文字等
        mBgPaint.setColor(Color.WHITE);
        int value = (int) currentValue;
        mBgPaint.setTextSize(dvProgressTextSize);
        String data = isFloat ? String.valueOf(currentValue) : String.valueOf(value);

        mBgPaint.getTextBounds(data, 0, data.length(), textBoundRect);
        int width = textBoundRect.width();
        int height = textBoundRect.height();
        canvas.drawText(data, -width / 2, height / 2, mBgPaint);
        mCirclePaint.getTextBounds(dataUnit, 0, dataUnit.length(), textBoundRect);
        canvas.drawText(dataUnit, textBoundRect.width() / 2, height / 2 + textBoundRect.height() * 3 / 2, mCirclePaint);
        mBgPaint.setTextSize(dvTitleTextSize);
        mBgPaint.getTextBounds(dvTitle, 0, dvTitle.length(), textBoundRect);
        canvas.drawText(dvTitle, -textBoundRect.width() / 2, dvOutCircleRadius - 2 * dvProgressWidth + textBoundRect.height() / 2, mBgPaint);
    }

    private float[] calculatePoint(float angle, float length) {
        float[] points = new float[2];
        if (angle <= 90f) {
            points[0] = -(float) Math.sin(angle * Math.PI / 180) * length;
            points[1] = (float) Math.cos(angle * Math.PI / 180) * length;
        } else if (angle <= 180f) {
            points[0] = -(float) Math.cos((angle - 90) * Math.PI / 180) * length;
            points[1] = -(float) Math.sin((angle - 90) * Math.PI / 180) * length;
        } else if (angle <= 270f) {
            points[0] = (float) Math.sin((angle - 180) * Math.PI / 180) * length;
            points[1] = -(float) Math.cos((angle - 180) * Math.PI / 180) * length;
        } else if (angle <= 360f) {
            points[0] = (float) Math.cos((angle - 270) * Math.PI / 180) * length;
            points[1] = (float) Math.sin((angle - 270) * Math.PI / 180) * length;
        }
        return points;

    }
}
