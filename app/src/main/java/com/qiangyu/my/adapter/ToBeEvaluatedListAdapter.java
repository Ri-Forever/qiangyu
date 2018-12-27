package com.qiangyu.my.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.qiangyu.R;
import com.qiangyu.my.activity.OrderDetailActivity;
import com.qiangyu.my.bean.Details;
import com.qiangyu.my.bean.JsonOrderListData;
import com.qiangyu.my.bean.OrderList;
import com.qiangyu.payment.PayActivity;
import com.qiangyu.utils.MyListView;

import java.util.List;

public class ToBeEvaluatedListAdapter extends BaseAdapter {

    private Context mContext;
    private List<OrderList> data;
    private double mTotalPrice;
    private OrderList mOrderList;
    private ViewHolder mViewHolder;

    public ToBeEvaluatedListAdapter(Context context, JsonOrderListData.Result orderListResult) {
        this.mContext = context;
        this.data = orderListResult.getOrderList();
    }

    @Override
    public int getCount() {
        return data.size();
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
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.order_activity, null);
            mViewHolder = new ViewHolder();
            mViewHolder.mLv_order_list = convertView.findViewById(R.id.lv_order_list);
            mViewHolder.mTv_quantity_of_goods = convertView.findViewById(R.id.tv_quantity_of_goods);
            mViewHolder.mTv_total_price = convertView.findViewById(R.id.tv_total_price);
            mViewHolder.mTv_order_status = convertView.findViewById(R.id.tv_order_status);
            mViewHolder.mBtn_payment = convertView.findViewById(R.id.btn_payment);
            mViewHolder.mBtn_order_details = convertView.findViewById(R.id.btn_order_details);
            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (ViewHolder) convertView.getTag();
        }

        mOrderList = data.get(position);
        List<Details> detailsList = mOrderList.getDetails();

        mTotalPrice = 0;
        for (Details details : detailsList) {
            double prive = details.getPrice() * details.getNum();
            mTotalPrice += prive;
        }
        mViewHolder.mTv_quantity_of_goods.setText("共 " + detailsList.size() + " 件商品");
        mViewHolder.mTv_total_price.setText("总价 ¥ " + mTotalPrice);
        if (mOrderList.getStatusValue().equals("待支付")) {
            mViewHolder.mBtn_payment.setVisibility(View.VISIBLE);
        }
        mViewHolder.mTv_order_status.setText(mOrderList.getStatusValue());
        mViewHolder.mLv_order_list.setAdapter(new SingleOrderAdapter(mContext, mOrderList));

        mViewHolder.mBtn_payment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, PayActivity.class);
                intent.putExtra("orderNo", data.get(position).getOrderNo());
                mContext.startActivity(intent);
            }
        });
        mViewHolder.mBtn_order_details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, OrderDetailActivity.class);
                String orderNo = data.get(position).getOrderNo();
                intent.putExtra("orderNo", orderNo);
                mContext.startActivity(intent);
            }
        });

        mViewHolder.mBtn_order_details.setTag(position);

        return convertView;
    }

    public void onDateChange(JsonOrderListData.Result orderListResult) {
        this.data.addAll(orderListResult.getOrderList());
        this.notifyDataSetChanged();
    }

    static class ViewHolder {
        private MyListView mLv_order_list;
        private TextView mTv_quantity_of_goods;
        private TextView mTv_total_price;
        private TextView mTv_order_status;
        private Button mBtn_payment;
        private Button mBtn_order_details;
    }
}
