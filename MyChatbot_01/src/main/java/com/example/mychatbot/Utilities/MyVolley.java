package com.example.mychatbot.Utilities;

/**
 * Created by david on 10/04/2017.
 */

import android.content.Context;


/**
 * Created by david on 09/04/2017.
 */

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;


public class MyVolley {

    private static MyVolley mInstance;
    private RequestQueue mRequestQueue;
    private static Context mCtx;

    private MyVolley(Context context) {
        mCtx = context;
        mRequestQueue = getRequestQueue();
    }

    public static synchronized MyVolley getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new MyVolley(context);
        }
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            mRequestQueue = Volley.newRequestQueue(mCtx.getApplicationContext());
        }
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }

}
