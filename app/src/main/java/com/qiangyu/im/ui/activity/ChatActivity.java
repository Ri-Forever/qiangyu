package com.qiangyu.im.ui.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMCmdMessageBody;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMImageMessageBody;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.chat.EMVoiceMessageBody;
import com.jude.easyrecyclerview.EasyRecyclerView;
import com.qiangyu.R;
import com.qiangyu.im.adapter.ChatAdapter;
import com.qiangyu.im.adapter.CommonFragmentPagerAdapter;
import com.qiangyu.im.enity.MessageInfo;
import com.qiangyu.im.ui.fragment.ChatEmotionFragment;
import com.qiangyu.im.ui.fragment.ChatFunctionFragment;
import com.qiangyu.im.util.Constants;
import com.qiangyu.im.util.GlobalOnItemClickManagerUtils;
import com.qiangyu.im.util.MediaManager;
import com.qiangyu.im.widget.EmotionInputDetector;
import com.qiangyu.im.widget.NoScrollViewPager;
import com.qiangyu.im.widget.StateButton;
import com.qiangyu.service.MyService;
import com.qiangyu.utils.DateUtil;
import com.qiangyu.utils.SPUtils;

import org.apache.commons.lang3.StringUtils;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ChatActivity extends FragmentActivity implements EMMessageListener {

    @BindView(R.id.chat_list)
    EasyRecyclerView chatList;
    @BindView(R.id.emotion_voice)
    ImageView emotionVoice;
    @BindView(R.id.edit_text)
    EditText editText;
    @BindView(R.id.voice_text)
    TextView voiceText;
    @BindView(R.id.emotion_button)
    ImageView emotionButton;
    @BindView(R.id.emotion_add)
    ImageView emotionAdd;
    @BindView(R.id.emotion_send)
    StateButton emotionSend;
    @BindView(R.id.viewpager)
    NoScrollViewPager viewpager;
    @BindView(R.id.emotion_layout)
    RelativeLayout emotionLayout;
    @BindView(R.id.ib_more_selling_back)
    ImageView mIbBack;
    @BindView(R.id.tv_search_more_selling)
    TextView mTvName;

    private Context mContext;
    private EmotionInputDetector mDetector;
    private ArrayList<Fragment> fragments;
    private ChatEmotionFragment chatEmotionFragment;
    private ChatFunctionFragment chatFunctionFragment;
    private CommonFragmentPagerAdapter adapter;
    // 当前会话对象
    private EMConversation mConversation;
    private ChatAdapter chatAdapter;
    private LinearLayoutManager layoutManager;
    private List<MessageInfo> messageInfos;
    //录音相关
    int animationRes = 0;
    int res = 0;
    AnimationDrawable animationDrawable = null;
    private ImageView animView;
    private int i = 0;
    private String mKefuName;
    private EMMessageListener mMessageListener;
    private String mContentText;
    private ServiceConnection mServiceConnection = null;
    private String mHeadPic;
    private MessageInfo mMessage;
    private MessageInfo mMessage1;
    private MessageInfo mMessage2;
    private String mUserHeadPic;
    private String mDisplayName;
    private String mMsgId;
    private int mMessagesSize;
    private List<EMMessage> mMessages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mContext = this;
        setContentView(R.layout.im_activity_main);
        ButterKnife.bind(this);
        mMessageListener = this;
        EventBus.getDefault().register(this);
        //获取点击传过来的名称
        mKefuName = getIntent().getStringExtra("kefu");
        mHeadPic = getIntent().getStringExtra("headPic");
        mDisplayName = getIntent().getStringExtra("displayName");
        mUserHeadPic = SPUtils.getInstance().getString("userHeadPic");
        initWidget();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //添加消息监听
        EMClient.getInstance().chatManager().addMessageListener(mMessageListener);

        //注销环信消息监听
        final Intent intent = new Intent(mContext, MyService.class);
        stopService(intent);

    }

    @Override
    protected void onPause() {
        super.onPause();
        //移除消息监听
        EMClient.getInstance().chatManager().removeMessageListener(mMessageListener);

        //开启环信消息监听
        final Intent intent = new Intent(mContext, MyService.class);
        startService(intent);
    }

    @SuppressLint("HandlerLeak")
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    EMMessage emMessage = (EMMessage) msg.obj;
                    String msgType = emMessage.getType().toString();
                    if ("TXT".equals(msgType)) {
                        mMessage = new MessageInfo();
                        EMTextMessageBody body = (EMTextMessageBody) emMessage.getBody();
                        String time = DateUtil.timeStampDate(emMessage.getMsgTime());
                        mMessage.setTime(time);
                        mMessage.setContent(body.getMessage());
                        mMessage.setType(Constants.CHAT_ITEM_TYPE_LEFT);
                        if (StringUtils.isNotEmpty(mHeadPic)) {
                            mMessage.setHeader(com.qiangyu.utils.Constants.QIANGYU_PIC_URL + mHeadPic);
                        } else {
                            Uri uri = Uri.parse("android.resource://" + mContext.getPackageName() + "/" + R.mipmap.logo_icon);
                            mMessage.setHeader(uri.toString());
                        }
                        messageInfos.add(mMessage);
                        chatAdapter.add(mMessage);
                        chatList.scrollToPosition(chatAdapter.getCount() - 1);
                        break;
                    }
                    if ("VOICE".equals(msgType)) {
                        mMessage = new MessageInfo();
                        EMVoiceMessageBody voiceMessageBody = (EMVoiceMessageBody) emMessage.getBody();
                        String time = DateUtil.timeStampDate(emMessage.getMsgTime());
                        mMessage.setTime(time);
                        mMessage.setFilepath(voiceMessageBody.getLocalUrl());
                        mMessage.setVoiceTime(voiceMessageBody.getLength());
                        mMessage.setType(Constants.CHAT_ITEM_TYPE_LEFT);
                        if (StringUtils.isNotEmpty(mHeadPic)) {
                            mMessage.setHeader(com.qiangyu.utils.Constants.QIANGYU_PIC_URL + mHeadPic);
                        } else {
                            Uri uri = Uri.parse("android.resource://" + mContext.getPackageName() + "/" + R.mipmap.logo_icon);
                            mMessage.setHeader(uri.toString());
                        }
                        messageInfos.add(mMessage);
                        chatAdapter.add(mMessage);
                        chatList.scrollToPosition(chatAdapter.getCount() - 1);
                        break;
                    }
                    if ("IMAGE".equals(msgType)) {
                        mMessage = new MessageInfo();
                        EMImageMessageBody emImageMessageBody = (EMImageMessageBody) emMessage.getBody();
                        String time = DateUtil.timeStampDate(emMessage.getMsgTime());
                        mMessage.setTime(time);
                        mMessage.setImageUrl(emImageMessageBody.getThumbnailUrl());
                        Log.d("handleMessage", "handleMessage: " + emImageMessageBody.getThumbnailUrl());

                        mMessage.setType(Constants.CHAT_ITEM_TYPE_LEFT);
                        if (StringUtils.isNotEmpty(mHeadPic)) {
                            mMessage.setHeader(com.qiangyu.utils.Constants.QIANGYU_PIC_URL + mHeadPic);
                        } else {
                            Uri uri = Uri.parse("android.resource://" + mContext.getPackageName() + "/" + R.mipmap.logo_icon);
                            mMessage.setHeader(uri.toString());
                        }
                        messageInfos.add(mMessage);
                        chatAdapter.add(mMessage);
                        chatList.scrollToPosition(chatAdapter.getCount() - 1);
                        break;
                    }
                    break;
            }
        }
    };

    // TODO: 2018/8/18 消息实时接收
    @Override
    public void onMessageReceived(List<EMMessage> messages) {

        //接收消息
        for (EMMessage emMessage : messages) {
            //Log.d("收到新消息", "收到新消息:" + emMessage);
            if (emMessage.getFrom().equals(mKefuName)) {
                Log.d("onMessageReceived", "在聊天界面 -- " + mKefuName);
                // 设置消息为已读
                mConversation.markMessageAsRead(emMessage.getMsgId());
                // 因为消息监听回调这里是非ui线程，所以要用handler去更新ui
                Message msg = mHandler.obtainMessage();
                msg.what = 0;
                msg.obj = emMessage;
                mHandler.sendMessage(msg);
            } /*else {
                // TODO 如果消息不是当前会话的消息发送通知栏通知
                Log.d("onMessageReceived", "不在聊天界面 -- " + mKefuName);
                //收到消息,进行推送
                //设置跳转的页面
                PendingIntent intent = PendingIntent.getActivity(mContext, 100, new Intent(mContext, ContactCustomerServiceActivity.class), PendingIntent.FLAG_CANCEL_CURRENT);
                String type = emMessage.getType().toString();
                if ("TXT".equals(type)) {
                    EMTextMessageBody body = (EMTextMessageBody) emMessage.getBody();
                    mContentText = body.getMessage();
                }
                if ("VOICE".equals(type)) {
                    mContentText = "[语音消息]";
                }
                if ("IMAGE".equals(type)) {
                    mContentText = "[图片]";
                }

                if (Build.VERSION.SDK_INT >= 26) {
                    NotificationManager manager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
                    //NotificationManager.IMPORTANCE_HIGH
                    NotificationChannel channel = new NotificationChannel("channel_id", "channel_name", NotificationManager.IMPORTANCE_HIGH);
                    //channel.setSound(Uri.parse("android.resource://" + mContext.getPackageName() + "/" + R.raw.shuisheng), new AudioAttributes.Builder().build());
                    channel.enableLights(true);//闪光灯
                    channel.setLockscreenVisibility(VISIBILITY_SECRET);//锁屏显示通知
                    channel.setLightColor(Color.RED);//闪关灯的灯光颜色
                    manager.createNotificationChannel(channel);

                    //播放声音
                    Uri uri = Uri.parse("android.resource://" + mContext.getPackageName() + "/" + R.raw.wumiaoshengyin);
                    Ringtone r = RingtoneManager.getRingtone(mContext.getApplicationContext(), uri);
                    r.play();

                    Notification.Builder builder = new Notification.Builder(mContext, "channel_id");
                    builder.setSmallIcon(R.mipmap.ic_launcher)
                            .setTicker("通知")
                            .setContentTitle(emMessage.getUserName())
                            .setContentText(mContentText)
                            .setWhen(System.currentTimeMillis())
                            .setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.ic_launcher))
                            .setContentIntent(intent)
                            .setAutoCancel(true);
                    manager.notify(1, builder.build());
                    return;
                }

                NotificationManager mNotificationManager = (NotificationManager) mContext.getSystemService(NOTIFICATION_SERVICE);
                Notification notification = new NotificationCompat.Builder(mContext, "default")
                        .setTicker("通知")
                        .setContentTitle(emMessage.getUserName())
                        .setContentText(mContentText)
                        .setWhen(System.currentTimeMillis())
                        .setContentIntent(intent)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setSound(Uri.parse("android.resource://" + mContext.getPackageName() + "/" + R.raw.wumiaoshengyin))
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setChannelId(mContext.getPackageName())
                        .setAutoCancel(true)
                        .build();
                mNotificationManager.notify(1, notification);
            }*/
        }
    }


    @Override
    public void onCmdMessageReceived(List<EMMessage> messages) {
        for (int i = 0; i < messages.size(); i++) {
            // 透传消息
            EMMessage cmdMessage = messages.get(i);
            EMCmdMessageBody body = (EMCmdMessageBody) cmdMessage.getBody();
            Log.i("onCmdMessageReceived", "收到 CMD 透传消息" + body.action());
        }
    }

    @Override
    public void onMessageRead(List<EMMessage> messages) {

    }

    @Override
    public void onMessageDelivered(List<EMMessage> messages) {

    }

    @Override
    public void onMessageRecalled(List<EMMessage> messages) {

    }

    @Override
    public void onMessageChanged(EMMessage message, Object change) {

    }

    private void initWidget() {
        fragments = new ArrayList<>();
        chatEmotionFragment = new ChatEmotionFragment();
        fragments.add(chatEmotionFragment);
        chatFunctionFragment = new ChatFunctionFragment();
        fragments.add(chatFunctionFragment);
        adapter = new CommonFragmentPagerAdapter(getSupportFragmentManager(), fragments);
        viewpager.setAdapter(adapter);
        viewpager.setCurrentItem(0);
        // TODO: 2018/10/31 显示昵称的位置
        mTvName.setText(mDisplayName);
        mDetector = EmotionInputDetector.with(this)
                .name(mKefuName)
                .setEmotionView(emotionLayout)
                .setViewPager(viewpager)
                .bindToContent(chatList)
                .bindToEditText(editText)
                .bindToEmotionButton(emotionButton)
                .bindToAddButton(emotionAdd)
                .bindToSendButton(emotionSend)
                .bindToVoiceButton(emotionVoice)
                .bindToVoiceText(voiceText)
                .build();

        // TODO: 2018/8/17  输入框
        GlobalOnItemClickManagerUtils globalOnItemClickListener = GlobalOnItemClickManagerUtils.getInstance(this);
        globalOnItemClickListener.attachToEditText(editText);

        chatAdapter = new ChatAdapter(this);
        layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        chatList.setLayoutManager(layoutManager);
        chatList.setAdapter(chatAdapter);
        chatList.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                switch (newState) {
                    case RecyclerView.SCROLL_STATE_IDLE:
                        /*int size = mConversation.loadMoreMsgFromDB(mMsgId, 999999999).size();
                        Log.d("onScrollStateChanged", "onScrollStateChanged: " + size);
                        //直接从本地数据库读取聊天记录
                        List<EMMessage> emMessageList = mConversation.loadMoreMsgFromDB(mMsgId, 999999999);
                        //List<EMMessage> listMessages = mConversation.loadMoreMsgFromDB(mKefuName, mConversation.getAllMessages().size());
                        //List<EMMessage> emMessageList = mConversation.getAllMessages();
                        for (EMMessage emMessage : emMessageList) {
                            EMMessage.Type type = emMessage.getType();
                            if ("VOICE".equals(type.toString())) {
                                String userName = emMessage.getFrom();
                                if (userName.equals(mKefuName)) {
                                    MessageInfo message = new MessageInfo();
                                    EMVoiceMessageBody voiceMessageBody = (EMVoiceMessageBody) emMessage.getBody();
                                    String time = DateUtil.timeStampDate(emMessage.getMsgTime());
                                    message.setTime(time);
                                    message.setFilepath(voiceMessageBody.getLocalUrl());
                                    message.setVoiceTime(voiceMessageBody.getLength());
                                    message.setType(Constants.CHAT_ITEM_TYPE_LEFT);
                                    if (StringUtils.isNotEmpty(mHeadPic)) {
                                        message.setHeader(com.qiangyu.utils.Constants.QIANGYU_PIC_URL + mHeadPic);
                                    } else {
                                        Uri uri = Uri.parse("android.resource://" + mContext.getPackageName() + "/" + R.mipmap.logo_icon);
                                        message.setHeader(uri.toString());
                                    }
                                    messageInfos.add(message);
                                    chatAdapter.add(message);
                                    chatList.scrollToPosition(chatAdapter.getCount() - 1);
                                } else {
                                    MessageInfo message = new MessageInfo();
                                    EMVoiceMessageBody voiceMessageBody = (EMVoiceMessageBody) emMessage.getBody();
                                    String time = DateUtil.timeStampDate(emMessage.getMsgTime());
                                    message.setTime(time);
                                    message.setFilepath(voiceMessageBody.getLocalUrl());
                                    message.setVoiceTime(voiceMessageBody.getLength());
                                    message.setType(Constants.CHAT_ITEM_TYPE_RIGHT);
                                    if (StringUtils.isNotEmpty(mUserHeadPic)) {
                                        message.setHeader(com.qiangyu.utils.Constants.QIANGYU_PIC_URL + mUserHeadPic);
                                    } else {
                                        Uri uri = Uri.parse("android.resource://" + mContext.getPackageName() + "/" + R.mipmap.logo_icon);
                                        message.setHeader(uri.toString());
                                    }
                                    messageInfos.add(message);
                                    chatAdapter.add(message);
                                    chatList.scrollToPosition(chatAdapter.getCount() - 1);
                                }

                            }
                            if ("TXT".equals(type.toString())) {
                                String userName = emMessage.getFrom();
                                if (userName.equals(mKefuName)) {
                                    MessageInfo message = new MessageInfo();
                                    EMTextMessageBody body = (EMTextMessageBody) emMessage.getBody();
                                    String time = DateUtil.timeStampDate(emMessage.getMsgTime());
                                    message.setTime(time);
                                    message.setContent(body.getMessage());
                                    message.setType(Constants.CHAT_ITEM_TYPE_LEFT);
                                    if (StringUtils.isNotEmpty(mHeadPic)) {
                                        message.setHeader(com.qiangyu.utils.Constants.QIANGYU_PIC_URL + mHeadPic);
                                    } else {
                                        Uri uri = Uri.parse("android.resource://" + mContext.getPackageName() + "/" + R.mipmap.logo_icon);
                                        message.setHeader(uri.toString());
                                    }
                                    messageInfos.add(message);
                                    chatAdapter.add(message);
                                    chatList.scrollToPosition(chatAdapter.getCount() - 1);
                                } else {
                                    MessageInfo message = new MessageInfo();
                                    EMTextMessageBody body = (EMTextMessageBody) emMessage.getBody();
                                    String time = DateUtil.timeStampDate(emMessage.getMsgTime());
                                    message.setTime(time);
                                    message.setContent(body.getMessage());
                                    message.setType(Constants.CHAT_ITEM_TYPE_RIGHT);
                                    if (StringUtils.isNotEmpty(mUserHeadPic)) {
                                        message.setHeader(com.qiangyu.utils.Constants.QIANGYU_PIC_URL + mUserHeadPic);
                                    } else {
                                        Uri uri = Uri.parse("android.resource://" + mContext.getPackageName() + "/" + R.mipmap.logo_icon);
                                        message.setHeader(uri.toString());
                                    }
                                    messageInfos.add(message);
                                    chatAdapter.add(message);
                                    chatList.scrollToPosition(chatAdapter.getCount() - 1);
                                }

                            }
                            if ("IMAGE".equals(type.toString())) {
                                String userName = emMessage.getFrom();
                                if (userName.equals(mKefuName)) {
                                    MessageInfo message = new MessageInfo();
                                    EMImageMessageBody emImageMessageBody = (EMImageMessageBody) emMessage.getBody();
                                    String time = DateUtil.timeStampDate(emMessage.getMsgTime());
                                    message.setTime(time);
                                    message.setImageUrl(emImageMessageBody.getThumbnailUrl());
                                    message.setType(Constants.CHAT_ITEM_TYPE_LEFT);
                                    if (StringUtils.isNotEmpty(mHeadPic)) {
                                        message.setHeader(com.qiangyu.utils.Constants.QIANGYU_PIC_URL + mHeadPic);
                                    } else {
                                        Uri uri = Uri.parse("android.resource://" + mContext.getPackageName() + "/" + R.mipmap.logo_icon);
                                        message.setHeader(uri.toString());
                                    }
                                    messageInfos.add(message);
                                    chatAdapter.add(message);
                                    chatList.scrollToPosition(chatAdapter.getCount() - 1);
                                } else {
                                    MessageInfo message = new MessageInfo();
                                    EMImageMessageBody emImageMessageBody = (EMImageMessageBody) emMessage.getBody();
                                    String time = DateUtil.timeStampDate(emMessage.getMsgTime());
                                    message.setTime(time);
                                    message.setImageUrl(emImageMessageBody.getThumbnailUrl());
                                    message.setType(Constants.CHAT_ITEM_TYPE_RIGHT);
                                    if (StringUtils.isNotEmpty(mUserHeadPic)) {
                                        message.setHeader(com.qiangyu.utils.Constants.QIANGYU_PIC_URL + mUserHeadPic);
                                    } else {
                                        Uri uri = Uri.parse("android.resource://" + mContext.getPackageName() + "/" + R.mipmap.logo_icon);
                                        message.setHeader(uri.toString());
                                    }
                                    messageInfos.add(message);
                                    chatAdapter.add(message);
                                    chatList.scrollToPosition(chatAdapter.getCount() - 1);
                                }
                            }
                        }*/
                        chatAdapter.handler.removeCallbacksAndMessages(null);
                        chatAdapter.notifyDataSetChanged();
                        break;
                    case RecyclerView.SCROLL_STATE_DRAGGING:

                        //Log.d("onScrollStateChanged", "onScrollStateChanged: 2");
                        chatAdapter.handler.removeCallbacksAndMessages(null);
                        mDetector.hideEmotionLayout(false);
                        mDetector.hideSoftInput();
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });
        chatAdapter.addItemClickListener(itemClickListener);
        LoadData();
    }

    /**
     * item点击事件
     */
    private ChatAdapter.onItemClickListener itemClickListener = new ChatAdapter.onItemClickListener() {
        @Override
        public void onHeaderClick(int position) {
            Toast.makeText(ChatActivity.this, "onHeaderClick", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onImageClick(View view, int position) {
            /*int location[] = new int[2];
            view.getLocationOnScreen(location);
            FullImageInfo fullImageInfo = new FullImageInfo();
            fullImageInfo.setLocationX(location[0]);
            fullImageInfo.setLocationY(location[1]);
            fullImageInfo.setWidth(view.getWidth());
            fullImageInfo.setHeight(view.getHeight());
            fullImageInfo.setImageUrl(messageInfos.get(position).getImageUrl());
            EventBus.getDefault().postSticky(fullImageInfo);
            startActivity(new Intent(ChatActivity.this, FullImageActivity.class));
            overridePendingTransition(0, 0);*/
            Intent intent = new Intent(mContext, PicActivity.class);
            intent.putExtra("ImageUrl", messageInfos.get(position).getImageUrl());
            startActivity(intent);
        }

        @Override
        public void onVoiceClick(final ImageView imageView, final int position) {
            if (animView != null) {
                animView.setImageResource(res);
                animView = null;
            }
            switch (messageInfos.get(position).getType()) {
                case 1:
                    animationRes = R.drawable.voice_left;
                    res = R.mipmap.icon_voice_left3;
                    break;
                case 2:
                    animationRes = R.drawable.voice_right;
                    res = R.mipmap.icon_voice_right3;
                    break;
            }
            animView = imageView;
            animView.setImageResource(animationRes);
            animationDrawable = (AnimationDrawable) imageView.getDrawable();
            animationDrawable.start();
            MediaManager.playSound(messageInfos.get(position).getFilepath(), new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    animView.setImageResource(res);
                }
            });
        }
    };

    /**
     * 获取聊天记录数据
     */
    private void LoadData() {
        messageInfos = new ArrayList<>();

        /**
         * 初始化会话对象，并且根据需要加载更多消息
         * 初始化会话对象，这里有三个参数么，
         * 第一个表示会话的当前聊天的 useranme 或者 groupid
         * 第二个是绘画类型可以为空
         * 第三个表示如果会话不存在是否创建
         */

        //SDK初始化加载的聊天记录为20条，到顶时需要去DB里获取更多
        //获取startMsgId之前的pagesize条消息，此方法获取的messages SDK会自动存入到此会话中，APP中无需再次把获取到的messages添加到会话中
        mConversation = EMClient.getInstance().chatManager().getConversation(mKefuName, null, true);
        //获取此会话的所有消息
        List<EMMessage> messages = mConversation.getAllMessages();
        int count = mConversation.getAllMessages().size();
        if (count != 0) {
            // 获取已经在列表中的最上边的一条消息id
            mMsgId = mConversation.getAllMessages().get(0).getMsgId();
            // 分页加载更多消息，需要传递已经加载的消息的最上边一条消息的id，以及需要加载的消息的条数
            mMessages = mConversation.loadMoreMsgFromDB(mMsgId, 999999999 - count);
            mMessages.addAll(messages);
        }
        //清空未读消息
        mConversation.markAllMessagesAsRead();

        mMessagesSize = messages.size();
        // 打开聊天界面获取消息内容并显示
        if (mMessagesSize > 0) {
            //直接从本地数据库读取聊天记录
            //List<EMMessage> emMessageList = mConversation.loadMoreMsgFromDB(mMsgId, 999999999);
            //List<EMMessage> listMessages = mConversation.loadMoreMsgFromDB(mKefuName, mConversation.getAllMessages().size());
            //List<EMMessage> emMessageList = mConversation.getAllMessages();
            for (EMMessage emMessage : mMessages) {
                EMMessage.Type type = emMessage.getType();
                if ("VOICE".equals(type.toString())) {
                    String userName = emMessage.getFrom();
                    if (userName.equals(mKefuName)) {
                        MessageInfo message = new MessageInfo();
                        EMVoiceMessageBody voiceMessageBody = (EMVoiceMessageBody) emMessage.getBody();
                        String time = DateUtil.timeStampDate(emMessage.getMsgTime());
                        message.setTime(time);
                        message.setFilepath(voiceMessageBody.getLocalUrl());
                        message.setVoiceTime(voiceMessageBody.getLength());
                        message.setType(Constants.CHAT_ITEM_TYPE_LEFT);
                        if (StringUtils.isNotEmpty(mHeadPic)) {
                            message.setHeader(com.qiangyu.utils.Constants.QIANGYU_PIC_URL + mHeadPic);
                        } else {
                            Uri uri = Uri.parse("android.resource://" + mContext.getPackageName() + "/" + R.mipmap.logo_icon);
                            message.setHeader(uri.toString());
                        }
                        messageInfos.add(message);
                        chatAdapter.add(message);
                        chatList.scrollToPosition(chatAdapter.getCount() - 1);
                    } else {
                        MessageInfo message = new MessageInfo();
                        EMVoiceMessageBody voiceMessageBody = (EMVoiceMessageBody) emMessage.getBody();
                        String time = DateUtil.timeStampDate(emMessage.getMsgTime());
                        message.setTime(time);
                        message.setFilepath(voiceMessageBody.getLocalUrl());
                        message.setVoiceTime(voiceMessageBody.getLength());
                        message.setType(Constants.CHAT_ITEM_TYPE_RIGHT);
                        if (StringUtils.isNotEmpty(mUserHeadPic)) {
                            message.setHeader(com.qiangyu.utils.Constants.QIANGYU_PIC_URL + mUserHeadPic);
                        } else {
                            Uri uri = Uri.parse("android.resource://" + mContext.getPackageName() + "/" + R.mipmap.logo_icon);
                            message.setHeader(uri.toString());
                        }
                        messageInfos.add(message);
                        chatAdapter.add(message);
                        chatList.scrollToPosition(chatAdapter.getCount() - 1);
                    }
                }
                if ("TXT".equals(type.toString())) {
                    String userName = emMessage.getFrom();
                    if (userName.equals(mKefuName)) {
                        MessageInfo message = new MessageInfo();
                        EMTextMessageBody body = (EMTextMessageBody) emMessage.getBody();
                        String time = DateUtil.timeStampDate(emMessage.getMsgTime());
                        message.setTime(time);
                        message.setContent(body.getMessage());
                        message.setType(Constants.CHAT_ITEM_TYPE_LEFT);
                        if (StringUtils.isNotEmpty(mHeadPic)) {
                            message.setHeader(com.qiangyu.utils.Constants.QIANGYU_PIC_URL + mHeadPic);
                        } else {
                            Uri uri = Uri.parse("android.resource://" + mContext.getPackageName() + "/" + R.mipmap.logo_icon);
                            message.setHeader(uri.toString());
                        }
                        messageInfos.add(message);
                        chatAdapter.add(message);
                        chatList.scrollToPosition(chatAdapter.getCount() - 1);
                    } else {
                        MessageInfo message = new MessageInfo();
                        EMTextMessageBody body = (EMTextMessageBody) emMessage.getBody();
                        String time = DateUtil.timeStampDate(emMessage.getMsgTime());
                        message.setTime(time);
                        message.setContent(body.getMessage());
                        message.setType(Constants.CHAT_ITEM_TYPE_RIGHT);
                        if (StringUtils.isNotEmpty(mUserHeadPic)) {
                            message.setHeader(com.qiangyu.utils.Constants.QIANGYU_PIC_URL + mUserHeadPic);
                        } else {
                            Uri uri = Uri.parse("android.resource://" + mContext.getPackageName() + "/" + R.mipmap.logo_icon);
                            message.setHeader(uri.toString());
                        }
                        messageInfos.add(message);
                        chatAdapter.add(message);
                        chatList.scrollToPosition(chatAdapter.getCount() - 1);
                    }

                }
                if ("IMAGE".equals(type.toString())) {
                    String userName = emMessage.getFrom();
                    if (userName.equals(mKefuName)) {
                        MessageInfo message = new MessageInfo();
                        EMImageMessageBody emImageMessageBody = (EMImageMessageBody) emMessage.getBody();
                        String time = DateUtil.timeStampDate(emMessage.getMsgTime());
                        message.setTime(time);
                        message.setImageUrl(emImageMessageBody.getThumbnailUrl());
                        message.setType(Constants.CHAT_ITEM_TYPE_LEFT);
                        if (StringUtils.isNotEmpty(mHeadPic)) {
                            message.setHeader(com.qiangyu.utils.Constants.QIANGYU_PIC_URL + mHeadPic);
                        } else {
                            Uri uri = Uri.parse("android.resource://" + mContext.getPackageName() + "/" + R.mipmap.logo_icon);
                            message.setHeader(uri.toString());
                        }
                        messageInfos.add(message);
                        chatAdapter.add(message);
                        chatList.scrollToPosition(chatAdapter.getCount() - 1);
                    } else {
                        MessageInfo message = new MessageInfo();
                        EMImageMessageBody emImageMessageBody = (EMImageMessageBody) emMessage.getBody();
                        String time = DateUtil.timeStampDate(emMessage.getMsgTime());
                        message.setTime(time);
                        message.setImageUrl(emImageMessageBody.getThumbnailUrl());
                        message.setType(Constants.CHAT_ITEM_TYPE_RIGHT);
                        if (StringUtils.isNotEmpty(mUserHeadPic)) {
                            message.setHeader(com.qiangyu.utils.Constants.QIANGYU_PIC_URL + mUserHeadPic);
                        } else {
                            Uri uri = Uri.parse("android.resource://" + mContext.getPackageName() + "/" + R.mipmap.logo_icon);
                            message.setHeader(uri.toString());
                        }
                        messageInfos.add(message);
                        chatAdapter.add(message);
                        chatList.scrollToPosition(chatAdapter.getCount() - 1);
                    }
                }
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void MessageEventBus(final MessageInfo messageInfo) {
        if (StringUtils.isNotEmpty(mUserHeadPic)) {
            messageInfo.setHeader(com.qiangyu.utils.Constants.QIANGYU_PIC_URL + mUserHeadPic);
        } else {
            Uri uri = Uri.parse("android.resource://" + mContext.getPackageName() + "/" + R.mipmap.logo_icon);
            messageInfo.setHeader(uri.toString());
        }

        messageInfo.setType(Constants.CHAT_ITEM_TYPE_RIGHT);
        //messageInfo.setSendState(Constants.CHAT_ITEM_SENDING);
        messageInfos.add(messageInfo);
        chatAdapter.add(messageInfo);
        chatList.scrollToPosition(chatAdapter.getCount() - 1);

        /*new Handler().postDelayed(new Runnable() {
            public void run() {
                messageInfo.setSendState(Constants.CHAT_ITEM_SEND_SUCCESS);
                chatAdapter.notifyDataSetChanged();
            }
        }, 10);*/

    }

    @OnClick(R.id.ib_more_selling_back)
    public void back() {
        finish();
    }

    @Override
    public void onBackPressed() {
        if (!mDetector.interceptBackPress()) {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //ButterKnife.unbind(this);
        EventBus.getDefault().removeStickyEvent(this);
        EventBus.getDefault().unregister(this);
    }

}
