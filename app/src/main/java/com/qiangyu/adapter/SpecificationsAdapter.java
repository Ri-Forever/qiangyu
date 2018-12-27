package com.qiangyu.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.qiangyu.R;
import com.qiangyu.app.GoodsInfoActivity;
import com.qiangyu.home.bean.JsonCommodityBeanData;
import com.qiangyu.home.bean.SpecTypes;
import com.qiangyu.home.bean.SpecValues;
import com.qiangyu.utils.MyRadioGroup;
import com.qiangyu.utils.RadioGroupUtil;

import java.util.ArrayList;
import java.util.List;

public class SpecificationsAdapter extends BaseAdapter {

    private final Context mContext;
    private List<SpecTypes> specTypes;
    private List<SpecValues> mSpecValues;
    private SpecTypes mSpecTypes;
    private int[] value;

    public SpecificationsAdapter(Context context, JsonCommodityBeanData.Result result) {
        this.mContext = context;
        this.specTypes = result.getSpecTypes();
        this.mSpecValues = result.getSpecValues();
        value = new int[specTypes.size()];
    }

    @Override
    public int getCount() {
        return specTypes.size();
    }

    @Override
    public Object getItem(int position) {
        return specTypes.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("ResourceType")
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.activity_specifications, null);
            viewHolder = new ViewHolder();
            viewHolder.tv_spec_types = convertView.findViewById(R.id.tv_spec_types);
            viewHolder.rg_spec_values = convertView.findViewById(R.id.rg_spec_values);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        mSpecTypes = this.specTypes.get(position);

        viewHolder.tv_spec_types.setText(mSpecTypes.getName());
        List<SpecValues> specValuesList = new ArrayList<>();
        for (SpecValues specValue : mSpecValues) {
            if (String.valueOf(mSpecTypes.getId()).equals(String.valueOf(specValue.getTypeId()))) {
                specValuesList.add(specValue);
            }
        }

        viewHolder.rg_spec_values.removeAllViews();
        for (SpecValues specValues : specValuesList) {
            RadioButton radioButton = new RadioButton(mContext);
            RadioGroupUtil.LayoutParams layoutParams = new RadioGroupUtil.LayoutParams(RadioGroupUtil.LayoutParams.WRAP_CONTENT,RadioGroupUtil.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(10, 10, 10, 10);
            radioButton.setText(specValues.getSpecValueName() + "");
            radioButton.setId(specValues.getValueId());
            radioButton.setTextSize(15);
            radioButton.setButtonDrawable(android.R.color.transparent);//隐藏单选圆形按钮
            radioButton.setGravity(Gravity.CENTER);
            radioButton.setPadding(20, 20, 20, 20);
            radioButton.setTextColor(mContext.getResources().getColorStateList(R.drawable.color_radiobutton));//设置选中/未选中的文字颜色
            radioButton.setBackground(mContext.getResources().getDrawable(R.drawable.radio_group_selector));//设置按钮选中/未选中的背景
            radioButton.setLayoutParams(layoutParams);
            viewHolder.rg_spec_values.setOrientation(LinearLayout.HORIZONTAL);
            viewHolder.rg_spec_values.addView(radioButton);//将单选按钮添加到RadioGroup中
        }

        viewHolder.rg_spec_values.setOnCheckedChangeListener(new RadioGroupUtil.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                value[position] = checkedId;
                GoodsInfoActivity.SetPrice(value);
            }
        });

        return convertView;
    }

    static class ViewHolder {
        TextView tv_spec_types;
        RadioGroupUtil rg_spec_values;
    }

}
