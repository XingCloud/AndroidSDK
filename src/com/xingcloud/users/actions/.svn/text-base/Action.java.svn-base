package com.xingcloud.users.actions;

import com.xingcloud.event.IEventListener;
import com.xingcloud.items.spec.AsObject;
import com.xingcloud.users.MessagingEvent;
import com.xingcloud.users.RemotingMessage;

public class Action extends RemotingMessage {

	/**
	 * 定义一个一般的action
	 * @param params 传到后台的参数
	 * @param onSuccess 成功后的回调函数
	 * @param onFail 失败后的回调函数
	 * */
	public Action(AsObject params,IEventListener onSuccess, IEventListener onFail) {
		super(null,params,onSuccess,onFail);
	}
	
	/**
	 * 定义一个一般的action
	 * @param name action的名字
	 * @param params 传到后台的参数
	 * @param onSuccess 成功后的回调函数
	 * @param onFail 失败后的回调函数
	 */
	public Action(String name,AsObject params,IEventListener onSuccess, IEventListener onFail ) {
		super(name,params,onSuccess,onFail);
	}

	/*
	 * (non-Javadoc)
	 * @see com.xingcloud.users.RemotingMessage#appendMessage(com.xingcloud.items.spec.AsObject)
	 */
	public void appendMessage(AsObject msg)
	{
		msg.setProperty("name", this.getName());
		msg.setProperty("params", params);
	}

	/**
	 * 加入action执行队列，等待下次一起打包执行
	 */
	public void execute()
	{
		ActionManager.instance().addMessage(this);
	}

	/**
	 * 执行此Action
	 * @param immediately 是否立即执行此action。如果为false，则会加入action执行队列，等待下次一起打包执行
	 */
	public void execute(Boolean immediately)
	{
		ActionManager.instance().addMessage(this);
		if(immediately)
			ActionManager.instance().send();
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.xingcloud.users.RemotingMessage#handleDataBack(com.xingcloud.users.MessagingEvent)
	 */
	public void handleDataBack(MessagingEvent evt)
	{
		super.handleDataBack(new ActionEvent(evt));
	}
}
