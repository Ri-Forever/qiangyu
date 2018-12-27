package com.qiangyu.home.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.qiangyu.R;
import com.qiangyu.utils.Constants;

import java.util.List;

public class PictureAdapter extends BaseAdapter {

    private Context mContext;
    private final List<String> datas;

    public PictureAdapter(Context context, List<String> commentPics) {
        this.mContext = context;
        this.datas = commentPics;
    }

    @Override
    public int getCount() {
        return datas.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.activity_picture, null);
            viewHolder = new ViewHolder();
            viewHolder.iv_picture = convertView.findViewById(R.id.iv_picture);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        String pic = datas.get(position);

        Glide.with(mContext).load(Constants.QIANGYU_PIC_URL + pic).diskCacheStrategy(DiskCacheStrategy.ALL).centerCrop().into(viewHolder.iv_picture);

        return convertView;
    }

    static class ViewHolder {
        ImageView iv_picture;
    }
}
