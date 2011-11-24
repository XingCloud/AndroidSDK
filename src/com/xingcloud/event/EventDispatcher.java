package com.xingcloud.event;

import java.util.Iterator;
import java.util.Vector;

import com.xingcloud.items.spec.AsObject;
import com.xingcloud.utils.Record;

/**
 * 事件分发类
 * 行云Android SDK内部采用了Oberver模型的事件机制
 * 
 */
public class EventDispatcher extends AsObject
{
	private Vector<Record<String, IEventListener>> foldSet = new Vector<Record<String,IEventListener>>();

	/**
	 * 注册监听
	 * @param type 事件类型
	 * @param listener 回调接口
	 */
	public synchronized void addEventListener(String type,IEventListener listener)
	{
		if(listener==null)
			return;
		
		Record<String,IEventListener> one = new Record<String, IEventListener>(type,listener);

		synchronized (foldSet) {
			foldSet.add(one);
		}
	}

	/**
	 * 分发事件
	 * @param evt 欲分发的事件
	 */
	public synchronized void dispatchEvent(XingCloudEvent evt)
	{
		synchronized (foldSet) {
			evt.setTarget(this);
			Vector<Record<String, IEventListener>> curFolderSet = (Vector<Record<String, IEventListener>>) foldSet.clone();
			Iterator<Record<String, IEventListener>> it = curFolderSet.iterator();
			Vector<IEventListener> list = new Vector<IEventListener>();
			while(it.hasNext())
			{	
				Record<String, IEventListener> one = it.next();
				if(one!=null)
				{
					String key = one.getKey();
					IEventListener value = one.getValue();
					if(value!=null && key.equals(evt.getType()))
					{
						list.add(value);
//						value.prePerformEvent(evt);
//						value.performEvent(evt);
//						value.postPerformEvent(evt);
					}
				}
			}
			int i=0;
			for(i=0;i<list.size();i++)
			{
				IEventListener ie = list.get(i);
				ie.prePerformEvent(evt);
			}
			for(i=0;i<list.size();i++)
			{
				IEventListener ie = list.get(i);
				ie.performEvent(evt);
			}
			for(i=0;i<list.size();i++)
			{
				IEventListener ie = list.get(i);
				ie.postPerformEvent(evt);
			}
		}
	}

	/**
	 * 移除监听
	 * @param type 事件类型
	 * @param listener 回调接口
	 */
	public synchronized void removeEventListener(String type,IEventListener listener)
	{
		Vector<Record<String, IEventListener>> temp = new Vector<Record<String,IEventListener>>();
		synchronized (foldSet) {
			Iterator<Record<String, IEventListener>> it = foldSet.iterator();
			while(it.hasNext())
			{

				Record<String, IEventListener> one = it.next();
				if(one.getKey().equals(type) && one.getValue().equals(listener))
				{
					foldSet.remove(one);
					break;
				}
			}

		}
	}

}
