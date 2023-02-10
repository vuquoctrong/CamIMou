package com.mm.android.mobilecommon.eventbus.event;

public interface ICachePool<T> {
	
	public abstract T obtain();
	
	public abstract void recycle(T o);
	
}
