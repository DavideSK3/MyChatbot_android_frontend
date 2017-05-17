package com.example.mychatbot;

import android.*;
import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.mychatbot.Adapters.MessageListAdapter;
import com.example.mychatbot.Adapters.MovieListAdapter;
import com.example.mychatbot.Adapters.RestaurantListAdapter;
import com.example.mychatbot.Entities.Message;
import com.example.mychatbot.Entities.Movie;
import com.example.mychatbot.Entities.Restaurant;
import com.example.mychatbot.Utilities.EndPoints;
import com.example.mychatbot.Utilities.MyMethods;
import com.example.mychatbot.Utilities.MyVolley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
import java.util.Map;

/**
 * Created by david on 08/05/2017.
 */

public class ResultsActivity extends AppCompatActivity implements LocationListener {

    private EditText place;
    private ImageButton gps;
    private ImageButton search;
    private ListView list;

    private ArrayList<Restaurant> restaurantList;
    private ArrayList<Movie> movieList;
    private RestaurantListAdapter rlAdapter;
    private MovieListAdapter mlAdapter;
    private Activity context;
    private LocationManager locationManager;

    private String intento;
    private String chatid;
    private String chatname;
    private String day;
    private String lat = "46.0741662"; //initialized for trento
    private String lon = "11.1204982"; //initialized for trento
    private String gps_lat = "";
    private String gps_lon = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        place = (EditText) findViewById(R.id.place);
        gps = (ImageButton) findViewById(R.id.gps);
        search = (ImageButton) findViewById(R.id.search);
        list = (ListView) findViewById(R.id.list);

        restaurantList = new ArrayList<>();
        movieList = new ArrayList<>();
        context = this;

        Intent intent = getIntent();
        intento = intent.getStringExtra(getPackageName() + ".intent");
        chatid = intent.getStringExtra(getPackageName() + ".chatid");
        chatname = intent.getStringExtra(getPackageName() + ".chatname");
        day = intent.getStringExtra(getPackageName() + ".day");
        if(day!=null) {
            if (day.equals("")) {
                System.out.println("Day is empty, setting it to today");
                day = MyMethods.getTodayDate();
            }
        }
        System.out.println("contextual day is : "+day);

        initLocationManager();

        loadListWrapper();

        gps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    askPermission();
                    return;
                }
                System.out.println("On clickLatitude:" + lat + ", Longitude:" + lon);
                System.out.println("gps " + locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER));
                if(gps_lat.equals("")){
                    promptLocationUnavailable();
                } else {
                    lat=gps_lat;
                    lon=gps_lon;
                    setAddressFromLocation();
                    fetchListWrapper();
                }
            }
        });

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setLocationFromAddress(place.getText().toString());
            }
        });
    }

    @Override
    protected void onResume(){
        super.onResume();

        fetchListWrapper();
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

    }

    //wraps fetcher methods whether restaurant or movie intent is found by NLP
    private void fetchListWrapper(){
        if(intento.equals("restaurant")) {
            fetchRestaurantList();
        } else if (intento.equals("cinema")){
            fetchMovieList();
        } else {
            Toast.makeText(ResultsActivity.this, "Intent can't be recognized", Toast.LENGTH_LONG).show();
        }
    }

    //fetch the list of restaurants for the currently set location (default,search,gps)
    private void fetchRestaurantList(){

        StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.POST, EndPoints.URL_GET_RESTAURANTS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        restaurantList.clear();
                        rlAdapter.reset();
                        try {
                            JSONObject obj = new JSONObject(response);
                            JSONArray arr= obj.getJSONArray("restaurants");
                            for(int i=0;i<arr.length();i++) {
                                JSONObject r = arr.getJSONObject(i);
                                restaurantList.add(new Restaurant(r.getString("id"),r.getString("name"), "",
                                        r.getString("lat"),r.getString("lon"),
                                        r.getString("street"), r.getString("number"), r.getString("city"),
                                        "", "", "",r.getDouble("distance")));
                            }
                            loadRestaurantList();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(ResultsActivity.this, "Turn on Internet Connection to run this App!", Toast.LENGTH_LONG).show();
                    }
                }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("lat", lat);
                params.put("lon", lon);
                return params;
            }
        };

        MyVolley.getInstance(this).addToRequestQueue(stringRequest);

    }

    //fetch the list of movies available in nearby cinemas
    private void fetchMovieList(){
        StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.POST, EndPoints.URL_GET_MOVIES,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        movieList.clear();
                        mlAdapter.reset();
                        try {
                            JSONObject obj = new JSONObject(response);
                            JSONArray arr= obj.getJSONArray("movies");
                            for(int i=0;i<arr.length();i++) {
                                JSONObject m = arr.getJSONObject(i);
                                System.out.println("Movie: "+m);
                                movieList.add(new Movie(m.getString("id"),m.getString("name"),
                                        m.getString("description"),m.getString("length"),
                                        m.getString("category"),m.getString("image")));
                            }
                            loadMovieList();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(ResultsActivity.this, "Turn on Internet Connection to run this App!", Toast.LENGTH_LONG).show();
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

    //wraps loading listview methods
    private void loadListWrapper(){
        if(intento.equals("restaurant")) {
            place.setVisibility(View.VISIBLE);
            place.setHint("Trento");
            gps.setVisibility(View.VISIBLE);
            search.setVisibility(View.VISIBLE);
            loadRestaurantList();
        } else if (intento.equals("cinema")){

            loadMovieList();
        } else {
            Toast.makeText(ResultsActivity.this, "Intent can't be matched to rest or cin", Toast.LENGTH_LONG).show();
        }

    }

    //loads listview with restaurants fetched
    private void loadRestaurantList(){
        rlAdapter = new RestaurantListAdapter(this,context, R.layout.restaurantrowlayout, restaurantList,lat,lon);
        list.setAdapter(rlAdapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent openRestaurantActivityIntent = new Intent(ResultsActivity.this,
                        RestaurantActivity.class);
                openRestaurantActivityIntent.putExtra(getPackageName() + ".chatid",chatid);
                openRestaurantActivityIntent.putExtra(getPackageName() + ".restaurantid",restaurantList.get(position).getId());
                openRestaurantActivityIntent.putExtra(getPackageName() + ".distance",String.valueOf(restaurantList.get(position).getDistance()));
                System.out.println("my restaurant chosen is"+ restaurantList.get(position).getId());
                openRestaurantActivityIntent.putExtra(getPackageName() + ".chatname",chatname);
                startActivity(openRestaurantActivityIntent);
            }
        });
        list.setSelectionAfterHeaderView();
    }

    //loads listview with movies fetched
    private void loadMovieList(){
        mlAdapter = new MovieListAdapter(this,context,R.layout.movierowlayout,movieList);
        list.setAdapter(mlAdapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent openRestaurantActivityIntent = new Intent(ResultsActivity.this,
                        MovieActivity.class);
                openRestaurantActivityIntent.putExtra(getPackageName() + ".movieid",movieList.get(position).getId());
                openRestaurantActivityIntent.putExtra(getPackageName() + ".chatname",chatname);
                openRestaurantActivityIntent.putExtra(getPackageName() + ".chatid",chatid);
                openRestaurantActivityIntent.putExtra(getPackageName() + ".day",day);
                startActivity(openRestaurantActivityIntent);
            }
        });
        list.setSelectionAfterHeaderView();
    }

    //initializes the location manager (gps and network based)
    private void initLocationManager(){
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 10, this);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 10000, 10, this);
    }

    //creates a String containing the address from a location in coordinates lat/long
    private void setAddressFromLocation(){
        Geocoder geocoder;
        List<Address> addresses = null;
        String address;
        geocoder = new Geocoder(context, Locale.getDefault()); //asks for context i give it activity

        try {
            addresses = geocoder.getFromLocation(Double.parseDouble(lat), Double.parseDouble(lon), 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(addresses!=null) {
            address = addresses.get(0).getAddressLine(0)+" "+addresses.get(0).getLocality();;
        }else{
            address = "Unknown Location name";
        }
        place.setText(address);
    }

    //gets the coordinates from the input string, representing an address
    public void setLocationFromAddress(String strAddress){

        Geocoder coder = new Geocoder(this);
        List<Address> address;
        try {
            address = coder.getFromLocationName(strAddress,5);
            if (address==null) {
                return;
            }
            Address location=address.get(0);
            lat= String.valueOf(location.getLatitude());
            lon= String.valueOf(location.getLongitude());
            System.out.println("Address "+strAddress+" matched to "+lat+" "+lon);
            setAddressFromLocation();
            fetchListWrapper();

        } catch (Exception e){
            Toast.makeText(ResultsActivity.this, "Input address can't be matched to an actual location", Toast.LENGTH_LONG).show();
        }
    }

    //an intent to activate gps is created when gps is off, otherwise informs the user to wait for the device to track location
    private void promptLocationUnavailable() {
        int off = 0;
        try {
            off = Settings.Secure.getInt(getContentResolver(), Settings.Secure.LOCATION_MODE);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        if (off == 0) {
            Toast.makeText(ResultsActivity.this, "Current location not found, consider turning on GPS", Toast.LENGTH_LONG).show();
            Intent onGPS = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(onGPS);
        } else {
            Toast.makeText(ResultsActivity.this, "Current location not found, try again in few seconds", Toast.LENGTH_LONG).show();
        }
    }

    //when current location is detected, lat and long variables are updated
    @Override
    public void onLocationChanged(Location location) {
        gps_lat = String.valueOf(location.getLatitude());
        gps_lon = String.valueOf(location.getLongitude());
        System.out.println("Latitude:" + location.getLatitude() + ", Longitude:" + location.getLongitude());
        Toast.makeText(getApplicationContext(), "Position received, consider turning off GPS to save battery", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.d("Latitude", "disable");
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.d("Latitude", "enable");
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.d("Latitude", "status");
    }

    //in android 6.0+ GPS permissions must be granted at runtime
    private void askPermission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String[] perms = {"android.permission.ACCESS_FINE_LOCATION"};
            requestPermissions(perms, 200);
        } else {
            Toast.makeText(ResultsActivity.this, "You need to enable GPS permission from settings.", Toast.LENGTH_LONG).show();
        }
    }
    //updates locationManager when permissions for GPS in Android 6.0+ are given
    @Override
    public void onRequestPermissionsResult(int permsRequestCode, String[] permissions, int[] grantResults){
        switch(permsRequestCode){
            case 200:
                if(grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    initLocationManager();
                }
                break;
        }
    }

    //creates a Menu with help option
    public boolean onCreateOptionsMenu(Menu menu){
        super.onCreateOptionsMenu(menu);
        int base=Menu.FIRST;
        MenuItem item1=menu.add(base,1,1,"Help");
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        System.out.println("item="+item.getItemId());
        if (item.getItemId()==1){
            if(intento.equals("restaurant")) {
                Toast.makeText(this, "Click on GPS button to find closest restaurants.\n" +
                                "Click on Search button to find restaurant near to the specified location.",Toast.LENGTH_LONG).show();
                Toast.makeText(this,"Tap on a restaurant's name to see more details.\n" +
                                "Tap on the map preview to find it on Google Maps.",
                        Toast.LENGTH_LONG).show();
            }else if(intento.equals("cinema")){
                Toast.makeText(this, "Click on a Movie to receive the schedules in available cinemas." ,
                        Toast.LENGTH_LONG).show();
            }
        }else {
            return super.onOptionsItemSelected(item);
        }return true;
    }
}
