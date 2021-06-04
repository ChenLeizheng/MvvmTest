package com.landleaf.everyday.widget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.*;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

import com.landleaf.everyday.R;

/**
 * Created by qijian on 17/2/13.
 */
public class IrregularWaveView extends View {

    private final Bitmap mBallBitmap;
    private Paint mPaint;
    private int mItemWaveLength = 0;
    private int dx=0;

    private Bitmap BmpSRC,BmpDST;

    public IrregularWaveView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mPaint = new Paint();

        BmpDST = BitmapFactory.decodeResource(getResources(), R.drawable.wave_bg, null);
        BmpSRC = BitmapFactory.decodeResource(getResources(), R.drawable.circle_shape,null);
        mBallBitmap = Bitmap.createBitmap(BmpSRC.getWidth(), BmpSRC.getHeight(), Bitmap.Config.ARGB_8888);
        mItemWaveLength = BmpDST.getWidth();

        startAnim();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(BmpSRC.getWidth(),BmpSRC.getHeight());
    }

    @Override
protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);

    canvas.drawColor(Color.WHITE);

    //先画上圆形
//    canvas.drawBitmap(BmpSRC,0,0,mPaint);
    //再画上结果
    int layerId = canvas.saveLayer(0, 0, getWidth(), getHeight(), null, Canvas.ALL_SAVE_FLAG);
//    canvas.drawBitmap(BmpDST,new Rect(dx,0,dx+BmpSRC.getWidth(),BmpSRC.getHeight()),new Rect(0,0,BmpSRC.getWidth(),BmpSRC.getHeight()),mPaint);
        Path wavePath1 = new Path();
        Path wavePath2 = new Path();
        createWavePath(wavePath1,mItemWidth,25,dx);
        createWavePath(wavePath2,mItemWidth*5/4,25,dx*5/4);
        Canvas cs = new Canvas(mBallBitmap);
        cs.drawColor(Color.BLACK, PorterDuff.Mode.CLEAR);
        cs.drawPath(wavePath1,mPaint);
//        cs.drawPath(wavePath2,mPaint);
        canvas.drawBitmap(mBallBitmap,0,0,mPaint);
//        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
//    canvas.drawBitmap(BmpSRC,0,0,mPaint);
//    mPaint.setXfermode(null);
    canvas.restoreToCount(layerId);
}

    private int mItemWidth = 150;
public void startAnim(){
    ValueAnimator animator = ValueAnimator.ofInt(0,mItemWaveLength);
    animator.setDuration(4000);
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

    private void createWavePath(Path path,int dx,int dy,int mOffsetX){
        path.reset();
        path.moveTo(-dx+mOffsetX,getWidth());
        for (int i = -dx; i < dx + getWidth(); i += dx) {
            path.rQuadTo(dx / 4, -dy, dx / 2, 0);
            path.rQuadTo(dx / 4, dy, dx / 2, 0);
        }
//        path.lineTo(getWidth(),getWidth());
//        path.lineTo(0,getWidth());
//        path.close();
    }
}

