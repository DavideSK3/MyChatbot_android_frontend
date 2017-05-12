package com.example.mychatbot.Services;

/**
 * Created by david on 19/04/2017.
 *
 *  Service allowing the app to constantly receive push notification from Firebase service
 */

import android.content.Intent;
import android.util.Log;

import com.example.mychatbot.ChatActivity;
import com.example.mychatbot.MainActivity;
import com.example.mychatbot.Utilities.MyNotificationManager;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "MyFirebaseMsgService";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if (remoteMessage.getData().size() > 0) {
            Log.e(TAG, "Data Payload: " + remoteMessage.getData().toString());
            try {
                JSONObject json = new JSONObject(remoteMessage.getData().toString());
                sendPushNotification(json);
                Intent broadcastIntent = new Intent();
                broadcastIntent.setAction(ChatActivity.NOTIFY_CHATACTIVITY_ACTION );

                sendBroadcast(broadcastIntent);
                System.out.println(broadcastIntent.getAction());
            } catch (Exception e) {
                Log.e(TAG, "Exception: " + e.getMessage());
            }
        }
    }

    //this method will display the notification from json file
    private void sendPushNotification(JSONObject json) {
        Log.e(TAG, "Notification JSON " + json.toString());
        try {
            JSONObject data = json.getJSONObject("data");

            String title = data.getString("title");
            String message = data.getString("message");
            if(message.startsWith("Check out:<u><font")){
                message = "Check out what I shared with you";
            }
            String imageUrl = data.getString("image");

            MyNotificationManager mNotificationManager = new MyNotificationManager(getApplicationContext());

            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            System.out.println("context:  "+getApplicationContext());

            //for the moment images aren't really implemented in the app
            if (imageUrl.equals("null")) {
                mNotificationManager.showSmallNotification(title, message, intent);
            } else {
                //if there is an image
            }
        } catch (JSONException e) {
            Log.e(TAG, "Json Exception: " + e.getMessage());
        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage());
        }
    }
}
