package com.mm.android.mobilecommon.dialog;

import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnticipateOvershootInterpolator;
import android.widget.TextView;

import com.mm.android.mobilecommon.R;
import com.mm.android.mobilecommon.base.BaseDialogFragment;
import com.mm.android.mobilecommon.common.LCConfiguration;
import com.mm.android.mobilecommon.widget.antistatic.spinnerwheel.AbstractWheel;
import com.mm.android.mobilecommon.widget.antistatic.spinnerwheel.OnWheelChangedListener;
import com.mm.android.mobilecommon.widget.antistatic.spinnerwheel.adapters.ArrayWheelAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

/**
 * The single option box suggests using the singlewheelpickerdialogas (optimized roller experience)
 *
 * 单项选择框建议使用SingleWheelPickerDialog（优化滚轮体验）
 */
@Deprecated
public class CommonRollSelectDialog extends BaseDialogFragment implements OnKeyListener {

    private TextView mTitle;
    private TextView mConfirmBtn;
    private AbstractWheel mVerticalWheel;
    private TextView mDescription;
    private OnClickListener mConfirmListener;
    private int mSelectedIndex;
    private String[] mWheelData;// wheel 数据源

    public static CommonRollSelectDialog newInstance(String dialogTitle, int selectIndex, String description, TreeMap<Integer, String> choiceMap) {
        CommonRollSelectDialog dialog = new CommonRollSelectDialog();
        Bundle bundle = new Bundle();
        bundle.putString(LCConfiguration.COMMON_SELECT_DIALOG_TITLE,dialogTitle);
        bundle.putSerializable(LCConfiguration.COMMON_SOURCE_INFO, choiceMap);
        bundle.putSerializable(LCConfiguration.COMMON_CURRENT_SELECTED_INDEX, selectIndex);
        bundle.putString(LCConfiguration.COMMON_SELECT_DIALOG_DESCRIPTION, description);
        dialog.setArguments(bundle);
        return dialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, R.style.mobile_common_checks_dialog);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.mobile_common_roll_select_dialog, null);
        initView(view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getDialog().setCanceledOnTouchOutside(true);

        Window win = getDialog().getWindow();
        if (win != null) {
            win.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            WindowManager.LayoutParams localLayoutParams = win.getAttributes();
            localLayoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
            localLayoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
            localLayoutParams.gravity = Gravity.BOTTOM;
            getDialog().getWindow().setAttributes(localLayoutParams);
        }
    }

    private void initView(View rootView) {

        mTitle = rootView.findViewById(R.id.center_title);
        mVerticalWheel = rootView.findViewById(R.id.wheel_vertical_view);
        mDescription = rootView.findViewById(R.id.description);
        mConfirmBtn = rootView.findViewById(R.id.confirm_btn);
        TextView cancelButton = rootView.findViewById(R.id.cancel_btn);

        cancelButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        mConfirmBtn.setOnClickListener(mConfirmListener);
        initData();
    }

    private void initData() {
        Bundle bundle = getArguments();
        if (bundle == null) return;

        TreeMap<Integer, String> map = (TreeMap<Integer, String>) bundle.getSerializable(LCConfiguration.COMMON_SOURCE_INFO);
        mSelectedIndex = bundle.getInt(LCConfiguration.COMMON_CURRENT_SELECTED_INDEX, -1);
        String description = bundle.getString(LCConfiguration.COMMON_SELECT_DIALOG_DESCRIPTION, "");
        if (map == null || map.size() == 0 || mSelectedIndex == -1) return;

        String title = bundle.getString(LCConfiguration.COMMON_SELECT_DIALOG_TITLE, "");

        List<Integer> keyList = new ArrayList<>(map.keySet());
        mWheelData = new String[keyList.size()];
        for (int i = 0; i < keyList.size(); i++) {
            Integer key = keyList.get(i);
            if (map.get(key) != null) {
                mWheelData[i] = map.get(key);
            }
        }

        mTitle.setText(title);
        mDescription.setText(description);

        initWheels();
    }

    private void initWheels() {

        mVerticalWheel.setViewAdapter(new ArrayWheelAdapter(getActivity(), mWheelData, R.layout.wheel_text, R.id.text));
        mVerticalWheel.setCyclic(false);
        mVerticalWheel.setInterpolator(new AnticipateOvershootInterpolator());
        mVerticalWheel.addChangingListener(new OnWheelChangedListener() {

            @Override
            public void onChanged(AbstractWheel wheel, int oldValue, int newValue) {
                mSelectedIndex = newValue;
            }
        });
        mVerticalWheel.setCurrentItem(mSelectedIndex);

    }

    public void setConfirmButtonClickListener(OnClickListener listener) {
        mConfirmListener = listener;
    }

    public int getCurrentSelectedIndex() {
        return mSelectedIndex;
    }

    @Override
    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            dismissAllowingStateLoss();
            return false;
        }
        return false;
    }
}
