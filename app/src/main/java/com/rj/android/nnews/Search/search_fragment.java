package com.rj.android.nnews.Search;

import android.annotation.TargetApi;
import android.app.ActivityOptions;
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
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.rj.android.nnews.Adapter.ArticleAdapter;
import com.rj.android.nnews.MainActivity;
import com.rj.android.nnews.NestedFragment.TopNewsFragmentRecycle;
import com.rj.android.nnews.data.Contract;
import com.rj.android.nnews.DetailActivity;
import com.rj.android.nnews.MainFragment;
import com.rj.android.nnews.R;
import com.rj.android.nnews.Sync.SyncAdapter;
import com.rj.android.nnews.Utility;


public class search_fragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    public interface  Callback {
        public void onItemSelected();
    }

    String getUrlString , getKeyName;

    public static Fragment newInstance(int pos, String title) {
        Fragment fragmentFirst = new search_fragment();

        Bundle args = new Bundle();

        String saveUrl =""  , saveKeyName ="" ;

        saveUrl = "https://api.nytimes.com/svc/search/v2/articlesearch.json?api-key=b7e41169ccbf43e7b05bb69b2dadfb66&q="+title;
        saveKeyName = "search";

        args.putString("saveUrl", saveUrl);
        args.putString("saveKeyName", saveKeyName);

        fragmentFirst.setArguments(args);
        return fragmentFirst;
    }
    private static final String LOG_TAG = MainFragment.class.getSimpleName();

    private static final int FORECAST_LOADER = 4;

    boolean mTwoPane;
    int mPosition;
    String SELECTED_KEY="POSITION";
    RecyclerView mRecycleView;

    ArticleAdapter madapter;
    String[] textinfo = new String[15];
    String[] ArticleColumns = {
            Contract.Article._id,
            Contract.Article.TABLE_NAME + "." +Contract.Article.KEY_ID,
            Contract.Article.TITLE,
            Contract.Article.ARTICLE_URL,
            Contract.Article.ABSTRACT,
            Contract.Article.SOURCE,
            Contract.Article.PHOTO_HEADING,
            Contract.Article.PHOTO_URL_HIGH,
            Contract.Article.PUBLISH_DATE,
            Contract.Article.PHOTO_URL
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getUrlString = getArguments().getString("saveUrl", "");
        getKeyName = getArguments().getString("saveKeyName","");
    }

    SwipeRefreshLayout mySwipeRefreshLayout;

    @TargetApi(Build.VERSION_CODES.M)
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.nested_fragment, container, false);

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

        mRecycleView = (RecyclerView) rootView.findViewById(R.id.main_list);
        mRecycleView.setHasFixedSize(true);

        View errorTextView;
        errorTextView = rootView.findViewById(R.id.ErrorInfo);
        madapter = new ArticleAdapter(getActivity(), new ArticleAdapter.ListItemClickListener() {
            @Override
            public void onListItemClick(long id, ArticleAdapter.ViewHolder vh, ImageView imageView) {


                SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                int articleId = (int) id;

                editor.putInt("ARTICLE_ID", articleId);
                editor.commit();

                ImageView sharedView = (ImageView) imageView;
                if (MainActivity.mTwoPane) {
                    ((TopNewsFragmentRecycle.Callback) getParentFragment().getActivity()).onItemSelected();
                } else {
                        Intent intent = new Intent(getActivity(), DetailActivity.class);
                        startActivity(intent);
                }

                mPosition = vh.getAdapterPosition();
            }
        },errorTextView);
        // mRecycleView.setEmptyView(errorTextView);
        mRecycleView.setLayoutManager((new GridLayoutManager(getActivity(),2)));
        mRecycleView.setAdapter(madapter);

//        mRecycleView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//
//                mPosition = position;
//
//                Log.d(LOG_TAG, "CLICKED  position  " + position + " id  " + id);
//
//                SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
//                SharedPreferences.Editor editor = sharedPreferences.edit();
//                int articleId = (int) id;
//                Log.d(LOG_TAG, "CLICKED  position  " + position + " id  " + id);
//                editor.putInt("ARTICLE_ID", articleId);
//                editor.commit();
//
//                ImageView sharedView = (ImageView) view.findViewById(R.id.list_item_image);
//                if (MainActivity.mTwoPane) {
//                    ((Callback) getParentFragment().getActivity()).onItemSelected();
//                } else {
//
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//
//                        sharedView.setVisibility(View.VISIBLE);
//                        Bundle bundle = ActivityOptions.makeSceneTransitionAnimation
//                                (getParentFragment().getActivity(),
//                                        sharedView, sharedView.getTransitionName()
//                                )
//                                .toBundle();
//                        Intent intent = new Intent(getActivity(), DetailActivity.class);
//                        startActivity(intent, bundle);
//                    } else {
//                        Intent intent = new Intent(getActivity(), DetailActivity.class);
//                        startActivity(intent);
//                    }
//                }
//            }
//        });

        if (savedInstanceState != null && savedInstanceState.containsKey(SELECTED_KEY)) {
            mPosition = savedInstanceState.getInt(SELECTED_KEY);
        }
        madapter.setUseMainLayout(mTwoPane);
        return rootView;
    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
        Log.d(LOG_TAG, " ON Saved instance state: ");
        if(mPosition!=ListView.INVALID_POSITION)
        {
            outState.putInt(SELECTED_KEY, mPosition);
        }

        super.onSaveInstanceState(outState);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.d("cursor", "onCreate: ");

        updateArticle();


        String KeyName = "search";

        Uri articleUri = Contract.Article.CONTENT_URI.buildUpon().appendPath(KeyName)
                .build();

        Log.d(LOG_TAG, " onCreateLoader: ");
        return new CursorLoader(
                getActivity(),
                articleUri,
                ArticleColumns,
                null,
                null,
                null
        );
    }
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        Log.d(LOG_TAG, " onLoaderFinished: ");

        madapter.swapCursor(data);

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


    private void updateArticle()
    {
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
    public void onStart() {
        super.onStart();
    }

    public void callResume() {
        updateArticle();onResume();
    }

    @Override
    public void onResume() {
        super.onResume();
        getLoaderManager().restartLoader(FORECAST_LOADER, null, this);

        mySwipeRefreshLayout.setRefreshing(false);
    }
}
