package com.qiangyu.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import java.io.File;
import java.io.FileOutputStream;

public class SDFileHelper {

    private Context context;

    public SDFileHelper() {
    }

    public SDFileHelper(Context context) {
        super();
        this.context = context;
    }

    //Glide保存图片
    public void savePicture(final String fileName, String url) {
        Glide.with(context).load(url).asBitmap().toBytes().into(new SimpleTarget<byte[]>() {
            @Override
            public void onResourceReady(byte[] resource, GlideAnimation<? super byte[]> glideAnimation) {
                try {
                    savaFileToSD(fileName, resource);
                } catch (Exception e) {
                    Loading.endLoad();
                    e.printStackTrace();
                }
            }
        });
    }

    //往SD卡写入文件的方法
    public void savaFileToSD(String filename, byte[] bytes) throws Exception {
        //如果手机已插入sd卡,且app具有读写sd卡的权限
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            String filePath = Environment.getExternalStorageDirectory().getCanonicalPath() + File.separator + "QiangYuPic";

            //String filePath = SavePictureUtil.getInstance().getFilePath("/QiangYuPic");
            File dir1 = new File(filePath);
            if (!dir1.exists()) {
                dir1.mkdirs();
            }
            filename = filePath + "/" + filename;
            //这里就不要用openFileOutput了,那个是往手机内存中写数据的
            FileOutputStream output = new FileOutputStream(filename);
            //将bytes写入到输出流中
            output.write(bytes);
            output.flush();
            //关闭输出流
            output.close();
            //Toast.makeText(context, "图片已成功保存到" + filePath, Toast.LENGTH_SHORT).show();
            Loading.endLoad();
            ToastUtil.toastCenter(context, "图片已成功保存到: " + filePath);

            // 其次把文件插入到系统图库
            //MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "title", "description");
            //MediaStore.Images.Media.insertImage(context.getContentResolver(), dir1.getAbsolutePath(), filename, null);

            //保存图片后发送广播通知更新数据库
            Uri uri = Uri.fromFile(dir1);
            //context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));
            Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            intent.setData(uri);
            context.sendBroadcast(intent);
        } else {
            Loading.endLoad();
            Toast.makeText(context, "SD卡不存在或者不可读写", Toast.LENGTH_SHORT).show();
        }
    }
}
