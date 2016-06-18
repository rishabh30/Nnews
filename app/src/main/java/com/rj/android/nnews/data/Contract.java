package com.rj.android.nnews.data;

import android.content.ContentUris;
import android.net.Uri;

/**
 * Contract is used here to store 2 tables
 * Key_type
 * Article
 *
 *
 */
public class Contract {


    private static final String LOG_TAG = Contract.class.getSimpleName();
    // base interface is used for linkage in the 2 tables to be join

    public final static String CONTENT_AUTHORITY = "com.rj.android.nnews";
    public final static Uri BASE_CONTENT_URI = Uri.parse("content://"+
                        CONTENT_AUTHORITY);

    public final static String  PATH_KEY ="key_type";;
    public final static String PATH_ARTICLE ="article";

    public interface Base_Column{
        //act as link between two tables
        public static final String KEY_ID = "KEY_ID";
    }

    //for storing key type in the storage like most viwed key, top newskey, lastest news key they
    //all have different key
    public static final class Key_Type implements Base_Column
    {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_KEY).build();
        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/" + CONTENT_AUTHORITY +"/" +
                        PATH_KEY ;
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/" + CONTENT_AUTHORITY +"/" +
                        PATH_KEY ;

        public static final String TABLE_NAME = "key_type";
        public static final String KEY_NAME = "KEY_NAME";
        public static final String KEY_URL = "KEY_URL";

        public static  Uri buildKeyUri(long id  )
        {
            return ContentUris.withAppendedId(CONTENT_URI,id);
        }

        public static long getKeyNameFromUri(Uri uri) {
            return Long.parseLong(uri.getQueryParameter(KEY_ID));
        }

    }

    //stores article information for a particular news
    public static final class Article implements Base_Column{

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_ARTICLE).build();

        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/" + CONTENT_AUTHORITY +"/" +
                        PATH_ARTICLE ;
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/" + CONTENT_AUTHORITY +"/" +
                        PATH_ARTICLE ;


        public static final String TABLE_NAME = "article";

        //Particular id is assigned to article
        public static final String _id = "_id";
        //defines title of the news
        public static final String TITLE = "TITLE";
        //provides html link to the article of page of times
        public static final String ARTICLE_URL = "ARTICLE_URL";
        //abstract to the story
        public static final String ABSTRACT = "ABSTRACT";
        //source of the story
        public static final String SOURCE = "SOURCE";
        //publish date
        public static final String PUBLISH_DATE = "PUBLISH_DATE";
        //photo url is provided for image downloading
        public static final String PHOTO_URL = "PHOTO_URL";
        //high resolution photo url is provided for image downloading
        public static final String PHOTO_URL_HIGH = "PHOTO_URL_HIGH";




        //photo heading

        public static final String PHOTO_HEADING = "PHOTO_HEADING";

        public static  Uri buildArticleUri(int id)
        {
                    return    CONTENT_URI.buildUpon().appendPath(Long.toString(id)).build();
        }


        public static Uri buildKeyWithStartDate(String keyName , String startDate){
            return  CONTENT_URI.buildUpon().appendPath(keyName)
                    .appendPath(startDate).build();
        }

        public static Uri buildUriWithKeyName(String keyName ){
            return  CONTENT_URI.buildUpon().appendPath(keyName).build();
        }

        public static String getStartDateFromUri(Uri uri) {

            String dateString = uri.getQueryParameter(PUBLISH_DATE);
            if (null != dateString && dateString.length() > 0)
                return dateString;
            else
                return null;
        }

        public static String getKeyNameFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }
    }
}