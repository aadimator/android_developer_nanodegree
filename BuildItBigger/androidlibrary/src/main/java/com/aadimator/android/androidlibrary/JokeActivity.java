package com.aadimator.android.androidlibrary;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class JokeActivity extends AppCompatActivity {

    TextView mJokeTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_joke);

        mJokeTextView = (TextView) findViewById(R.id.jokeTextView);

        Intent intent = getIntent();
        String joke = intent.getStringExtra("joke");

        mJokeTextView.setText(joke);
    }
}
