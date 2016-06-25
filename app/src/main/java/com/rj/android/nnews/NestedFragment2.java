package com.rj.android.nnews;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.rj.android.nnews.data.Contract;
import com.rj.android.nnews.sync.SyncAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class NestedFragment2 extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    String getUrlString, getKeyName;
    String SELECTED_KEY = "POSITION";
    String[] ArticleColumns = {
            Contract.Article._id,
            Contract.Article.TABLE_NAME + "." + Contract.Article.KEY_ID,
            Contract.Article.TITLE,
            Contract.Article.ARTICLE_URL,
            Contract.Article.ABSTRACT,
            Contract.Article.SOURCE,
            Contract.Article.PHOTO_HEADING,
            Contract.Article.PHOTO_URL_HIGH,
            Contract.Article.PUBLISH_DATE,
            Contract.Article.PHOTO_URL
    };

    private static final String LOG_TAG = MainFragment.class.getSimpleName();

    public static final int COL_ARTICLE_ID = 0;
    public static final int COL_ARTICLE_KEY_ID = 1;
    public static final int COL_ARTICLE_TITLE = 2;
    public static final int COL_ARTICLE_URL = 3;
    public static final int COL_ARTICLE_ABSTRACT = 4;
    public static final int COL_ARTICLE_SOURCE = 5;
    public static final int COL_ARTICLE_PHOTO_HEADING = 6;
    public static final int COL_ARTICLE_PHOTO_URL_HIGH = 7;
    public static final int COL_ARTICLE_PUBLISH_DATE = 8;
    public static final int COL_ARTICLE_PHOTO_URL = 9;
    private static final int FORECAST_LOADER = 1;

    boolean mTwoPane;
    int mPosition;

    public static NestedFragment2 newInstance(int pos, String title) {
        NestedFragment2 fragmentFirst = new NestedFragment2();

        Bundle args = new Bundle();


        String saveUrl = "", saveKeyName = "";


        saveUrl = "https://api.nytimes.com/svc/news/v3/content/iht/all.json?api-key=b7e41169ccbf43e7b05bb69b2dadfb66";
        saveKeyName = "newswire";


        args.putString("saveUrl", saveUrl);
        args.putString("saveKeyName", saveKeyName);


        fragmentFirst.setArguments(args);
        return fragmentFirst;
    }
    ListView main_list;

    ArticleListAdapter madapter;
    String[] textinfo = new String[15];

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getUrlString = getArguments().getString("saveUrl", "");
        getKeyName = getArguments().getString("saveKeyName", "");
    }
    SwipeRefreshLayout mySwipeRefreshLayout;
    @TargetApi(Build.VERSION_CODES.M)
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.nesyed_fragment2, container, false);

        mySwipeRefreshLayout =(SwipeRefreshLayout)rootView.findViewById(R.id.swiperefresh);
        mySwipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        Log.i(LOG_TAG, "onRefresh called from SwipeRefreshLayout");

                        // This method performs the actual data-refresh operation.
                        // The method calls setRefreshing(false) when it's finished.
                        callResume();
                    }
                }
        );

        List<String> sample_Data = new ArrayList<String>(Arrays.asList(textinfo));
        main_list = (ListView) rootView.findViewById(R.id.main_list);


        madapter = new ArticleListAdapter(getActivity(), null, 0);

        main_list.setAdapter(madapter);


        main_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                mPosition = position;

                Log.d(LOG_TAG, "CLICKED  position  " + position + " id  " + id);

                SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                int articleId = (int) id;
                Log.d(LOG_TAG, "CLICKED  position  " + position + " id  " + id);
                editor.putInt("ARTICLE_ID", articleId);
                editor.commit();

                if (MainActivity.mTwoPane) {
                    ((Callback) getParentFragment().getActivity()).onItemSelected();
                } else {

                    Intent intent = new Intent(getActivity(), DetailActivity.class);
                    startActivity(intent);
                }
            }
        });


        if (savedInstanceState != null && savedInstanceState.containsKey(SELECTED_KEY)) {
            mPosition = savedInstanceState.getInt(SELECTED_KEY);
            main_list.setSelection(mPosition);
        }
        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        Log.d(LOG_TAG, " ON Saved instance state: ");
        if (mPosition != ListView.INVALID_POSITION) {
            outState.putInt(SELECTED_KEY, mPosition);
        }

        super.onSaveInstanceState(outState);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        Log.d(LOG_TAG, " onLoaderFinished: ");

        madapter.swapCursor(data);


        if (mPosition != ListView.INVALID_POSITION && mTwoPane == true) {
            main_list.setSelection(mPosition);
        }
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String sortOrder = Contract.Article.PUBLISH_DATE + " DESC LIMIT 15";
        Log.d("cursor", "onCreate: ");

        updateArticle();

        String KeyName = "newswire";

        Uri articleUri = Contract.Article.CONTENT_URI.buildUpon().appendPath(KeyName)
                .build();

        Log.d(LOG_TAG, " onCreateLoader: ");
        return new CursorLoader(
                getActivity(),
                articleUri,
                ArticleColumns,
                null,
                null,
                sortOrder
        );
    }

    private void updateArticle() {
        Log.d("FUCK ", String.valueOf(1));

        Context context = getContext();
        SharedPreferences SP = context.getSharedPreferences("UrlDetails", Context.MODE_PRIVATE);
        String urlKey = context.getString(R.string.url);
        String Ks = context.getString(R.string.keySaved);

        SharedPreferences.Editor editor = SP.edit();
        editor.putString(urlKey, getUrlString);
        editor.putString(Ks, getKeyName);
        editor.commit();
        SyncAdapter.syncImmediately(getActivity());
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        madapter.swapCursor(null);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getLoaderManager().initLoader(FORECAST_LOADER, null, this);
    }

    public void callResume() {
        updateArticle();
        onResume();
    }

    public interface Callback {
        public void onItemSelected();
    }

    @Override
    public void onStart() {

        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        getLoaderManager().restartLoader(FORECAST_LOADER, null, this);
        mySwipeRefreshLayout.setRefreshing(false);
    }

}
