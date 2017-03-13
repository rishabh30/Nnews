package com.rj.android.nnews;

import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.rj.android.nnews.NestedFragment.MostViwedFragment;
import com.rj.android.nnews.NestedFragment.MoviesFragment;
import com.rj.android.nnews.NestedFragment.TopNewsFragment;
import com.rj.android.nnews.NestedFragment.TopNewsFragmentRecycle;
import com.rj.android.nnews.Settings.SettingsActivity;
import com.rj.android.nnews.Sync.SyncAdapter;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener
        , TopNewsFragmentRecycle.Callback, MostViwedFragment.Callback, MoviesFragment.Callback{

    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    public static boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Log.d(LOG_TAG, " ON CREATE: ");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setElevation(0);
        getSupportActionBar() .setTitle(Html.fromHtml("<font color=\"@color/white\">" + "Nnews" + "</font>"));

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);

        navigationView.setNavigationItemSelectedListener(this);

        if (findViewById(R.id.fragmentDetail) != null) {

            mTwoPane = true;
            if (savedInstanceState == null) {
                String tag = null;
                FragmentManager fm = getFragmentManager();
                Fragment fragment = new DialogFragment();
                FragmentTransaction ft = fm.beginTransaction();
                ft.addToBackStack(tag);
                ft.replace(R.id.fragmentDetail, fragment);
                ft.commit();
               /* getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragmentDetail, new DetailFragment())
                        .commit();*/
            }

        } else {
            mTwoPane = false;
        }

        Context context = this;
        SharedPreferences sharedPreferences = context.getSharedPreferences("UrlDetails", Context.MODE_PRIVATE);

        String urlKey = context.getString(R.string.url);
        String KeySaved = context.getString(R.string.keySaved);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        MainFragment mainFragment = ((MainFragment) getSupportFragmentManager().findFragmentById(R.id.myfragment));
        mainFragment.setTwoPane(mTwoPane);

        String saveUrl = "", saveKeyName = "";

        saveUrl = "https://api.nytimes.com/svc/topstories/v2/world.json?api-key=b7e41169ccbf43e7b05bb69b2dadfb66";
        saveKeyName = "top_stories";
        editor.putString(urlKey, saveUrl);
        editor.putString(KeySaved, saveKeyName);

        editor.commit();
        mainFragment.setCurrentTab(1);
        //navigationView.getMenu().getItem(1).setChecked(true);
        // account created and content is passed safely
        SyncAdapter.initializeSyncAdapter(this);
        SyncAdapter.syncImmediately(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));
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
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
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

        int pos = 0;
        String saveUrl = "", saveKeyName = "";

       if (id == R.id.top_stories) {
            pos = 0;
            saveUrl = "https://api.nytimes.com/svc/topstories/v2/world.json?api-key=b7e41169ccbf43e7b05bb69b2dadfb66";
            saveKeyName = "top_stories";
        } else if (id == R.id.newswire) {

            pos = 1;
            saveUrl = "https://api.nytimes.com/svc/news/v3/content/iht/all.json?api-key=b7e41169ccbf43e7b05bb69b2dadfb66";
            saveKeyName = "newswire";

        } else if (id == R.id.movie_reviews) {
            pos = 2;
            saveUrl = "https://api.nytimes.com/svc/movies/v2/reviews/picks.json?api-key=b7e41169ccbf43e7b05bb69b2dadfb66";
            saveKeyName = "movie_reviews";

        } else if (id == R.id.action_setting) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);

        } else if (id == R.id.feedback) {
            Intent Email = new Intent(Intent.ACTION_SEND);
            Email.setType("text/email");
            Email.putExtra(Intent.EXTRA_EMAIL, new String[] { "rishabhjainps2@gmail.com" });
            Email.putExtra(Intent.EXTRA_SUBJECT, "Feedback");
            Email.putExtra(Intent.EXTRA_TEXT, "Hello Rishabh ," + "");
            startActivity(Intent.createChooser(Email, "Send Feedback:"));
            return true;
        }

        editor.putString(urlKey, saveUrl);
        editor.putString(KeySaved, saveKeyName);

        editor.commit();

        SyncAdapter.syncImmediately(this);
        MainFragment mainFragment = (MainFragment) getSupportFragmentManager().findFragmentById(R.id.myfragment);
        mainFragment.setCurrentTab(pos);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    public void onItemSelected() {
        DetailFragment fragment = new DetailFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentDetail, fragment)
                .commit();
    }
}