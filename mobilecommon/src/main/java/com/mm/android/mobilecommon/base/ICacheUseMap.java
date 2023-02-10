package com.mm.android.mobilecommon.base;

import java.util.Map;

public interface ICacheUseMap<T1,T2> {
    /**
     * Update Data
     *
     * 更新数据
     */
    void put(T1 key, T2 data);

    /**
     * Clearing all data
     *
     * 清除全部数据
     */
    void clearAll();

    /**
     * Clearing data
     *
     * 清除数据
     */
    void clear(T1 key);

    /**
     * Get data
     *
     * 获取数据
     */
    Map<T1, T2> getList();
    void remove(T1 key);
}
