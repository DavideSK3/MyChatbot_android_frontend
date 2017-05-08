package com.example.mychatbot;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.mychatbot.Adapters.MessageListAdapter;
import com.example.mychatbot.Entities.Message;
import com.example.mychatbot.Entities.Restaurant;
import com.example.mychatbot.Utilities.EndPoints;
import com.example.mychatbot.Utilities.MyVolley;
import com.example.mychatbot.Utilities.OnSwipeTouchListener;
import com.example.mychatbot.Utilities.SharedPrefManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by david on 15/04/2017.
 */

public class ChatActivity extends AppCompatActivity implements LocationListener {

    private TextView chatName;
    private ImageButton chatbot;
    private ListView list;
    private EditText text;
    private Button send;

    private ArrayList<Restaurant> restaurants = new ArrayList<>();
    private ArrayList<Message> messageList;
    private MessageListAdapter mlAdapter;
    private ProgressDialog progressDialog;
    private BroadcastReceiver broadcastReceiver;
    private FragmentManager fragmentManager;
    private Activity context;
    private LocationManager locationManager;

    public static final String NOTIFY_CHATACTIVITY_ACTION = "notify_chatactivity";
    private static final String TAG_FRAGMENT = "popupFrament";
    private String thisChatname = null;
    private String thisChatid = null;
    private String witaiOutput = "";
    private String todayText = "";
    public String lat = "";
    private String lon = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        chatName = (TextView) findViewById(R.id.chatname);
        chatbot = (ImageButton) findViewById(R.id.chatbot);
        list = (ListView) findViewById(R.id.list);
        text = (EditText) findViewById(R.id.text);
        send = (Button) findViewById(R.id.send);
        send.setEnabled(false);

        context = this;

        messageList = new ArrayList<>();
        fragmentManager = getFragmentManager();

        initLocationManager();

        //get extra parameters
        Intent intent = getIntent();
        thisChatname = intent.getStringExtra(getPackageName() + ".chatname");
        thisChatid = intent.getStringExtra(getPackageName() + ".chatid");
        chatName.setText(thisChatname);

        loadList(); //initialize empty listview to be filled/updated on each onResume()

        //close virtual keyboard and send message in textview
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager inputManager = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);

                inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
                callWitAi();
            }
        });

        chatbot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    System.out.println("No permissions");
                    return;
                }
                System.out.println("On clickLatitude:" + lat + ", Longitude:" + lon);
                System.out.println("gps " + locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER));
                System.out.println("network " + locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER));
                if(lat.equals("")){
                    promptLocationUnavailable();
                } else {
                    callWitAi();
                }
            }
        });

        //disable send button when textfield is empty
        text.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim().length() == 0) {
                    send.setEnabled(false);
                } else {
                    send.setEnabled(true);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

    }

    //each time activity is refreshed, message list is updated
    @Override
    protected void onResume() {
        super.onResume();

        fetchMessageList();
    }

    //When a notification arrives, refreshes the current chat activity to get new messages
    @Override
    protected void onStart() {
        super.onStart();
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(NOTIFY_CHATACTIVITY_ACTION)) {
                    fetchMessageList();
                }
            }
        };

        IntentFilter filter = new IntentFilter(NOTIFY_CHATACTIVITY_ACTION);
        registerReceiver(broadcastReceiver, filter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(broadcastReceiver);
    }

    //if Fragment is showed, popup closes fragment, otherwise returns to previous activity
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() == 0) {
            super.onBackPressed();
        } else {
            getFragmentManager().popBackStack();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        lat = String.valueOf(location.getLatitude());
        lon = String.valueOf(location.getLongitude());
        System.out.println("Latitude:" + location.getLatitude() + ", Longitude:" + location.getLongitude());
        Toast.makeText(getApplicationContext(), "Position received, turn off GPS to save battery", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.d("Latitude", "disable");
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.d("Latitude", "enable");
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.d("Latitude", "status");
    }

    private void fetchMessageList() {
        //progressDialog = new ProgressDialog(this);
        //progressDialog.setMessage("Fetching messages...");
        //progressDialog.show();

        StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.POST, EndPoints.URL_GET_MESSAGES,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //progressDialog.dismiss();
                        messageList.clear();
                        mlAdapter.reset();
                        try {
                            JSONObject obj = new JSONObject(response);
                            System.out.println(obj);
                            JSONArray arr = obj.getJSONArray("messages");
                            todayText = "";
                            for (int i = 0; i < arr.length(); i++) {
                                String sender = arr.getJSONObject(i).getString("sender");
                                String content = arr.getJSONObject(i).getString("content");
                                String time = arr.getJSONObject(i).getString("time");
                                String intent = arr.getJSONObject(i).getString("intent");
                                String restaurant = arr.getJSONObject(i).getString("restaurant");
                                String cinema = arr.getJSONObject(i).getString("cinema");
                                //System.out.println("messages   "+sender +  content +  time);
                                messageList.add(new Message(sender, content, time, intent, restaurant, cinema));
                                pushSentence(content, time);
                            }
                            loadList();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //progressDialog.dismiss();
                        Toast.makeText(ChatActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("chatid", thisChatid);
                return params;
            }
        };

        MyVolley.getInstance(this).addToRequestQueue(stringRequest);
    }

    private void loadList() {
        mlAdapter = new MessageListAdapter(this, R.layout.messagerowlayout, messageList);
        list.setAdapter(mlAdapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getApplicationContext(),
                        messageList.get(position).getTime().toString(),
                        Toast.LENGTH_LONG).show();
            }
        });
        list.setSelection(mlAdapter.getCount() - 1);
    }

    private void sendMessage(final String i) {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Sendind message with intent..."+i);
        progressDialog.show();

        final String fb_id = SharedPrefManager.getInstance(this).getFacebookId();
        final String content = text.getText().toString();
        text.setText("");
        System.out.println("Message: " + fb_id + "  " + content + " " + thisChatid);

        StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.POST, EndPoints.URL_SEND_MESSAGE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        try {
                            JSONObject obj = new JSONObject(response);
                            Toast.makeText(ChatActivity.this, obj.getString("message"), Toast.LENGTH_LONG).show();
                            if (obj.getBoolean("error") == false) {
                                sendNotification(content);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        fetchMessageList();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        Toast.makeText(ChatActivity.this, "Error sending message", Toast.LENGTH_LONG).show();
                    }
                }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("sender", fb_id);
                params.put("content", content);
                params.put("chatid", thisChatid);
                params.put("intent", i);
                params.put("restaurant", "");
                params.put("cinema", "");
                return params;
            }
        };

        MyVolley.getInstance(this).addToRequestQueue(stringRequest);
    }

    private void sendNotification(final String content) {

        final String fb_id = SharedPrefManager.getInstance(this).getFacebookId();
        final String title = getString(R.string.app_name);
        System.out.println("Message: " + fb_id + "  " + content + " " + title);

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
                params.put("chatid", thisChatid);
                return params;
            }
        };

        MyVolley.getInstance(this).addToRequestQueue(stringRequest);
    }

    private void callWitAi() {

        final String witai_token = SharedPrefManager.getInstance(this).getWitAiToken();
        final String query_encoded;
        String query_encodedtemp = "null";
        final String message_text = text.getText().toString();

        try {
            query_encodedtemp = URLEncoder.encode(message_text, "UTF-8").replace("+", "%20");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        query_encoded = query_encodedtemp;
        System.out.println("query: " + query_encoded);

        StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.GET, EndPoints.URL_WITAI_MESSAGE + query_encoded,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //Toast.makeText(ChatActivity.this, response, Toast.LENGTH_LONG).show();
                        System.out.println(response);

                        try {
                            JSONObject obj = new JSONObject(response);
                            JSONObject entities = new JSONObject(obj.getString("entities"));
                            System.out.println("witai answer  "+obj);
                            if (entities.has("intent")) {
                                JSONArray intents = entities.getJSONArray("intent");
                                witaiOutput = intents.getJSONObject(intents.length() - 1).getString("value");
                            } else {
                                witaiOutput="";
                            }
                            System.out.println("witaiOutput  " + witaiOutput);
                            if (!witaiOutput.equals("")) {
                                if (witaiOutput.equals("restaurant")) {
                                    sendMessage(witaiOutput);
                                } else if (witaiOutput.equals("cinema")) {

                                    sendMessage(witaiOutput);
                                }
                            } else {
                                sendMessage("");
                                Toast.makeText(ChatActivity.this, "No Intent detected", Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Authorization", "Bearer " + witai_token);
                return params;
            }
        };

        MyVolley.getInstance(this).addToRequestQueue(stringRequest);
    }

    private void getRestaurantSuggestion() {

        System.out.println("on call lat and lon: "+lat+"  "+lon);


        StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.POST, EndPoints.URL_SUGGEST_RESTAURANT,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);
                            JSONArray arr= obj.getJSONArray("restaurants");
                            ArrayList<Double> distances= new ArrayList<>();
                            for(int i=0;i<arr.length();i++) {
                                JSONObject r = arr.getJSONObject(i);
                                System.out.println("suggestRestaurant:  " + obj);
                                restaurants.add(new Restaurant(r.getString("name"), r.getString("desc"), r.getString("lat"), r.getString("lon"),
                                        r.getString("street"), r.getString("number"), r.getString("city"),
                                        r.getString("phone"), r.getString("email"), r.getString("url")));
                                distances.add(r.getDouble("distance"));
                            }
                            PopupRestaurantFragment popupRestaurantFragment = new PopupRestaurantFragment();
                            popupRestaurantFragment.setRestaurant(restaurants);
                            popupRestaurantFragment.setDistances(distances);
                            popupRestaurantFragment.setContext(context);

                            FragmentTransaction transaction = fragmentManager.beginTransaction();
                            transaction.addToBackStack(TAG_FRAGMENT);
                            transaction.add(R.id.frame_popup, popupRestaurantFragment).commit();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        Toast.makeText(ChatActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("lat", lat);
                params.put("lon", lon);
                return params;
            }
        };

        MyVolley.getInstance(this).addToRequestQueue(stringRequest);

    }

    private void pushSentence(String sentence, String time){
        String now = new Timestamp(System.currentTimeMillis()).toString();
        String today = now.substring(0,11);
        String day = time.substring(0,11);
        if (today.equals(day)){
            todayText+=" "+sentence;
            System.out.println(todayText);
        }
    }

    private void promptLocationUnavailable() {
        int off = 0;
        try {
            off = Settings.Secure.getInt(getContentResolver(), Settings.Secure.LOCATION_MODE);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        if (off == 0) {
            Toast.makeText(ChatActivity.this, "Current location not found, consider turning on GPS", Toast.LENGTH_LONG).show();
            Intent onGPS = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(onGPS);
        } else {
            Toast.makeText(ChatActivity.this, "Current location not found, try again in few seconds", Toast.LENGTH_LONG).show();
        }
    }

    private void initLocationManager(){
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 10, this);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 10000, 10, this);
    }

}
