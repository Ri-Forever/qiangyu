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
import com.qiangyu.home.bean.ResultBeanData;
import com.qiangyu.utils.Constants;

import java.util.List;

public class MoreRecommendAdapter extends BaseAdapter {

    private Context context;
    private List<ResultBeanData.ResultBean.RecommendInfoBean> datas;


    public MoreRecommendAdapter(Context context, List<ResultBeanData.ResultBean.RecommendInfoBean> recommendInfoBean) {
        this.context = context;
        this.datas = recommendInfoBean;
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
            convertView = View.inflate(context, R.layout.view_more_selling, null);
            viewHolder = new ViewHolder();
            viewHolder.iv_more_selling = convertView.findViewById(R.id.iv_more_selling);
            viewHolder.tv_name = convertView.findViewById(R.id.tv_more_selling_name);
            viewHolder.tv_price = convertView.findViewById(R.id.tv_more_selling_price);
            viewHolder.tv_sold = convertView.findViewById(R.id.tv_more_selling_sold);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        ResultBeanData.ResultBean.RecommendInfoBean recommend = datas.get(i);
        Glide.with(context).load(Constants.QIANGYU_PIC_URL + recommend.getFigure()).diskCacheStrategy(DiskCacheStrategy.ALL).centerCrop().into(viewHolder.iv_more_selling);
        viewHolder.tv_name.setText(recommend.getName());
        viewHolder.tv_sold.setText("已售出  50");
        viewHolder.tv_price.setText("￥" + recommend.getCover_price());
        return convertView;
    }

    static class ViewHolder {
        ImageView iv_more_selling;
        TextView tv_name;
        TextView tv_price;
        TextView tv_sold;
    }

}
