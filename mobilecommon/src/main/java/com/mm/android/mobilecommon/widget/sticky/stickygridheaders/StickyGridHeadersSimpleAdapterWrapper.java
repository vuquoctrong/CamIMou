package com.mm.android.mobilecommon.widget.sticky.stickygridheaders;

import android.database.DataSetObserver;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Adapter wrapper to insert extra views and otherwise hack around GridView to add sections and
 * headers.
 *
 * 适配器包装器插入额外的视图，或者绕过GridView添加部分和标题。
 */
public class StickyGridHeadersSimpleAdapterWrapper extends BaseAdapter implements StickyGridHeadersBaseAdapter {
    private StickyGridHeadersSimpleAdapter mDelegate;

    private HeaderData[] mHeaders;

    public StickyGridHeadersSimpleAdapterWrapper(StickyGridHeadersSimpleAdapter adapter) {
        mDelegate = adapter;
        adapter.registerDataSetObserver(new DataSetObserverExtension());
        mHeaders = generateHeaderList(adapter);
    }

    @Override
    public int getCount() {
        return mDelegate.getCount();
    }

    @Override
    public int getCountForHeader(int position) {
        int headerPosition = 0;
        if(position <0 || position>=mHeaders.length){
            headerPosition = 0;
        }else{
            headerPosition = position;
        }

        return mHeaders[headerPosition].getCount();
    }

    @Override
    public View getHeaderView(int position, View convertView, ViewGroup parent) {
        int headerPosition = 0;
        if(position <0 || position>=mHeaders.length){
            headerPosition = 0;
        }else{
            headerPosition = mHeaders[position].getRefPosition();
        }
        return mDelegate.getHeaderView(headerPosition, convertView, parent);
    }

    @Override
    public Object getItem(int position) {
        return mDelegate.getItem(position);
    }

    @Override
    public long getItemId(int position) {
        return mDelegate.getItemId(position);
    }

    @Override
    public int getItemViewType(int position) {
        return mDelegate.getItemViewType(position);
    }

    @Override
    public int getNumHeaders() {
        return mHeaders.length;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return mDelegate.getView(position, convertView, parent);
    }

    @Override
    public int getViewTypeCount() {
        return mDelegate.getViewTypeCount();
    }

    @Override
    public boolean hasStableIds() {
        return mDelegate.hasStableIds();
    }

    protected HeaderData[] generateHeaderList(StickyGridHeadersSimpleAdapter adapter) {
        Map<Long, HeaderData> mapping = new HashMap<>();
        List<HeaderData> headers = new ArrayList<>();

        for (int i = 0; i < adapter.getCount(); i++) {
            long headerId = adapter.getHeaderId(i);
            HeaderData headerData = mapping.get(headerId);
            if (headerData == null) {
                headerData = new HeaderData(i);
                headers.add(headerData);
            }
            headerData.incrementCount();
            mapping.put(headerId, headerData);
        }

        return headers.toArray(new HeaderData[headers.size()]);
    }

    private final class DataSetObserverExtension extends DataSetObserver {
        @Override
        public void onChanged() {
            mHeaders = generateHeaderList(mDelegate);
            notifyDataSetChanged();
        }

        @Override
        public void onInvalidated() {
            mHeaders = generateHeaderList(mDelegate);
            notifyDataSetInvalidated();
        }
    }

    private static class HeaderData {
        private int mCount;

        private int mRefPosition;

        public HeaderData(int refPosition) {
            mRefPosition = refPosition;
            mCount = 0;
        }

        public int getCount() {
            return mCount;
        }

        public int getRefPosition() {
            return mRefPosition;
        }

        public void incrementCount() {
            mCount++;
        }
    }
}
