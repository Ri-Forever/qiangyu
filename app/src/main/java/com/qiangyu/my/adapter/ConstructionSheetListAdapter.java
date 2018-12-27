package com.qiangyu.my.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.qiangyu.R;
import com.qiangyu.my.activity.TeamWantContractActivity;
import com.qiangyu.my.activity.TeamWantsDtailsActivity;
import com.qiangyu.my.bean.NeedList;
import com.qiangyu.my.bean.WantList;

import java.util.List;

public class ConstructionSheetListAdapter extends BaseAdapter {

    private Context mContext;
    private final List<NeedList> data;

    public ConstructionSheetListAdapter(Context context, List<NeedList> needList) {
        this.mContext = context;
        this.data = needList;
    }

    public void onDateChange(List<NeedList> needList) {
        this.data.addAll(needList);
        this.notifyDataSetChanged();
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
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.activity_construction_sheet_list, null);
            viewHolder = new ViewHolder();
            viewHolder.tvNumber = convertView.findViewById(R.id.tv_number);
            viewHolder.tvBiaoti = convertView.findViewById(R.id.tv_biaoti);
            viewHolder.tvLianxiren = convertView.findViewById(R.id.tv_lianxiren);
            viewHolder.tvDianhua = convertView.findViewById(R.id.tv_dianhua);
            viewHolder.tvDizhi = convertView.findViewById(R.id.tv_dizhi);
            viewHolder.tvNeirong = convertView.findViewById(R.id.tv_neirong);
            viewHolder.tvStatus = convertView.findViewById(R.id.tv_status);
            viewHolder.tvTianxie = convertView.findViewById(R.id.tv_tianxie);
            viewHolder.tvQuxiao = convertView.findViewById(R.id.tv_quxiao);
            viewHolder.tvChakan = convertView.findViewById(R.id.tv_chakan);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        NeedList needList = data.get(position);

        viewHolder.tvNumber.setText("编号:  " + needList.getWantNo());
        viewHolder.tvBiaoti.setText("标题:  " + needList.getTitle());
        viewHolder.tvLianxiren.setText("联系人:  " + needList.getReceiver());
        viewHolder.tvDianhua.setText("联系电话:  " + needList.getMobile());
        viewHolder.tvDizhi.setText("地址:  " + needList.getAddress());
        viewHolder.tvNeirong.setText("内容:  " + needList.getWantInfo());
        viewHolder.tvStatus.setText(needList.getStatus_Desc());

        switch (needList.getStatus()) {
            case 0:
                break;
            case 1:
                break;
            case 2:
                break;
            case 3:
                viewHolder.tvChakan.setVisibility(View.GONE);
                viewHolder.tvTianxie.setVisibility(View.VISIBLE);
                break;
            case 4:
                break;
            case 5:
                break;
            case 6:
                break;
            case 7:
                break;
            case 8:
                break;
            case 9:
                break;
            case 10:
                break;
            case 11:
                break;
            case 12:
                break;
            case 13:
                break;
        }
        final String wantNo = needList.getWantNo();
        viewHolder.tvTianxie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //填写
                Intent intent = new Intent(mContext, TeamWantContractActivity.class);
                intent.putExtra("wantNo", wantNo);
                mContext.startActivity(intent);
            }
        });
        viewHolder.tvQuxiao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //取消
            }
        });
        viewHolder.tvChakan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //查看合同
                Intent intent = new Intent(mContext, TeamWantsDtailsActivity.class);
                intent.putExtra("wantNo", wantNo);
                mContext.startActivity(intent);
            }
        });
        return convertView;
    }

    static class ViewHolder {
        public TextView tvNumber;
        public TextView tvBiaoti;
        public TextView tvLianxiren;
        public TextView tvDianhua;
        public TextView tvDizhi;
        public TextView tvNeirong;
        public TextView tvStatus;
        public TextView tvTianxie;
        public TextView tvQuxiao;
        public TextView tvChakan;
    }
}
