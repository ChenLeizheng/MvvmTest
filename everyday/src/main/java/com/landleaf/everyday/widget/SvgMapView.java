package com.landleaf.everyday.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.core.graphics.PathParser;

import com.landleaf.everyday.R;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class SvgMapView extends View {

    //资源id
    private int rawId;
    private List<ProvinceItem> provinceItems;
    private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    //选中的item
    private ProvinceItem selectItem;
    //svg的宽高
    private float svgWidth, svgHeight;
    private float scale = 1.3f;

    public SvgMapView(Context context) {
        this(context,null);
    }

    public SvgMapView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        rawId = R.raw.vector_china;
        new ParseThread().start();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        scale = getMeasuredWidth() / svgWidth;
        //缩放取小的值
        scale = Math.min(scale,getMeasuredHeight() / svgHeight);

        canvas.save();
        //按照SVG原尺寸绘制，然后根据屏大小缩放得到对应的大小
        canvas.scale(scale, scale);
        for (ProvinceItem provinceItem : provinceItems) {
            if (selectItem != provinceItem)
                provinceItem.drawPath(canvas, mPaint, false);
        }

        if (selectItem != null) selectItem.drawPath(canvas, mPaint, true);
        canvas.restore();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                return true;
            case MotionEvent.ACTION_UP: {
                //获取点击区域
                ProvinceItem temp = null;
                for (ProvinceItem provinceItem : provinceItems) {
                    if (provinceItem.isOnTouch(event.getX() / scale, event.getY() / scale)) {
                        temp = provinceItem;
                        break;
                    }
                }

                if (temp != null) {
                    selectItem = temp;
                    postInvalidate();
                }
            }
        }
        return super.onTouchEvent(event);
    }

    private class ParseThread extends Thread{

        private static final String PATH = "path";
        private static final String DATA = "android:pathData";
        private static final String TITLE = "title";

        @SuppressLint("RestrictedApi")
        @Override
        public void run() {
            try {
                //xml解析地图路径path
                InputStream inputStream = getContext().getResources().openRawResource(rawId);
                DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
                Document document = builder.parse(inputStream);
                Element root = document.getDocumentElement();//获取根节点
                NodeList nodeList = root.getElementsByTagName(PATH);

                provinceItems = new ArrayList<>(nodeList.getLength());
                float left, top, right, bottom;
                left = top = right = bottom = -1;

                for (int i = 0; i < nodeList.getLength(); i++) {
                    Element element = (Element) nodeList.item(i);//path节点
                    //解析pathData属性
                    ProvinceItem provinceItem = new ProvinceItem();
                    provinceItem.setPath(PathParser.createPathFromPathData(element.getAttribute(DATA)));

                    Rect bound = provinceItem.getRegion().getBounds();
                    if (left > bound.left || left == -1) left = bound.left;
                    if (top > bound.top || top == -1) top = bound.top;
                    if (right < bound.right || right == -1) right = bound.right;
                    if (bottom < bound.bottom || bottom == -1) bottom = bound.bottom;
                    //设置color
                    provinceItem.setColor(0xFFCCCCCC);
                    provinceItems.add(provinceItem);
                }

                svgWidth = right - left;
                svgHeight = bottom - top;
                postInvalidate();
            } catch (ParserConfigurationException | IOException | SAXException e) {
                e.printStackTrace();
                Log.d("SvgMapView", e.toString());
            }
        }
    }


    public class ProvinceItem implements Serializable {
        private Path path;
        private int color;
        private Region region;

        public Path getPath() {
            return path;
        }

        public void setPath(Path path) {
            this.path = path;

            region = new Region();
            RectF rect = new RectF();
            path.computeBounds(rect, true);
            region.setPath(path, new Region((int) rect.left, (int) rect.top, (int) rect.right, (int) rect.bottom));
        }

        public int getColor() {
            if (color == 0)
                return Color.BLUE;
            return color;
        }

        public void setColor(int color) {
            this.color = color;
        }

        //画path
        public void drawPath(Canvas canvas, Paint mPaint, boolean isSelected) {
            if (isSelected) {
                //画边框
                mPaint.setStyle(Paint.Style.STROKE);
                mPaint.setShadowLayer(8, 0, 0, Color.BLACK);
                mPaint.setColor(Color.YELLOW);
                mPaint.setStrokeWidth(2);
                canvas.drawPath(path, mPaint);
                mPaint.clearShadowLayer();

                //画内容
                mPaint.setStyle(Paint.Style.FILL);
                mPaint.setColor(color);
                canvas.drawPath(path, mPaint);
            } else {
                //画内容
                mPaint.setStyle(Paint.Style.FILL);
                mPaint.setColor(getColor());
                canvas.drawPath(path, mPaint);

                //画边框
                mPaint.setStyle(Paint.Style.STROKE);
                mPaint.setColor(Color.WHITE);
                mPaint.setStrokeWidth(1);
                canvas.drawPath(path, mPaint);
            }
        }

        public boolean isOnTouch(float x, float y) {
            if (region == null)
                throw new RuntimeException("path has not set");
            return region.contains((int) x, (int) y);
        }

        public Region getRegion() {
            if (region == null)
                throw new RuntimeException("path has not set");
            return region;
        }
    }
}
