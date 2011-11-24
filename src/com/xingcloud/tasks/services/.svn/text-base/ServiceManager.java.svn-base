package com.xingcloud.tasks.services;

import java.util.ArrayList;

import com.xingcloud.core.Config;
import com.xingcloud.event.IEventListener;
import com.xingcloud.event.XingCloudEvent;
import com.xingcloud.items.spec.AsObject;
import com.xingcloud.tasks.base.ParallelTask;
import com.xingcloud.tasks.base.TaskEvent;
import com.xingcloud.tasks.net.Remoting;
import com.xingcloud.tasks.net.Remoting.RemotingMethod;
import com.xingcloud.tasks.net.RemotingResponse;

public class ServiceManager extends ParallelTask {
	private static ServiceManager _instance;

	private static ArrayList<Service> _servicesList=new ArrayList<Service>();
	private static AsObject _serviceStatusData;

	public ServiceManager()
	{
		super();
	}

	public void init()
	{
		Remoting status=new Remoting("status",new AsObject("{data:{lang:"+Config.languageType()+"}}"),RemotingMethod.POST, Config.getConfig("gateway").toString());
		status.addEventListener(TaskEvent.TASK_COMPLETE,onGetStatus);
		status.addEventListener(TaskEvent.TASK_ERROR,onStatusFail);
		status.execute();
	}

	public static ServiceManager instance()
	{
		if(_instance==null){
			_instance=new ServiceManager();
		}
		return _instance;
	}
	/**
	 * 增加一个服务到加载队列
	 * @param s 服务
	 */
	public void addService(Service s)
	{
		_servicesList.add(s);
	}

	private IEventListener onGetStatus= new IEventListener() 
	{
		public void performEvent(XingCloudEvent evt) {
			Remoting rem = (Remoting)(evt.getTarget());
			evt.getTarget().removeEventListener(TaskEvent.TASK_COMPLETE,onGetStatus);
			evt.getTarget().removeEventListener(TaskEvent.TASK_ERROR,onStatusFail);
			handleStatusSuccess(rem.response);
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
	private IEventListener onStatusFail= new IEventListener() 
	{
		public void performEvent(XingCloudEvent evt) {
			evt.getTarget().removeEventListener(TaskEvent.TASK_COMPLETE,onGetStatus);
			evt.getTarget().removeEventListener(TaskEvent.TASK_ERROR,onStatusFail);
			handelStatusFailure();
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
	private void handleStatusSuccess(RemotingResponse result)
	{
		if(!result.isSuccess())
		{
			_serviceStatusData=null;
		}
		else
		{
			_serviceStatusData = (AsObject) result.getData();
		}

		if(_serviceStatusData!=null)
		{
			Object server_time = _serviceStatusData.getProperty("server_time");
			if(server_time==null)
			{
				Config.setSystemTime(0);
			}
			else
			{
				Config.setSystemTime(Long.parseLong(server_time.toString())*1000);
			}

			send();

		}
		this.dispatchEvent(new XingCloudEvent(XingCloudEvent.SERVICE_STATUS_READY,this));
	}

	public void send(Service s)
	{
		s.getExecutor().execute();
	}

	public void send()
	{
		for(int serviceIndex = 0;serviceIndex<_servicesList.size();serviceIndex++)
		{
			Service item = _servicesList.get(serviceIndex);

			Object apiData = _serviceStatusData.getProperty(item.getApiName());

			if(apiData!=null)
			{
				AsObject apiasData = (AsObject)apiData;
				Object funcData = apiasData.getProperty(item.getFunctionName());
				if(funcData==null || !(funcData instanceof AsObject))
				{
					item.timestamp = "";
				}
				else
				{
					AsObject funcasData = (AsObject)funcData;
					item.timestamp = funcasData.getStringProperty("timestamp");
					item.md5 = funcasData.getStringProperty("md5");
					Config.setConfig(item.type+"md5", item.md5);
				}
			}
			if(item.sendable())
			{
				enqueue(item.getExecutor(),"");
			}
		}
	}

	private void handelStatusFailure()
	{
		this.dispatchEvent(new XingCloudEvent(XingCloudEvent.SERVICE_STATUS_ERROR,this));
	}

	protected void notifyError(String errorMsg)
	{
		_servicesList.clear();
		super.notifyError(errorMsg,null);
	}
	protected void complete()
	{
		_servicesList.clear();
		super.complete();
	}
}
