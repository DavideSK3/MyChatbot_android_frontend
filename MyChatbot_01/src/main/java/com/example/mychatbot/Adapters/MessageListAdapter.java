package com.example.mychatbot.Adapters;

import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.mychatbot.MovieActivity;
import com.example.mychatbot.Entities.Message;
import com.example.mychatbot.R;
import com.example.mychatbot.RestaurantActivity;
import com.example.mychatbot.ResultsActivity;
import com.example.mychatbot.Utilities.DownloadImageTask;
import com.example.mychatbot.Utilities.MyMethods;
import com.example.mychatbot.Utilities.SharedPrefManager;

import java.util.ArrayList;

/**
 * Created by david on 15/04/2017.
 */

public class MessageListAdapter extends ArrayAdapter<Message> {

    private ArrayList<Message> messageList;
    private Context mContext = null;
    private int mLayout;
    private String chatid= "";
    private String chatname= "";
    private TextView content;

    public MessageListAdapter(Context context, int layoutId, ArrayList<Message> messageList, String chatid, String chatname) {
        super(context, layoutId, messageList);
        this.messageList = messageList;
        mContext = context;
        mLayout = layoutId;
        this.chatid = chatid;
        this.chatname = chatname;
    }

    public void reset(){
        messageList.clear();
        notifyDataSetChanged();
    }


    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater vi = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = vi.inflate(mLayout, null);

        final Message message = messageList.get(position);

        System.out.println(" message intent  "+message.getContent()+"  "+message.getIntent());

        if(message.getSender().equals(SharedPrefManager.getInstance(mContext).getFacebookId())) {

            TextView time = (TextView) view
                    .findViewById(R.id.timeR);
            String msgtime = message.getTime();
            time.setText(MyMethods.getTimeString(msgtime));
            content = (TextView) view
                    .findViewById(R.id.contentR);
            content.setText(message.getContent());
            LinearLayout messagecontainer = (LinearLayout) view.findViewById(R.id.message_backgroundR);
            messagecontainer.setBackgroundResource(R.drawable.lightgreen_message);
            ImageView image = (ImageView) view.findViewById(R.id.imageR);

            if(!message.getIntent().equals("null") && !message.getIntent().equals("")) {
                ImageButton button = (ImageButton) view
                        .findViewById(R.id.chatbotL);
                button.setVisibility(View.VISIBLE);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent openResultsActivityIntent = new Intent(mContext,
                                ResultsActivity.class);
                        String intent = message.getIntent();
                        openResultsActivityIntent.putExtra(mContext.getPackageName() + ".intent",intent);
                        openResultsActivityIntent.putExtra(mContext.getPackageName() + ".chatid",chatid);
                        openResultsActivityIntent.putExtra(mContext.getPackageName() + ".chatname",chatname);
                        mContext.startActivity(openResultsActivityIntent);
                    }
                });
            }
            if(message.getRestaurant()!=null &&  !message.getRestaurant().equals("0")) {
                content.setText(Html.fromHtml(message.getContent()), TextView.BufferType.SPANNABLE);
                image.setVisibility(View.VISIBLE);
                new DownloadImageTask(image)
                        .execute(message.getImage());
                content.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //content.setText(Html.fromHtml(message.getContent().replace("0645AD","0B0080")), TextView.BufferType.SPANNABLE);
                        System.out.println("restaurant intent found clicking message");
                        Intent openRestaurantActivityIntent = new Intent(mContext,
                                RestaurantActivity.class);
                        String restaurant = message.getRestaurant();
                        openRestaurantActivityIntent.putExtra(mContext.getPackageName() + ".restaurantid", restaurant);
                        mContext.startActivity(openRestaurantActivityIntent);
                    }
                });
            }
            if(message.getCinema()!=null && !message.getCinema().equals("0")) {
                content.setText(Html.fromHtml(message.getContent()), TextView.BufferType.SPANNABLE);
                image.setVisibility(View.VISIBLE);
                new DownloadImageTask(image)
                        .execute(message.getImage());
                content.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //content.setText(Html.fromHtml(message.getContent().replace("0645AD","0B0080")), TextView.BufferType.SPANNABLE);
                        System.out.println("cinema intent found clicking message");
                        Intent openMovieActivityIntent = new Intent(mContext,
                                MovieActivity.class);
                        String cinema = message.getCinema();
                        String messaggio = message.getContent();
                        openMovieActivityIntent.putExtra(mContext.getPackageName() + ".movieid",cinema);
                        openMovieActivityIntent.putExtra(mContext.getPackageName() + ".day",messaggio.substring(messaggio.length()-10));
                        mContext.startActivity(openMovieActivityIntent);
                    }
                });
            }


        } else {


            TextView time = (TextView) view
                    .findViewById(R.id.timeL);
            String msgtime = message.getTime();
            time.setText(MyMethods.getTimeString(msgtime));
            content = (TextView) view
                    .findViewById(R.id.contentL);
            content.setText(message.getContent());

            LinearLayout messagecontainer = (LinearLayout) view.findViewById(R.id.message_backgroundL);
            messagecontainer.setBackgroundResource(R.drawable.white_message);
            ImageView image = (ImageView) view.findViewById(R.id.imageL);

            if(!message.getIntent().equals("null") &&  !message.getIntent().equals("")) {
                ImageButton button = (ImageButton) view
                        .findViewById(R.id.chatbotR);
                button.setVisibility(View.VISIBLE);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent openResultsActivityIntent = new Intent(mContext,
                                ResultsActivity.class);
                        String intent = message.getIntent();
                        openResultsActivityIntent.putExtra(mContext.getPackageName() + ".intent",intent);
                        openResultsActivityIntent.putExtra(mContext.getPackageName() + ".chatid",chatid);
                        openResultsActivityIntent.putExtra(mContext.getPackageName() + ".chatname",chatname);
                        mContext.startActivity(openResultsActivityIntent);
                    }
                });
            }
            if(message.getRestaurant()!=null && message.getRestaurant()!=("0")) {
                content.setText(Html.fromHtml(message.getContent()), TextView.BufferType.SPANNABLE);
                image.setVisibility(View.VISIBLE);
                new DownloadImageTask(image)
                        .execute(message.getImage());
                content.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //content.setText(Html.fromHtml(message.getContent().replace("0645AD","0B0080")), TextView.BufferType.SPANNABLE);
                        System.out.println("restaurant intent found clicking message");
                        Intent openRestaurantActivityIntent = new Intent(mContext,
                                RestaurantActivity.class);
                        String restaurant = message.getRestaurant();
                        openRestaurantActivityIntent.putExtra(mContext.getPackageName() + ".restaurantid", restaurant);
                        mContext.startActivity(openRestaurantActivityIntent);
                    }
                });
            }
            if(message.getCinema()!=null && message.getCinema()!=("0")) {
                content.setText(Html.fromHtml(message.getContent()), TextView.BufferType.SPANNABLE);
                image.setVisibility(View.VISIBLE);
                new DownloadImageTask(image)
                        .execute(message.getImage());
                content.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //content.setText(Html.fromHtml(message.getContent().replace("0645AD","0B0080")), TextView.BufferType.SPANNABLE);
                        System.out.println("cinema intent found clicking message");
                        Intent openMovieActivityIntent = new Intent(mContext,
                                MovieActivity.class);
                        String cinema = message.getCinema();
                        String messaggio = message.getContent();
                        openMovieActivityIntent.putExtra(mContext.getPackageName() + ".movieid",cinema);
                        openMovieActivityIntent.putExtra(mContext.getPackageName() + ".day",messaggio.substring(messaggio.length()-10));
                        mContext.startActivity(openMovieActivityIntent);
                    }
                });
            }
        }

        return view;
    }
}
