package com.xingcloud.event;

import com.xingcloud.core.ModelBase;

/**
 * 属性变化事件
 * @author chuckzhang
 *
 */
public class PropertyChangeEvent extends XingCloudEvent {

	public static String PROPERTY_CHANGE="PROPERTY_CHANGE";
	/**
	 * 新数据
	 */
	public Object newValue;
	/**
	 * 原始数据
	 */
	public Object oldValue;
	/**
	 * 属性名称
	 */
	public String property;
	
	/**
	 * 关联的数据模型
	 */
	public ModelBase source;
	
	/**
	 * 
	 * @param prop 属性名称
	 * @param oldVal 原始数据
	 * @param newVal 新数据
	 * @param src 关联的数据模型
	 */
	public PropertyChangeEvent(String prop,Object oldVal,Object newVal,ModelBase src) {
		super(PROPERTY_CHANGE,null);
		property = prop;
		oldValue = oldVal;
		newValue = newVal;
		source = src;
	}

}
