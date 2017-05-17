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
import android.view.Menu;
import android.view.MenuItem;
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

public class ChatActivity extends AppCompatActivity {

    private TextView chatName;
    private ListView list;
    private EditText text;
    private ImageButton send;

    private ArrayList<Message> messageList;
    private MessageListAdapter mlAdapter;
    private ProgressDialog progressDialog;
    private BroadcastReceiver broadcastReceiver;
    private Activity context;

    public static final String NOTIFY_CHATACTIVITY_ACTION = "notify_chatactivity";
    private String thisChatname = null;
    private String thisChatid = null;
    private String witaiOutput = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        chatName = (TextView) findViewById(R.id.chatname);
        list = (ListView) findViewById(R.id.list);
        text = (EditText) findViewById(R.id.text);
        send = (ImageButton) findViewById(R.id.send);
        send.setEnabled(false);

        context = this;

        messageList = new ArrayList<>();

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
                send.setEnabled(false);
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

    //Updates messages
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
                            for (int i = 0; i < arr.length(); i++) {
                                String sender = arr.getJSONObject(i).getString("sender");
                                String content = arr.getJSONObject(i).getString("content");
                                String time = arr.getJSONObject(i).getString("time");
                                String intent = arr.getJSONObject(i).getString("intent");
                                String restaurant = arr.getJSONObject(i).getString("restaurant");
                                String cinema = arr.getJSONObject(i).getString("cinema");
                                String image = arr.getJSONObject(i).getString("image");
                                String day = arr.getJSONObject(i).getString("day");
                                System.out.println("dayyyy : "+day);
                                //System.out.println("messages   "+sender +  content +  time);
                                messageList.add(new Message(sender, content, time, intent, restaurant, cinema, image, day));
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
                        Toast.makeText(ChatActivity.this,"Turn on Internet Connection to run this App!", Toast.LENGTH_LONG).show();
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

    //loads messagelist into listview
    private void loadList() {
        mlAdapter = new MessageListAdapter(this, R.layout.messagerowlayout, messageList, thisChatid, thisChatname);
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

    //sends a new message
    private void sendMessage(final String i, final String day) {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Sendind message...");
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
                params.put("image", "");
                params.put("day", day);
                System.out.println("params send: "+params);
                return params;
            }
        };

        MyVolley.getInstance(this).addToRequestQueue(stringRequest);
    }

    //sends a notification when message is succesfully sent
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
                params.put("chatid", thisChatid);
                return params;
            }
        };

        MyVolley.getInstance(this).addToRequestQueue(stringRequest);
    }

    //calls WitAi Natural Language Processor to extrapolate intent from message
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
                                    sendMessage(witaiOutput,"");
                                } else if (witaiOutput.equals("cinema")) {
                                    String day="";
                                    if(entities.has("datetime")){
                                        day=getDatetime(entities.getJSONArray("datetime"));
                                        System.out.println("Parsed day: "+day);
                                    }
                                    sendMessage(witaiOutput,day);
                                }
                            } else {
                                sendMessage("","");
                                //Toast.makeText(ChatActivity.this, "No Intent detected", Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(ChatActivity.this,"Turn on Internet Connection to run this App!", Toast.LENGTH_LONG).show();
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

    private String getDatetime(JSONArray datetime){
        String value ="";
        try {
            if(datetime.getJSONObject(datetime.length()-1).has("value")) {
                value = datetime.getJSONObject(datetime.length() - 1).getString("value");
            } else if(datetime.getJSONObject(datetime.length()-1).has("from")){
                value = datetime.getJSONObject(datetime.length() - 1).getJSONObject("from").getString("value");
            }
        } catch (JSONException e) {
        }

        return value.substring(0,10);
    }

    @Override
    public void onBackPressed(){
        finish();
        Intent intent = new Intent(context, HomeActivity.class);
        startActivity(intent);
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
            Toast.makeText(this, "When an intent is detected in a message, a contextual button appears next to it.", Toast.LENGTH_LONG).show();
            Toast.makeText(this, "Click on contextual button to access additional information.\n" +
                            "Click on underlined links to access details page",
                    Toast.LENGTH_LONG).show();
        }else {
            return super.onOptionsItemSelected(item);
        }return true;
    }
}
