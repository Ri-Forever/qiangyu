package com.qiangyu.home.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.qiangyu.R;
import com.qiangyu.utils.Constants;

import java.util.List;

public class GridRecyclerAdapter extends RecyclerView.Adapter<GridRecyclerAdapter.ViewHolder> {

    private Context mContext;
    private final List<String> datas;
    private OnItemClickListener mListener;

    public GridRecyclerAdapter(Context context, List<String> commentPics, OnItemClickListener listener) {
        this.mContext = context;
        this.datas = commentPics;
        this.mListener = listener;
    }

    @NonNull
    @Override
    public GridRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.activity_grid_pic, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull GridRecyclerAdapter.ViewHolder viewHolder, final int i) {
        String data = datas.get(i);
        Glide.with(mContext).load(Constants.QIANGYU_PIC_URL + data).diskCacheStrategy(DiskCacheStrategy.ALL).centerCrop().into(viewHolder.mImageView);
        viewHolder.mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onClick(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private final ImageView mImageView;

        ViewHolder(@NonNull View itemView) {
            super(itemView);

            mImageView = itemView.findViewById(R.id.iv_image);
        }
    }

    public interface OnItemClickListener {
        void onClick(int i);
    }
}
