package com.brunogtavares.minglr.chat;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.brunogtavares.minglr.ChooseLoginRegistrationActivity;
import com.brunogtavares.minglr.FirebaseData.FirebaseContract.FirebaseEntry;
import com.brunogtavares.minglr.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ChatActivity extends AppCompatActivity {

    public static final String MATCH_KEY = "matchId" ;
    private static final int RC_PHOTO_PICKER = 100;
    private static final String CHAT_PHOTOS_FOLDER = "chat_photos";
    private static final int DEFAULT_MSG_LENGTH_LIMIT = 300;

    private List<Chat> mChatList;

    private RecyclerView mRecyclerView;
    private ChatAdapater mChatAdapter;

    private EditText mChatBox;
    private Button mSendButton;
    private ImageButton mPhotoPickerButton;
    private ProgressBar mProgressBar;

    private String mCurrentUserId, mMatchId, mChatId;

    private DatabaseReference mUserDb, mChatDb;
    private FirebaseStorage mFirebaseStorage;
    private StorageReference mChatPhotosStorageReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // Getting info from MatchAdapter
        mMatchId = getIntent().getExtras().getString(MATCH_KEY);

        // Firebase
        mCurrentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        mUserDb = FirebaseDatabase.getInstance().getReference().child(FirebaseEntry.TABLE_USERS)
                .child(mCurrentUserId).child(FirebaseEntry.COLUMN_CONNECTIONS).child(FirebaseEntry.COLUMN_MATCHES)
                .child(mMatchId).child(FirebaseEntry.COLUMN_CHAT_ID);
        mChatDb = FirebaseDatabase.getInstance().getReference().child(FirebaseEntry.TABLE_CHAT);

        // Image upload storage
        mFirebaseStorage = FirebaseStorage.getInstance();
        mChatPhotosStorageReference = mFirebaseStorage.getReference().child(CHAT_PHOTOS_FOLDER);


        mChatBox = (EditText) findViewById(R.id.et_chat_edit_text);
        mSendButton = (Button) findViewById(R.id.bt_chat_send_button);
        mPhotoPickerButton = (ImageButton) findViewById(R.id.ib_photo_picker);
        mProgressBar = (ProgressBar) findViewById(R.id.pb_chat_progressBar);

        // Initialize progress bar
        mProgressBar.setVisibility(ProgressBar.INVISIBLE);

        mChatList = new ArrayList<>();

        getChatId();

        mRecyclerView = findViewById(R.id.rv_chat_recyclerview);
        // this will allow the scrolling run smoothly
        mRecyclerView.setNestedScrollingEnabled(false);
        mRecyclerView.setHasFixedSize(false);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ChatActivity.this);
        mRecyclerView.setLayoutManager(linearLayoutManager);

        mChatAdapter = new ChatAdapater(getChatData(), ChatActivity.this);
        mRecyclerView.setAdapter(mChatAdapter);

        // Set limit to chatBox input
        mChatBox.setFilters(new InputFilter[]{new InputFilter.LengthFilter(DEFAULT_MSG_LENGTH_LIMIT)});

        // Only enable Send button when there's text to send
        mChatBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                if (charSequence.toString().trim().length() > 0) {
                    mSendButton.setEnabled(true);
                }
                else {
                    mSendButton.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        // Send text button
        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });

        // Send image button
        mPhotoPickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Fire an intent to show an image picker
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                startActivityForResult(Intent.createChooser(intent,
                        "Complete action using"), RC_PHOTO_PICKER);
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
                    getChatMessages();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getChatMessages() {
        mChatDb.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (dataSnapshot.exists()) {
                    String message = null;
                    String createByUser = null;
                    String image = null;

                    if (dataSnapshot.child(FirebaseEntry.COLUMN_CHAT_TEXT).getValue() != null) {
                        message = dataSnapshot.child(FirebaseEntry.COLUMN_CHAT_TEXT).getValue().toString();
                    }
                    if (dataSnapshot.child(FirebaseEntry.COLUMN_CREATED_BY_USER).getValue() != null) {
                        createByUser = dataSnapshot.child(FirebaseEntry.COLUMN_CREATED_BY_USER).getValue().toString();
                    }
                    if (dataSnapshot.child(FirebaseEntry.COLUMN_IMAGE_URL).getValue() != null) {
                        image = dataSnapshot.child(FirebaseEntry.COLUMN_IMAGE_URL).getValue().toString();
                    }
                    if (message != null && createByUser != null) {
                        Boolean isCurrentUser = createByUser.equals(mCurrentUserId);
                        Chat newMessage = new Chat(message, isCurrentUser);
                        mChatList.add(newMessage);
                        mChatAdapter.notifyDataSetChanged();
                    }
                    if (image != null && createByUser != null) {
                        Boolean isCurrentUser = createByUser.equals(mCurrentUserId);
                        Chat newMessage = new Chat(null, isCurrentUser, image);
                        mChatList.add(newMessage);
                        mChatAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RC_PHOTO_PICKER && resultCode == RESULT_OK) {
            final Uri selectedImageUri = data.getData();
            Log.d("ChatActivity", selectedImageUri.toString());

            // Get a reference to store file at chat_photos/<FILENAME>
            final StorageReference photoRef =
                    mChatPhotosStorageReference.child(selectedImageUri.getLastPathSegment());

            // Upload file to Firebase Storage
            photoRef.putFile(selectedImageUri).continueWithTask(
                    new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {

                        @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if (!task.isSuccessful()) {
                                throw task.getException();
                            }
                            return photoRef.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        String imageUrl = downloadUri.toString();

                        Map newImageMessage = new HashMap();
                        newImageMessage.put(FirebaseEntry.COLUMN_CREATED_BY_USER, mCurrentUserId);
                        newImageMessage.put(FirebaseEntry.COLUMN_IMAGE_URL, imageUrl);
                        mChatDb.push().setValue(newImageMessage);
                    }
                    else {
                        Toast.makeText(ChatActivity.this, "Upload failed: " + task.getException(),
                                Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }
    }

    private void sendMessage() {

        String messageText = mChatBox.getText().toString();

        if(!messageText.isEmpty()) {
            DatabaseReference newMessageDb = mChatDb.push();

            Map newMessage = new HashMap();
            newMessage.put(FirebaseEntry.COLUMN_CREATED_BY_USER, mCurrentUserId);
            newMessage.put(FirebaseEntry.COLUMN_CHAT_TEXT, messageText);
            newMessageDb.setValue(newMessage);
        }

        // clear text after hitting send;
        mChatBox.setText(null);
    }

    private List<Chat> getChatData() { return mChatList; }
}
