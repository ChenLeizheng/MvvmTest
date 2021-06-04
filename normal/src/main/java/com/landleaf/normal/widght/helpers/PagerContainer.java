/*
 * Copyright (c) 2017. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.landleaf.normal.widght.helpers;

import android.content.Context;
import android.graphics.Point;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import androidx.viewpager.widget.ViewPager;

import com.landleaf.normal.widght.CustomViewPager;


/**
 * A FrameLayout which is used in conjunction with a viewpager. This view allows touches to be
 * passed to the viewpager which are normally outside of the viewpager's bounds. Also has the
 * clip children false set to true so that views on the edge of the viewpager can be seen.
 * ("peeking views")
 * <p/>
 * Structured after https://gist.github.com/devunwired/8cbe094bb7a783e37ad1
 *
 * @author ben.temple@gmail.com (Benjamin Temple)
 */
public class PagerContainer extends FrameLayout implements CustomViewPager.OnPageChangeListener {

    private CustomViewPager pager;

    private boolean needsRedraw = false;

    public PagerContainer(Context context) {
        super(context);
        setupView();
    }

    public PagerContainer(Context context, AttributeSet attrs) {
        super(context, attrs);
        setupView();
    }

    public PagerContainer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setupView();
    }

    private void setupView() {
        /**
         * Necessary to show views outside of the viewpager's frame.
         */
        setClipChildren(false);

        /**
         * Child clipping doesn't work with hardware acceleration in Android 3.x/4.x You need to
         * set this value here if using hardware acceleration in an application targeted at these
         * releases.
         */
        if (Build.VERSION.SDK_INT < 19) {
            setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
    }

    /**
     * Make sure that the child of this container is a viewpager if not, throw an exception.
     */
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        try {
            pager = (CustomViewPager) getChildAt(0);
            pager.addOnPageChangeListener(this);
        } catch (Exception e) {
            throw new IllegalStateException("The root child of PagerContainer must be a ViewPager");
        }
    }

    public CustomViewPager getViewPager() {
        return pager;
    }

    private Point center = new Point();
    private Point initialTouch = new Point();

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        center.x = w / 2;
        center.y = h / 2;
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        /**
         * Capture any touches outside of the viewpager but within our container.
         */
        if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
            initialTouch.x = (int) motionEvent.getX();
            initialTouch.y = (int) motionEvent.getY();
        }
        motionEvent.offsetLocation(pager.getWidth() / 2 - center.x, 0);

        return pager.dispatchTouchEvent(motionEvent);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        /**
         * Force the container to redraw when scrolling.
         * Without this, the outer pages render initially but then stay static.
         */
        if (needsRedraw)
            invalidate();
    }

    @Override
    public void onPageSelected(int position) {
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        needsRedraw = (state != ViewPager.SCROLL_STATE_IDLE);
    }
}
