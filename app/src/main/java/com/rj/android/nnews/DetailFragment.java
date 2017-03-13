 package com.rj.android.nnews;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.rj.android.nnews.data.Contract;

public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final String LOG_TAG = DetailFragment.class.getSimpleName();
    private static final int DETAIL_LOADER = 0;

    public ImageView mImage;
    public TextView mHeading;
    public TextView mDate;

    public TextView mTitle;
    public TextView mAbstract;
    public TextView mSource;
    public TextView mNYTimes;
    Cursor oldData=null;

    String mMessage = null;

    String[] ArticleColumns = {
            Contract.Article._id,
            Contract.Article.TABLE_NAME + "." +Contract.Article.KEY_ID,
            Contract.Article.TITLE,
            Contract.Article.ARTICLE_URL,
            Contract.Article.ABSTRACT,
            Contract.Article.SOURCE,
            Contract.Article.PHOTO_HEADING,
            Contract.Article.PHOTO_URL_HIGH,
            Contract.Article.PUBLISH_DATE
    };

    public DetailFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu , MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.fragment_detail, menu);
        MenuItem menuItem = menu.findItem(R.id.action_share);

        ShareActionProvider mShareActionProvider
                =  (ShareActionProvider)MenuItemCompat.getActionProvider(menuItem);

        if(mShareActionProvider!=null)
        {
            mShareActionProvider.setShareIntent(createShareIntent());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View rootView =  inflater.inflate(com.rj.android.nnews.R.layout.fragment_detail, container, false);

        mImage = (ImageView)rootView.findViewById(R.id.detail_image);
        mHeading= (TextView)rootView.findViewById(R.id.detail_photo_heading);
        mDate= (TextView)rootView.findViewById(R.id.detail_date);

        mTitle= (TextView)rootView.findViewById(R.id.detail_title);
        mAbstract= (TextView)rootView.findViewById(R.id.detail_abstract);
        mSource= (TextView)rootView.findViewById(R.id.detail_source);
        mNYTimes= (TextView)rootView.findViewById(R.id.NYTimes);
        return  rootView;

    }

    @Override
    public Loader onCreateLoader(int id, Bundle args) {

        Log.v(LOG_TAG , "on Loader Created");

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        int articleId = sharedPreferences.getInt("ARTICLE_ID", 1);

        String sortOrder = Contract.Article.PUBLISH_DATE + " DESC ";
        Log.d(LOG_TAG, "onCreate: ");
        Uri articleUri = ContentUris.withAppendedId(Contract.Article.CONTENT_URI , articleId);
        Log.d(LOG_TAG, "CLICKED  position  " + articleId +   " id  "+id);
        Log.d(LOG_TAG, "onCreateLoader: ");
        Log.v(LOG_TAG, articleUri.toString());
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
    public void onResume() {
        super.onResume();
        getLoaderManager().initLoader(DETAIL_LOADER,null,this);
        onCreateLoader(DETAIL_LOADER, null);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.v(LOG_TAG, "on Activity created");
        getLoaderManager().initLoader(DETAIL_LOADER, null, this);
    }

    @Override
    public void onLoadFinished(Loader loader, Cursor data) {
        Log.v(LOG_TAG, "on Loader Finished");
       if(!data.moveToFirst())
        {
            oldData = data;
        }
        else if (oldData!=null)
        {
            data =oldData;
        }

        if (data.moveToFirst())
        {

            String imageUrl = data.getString(
                    data.getColumnIndex(Contract.Article.PHOTO_URL_HIGH));
            String heading= data.getString(
                    data.getColumnIndex(Contract.Article.PHOTO_HEADING));
            if(heading=="null")
                heading="";
            String date  = data.getString(
                data.getColumnIndex(Contract.Article.PUBLISH_DATE));

            try {
                date = Utility.getDatabaseDate(date);
            } catch (Exception e) {
                e.printStackTrace();
                date = null;
            }

            String Title= data.getString(
                data.getColumnIndex(Contract.Article.TITLE));
            String abs= data.getString(
                data.getColumnIndex(Contract.Article.ABSTRACT));
            String Source= data.getString(
                data.getColumnIndex(Contract.Article.SOURCE));

            if(Title=="null")
                Title="";

            final String articleUrl = data.getString(
                    data.getColumnIndex(Contract.Article.ARTICLE_URL)
            );
            mImage.setVisibility(View.VISIBLE);
            mMessage = Title + " - " + articleUrl;
            if(imageUrl.matches("no"))
            {
                mImage.setImageResource(R.drawable.noblogo);
            }
            else {

                Glide.with(getActivity())
                        .load(imageUrl)
                        .fitCenter()
                        .placeholder(R.drawable.progress_animation)
                        .centerCrop()
                        .into(mImage);
            }

            mHeading.setText(heading);
            mDate.setText(date);
            mTitle.setText(Title);
            mAbstract.setText(abs);
            mSource.setText(Source);
            mNYTimes.setClickable(true);
            mNYTimes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(articleUrl));
                    startActivity(i);
                }
            });

        }else {
            Log.v(LOG_TAG," NO DATA");
        }
    }

    @Override
    public void onLoaderReset(Loader loader) {
    }

    private Intent createShareIntent()
    {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT ,  mMessage);
        return shareIntent;
    }
}