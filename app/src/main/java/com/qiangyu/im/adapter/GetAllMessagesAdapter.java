package com.qiangyu.im.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.qiangyu.R;
import com.qiangyu.im.pojo.Message;
import com.qiangyu.utils.Constants;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class GetAllMessagesAdapter extends BaseAdapter {

    private final List<Message> mMessageList;
    private Context mContext;
    private String mHeadPic;
    private List<String> touxiang = new ArrayList();

    public GetAllMessagesAdapter(Context context, List<Message> messageList) {
        this.mContext = context;
        this.mMessageList = messageList;
    }

    @Override
    public int getCount() {
        return mMessageList.size();
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
        System.gc();
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.im_view_more_selling, null);
            viewHolder = new ViewHolder();
            viewHolder.iv_more_selling = convertView.findViewById(R.id.iv_more_selling);
            viewHolder.tv_name = convertView.findViewById(R.id.tv_more_selling_name);
            viewHolder.tv_price = convertView.findViewById(R.id.tv_more_selling_price);
            viewHolder.tv_sold = convertView.findViewById(R.id.tv_more_selling_sold);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        String name = mMessageList.get(i).getDisplayName();
        int unreadMsg = mMessageList.get(i).getUnreadMsg();
        String headPic = mMessageList.get(i).getHeadPic();
        if (StringUtils.isNotEmpty(headPic)) {
            Glide.with(mContext).load(Constants.QIANGYU_PIC_URL + headPic).diskCacheStrategy(DiskCacheStrategy.ALL).centerCrop().into(viewHolder.iv_more_selling);
        }
        viewHolder.tv_name.setText(name);
        if (unreadMsg > 0) {
            viewHolder.tv_sold.setText(unreadMsg + "条未读消息");//未读消息设置
        } else {
            viewHolder.tv_sold.setText("");//未读消息设置
        }
        viewHolder.tv_price.setText("");
        return convertView;
    }

    static class ViewHolder {
        ImageView iv_more_selling;
        TextView tv_name;
        TextView tv_price;
        TextView tv_sold;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }


}
