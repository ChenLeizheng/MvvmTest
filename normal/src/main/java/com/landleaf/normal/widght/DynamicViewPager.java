/*
 * Copyright (c) 2017. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.landleaf.normal.widght;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

/**
 * Listens for swipe and drag-and-drop gestures on children to delete items with animations.
 * Adapters *must* inherit from DynamicPagerAdapter for this to work, but DynamicPagerAdapter
 * can be used on its own if the use of gestures is not desired.
 *
 * @author jacobamuchow@gmail.com (Jacob Muchow)
 */
public class DynamicViewPager extends CustomViewPager {

    private static final String TAG = DynamicViewPager.class.getSimpleName();

    private DynamicPagerAdapter dynamicPagerAdapter;

    public DynamicViewPager(Context context) {
        super(context);
        initialize();
    }

    public DynamicViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }

    private void initialize() {

    }

    @Override
    public void setAdapter(PagerAdapter adapter) {
        if (!(adapter instanceof DynamicPagerAdapter))
            throw new IllegalStateException("You must use a DynamicPagerAdapter along with the " +
                    "DynamicViewPager.");
        dynamicPagerAdapter = (DynamicPagerAdapter) adapter;
        super.setAdapter(adapter);
    }

    public DynamicPagerAdapter getDynamicPagerAdapter() {
        return dynamicPagerAdapter;
    }

    @Nullable
    public View getCurrentView() {
        return dynamicPagerAdapter.getViewAt(getCurrentItem());
    }
}