package com.qiangyu.my.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.qiangyu.R;
import com.qiangyu.my.activity.CommodityEvaluationActivity;
import com.qiangyu.my.bean.Details;
import com.qiangyu.my.bean.OrderList;
import com.qiangyu.utils.Constants;

public class CommodityAdapter extends BaseAdapter {

    private String TAG = "CommodityAdapter";
    private Context mContext;
    private final OrderList data;

    public CommodityAdapter(Context context, OrderList orderList) {
        this.mContext = context;
        this.data = orderList;
    }

    @Override
    public int getCount() {
        return data.getDetails().size();
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.activity_commodity, null);
            viewHolder = new ViewHolder();
            viewHolder.iv_commodity_picture = convertView.findViewById(R.id.iv_commodity_picture);
            viewHolder.tv_commodity_name = convertView.findViewById(R.id.tv_commodity_name);
            viewHolder.tv_commodity_parameters = convertView.findViewById(R.id.tv_commodity_parameters);
            viewHolder.tv_number = convertView.findViewById(R.id.tv_number);
            viewHolder.tv_commodity_price = convertView.findViewById(R.id.tv_commodity_price);
            viewHolder.btn_pingjia = convertView.findViewById(R.id.btn_pingjia);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        Details details = data.getDetails().get(position);

        Glide.with(mContext).load(Constants.QIANGYU_PIC_URL + details.getPicture()).diskCacheStrategy(DiskCacheStrategy.ALL).centerCrop().into(viewHolder.iv_commodity_picture);
        viewHolder.tv_commodity_name.setText(details.getGoodsName());
        viewHolder.tv_commodity_parameters.setText(details.getSpecValues_Desc());
        viewHolder.tv_number.setText("X " + details.getNum());
        viewHolder.tv_commodity_price.setText("¥ " + details.getPrice());

        if (!details.getIsEvaluated()) {
            viewHolder.btn_pingjia.setVisibility(View.VISIBLE);
        }

        viewHolder.btn_pingjia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Details detail = data.getDetails().get(position);
                Log.d(TAG, "onClick: " + detail.getSpecValues_Desc());
                Intent intent = new Intent(mContext, CommodityEvaluationActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("Details", detail);
                intent.putExtras(bundle);
                mContext.startActivity(intent);
            }
        });
        return convertView;
    }

    static class ViewHolder {
        private ImageView iv_commodity_picture;//图片
        private TextView tv_commodity_name;//商品名
        private TextView tv_commodity_parameters;//规格参数
        private TextView tv_number;//数量
        private TextView tv_commodity_price;//单价
        private Button btn_pingjia;//评价
    }
}
