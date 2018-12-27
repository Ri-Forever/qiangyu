package com.qiangyu.home.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.qiangyu.R;
import com.qiangyu.home.bean.Category;

import java.util.List;


/**
 * Created by Administrator on 2016/10/3.
 */
public class TypeLeftAdapter extends BaseAdapter {
    private List<Category> categorys;
    private Context mContext;
    private int mSelect = 0;//选中项
    private String[] titles = new String[]{"小裙子", "上衣", "下装", "外套", "配件", "包包", "装扮", "居家宅品", "办公文具", "数码周边", "游戏专区"};

    public TypeLeftAdapter(Context mContext) {
        this.mContext = mContext;
    }

    public TypeLeftAdapter(Context mContext, List<Category> categorys) {
        this.mContext = mContext;
        this.categorys = categorys;
    }

    @Override
    public int getCount() {
        //return titles.length;
        return categorys.size();
    }

    @Override
    public Object getItem(int position) {
        //return titles[position];
        return categorys.get(position).getName();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.item_type, null);
            holder = new ViewHolder();
            holder.tv_name = convertView.findViewById(R.id.tv_title);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        //holder.tv_name.setText(titles[position]);
        holder.tv_name.setText(categorys.get(position).getName());

        if (mSelect == position) {
            convertView.setBackgroundResource(R.drawable.type_item_background_selector);  //选中项背景
            holder.tv_name.setTextColor(Color.parseColor("#fd3f3f"));
        } else {
            convertView.setBackgroundResource(R.mipmap.bg2);  //其他项背景
            holder.tv_name.setTextColor(Color.parseColor("#323437"));
        }
        return convertView;
    }

    public void changeSelected(int positon) { //刷新方法
        if (positon != mSelect) {
            mSelect = positon;
            notifyDataSetChanged();
        }
    }

    static class ViewHolder {
        private TextView tv_name;
    }
}
