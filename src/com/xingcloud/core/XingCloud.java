package com.xingcloud.core;

import java.lang.reflect.Constructor;
import java.util.ArrayList;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

import com.xingcloud.event.EventDispatcher;
import com.xingcloud.event.IEventListener;
import com.xingcloud.event.XingCloudEvent;
import com.xingcloud.items.spec.ItemSpecManager;
import com.xingcloud.resource.Resource;
import com.xingcloud.resource.ResourceManager;
import com.xingcloud.social.SocialContainer;
import com.xingcloud.tasks.base.SerialTask;
import com.xingcloud.tasks.base.TaskEvent;
import com.xingcloud.tasks.services.ItemsService;
import com.xingcloud.tasks.services.LanguageService;
import com.xingcloud.tasks.services.Service;
import com.xingcloud.tasks.services.ServiceManager;
import com.xingcloud.users.AbstractUserProfile;
import com.xingcloud.users.actions.ActionManager;
import com.xingcloud.users.auditchanges.AuditChangeManager;

/**
 * XingCloud核心类，用于初始化和启动XingCloud各种服务功能
 * */
public class XingCloud extends EventDispatcher {


	private static XingCloud _instance;
	/**
	 *是否自动加载物品
	 */
	public static boolean autoLoadItems=true;

	/**
	 * 是否自动检查资源文件的正确性
	 */
	public static boolean checkFileValidity = true;
	/**
	 * 是否启用缓存机制
	 */
	public static Boolean enableCache=true;

	/**
	 *是否自动登录
	 */
	public static boolean autoLogin=true;

	/**
	 *是否打开Auditchange模式
	 */
	public static boolean changeMode=true;
	public int appVersionCode=0;
	static String GDP_KEY = "XINGCLOUD_GDP_KEY";
	/**
	 *是否需要对封装的服务请求、登录、和auditchange交互使用安全验证
	 */
	public static boolean needAuth=true;
	/**
	 *当前玩家信息,是否采用覆盖UserProfile的方式，todo
	 * */
	protected static AbstractUserProfile ownerUser;
	/**
	 * 进度提示对话框内容
	 */
	public static String progressMsg = "Please wait for few seconds...";

	/*
	public static String SFS_ENABLE_CONFIG = "sfs_enabled";

	public static String SFS_XINGCLOUD_SERVICE_TOKEN = "service";
	public static String SFS_XINGCLOUD_ZONE = "Game" + "";
	 */

	/**
	 * 是否自动加载Item资源文件
	 */
	public static boolean itemServiceEnabled = true;
	/**
	 * 是否自动加载多语言资源文件
	 */
	public static boolean languageServiceEnabled = true;

	/**
	 * 获取XingCloud实例 
	 * @return XingCloud实例 
	 * 
	 */		
	public static XingCloud instance()
	{
		if(_instance==null)
		{
			_instance = new XingCloud();
			_instance.initConfigs();
		}			
		return _instance;
	}
	private Context _context;

	private ArrayList<Resource> _resources=new ArrayList<Resource>();

	private ArrayList<Service> _services=new ArrayList<Service>();


	private IEventListener loadFailCallback = null;


	private IEventListener loadOkCallback = null;

	public void reloadResource()
	{
		_resource_loaded = false;
		SerialTask task=new SerialTask();
		task.enqueue(ResourceManager.instance(),"");
		task.enqueue(ServiceManager.instance(),"");
		task.addEventListener(TaskEvent.TASK_COMPLETE,new IEventListener() {

			public void performEvent(XingCloudEvent evt) {
				evt.getTarget().removeEventListener(TaskEvent.TASK_COMPLETE,this);
				evt.getTarget().removeEventListener(TaskEvent.TASK_ERROR,this);
				XingCloud.instance().onResourceLoaded((TaskEvent)evt);
			}

			@Override
			public void prePerformEvent(XingCloudEvent evt) {
				// TODO Auto-generated method stub

			}

			@Override
			public void postPerformEvent(XingCloudEvent evt) {
				// TODO Auto-generated method stub

			}
		});
		task.addEventListener(TaskEvent.TASK_ERROR,new IEventListener() {

			public void performEvent(XingCloudEvent evt) {
				evt.getTarget().removeEventListener(TaskEvent.TASK_COMPLETE,this);
				evt.getTarget().removeEventListener(TaskEvent.TASK_ERROR,this);
				XingCloud.instance().onResourceLoadError((TaskEvent)evt);
			}

			@Override
			public void prePerformEvent(XingCloudEvent evt) {
				// TODO Auto-generated method stub

			}

			@Override
			public void postPerformEvent(XingCloudEvent evt) {
				// TODO Auto-generated method stub

			}
		});
		task.execute();
	}
	
	private IEventListener onServiceError=new IEventListener()
	{
		public void performEvent(XingCloudEvent e)
		{
			ServiceManager.instance().removeEventListener(XingCloudEvent.SERVICE_STATUS_READY, onServiceReady);
			ServiceManager.instance().removeEventListener(XingCloudEvent.SERVICE_STATUS_ERROR, onServiceError);
			dispatchEvent(new XingCloudEvent(XingCloudEvent.ENGINE_INITERROR,null));
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

	private IEventListener onServiceReady=new IEventListener()
	{
		public void performEvent(XingCloudEvent e)
		{
			ServiceManager.instance().removeEventListener(XingCloudEvent.SERVICE_STATUS_READY, onServiceReady);
			ServiceManager.instance().removeEventListener(XingCloudEvent.SERVICE_STATUS_ERROR, onServiceError);

			if(ownerUser==null)
			{
				try
				{
					setOwnerUser(createOwnerUser());
				}
				catch(Exception except)
				{
					except.printStackTrace();
					dispatchEvent(new XingCloudEvent(XingCloudEvent.ENGINE_INITERROR,null));
					return;
				}
			}

			dispatchEvent(new XingCloudEvent(XingCloudEvent.ENGINE_INITED,null));

			reloadResource();			
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

	private void checkEnginStatus()
	{
		if(_userinfo_loaded && _resource_loaded)
			XingCloud.instance().dispatchEvent(new XingCloudEvent(XingCloudEvent.ENGINE_REDAY,null));
	}

	IEventListener onUserStatusEvent=new IEventListener() {

		public void performEvent(XingCloudEvent evt) {
			ownerUser.removeEventListener(evt.getType(), this);

			if(evt.getType().equals(XingCloudEvent.ITEMS_LOADED))
				_userinfo_loaded = true;

			dispatchEvent(evt);
		}

		@Override
		public void prePerformEvent(XingCloudEvent evt) {
			// TODO Auto-generated method stub

		}

		@Override
		public void postPerformEvent(XingCloudEvent evt) {
			checkEnginStatus();
		}
	};

	/**
	 * 全局的进度提示对话框
	 */
	public ProgressDialog progressDialog;
	protected XingCloud()
	{
		if(_instance!=null)
			throw new Error("Use XingCloud.instance please!");
	}

	/**
	 * 添加一個资源
	 * @param src 资源路径
	 * @param type 资源类型，见Resource,默认为"default",自动根据后缀判断
	 * */
	public void addResource(String src,String type/*"default"*/)
	{
		Resource r=new Resource(src,type,true);
		if(_resources.indexOf(r)!=-1) return;
		_resources.add(r);
	}

	/**
	 *  获取AndroidMenifest.xml中设置的metadata
	 * @param metadata metadata名
	 * @return 如果没有获取到，会返回空字符串
	 */
	public String getAppMetadata(String metadata)
	{
		if(this._context==null)
			return "";

		ApplicationInfo ai;
		try {
			ai = this._context.getApplicationContext().getPackageManager().getApplicationInfo(this._context.getPackageName(),
					PackageManager.GET_META_DATA);
			if(ai!=null && ai.metaData!=null && ai.metaData.get(metadata) != null)
			{
				return ai.metaData.get(metadata).toString();
			}
		} catch (NameNotFoundException e) {
		}
		return "";
	}

	public String getActivityMetadata(String metadata)
	{
		if(this._context==null)
			return "";
		ActivityInfo ai = null;
		String gkey = "";
		try {
			ai = _context.getPackageManager().getActivityInfo(((Activity)_context).getComponentName(), PackageManager.GET_META_DATA);
			if(ai!=null && ai.metaData!=null && ai.metaData.get(metadata) != null)
			{
				gkey = ai.metaData.get(metadata).toString();  
			}

		} catch (NameNotFoundException e) {
		}

		return gkey;
	}

	/**
	 * 获取android程序的context
	 * @return context
	 */
	public Context getContext()
	{
		return _context.getApplicationContext();
	}

	public Context getActivity()
	{
		return _context;
	}

	/**
	 * 初始化XingCloud，获取必要参数，启动加载。
	 * 完成后派发ENGINE_INITED事件
	 * 失败后排放ENGINE_INITERROR事件
	 * @param context android程序的context
	 */
	public void init(Context context)
	{
		this._context = context;
		if(context==null)
			throw new Error("XingCloud->startUp : null Context!");

		if(Config.localGDP())
		{
			SocialContainer.instance().initContainer((Activity) context, true);
			Config.init(null);
		}
		else
		{
			SocialContainer.instance().initContainer((Activity) context, false);
			Config.init(null);
		}
		
		PackageManager manager = context.getPackageManager();
		try {  
		   PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
		   this.appVersionCode = info.versionCode;
		} catch (NameNotFoundException e) {
		}
		
		deinit();

		//初始化AuditChangeManager
		AuditChangeManager.instance().init(Config.AUDIT_CHANGE_SERVICE);
		//初始化AuditChangeManager
		ActionManager.instance().init(Config.ACTION_SERVICE);

		XingCloud.instance().preload();
	}

	public void deinit()
	{
		_resource_loaded = false;
		_userinfo_loaded = false;	

		setOwnerUser(null);

		AuditChangeManager.instance().clean();
		ActionManager.instance().clean();
		ModelBaseManager.instance().clean();
	}

	/**
	 * 初始化配置，方便本地调试
	 * */
	private void initConfigs()
	{
		Config.setConfig("webbase","");
	}

	public AbstractUserProfile createOwnerUser() throws Exception
	{
		try
		{
			Class userClass=Class.forName("model.user.UserProfile");//装载类.
			Constructor[] constructors=userClass.getConstructors();
			AbstractUserProfile prof=(AbstractUserProfile) constructors[0].newInstance(true);
			return prof;
		}
		catch(Exception except)
		{
			throw except;
		}
	}

	public void setOwnerUser(AbstractUserProfile prof)
	{
		_userinfo_loaded = false;

		if(ownerUser!=null)
		{
			ownerUser.removeEventListener(XingCloudEvent.PROFILE_LOADED, onUserStatusEvent); //是否载入了用户信息
			ownerUser.removeEventListener(XingCloudEvent.PROFILE_LOAD_ERROR, onUserStatusEvent); //是否载入了用户信息
			ownerUser.removeEventListener(XingCloudEvent.ITEMS_LOADED, onUserStatusEvent); //监听物品是否载入
			ownerUser.removeEventListener(XingCloudEvent.ITEMS_LOADED_ERROR, onUserStatusEvent);
		}

		ownerUser = prof;
		if(ownerUser!=null)
		{
			ownerUser.addEventListener(XingCloudEvent.PROFILE_LOADED, onUserStatusEvent); //是否载入了用户信息
			ownerUser.addEventListener(XingCloudEvent.PROFILE_LOAD_ERROR, onUserStatusEvent); //是否载入了用户信息
			ownerUser.addEventListener(XingCloudEvent.ITEMS_LOADED, onUserStatusEvent); //监听物品是否载入
			ownerUser.addEventListener(XingCloudEvent.ITEMS_LOADED_ERROR, onUserStatusEvent);
		}
	}

	public static AbstractUserProfile getOwnerUser() {
		return ownerUser;
	}

	boolean _resource_loaded = false;
	boolean _userinfo_loaded = false;

	protected void onResourceLoaded(TaskEvent e)
	{
		this.dispatchEvent(new XingCloudEvent(XingCloudEvent.RESOURCE_LOADED,null));
		_resource_loaded = true;
		ItemSpecManager.instance().init();

		checkEnginStatus();
	}

	protected void onResourceLoadError(TaskEvent e)
	{
		this.dispatchEvent(new XingCloudEvent(XingCloudEvent.RESOURCE_LOAD_ERROR,null));
	}

	/*
	public void onCreate(Context context) {
		CloudAnalytic.instance().onStart(context);
	}
	public void onFinish(Context context) {
		CloudAnalytic.instance().onFinish(context);
	}
	public void onResume(Context context)
	{
		CloudAnalytic.instance().onResume(context);
	}
	public void onPause(Context context)
	{
		CloudAnalytic.instance().onPause(context);
	}
	 */

	private void preload()
	{
		/*
		Object sfsGateWay = Config.getConfig("sfs_gateway");
		if(sfsGateWay!=null)
		{
			String[] sfsGateWayStr = sfsGateWay.toString().split(":");
			if(sfsGateWayStr.length!=2)
			{
				//TODO handle error
			}
			else
			{
				SFSManager.instance().init(sfsGateWayStr[0], Integer.parseInt(sfsGateWayStr[1]));
			}
		}
		 */

		for (int i=0;i<this._resources.size();i++)
		{
			ResourceManager.instance().addResource(_resources.get(i));
		}
		_resources.clear();

		if(itemServiceEnabled)
			ServiceManager.instance().addService(new ItemsService());
		if(languageServiceEnabled)
			ServiceManager.instance().addService(new LanguageService());

		ServiceManager.instance().addEventListener(XingCloudEvent.SERVICE_STATUS_READY, onServiceReady);
		ServiceManager.instance().addEventListener(XingCloudEvent.SERVICE_STATUS_ERROR, onServiceError);
		ServiceManager.instance().init();
	}
	/**
	 * 设置行云资源加载任务
	 */
	public void setResources(ArrayList<Resource> r)
	{
		_resources=r;
	}

	/**
	 * 设置行云服务器任务
	 */
	public void setServices(ArrayList<Service> s)
	{
		_services=s;
	}

}
