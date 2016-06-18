package com.rj.android.nnews;

import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.rj.android.nnews.sync.SyncAdapter;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener
        ,MainFragment.Callback{


    private static final String LOG_TAG = MainFragment.class.getSimpleName();
    static boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        Log.d(LOG_TAG, " ON CREATE: ");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(com.rj.android.nnews.R.id.toolbar);
        setSupportActionBar(toolbar);


        DrawerLayout drawer = (DrawerLayout) findViewById(com.rj.android.nnews.R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, com.rj.android.nnews.R.string.navigation_drawer_open, com.rj.android.nnews.R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(com.rj.android.nnews.R.id.nav_view);


        navigationView.setNavigationItemSelectedListener(this);



        if(findViewById(com.rj.android.nnews.R.id.fragmentDetail)!=null)
        {

            mTwoPane = true;
            if(savedInstanceState==null)
            {
                String tag=null;
                FragmentManager fm = getFragmentManager();
                Fragment fragment = new DialogFragment();
                FragmentTransaction ft  = fm.beginTransaction();
                ft.addToBackStack(tag);
                ft.replace(com.rj.android.nnews.R.id.fragmentDetail, fragment);
                ft.commit();
               /* getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragmentDetail, new DetailFragment())
                        .commit();*/
            }

        }
        else
        {
            mTwoPane = false;
        }
        // account created and content is passed safely
        SyncAdapter.initializeSyncAdapter(this);


        MainFragment mainFragment = ((MainFragment)getSupportFragmentManager().findFragmentById(com.rj.android.nnews.R.id.myfragment));
        mainFragment.setTwoPane(mTwoPane);



        Context context = this;
        SharedPreferences sharedPreferences = context.getSharedPreferences("UrlDetails", Context.MODE_PRIVATE);

        String urlKey = context.getString(R.string.url);
        String KeySaved = context.getString(R.string.keySaved);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        if(savedInstanceState==null) {

            String saveUrl = "", saveKeyName = "";

            saveUrl="https://api.nytimes.com/svc/topstories/v2/world.json?api-key=b7e41169ccbf43e7b05bb69b2dadfb66";
            saveKeyName="top_stories";

            editor.putString(urlKey, saveUrl);
            editor.putString(KeySaved, saveKeyName);

            editor.commit();
        }

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(com.rj.android.nnews.R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(com.rj.android.nnews.R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == com.rj.android.nnews.R.id.action_settings) {
            return true;
        }


        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {


        Log.d(LOG_TAG, " ON NAVIGATION ITEM SELECTED : ");
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Context context = this;
        SharedPreferences sharedPreferences = context.getSharedPreferences("UrlDetails", Context.MODE_PRIVATE);

        String urlKey = context.getString(R.string.url);
        String KeySaved = context.getString(R.string.keySaved);
        SharedPreferences.Editor editor = sharedPreferences.edit();


        String saveUrl =""  , saveKeyName ="" ;


        if (id == com.rj.android.nnews.R.id.top_stories) {
            saveUrl="https://api.nytimes.com/svc/topstories/v2/world.json?api-key=b7e41169ccbf43e7b05bb69b2dadfb66";
            saveKeyName="top_stories";
        } else if (id == com.rj.android.nnews.R.id.newswire) {

            saveUrl="https://api.nytimes.com/svc/news/v3/content/all/all/24.json?api-key=b7e41169ccbf43e7b05bb69b2dadfb66";
            saveKeyName="newswire";

        } else if (id == com.rj.android.nnews.R.id.movie_reviews) {

            saveUrl="https://api.nytimes.com/svc/movies/v2/reviews/all.json?api-key=b7e41169ccbf43e7b05bb69b2dadfb66";
            saveKeyName="movie_reviews";

        } else if (id == com.rj.android.nnews.R.id.nav_share) {

        } else if (id == com.rj.android.nnews.R.id.nav_send) {

        }

        editor.putString(urlKey, saveUrl);
        editor.putString(KeySaved, saveKeyName);

        editor.commit();

        SyncAdapter.syncImmediately(this);


        MainFragment mainFragment = (MainFragment)getSupportFragmentManager().findFragmentById(R.id.myfragment);
        mainFragment.onResume();
        DrawerLayout drawer = (DrawerLayout) findViewById(com.rj.android.nnews.R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onItemSelected() {
        DetailFragment fragment = new DetailFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(com.rj.android.nnews.R.id.fragmentDetail,fragment)
                .commit();
    }

}
