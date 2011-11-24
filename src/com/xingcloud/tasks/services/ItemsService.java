package com.xingcloud.tasks.services;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.content.res.AssetManager;

import com.xingcloud.core.Config;
import com.xingcloud.core.FileHelper;
import com.xingcloud.core.XingCloud;
import com.xingcloud.event.IEventListener;
import com.xingcloud.event.XingCloudEvent;
import com.xingcloud.items.ItemsParser;
import com.xingcloud.utils.DbAssitant;

public class ItemsService extends FileService {

	public ItemsService(IEventListener onSuccess,IEventListener onFail) {
		super(onSuccess, onFail);
		this.type = ITEMS;
		this.command = Config.ITEMSDB_SERVICE;
	}

	public ItemsService() {
		super();
		this.type = ITEMS;
		this.command = Config.ITEMSDB_SERVICE;
	}

	protected void handleSuccess(XingCloudEvent evt)
	{
		delFiles();
		DbAssitant.instance().updateDatabase();
		super.handleSuccess(evt);
	}

	/*
	 * (non-Javadoc)
	 * @see com.xingcloud.tasks.services.Service#applyService(java.lang.Object)
	 */
	public void applyService(Object content)
	{
		ItemsParser.parse(content.toString());
	}

	public boolean sendable()
	{
		if(XingCloud.enableCache)
		{
			String dbFile = Service.ITEMS+md5+XingCloud.instance().appVersionCode+".db";
			String fileName=type+"?"+md5+XingCloud.instance().appVersionCode;
			if(FileHelper.exist(fileName) || FileHelper.exist(dbFile))
			{
				return false;
			}
			else if (!FileHelper.exist(dbFile)) 
			{
				AssetManager assetManager = XingCloud.instance().getContext().getAssets();  
		        try {
		        	InputStream is = assetManager.open("xingcloud/"+dbFile);
		        	byte[] content = readFile(is);
		        	FileHelper.save(dbFile, content);
					return false;
		        } catch (IOException e) {
		        	checkOldCache();
					return true;
				}
			}
			checkOldCache();
			return true;
		}
		else
		{
			checkOldCache();
			return true;
		}
	}

	private byte[] readFile(InputStream inputStream) {  
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();  
        byte buf[] = new byte[1024];  
        int len;  
        try {  
            while ((len = inputStream.read(buf)) != -1) {  
                outputStream.write(buf, 0, len);  
            }  
            outputStream.close();  
            inputStream.close();  
        } catch (IOException e) {  
        }  
        return outputStream.toByteArray();  
    }
}
