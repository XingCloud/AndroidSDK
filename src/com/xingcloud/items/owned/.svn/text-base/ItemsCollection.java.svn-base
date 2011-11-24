package com.xingcloud.items.owned;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import com.xingcloud.core.Config;
import com.xingcloud.core.ModelBaseManager;
import com.xingcloud.core.Reflection;
import com.xingcloud.core.XingCloud;
import com.xingcloud.event.IEventListener;
import com.xingcloud.event.XingCloudEvent;
import com.xingcloud.items.spec.AsObject;
import com.xingcloud.tasks.base.TaskEvent;
import com.xingcloud.tasks.net.Remoting;
import com.xingcloud.tasks.net.Remoting.RemotingMethod;
import com.xingcloud.users.AbstractUserProfile;

/**
 * 物品数据的基础存储结构
 * 当物品数据有增删操作后，会自动分发事件
 * @author chuckzhang
 *
 * @param <E>
 */
public class ItemsCollection<E> extends ArrayCollection<E>
{
	class ItemsCollectionRemotingEventListener implements IEventListener
	{
		private ItemsCollection items=null;
		private int type=0;
		public ItemsCollectionRemotingEventListener(ItemsCollection items,int type)
		{
			this.type = type;
			this.items = items;
		}

		public void performEvent(XingCloudEvent evt) {
			evt.getTarget().removeEventListener(TaskEvent.TASK_COMPLETE,this);
			evt.getTarget().removeEventListener(TaskEvent.TASK_ERROR,this);
			if(type==0)
				items.onDataUpdated(evt);
			else
				items.onDataUpdateFail(evt);
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
	/**key:uid,value:OwnedItem**/
	protected HashMap<String,OwnedItem> itemsMap=new HashMap<String,OwnedItem>();
	/**此数组只允许itemType制定的class名作为元素，防止误操作,会严格考虑类型，继承的类都不算**/
	public String itemType;
	protected IEventListener loadFailCallback;
	protected IEventListener loadOkCallback;
	/**
	 * 是否自动加载物品详细数据
	 */
	public Boolean needLoad=false;

	/**此组物品属于哪个玩家***/
	public AbstractUserProfile owner;
	/**
	 * 
	 */
	public String OwnerProperty="ownedItems";
	public ItemsCollection()
	{
		super();
	}

	/**
	 * 
	 * @param source 数据源
	 */
	public ItemsCollection(ArrayList<E> source)
	{
		super(source);
	}

	public void addItem(OwnedItem item)
	{
		this.checkType(item);
		item.ownerId=owner.getUid();
		item.collection = this;
		if(item.getUid()!=null) 
			itemsMap.put(item.getUid(), item);
		super.addItem((E) item);
	}

	public void addItemAt(int index,OwnedItem item)
	{

		this.checkType(item);
		item.ownerId=owner.getUid();
		item.collection = this;
		if(item.getUid()!=null) 
			itemsMap.put(item.getUid(),item);
		super.addItemAt((E) item, index);
	}
	/**严格检查元素类型**/
	protected void checkType(OwnedItem item)
	{

		String className=item.getClass().getName();
		if(!className.equals(itemType)){
			throw new Error("The itemType must be "+this.itemType+" only, the inherited class is not permitted!");
		}
	}
	/**
	 * 清空数据
	 */
	public void clear()
	{
		itemsMap.clear();
		this.removeAll();
	}
	private ArrayList getCompositeIDs()
	{
		ArrayList IDs=new ArrayList();
		for (E item:this.source){
			if(!(item instanceof OwnedItem))
				continue;
			Object compositeID = Reflection.getProperty((AsObject)item, "compositeID");
			if(compositeID==null)
				continue;
			IDs.add(compositeID);
		}
		return IDs;
	}
	/**
	 *通过UID返回具体物品实例 
	 * @param uid 物品uid
	 * 
	 */		
	public OwnedItem getItemByUID(String uid)
	{
		return (OwnedItem)itemsMap.get(uid);
	}
	/**
	 * 向服务器请求物品数据详情，并更新相应数据
	 * @param successCallback 操作成功的回调接口
	 * @param failCallback 操作失败的回调接口
	 * @return 操作是否成功
	 */
	public boolean load(IEventListener successCallback,IEventListener failCallback)
	{
		/*
		if(!needLoad)
		{
			if(successCallback!=null)
				successCallback.performEvent(new XingCloudEvent(XingCloudEvent.ITEMS_LOADED));
			return true;
		}
		*/
		
		this.loadOkCallback=successCallback;
		this.loadFailCallback=failCallback;

		AsObject params = new AsObject();
		params.setProperty("user_uid", owner.getUid());
		params.setProperty("property", OwnerProperty);
		
		RemotingMethod method = RemotingMethod.POST;
		/*
		if(Config.getConfig(XingCloud.SFS_ENABLE_CONFIG)!=null)
		{
			boolean isSFSEnabled = (Boolean)Config.getConfig(XingCloud.SFS_ENABLE_CONFIG);
			if(isSFSEnabled)
				method = RemotingMethod.SFS;
		}
		*/
		Remoting rem=new Remoting(Config.ITEMSLOAD_SERVICE,params,method,XingCloud.instance().needAuth);
		rem.addEventListener(TaskEvent.TASK_COMPLETE, new ItemsCollectionRemotingEventListener(this, 0));
		rem.addEventListener(TaskEvent.TASK_ERROR, new ItemsCollectionRemotingEventListener(this, 1));
		rem.execute();

		return true;
	}
	protected void onDataUpdated(XingCloudEvent evt)
	{

		Remoting rem = (Remoting)evt.getTarget();
		Object dataObj = rem.response.getData();
		if(dataObj!=null)
		{
			this.clear();
			if(dataObj instanceof AsObject)
			{
				AsObject data = (AsObject)dataObj;
				Iterator it = data.properties.entrySet().iterator();
				while(it.hasNext())
				{
					Entry ent = (Entry)it.next();
					Object value = ent.getValue();
					if(value instanceof AsObject)
					{
						Object uidObj = ((AsObject)value).getProperty("uid");
						if(uidObj!=null)
						{
							String uid = uidObj.toString();
							if(uid!=null && uid.trim().length()!=0)
							{
								String itemId = ((AsObject)value).getProperty("itemId").toString();
								OwnedItem item=ModelBaseManager.instance().createModelItem(itemType,itemId,false);
								if(item!=null)
								{
									item.parseFromObject((AsObject)value, null);
									this.addItem(item);
								}
							}
						}
					}
				}
			}
			this.dispatchEvent(new XingCloudEvent(XingCloudEvent.ITEMS_LOADED,null));
			if(this.loadOkCallback!=null) 
				this.loadOkCallback.performEvent(evt);
		}
		else
		{
			onDataUpdateFail(evt);
		}
		
	}

	protected void onDataUpdateFail(XingCloudEvent evt)
	{
		XingCloudEvent newEvt = new XingCloudEvent(XingCloudEvent.ITEMS_LOADED_ERROR,evt);
		this.dispatchEvent(newEvt);
		if(this.loadFailCallback!=null) 
			this.loadFailCallback.performEvent(newEvt);
	}

	/**
	 *移除一个物品 
	 * @param item 物品
	 * @return  返回此物品
	 * 
	 */		
	public OwnedItem removeItem(OwnedItem item)
	{
		int index=this.indexOf(item);
		if(index==-1)
			return null;
		item.ownerId=null;
		if(itemsMap.containsKey(item.getUid()))
			itemsMap.remove(item.getUid());

		return (OwnedItem)this.removeItemAt(index);
	}

	public OwnedItem updateItem(OwnedItem item)
	{
		int index=this.indexOf(item);
		if(index==-1)
		{
			this.addItem(item);
		}
		else
		{
			super.updateItem(index, (E) item);
		}
		return item;
	}

	/**
	 * 更新item的uid，使之可以查询。一般用于新增物品之后的处理
	 * @param item
	 * @return 更新后的OwnedItem
	 */		
	public OwnedItem updateItemUID(OwnedItem item)
	{
		itemsMap.put(item.getUid(), item);
		return item;
	}
}
