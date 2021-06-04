package com.landleaf.everyday.widget;

import android.content.Context;
import android.graphics.Bitmap;
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

public class MyBitmapViewAnother2 extends View {

    private Bitmap bitmap;
    private Canvas bitmapCanvas;

    public MyBitmapViewAnother2(Context context) {
        super(context);
    }

    public MyBitmapViewAnother2(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(200,200);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Paint mPaint = new Paint();//Bitmap的画笔

        Paint mPaintCirlcle = new Paint();
        mPaintCirlcle.setColor(Color.YELLOW);
//        setLayerType(LAYER_TYPE_SOFTWARE,null);
//        mPaintCirlcle.setAntiAlias(true);
//        int[] colors = {0x33FFFFFF,0x33FFFFFF,0x88FFFFFF};
//        float[] stops = {0,24/30f,1};
//        canvas.drawColor(Color.LTGRAY);
//        RadialGradient radialGradient = new RadialGradient(getWidth() / 2, getWidth() / 2, getHeight() / 2, colors, stops, Shader.TileMode.CLAMP);
//        mPaintCirlcle.setShader(radialGradient);
//        canvas.drawCircle(getWidth()/2,getWidth()/2,getHeight()/2,mPaintCirlcle);
//        PorterDuffXfermode mode  = new PorterDuffXfermode(PorterDuff.Mode.DST_OVER);
//        mPaintCirlcle.setXfermode(mode);
        canvas.drawColor(Color.LTGRAY);
        bitmap = Bitmap.createBitmap(200, 200, Bitmap.Config.ARGB_8888);
        bitmapCanvas = new Canvas(bitmap);//该画布为bitmap的
        Paint mPaintRect = new Paint();
        mPaintRect.setAntiAlias(true);
        mPaintRect.setColor(Color.GRAY);
        PorterDuffXfermode mode = new PorterDuffXfermode(PorterDuff.Mode.SRC_IN);
        mPaintRect.setXfermode(mode);

        canvas.drawBitmap(bitmap, 0, 0, mPaint);

        bitmapCanvas.drawCircle(getWidth() / 2, getHeight() / 2, getHeight() / 2, mPaintCirlcle);
        bitmapCanvas.drawRect(0, 0, getWidth() / 2, getHeight() / 2, mPaintRect);
    }
}
