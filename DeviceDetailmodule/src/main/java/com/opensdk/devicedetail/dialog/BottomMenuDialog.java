package com.opensdk.devicedetail.dialog;

import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.mm.android.mobilecommon.base.BaseDialogFragment;
import com.mm.android.mobilecommon.widget.CommonMenu4Lc;
import com.opensdk.devicedetail.R;

import java.util.List;


public class BottomMenuDialog extends BaseDialogFragment implements OnClickListener {
    private FrameLayout titleLayout;
    private TextView title, message;
    private ViewGroup btnsView;
    private List<CommonMenu4Lc> mList;
    private View mView;
    private boolean CanceledOnTouchOutside = false;

    private OnDismissListener mOnDismissListener;

    private onMenuOnclickListener mListener;
    private int titleLayoutId;
    private String titleTime;

    public interface onMenuOnclickListener {
        public void onclick(CommonMenu4Lc t);
    }

    public void setOnClickListener(onMenuOnclickListener listener) {
        mListener = listener;
    }

    public static final String TITLE = "TITLE";
    public static final String MESSAGE = "MESSAGE";
    public static final String MESSAGE_GRAVITY = "MESSAGE_GRAVITY";
    public static final String BUTTON_KEY_LIST = "BUTTON_KEY_LIST";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, R.style.mobile_common_checks_dialog);
    }

    @Override public void onStart() {
        super.onStart();
        suitFullScreen();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.mobile_common_dialog_bottom_layout_new, container, false);
        title = mView.findViewById(R.id.dialog_bottom_title);
        message = mView.findViewById(R.id.dialog_bottom_message);
        btnsView = mView.findViewById(R.id.comm);

        if (getArguments() != null) {
            if (getArguments().containsKey(BUTTON_KEY_LIST)) {
                mList = getArguments().getParcelableArrayList(BUTTON_KEY_LIST);
                if (mList != null && !mList.isEmpty()) {
                    for (CommonMenu4Lc i : mList) {
                        addButton(btnsView, inflater, i);
                    }
                }
            }
            if (getArguments().containsKey(TITLE)) {
                Object mObject = getArguments().get(TITLE);
                if (mObject instanceof Integer) {
                    title.setText((Integer) mObject);
                } else if (mObject instanceof String) {
                    title.setText((String) mObject);
                }
            } else {
                title.setVisibility(View.GONE);
            }
            if (getArguments().containsKey(MESSAGE)) {
                Object mObject = getArguments().get(MESSAGE);
                if (mObject instanceof Integer) {
                    message.setText((Integer) mObject);
                } else if (mObject instanceof String) {
                    message.setText((String) mObject);
                }
                if(getArguments().containsKey(MESSAGE_GRAVITY)) {
                    int gravity = getArguments().getInt(MESSAGE_GRAVITY, Gravity.CENTER);
                    message.setGravity(gravity);
                }
            } else {
                message.setVisibility(View.GONE);
            }
        }
        titleLayout = mView.findViewById(R.id.dialog_bottom_title_view2);
        setTitleLayout();
        return mView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);
        view.setOnClickListener(rootClick);
    }

    /* (non-Javadoc)
     * @see android.support.v4.app.DialogFragment#onActivityCreated(android.os.Bundle)
     */
    @Override
    public void onActivityCreated(Bundle arg0) {
        super.onActivityCreated(arg0);
        getDialog().setCanceledOnTouchOutside(CanceledOnTouchOutside);
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if (mOnDismissListener != null) {
            mOnDismissListener.onDismiss(dialog);
        }
    }

    public void setOnDismissListener(OnDismissListener l) {
        mOnDismissListener = l;
    }

    @Override
    public void onClick(View arg0) {
        if (mListener != null) {
            mListener.onclick((CommonMenu4Lc) arg0.getTag());
        }
        dismiss();
    }

    public void setCanceledOnTouchOutside(boolean flag) {
        CanceledOnTouchOutside = flag;
        try {
            if (getDialog() != null) {
                getDialog().setCanceledOnTouchOutside(flag);
            }

        } catch (Exception e) {

        }
    }

    private OnClickListener rootClick = new OnClickListener() {

        @Override
        public void onClick(View arg0) {
            if (CanceledOnTouchOutside) {
                dismiss();
            }
        }
    };


    private void addButton(ViewGroup parent, LayoutInflater inflater, CommonMenu4Lc menu4Lc) {
        Button b = (Button) inflater.inflate(R.layout.mobile_common_dialog_bottom_item_layout, null);


        if (menu4Lc.getStringId() != 0) {
            b.setText(menu4Lc.getStringId());
        }
        if (menu4Lc.getTextAppearance() != 0) {
            b.setTextAppearance(getActivity(), menu4Lc.getTextAppearance());
        }
        if (menu4Lc.getDrawId() != 0) {
            b.setBackgroundResource(menu4Lc.getDrawId());
        }
        if (menu4Lc.getColorId() != 0) {
            b.setTextColor(getResources().getColorStateList(menu4Lc.getColorId()));
        }
        if (menu4Lc.getTextSize() != -1) {
            b.setTextSize(TypedValue.COMPLEX_UNIT_PX, menu4Lc.getTextSize());
        }
        b.setTag(menu4Lc);
        b.setOnClickListener(this);
        LayoutParams mLayoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, getResources().getDimensionPixelSize(
                R.dimen.mobile_common_lc_common_menu_height));
        mLayoutParams.setMargins(menu4Lc.getMargins()[0], menu4Lc.getMargins()[1], menu4Lc.getMargins()[2], menu4Lc.getMargins()[3]);
        btnsView.addView(b, mLayoutParams);
    }

    public void setTitleLayoutId(int layoutId, String time) {
        this.titleLayoutId = layoutId;
        this.titleTime = time;
    }

    private void setTitleLayout() {
        /*if (titleLayout != null && titleLayoutId != 0) {
            View view = LayoutInflater.from(getContext()).inflate(titleLayoutId, null);
            ((TextView)view.findViewById(R.id.share_close_time)).setText(titleTime);
            titleLayout.addView(view);
            titleLayout.setVisibility(View.VISIBLE);
            title.setVisibility(View.GONE);
        }*/
    }


    private void suitFullScreen(){
        Window dialogWindow = getDialog().getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
        lp.height = ViewGroup.LayoutParams.MATCH_PARENT; //注意一定要是MATCH_PARENT
        dialogWindow.setAttributes(lp);
    }

}