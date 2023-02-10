package com.mm.android.deviceaddmodule.views;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

import com.lechange.pulltorefreshlistview.Mode;
import com.lechange.pulltorefreshlistview.Orientation;
import com.lechange.pulltorefreshlistview.OverscrollHelper;
import com.lechange.pulltorefreshlistview.PullToRefreshAdapterViewBase;
import com.lechange.pulltorefreshlistview.extras.ScrollChangedListener;
import com.lechange.pulltorefreshlistview.internal.EmptyViewMethodAccessor;
import com.mm.android.deviceaddmodule.R;
import com.mm.android.mobilecommon.widget.sticky.stickygridheaders.StickyGridHeadersGridView;

/**
 * A pull-down flush gridview with headers
 *
 * 带header的可下拉刷新的gridview
 **/
public class PullToRefreshStickyGridView extends PullToRefreshAdapterViewBase<StickyGridHeadersGridView> {
    private ScrollChangedListener mScrollChangedListener;

    public PullToRefreshStickyGridView(Context context) {
        super(context);
    }

    public PullToRefreshStickyGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PullToRefreshStickyGridView(Context context, Mode mode) {
        super(context, mode);
    }

    public PullToRefreshStickyGridView(Context context, Mode mode, AnimationStyle style) {
        super(context, mode, style);
    }

    public void setOnScrollChangedListener(ScrollChangedListener mScrollChangedListener) {
        this.mScrollChangedListener = mScrollChangedListener;
    }

    public final Orientation getPullToRefreshScrollDirection() {
        return Orientation.VERTICAL;
    }

    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (this.mScrollChangedListener != null) {
            this.mScrollChangedListener.onScrollChanged(l, t, oldl, oldt);
        }

    }

    protected final StickyGridHeadersGridView createRefreshableView(Context context, AttributeSet attrs) {
        Object gv;
        if (Build.VERSION.SDK_INT >= 9) {
            gv = new PullToRefreshStickyGridView.InternalGridViewSDK9(context, attrs);
        } else {
            gv = new PullToRefreshStickyGridView.InternalGridView(context, attrs);
        }

        ((StickyGridHeadersGridView)gv).setId(R.id.gridview);
        return (StickyGridHeadersGridView)gv;
    }

    @TargetApi(9)
    final class InternalGridViewSDK9 extends PullToRefreshStickyGridView.InternalGridView {
        public InternalGridViewSDK9(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        protected boolean overScrollBy(int deltaX, int deltaY, int scrollX, int scrollY, int scrollRangeX, int scrollRangeY, int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent) {
            boolean returnValue = super.overScrollBy(deltaX, deltaY, scrollX, scrollY, scrollRangeX, scrollRangeY, maxOverScrollX, maxOverScrollY, isTouchEvent);
            OverscrollHelper.overScrollBy(PullToRefreshStickyGridView.this, deltaX, scrollX, deltaY, scrollY, isTouchEvent);
            return returnValue;
        }
    }

    class InternalGridView extends StickyGridHeadersGridView implements EmptyViewMethodAccessor {
        public InternalGridView(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        public void setEmptyView(View emptyView) {
            PullToRefreshStickyGridView.this.setEmptyView(emptyView);
        }

        public void setEmptyViewInternal(View emptyView) {
            super.setEmptyView(emptyView);
        }
    }
}
