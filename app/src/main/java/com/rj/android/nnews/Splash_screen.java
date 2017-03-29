package com.rj.android.nnews;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import com.rj.android.nnews.Sync.SyncAdapter;

public class Splash_screen extends Activity {

    private static int SPLASH_TIME_OUT = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        Context context = getBaseContext();


        SharedPreferences sharedPreferences2 = context.getSharedPreferences("FirstTime", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences2.edit();
        editor.putInt("a", 0);
        editor.commit();

        SyncAdapter.initializeSyncAdapter(this);
        SyncAdapter.syncImmediately(this);
        new Handler().postDelayed(new Runnable() {

            /*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             */

            @Override
            public void run() {
                // This method will be executed once the timer is over
                // Start your app main activity
                Intent i = new Intent(Splash_screen.this, MainActivity.class);
                startActivity(i);

                // close this activity
                finish();
            }
        }, SPLASH_TIME_OUT);

    }
}
