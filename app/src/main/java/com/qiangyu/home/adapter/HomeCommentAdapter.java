package com.qiangyu.home.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.qiangyu.R;
import com.qiangyu.home.bean.Comments;
import com.qiangyu.im.ui.activity.PicActivity;
import com.qiangyu.utils.Constants;
import com.qiangyu.utils.ToastUtil;

import org.apache.commons.lang3.StringUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class HomeCommentAdapter extends BaseAdapter {

    private Context mContext;
    private List<Comments> datas;

    public HomeCommentAdapter(Context context, List<Comments> comments) {
        this.mContext = context;
        this.datas = comments;
    }

    @Override
    public int getCount() {
        return datas.size();
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
        final ViewHolder viewHolder;
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.activity_home_comment, null);
            viewHolder = new ViewHolder();
            viewHolder.rbXingji = convertView.findViewById(R.id.rb_xingji);
            viewHolder.tvRiqi = convertView.findViewById(R.id.tv_riqi);
            viewHolder.tvPinglun = convertView.findViewById(R.id.tv_pinglun);
            viewHolder.lvPic = convertView.findViewById(R.id.lv_pic);
            viewHolder.iv_touxiang = convertView.findViewById(R.id.iv_touxiang);
            viewHolder.tv_yonghu = convertView.findViewById(R.id.tv_yonghu);
            viewHolder.rv_image = convertView.findViewById(R.id.rv_image);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        Comments comments = datas.get(position);

        String userName = comments.getUserName();
        String headPic = comments.getHeadPic();
        if (StringUtils.isNotEmpty(headPic)) {
            Glide.with(mContext).load(Constants.QIANGYU_PIC_URL + headPic).diskCacheStrategy(DiskCacheStrategy.ALL).centerCrop().into(viewHolder.iv_touxiang);
        }
        if (StringUtils.isNotEmpty(userName)) {
            viewHolder.tv_yonghu.setText(userName);
        }
        @SuppressLint("SimpleDateFormat") SimpleDateFormat SFDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String riqi = SFDate.format(comments.getCommDT());
        viewHolder.rbXingji.setRating(comments.getRating());
        viewHolder.tvRiqi.setText(riqi);
        viewHolder.tvPinglun.setText(comments.getComments());
        /*final List<String> testPic = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            testPic.add("/UploadFiles/c9c56111ad2342e783b6b91baf9ce500.jpg");
        }
        viewHolder.rv_image.setLayoutManager(new GridLayoutManager(mContext, 4));
        viewHolder.rv_image.setAdapter(new GridRecyclerAdapter(mContext, testPic, new GridRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onClick(int i) {
                ToastUtil.toastCenter(mContext, "第 " + i + " 个");
                Intent intent = new Intent(mContext, PicActivity.class);
                intent.putExtra("ImageUrl", testPic.get(i));
                intent.putExtra("comment","comment");
                mContext.startActivity(intent);
            }
        }));*/
        if (comments.getCommentPics() != null) {
            final List<String> commentPics = comments.getCommentPics();
            Log.d("getView", "commentPics: " + commentPics.size());
            //viewHolder.lvPic.setAdapter(new PictureAdapter(mContext, commentPics));
            viewHolder.rv_image.setLayoutManager(new GridLayoutManager(mContext, 4));
            viewHolder.rv_image.setAdapter(new GridRecyclerAdapter(mContext, commentPics, new GridRecyclerAdapter.OnItemClickListener() {
                @Override
                public void onClick(int i) {
                    Intent intent = new Intent(mContext, PicActivity.class);
                    intent.putExtra("ImageUrl", commentPics.get(i));
                    intent.putExtra("comment","comment");
                    mContext.startActivity(intent);
                }
            }));
        }

        return convertView;
    }

    static class ViewHolder {
        RatingBar rbXingji;
        TextView tvRiqi;
        TextView tvPinglun;
        ListView lvPic;
        ImageView iv_touxiang;
        TextView tv_yonghu;
        RecyclerView rv_image;
    }
}
