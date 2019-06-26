package com.example.zhengmin.maidian.aop;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.SwitchCompat;
import android.text.TextUtils;
import android.util.Log;
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


/**
 * Created by zhengmin on 2018/3/14.
 */
@Aspect
public class ViewOnClickListenerAspectj {
    private final static String TAG = "ViewOnClick";
    @After("execution(* android.view.View.OnClickListener.onClick(android.view.View))")
    public void onViewClickAOP(final JoinPoint joinPoint) throws Throwable {
        Log.e("zhengmin", "onViewOnClick");
        try {
            //基本校验
            if (joinPoint.getArgs() == null || joinPoint.getArgs().length != 1) {
                return;
            }


            //获取被点击的 View
            View view = (View) joinPoint.getArgs()[0];
            if (view == null) {
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
            CharSequence viewText = null;
            if (view instanceof CheckBox) { // CheckBox
                viewType = "CheckBox";
                CheckBox checkBox = (CheckBox) view;
                viewText = checkBox.getText();
            } else if (view instanceof SwitchCompat) {
                viewType = "SwitchCompat";
                SwitchCompat switchCompat = (SwitchCompat) view;
                viewText = switchCompat.getTextOn();
            } else if (view instanceof RadioButton) { // RadioButton
                viewType = "RadioButton";
                RadioButton radioButton = (RadioButton) view;
                viewText = radioButton.getText();
            } else if (view instanceof ToggleButton) { // ToggleButton
                viewType = "ToggleButton";
                ToggleButton toggleButton = (ToggleButton) view;
                boolean isChecked = toggleButton.isChecked();
                if (isChecked) {
                    viewText = toggleButton.getTextOn();
                } else {
                    viewText = toggleButton.getTextOff();
                }
            } else if (view instanceof Button) { // Button
                viewType = "Button";
                Button button = (Button) view;
                viewText = button.getText();
            } else if (view instanceof CheckedTextView) { // CheckedTextView
                viewType = "CheckedTextView";
                CheckedTextView textView = (CheckedTextView) view;
                viewText = textView.getText();
            } else if (view instanceof TextView) { // TextView
                viewType = "TextView";
                TextView textView = (TextView) view;
                viewText = textView.getText();
            } else if (view instanceof ImageButton) { // ImageButton
                viewType = "ImageButton";
            } else if (view instanceof ImageView) { // ImageView
                viewType = "ImageView";
            } else if (view instanceof ViewGroup) {
                try {
                    StringBuilder stringBuilder = new StringBuilder();
                    viewText = AopUtil.traverseView(stringBuilder, (ViewGroup) view);
                    if (!TextUtils.isEmpty(viewText)) {
                        viewText = viewText.toString().substring(0, viewText.length() - 1);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            //$element_type
            properties.put(AopConstants.ELEMENT_TYPE, viewType);

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

            Log.e(AopConstants.APP_CLICK_EVENT_NAME, properties.toString());
            ((BaseApplication)(activity.getApplication())).getInterface().saveLogToDb(AopConstants.VIEW_ON_CLICK,
                    viewText.toString(),idString,currentOnClickTimestamp);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "onViewClickMethod error: " + e.getMessage());
        }
    }
}
