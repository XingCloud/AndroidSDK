package com.xingcloud.items.owned;

import java.util.ArrayList;
import java.util.Collection;

import com.xingcloud.event.CollectionEvent;
import com.xingcloud.event.EventDispatcher;

/**
 * 数据变化后会自动分发事件的数组类型
 * @author chuckzhang
 *
 * @param <E>
 */
public class ArrayCollection<E> extends EventDispatcher{
	protected ArrayList<E> source = new ArrayList<E>();

	public ArrayCollection()
	{
		
	}
	
	/**
	 * 
	 * @param source 数据源
	 */
	public ArrayCollection(ArrayList<E> source)
	{
		this.source = source;
	}
	
	/**
	 * 将数据源中的数据全部加入
	 * @param o 欲增加的数据源
	 * @return 是否成功
	 */
	public boolean addAll(Collection<? extends E> o)
	{
		if(source==null)
			return false;
		
		boolean result = source.addAll(o);
		ArrayList items = new ArrayList();
		items.add(o);
		CollectionEvent evt = new CollectionEvent(CollectionEvent.CollectionEventKind.CollectionAdd,items,this);
		this.dispatchEvent(evt);
		return result;
	}
	
	/**
	 * 增加数据
	 * @param o 欲增加的数据
	 * @return 是否成功
	 */
	public boolean addItem(E o)
	{
		if(source==null)
			return false;
		boolean result = source.add(o);
		ArrayList items = new ArrayList();
		items.add(o);
		CollectionEvent evt = new CollectionEvent(CollectionEvent.CollectionEventKind.CollectionAdd,items,this);
		this.dispatchEvent(evt);
		return result;
	}
	
	/**
	 * 在指定位置增加数据
	 * @param index 欲插入的位置
	 * @param o 欲增加的数据
	 */
	public void addItemAt(E o,int index)
	{
		if(source==null)
			return ;
		
		source.add(index,o);
		ArrayList items = new ArrayList();
		items.add(o);
		CollectionEvent evt = new CollectionEvent(CollectionEvent.CollectionEventKind.CollectionAdd,items,this);
		this.dispatchEvent(evt);
	}
	
	/**
	 * 
	 * @param elem 欲监测的数据
	 * @return 是否包含某数据
	 */
	public boolean contains(Object elem)
	{
		if(source==null)
			return false;
		
		return source.contains(elem);
	}
	
	/**
	 * 获取一个位置的数据
	 * @param index 索引
	 * @return 数据
	 */
	public E getItemAt(int index)
	{
		if(source==null || index<0 || index>=source.size())
			return null;
		return source.get(index);
	}
	
	public ArrayList<E> getSource() {
		return source;
	}
	
	/**
	 * 获取某一数据的索引
	 * @param elem 欲监测的索引
	 * @return 索引位置，如果不包含该数据，则返回-1
	 */
	public int indexOf(Object elem)
	{
		if(source==null)
			return -1;
		
		return source.indexOf(elem);
	}
	
	/**
	 * 
	 * @return 是否不包含数据
	 */
	public boolean isEmpty()
	{
		if(source==null)
			return true;
		return source.isEmpty();
	}
	
	/**
	 * 清除全部数据
	 */
	public void removeAll()
	{
		if(source==null)
			return ;
		
		//ArrayList items = new ArrayList(source);
		//CollectionEvent evt = new CollectionEvent(CollectionEvent.CollectionEventKind.CollectionRemove,items);
		source.clear();
		//this.dispatchEvent(evt);
	}
	
	/**
	 * 移除一个数据
	 * @param elem 欲移除的数据
	 * @return 是否成功
	 */
	public boolean removeItem(Object elem)
	{
		if(source==null)
			return false;
		
		boolean result = source.remove(elem);
		ArrayList items = new ArrayList();
		items.add(elem);
		CollectionEvent evt = new CollectionEvent(CollectionEvent.CollectionEventKind.CollectionRemove,items,this);
		this.dispatchEvent(evt);
		return result;
	}
	
	/**
	 * 移除指定位置的数据
	 * @param index 欲移除的数据索引
	 * @return 被移除的数据
	 */
	public E removeItemAt(int index)
	{
		if(source==null)
			return null;
		
		E result = source.remove(index);
		ArrayList items = new ArrayList();
		items.add(result);
		CollectionEvent evt = new CollectionEvent(CollectionEvent.CollectionEventKind.CollectionRemove,items,this);
		this.dispatchEvent(evt);
		return result;
	}
	
	/**
	 * 设置数据源
	 * @param source
	 */
	public void setSource(ArrayList<E> source) {
		this.source = source;
	}
	
	/**
	 * 
	 * @return 数组大小
	 */
	public int size()
	{
		if(source!=null)
			return source.size();
		else
			return 0;
	}
	
	/**
	 * 设置某一个位置的数据
	 * @param index 某一位置
	 * @param element 新数据
	 */
	public void updateItem(int index, E element)
	{
		if(source==null)
			return ;
		
		source.set(index, element);
		ArrayList items = new ArrayList();
		items.add(element);
		CollectionEvent evt = new CollectionEvent(CollectionEvent.CollectionEventKind.CollectionUpdated,items,this);
		this.dispatchEvent(evt);
	}
}
