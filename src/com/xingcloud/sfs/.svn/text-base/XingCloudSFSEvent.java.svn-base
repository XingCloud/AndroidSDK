package com.xingcloud.sfs;

import com.xingcloud.event.XingCloudEvent;

/**
 * SFS事件类，包含一个事件类型和事件监听器
 * 行云Android SDK内部采用了Oberver模型的事件机制
 */
public class XingCloudSFSEvent extends XingCloudEvent {
	
	public static String EXTENSION_SERVICE="xingcloud_sfs_service";
	protected BaseEvent _sfs_event;
	
	public XingCloudSFSEvent(String type,BaseEvent evt)
	{
		super(type,null);
		_sfs_event = evt;
	}
	
	public BaseEvent getSFSEvent()
	{
		return _sfs_event;
	}
}