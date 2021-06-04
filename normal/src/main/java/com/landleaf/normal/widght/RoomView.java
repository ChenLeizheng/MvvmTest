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
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import com.landleaf.normal.R;
import com.landleaf.normal.adapter.SmartRoomAdapter;

public class RoomView extends LinearLayout {

    private HorizontalListView horizontalListView;

    public RoomView(Context context) {
        super(context);
        initialize();
    }

    public RoomView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }

    public RoomView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize();
    }

    private void initialize() {
        LayoutInflater.from(getContext()).inflate(R.layout.view_rooms, this);
        horizontalListView = (HorizontalListView) findViewById(R.id.room_list_view);
    }

    public void setViewData(SmartRoomAdapter smartRoomAdapter) {
        horizontalListView.setAdapter(smartRoomAdapter);
    }
}