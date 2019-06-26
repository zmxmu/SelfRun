package com.example.zhengmin.maidian.aop;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.support.v7.widget.SwitchCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.example.zhengmin.maidian.BaseApplication;
import com.example.zhengmin.maidian.R;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.json.JSONObject;

import java.lang.reflect.Method;


/**
 * Created by zhengmin on 2018/3/14.
 */
@Aspect
public class ActivityOnKeyUpAspectj {
    private final static String TAG = "ActivityOnKeyUp";
    @After("execution(* android.app.Activity.onKeyUp(..))")
    public void onViewClickAOP(final JoinPoint joinPoint) throws Throwable {
        Log.e("zhengmin", "ActivityOnKeyUp");
        try {
            //基本校验
            if (joinPoint.getArgs() == null || joinPoint.getArgs().length != 2) {
                return;
            }


            //获取被点击的 View
            int keyCode  = (int) joinPoint.getArgs()[0];
            KeyEvent event = (KeyEvent)joinPoint.getArgs()[1];
            if (!(keyCode == KeyEvent.KEYCODE_BACK
//                    && event.isTracking() && !event.isCanceled()
            )) {
                return;
            }

            long currentOnClickTimestamp = System.currentTimeMillis();


            JSONObject properties = new JSONObject();
            properties.put(AopConstants.ELEMENT_TIME, String.valueOf(currentOnClickTimestamp));

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


            ((BaseApplication)application).getInterface().saveLogToDb(AopConstants.ACTIVITY_KEY_BACK,
                "","",currentOnClickTimestamp);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "onViewClickMethod error: " + e.getMessage());
        }
    }
}
