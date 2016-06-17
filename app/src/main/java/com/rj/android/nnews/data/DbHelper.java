package com.rj.android.nnews.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.rj.android.nnews.data.Contract.Article;
import com.rj.android.nnews.data.Contract.Key_Type;


public class DbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME="article.db";

    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
          final String SQL_CREATE_KEY_TABLE =
                  "CREATE TABLE " + Key_Type.TABLE_NAME + " ("
                  +
                          Key_Type.KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "+
                          Key_Type.KEY_NAME + " TEXT NOT NULL , "+
                          Key_Type.KEY_URL + " TEXT NOT NULL ); ";

          final String SQL_CREATE_ARTICLE_TABLE =
                  " CREATE TABLE " + Article.TABLE_NAME + " ( "
                  +
                          Article._id + " INTEGER PRIMARY KEY AUTOINCREMENT, "+
                          Article.KEY_ID + " INTEGER NOT NULL, "+
                          Article.TITLE +" TEXT NOT NULL, "+
                          Article.ARTICLE_URL + " TEXT NOT NULL, "+
                          Article.ABSTRACT +" TEXT NOT NULL, "+
                          Article.SOURCE +" TEXT NOT NULL, "+
                          Article.PHOTO_HEADING +" TEXT NOT NULL, "+
                          Article.PHOTO_URL +" TEXT NOT NULL, "+
                          Article.PHOTO_URL_HIGH +" TEXT NOT NULL, "+
                          Article.PUBLISH_DATE +" TEXT NOT NULL, "+
                          " FOREIGN KEY ( "+ Article.KEY_ID +" ) REFERENCES " +
                          Key_Type.TABLE_NAME +" ("+ Key_Type.KEY_ID +") " +
                          " UNIQUE (" + Article.ABSTRACT + ") ON CONFLICT IGNORE "+
                          ");";
            Log.v("QUERY :",SQL_CREATE_ARTICLE_TABLE);
        db.execSQL(SQL_CREATE_KEY_TABLE);
        db.execSQL( SQL_CREATE_ARTICLE_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS "+ Key_Type.TABLE_NAME );
        db.execSQL("DROP TABLE IF EXISTS "+ Article.TABLE_NAME );
        onCreate(db);
    }
}
