package com.mm.android.mobilecommon.widget.sticky.stickygridheaders;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;

/**
 * Base adapter interface for StickyGridHeadersGridView. The adapter expects two sets of data,
 * items, and headers. Implement this interface to provide an optimised method for generating the
 * header data set. Otherwise see {@link StickyGridHeadersSimpleAdapter} for a solution which will
 * auto-generate the set of headers.
 *
 * StickyGridHeadersGridView的基本适配器接口。
 * 适配器需要两组数据、项和标头。实现这个接口来提供生成头数据集的优化方法。
 * 否则，请参阅{@link StickyGridHeadersSimpleAdapter}以获得自动生成头集的解决方案。
 */
public interface StickyGridHeadersBaseAdapter extends ListAdapter {
    /**
     * Get the number of items with a given header.
     * @return The number of items for the specified header.
     *
     * 获取具有给定标头的项的数量。
     * @return 指定标头的项数。
     */
    public int getCountForHeader(int header);

    /**
     * Get the number of headers in the adapter's data set.
     * @return Number of headers.
     *
     * 获取适配器数据集中头的数量。
     * @return 头的数量。
     */
    public int getNumHeaders();

    /**
     * Get a View that displays the header data at the specified position in the set. You can either
     * create a View manually or inflate it from an XML layout file.
     * @param position
     *            The position of the header within the adapter's header data set.
     * @param convertView
     *            The old view to reuse, if possible. Note: You should check that this view is
     *            non-null and of an appropriate type before using. If it is not possible to convert
     *            this view to display the correct data, this method can create a new view.
     * @param parent
     *            The parent that this view will eventually be attached to.
     * @return A View corresponding to the data at the specified position.
     *
     *
     * 获取在集合中显示指定位置的头数据的视图。您可以手动创建视图,或者从XML布局文件中扩展它。
     * @param position 标头在适配器标头数据集中的位置。
     * @param convertView 旧视图要重用，如果可能的话。注意:在使用视图之前，
     *                    应该检查该视图是否为非空且具有适当的类型。如果无法将此视图转换为显示正确的数据，则此方法可以创建一个新视图。
     * @param parent 该视图最终将附加到的父对象。
     * @return 与指定位置的数据相对应的视图。
     */
    View getHeaderView(int position, View convertView, ViewGroup parent);
}
