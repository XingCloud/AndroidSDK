package com.xingcloud.tasks.services;

import com.xingcloud.event.IEventListener;
import com.xingcloud.event.XingCloudEvent;
import com.xingcloud.items.spec.AsObject;
import com.xingcloud.tasks.base.TaskEvent;
import com.xingcloud.tasks.net.Remoting;
import com.xingcloud.tasks.net.Remoting.RemotingMethod;

/**
 * 服务
 * */
public class Service {
	
	public static String FILE="file";
	public static String ITEMS="item";
	public static String LANGUAGE="lang";
	public static String PLATFORM_LOGIN="platform_login";
	public static String USER_LOGIN="user_login";
	public static String USER_REGISTER="user_register";
	public static String BIND_PLATFORM="bind_platform";
	public static String USER_GET_PROFILE="user_get_profile";
	public static String CUSTOM_SERVICE="custom_service";
	
	/**
	 * 调用服务的方式，见Remoting，默认为rest_get
	 * */
	public RemotingMethod method=RemotingMethod.POST;
	/**
	 * 时间戳，用来进行版本控制
	 */
	public String timestamp="";
	/**
	 * 数据内容md5，用以完整性检测
	 */
	public String md5="";

	/**
	 * 设置服务类型
	 * language是语言文件
	 * database是物品数据文件
	 * */
	public String type;


	protected IEventListener onFail;

	protected IEventListener onSuccess;

	protected IEventListener onExecuted = new IEventListener() {

		@Override
		public void performEvent(XingCloudEvent evt) {
			Remoting rem = (Remoting)(evt.getTarget());
			rem.removeEventListener(TaskEvent.TASK_COMPLETE,onExecuted);
			rem.removeEventListener(TaskEvent.TASK_ERROR,onExecutedError);
			try{
				handleSuccess(evt);
				
			} catch(Exception e)
			{
				handleFail(evt);
			}
		}

		@Override
		public void prePerformEvent(XingCloudEvent evt) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void postPerformEvent(XingCloudEvent evt) {
			// TODO Auto-generated method stub
			
		}
	};

	protected IEventListener onExecutedError = new IEventListener() {

		@Override
		public void performEvent(XingCloudEvent evt) {
			Remoting rem = (Remoting)(evt.getTarget());
			rem.removeEventListener(TaskEvent.TASK_COMPLETE,onExecuted);
			rem.removeEventListener(TaskEvent.TASK_ERROR,onExecutedError);
			handleFail(evt);
		}

		@Override
		public void prePerformEvent(XingCloudEvent evt) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void postPerformEvent(XingCloudEvent evt) {
			// TODO Auto-generated method stub
			
		}
	};
	
	public boolean sendable()
	{
		return true;
	}

	public Service(String type,IEventListener onSuccess,IEventListener onFail,RemotingMethod method)
	{
		super();
		this.type = type;
		this.onSuccess = onSuccess;
		this.onFail = onFail;
		this.method = method;
	}

	public Service(String type,RemotingMethod method)
	{
		super();
		this.type = type;
		this.method = method;
	}

	public void execute()
	{
		this.getExecutor().execute();
	}
	
	protected String command="";
	
	public String getApiName()
	{
		return command.substring(0,command.lastIndexOf("."));
	}

	public String getFunctionName()
	{
		return command.substring(command.lastIndexOf(".")+1);
	}
	
	protected AsObject params = new AsObject();

	public Remoting getExecutor()
	{
		Remoting serviceExecutor=new Remoting(command,params,method,true);
		serviceExecutor.addEventListener(TaskEvent.TASK_COMPLETE,onExecuted);
		serviceExecutor.addEventListener(TaskEvent.TASK_ERROR,onExecutedError);

		return serviceExecutor;
	}
	protected void handleSuccess(XingCloudEvent evt)
	{
		if(onSuccess!=null)
			onSuccess.performEvent(evt);
		onSuccess = null;
	}
	protected void handleFail(XingCloudEvent evt)
	{
		if(onFail!=null) 
			onFail.performEvent(evt);
		onFail = null;
	}
}
