package com.aadimator.android.myappportfolio;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        makeToast(R.id.popularMovies, R.string.popular_movies);
        makeToast(R.id.stockHawk, R.string.stock_hawk);
        makeToast(R.id.buildBigger, R.string.build_bigger);
        makeToast(R.id.materialApp, R.string.material_app);
        makeToast(R.id.goUbiquitous, R.string.go_ubiquitous);
        makeToast(R.id.capstone, R.string.capstone);
    }

    private void makeToast(int buttonID, final int stringRes) {
        Button button = (Button) findViewById(buttonID);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this,
                        "This button will launch my " + getString(stringRes).toLowerCase() + " app!",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}
