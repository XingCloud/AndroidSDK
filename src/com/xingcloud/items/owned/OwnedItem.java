package com.xingcloud.items.owned;

import java.util.ArrayList;

import com.xingcloud.core.ModelBase;
import com.xingcloud.core.ModelBaseManager;
import com.xingcloud.core.Reflection;
import com.xingcloud.core.XingCloud;
import com.xingcloud.event.CollectionEvent;
import com.xingcloud.event.PropertyChangeEvent;
import com.xingcloud.items.spec.AsObject;
import com.xingcloud.items.spec.ItemSpec;
import com.xingcloud.items.spec.ItemSpecManager;
import com.xingcloud.utils.Utils;

/**
 * 建立用户拥有的物品与ItemSpec之间的联系，也就是说每一个物品都拥有一个类型（也就是ItemSpec）
 */	
public class OwnedItem extends ModelBase {
	protected String uniqueIdentifierString = "";
	public String getUniqueIdentifierString() {
		return uniqueIdentifierString;
	}
	
	public String getUniqueString()
	{
		return getUniqueIdentifierString();
	}

	public void setUniqueIdentifierString(String uniqueIdentifierString) {
		this.uniqueIdentifierString = uniqueIdentifierString;
	}
	protected String itemId;
	protected ItemSpec itemSpec;
	
	/**
	 * 是否为活跃的item
	 * 比如对于身体部件或服装等，是否穿着
	 */
	public Boolean active;
    protected ItemsCollection collection;
	/**
	 * 此物品的归属者，在后台需要用，前台自动处理，不必理会
	 */
	public String ownerId;		
	public String ownerProperty;
	
	/**
	 * 
	 * @param itemId 物品id
	 */
	public OwnedItem(String itemId)
	{
		this.itemId = itemId;
	}
	
	public String getUid() {
		if(uid.equals(""))
			generateUID();
		return uid;
	}
	
	public void generateUID()
	{
		String uniqueString=""+this.hashCode()+ModelBaseManager.instance().getModelListSize()+System.currentTimeMillis();
		uniqueString = Utils.MD5(uniqueString.getBytes());
		String baseString = XingCloud.getOwnerUser().getUid()+"&"+Reflection.tinyClassName(this)+"&"+uniqueString;
		this.uid = Utils.MD5(baseString.getBytes());
		this.setUniqueIdentifierString(uniqueString);
	}
	
	protected void dispatchPropertyChangeEvent(String prop,Object oldValue,Object newValue)
	{
		if(this.collection==null) 
			return;
		PropertyChangeEvent propEvt=new PropertyChangeEvent(prop,oldValue,newValue,this);
		ArrayList items = new ArrayList();
		items.add(propEvt);
		CollectionEvent evt=new CollectionEvent(CollectionEvent.CollectionEventKind.CollectionUpdated,items,this);
		this.collection.dispatchEvent(evt);
	}
	/**
	 * 这个物品是否属于ownerUser
	 * */	
	public Boolean getBelongOwner()
	{
		return (XingCloud.getOwnerUser()!=null&&(XingCloud.getOwnerUser().getUid().equals(this.ownerId)));
	}
	
	/**
	 * 获取物品id
	 * @return id
	 */
	public String getItemId()
	{
		return itemId;
	}
	/**
	 * 获取对于的物品ItemSpec
	 * @return 物品ItemSpec
	 */
	public ItemSpec getItemSpec()
	{
		if(itemSpec==null)
			this.itemSpec=ItemSpecManager.instance().getItem(itemId);

		return this.itemSpec;
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.xingcloud.core.ModelBase#parseFromObject(com.xingcloud.items.spec.AsObject, java.util.ArrayList)
	 */
	public void parseFromObject(AsObject data,ArrayList<String> excluded)
	{
		//如果是当前玩家的物品从数据源更新，关闭下track
//		if(this.getBelongOwner())
//			AuditChangeManager.instance().stopTrack();
		super.parseFromObject(data,excluded);
	}
	
    /**
	 * 设置物品id
	 * @param id
	 */
	public void setItemId(String id)
	{
		this.itemId=id;
	}
	
    public String toJSONString()
    {
    	return "{uid:"+this.getUid()+",ownerId:"+this.ownerId+"}";
    }
}
