package com.landleaf.everyday.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.landleaf.everyday.R;

import java.util.List;

public class SceneAdapter extends RecyclerView.Adapter<SceneAdapter.ViewHolder> {

    Context mContext;
    List<String> dataList;

    public SceneAdapter(Context mContext, List<String> dataList) {
        this.mContext = mContext;
        this.dataList = dataList;
    }

    public void setDataList(List<String> dataList) {
        this.dataList = dataList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.item_scene, parent, false);
        ViewHolder viewHolder = new ViewHolder(itemView);
//        int width = parent.getWidth();
//        ViewGroup.LayoutParams layoutParams = viewHolder.itemView.getLayoutParams();
//        layoutParams.width = width/4;
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.tvName.setText(dataList.get(position));
    }

    @Override
    public int getItemCount() {
        return dataList == null ? 0:dataList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        View itemView;
        ImageView ivIcon;
        TextView tvName;

        public ViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            this.ivIcon = itemView.findViewById(R.id.ivIcon);
            this.tvName = itemView.findViewById(R.id.tvName);
        }
    }
}
