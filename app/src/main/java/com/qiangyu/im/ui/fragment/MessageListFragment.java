package com.qiangyu.im.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.alibaba.fastjson.JSON;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.qiangyu.R;
import com.qiangyu.im.adapter.GetAllMessagesAdapter;
import com.qiangyu.im.pojo.Message;
import com.qiangyu.im.ui.activity.ChatActivity;
import com.qiangyu.my.bean.Designer;
import com.qiangyu.my.bean.JsonDesignerData;
import com.qiangyu.utils.MD5Util;
import com.qiangyu.utils.SPUtils;
import com.qiangyu.utils.ToastUtil;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

public class MessageListFragment extends BaseFragment {

    private ListView mListView;
    private Message mMessage;
    private List<Message> mMessageList;
    private String yonghu;
    private List<Long> shijian;
    private String mCustomer;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected View initView() {
        mCustomer = getActivity().getIntent().getStringExtra("Customer");
        SPUtils.getInstance().put("Customer", mCustomer);
        mMessageList = new ArrayList();
        shijian = new ArrayList();
        View v = View.inflate(mContext, R.layout.im_list_view_more_selling, null);
        mListView = v.findViewById(R.id.lv_more_selling);

        Map<String, EMConversation> conversations = EMClient.getInstance().chatManager().getAllConversations();
        for (Map.Entry entry : conversations.entrySet()) {
            //mMessage = new Message();
            EMConversation conversation = EMClient.getInstance().chatManager().getConversation(entry.getKey().toString(), EMConversation.EMConversationType.Chat, false);
            EMMessage emMessage = conversation.getLastMessage();
            if (emMessage != null) {
                String key = entry.getKey().toString();
                /*int unreadMsgCount = conversation.getUnreadMsgCount();
                long msgTime = emMessage.getMsgTime();
                mMessage.setName(key);
                mMessage.setUnreadMsg(unreadMsgCount);
                mMessage.setTime(msgTime);
                mMessageList.add(mMessage);*/
                if (!StringUtils.isNotEmpty(yonghu)) {
                    yonghu = key;
                } else {
                    yonghu = yonghu + "," + key;
                }
            }
        }
        if (StringUtils.isNotEmpty(mCustomer)) {
            //Log.d("mCustomer", "有值: " + mCustomer + " -- 用户: " + yonghu);
            if (StringUtils.isNotEmpty(yonghu)) {
                getDataFromGetHeaderPic(yonghu, "0");//type=0获取会员
            }
        } else {
            //Log.d("mCustomer", "无值: " + mCustomer + " -- 用户: " + yonghu);
            if (StringUtils.isNotEmpty(yonghu)) {
                getDataFromGetHeaderPic(yonghu, "1");//type=1获取客服
            }
        }

        return v;

    }

    private void initListener() {
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String name = mMessageList.get(i).getName();
                String headPic = mMessageList.get(i).getHeadPic();
                String displayName = mMessageList.get(i).getDisplayName();
                //未读消息清零
                EMConversation conversation = EMClient.getInstance().chatManager().getConversation(name);
                conversation.markAllMessagesAsRead();
                Intent intent = new Intent(mContext, ChatActivity.class);
                intent.putExtra("kefu", name);
                intent.putExtra("headPic", headPic);
                intent.putExtra("displayName", displayName);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void setUpView() {

    }

    //自定义比较器：按时间来排序
    static class CalendarComparator implements Comparator {
        public int compare(Object object1, Object object2) {// 实现接口中的方法
            Message message1 = (Message) object1;
            Message message2 = (Message) object2;
            return new Long(message2.getTime()).compareTo(new Long(message1.getTime()));
        }
    }

    //=================================================获取客服/用户头像=================================================\\
    //(string name, string nonceStr, string timeStamp, string sign)
    /// <param name="type">类型 0会员（会员、施工队） 1用户（设计师）</param>
    void getDataFromGetHeaderPic(final String name, String type) {
        SortedMap<Object, Object> parameters = new TreeMap<>();
        String randomStr = MD5Util.getRandomStr();
        String timeStamp = MD5Util.getTimeStamp();
        String characterEncoding = "UTF-8";
        parameters.put("name", name);
        parameters.put("type", type);
        parameters.put("nonceStr", randomStr);
        parameters.put("timeStamp", timeStamp);
        String mySign = MD5Util.createSign(characterEncoding, parameters);

        OkHttpUtils
                .post()
                .url(com.qiangyu.utils.Constants.QIANGYU_URL + "GetHeaderPic")
                .addParams("name", name)
                .addParams("type", type)
                .addParams("nonceStr", randomStr)
                .addParams("timeStamp", timeStamp)
                .addParams("sign", mySign)
                .build()
                .readTimeOut(10000)
                .writeTimeOut(10000)
                .connTimeOut(10000)
                /**
                 * 请求失败的时候回调
                 */
                .execute(new StringCallback() {
                    @Override
                    public void onError(okhttp3.Call call, Exception e, int id) {
                        Log.d("getDataFromNet", "请求失败 === >" + e.getMessage());
                        ToastUtil.toastCenter(mContext, "网络连接失败,请稍后再试...");
                    }

                    /**
                     * 当联网成功时回调
                     * @param response
                     * @param id
                     */
                    @Override
                    public void onResponse(String response, int id) {
                        //解析数据
                        Log.d("getDataFromNet", " 未改过 === >" + response);
                        response = response.replace("\\", "");
                        response = response.replace("\"[", "[");
                        response = response.replace("]\"", "]");
                        Log.d("getDataFromNet", " 消息列表获取客服 === >" + response);
                        GetHeaderPic(response);
                    }
                });

    }

    private void GetHeaderPic(String json) {
        Log.d("GetHeaderPic", "GetHeaderPic: " + json);
        String code = JSON.parseObject(json).get("Code").toString().trim();
        if ("OK".equals(code)) {
            JsonDesignerData jsonDesignerData = JSON.parseObject(json, JsonDesignerData.class);
            List<Designer> designerList = jsonDesignerData.getResult().getDesigner();

            Map<String, EMConversation> conversations = EMClient.getInstance().chatManager().getAllConversations();
            for (Map.Entry entry : conversations.entrySet()) {
                mMessage = new Message();
                EMConversation conversation = EMClient.getInstance().chatManager().getConversation(entry.getKey().toString(), EMConversation.EMConversationType.Chat, false);
                EMMessage emMessage = conversation.getLastMessage();
                if (emMessage != null) {
                    int unreadMsgCount = conversation.getUnreadMsgCount();
                    String key = entry.getKey().toString();
                    long msgTime = emMessage.getMsgTime();
                    for (Designer designer : designerList) {
                        if (designer.getCode().equals(key)) {
                            mMessage.setHeadPic(designer.getHeadPic());
                            mMessage.setDisplayName(designer.getDisplayName());
                        }
                    }
                    mMessage.setName(key);
                    mMessage.setUnreadMsg(unreadMsgCount);
                    mMessage.setTime(msgTime);
                    mMessageList.add(mMessage);
                    shijian.add(msgTime);
                    if (!StringUtils.isNotEmpty(yonghu)) {
                        yonghu = key;
                    } else {
                        yonghu = yonghu + "," + key;
                    }
                }
            }
            Collections.sort(mMessageList, new CalendarComparator());
            mListView.setAdapter(new GetAllMessagesAdapter(mContext, mMessageList));
            initListener();
        }

    }

}
