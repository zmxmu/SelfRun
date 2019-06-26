package com.example.zhengmin.maidian.aop;

import android.app.Activity;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * Created by zhengmin on 2018/3/21.
 */

public class HookHelper {
    public static void hookActivity(Activity activity) throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        Class<?> activityClass = Class.forName("android.app.Activity");
        //拿到gDefault字段
        Field gDefaultField = activityClass.getDeclaredField("mWindow");
        gDefaultField.setAccessible(true);
        //从gDefault字段中取出这个对象的值
        Object window = gDefaultField.get(activity);

        Class<?> iWindowClass = Class.forName("android.view.Window");
        Class<?> iWindowCallbackClass = Class.forName("android.view.Window$Callback");
        Method setCallbackMethod = iWindowClass.getDeclaredMethod("setCallback",new Class[]{iWindowCallbackClass});
        Object proxy = Proxy.newProxyInstance(activity.getClassLoader(),
                new Class<?>[] { iWindowCallbackClass }, new ActivityEventInvocationHandler(activity));
        setCallbackMethod.invoke(window,proxy);
    }
}
