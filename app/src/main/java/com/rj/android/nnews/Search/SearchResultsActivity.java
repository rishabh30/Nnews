package com.rj.android.nnews.Search;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.rj.android.nnews.R;

public class SearchResultsActivity extends AppCompatActivity {


    private static final String LOG_TAG = SearchResultsActivity.class.getSimpleName();
    static boolean mTwoPane;
    String getUrlString, getKeyName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        ActionBar actionBar = getSupportActionBar();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        handleIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {

            String query = intent.getStringExtra(SearchManager.QUERY);

            getUrlString = "https://api.nytimes.com/svc/search/v2/articlesearch.json?api-key=b7e41169ccbf43e7b05bb69b2dadfb66&q=" + query;
            getKeyName = "search";

            String tag = null;
            FragmentManager fm = getSupportFragmentManager();
            Fragment fragment = search_fragment.newInstance(1, query);
            FragmentTransaction ft = fm.beginTransaction();
            ft.replace(R.id.search_fragment, fragment, "TAG");
            ft.commit();

        }
    }
}
