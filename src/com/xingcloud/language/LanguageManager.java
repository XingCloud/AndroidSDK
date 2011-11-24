package com.xingcloud.language;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Locale;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import android.content.res.Configuration;
import android.util.Log;

import com.xingcloud.core.Config;
import com.xingcloud.core.XingCloud;
import com.xingcloud.event.EventDispatcher;
import com.xingcloud.event.IEventListener;
import com.xingcloud.event.XingCloudEvent;
import com.xingcloud.tasks.services.LanguageService;
import com.xingcloud.utils.XingCloudLogger;

public class LanguageManager extends EventDispatcher{

	private static LanguageManager _instance;
	private static Element languageSource;

	/**
	 * 获取语言文件数据源
	 * @return 语言数据源
	 */
	public static Element getLanguageSource() {
		return languageSource;
	}
	/**
	 * 语言文件可以用value，也可以用text属性，优先后者
	 * TEMPLATE
		<?xml version="1.0" encoding="utf-8"?>
		<language type="cn">
		   <string name="dialog.title">错误提示</string>
		   <string name="dialog.message"><![CDATA[<font size='16'><b>{0}</b></font><font size='12'>(Lv{1})</font>]]></string>
		   <string name="panel.title">你要花{0}金币购买{1}个吗？</string>
		   <string name="panel.okButton">确定</string>
		</language>
	 * @param _key 获取key名字对应的语言字符串,以"."分割开的关键字（参照上面的模板,"panel.title"获取第二个title，如果只是"title"，将返回第一个）
	 * @param replacement 用于替换文本中{0},{1}...字样的字符串
	 * @return 多语言内容
	 * */
	public static String getText(String _key,ArrayList<String> replacement)
	{
		String result="undefined";

		if(languageSource==null)
		{
			XingCloudLogger.log(XingCloudLogger.ERROR,"LanguageManager->getText : Language resource is not loaded!");
			return result;
			//throw new Error("Language resource is not loaded!");
		}			

		if(_key==null || _key.trim().length()==0)
			return result;
		NodeList nl=languageSource.getChildNodes();
		int num=nl.getLength();
		int i=0;
		while(i<num)
		{
			Node langNode = nl.item(i);
			if(langNode==null)
			{
				i++;
				continue;
			}
			NamedNodeMap langNodeNames = langNode.getAttributes();
			if(langNodeNames==null)
			{
				i++;
				continue;
			}
			Node langNamedNode = langNodeNames.getNamedItem("name");
			if(langNamedNode==null)
			{
				i++;
				continue;
			}
			String label=langNamedNode.getNodeValue();
			if(label.equals(_key))
			{
				//result=langNode.getTextContent();
				//Text text = (Text) langNode;
				//result = text.getNodeValue();
				result = langNode.getFirstChild().getNodeValue();

				break;
			}
			i++;
		}
		if(replacement!=null)
		{
			int length = replacement.size();
			if(replacement==null || length==0)
			{
				return result;				
			}
			for(int j=0;j<length;j++)
			{
				String str = replacement.get(j);
				result = result.replace("{"+j+"}", str);
			}
		}
		return result;

	}

	public static LanguageManager instance()
	{
		if(_instance==null){
			_instance=new LanguageManager();
		}
		return _instance;
	}

	/**
	 * 设置预约文件数据源
	 * @param languageSource 语言数据源
	 */
	public static void parse(String language) {

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		try
		{
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document dom = builder.parse(new InputSource(new StringReader(language)));
			Element root = dom.getDocumentElement();
			LanguageManager.languageSource = root;
			LanguageManager.instance().addUsedLanguageTypes(LanguageManager.instance().getLanguageType(root));
		}
		catch (Exception e) {
			XingCloudLogger.log(XingCloudLogger.WARN,"LanguageManager->parse : illegal language content!");
		}
	}

	private ArrayList<String> languageTypes = new ArrayList<String>();

	/**
	 * 语言管理器，用于多语言文本的管理
	 */
	public LanguageManager() {
		super();
	}

	private void addUsedLanguageTypes(String type)
	{
		if(!languageTypes.contains(type))
		{
			languageTypes.add(type);
		}
	}

	/**
	 * 获取下载的语言的类型
	 * @return
	 */
	private String getLanguageType(Element node)
	{
		String undefText="undefined";

		if(node==null)
		{
			XingCloudLogger.log(XingCloudLogger.ERROR,"LanguageManager->getText : Language resource is not loaded!");
			return undefText;
		}			
		String result="";

		if(node.getNodeName().toLowerCase().compareTo("resources") ==0)
		{
			result = node.getAttribute("type");

			return result;
		}
		NodeList nl=node.getChildNodes();
		int num=nl.getLength();
		int i=0;
		while(i<num)
		{
			i++;
			Node langNode = nl.item(i);
			if(langNode==null)
			{
				continue;
			}
			if(langNode.getNodeName().toLowerCase().compareTo("resources") ==0)
			{
				NamedNodeMap langNodeNames = langNode.getAttributes();
				if(langNodeNames==null)
				{
					continue;
				}
				Node langNamedNode = langNodeNames.getNamedItem("type");
				if(langNamedNode==null)
				{
					continue;
				}
				result =langNamedNode.getNodeValue();
				break;
			}
		}

		if(result.equals(undefText))
		{
			throw new Error("LanguageManager-->getLanguageType : Invalid language type. Please check lang.xml file.");
		}

		return result;
	}
	
	private String originType="";

	public void loadLanguage(String type)
	{
		originType = Config.getStringConfig("lang");
		Config.setConfig("lang", type);
		LanguageService ls = new LanguageService(null,new IEventListener(){

			/*
			 * (non-Javadoc)
			 * @see com.xingcloud.event.IEventListener#prePerformEvent(com.xingcloud.event.XingCloudEvent)
			 */
			public void prePerformEvent(XingCloudEvent evt) {
			}

			/*
			 * (non-Javadoc)
			 * @see com.xingcloud.event.IEventListener#performEvent(com.xingcloud.event.XingCloudEvent)
			 */
			public void performEvent(XingCloudEvent evt) {
				Config.setConfig("lang", originType);
			}

			/*
			 * (non-Javadoc)
			 * @see com.xingcloud.event.IEventListener#postPerformEvent(com.xingcloud.event.XingCloudEvent)
			 */
			public void postPerformEvent(XingCloudEvent evt) {
			}
			
		});
		ls.execute();
	}

	/**
	 * 
	 * 
	 * 设置游戏应用的默认语言，英语
	 */
	public void setDefaultLanguage(/*default*/)
	{
		try
		{
			Locale locale = new Locale(Language.ENGLISH);
			Locale.setDefault(locale);
			Configuration config = new Configuration();
			config.locale = locale;
			//更新应用程序的配置信息
			XingCloud.instance().getContext().getResources().updateConfiguration(config, null);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	/**
	 * 
	 * @param lang
	 * 设置游戏应用的语言，传入参数的为：en,zh-cn,tw-cn,jp,de等
	 */
	public void setLanguage(String lang)
	{
		if(lang == null || lang.trim().length() <=0)
		{
			throw new Error("Language type is not setted! please set as en,zh-cn,tw-cn,jp,de etc.");
		}
		if(lang.toLowerCase().trim().compareTo("cn") == 0)
		{
			lang = "zh-cn";
		}
		try
		{
			Locale locale = new Locale(lang.toLowerCase());
			Locale.setDefault(locale);
			Configuration config = new Configuration();
			config.locale = locale;
			//更新应用程序的配置信息
			XingCloud.instance().getContext().getResources().updateConfiguration(config, null);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}
