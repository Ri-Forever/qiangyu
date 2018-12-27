package com.qiangyu.im.ui.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.qiangyu.R;
import com.qiangyu.home.adapter.PhotoAdapter;
import com.qiangyu.im.enity.MessageInfo;
import com.qiangyu.im.util.Constants;
import com.qiangyu.utils.ToastUtil;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class ChatFunctionFragment extends BaseFragment {
    private View rootView;
    private static final int CROP_PHOTO = 2;
    private static final int REQUEST_CODE_PICK_IMAGE = 3;
    private static final int MY_PERMISSIONS_REQUEST_CALL_PHONE = 6;
    private static final int MY_PERMISSIONS_REQUEST_CALL_PHONE2 = 7;
    private static final int REQ_GALLERY = 33;
    private Uri uri;
    private String path;
    private File output;
    private Uri imageUri;
    private String mKefuName;
    private String mPublicPhotoPath;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mKefuName = getActivity().getIntent().getStringExtra("kefu");
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.im_fragment_chat_function, container, false);
            ButterKnife.bind(this, rootView);
        }
        return rootView;
    }

    @Override
    protected View initView() {
        return null;
    }

    @Override
    protected void setUpView() {

    }

    @OnClick({R.id.chat_function_photo, R.id.chat_function_photograph})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.chat_function_photograph:
                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_CALL_PHONE2);
                } else {
                    takePhoto();
                }
                break;
            case R.id.chat_function_photo:
                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_CALL_PHONE2);
                } else {
                    choosePhoto();
                }
                break;
        }
    }

    /**
     * 拍照
     */
    private void takePhoto() {
        /**
         * 最后一个参数是文件夹的名称，可以随便起
         */
        File file = new File(Environment.getExternalStorageDirectory(), "拍照");
        if (!file.exists()) {
            file.mkdir();
        }
        /**
         * 这里将时间作为不同照片的名称
         */
        output = new File(file, System.currentTimeMillis() + ".jpg");

        /**
         * 如果该文件夹已经存在，则删除它，否则创建一个
         */
        try {
            if (output.exists()) {
                output.delete();
            }
            output.createNewFile();
        } catch (Exception e) {
            e.printStackTrace();
        }
        /**
         * 隐式打开拍照的Activity，并且传入CROP_PHOTO常量作为拍照结束后回调的标志
         */
        if (Build.VERSION.SDK_INT >= 24) {
            // TODO: 2018/10/28 0028 7.0以上拍照权限
            //ToastUtil.toastCenter(mContext, "安卓8.0以上系统暂时无法使用拍照功能");
            if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.CAMERA)) {
                } else {
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                }
            } else {
                startTake();
            }
            return;
        }
        imageUri = Uri.fromFile(output);
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, CROP_PHOTO);

    }

    //====================================================7.0以上相机权限====================================================\\

    private void startTake() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //判断是否有相机应用
        if (takePictureIntent.resolveActivity(mContext.getPackageManager()) != null) {
            //创建临时图片文件
            File photoFile = null;
            try {
                photoFile = createPublicImageFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            //设置Action为拍照
            if (photoFile != null) {
                takePictureIntent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
                //这里加入flag
                takePictureIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                Uri photoURI;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {//7.0及以上
                    photoURI = FileProvider.getUriForFile(mContext, mContext.getPackageName() + ".updateFileProvider", photoFile);
                } else {
                    photoURI = Uri.fromFile(photoFile);
                }
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQ_GALLERY);

            }
        }
        //将照片添加到相册中
        galleryAddPic(mPublicPhotoPath, mContext);
    }

    /**
     * 创建临时图片文件
     *
     * @return
     * @throws IOException
     */
    private File createPublicImageFile() throws IOException {
        File path = null;
        if (hasSdcard()) {
            path = Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DCIM);
        }
        Date date = new Date();
        String timeStamp = getTime(date, "yyyyMMdd_HHmmss", Locale.CHINA);
        String imageFileName = "Camera/" + "IMG_" + timeStamp + ".jpg";
        File image = new File(path, imageFileName);
        mPublicPhotoPath = image.getAbsolutePath();
        return image;
    }

    /**
     * 判断sdcard是否被挂载
     *
     * @return
     */
    public static boolean hasSdcard() {
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 获取时间的方法
     *
     * @param date
     * @param mode
     * @param locale
     * @return
     */
    private String getTime(Date date, String mode, Locale locale) {
        SimpleDateFormat format = new SimpleDateFormat(mode, locale);
        return format.format(date);
    }

    /**
     * 将照片添加到相册中
     */
    public static void galleryAddPic(String mPublicPhotoPath, Context context) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mPublicPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        context.sendBroadcast(mediaScanIntent);
    }

    //====================================================7.0以上相机权限====================================================\\

    /**
     * 从相册选取图片
     */
    private void choosePhoto() {
        /**
         * 打开选择图片的界面
         */
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");//相片类型
        startActivityForResult(intent, REQUEST_CODE_PICK_IMAGE);

    }

    /**
     * 拍照之后获取结果的方法
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQ_GALLERY) {
            if (resultCode != Activity.RESULT_OK) return;
            uri = Uri.parse(mPublicPhotoPath);
            path = uri.getPath();
            MessageInfo messageInfo = new MessageInfo();
            //imagePath为图片本地路径，false为不发送原图（默认超过100k的图片会压缩后发给对方），需要发送原图传true
            EMMessage emMessage = EMMessage.createImageSendMessage(path, true, mKefuName);
            //如果是群聊，设置chattype，默认是单聊
            EMClient.getInstance().chatManager().sendMessage(emMessage);
            System.out.println("图片2 == " + path);
            messageInfo.setImageUrl(path);
            EventBus.getDefault().post(messageInfo);
            return;
        }
        switch (requestCode) {
            case CROP_PHOTO:
                if (resultCode == Activity.RESULT_OK) {
                    try {
                        MessageInfo messageInfo = new MessageInfo();

                        //imagePath为图片本地路径，false为不发送原图（默认超过100k的图片会压缩后发给对方），需要发送原图传true
                        EMMessage emMessage = EMMessage.createImageSendMessage(imageUri.getPath(), true, mKefuName);
                        //如果是群聊，设置chattype，默认是单聊
                        EMClient.getInstance().chatManager().sendMessage(emMessage);
                        System.out.println("图片1 == " + imageUri.getPath());
                        messageInfo.setImageUrl(imageUri.getPath());
                        EventBus.getDefault().post(messageInfo);
                    } catch (Exception e) {
                    }
                } else {
                    Log.d(Constants.TAG, "失败");
                }

                break;
            case REQUEST_CODE_PICK_IMAGE:
                if (resultCode == Activity.RESULT_OK) {
                    try {
                        Uri uri = data.getData();
                        MessageInfo messageInfo = new MessageInfo();
                        //imagePath为图片本地路径，false为不发送原图（默认超过100k的图片会压缩后发给对方），需要发送原图传true
                        EMMessage emMessage = EMMessage.createImageSendMessage(getRealPathFromURI(uri), true, mKefuName);
                        //如果是群聊，设置chattype，默认是单聊
                        EMClient.getInstance().chatManager().sendMessage(emMessage);
                        System.out.println("图片2 == " + getRealPathFromURI(uri));
                        messageInfo.setImageUrl(getRealPathFromURI(uri));
                        EventBus.getDefault().post(messageInfo);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.d(Constants.TAG, e.getMessage());
                    }
                } else {
                    Log.d(Constants.TAG, "失败");
                }

                break;

            default:
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    /*public void onActivityResult(int req, int res, Intent data) {
        if (req == REQ_GALLERY) {
            if (req != Activity.RESULT_OK) return;
            uri = Uri.parse(mPublicPhotoPath);
            path = uri.getPath();
            Uri uri = data.getData();
            MessageInfo messageInfo = new MessageInfo();
            //imagePath为图片本地路径，false为不发送原图（默认超过100k的图片会压缩后发给对方），需要发送原图传true
            EMMessage emMessage = EMMessage.createImageSendMessage(getRealPathFromURI(uri), true, mKefuName);
            //如果是群聊，设置chattype，默认是单聊
            EMClient.getInstance().chatManager().sendMessage(emMessage);
            System.out.println("图片2 == " + getRealPathFromURI(uri));
            messageInfo.setImageUrl(getRealPathFromURI(uri));
            EventBus.getDefault().post(messageInfo);
            return;
        }
        switch (req) {
            case CROP_PHOTO:
                if (res == Activity.RESULT_OK) {
                    try {
                        MessageInfo messageInfo = new MessageInfo();

                        //imagePath为图片本地路径，false为不发送原图（默认超过100k的图片会压缩后发给对方），需要发送原图传true
                        EMMessage emMessage = EMMessage.createImageSendMessage(imageUri.getPath(), true, mKefuName);
                        //如果是群聊，设置chattype，默认是单聊
                        EMClient.getInstance().chatManager().sendMessage(emMessage);
                        System.out.println("图片1 == " + imageUri.getPath());
                        messageInfo.setImageUrl(imageUri.getPath());
                        EventBus.getDefault().post(messageInfo);
                    } catch (Exception e) {
                    }
                } else {
                    Log.d(Constants.TAG, "失败");
                }

                break;
            case REQUEST_CODE_PICK_IMAGE:
                if (res == Activity.RESULT_OK) {
                    try {
                        Uri uri = data.getData();
                        MessageInfo messageInfo = new MessageInfo();
                        //imagePath为图片本地路径，false为不发送原图（默认超过100k的图片会压缩后发给对方），需要发送原图传true
                        EMMessage emMessage = EMMessage.createImageSendMessage(getRealPathFromURI(uri), true, mKefuName);
                        //如果是群聊，设置chattype，默认是单聊
                        EMClient.getInstance().chatManager().sendMessage(emMessage);
                        System.out.println("图片2 == " + getRealPathFromURI(uri));
                        messageInfo.setImageUrl(getRealPathFromURI(uri));
                        EventBus.getDefault().post(messageInfo);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.d(Constants.TAG, e.getMessage());
                    }
                } else {
                    Log.d(Constants.TAG, "失败");
                }

                break;

            default:
                break;
        }
    }*/

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        if (requestCode == MY_PERMISSIONS_REQUEST_CALL_PHONE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                takePhoto();
            } else {
                Toast.makeText(mContext, "请同意系统权限后继续", Toast.LENGTH_SHORT).show();
            }
        }

        if (requestCode == MY_PERMISSIONS_REQUEST_CALL_PHONE2) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                choosePhoto();
            } else {
                Toast.makeText(mContext, "请同意系统权限后继续", Toast.LENGTH_SHORT).show();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public String getRealPathFromURI(Uri contentUri) {
        String res = null;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getActivity().getContentResolver().query(contentUri, proj, null, null, null);
        if (cursor.moveToFirst()) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            res = cursor.getString(column_index);
        }
        cursor.close();
        return res;
    }
}
