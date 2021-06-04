package com.landleaf.everyday.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.landleaf.everyday.R;

import java.util.List;

public class PageAdapter extends RecyclerView.Adapter<PageAdapter.ViewHolder> {

    Context mContext;
    List<String> dataList;

    public PageAdapter(Context mContext, List<String> dataList) {
        this.mContext = mContext;
        this.dataList = dataList;
    }

    public void setDataList(List<String> dataList) {
        this.dataList = dataList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.item_viewpager, parent, false);
        ViewHolder viewHolder = new ViewHolder(itemView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Log.d("PageAdapter", "position:" + position);
        boolean show1 = dataList.size() > position * 4 + 1;
        boolean show2 = dataList.size() > position * 4 + 2;
        boolean show3 = dataList.size() > position * 4 + 3;
            holder.ivIcon2.setVisibility(show1 ? View.VISIBLE : View.INVISIBLE);
            holder.tvName2.setVisibility(show1 ? View.VISIBLE : View.INVISIBLE);

            holder.ivIcon3.setVisibility(show2 ? View.VISIBLE : View.INVISIBLE);
            holder.tvName3.setVisibility(show2 ? View.VISIBLE : View.INVISIBLE);


            holder.ivIcon4.setVisibility(show3 ? View.VISIBLE : View.INVISIBLE);
            holder.tvName4.setVisibility(show3 ? View.VISIBLE : View.INVISIBLE);

    }

    @Override
    public int getItemCount() {
        return dataList == null ? 0:dataList.size()/4+1;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        View itemView;
        ImageView ivIcon1;
        TextView tvName1;
        ImageView ivIcon2;
        TextView tvName2;
        ImageView ivIcon3;
        TextView tvName3;
        ImageView ivIcon4;
        TextView tvName4;


        public ViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            this.ivIcon1 = itemView.findViewById(R.id.ivIcon1);
            this.tvName1 = itemView.findViewById(R.id.tvName1);
            this.ivIcon2 = itemView.findViewById(R.id.ivIcon2);
            this.tvName2 = itemView.findViewById(R.id.tvName2);
            this.ivIcon3 = itemView.findViewById(R.id.ivIcon3);
            this.tvName3 = itemView.findViewById(R.id.tvName3);
            this.ivIcon4 = itemView.findViewById(R.id.ivIcon4);
            this.tvName4 = itemView.findViewById(R.id.tvName4);
        }
    }
}
