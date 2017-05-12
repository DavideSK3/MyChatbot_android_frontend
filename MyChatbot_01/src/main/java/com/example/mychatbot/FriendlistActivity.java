package com.example.mychatbot;

import android.app.Notification;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.mychatbot.Adapters.FriendListAdapter;
import com.example.mychatbot.Entities.Friend;
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
 * Created by david on 15/04/2017.
 */

public class FriendlistActivity extends AppCompatActivity {

    private Button update;
    private ListView view;

    private ArrayList<Friend> friendList;
    private FriendListAdapter flAdapter;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friendlist);

        update = (Button) findViewById(R.id.update);
        view = (ListView) findViewById(R.id.list);

        friendList = new ArrayList<>();

        loadList();

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFbFriends();
            }
        });
    }

    @Override
    protected void onResume(){
        super.onResume();
        fetchFriendList();
    }

    //gets friendlist from DB
    private void fetchFriendList() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Fetching friends...");
        progressDialog.show();

        final String fb_id = SharedPrefManager.getInstance(this).getFacebookId();

        StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.POST, EndPoints.URL_GET_FRIENDS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        friendList.clear();
                        flAdapter.reset();
                        try {
                            JSONObject obj = new JSONObject(response);
                            JSONArray arr = obj.getJSONArray("friends");
                            for (int i = 0; i < arr.length(); i++) {
                                String tempfbid = arr.getJSONObject(i).getString("fbid");
                                String tempname = arr.getJSONObject(i).getString("name");
                                System.out.println("friends:  " + tempfbid + tempname);
                                friendList.add(new Friend(tempfbid, tempname));
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
                        Toast.makeText(FriendlistActivity.this, "Turn on Internet Connection to run this App!", Toast.LENGTH_LONG).show();
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

    //loads list into listview
    private void loadList() {
        flAdapter= new FriendListAdapter(this, R.layout.friendrowlayout, friendList);
        view.setAdapter(flAdapter);
        view.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                openChat(friendList.get(position).getFbid());
            }
        });

    }

    //opens the chat for that friend, creating one if chat isn't already existing
    private void openChat(final String friendid) {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Fetching friends...");
        progressDialog.show();

        final String fb_id = SharedPrefManager.getInstance(this).getFacebookId();

        StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.POST, EndPoints.URL_OPEN_CHAT,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        try {
                            JSONObject obj = new JSONObject(response);
                            String chatid = obj.getString("chatid");
                            String chatname = obj.getString("chatname");
                            Intent openChatActivityIntent = new Intent(FriendlistActivity.this,
                                    ChatActivity.class);
                            openChatActivityIntent.putExtra(getPackageName() + ".chatid", chatid);
                            openChatActivityIntent.putExtra(getPackageName() + ".chatname", chatname);
                            startActivity(openChatActivityIntent);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        Toast.makeText(FriendlistActivity.this, "Turn on Internet Connection to run this App!", Toast.LENGTH_LONG).show();
                    }
                }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("id", fb_id);
                params.put("friendid", friendid);
                System.out.println(fb_id + "  " + friendid);
                return params;
            }
        };

        MyVolley.getInstance(this).addToRequestQueue(stringRequest);
    }

    //Retrieves the list of fb friends which installed this app
    private void getFbFriends() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Updating friend list...");
        progressDialog.show();

        final String fb_token = SharedPrefManager.getInstance(this).getFacebookToken();
        System.out.println("perm  " + AccessToken.getCurrentAccessToken().getPermissions());


        StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.GET, EndPoints.URL_FB_BASE + Profile.getCurrentProfile().getId() + "/friends?access_token=" + fb_token,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        try {
                            JSONObject obj = new JSONObject(response);
                            JSONArray arr = obj.getJSONArray("data");
                            for (int i = 0; i < arr.length(); i++) {
                                String friendid = arr.getJSONObject(i).getString("id");
                                System.out.println(friendid);
                                addFriend(friendid);
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
                        Toast.makeText(FriendlistActivity.this, "Turn on Internet Connection to run this App!", Toast.LENGTH_LONG).show();
                    }
                }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                return params;
            }
        };

        MyVolley.getInstance(this).addToRequestQueue(stringRequest);
    }

    //Updates your friends by syncing them with FB users
    private void addFriend(final String friendid) {

        final String fb_id = SharedPrefManager.getInstance(this).getFacebookId();

        StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.POST, EndPoints.URL_ADD_FRIEND,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        try {
                            JSONObject obj = new JSONObject(response);
                            System.out.println("Insert of friend: " + friendid + ":  " + obj.getString("message"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        fetchFriendList();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(FriendlistActivity.this, "Turn on Internet Connection to run this App!", Toast.LENGTH_LONG).show();
                    }
                }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("fbid", fb_id);
                params.put("friendid", friendid);
                return params;
            }
        };

        MyVolley.getInstance(this).addToRequestQueue(stringRequest);
    }
}
