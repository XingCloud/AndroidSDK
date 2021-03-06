package com.xingcloud.items.spec;

import java.util.ArrayList;

import com.xingcloud.utils.XingCloudLogger;

import android.util.Log;

/**
 * 定义一个物品组，这个定义无需继承，他是动态类，可以赋值任何自定义itemSpec属性，
 * 当子元素设此属性时忽略，子元素没设此属性时，用此属性赋值之，可以方便的设置整组物品的一些通用属性
 * */
public final class ItemGroup extends ItemBase {
	protected ArrayList<ItemBase> _children;

	public ItemGroup()
	{
		super();
		_children=new ArrayList<ItemBase>();
	}
	public void addItem(ArrayList<ItemBase> itm)
	{
		for(int i = 0; i < itm.size();i++)
		{
			addItem(itm.get(i));
		}
		
	}
	/**
	 *在物品组内增加一个物品定义 
	 * @param itm 物品定义
	 * 
	 */	
	public void addItem(ItemBase itm)
	{
		if(this.contains(itm)) {
			XingCloudLogger.log(XingCloudLogger.WARN,"ItemGroup->addItem: Child with id "+itm.id+"has existed!");
			return;
		}
		_children.add(itm);
		itm.parent=this;
	}
	/**
	 *是否包含此元素 
	 * @param itm
	 * @return 是否包含此元素
	 * 
	 */		
	public boolean contains(ItemBase itm)
	{
		for(ItemBase item:_children)
		{
			if(item.id.equals(itm.id)){
				return true;
			}
		}	
		return false;
	}
	/**
	 * 获取为ItemGroup类型的所有子元素，只在当前层
	 * **/
	public ArrayList<ItemBase> getAllGroups()
	{
		ArrayList<ItemBase> arr=new ArrayList<ItemBase>();
		for(ItemBase item:_children){
			if(item instanceof ItemGroup){
				arr.add(item);
			}
		}	
		return arr;			
	}	

	/**
	 * 获取当前ItemGroup下所有的items
	 *  @param deepSearch 如果子元素是ItemGroup，是否寻找底层的ItemSpec
	 *  @param arr 赋值给他
	 * **/
	public ArrayList<ItemBase> getAllItems(Boolean deepSearch,ArrayList<ItemBase> arr)
	{
		if(arr==null) 
			arr=new ArrayList<ItemBase>();
		for(ItemBase item:_children)
		{
			if(item instanceof ItemSpec){
				arr.add(item);
			}else if(deepSearch){
				((ItemGroup)item).getAllItems(true,arr);
			}
		}	
		return arr;				
	}
	/**
	 *获取特定id的ItemGroup
	 * @param id groupid
	 * @param deepSearch 是否深度搜索
	 * @return 获取到的ItemGroup
	 * 
	 */		
	public ItemGroup getChildGroup(String id,Boolean deepSearch)
	{
		for(ItemBase item:_children){
			if(item instanceof ItemGroup) {
				if(((ItemGroup)item).id.equals(id)) return (ItemGroup)item;
				if(deepSearch){
					ItemGroup g=((ItemGroup)item).getChildGroup(id,true);
					if(g!=null)
						return g;
				}
			}
		}	
		return null;					
	}
	/**
	 * 所有子元素，包括ItemSpec和所有嵌套的ItemGroup
	 * */
	public ArrayList<ItemBase> getChildren()
	{
		ArrayList<ItemBase> arr=new ArrayList<ItemBase>();
		for(ItemBase item:_children)
		{
			arr.add(item);
		}
		return arr;
	}
	/**
	 *获取特定id的ItemSpec
	 * @param id ItemSpec的id
	 * @param deepSearch 如果子元素是ItemGroup，是否继续向下搜寻
	 * @return ItemSpec
	 * 
	 */	
	public ItemSpec getItem(String id,Boolean deepSearch)
	{
		for(ItemBase item:_children)
		{
			if((item instanceof ItemSpec)&&(id.equals(((ItemSpec)item).id)))
				return (ItemSpec)item;
			if(deepSearch&&(item instanceof ItemGroup)){
				ItemSpec ri=((ItemGroup)item).getItem(id,true);
				if(ri!=null)
					return ri;
			}
		}		
		return null;
	}		
	/**
	 *获取特定名称的物品定义
	 * @param name 物品名称
	 * @param deepSearch  如果子元素是ItemGroup，是否继续向下搜寻
	 * **/
	public ItemSpec getItemByName(String name,Boolean deepSearch)
	{
		for(ItemBase item:_children){
			if((name.equals(item.name))&&(item instanceof ItemSpec))
				return (ItemSpec)item;
			if(deepSearch&&(item instanceof ItemGroup)){
				ItemSpec ri=((ItemGroup)item).getItemByName(name,true);
				if(ri!=null)
					return ri;
			}
		}		
		return null;
	}
	/**
	 * 获取ItemGroup的长度 
	 * @return 长度
	 * 
	 */	
	public int getLength()
	{
		return _children.size();
	}
	/**
	 * 在物品组内移除一个物品定义 
	 * @param itm 物品定义
	 * @return 是否移除成功
	 * 
	 */		
	public boolean removeItem(ItemBase itm)
	{
		int i=_children.indexOf(itm);
		if(i>-1){
			_children.remove(i);
			itm.parent=null;
			return true;
		}
		return false;
	}
}
