package com.rj.android.nnews.sync;


import android.accounts.Account;
import android.accounts.AccountManager;
import android.annotation.TargetApi;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.ArrayAdapter;

import com.bumptech.glide.Glide;
import com.rj.android.nnews.MainActivity;
import com.rj.android.nnews.R;
import com.rj.android.nnews.Utility;
import com.rj.android.nnews.data.Contract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Vector;
import java.util.concurrent.ExecutionException;

public class SyncAdapter extends AbstractThreadedSyncAdapter {
    public final String LOG_TAG = SyncAdapter.class.getSimpleName();



    // Interval at which to sync with the weather, in seconds.
    // 60 seconds (1 minute) * 180 = 3 hours
    public static final int SYNC_INTERVAL = 60 * 180 *3 ;
    public static final int SYNC_FLEXTIME = SYNC_INTERVAL/3;
    private static final long DAY_IN_MILLIS = 1000 * 60 * 60 * 24;
    private static final int NOTIFICATION_ID = 1934;


    String[] ArticleColumns = {
            Contract.Article._id,
            Contract.Article.TABLE_NAME + "." +Contract.Article.KEY_ID,
            Contract.Article.TITLE,
            Contract.Article.ARTICLE_URL,
            Contract.Article.PHOTO_URL,
            Contract.Article.PUBLISH_DATE
    };
    public static final int COL_ARTICLE_ID = 0;
    public static final int COL_ARTICLE_KEY_ID = 1;
    public static final int COL_ARTICLE_TITLE = 2;
    public static final int COL_ARTICLE_URL = 3;
    public static final int COL_ARTICLE_PHOTO_URL = 4;
    public static final int COL_ARTICLE_PUBLISH_DATE = 5;



    ArrayAdapter madapter;
    String[] textinfo = new String[15];

    public static final String RESULTS = "results";
    public static final String TITLE = "title";
    public static final String ABSTRACT = "abstract";
    public static final String P_DATE = "updated_date";
    public static final String SOURCE = "byline";
    public static final String ID = "id";
    public static final String ARITCLE_URL = "url";


    public static final String MEDIA = "media";
    public static final String MULTIMEDIA_ARRAY = "multimedia";
    public static final String PHOTO_CAPTION = "caption";
    public static final String PHOTO_URL = "url";





    public SyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
    }




    private  long addKey(String KeyName , String url )
    {
        Log.v(LOG_TAG, "INSERTING " + KeyName);
        Cursor cursor = getContext().getContentResolver().query(
                Contract.Key_Type.CONTENT_URI,
                new String[]{Contract.Key_Type.KEY_ID},
                Contract.Key_Type.KEY_NAME + " =? ",
                new String[]{KeyName},
                null
        );

        if(cursor.moveToFirst())
        {
            Log.v(LOG_TAG,"FOUND IN DATABASE  " );
            int KeyIdIndex = cursor.getColumnIndex(Contract.Key_Type.KEY_ID);
            long temp =  cursor.getLong(KeyIdIndex);
            cursor.close();
            return temp;
        }
        else
        {
            Log.v(LOG_TAG, "DIDN'T  FOUND IN DATABASE  INSERTING ....");
            ContentValues cv = new ContentValues();
            cv.put(Contract.Key_Type.KEY_NAME,KeyName);
            cv.put(Contract.Key_Type.KEY_URL, url);
            Uri KeyInsertUri = getContext().getContentResolver().insert(Contract.Key_Type.CONTENT_URI,cv);
            cursor.close();
            Cursor cr = getContext().getContentResolver().query(
                    Contract.Key_Type.CONTENT_URI,
                    new String[]{Contract.Key_Type.KEY_ID},
                    Contract.Key_Type.KEY_NAME + " =? ",
                    new String[]{KeyName},
                    null
            );
            Log.v(LOG_TAG, "FOUND IN DATABASE  ");
            if(cr.moveToFirst()) {
                int KeyIdIndex = cr.getColumnIndex(Contract.Key_Type.KEY_ID);
                if (KeyIdIndex != -1)
                    return cr.getInt(KeyIdIndex);
            }
            else
                return 0;
        }
        return  0;

    }



    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void notifyWeather()
    {
        Context context = getContext();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String lastNotificationKey = context.getString(R.string.pref_last_notification);
        long lastSync = prefs.getLong(lastNotificationKey, 0);

        if(System.currentTimeMillis()-lastSync  >= DAY_IN_MILLIS ||true )
        {


            String deleteDate = Utility.getDeleteDate();
            long id = getContext().getContentResolver().delete(Contract.Article.CONTENT_URI
                    , Contract.Article.PUBLISH_DATE + " <= ?"
                    , new String[]{deleteDate});
            Log.v(LOG_TAG, "delete " + id + " rows of weather data");


            String sortOrder = Contract.Article.PUBLISH_DATE + " DESC ";


            String Key_Name ="top_stories";
            Uri uri = Contract.Article.buildUriWithKeyName(Key_Name);

            Cursor cursor = context.getContentResolver()
                    .query(uri, ArticleColumns, null, null, sortOrder);


            if(cursor.moveToFirst())
            {

                String title =context.getString(R.string.app_name);
                String imageUrl = cursor.getString(COL_ARTICLE_PHOTO_URL);
                String contextTitle = cursor.getString(COL_ARTICLE_TITLE);
                String publish_Date = cursor.getString(COL_ARTICLE_PUBLISH_DATE);
                int notifi_photo = R.drawable.notific_photo;
                Resources resources = context.getResources();
                Bitmap Icon = BitmapFactory.decodeResource(resources, notifi_photo);
                try {
                     Icon = Glide.with(context)
                            .load(imageUrl)
                            .asBitmap()
                            .into(100,100)
                            .get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }

                int color =getContext().getResources().getColor(R.color.blue);

                NotificationCompat.Builder mBuilder =
                        new NotificationCompat.Builder(getContext())
                                .setSmallIcon(R.drawable.notic)
                                .setLargeIcon(Icon)
                                .setContentTitle(title)
                                .setStyle(new NotificationCompat.BigTextStyle().bigText(contextTitle))
                                .setColor(color);


                mBuilder.setAutoCancel(true);
                Intent resultIntent = new Intent (context, MainActivity.class);
                TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
                stackBuilder.addNextIntent(resultIntent);

                PendingIntent resultPendingIntent =
                        stackBuilder.getPendingIntent(
                                0,
                                PendingIntent.FLAG_UPDATE_CURRENT
                        );
                mBuilder.setContentIntent(resultPendingIntent);

                NotificationManager mNotificationManager =
                        (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);
                mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());


            }

            SharedPreferences.Editor edit = prefs.edit();
            edit.putLong(lastNotificationKey, System.currentTimeMillis());
            edit.commit();
        }
    }


    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        Log.d(LOG_TAG, "Starting sync");


        String KeyName="Most-Viewed";

        HttpURLConnection urlConnection = null;
        BufferedReader bufferedReader = null;

        String JsonData = "";
        URL url=null;

        try {

            Context context = getContext();
            SharedPreferences sharedPreferences = getContext().getSharedPreferences("UrlDetails", Context.MODE_PRIVATE);
            String urlKey = context.getString(R.string.url);
            String KeySaved = context.getString(R.string.keySaved);
            String urlString = sharedPreferences.getString(urlKey," ");
            KeyName = sharedPreferences.getString(KeySaved," ");

            Log.d(LOG_TAG ,"  KEYName : "+KeyName +"  UrlString : "+urlString   );
            url =new URL(urlString);
           // url = new URL ("https://api.nytimes.com/svc/topstories/v2/world.json?api-key=b7e41169ccbf43e7b05bb69b2dadfb66");
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer stringBuffer = new StringBuffer();
            if (inputStream == null) {
                return;
            }

            bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuffer.append(line + '\n');
            }

            if (stringBuffer.length() == 0) {
                JsonData = null;
            }

            JsonData = stringBuffer.toString();


        } catch (IOException e) {
            Log.e("DOwnLoad dATA eRROR", "Error ", e);
            e.printStackTrace();

        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }

            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e("PlaceholderFragment", "Error closing stream", e);
                }
            }
        }

        Log.d("JSON DATA", JsonData);
        try {
            long KeyId = addKey(KeyName, String.valueOf(url));

            if(KeyName.matches("movie_reviews"))
                getJsonStringArrayForMedia(JsonData,KeyId);
            else if(KeyName.matches("search"))
                getJsonStringArrayForSearch(JsonData,KeyId);
            else
            getJsonStringArray(JsonData,KeyId);

        } catch (JSONException e) {
            Log.e("Download Data ", "JSON ERROR");
            e.printStackTrace();
        }

        return;
    }

    private void getJsonStringArrayForMedia(String jsonData, long KeyId) throws JSONException {

        JSONObject reader = new JSONObject(jsonData);
        JSONArray result = reader.getJSONArray(RESULTS);
        int length = result.length();

        Vector<ContentValues> cVVector = new Vector<ContentValues>(15);
        for (int i = 0; i < length; i++) {
            JSONObject object = result.getJSONObject(i);
            String title = object.getString("display_title");
            String Abstract = object.getString("summary_short");
            String p_date = object.getString("date_updated");
            String source = object.getString("byline");


                    JSONObject multimedia;
                    multimedia = object.getJSONObject("multimedia");

                    String imageUrlHigh = multimedia.getString("src");
                    String imageUrl = multimedia.getString("src");
                    String caption = object.getString("headline");

                    //      String id = object.getString(ID);
                    String aritcle_url = object.getJSONObject("link").getString("url");

                    ContentValues contentValues = new ContentValues();
                    contentValues.put(Contract.Article.KEY_ID, KeyId);
                    //      contentValues.put(Contract.Article.ID, id);
                    contentValues.put(Contract.Article.TITLE, title);
                    contentValues.put(Contract.Article.ARTICLE_URL, aritcle_url);
                    contentValues.put(Contract.Article.ABSTRACT, Abstract);
                    contentValues.put(Contract.Article.SOURCE, source);
                    contentValues.put(Contract.Article.PHOTO_HEADING, caption);
                    contentValues.put(Contract.Article.PHOTO_URL, imageUrl);
                    contentValues.put(Contract.Article.PHOTO_URL_HIGH, imageUrlHigh);
                    contentValues.put(Contract.Article.PUBLISH_DATE, p_date);


                    cVVector.add(contentValues);
        }

        if (cVVector.size() > 0) {
            ContentValues[] cvArray = new ContentValues[cVVector.size()];
            cVVector.toArray(cvArray);
            int rowsInserted = getContext().getContentResolver()
                    .bulkInsert(Contract.Article.CONTENT_URI, cvArray);
            Log.v(LOG_TAG, "inserted " + rowsInserted + " rows of weather data");



        }

    }

    private String[] getJsonStringArray(String jsonData,long KeyId) throws JSONException {


        JSONObject reader = new JSONObject(jsonData);
        JSONArray result = reader.getJSONArray(RESULTS);
        int length = result.length();
        String[] resultStrs = new String[15];

        Vector<ContentValues> cVVector = new Vector<ContentValues>(15);
        for (int i = 0; i < length; i++) {
            JSONObject object = result.getJSONObject(i);
            String title = object.getString(TITLE);
            // JSONArray media = object.getJSONArray(MEDIA);
            //JSONObject hold = media.getJSONObject(0);

            String Abstract = object.getString(ABSTRACT);
            String p_date = object.getString(P_DATE);
            p_date= Utility.getFriendlyDate(p_date);
            String source = object.getString(SOURCE);

            Object metad  = object.get(MULTIMEDIA_ARRAY);
            if(metad instanceof JSONArray) {


                JSONArray metadata = object.getJSONArray(MULTIMEDIA_ARRAY);
                if (metadata.length() > 3) {
                    JSONObject multimedia;
                    if (metadata.length() > 4)
                     multimedia = metadata.getJSONObject(4);
                    else
                    multimedia = metadata.getJSONObject(3);
                    String imageUrlHigh = multimedia.getString(PHOTO_URL);
                    String imageUrl = metadata.getJSONObject(1).getString(PHOTO_URL);
                    String caption = multimedia.getString(PHOTO_CAPTION);

                    //      String id = object.getString(ID);
                    String aritcle_url = object.getString(ARITCLE_URL);


                    ContentValues contentValues = new ContentValues();
                    contentValues.put(Contract.Article.KEY_ID, KeyId);
                    //      contentValues.put(Contract.Article.ID, id);
                    contentValues.put(Contract.Article.TITLE, title);
                    contentValues.put(Contract.Article.ARTICLE_URL, aritcle_url);
                    contentValues.put(Contract.Article.ABSTRACT, Abstract);
                    contentValues.put(Contract.Article.SOURCE, source);
                    contentValues.put(Contract.Article.PHOTO_HEADING, caption);
                    contentValues.put(Contract.Article.PHOTO_URL, imageUrl);
                    contentValues.put(Contract.Article.PHOTO_URL_HIGH, imageUrlHigh);
                    contentValues.put(Contract.Article.PUBLISH_DATE, p_date);


                    cVVector.add(contentValues);
                }
            }
            //    resultStrs [i][1] = imageURL.getString("url");
        }

        if (cVVector.size() > 0) {
            ContentValues[] cvArray = new ContentValues[cVVector.size()];
            cVVector.toArray(cvArray);
            int rowsInserted = getContext().getContentResolver()
                    .bulkInsert(Contract.Article.CONTENT_URI, cvArray);
            Log.v(LOG_TAG, "inserted " + rowsInserted + " rows of weather data");



            notifyWeather();
        }

        textinfo = resultStrs;
        return resultStrs;



    }



    private void getJsonStringArrayForSearch(String jsonData, long KeyId) throws JSONException {

        long id = getContext().getContentResolver().delete(Contract.Article.CONTENT_URI
                , Contract.Article.KEY_ID + " = ?"
                , new String[]{String.valueOf(KeyId)});
        Log.v(LOG_TAG, "delete " + id + " rows of weather data");




        JSONObject reader = new JSONObject(jsonData);
        JSONArray result = reader.getJSONObject("response").getJSONArray("docs");
        int length = result.length();

        Vector<ContentValues> cVVector = new Vector<ContentValues>(55);
        for (int i = 0; i < length; i++) {
            JSONObject object = result.getJSONObject(i);
            String title = object.getString("snippet");
            String Abstract = object.getString("lead_paragraph");
            String p_date = object.getString("pub_date");
            Object sourceObject = object.get("byline");
            String source="";

            String imageUrlHigh = "no";
            String imageUrl = "no";

            Object multimedia;
            multimedia = object.get("multimedia");

            if(multimedia instanceof JSONArray  ) {

                JSONArray metadata = object.getJSONArray("multimedia");
                if(((JSONArray) multimedia).length()>1) {

                    imageUrlHigh ="https://static01.nyt.com/"+ metadata.getJSONObject(1).getString("url");
                    imageUrl = "https://static01.nyt.com/" +metadata.getJSONObject(0).getString("url");
                }
            }
            String caption = object.getJSONObject("headline").getString("main");

            //      String id = object.getString(ID);
            String aritcle_url = object.getString("web_url");

            ContentValues contentValues = new ContentValues();
            contentValues.put(Contract.Article.KEY_ID, KeyId);
            //      contentValues.put(Contract.Article.ID, id);
            contentValues.put(Contract.Article.TITLE, title);
            contentValues.put(Contract.Article.ARTICLE_URL, aritcle_url);
            contentValues.put(Contract.Article.ABSTRACT, Abstract);
            contentValues.put(Contract.Article.SOURCE, source);
            contentValues.put(Contract.Article.PHOTO_HEADING, caption);
            contentValues.put(Contract.Article.PHOTO_URL, imageUrl);
            contentValues.put(Contract.Article.PHOTO_URL_HIGH, imageUrlHigh);
            contentValues.put(Contract.Article.PUBLISH_DATE, p_date);


            cVVector.add(contentValues);


            //    resultStrs [i][1] = imageURL.getString("url");
        }

        if (cVVector.size() > 0) {
            ContentValues[] cvArray = new ContentValues[cVVector.size()];
            cVVector.toArray(cvArray);
            int rowsInserted = getContext().getContentResolver()
                    .bulkInsert(Contract.Article.CONTENT_URI, cvArray);
            Log.v(LOG_TAG, "inserted " + rowsInserted + " rows of weather data");



        }

    }







    /**
     * Helper method to have the sync adapter sync immediately
     * @param context The context used to access the account service
     */
    public static void syncImmediately(Context context) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(getSyncAccount(context),
                context.getString(R.string.content_authority), bundle);
    }

    /**
     * Helper method to get the fake account to be used with SyncAdapter, or make a new one
     * if the fake account doesn't exist yet.  If we make a new account, we call the
     * onAccountCreated method so we can initialize things.
     *
     * @param context The context used to access the account service
     * @return a fake account.
     */
    public static Account getSyncAccount(Context context) {
        // Get an instance of the Android account manager
        AccountManager accountManager =
                (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        // Create the account type and default account
        Account newAccount = new Account(
                context.getString(R.string.app_name), context.getString(R.string.sync_account_type));

        // If the password doesn't exist, the account doesn't exist
        if ( null == accountManager.getPassword(newAccount) ) {

        /*
         * Add the account and account type, no password or user data
         * If successful, return the Account object, otherwise report an error.
         */
            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                return null;
            }
            /*
             * If you don't set android:syncable="true" in
             * in your <provider> element in the manifest,
             * then call ContentResolver.setIsSyncable(account, AUTHORITY, 1)
             * here.
             */

            onAccountCreated(newAccount, context);
        }
        return newAccount;
    }

    /**
     * Helper method to schedule the sync adapter periodic execution
     */
    public static void configurePeriodicSync(Context context, int syncInterval, int flexTime) {
        Account account = getSyncAccount(context);
        String authority = context.getString(R.string.content_authority);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // we can enable inexact timers in our periodic sync
            SyncRequest request = new SyncRequest.Builder().
                    syncPeriodic(syncInterval, flexTime).
                    setSyncAdapter(account, authority).
                    setExtras(new Bundle()).build();
            ContentResolver.requestSync(request);
        } else {
            ContentResolver.addPeriodicSync(account,
                    authority, new Bundle(), syncInterval);
        }
    }


    private static void onAccountCreated(Account newAccount, Context context) {
        /*
         * Since we've created an account
         */
        SyncAdapter.configurePeriodicSync(context, SYNC_INTERVAL, SYNC_FLEXTIME);

        /*
         * Without calling setSyncAutomatically, our periodic sync will not be enabled.
         */
        ContentResolver.setSyncAutomatically(newAccount, context.getString(R.string.content_authority), true);

        /*
         * Finally, let's do a sync to get things started
         */
        syncImmediately(context);
    }


    public static void initializeSyncAdapter(Context context) {
        getSyncAccount(context);
    }

}
