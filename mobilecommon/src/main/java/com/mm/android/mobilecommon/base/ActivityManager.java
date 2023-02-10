package com.mm.android.mobilecommon.base;

import android.app.Activity;

import java.util.Stack;

/**
 * Activity stack management
 *
 * activity 栈管理
 */
public class ActivityManager {
    private static Stack<Activity> activityStack;

    private static ActivityManager instance;

    private ActivityManager() {
    }

    public static ActivityManager getScreenManager() {
        if (instance == null) {
            instance = new ActivityManager();
        }
        return instance;
    }

    /**
     * Exit stack top activity
     *
     * 退出栈顶Activity
     */
    public void popActivity(Activity activity) {
        if (activity != null) {
            // 在从自定义集合中取出当前Activity时，也进行了Activity的关闭操作
            //activity.finish();
            activityStack.remove(activity);
            activity = null;
        }
    }

    /**
     * Get the current stack top activity
     *
     * 获得当前栈顶Activity
     */
    public Activity currentActivity() {
        Activity activity = null;
        try{
            if (activityStack != null && !activityStack.empty()){
                activity = activityStack.lastElement();
            }
        }catch (Exception e){
            return activity;
        }
        return activity;
    }

    /**
     * Push current activity into the stack
     *
     * 将当前Activity推入栈中
     */
    public void pushActivity(Activity activity) {
        if (activityStack == null) {
            activityStack = new Stack<>();
        }
        activityStack.add(activity);
    }

    /**
     * Exit all activity in the stack
     *
     * 退出栈中所有Activity
     */
    public void popAllActivityExceptOne(Class cls) {
        while (true) {
            Activity activity = currentActivity();
            if (activity == null) {
                break;
            }
            if (activity.getClass().equals(cls)) {
                break;
            }
            popActivity(activity);
        }
    }
}
