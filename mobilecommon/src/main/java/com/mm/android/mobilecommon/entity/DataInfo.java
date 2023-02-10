package com.mm.android.mobilecommon.entity;

import java.io.Serializable;
import java.util.Hashtable;
import java.util.UUID;


/**
 * File description: Entity base class, support cloning, serialization
 *
 * 文件描述：实体基类，支持克隆、序列化
 */

public class DataInfo implements Cloneable, Serializable  {

	private static final long serialVersionUID = 1L;
	protected String uuid = UUID.randomUUID().toString();	   // 对象唯一标示
	public Hashtable<String, Object> getExtandAttributeTable() {
		if(extandAttributeTable == null){
			extandAttributeTable = new Hashtable<>();
		}
		return extandAttributeTable;
	}
	protected Hashtable<String, Object> extandAttributeTable = null; // 扩展属性列表


	/**
	 * Gets the extended attribute value
	 *
	 * 获得扩展属性值
	 */
	public Object getExtandAttributeValue(String name)
	{
		if (extandAttributeTable == null
			|| !extandAttributeTable.containsKey(name))
		{
			return null;
		}
		
		return extandAttributeTable.get(name);
	}

	/**
	 * Setting extended attributes
	 *
	 * 设置扩展属性
	 */
	public void setExtandAttributeValue(String name, Object value)
	{
		if (extandAttributeTable == null)
		{
			extandAttributeTable = new Hashtable<>();
		}
		if(value != null){
			extandAttributeTable.put(name, value);
		}
	}

	public String getUuid() {
		return uuid;
	}
	
	@Override
	public String toString() {
		return uuid;
	}
	
	
	@Override
	protected Object clone() throws CloneNotSupportedException
	{
		DataInfo dataInfo = null;
		
		dataInfo = (DataInfo) super.clone();
		
		if (extandAttributeTable != null) {
			dataInfo.extandAttributeTable = new Hashtable<>();
			dataInfo.extandAttributeTable.putAll(extandAttributeTable);
			
		}
		
		return dataInfo;
	}
	
}
