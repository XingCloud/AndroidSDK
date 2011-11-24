package com.xingcloud.event;

import java.util.ArrayList;

/**
 * 行云数据集合事件，包括
 * 添加数据事件（CollectionAdd）
 * 删除数据事件（CollectionRemove）
 * 更新数据事件（CollectionUpdated）
 */
public class CollectionEvent extends XingCloudEvent {

	public enum CollectionEventKind
	{
		CollectionAdd,
		CollectionRemove,
		CollectionUpdated;
	}
	public static String COLLECTION_CHANGE = "COLLECTION_CHANGE";
	public ArrayList items;
	
	public CollectionEventKind kind;

	public CollectionEvent(CollectionEventKind kind,ArrayList items,Object currentTarget) {
		super(COLLECTION_CHANGE,currentTarget);
		this.kind = kind;
		this.items = items;
	}
}
