package com.qiangyu.shopcart.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.qiangyu.R;
import com.qiangyu.shopcart.bean.AddrList;

import java.util.List;

public class RegionAdapter extends BaseAdapter {

    private Context mContext;
    private List<AddrList> data;

    public RegionAdapter(Context context, List<AddrList> addrLists1) {
        this.mContext = context;
        this.data = addrLists1;
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
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.address_activity, null);
            viewHolder = new ViewHolder();
            viewHolder.tv_address = convertView.findViewById(R.id.tv_address);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        AddrList addrList = data.get(position);

        viewHolder.tv_address.setText(addrList.getAddress());

        return convertView;
    }

    static class ViewHolder {
        TextView tv_address;
    }
}
