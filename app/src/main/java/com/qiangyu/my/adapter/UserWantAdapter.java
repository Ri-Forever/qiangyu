package com.qiangyu.my.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.qiangyu.R;
import com.qiangyu.my.activity.DemandDetailedActivity;
import com.qiangyu.my.bean.NeedList;

import java.text.SimpleDateFormat;
import java.util.List;

public class UserWantAdapter extends BaseAdapter {

    private final List<NeedList> data;
    private Context mContext;

    public UserWantAdapter(Context context, List<NeedList> needList) {
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.activity_user_want, null);
            viewHolder = new ViewHolder();
            viewHolder.tvNumber = convertView.findViewById(R.id.tv_number);
            viewHolder.tvBiaoti = convertView.findViewById(R.id.tv_biaoti);
            viewHolder.tvShijian = convertView.findViewById(R.id.tv_shijian);
            viewHolder.tvNeirong = convertView.findViewById(R.id.tv_neirong);
            viewHolder.tvChakan = convertView.findViewById(R.id.tv_chakan);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        NeedList needList = data.get(position);

        SimpleDateFormat SFDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String riqi = SFDate.format(needList.getPublicDT());//entity.getTranDate();

        viewHolder.tvNumber.setText("编号:  " + needList.getWantNo());
        viewHolder.tvBiaoti.setText("标题:  " + needList.getTitle());
        viewHolder.tvShijian.setText("发布时间:  " + riqi);
        viewHolder.tvNeirong.setText("需求内容:  " + needList.getWantInfo());

        viewHolder.tvChakan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //查看详情
                //demand_detailed
                String wantNo = data.get(position).getWantNo();
                Intent intent = new Intent(mContext, DemandDetailedActivity.class);
                intent.putExtra("wantNo", wantNo);
                mContext.startActivity(intent);
            }
        });
        return convertView;
    }

    static class ViewHolder {
        public TextView tvNumber;
        public TextView tvBiaoti;
        public TextView tvShijian;
        public TextView tvNeirong;
        public TextView tvChakan;
    }
}
