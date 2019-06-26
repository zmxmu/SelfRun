// ISaveLogInterface.aidl
package com.example.zhengmin.maidian;

// Declare any non-default types here with import statements

interface ISaveLogInterface {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */

    void saveLogToDb(int eventType, String elementContent,String elementId,long timeStamp);
}
