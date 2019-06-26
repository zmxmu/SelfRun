package com.example.zhengmin.maidian;

import android.app.Application;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

/**
 * Created by zhengmin on 2018/3/14.
 */

public class BaseApplication extends Application {
    private ServiceConnection mServiceConnection;
    private ISaveLogInterface mISaveLogInterface;
    @Override
    public void onCreate() {
        super.onCreate();
        Intent i = new Intent(this,SaveLogService.class);
        mServiceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                mISaveLogInterface = ISaveLogInterface.Stub.asInterface(iBinder);
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {

            }
        };
        bindService(i,mServiceConnection,BIND_AUTO_CREATE);

    }
    public ISaveLogInterface getInterface(){
        return mISaveLogInterface;
    }

    public void unbindService() {
        // 程序终止的时候执行
        unbindService(mServiceConnection);
    }
}
