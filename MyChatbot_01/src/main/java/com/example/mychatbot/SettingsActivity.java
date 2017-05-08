package com.example.mychatbot;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.mychatbot.Utilities.EndPoints;
import com.example.mychatbot.Utilities.MyVolley;
import com.example.mychatbot.Utilities.SharedPrefManager;
import com.facebook.login.LoginManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by david on 15/04/2017.
 */

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener{

    private Button logoutButton;
    private ProgressDialog progressDialog;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        logoutButton=(Button)findViewById(R.id.logout_button);
        context = getApplicationContext();

        logoutButton.setOnClickListener(this);
    }

    public void onClick(View v) {
        if (v == logoutButton) {
            LoginManager.getInstance().logOut();
            logOut();
        }
    }


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
                            Toast.makeText(SettingsActivity.this, obj.getString("message"), Toast.LENGTH_LONG).show();
                            System.out.println(obj.getString("message"));
                            if(obj.getString("message").equals("Logout Succesful")){
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
                        Toast.makeText(SettingsActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
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
}
