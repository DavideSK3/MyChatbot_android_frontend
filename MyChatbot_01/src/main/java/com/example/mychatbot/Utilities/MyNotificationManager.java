package com.example.mychatbot.Utilities;

/**
 * Created by david on 19/04/2017.
 *
 * Creates a notification for the target device with Message content and personalized icon
 */

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import android.text.Html;

import com.example.mychatbot.R;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;


public class MyNotificationManager {

    public static final int ID_SMALL_NOTIFICATION = 235;

    private Context mCtx;

    public MyNotificationManager(Context mCtx) {
        this.mCtx = mCtx;
    }


    public void showSmallNotification(String title, String message, Intent intent) {
        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        mCtx,
                        ID_SMALL_NOTIFICATION,
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );


        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mCtx);
        Notification notification;
        notification = mBuilder.setSmallIcon(R.drawable.icon72).setTicker(title).setWhen(0)
                .setAutoCancel(true)
                .setContentIntent(resultPendingIntent)
                .setContentTitle(title)
                .setSmallIcon(R.drawable.notification_icon)
                .setLargeIcon(BitmapFactory.decodeResource(mCtx.getResources(), R.drawable.icon72))
                .setContentText(message)
                .build();

        notification.flags |= Notification.FLAG_AUTO_CANCEL;

        NotificationManager notificationManager = (NotificationManager) mCtx.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(ID_SMALL_NOTIFICATION, notification);
    }

    //The method will return Bitmap from an image URL
    private Bitmap getBitmapFromURL(String strURL) {
        try {
            URL url = new URL(strURL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
