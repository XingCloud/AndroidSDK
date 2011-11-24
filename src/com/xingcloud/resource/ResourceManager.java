package com.xingcloud.resource;

import java.util.HashMap;

import android.util.Log;

import com.xingcloud.event.IEventListener;
import com.xingcloud.event.XingCloudEvent;
import com.xingcloud.tasks.base.ParallelTask;
import com.xingcloud.tasks.base.TaskEvent;
import com.xingcloud.utils.XingCloudLogger;

/**
 * 资源管理类
 * @author chuckzhang
 *
 */
public class ResourceManager extends ParallelTask {
	class ResourceLoadingEventListener implements IEventListener
	{
		IEventListener e;
		Resource r;
		public ResourceLoadingEventListener(IEventListener e,Resource r)
		{
			this.e = e;
			this.r = r;
		}

		public void performEvent(XingCloudEvent evt) {
			evt.getTarget().removeEventListener(TaskEvent.TASK_COMPLETE,this);
			evt.getTarget().removeEventListener(TaskEvent.TASK_ERROR,this);
			e.performEvent(new ResourceEvent(r));
		}

		@Override
		public void prePerformEvent(XingCloudEvent evt) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void postPerformEvent(XingCloudEvent evt) {
			// TODO Auto-generated method stub
			
		}
		
	}
	
	private static ResourceManager _instance;
	
	public static ResourceManager instance()
	{
		if(_instance==null){
			_instance=new ResourceManager();
		}
		return _instance;
	}
	
	private HashMap<String,Resource> _resources = new HashMap<String,Resource>();
	
	private int totalCount=0;
	
	public ResourceManager() {
		super();
	}
	
	/**
	 * 增加一个文件加载任务
	 * @param url
	 * @param type
	 * @return 文件加载任务
	 */
	public Resource addFile(String url,String type/*null*/)
	{

		Resource entry=getResource(url);
		if(entry!=null)
			return entry;
		entry=new Resource(url,type,false);
		this.addResource(entry);
		return entry;
	}
	/**
	 * 增加一个资源加载任务
	 * @param r
	 */
	public void addResource(Resource r)
	{
		if(r.getPath()==null){
			XingCloudLogger.log(XingCloudLogger.WARN,"ResourceManager->addEntry: The resource entry has no path!");
			return;				
		}
		if(getResource(r.getPath())!=null){
			XingCloudLogger.log(XingCloudLogger.WARN,"ResourceManager->addEntry: The resource entry with url: "+r.getPath()+" has existed!");
			return;
		}
		this._resources.put(r.getPath(), r);
		this.enqueue(r.getLoader(),"");
	}

	/*
	 * (non-Javadoc)
	 * @see com.xingcloud.tasks.base.SerialTask#complete()
	 */
	protected void complete()
	{
		_resources.clear();
		super.complete();
	}
	
	/**
	 * 获取某个路径的资源加载任务
	 * @param url
	 * @return 资源加载任务
	 */
	public Resource getResource(String url)
	{
		return _resources.get(url);
	}
	
	/**
	 * 单独加载一个文件
	 * @param url 文件地址
	 * @param type 数据类型可以根据文件后缀来自动判断，如果指定见Resource.SpecialTypes
	 * @param successHandler 成功回调，传参数EntryBase
	 * @param failHandler 失败回调，传参数EntryBase
	 * @param forceReLoad 如果文件已经加载，是否重新加载
	 * */
	public Resource load(String url,String type/*null*/,IEventListener successHandler/*null*/,IEventListener failHandler/*null*/,Boolean forceReLoad/*false*/)
	{

		Resource entry=getResource(url);
		if(entry!=null&&(!forceReLoad)){
			if(successHandler!=null) 
				successHandler.performEvent(new ResourceEvent(entry));
			return entry;
		}
		entry=addFile(url,type);
		
		if(successHandler!=null)
			entry.getLoader().addEventListener(TaskEvent.TASK_COMPLETE,new ResourceLoadingEventListener(successHandler,entry));
		if(failHandler!=null)
			entry.getLoader().addEventListener(TaskEvent.TASK_ERROR,new ResourceLoadingEventListener(failHandler,entry));
		//entry.loader.execute();
		this.execute();
		return entry;
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.xingcloud.tasks.base.Task#notifyError(java.lang.String)
	 */
	protected void notifyError(String errorMsg)
	{
		_resources.clear();
		super.notifyError(errorMsg,null);
	}

}
