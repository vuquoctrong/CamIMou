package com.lc.media.api;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.lc.media.entity.DeviceLocalCacheData;

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

    public int addLocalCache(DeviceLocalCacheData deviceLocalCacheData) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues v = new ContentValues();
        v.put("device_id", deviceLocalCacheData.getDeviceId());
        v.put("device_name", deviceLocalCacheData.getDeviceName());
        v.put("channel_id", deviceLocalCacheData.getChannelId());
        v.put("channel_name", deviceLocalCacheData.getChannelName());
        v.put("pic_path", deviceLocalCacheData.getPicPath());
        v.put("creation_time", System.currentTimeMillis());
        v.put("modify_time", System.currentTimeMillis());
        long rowid = db.insert(TABLE_NAME, null, v);
        if (rowid != -1) {
            return 1;
        }
        return 0;
    }

    public int updateLocalCache(DeviceLocalCacheData deviceLocalCacheData) {
        ContentValues v = new ContentValues();
        v.put("pic_path", deviceLocalCacheData.getPicPath());
        v.put("modify_time", System.currentTimeMillis());
        SQLiteDatabase db = getWritableDatabase();
        int ret = db.update(TABLE_NAME, v, "cache_id=?", new String[]{String.valueOf(deviceLocalCacheData.getCacheId())});
        if (ret > 0) {
            return 1;
        }
        return 0;
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
