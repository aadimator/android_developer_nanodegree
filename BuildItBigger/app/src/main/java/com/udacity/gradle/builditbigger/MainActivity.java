package com.udacity.gradle.builditbigger;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.aadimator.android.androidlibrary.JokeActivity;
import com.example.JokeTeller;


public class MainActivity extends AppCompatActivity {

    Button mTellJokeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final JokeTeller jokeTeller = new JokeTeller();
        mTellJokeButton = (Button) findViewById(R.id.tellJokeBtn);
        mTellJokeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new TellJoke(MainActivity.this).execute();
                Toast.makeText(MainActivity.this, "Getting your delicious joke.....", Toast.LENGTH_LONG).show();
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public class TellJoke extends TellJokeTask {

        private Context mContext;

        public TellJoke(Context context) {
            mContext = context;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s != null) {
                Intent intent = new Intent(mContext, JokeActivity.class);
                intent.putExtra("joke", JokeTeller.tellJoke());
                mContext.startActivity(intent);
            }
        }
    }


}
