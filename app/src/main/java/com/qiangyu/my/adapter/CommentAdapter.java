package com.qiangyu.my.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RatingBar;
import android.widget.TextView;

import com.qiangyu.R;
import com.qiangyu.my.bean.CommentList;

import java.util.List;

public class CommentAdapter extends BaseAdapter {

    private Context mContext;
    private final List<CommentList> data;

    public CommentAdapter(Context context, List<CommentList> commentList) {
        this.mContext = context;
        this.data = commentList;
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
            convertView = View.inflate(mContext, R.layout.activity_comment, null);
            viewHolder = new ViewHolder();
            viewHolder.rbXingji = convertView.findViewById(R.id.rb_xingji);
            viewHolder.tvRiqi = convertView.findViewById(R.id.tv_riqi);
            viewHolder.tvPinglun = convertView.findViewById(R.id.tv_pinglun);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        CommentList commentList = data.get(position);

        viewHolder.tvRiqi.setText(commentList.getCommDT());
        viewHolder.tvPinglun.setText(commentList.getComments());
        viewHolder.rbXingji.setRating(Float.valueOf(commentList.getRating()));

        return convertView;
    }

    static class ViewHolder {
        public RatingBar rbXingji;
        public TextView tvRiqi;
        public TextView tvPinglun;
    }
}
