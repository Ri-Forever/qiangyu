package com.qiangyu.my.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.qiangyu.R;
import com.qiangyu.my.activity.MyWantsActivity;
import com.qiangyu.my.bean.JsonOrderListData;
import com.qiangyu.my.bean.WantList;

import java.text.SimpleDateFormat;
import java.util.List;

public class RequirementAdapter extends BaseAdapter {

    private List<WantList> data;
    private Context mContext;

    public RequirementAdapter(Context context, List<WantList> wantList) {
        this.mContext = context;
        this.data = wantList;
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
    public View getView(final int i, View convertView, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.activity_requirement, null);
            viewHolder = new ViewHolder();
            viewHolder.tvNumber = convertView.findViewById(R.id.tv_number);
            viewHolder.tvTitle = convertView.findViewById(R.id.tv_title);
            viewHolder.tvDate = convertView.findViewById(R.id.tv_date);
            viewHolder.tvContent = convertView.findViewById(R.id.tv_content);
            viewHolder.tvDetails = convertView.findViewById(R.id.tv_details);
            viewHolder.tvStatus = convertView.findViewById(R.id.tv_status);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        final WantList wantList = data.get(i);

        SimpleDateFormat SFDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String riqi = SFDate.format(wantList.getPublicDT());

        viewHolder.tvNumber.setText("编号:  " + wantList.getWantNo());
        viewHolder.tvTitle.setText("标题:  " + wantList.getTitle());
        viewHolder.tvContent.setText("发布时间:  " + riqi);
        viewHolder.tvDate.setText("施工队:  " + wantList.getManagerName());
        viewHolder.tvStatus.setText("当前状态:  " + wantList.getStatus_Desc());
        viewHolder.tvDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, MyWantsActivity.class);
                intent.putExtra("wantNo", wantList.getWantNo());
                mContext.startActivity(intent);
            }
        });
        return convertView;
    }

    public void onDateChange(List<WantList> wantList) {
        this.data.addAll(wantList);
        this.notifyDataSetChanged();
    }

    static class ViewHolder {
        private TextView tvNumber;
        private TextView tvTitle;
        private TextView tvDate;
        private TextView tvContent;
        private TextView tvStatus;
        private TextView tvDetails;
    }
}
