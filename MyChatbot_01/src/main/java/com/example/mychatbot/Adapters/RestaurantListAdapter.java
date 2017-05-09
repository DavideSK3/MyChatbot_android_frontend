package com.example.mychatbot.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.mychatbot.Entities.Chat;
import com.example.mychatbot.Entities.Restaurant;
import com.example.mychatbot.R;
import com.example.mychatbot.Utilities.MyMethods;

import java.util.ArrayList;

/**
 * Created by david on 15/04/2017.
 */

public class RestaurantListAdapter extends ArrayAdapter<Restaurant> {

    private ArrayList<Restaurant> mRestaurantList;
    private Context mContext = null;
    private int mLayout;

    public RestaurantListAdapter(Context context, int layoutId, ArrayList<Restaurant> restaurantList) {
        super(context, layoutId, restaurantList);
        mRestaurantList = restaurantList;
        mContext = context;
        mLayout = layoutId;
    }

    public void reset(){
        mRestaurantList.clear();
        notifyDataSetChanged();
    }


    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater vi = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = vi.inflate(mLayout, null);

        Restaurant r = mRestaurantList.get(position);

        TextView name = (TextView) view
                .findViewById(R.id.name);
        name.setText(r.getName());

        TextView address = (TextView) view
                .findViewById(R.id.address);
        address.setText(r.getStreet()+", "+r.getNumber()+" "+r.getCity()+" ("+r.getDistance()+"km from you)");

        return view;
    }
}

