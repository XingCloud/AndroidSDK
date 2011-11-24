package com.xingcloud.users.actions;

import com.xingcloud.users.MessagingEvent;

public class ActionEvent extends MessagingEvent {
	
	public ActionEvent(MessagingEvent evt)
	{
		super(null);
		code = evt.getCode();
		message = evt.getMessage();
		data = evt.getData();
	}
}
