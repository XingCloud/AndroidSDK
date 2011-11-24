package com.xingcloud.resource;

import com.xingcloud.core.Config;
import com.xingcloud.event.IEventListener;
import com.xingcloud.event.XingCloudEvent;
import com.xingcloud.tasks.base.TaskEvent;
import com.xingcloud.tasks.net.AbstractLoader;
import com.xingcloud.tasks.net.AssetsLoader;
import com.xingcloud.tasks.net.CSSLoader;
import com.xingcloud.tasks.net.DatabaseLoader;
import com.xingcloud.tasks.net.LanguageLoader;
import com.xingcloud.tasks.net.RemotingResponse;

public class Resource {
	class ResourceLoadingEventListener implements IEventListener
	{
		private Resource r;
		private int type;
		public ResourceLoadingEventListener(Resource r,int type)
		{
			this.r = r;
			this.type = type;
		}

		public void performEvent(XingCloudEvent evt) {
			evt.getTarget().removeEventListener(TaskEvent.TASK_COMPLETE,this);
			evt.getTarget().removeEventListener(TaskEvent.TASK_ERROR,this);
			if(type==0)
			{
				r.onLoaded((TaskEvent)evt);
			}
			else
				r.onFailed((TaskEvent)evt);
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
	public static String BINARY_TYPE="binary";
	public static String DATABASE_TYPE="database";
	public static String FONT_TYPE="font";
	public static String LANGUAGE_TYPE="language";
	public static String LANGUAGESTYLE_TYPE="languageStyle";
	/**
	 * 除了以文件后缀来决定类型外，还有些特定的类型，决定他们用特定的加载器
	 * */
	public static String[] SpecialTypes={"database","language","languageStyle","font","binary"};
	private boolean _failed;
	private boolean _loaded;	

	private AbstractLoader _loader;

	private String _path;

	private int _priority=0;
	private RemotingResponse _response;
	
	/**
	 * 是否始终从服务器端加载，绕过缓存，对一些常更新的xml有用
	 * */
	public boolean alwaysUpdate;
	/**
	 * 设置资源类型，default会根据文件后缀自动判断
	 * language是语言文件
	 * database是物品数据文件
	 * font是字体文件，嵌入字体的swf
	 * */
	//[Inspectable(enumeration="default,language,languageStyle,database,font,binary")]
	public String type="default";

	/**
	 * 定义一个资源模型
	 * @param path 可以是文件地址
	 * @param type 资源类型，default,database,language,font之一
	 * */
	public Resource(String path/*=null*/,String type/*null*/,Boolean alwaysUpdate/*false*/)
	{
		if(path!=null)
			this._path=path;
		if(type!=null) 
			this.type=type;
		this.alwaysUpdate = alwaysUpdate;
	}
	/**
	 * 根据文件名后缀判断文件类型，考虑了?参数的情况
	 * */
	private AbstractLoader autoMatchLoader(String url)
	{
		AbstractLoader loader = null;

		url = url.replace("\\", "/");
		int index = url.lastIndexOf("/");
		if(index!=-1)
			url = url.substring(index+1, url.length());
		index = url.indexOf("?");
		if(index!=-1)
			url = url.substring(0,index);
		index = url.lastIndexOf(".");
		String type = "";
		if(index!=-1)
			type = url.substring(index+1,url.length());


		if(type.equals("swf") || type.equals("png") || type.equals("jpg") || type.equals("jpeg") || type.equals("gif"))
		{
			loader=new AssetsLoader(url);
		}
		else if(type.equals("xml"))
		{
			loader=new DatabaseLoader(url);
		}
		else if(type.equals("txt"))
		{
			loader=new DatabaseLoader(url);
		}
		else if(type.equals("css"))
		{
			loader=new CSSLoader(url);
		}
		else
		{
			throw new Error(url+" is a invalide file type");
		}

		return loader;
	}
	
	/**
	 * 获取已加载的数据(转换为字符数据)
	 * @return 字符数据
	 */
	public String getContent()
	{
		if(_response!=null)
			return _response.getContent();
		else
			return "";
	}
	
	/**
	 * 
	 * @return 是否加载失败
	 */
	public boolean getFailed()
	{
		return _failed;
	}

	/**
	 * 
	 * @return 是否已经成功加载
	 */
	public boolean getLoaded()
	{
		return _loaded;
	}
	
	/**
	 * 获取数据加载器
	 * @return 加载器 
	 */
	public AbstractLoader getLoader()
	{
		validatePath();
		if(_loader!=null)
			return _loader;
		if(type.equals("database")){
			_loader=new DatabaseLoader(getPath());
		}else if(type.equals("language")){
			_loader=new  LanguageLoader(getPath());
		}else if(type.equals("binary")){
			//_loader=new BinaryLoader(getPath());
		}else{
			_loader=autoMatchLoader(getPath());
		}
		_loader.addEventListener(TaskEvent.TASK_COMPLETE,new ResourceLoadingEventListener(this,1));
		_loader.addEventListener(TaskEvent.TASK_ERROR,new ResourceLoadingEventListener(this,0));
		return _loader;
	}
	
	/**
	 * 资源地址
	 * */
	public String getPath()
	{
		return _path;
	}

	/**
	 * 获取优先级
	 * @return 优先级序号
	 */
	public int getPriority()
	{
		return _priority;
	}
	/**
	 * 获取已加载的数据（原始格式）
	 * @return byte数据
	 */
	public byte[] getRawContent()
	{
		if(_response!=null)
			return _response.getRawData();
		else
			return null;
	}
	private void onFailed(TaskEvent E)
	{
		_failed=true;
	}
	private void onLoaded(TaskEvent e)
	{
		_loaded=true;
		_response = ((AbstractLoader)e.getTarget()).response;
	}		
	public void setPath(String s)
	{
		_path=s;
	}
	/**
	 * 设置优先级
	 * @param value
	 */
	public void setPriority(int value)
	{
		if(_priority==value)
			return;
		_priority=value;
		//to dispatch...
	}

	/**
	 * 获取全路径
	 * */
	private void validatePath()
	{
		//如果path已经是网络地址了，就不加webbase
		if(_path.indexOf("://")<0)
		{
			_path=Config.webbase()+_path;
		}
		if(this.alwaysUpdate)
			_path+="?r="+Math.random();

	}
}
