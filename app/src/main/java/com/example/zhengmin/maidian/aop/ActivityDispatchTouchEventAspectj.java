package com.example.zhengmin.maidian.aop;

import android.app.Application;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;

import com.example.zhengmin.maidian.BaseApplication;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.json.JSONObject;

import java.lang.reflect.Method;


/**
 * Created by zhengmin on 2018/3/14.
 */
@Aspect
public class ActivityDispatchTouchEventAspectj {
    private final static String TAG = "dispatchTouchEvent";
    @After("execution(* android.app.Activity.dispatchTouchEvent(..))")
    public void dispatchTouchEventAOP(final JoinPoint joinPoint) throws Throwable {
        Log.e("zhengmin", TAG);
        try {
            //基本校验
            if (joinPoint.getArgs() == null || joinPoint.getArgs().length != 1) {
                return;
            }
            //获取被点击的 View
            MotionEvent motionEvent  = (MotionEvent) joinPoint.getArgs()[0];
            int eventType = AopConstants.ACTION_DEFAULT;
            switch (motionEvent.getAction()){
                case MotionEvent.ACTION_DOWN:
                    eventType = AopConstants.ACTIVITY_TOUCH_DOWN;
                    break;
                case MotionEvent.ACTION_UP:
                    eventType = AopConstants.ACTIVITY_TOUCH_UP;
                    break;

                default:
                    return;

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

            final Method method = activityThreadClass
                    .getMethod("getApplication");
            application = (Application) method.invoke(localObject, (Object[]) null);


            ((BaseApplication)application).getInterface().saveLogToDb(eventType,
                    properties.toString(),"",currentOnClickTimestamp);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "onViewClickMethod error: " + e.getMessage());
        }
    }
}
