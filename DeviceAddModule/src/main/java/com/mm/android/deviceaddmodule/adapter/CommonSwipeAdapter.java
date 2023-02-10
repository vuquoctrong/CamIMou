package com.mm.android.deviceaddmodule.adapter;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.mm.android.mobilecommon.common.ViewHolder;
import com.mm.android.mobilecommon.widget.swipe.SwipeLayout;
import com.mm.android.mobilecommon.widget.swipe.implments.SwipeItemMangerImpl;
import com.mm.android.mobilecommon.widget.swipe.interfaces.SwipeAdapterInterface;
import com.mm.android.mobilecommon.widget.swipe.interfaces.SwipeItemMangerInterface;

import java.util.List;

/**
 * Generic SwipeListView adapter
 * @param <T>
 * @author 16552 December 2, 2015 at 9:38:44 am
 * @version V2.0
 * @modificationHistory Logical or functional material change record
 * @modify by user: {Modifier} December 2, 2015
 * @modify by reason:{method name}:{cause}
 *
 * 通用的SwipeListView适配器
 * @param <T>
 * @author 16552 2015年12月2日 上午9:38:44
 * @version V2.0
 * @modificationHistory=========================逻辑或功能性重大变更记录
 * @modify by user: {修改人} 2015年12月2日
 * @modify by reason:{方法名}:{原因}
 */
public abstract class CommonSwipeAdapter<T> extends BaseAdapter implements SwipeItemMangerInterface,
        SwipeAdapterInterface, SwipeItemMangerImpl.SwipeItemListener {
    private int mLayoutRes;

    private int mFirstPosition; // 从头部插入的位置

    private OnMenuItemClickListener mMenuItemCllickLinstener;

    protected List<T> mList;

    protected Context mContext;

    protected SwipeItemMangerImpl mItemManger = new SwipeItemMangerImpl(this);

    private int mOpenStatePosition = -1;

    private SwipeLayout mOpenLayout;

    public CommonSwipeAdapter(int layout, List<T> list, Context mContext) {
        this.mList = list;
        this.mLayoutRes = layout;
        this.mContext = mContext;
        notifyDataSetChanged();
    }

    public CommonSwipeAdapter(int layout, List<T> list, Context mContext,
                              OnMenuItemClickListener menuItemCllickLinstener) {
        this(layout, list, mContext);
        this.mMenuItemCllickLinstener = menuItemCllickLinstener;
    }

    /**
     * Left - swipe Menu - click event, now the control only supports a menu
     * @author 16552 December 2, 2015 at 10:51:19 AM
     * @version V2.0
     * @modificationHistory Logical or functional material change record
     * @modify by user: {Modifier} December 2, 2015
     * @modify by reason:{method name}:{cause}
     *
     * 左滑出现的菜单点击事件，现在的控件只支持一个Menu
     * @author 16552 2015年12月2日 上午10:51:19
     * @version V2.0
     * @modificationHistory=========================逻辑或功能性重大变更记录
     * @modify by user: {修改人} 2015年12月2日
     * @modify by reason:{方法名}:{原因}
     */
    public interface OnMenuItemClickListener {
        /**
         * Menu Click event
         * @param menuResId The menu of ResId
         * @param menuId    menu subscript, temporarily return 0, support multiple menu after extension
         * @param position  A subscript in the ListView
         * @author 16552 December 2, 2015 at 10:52:53 AM
         *
         * Menu点击事件
         * @param menuResId menu的ResId
         * @param menuId    menu下标，暂时都返回0，后面支持多个menu后扩展用
         * @param position  ListView中的下标
         * @author 16552 2015年12月2日 上午10:52:53
         */
        public void onMenuItemClick(int menuResId, int menuId, int position);
    }

    /**
     * Gets whether item can be left swiped
     * @param position
     * @return boolean
     * @author 16552 December 1, 2015 at 4:51:10 PM
     *
     * 获取item是否可以左滑
     * @param position  位置
     * @return boolean 布尔值
     * @author 16552 2015年12月1日 下午4:51:10
     */
    public abstract boolean getSwipeLayoutEnable(int position);

    public abstract void convert(ViewHolder viewHolder, T item, int position, ViewGroup parent);

    @Override
    public int getCount() {

        return mList == null ? 0 : mList.size();
    }

    @Override
    public T getItem(int position) {

        return mList == null ? null : mList.get(position);
    }

    @Override
    public long getItemId(int position) {

        return position;
    }

    @Override
    public final View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            viewHolder = ViewHolder.getViewHolder(mLayoutRes, convertView, mContext, parent);
            mItemManger.initialize(viewHolder.getView(), position);
        } else {
            viewHolder = ViewHolder.getViewHolder(mLayoutRes, convertView, mContext, parent);
            mItemManger.updateConvertView(viewHolder.getView(), position);
        }

        // 控制单个Item是否可以左滑
        final SwipeLayout swipeLayout = viewHolder.getView().findViewById(
                getSwipeLayoutResourceId(position));
        if (swipeLayout != null) {
            boolean swipeEnable = getSwipeLayoutEnable(position);
            swipeLayout.setSwipeEnabled(getSwipeLayoutEnable(position));
            if (!swipeEnable && mItemManger.isOpen(position)) {
                // 如果使能不可用，但是menu却是open的状态，重置掉
                mItemManger.closeItem(position);
            }
            swipeLayout.getChildAt(0).setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (mMenuItemCllickLinstener != null) {
                        mMenuItemCllickLinstener.onMenuItemClick(v.getId(), 0, position);
                    }
                    swipeLayout.close();
                    closeItem(position);
                    mOpenStatePosition = -1;
                }
            });
        }

        convert(viewHolder, getItem(position), position, parent);
        return viewHolder.getView();
    }

    @Override
    public void openItem(int position) {
        mItemManger.openItem(position);
    }

    @Override
    public void closeItem(int position) {
        mItemManger.closeItem(position);
    }

    @Override
    public void colseItemNoDataChange(int position) {
        mItemManger.colseItemNoDataChange(position);
    }

    @Override
    public void closeAllExcept(SwipeLayout layout) {
        mItemManger.closeAllExcept(layout);
    }

    @Override
    public List<Integer> getOpenItems() {
        return mItemManger.getOpenItems();
    }

    @Override
    public List<SwipeLayout> getOpenLayouts() {
        return mItemManger.getOpenLayouts();
    }

    @Override
    public void removeShownLayouts(SwipeLayout layout) {
        mItemManger.removeShownLayouts(layout);
    }

    @Override
    public boolean isOpen(int position) {
        return mItemManger.isOpen(position);
    }

    @Override
    public SwipeItemMangerImpl.Mode getMode() {
        return mItemManger.getMode();
    }

    @Override
    public void setMode(SwipeItemMangerImpl.Mode mode) {
        mItemManger.setMode(mode);
    }

    public void addFirstData(List<T> list) {
        this.mList.addAll(mFirstPosition, list);
        mFirstPosition = mFirstPosition + list.size();
    }

    public void addData(List<T> list) {
        this.mList.addAll(list);
    }

    public void removeData(List<T> list) {
        this.mList.removeAll(list);
    }

    public void clearData() {
        this.mList.clear();
        mFirstPosition = 0;
    }

    public List<T> getData() {
        return mList;
    }

    public void replaceData(List<T> list2) {
        if (list2 != mList) {
            clearData();
            addData(list2);
        }
    }

    public void setOnMenuItemClickListener(OnMenuItemClickListener linstener) {
        this.mMenuItemCllickLinstener = linstener;
    }

    public void removeItem(int index) {
        this.mList.remove(index);
    }

    @Override
    public void swipeCloseListner(int position, SwipeLayout layout) {
        if (mOpenStatePosition == position) {
            mOpenStatePosition = -1;
        }
    }

    @Override
    public void swipeOpenListener(int position, SwipeLayout layout) {
        mOpenStatePosition = position;
        mOpenLayout = layout;
    }

    private boolean isOpenState() {
        return mOpenStatePosition != -1;
    }

    /**
     * Determine whether the current ListView can trigger click events and reset all SwipeLayout. Do not respond to onItemClick when an item is in the Open state
     * @return Whether a swipeLayout needs to be reset
     * @author 16552 April 21, 2016 at 6:34:04 PM
     *
     * 判断当前ListView是否可以触发点击事件，并重置所有SwipeLayout。当有item处于Open状态下，不响应onItemClick
     * @return 是否有需要重置的swipeLayout
     * @author 16552 2016年4月21日 下午6:34:04
     */
    public boolean resetSwipes() {
        //再判断是否有已经打开的item
        boolean hasOpened = isOpenState();
        if (hasOpened) {
            mOpenLayout.close();
            mItemManger.colseItemNoDataChange(mOpenStatePosition);
            mOpenStatePosition = -1;
        }

        return hasOpened;
    }
}
