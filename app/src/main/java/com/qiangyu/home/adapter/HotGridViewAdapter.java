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
import com.qiangyu.home.bean.Hot;
import com.qiangyu.utils.Constants;

import java.util.List;

public class HotGridViewAdapter extends BaseAdapter {

    private final Context context;
    private final List<Hot> datas;

    /*public HotGridViewAdapter(Context context, List<ResultBeanData.ResultBean.HotInfoBean> hot_info) {
        this.context = context;
        this.datas = hot_info;
    }*/

    public HotGridViewAdapter(Context context, List<Hot> hot) {
        this.context = context;
        this.datas = hot;
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
            convertView = View.inflate(context, R.layout.item_hot_grid_view, null);
            viewHolder = new ViewHolder();
            viewHolder.iv_hot = convertView.findViewById(R.id.iv_hot);
            viewHolder.tv_name = convertView.findViewById(R.id.tv_name);
            viewHolder.tv_price = convertView.findViewById(R.id.tv_price);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        Hot hot = datas.get(i);
        Glide.with(context).load(Constants.QIANGYU_PIC_URL + hot.getPicture()).diskCacheStrategy(DiskCacheStrategy.ALL).centerCrop().into(viewHolder.iv_hot);
        viewHolder.tv_name.setText(hot.getName());
        viewHolder.tv_price.setText("￥" + hot.getPrice());
        return convertView;
    }

    static class ViewHolder {
        ImageView iv_hot;
        TextView tv_name;
        TextView tv_price;
    }
}
