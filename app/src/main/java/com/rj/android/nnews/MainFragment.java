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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.rj.android.nnews.data.Contract;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final int COL_ARTICLE_ID = 0;
    public static final int COL_ARTICLE_KEY_ID = 1;
    public static final int COL_ARTICLE_TITLE = 2;
    public static final int COL_ARTICLE_URL = 3;
    public static final int COL_ARTICLE_ABSTRACT = 4;
    public static final int COL_ARTICLE_SOURCE = 5;
    public static final int COL_ARTICLE_PHOTO_HEADING = 6;
    public static final int COL_ARTICLE_PHOTO_URL = 7;
    public static final int COL_ARTICLE_PUBLISH_DATE = 8;
    private static final int FORECAST_LOADER = 0;

    boolean mTwoPane;


    String most_viewed_url = "https://api.nytimes.com/svc/topstories/v2/world.json?api-key=b7e41169ccbf43e7b05bb69b2dadfb66";
    ArticleListAdapter madapter;
    String[] textinfo = new String[15];
    String[] ArticleColumns = {
            Contract.Article._id,
            Contract.Article.KEY_ID,
            Contract.Article.TITLE,
            Contract.Article.ARTICLE_URL,
            Contract.Article.ABSTRACT,
            Contract.Article.SOURCE,
            Contract.Article.PHOTO_HEADING,
            Contract.Article.PHOTO_URL,
            Contract.Article.PUBLISH_DATE
    };


    public interface  Callback {
        public void onItemSelected();
    }


    public void setTwoPane(boolean twoPane)
    {
        mTwoPane = twoPane;
    }

    public MainFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(com.rj.android.nnews.R.menu.main_fragment_menu, menu);
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == com.rj.android.nnews.R.id.action_refresh) {

            updateArticle();
            /*String JsonData = getJsonData();

            parseJson(JsonData);*/
            Toast.makeText(getContext(), "Refreshed", Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }


    @TargetApi(Build.VERSION_CODES.M)
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(com.rj.android.nnews.R.layout.fragment_main, container, false);


        List<String> sample_Data = new ArrayList<String>(Arrays.asList(textinfo));
        ListView main_list = (ListView) rootView.findViewById(com.rj.android.nnews.R.id.main_list);

        //madapter = new CustomAdapter(getContext(), textinfo);
         /* madapter = new SimpleCursorAdapter(
            getActivity(),
                  R.layout.list_item_layout2,
                  null,
                  new String[]{
                          Contract.Article.TITLE,
                  },
                  new int[]{
                        R.id.list_item_title
                  },0
          );*/

        madapter = new ArticleListAdapter(getActivity(), null, 0);

     /*   madapter =new ArrayAdapter(
                getActivity(),
                R.layout.temporary_textview,
                R.id.list_item_textView,
                sample_Data
        );*/


        main_list.setAdapter(madapter);
        main_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {



                Log.d("MAIN FRAGMENT   ", "CLICKED  position  " + position + " id  " + id);

                SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                int articleId = (int) id;
                Log.d("MAIN FRAGMENT   ", "CLICKED  position  " + position + " id  " + id);
                editor.putInt("ARTICLE_ID", articleId);
                editor.commit();

                if(MainActivity.mTwoPane)
                {
                    ((Callback)getActivity()).onItemSelected();
                }
                else {

                    Intent intent = new Intent(getActivity(), DetailActivity.class);
                    startActivity(intent);
                }



            }
        });


        return rootView;
    }

    private void updateArticle() {
        FetchArticleTask Task = new FetchArticleTask(getActivity());
        URL url = null;
        try {
            url = new URL(most_viewed_url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        Task.execute(url);
    }

    @Override
    public void onStart() {
        super.onStart();
        updateArticle();
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getLoaderManager().initLoader(FORECAST_LOADER, null, this);

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String sortOrder = Contract.Article.PUBLISH_DATE + " DESC ";
        Log.d("cursor", "onCreate: ");
        Uri articleUri = Contract.Article.CONTENT_URI.buildUpon().
                appendQueryParameter(Contract.Key_Type.KEY_NAME, "Most-Viewed")
                .build();
        Log.d("cursor", "onCreateLoader: ");
        return new CursorLoader(
                getActivity(),
                articleUri,
                ArticleColumns,
                null,
                null,
                sortOrder
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        madapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        madapter.swapCursor(null);

    }

/*
    @TargetApi(Build.VERSION_CODES.M)
    private String getJsonData() {
        String JsonData = " ";

        try {
            URL url = new URL(most_viewed_url);
            new Downloaddata().execute(url);

            Toast.makeText(getContext(), "Success", Toast.LENGTH_SHORT).show();
        } catch (MalformedURLException e) {
            e.printStackTrace();
            e.printStackTrace();
        }

        Log.v("JSON Data : ", JsonData);
        return JsonData;
    }

*/
}
