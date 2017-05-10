package com.example.mychatbot.Adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mychatbot.Entities.Chat;
import com.example.mychatbot.Entities.Restaurant;
import com.example.mychatbot.R;
import com.example.mychatbot.Utilities.DownloadImageTask;
import com.example.mychatbot.Utilities.IntentUtils;
import com.example.mychatbot.Utilities.MyMethods;

import java.util.ArrayList;

/**
 * Created by david on 15/04/2017.
 */

public class RestaurantListAdapter extends ArrayAdapter<Restaurant> {

    private ArrayList<Restaurant> mRestaurantList;
    private Context mContext = null;
    private Activity mActivity = null;
    private int mLayout;
    private String lat;
    private String lon;

    public RestaurantListAdapter(Context context, Activity activity, int layoutId, ArrayList<Restaurant> restaurantList,String lat, String lon) {
        super(context, layoutId, restaurantList);
        mRestaurantList = restaurantList;
        mContext = context;
        mActivity = activity;
        mLayout = layoutId;
        this.lat = lat;
        this.lon = lon;
    }

    public void reset(){
        mRestaurantList.clear();
        notifyDataSetChanged();
    }


    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater vi = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = vi.inflate(mLayout, null);

        final Restaurant r = mRestaurantList.get(position);

        TextView name = (TextView) view.findViewById(R.id.name);
        name.setText(r.getName());

        TextView address = (TextView) view.findViewById(R.id.address);
        address.setText(r.getStreet()+", "+r.getNumber()+" "+r.getCity()+" ("+r.getDistance()+"km from you)");

        ImageView map = (ImageView) view.findViewById(R.id.map);

        new DownloadImageTask(map)
                .execute("http://maps.google.com/maps/api/staticmap?center="+r.getLat()+","+r.getLon()+
                        "&zoom=16&size=500x250&maptype=roadmap&sensor=true&markers=color:red%7Clabel:B%7C"
                        +r.getLat()+","+r.getLon()+"&markers=color:blue%7Clabel:A%7C"+lat+","+lon);

        map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentUtils.showDirections(mActivity,r.getLat(),r.getLon());
            }
        });

        return view;
    }
}

