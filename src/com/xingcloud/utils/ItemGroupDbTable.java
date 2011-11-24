package com.xingcloud.utils;

public class ItemGroupDbTable {
	public static final String TB_CLOUMN_GROUP_ID= "groupId";//item组Id
    public static final String TB_CLOUMN_PARENT_ID= "parentId";//父group id
    public static final String TB_COLUMN_CHILD_GROUP="child"; //子group 
    public static final String XC_GROUP_TABLE = "GroupTable"; //表名
    
    public String groupId;
    public String parentId;
 
    public ItemGroupDbTable()
    {
    	
    }
}
