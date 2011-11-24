package com.xingcloud.users;

import com.xingcloud.core.Reflection;
import com.xingcloud.event.IEventListener;
import com.xingcloud.items.spec.AsObject;

public class RemotingMessage {
	
	public static int nextID=-1;
	public IEventListener _onFail;
	public IEventListener _onSuccess;
	protected int id;
	protected String name="";
	
	protected AsObject params;
	/**
	 * 此Message是否发送出去
	 */
	public boolean sended=false;
	
	public RemotingMessage(String name,AsObject params,IEventListener onSuccess, IEventListener onFail)
	{
		this.name = name;
		this.params = params;
		this._onSuccess = onSuccess;
		this._onFail = onFail;
		this.id = nextID++;
	}
	
	public void appendMessage(AsObject msg)
	{
	}
	
	public int getId()
	{
		return id;
	}
	
	public String getName() {
		if(name==null || name.equals(""))
			return Reflection.tinyClassName(this);
		else
			return name;
	}
	
	public AsObject getParams() {
		return this.params;
	}

	public void handleDataBack(MessagingEvent evt)
	{
		if(evt==null || !(evt.isSuccess()))
		{
			if(_onFail!=null)
				_onFail.performEvent(evt);
		}
		else
		{
			if(_onSuccess!=null)
				_onSuccess.performEvent(evt);
		}
	}
	
	public void setName(String _name) {
		this.name = _name;
	}
}
