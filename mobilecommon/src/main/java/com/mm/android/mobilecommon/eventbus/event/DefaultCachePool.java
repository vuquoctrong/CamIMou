package com.mm.android.mobilecommon.eventbus.event;

import java.util.ArrayList;
import java.util.List;


public class DefaultCachePool<T> implements ICachePool<T>{
	
    private static final int DEFAULT_MAX_POOL_SIZE = 1000;
	
	private final List<T> cachePool;
	
	private int maxPoolSize = DEFAULT_MAX_POOL_SIZE;
	
	private Class<T> cls;
	
	public DefaultCachePool(Class<T> cls, int maxPoolSize)
	{
		cachePool = new ArrayList<>();
		this.maxPoolSize = maxPoolSize;
		this.cls = cls;
	}

	@Override
	public T obtain()
	{
		synchronized (cachePool) {
            int size = cachePool.size();
            if (size > 0) {
            	return cachePool.remove(size - 1);
            }
        }
		
		T obj = null;
		
		try {
			obj = cls.newInstance();
		} catch (Exception e) {
			obj = null;
		} 
		
		return obj;
	}

	@Override
	public void recycle(T o)
	{
		 synchronized (cachePool) {
	            if (cachePool.size() < maxPoolSize) {
	            	cachePool.add(o);
	            }
	     }
	}
}
