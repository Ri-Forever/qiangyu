package com.qiangyu.im.ui.activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.qiangyu.R;
import com.qiangyu.utils.Constants;
import com.qiangyu.utils.Loading;
import com.qiangyu.utils.SavePictureUtil;
import com.qiangyu.utils.ToastUtil;
import com.qiangyu.utils.TouchImageView;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;


public class PicActivity extends Activity implements EasyPermissions.PermissionCallbacks {

    private Context mContext;
    private TouchImageView iv_tupian;
    private String imageUrl;
    private static final int REQUEST_CODE_SAVE_IMG = 10;
    private String mComment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mContext = this;
        imageUrl = getIntent().getStringExtra("ImageUrl");
        mComment = getIntent().getStringExtra("comment");
        setContentView(R.layout.activity_pic);
        initView();
    }

    private void initView() {
        iv_tupian = findViewById(R.id.iv_tupian);
        //.placeholder(R.drawable.loading)  加载过程
        //显示图片
        Log.d("initView", "initView: " + imageUrl);
        if ("comment".equals(mComment)) {
            Glide.with(mContext).load(Constants.QIANGYU_PIC_URL + imageUrl).diskCacheStrategy(DiskCacheStrategy.ALL).centerCrop().dontAnimate().into(iv_tupian);
        } else {
            Glide.with(mContext).load(imageUrl).diskCacheStrategy(DiskCacheStrategy.ALL).centerCrop().dontAnimate().into(iv_tupian);
        }

        /*//Glide的储存方式
        Loading.loading(mContext, "正在储存图片请稍后...");
        SDFileHelper helper = new SDFileHelper(mContext);
        helper.savePicture(SavePictureUtil.getInstance().getFileNameByTime(), imageUrl);*/

        iv_tupian.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        iv_tupian.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(mContext);
                alertBuilder.setTitle("确定要保存图片吗?");
                alertBuilder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                alertBuilder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (Build.VERSION.SDK_INT >= 23) {
                            //读取sd卡的权限
                            String[] mPermissionList = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
                            if (EasyPermissions.hasPermissions(mContext, mPermissionList)) {
                                //已经同意过
                                //saveImage();
                                Loading.loading(mContext, "正在保存图片,图片越大耗时越长,请耐心等待...");
                                saveImage(imageUrl);
                            } else {
                                //未同意过,或者说是拒绝了，再次申请权限
                                //上下文//提示文言//请求码//权限列表
                                EasyPermissions.requestPermissions((Activity) mContext, "保存图片需要读取sd卡的权限", REQUEST_CODE_SAVE_IMG, mPermissionList);
                            }
                        } else {
                            Loading.loading(mContext, "正在保存图片,图片越大耗时越长,请耐心等待...");
                            saveImage(imageUrl);
                        }
                    }
                });

                alertBuilder.setCancelable(false);
                AlertDialog alertDialog = alertBuilder.create();
                alertDialog.show();
                return false;
            }
        });
    }

    //保存图片
    public void saveImage(final String picUrl) {

        new Thread(new Runnable() {
            Bitmap bitmap = null;

            @Override
            public void run() {
                URL url = null;
                try {
                    url = new URL(picUrl);
                    InputStream is = null;
                    BufferedInputStream bis = null;
                    try {
                        is = url.openConnection().getInputStream();
                        bis = new BufferedInputStream(is);
                        bitmap = BitmapFactory.decodeStream(bis);

                        boolean isSaveSuccess = SavePictureUtil.saveImageToGallery(getApplicationContext(), bitmap);
                        if (isSaveSuccess) {
                            Loading.endLoad();
                            Looper.prepare();
                            Toast.makeText(mContext, "保存图片成功", Toast.LENGTH_SHORT).show();
                            ToastUtil.toastCenter(mContext, "图片保存成功");
                            Looper.loop();
                        } else {
                            Loading.endLoad();
                            Looper.prepare();
                            Toast.makeText(mContext, "保存图片失败，请稍后重试", Toast.LENGTH_SHORT).show();
                            ToastUtil.toastCenter(mContext, "保存图片失败，请稍后重试");
                            Looper.loop();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            }
        }).start();


    }

    //授权结果，分发下去
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // Forward results to EasyPermissions
        //跳转到onPermissionsGranted或者onPermissionsDenied去回调授权结果
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }


    //同意授权
    @Override
    public void onPermissionsGranted(int requestCode, List<String> list) {
        Log.i("onPermissionsGranted", "onPermissionsGranted:" + requestCode + ":" + list.size());
        Loading.loading(mContext, "正在保存图片,图片越大耗时越长,请耐心等待...");
        saveImage(imageUrl);
    }

    //拒绝授权
    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        Log.i("onPermissionsDenied", "onPermissionsDenied:" + requestCode + ":" + perms.size());
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            //打开系统设置，手动授权
            new AppSettingsDialog.Builder(this).build().show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AppSettingsDialog.DEFAULT_SETTINGS_REQ_CODE) {
            //拒绝授权后，从系统设置了授权后，返回APP进行相应的操作
            Log.i("onActivityResult", "onPermissionsDenied:------>自定义设置授权后返回APP");
            Loading.loading(mContext, "正在保存图片,图片越大耗时越长,请耐心等待...");
            saveImage(imageUrl);
        }
    }

}
