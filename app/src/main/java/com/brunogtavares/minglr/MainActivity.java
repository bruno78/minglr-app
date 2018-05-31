package com.brunogtavares.minglr;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.brunogtavares.minglr.FirebaseData.FirebaseContract.FirebaseEntry;
import com.brunogtavares.minglr.model.Card;
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

    private Button mSignoutButton;

    private FirebaseAuth mAuth;

    private String mUserSex;
    private String mOppositeSex;
    private String mCurrentUserId;

    private DatabaseReference mUsersDb;

    private ListView mListView;
    private List<Card> mRowItems;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mUsersDb = FirebaseDatabase.getInstance().getReference().child(FirebaseEntry.TABLE_NAME);

        mSignoutButton = findViewById(R.id.bt_signout);

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

                mUsersDb.child(mOppositeSex).child(userId).child(FirebaseEntry.COLUMN_CONNECTIONS)
                        .child(FirebaseEntry.COLUMN_NOPE).child(mCurrentUserId).setValue(true);

                Toast.makeText(MainActivity.this, FirebaseEntry.COLUMN_NOPE, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onRightCardExit(Object dataObject) {

                Card card = (Card) dataObject;
                String userId = card.getUserId();

                mUsersDb.child(mOppositeSex).child(userId).child(FirebaseEntry.COLUMN_CONNECTIONS)
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
        mSignoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logoutUser();
            }
        });

    }

    private void isConnectionMatch(String userId) {

        DatabaseReference currentUserConnectionsDB = mUsersDb.child(mUserSex).child(mCurrentUserId)
                .child(FirebaseEntry.COLUMN_CONNECTIONS).child(FirebaseEntry.COLUMN_YEP).child(userId);

        currentUserConnectionsDB.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {

                    Toast.makeText(MainActivity.this, "A match has been made!", Toast.LENGTH_LONG).show();

                    mUsersDb.child(mOppositeSex).child(dataSnapshot.getKey())
                            .child(FirebaseEntry.COLUMN_CONNECTIONS).child(FirebaseEntry.COLUMN_MATCHES)
                            .child(mCurrentUserId).setValue(true);

                    mUsersDb.child(mUserSex).child(mCurrentUserId)
                            .child(FirebaseEntry.COLUMN_CONNECTIONS).child(FirebaseEntry.COLUMN_MATCHES)
                            .child(dataSnapshot.getKey()).setValue(true);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void checkUserSex() {

        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference maleDb = FirebaseDatabase.getInstance().getReference()
                .child(FirebaseEntry.TABLE_NAME).child(FirebaseEntry.COLUMN_SEX_MALE);

        DatabaseReference femaleDb = FirebaseDatabase.getInstance().getReference()
                .child(FirebaseEntry.TABLE_NAME).child(FirebaseEntry.COLUMN_SEX_FEMALE);

        maleDb.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if(dataSnapshot.getKey().equals(user.getUid())) {
                    mUserSex = FirebaseEntry.COLUMN_SEX_MALE;
                    mOppositeSex = FirebaseEntry.COLUMN_SEX_FEMALE;
                    getOppositeSex();
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

        femaleDb.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if(dataSnapshot.getKey().equals(user.getUid())) {
                    mUserSex = FirebaseEntry.COLUMN_SEX_FEMALE;
                    mOppositeSex = FirebaseEntry.COLUMN_SEX_MALE;
                    getOppositeSex();
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

    private void getOppositeSex() {
        DatabaseReference oppositeSexDb = FirebaseDatabase.getInstance().getReference()
                .child(FirebaseEntry.TABLE_NAME).child(mOppositeSex);

        oppositeSexDb.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                // Check whether the database exists and also check if the user hasn't already swiped the matches left or right
                if(dataSnapshot.exists()
                        && !dataSnapshot.child(FirebaseEntry.COLUMN_CONNECTIONS).child(FirebaseEntry.COLUMN_NOPE).hasChild(mCurrentUserId)
                        && !dataSnapshot.child(FirebaseEntry.COLUMN_CONNECTIONS).child(FirebaseEntry.COLUMN_YEP).hasChild(mCurrentUserId)) {

                    Card card = new Card(dataSnapshot.getKey(), dataSnapshot.child(FirebaseEntry.COLUMN_NAME).getValue().toString());
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

    private void logoutUser() {
        mAuth.signOut();
        Intent intent = new Intent(MainActivity.this, ChooseLoginRegistrationActivity.class);
        startActivity(intent);
        finish();
        return;
    }
}
