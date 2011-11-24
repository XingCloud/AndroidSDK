package com.xingcloud.event;

/**
 * XingCloud的事件回调接口
 *
 */
public interface IEventListener {
	/**
	 * 事件之前函数调用之前会被调用
	 * @param evt 事件
	 */
	public void prePerformEvent(XingCloudEvent evt);
	/**
	 * 事件执行函数
	 * @param evt 事件
	 */
	public void performEvent(XingCloudEvent evt);
	/**
	 * 事件之前函数调用之后会被调用
	 * @param evt 事件
	 */
	public void postPerformEvent(XingCloudEvent evt);
}
