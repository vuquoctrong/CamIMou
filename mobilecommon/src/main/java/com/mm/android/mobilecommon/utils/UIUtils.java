package com.mm.android.mobilecommon.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.GradientDrawable;
import android.text.TextPaint;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.TouchDelegate;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.EditText;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.mm.android.mobilecommon.R;

import java.lang.reflect.Field;
import java.util.List;

public class UIUtils {
    private static final String TAG = "UIUtils";
    private static long mLastClickTime;

    private static float sDensity = -1;

    /**
     * Gets the screen pixel density
     *
     * 获取屏幕像素密度
     */
    public static float getScreenDensity(Context context) {
        if(context == null)return sDensity;
        DisplayMetrics  dm =  context.getApplicationContext().getResources().getDisplayMetrics();
        sDensity = dm.density;

        return dm.density;
    }


    /**
     * Convert the px value to a dip or dp value, keeping the size the same
     *
     * 将px值转换为dip或dp值，保证尺寸大小不变
     */
    public static int px2dip(DisplayMetrics dM, float pxValue) {
        final float scale = dM.density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * Convert the dip or dp value to a px value, keeping the size unchanged
     *
     * 将dip或dp值转换为px值，保证尺寸大小不变
     */
    public static int dip2px(DisplayMetrics dM, float dipValue) {
        final float scale = dM.density;
        return (int) (dipValue * scale + 0.5f);
    }

    /**
     * Gets the current screen width
     *
     * 获取当前屏幕宽度
     */
    public static int getDefaultDialogWidth(Context context) {
        return getScreenWidth(context) * 4 / 5;
    }
    /**
     * Gets the current screen width
     *
     * 获取当前屏幕宽度
     */
    public static int getScreenWidth(Context context) {
        if(context == null)return -1;
        DisplayMetrics  dm =  context.getApplicationContext().getResources().getDisplayMetrics();
        return dm.widthPixels;
    }

    public static int getDefaultDialogWidthWithLandscape(Context context){
        return getScreenHeight(context) * 4 / 5;
    }

    /**
     * Gets the current screen height
     *
     * 获取当前屏幕高度
     */
    public static int getScreenHeight(Context context) {
        if(context == null)return -1;
        DisplayMetrics  dm =  context.getApplicationContext().getResources().getDisplayMetrics();
        return dm.heightPixels;
    }

    /**
     * Convert the px value to a dip or dp value, keeping the size the same
     *
     * 将px值转换为dip或dp值，保证尺寸大小不变
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * Convert the dip or dp value to a px value, keeping the size unchanged
     *
     * 将dip或dp值转换为px值，保证尺寸大小不变
     */
    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }


    /**
     * Convert the px value to the sp value, keeping the text size the same
     *
     * 将px值转换为sp值，保证文字大小不变
     */
    public static int px2sp(Context context, float pxValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }

    /**
     * Convert the sp value to a px value, keeping the text size unchanged
     *
     * 将sp值转换为px值，保证文字大小不变
     */
    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    /**
     * Screen width in pixels
     *
     * 屏幕宽度（像素）
     */
    public static int getWindowWidth(Activity context) {
        DisplayMetrics metric = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(metric);
        return metric.widthPixels;
    }

    /**
     * Screen height in pixels
     *
     * 屏幕高度（像素）
     */
    public static int getWindowHeight(Activity context) {
        DisplayMetrics metric = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(metric);
        return metric.heightPixels;
    }


    /**
     * Check for repeated click events. The default time is 800 milliseconds
     * @return boolean  Whether to connect twice
     *
     * 检测是否重复点击事件,默认时间为800毫秒
     * @return boolean  是否连点两次
     */
    public static boolean isFastDoubleClick() {
        long time = System.currentTimeMillis();
        long timeD = time - mLastClickTime;
        if (0 < timeD && timeD < 800) {
            return true;
        }
        mLastClickTime = time;
        return false;
    }

    /**
     * Pull the current activity to the front desk
     * @param paramActivity
     *
     * 将当前activity拉到前台
     * @param paramActivity
     */
    public static void goForeground(Activity paramActivity) {
        if (paramActivity == null) return;
        Window localWindow = paramActivity.getWindow();
        if (localWindow != null) {
            localWindow.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            localWindow.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
            localWindow.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        }
    }


    /**
     * Detects if the click event is repeated within the specified time
     * @return boolean  Whether to click again or not
     *
     * 检测在规定的时间里面是否重复点击事件
     * @return boolean  是否重复点击
     */
    public static boolean isFastDoubleClick(long elapse) {
        long time = System.currentTimeMillis();
        long timeD = time - mLastClickTime;
        if (0 < timeD && timeD < elapse) {
            return true;
        }
        mLastClickTime = time;
        return false;
    }
    /**
     * Check whether the same method is quickly triggered twice within the specified time of 100ms, for the compatibility of false trigger
     * @return boolean  Boolean value
     *
     * 检测在100ms规定的时间里面是否快速触发相同方法两次，针对误触发情况的兼容
     * @return boolean  布尔值
     */
    public static boolean isFastCallFuncTwice() {
        long time = System.currentTimeMillis();
        long timeD = time - mLastClickTime;
        if (0 < timeD && timeD < 100) {
            return true;
        }
        mLastClickTime = time;
        return false;
    }
    /**
     * Enter the full screen
     * @param activity
     *
     * 进入全屏
     * @param activity
     */
    public static void setFullScreen(Activity activity) {
        activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    /**
     * Exit full screen
     * @param activity
     *
     * 退出全屏
     * @param activity
     */
    public static void quitFullScreen(Activity activity) {
        if (activity == null) {
            return;
        }
        final WindowManager.LayoutParams attrs = activity.getWindow().getAttributes();
        attrs.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
        activity.getWindow().setAttributes(attrs);
        activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
    }

    /**
     * Do not copy or paste the input box
     * @param editText
     *
     * 输入框禁止复制，粘贴
     * @param editText  输入框
     */
    public static void UnCopyAble(EditText editText) {
        if (editText == null) {
            return;
        }
        editText.setLongClickable(false);
    }

    /**
     * Gets the height of the status bar
     *
     * 获取状态栏高度
     */
    public static int getStatusBarHeight(Context context) {
        Class<?> c = null;
        Object object = null;
        Field field = null;
        int x = 0;
        int statusBar = 0;
        try {
            c = Class.forName("com.android.internal.R$dimen");
            object = c.newInstance();
            field = c.getField("status_bar_height");
            x = Integer.parseInt(field.get(object).toString());
            statusBar = context.getResources().getDimensionPixelSize(x);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return statusBar == 0 ? dp2px(context, 20) : statusBar;
    }

    /**
     * The setting button is selected or not
     * @param selected  If the selected
     * @param views
     *
     * 设置按钮选中与否
     * @param selected  是否选中
     * @param views
     */
    public static void setSelected(boolean selected, View... views) {
        for (View view : views) {
            view.setSelected(selected);
        }
    }

    public static void setSelected(boolean selected, ViewGroup... viewGroups) {
        for (ViewGroup viewGroup : viewGroups) {
            viewGroup.setSelected(selected);
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                View v = viewGroup.getChildAt(i);
                if (v instanceof ViewGroup) {
                    setSelected(selected, (ViewGroup) v);
                } else {
                    setSelected(selected, v);
                }
            }
        }
    }

    /**
     * Set whether space is available (no ash)
     * @param enabled  available or not
     * @param views
     *
     * 设置空间是否可用（不置灰）
     * @param enabled  是否可用
     * @param views
     */
    public static void setEnabled(boolean enabled, View... views) {
        for (View view : views) {
            view.setEnabled(enabled);
        }
    }

    /**
     * Enables/disables controls, including all child controls
     *
     * 启用/禁用控件，包括所有子控件
     */
    public static void setEnabledSub(boolean enabled, ViewGroup... viewGroups) {
        for (ViewGroup viewGroup : viewGroups) {
            viewGroup.setEnabled(enabled);
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                View v = viewGroup.getChildAt(i);
                if (v instanceof ViewGroup) {
                    setEnabledSub(enabled, (ViewGroup) v);
                } else {
                    setEnabled(enabled, v);
                }
            }
        }
    }

    /**
     * Enable/disable item
     * @param enabled  Whether to enable
     * @param viewGroup
     * @param tv  TextView
     *
     * 启用/禁用 item
     * @param enabled  是否启用
     * @param viewGroup
     * @param tv  输入框
     */
    public static void setDevDetailItemEnable(boolean enabled, ViewGroup viewGroup, TextView tv) {
        setEnabledSub(enabled, viewGroup);

        if (tv == null) {
            return;
        }
        if (enabled) {
            tv.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.mobile_common_icon_nextarrow, 0);
        } else {
            tv.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        }
    }

    /**
     * Set whether the control is available (gray)
     * @param views  The control to be set
     * @param enabled  available or not
     *
     * 设置控件可用与否 (置灰)
     * @param views   被设置的控件
     * @param enabled 是否可用
     */
    public static void setEnabledEX(boolean enabled, View... views) {
        for (View view : views) {
            if (enabled) {
                view.setEnabled(enabled);
                view.clearAnimation();
            } else {
                Animation aniAlp = new AlphaAnimation(1f, 0.5f);
                aniAlp.setDuration(0);
                aniAlp.setInterpolator(new AccelerateDecelerateInterpolator());
                aniAlp.setFillEnabled(true);
                aniAlp.setFillAfter(true);
                view.startAnimation(aniAlp);
                view.setEnabled(enabled);
            }
        }
    }

    /**
     * Traverse the layout, disabling/enabling all child controls
     * @param viewGroup  Whether to enable
     * @param enabled
     *
     * 遍历布局，禁用/启用所有子控件
     * @param viewGroup
     * @param enabled  是否启用
     */
    public static void setEnabledSubControls(ViewGroup viewGroup, boolean enabled) {
        setEnabledEX(enabled, viewGroup);
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            View v = viewGroup.getChildAt(i);
            if (v instanceof ViewGroup) {
                setEnabledSubControls((ViewGroup) v, enabled);
            } else {
                setEnabledEX(enabled, v);
            }
        }
    }

    /**
     * Hide/Show
     * @param visibility  Whether or not shown
     * @param views
     *
     * 隐藏/显示
     * @param visibility  是否显示
     * @param views
     */
    public static void setVisibility(int visibility, View... views) {
        for (View view : views) {
            view.setVisibility(visibility);
        }
    }

    public static void expandViewTouchDelegate(final View view, final int top, final int bottom, final int left,
                                               final int right) {

        ((View) view.getParent()).post(new Runnable() {

            @Override
            public void run() {
                Rect bounds = new Rect();
                view.setEnabled(true);
                view.getHitRect(bounds);

                bounds.top -= top;
                bounds.bottom += bottom;
                bounds.left -= left;
                bounds.right += right;

                TouchDelegate touchDelegate = new TouchDelegate(bounds, view);

                if (View.class.isInstance(view.getParent())) {
                    ((View) view.getParent()).setTouchDelegate(touchDelegate);
                }

            }
        });
    }


    /**
     * Sets a set of controls to appear or hide
     * @param views
     * @param flags
     *
     * 设置一组控件显隐
     * @param views
     * @param flags
     */
    public static void setViewsVisibility(View[] views, int[] flags) {
        if (views == null) {
            return;
        }
        for (int i = 0; i < views.length; i++) {
            if (views[i] != null) views[i].setVisibility(i < flags.length ? flags[i] : View.GONE);
        }
    }

    /**
     * Sets the control to be explicit or implicit
     * @param view
     * @param flag
     *
     * 设置控件显隐
     * @param view
     * @param flag
     */
    public static void setViewVisibility(View view, int flag) {
        if (view == null) {
            return;
        }
        view.setVisibility(flag);
    }

    public static void setEnableWithAlphaChanged(View view, boolean enable) {
        if (view == null) {
            return;
        }
        view.setEnabled(enable);
        view.setAlpha(enable ? 1f : 0.5f);
    }

    public static void setEnabledAllInGroup(ViewGroup viewGroup, boolean enabled) {
        if (viewGroup == null) {
            return;
        }
        viewGroup.setEnabled(enabled);
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            View v = viewGroup.getChildAt(i);
            if (v instanceof ViewGroup) {
                setEnabledAllInGroup((ViewGroup) v, enabled);
            } else {
                v.setEnabled(enabled);
            }
        }
    }

    public static int getEncryV2(int byAuthMode, int byEncrAlgr) {
        int nEncryption = 0;

        if(byAuthMode == 6 && byEncrAlgr == 0)
        {
            nEncryption = 0;
        }
        else if(byAuthMode == 0 && byEncrAlgr == 0)
        {
            nEncryption = 1;
        }
        else  if(byAuthMode == 0 && byEncrAlgr == 4)
        {
            nEncryption = 2;
        }
        else  if(byAuthMode == 1 && byEncrAlgr == 4)
        {
            nEncryption = 3;
        }
        else  if(byAuthMode == 2 && byEncrAlgr == 5)
        {
            nEncryption = 4;
        }
        else  if(byAuthMode == 3 && byEncrAlgr == 5)
        {
            nEncryption = 5;
        }
        else  if(byAuthMode == 4 && byEncrAlgr == 5)
        {
            nEncryption = 6;
        }
        else  if(byAuthMode == 5 && byEncrAlgr == 5)
        {
            nEncryption = 7;
        }
        else  if(byAuthMode == 2 && byEncrAlgr == 6)
        {
            nEncryption = 8;
        }
        else  if(byAuthMode == 3 && byEncrAlgr == 6)
        {
            nEncryption = 9;
        }
        else  if(byAuthMode == 4 && byEncrAlgr == 6)
        {
            nEncryption = 10;
        }
        else  if(byAuthMode == 5 && byEncrAlgr == 6)
        {
            nEncryption = 11;
        }
        else  if(byAuthMode == 2 && byEncrAlgr == 7)
        {
            nEncryption = 8;  // 4或者8
        }
        else  if(byAuthMode == 3 && byEncrAlgr == 7)
        {
            nEncryption = 9;  // 5或9
        }
        else  if(byAuthMode == 4 && byEncrAlgr == 7)
        {
            nEncryption = 10;  // 6或10
        }
        else  if(byAuthMode == 5 && byEncrAlgr == 7)
        {
            nEncryption = 11;  // 7或11
        }
        else if(byAuthMode == 7)  // 混合模式WPA-PSK|WPA2-PSK   3或5
        {
            if(byEncrAlgr == 5) {
                nEncryption = 7;  // 5或7
            }
            else if(byEncrAlgr == 6)
            {
                nEncryption = 11;  // 9或11
            }
            else if(byEncrAlgr == 7)
            {
                nEncryption = 11;  // 5或7或9或11
            }
            else
            {
                nEncryption = 12;
            }
        }
        else if(byAuthMode == 8)  // 混合模式WPA|WPA2    2或4
        {
            if(byEncrAlgr == 5) {
                nEncryption = 6;  // 4或6
            }
            else if(byEncrAlgr == 6)
            {
                nEncryption = 10;  // 8或10
            }
            else if(byEncrAlgr == 7)
            {
                nEncryption = 10;  // 4或6或8或10
            }
            else
            {
                nEncryption = 12;
            }
        }
        else if(byAuthMode == 9)  // 混合模式WPA|WPA-PSK  2或3
        {
            if(byEncrAlgr == 5) {
                nEncryption = 5;  // 4或5
            }
            else if(byEncrAlgr == 6)
            {
                nEncryption = 9;  // 8或9
            }
            else if(byEncrAlgr == 7)
            {
                nEncryption = 9;  // 4或5或8或9
            }
            else
            {
                nEncryption = 12;
            }
        }
        else if(byAuthMode == 10)  // 混合模式WPA2|WPA2-PSK  4或5
        {
            if(byEncrAlgr == 5) {
                nEncryption = 7;  // 6或7
            }
            else if(byEncrAlgr == 6)
            {
                nEncryption = 11;  // 10或11
            }
            else if(byEncrAlgr == 7)
            {
                nEncryption = 11;  // 6或7或10或11
            }
            else
            {
                nEncryption = 12;
            }
        }
        else if(byAuthMode == 11)  // 混合模式WPA|WPA-PSK|WPA2|WPA2-PSK  2或3或4或5
        {
            if(byEncrAlgr == 5) {
                nEncryption = 7;  // 4或5或6或7
            }
            else if(byEncrAlgr == 6)
            {
                nEncryption = 11;  // 8或9或10或11
            }
            else if(byEncrAlgr == 7)
            {
                nEncryption = 11;  // 4或5或6或7或8或9或10或11
            }
            else
            {
                nEncryption = 12;
            }
        } else {
            nEncryption = 12;
        }
        return nEncryption;
    }

    public static int getEncry(int byAuthMode, int byEncrAlgr) {
        int nEncryption = 0;

        if(byAuthMode == 6 && byEncrAlgr == 0)
        {
            nEncryption = 0;
        }
        else if(byAuthMode == 0 && byEncrAlgr == 0)
        {
            nEncryption = 1;
        }
        else  if(byAuthMode == 0 && byEncrAlgr == 1)
        {
            nEncryption = 2;
        }
        else  if(byAuthMode == 1 && byEncrAlgr == 1)
        {
            nEncryption = 3;
        }
        else  if(byAuthMode == 2 && byEncrAlgr == 2)
        {
            nEncryption = 4;
        }
        else  if(byAuthMode == 3 && byEncrAlgr == 2)
        {
            nEncryption = 5;
        }
        else  if(byAuthMode == 4 && byEncrAlgr == 2)
        {
            nEncryption = 6;
        }
        else  if(byAuthMode == 5 && byEncrAlgr == 2)
        {
            nEncryption = 7;
        }
        else  if(byAuthMode == 2 && byEncrAlgr == 3)
        {
            nEncryption = 8;
        }
        else  if(byAuthMode == 3 && byEncrAlgr == 3)
        {
            nEncryption = 9;
        }
        else  if(byAuthMode == 4 && byEncrAlgr == 3)
        {
            nEncryption = 10;
        }
        else  if(byAuthMode == 5 && byEncrAlgr == 3)
        {
            nEncryption = 11;
        }
        else  if(byAuthMode == 2 && byEncrAlgr == 4)
        {
            nEncryption = 8;  // 4或者8
        }
        else  if(byAuthMode == 3 && byEncrAlgr == 4)
        {
            nEncryption = 9;  // 5或9
        }
        else  if(byAuthMode == 4 && byEncrAlgr == 4)
        {
            nEncryption = 10;  // 6或10
        }
        else  if(byAuthMode == 5 && byEncrAlgr == 4)
        {
            nEncryption = 11;  // 7或11
        }
        else if(byAuthMode == 7)  // 混合模式WPA-PSK|WPA2-PSK   3或5
        {
            if(byEncrAlgr == 2) {
                nEncryption = 7;  // 5或7
            }
            else if(byEncrAlgr == 3)
            {
                nEncryption = 11;  // 9或11
            }
            else if(byEncrAlgr == 4)
            {
                nEncryption = 11;  // 5或7或9或11
            }
            else
            {
                nEncryption = 12;
            }
        }
        else if(byAuthMode == 8)  // 混合模式WPA|WPA2    2或4
        {
            if(byEncrAlgr == 2) {
                nEncryption = 6;  // 4或6
            }
            else if(byEncrAlgr == 3)
            {
                nEncryption = 10;  // 8或10
            }
            else if(byEncrAlgr == 4)
            {
                nEncryption = 10;  // 4或6或8或10
            }
            else
            {
                nEncryption = 12;
            }
        }
        else if(byAuthMode == 9)  // 混合模式WPA|WPA-PSK  2或3
        {
            if(byEncrAlgr == 2) {
                nEncryption = 5;  // 4或5
            }
            else if(byEncrAlgr == 3)
            {
                nEncryption = 9;  // 8或9
            }
            else if(byEncrAlgr == 4)
            {
                nEncryption = 9;  // 4或5或8或9
            }
            else
            {
                nEncryption = 12;
            }
        }
        else if(byAuthMode == 10)  // 混合模式WPA2|WPA2-PSK  4或5
        {
            if(byEncrAlgr == 2) {
                nEncryption = 7;  // 6或7
            }
            else if(byEncrAlgr == 3)
            {
                nEncryption = 11;  // 10或11
            }
            else if(byEncrAlgr == 4)
            {
                nEncryption = 11;  // 6或7或10或11
            }
            else
            {
                nEncryption = 12;
            }
        }
        else if(byAuthMode == 11)  // 混合模式WPA|WPA-PSK|WPA2|WPA2-PSK  2或3或4或5
        {
            if(byEncrAlgr == 2) {
                nEncryption = 7;  // 4或5或6或7
            }
            else if(byEncrAlgr == 3)
            {
                nEncryption = 11;  // 8或9或10或11
            }
            else if(byEncrAlgr == 4)
            {
                nEncryption = 11;  // 4或5或6或7或8或9或10或11
            }
            else
            {
                nEncryption = 12;
            }
        } else {
            nEncryption = 12;
        }
        return nEncryption;
    }

    public static int getEncry4Sc(int byAuthMode, int byEncrAlgr) {

        int nEncryption = 0;

        if (byAuthMode == 6 && byEncrAlgr == 0) {
            nEncryption = 0;
        } else if (byAuthMode == 0 && byEncrAlgr == 0) {
            nEncryption = 1;
        } else if (byAuthMode == 0 && byEncrAlgr == 4) {
            nEncryption = 13;
        } else if (byAuthMode == 1 && byEncrAlgr == 4) {
            nEncryption = 14;
        } else if (byAuthMode == 2 && byEncrAlgr == 5) {
            nEncryption = 8;
        } else if (byAuthMode == 3 && byEncrAlgr == 5) {
            nEncryption = 4;
        } else if (byAuthMode == 4 && byEncrAlgr == 5) {
            nEncryption = 10;
        } else if (byAuthMode == 5 && byEncrAlgr == 5) {
            nEncryption = 6;
        } else if (byAuthMode == 2 && byEncrAlgr == 6) {
            nEncryption = 9;
        } else if (byAuthMode == 3 && byEncrAlgr == 6) {
            nEncryption = 5;
        } else if (byAuthMode == 4 && byEncrAlgr == 6) {
            nEncryption = 11;
        } else if (byAuthMode == 5 && byEncrAlgr == 6) {
            nEncryption = 7;
        } else if (byAuthMode == 2 && byEncrAlgr == 7) {
            nEncryption = 9;  // 8或者9
        } else if (byAuthMode == 3 && byEncrAlgr == 7) {
            nEncryption = 5;  // 4或5
        } else if (byAuthMode == 4 && byEncrAlgr == 7) {
            nEncryption = 11;  // 10或11
        } else if (byAuthMode == 5 && byEncrAlgr == 7) {
            nEncryption = 7;  // 6或7
        } else if (byAuthMode == 7)  // 混合模式WPA-PSK|WPA2-PSK   3或5
        {
            if (byEncrAlgr == 5) {
                nEncryption = 6;  // 4或6
            } else if (byEncrAlgr == 6) {
                nEncryption = 7;  // 5或7
            } else if (byEncrAlgr == 7) {
                nEncryption = 7;  // 4或5或6或7
            } else {
                nEncryption = 12;
            }
        } else if (byAuthMode == 8)  // 混合模式WPA|WPA2    2或4
        {
            if (byEncrAlgr == 5) {
                nEncryption = 10;  // 8或10
            } else if (byEncrAlgr == 6) {
                nEncryption = 11;  // 9或11
            } else if (byEncrAlgr == 7) {
                nEncryption = 10;  // 8或9或10或11
            } else {
                nEncryption = 12;
            }
        } else if (byAuthMode == 9)  // 混合模式WPA|WPA-PSK  2或3
        {
            if (byEncrAlgr == 5) {
                nEncryption = 8;  // 4或8
            } else if (byEncrAlgr == 6) {
                nEncryption = 9;  // 5或9
            } else if (byEncrAlgr == 7) {
                nEncryption = 9;  // 4或5或8或9
            } else {
                nEncryption = 12;
            }
        } else if (byAuthMode == 10)  // 混合模式WPA2|WPA2-PSK  4或5
        {
            if (byEncrAlgr == 5) {
                nEncryption = 10;  // 6或10
            } else if (byEncrAlgr == 6) {
                nEncryption = 11;  // 7或11
            } else if (byEncrAlgr == 7) {
                nEncryption = 11;  // 6或7或10或11
            } else {
                nEncryption = 12;
            }
        } else if (byAuthMode == 11)  // 混合模式WPA|WPA-PSK|WPA2|WPA2-PSK  2或3或4或5
        {
            if (byEncrAlgr == 5) {
                nEncryption = 10;  // 4或6或8或10
            } else if (byEncrAlgr == 6) {
                nEncryption = 11;  // 5或7或9或11
            } else if (byEncrAlgr == 7) {
                nEncryption = 11;  // 4或5或6或7或8或9或10或11
            } else {
                nEncryption = 12;
            }
        } else {
            nEncryption = 12;
        }
        return nEncryption;
    }

    public static Fragment getVisibleFragment(FragmentActivity activity) {
        FragmentManager fragmentManager = activity.getSupportFragmentManager();
        @SuppressLint("RestrictedApi") List<Fragment> fragments = fragmentManager.getFragments();
        for (Fragment fragment : fragments) {
            if (fragment != null && fragment.isVisible()) {
                return fragment;
            }
        }

        return null;
    }

    public static boolean isAppOnForeground(Context context) {
        if (context == null) {
            return false;
        }
        ActivityManager mActivityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (mActivityManager == null) {
            return false;
        }
        List<ActivityManager.RunningTaskInfo> tasksInfo = mActivityManager.getRunningTasks(1);
        if (tasksInfo != null && tasksInfo.size() > 0) {
            // 应用程序位于堆栈的顶层
            ComponentName componentName = tasksInfo.get(0).topActivity;
            if (componentName != null && context.getPackageName().equals(componentName.getPackageName())) {
                return true;
            }
        }
        return false;
    }

    public static boolean isAssignClass(Context context, String className){
        if (context == null) {
            return false;
        }
        ActivityManager mActivityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (mActivityManager == null) {
            return false;
        }
        List<ActivityManager.RunningTaskInfo> tasksInfo = mActivityManager.getRunningTasks(1);
        if (tasksInfo != null && tasksInfo.size() > 0) {
            ComponentName componentName = tasksInfo.get(0).topActivity;
            if (componentName != null) {
                LogUtil.debugLog("32752", "className->"+componentName.getClassName() + " & packageName->" + componentName.getPackageName());
                if (componentName.getClassName().contains(className)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean listIsEmpty(List list){
        return list == null || list.isEmpty();
    }

    /**
     * 测量字符高度
     *
     * @param text
     * @return
     */
    public static int getTextHeight(TextPaint textPaint, String text) {
        Rect bounds = new Rect();
        textPaint.getTextBounds(text, 0, text.length(), bounds);
        return bounds.height();
    }

    public static boolean isFragmentExist(Fragment fragment){
        return fragment != null && fragment.isAdded() && fragment.isVisible();
    }

    public static GradientDrawable createShape(int color, int radius, Context context){
        GradientDrawable drawable = new GradientDrawable();
        drawable.setCornerRadius(UIUtils.dip2px(context, radius));
        drawable.setColor(color);
        return drawable;
    }

    public static int makeDropDownMeasureSpec(int measureSpec){
        int mode;
        if(measureSpec == ViewGroup.LayoutParams.WRAP_CONTENT){
            mode = View.MeasureSpec.UNSPECIFIED;
        }else{
            mode = View.MeasureSpec.EXACTLY;
        }
        return View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(measureSpec), mode);
    }

    public static int dp2px(Context context, float dpValue){
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,dpValue,context.getResources().getDisplayMetrics());
    }

    public static float px2dp(Context context, float dpValue){
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX,dpValue,context.getResources().getDisplayMetrics());
    }

    // 设置view 的margin值
    public static void setMargins(View v, int l, int t, int r, int b) {
        if (v.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            p.setMargins(l, t, r, b);
            v.requestLayout();
        }
    }
}
