package com.example.zhengmin.maidian.aop;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.example.zhengmin.maidian.BaseApplication;
import com.example.zhengmin.maidian.R;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.json.JSONObject;

/**
 * Created by zhengmin on 2018/3/14.
 */
@Aspect
public class EditTextOnFocusChangeAspectj {
    private final static String TAG = "ViewOnClick";
    @After("execution(* android.view.View.OnFocusChangeListener.onFocusChange(android.view.View, boolean))")
    public void onFocusChangeAOP(final JoinPoint joinPoint) throws Throwable {
        Log.e("zhengmin", "onFocusChange");
        try {
            //基本校验
            if (joinPoint.getArgs() == null || joinPoint.getArgs().length != 2) {
                return;
            }


            //获取被点击的 View
            View view = (View) joinPoint.getArgs()[0];
            if (view == null ||  !(view instanceof EditText)) {
                return;
            }

            if((Boolean)joinPoint.getArgs()[1]){
                return;
            }

            //获取所在的 Context
            Context context = view.getContext();
            if (context == null) {
//                        return;
            }

            //将 Context 转成 Activity
            Activity activity = AopUtil.getActivityFromContext(context, view);

            long currentOnClickTimestamp = System.currentTimeMillis();
            String tag = (String) view.getTag(R.id.sensors_analytics_tag_view_timestamp);
            if (!TextUtils.isEmpty(tag)) {
                try {
                    long lastOnClickTimestamp = Long.parseLong(tag);
                    if ((currentOnClickTimestamp - lastOnClickTimestamp) < 500) {
                        Log.i(TAG, "This onClick maybe extends from super, IGNORE");
                        return;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            view.setTag(R.id.sensors_analytics_tag_view_timestamp, String.valueOf(currentOnClickTimestamp));

            JSONObject properties = new JSONObject();
            properties.put(AopConstants.ELEMENT_TIME, String.valueOf(currentOnClickTimestamp));
//            AopUtil.addViewPathProperties(activity, view, properties);

            //ViewId
            String idString = AopUtil.getViewId(view);
            if (!TextUtils.isEmpty(idString)) {
                properties.put(AopConstants.ELEMENT_ID, idString);
            }

            //$screen_name & $title
            if (activity != null) {
                properties.put(AopConstants.SCREEN_NAME, activity.getClass().getCanonicalName());
                String activityTitle = AopUtil.getActivityTitle(activity);
                if (!TextUtils.isEmpty(activityTitle)) {
                    properties.put(AopConstants.TITLE, activityTitle);
                }
            }

            String viewType = view.getClass().getCanonicalName();
            //$element_type
            properties.put(AopConstants.ELEMENT_TYPE, viewType);

            CharSequence viewText = ((EditText)view).getText();

            //$element_content
            if (!TextUtils.isEmpty(viewText)) {
                properties.put(AopConstants.ELEMENT_CONTENT, viewText.toString());
            }

            //fragmentName
            AopUtil.getFragmentNameFromView(view, properties);

            //获取 View 自定义属性
            JSONObject p = (JSONObject) view.getTag(R.id.sensors_analytics_tag_view_properties);
            if (p != null) {
                AopUtil.mergeJSONObject(p, properties);
            }
            ((BaseApplication)(activity.getApplication())).getInterface().saveLogToDb(AopConstants.ET_INPUT,
                    viewText.toString(),idString,currentOnClickTimestamp);
            Log.e(AopConstants.TEXT_CHANGED_EVENT_NAME, properties.toString());
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "onViewClickMethod error: " + e.getMessage());
        }
    }
}
