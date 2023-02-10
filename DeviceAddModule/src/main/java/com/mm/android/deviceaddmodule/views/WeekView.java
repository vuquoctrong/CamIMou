package com.mm.android.deviceaddmodule.views;

import static com.mm.android.mobilecommon.common.LCConfiguration.APP_LECHANGE_OVERSEA;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.mm.android.deviceaddmodule.R;
import com.mm.android.deviceaddmodule.helper.InterfaceConstant;
import com.mm.android.mobilecommon.AppConsume.ProviderManager;
import com.mm.android.mobilecommon.utils.UIUtils;


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;


/**
 * Created by zhengcong on 2018/8/21.
 * Week selection control
 *
 * Created by zhengcong on 2018/8/21.
 * 星期选择控件
 */
public class WeekView extends RelativeLayout {

    private static final int ITEM_MARGIN = 5;      //单位dp
    private boolean isIt; // 是否是意大利语

    public interface OnItemClickListener {
        void onItemClickListener(InterfaceConstant.Period period, int position);
    }

    private OnItemClickListener mOnItemClickListener;
    private List<InterfaceConstant.Period> mDays;
    private Set<InterfaceConstant.Period> mSelectDays;
    private int mCurSelectPosition;
    private int mItemMargin;
    private RecyclerView mDayListView;
    private Context mContext;
    private WeekAdapter mAdapter;

    public WeekView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WeekView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        mItemMargin = UIUtils.dip2px(mContext, ITEM_MARGIN);
        mDays = new ArrayList<>();
        mSelectDays = new HashSet<>();
        isIt = Locale.getDefault().getLanguage().equalsIgnoreCase("it");
        Collections.addAll(mDays, InterfaceConstant.Period.values());
        initView();
    }

    public WeekView(Context context) {
        this(context, null);
    }

    /**
     * Initialize the control component
     *
     * 初始化控件
     */
    private void initView() {
        View view = LayoutInflater.from(mContext).
                inflate(R.layout.widget_week_view, this, true);
        mDayListView = (RecyclerView) view.findViewById(R.id.recycler_view);
        LinearLayoutManager manager = new LinearLayoutManager(mContext);
        manager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mDayListView.setLayoutManager(manager);
        mAdapter = new WeekAdapter();

        mDayListView.setAdapter(mAdapter);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    public void setSelectedDays(List<InterfaceConstant.Period> days) {
        if (days == null)
            return;

        mSelectDays.clear();
        for (InterfaceConstant.Period period : days) {
            mSelectDays.add(period);
        }
        mAdapter.notifyDataSetChanged();
    }

    public void addSelectedDay(InterfaceConstant.Period day) {
        if (day == null)
            return;
        this.mSelectDays.add(day);
        mAdapter.notifyDataSetChanged();
    }

    public void removeSelectedDay(InterfaceConstant.Period day) {
        if (day == null)
            return;

        for (InterfaceConstant.Period period : mSelectDays) {
            if (period.name().equalsIgnoreCase(day.name())) {
                mSelectDays.remove(period);
                break;
            }
        }
        mAdapter.notifyDataSetChanged();
    }

    public void setSelectedPeriod(InterfaceConstant.Period period) {
        mCurSelectPosition = mDays.indexOf(period);
        mAdapter.notifyDataSetChanged();
    }

    class PeriodHolder extends RecyclerView.ViewHolder {

        private TextView periodBtn;
        private ImageView arrow;

        PeriodHolder(View itemView) {
            super(itemView);
        }

        private TextView getPeriodBtn() {
            if (periodBtn == null)
                periodBtn = (TextView) itemView.findViewById(R.id.week_view_item);
            return periodBtn;
        }

        private ImageView getArrowView() {
            if (arrow == null)
                arrow = (ImageView) itemView.findViewById(R.id.arrow);
            return arrow;
        }
    }

    private class WeekAdapter extends RecyclerView.Adapter<PeriodHolder> {

        @Override
        public PeriodHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(mContext).
                    inflate(R.layout.widget_item_week_view, parent, false);
            PeriodHolder holder = new PeriodHolder(view);
            int windowWidth = mDayListView.getWidth();
            int windowHeight = mDayListView.getHeight();
            int itemWidth = (windowWidth - (mDays.size() * 2 * mItemMargin)) / mDays.size();
            int itemRadius = itemWidth > windowHeight ? windowHeight : itemWidth;
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(itemRadius, itemRadius);
            params.setMargins(mItemMargin, mItemMargin, mItemMargin, 0);
            holder.getPeriodBtn().setLayoutParams(params);
            holder.getPeriodBtn().setBackgroundResource(R.drawable.setting_period_bg_n);
            return holder;
        }

        @Override
        public void onBindViewHolder(final PeriodHolder holder, int position) {
            String period;
            InterfaceConstant.Period i = mDays.get(position);
            if (i == InterfaceConstant.Period.Monday) {
                period = mContext.getResources().getString(R.string.device_manager_monday);
                if (ProviderManager.getAppProvider().getAppType() == APP_LECHANGE_OVERSEA) {
                    period = isIt ? "L" : "M";
                } else {
                    period = "一";
                }
            } else if (i == InterfaceConstant.Period.Tuesday) {
                if (ProviderManager.getAppProvider().getAppType() == APP_LECHANGE_OVERSEA) {
                    period = mContext.getResources().getString(R.string.device_manager_tuesday);
                    period = isIt ? "M" : "T";
                } else {
                    period = "二";
                }
            } else if (i == InterfaceConstant.Period.Wednesday) {
                if (ProviderManager.getAppProvider().getAppType() == APP_LECHANGE_OVERSEA) {
                    period = mContext.getResources().getString(R.string.device_manager_wednesday);
                    period = isIt ? "M" : "W";
                } else {
                    period = "三";
                }
            } else if (i == InterfaceConstant.Period.Thursday) {
                if (ProviderManager.getAppProvider().getAppType() == APP_LECHANGE_OVERSEA) {
                    period = mContext.getResources().getString(R.string.device_manager_thursday);
                    period = isIt ? "G" : "T";
                } else {
                    period = "四";
                }
            } else if (i == InterfaceConstant.Period.Friday) {
                if (ProviderManager.getAppProvider().getAppType() == APP_LECHANGE_OVERSEA) {
                    period = mContext.getResources().getString(R.string.device_manager_friday);
                    period = isIt ? "V" : "F";
                } else {
                    period = "五";
                }
            } else if (i == InterfaceConstant.Period.Saturday) {
                if (ProviderManager.getAppProvider().getAppType() == APP_LECHANGE_OVERSEA) {
                    period = mContext.getResources().getString(R.string.device_manager_saturday);
                    period = "S";
                } else {
                    period = "六";
                }
            } else if (i == InterfaceConstant.Period.Sunday) {
                if (ProviderManager.getAppProvider().getAppType() == APP_LECHANGE_OVERSEA) {
                    period = mContext.getResources().getString(R.string.device_manager_sunday);
                    period = isIt ? "D" : "S";
                } else {
                    period = "日";
                }
            } else {
                if (ProviderManager.getAppProvider().getAppType() == APP_LECHANGE_OVERSEA) {
                    period = mContext.getResources().getString(R.string.device_manager_sunday);
                    period = "S";
                } else {
                    period = "日";
                }
            }
            holder.getPeriodBtn().setText(period);
            boolean isContain = false;
            for (InterfaceConstant.Period day : mSelectDays) {
                if (day.name().equalsIgnoreCase(i.name())) {
                    isContain = true;
                    break;
                }
            }
            if (isContain) {
//                holder.getPeriodBtn().setBackgroundResource(R.drawable.setting_period_bg_h);
                holder.itemView.setSelected(true);
                if (mCurSelectPosition == position) {
                    holder.getPeriodBtn().setTextColor(getResources().getColor(R.color.c43));
                    holder.getArrowView().setVisibility(GONE);
                } else {
                    holder.getPeriodBtn().setTextColor(getResources().getColor(R.color.c10));
                    holder.getArrowView().setVisibility(GONE);
                }
            } else {
//                holder.getPeriodBtn().setBackgroundResource(R.drawable.setting_period_bg_n);
                holder.itemView.setSelected(false);
                holder.getArrowView().setVisibility(GONE);
                if (mCurSelectPosition == position) {
                    holder.getPeriodBtn().setTextColor(getResources().getColor(R.color.c43));
                } else {
                    holder.getPeriodBtn().setTextColor(getResources().getColor(R.color.c2));
                }
            }

            if (mCurSelectPosition == position) {
                holder.getPeriodBtn().setBackgroundResource(R.drawable.setting_period_bg_h);
            } else {
                holder.getPeriodBtn().setBackgroundResource(R.drawable.setting_period_bg_n);
            }

            holder.itemView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = holder.getAdapterPosition();
                    if (mDays == null || position < 0 || position >= mDays.size()) {
                        return;
                    }
                    InterfaceConstant.Period period = mDays.get(position);
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onItemClickListener(period,position);
                    }

                    mCurSelectPosition = position;
                    mAdapter.notifyDataSetChanged();
                }
            });
        }

        @Override
        public int getItemCount() {
            return mDays.size();
        }
    }
}
