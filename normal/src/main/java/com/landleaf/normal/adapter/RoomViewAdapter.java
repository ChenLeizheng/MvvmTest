/*
 * Copyright (c) 2017. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.landleaf.normal.adapter;

import android.view.ViewGroup;


import com.landleaf.normal.widght.DynamicPagerAdapter;
import com.landleaf.normal.widght.RoomView;

import java.util.List;

public class RoomViewAdapter extends DynamicPagerAdapter {

    private List<SmartRoomAdapter> smartRoomAdapters;

    public RoomViewAdapter(List<SmartRoomAdapter> smartRoomAdapters) {
        this.smartRoomAdapters = smartRoomAdapters;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup container, int position, int viewType) {
        final RoomView roomView = new RoomView(container.getContext());

        return new ViewHolder(roomView) {};
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        RoomView roomView = (RoomView) viewHolder.view;
        roomView.setViewData(smartRoomAdapters.get(position));
    }

    @Override
    public int getCount() {
        return smartRoomAdapters.size();
    }
}