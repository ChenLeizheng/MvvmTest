package com.landleaf.normal.adapter;


import android.content.Context;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;


import com.landleaf.normal.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * 房间适配器
 */
public class SmartRoomAdapter extends ArrayAdapter<String> {

    private List<String> mItems;

    private SparseBooleanArray selectedItems = new SparseBooleanArray();//控件是否被点击,默认为false，如果被点击，改变值，控件根据值改变自身颜色

    private OnItemClickListener mOnItemClickListener = null;

    private LayoutInflater mInflater;

    public SmartRoomAdapter(Context context, List<String> mItems) {
        super(context, R.layout.item_room, mItems);
        this.mItems = mItems;
        initSelection();
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    private void toggleSelection(int pos) {
        for (int i = 0; i < mItems.size(); i++) {
            if (selectedItems.get(i)) {
                selectedItems.delete(i);
                selectedItems.put(i, false);
                if (pos != i)
                    notifyDataSetChanged();
            }
        }
        selectedItems.delete(pos);
        selectedItems.put(pos, true);
        notifyDataSetChanged();
    }

    public void initSelection() {
        selectedItems.put(0, true);
        for (int i = 1; i < mItems.size(); i++)
            selectedItems.put(i, false);
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView,  ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            // Inflate the view since it does not exist
            convertView = mInflater.inflate(R.layout.item_room, parent, false);

            // Create and save off the holder in the tag so we get quick access to inner fields
            // This must be done for performance reasons
            holder = new ViewHolder(convertView);
            holder.tvDnakeRoom = (TextView) convertView.findViewById(R.id.tvDnakeRoom);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        String roomName = mItems.get(position);
        if (mOnItemClickListener != null){
            holder.tvDnakeRoom.setOnClickListener(view -> {
                toggleSelection(position);
                mOnItemClickListener.onItemClick(holder.itemView, holder.tvDnakeRoom, position);
            });
        }

        //设置房间名
        holder.tvDnakeRoom.setText(roomName);
        //设置是否被选中
        holder.tvDnakeRoom.setBackgroundResource(selectedItems.get(position)?R.drawable.icon_21_room_selected:R.drawable.icon_21_room);
        return convertView;
    }

    public void setOnItemClickListener(OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, View textView, int position);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tvDnakeRoom)
        TextView tvDnakeRoom;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}