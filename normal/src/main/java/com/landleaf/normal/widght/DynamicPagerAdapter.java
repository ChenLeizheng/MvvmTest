/*
 * Copyright (c) 2017. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.landleaf.normal.widght;

import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;

import androidx.annotation.Nullable;

import com.landleaf.normal.widght.helpers.RealTranslateAnimation;
import com.landleaf.normal.widght.helpers.SimpleAnimationListener;

import java.util.Map;
import java.util.WeakHashMap;

/**
 * A PagerAdapter for ViewPager that keeps a cache of ViewHolders. The data set can be updated
 * dynamically and items can be removed with animations if desired. Use discardView()
 * to start a dismissal animation. collapseViewsIn() is also exposed if you wish to use gestures
 * to delete items (ex. fling a View off the screen, then call collapseViewsIn(view) to start the
 * collapsing animation - this is done in DynamicViewPager).
 *
 * @author jacobamuchow@gmail.com (Jacob Muchow)
 */
abstract public class DynamicPagerAdapter<VH extends DynamicPagerAdapter.ViewHolder> extends PagerAdapter {
    private static final String TAG = DynamicPagerAdapter.class.getSimpleName();

    /**
     * Used to denote an invalid position for a View
     */
    public static final int NO_POSITION = -1;

    /**
     * To use multiple view types, override {@link #getViewType(int)}
     */
    public static final int DEFAULT_VIEW_TYPE = -1;

    public static abstract class ViewHolder {
        public final View view;
        public int viewType = DEFAULT_VIEW_TYPE;

        public ViewHolder(View view) {
            this.view = view;
        }
    }

    private WeakHashMap<Integer, VH> viewHolderCache = new WeakHashMap<>();

    private boolean isViewAnimating = false;

    @Nullable private Callbacks callbacks;

    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        VH viewHolder = null;

        int viewType = getViewType(position);

        if(viewHolderCache.containsKey(position)) {
            viewHolder = viewHolderCache.get(position);

            if(viewHolder.viewType != viewType) {
                destroyItem(container, position, viewHolder.view);
                viewHolder = null;
            }
        }

        if (viewHolder == null) {
            viewHolder = onCreateViewHolder(container, position, viewType);
            viewHolder.viewType = viewType;
            viewHolderCache.put(position, viewHolder);
        }

        onBindViewHolder(viewHolder, position);

        container.addView(viewHolder.view);
        return viewHolder.view;
    }

    public abstract VH onCreateViewHolder(ViewGroup container, int position, int viewType);

    public abstract void onBindViewHolder(VH viewHolder, int position);

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    /**
     * Removes a View from its container and the cache
     */
    @Override
    public void destroyItem(ViewGroup container, int position, Object view) {
        viewHolderCache.remove(position);
        container.removeView((View) view);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    public int getViewType(int position) {
        return DEFAULT_VIEW_TYPE;
    }

    /**
     * A non-destructive way to "notify data set changed". By default, when {@link #notifyDataSetChanged()}
     * is called, the ViewPager will destroy all the Views and recreate them if {@link #getItemPosition(Object)}
     * returns POSITION_NONE. This may not be desirable behavior in some cases. This method will call
     * {@link #onBindViewHolder(ViewHolder, int)} on each of the cached ViewHolders in order to
     * update them without destroying the Views.
     */
    public void updateViews() {
        for (Map.Entry<Integer, VH> entry : viewHolderCache.entrySet()) {
            onBindViewHolder(entry.getValue(), entry.getKey());
        }
    }

    /**
     * Returns a View for the position provided if cached. Take a look at
     * {@link #getViewHolderAt(int)} or more details.
     */
    @Nullable
    public View getViewAt(int position) {
        VH viewHolder = getViewHolderAt(position);
        return viewHolder == null ? null : viewHolder.view;
    }

    /**
     * Returns a ViewHolder for the position provided if cached.
     *
     * A limited numbers of Views are cached (default is 5), so this is not too reliable except for
     * getting the current View or surrounding Views in the ViewPager.
     */
    @Nullable
    public VH getViewHolderAt(int position) {
        return viewHolderCache.get(position);
    }

    /**
     * Returns a position in the ViewPager for the View provided if cached.
     *
     * A limited numbers of Views are cached (default is 5), so this is not too reliable except for
     * getting the current View or surrounding Views in the ViewPager.
     */
    public int getPositionForViewHolder(VH viewHolder) {
        return getPositionForView(viewHolder.view);
    }

    public int getPositionForView(@Nullable View view) {
        if(view == null) {
            return NO_POSITION;
        }

        for(Map.Entry<Integer, VH> entry : viewHolderCache.entrySet()) {
            if(entry.getValue().view.equals(view)) {
                return entry.getKey();

            }
        }

        return NO_POSITION;
    }

    /**
     * @return the cache of ViewHolders
     */
    protected WeakHashMap<Integer, VH> getViewHolderCache() {
        return viewHolderCache;
    }

    /**
     * You may want to use this to stop gesture detection or other UI elements during animation.
     *
     * @return true if a child is in the middle of animating.
     */
    public boolean isViewAnimating() {
        return isViewAnimating;
    }

    /**
     * This is only used as a state holder. The DynamicPagerAdapter does not change behavior
     * due to this variable.
     */
    public void setViewAnimating(boolean viewAnimating) {
        isViewAnimating = viewAnimating;
    }

    /**
     * This will fade out the View passed in, then call collapseViewsIn() to finish the animation
     * (resembles a deletion).
     *
     * @param position The position of the View to hide and collapse around. You will usually want
     *                 to use the current item of the ViewPager.
     *
     * @return True if a discard animation was started for the View.
     */
    public boolean discardViewAt(int position) {
        return discardView(getViewAt(position));
    }

    /**
     * This will fade out the View passed in, then call collapseViewsIn() to finish the animation
     * (resembles a deletion).
     *
     * @param view The view to hide and collapse around. You will usually want to use the current
     * item of the ViewPager.
     *
     * @return True if a discard animation was started for the View.
     */
    public boolean discardView(@Nullable final View view) {
        if (view == null) {
            return false;
        }

        isViewAnimating = startDiscardAnimation(view, new SimpleAnimationListener() {
            @Override
            public void onAnimationEnd(Animation animation) {
                collapseViewsIn(view);
            }
        });

        return isViewAnimating;
    }

    /**
     * This method should be used to create a discard animation and start it on the View provided.
     * You can override it to use your own animation if you desire. Remember to 1) set the animation
     * listener so the adapter will be updated and 2) start the animation.
     *
     * @return True if the a discard animation was started for the View.
     */
    protected boolean startDiscardAnimation(@Nullable View view, Animation.AnimationListener animationListener) {
        if (view == null) {
            return false;
        }

        RealTranslateAnimation translateAnimation = new RealTranslateAnimation(view, 0, 0, 0, -view.getHeight());
        translateAnimation.setDuration(400);
        translateAnimation.setFillAfter(true);
        translateAnimation.setInterpolator(new AccelerateInterpolator());
        translateAnimation.setAnimationListener(animationListener);

        view.startAnimation(translateAnimation);
        return true;
    }

    /**
     * This will look for views to the right, then the left, to animate to the position of the View
     * passed in. This is used mainly for discardView(), but it is being exposed for gesture dismissals.
     *
     * @param view The view to collapse around. You will usually want to use the current item
     * of the ViewPager.
     *
     * @return True if a collapse animation was started for the View.
     */
    public boolean collapseViewsIn(@Nullable final View view) {
        if (view == null) {
            return false;
        }

        int position = getPositionForView(view);

        /**
         * Stop if a position can't be found
         */
        if(position == NO_POSITION) {
            if(callbacks != null) {
                isViewAnimating = false;
                callbacks.onDiscardFinished(position, view);
            }
            return false;
        }

        /**
         * Get next view (check right first, then left)
         */
        View nextView = getViewAt(position+1);
        View farNextView = getViewAt(position+2);

        if(nextView == null) {
            nextView = getViewAt(position-1);
            farNextView = getViewAt(position-2);
        }

        /**
         * If it is still null, just discard the current View.
         */
        if(nextView == null) {
            if(callbacks != null) {
                isViewAnimating = false;
                callbacks.onDiscardFinished(position, view);
            }
            return false;
        }

        /**
         * Start collapsing animations
         */
        final int pos = position;

        startNextViewAnimation(position, view, nextView, new SimpleAnimationListener() {
            @Override
            public void onAnimationEnd(Animation animation) {

                /**
                 * Notify the callback on the next main loop (prevents screen flash)
                 */
                if(callbacks != null) {
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            isViewAnimating = false;
                            callbacks.onDiscardFinished(pos, view);
                        }
                    });
                }
            }
        });

        if(farNextView != null) {
            startFarNextViewAnimation(position, view, nextView, farNextView);
        }

        isViewAnimating = true;
        return true;
    }

    /**
     * This method should be used to start the animation for the next View to move in after the
     * current view has been discarded. You can override it in order to create your own animation if you desire.
     * Remember to 1) set the animation listener so the adapter will be updated and 2) start the animation.
     */
    protected void startNextViewAnimation(final int currentPos, View currentView, View nextView, Animation.AnimationListener animationListener) {

        float toXDelta = currentView.getX() - nextView.getX();

        TranslateAnimation nextViewAnimation = new TranslateAnimation(0, toXDelta, 0, 0);
        nextViewAnimation.setDuration(400);
        nextViewAnimation.setFillAfter(true);
        nextViewAnimation.setAnimationListener(animationListener);

        nextView.startAnimation(nextViewAnimation);
    }

    /**
     * This method should be used to start the animation for the far next View to move in after the
     * current view has been discarded. You can override it in order to create your own animation if you desire.
     * Remember to start the animation.
     */
    protected void startFarNextViewAnimation(final int currentPos, View currentView, View nextView, View farNextView) {

        //Same translation distance as nextView
        float toXDelta = currentView.getX() - nextView.getX();

        TranslateAnimation farNextViewAnimation = new TranslateAnimation(0, toXDelta, 0, 0);
        farNextViewAnimation.setDuration(400);
        farNextViewAnimation.setFillAfter(true);

        farNextView.startAnimation(farNextViewAnimation);
    }

    public interface Callbacks {
        void onDiscardFinished(int position, View view);
    }

    public void setCallbacks(@Nullable Callbacks callbacks) {
        this.callbacks = callbacks;
    }
}
