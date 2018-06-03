package com.brunogtavares.minglr.matches;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.brunogtavares.minglr.FirebaseData.FirebaseContract.FirebaseEntry;
import com.brunogtavares.minglr.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MatchesActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private MatchAdpater mMatchAdapter;

    private List<Match> mMatchesList;

    private String mCurrentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_matches);

        mCurrentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        mMatchesList = new ArrayList<>();

        mRecyclerView = findViewById(R.id.rv_recyclerview);
        // this will allow the scrolling run smoothly
        mRecyclerView.setNestedScrollingEnabled(false);
        mRecyclerView.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MatchesActivity.this);
        mRecyclerView.setLayoutManager(linearLayoutManager);

        mMatchAdapter = new MatchAdpater(getMatchesData(), MatchesActivity.this);
        mRecyclerView.setAdapter(mMatchAdapter);

        getUserMatchId();

    }

    private void getUserMatchId() {

        DatabaseReference matchDb = FirebaseDatabase.getInstance().getReference()
                .child(FirebaseEntry.TABLE_NAME).child(mCurrentUserId).child(FirebaseEntry.COLUMN_CONNECTIONS)
                .child(FirebaseEntry.COLUMN_MATCHES);

        matchDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for(DataSnapshot match : dataSnapshot.getChildren()) {
                        fetchMatchInformation(match.getKey());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void fetchMatchInformation(String key) {
        DatabaseReference userDb = FirebaseDatabase.getInstance().getReference()
                .child(FirebaseEntry.TABLE_NAME).child(key);

        userDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String userId = dataSnapshot.getKey();
                    String name = "";
                    String profileImageUrl = "";


                    if (dataSnapshot.child(FirebaseEntry.COLUMN_NAME).getValue() != null) {
                        name = dataSnapshot.child(FirebaseEntry.COLUMN_NAME).getValue().toString();
                    }
                    if (dataSnapshot.child(FirebaseEntry.COLUMN_PROFILE_IMAGE_URL) != null) {
                        profileImageUrl = dataSnapshot.child(FirebaseEntry.COLUMN_PROFILE_IMAGE_URL)
                                .getValue().toString();
                    }

                    mMatchesList.add(new Match(userId, name, profileImageUrl));
                    mMatchAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private List<Match> getMatchesData() {
        return mMatchesList;
    }
}
