package com.brunogtavares.minglr;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableString;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.brunogtavares.minglr.FirebaseData.FirebaseContract.FirebaseEntry;
import com.brunogtavares.minglr.cards.CardAdapter;
import com.brunogtavares.minglr.cards.Card;
import com.brunogtavares.minglr.matches.MatchesActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lorentzos.flingswipe.SwipeFlingAdapterView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Card mCardData[];

    private CardAdapter mAdapter;
    private int i;

    private Button mFavoritesButton, mMatchesButton, mResetButton;

    private FirebaseAuth mAuth;

    private String mUserSex, mOppositeSex, mCurrentUserId;

    private DatabaseReference mUsersDb;

    private ListView mListView;
    private List<Card> mRowItems;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mUsersDb = FirebaseDatabase.getInstance().getReference().child(FirebaseEntry.TABLE_USERS);

        mFavoritesButton = findViewById(R.id.bt_favorites);
        mResetButton = findViewById(R.id.bt_reset);
        mMatchesButton = findViewById(R.id.bt_matches);

        mAuth = FirebaseAuth.getInstance();
        mCurrentUserId = mAuth.getCurrentUser().getUid();

        checkUserSex();

        mRowItems = new ArrayList<>();
        mAdapter = new CardAdapter(this, R.layout.item, mRowItems );

        SwipeFlingAdapterView flingContainer = (SwipeFlingAdapterView) findViewById(R.id.fs_frame);

        flingContainer.setAdapter(mAdapter);
        flingContainer.setFlingListener(new SwipeFlingAdapterView.onFlingListener() {

            @Override
            public void removeFirstObjectInAdapter() {
                // this is the simplest way to delete an object from the Adapter (/AdapterView)
                Log.d("LIST", "removed object!");
                mRowItems.remove(0);
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onLeftCardExit(Object dataObject) {

                Card card = (Card) dataObject;
                String userId = card.getUserId();

                mUsersDb.child(userId).child(FirebaseEntry.COLUMN_CONNECTIONS)
                        .child(FirebaseEntry.COLUMN_NOPE).child(mCurrentUserId).setValue(true);

                Toast.makeText(MainActivity.this, FirebaseEntry.COLUMN_NOPE, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onRightCardExit(Object dataObject) {

                Card card = (Card) dataObject;
                String userId = card.getUserId();

                mUsersDb.child(userId).child(FirebaseEntry.COLUMN_CONNECTIONS)
                        .child(FirebaseEntry.COLUMN_YEP).child(mCurrentUserId).setValue(true);
                isConnectionMatch(userId);
                Toast.makeText(MainActivity.this, FirebaseEntry.COLUMN_YEP, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdapterAboutToEmpty(int itemsInAdapter) {
            }

            @Override
            public void onScroll(float scrollProgressPercent) {
//                View view = flingContainer.getSelectedView();
//                view.findViewById(R.id.item_swipe_right_indicator).setAlpha(scrollProgressPercent < 0 ? -scrollProgressPercent : 0);
//                view.findViewById(R.id.item_swipe_left_indicator).setAlpha(scrollProgressPercent > 0 ? scrollProgressPercent : 0);
            }
        });


        // Optionally add an OnItemClickListener
        flingContainer.setOnItemClickListener(new SwipeFlingAdapterView.OnItemClickListener() {
            @Override
            public void onItemClicked(int itemPosition, Object dataObject) {
                Toast.makeText(MainActivity.this, "Item clicked", Toast.LENGTH_SHORT).show();
            }
        });

        // Adding a listener to signout button
        mResetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this,
                        "Resets all the nopes", Toast.LENGTH_SHORT).show();
            }
        });

        // Adding a listener to Settings button
        mFavoritesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this,
                        "Takes to Favorites", Toast.LENGTH_SHORT).show();
            }
        });

        // Adding a listener to Matches button
        mMatchesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToMatches();
            }
        });

    }

    private void isConnectionMatch(String userId) {

        DatabaseReference currentUserConnectionsDB = mUsersDb.child(mCurrentUserId)
                .child(FirebaseEntry.COLUMN_CONNECTIONS).child(FirebaseEntry.COLUMN_YEP).child(userId);

        currentUserConnectionsDB.addListenerForSingleValueEvent(new ValueEventListener() {

            // Keeps looking for change of data in Firebase database content
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {

                    Toast.makeText(MainActivity.this, "A match has been made!", Toast.LENGTH_LONG).show();

                    // This won't create a child inside chat but it will give the key for that chat.
                    String key = FirebaseDatabase.getInstance().getReference().child(FirebaseEntry.TABLE_CHAT).push().getKey();

                    mUsersDb.child(dataSnapshot.getKey())
                            .child(FirebaseEntry.COLUMN_CONNECTIONS).child(FirebaseEntry.COLUMN_MATCHES)
                            .child(mCurrentUserId).child(FirebaseEntry.COLUMN_CHAT_ID).setValue(key);

                    mUsersDb.child(mCurrentUserId)
                            .child(FirebaseEntry.COLUMN_CONNECTIONS).child(FirebaseEntry.COLUMN_MATCHES)
                            .child(dataSnapshot.getKey()).child(FirebaseEntry.COLUMN_CHAT_ID).setValue(key);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void checkUserSex() {

        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference userDb = mUsersDb.child(user.getUid());
        userDb.addListenerForSingleValueEvent(new ValueEventListener() {

            // keeps looking for changes in the Firebase database
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    if (dataSnapshot.child(FirebaseEntry.COLUMN_SEX).getValue() != null) {
                        mUserSex = dataSnapshot.child(FirebaseEntry.COLUMN_SEX).getValue().toString();

                        // Temporary preference assignment
                        if(mUserSex.equals(FirebaseEntry.COLUMN_SEX_MALE)) {
                            mOppositeSex = FirebaseEntry.COLUMN_SEX_FEMALE;
                        }
                        if(mUserSex.equals(FirebaseEntry.COLUMN_SEX_FEMALE)) {
                            mOppositeSex = FirebaseEntry.COLUMN_SEX_MALE;
                        }
                        getOppositeSex();
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getOppositeSex() {
        mUsersDb.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                // if(dataSnapshot.child(FirebaseEntry.COLUMN_SEX).getValue() != null)

                // Check whether the database exists and also check if the user hasn't already swiped the matches left or right
                if(dataSnapshot.exists()
                        && !dataSnapshot.child(FirebaseEntry.COLUMN_CONNECTIONS).child(FirebaseEntry.COLUMN_NOPE).hasChild(mCurrentUserId)
                        && !dataSnapshot.child(FirebaseEntry.COLUMN_CONNECTIONS).child(FirebaseEntry.COLUMN_YEP).hasChild(mCurrentUserId)
                        && dataSnapshot.child(FirebaseEntry.COLUMN_SEX).getValue().toString().equals(mOppositeSex)) {

                    String profileImageUrl = "default";

                    // If user has assigned an image on registration, assign it to profileImageUrl
                    if(!dataSnapshot.child(FirebaseEntry.COLUMN_PROFILE_IMAGE_URL).getValue().equals("default")) {
                        profileImageUrl = dataSnapshot.child(FirebaseEntry.COLUMN_PROFILE_IMAGE_URL).getValue().toString();
                    }

                    Card card = new Card(dataSnapshot.getKey(),
                            dataSnapshot.child(FirebaseEntry.COLUMN_NAME).getValue().toString(),
                            profileImageUrl);

                    mRowItems.add(card);
                    mAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    // Handles the more options menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch(id) {
            case R.id.item_settings_option:
                goToSettings();
                break;
            case R.id.item_signout_option:
                logoutUser();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void logoutUser() {
        mAuth.signOut();
        Intent intent = new Intent(MainActivity.this, ChooseLoginRegistrationActivity.class);
        startActivity(intent);
        finish();
        return;
    }

    private void goToSettings() {
        Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
        startActivity(intent);
        return;
    }

    private void goToMatches() {
        Intent intent = new Intent(MainActivity.this, MatchesActivity.class);
        startActivity(intent);
        return;
    }
}
