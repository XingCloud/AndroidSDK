package com.xingcloud.users.auditchanges;

import com.xingcloud.core.ModelBase;
import com.xingcloud.event.IEventListener;
import com.xingcloud.items.spec.AsObject;
import com.xingcloud.users.MessagingManager;

public class AuditChangeManager extends MessagingManager {
	protected static AuditChange _currentAudit;
	private static AuditChangeManager _instance;
	public static AuditChangeManager instance()
	{
		if(_instance==null)
		{
			_instance = new AuditChangeManager();
			enableQueued = true;
		}
		return _instance;
	}

	/**
	 * 要添加一条记录，后台会直接改变数据库
	 * @param item 新物品
	 * */
	synchronized public  Boolean appendItemAddChange(ModelBase item)
	{
		if(!getCanUse()) 
			return false;
		//添加ownedItem时，ownerId要设为当前用户的uid，后台靠这个
//		item.ownerId=Global.ownerUser.uid;
		AsObject change = new AsObject();
		change.setProperty("target", item.getClassName());
		change.setProperty("method", "add");
		change.setProperty("item",item);
		_currentAudit.changes.add(change);
		_currentAudit.changeField.setProperty(item.getClassName(), item);
		return true;
	}
	
	/**
	 * 要删除一条记录，后台会直接改变数据库
	 * @param item 要删除的物品
	 * */
	synchronized public  Boolean appendItemRemoveChange(ModelBase item)
	{
		if(!getCanUse()) return false;
		AsObject change = new AsObject();
		change.setProperty("target", item.getClassName());
		change.setProperty("method", "remove");
		change.setProperty("uid",item.getUid());
		_currentAudit.changes.add(change);
		_currentAudit.changeField.setProperty(item.getClassName(), item);
		return true;
	}

	/**
	 * 添加一个属性改变，后台会直接改变数据库
	 * @param item 要改变属性的实例对象，UserInfoBase的子类，如UserProfile,OwnedItem等
	 * @param property 要改变的属性名
	 * @param oldValue 改变前的值
	 * @param newValue 改变后的值
	 * */
	synchronized public Boolean appendUpdateChange(ModelBase item,String property,Object oldValue,Object newValue)
	{
		if(!getCanUse())
			return false;
		
		AsObject change = new AsObject();
		change.setProperty("target", item.getClassName());
		change.setProperty("method", "update");
		change.setProperty("uid", item.getUid());
		change.setProperty("property", property);
		change.setProperty("oldValue", oldValue);
		change.setProperty("newValue", newValue);
		int index=checkSamePropChange(change);
		//删除旧的,有问题再研究，对于物品应该要采取这种措施，但userProfile本身不要这个措施吧
		if(index!=-1) {
			change.setProperty("oldValue", _currentAudit.changes.get(index).getProperty("oldValue"));
			_currentAudit.changes.remove(index);
		}
		_currentAudit.changes.add(change);
		_currentAudit.changeField.setProperty(item.getClassName(), item);
		return true;
	}
	
	/**
	 * 检查和chg具有相同对象，相同改变方法及改变属性的change，如果玩家拖着一个物品不停的动，那是不是要非常频繁的发数据到后台呢，遇到这种情况，只
	 * 发最近的那次改变，ok
	 * */
	synchronized private  int checkSamePropChange(AsObject chg)
	{
		for (int i=0;i<_currentAudit.changes.size();i++){
			AsObject change=_currentAudit.changes.get(i);
			Object targetProp = change.getProperty("target");
			Object methodProp = change.getProperty("method");
			Object uidProp = change.getProperty("uid");
			Object propertyProp = change.getProperty("property");
			Object targetProp2 = chg.getProperty("target");
			Object methodProp2 = chg.getProperty("method");
			Object uidProp2 = chg.getProperty("uid");
			Object propertyProp2 = chg.getProperty("property");
			if(targetProp!=null && targetProp2!=null && targetProp.equals(targetProp2) && 
					methodProp!=null && methodProp2!=null && methodProp.equals(methodProp2) &&
					uidProp!=null && uidProp2!=null && uidProp.equals(uidProp2) &&
					propertyProp!=null && propertyProp2!=null && propertyProp.equals(propertyProp2)){
				return i;
			}
		}
		
		return -1;
	}
	
	/**
	 * 当前是否有指定的audit可用
	 * */
	private Boolean getCanUse()
	{
		return (_currentAudit!=null);
	}
	/**
	 * 希望后面的操作不记录AuditChange
	 * */
	synchronized public  void stopTrack()
	{
		if(_currentAudit!=null)
			addMessage(_currentAudit);
		_currentAudit=null;
	}		
	/**
	 * 开始一个AuditChange记录,想往后台发送audit，就要从这句话开始
	 * @param audit 要发送的AuditChange
	 * @param successCallback 成功回调函数,模板
	 * @param failCallback 失败回调函数
	 * */
	synchronized public AuditChange track(AuditChange audit,IEventListener successCallback,IEventListener failCallback)
	{
		_currentAudit=audit;
		_currentAudit._onSuccess=successCallback;
		_currentAudit._onFail=failCallback;
		return _currentAudit;
	}
	/***只是向后台提交一个AuditChange，后面不会涉及profile任何属性改变，像在track向导的某一步时，后台只需记录现在是哪一步，需要如此*/
	public void trackOnce(AuditChange audit,IEventListener successCallback,IEventListener failCallback)
	{
		track(audit,successCallback,failCallback);
		stopTrack();
	}
}