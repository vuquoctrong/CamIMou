package com.common.openapi;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.common.openapi.entity.DeviceLocalCacheData;
import java.util.ArrayList;
import java.util.List;

public class DeviceLocalCacheDatabase extends SQLiteOpenHelper {
    private static final String TAG = DeviceLocalCacheDatabase.class.getSimpleName();

    private static final String DB_NAME = "device_local_cache";
    private static final String TABLE_NAME = "t_device_local_cache";

    private static final String TABLE_CREATE_SQL = "create table " + TABLE_NAME + "(" +
            "cache_id integer primary key autoincrement, " +
            "device_id text, " +
            "device_name text, " +
            "channel_id text, " +
            "channel_name text, " +
            "pic_path text, " +
            "creation_time integer, " +
            "modify_time integer " +
            ")";

    private static final String[] TABLE_COLUMNS = {
            "cache_id",
            "device_id",
            "device_name",
            "channel_id",
            "channel_name",
            "pic_path",
            "creation_time",
            "modify_time"
    };

    public DeviceLocalCacheDatabase(Context context) {
        super(context, DB_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE_SQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public DeviceLocalCacheData findLocalCache(DeviceLocalCacheData deviceLocalCacheData) {
        StringBuilder selectionBuilder = new StringBuilder();
        List<String> selectionArgs = new ArrayList<>();
        boolean first = true;
        if (deviceLocalCacheData.getDeviceId() != null) {
            selectionBuilder.append(" device_id =? ");
            selectionArgs.add(deviceLocalCacheData.getDeviceId());
            first = false;
        }
        if (deviceLocalCacheData.getChannelId() != null) {
            if (!first) {
                selectionBuilder.append(" and ");
            }
            selectionBuilder.append(" channel_id =? ");
            selectionArgs.add(deviceLocalCacheData.getChannelId());
        }
        SQLiteDatabase db = getReadableDatabase();
        try (Cursor c = db.query(TABLE_NAME, TABLE_COLUMNS, selectionBuilder.toString(), selectionArgs.toArray(new String[0]), null, null, null, "1")) {
            if (c.moveToFirst()) {
                return (buildLocalCacheFromCursor(c));
            }
        }
        return null;
    }

    private DeviceLocalCacheData buildLocalCacheFromCursor(Cursor c) {
        DeviceLocalCacheData deviceLocalCacheData = new DeviceLocalCacheData();
        deviceLocalCacheData.setCacheId(c.getInt(0));
        deviceLocalCacheData.setDeviceId(c.getString(1));
        deviceLocalCacheData.setDeviceName(c.getString(2));
        deviceLocalCacheData.setChannelId(c.getString(3));
        deviceLocalCacheData.setChannelName(c.getString(4));
        deviceLocalCacheData.setPicPath(c.getString(5));
        deviceLocalCacheData.setCreationTime(c.getLong(6));
        deviceLocalCacheData.setModifyTime(c.getLong(7));
        return deviceLocalCacheData;
    }
}
