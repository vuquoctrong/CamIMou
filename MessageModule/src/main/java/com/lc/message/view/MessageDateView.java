package com.lc.message.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.lc.message.R;
import com.mm.android.mobilecommon.utils.TimeUtils;
import com.mm.android.mobilecommon.utils.UIUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by 29217 on 2017/4/14.
 * Date selection controls in messages
 *
 * Created by 29217 on 2017/4/14.
 * 消息中的日期选择控件
 */
public class MessageDateView extends RelativeLayout {

    private int NUMBER_PAGER = 7;     //一屏展示的数量
    private static final int ITEM_MARGIN = 5;      //单位dp

    public interface OnDayChangeListener {
        void onDayChangeClick(int position, @NonNull Date toDate);
    }

    private TextView mMonthView;
    private OnDayChangeListener mListener;
    private int mSelectPosition = 0;
    private List<Date> mDates;
    private RecyclerView mDayListView;
    private int mItemMargin;
    private Calendar mCalendar;
    private Context mContext;
    private DayAdapter mAdapter;

    public MessageDateView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MessageDateView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        initData(attrs, defStyleAttr);
        initView();
    }

    public MessageDateView(Context context) {
        this(context, null);
    }

    /**
     * Initialization data
     *
     * 初始化数据
     */
    private void initData(AttributeSet attrs, int defStyleAttr) {
        mCalendar = Calendar.getInstance();
        mItemMargin = UIUtils.dip2px(mContext, ITEM_MARGIN);
        TypedArray typedArray = mContext.obtainStyledAttributes(attrs,
                R.styleable.MessageDateView, defStyleAttr, 0);

        int dayCount = typedArray.getInteger(R.styleable.MessageDateView_day_count,NUMBER_PAGER);
        typedArray.recycle();
        mDates = new ArrayList<>();
        for (int i = dayCount - 1; i > -1; i--) {
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DATE, -i);
            mDates.add(calendar.getTime());
        }
        mSelectPosition = mDates.size() - 1;
        //海外V3.9 需求 往后添加一天 解决手机和设备时区不一致问题
//        if(ProviderManager.getAppProvider().getAppType() == LCConfiguration.APP_LECHANGE_OVERSEA){
//            Calendar calendar = Calendar.getInstance();
//            calendar.add(Calendar.DATE, 1);
//            mDates.add(calendar.getTime());
//        }
    }

    /**
     * Initialize the control component
     *
     * 初始化控件
     */
    private void initView() {
        View view = LayoutInflater.from(mContext).
                inflate(R.layout.message_module_date_view_layout, this, true);
        mMonthView = view.findViewById(R.id.date_view_month);
        mDayListView = view.findViewById(R.id.date_view_day_list);
        LinearLayoutManager manager = new LinearLayoutManager(mContext);
        manager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mDayListView.setLayoutManager(manager);
        mAdapter = new DayAdapter();
        mDayListView.setAdapter(mAdapter);
        updateDateView();
    }

    /**
     * Get the time list
     * @return List  Returns time collection data
     *
     * 获取时间列表
     * @return List  返回时间集合数据
     */
    public List<Date> getDateList() {
        return this.mDates;
    }

    /**
     * Select the last item
     * @return Int  the ID of the last item
     *
     * 选择最后一项
     * @return Int  返回最后一项的ID
     */
    public int selectLastPosition() {
//        if (mDayListView != null && mDates != null && !mDates.isEmpty()) {
//            this.mSelectPosition = /*ProviderManager.getAppProvider().getAppType() == LCConfiguration.APP_LECHANGE_OVERSEA ? mDates.size() - 2 :*/ mDates.size() - 1;
//            mDayListView.scrollToPosition(/*ProviderManager.getAppProvider().getAppType() == LCConfiguration.APP_LECHANGE_OVERSEA ? 0 : */mSelectPosition);
//            updateDateView();
//        }
        return mSelectPosition;
    }

    public void setDayChangeListener(OnDayChangeListener listener) {
        this.mListener = listener;
    }

    class DayHolder extends RecyclerView.ViewHolder {

        private TextView day;
        private TextView week;
        private LinearLayout dateLayout;

        DayHolder(View itemView) {
            super(itemView);
        }

        private TextView getDay() {
            if (day == null)
                day = itemView.findViewById(R.id.date_view_item_day);
            return day;
        }

        private TextView getWeek() {
            if (week == null)
                week = itemView.findViewById(R.id.date_view_item_week);
            return week;
        }

        private LinearLayout getDateLayout() {
            if (dateLayout == null)
                dateLayout = itemView.findViewById(R.id.date_view_item_layout);
            return dateLayout;
        }
    }

    private class DayAdapter extends RecyclerView.Adapter<DayHolder> {

        @Override
        public DayHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(mContext).
                    inflate(R.layout.message_module_date_view_item, parent, false);
            DayHolder holder = new DayHolder(view);
            int windowWidth = mDayListView.getWidth();
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams((windowWidth -
                    (mDates.size() * 2 * mItemMargin)) / getItemCount(),
                    LayoutParams.MATCH_PARENT);
            params.setMargins(mItemMargin, 0, mItemMargin, 0);
            holder.getDateLayout().setLayoutParams(params);
            holder.getDateLayout().setBackgroundResource(
                    R.drawable.message_module_date_item_bg_selector);
            return holder;
        }

        @Override
        public void onBindViewHolder(final DayHolder holder, int position) {

            holder.getDay().setText(TimeUtils.getDateFormatWithUS("dd").format(mDates.get(position)));

            mCalendar.setTime(mDates.get(position));
            String week;
            switch (mCalendar.get(Calendar.DAY_OF_WEEK)) {
                case Calendar.TUESDAY:
                    week = mContext.getResources().getString(R.string.device_manager_tue);
                    break;
                case Calendar.WEDNESDAY:
                    week = mContext.getResources().getString(R.string.device_manager_wed);
                    break;
                case Calendar.THURSDAY:
                    week = mContext.getResources().getString(R.string.device_manager_thu);
                    break;
                case Calendar.FRIDAY:
                    week = mContext.getResources().getString(R.string.device_manager_fri);
                    break;
                case Calendar.SATURDAY:
                    week = mContext.getResources().getString(R.string.device_manager_sat);
                    break;
                case Calendar.SUNDAY:
                    week = mContext.getResources().getString(R.string.device_manager_sun);
                    break;
                default:
                    week = mContext.getResources().getString(R.string.device_manager_mon);
                    break;
            }
            Log.d("11223344", "week:" + week);
            holder.getWeek().setText(week);
            if (position == mSelectPosition) {
                holder.getDay().setSelected(true);
                holder.getWeek().setSelected(true);
                holder.itemView.setSelected(true);
            } else {
                holder.getDay().setSelected(false);
                holder.getWeek().setSelected(false);
                holder.itemView.setSelected(false);
            }

            holder.itemView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (UIUtils.isFastDoubleClick())
                        return;

                    if (mSelectPosition == holder.getAdapterPosition())
                        return;

                    mSelectPosition = holder.getAdapterPosition();
                    if (mSelectPosition >= 2 && mSelectPosition <= mDates.size() - 1) {
//                        mDayListView.smoothScrollToPosition(mSelectPosition - 1);
                        //解决点击左边可见项滑动到最整个列表左边的问题 DTS000354039
                        mDayListView.scrollToPosition(mSelectPosition - 2);
                        LinearLayoutManager layoutManager = (LinearLayoutManager) mDayListView.getLayoutManager();
                        layoutManager.scrollToPositionWithOffset(mSelectPosition - 2, 0);
                    }

                    updateDateView();
                }
            });
        }

        @Override
        public int getItemCount() {
            return mDates.size();
        }
    }

    /**
     * Update time control
     *
     * 更新时间控件
     */
    private void updateDateView(){
        if (mMonthView == null || mAdapter == null)
            return;

        if (mSelectPosition < 0 || mSelectPosition >= mDates.size())
            return;

        mMonthView.setText(TimeUtils.getDateFormatWithUS("yyyy-MM")
                .format(mDates.get(mSelectPosition)));
        if (mListener != null) {
            mListener.onDayChangeClick(mSelectPosition,mDates.get(mSelectPosition));
        }

        mAdapter.notifyDataSetChanged();
    }

    /**
     * Set a screen to display for several days
     *
     * 设置一屏显示几天
     */
    public void setNumberPager(int num) {
        NUMBER_PAGER = num;
    }
}
