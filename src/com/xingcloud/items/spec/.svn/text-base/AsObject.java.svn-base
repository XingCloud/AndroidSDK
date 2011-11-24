package com.xingcloud.items.spec;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.xingcloud.core.ModelBase;
import com.xingcloud.core.Reflection;

/**
 * XingCloud的基础数据类型，它有如下特点
 * 1. 包含动态属性列表。可以动态的增加、删除属性
 * 2. 直接转换为JSON数据
 * 3. 直接转换为通用的JSON字符串
 * 4. 直接转换为URL字符串
 * 5. 从JSON字符串构造数据
 *
 */
public class AsObject extends Object {

	static String INIT_ASOBJECT_KEY = "initAsObject";

	public HashMap<String, Object> properties=new HashMap<String, Object>();
	
	/**
	 * 构造一个空数据的AsObject
	 */
	public AsObject()
	{
	}

	public AsObject(JSONObject resultObj) {
		
	}
	
	/*
	public AsObject(SFSObject sfsObj)
	{
		parseSFSObject(this, INIT_ASOBJECT_KEY, sfsObj);
	}
	*/

	/**
	 * 从JSON字符串构造数据
	 * @param jsonString json字符串
	 */
	public AsObject(String jsonString)
	{
		JSONObject jsobj;
		try {
			jsobj = new JSONObject(jsonString);
			parseJSONObject(this, INIT_ASOBJECT_KEY, jsobj);
		} catch (JSONException e) {
			//e.printStackTrace();
		}
	}

	/**
	 * 获取动态属性
	 * @param propName 属性名称
	 * @return 属性内容
	 */
	public Object getProperty(String propName)
	{
		return Reflection.getProperty(this, propName);
	}
	
	public String getStringProperty(String propName)
	{
		Object value = Reflection.getProperty(this, propName);
		if(value==null)
			return null;
		else
			return value.toString();
	}

	private void parseAsObject(JSONObject target,String targetKey,Object asObj)
	{
		try {
			if(asObj instanceof ModelBase)
			{
				JSONObject jsObj;

				if(targetKey.equals(INIT_ASOBJECT_KEY))
					jsObj = target;
				else
					jsObj = new JSONObject();

				Field[] fields = asObj.getClass().getFields();
				Method[] methods = asObj.getClass().getMethods();
				try {
					int length = fields.length;
					for(int i=0;i<length;i++)
					{
						Field f = fields[i];
						//if(f.isAccessible())
						{
							String type = f.getType().getName();
							if(type.equals("int") ||
									type.equals("java.lang.Boolean") ||
									type.equals("java.lang.String"))
							{
								jsObj.put(f.getName(), f.get(asObj));
							}
						}
					}

					length = methods.length;
					for(int i=0;i<length;i++)
					{
						Method m = methods[i];
						//if(m.isAccessible() && m.getName().startsWith("get"))
						if(m.getName().startsWith("get"))
						{
							String returnType = m.getReturnType().getName();
							if(returnType.equals("int") ||
									returnType.equals("java.lang.Boolean") ||
									returnType.equals("java.lang.String"))
							{
								if(m.getParameterTypes().length!=0)
									continue;
								if(m.getName().startsWith("get"))
								{
									String key = m.getName().substring(3,m.getName().length());
									key = key.substring(0, 1).toLowerCase()+key.substring(1, key.length());
									jsObj.put(key, m.invoke(asObj));
								}
							}
						}
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
				}
				if(!targetKey.equals(INIT_ASOBJECT_KEY))
					target.put(targetKey, jsObj);
			}
			else if(asObj instanceof AsObject)
			{
				JSONObject jsObj;

				if(targetKey.equals(INIT_ASOBJECT_KEY))
					jsObj = target;
				else
					jsObj = new JSONObject();

				AsObject obj = (AsObject) asObj;
				Iterator it = obj.properties.entrySet().iterator();
				while(it.hasNext())
				{
					Entry ent = (Entry)it.next();
					String key = (String)ent.getKey();
					Object value = ent.getValue();
					parseAsObject(jsObj, key, value);
				}

				if(!targetKey.equals(INIT_ASOBJECT_KEY))
					target.put(targetKey, jsObj);
			}
			else if(asObj instanceof ArrayList)
			{
				ArrayList asArr = (ArrayList)asObj;
				JSONArray jsArr = new JSONArray();
				int length = asArr.size();
				for(int i=0;i<length;i++)
				{
					Object value = asArr.get(i);
					JSONObject jj = new JSONObject();
					this.parseAsObject(jj, INIT_ASOBJECT_KEY, value);
					Object newValue = null;
					try{
						newValue = jj.get(INIT_ASOBJECT_KEY);
					} catch (JSONException e){
					}
					if(jj.length()==1 && newValue!=null)
					{
						jsArr.put(newValue);
					}
					else
					{
						jsArr.put(jj);
					}
				}
				target.put(targetKey, jsArr);
			}
			else if(asObj!=null && asObj.getClass().isArray())
			{
				JSONArray jsArr = new JSONArray();
				int length = Array.getLength(asObj);
				for(int i=0;i<length;i++)
				{
					Object jsValue = Array.get(asObj, i);
					jsArr.put(jsValue);
				}
				target.put(targetKey, jsArr);
			}
			else
			{
				target.put(targetKey, asObj);
			}
		} catch (JSONException e) {
		}
	}
	
	/*
	private void parseAsObjectToSFS(SFSObject target,String targetKey,Object asObj)
	{
		if(asObj instanceof ModelBase)
		{
			SFSObject jsObj;

			if(targetKey.equals(INIT_ASOBJECT_KEY))
				jsObj = target;
			else
				jsObj = new SFSObject();

			Field[] fields = asObj.getClass().getFields();
			Method[] methods = asObj.getClass().getMethods();
			try {
				int length = fields.length;
				for(int i=0;i<length;i++)
				{
					Field f = fields[i];
					//if(f.isAccessible())
					{
						String type = f.getType().getName();
						if(type.equals("int") ||
								type.equals("java.lang.Boolean") ||
								type.equals("java.lang.String"))
						{
							jsObj.putUtfString(f.getName(), f.get(asObj).toString());
						}
					}
				}

				length = methods.length;
				for(int i=0;i<length;i++)
				{
					Method m = methods[i];
					//if(m.isAccessible() && m.getName().startsWith("get"))
					if(m.getName().startsWith("get"))
					{
						String returnType = m.getReturnType().getName();
						if(returnType.equals("int") ||
								returnType.equals("java.lang.Boolean") ||
								returnType.equals("java.lang.String"))
						{
							if(m.getParameterTypes().length!=0)
								continue;
							if(m.getName().startsWith("get"))
							{
								String key = m.getName().substring(3,m.getName().length());
								key = key.substring(0, 1).toLowerCase()+key.substring(1, key.length());
								jsObj.putUtfString(key, m.invoke(asObj).toString());
							}
						}
					}
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
			}
			if(!targetKey.equals(INIT_ASOBJECT_KEY))
				target.putSFSObject(targetKey, jsObj);
		}
		else if(asObj instanceof AsObject)
		{
			SFSObject jsObj;

			if(targetKey.equals(INIT_ASOBJECT_KEY))
				jsObj = target;
			else
				jsObj = new SFSObject();

			AsObject obj = (AsObject) asObj;
			Iterator it = obj.properties.entrySet().iterator();
			while(it.hasNext())
			{
				Entry ent = (Entry)it.next();
				String key = (String)ent.getKey();
				Object value = ent.getValue();
				parseAsObjectToSFS(jsObj, key, value);
			}

			if(!targetKey.equals(INIT_ASOBJECT_KEY))
				target.putSFSObject(targetKey, jsObj);
		}
		else if(asObj instanceof ArrayList)
		{
			ArrayList asArr = (ArrayList)asObj;
			SFSArray jsArr = new SFSArray();
			int length = asArr.size();
			for(int i=0;i<length;i++)
			{
				Object value = asArr.get(i);
				SFSObject jj = new SFSObject();
				this.parseAsObjectToSFS(jj, INIT_ASOBJECT_KEY, value);
				jsArr.addSFSObject(jj);
			}
			target.putSFSArray(targetKey, jsArr);
		}
		else
		{
			target.putUtfString(targetKey, asObj.toString());
		}
	}
	*/
	
	private void parseJSONObject(AsObject target,String targetKey,Object json)
	{
		if(json instanceof JSONObject)
		{
			AsObject newObj = null;
			if(targetKey.equals(INIT_ASOBJECT_KEY))
				newObj = target;
			else
				newObj = new AsObject();

			Iterator it = ((JSONObject)json).keys();
			while(it.hasNext())
			{
				String key = (String)it.next();
				Object value;
				try {
					value = ((JSONObject)json).get(key);
				} catch (JSONException e) {
					continue;
				}
				this.parseJSONObject(newObj, key, value);
			}

			if(!targetKey.equals(INIT_ASOBJECT_KEY))
				target.setProperty(targetKey, newObj);
		}
		else if(json instanceof JSONArray)
		{
			target.setProperty(targetKey, parseJSONArray((JSONArray)json));
		}
		else
		{
			target.setProperty(targetKey, json);
		}
	}
	
	private ArrayList parseJSONArray(JSONArray data)
	{
		ArrayList arr = new ArrayList();
		int length = data.length();
		for(int i=0;i<length;i++)
		{
			try {
				Object value = data.get(i);
				if(value instanceof JSONObject)
				{
					AsObject newObj = new AsObject();
					this.parseJSONObject(newObj, INIT_ASOBJECT_KEY, value);
					arr.add(newObj);
				}
				else if(value instanceof JSONArray) 
				{
					arr.add(parseJSONArray((JSONArray)value));
				}
				else
				{
					if(value==JSONObject.NULL)
						arr.add(null);
					else
						arr.add(value);
				}
			} catch (JSONException e) {
				continue;
			}
		}
		return arr;
	}

	/*
	private void parseSFSObject(AsObject target,String targetKey,Object json)
	{
		if(json instanceof SFSObject)
		{
			AsObject newObj = null;
			if(targetKey.equals(INIT_ASOBJECT_KEY))
				newObj = target;
			else
				newObj = new AsObject();
			
			Iterator it = ((SFSObject)json).getKeys().iterator();
			while(it.hasNext())
			{
				String key = (String)it.next();
				Object value = ((SFSObject)json).get(key);
				if(value instanceof SFSDataWrapper)
				{
					SFSDataWrapper data = (SFSDataWrapper)value;
					this.parseSFSObject(newObj, key, data.getObject());
				}
				else
					this.parseSFSObject(newObj, key, value);
			}

			if(!targetKey.equals(INIT_ASOBJECT_KEY))
				target.setProperty(targetKey, newObj);
		}
		else if(json instanceof SFSArray)
		{
			ArrayList arr = new ArrayList();
			int length = ((SFSArray)json).size();
			for(int i=0;i<length;i++)
			{
				Object value = ((SFSArray)json).getElementAt(i);
				AsObject newObj = new AsObject();
				if((value instanceof JSONArray) || (value instanceof JSONObject))
				{
					this.parseSFSObject(newObj, INIT_ASOBJECT_KEY, value);
					arr.add(newObj);
				}
				else
				{
					arr.add(value);
				}
			}
			target.setProperty(targetKey, arr);
		}
		else
		{
			target.setProperty(targetKey, json);
		}
	}
	*/

	/**
	 * 设置属性
	 * @param propName 属性名称
	 * @param propValue 属性数据
	 */
	public void setProperty(String propName,Object propValue)
	{
		Reflection.setProperty(this, propName, propValue);
	}

	/**
	 * 输出为JSON数据结构
	 * @return JSON数据
	 */
	public JSONObject toJSON()
	{
		JSONObject content = new JSONObject();
		this.parseAsObject(content, INIT_ASOBJECT_KEY, this);
		return content;
	}

	/**
	 * 输出为JSON字符串
	 * @return json字符串
	 */
	public String toJSONString()
	{
		return toJSON().toString();
	}

	/*
	public SFSObject toSFSObject()
	{
		SFSObject content = new SFSObject();
		this.parseAsObjectToSFS(content, INIT_ASOBJECT_KEY, this);
		return content;
	}
	*/

	/**
	 * 输出为URL数据，用于GET请求
	 * @return url字符串
	 */
	public String toURLString()
	{
		StringBuffer param = new StringBuffer();
		Iterator it = properties.entrySet().iterator();
		int i = 0;
		while(it.hasNext())
		{
			Entry ent = (Entry)it.next();
			String key = (String)ent.getKey();
			Object value = ent.getValue();
			if (i == 0)
				param.append("?");
			else
				param.append("&");

			String valueStr = "";
			if(value instanceof AsObject)
				valueStr = ((AsObject)value).toJSONString();
			else
				valueStr = value.toString();
			
			param.append(key).append("=").append(valueStr);

			i++;
		}
		return param.toString();
	}
}
