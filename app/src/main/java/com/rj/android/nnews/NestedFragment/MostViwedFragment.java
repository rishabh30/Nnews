package com.rj.android.nnews.NestedFragment;

import android.accounts.Account;
import android.annotation.TargetApi;
import android.app.ActivityOptions;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SyncStatusObserver;
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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.rj.android.nnews.Adapter.ArticleListAdapter;
import com.rj.android.nnews.DetailActivity;
import com.rj.android.nnews.MainActivity;
import com.rj.android.nnews.R;
import com.rj.android.nnews.Sync.SyncAdapter;
import com.rj.android.nnews.Utility;
import com.rj.android.nnews.data.Contract;

public class MostViwedFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = MostViwedFragment.class.getSimpleName();
    private static final int FORECAST_LOADER = 1;
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
    boolean mTwoPane;

    boolean isRefresh = true;
    int mPosition;
    ListView main_list;
    ArticleListAdapter madapter;
    SwipeRefreshLayout mySwipeRefreshLayout;

    public static MostViwedFragment newInstance() {
        MostViwedFragment fragmentFirst = new MostViwedFragment();

        Bundle args = new Bundle();

        String saveUrl = "", saveKeyName = "";

        saveUrl = "https://api.nytimes.com/svc/news/v3/content/all/all.json?api-key=b7e41169ccbf43e7b05bb69b2dadfb66";
        saveKeyName = "newswire";

        args.putString("saveUrl", saveUrl);
        args.putString("saveKeyName", saveKeyName);

        fragmentFirst.setArguments(args);
        return fragmentFirst;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.v(LOG_TAG, " onCreate");
        getUrlString = getArguments().getString("saveUrl", "");
        getKeyName = getArguments().getString("saveKeyName", "");

        ContentResolver.addStatusChangeListener(
                ContentResolver.SYNC_OBSERVER_TYPE_PENDING
                        | ContentResolver.SYNC_OBSERVER_TYPE_ACTIVE,
                new

                        MySyncStatusObserver()

        );

    }

    @TargetApi(Build.VERSION_CODES.M)
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Log.v(LOG_TAG, " onCreateView");
        View rootView = inflater.inflate(R.layout.nesyed_fragment2, container, false);

        mySwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swiperefresh);
        mySwipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        Log.i(LOG_TAG, "onRefresh called from SwipeRefreshLayout");

                        // This method performs the actual data-refresh operation.
                        // The method calls setRefreshing(false) when it's finished.
                        updateArticle();
                    }
                }
        );

        main_list = (ListView) rootView.findViewById(R.id.main_list);
        View errorTextView;
        errorTextView = rootView.findViewById(R.id.ErrorInfo);

        madapter = new ArticleListAdapter(getActivity(), null, 0);
        main_list.setEmptyView(errorTextView);
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

                ImageView sharedView = (ImageView) view.findViewById(R.id.list_item_image);
                if (MainActivity.mTwoPane) {
                    ((Callback) getParentFragment().getActivity()).onItemSelected();
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

                        Bundle bundle = ActivityOptions.makeSceneTransitionAnimation
                                (getParentFragment().getActivity(),
                                        sharedView, sharedView.getTransitionName()
                                )
                                .toBundle();
                        Intent intent = new Intent(getActivity(), DetailActivity.class);
                        startActivity(intent, bundle);
                    } else {
                        Intent intent = new Intent(getActivity(), DetailActivity.class);
                        startActivity(intent);
                    }
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
        setRefereshLayout();
        madapter.swapCursor(data);

        if (mPosition != ListView.INVALID_POSITION && mTwoPane == true) {
            main_list.setSelection(mPosition);
        }
        setEmptyInfo();
    }

    private void setEmptyInfo() {

        if (main_list.getCount() == 0) {
            TextView errorTextView;
            int message = R.string.no_info_available;
            errorTextView = (TextView) getView().findViewById(R.id.ErrorInfo);
            if (errorTextView != null) {
                if (!Utility.isNetworkAvailable(getContext())) {
                    message = R.string.no_network;
                }
            }

            errorTextView.setText(message);
        }

    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String sortOrder = Contract.Article.PUBLISH_DATE + " DESC LIMIT " + Utility.get_noi_list(getContext());

        Log.v(LOG_TAG, " onCreateLoader");


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

        Log.v(LOG_TAG, " UpdateArticle");

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

        Log.v(LOG_TAG, " onLoader Reset");
        madapter.swapCursor(null);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Log.v(LOG_TAG, " Activity Created ");
        getLoaderManager().initLoader(FORECAST_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }


    void setRefereshLayout() {
        if (isRefresh == false) {
            mySwipeRefreshLayout.setRefreshing(false);
            Log.i("REFRESH ", "END");
            isRefresh = true;
        }
    }

    @Override
    public void onStart() {

        Log.v(LOG_TAG, " START");
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.v(LOG_TAG, " RESUME");

        String KeyName = "newswire";

        Uri articleUri = Contract.Article.CONTENT_URI.buildUpon().appendPath(KeyName)
                .build();

        Log.d(LOG_TAG, " onCreateLoader: ");
        Cursor cursor = getContext().getContentResolver().query(
                articleUri,
                ArticleColumns,
                null,
                null,
                null
        );
        if (!cursor.moveToFirst()) {
            updateArticle();
        }

        getLoaderManager().restartLoader(FORECAST_LOADER, null, this);
    }

    public interface Callback {
        void onItemSelected();
    }

    private class MySyncStatusObserver implements SyncStatusObserver {
        @Override
        public void onStatusChanged(int which) {

            String MY_AUTHORITY = "com.rj.android.nnews";
            Account mAccount = new Account("Nnews", "android.rj.com");
            if (which == ContentResolver.SYNC_OBSERVER_TYPE_PENDING) {
                // 'Pending' state changed.
                if (ContentResolver.isSyncPending(mAccount, MY_AUTHORITY)) {
                    // There is now a pending sync.
                } else {
                    // There is no longer a pending sync.
                }
            } else if (which == ContentResolver.SYNC_OBSERVER_TYPE_ACTIVE) {
                // 'Active' state changed.
                if (ContentResolver.isSyncActive(mAccount, MY_AUTHORITY)) {
                    // There is now an active sync.zzz
                    Log.i("Sync Adapter 2", "Start");
                } else {
                    Log.i("Sync Adapter 2", "End");
                    isRefresh = false;
                    // There is no longer an active sync.
                }
            }
        }
    }
}
