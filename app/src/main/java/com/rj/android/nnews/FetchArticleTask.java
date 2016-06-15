package com.rj.android.nnews;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ArrayAdapter;

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

public class FetchArticleTask extends AsyncTask<URL,Void,Void> {

    private final String LOG_TAG =FetchArticleTask.class.getSimpleName();

    Context mContext;
    public FetchArticleTask(Context context) {
        mContext = context;
    }

    private  long addKey(String KeyName , String url )
    {
        Log.v(LOG_TAG,"INSERTING "+ KeyName  );
        Cursor cursor = mContext.getContentResolver().query(
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
            Uri KeyInsertUri = mContext.getContentResolver().insert(Contract.Key_Type.CONTENT_URI,cv);
            cursor.close();
            Cursor cr = mContext.getContentResolver().query(
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


    ArrayAdapter madapter;
    String[] textinfo = new String[15];

    public static final String RESULTS = "results";
    public static final String TITLE = "title";
    public static final String ABSTRACT = "abstract";
    public static final String P_DATE = "published_date";
    public static final String SOURCE = "byline";
    public static final String ID = "id";
    public static final String ARITCLE_URL = "url";


    public static final String MEDIA = "media";
    public static final String MULTIMEDIA_ARRAY = "multimedia";
    public static final String PHOTO_CAPTION = "caption";
    public static final String PHOTO_URL = "url";




    @Override
    protected Void doInBackground(URL[] urls) {
        String KeyName="Most-Viewed";

        HttpURLConnection urlConnection = null;
        BufferedReader bufferedReader = null;

        String JsonData = "";
        URL url=null;

        try {

            url = urls[0];
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer stringBuffer = new StringBuffer();
            if (inputStream == null) {
                return null;
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
            String[] json =  getJsonStringArray(JsonData,KeyId);
        } catch (JSONException e) {
            Log.e("Download Data ", "JSON ERROR");
            e.printStackTrace();
        }

        return null;
    }

    private String[] getJsonStringArray(String jsonData,long KeyId) throws JSONException {


        JSONObject reader = new JSONObject(jsonData);
        JSONArray result = reader.getJSONArray(RESULTS);
        int length = result.length();
        String[] resultStrs = new String[15];

        Vector<ContentValues> cVVector = new Vector<ContentValues>(15);
        for (int i = 0; i < 15; i++) {
            JSONObject object = result.getJSONObject(i);
            String title = object.getString(TITLE);
            resultStrs[i]=title;
           // JSONArray media = object.getJSONArray(MEDIA);
            //JSONObject hold = media.getJSONObject(0);

            String Abstract = object.getString(ABSTRACT);
            Log.d(LOG_TAG , Abstract);
            String p_date = object.getString(P_DATE);
            String source = object.getString(SOURCE);

            JSONArray metadata = object.getJSONArray(MULTIMEDIA_ARRAY);

            if(metadata.length()>1) {
                JSONObject multimedia = metadata.getJSONObject(4);

                String imageURL = multimedia.getString(PHOTO_URL);
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
                contentValues.put(Contract.Article.PHOTO_URL, imageURL);
                contentValues.put(Contract.Article.PUBLISH_DATE, p_date);


                cVVector.add(contentValues);
            }
            //    resultStrs [i][1] = imageURL.getString("url");
        }

        if (cVVector.size() > 0) {
            ContentValues[] cvArray = new ContentValues[cVVector.size()];
            cVVector.toArray(cvArray);
            int rowsInserted = mContext.getContentResolver()
                    .bulkInsert(Contract.Article.CONTENT_URI, cvArray);
            Log.v(LOG_TAG, "inserted " + rowsInserted + " rows of weather data");
        }

        textinfo = resultStrs;
        return resultStrs;

    }
}