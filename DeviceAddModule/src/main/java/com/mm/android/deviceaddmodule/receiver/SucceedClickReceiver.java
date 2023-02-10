package com.mm.android.deviceaddmodule.receiver;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.List;

/**
 * Click the notification bar to receive the hotspot connection
 *
 * 热点连接成功后通知栏的点击接收
 */

public class SucceedClickReceiver extends BroadcastReceiver {
    public static final String TAG = SucceedClickReceiver.class.getSimpleName();

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
            am.moveTaskToFront(taskid, ActivityManager.MOVE_TASK_WITH_HOME);
        }
    }
}
