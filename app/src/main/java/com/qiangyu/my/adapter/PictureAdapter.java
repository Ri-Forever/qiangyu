package com.qiangyu.my.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.qiangyu.R;
import com.qiangyu.my.bean.PictList;
import com.qiangyu.utils.Constants;

import java.util.List;

public class PictureAdapter extends BaseAdapter {

    private final List<PictList> data;
    private Context mContext;

    public PictureAdapter(Context context, List<PictList> pictList) {
        this.mContext = context;
        this.data = pictList;
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
            convertView = View.inflate(mContext, R.layout.activity_picture, null);
            viewHolder = new ViewHolder();
            viewHolder.iv_picture = convertView.findViewById(R.id.iv_picture);
            viewHolder.tv_bianhao = convertView.findViewById(R.id.tv_bianhao);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        PictList pictList = data.get(position);

        //viewHolder.tv_bianhao.setText("图" + position + 1);

        //Glide.with(mContext).load(Constants.QIANGYU_PIC_URL1 + pictList.getPicture()).into(viewHolder.iv_picture);
        Glide.with(mContext)
                .load(Constants.QIANGYU_PIC_URL + pictList.getPicture())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop()
                .into(viewHolder.iv_picture);
                /*.into(new SimpleTarget<GlideDrawable>() {
                    @Override
                    public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                        int width = resource.getIntrinsicWidth();
                        int height = resource.getIntrinsicHeight();
                        viewHolder.iv_picture.setMaxWidth(width);
                        viewHolder.iv_picture.setMaxHeight(height);
                        viewHolder.iv_picture.setImageDrawable(resource);
                        Log.d("getView", "getView: 图片" + width + "-" + height);
                    }
                });*/

        return convertView;
    }

    static class ViewHolder {
        private ImageView iv_picture;
        private TextView tv_bianhao;
    }
}
