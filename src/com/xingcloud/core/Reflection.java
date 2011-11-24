package com.xingcloud.core;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Vector;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.xingcloud.items.owned.OwnedItem;
import com.xingcloud.items.spec.AsObject;
import com.xingcloud.utils.XingCloudLogger;

/**
 * 反射工具类
 * @author chuckzhang
 *
 */
public class Reflection {
	/**
	 * 将source的所有属性复制到target上
	 * @param source 要复制的源头
	 * @param target 要赋值的对象
	 * @param excluded 不希望复制的字段,如["id","name"]
	 * */
	public static  void cloneProperties(AsObject source,AsObject target,ArrayList<String> excluded)
	{
		try {
			Field[] fs = source.getClass().getFields();
			int length = fs.length;
			for(int i=0;i<length;i++)
			{
				Field f = fs[i];
				if(f.getName().equals("properties"))
					continue;

				boolean originAccess = f.isAccessible();
				f.setAccessible(true);
				if(excluded==null || !(excluded.contains(f.getName())))
				{
					Reflection.setProperty(target, f.getName(), f.get(source));
				}
				f.setAccessible(originAccess);
			}
			Iterator it = source.properties.entrySet().iterator();
			while(it.hasNext())
			{
				Entry ent = (Entry) it.next();
				String key = (String) ent.getKey();
				Object value = ent.getValue();
				if(excluded==null || !(excluded.contains(key)))
					Reflection.setProperty(target,key,value);
			}
		} catch(Exception e) {

		}
	}

	/**
	 * 从AsObject中获取指定名字的属性
	 * 如果该属性为getter/setter方式进行存储，则从getter中获取
	 * 如果该属性存属于AsObject的动态属性对列中，则从列表中获取
	 * 否则返回null
	 * 
	 * @param obj AsObject实例
	 * @param propName 希望获取的属性名
	 * @return 属性内容
	 */
	public static Object getProperty(AsObject obj,String propName)
	{
		try {
			Field f = obj.getClass().getField(propName);
			boolean originAccess = f.isAccessible();
			f.setAccessible(true);
			Object result = f.get(obj);
			f.setAccessible(originAccess);
			return result;
		} catch (Exception e) {
			try {
				Method m_get = obj.getClass().getMethod("get"+toCapitalString(propName));
				return m_get.invoke(obj);
			} catch (Exception e2) {

			}

			if(obj.properties.containsKey(propName))
				return obj.properties.get(propName);
		}
		return null;
	}
	
	private static Object parseMapProperty(JSONObject propValue,Class propCls)
	{
		if(propValue==null || propCls==null)
			return new HashMap();
		
		String propCompoStr = propCls.getCanonicalName();
		
		Map newMap = null;
		if(propCompoStr.equals("java.util.HashMap"))
		{
			newMap = new HashMap();
		}
		else if(propCompoStr.equals("java.util.Map"))
		{
			newMap = Collections.synchronizedMap(new HashMap());
		}
		
		Iterator it = propValue.keys();
		while(it.hasNext())
		{
			String key = (String)it.next();
			Object value;
			try {
				value = propValue.get(key);
			} catch (JSONException e) {
				continue;
			}
			newMap.put(key, value);
		}
		
		return newMap;
	}

	private static Object parseArrayProperty(JSONArray propValueArr,Class propCls)
	{
		if(propValueArr==null || propValueArr.length()==0 || propCls==null)
			return new AsObject();


		int propJsonLength = propValueArr.length();
		
		try {
			propValueArr.get(propJsonLength-1);
		} catch (JSONException e) {
			propJsonLength -= 1;
		}
		
		String propCompoStr = propCls.getCanonicalName();
		
		Object newArr = null;
		if(propCompoStr.equals("java.util.ArrayList"))
		{
			newArr = new ArrayList();
		}
		else if(propCompoStr.equals("java.util.Vector"))
		{
			newArr = new Vector();
		}
		else
		{
			newArr = Array.newInstance(propCls, propJsonLength);
		}
		
		for(int propJsonI=0;propJsonI<propJsonLength;propJsonI++)
		{
			Object value = null;	
			try {
				value = propValueArr.get(propJsonI);
			} catch (JSONException e) {
				value = null;
			}
			
			if(value==null || value==JSONObject.NULL)
			{
				Array.set(newArr, propJsonI,null);
				continue;
			}

			if(value instanceof JSONArray)
			{
				if(propCls.isArray())
				{
					Array.set(newArr, propJsonI,parseArrayProperty((JSONArray)value,propCls.getComponentType()));
				}
				else if(newArr instanceof ArrayList)
				{
					try {
						((ArrayList)newArr).add(parseArrayProperty((JSONArray)value,Class.forName("java.util.ArrayList")));
					} catch (ClassNotFoundException e) {
						((ArrayList)newArr).add(null);
					}
				}
				else if(newArr instanceof Vector)
				{
					try {
						((Vector)newArr).add(parseArrayProperty((JSONArray)value,Class.forName("java.util.Vector")));
					} catch (ClassNotFoundException e) {
						((Vector)newArr).add(null);
					}
				}
				else
				{
					Array.set(newArr, propJsonI,null);
				}

				continue;
			}
			
			String valueStr = value.toString();
			if(valueStr.length()==0)
			{
				Array.set(newArr, propJsonI,null);
				continue;
			}

			if(propCompoStr.equals("int") || propCompoStr.equals("java.lang.Int"))
			{
				Array.set(newArr, propJsonI,Integer.parseInt(valueStr));
			}
			else if(propCompoStr.equals("boolean") || propCompoStr.equals("java.lang.Boolean"))
			{
				Array.set(newArr, propJsonI,Boolean.parseBoolean(valueStr));
			}
			else if(propCompoStr.equals("long") || propCompoStr.equals("java.lang.Long"))
			{
				Array.set(newArr, propJsonI,Long.parseLong(valueStr));
			}
			else if(propCompoStr.equals("short") || propCompoStr.equals("java.lang.Short"))
			{
				Array.set(newArr, propJsonI,Short.parseShort(valueStr));
			}
			else if(propCompoStr.equals("double") || propCompoStr.equals("java.lang.Double"))
			{
				Array.set(newArr, propJsonI,Double.parseDouble(valueStr));
			}
			else if(propCompoStr.equals("float") || propCompoStr.equals("java.lang.Float"))
			{
				Array.set(newArr, propJsonI,Float.parseFloat(valueStr));
			}
			else if(propCompoStr.equals("byte") || propCompoStr.equals("java.lang. Byte"))
			{
				Array.set(newArr, propJsonI,Byte.parseByte(valueStr));
			}
			else if(propCompoStr.equals("java.util.ArrayList"))
			{
				((ArrayList)newArr).add(valueStr);
			}
			else
				Array.set(newArr, propJsonI,valueStr);

		}

		return newArr;


	}

	private static Object parseProperty(Class propCls,Object propValue)
	{
		String type = propCls.getCanonicalName();

		String propValueStr = propValue.toString();

		if(propValueStr.equals("") || propValueStr.equals("null"))
			return null;

		try {
			if(propCls.isArray())
			{
				return parseArrayProperty(new JSONArray(propValueStr),propCls.getComponentType());
			}
			else if(type.equals("java.util.ArrayList") || type.equals("java.util.List"))
			{
				return parseArrayProperty(new JSONArray(propValueStr),Class.forName("java.util.ArrayList"));
			}
			else if(type.equals("java.util.Vector"))
			{
				return parseArrayProperty(new JSONArray(propValueStr),Class.forName("java.util.Vector"));
			}
			else if(type.equals("java.util.HashMap"))
			{
				return parseMapProperty(new JSONObject(propValueStr),Class.forName("java.util.HashMap"));
			}
			else if(type.equals("java.util.Map"))
			{
				return parseMapProperty(new JSONObject(propValueStr),Class.forName("java.util.Map"));
			}
			else
				return propValue;
		} catch (Exception e) {
			XingCloudLogger.log(XingCloudLogger.DEBUG,"Reflection.parseProperty -> Illegal property. Type : "+type+", value : "+propValueStr);
			return null;
		}
	}

	/**
	 * 设置AsObject的属性
	 * 如果该属性为getter/setter方式进行存储，则调用setter进行设置
	 * 如果该属性存属于AsObject的动态属性对列中，则对列表中的属性进行设置
	 * 
	 */
	public static void setProperty(AsObject obj,String propName,Object propValue)
	{
		try {
			Field f = obj.getClass().getField(propName);
			boolean originAccess = f.isAccessible();
			f.setAccessible(true);
			Class propCls = f.getType();
			if(propValue!=null && !(propValue.toString().equals("null")))
				f.set(obj, parseProperty(propCls,propValue));
			f.setAccessible(originAccess);
		} catch (Error er) {
			XingCloudLogger.log(XingCloudLogger.ERROR,"Reflection.setProperty-->Error occured when set type "+obj.getClass().getCanonicalName()+"'s property "+propName+" of value:"+propValue+".\n Details: "+er.getMessage());
			throw er;
		} catch (Exception e) {
			try {
				String getMethod = "get"+toCapitalString(propName);
				Method m_get = obj.getClass().getMethod(getMethod);
				String setMethod = "set"+toCapitalString(propName);
				Method m_set = obj.getClass().getMethod(setMethod,m_get.getReturnType());
				String type = m_get.getReturnType().getCanonicalName();
				if(type.equals("int") || type.equals("java.lang.Int"))
				{
					m_set.invoke(obj, Integer.parseInt(propValue.toString()));
				}
				else if(type.equals("boolean") || type.equals("java.lang.Boolean"))
				{
					m_set.invoke(obj, Boolean.parseBoolean(propValue.toString()));
				}
				else if(type.equals("long") || type.equals("java.lang.Long"))
				{
					m_set.invoke(obj, Long.parseLong(propValue.toString()));
				}
				else if(type.equals("short") || type.equals("java.lang.Short"))
				{
					m_set.invoke(obj, Short.parseShort(propValue.toString()));
				}
				else if(type.equals("double") || type.equals("java.lang.Double"))
				{
					m_set.invoke(obj, Double.parseDouble(propValue.toString()));
				}
				else if(type.equals("float") || type.equals("java.lang.Float"))
				{
					m_set.invoke(obj, Float.parseFloat(propValue.toString()));
				}
				else if(type.equals("byte") || type.equals("java.lang. Byte"))
				{
					m_set.invoke(obj, Byte.parseByte(propValue.toString()));
				}
				else if(m_get.getReturnType().isArray() ||
						type.equals("java.util.ArrayList") ||
						type.equals("java.util.List"))
				{
					m_set.invoke(obj,parseProperty(m_get.getReturnType(), propValue));
				}
				else if(type.startsWith(ModelBaseManager.instance().packages) && (propValue instanceof AsObject))
				{
					//This is an ownedItem object
					AsObject propValueAsObject = (AsObject)propValue;
					Object itemId = propValueAsObject.getProperty("itemId");
					Object uidObj = propValueAsObject.getProperty("uid");
					OwnedItem item = null;
					if(itemId!=null)
					{
						String uid = null;
						if(uidObj!=null)
							uid = uidObj.toString();
						item=ModelBaseManager.instance().createModelItem(type,itemId.toString(),false);
						if(item!=null)
						{
							item.parseFromObject(propValueAsObject, null);
						}
					}
					m_set.invoke(obj, item);
				}
				else
				{
					m_set.invoke(obj, propValue);
				}

				return;
			}  catch (Exception e1) {
			}


			if(obj.properties.containsKey(propName))
			{
				obj.properties.remove(propName);
			}
			
			obj.properties.put(propName,propValue);
			
		}
	}

	/**
	 * 通过反射获取去除package的类名
	 * @param <E>
	 * @param cls 欲反射的类
	 * @return 类名
	 */
	public static <E> String tinyClassName(E cls)
	{
		String clsName = cls.getClass().getName();
		int index = clsName.lastIndexOf(".");
		return clsName.substring(index+1, clsName.length());
	}

	private static String toCapitalString(String str)
	{
		return str.substring(0,1).toUpperCase()+str.substring(1,str.length());
	}


}
