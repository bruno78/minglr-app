package com.brunogtavares.minglr;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.brunogtavares.minglr.model.Card;

import java.util.List;

public class CardAdapter extends ArrayAdapter<Card> {

    private Context mContext;

    public CardAdapter(Context context, int resourceId, List<Card> items) {
        super(context, resourceId, items);
        this.mContext = context;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        Card cardItem = getItem(position);

        if(convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item, parent, false);
        }

        TextView name = (TextView) convertView.findViewById(R.id.tv_card_user_name);
        ImageView profilePicture = (ImageView) convertView.findViewById(R.id.iv_card_image);

        name.setText(cardItem.getUserName());
        // Temporary place holder
        profilePicture.setImageResource(R.mipmap.ic_launcher);

        return convertView;
    }
}
