package com.mm.android.deviceaddmodule.views.popwindow;

import android.app.Activity;
import android.graphics.Rect;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.mm.android.deviceaddmodule.R;
import com.mm.android.mobilecommon.utils.LogUtil;
import com.mm.android.mobilecommon.widget.CommonTitle;

public class PopWindowFactory {

    private final String TAG=PopWindowFactory.this.getClass().getSimpleName();

    public enum PopWindowType {
        LOADING,        //加载框
        OPTION1,        //更多选项
        OPTION2,        //切换有线
        OPTION3,         //切换无线
        OPTION4,         //切换软AP
        OPTION5,         //手动选择配网方式
        CHOSETYPE,         //手动选择
    }

    public BasePopWindow createPopWindow(final Activity context, CommonTitle commonTitle, PopWindowType type) {
        BasePopWindow popupWindow = null;
        final WindowManager.LayoutParams params = context.getWindow().getAttributes();
        if (type != PopWindowType.LOADING) {
            params.alpha = 0.5f;//设置popwindow弹出时背景
            context.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);//兼容华为手机
            context.getWindow().setAttributes(params);
        }
        switch (type) {
            case LOADING:
                popupWindow = createLoadingPop(context, commonTitle);
                break;
            case OPTION1:
            case OPTION2:
            case OPTION3:
            case OPTION4:
                popupWindow = createOptionPop(context, type);
                break;
            case CHOSETYPE:
                popupWindow = createTypeChosePop(context, type);
                break;
            default:
                break;
        }

        if (popupWindow != null) {
            popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    params.alpha = 1.0f;
                    context.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                    context.getWindow().setAttributes(params);
                }
            });
        }
        return popupWindow;
    }

    public BasePopWindow createLoadingPopWindow(final Activity context, View title) {
        final WindowManager.LayoutParams params = context.getWindow().getAttributes();
        BasePopWindow popupWindow = createLoadingPop(context, (CommonTitle) title);
        if (popupWindow != null) {
            popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    params.alpha = 1.0f;
                    context.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                    context.getWindow().setAttributes(params);
                }
            });
        }
        return popupWindow;
    }

    /**
     * Loading frame
     * @param context  Context object
     * @return BasePopWindow  Pop window
     *
     * 加载框
     * @param context  上下文对象
     * @return BasePopWindow  弹出框
     */
    private BasePopWindow createLoadingPop(final Activity context, final CommonTitle commonTitle) {
        View view = LayoutInflater.from(context).inflate(R.layout.common_progressdialog_layout1, null);
        final BasePopWindow popupWindow = new LoadingPopWindow(view, RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        LogUtil.debugLog(TAG,"createLoadingPop--->" + popupWindow);
        int[] location = new int[2];
        View decorView = context.getWindow().getDecorView();
        Rect rect = new Rect();
        decorView.getWindowVisibleDisplayFrame(rect);
        int screenHeight = rect.bottom;
        commonTitle.getLocationOnScreen(location);
        int height = screenHeight - location[1] - commonTitle.getMeasuredHeight();
        popupWindow.setHeight(height);
        popupWindow.showAsDropDown(commonTitle);
        return popupWindow;
    }

    /**
     * Options dialog
     * @param context  Context object
     * @return BasePopWindow  Pop window
     *
     * 选项框
     * @param context  上下文对象
     * @return BasePopWindow  弹出框
     */
    private BasePopWindow createOptionPop(Activity context, PopWindowType type) {
        View view = LayoutInflater.from(context).inflate(R.layout.more_options_layout, null);
        BasePopWindow popupWindow = new MoreOptionsPopWindow(view, RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        ((MoreOptionsPopWindow) popupWindow).setType(type);
        popupWindow.showAtLocation(context.getWindow().getDecorView(), Gravity.BOTTOM, 0, 0);
        popupWindow.drawContent(context);
        return popupWindow;
    }

    /**
     * Options dialog
     * @param context  Context object
     * @return BasePopWindow  Pop window
     *
     * 选项框
     * @param context  上下文对象
     * @return BasePopWindow  弹出框
     */
    private BasePopWindow createTypeChosePop(Activity context, PopWindowType type) {
        View view = LayoutInflater.from(context).inflate(R.layout.chose_type_layout, null);
        BasePopWindow popupWindow = new ChoseTypePopWindow(view, RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        ((ChoseTypePopWindow) popupWindow).setType(type);
        popupWindow.showAtLocation(context.getWindow().getDecorView(), Gravity.BOTTOM, 0, 0);
        popupWindow.drawContent(context);
        return popupWindow;
    }
}
