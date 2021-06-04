package com.landleaf.everyday.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

public class MyBitmapViewAnother extends View {

    private Bitmap bitmap;
    private Canvas bitmapCanvas;

    public MyBitmapViewAnother(Context context) {
        super(context);
    }

    public MyBitmapViewAnother(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(300,600);
        bitmap = Bitmap.createBitmap(300, 300, Bitmap.Config.ARGB_8888);
        bitmapCanvas = new Canvas(bitmap);//该画布为bitmap的
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Paint mPaint = new Paint();//Bitmap的画笔

        Paint mPaintCirlcle = new Paint();
        setLayerType(LAYER_TYPE_SOFTWARE,null);
        mPaintCirlcle.setAntiAlias(true);
        mPaintCirlcle.setColor(Color.LTGRAY);
        BlurMaskFilter maskfilterOuter = new BlurMaskFilter(20, BlurMaskFilter.Blur.OUTER);
        BlurMaskFilter maskfilterSolid = new BlurMaskFilter(20, BlurMaskFilter.Blur.SOLID);
        mPaintCirlcle.setMaskFilter(maskfilterOuter);
        canvas.drawCircle(getWidth()/2,getWidth()/2,getWidth()/2-10,mPaintCirlcle);
        mPaintCirlcle.setMaskFilter(maskfilterSolid);
        canvas.drawCircle(getWidth()/2,getHeight()-getWidth()/2,getWidth()/2-10,mPaintCirlcle);


//        int[] colors = {0x33FFFFFF,0x33FFFFFF,0x88FFFFFF};
//        float[] stops = {0,24/30f,1};
//        canvas.drawColor(Color.LTGRAY);
//        RadialGradient radialGradient = new RadialGradient(getWidth() / 2, getWidth() / 2, getHeight() / 2, colors, stops, Shader.TileMode.CLAMP);
//        mPaintCirlcle.setShader(radialGradient);
//        canvas.drawCircle(getWidth()/2,getWidth()/2,getHeight()/4,mPaintCirlcle);
//        PorterDuffXfermode mode  = new PorterDuffXfermode(PorterDuff.Mode.DST_OVER);
//        mPaintCirlcle.setXfermode(mode);

//        Paint mPaintRect = new Paint();
//        mPaintRect.setAntiAlias(true);
//        mPaintRect.setColor(Color.GRAY);
//        PorterDuffXfermode mode = new PorterDuffXfermode(PorterDuff.Mode.SRC_IN);
//        mPaintRect.setXfermode(mode);

//        canvas.drawBitmap(bitmap, 0, 0, mPaint);

//        bitmapCanvas.drawCircle(getWidth() / 2, getHeight() / 2, getHeight() / 2, mPaintCirlcle);
//        bitmapCanvas.drawRect(0, 0, getWidth() / 2, getHeight() / 2, mPaintRect);
    }
}
