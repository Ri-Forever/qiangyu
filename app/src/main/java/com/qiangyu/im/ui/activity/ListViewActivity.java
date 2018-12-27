package com.qiangyu.im.ui.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.qiangyu.R;

import java.util.Map;

public class ListViewActivity extends Activity {

    String[] ctype = new String[2];
    private ListView mListView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.im_activity_listview);
        initView();
    }

    private void initView() {
        mListView = findViewById(R.id.list_view);
        ctype[0] = "qwert";
        ctype[1] = "测试";
        Map<String, EMConversation> conversations = EMClient.getInstance().chatManager().getAllConversations();
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, ctype);
        mListView.setAdapter(adapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(ListViewActivity.this,"选择的是 -- >" + view.toString(),Toast.LENGTH_SHORT).show();
            }
        });
    }
}
