package com.rj.android.nnews;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

public class DetailActivity extends AppCompatActivity {


    private static final String LOG_TAG = DetailActivity.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        if(savedInstanceState==null&&MainActivity.mTwoPane!=true)
        {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragmentDetail,new DetailFragment())
                    .commit();
        }
    }
}
