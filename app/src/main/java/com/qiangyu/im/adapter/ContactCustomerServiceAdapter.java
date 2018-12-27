package com.qiangyu.im.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.qiangyu.R;
import com.qiangyu.im.enity.KFList;
import com.qiangyu.utils.Constants;

import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class ContactCustomerServiceAdapter extends BaseAdapter {

    private Context context;
    private List<KFList> datas;

    public ContactCustomerServiceAdapter(Context context, List<KFList> ctype) {
        this.context = context;
        this.datas = ctype;
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
            convertView = View.inflate(context, R.layout.im_view_more_selling, null);
            viewHolder = new ViewHolder();
            viewHolder.iv_more_selling = convertView.findViewById(R.id.iv_more_selling);
            viewHolder.tv_name = convertView.findViewById(R.id.tv_more_selling_name);
            viewHolder.tv_price = convertView.findViewById(R.id.tv_more_selling_price);
            viewHolder.tv_sold = convertView.findViewById(R.id.tv_more_selling_sold);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        KFList kfList = datas.get(i);
        if (StringUtils.isNotEmpty(kfList.getHeadPic())) {
            Glide.with(context).load(Constants.QIANGYU_PIC_URL + kfList.getHeadPic()).diskCacheStrategy(DiskCacheStrategy.ALL).centerCrop().into(viewHolder.iv_more_selling);
        }
        viewHolder.tv_name.setText(kfList.getDisplayName());
        viewHolder.tv_sold.setText("");
        viewHolder.tv_price.setText("");
        return convertView;
    }

    static class ViewHolder {
        ImageView iv_more_selling;
        TextView tv_name;
        TextView tv_price;
        TextView tv_sold;
    }

}
