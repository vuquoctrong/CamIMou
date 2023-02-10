package com.mm.android.mobilecommon.eventbus.event;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;


public final class Event implements Parcelable{
	
    private static final int MAX_POOL_SIZE = 1000;
    
    private final static ICachePool<Event> cachePool = new DefaultCachePool<>(Event.class, MAX_POOL_SIZE);
	
	private int eventId;
	
	private int arg1; 
	
	private int arg2;
	
	private Object obj;
	
    private String posterId;

	private Bundle extras;
	
	private Bundle data = new Bundle();
	
    public Event()
	{
	}
	
	public Event(int eventId) {
		this.eventId = eventId;
	}
	
	public Event(int eventId, int arg1) {
		this.eventId = eventId;
		this.arg1 = arg1;
	}
	
	public Event(int eventId, int arg1, int arg2)
	{
		this.eventId = eventId;
		this.arg1 = arg1;
		this.arg2 = arg2;
	}
	
	public Event(int eventId, int arg1, int arg2, Object obj)
	{
		this.eventId = eventId;
		this.arg1 = arg1;
		this.arg2 = arg2;
		this.obj = obj;
	}
	
	public Event(int eventId, Bundle extras) {
		this.eventId = eventId;
		this.extras = extras;
	}
	
	public static Event obtain(int eventId) {
		Event event = cachePool.obtain();
		
		if (event == null) {
			event = new Event();
		}
		
		event.setEventId(eventId);
		
		return event;
	}

	
	public void recycle()
	{
		clearForRecycle();
		cachePool.recycle(this);
	}
	
    public void copyFrom(Event e) {
        this.eventId = e.eventId;
        this.arg1 = e.arg1;
        this.arg2 = e.arg2;
        this.obj = e.obj;
        this.posterId = e.posterId;
        this.data = (Bundle)data.clone();
        if (e.extras != null) {
            this.extras = (Bundle) e.extras.clone();
        } else {
            this.extras = null;
        }
    }
    
    void clearForRecycle() {
    	eventId = 0;
        arg1 = 0;
        arg2 = 0;
        obj = null;
        posterId = null;
        if (this.extras != null) {
        	extras.clear();
        	extras = null;
        }
        this.data.clear();
    }
    
    public void setPosterId(String id) {
        posterId = id;
    }

    public String getPosterId() {
        return posterId;
    }

    public void putString(String key, String value)
    {
    	data.putString(key, value);
    }
    
    public void putInt(String key, int value)
    {
    	data.putInt(key, value);
    }
    
    public void putShort(String key, short value)
    {
    	data.putShort(key, value);
    }
    
    public void putBoolean(String key, boolean value)
    {
    	data.putBoolean(key, value);
    }
    
    public void putDouble(String key, double value)
    {
    	data.putDouble(key, value);
    }
    
    public void putParcelable(String key, Parcelable value)
    {
    	data.putParcelable(key, value);
    }
    
    
    public void putSerializable(String key, Serializable value)
    {
    	data.putSerializable(key, value);
    }
    
    public void putBundle(String key, Bundle value)
    {
    	data.putBundle(key, value);
    }
    
    public String getString(String key)
    {
    	return data.getString(key);
    }
    
    public int getInt(String key)
    {
    	return data.getInt(key);
    }
    
    public short getShort(String key)
    {
    	return data.getShort(key);
    }
    
    public boolean getBoolean(String key)
    {
    	return data.getBoolean(key);
    }
    
    public double getDouble(String key)
    {
    	return data.getDouble(key);
    }
    
    public Parcelable getParcelable(String key)
    {
    	return data.getParcelable(key);
    }
    
    public Serializable getSerializable(String key)
    {
    	return data.getSerializable(key);
    }
    
    public Bundle getBundle(String key)
    {
    	return data.getBundle(key);
    }
    
    public Object get(String key)
    {
    	return data.get(key);
    	
    }
    
    public boolean containsKey(String key)
    {
    	return data.containsKey(key);
    }
    
    @Override
    public String toString() {
    	StringBuilder b = new StringBuilder();
          
        b.append("{ eventId=");
        b.append(eventId);
        
        if (arg1 != 0) {
        	b.append(" arg1=");
            b.append(arg1);
        }

        if (arg2 != 0) {
            b.append(" arg2=");
            b.append(arg2);
        }

        if (obj != null) {
            b.append(" obj=");
            b.append(obj);
        }
        
        if (posterId != null) {
            b.append("posterId =");
            b.append(posterId);
        }

        if (extras != null) {
        	b.append(" extras=");
            b.append(extras.toString());
        }
        
        b.append(" data=");
        b.append(data.toString());

        b.append(" }");
          
        return b.toString();
    }
	
	public Object getObject()
	{
		return obj;
	}
	
	public void setObject(Object object)
	{
		this.obj = object;
	}
	
	public Bundle getExtras()
	{
		return extras;
	}
	
	public int getEventId()
	{
		return eventId;
	}
	
	public int getArg1()
	{
		return arg1;
	}
	
	public void setArg1(int arg1)
	{
		this.arg1 = arg1;
	}
	
	public int getArg2()
	{
		return arg2;
	}
	
	public void setArg2(int arg2)
	{
		this.arg2 = arg2;
	}
	
	public void setEventId(int eventId)
	{
		this.eventId = eventId;
	}
	
	public void setExtras(Bundle bundle)
	{
		this.extras = bundle;
	}

	@Override
	public int describeContents()
	{

		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags)
	{
		dest.writeInt(eventId);
	    dest.writeInt(arg1);
	    dest.writeInt(arg2);
	    Bundle bundle = new Bundle();

        if (posterId != null) {
            dest.writeString(posterId);
        }

	    if (obj != null) {
            try {
                if (obj instanceof Parcelable) {
                    Parcelable p = (Parcelable) obj;
                    bundle.putParcelable("obj", p);
                } else if (obj instanceof Serializable) {
                    Serializable p = (Serializable) obj;
                    bundle.putSerializable("obj", p);
                }

            } catch (ClassCastException e) {
                throw new RuntimeException("Can't marshal non-Parcelable objects across setObject().");
            }
	    }

	    if (extras != null) {
	    	bundle.putBundle("extras", extras);
	    }
	    
	    bundle.putBundle("data", data);
	    
	    dest.writeBundle(bundle);
	}
	
	public static final Parcelable.Creator<Event> CREATOR = new Parcelable.Creator<Event>() {
		public Event createFromParcel(Parcel source) {
			Event event = new Event();
			event.readFromParcel(source);
			return event;
		}
 
		public Event[] newArray(int size) {
			return new Event[size];
		}
	};
	
	private void readFromParcel(Parcel source) {
		eventId = source.readInt();
        arg1 = source.readInt();
        arg2 = source.readInt();
        posterId = source.readString();
      /*  if (source.readInt() != 0) {
            obj = source.readParcelable(getClass().getClassLoader());
        }*/
        Bundle bundle = source.readBundle();
        
        if (bundle.containsKey("obj")) {
            obj = bundle.getParcelable("obj");
            if (obj == null) {
                obj = bundle.getSerializable("obj");
            }

        }
        
        if (bundle.containsKey("extras")) {
        	extras = bundle.getBundle("extras");
        }
        
        data = bundle.getBundle("data");
    }
}
