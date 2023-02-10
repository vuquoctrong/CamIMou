package com.lc.playback.ui;

import static com.mm.android.mobilecommon.utils.TimeUtils.LONG_FORMAT;

import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.lc.playback.R;
import com.lc.playback.adapter.DeviceRecordListAdapter;
import com.lc.playback.api.DeviceRecordService;
import com.lc.playback.api.IRecordInfoCallBack;
import com.lc.playback.contacts.MethodConst;
import com.lc.playback.entity.DeviceDetailListData;
import com.lc.playback.entity.LocalRecordsData;
import com.lc.playback.entity.RecordListData;
import com.lc.playback.entity.RecordsData;
import com.lc.playback.utils.DateHelper;
import com.lechange.pulltorefreshlistview.Mode;
import com.lechange.pulltorefreshlistview.PullToRefreshBase;
import com.mm.android.mobilecommon.openapi.HttpSend;
import com.mm.android.mobilecommon.route.ProviderManager;
import com.mm.android.mobilecommon.utils.DialogUtils;
import com.mm.android.mobilecommon.utils.LogUtil;
import com.mm.android.mobilecommon.utils.TimeUtils;
import com.mm.android.mobilecommon.utils.UIUtils;
import com.mm.android.mobilecommon.widget.LcPullToRefreshRecyclerView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DeviceLocalRecordListFragment extends Fragment implements View.OnClickListener, IRecordInfoCallBack.IDeviceLocalRecordCallBack, PullToRefreshBase.OnRefreshListener2 {
    private static final String TAG = DeviceLocalRecordListFragment.class.getSimpleName();
    private Bundle arguments;
    private String searchDate;
    private long searchDate1;
    private DeviceDetailListData.ResponseData.DeviceListBean deviceListBean;
    private RecyclerView recyclerView;
    private TextView tvMonthDay;
    private long oneDay = 24 * 60 * 60 * 1000;
    private List<RecordListData> recordListDataList = new ArrayList<>();
    private DeviceRecordListAdapter deviceRecordListAdapter;
    private DeviceRecordService deviceRecordService = DeviceRecordService.newInstance();
    private static DeviceLocalRecordListFragment fragment;
    private DeviceRecordListActivity deviceRecordListActivity;
    private LcPullToRefreshRecyclerView deviceList;
    private int pageSize = 30;
    private int pageIndex = 1;
    private String beginTimeForPage;
    private String endTimeOfPageList;
    private String time = "";
    private TextView tvToday;
    private ImageView  mNextDayTv;
    public static DeviceLocalRecordListFragment newInstance() {
        fragment = new DeviceLocalRecordListFragment();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        arguments = getArguments();
        searchDate = DateHelper.dateFormat(new Date(System.currentTimeMillis()));
        searchDate1 = DateHelper.parseMills(searchDate + " 00:00:00");
        deviceRecordListActivity = (DeviceRecordListActivity) getActivity();
        deviceRecordListActivity.llEdit.setVisibility(View.GONE);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_device_cloud_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        if (arguments == null) {
            return;
        }
//        deviceListBean = (DeviceDetailListData.ResponseData.DeviceListBean) arguments.getSerializable(MethodConst.ParamConst.deviceDetail);
        Gson gson = new Gson();
        deviceListBean = gson.fromJson(arguments.getString(MethodConst.ParamConst.deviceDetail), DeviceDetailListData.ResponseData.DeviceListBean.class);
        if (deviceListBean == null) {
            return;
        }
        initData();
    }

    private void initView(View view) {
        view.findViewById(R.id.iv_day_pre).setOnClickListener(this);
        mNextDayTv =view.findViewById(R.id.iv_day_next);
        tvMonthDay = view.findViewById(R.id.tv_month_day);
        deviceList = view.findViewById(R.id.record_list);
        tvToday = view.findViewById(R.id.tv_today);
        deviceList.setOnRefreshListener(this);
        mNextDayTv.setOnClickListener(this);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                refreshMode(Mode.PULL_FROM_START);
                refreshState(true);
            }
        }, 200);
        recyclerView = deviceList.getRefreshableView();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
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

    private void initData() {
        tvMonthDay.setText(searchDate);
        mCalendar.setTime(TimeUtils.stringToDate(searchDate,"yyyy-MM-dd HH:mm:ss"));
        UIUtils.setEnabledEX(TimeUtils.isBeforeToday(mCalendar), mNextDayTv);
    }

    protected Calendar mCalendar = Calendar.getInstance();

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.iv_day_pre) {
            searchDate1 = searchDate1 - oneDay;
            searchDate = DateHelper.dateFormat(new Date(searchDate1));
            tvMonthDay.setText(searchDate);
            mCalendar.setTime(new Date(searchDate1));
            UIUtils.setEnabledEX(TimeUtils.isBeforeToday(mCalendar), mNextDayTv);
            initLocalRecord(false);
        } else if (id == R.id.iv_day_next) {
            searchDate1 = searchDate1 + oneDay;
            searchDate = DateHelper.dateFormat(new Date(searchDate1));
            tvMonthDay.setText(searchDate);
            mCalendar.setTime(new Date(searchDate1));
            UIUtils.setEnabledEX(TimeUtils.isBeforeToday(mCalendar), mNextDayTv);
            initLocalRecord(false);
        }
    }



    @Override
    public void deviceLocalRecord(LocalRecordsData.Response result) {
        if (!isAdded()){
            return;
        }
        tvToday.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
        DialogUtils.dismiss();
        refreshState(false);
        if (result != null && result.data != null && result.data.records != null && result.data.records.size() > 0) {
            if (result.data.records.size() >= pageSize) {
                refreshMode(Mode.BOTH);
            } else {
                refreshMode(Mode.PULL_FROM_START);
            }
            recordListDataList = dealLocalRecord(result, deviceListBean.deviceId);
        } else {
            if (pageIndex == 1){
                tvToday.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            }
            refreshMode(Mode.PULL_FROM_START);
        }
        showList();
    }

    @Override
    public void onError(Throwable throwable) {
        if (!isAdded()){
            return;
        }
        LogUtil.errorLog(TAG, "error", throwable);
        DialogUtils.dismiss();
        refreshState(false);
        refreshMode(Mode.PULL_FROM_START);
        if(TextUtils.equals(throwable.getMessage(),String.valueOf(HttpSend.NET_ERROR_CODE))){
            Toast.makeText(getContext(), R.string.mobile_common_operate_fail, Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(getContext(), throwable.getMessage(), Toast.LENGTH_SHORT).show();
        }
        pageIndex = 1;
        time = "";
        recordListDataList.clear();
        showList();
    }

    private void initLocalRecord(boolean isLoadMore) {
        if (isLoadMore) {
            pageIndex = pageIndex + pageSize;
            beginTimeForPage = TimeUtils.date2String(new Date(DateHelper.parseMills(endTimeOfPageList)+1*1000),LONG_FORMAT);
        } else {
            pageIndex = 1;
            time = "";
            recordListDataList.clear();
            beginTimeForPage =  searchDate +" 00:00:00";
        }
        DialogUtils.show(getActivity());
        getLocalData();
      /*  deviceRecordService.querySDUse(deviceListBean.deviceId, new IGetDeviceInfoCallBack.ICommon<String>() {

            @Override
            public void onCommonBack(String response) {
                if (!"empty".equals(response)){
                    getLocalData();
                }else{
                    DialogUtils.dismiss();
                    refreshState(false);
                    tvToday.setVisibility(View.VISIBLE);
                    tvToday.setText(getActivity().getResources().getString(R.string.lc_demo_device_local_sd));
                }
            }

            @Override
            public void onError(Throwable throwable) {
                Toast.makeText(getActivity(),throwable.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });*/
    }

    private void getLocalData(){
        LocalRecordsData localRecordsData = new LocalRecordsData();
        localRecordsData.data.deviceId = deviceListBean.deviceId;
        localRecordsData.data.channelId = deviceListBean.channelList.get(deviceListBean.checkedChannel).channelId;
        localRecordsData.data.beginTime = beginTimeForPage;
        localRecordsData.data.endTime = searchDate + " 23:59:59";
        localRecordsData.data.type = "All";
        localRecordsData.data.count = "30";
        localRecordsData.data.queryRange =  pageIndex + "-" + (pageIndex + pageSize - 1);
        deviceRecordService.queryLocalRecords(localRecordsData, this);
    }

    private void showList() {
        if (deviceRecordListAdapter == null) {
            deviceRecordListAdapter = new DeviceRecordListAdapter(getContext(), recordListDataList);
            recyclerView.setAdapter(deviceRecordListAdapter);
        } else {
            deviceRecordListAdapter.notifyDataSetChanged();
        }
        deviceRecordListAdapter.setEditClickListener((outPosition, innerPosition) -> {
            LogUtil.debugLog(TAG, outPosition + "..." + innerPosition);
            if(UIUtils.isFastDoubleClick()){
                return;
            }
//            Bundle bundle = new Bundle();
//            bundle.putSerializable(MethodConst.ParamConst.deviceDetail, deviceListBean);
//            bundle.putSerializable(MethodConst.ParamConst.recordData, recordListDataList.get(outPosition).recordsData.get(innerPosition));
//            bundle.putInt(MethodConst.ParamConst.recordType, MethodConst.ParamConst.recordTypeLocal);
//                Intent intent = new Intent(getContext(), DeviceRecordPlayActivity.class);
//                intent.putExtras(bundle);
//                startActivity(intent);
            Gson gson = new Gson();
            ProviderManager.getPlaybackProvider().gotoPlayback(gson.toJson(deviceListBean),
                    gson.toJson(recordListDataList.get(outPosition).recordsData.get(innerPosition)),
                    MethodConst.ParamConst.recordTypeLocal);
        });
    }

    private List<RecordListData> dealLocalRecord(LocalRecordsData.Response result, String deviceId) {
        for (LocalRecordsData.ResponseData.RecordsBean recordsBean : result.data.records) {
            String innerTime = recordsBean.beginTime.substring(11, 13);
            RecordsData a = new RecordsData();
            a.recordType = 1;
            a.deviceId = deviceId;
            a.size = "" + recordsBean.fileLength;
            a.recordId = recordsBean.recordId;
            a.channelID = recordsBean.channelID;
            a.beginTime = recordsBean.beginTime;
            a.endTime = recordsBean.endTime;
            a.fileLength = recordsBean.fileLength;
            a.type = recordsBean.type;
            if (!innerTime.equals(time)) {
                RecordListData r = new RecordListData();
                r.period = innerTime + ":00";
                r.recordsData = new ArrayList<>();
                r.recordsData.add(a);
                recordListDataList.add(r);
                time = innerTime;
            } else {
                RecordListData b = recordListDataList.get(recordListDataList.size() - 1);
                b.recordsData.add(a);
            }
            //分页最后一个录像片段的结束时间+1，作为下一次分页的开始时间
            endTimeOfPageList = recordsBean.endTime;
            Log.i(TAG,"endTimeOfPageList================"+endTimeOfPageList);
        }
        LogUtil.debugLog(TAG, recordListDataList.size() + "");
        return recordListDataList;
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase pullToRefreshBase) {
        initLocalRecord(false);

    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase pullToRefreshBase) {
        initLocalRecord(true);
    }
}
