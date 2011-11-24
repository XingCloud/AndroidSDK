package com.xingcloud.users.auditchanges;

import com.xingcloud.users.MessagingEvent;

public class AuditChangeEvent extends MessagingEvent {
	
	public AuditChangeEvent(MessagingEvent evt)
	{
		super(null);
		code = evt.getCode();
		message = evt.getMessage();
		data = evt.getData();
	}
	
}
