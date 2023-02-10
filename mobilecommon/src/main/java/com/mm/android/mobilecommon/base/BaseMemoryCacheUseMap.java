package com.mm.android.mobilecommon.base;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Memory Cache
 *
 * 内存缓存
 */

public abstract class BaseMemoryCacheUseMap<T1,T2> implements ICacheUseMap<T1,T2> {
    public Map<T1, T2> cache= new ConcurrentHashMap<>();

    @Override
    public void clear(T1 key){
        cache.remove(key);
    }

    @Override
    public void put(T1 key, T2 data) {
        cache.put(key, data);
    }

    @Override
    public void remove(T1 key) {
        cache.remove(key);
    }

    @Override
    public void clearAll(){
        cache.clear();
    }

    @Override
    public Map<T1, T2> getList(){
        return cache;
    }
}
