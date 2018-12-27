package com.qiangyu.my.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.qiangyu.R;
import com.qiangyu.my.bean.Record;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class RecordsConsumptionAdapter extends BaseAdapter {

    private final Context mContext;
    private final List<Record> datas;

    public RecordsConsumptionAdapter(Context context, List<Record> record) {
        this.mContext = context;
        this.datas = record;
    }

    public void onDateChange(List<Record> record) {
        this.datas.addAll(record);
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return datas.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.records_consumption_activity, null);
            viewHolder = new ViewHolder();
            viewHolder.tvLeixing = convertView.findViewById(R.id.tv_leixing);
            viewHolder.tvRiqi = convertView.findViewById(R.id.tv_riqi);
            viewHolder.tvYue = convertView.findViewById(R.id.tv_yue);
            viewHolder.tvXiaofei = convertView.findViewById(R.id.tv_xiaofei);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        Record record = datas.get(position);

        /*
        "TradeMoney_desc": "415.00",
        "AfterBal_desc": "49425.00",
        "TradeDT_desc": "2018-10-09 11:03:37",
         */
        Date tradeDT_desc = record.getTradeDT_desc();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String riqi = simpleDateFormat.format(tradeDT_desc);
        viewHolder.tvLeixing.setText(record.getTradeType_desc());
        viewHolder.tvRiqi.setText(riqi);
        viewHolder.tvYue.setText("余额: " + record.getAfterBal_desc());
        viewHolder.tvXiaofei.setText("扣除: " + record.getTradeMoney_desc());

        return convertView;
    }

    static class ViewHolder {
        public TextView tvLeixing;
        public TextView tvRiqi;
        public TextView tvYue;
        public TextView tvXiaofei;
    }
}
