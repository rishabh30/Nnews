package com.rj.android.nnews;

import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.test.AndroidTestCase;
import android.util.Log;

import com.rj.android.nnews.data.Contract.Article;
import com.rj.android.nnews.data.Contract.Key_Type;
import com.rj.android.nnews.data.DbHelper;

import java.util.Map;
import java.util.Set;

public class TestProvider extends AndroidTestCase {
    public static final String LOG_TAG = TestProvider.class.getSimpleName();

    public void testDeleteDb() throws Throwable {
        mContext.deleteDatabase(DbHelper.DATABASE_NAME);
    }

    String KeyColumns[] = {
            Key_Type.KEY_ID, Key_Type.KEY_NAME, Key_Type.KEY_URL
    };
    String[] ArticleColumns = {
            Article.KEY_ID, Article._id, Article.TITLE, Article.ARTICLE_URL, Article.ABSTRACT,
            Article.SOURCE, Article.PHOTO_HEADING, Article.PHOTO_URL, Article.PUBLISH_DATE
    };

    static void validateCursor(Cursor valueCursor, ContentValues expectedValues) {
        assertTrue(valueCursor.moveToFirst());
        Set<Map.Entry<String, Object>> valueSet = expectedValues.valueSet();
        for (Map.Entry<String, Object> entry : valueSet) {
            String columnName = entry.getKey();
            int index = valueCursor.getColumnIndex(columnName);
            assertFalse(index == -1);
            String expectedValue = entry.getValue().toString();
            assertEquals(expectedValue, valueCursor.getString(index));
        }
    }


    public ContentValues getArticleValues() {
        int articleId = 1029;
        int testId = 1;
        String title = "mess",
                urlArticle = "http://www.nytimes.com/2016/05/05/arts/music/addiction‐lawyer‐says.html",
                absTest = "a man on wire",
                sourceTest = "new of times",
                photoTest = "wire",
                urlPhoto = "http://45",
                dateTest = "1209994";
        ContentValues contentValues = new ContentValues();
        contentValues.put(Article.KEY_ID, testId);
        contentValues.put(Article._id, articleId);
        contentValues.put(Article.TITLE, title);
        contentValues.put(Article.ARTICLE_URL, urlArticle);
        contentValues.put(Article.ABSTRACT, absTest);
        contentValues.put(Article.SOURCE, sourceTest);
        contentValues.put(Article.PHOTO_HEADING, photoTest);
        contentValues.put(Article.PHOTO_URL, urlPhoto);
        contentValues.put(Article.PUBLISH_DATE, dateTest);
        return contentValues;
    }

    public ContentValues getKeyValues() {

        int testId = 1029;
        String testName = "mostViewed";
        String testUrl = "http://api.nytimes.com/svc/mostpopular/v2/mostviewed/arts/30.json?api-key=a52e5f46fbb4f8f1d76be5f07e97d0dd:16:74804765";
        ContentValues cv = new ContentValues();
        cv.put(Key_Type.KEY_NAME, testName);
        cv.put(Key_Type.KEY_URL, testUrl);
        return cv;
    }

    public void  testGetType()
    {
        String type = mContext.getContentResolver().getType(Article.CONTENT_URI);
        assertEquals(Article.CONTENT_TYPE,type);

        String key_id = "1";
        long article_id = 100394;
        type = mContext.getContentResolver().getType(Article.buildArticleUri(1));
        assertEquals(type,Article.CONTENT_TYPE);

        type = mContext.getContentResolver().getType(Article.buildKeyWithStartDate(key_id, "2009933"));
        assertEquals(type,Article.CONTENT_TYPE);

        type = mContext.getContentResolver().getType(Key_Type.CONTENT_URI);
        assertEquals(type,Key_Type.CONTENT_TYPE);

        type = mContext.getContentResolver().getType(Key_Type.buildKeyUri(1));
        assertEquals(type,Key_Type.CONTENT_ITEM_TYPE);


        type = mContext.getContentResolver().getType(Article.buildArticleWithIdUri(100394));
        Log.v(LOG_TAG, Article.buildArticleUri(100394).toString());
        assertEquals(type, Article.CONTENT_ITEM_TYPE);
    }


    public void testInsetReadProvider() {

        DbHelper dbHelper = new DbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues cv = getKeyValues();

        long rowId;
        Uri insertdb  = mContext.getContentResolver().insert(Key_Type.CONTENT_URI, cv);
        rowId = ContentUris.parseId(insertdb);
        Log.d(LOG_TAG, ": New RowId : " + rowId);
        Log.d(LOG_TAG, ": BREAK ");

        Cursor cursor = mContext.getContentResolver().query(Key_Type.CONTENT_URI,
                null,
                null,
                null,
                null
        );

        if (cursor.moveToFirst()) {
         /*   int idIndex =  cursor.getColumnIndex(Key_Type.KEY_ID);
            int id = cursor.getInt(idIndex);
            int nameIndex = cursor.getColumnIndex(Key_Type.KEY_NAME);
            String name = cursor.getString(nameIndex);
            int urlIndex = cursor.getColumnIndex(Key_Type.KEY_URL);
            String url = cursor.getString(urlIndex);

            assertEquals(testId,id);
            assertEquals(testName,name);
            assertEquals(testUrl,url);
        */
            validateCursor(cursor, cv);
        } else {
            fail(" : NO VALUES RETURNED");
        }
        ContentValues contentValues = getArticleValues();
        Uri insetdb= mContext.getContentResolver().insert(Article.CONTENT_URI, contentValues);
        long ArticleRowId= ContentUris.parseId(insetdb);
        Uri TestUri = Article.CONTENT_URI.buildUpon().appendQueryParameter(Key_Type.KEY_NAME, "mostViewed")
                .appendQueryParameter(Article.PUBLISH_DATE,"2009933").build();

        Cursor cursor1 = mContext.getContentResolver().query(TestUri, null, null, null, null);

        if (cursor1.moveToFirst()) {
            validateCursor(cursor1, contentValues);
        } else {
            fail(LOG_TAG + "   no Article value found");
        }

    }
}

  /*  int KeyidIndex =  cursor1.getColumnIndex(Article.KEY_ID);
            int idIndex =  cursor1.getColumnIndex(Article.ID);
            int titleIndex =  cursor1.getColumnIndex(Article.TITLE);
            int urlAticleIndex =  cursor1.getColumnIndex(Article.ARTICLE_URL);
            int abstractIndex =  cursor1.getColumnIndex(Article.ABSTRACT);
            int sourceIndex =  cursor1.getColumnIndex(Article.SOURCE);
            int headIndex =  cursor1.getColumnIndex(Article.PHOTO_HEADING);
            int urlPhotoIndex =  cursor1.getColumnIndex(Article.PHOTO_URL);
            int dateIndex =  cursor1.getColumnIndex(Article.PUBLISH_DATE);


            int Keyid   =  cursor1.getInt(KeyidIndex);
            int id   =  cursor1.getInt(idIndex);
            String titleString  =  cursor1.getString(titleIndex);
            String urlArticleString  =  cursor1.getString(urlAticleIndex);
            String abstractString  =  cursor1.getString(abstractIndex);
            String source  =  cursor1.getString(sourceIndex);
            String head  =  cursor1.getString(headIndex);
            String urlPhotoString  =  cursor1.getString(urlPhotoIndex);
            String date  =  cursor1.getString(dateIndex);
            assertEquals(Keyid,testId);
            assertEquals(id , articleId);
            assertEquals(titleString,title);
            assertEquals(absTest,abstractString);
            assertEquals(sourceTest,source);
            assertEquals(photoTest,head);
            assertEquals(urlPhoto,urlPhotoString);
            assertEquals(dateTest,date);
            assertEquals(urlArticle,urlArticleString);
  */