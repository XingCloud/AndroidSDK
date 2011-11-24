package com.xingcloud.core;

import com.xingcloud.items.owned.OwnedItem;

public class ModelBaseManager {
	private static ModelBaseManager _instance;
	//private HashMap<String,ModelBase> _modelList = new HashMap<String,ModelBase>();
	private int _modelCount = 0;
	public String packages="model.item";
	
	public static ModelBaseManager instance()
	{
		if(_instance==null)
			_instance = new ModelBaseManager();

		return _instance;
	}
	
	/**
	 * 创建一个OwnedItem
	 * @param type item类型，也即包路径+类名
	 * @param itemID 关联的itemspec的id
	 * @return 新创建的OwnedItem
	 */
	public OwnedItem createModelItem(String type,String itemID)
	{
		return createModelItem(type,itemID,false);
	}

	/**
	 * 创建一个OwnedItem
	 * @param type item类型，也即包路径+类名
	 * @param itemID 关联的itemspec的id
	 * @param autoGenerateUID 是否自动生成uid。
	 * @return 新创建的OwnedItem
	 */
	public OwnedItem createModelItem(String type,String itemID,boolean autoGenerateUID)
	{
		try {
			
			if(!(type.startsWith(packages)))
			{
				type = packages+"."+type;
			}
			
			Class<?> cls = Class.forName(type);
			if(cls==null)
				return null;
			OwnedItem item=(OwnedItem)cls.getConstructor(Class.forName("java.lang.String")).newInstance(itemID);
			if(autoGenerateUID)
			{
				generateUID(item);
			}
			return item;
		} catch (Exception e) {
		}
		return null;
	}

	public void generateUID(OwnedItem model)
	{
		model.generateUID();
	}

	synchronized public void clean()
	{
		_modelCount = 0;
		//_modelList.clear();
	}
	
	synchronized public void addModel(ModelBase model)
	{
		_modelCount++;
		//_modelList.put(model.uid, model);
	}
	
	synchronized public void removeModel(String modelUid)
	{
		_modelCount--;
		//_modelList.remove(modelUid);
	}
	
	/*
	synchronized public ModelBase getModel(String modelUid)
	{
		return _modelList.get(modelUid);
	}
	*/
	
	synchronized public int getModelListSize()
	{
		return _modelCount;
		//return _modelList.size();
	}
}
