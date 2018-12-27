package com.qiangyu.my.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.qiangyu.R;
import com.qiangyu.my.bean.Details;
import com.qiangyu.my.bean.OrderList;
import com.qiangyu.utils.Constants;

public class SingleOrderAdapter extends BaseAdapter {

    private Context mContext;
    private final OrderList data;

  /*  public SingleOrderAdapter(Context context, List<OrderList> orderListList) {
        this.mContext = context;
        this.data = orderListList;
    }*/

    public SingleOrderAdapter(Context context, OrderList orderList) {
        this.mContext = context;
        this.data = orderList;
    }

    @Override
    public int getCount() {
        return data.getDetails().size();
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
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.single_order_activity, null);
            viewHolder = new ViewHolder();
            viewHolder.iv_commodity_picture = convertView.findViewById(R.id.iv_commodity_picture);
            viewHolder.tv_commodity_name = convertView.findViewById(R.id.tv_commodity_name);
            viewHolder.tv_commodity_parameters = convertView.findViewById(R.id.tv_commodity_parameters);
            viewHolder.tv_number = convertView.findViewById(R.id.tv_number);
            viewHolder.tv_commodity_price = convertView.findViewById(R.id.tv_commodity_price);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        Details details = data.getDetails().get(i);
        Glide.with(mContext).load(Constants.QIANGYU_PIC_URL + details.getPicture()).diskCacheStrategy(DiskCacheStrategy.ALL).centerCrop().into(viewHolder.iv_commodity_picture);
        viewHolder.tv_commodity_name.setText(details.getGoodsName());
        viewHolder.tv_commodity_parameters.setText(details.getSpecValues_Desc());
        viewHolder.tv_number.setText("X " + details.getNum());
        viewHolder.tv_commodity_price.setText("¥ " + details.getPrice());
        return convertView;
    }

    static class ViewHolder {
        private ImageView iv_commodity_picture;//商品图片
        private TextView tv_commodity_name;//商品名称
        private TextView tv_commodity_parameters;//商品规格参数
        private TextView tv_number;//商品数量
        private TextView tv_commodity_price;//商品价钱
    }
}
