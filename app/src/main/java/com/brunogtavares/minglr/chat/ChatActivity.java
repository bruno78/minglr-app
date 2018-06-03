package com.brunogtavares.minglr.chat;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.brunogtavares.minglr.FirebaseData.FirebaseContract.FirebaseEntry;
import com.brunogtavares.minglr.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ChatActivity extends AppCompatActivity {

    private List<Chat> mChatList;

    private RecyclerView mRecyclerView;
    private ChatAdapater mChatAdapter;

    private EditText mChatBox;
    private Button mSendButton;

    private String mCurrentUserId, mMatchId, mChatId;

    private DatabaseReference mUserDb, mChatDb;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        mCurrentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        mUserDb = FirebaseDatabase.getInstance().getReference().child(FirebaseEntry.TABLE_NAME)
                .child(mCurrentUserId).child(FirebaseEntry.COLUMN_CONNECTIONS).child(FirebaseEntry.COLUMN_MATCHES)
                .child(mMatchId).child(FirebaseEntry.COLUMN_CHAT_ID);
        mChatDb = FirebaseDatabase.getInstance().getReference().child(FirebaseEntry.TABLE_CHAT);

        mChatBox = (EditText) findViewById(R.id.et_chat_edit_text);
        mSendButton = (Button) findViewById(R.id.bt_chat_send_button);

        mChatList = new ArrayList<>();

        // Getting info from MatchAdapter
        mMatchId = getIntent().getExtras().getString("matchId");

        getChatId();

        mRecyclerView = findViewById(R.id.rv_chat_recyclerview);
        // this will allow the scrolling run smoothly
        mRecyclerView.setNestedScrollingEnabled(false);
        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ChatActivity.this);
        mRecyclerView.setLayoutManager(linearLayoutManager);

        mChatAdapter = new ChatAdapater(getChatData(), ChatActivity.this);
        mRecyclerView.setAdapter(mChatAdapter);

        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });
    }

    private void getChatId() {
        mUserDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    mChatId = dataSnapshot.getValue().toString();
                    mChatDb = mChatDb.child(mChatId);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void sendMessage() {

        String messageText = mChatBox.getText().toString();

        if(!messageText.isEmpty()) {
            DatabaseReference newMessageDb = mChatDb.push();

            Map newMessage = new HashMap();
            newMessage.put("createdByUser", mCurrentUserId);
            newMessage.put("text", messageText);
            newMessageDb.setValue(newMessage);
        }

        // clear text after hitting send;
        mChatBox.setText(null);
    }

    private List<Chat> getChatData() { return mChatList; }
}
