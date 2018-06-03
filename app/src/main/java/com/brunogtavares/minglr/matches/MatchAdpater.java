package com.brunogtavares.minglr.matches;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.brunogtavares.minglr.R;
import com.bumptech.glide.Glide;

import java.util.List;

/**
 * Created by brunogtavares on 6/2/18.
 */

public class MatchAdpater extends RecyclerView.Adapter<MatchAdpater.MatchViewHolder> {

    private List<Match> mMatchesList;
    private Context mContext;

    public MatchAdpater(List<Match> mMatchesList, Context context) {
        this.mContext = context;
        this.mMatchesList = mMatchesList;
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
        Match match = mMatchesList.get(position);
        holder.mMatchIdTextView.setText(match.getUserId());
        holder.mMatchUserNameTextView.setText(match.getName());

        if (!match.getProfileImageUrl().equals("default")) {
            Glide.with(mContext).load(match.getProfileImageUrl()).into(holder.mMatchUserPic);
        }
    }

    @Override
    public int getItemCount() {
        return mMatchesList.size();
    }


    public class MatchViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView mMatchIdTextView, mMatchUserNameTextView;
        public ImageView mMatchUserPic;

        public MatchViewHolder(View itemView) {
            super(itemView);

            mMatchIdTextView = (TextView) itemView.findViewById(R.id.tv_match_user_id);
            mMatchUserNameTextView = (TextView) itemView.findViewById(R.id.tv_match_user_name);
            mMatchUserPic = (ImageView) itemView.findViewById(R.id.iv_match_user_pic);

            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {

        }
    }
}
