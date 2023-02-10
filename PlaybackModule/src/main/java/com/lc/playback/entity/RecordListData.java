package com.lc.playback.entity;


import com.lc.playback.adapter.DeviceInnerRecordListAdapter;

import java.io.Serializable;
import java.util.List;

public class RecordListData implements Serializable {
    public String period;
    public List<RecordsData> recordsData;
    public DeviceInnerRecordListAdapter deviceInnerRecordListAdapters;
}
