package com.landleaf.everyday.widget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.*;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

import com.landleaf.everyday.R;

/**
 * Created by qijian on 17/2/10.
 */
public class TextWave_DSTIN extends View {
    private Paint mPaint;
    private Path mPath;
    private int mItemWaveLength = 1000;
    private int dx;

    private Bitmap BmpSRC,BmpDST;
    public TextWave_DSTIN(Context context, AttributeSet attrs) {
        super(context, attrs);
        mPath = new Path();
        mPaint = new Paint();
        mPaint.setColor(Color.GREEN);
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);

        BmpSRC = BitmapFactory.decodeResource(getResources(), R.drawable.text_shade, null);
        BmpDST = Bitmap.createBitmap(BmpSRC.getWidth(), BmpSRC.getHeight(), Bitmap.Config.ARGB_8888);

        startAnim();
    }

    @Override
protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);
    canvas.drawColor(Color.WHITE);

    generageWavePath();

    //先清空bitmap上的图像,然后再画上Path
    Canvas c = new Canvas(BmpDST);
    c.drawColor(Color.BLACK, PorterDuff.Mode.CLEAR);
    c.drawPath(mPath,mPaint);

    canvas.drawBitmap(BmpSRC,0,0,mPaint);
    int layerId = canvas.saveLayer(0, 0, getWidth(), getHeight(), null, Canvas.ALL_SAVE_FLAG);
    canvas.drawBitmap(BmpDST,0,0,mPaint);
    mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
    canvas.drawBitmap(BmpSRC,0,0,mPaint);
    mPaint.setXfermode(null);
    canvas.restoreToCount(layerId);
}

/**
 * 生成此时的Path
 */
private void generageWavePath(){
    mPath.reset();
    int originY = BmpSRC.getHeight()/2;
    int halfWaveLen = mItemWaveLength/2;
    mPath.moveTo(-mItemWaveLength+dx,originY);
    for (int i = -mItemWaveLength;i<=getWidth()+mItemWaveLength;i+=mItemWaveLength){
        mPath.rQuadTo(halfWaveLen/2,-50,halfWaveLen,0);
        mPath.rQuadTo(halfWaveLen/2,50,halfWaveLen,0);
    }
    mPath.lineTo(BmpSRC.getWidth(),BmpSRC.getHeight());
    mPath.lineTo(0,BmpSRC.getHeight());
    mPath.close();
}

public void startAnim(){
    ValueAnimator animator = ValueAnimator.ofInt(0,mItemWaveLength);
    animator.setDuration(2000);
    animator.setRepeatCount(ValueAnimator.INFINITE);
    animator.setInterpolator(new LinearInterpolator());
    animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
        public void onAnimationUpdate(ValueAnimator animation) {
            dx = (Integer)animation.getAnimatedValue();
            postInvalidate();
        }
    });
    animator.start();
}
}
