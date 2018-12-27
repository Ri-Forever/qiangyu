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
import com.qiangyu.home.activity.DemandReleaseActivity;
import com.qiangyu.home.activity.GoodsListActivity;
import com.qiangyu.home.bean.Category;
import com.qiangyu.home.bean.JsonGoodsListBeanData;
import com.qiangyu.utils.Constants;

import java.util.List;

/**
 * 频道适配器
 */

public class ChannelAdapter extends BaseAdapter {
    private Context context;
    private List<Category> data;
    private Category mCategory;
    private JsonGoodsListBeanData jsonGoodsListBeanData;
    private JsonGoodsListBeanData.Result mResult;

    public ChannelAdapter(Context context, List<Category> category) {
        this.context = context;
        this.data = category;
    }

    @Override
    public int getCount() {
        return data.size() + 1;
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
        //mCategory = data.get(i);

        //根据位置得到对应的数据
        if (i == 0) {
            Glide.with(context).load(R.mipmap.dingzhi).diskCacheStrategy(DiskCacheStrategy.ALL).centerCrop().into(viewHolder.iv_icon);
            viewHolder.tv_title.setText("我要定制");
        } else {
            mCategory = data.get(i - 1);
            Glide.with(context).load(Constants.QIANGYU_PIC_URL + mCategory.getIcon()).diskCacheStrategy(DiskCacheStrategy.ALL).centerCrop().into(viewHolder.iv_icon);
            viewHolder.tv_title.setText(mCategory.getName());
            viewHolder.tv_id.setText(mCategory.getId() + "");
        }

        viewHolder.iv_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("onClick", "id: " + viewHolder.tv_id.getText().toString() + " --- name: " + viewHolder.tv_title.getText().toString());
                String cid = viewHolder.tv_id.getText().toString();
                if (viewHolder.tv_id.getText().toString().isEmpty()) {
                    Intent intent = new Intent(context, DemandReleaseActivity.class);
                    context.startActivity(intent);
                } else {
                    Intent intent = new Intent(context, GoodsListActivity.class);
                    intent.putExtra("cid", cid);
                    context.startActivity(intent);
                }
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
