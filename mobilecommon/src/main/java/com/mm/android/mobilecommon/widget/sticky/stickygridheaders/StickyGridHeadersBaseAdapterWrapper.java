package com.mm.android.mobilecommon.widget.sticky.stickygridheaders;

import android.content.Context;
import android.database.DataSetObserver;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;

/**
 * Adapter wrapper to insert extra views and otherwise hack around GridView to add sections and
 * headers.
 *
 * 适配器包装器插入额外的视图，或者绕过GridView添加部分和标题。
 */
public class StickyGridHeadersBaseAdapterWrapper extends BaseAdapter {
    private static final int sNumViewTypes = 3;

    protected static final int ID_FILLER = -0x02;

    protected static final int ID_HEADER = -0x01;

    protected static final int ID_HEADER_FILLER = -0x03;

    protected static final int POSITION_FILLER = -0x01;

    protected static final int POSITION_HEADER = -0x02;

    protected static final int POSITION_HEADER_FILLER = -0x03;

    protected static final int VIEW_TYPE_FILLER = 0x00;

    protected static final int VIEW_TYPE_HEADER = 0x01;

    protected static final int VIEW_TYPE_HEADER_FILLER = 0x02;

    private final Context mContext;

    private int mCount;

    private boolean mCounted = false;

    private DataSetObserver mDataSetObserver = new DataSetObserver() {
        @Override
        public void onChanged() {
            updateCount();
        }

        @Override
        public void onInvalidated() {
            mCounted = false;
        }
    };

    private final StickyGridHeadersBaseAdapter mDelegate;

    private StickyGridHeadersGridView mGridView;

    private View mLastHeaderViewSeen;

    private View mLastViewSeen;

    private int mNumColumns = 1;

    public StickyGridHeadersBaseAdapterWrapper(Context context, StickyGridHeadersGridView gridView,
        StickyGridHeadersBaseAdapter delegate) {
        mContext = context;
        mDelegate = delegate;
        mGridView = gridView;
        delegate.registerDataSetObserver(mDataSetObserver);
    }

    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public int getCount() {
        if (mCounted) {
            return mCount;
        }
        mCount = 0;
        int numHeaders = mDelegate.getNumHeaders();
        if (numHeaders == 0) {
            mCount = mDelegate.getCount();
            mCounted = true;
            return mCount;
        }

        for (int i = 0; i < numHeaders; i++) {
            // Pad count with space for header and trailing filler in header
            // group.
            mCount += mDelegate.getCountForHeader(i) + unFilledSpacesInHeaderGroup(i) + mNumColumns;
        }
        mCounted = true;
        return mCount;
    }

    /**
     * Get the data item associated with the specified position in the data set.
     *
     * <p>
     * Since this wrapper inserts fake entries to fill out items grouped by header and also spaces
     * to insert headers into some positions will return null.
     * </p>
     * 
     * @param position
     *            Position of the item whose data we want within the adapter's data set.
     * @return The data at the specified position.
     *
     *
     * 获取与数据集中的指定位置相关联的数据项。
     *
     * <p> 因为这个包装器插入假条目来填充按标题和空格分组的项,在某些位置插入头文件将返回null。</p>
     *
     * @param position 需要其数据的项在适配器数据集中的位置。
     * @return 在指定位置的数据。
     */
    @Override
    public Object getItem(int position) throws ArrayIndexOutOfBoundsException {
        Position adapterPosition = translatePosition(position);
        if (adapterPosition.mPosition == POSITION_FILLER || adapterPosition.mPosition == POSITION_HEADER) {
            // Fake entry in view.
            return null;
        }

        return mDelegate.getItem(adapterPosition.mPosition);
    }

    @Override
    public long getItemId(int position) {
        Position adapterPosition = translatePosition(position);
        if (adapterPosition.mPosition == POSITION_HEADER) {
            return ID_HEADER;
        }
        if (adapterPosition.mPosition == POSITION_FILLER) {
            return ID_FILLER;
        }
        if (adapterPosition.mPosition == POSITION_HEADER_FILLER) {
            return ID_HEADER_FILLER;
        }
        return mDelegate.getItemId(adapterPosition.mPosition);
    }

    @Override
    public int getItemViewType(int position) {
        Position adapterPosition = translatePosition(position);
        if (adapterPosition.mPosition == POSITION_HEADER) {
            return VIEW_TYPE_HEADER;
        }
        if (adapterPosition.mPosition == POSITION_FILLER) {
            return VIEW_TYPE_FILLER;
        }
        if (adapterPosition.mPosition == POSITION_HEADER_FILLER) {
            return VIEW_TYPE_HEADER_FILLER;
        }
        int itemViewType = mDelegate.getItemViewType(adapterPosition.mPosition);
        if (itemViewType == IGNORE_ITEM_VIEW_TYPE) {
            return itemViewType;
        }
        return itemViewType + sNumViewTypes;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Position adapterPosition = translatePosition(position);
        if (adapterPosition.mPosition == POSITION_HEADER) {
            HeaderFillerView v = getHeaderFillerView(adapterPosition.mHeader, convertView, parent);
            View view = mDelegate.getHeaderView(adapterPosition.mHeader, (View) v.getTag(), parent);
            mGridView.detachHeader((View) v.getTag());
            v.setTag(view);
            mGridView.attachHeader(view);
            convertView = v;
            mLastHeaderViewSeen = v;
            v.forceLayout();
        } else if (adapterPosition.mPosition == POSITION_HEADER_FILLER) {
            convertView = getFillerView(convertView, parent, mLastHeaderViewSeen);
            convertView.forceLayout();
        } else if (adapterPosition.mPosition == POSITION_FILLER) {
            convertView = getFillerView(convertView, parent, mLastViewSeen);
        } else {
            convertView = mDelegate.getView(adapterPosition.mPosition, convertView, parent);
            mLastViewSeen = convertView;
        }

        return convertView;
    }

    @Override
    public int getViewTypeCount() {
        return mDelegate.getViewTypeCount() + sNumViewTypes;
    }

    /**
     * @return the adapter wrapped by this adapter.
     *
     * @return 由该适配器包装的适配器。
     */
    public StickyGridHeadersBaseAdapter getWrappedAdapter() {
        return mDelegate;
    }

    @Override
    public boolean hasStableIds() {
        return mDelegate.hasStableIds();
    }

    @Override
    public boolean isEmpty() {
        return mDelegate.isEmpty();
    }

    @Override
    public boolean isEnabled(int position) {
        Position adapterPosition = translatePosition(position);
        if (adapterPosition.mPosition == POSITION_FILLER || adapterPosition.mPosition == POSITION_HEADER) {
            return false;
        }

        return mDelegate.isEnabled(adapterPosition.mPosition);
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {
        super.registerDataSetObserver(observer);
        mDelegate.registerDataSetObserver(observer);
    }

    public void setNumColumns(int numColumns) {
        mNumColumns = numColumns;
        mCounted = false;
        // notifyDataSetChanged();
    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {
        super.unregisterDataSetObserver(observer);
        mDelegate.unregisterDataSetObserver(observer);
    }

    private FillerView getFillerView(View convertView, ViewGroup parent, View lastViewSeen) {
        FillerView fillerView = (FillerView) convertView;
        if (fillerView == null) {
            fillerView = new FillerView(mContext);
        }

        fillerView.setMeasureTarget(lastViewSeen);

        return fillerView;
    }

    private HeaderFillerView getHeaderFillerView(int headerPosition, View convertView, ViewGroup parent) {
        HeaderFillerView headerFillerView = (HeaderFillerView) convertView;
        if (headerFillerView == null) {
            headerFillerView = new HeaderFillerView(mContext);
        }

        return headerFillerView;
    }

    /**
     * Counts the number of items that would be need to fill out the last row in the group of items
     * with the given header.
     * @param header
     *            Header set of items are grouped by.
     * @return The count of unfilled spaces in the last row.
     *
     * 计算填充具有给定标头的项组的最后一行所需的项数。
     * @param header 项目的标题集按。
     * @return 最后一行未填空格的计数。
     */
    private int unFilledSpacesInHeaderGroup(int header) {
        // If mNumColumns is equal to zero we will have a divide by 0 exception
        if (mNumColumns == 0) {
            return 0;
        }

        int remainder = mDelegate.getCountForHeader(header) % mNumColumns;
        return remainder == 0 ? 0 : mNumColumns - remainder;
    }

    protected long getHeaderId(int position) {
        return translatePosition(position).mHeader;
    }

    protected View getHeaderView(int position, View convertView, ViewGroup parent) {
        if (mDelegate.getNumHeaders() == 0) {
            return null;
        }

        return mDelegate.getHeaderView(translatePosition(position).mHeader, convertView, parent);
    }

    protected Position translatePosition(int position) {
        int numHeaders = mDelegate.getNumHeaders();
        if (numHeaders == 0) {
            if (position >= mDelegate.getCount()) {
                return new Position(POSITION_FILLER, 0);
            }
            return new Position(position, 0);
        }

        // Translate GridView position to Adapter position.
        int adapterPosition = position;
        int place = position;
        int i;

        for (i = 0; i < numHeaders; i++) {
            int sectionCount = mDelegate.getCountForHeader(i);

            // Skip past fake items making space for header in front of
            // sections.
            if (place == 0) {
                // Position is first column where header will be.
                return new Position(POSITION_HEADER, i);
            }
            place -= mNumColumns;
            if (place < 0) {
                // Position is a fake so return null.
                return new Position(POSITION_HEADER_FILLER, i);
            }
            adapterPosition -= mNumColumns;

            if (place < sectionCount) {
                return new Position(adapterPosition, i);
            }

            // Skip past section end of section row filler;
            int filler = unFilledSpacesInHeaderGroup(i);
            adapterPosition -= filler;
            place -= sectionCount + filler;

            if (place < 0) {
                // Position is a fake so return null.
                return new Position(POSITION_FILLER, i);
            }
        }

        // Position is a fake.
        return new Position(POSITION_FILLER, i);
    }

    protected void updateCount() {
        mCount = 0;
        int numHeaders = mDelegate.getNumHeaders();
        if (numHeaders == 0) {
            mCount = mDelegate.getCount();
            mCounted = true;
            return;
        }

        for (int i = 0; i < numHeaders; i++) {
            mCount += mDelegate.getCountForHeader(i) + mNumColumns;
        }
        mCounted = true;
    }

    /**
     * Simple view to fill space in grid view.
     *
     * 在网格视图中填充空间的简单视图。
     */
    protected static class FillerView extends View {
        private View mMeasureTarget;

        public FillerView(Context context) {
            super(context);
        }

        public FillerView(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        public FillerView(Context context, AttributeSet attrs, int defStyle) {
            super(context, attrs, defStyle);
        }

        public void setMeasureTarget(View lastViewSeen) {
            mMeasureTarget = lastViewSeen;
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(mMeasureTarget.getMeasuredHeight(), MeasureSpec.EXACTLY);
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    /**
     * A view to hold the section header and measure the header row height correctly.
     *
     * 保存section标题并正确测量标题行高度的视图。
     */
    protected class HeaderFillerView extends FrameLayout {
        private int mHeaderId;

        public HeaderFillerView(Context context) {
            super(context);
        }

        public HeaderFillerView(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        public HeaderFillerView(Context context, AttributeSet attrs, int defStyle) {
            super(context, attrs, defStyle);
        }

        public int getHeaderId() {
            return mHeaderId;
        }

        /**
         * Set the adapter id for this header so we can easily pull it later.
         *
         * 为这个头文件设置适配器id，以便稍后可以轻松地提取它。
         */
        public void setHeaderId(int headerId) {
            mHeaderId = headerId;
        }

        @Override
        protected LayoutParams generateDefaultLayoutParams() {
            return new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            View v = (View) getTag();
            ViewGroup.LayoutParams params = v.getLayoutParams();
            if (params == null) {
                params = generateDefaultLayoutParams();
                v.setLayoutParams(params);
            }
            if (v.getVisibility() != View.GONE) {
                int heightSpec = getChildMeasureSpec(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED), 0,
                        params.height);
                int widthSpec = getChildMeasureSpec(
                        MeasureSpec.makeMeasureSpec(mGridView.getWidth(), MeasureSpec.EXACTLY), 0, params.width);
                v.measure(widthSpec, heightSpec);
            }
            setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), v.getMeasuredHeight());
        }
    }

//    protected static class HeaderHolder {
//        protected View mHeaderView;
//    }

    protected static class Position {
        protected int mHeader;

        protected int mPosition;

        protected Position(int position, int header) {
            mPosition = position;
            mHeader = header;
        }
    }
}
