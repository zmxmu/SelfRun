package com.example.zhengmin.maidian.aop;


import android.app.Application;
import android.content.ComponentName;
import android.content.Intent;
import android.util.Log;
import android.view.MotionEvent;

import com.example.zhengmin.maidian.BaseApplication;

import org.json.JSONObject;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * Created by zhengmin on 2018/3/21.
 */

public class ActivityEventInvocationHandler implements InvocationHandler {
    private Object mBase;
    public ActivityEventInvocationHandler(Object base){
        mBase = base;
    }
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if ("dispatchTouchEvent".equals(method.getName())){
            Log.e("zhengmin ","dispatchTouchEvent方法拦截了");

            // 找到参数里面的第一个Intent 对象
            MotionEvent motionEvent;
            int index = 0;

            for (int i = 0; i < args.length; i++) {
                if (args[i] instanceof MotionEvent) {
                    index = i;
                    break;
                }
            }
            motionEvent = (MotionEvent) args[index];
            int eventType = AopConstants.ACTION_DEFAULT;
            switch (motionEvent.getAction()){
                case MotionEvent.ACTION_DOWN:
                    eventType = AopConstants.ACTIVITY_TOUCH_DOWN;
                    break;
                case MotionEvent.ACTION_UP:
                    eventType = AopConstants.ACTIVITY_TOUCH_UP;
                    break;

                default:
                    return method.invoke(mBase, args);

            }

            long currentOnClickTimestamp = System.currentTimeMillis();
            JSONObject properties = new JSONObject();
            properties.put("x", String.valueOf(motionEvent.getRawX()));
            properties.put("y", String.valueOf(motionEvent.getRawY()));

            Application application = null;
            Class<?> activityThreadClass;

            activityThreadClass = Class.forName("android.app.ActivityThread");
            final Method method2 = activityThreadClass.getMethod(
                    "currentActivityThread", new Class[0]);
            // 得到当前的ActivityThread对象
            Object localObject = method2.invoke(null, (Object[]) null);

            final Method method3 = activityThreadClass
                    .getMethod("getApplication");
            application = (Application) method3.invoke(localObject, (Object[]) null);


            ((BaseApplication)application).getInterface().saveLogToDb(eventType,
                    properties.toString(),"",currentOnClickTimestamp);
            Log.e("zhengmin","dispatchTouchEvent方法 hook 成功");
            Log.e("zhengmin","args[index] hook = " + args[index]);
        }
        return method.invoke(mBase, args);
    }
}
