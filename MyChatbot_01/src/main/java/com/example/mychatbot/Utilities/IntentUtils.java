package com.example.mychatbot.Utilities;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

/**
 * Created by david on 02/05/2017.
 */

public class IntentUtils {
    public static void invokeWebBrowser(Activity activity, String url) {

        if (!url.startsWith("http://") && !url.startsWith("https://"))
            url = "http://" + url;
        Intent intent = new Intent(
                android.content.Intent.ACTION_VIEW);
        intent.setAction(Intent.ACTION_VIEW);
        intent.setType("message/rfc822");
        intent.setData(Uri.parse(url));

        activity.startActivity(Intent.createChooser(intent,
                "Browse Using: "));
    }

    public static void dial(Activity activity, String number) {
        Intent intent=new Intent(Intent.ACTION_DIAL);
        String uri = "tel:" + number.trim() ;
        intent.setData(Uri.parse(uri));
        activity.startActivity(intent);
    }

    public static void showDirections(Activity activity, String lat, String lon){
        Intent intent = new Intent(android.content.Intent.ACTION_VIEW,Uri.parse("http://maps.google.com/?q="+lat +","+lon));
        activity.startActivity(intent);
    }

    public static void sendEmail(Activity activity, String email){
        Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
        emailIntent.setAction(Intent.ACTION_SEND);
        //emailIntent.setClassName("com.google.android.gm","com.google.android.gm.ComposeActivityGmail");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{email});
        emailIntent.setType("message/rfc822");
        emailIntent.setData(Uri.parse("mailto:"+email));
        emailIntent.setType("text/html");

        activity.startActivity(Intent.createChooser(emailIntent,
                "Send Email Using: "));
    }
}
