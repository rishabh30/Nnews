package com.rj.android.nnews;

import android.annotation.TargetApi;
import android.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.rj.android.nnews.data.Contract;
import com.rj.android.nnews.sync.SyncAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SearchResultsActivity extends AppCompatActivity{



    private static final String LOG_TAG = SearchResultsActivity.class.getSimpleName();
    static boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);



        handleIntent(getIntent());
    }


    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    String getUrlString , getKeyName;
    private void handleIntent(Intent intent) {

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            Toast.makeText(this,query,Toast.LENGTH_SHORT).show();


            getUrlString = "https://api.nytimes.com/svc/search/v2/articlesearch.json?api-key=b7e41169ccbf43e7b05bb69b2dadfb66&q="+query;
            getKeyName =  "search";

            String tag=null;
            FragmentManager fm = getSupportFragmentManager();
            Fragment fragment = search_fragment.newInstance(1,query);
            FragmentTransaction ft  = fm.beginTransaction();
            ft.replace(R.id.search_fragment,fragment,"TAG").addToBackStack("TAG");
            ft.commit();

            //use the query to search your data somehow
        }
    }

}
