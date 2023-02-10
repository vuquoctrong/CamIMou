package com.lechange.demo.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.common.openapi.ClassInstanceManager;
import com.common.openapi.DeviceSubAccountListService;
import com.common.openapi.IGetDeviceInfoCallBack;
import com.common.openapi.MethodConst;
import com.common.openapi.entity.DeviceDetailListData;
import com.google.gson.Gson;
import com.lc.message.MessageDetailActivity;
import com.lechange.common.log.Logger;
import com.lechange.demo.R;
import com.lechange.demo.adapter.DeviceListAdapter;
import com.lechange.opensdk.media.LCOpenSDK_LoginManager;
import com.lechange.opensdk.media.LCOpenSDK_P2PDeviceInfo;
import com.lechange.pulltorefreshlistview.Mode;
import com.lechange.pulltorefreshlistview.PullToRefreshBase;
import com.mm.android.deviceaddmodule.LCDeviceEngine;
import com.mm.android.mobilecommon.AppConsume.ThreadPool;
import com.mm.android.mobilecommon.openapi.HttpSend;
import com.mm.android.mobilecommon.openapi.TokenHelper;
import com.mm.android.mobilecommon.route.ProviderManager;
import com.mm.android.mobilecommon.utils.LogUtil;
import com.mm.android.mobilecommon.widget.CommonTitle;
import com.mm.android.mobilecommon.widget.LcPullToRefreshRecyclerView;
import com.opensdk.devicedetail.tools.GsonUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static com.mm.android.mobilecommon.route.RoutePathManager.ActivityPath.DeviceListActivityPath;

import org.json.JSONArray;
import org.json.JSONObject;

@Route(path = DeviceListActivityPath)
public class DeviceListActivity extends Activity implements IGetDeviceInfoCallBack.ISubAccountDevice<DeviceDetailListData.Response>, PullToRefreshBase.OnRefreshListener2 {
    private static final String TAG = DeviceListActivity.class.getSimpleName();
    private LcPullToRefreshRecyclerView deviceList;
    private RecyclerView mRecyclerView;
    private List<DeviceDetailListData.ResponseData.DeviceListBean> datas = new ArrayList<>();
    private DeviceListAdapter deviceListAdapter;
    //乐橙分页index
    public long baseBindId = -1;
    //开放平台分页index
    public long openBindId = -1;
    int pageNum = 1;
    int listOffset = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_list);
        initView();
    }

    private void initView() {
        CommonTitle title = (CommonTitle) findViewById(R.id.rl_title);
        title.setIconRight(R.mipmap.lc_demo_nav_add);
        title.setTitleCenter(R.string.lc_demo_main_title);
        title.setOnTitleClickListener(new CommonTitle.OnTitleClickListener() {
            @Override
            public void onCommonTitleClick(int id) {
                if (id == CommonTitle.ID_LEFT) {
                    finish();
                } else if (id == CommonTitle.ID_RIGHT) {
                    try {
                        LCDeviceEngine.newInstance().addDevice(DeviceListActivity.this);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        deviceList = findViewById(R.id.device_list);
        deviceList.setOnRefreshListener(this);
        refreshState(false);
        mRecyclerView = deviceList.getRefreshableView();
        mRecyclerView.setVisibility(View.GONE);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(DeviceListActivity.this));
        listOffset = getResources().getDimensionPixelOffset(R.dimen.px_30);
        mRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                outRect.bottom = listOffset;
                // Add top margin only for the first item to avoid double space between items
                if (parent.getChildLayoutPosition(view) == 0) {
                    outRect.top = listOffset;
                }
            }
        });
    }

    private void setAdapter() {
        if (deviceListAdapter == null) {
            deviceListAdapter = new DeviceListAdapter(DeviceListActivity.this, datas);
        }
        mRecyclerView.setAdapter(deviceListAdapter);
        deviceListAdapter.setOnItemClickListener(new DeviceListAdapter.OnItemClickListener() {
            @Override
            public void onMessageClick(int outPosition, int innerPosition) {
                if (datas.size() == 0) {
                    return;
                }
                Bundle bundle = new Bundle();
                bundle.putString("deviceId", datas.get(outPosition).deviceId);
                bundle.putString("channelId", "" + innerPosition);
                if (datas.get(outPosition).channelList.size() > 1) {
                    bundle.putString("deviceName", datas.get(outPosition).channelList.get(innerPosition).channelName);
                } else {
                    bundle.putString("deviceName", datas.get(outPosition).deviceName);
                }
                Intent intent = new Intent(DeviceListActivity.this, MessageDetailActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }

            @Override
            public void onSettingClick(int position) {
                if (datas.size() == 0) {
                    return;
                }
                ProviderManager.getDeviceDetailProvider().gotoDeviceDetails(DeviceListActivity.this, GsonUtils.toJson(datas.get(position)), MethodConst.ParamConst.fromList);
            }

            @Override
            public void onDetailClick(int position) {
                if (datas.size() == 0) {
                    return;
                }
                if (!datas.get(position).getDeviceStatus().equals("online")) {
                    return;
                }
                Logger.e("998877", "DeviceListActivity onChannelClick");
                Gson gson = new Gson();
                String json = gson.toJson(datas.get(position));
                ProviderManager.getPreviewProvider().gotoPrevew(json);
            }

            @Override
            public void onChannelClick(int outPosition, int innerPosition) {
                if (datas.size() == 0) {
                    return;
                }
                if (!datas.get(outPosition).channelList.get(innerPosition).channelStatus.equals("online")) {
                    return;
                }
                Logger.e("998877", "DeviceListActivity onChannelClick");
                DeviceDetailListData.ResponseData.DeviceListBean deviceListBean = datas.get(outPosition);
                deviceListBean.checkedChannel = innerPosition;
                Gson gson = new Gson();
                String json = gson.toJson(deviceListBean);
                ProviderManager.getPreviewProvider().gotoPrevew(json);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                setAdapter();
                refreshMode(Mode.PULL_FROM_START);
                refreshState(true);
            }
        }, 200);
    }

    @Override
    public void DeviceList(DeviceDetailListData.Response responseData) {
        if (isDestroyed()) {
            return;
        }
        mRecyclerView.setVisibility(View.VISIBLE);
        refreshState(false);
        if (responseData.baseBindId != -1) {
            baseBindId = responseData.baseBindId;
        }
        if (responseData.openBindId != -1) {
            openBindId = responseData.openBindId;
        }
        if (responseData.data != null && responseData.data.deviceList != null && responseData.data.deviceList.size() != 0) {
            Iterator<DeviceDetailListData.ResponseData.DeviceListBean> iterator = responseData.data.deviceList.iterator();
            while (iterator.hasNext()) {
                DeviceDetailListData.ResponseData.DeviceListBean next = iterator.next();
                if (next.channelList != null && next.channelList.size() == 0 && !next.catalog.contains("NVR")) {
                    // 使用迭代器中的remove()方法,可以删除元素.
                    iterator.remove();
                }
            }
        }
        //没有数据
        if ((responseData.data == null || responseData.data.deviceList == null || responseData.data.deviceList.size() == 0) && datas.size() == 0) {
            //本次未拉到数据且上次也没有数据
            deviceListAdapter.notifyDataSetChanged();
        } else {
            if ((responseData.data == null || responseData.data.deviceList == null || responseData.data.deviceList.size() == 0)) {//本次未拉到数据但上次有数据
                if (pageNum == 1) {
                    datas.clear();
                    deviceListAdapter.notifyDataSetChanged();
                }
                return;
            }
            deviceList.setVisibility(View.VISIBLE);
            if (pageNum == 1) {
                datas.clear();
            }
            datas.addAll(responseData.data.deviceList);
            deviceListAdapter.notifyDataSetChanged();
            if (datas.size() >= 8) {
                refreshMode(Mode.BOTH);
            } else {
                refreshMode(Mode.PULL_FROM_START);
            }
            ThreadPool.submit(() -> {
                try {
                    List<LCOpenSDK_P2PDeviceInfo> deviceInfos = new ArrayList<>();
                    for (DeviceDetailListData.ResponseData.DeviceListBean deviceListBean : responseData.data.deviceList) {
                        // 没有playToken，则不需要参与预打洞
                        if (TextUtils.isEmpty(deviceListBean.playToken)) {
                            continue;
                        }
                        LCOpenSDK_P2PDeviceInfo p2PDeviceInfo = new LCOpenSDK_P2PDeviceInfo();
                        p2PDeviceInfo.playToken = deviceListBean.playToken;
                        p2PDeviceInfo.did = deviceListBean.deviceId;
                        deviceInfos.add(p2PDeviceInfo);
                    }
                    int rst=LCOpenSDK_LoginManager.addDevices(TokenHelper.getInstance().subAccessToken, deviceInfos);
                    Logger.e("addDevices 返回信息：", ""+rst);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

        }
    }

    @Override
    public void onError(Throwable throwable) {
        if (isDestroyed()) {
            return;
        }
        refreshState(false);
        LogUtil.errorLog(TAG, "error", throwable);
        if(TextUtils.equals(throwable.getMessage(),String.valueOf(HttpSend.NET_ERROR_CODE))){
            Toast.makeText(DeviceListActivity.this, R.string.mobile_common_operate_fail, Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(DeviceListActivity.this, throwable.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase pullToRefreshBase) {
        pageNum = 1;
        getDeviceList(false);
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase pullToRefreshBase) {
        pageNum = pageNum + 1;
        getDeviceList(true);
    }

    private void getDeviceList(boolean isLoadMore) {
        if (!isLoadMore) {
            baseBindId = -1;
            openBindId = -1;
//            datas.clear();
        }
        DeviceSubAccountListService deviceSubAccountListService = ClassInstanceManager.newInstance().getDeviceSubAccountListService();
        deviceSubAccountListService.getSubAccountDeviceList(pageNum, this);


      /*  DeviceListService deviceVideoService = ClassInstanceManager.newInstance().getDeviceListService();
        DeviceListData deviceListData = new DeviceListData();
        deviceListData.data.openBindId = this.openBindId;
        deviceListData.data.baseBindId = this.baseBindId;
        deviceVideoService.deviceBaseList(deviceListData, DeviceListActivity.this);*/
    }

    private void refreshState(boolean refresh) {
        if (refresh) {
            deviceList.setRefreshing(true);
        } else {
            deviceList.onRefreshComplete();
        }
    }

    private void refreshMode(Mode mode) {
        deviceList.setMode(mode);
    }
}
