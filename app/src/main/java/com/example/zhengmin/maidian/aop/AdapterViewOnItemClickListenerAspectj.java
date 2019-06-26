package com.example.zhengmin.maidian.aop;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

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
public class AdapterViewOnItemClickListenerAspectj {
    private final static String TAG = "AdapterViewOnItemClick";
    @After("execution(* android.widget.AdapterView.OnItemClickListener.onItemClick(..))")
    public void onItemClickAOP(final JoinPoint joinPoint) throws Throwable {
        Log.e("zhengmin", "onItemClick");
        try {

            //基本校验
            if (joinPoint == null || joinPoint.getArgs() == null || joinPoint.getArgs().length != 4) {
                return;
            }

            //AdapterView
            Object object = joinPoint.getArgs()[0];
            if (object == null) {
                return;
            }

            //View
            View view = (View) joinPoint.getArgs()[1];
            if (view == null) {
                return;
            }

            //获取所在的 Context
            Context context = view.getContext();
            if (context == null) {
                return;
            }

            //将 Context 转成 Activity
            Activity activity = AopUtil.getActivityFromContext(context, view);

            //position
            int position = (int) joinPoint.getArgs()[2];

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
            //View 被忽略
            AdapterView adapterView = (AdapterView) object;

            AopUtil.addViewPathProperties(activity, view, properties);
            String viewType = "AdapterView";

            //$element_type
            properties.put(AopConstants.ELEMENT_TYPE, viewType);

            //ViewId
            String idString = AopUtil.getViewId(adapterView);
            if (!TextUtils.isEmpty(idString)) {
                properties.put(AopConstants.ELEMENT_ID, idString);
            }

            //Activity 名称和页面标题
            if (activity != null) {
                properties.put(AopConstants.SCREEN_NAME, activity.getClass().getCanonicalName());
                String activityTitle = AopUtil.getActivityTitle(activity);
                if (!TextUtils.isEmpty(activityTitle)) {
                    properties.put(AopConstants.TITLE, activityTitle);
                }
            }

            //点击的 position
            properties.put(AopConstants.ELEMENT_POSITION, String.valueOf(position));

            String viewText = null;
            if (view instanceof ViewGroup) {
                try {
                    StringBuilder stringBuilder = new StringBuilder();
                    viewText = AopUtil.traverseView(stringBuilder, (ViewGroup) view);
                    if (!TextUtils.isEmpty(viewText)) {
                        viewText = viewText.substring(0, viewText.length() - 1);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            //$element_content
            if (!TextUtils.isEmpty(viewText)) {
                properties.put(AopConstants.ELEMENT_CONTENT, viewText);
            }

            //fragmentName
            AopUtil.getFragmentNameFromView(adapterView, properties);

            //获取 View 自定义属性
            JSONObject p = (JSONObject) view.getTag(R.id.sensors_analytics_tag_view_properties);
            if (p != null) {
                AopUtil.mergeJSONObject(p, properties);
            }
            ((BaseApplication)(activity.getApplication())).getInterface().saveLogToDb(AopConstants.LIST_ITEM_CLICK,
                    viewText.toString(),idString,currentOnClickTimestamp);
            Log.e(AopConstants.APP_CLICK_EVENT_NAME, properties.toString());
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, " AdapterView.OnItemClickListener.onItemClick AOP ERROR: " + e.getMessage());
        }
    }
}
