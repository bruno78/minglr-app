package com.brunogtavares.minglr.chat;


import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.brunogtavares.minglr.R;
import com.bumptech.glide.Glide;

import java.util.List;

/**
 * Created by brunogtavares on 6/2/18.
 */

public class ChatAdapater extends RecyclerView.Adapter<ChatAdapater.ChatViewHolder> {

    private List<Chat> mChatList;
    private Context mContext;

    public ChatAdapater(List<Chat> chatList, Context context){
        this.mChatList = chatList;
        this.mContext = context;
    };

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.chat_list_item, null, false);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        itemView.setLayoutParams(lp);

        return new ChatViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {

        Chat chatMessage = mChatList.get(position);

        // Handling image view, if there's no image, hide the image view
        boolean isPhoto = chatMessage.getImageUrl() != null;
        if (isPhoto) {
            // hide the text view
            holder.mChatMessage.setVisibility(View.GONE);
            holder.mChatImage.setVisibility(View.VISIBLE);
            Glide.with(holder.mChatImage.getContext())
                    .load(chatMessage.getImageUrl())
                    .into(holder.mChatImage);
        }
        else {
            holder.mChatMessage.setVisibility(View.VISIBLE);
            holder.mChatImage.setVisibility(View.GONE);

            holder.mChatMessage.setText(chatMessage.getMessage());

            // Direction of the text
            if (chatMessage.getCurrentUser()) {
                holder.mChatMessage.setGravity(Gravity.END);
                holder.mChatMessage.setTextColor(Color.parseColor("#404040"));
                holder.mChatMessageContainer.setBackgroundColor(Color.parseColor("#F4F4F4"));
            }
            else {
                holder.mChatMessage.setGravity(Gravity.START);
                holder.mChatMessage.setTextColor(Color.parseColor("#FFFFFF"));
                holder.mChatMessageContainer.setBackgroundColor(Color.parseColor("#2DB4C8"));
            }
        }

    }

    @Override
    public int getItemCount() {
        return mChatList.size();
    }

    public class ChatViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView mChatMessage;
        public ImageView mChatImage;
        public LinearLayout mChatMessageContainer;

        public ChatViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);

            mChatMessage = itemView.findViewById(R.id.tv_chat_message);
            mChatImage = itemView.findViewById(R.id.iv_chat_image);
            mChatMessageContainer = itemView.findViewById(R.id.ll_text_container);
        }

        @Override
        public void onClick(View view) {

        }
    }
}
