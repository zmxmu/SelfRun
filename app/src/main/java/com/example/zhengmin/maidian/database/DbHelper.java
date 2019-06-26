package com.example.zhengmin.maidian.database;


import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by zhengmin on 2018/3/19.
 */

public class DbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "log.db";//数据库名字
    private static final int DATABASE_VERSION = 2;//数据库版本号
    public static final String TABLE_NAME = "log";//数据库里的表
    private static final String CREATE_TABLE = "create table if not exists "+TABLE_NAME+"("
            + "_id integer primary key autoincrement,"
            + "eventType int, "
            + "elementContent text, "
            + "elementId text," +
            "timeStamp datetime)";//数据库里的表
    public DbHelper(Context context){
        this(context,DATABASE_NAME,null,DATABASE_VERSION);

    }
    public DbHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version){
        super(context,name,factory,version);

    }
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        // 删除原来的数据表
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
        // 重新创建
        onCreate(sqLiteDatabase);
    }
}
