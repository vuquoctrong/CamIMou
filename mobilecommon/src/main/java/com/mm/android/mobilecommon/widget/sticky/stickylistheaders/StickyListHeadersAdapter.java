package com.mm.android.mobilecommon.widget.sticky.stickylistheaders;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;

public interface StickyListHeadersAdapter extends ListAdapter {
	/**
	 * Get a View that displays the header data at the specified position in the
	 * set. You can either create a View manually or inflate it from an XML layout
	 * file.
	 * @param position
	 * The position of the item within the adapter's data set of the item whose
	 * header view we want.
	 * @param convertView
	 * The old view to reuse, if possible. Note: You should check that this view is
	 * non-null and of an appropriate type before using. If it is not possible to
	 * convert this view to display the correct data, this method can create a new
	 * view.
	 * @param parent
	 * The parent that this view will eventually be attached to.
	 * @return
	 * A View corresponding to the data at the specified position.
	 *
	 *
	 * 获取在集合中显示指定位置的头数据的视图。您可以手动创建视图,或者从XML布局文件中扩展它。
	 * @param position 我们想要其头视图的项在适配器数据集中的项的位置。
	 * @param convertView 旧视图要重用，如果可能的话。注意:在使用视图之前，应该检查该视图是否为非空且具有适当的类型。
	 *                       如果无法将此视图转换为显示正确的数据，则此方法可以创建一个新视图。
	 * @param parent 该视图最终将附加到的父对象。
	 * @return 与指定位置的数据相对应的视图。
	 */
	View getHeaderView(int position, View convertView, ViewGroup parent);

	/**
	 * Get the header id associated with the specified position in the list.
	 * @param position
	 * The position of the item within the adapter's data set whose header id we
	 * want.
	 * @return The id of the header at the specified position.
	 *
	 * 获取与列表中指定位置关联的标头id。
	 * @param position 需要其标头id的项在适配器数据集中的位置。
	 * @return 位于指定位置的标头的id。
	 */
	long getHeaderId(int position);
}
