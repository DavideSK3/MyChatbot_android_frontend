package com.example.mychatbot.Utilities;

/**
 * Created by david on 10/04/2017.
 */

//Modify to current Local IP foreach network change (check XAMPP - Netstat - httpd.exe)
public class EndPoints {
    public static final String LOCAL_URL = "http://10.196.179.134/FcmExample/";
    public static final String BASE_URL = "https://cryptic-mesa-27038.herokuapp.com/MyPhpInterface/";
    public static final String URL_REGISTER_USER = BASE_URL + "RegisterUser.php";
    public static final String URL_LOGIN_USER = BASE_URL + "LoginUser.php";
    public static final String URL_SEND_SINGLE_PUSH = BASE_URL + "SendSinglePush.php";
    public static final String URL_SEND_MULTIPLE_PUSH = BASE_URL + "SendMultiplePush.php";
    public static final String URL_FETCH_USERS = BASE_URL + "GetRegisteredUsers.php";
    public static final String URL_GET_FRIENDS  = BASE_URL + "GetFriends.php";
    public static final String URL_ADD_FRIEND  = BASE_URL + "AddFriend.php";
    public static final String URL_GET_CHATS  = BASE_URL + "GetChats.php";
    public static final String URL_OPEN_CHAT  = BASE_URL + "OpenChat.php";
    public static final String URL_GET_MESSAGES  = BASE_URL + "GetMessages.php";
    public static final String URL_SEND_MESSAGE  = BASE_URL + "SendMessage.php";
    public static final String URL_SEND_NOTIFICATION = BASE_URL + "SendNotification.php";
    public static final String URL_LOGOUT_USER = BASE_URL + "RemoveUser.php";
    public static final String URL_GET_RESTAURANTS = BASE_URL + "GetRestaurants.php";
    public static final String URL_GET_SINGLE_RESTAURANT = BASE_URL + "GetSingleRestaurant.php";
    public static final String URL_GET_MOVIES = BASE_URL + "GetMovies.php";
    public static final String URL_GET_SINGLE_MOVIE = BASE_URL + "GetSingleMovie.php";
    public static final String URL_GET_SCHEDULES = BASE_URL + "GetSchedules.php";
    public static final String URL_SUGGEST_RESTAURANT = BASE_URL + "SuggestRestaurant.php";
    public static final String URL_FB_BASE= "https://graph.facebook.com/";
    public static final String URL_FB_ME= URL_FB_BASE+"me?access_token=";
    public static final String URL_WITAI_MESSAGE= "https://api.wit.ai/message?v=26/04/2017&q=";
    //public static final String URL_FB_FRIENDS= URL_FB_BASE+"me/friends?access_token=";
}

