package com.example.mychatbot;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

/**
 * Created by david on 08/05/2017.
 */

public class MovieActivity extends AppCompatActivity {
    private TextView extras;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);
        Intent intent = getIntent();
        String i = intent.getStringExtra(getPackageName() + ".intent");
        //extras = (TextView) findViewById(R.id.extras);
        //extras.setText(i);
    }
}
