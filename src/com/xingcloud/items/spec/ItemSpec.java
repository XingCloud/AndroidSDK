package com.xingcloud.items.spec;

/**
 * 单个物品定义的基类
 * */
public class ItemSpec extends ItemBase {

	public ItemSpec() {
		super();
	}
	
	/**
	 *是否在某个物品定义组内 
	 * @param _group
	 * @return 
	 * 
	 */	
	public Boolean inGroup(String _group)
	{
		ItemGroup par=this.parent;
		while(par!=null)
		{
			if(par.name.equals(_group)) 
				return true;
			
			par=par.parent;
		}
		return false;
	}
}
