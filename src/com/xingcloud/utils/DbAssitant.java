package com.xingcloud.utils;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import com.xingcloud.core.Config;
import com.xingcloud.core.XingCloud;
import com.xingcloud.items.ItemsParser;
import com.xingcloud.items.spec.ItemBase;
import com.xingcloud.items.spec.ItemSpec;
import com.xingcloud.tasks.services.Service;



/**
*(non-javadoc)
*/
public class DbAssitant {
	public static String dbItemAttributeSplit = "`";
	private static DbAssitant _instance;
	/**
	 * 获取DbAssitant实例 
	 * @return DbAssitant实例 
	 * 
	 */		
	public static DbAssitant instance()
	{
		if(_instance==null)
		{
			_instance = new DbAssitant();
			_instance.openDatabase();
		}			
		return _instance;
	}
	private SQLiteDatabase db = null;
	
	public synchronized void deleteGroupById(String groupId)
	{
		String[] childIds = getChildGroupsById(groupId);
		try
		{
			if(!_instance.isDbOpen() )
	    	{
	    		 openDatabase();
	    	}
			 
			if(null == childIds || childIds.length <=0)
			{
				String whereArgs= ItemGroupDbTable.TB_CLOUMN_GROUP_ID+" =?";
		        String[] whereValue = {groupId };
				db.delete(ItemGroupDbTable.XC_GROUP_TABLE, whereArgs, whereValue);
			}
			else
			{
				String whereArgs= ItemGroupDbTable.TB_CLOUMN_GROUP_ID+" =?";
				String[] whereValue = childIds.clone();
				whereValue[childIds.length] = groupId;
				db.delete(ItemGroupDbTable.XC_GROUP_TABLE, whereArgs, whereValue);
			}
		}
		catch (Exception e) {
    		e.printStackTrace();
		}
	}
	public synchronized void deleteItemsByGroupId(String groupId)
	{
		String[] childIds = getChildGroupsById(groupId);
		try
		{
			if(!_instance.isDbOpen() )
	    	{
	    		 openDatabase();
	    	}
			 
			if(null == childIds || childIds.length <=0)
			{
				String whereArgs= ItemDbTable.TB_CLOUMN_GROUP_ID+" =?";
		        String[] whereValue = {groupId };
				db.delete(ItemDbTable.XC_ITEM_TABLE, whereArgs, whereValue);
			}
			else
			{
				String whereArgs= ItemDbTable.TB_CLOUMN_GROUP_ID+" =?";
				String[] whereValue = childIds.clone();
				whereValue[childIds.length] = groupId;
				db.delete(ItemDbTable.XC_ITEM_TABLE, whereArgs, whereValue);
			}
		}
		catch (Exception e) {
    		e.printStackTrace();
		}
	}
	
	public String getAllChildGroups()
	{
		Cursor c = null;
		String result = null;
		try{
    		
			if(!_instance.isDbOpen() )
	    	{
	    		 openDatabase();
	    	}
            String[] columns = { ItemGroupDbTable.TB_COLUMN_CHILD_GROUP};
            c = db.query(ItemGroupDbTable.XC_GROUP_TABLE, columns, null, null, null, null,
                    null);
            c.moveToFirst();
           
            if (c != null && c.getCount() != 0) {
                final int length = c.getCount();
                for (int i = 0; i < length; i++) {
                    c.moveToPosition(i);
                    if(i == 0)
                    {
                    	result += c.getString(c.getColumnIndex(ItemGroupDbTable.TB_COLUMN_CHILD_GROUP));
                    }
                    else
                    {
                    	result += ("," + c.getString(c.getColumnIndex(ItemGroupDbTable.TB_COLUMN_CHILD_GROUP)));
                    }
                }
            }
    	}
		finally
		{
			if(c!=null)
			{
				c.close();
				c =null;
			}
    	}
		return result;
	}
	/**
	 * 
	 * @param groupId
	 * @return
	 */
	public String[] getChildGroupsById(String groupId)
	{
		String[] childGroupIds = null;
		String childId = null;
        Cursor c = null;
    	try{
    		if(!_instance.isDbOpen() )
	    	{
	    		 openDatabase();
	    	}
	     
	        
	        String whereArgs= ItemGroupDbTable.TB_CLOUMN_GROUP_ID+" =?";
	        String[] whereValue = {groupId };
	       
	        String[] columns = {ItemGroupDbTable.TB_COLUMN_CHILD_GROUP};
	        c = db.query(ItemGroupDbTable.XC_GROUP_TABLE, columns, whereArgs, whereValue, null, null,
	                null);
	        if(c == null || c.getCount() <= 0)
	        {
	        	return null;
	        }
	        c.moveToFirst();
	        if (c != null && c.getCount() != 0) {
	        	c.moveToPosition(0);
	        	childId = c.getString(c.getColumnIndex(ItemGroupDbTable.TB_COLUMN_CHILD_GROUP));
	        }
	        childGroupIds = childId.split(",");
    	}catch (Exception e) {
    		e.printStackTrace();
		}finally{
			if(c!=null&&!c.isClosed()){
				c.close();
			}
		}
		
        return childGroupIds;
	}
	public String getChildGroupsByIdReturnString(String groupId)
	{
		
		String childId = null;
        Cursor c = null;
    	try{
    		if(!_instance.isDbOpen() )
	    	{
	    		 openDatabase();
	    	}
	     
	        
	        String whereArgs= ItemGroupDbTable.TB_CLOUMN_GROUP_ID+" =?";
	        String[] whereValue = {groupId };
	       
	        String[] columns = {ItemGroupDbTable.TB_COLUMN_CHILD_GROUP};
	        c = db.query(ItemGroupDbTable.XC_GROUP_TABLE, columns, whereArgs, whereValue, null, null,
	                null);
	        if(c == null || c.getCount() <= 0)
	        {
	        	return null;
	        }
	        c.moveToFirst();
	        if (c != null && c.getCount() != 0) {
	        	c.moveToPosition(0);
	        	childId = c.getString(c.getColumnIndex(ItemGroupDbTable.TB_COLUMN_CHILD_GROUP));
	        }
	        
    	}catch (Exception e) {
    		e.printStackTrace();
		}finally{
			if(c!=null&&!c.isClosed()){
				c.close();
			}
		}
		
        return childId;
	}
	public ArrayList<ItemBase> getChildItemsByGroupId(String id)
	{
		ArrayList<ItemBase> items = null;
		if(!_instance.isDbOpen() )
    	{
    		 openDatabase();
    	}
		Cursor c = null;
		try
		{
	        String whereArgs= ItemDbTable.TB_CLOUMN_GROUP_ID+" =?";
	        String[] whereValue = {id};
	        
	        c = db.query(ItemDbTable.XC_ITEM_TABLE, null, whereArgs, whereValue, null, null, null);
	        
	        c.moveToFirst();
	        
	        if (c != null && c.getCount() != 0) 
            {
	        	items = new ArrayList<ItemBase>();
	        	ItemBase it =  null;
                final int length = c.getCount();
                for (int i = 0; i < length; i++) {
                    c.moveToPosition(i);
                    it= new ItemBase();
                    it.setId(c.getString(c.getColumnIndex(ItemDbTable.TB_CLOUMN_ITEM_ID)));
                    
                    String attrs = c.getString(c.getColumnIndex(ItemDbTable.TB_CLOUMN_ITEM_ATTRIBUTES));
                    it = ItemsParser.getModel(attrs);
                    Utils.parseProperty(it, attrs);
                    
                    items.add(it);
                    it= null;
                   // result = c.getString(c.getColumnIndex(ItemDbTable.TB_CLOUMN_ITEM_ATTRIBUTES));
                }
            }
		}
		catch(Exception e)
		{
			e.printStackTrace();
			items = null;
		}
		finally
		{
    		if(c!=null){
    			c.close();
    		}
    		
    	}
		return items;
	}
	public HashMap<String,String> getGroupRelationMap()
	{
		HashMap<String,String> relation = null;
		Cursor c = null;
		try{
    		
			if(!_instance.isDbOpen() )
	    	{
	    		 openDatabase();
	    	}
            String[] columns = {ItemGroupDbTable.TB_CLOUMN_GROUP_ID, ItemGroupDbTable.TB_COLUMN_CHILD_GROUP};
            c = db.query(ItemGroupDbTable.XC_GROUP_TABLE, columns, null, null, null, null,
                    null);
            c.moveToFirst();
           
            if (c != null && c.getCount() != 0) {
                final int length = c.getCount();
                relation = new HashMap<String,String>();
                for (int i = 0; i < length; i++) {
                    c.moveToPosition(i);
                    String groupID = c.getString(c.getColumnIndex(ItemGroupDbTable.TB_CLOUMN_GROUP_ID));
                    String childID = c.getString(c.getColumnIndex(ItemGroupDbTable.TB_COLUMN_CHILD_GROUP));
                    if(groupID.equals("topRoot"))
                    {
                    	String value = relation.get(groupID);
                    	if(value==null)
                    	{
                    		relation.put(groupID,childID);
                    	}
                    	else
                    	{
                    		relation.put(groupID,value+","+childID);
                    	}
                    }
                    else
                    {
                    	relation.put(groupID,childID);
                    }
                    
                }
            }
    	}
		finally
		{
			if(c!=null)
			{
				c.close();
				c =null;
			}
    	}
		return relation;
	}
	/**
	 * 
	 * @return
	 */
	public HashMap<String,String> getGroups()
	{
		Cursor c = null;
		HashMap<String,String> result = null;
		try{
    		
			if(!_instance.isDbOpen() )
	    	{
	    		 openDatabase();
	    	}
            String[] columns = {ItemGroupDbTable.TB_CLOUMN_GROUP_ID,ItemGroupDbTable.TB_COLUMN_CHILD_GROUP};
            c = db.query(ItemGroupDbTable.XC_GROUP_TABLE, columns, null, null, null, null,
                    null);
            c.moveToFirst();
           
            if (c != null && c.getCount() != 0) {
            	result = new HashMap<String,String>();
                final int length = c.getCount();
                for (int i = 0; i < length; i++) {
                    c.moveToPosition(i);
                    result.put(c.getString(c.getColumnIndex(ItemGroupDbTable.TB_CLOUMN_GROUP_ID)), c.getString(c.getColumnIndex(ItemGroupDbTable.TB_COLUMN_CHILD_GROUP)));
                }
            }
    	}
		finally
		{
			if(c!=null)
			{
				c.close();
				c =null;
			}
    	}
		return result;
	}
	
	/**
	 * 根据ItemId查询item的属性
	 * @param itemId
	 * @return
	 */
	public String getItemAttrsByItemId(String itemId)
	{
		if(!_instance.isDbOpen() )
    	{
    		 openDatabase();
    	}
		String result =null;
		Cursor c = null;
		try
		{
	        String whereArgs= ItemDbTable.TB_CLOUMN_ITEM_ID+" =?";
	        String[] whereValue = {itemId};
	        
	        c = db.query(ItemDbTable.XC_ITEM_TABLE, null, whereArgs, whereValue, null, null, null);
	        
	        c.moveToFirst();
	           
            if (c != null && c.getCount() != 0) 
            {
                final int length = c.getCount();
                for (int i = 0; i < length; i++) {
                    c.moveToPosition(i);
                    
                    result = c.getString(c.getColumnIndex(ItemDbTable.TB_CLOUMN_ITEM_ATTRIBUTES));
                }
            }
    	}
		catch(Exception e)
	    {
	    	throw new Error(e.getMessage());
	    }
		finally
		{
    		if(c!=null){
    			c.close();
    		}
    		
    	}
		return (null==result)?null:result;
	}
	/**
	 * 根据group id及其子group id获取所有itemid及属性
	 * @param groupIds
	 * @return
	 */
	public HashMap<String, String> getItemsByGroupIds(String[] groupIds)
	{
		if(!_instance.isDbOpen() )
    	{
    		 openDatabase();
    	}
		Cursor c = null;
		HashMap<String, String> result=new HashMap<String, String>();
		try
		{
			 String whereArgs= ItemDbTable.TB_CLOUMN_GROUP_ID+" =?";
		        String[] whereValue = groupIds.clone();
		        
		        c = db.query(ItemDbTable.XC_ITEM_TABLE, null, whereArgs, whereValue, null, null, null);
		        
		        c.moveToFirst();
		           
	            if (c != null && c.getCount() != 0) 
	            {
	                final int length = c.getCount();
	                for (int i = 0; i < length; i++) {
	                    c.moveToPosition(i);
	                    result.put(c.getString(c.getColumnIndex(ItemDbTable.TB_CLOUMN_ITEM_ID)), 
	                    		c.getString(c.getColumnIndex(ItemDbTable.TB_CLOUMN_ITEM_ATTRIBUTES)));
	                }
	            }
		}
		catch(Exception e)
	    {
	    	throw new Error(e.getMessage());
	    }
		finally
		{
    		if(c!=null){
    			c.close();
    		}
    		
    	}
		return result;
	}
	
	
	public ItemSpec getItemSpec(String itemId,String groupId)
	{
		ItemSpec itemSpec = null;
		if(!_instance.isDbOpen() )
    	{
    		 openDatabase();
    	}
		Cursor c = null;
		String whereArgs = null;
		String[] whereValue = null;
		try
		{
			if(null == itemId || itemId.trim().length() <=0)
			{
				XingCloudLogger.log(XingCloudLogger.WARN,"DBAssistant->getItemSpec:please input the itemSpec id!");
				return null;
			}
			if(null == groupId || groupId.trim().length()<= 0)
			{
				whereArgs= ItemDbTable.TB_CLOUMN_ITEM_ID+" =?";
				whereValue =new String[1];
				whereValue[0] = itemId;
			}
			else
			{
				whereArgs= ItemDbTable.TB_CLOUMN_ITEM_ID+" =?"+" and " + ItemDbTable.TB_CLOUMN_GROUP_ID+" =?";
				whereValue =new String[2];
				whereValue[0] = itemId;
				whereValue[1] = groupId;
			}
			c = db.query(ItemDbTable.XC_ITEM_TABLE, null, whereArgs, whereValue, null, null, null);
		        
	        c.moveToFirst();
	        
	        if (c != null && c.getCount() != 0) 
            {
	        	ItemBase it =  null;
                final int length = c.getCount();
                for (int i = 0; i < length; i++) {
                    c.moveToPosition(i);
                    it= new ItemBase();
                    it.setId(c.getString(c.getColumnIndex(ItemDbTable.TB_CLOUMN_ITEM_ID)));
                    
                    String attrs = c.getString(c.getColumnIndex(ItemDbTable.TB_CLOUMN_ITEM_ATTRIBUTES));
                    it = ItemsParser.getModel(attrs);
                    Utils.parseProperty(it, attrs);
                    if((it instanceof ItemSpec)&&(itemId.equals(((ItemSpec)it).getId())))
                    {
                    	itemSpec = (ItemSpec)it;
                    }
                    it= null;
                   // result = c.getString(c.getColumnIndex(ItemDbTable.TB_CLOUMN_ITEM_ATTRIBUTES));
                }
            }
		}
		catch(Exception e)
		{
			e.printStackTrace();
			itemSpec = null;
		}
		finally
		{
    		if(c!=null){
    			c.close();
    		}
    		
    	}
		return itemSpec;
	}
	
	public ArrayList<ItemSpec> getItemSpecByName(String name,String _groupId)
	{
		ArrayList<ItemSpec> itemSpecArray = null;
		ItemSpec itemSpec = null;
		if(!_instance.isDbOpen() )
    	{
    		 openDatabase();
    	}
		Cursor c = null;
		String whereArgs= null;
		String[] whereValue = null;
		try
		{
			if(_groupId.compareTo("all") == 0)
			{
				c = db.query(ItemDbTable.XC_ITEM_TABLE,  null, null, null, null, null, null);
			}
			else
			{
				whereArgs= ItemDbTable.TB_CLOUMN_GROUP_ID+" =?";
				whereValue = new String[1];
				whereValue[0]= _groupId;
				c = db.query(ItemDbTable.XC_ITEM_TABLE,  null, whereArgs, whereValue, null, null, null);
			}
	        c.moveToFirst();
	           
            if (c != null && c.getCount() != 0) 
            {
                final int length = c.getCount();
                ItemBase it =  null;
                for (int i = 0; i < length; i++) 
                {
                    c.moveToPosition(i);
                    String attrs = c.getString(c.getColumnIndex(ItemDbTable.TB_CLOUMN_ITEM_ATTRIBUTES));
                    if(attrs.contains(name.trim()))
                    {
                    	 it= new ItemBase();
                    	// String attrs = c.getString(c.getColumnIndex(ItemDbTable.TB_CLOUMN_ITEM_ATTRIBUTES));
                         it = ItemsParser.getModel(attrs);
                         Utils.parseProperty(it, attrs);
                         if(it instanceof ItemSpec)
                         {
                        	 itemSpec = (ItemSpec)it;
                        	 itemSpecArray.add(itemSpec);
                        	 itemSpec = null;
                         }
                        
                         it= null;
                         attrs = null;
                    }
                    attrs = null;
                }
            }
		}
		catch(Exception e)
		{
			e.printStackTrace();
			itemSpec = null;
		}
		finally
		{
    		if(c!=null){
    			c.close();
    		}
    		
    	}
		return itemSpecArray;
	}
	
	
	public String getTopGroups()
	{
		Cursor c = null;
		String result = "";
		try{
    		
			if(!_instance.isDbOpen() )
	    	{
	    		 openDatabase();
	    	}
			String whereArgs= ItemGroupDbTable.TB_CLOUMN_GROUP_ID+" =?";
	        String[] whereValue = {"topRoot" };
	       
	        String[] columns = {ItemGroupDbTable.TB_COLUMN_CHILD_GROUP};
	        c = db.query(ItemGroupDbTable.XC_GROUP_TABLE, columns, whereArgs, whereValue, null, null,
	                null);
           
            c.moveToFirst();
           
            if (c != null && c.getCount() != 0) {
            	
                final int length = c.getCount();
                for (int i = 0; i < length; i++) {
                    c.moveToPosition(i);
                    if(i==0)
                    {
                    	result += c.getString(c.getColumnIndex(ItemGroupDbTable.TB_COLUMN_CHILD_GROUP));
                    }
                    else
                    {
                    	result += ("," + c.getString(c.getColumnIndex(ItemGroupDbTable.TB_COLUMN_CHILD_GROUP)));
                    }
                }
            }
    	}
		finally
		{
			if(c!=null)
			{
				c.close();
				c =null;
			}
    	}
		return result;
	}
	
	
	/**
	 * 插入一项item数据到数据到group数据库
	 * @param groupId
	 * @param parentId
	 * @param childId
	 */
	public synchronized void insertGroup(String groupId, String parentId, String childId) 
	{
	    try
	    {
	    	if(!_instance.isDbOpen() )
	    	{
	    		 openDatabase();
	    	}
	        ContentValues contentValues = new ContentValues();
	        contentValues.put(ItemGroupDbTable.TB_CLOUMN_GROUP_ID, groupId);
	        contentValues.put(ItemGroupDbTable.TB_CLOUMN_PARENT_ID, parentId);
	        contentValues.put(ItemGroupDbTable.TB_COLUMN_CHILD_GROUP, childId);
	        db.insert(ItemGroupDbTable.XC_GROUP_TABLE, null, contentValues);
		}
	    catch(Exception e)
	    {
	    	throw new Error(e.getMessage());
	    }
	}
	/**
	 * 插入一项item数据到数据到item数据库
	 * @param itemId
	 * @param groupId
	 * @param itemAttrs
	 */
	public synchronized void insertItem(String itemId, String groupId, String itemAttrs) 
	{
	    try
	    {
	    	if(!_instance.isDbOpen() )
	    	{
	    		 openDatabase();
	    	}
	        ContentValues contentValues = new ContentValues();
	        contentValues.put(ItemDbTable.TB_CLOUMN_ITEM_ID, itemId);
	        contentValues.put(ItemDbTable.TB_CLOUMN_GROUP_ID, groupId);
	        contentValues.put(ItemDbTable.TB_CLOUMN_ITEM_ATTRIBUTES, itemAttrs);
	        db.insert(ItemDbTable.XC_ITEM_TABLE, null, contentValues);
		}
	    catch(Exception e)
	    {
	    	throw new Error(e.getMessage());
	    }
	}
	/************
	 * 
	 * @return true 数据库打开
	 */
	protected Boolean isDbOpen()
	{
		if(db == null)
		{
			throw new Error("DBAssistant->isDbOpen : Database is not open!");
		}
		else
		{
			if(db.isOpen())
			{
				return true;
			}
			else
			{
				return false;
			}
		}
		
	}
	/***********************
	 * 
	 * @return true 数据库只读
	 */
	public Boolean isDbReadOnly()
	{
		if(db == null)
		{
			throw new Error("DBAssistant->isDbReadOnly : Database is not open!");
		}
		else
		{
			if(db.isReadOnly())
			{
				return true;
			}
			else
			{
				return false;
			}
		}
	}
	
	public Boolean isItemIn(String itemId)
	{
		if(!_instance.isDbOpen() )
    	{
    		 openDatabase();
    	}
		Cursor c = null;
		try
		{
	        String whereArgs= ItemDbTable.TB_CLOUMN_ITEM_ID+" =?";
	        String[] whereValue = {itemId};
	        
	        c = db.query(ItemDbTable.XC_ITEM_TABLE, null, whereArgs, whereValue, null, null, null);
	        
	        c.moveToFirst();
	           
            if (c != null && c.getCount() != 0) 
            {
               return true;
            }
    	}
		catch(Exception e)
	    {
	    	throw new Error(e.getMessage());
	    }
		finally
		{
    		if(c!=null){
    			c.close();
    		}
    		
    	}
		return false;
	}
	
	
	private synchronized void openDatabase(){
		if(null == db)
		{
			try{
				db = SQLiteDatabase.openOrCreateDatabase("/data/data/"+XingCloud.instance().getContext().getPackageName()+"/files/"+Service.ITEMS+Config.getStringConfig(Service.ITEMS+"md5")+XingCloud.instance().appVersionCode+".db", null);  
			} catch (SQLiteException e)
			{
				e.printStackTrace();
			}
		}
	}
	public void updateDatabase()
	{
		//dbHelper.onUpgrade(db, oldVersion, newVersion)
		
		db.execSQL("drop table if exists " +ItemDbTable.XC_ITEM_TABLE);
        db.execSQL("drop table if exists " +ItemGroupDbTable.XC_GROUP_TABLE);
        onCreate(db);
	}
	/********************************
	 * 根据groupId更新group的关系内容
	 * @param groupId
	 * @param parentId
	 * @param childId
	 */
	public synchronized void updateGroup(String groupId, String parentId, String childId) 
	{
	    try
	    {
	    	if(!_instance.isDbOpen() )
	    	{
	    		 openDatabase();
	    	}
	        ContentValues contentValues = new ContentValues();
	        contentValues.put(ItemGroupDbTable.TB_CLOUMN_GROUP_ID, groupId);
	        contentValues.put(ItemGroupDbTable.TB_CLOUMN_PARENT_ID, parentId);
	        contentValues.put(ItemGroupDbTable.TB_COLUMN_CHILD_GROUP, childId);

	        String whereArgs= ItemGroupDbTable.TB_CLOUMN_GROUP_ID+" =?";
	        String[] whereValue = {groupId};
	        db.update(ItemGroupDbTable.XC_GROUP_TABLE, contentValues, whereArgs, whereValue);
		}
	    catch(Exception e)
	    {
	    	throw new Error(e.getMessage());
	    }
	}
	
	/*******************
	 * 根据itemId更新item的属性
	 * @param itemId
	 * @param groupId
	 * @param itemAttrs
	 */
	public void updateItemsById(String itemId,String groupId, String itemAttrs)
	{
		if(!_instance.isDbOpen() )
    	{
    		 openDatabase();
    	}
		try
		{
			ContentValues contentValues = new ContentValues();
			contentValues.put(ItemDbTable.TB_CLOUMN_ITEM_ID, itemId);
	        contentValues.put(ItemDbTable.TB_CLOUMN_GROUP_ID, groupId);
	        contentValues.put(ItemDbTable.TB_CLOUMN_ITEM_ATTRIBUTES, itemAttrs);
	        String whereArgs= ItemDbTable.TB_CLOUMN_ITEM_ID+" =?";
	        String[] whereValue = {itemId};
	        db.update(ItemDbTable.XC_ITEM_TABLE, contentValues, whereArgs, whereValue);
		}
		catch(Exception e)
	    {
	    	throw new Error(e.getMessage());
	    }
	        
	}
	
    public void onCreate(SQLiteDatabase db) {
  
        final String CREATE_TABLE_ITEMS = "CREATE TABLE "
            + ItemDbTable.XC_ITEM_TABLE
            + "('id' INTEGER PRIMARY KEY  NOT NULL ,"
            + ItemDbTable.TB_CLOUMN_ITEM_ID+ " TEXT,"
            + ItemDbTable.TB_CLOUMN_GROUP_ID + " TEXT,"
            + ItemDbTable.TB_CLOUMN_ITEM_ATTRIBUTES + " TEXT,"
            + " 'browseTime' DATETIME DEFAULT CURRENT_TIMESTAMP)";
        db.execSQL(CREATE_TABLE_ITEMS);
        final String CREATE_TABLE_GROUP = "CREATE TABLE "
            + ItemGroupDbTable.XC_GROUP_TABLE
            + "('id' INTEGER PRIMARY KEY  NOT NULL ,"
            + ItemGroupDbTable.TB_CLOUMN_GROUP_ID+ " TEXT,"
            + ItemGroupDbTable.TB_CLOUMN_PARENT_ID + " TEXT,"
            + ItemGroupDbTable.TB_COLUMN_CHILD_GROUP + " TEXT,"
            + " 'browseTime' DATETIME DEFAULT CURRENT_TIMESTAMP)";
        db.execSQL(CREATE_TABLE_GROUP);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    	if(oldVersion<newVersion){
    		
            db.execSQL("drop table if exists " +ItemDbTable.XC_ITEM_TABLE);
            db.execSQL("drop table if exists " +ItemGroupDbTable.XC_GROUP_TABLE);
            onCreate(db);
    	}
    }
}