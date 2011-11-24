package com.xingcloud.event;

import java.util.HashMap;

/**
 * 事件类，包含一个事件类型和事件监听器
 * 行云Android SDK内部采用了Observer模型的事件机制
 */
public class XingCloudEvent<E> {
	/**游戏初始化完毕。此时尚未进行资源加载操作，资源加载完成会分发RESOURCE_LOADED事件*/
	public static String ENGINE_INITED = "engine_inited";
	/**游戏初始化失败*/
	public static String ENGINE_INITERROR = "engine_initerror";
	/**游戏资源加载成功*/
	public static String RESOURCE_LOADED = "resource_loaded";	
	/**游戏资源加载失败*/
	public static String RESOURCE_LOAD_ERROR = "resource_load_error";	
	/**游戏加载完成 */
	public static String ENGINE_REDAY = "engine_ready";
	/**UserProfile的全部物品信息加载完成，或某组ItemCollection的信息加载完毕*/
	public static String ITEMS_LOADED = "items_loaded";

	/**物品加载失败*/
	public static String ITEMS_LOADED_ERROR="items_loaded_error";

	/**UserProfile登录失败*/
	public static String LOGIN_ERROR="login_error";

	/**UserProfile加载失败*/
	public static String PROFILE_LOAD_ERROR = "profile_load_error";
	/**UserProfile加载完毕*/
	public static String PROFILE_LOADED = "profile_loaded";

	/**服务器信息获取完毕**/
	public static String SERVICE_STATUS_READY="service_status_ready";
	/**服务器信息获取完毕**/
	public static String SERVICE_STATUS_ERROR="service_status_error";

	/**社交平台对接完成*/
	public static String SOCIAL_INITED="social_inited";

	/**用户注册成功*/
	public static String USER_REGISTERED="user_registered";

	private Object currentTarget;

	private HashMap<String, E> params;

	private EventDispatcher target;

	private String type;

	private Object data;

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

	/**
	 * 
	 * @param type 事件类型
	 */
	public XingCloudEvent(String type,Object currentTarget)
	{
		this.type = type;
		this.currentTarget = currentTarget;
		params = new HashMap<String, E>();
	}
	/**
	 * 
	 * @param type 事件类型
	 * @param content 事件内容
	 */
	public XingCloudEvent(String type,String content,Object currentTarget)
	{
		this.type = type;
		//this.content = content;
		this.currentTarget = currentTarget;
		params = new HashMap<String, E>();
	}

	public XingCloudEvent(String type,XingCloudEvent originEvent)
	{
		this.type = type;
		if(originEvent!=null)
		{
			this.currentTarget = originEvent.currentTarget;
			this.target = originEvent.target;
			this.params = originEvent.params;
			this.data = originEvent.data;
		}
	}

	/**
	 * 获取当前的目标
	 * @return 当前的目标
	 */
	public Object getCurrentTarget() {
		return currentTarget;
	}
	/**
	 * 获取事件分发者
	 * @return 事件分发者
	 */
	public EventDispatcher getTarget()
	{
		return target;
	}
	/**
	 * 获取事件类型标识字符串
	 * 
	 * @return 事件类型
	 */
	public String getType()
	{
		return type;
	}
	/**
	 * 设置当前的目标
	 * @param currentTarget
	 */
	public void setCurrentTarget(Object currentTarget) {
		this.currentTarget = currentTarget;
	}
	/**
	 * 设置事件分发者
	 * @param target 事件分发者
	 */
	public void setTarget(EventDispatcher target)
	{
		this.target = target;
	}

}