package com.xingcloud.users.auditchanges;

import java.util.ArrayList;

import com.xingcloud.core.ModelBase;
import com.xingcloud.core.XingCloud;
import com.xingcloud.event.IEventListener;
import com.xingcloud.items.owned.ItemsCollection;
import com.xingcloud.items.owned.OwnedItem;
import com.xingcloud.items.spec.AsObject;
import com.xingcloud.users.MessagingEvent;
import com.xingcloud.users.RemotingMessage;

public class AuditChange extends RemotingMessage {
	/**
	 * 缓存哪些对象发生了变更，用于服务器返回数据后的数据更新处理 
	 */		
	public AsObject changeField=new AsObject();
	/**
	 * 所有数据表属性变化列表
	 */
	public ArrayList<AsObject> changes=new ArrayList<AsObject>();	

	public AuditChange()
	{
		super(null,new AsObject(),null,null);
	}
	
	/**
	 * auditChange
	 * */
	public AuditChange(AsObject params)
	{
		super(null,params,null,null);
	}

	/**
	 * auditChange
	 * */
	public AuditChange(AsObject params,IEventListener onSuccess, IEventListener onFail)
	{
		super(null,params,onSuccess,onFail);
	}

	/*
	 * (non-Javadoc)
	 * @see com.xingcloud.users.RemotingMessage#appendMessage(com.xingcloud.items.spec.AsObject)
	 */
	public void appendMessage(AsObject msg)
	{
		msg.setProperty("name", this.getName());
		msg.setProperty("changes", changes);
		msg.setProperty("params", params);
	}

	/*
	 * (non-Javadoc)
	 * @see com.xingcloud.users.RemotingMessage#handleDataBack(com.xingcloud.users.MessagingEvent)
	 */
	public void handleDataBack(MessagingEvent evt)
	{
		if(evt.isSuccess())
		{
			updateAuditChangeData(evt.getData());
		}
		
		super.handleDataBack(new AuditChangeEvent(evt));
	}
	
	//统一处理服务器返回的变更
	private void updateAuditChangeData(Object _data)
	{
		if(_data==null || !(_data instanceof AsObject))
			return;
		
		AsObject data = (AsObject)_data;
		String clsName = data.getProperty("className").toString();
		if(clsName!=null)
		{
			if(changeField.getProperty(clsName)!=null)
			{
				ModelBase item=(ModelBase) changeField.getProperty(clsName);
				item.parseFromObject(data,null);

				if(!clsName.equals(XingCloud.getOwnerUser().getClassName()))
				{
					OwnedItem oi=(OwnedItem)item;
					((ItemsCollection) XingCloud.getOwnerUser().getProperty(oi.ownerProperty)).updateItemUID((OwnedItem)item);
				}
			}
		}
	}
}
