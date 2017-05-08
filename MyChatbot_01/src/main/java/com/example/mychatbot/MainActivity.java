package com.example.mychatbot;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.mychatbot.Utilities.EndPoints;
import com.example.mychatbot.Utilities.MyVolley;
import com.example.mychatbot.Utilities.SharedPrefManager;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private LoginButton loginButton;
    private CallbackManager callbackManager;
    private TextView info;
    private Context context;
    private String name;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = getApplicationContext();
        info = (TextView)findViewById(R.id.info);
        loginButton = (LoginButton)findViewById(R.id.login_button);
        callbackManager = CallbackManager.Factory.create();
        name="";
        loginButton.setReadPermissions(Arrays.asList("user_friends"));

        //get User token and check if it exists in DB
        tryLogin();

        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                System.out.println("Logged with UserID"
                                + loginResult.getAccessToken().getUserId()
                                + "\n" +
                                "Auth Token: "
                                + loginResult.getAccessToken().getToken()
                );
                SharedPrefManager.getInstance(context).saveFacebookId(loginResult.getAccessToken().getUserId());
                SharedPrefManager.getInstance(context).saveFacebookToken(loginResult.getAccessToken().getToken());
                getFbNameThenSignUp();
            }

            @Override
            public void onCancel() {
                info.setText("Login attempt canceled.");
            }

            @Override
            public void onError(FacebookException e) {
                info.setText("Login attempt failed.");
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        callbackManager.onActivityResult(requestCode,resultCode,data);
    }

    private void tryLogin() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Logging in...");
        progressDialog.show();

        SharedPrefManager spm= SharedPrefManager.getInstance(this);
        SharedPrefManager.fetchDeviceToken();
        final String token = spm.getDeviceToken();

        if (token == null) {
            progressDialog.dismiss();
            Toast.makeText(this, "Token not generated", Toast.LENGTH_LONG).show();
        }

        StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.POST, EndPoints.URL_LOGIN_USER,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        try {
                            JSONObject obj = new JSONObject(response);
                            //Toast.makeText(MainActivity.this, obj.getString("message"), Toast.LENGTH_LONG).show();
                            System.out.println(obj.getString("message"));
                            if(obj.getString("message").equals("Succesfully Logged in")){
                                startActivity(new Intent(context, HomeActivity.class));
                                finish();
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
                        Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("token", token);
                return params;
            }
        };

        MyVolley.getInstance(this).addToRequestQueue(stringRequest);
    }

    private void signUp() {

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Registering User...");
        progressDialog.show();

        final String token = SharedPrefManager.getInstance(this).getDeviceToken();
        final String fb_id = SharedPrefManager.getInstance(this).getFacebookId();
        System.out.println("in signup and fbid is "+fb_id);

        if (token == null) {
            progressDialog.dismiss();
            Toast.makeText(this, "Token not generated", Toast.LENGTH_LONG).show();
            return;
        }

        StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.POST, EndPoints.URL_REGISTER_USER,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        try {
                            JSONObject obj = new JSONObject(response);
                            System.out.println("Signup:  "+obj.getString("message"));
                            //Toast.makeText(MainActivity.this, obj.getString("message"), Toast.LENGTH_LONG).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        startActivity(new Intent(context, HomeActivity.class));
                        finish();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("fbid", fb_id);
                params.put("token", token);
                System.out.println("name is:  "+name);
                params.put("name", name);
                return params;
            }
        };

        MyVolley.getInstance(this).addToRequestQueue(stringRequest);
    }

    private void getFbNameThenSignUp() {
        final String fb_token = SharedPrefManager.getInstance(this).getFacebookToken();

        if (fb_token == null) {
            Toast.makeText(this, "fb token for this app is empty", Toast.LENGTH_LONG).show();
        }

        StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.GET, EndPoints.URL_FB_ME+fb_token,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);
                            System.out.println("name:  "+obj.getString("name"));
                            System.out.println("obj:  "+obj);
                            name= obj.getString("name");
                            signUp();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.println("error is : "+error.getMessage());
                        Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
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
}
