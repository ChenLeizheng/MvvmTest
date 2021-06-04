/*
 * Copyright (c) 2017. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.landleaf.normal.widght.helpers;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;

/**
 * Acts on the View's built in translation directly instead of the transformation given by the
 * Animation. This helps with some of the succeeding animations in the DynamicViewPager and can
 * be applied elsewhere since it is generic.
 *
 * @author jacobamuchow@gmail.com (Jacob Muchow)
 */
public class RealTranslateAnimation extends Animation {
    private static final String TAG = RealTranslateAnimation.class.getSimpleName();

    private View view;
    private float fromXDelta, toXDelta = -1.0f;
    private float fromYDelta, toYDelta = -1.0f;

    private float initialTransX = -1.0f;
    private float initialTransY = -1.0f;

    public RealTranslateAnimation(View view, float fromXDelta, float toXDelta, float fromYDelta, float toYDelta) {
        this.view = view;
        this.fromXDelta = fromXDelta;
        this.toXDelta = toXDelta;
        this.fromYDelta = fromYDelta;
        this.toYDelta = toYDelta;
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        super.applyTransformation(interpolatedTime, t);

        if(initialTransX == -1.0f) {
            initialTransX = view.getTranslationX();
            initialTransY = view.getTranslationY();
        }

        float offsetX = (toXDelta - fromXDelta) * interpolatedTime;
        float offsetY = (toYDelta - fromYDelta) * interpolatedTime;

        float transX = initialTransX + offsetX;
        float transY = initialTransY + offsetY;

        view.setTranslationX(transX);
        view.setTranslationY(transY);
    }
}
