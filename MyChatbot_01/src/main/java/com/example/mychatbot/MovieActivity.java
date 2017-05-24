package com.example.mychatbot;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.mychatbot.Adapters.ScheduleListAdapter;
import com.example.mychatbot.Entities.Movie;
import com.example.mychatbot.Entities.Schedule;
import com.example.mychatbot.Utilities.DownloadImageTask;
import com.example.mychatbot.Utilities.EndPoints;
import com.example.mychatbot.Utilities.MyMethods;
import com.example.mychatbot.Utilities.MyVolley;
import com.example.mychatbot.Utilities.OnSwipeTouchListener;
import com.example.mychatbot.Utilities.SharedPrefManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by david on 08/05/2017.
 *
 * User can see screenings for the selected movie in the specified (or current by default) date.
 * User can swipe left/right to see previous/next day's screenings
 * User can share screening by swiping up
 */

public class MovieActivity extends AppCompatActivity {

    private TextView day_view;
    private TextView name;
    private TextView length;
    private TextView category;
    private TextView desc;
    private ImageView image;
    private ListView list;
    private RelativeLayout layout_activity;

    private Movie movie;
    private ArrayList<Schedule> scheduleList;
    private ArrayList<Schedule> todayScheduleList;
    private ProgressDialog progressDialog;
    private Activity context;
    private ScheduleListAdapter slAdapter;

    private String movieid;
    private String chatid;
    private String chatname;
    private String day;

    private int currentIndex = 0;
    private final int RANGE = 6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie);

        day_view = (TextView) findViewById(R.id.day);
        name = (TextView) findViewById(R.id.name);
        length = (TextView) findViewById(R.id.length);
        category = (TextView) findViewById(R.id.category);
        desc = (TextView) findViewById(R.id.desc);
        image = (ImageView) findViewById(R.id.image);
        list = (ListView) findViewById(R.id.list);
        layout_activity = (RelativeLayout) findViewById(R.id.layout_activity);

        Intent intent = getIntent();
        movieid = intent.getStringExtra(getPackageName() + ".movieid");
        chatid = intent.getStringExtra(getPackageName() + ".chatid");
        chatname = intent.getStringExtra(getPackageName() + ".chatname");
        day = intent.getStringExtra(getPackageName()+".day");
        context = this;

        day_view.setText(day);
        scheduleList = new ArrayList<>();
        todayScheduleList = new ArrayList<>();

        getMovie();
        fetchScheduleList();

        //adds a swipe listener to the activity to manage scrolling between dates and sharing a movie with friend
        layout_activity.setOnTouchListener(new OnSwipeTouchListener(context){

            public void onSwipeRight() {
                if(currentIndex>0){
                    currentIndex--;
                    todayScheduleList.clear();
                    slAdapter.reset();
                    System.out.println("index "+ currentIndex);
                    day = MyMethods.getYesterday(day);
                    System.out.println("dayupdate "+day);
                    day_view.setText(day);
                    loadScheduleList();
                }
            }

            public void onSwipeLeft() {
                if(currentIndex<RANGE){
                    currentIndex++;
                    todayScheduleList.clear();
                    slAdapter.reset();
                    System.out.println("index "+ currentIndex);
                    day = MyMethods.getTomorrow(day);
                    System.out.println("dayupdate "+day);
                    day_view.setText(day);
                    loadScheduleList();
                }
            }

            public void onSwipeTop() {
                if (chatid!=null){
                    sendMessage();
                }
            }
            public void onSwipeBottom() {
            }
        });
    }

    //gets info about current movie
    private void getMovie() {
        System.out.println("Querying for restaurant: "+movieid);

        StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.POST, EndPoints.URL_GET_SINGLE_MOVIE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);
                            System.out.println(obj);
                            JSONArray arr= obj.getJSONArray("movies");
                            JSONObject m = arr.getJSONObject(0);
                            System.out.println("Movie: "+m);
                            movie= new Movie(m.getString("id"),m.getString("name"),
                                    m.getString("description"),m.getString("length"),
                                    m.getString("category"),m.getString("image"));
                            setMovieContent();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MovieActivity.this, "Turn on Internet Connection to run this App!", Toast.LENGTH_LONG).show();
                    }
                }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("id", movieid);
                return params;
            }
        };

        MyVolley.getInstance(this).addToRequestQueue(stringRequest);
    }

    //gets the schedule list in near cinemas for the next week
    private void fetchScheduleList(){
        StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.POST, EndPoints.URL_GET_SCHEDULES,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);
                            JSONArray arr= obj.getJSONArray("schedules");
                            for(int i=0;i<arr.length();i++) {
                                JSONObject s = arr.getJSONObject(i);
                                System.out.println("Schedule: "+s);
                                scheduleList.add(new Schedule(s.getString("id"),s.getString("cinema"),
                                        s.getString("day"),s.getString("hour")));
                            }
                            loadScheduleList();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MovieActivity.this, "Turn on Internet Connection to run this App!", Toast.LENGTH_LONG).show();
                    }
                }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("id", movieid);
                params.put("day", day);
                return params;
            }
        };

        MyVolley.getInstance(this).addToRequestQueue(stringRequest);
    }

    //updates layout with movie content
    private void setMovieContent(){
        name.setText(movie.getName());
        length.setText(Html.fromHtml("<b>Length:</b> "+movie.getLength()), TextView.BufferType.SPANNABLE);
        category.setText(Html.fromHtml("<b>Categories:</b> "+movie.getCategory()), TextView.BufferType.SPANNABLE);
        desc.setText("\""+movie.getDesc()+"\"");
        new DownloadImageTask(image)
                .execute(movie.getImage());

    }

    //updates listview with schedules list
    private void loadScheduleList(){
        for(int i=0;i<scheduleList.size();i++){
            System.out.println("wholelist: "+scheduleList.get(i).getDay()+" "+scheduleList.get(i).getHour()+" "+scheduleList.get(i).getCinema());
        }

        updateTodayScheduleList();
        for(int i=0;i<todayScheduleList.size();i++){
            System.out.println("todaywholelist: "+todayScheduleList.get(i).getDay()+" "+todayScheduleList.get(i).getHour()+" "+todayScheduleList.get(i).getCinema());
        }
        slAdapter = new ScheduleListAdapter(this,context,R.layout.schedulerowlayout,todayScheduleList);
        list.setAdapter(slAdapter);
        list.setSelectionAfterHeaderView();
    }

    //when swiping left/right, yesterday's or tomorrow's schedule is loaded for current film
    private void updateTodayScheduleList(){
        for(int i=0;i<scheduleList.size();i++){
            int flag = -1;
            int j=0;
            if(scheduleList.get(i).getDay().equals(day)){
                for(j=0;j<todayScheduleList.size();j++){
                    if(scheduleList.get(i).getCinema().equals(todayScheduleList.get(j).getCinema())){
                        flag=j;
                    }
                }
                if(flag!=-1){
                    todayScheduleList.get(flag).setHour(todayScheduleList.get(flag).getHour()+" ; "+scheduleList.get(i).getHour());
                } else {
                    todayScheduleList.add(new Schedule(scheduleList.get(i).getId(),scheduleList.get(i).getCinema(),
                                            scheduleList.get(i).getDay(),scheduleList.get(i).getHour()));
                }
            }
        }
    }

    //when swiping towards top of the screen, the current movie/schedule day page is shared as a message
    private void sendMessage() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Suggesting movie..."+movie.getName());
        progressDialog.show();

        final String fb_id = SharedPrefManager.getInstance(this).getFacebookId();
        final String content = "Check out:<u><font color='#0645AD'><br>"+movie.getName()+"</font></u>"+",<br>Shows on date "+day;
        System.out.println("Message: " + fb_id + "  " + content + " " + chatid);

        StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.POST, EndPoints.URL_SEND_MESSAGE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        try {
                            JSONObject obj = new JSONObject(response);
                            Toast.makeText(MovieActivity.this, obj.getString("message"), Toast.LENGTH_LONG).show();
                            if (obj.getBoolean("error") == false) {
                                sendNotification(content);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Intent openChatActivityIntent = new Intent(MovieActivity.this,
                                ChatActivity.class);
                        openChatActivityIntent.putExtra(getPackageName() + ".chatid",chatid);
                        openChatActivityIntent.putExtra(getPackageName() + ".chatname",chatname);
                        startActivity(openChatActivityIntent);
                        overridePendingTransition(R.anim.enter_up, R.anim.exit_up);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        Toast.makeText(MovieActivity.this, "Error sending message", Toast.LENGTH_LONG).show();
                    }
                }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("sender", fb_id);
                params.put("content", content);
                params.put("chatid", chatid);
                params.put("intent", "");
                params.put("restaurant", "");
                params.put("cinema", movieid);
                params.put("image", movie.getImage());
                return params;
            }
        };

        MyVolley.getInstance(this).addToRequestQueue(stringRequest);
    }

    //a push notification is created when message is succesfully sent
    private void sendNotification(final String content) {

        final String fb_id = SharedPrefManager.getInstance(this).getFacebookId();
        final String title = getString(R.string.app_name);
        System.out.println("Notification: " + fb_id + "  " + content + " " + title);

        StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.POST, EndPoints.URL_SEND_NOTIFICATION,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //Toast.makeText(ChatActivity.this, response, Toast.LENGTH_LONG).show();
                        System.out.println("Firebase notification: " + response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("title", title);
                params.put("message", content);
                params.put("fbid", fb_id);
                params.put("chatid", chatid);
                return params;
            }
        };

        MyVolley.getInstance(this).addToRequestQueue(stringRequest);
    }

    //creates a Menu with help option
    public boolean onCreateOptionsMenu(Menu menu){
        super.onCreateOptionsMenu(menu);
        int base=Menu.FIRST;
        MenuItem item1=menu.add(base,1,1,"Help");
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        System.out.println("item="+item.getItemId());
        if (item.getItemId()==1){
            Toast.makeText(this, "Swipe left/right to check this movie's schedule for the day before/after the current one.", Toast.LENGTH_LONG).show();
            Toast.makeText(this, "Swipe Up to share this day and Movie schedule as a message.", Toast.LENGTH_LONG).show();
        }else {
            return super.onOptionsItemSelected(item);
        }return true;
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        overridePendingTransition(R.anim.enter_left, R.anim.exit_left);
    }

}
