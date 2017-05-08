package com.example.mychatbot.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.mychatbot.Entities.Message;
import com.example.mychatbot.R;
import com.example.mychatbot.Utilities.MyMethods;
import com.example.mychatbot.Utilities.SharedPrefManager;

import java.sql.Timestamp;
import java.util.ArrayList;

/**
 * Created by david on 15/04/2017.
 */

public class MessageListAdapter extends ArrayAdapter<Message> {

    private ArrayList<Message> messageList;
    private Context mContext = null;
    private int mLayout;

    public MessageListAdapter(Context context, int layoutId,	ArrayList<Message> messageList) {
        super(context, layoutId, messageList);
        this.messageList = messageList;
        mContext = context;
        mLayout = layoutId;
    }

    public void reset(){
        messageList.clear();
        notifyDataSetChanged();
    }


    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater vi = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = vi.inflate(mLayout, null);

        Message message = messageList.get(position);

        if(message.getSender().equals(SharedPrefManager.getInstance(mContext).getFacebookId())) {
            TextView time = (TextView) view
                    .findViewById(R.id.timeR);
            String msgtime = message.getTime();
            time.setText(MyMethods.getTimeString(msgtime));

            TextView content = (TextView) view
                    .findViewById(R.id.contentR);
            content.setText(message.getContent());
        } else {
            TextView time = (TextView) view
                    .findViewById(R.id.timeL);
            String msgtime = message.getTime();
            time.setText(MyMethods.getTimeString(msgtime));

            TextView content = (TextView) view
                    .findViewById(R.id.contentL);
            content.setText(message.getContent());
        }

        return view;
    }
}
