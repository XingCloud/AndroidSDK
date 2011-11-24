package com.xingcloud.items;


import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import android.util.Log;

import com.xingcloud.core.XingCloud;
import com.xingcloud.items.spec.AsObject;
import com.xingcloud.items.spec.ItemBase;
import com.xingcloud.items.spec.ItemSpecManager;
import com.xingcloud.reader.XMLParser;
import com.xingcloud.utils.DbAssitant;
import com.xingcloud.utils.XingCloudLogger;

/**
 * items database 解析器
 * */
public class ItemsParser {

	/**
	 * 节点只要包含group字符，我们视为group类型，如果没有找到对应类，用默认的ItemGroup，
	 * 节点只要包含item字符，我们视为item类型，如果没有找到对应类，用默认的ItemSpec
	 * */
	public static ItemBase getModel(String attrs)
	{
		
		String name = null;
		String clsName=name;
		//modified by何松 2011530
		String[] atts = attrs.split(DbAssitant.dbItemAttributeSplit);//,
		for(int i=0;i< atts.length;i++)
		{
			if(atts[i].contains("name="))
			{
				name = atts[i].substring(atts[i].indexOf("=")+1, atts[i].length());
			}
			if(atts[i].contains("class="))
			{
				clsName = atts[i].substring(atts[i].indexOf("=")+1, atts[i].length());
			}
		}
		atts = null;
		List<String> pkgList = ItemSpecManager.instance().getPackages();
		Class<?> cls=null;
		for(String pkg:pkgList)
		{
			try {
				cls = Class.forName(pkg+"."+clsName);
				if(cls!=null)
					break;
			} catch (ClassNotFoundException e) {
				continue;
			}
		}

		if(cls==null)
		{
			try {
				if(name.trim().toLowerCase().equals("group"))
				{
					cls = Class.forName("com.xingcloud.items.spec.ItemGroup");
				}
				else if(name.trim().toLowerCase().contains("itemspec"))
				{
					cls = Class.forName("com.xingcloud.items.spec.ItemSpec");
				}
				else
				{
					throw new Error("The class "+clsName+" is not defined!");
				}
			}
			catch (ClassNotFoundException e) {
				throw new Error("The xingcloud item class "+name+" is not defined!");
			}
		}

		if(cls!=null)
		{
			try {
				return (ItemBase)cls.newInstance();
			} catch (Exception e) {
			}
		}

		return null;
	}
	
	/**
	 * 从xml描述中解析	
	 * @param source xml原始数据
	 * 
	 */
	public static void parse(String source)
	{
		try
		{
			if(XingCloud.instance().getContext()==null)
			{
				XingCloudLogger.log(XingCloudLogger.ERROR,"ItemsParser->parse : Null context in XingCloud!");
				return;
			}
			
			InputStream stream = new ByteArrayInputStream(source.getBytes());
			XMLParser reder = new XMLParser();
			reder.parse(stream);
			stream.close();
			stream = null;
			reder = null;
		
		} catch (Exception e) {
			XingCloudLogger.log(XingCloudLogger.ERROR,"ItemsParser->parse : Invalid Items Format");
		}
	}

	protected static void parseProperties(AsObject item,Node node)
	{
		if(item==null || node==null)
			return;

		NamedNodeMap nm = node.getAttributes();
		if(nm==null)
			return;
		int length = nm.getLength();
		for(int i=0;i<length;i++)
		{
			Node n = nm.item(i);
			parseProperty(item,n);
		}

	}

	protected static void parseProperty(AsObject item,Node node)
	{
		String key = node.getNodeName();
		String val = node.getNodeValue();
		item.setProperty(key, val);
	}
	
	
}
