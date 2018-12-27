package com.qiangyu.shopcart.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.qiangyu.R;

/**
 * 自定义增删
 */
public class AddSubView extends LinearLayout implements View.OnClickListener {

    private ImageView mIv_add;
    private ImageView mIv_sub;
    //private TextView mTv_value;
    private Context mContext;

    private double value = 1;
    private double maxValue = 999999;
    private double minValue = 1;
    private TextView mEt_value;
    private int divisive;

    public AddSubView(Context context) {
        super(context);
    }

    public AddSubView(Context context, int divisive) {
        super(context);
        this.divisive = divisive;
        Log.d("setDivisive", "setDivisive: " + divisive + " -- " + this.divisive);
    }

    public int getDivisive() {
        return divisive;
    }

    public void setDivisive(int divisive) {
        this.divisive = divisive;
    }

    public AddSubView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        //把布局文件实例化,并加载到AddSubView类中
        View.inflate(mContext, R.layout.add_sub_view, AddSubView.this);
        mIv_add = findViewById(R.id.iv_add);
        mIv_sub = findViewById(R.id.iv_sub);
        //mTv_value = findViewById(R.id.tv_value);
        mEt_value = findViewById(R.id.et_value);

        double value = getValue();
        setValue(value);
        //设置点击事件
        mIv_add.setOnClickListener(this);
        mIv_sub.setOnClickListener(this);
        mEt_value.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                String strValue = mEt_value.getText().toString().trim();
                int etValue = Integer.parseInt(strValue);
                if (etValue > maxValue) {
                    setValue(maxValue);
                    return;
                }
                if (etValue < minValue) {
                    setValue(minValue);
                    return;
                }
                setValue(etValue);
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_add://加
                addNumber(divisive);
                break;
            case R.id.iv_sub://减
                subNumber(divisive);
                break;
        }
        //Toast.makeText(mContext, "当前商品数--->" + value, Toast.LENGTH_SHORT).show();
    }

    private void addNumber(int zhi) {
        if (value < maxValue) {
            //value = value + 0.10;
            Log.d("addNumber", "addNumber: " + zhi + " -- " + divisive);
            if (zhi == 0) {
                value++;
            } else {
                value += 0.1;
            }
        }
        String format = String.format("%.2f", value);
        setValue(Double.valueOf(format));

        if (mOnNumberChangeListener != null) {
            mOnNumberChangeListener.onNumberChange(value);
        }
    }

    private void subNumber(int zhi) {
        if (value > minValue) {
            Log.d("subNumber", "subNumber: " + zhi + " -- " + divisive);
            //value = value - 0.10;
            if (zhi == 0) {
                value--;
            } else {
                value -= 0.1;
            }
        }
        String format = String.format("%.2f", value);
        setValue(Double.valueOf(format));
        //setValue(value);

        if (mOnNumberChangeListener != null) {
            mOnNumberChangeListener.onNumberChange(value);
        }
    }

    public double getValue() {
        //String strValue1 = mTv_value.getText().toString().trim();
        String strValue2 = mEt_value.getText().toString().trim();
        /*if (!TextUtils.isEmpty(strValue1)) {
            value = Integer.parseInt(strValue1);
        }*/
        if (!TextUtils.isEmpty(strValue2)) {
            value = Double.parseDouble(strValue2);
        }
        return value;
    }

    public void setValue(double value) {
        this.value = value;
        //mTv_value.setText(value + "");
        mEt_value.setText(value + "");
    }

    public double getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(double maxValue) {
        this.maxValue = maxValue;
    }

    public double getMinValue() {
        return minValue;
    }

    public void setMinValue(double minValue) {
        this.minValue = minValue;
    }

    public interface OnNumberChangeListener {
        /**
         * 当数据发生变化时回调
         */
        public void onNumberChange(double value);
    }

    private OnNumberChangeListener mOnNumberChangeListener;

    /**
     * 设置数据变化的监听
     *
     * @param onNumberChangeListener
     */
    public void setOnNumberChangeListener(OnNumberChangeListener onNumberChangeListener) {
        this.mOnNumberChangeListener = onNumberChangeListener;
    }

}
