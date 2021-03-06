package com.example.mychatbot;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.mychatbot.Adapters.ChatsListAdapter;
import com.example.mychatbot.Entities.Chat;
import com.example.mychatbot.Utilities.EndPoints;
import com.example.mychatbot.Utilities.MyVolley;
import com.example.mychatbot.Utilities.SharedPrefManager;
import com.facebook.AccessToken;
import com.facebook.Profile;
import com.facebook.login.LoginManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by david on 10/04/2017.
 *
 * Presents the whole list of chats in chronological inverse order.
 * User can click on a chat to open it or click on the button to create new chats.
 * User can logout from the menu.
 */

public class HomeActivity extends AppCompatActivity implements View.OnClickListener{

    private ImageButton friendlistButton;
    private FrameLayout frame_text_empty;
    private TextView text_empty;
    private ListView view;

    private Context context;
    private ArrayList<Chat> chatsList;
    private ProgressDialog progressDialog;
    private ChatsListAdapter clAdapter;

    private Boolean exit = false;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        context = getApplicationContext();

        friendlistButton=(ImageButton)findViewById(R.id.friendlist_button);
        frame_text_empty=(FrameLayout)findViewById(R.id.frame_text_empty);
        text_empty=(TextView)findViewById(R.id.text_empty);
        view = (ListView) findViewById(R.id.list);

        chatsList = new ArrayList<>();

        friendlistButton.setOnClickListener(this);

        loadList();
    }

    @Override
    protected void onResume(){
        super.onResume();
        fetchChatsList();
    }

    @Override
    public void onClick(View v) {
        if (v==friendlistButton){
            startActivity(new Intent(context, FriendlistActivity.class));
            overridePendingTransition(R.anim.enter_right, R.anim.exit_right);
        }
    }

    //gets the list of all chats in inverse chronological order
    private void fetchChatsList() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Fetching chats...");
        progressDialog.show();

        final String fb_id = SharedPrefManager.getInstance(this).getFacebookId();

        StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.POST, EndPoints.URL_GET_CHATS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();

                        chatsList.clear();
                        clAdapter.reset();
                        try {
                            JSONObject obj = new JSONObject(response);
                            JSONArray arr= obj.getJSONArray("chats");
                            for (int i = 0; i < arr.length(); i++) {
                                String tempchatid=arr.getJSONObject(i).getString("chatid");
                                String tempchatname=arr.getJSONObject(i).getString("chatname");
                                String tempchattime=arr.getJSONObject(i).getString("time");
                                System.out.println("friends:  "+tempchatid+tempchatname+tempchattime);
                                chatsList.add(new Chat(tempchatid,tempchatname,tempchattime));
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
                        progressDialog.dismiss();
                        Toast.makeText(HomeActivity.this, "Turn on Internet Connection to run this App!", Toast.LENGTH_LONG).show();
                    }
                }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("fbid", fb_id);
                return params;
            }
        };

        MyVolley.getInstance(this).addToRequestQueue(stringRequest);
    }

    //loads chatlist into listview
    private void loadList(){
        clAdapter = new ChatsListAdapter(this, R.layout.chatrowlayout, chatsList);
        view.setAdapter(clAdapter);
        view.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
                //Toast.makeText(getApplicationContext(),chatsList.get(position).getChatId(),Toast.LENGTH_LONG).show();
                Intent openChatActivityIntent = new Intent(HomeActivity.this,
                        ChatActivity.class);
                String chatid = chatsList.get(position).getChatId();
                openChatActivityIntent.putExtra(getPackageName() + ".chatid",chatid);
                String chatname = chatsList.get(position).getChatName();
                openChatActivityIntent.putExtra(getPackageName() + ".chatname",chatname);
                startActivity(openChatActivityIntent);
                overridePendingTransition(R.anim.enter_right, R.anim.exit_right);
            }
        });
        if(chatsList.isEmpty()){
            text_empty.setText("Start a new chat from your Friendlist!");
        } else {
            frame_text_empty.removeView(text_empty);
        }
    }

    //creates a Menu with logout and help options
    public boolean onCreateOptionsMenu(Menu menu){
        super.onCreateOptionsMenu(menu);
        int base=Menu.FIRST;
        MenuItem item1=menu.add(base,1,1,"Help");
        MenuItem item2=menu.add(base,2,2,"Logout");
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        System.out.println("item="+item.getItemId());
        if (item.getItemId()==2) {
            LoginManager.getInstance().logOut();
            logOut();
        } else if (item.getItemId()==1){
            Toast.makeText(this, "Click on + Button to create a new chat!", Toast.LENGTH_LONG).show();
        }else {
            return super.onOptionsItemSelected(item);
        }return true;
    }

    //logs out the user
    private void logOut() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Logging out...");
        progressDialog.show();

        final String fb_id = SharedPrefManager.getInstance(this).getFacebookId();

        if (fb_id == null) {
            progressDialog.dismiss();
            Toast.makeText(this, "fb token for this app is empty", Toast.LENGTH_LONG).show();
        }

        StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.POST, EndPoints.URL_LOGOUT_USER,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        try {
                            JSONObject obj = new JSONObject(response);
                            Toast.makeText(HomeActivity.this, obj.getString("message"), Toast.LENGTH_LONG).show();
                            System.out.println(obj.getString("message"));
                            if(obj.getString("message").equals("Logout Succesful")){
                                finish();
                                startActivity(new Intent(context, MainActivity.class));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        Toast.makeText(HomeActivity.this, "Turn on Internet Connection to run this App!", Toast.LENGTH_LONG).show();
                    }
                }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("fbid", fb_id);
                return params;
            }
        };

        MyVolley.getInstance(this).addToRequestQueue(stringRequest);
    }

    @Override
    public void onBackPressed(){
        if (exit) {
            Intent a = new Intent(Intent.ACTION_MAIN);
            a.addCategory(Intent.CATEGORY_HOME);
            a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(a);
        } else {
            Toast.makeText(this, "Press Back again to Exit.",
                    Toast.LENGTH_SHORT).show();
            exit = true;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    exit = false;
                }
            }, 3 * 1000);

        }
    }
}
