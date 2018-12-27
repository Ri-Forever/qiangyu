package com.qiangyu.home.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.qiangyu.R;
import com.qiangyu.home.activity.GoodsListActivity;
import com.qiangyu.home.bean.Category;
import com.qiangyu.utils.Constants;

import java.util.List;

/**
 * 频道适配器
 */

public class ChannelRightAdapter extends BaseAdapter {
    private Context context;
    private List<Category> data;
    private Category mCategory;

    public ChannelRightAdapter(Context context, List<Category> category) {
        this.context = context;
        this.data = category;
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
            convertView = View.inflate(context, R.layout.item_right_category, null);
            viewHolder = new ViewHolder();
            viewHolder.tv_id = convertView.findViewById(R.id.tv_id);
            viewHolder.iv_icon = convertView.findViewById(R.id.iv_channel);
            viewHolder.tv_title = convertView.findViewById(R.id.tv_channel);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        mCategory = data.get(i);

        Glide.with(context).load(Constants.QIANGYU_PIC_URL + mCategory.getIcon()).diskCacheStrategy(DiskCacheStrategy.ALL).centerCrop().into(viewHolder.iv_icon);
        viewHolder.tv_title.setText(mCategory.getName());
        viewHolder.tv_id.setText(mCategory.getId() + "");

        viewHolder.iv_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("onClick", "name: " + viewHolder.tv_title.getText().toString() + " id: " + viewHolder.tv_id.getText().toString());
                Intent intent = new Intent(context, GoodsListActivity.class);
                intent.putExtra("cid", viewHolder.tv_id.getText().toString());
                context.startActivity(intent);
            }
        });

        return convertView;
    }

    static class ViewHolder {
        ImageView iv_icon;
        TextView tv_title;
        TextView tv_id;
    }
}
