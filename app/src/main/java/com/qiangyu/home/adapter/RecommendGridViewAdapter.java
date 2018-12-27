package com.qiangyu.home.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.qiangyu.R;
import com.qiangyu.home.bean.Tj;
import com.qiangyu.utils.Constants;

import java.util.List;

public class RecommendGridViewAdapter extends BaseAdapter {
    private final Context mContext;
    private final List<Tj> datas;

    public RecommendGridViewAdapter(Context context, List<Tj> tj) {
        this.mContext = context;
        this.datas = tj;
    }

    @Override
    public int getCount() {
        return datas.size();
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
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.item_recommend_grid_view, null);
            viewHolder = new ViewHolder();
            viewHolder.iv_recommend = convertView.findViewById(R.id.iv_recommend);
            viewHolder.tv_name = convertView.findViewById(R.id.tv_name);
            viewHolder.tv_price = convertView.findViewById(R.id.tv_price);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        Tj tj = datas.get(i);
        Glide.with(mContext).load(Constants.QIANGYU_PIC_URL + tj.getPicture()).diskCacheStrategy(DiskCacheStrategy.ALL).centerCrop().into(viewHolder.iv_recommend);
        viewHolder.tv_name.setText(tj.getName());
        viewHolder.tv_price.setText("￥" + tj.getPrice());
        return convertView;
    }

    static class ViewHolder {
        ImageView iv_recommend;
        TextView tv_name;
        TextView tv_price;
    }
}
