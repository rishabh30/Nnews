package com.rj.android.nnews.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by DELL on 6/8/2016.
 */
public class ArticleProvider extends ContentProvider {

    private static final int KEY = 100;//dir
    private static final int KEY_ID = 101;//item
    private static final int ARTICLE = 300;//dir
    private static final int ARTICLE_WITH_KEY = 301;//dir
    private static final int ARTICLE_WITH_KEY_AND_DATE = 302;//dir
    private static final int ARTICLE_WITH_ID = 303;


    private  static final UriMatcher sUriMatcher = buildUriMatcher();

    private DbHelper mOpenHelper;


    private static SQLiteQueryBuilder sArticleByKeyNameQueryBuilder;
    static {
        sArticleByKeyNameQueryBuilder =new SQLiteQueryBuilder();
        sArticleByKeyNameQueryBuilder.setTables(
                Contract.Key_Type.TABLE_NAME + " INNER JOIN " +
                        Contract.Article.TABLE_NAME + " ON " +
                        Contract.Article.TABLE_NAME + "." + Contract.Article.KEY_ID
                        + " = " + Contract.Key_Type.TABLE_NAME + "." + Contract.Key_Type.KEY_ID
        );

    }

    private static final String sArticleSelectionByKey =
            Contract.Key_Type.TABLE_NAME +
                    "." + Contract.Key_Type.KEY_NAME + " = ? ";

    private static final String sArticleSelectionByKeyAndDate =
            Contract.Key_Type.TABLE_NAME + "." + Contract.Key_Type.KEY_NAME + " = ? AND " +
                    Contract.Article.PUBLISH_DATE +" >= ? ";

    private Cursor getArticleByKeyName(Uri uri , String[] projection , String sortOrder)
    {
        String KeyName = Contract.Article.getKeyNameFromUri(uri);
        String startDate = Contract.Article.getStartDateFromUri(uri);

        String[] selectionArgs;
        String selection ;
        Toast.makeText(getContext(),"Bullshit",Toast.LENGTH_SHORT).show();
        if(startDate == null)
        {
            Log.d("URI :"," START DATE NULL ");
            selection = sArticleSelectionByKey;
            selectionArgs = new String[]{KeyName};
        }else
        {
            Log.d("URI :"," START DATE ");
            selectionArgs=new String []{KeyName,startDate};
            selection = sArticleSelectionByKeyAndDate;
        }
        return sArticleByKeyNameQueryBuilder.query(mOpenHelper.getWritableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder);
    }



    private static UriMatcher buildUriMatcher()
    {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = Contract.CONTENT_AUTHORITY;

        matcher.addURI(authority,Contract.PATH_KEY,KEY);
        matcher.addURI(authority,Contract.PATH_KEY +"/*",KEY_ID);

        matcher.addURI(authority,Contract.PATH_ARTICLE , ARTICLE);
        matcher.addURI(authority,Contract.PATH_ARTICLE + "/#" , ARTICLE_WITH_KEY);
        matcher.addURI(authority,Contract.PATH_ARTICLE + "/#/*" ,ARTICLE_WITH_KEY_AND_DATE);
        matcher.addURI(authority,Contract.PATH_ARTICLE + "/some/#" ,ARTICLE_WITH_ID);

        return matcher;
    }


    @Override
    public boolean onCreate() {
       mOpenHelper = new DbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        final  int match = sUriMatcher.match(uri);
        switch (match)
        {
            case ARTICLE:
                return Contract.Article.CONTENT_TYPE;
            case ARTICLE_WITH_KEY:
                return Contract.Article.CONTENT_TYPE;
            case ARTICLE_WITH_KEY_AND_DATE:
                return Contract.Article.CONTENT_TYPE;
            case ARTICLE_WITH_ID:
                return Contract.Article.CONTENT_ITEM_TYPE;

            case KEY:
                return Contract.Key_Type.CONTENT_TYPE;
            case KEY_ID:
                return Contract.Key_Type.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri : "+ uri );
        }
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
       Cursor retCursor;
        Log.d("ARTICLE PROVIDER   ", uri.toString());
        switch (sUriMatcher.match(uri))
        {
            case ARTICLE:
            {
                Log.d("ARTICLE PROVIDER   ", "ARTICLE");
                retCursor=mOpenHelper.getReadableDatabase().query(
                        Contract.Article.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }

            case ARTICLE_WITH_ID: {
                Log.d("ARTICLE PROVIDER   ", "ARTICLE WITH ID"+ContentUris.parseId(uri));
                retCursor=mOpenHelper.getReadableDatabase().query(
                        Contract.Article.TABLE_NAME,
                        projection,
                        Contract.Article._id + " = '" + ContentUris.parseId(uri)+" ' ",
                        null,
                        null,
                        null,
                        sortOrder
                );
                break;
            }

            case ARTICLE_WITH_KEY:
            {       Log.d("ARTICLE PROVIDER   ", "ARTICLE WITH key");
                retCursor=getArticleByKeyName(uri,projection,sortOrder);
                break;
            }
            case ARTICLE_WITH_KEY_AND_DATE:
            {
                Log.d("ARTICLE PROVIDER   ", "ARTICLE WITH ID");
                retCursor=getArticleByKeyName(uri,projection,sortOrder);
                break;
            }
            case KEY:
            {
                //for a list of keys
                retCursor=mOpenHelper.getReadableDatabase().query(
                        Contract.Key_Type.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case KEY_ID:
            {       //for a specific key id
                retCursor=mOpenHelper.getReadableDatabase().query(
                        Contract.Key_Type.TABLE_NAME,
                        projection,
                        Contract.Key_Type.KEY_ID + " = '" + ContentUris.parseId(uri)+" ' ",
                        null,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri : "+ uri );

        }
        retCursor.setNotificationUri(getContext().getContentResolver(),uri);
        return  retCursor;
    }



    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri=null;

        switch(match)
        {
            case KEY : {
                long id = db.insert(Contract.Key_Type.TABLE_NAME,null,values);
                if(id>0)
                {
                    returnUri = Contract.Key_Type.buildKeyUri(id);
                }
                else
                {
                    throw new android.database.SQLException("Failed to insert rows "+ uri);
                }
                break;
            }
            case ARTICLE:
            {
                long id = db.insert(Contract.Article.TABLE_NAME,null,values);
                if(id>0)
                {
                    returnUri = Contract.Article.buildArticleUri(id);
                }
                else
                {
                    throw new android.database.SQLException("Failed to insert rows "+ uri);
                }
                break;
            }
            default:
                throw  new UnsupportedOperationException("Unkonown Uri : "+uri);

        }
        getContext().getContentResolver().notifyChange(uri,null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowDeleted ;
        switch (match)
        {
            case KEY :
                rowDeleted = db.delete(Contract.Key_Type.TABLE_NAME,selection,selectionArgs);
                break;
            case ARTICLE:
                rowDeleted = db.delete(Contract.Article.TABLE_NAME,selection,selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown Uri : "+ uri);
        }

        if(null == selection || rowDeleted!=0 )
        {
            getContext().getContentResolver().notifyChange(uri,null);
        }
        return  rowDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowUpdated ;
        switch (match)
        {
            case KEY :
                rowUpdated = db.update(Contract.Key_Type.TABLE_NAME,values,selection,selectionArgs);
                break;
            case ARTICLE:
                rowUpdated = db.update(Contract.Article.TABLE_NAME,values,selection,selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown Uri : "+ uri);
        }

        if(rowUpdated!=0 )
        {
            getContext().getContentResolver().notifyChange(uri,null);
        }
        return  rowUpdated;
    }


    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {

        final SQLiteDatabase db =mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        switch(match)
        {
            case ARTICLE:
                db.beginTransaction();
                int returnCount = 0;
                try{
                    for(ContentValues value : values)
                    {
                        long _id = db.insert(Contract.Article.TABLE_NAME,null,value);
                        if(_id!=-1)
                        {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                }finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri,null);
                return  returnCount;
            default:
                return super.bulkInsert(uri, values);
        }




    }
}
