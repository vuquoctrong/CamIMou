package com.mm.android.deviceaddmodule.receiver;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.mm.android.mobilecommon.eventbus.event.NoticeToBackEvent;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

/**
 * Hotspot link timeout notification bar click Receive
 *
 * 热点链接超时的通知栏点击接收
 */

public class TimeoutClickReceiver extends BroadcastReceiver {
    public static final String TAG = TimeoutClickReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RecentTaskInfo> recentTaskList = am.getRecentTasks(Integer.MAX_VALUE,
                ActivityManager.RECENT_IGNORE_UNAVAILABLE);
        int taskid = -1;
        if (recentTaskList != null) {
            for (ActivityManager.RecentTaskInfo r : recentTaskList) {
                try {
                    String appName = context.getApplicationContext().getPackageName();
                    if (appName.equals(r.baseIntent.getComponent().getPackageName())) {// 包名一致
                        taskid = r.id;
                        break;
                    }
                } catch (Exception e) {
                }
            }
        }

        if (taskid != -1) {// 表示已启动过
            EventBus.getDefault().post(new NoticeToBackEvent());
            am.moveTaskToFront(taskid, ActivityManager.MOVE_TASK_WITH_HOME);
        }
    }
}
