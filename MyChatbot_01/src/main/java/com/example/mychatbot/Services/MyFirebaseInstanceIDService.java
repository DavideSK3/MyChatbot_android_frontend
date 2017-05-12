package com.example.mychatbot.Services;

/**
 * Created by david on 19/04/2017.
 *
 * Service used to update the device token fetched from Firebase, in case it gets refreshed
 */

import android.util.Log;

import com.example.mychatbot.Utilities.SharedPrefManager;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;



//Class extending FirebaseInstanceIdService
public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    private static final String TAG = "MyFirebaseIIDService";

    @Override
    public void onTokenRefresh() {

        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        System.out.println("called:   " + refreshedToken);

        Log.d(TAG, "Refreshed token: " + refreshedToken);

        storeToken(refreshedToken);
    }

    private void storeToken(String token) {
        SharedPrefManager.getInstance(getApplicationContext()).saveDeviceToken(token);
    }
}
