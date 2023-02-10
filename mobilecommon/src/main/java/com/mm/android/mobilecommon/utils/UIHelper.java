package com.mm.android.mobilecommon.utils;

import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;

public class UIHelper {
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
     * @param enabled  Whether to enable
     *
     * 启用/禁用控件，包括所有子控件
     * @param enabled  是否启用
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
     * Set whether the control is available (gray)
     * @param enabled  available or not
     *
     * 设置控件可用与否 (置灰)
     * @param enabled  是否可用
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
     * @param enabled  Whether to enable
     *
     * 遍历布局，禁用/启用所有子控件
     * @param enabled  是否启用
     */
    public static void setEnabledSubEX(boolean enabled, ViewGroup... viewGroups) {
        for (ViewGroup viewGroup : viewGroups) {
            setEnabledEX(enabled, viewGroup);
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                View v = viewGroup.getChildAt(i);
                if (v instanceof ViewGroup) {
                    setEnabledSubEX(enabled, (ViewGroup) v);
                } else {
                    setEnabledEX(enabled, v);
                }
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
}
