package com.example.zhengmin.maidian;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import com.example.zhengmin.maidian.database.DbHelper;

public class LogDataContentProvider extends ContentProvider {
    private static final UriMatcher mUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    public static final String AUTHORITY = "com.example.zhengmin.maidian.event_log";  //授权
    public static final Uri LOG_CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/log");

    private static final int TABLE_CODE_LOG = 1;

    private SQLiteDatabase mDatabase;
    private Context mContext;

    static {
        //关联不同的 URI 和 code，便于后续 getType
        mUriMatcher.addURI(AUTHORITY, "log", TABLE_CODE_LOG);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
       int deleteCount =  mDatabase.delete(getTableName(uri),selection,selectionArgs);
       if(deleteCount>0){
           mContext.getContentResolver().notifyChange(uri,null);
       }
       return deleteCount;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        mDatabase.insert(getTableName(uri),null,values);
        mContext.getContentResolver().notifyChange(uri,null);
        return null;
    }

    @Override
    public boolean onCreate() {
        mContext = getContext();
        mDatabase = new DbHelper(mContext).getWritableDatabase();
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        return mDatabase.query(getTableName(uri),projection,selection,selectionArgs,null,null,sortOrder);
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        int updateCount = mDatabase.update(getTableName(uri),values,selection,selectionArgs);
        if(updateCount>0){
            mContext.getContentResolver().notifyChange(uri,null);
        }
        return updateCount;
    }

    private String getTableName(final Uri uri) {
        String tableName = "";
        int match = mUriMatcher.match(uri);
        switch (match){
            case TABLE_CODE_LOG:
                tableName = DbHelper.TABLE_NAME;
        }
        Log.e("zhengmin","UriMatcher " + uri.toString() + ", result: " + match);
        return tableName;
    }
}
