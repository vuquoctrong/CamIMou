package com.mm.android.mobilecommon.widget.sticky.stickylistheaders;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Checkable;
import android.widget.ListAdapter;

import java.util.ArrayList;
import java.util.List;

final class StickyListHeadersAdapterWrapper extends BaseAdapter implements StickyListHeadersAdapter {


	public interface OnHeaderClickListener{
		public void onHeaderClick(View header, int itemPosition, long headerId);
	}

	private final List<View> headerCache = new ArrayList<>();
	private final Context context;
	private final StickyListHeadersAdapter delegate;
	private Drawable divider;
	private int dividerHeight;
	private DataSetObserver dataSetObserver = new DataSetObserver() {

		@Override
		public void onInvalidated() {
			headerCache.clear();
		}
	};
	private OnHeaderClickListener onHeaderClickListener;

	StickyListHeadersAdapterWrapper(Context context,
			StickyListHeadersAdapter delegate) {
		this.context = context;
		this.delegate = delegate;
		delegate.registerDataSetObserver(dataSetObserver);
	}

	void setDivider(Drawable divider) {
		this.divider = divider;
	}

	void setDividerHeight(int dividerHeight) {
		this.dividerHeight = dividerHeight;
	}

	@Override
	public boolean areAllItemsEnabled() {
		return delegate.areAllItemsEnabled();
	}

	@Override
	public boolean isEnabled(int position) {
		return delegate.isEnabled(position);
	}

	@Override
	public void registerDataSetObserver(DataSetObserver observer) {
		delegate.registerDataSetObserver(observer);
	}

	@Override
	public void unregisterDataSetObserver(DataSetObserver observer) {
		delegate.unregisterDataSetObserver(observer);
	}

	@Override
	public int getCount() {
		return delegate.getCount();
	}

	@Override
	public Object getItem(int position) {
		return delegate.getItem(position);
	}

	@Override
	public long getItemId(int position) {
		return delegate.getItemId(position);
	}

	@Override
	public boolean hasStableIds() {
		return delegate.hasStableIds();
	}

	@Override
	public int getItemViewType(int position) {
		return delegate.getItemViewType(position);
	}

	@Override
	public int getViewTypeCount() {
		return delegate.getViewTypeCount();
	}

	@Override
	public boolean isEmpty() {
		return delegate.isEmpty();
	}

	/**
	 * Will recycle header from {@link WrapperView} if it exists
	 *
	 * 如果{@link WrapperView}存在，将回收头文件吗
	 */
	private void recycleHeaderIfExists(WrapperView wv) {
		View header = wv.header;
		if (header != null) {
			headerCache.add(header);
		}
	}

	/**
	 * Get a header view. This optionally pulls a header from the supplied
	 * {@link WrapperView} and will also recycle the divider if it exists.
	 *
	 * 获取头部视图。这将可选地从提供的{@link WrapperView}中提取一个头文件，如果分隔符存在，也将回收它。
	 */
	private View configureHeader(WrapperView wv, final int position) {
		View header = wv.header;
		header = delegate.getHeaderView(position, header, wv);
		if (header == null) {
			throw new NullPointerException("Header view must not be null.");
		}
		//if the header isn't clickable, the listselector will be drawn on top of the header
		header.setClickable(true);
		header.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if(onHeaderClickListener != null){
					long headerId = delegate.getHeaderId(position);
					onHeaderClickListener.onHeaderClick(v, position, headerId);
				}
			}
		});
		return header;
	}

	/**
	 * Returns {@code true} if the previous position has the same header ID.
	 *
	 * 如果前一个位置具有相同的头ID，则返回{@code true}。
	 */
	private boolean previousPositionHasSameHeader(int position) {
		return position != 0
				&& delegate.getHeaderId(position) == delegate
						.getHeaderId(position - 1);
	}

	@Override
	public WrapperView getView(int position, View convertView, ViewGroup parent) {
		WrapperView wv = (convertView == null) ? new WrapperView(context) : (WrapperView) convertView;
		View item = delegate.getView(position, wv.item, wv);
		View header = null;
		if (previousPositionHasSameHeader(position)) {
			recycleHeaderIfExists(wv);
		} else {
			header = configureHeader(wv, position);
		}
		if((item instanceof Checkable) && !(wv instanceof CheckableWrapperView)) {
			// Need to create Checkable subclass of WrapperView for ListView to work correctly
			wv = new CheckableWrapperView(context);
		} else if(!(item instanceof Checkable) && (wv instanceof CheckableWrapperView)) {
			wv = new WrapperView(context);
		}
		wv.update(item, header, divider, dividerHeight);
		return wv;
	}

	public void setOnHeaderClickListener(OnHeaderClickListener onHeaderClickListener){
		this.onHeaderClickListener = onHeaderClickListener;
	}

	@Override
	public boolean equals(Object o) {
		if(delegate == null){
			return o == null;
		}
		return delegate.equals(o); 
	}

	@Override
	public View getDropDownView(int position, View convertView, ViewGroup parent) {
		return ((BaseAdapter) delegate).getDropDownView(position, convertView, parent);
	}

	@Override
	public int hashCode() {
		return delegate.hashCode();
	}

	@Override
	public void notifyDataSetChanged() {
		super.notifyDataSetChanged();
		((BaseAdapter) delegate).notifyDataSetChanged();
	}

	@Override
	public void notifyDataSetInvalidated() {
		((BaseAdapter) delegate).notifyDataSetInvalidated();
	}

	@Override
	public String toString() {
		return delegate.toString();
	}

	@Override
	public View getHeaderView(int position, View convertView, ViewGroup parent) {
		return delegate.getHeaderView(position, convertView, parent);
	}

	@Override
	public long getHeaderId(int position) {
		return delegate.getHeaderId(position);
	}

}
