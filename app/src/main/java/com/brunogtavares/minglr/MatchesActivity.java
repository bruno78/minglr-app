package com.brunogtavares.minglr;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.brunogtavares.minglr.model.Match;

import java.util.ArrayList;
import java.util.List;

public class MatchesActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private MatchAdpater mMatchAdapter;

    private List<Match> mMatchesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_matches);

        mMatchesList = new ArrayList<>();

        mRecyclerView = findViewById(R.id.rv_recyclerview);
        // this will allow the scrolling run smoothly
        mRecyclerView.setNestedScrollingEnabled(false);
        mRecyclerView.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MatchesActivity.this);
        mRecyclerView.setLayoutManager(linearLayoutManager);

        mMatchAdapter = new MatchAdpater(getMatchesData(), MatchesActivity.this);
        mRecyclerView.setAdapter(mMatchAdapter);

        // Mock data for test.
        for(int i = 0; i < 100; i++) {
            mMatchesList.add(new Match("User " + i));
        }
        mMatchAdapter.notifyDataSetChanged();

    }

    private List<Match> getMatchesData() {
        return mMatchesList;
    }
}
