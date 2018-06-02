package com.brunogtavares.minglr;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.brunogtavares.minglr.model.Match;

import java.util.List;

/**
 * Created by brunogtavares on 6/2/18.
 */

public class MatchAdpater extends RecyclerView.Adapter<MatchAdpater.MatchViewHolder> {

    private List<Match> mMatchestList;
    private Context mContext;

    public MatchAdpater(List<Match> mMatchestList, Context context) {
        this.mContext = context;
        this.mMatchestList = mMatchestList;
    }

    @NonNull
    @Override
    public MatchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.match_list_item, null, false);

        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        itemView.setLayoutParams(lp);

        return new MatchViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MatchViewHolder holder, int position) {
        holder.mMatchIdTextView.setText(mMatchestList.get(position).getUserId());
    }

    @Override
    public int getItemCount() {
        return mMatchestList.size();
    }


    public class MatchViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView mMatchIdTextView;

        public MatchViewHolder(View itemView) {
            super(itemView);
            mMatchIdTextView = (TextView) itemView.findViewById(R.id.tv_match_user_id);
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {

        }
    }
}
