package com.mm.android.mobilecommon.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.lechange.pulltorefreshlistview.Mode;
import com.lechange.pulltorefreshlistview.Orientation;
import com.lechange.pulltorefreshlistview.PullToRefreshBase;

public class LcPullToRefreshRecyclerView extends PullToRefreshBase<RecyclerView> {
    public LcPullToRefreshRecyclerView(Context context) {
        super(context);
    }

    public LcPullToRefreshRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LcPullToRefreshRecyclerView(Context context, Mode mode) {
        super(context, mode);
    }

    @Override
    public Orientation getPullToRefreshScrollDirection() {
        return Orientation.VERTICAL;
    }

    @Override
    protected RecyclerView createRefreshableView(Context context, AttributeSet attrs) {
        RecyclerView recyclerView = new RecyclerView(context);
        return recyclerView;
    }


    @Override
    protected boolean isReadyForPullStart() {
        return isFirstItemVisible();
    }

    public boolean isFirstItemVisible() {
        final RecyclerView.Adapter<?> adapter = getRefreshableView().getAdapter();

        //如果未设置Adapter或者Adpter没有数据可以下拉刷新
        if(null == adapter || adapter.getItemCount() == 0){
            return true;
        }else{
            if(getFristVisiblePosition() == 0){
                return mRefreshableView.getChildAt(0).getTop() >= mRefreshableView.getTop();
            }
        }

        return false;
    }

    public int getFristVisiblePosition() {
        View firstVisibleChild = mRefreshableView.getChildAt(0);
        return firstVisibleChild != null ? mRefreshableView.getChildPosition(firstVisibleChild) : -1;
    }

    @Override
    protected boolean isReadyForPullEnd() {
        return isLastItemVisible();
    }

    public boolean isLastItemVisible() {
        final RecyclerView.Adapter<?> adapter = getRefreshableView().getAdapter();

        //如果未设置Adapter或者Adpter没有数据可以下拉刷新
        if(null == adapter || adapter.getItemCount() == 0){
            return true;
        }else{
            if(getLastVisiblePosition() == adapter.getItemCount() - 1){
                return mRefreshableView.getChildAt(mRefreshableView.getChildCount() - 1).getBottom() <= mRefreshableView.getBottom();
            }
        }

        return false;
    }

    public int getLastVisiblePosition() {
        View lastVisibleChild = mRefreshableView.getChildAt(mRefreshableView.getChildCount() - 1);
        return lastVisibleChild != null ? mRefreshableView.getChildPosition(lastVisibleChild) : -1;
    }

}
