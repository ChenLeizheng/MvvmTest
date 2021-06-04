package com.landleaf.everyday;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.landleaf.everyday.adapter.PageAdapter;
import com.landleaf.everyday.adapter.PageAdapter2;
import com.landleaf.everyday.adapter.SceneAdapter;
import com.landleaf.everyday.wheelview.DateItem;
import com.landleaf.everyday.wheelview.WheelItemView;
import com.landleaf.everyday.wheelview.WheelView;
import com.landleaf.everyday.widget.BannerIndicator;
import com.landleaf.everyday.widget.ModeView;
import com.landleaf.everyday.widget.SelectLineTextView;
import com.landleaf.everyday.widget.TimeView;
import com.landleaf.everyday.widget.WindSpeedView;

import java.util.ArrayList;
import java.util.List;

import static androidx.recyclerview.widget.RecyclerView.SCROLL_STATE_IDLE;

public class ViewActivity extends Activity {

    private BannerIndicator bannerIndicator;
    int currentPageIndex = 0;
    private SceneAdapter sceneAdapter;
    private TimeView timeView;
    private ModeView modeView;
    boolean select = false;
    private SelectLineTextView lv1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);
        bannerIndicator = findViewById(R.id.BannerIndicator);
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        ViewPager2 viewPager = findViewById(R.id.viewPager);
        Button btSend = findViewById(R.id.btSend);
        WindSpeedView windSpeedView = findViewById(R.id.windSpeedView);
        windSpeedView.setWindLevel(5);
        windSpeedView.setOnWindChangeListener(new WindSpeedView.OnWindChangeListener() {
            @Override
            public void windChange(int currentWind) {
                Log.d("ViewActivity", "currentWind:" + currentWind);
            }
        });
        timeView = findViewById(R.id.timeView);
        modeView = findViewById(R.id.modeView);
        modeView.setSelect(false);
//        modeView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                modeView.setSelect(true);
//            }
//        });
//        btSend.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                windSpeedView.setProgress(3);
//                modeView.setSelect(select);
//                select = !select;
//            }
//        });
        lv1 = findViewById(R.id.lv1);
        SelectLineTextView lv2 = findViewById(R.id.lv2);
        SelectLineTextView lv3 = findViewById(R.id.lv3);
        final SelectLineTextView[] lvArray = {lv1,lv2,lv3};
        for (int i = 0; i < lvArray.length; i++) {
            int finalI = i;
            lvArray[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    for (SelectLineTextView selectLineTextView : lvArray) {
                        selectLineTextView.setSelect(v==selectLineTextView);
                    }
                }
            });
        }


        List<String> list = new ArrayList<>();
        list.add("在家");
        list.add("离家");
        list.add("睡眠");
        list.add("度假1");
        list.add("度假2");
        list.add("度假3");
        list.add("度假4");
        list.add("度假5");
        list.add("度假6");
        bannerIndicator.setNumber(list.size() / 4 + 1);
        recyclerView.setLayoutManager(new GridLayoutManager(this,4));
//        recyclerView.setAdapter(new PageAdapter2(this,list));

        viewPager.setAdapter(new PageAdapter(this,list));
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                bannerIndicator.setPosition(position);
            }
        });
//        initRecyclerView(recyclerView, list);

        WheelItemView wheel_view_left = findViewById(R.id.wheel_view_left);
        WheelItemView wheel_view_right = findViewById(R.id.wheel_view_right);
        wheel_view_left.setItems(updateItems(DateItem.TYPE_HOUR,0,480));
        wheel_view_left.setSelectedIndex(240,false);

        wheel_view_left.setOnSelectedListener(new WheelView.OnSelectedListener() {
            @Override
            public void onSelected(Context context, int selectedIndex) {
                Log.d("ViewActivity", "selectedIndex:" + selectedIndex);
            }
        });
        wheel_view_right.setItems(updateItems(DateItem.TYPE_MINUTE,0,59));
    }

    private DateItem[] updateItems(@DateItem.DateType int type, int startValue, int endValue) {
        int index = -1;
        DateItem[] items = new DateItem[endValue - startValue + 1];
        for (int i = startValue; i <= endValue; i++) {
            index++;
            int value = (type == DateItem.TYPE_HOUR)? i%24:i%60;
            items[index] = new DateItem(type, value);
        }
        return items;
    }

    public void onViewClick(View view){
        switch (view.getId()){
            case R.id.modeView:
                Toast.makeText(this, "modeView", Toast.LENGTH_SHORT).show();
                break;
            case R.id.btSend:
                Toast.makeText(this, "btSend", Toast.LENGTH_SHORT).show();
                break;

        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        timeView.start();
    }

    @Override
    protected void onStop() {
        super.onStop();
        timeView.stop();
    }

    private void initRecyclerView(RecyclerView recyclerView, final List<String> list) {
        sceneAdapter = new SceneAdapter(this, list.subList(currentPageIndex, currentPageIndex + 5));
        Log.d("ViewActivity", "list.size():" + list.size());
        LinearLayoutManager layoutManger = new LinearLayoutManager(this);
        layoutManger.setOrientation(RecyclerView.HORIZONTAL);
        recyclerView.setLayoutManager(layoutManger);
        recyclerView.setAdapter(sceneAdapter);
//        PagerSnapHelper pagerSnapHelper = new PagerSnapHelper(){
//            @Nullable
//            @Override
//            public View findSnapView(RecyclerView.LayoutManager layoutManager) {
//                return super.findSnapView(layoutManager);
//            }
//
//            @Override
//            public int findTargetSnapPosition(RecyclerView.LayoutManager layoutManager, int velocityX, int velocityY) {
//                return super.findTargetSnapPosition(layoutManager, velocityX, velocityY);
//            }
//        };
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            private boolean scrollLeft;
            boolean mScrolled = false;

            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (newState == SCROLL_STATE_IDLE && mScrolled) {
                    mScrolled = false;
                    LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                    int firstItem = layoutManager.findFirstVisibleItemPosition();
                    int lastItem = layoutManager.findLastVisibleItemPosition();
                    int pageIndex = firstItem / 4;
                    if (scrollLeft) {
                        pageIndex++;
                    } else {
                        pageIndex--;
                    }
                    if (pageIndex < 0) {
                        pageIndex = 0;
                    }
                    if (pageIndex > list.size() / 4 + 1) {
                        pageIndex = list.size() / 4 + 1;
                    }
                    bannerIndicator.setPosition(pageIndex);
                    Log.d("ViewActivity", "onScrollStateChanged firstItem:" + firstItem);
                    smoothMoveToPosition(recyclerView, pageIndex * 4, firstItem, lastItem);
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                scrollLeft = (dx < 0);
                if (dx != 0 || dy != 0) {
                    mScrolled = true;
                }
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                int position = layoutManager.findFirstVisibleItemPosition();
                Log.d("ViewActivity", "onScrolled position:" + position + scrollLeft);
            }
        });
    }


    private void smoothMoveToPosition(RecyclerView mRecyclerView, int position, int firstItem, int lastItem) {
        Log.d("ViewActivity", "smoothMoveToPosition" + position + ",firstItem:" + firstItem + "," + lastItem);
        if (position < firstItem || position > lastItem) {
            mRecyclerView.smoothScrollToPosition(position);
            return;
        }
        int dx = mRecyclerView.getChildAt(position).getLeft();
        mRecyclerView.smoothScrollBy(dx, 0);

//        if (position < firstItem) {
//            // 如果跳转位置在第一个可见位置之前，就smoothScrollToPosition可以直接跳转
//            mRecyclerView.smoothScrollToPosition(position);
//        } else if (position <= lastItem) {
//            // 跳转位置在第一个可见项之后，最后一个可见项之前
//            // smoothScrollToPosition根本不会动，此时调用smoothScrollBy来滑动到指定位置
//            int movePosition = position - firstItem;
//            if (movePosition >= 0 && movePosition < mRecyclerView.getChildCount()) {
//                int top = mRecyclerView.getChildAt(movePosition).getTop();
//                mRecyclerView.smoothScrollBy(0, top);
//            }
//        }else {
//            // 如果要跳转的位置在最后可见项之后，则先调用smoothScrollToPosition将要跳转的位置滚动到可见位置
//            // 再通过onScrollStateChanged控制再次调用smoothMoveToPosition，执行上一个判断中的方法
//            mRecyclerView.smoothScrollToPosition(position);
//
//        }
    }
}
