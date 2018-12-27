package com.qiangyu.home.adapter;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

import com.qiangyu.R;
import com.qiangyu.home.bean.Category;

import java.util.ArrayList;
import java.util.List;


public class TypeRightAdapter extends BaseAdapter {

    private final Context context;
    private final List<Category> data;
    private final List<Category> categorys2;
    private ChannelRightAdapter mChannelAdapter;
    private List<Category> mCategories;
    private Category mCategory;

    public TypeRightAdapter(Context context, List<Category> categoryList, List<Category> categorys2) {
        this.context = context;
        this.data = categoryList;
        this.categorys2 = categorys2;
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
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        if (view == null) {
            view = View.inflate(context, R.layout.right_activity, null);
            viewHolder = new ViewHolder();
            viewHolder.tv_biaoti = view.findViewById(R.id.tv_biaoti);
            viewHolder.gv_channel = view.findViewById(R.id.gv_channel);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        mCategory = data.get(i);
        Log.d("TypeRightAdapter", "TypeRightAdapter: " + i);
        viewHolder.tv_biaoti.setText(mCategory.getName());
        mCategories = new ArrayList<>();

        for (Category category1 : categorys2) {
            if (Integer.parseInt(category1.getParentId()) == mCategory.getId()) {
                mCategories.add(category1);
            }
        }

        mChannelAdapter = new ChannelRightAdapter(context, mCategories);
        viewHolder.gv_channel.setAdapter(mChannelAdapter);
        return view;
    }

    static class ViewHolder {
        TextView tv_biaoti;
        GridView gv_channel;
    }

}
