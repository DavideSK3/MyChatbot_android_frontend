package com.example.mychatbot.Utilities;

/**
 * Created by david on 10/04/2017.
 */

import android.content.Context;
import android.content.SharedPreferences;

import com.google.firebase.iid.FirebaseInstanceId;

/**
 * Created by david on 08/04/2017.
 *
 * Stores some useful and frequently used data in a local sharedPreference instance
 */

public class SharedPrefManager {
    private static final String CLIENT_VERSION = "2.1_S170516";
    private static final String SHARED_PREF_NAME = "FCMSharedPref";
    private static final String TAG_ID_FACEBOOK = "tagidfacebook";
    private static final String TAG_TOKEN_FIREBASE = "tagtokenfirebase";
    private static final String TAG_TOKEN_FACEBOOK = "tagtokenfacebook";
    private static final String TAG_TOKEN_WITAI = "tagtokenwitai";
    private static final String token_witai = "HFGP66IOUMMBHVLDQ6AEWUOEDQUIOL2A"; //token from my NLP system on WitAi


    private static SharedPrefManager mInstance;
    private static Context mCtx;

    private SharedPrefManager(Context context) {
        mCtx = context;
    }

    public static String getClientVersion() {
        return CLIENT_VERSION;
    }

    public static void fetchDeviceToken(){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String token= FirebaseInstanceId.getInstance().getToken();
        System.out.println("fetched token: "+token);
        editor.putString(TAG_TOKEN_FIREBASE, token);
        editor.apply();
    }

    public static synchronized SharedPrefManager getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new SharedPrefManager(context);
            mInstance.setWitAiToken();
        }
        return mInstance;
    }

    //this method will save the device token to shared preferences
    public boolean saveDeviceToken(String token){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(TAG_TOKEN_FIREBASE, token);
        editor.apply();
        System.out.println("setdevice "+sharedPreferences.getString(TAG_TOKEN_FIREBASE, null));
        return true;
    }

    public boolean saveFacebookId(String id){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(TAG_ID_FACEBOOK, id);
        editor.apply();
        System.out.println("setfacebook "+sharedPreferences.getString(TAG_ID_FACEBOOK, null));
        return true;
    }

    public boolean saveFacebookToken(String token){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(TAG_TOKEN_FACEBOOK, token);
        editor.apply();
        System.out.println("setfacebooktoken "+sharedPreferences.getString(TAG_TOKEN_FACEBOOK, null));
        return true;
    }

    public boolean setWitAiToken(){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(TAG_TOKEN_WITAI, token_witai);
        editor.apply();
        System.out.println("setwitaitoken "+sharedPreferences.getString(TAG_TOKEN_WITAI, null));
        return true;
    }

    //this method will fetch the device token from shared preferences
    public String getDeviceToken(){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        System.out.println("getdevice "+sharedPreferences.getString(TAG_TOKEN_FIREBASE, null));
        return  sharedPreferences.getString(TAG_TOKEN_FIREBASE, null);
    }

    public String getFacebookId(){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        System.out.println("getfacebook "+sharedPreferences.getString(TAG_ID_FACEBOOK, null));
        return  sharedPreferences.getString(TAG_ID_FACEBOOK, null);
    }

    public String getFacebookToken(){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        System.out.println("getfacebooktoken "+sharedPreferences.getString(TAG_TOKEN_FACEBOOK, null));
        return  sharedPreferences.getString(TAG_TOKEN_FACEBOOK, null);
    }

    public String getWitAiToken(){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        System.out.println("getwitaitoken "+sharedPreferences.getString(TAG_TOKEN_WITAI, null));
        return  sharedPreferences.getString(TAG_TOKEN_WITAI, null);
    }

}