package com.example.mychatbot;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by david on 10/04/2017.
 */

public class HomeActivity extends AppCompatActivity implements View.OnClickListener{

    private Button settingsButton;
    private Button friendlistButton;
    private FrameLayout frame_text_empty;
    private TextView text_empty;
    private ListView view;

    private Context context;
    private ArrayList<Chat> chatsList;
    private ProgressDialog progressDialog;
    private ChatsListAdapter clAdapter;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        context = getApplicationContext();

        settingsButton=(Button)findViewById(R.id.settings_button);
        friendlistButton=(Button)findViewById(R.id.friendlist_button);
        frame_text_empty=(FrameLayout)findViewById(R.id.frame_text_empty);
        text_empty=(TextView)findViewById(R.id.text_empty);
        view = (ListView) findViewById(R.id.list);

        chatsList = new ArrayList<>();

        settingsButton.setOnClickListener(this);
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
        if (v==settingsButton){
            startActivity(new Intent(context, SettingsActivity.class));
        } else if (v==friendlistButton){
            startActivity(new Intent(context, FriendlistActivity.class));
        }
    }

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
                        Toast.makeText(HomeActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
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
            }
        });
        if(chatsList.isEmpty()){
            text_empty.setText("Start a new chat from your Friendlist!");
        } else {
            frame_text_empty.removeView(text_empty);
        }
    }
}
