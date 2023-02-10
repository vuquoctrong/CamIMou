package com.lc.media.ui;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.lc.media.R;
import com.lc.media.adapter.PixelListAdapter;
import com.lc.media.entity.DeviceDetailListData;

import java.util.List;


/**
 * Displays the supported resolution
 *
 * 显示支持分辨率的弹框
 */
public class LcPopupWindow extends PopupWindow {

    private RecyclerView pixelRecycle;
//    private ArrayList<DeviceDetailListData.ResponseData.DeviceListBean.ChannelsBean.ResolutionBean> mData = new ArrayList<>();
    private PixelListAdapter mPixelAdapter;

    public LcPopupWindow(Context context, List<DeviceDetailListData.ResponseData.DeviceListBean.ChannelsBean.ResolutionBean> dataList) {
        super(context);
        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        setOutsideTouchable(true);
        setFocusable(true);
        setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        View contentView = LayoutInflater.from(context).inflate(R.layout.view_pixel_popwindow,
                null, false);
        pixelRecycle = contentView.findViewById(R.id.pixel_recycle);
        pixelRecycle.setLayoutManager(new LinearLayoutManager(context));
        mPixelAdapter = new PixelListAdapter(context,dataList);
        pixelRecycle.setAdapter(mPixelAdapter);
        setContentView(contentView);
    }

    public void setPixelRecycleListener(onRecyclerViewItemClickListener listener){
        mPixelAdapter.setOnItemClickListener(listener);
    }

    @SuppressWarnings("ResourceType")
    public int makeDropDownMeasureSpec(int measureSpec) {
        int mode;
        if (measureSpec == ViewGroup.LayoutParams.WRAP_CONTENT) {
            mode = View.MeasureSpec.UNSPECIFIED;
        } else {
            mode = View.MeasureSpec.EXACTLY;
        }
        return View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(measureSpec), mode);
    }

    public  interface onRecyclerViewItemClickListener{
        void onItemClick(RecyclerView parent, View view, int position,String name,int imageSize,int streamType);
    }

}
