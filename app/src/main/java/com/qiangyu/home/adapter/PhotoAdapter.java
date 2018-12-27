package com.qiangyu.home.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.qiangyu.R;

import java.util.List;

public class PhotoAdapter extends BaseAdapter {
    private final List<String> data;
    private Context context;

    public PhotoAdapter(Context context, List<String> paths) {
        this.context = context;
        this.data = paths;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(final int i, View convertView, ViewGroup viewGroup) {
        final ViewHolder viewHolder;
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.activity_photo, null);
            viewHolder = new ViewHolder();
            viewHolder.iv_icon = convertView.findViewById(R.id.iv_channel);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        String path = data.get(i);

        Glide.with(context).load(path).diskCacheStrategy(DiskCacheStrategy.ALL).centerCrop().into(viewHolder.iv_icon);

        return convertView;
    }

    static class ViewHolder {
        ImageView iv_icon;
    }
}
