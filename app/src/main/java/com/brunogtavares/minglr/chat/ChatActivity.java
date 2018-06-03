package com.brunogtavares.minglr.chat;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.brunogtavares.minglr.R;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;


public class ChatActivity extends AppCompatActivity {

    private List<Chat> mChatList;

    private RecyclerView mRecyclerView;
    private ChatAdapater mChatAdapter;

    private String mCurrentUserId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        mCurrentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        mChatList = new ArrayList<>();

        mRecyclerView = findViewById(R.id.rv_chat_recyclerview);
        // this will allow the scrolling run smoothly
        mRecyclerView.setNestedScrollingEnabled(false);
        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ChatActivity.this);
        mRecyclerView.setLayoutManager(linearLayoutManager);

        mChatAdapter = new ChatAdapater(getChatData(), ChatActivity.this);
        mRecyclerView.setAdapter(mChatAdapter);
    }

    private List<Chat> getChatData() { return mChatList; }
}
