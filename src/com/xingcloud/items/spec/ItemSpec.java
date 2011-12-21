package com.xingcloud.items.spec;

/**
 * 单个物品定义的基类
 * */
public class ItemSpec extends ItemBase {
	
	protected String groupID="";

	public ItemSpec() {
		super();
	}
	
	/**
	 *是否在某个物品定义组内 
	 * @param _group
	 * @return 
	 * 
	 */	
	public boolean inGroup(String _group)
	{
		if(!groupID.equals(""))
			return true;
		
		ItemGroup par=this.parent;
		while(par!=null)
		{
			if(par.name.equals(_group)) 
				return true;
			
			par=par.parent;
		}
		return false;
	}
	
	public String getGroupId()
	{
		return groupID;
	}
	
	public void setGroupId(String id)
	{
		if(id!=null)
			groupID = id;
	}
}
