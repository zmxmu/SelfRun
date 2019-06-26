package com.example.zhengmin.maidian;

import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;

import com.example.zhengmin.maidian.database.DbHelper;

import com.example.zhengmin.maidian.aop.AopConstants;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

public class SaveLogService extends Service {
    private final static int CLICK_GAP = 100;
    private final static int MSG_CLOSE_DB = 10;
    private final static String FILE_NAME = "maidian.py";
    private SQLiteDatabase mSQLiteDatabase;
    private DbHelper mDbHelper;
    private MyHandler mHandler;
    @Override
    public void onCreate(){
        super.onCreate();
        mDbHelper = new DbHelper(this);
        mSQLiteDatabase = mDbHelper.getWritableDatabase();
        mSQLiteDatabase.execSQL("DELETE FROM " + DbHelper.TABLE_NAME);
        mHandler = new MyHandler();

    }
    private StringBuilder readTextFromFile(InputStream is) throws Exception {
        InputStreamReader reader = new InputStreamReader(is);
        BufferedReader bufferedReader = new BufferedReader(reader);
        StringBuilder buffer = new StringBuilder("");
        String str;
        while ((str = bufferedReader.readLine()) != null) {
            buffer.append(str);
            buffer.append("\n");
        }
        return buffer;
    }
    private IBinder mBinder = new ISaveLogInterface.Stub(){
        @Override
        public void saveLogToDb(final int eventType, final String elementContent, final String elementId, final long timeStamp) throws RemoteException {
            new Runnable(){
                @Override
                public void run() {
                    if(mSQLiteDatabase!=null){
                        ContentValues cv = new ContentValues();
                        cv.put("eventType",eventType);
                        cv.put("elementContent",elementContent);
                        cv.put("elementId",elementId);
                        cv.put("timeStamp",timeStamp);
                        if(mSQLiteDatabase.insert(DbHelper.TABLE_NAME,null,cv)!=-1){
                            Log.e("zhengmin","saveLogToDb success");
                        }
                        else{
                            Log.e("zhengmin","saveLogToDb false");
                        }
                    }
                }
            }.run();
        }
    };
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent){
        if(mSQLiteDatabase!=null && mSQLiteDatabase.isOpen()){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    StringBuilder sb =  new StringBuilder();
                    Cursor cursor = mSQLiteDatabase.rawQuery("Select * from "+DbHelper.TABLE_NAME, null);
                    if(cursor.moveToFirst()){
                        long lastTime = cursor.getLong(cursor.getColumnIndex("timeStamp"));
                        do{
                            int logType = cursor.getInt(cursor.getColumnIndex("eventType"));
                            String elementContent = cursor.getString(cursor.getColumnIndex("elementContent"));
                            String elementId = cursor.getString(cursor.getColumnIndex("elementId"));
                            long timeStamp = cursor.getLong(cursor.getColumnIndex("timeStamp"));
                            switch (logType){
                                case AopConstants.VIEW_ON_CLICK:
                                    sb.append(getString(R.string.SLEEP_FOR_SECONDS,((float)(timeStamp-lastTime))/1000));
                                    sb.append(getString(R.string.VIEW_ON_CLICK_CODE,elementId));
                                    break;
                                case AopConstants.ET_INPUT:
//                                    if(timeStamp-lastTime>500){
//
//                                    }
                                    sb.append(getString(R.string.SLEEP_FOR_SECONDS,((float)(timeStamp-lastTime))/1000));
                                    sb.append(getString(R.string.ET_INPUT_CODE,elementId,elementContent));
                                    break;
                                case AopConstants.LIST_ITEM_CLICK:
//                                    if(timeStamp-lastTime>500){
//
//                                    }
                                    sb.append(getString(R.string.SLEEP_FOR_SECONDS,((float)(timeStamp-lastTime))/1000));
                                    sb.append(getString(R.string.LIST_ITEM_CLICK,elementContent));
                                    break;
                                case AopConstants.ACTIVITY_KEY_BACK:
//                                    if(timeStamp-lastTime>500){
//
//                                    }
                                    sb.append(getString(R.string.SLEEP_FOR_SECONDS,((float)(timeStamp-lastTime))/1000));
                                    sb.append(getString(R.string.ACTIVITY_KEY_BACK));
                                    break;
                                case AopConstants.ACTIVITY_TOUCH_DOWN:
                                    sb.append(getString(R.string.SLEEP_FOR_SECONDS,((float)(timeStamp-lastTime))/1000));
                                    try {

                                        JSONObject jb = new JSONObject(elementContent);
                                        if(jb!=null){
                                            sb.append(getString(R.string.ACTIVITY_TOUCH_DOWN,
                                                    jb.getInt("x"),jb.getInt("y")));

                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    break;
                                case AopConstants.ACTIVITY_TOUCH_UP:
                                    try {
                                        JSONObject jb = new JSONObject(elementContent);
                                        if(jb!=null){
                                            sb.append(getString(R.string.ACTIVITY_TOUCH_UP,timeStamp-lastTime,
                                                    jb.getInt("x"),jb.getInt("y")));
                                            lastTime = timeStamp;
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    break;
                            }
                            lastTime = timeStamp;

                        }while(cursor.moveToNext());
                    }
                    try {
                        InputStream beginIs = getAssets().open("begin.py");
                        StringBuilder targetPy = readTextFromFile(beginIs);
                        targetPy.append(sb);
                        targetPy.append(readTextFromFile(getResources().openRawResource(R.raw.end)));
                        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))   {
                            FileOutputStream outputStream;
                            File file = new File(getCacheDir().getAbsolutePath()+"/"+FILE_NAME);
                            outputStream = new FileOutputStream(file, false);
                            outputStream.write(targetPy.toString().getBytes());
                            outputStream.flush();
                            outputStream.close();
                        }

                        Message msg = mHandler.obtainMessage();
                        msg.what = MSG_CLOSE_DB;
                        mHandler.sendMessage(msg);
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                }
            }).start();
        }
        return false;
    }

    class MyHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case MSG_CLOSE_DB:
                    mSQLiteDatabase.close();
                    break;
            }

        }
    }
}
