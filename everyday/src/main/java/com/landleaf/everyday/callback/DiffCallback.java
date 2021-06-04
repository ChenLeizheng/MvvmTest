package com.landleaf.everyday.callback;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;

import com.landleaf.everyday.bean.TestBean;

import java.util.List;

/**
 * Author：Lei on 2020/12/7
 */
public class DiffCallback extends DiffUtil.Callback {

    private List<TestBean> oldList, newList;

    public DiffCallback(List<TestBean> oldList, List<TestBean> newList) {
        this.oldList = oldList;
        this.newList = newList;
    }

    @Override
    public int getOldListSize() {
        return oldList == null ? 0 : oldList.size();
    }

    @Override
    public int getNewListSize() {
        return newList == null ? 0 : newList.size();
    }


    /**
     * Called by the DiffUtil to decide whether two object represent the same Item.
     * 被 DiffUtil 调用，用来判断两个对象是否是相同的 Item。
     * For example, if your items have unique ids, this method should check their id equality.
     * 例如，如果你的Item有唯一的id字段，这个方法就判断id是否相等。
     *
     * @param oldItemPosition The position of the item in the old list
     * @param newItemPosition The position of the item in the new list
     * @return True if the two items represent the same object or false if they are different.
     */
    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        int oldId = oldList.get(oldItemPosition).getId();
        int newId = newList.get(newItemPosition).getId();
        return oldId == newId;
    }

    /**
     * Called by the DiffUtil when it wants to check whether two items have the same data.
     * 被 DiffUtil 调用，用来检查两个 item 是否含有相同的数据
     * DiffUtil uses this information to detect if the contents of an item has changed.
     * DiffUtil 用返回的信息（true false）来检测当前 item 的内容是否发生了变化
     * DiffUtil uses this method to check equality instead of {@link Object#equals(Object)}
     * DiffUtil 用这个方法替代 equals 方法去检查是否相等。
     * so that you can change its behavior depending on your UI.
     * 所以你可以根据你的 UI 去改变它的返回值
     * For example, if you are using DiffUtil with a
     * {RecyclerView.Adapter}, you should
     * return whether the items' visual representations are the same.
     * 例如，如果你用 RecyclerView.Adapter 配合 DiffUtil 使用，你需要返回 Item 的视觉表现是否相同。
     * This method is called only if {@link #areItemsTheSame(int, int)} returns
     * {@code true} for these items.
     * 这个方法仅仅在 areItemsTheSame() 返回 true 时，才会被调用。
     *
     * @param oldItemPosition The position of the item in the old list
     * @param newItemPosition The position of the item in the new list which replaces the
     *                        oldItem
     * @return True if the contents of the items are the same or false if they are different.
     */
    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        return oldList.get(oldItemPosition).getContent().equals(newList.get(newItemPosition).getContent());
    }

    @Nullable
    @Override
    public Object getChangePayload(int oldItemPosition, int newItemPosition) {
        Log.d("DiffCallback", "oldItemPosition:" + oldItemPosition);
        TestBean oldTestBean = oldList.get(oldItemPosition);
        TestBean newTestBean = newList.get(newItemPosition);

        Bundle diff = new Bundle();
        if (!oldTestBean.getContent().equals(newTestBean.getContent())) {
            diff.putInt("IMG", newTestBean.getId());
        }
        if (diff.size() == 0) {
            return null;
        }
        return diff;
//        return super.getChangePayload(oldItemPosition, newItemPosition);
    }
}
