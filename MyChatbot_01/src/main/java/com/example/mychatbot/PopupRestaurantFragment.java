package com.example.mychatbot;


import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mychatbot.Entities.Restaurant;
import com.example.mychatbot.Utilities.IntentUtils;
import com.example.mychatbot.Utilities.MyMethods;
import com.example.mychatbot.Utilities.OnSwipeTouchListener;

import java.util.ArrayList;

/**
 * Created by david on 02/05/2017.
 *
 *
 * UNUSED POPUP, KEPT HERE AS MAY BE ADDED TO CONSECUTIVE VERSIONS
 */

public class PopupRestaurantFragment extends Fragment{

    private ArrayList<Restaurant> restaurants = new ArrayList<>();
    private ArrayList<Double> distances = new ArrayList<>();
    private int currentIndex;
    private final int RANGE = 4;

    private TextView name;
    private TextView address;
    private TextView distance;
    private TextView desc;
    private ImageButton phone;
    private ImageButton email;
    private ImageButton url;

    private Activity context;

    public PopupRestaurantFragment(){
        this.currentIndex=0;
    }

    public void setRestaurant(ArrayList<Restaurant> restaurants){
        for(int i=0; i<restaurants.size();i++) {
            this.restaurants.add(restaurants.get(i));
        }
    }

    public void setDistances(ArrayList<Double> distances) {
        for(int i=0; i<distances.size();i++) {
            this.distances.add(MyMethods.round(distances.get(i), 2));
        }
    }

    public Activity getContext() {
        return context;
    }

    public void setContext(Activity context) {
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_popup, container, false);

        RelativeLayout frameLayout = (RelativeLayout) view.findViewById(R.id.fragment_layout);
        frameLayout.setBackgroundColor(Color.WHITE);

        name = (TextView) view.findViewById(R.id.name);
        address = (TextView) view.findViewById(R.id.address);
        distance = (TextView) view.findViewById(R.id.distance);
        desc = (TextView) view.findViewById(R.id.desc);
        phone = (ImageButton) view.findViewById(R.id.phone);
        email = (ImageButton) view.findViewById(R.id.email);
        url = (ImageButton) view.findViewById(R.id.url);

        setPopupContent();

        frameLayout.setOnTouchListener(new OnSwipeTouchListener(context){

            public void onSwipeRight() {
                if(currentIndex>0){
                    currentIndex--;
                    System.out.println("index "+ currentIndex);
                    setPopupContent();
                }
            }

            public void onSwipeLeft() {
                if(currentIndex<RANGE){
                    currentIndex++;
                    System.out.println("index "+ currentIndex);
                    setPopupContent();
                }
            }

            public void onSwipeTop() {
            }
            public void onSwipeBottom() {
            }
        });

        phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(restaurants.get(currentIndex).getPhone().equals("")){
                    Toast.makeText(context, "Phone number unavailable for this restaurant", Toast.LENGTH_LONG).show();
                } else {
                    IntentUtils.dial(context, restaurants.get(currentIndex).getPhone());
                }
            }
        });

        url.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(restaurants.get(currentIndex).getUrl().equals("")){
                    Toast.makeText(context, "Website unavailable for this restaurant", Toast.LENGTH_LONG).show();
                } else {
                    IntentUtils.invokeWebBrowser(context, restaurants.get(currentIndex).getUrl());
                }
            }
        });

        email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(restaurants.get(currentIndex).getEmail().equals("")){
                    Toast.makeText(context, "Email address unavailable for this restaurant", Toast.LENGTH_LONG).show();
                } else {
                    IntentUtils.sendEmail(context, restaurants.get(currentIndex).getEmail());
                }
            }
        });
        return view;
    }

    private void setPopupContent(){
        System.out.println("index "+ currentIndex);
        name.setText(restaurants.get(currentIndex).getName());
        address.setText(restaurants.get(currentIndex).getStreet()+", "+restaurants.get(currentIndex).getNumber()+" "+restaurants.get(currentIndex).getCity());
        distance.setText(" ("+distances.get(currentIndex)+" km from you)");
        desc.setText(restaurants.get(currentIndex).getDesc());
    }
}
