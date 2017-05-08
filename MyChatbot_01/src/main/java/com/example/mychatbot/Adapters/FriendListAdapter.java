package com.example.mychatbot.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.mychatbot.Entities.Friend;
import com.example.mychatbot.R;

import java.util.ArrayList;

/**
 * Created by david on 14/04/2017.
 */

public class FriendListAdapter extends ArrayAdapter<Friend> {

    private ArrayList<Friend> mFriendList;
    private Context mContext = null;
    private int mLayout;

    public FriendListAdapter(Context context, int layoutId,	ArrayList<Friend> friendList) {
        super(context, layoutId, friendList);
        mFriendList = friendList;
        mContext = context;
        mLayout = layoutId;
    }

    public void reset(){
        mFriendList.clear();
        notifyDataSetChanged();
    }


    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater vi = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = vi.inflate(mLayout, null);

        Friend friend = mFriendList.get(position);

        TextView friendFbId = (TextView) view
                .findViewById(R.id.fbid);
        friendFbId.setText(friend.getFbid());

        TextView friendName = (TextView) view
                .findViewById(R.id.name);
        friendName.setText(friend.getName());

        return view;
    }
}