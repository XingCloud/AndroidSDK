package com.xingcloud.core;

import java.util.HashMap;
import java.util.Iterator;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

import android.util.Log;

import com.xingcloud.items.spec.AsObject;
import com.xingcloud.tasks.net.Remoting;
import com.xingcloud.utils.XingCloudLogger;

/**
 *配合GDP发布，约定几个参数名：
 * 1.webbase：swf主文件的存放目录，这样我们在服务器上时，会给资源加载路径统一加上这个参数，XingCloud会初始化为"",以方便本地调试
 * 2.gateway: 后台gateway地址
 * 3.help:    游戏帮助页面地址
 * 地址会以"/"结束
 * */	
public class Config {

	private static long _serverTime = 0;
	/**
	 * 进行时间同步时的值,todo,在请求服务器时记得同步一下
	 */
	private static long _syncTime=0;
	/**
	 * action服务
	 * */
	public static String ACTION_SERVICE="action.action.execute";

	/**
	 * auditchange服务 
	 */		
	public static String AUDIT_CHANGE_SERVICE="change.change.apply";
	/**
	 * 获取物品数据服务
	 */
	public static String ITEMSDB_SERVICE="item.itemSpec.xml";
	/**
	 * 获取用户拥有的物品数据服务
	 */
	public static String ITEMSLOAD_SERVICE="user.user.getItems";
	/**
	 * 多语言服务
	 */
	public static String LANGUAGE_SERVICE="locale.text.getAll";
	private static HashMap<String,Object> local = new HashMap<String,Object>();
	/**
	 * 自主用户登录服务
	 */
	public static String LOGIN_SERVICE="user.user.login";
	/**
	 * 自主用户注册服务
	 */
	public static String REGISTER_SERVICE="user.user.register";
	/**
	 * 平台用户登录服务
	 */
	public static String PLATFORM_LOGIN_SERVICE="user.user.platformLogin";
	/**
	 * 平台用户注册服务
	 */
	public static String PLARFOMR_REGISTER_SERVICE="user.user.platformRegister";
	/**
	 * 对用户账户绑定平台
	 */
	public static String BIND_PLATFORM_SERVICE="user.user.bindPlatform";
	/**
	 * 加载用户数据服务
	 */
	public static String USERPROFILE_SERVICE="user.user.get";
	/**
	 * 要传到后台的必备参数,xa_target/plateform_sig_api_key都是后台约定
	 * */
	public static AsObject appInfo()
	{
		//ABTest参数
		String abtest=Config.getStringConfig("xa_target");
		
		//平台应用id
		String platformAppId=Config.getStringConfig("platefrom_app_id");
		
		//平台用户uid
		String platformUserId=Config.getStringConfig("sig_user");
		
		//用户uid
		String gameUserId="";
		if(XingCloud.ownerUser!=null)
			gameUserId = XingCloud.ownerUser.getUid();
		
		String lang = Config.getStringConfig("lang");
		
		AsObject result = new AsObject();
		result.setProperty("platformAppId", platformAppId);
		result.setProperty("platformUserId", platformUserId);
		result.setProperty("gameUserId", gameUserId);
		result.setProperty("abtest", abtest);
		result.setProperty("lang", lang);
		
		return result;
	}
	
	public static String extraParams()
	{
		return Config.getStringConfig("extraParams");
	}
	
	/**
	 * file服务调用的gateway
	 * 
	 */
	public static String fileGateway()
	{
		return Config.getConfig("gateway")+"/file";
	}
	/**
	 *获取配置 
	 * @param name 参数名
	 */	
	public static Object getConfig(String name) {
		if (name==null || name.trim().length()==0)
			return null;
		else
			return Config.local.get(name);
	}
	/**
	 *获取配置 
	 * @param name 参数名
	 */	
	public static String getStringConfig(String name) {
		Object value = getConfig(name);
		if(value==null)
			return "";
		else
			return value.toString();
	}
	/**
	 *从服务器获得的，后台标准的系统时间,是从1970年1月1日0时0秒至今的毫秒数
	 */		
	public static long getSystemTime()
	{
		return System.currentTimeMillis() -_syncTime + _serverTime;
	}
	/**
	 * 帮助页面地址
	 * */
	public static String help()
	{
		return (String)Config.getConfig("help");
	}
	
	/**
	 * 进行基本配置的初始化，包括安全验证参数
	 * @param config 游戏基本配置文件
	 */
	
	public static void init(JSONObject config)
	{
		Config.parseFromJSON(config);
		do_init();
	}
	
	private static void do_init()
	{
		//初始化gateway
		String gateway=(String)getConfig("gateway");
		if(gateway==null) 
			XingCloudLogger.log(XingCloudLogger.ERROR, "Global->init:If you want to use backend,please use <ConfigManager.setConfig('gateway','your gateway')> firstly!");
	}

	/****************************************以下是配合行云使用的一些参数及约定****************************************/

	/**
	 * 当前语言版本类型，如"cn","en"
	 * */
	public static String languageType()
	{
		return (String)getConfig("lang");
	}
	/**
	 * 将xml形式的配置信息记录进来 <config> <config1>value1</config1>
	 * <config2>value2</config2> </config>
	 * */
	public static void parseFromXML(NodeList xmlConfig) {
		if(xmlConfig==null)
			return ;
		
		int length = xmlConfig.getLength();
		for(int i=0;i<length;i++)
		{
			Node n = xmlConfig.item(i);
			if(n.getNodeType()!=Node.ELEMENT_NODE)
				continue;
			
			Config.setConfig(n.getNodeName(), ((Text)n).getNodeValue());
		}
	}
	/**
	 * 将xml形式的配置信息记录进来 <config> <config1>value1</config1>
	 * <config2>value2</config2> </config>
	 * */
	public static void parseFromJSON(JSONObject config) {
		if(config==null)
		{
			Config.local.put("platefrom_app_id", XingCloud.instance().getActivityMetadata("XINGCLOUD_GAME_APPID"));
			Config.local.put("xa_target", "4125");
			return ;
		}
		
		Iterator it = config.keys();
		while(it.hasNext())
		{
			String key = (String)it.next();
			Object value;
			try {
				value = config.get(key);
				
				Config.setConfig(key, value.toString());
				
			} catch (JSONException e) {
				continue;
			}
		}

	}
	/**
	 * 当前社交平台的代号
	 * */
	public static String platefrom_app_id()
	{
		return (String)Config.getConfig("platefrom_app_id");
	}

	/**
	 * rest调用gateway地址
	 * */
	public static String restGateway()
	{
		return Config.getConfig("gateway")+"/rest";
	}
	
	/**
	 *设置参数 
	 * @param name 参数名
	 * @param value 参数值
	 * 
	 */		
	public static void setConfig(String name, Object value) {
		Config.local.put(name, value);
		
		if(name.equals("gateway"))
		{
			Remoting.defaultGateway = Config.restGateway();
		}
	}

	/**
	 * 设置服务器时间 
	 * @param time 标准的系统时间,是从1970年1月1日0时0秒至今的毫秒数
	 */
	public static void setSystemTime(long time)
	{
		_serverTime = time;
		_syncTime = System.currentTimeMillis();
	}
	
	public static boolean localGDP()
	{
		Object local = Config.getConfig("localGDP");
		if(local==null || local.toString().equals("true"))
			return true;
		else
			return false;
	}

	//public static String TUTORIAL_GET_SERVICE="tutorial.tutorial.get";

	//public static String TUTORIAL_COMPLETE_SERVICE="tutorial.tutorial.complete";

	//public static String TUTORIAL_STEP_SERVICE="tutorial.tutorial.step";

	/**
	 * 用户在社交平台上的uid
	 * */
	public static String sig_user()
	{
		return (String)Config.getConfig("sig_user");
	}

	//public static String STYLE_SERVICE="locale.style.get";

	//public static String FONTS_SERVICE="locale.font.get";

	/**
	 * 游戏放置的根目录
	 * */
	public static String webbase()
	{
		String _webbase= (String)Config.getConfig("webbase");
		if((_webbase.length()>0)&&(_webbase.lastIndexOf("/")!=0))
			_webbase+="/";
		return _webbase;
	}


}
