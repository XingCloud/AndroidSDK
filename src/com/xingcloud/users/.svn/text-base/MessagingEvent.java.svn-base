package com.xingcloud.users;

import com.xingcloud.event.XingCloudEvent;
import com.xingcloud.items.spec.AsObject;

public class MessagingEvent extends XingCloudEvent {

	public static String MESSAGING_EVENT = "MessagingEvent";
	//private int index;
	protected int code=0;
	protected Object data;
	
	protected String message;
	
	public MessagingEvent (AsObject data)
	{
		super(MESSAGING_EVENT,null);
		
		if(data!=null)
		{
			//this.index=Integer.parseInt(data.getProperty("index").toString());
			this.code=Integer.parseInt(data.getProperty("code").toString());
			this.message=data.getStringProperty("message");
			this.data = data.getProperty("data");
		}
	}
	
	public int getCode() {
		return code;
	}

	public Object getData() {
		return data;
	}
	
	public void setData(Object data) {
		this.data = data;
	}

	public String getMessage() {
		return message;
	}
	
	public boolean isSuccess() {
		return code == 200;
	}
}
