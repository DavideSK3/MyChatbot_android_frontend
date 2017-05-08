package com.example.mychatbot.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.mychatbot.Entities.Chat;
import com.example.mychatbot.R;
import com.example.mychatbot.Utilities.MyMethods;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by david on 15/04/2017.
 */

public class ChatsListAdapter extends ArrayAdapter<Chat> {

    private ArrayList<Chat> mChatsList;
    private Context mContext = null;
    private int mLayout;

    public ChatsListAdapter(Context context, int layoutId,	ArrayList<Chat> chatsList) {
        super(context, layoutId, chatsList);
        mChatsList = chatsList;
        mContext = context;
        mLayout = layoutId;
    }

    public void reset(){
        mChatsList.clear();
        notifyDataSetChanged();
    }


    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater vi = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = vi.inflate(mLayout, null);

        Chat chat = mChatsList.get(position);

        TextView chatId = (TextView) view
                .findViewById(R.id.chatid);
        chatId.setText(chat.getChatId());

        TextView time = (TextView) view
                .findViewById(R.id.time);
        time.setText(MyMethods.getTimeString(chat.getTime()));

        TextView chatName = (TextView) view
                .findViewById(R.id.chatname);
        chatName.setText(chat.getChatName());

        return view;
    }
}

