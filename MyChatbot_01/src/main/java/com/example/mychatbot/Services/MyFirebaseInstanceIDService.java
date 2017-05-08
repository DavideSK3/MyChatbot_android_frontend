package com.example.mychatbot.Services;

/**
 * Created by david on 19/04/2017.
 */

import android.util.Log;

import com.example.mychatbot.Utilities.SharedPrefManager;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/*he onTokenRefresh callback fires whenever a new token is
generated, so calling getToken in its context ensures that you
 are accessing a current, available registration token.
 FirebaseInstanceID.getToken() returns null if the token has not
  yet been generated.*/

//Class extending FirebaseInstanceIdService
public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    private static final String TAG = "MyFirebaseIIDService";

    @Override
    public void onTokenRefresh() {

        //Getting registration token
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        System.out.println("called:   " + refreshedToken);

        //Displaying token on logcat
        Log.d(TAG, "Refreshed token: " + refreshedToken);

        //calling the method store token and passing token
        storeToken(refreshedToken);
    }

    private void storeToken(String token) {
        //we will save the token in sharedpreferences later
        SharedPrefManager.getInstance(getApplicationContext()).saveDeviceToken(token);
    }
}
