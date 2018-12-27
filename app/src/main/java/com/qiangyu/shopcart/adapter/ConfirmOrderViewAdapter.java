package com.qiangyu.shopcart.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.qiangyu.R;
import com.qiangyu.home.bean.GoodsBean;
import com.qiangyu.shopcart.bean.CartList;
import com.qiangyu.shopcart.bean.PicList;
import com.qiangyu.utils.Constants;
import com.qiangyu.utils.SPUtils;

import java.util.List;

public class ConfirmOrderViewAdapter extends BaseAdapter {

    private final Context mContext;
    private final List<CartList> data;
    private final List<PicList> mPicList;
    private final String mType;
    private double mSum;

    public ConfirmOrderViewAdapter(Context context, GoodsBean goodsBean) {
        this.mContext = context;
        this.data = goodsBean.getCartResult().getCartList();
        this.mPicList = goodsBean.getCartResult().getPicList();
        mType = SPUtils.getInstance().getString("type");
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
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = View.inflate(mContext, R.layout.activity_confirm_order_view, null);
            viewHolder.ivTupian = convertView.findViewById(R.id.iv_tupian);
            viewHolder.tvShangpinming = convertView.findViewById(R.id.tv_shangpinming);
            viewHolder.tvGuigecanshu1 = convertView.findViewById(R.id.tv_guigecanshu1);
            viewHolder.tvGuigecanshu2 = convertView.findViewById(R.id.tv_guigecanshu2);
            viewHolder.tvJiage = convertView.findViewById(R.id.tv_jiage);
            viewHolder.tvShuliang = convertView.findViewById(R.id.tv_shuliang);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        CartList cartList = data.get(i);
        PicList picList = mPicList.get(i);
        Glide.with(mContext).load(Constants.QIANGYU_PIC_URL + picList.getPicture()).diskCacheStrategy(DiskCacheStrategy.ALL).centerCrop().into(viewHolder.ivTupian);
        viewHolder.tvShangpinming.setText(cartList.getGoodsName());
        viewHolder.tvGuigecanshu1.setText("规格参数 " + cartList.getSpecValues());
        viewHolder.tvGuigecanshu2.setVisibility(View.GONE);
        if ("0".equals(mType)) {
            mSum = cartList.getSalePrice2() * cartList.getNum();
        } else {
            mSum = cartList.getSalePrice1() * cartList.getNum();
        }
        String format = String.format("%.1f", mSum);
        viewHolder.tvJiage.setText("￥ " + format);
        viewHolder.tvShuliang.setText("X " + cartList.getNum());
        return convertView;
    }

    static class ViewHolder {
        ImageView ivTupian;
        TextView tvShangpinming;
        TextView tvGuigecanshu1;
        TextView tvGuigecanshu2;
        TextView tvJiage;
        TextView tvShuliang;

    }
}
