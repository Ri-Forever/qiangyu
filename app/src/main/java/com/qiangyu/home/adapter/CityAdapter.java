package com.qiangyu.home.adapter;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.qiangyu.R;
import com.qiangyu.home.bean.ShopList;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

import java.util.ArrayList;
import java.util.List;

public class CityAdapter extends BaseAdapter {

    private Context mContext;
    private final List<ShopList> data;
    private HanyuPinyinOutputFormat mFormat;
    private String[] mPinyin;

    public CityAdapter(Context context, List<ShopList> shopListList) {
        this.mContext = context;
        this.data = shopListList;
        mFormat = new HanyuPinyinOutputFormat();
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
            convertView = View.inflate(mContext, R.layout.activity_city, null);
            viewHolder = new ViewHolder();
            viewHolder.tv_city = convertView.findViewById(R.id.tv_city);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        ShopList shopList = data.get(position);
        String cityName = shopList.getName();
        mFormat.setCaseType(HanyuPinyinCaseType.UPPERCASE);
        try {
            mPinyin = PinyinHelper.toHanyuPinyinStringArray(cityName.charAt(0), mFormat);
        } catch (BadHanyuPinyinOutputFormatCombination badHanyuPinyinOutputFormatCombination) {
            badHanyuPinyinOutputFormatCombination.printStackTrace();
        }
        viewHolder.tv_city.setText(mPinyin[0].charAt(0)+" -- " + cityName);

        return convertView;
    }

    static class ViewHolder {
        TextView tv_city;
    }


}
