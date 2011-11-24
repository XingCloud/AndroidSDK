package com.xingcloud.tasks.services;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.util.Log;

import com.xingcloud.core.Config;
import com.xingcloud.core.FileHelper;
import com.xingcloud.core.XingCloud;
import com.xingcloud.event.IEventListener;
import com.xingcloud.event.XingCloudEvent;
import com.xingcloud.items.spec.AsObject;
import com.xingcloud.tasks.base.TaskEvent;
import com.xingcloud.tasks.net.Remoting;
import com.xingcloud.tasks.net.Remoting.RemotingMethod;
import com.xingcloud.utils.XingCloudLogger;
import com.xingcloud.utils.Utils;

public class FileService extends Service {
	
	public FileService(IEventListener onSuccess,IEventListener onFail) {
		super(FILE, onSuccess, onFail, RemotingMethod.POST);
	}
	
	public FileService() {
		super(FILE, null, null, RemotingMethod.POST);
	}

	/**
	 * 解析服务内容，应用服务
	 * @param content 文件内容
	 */
	public void applyService(Object content)
	{
	}
	
	public Remoting getExecutor()
	{
		params.setProperty("data", new AsObject("{lang:"+Config.languageType()+",timestamp:"+timestamp+"}"));
		Remoting serviceExecutor=new Remoting(command,params,method,true,Config.fileGateway());
		serviceExecutor.addEventListener(TaskEvent.TASK_COMPLETE,onExecuted);
		serviceExecutor.addEventListener(TaskEvent.TASK_ERROR,onExecutedError);
		
		return serviceExecutor;
	}
	
	protected void handleSuccess(XingCloudEvent evt)
	{
		Remoting rem = (Remoting)(evt.getTarget());
		
		String contentStr = rem.response.getContent();
		if(contentStr==null)
		{
			this.handleFail(evt);
		}
		else
		{
			boolean validity = true;
			if(XingCloud.checkFileValidity)
			{
				String contentMd5 = Utils.MD5(contentStr.getBytes());
				if(!(contentMd5.equals(this.md5)))
					validity = false;
			}
			
			if(validity)
			{
				FileSavingThread saveThread = new FileSavingThread(type+"?"+md5+XingCloud.instance().appVersionCode,rem.response.getContent().getBytes());
				saveThread.start();
				applyService(contentStr);
			}
			else
			{
				XingCloudLogger.log(XingCloudLogger.ERROR, "FileService->handleSuccess : Incomplete file cotent for "+this.type);
				this.handleFail(evt);
			}
		}
	}
	
	protected void checkOldCache()
	{
		SharedPreferences settings = XingCloud.instance().getActivity().getSharedPreferences("XingCloudSDK", Activity.MODE_PRIVATE);
		
		String originMD5 = settings.getString(type+Config.languageType(), "");
		if(!originMD5.equals(""))
		{
			FileHelper.delete(type+"?"+originMD5+XingCloud.instance().appVersionCode);
			FileHelper.delete(type+originMD5+XingCloud.instance().appVersionCode+".db");
		}
		
		SharedPreferences.Editor editor = settings.edit();  
		editor.putString(type+Config.languageType(), md5);
		editor.commit();  
	}
	
	public boolean sendable()
	{
		if(XingCloud.enableCache)
		{
			String fileName=type+"?"+md5+XingCloud.instance().appVersionCode;
			if(FileHelper.exist(fileName))
			{
				return false;
			}
			else
			{
		        try {  
		        	
					AssetManager assetManager = XingCloud.instance().getContext().getAssets();  
		        	InputStream is = assetManager.open("xingcloud/"+Config.languageType()+"/"+type+".xml");
		        	String contentString = readTextFile(is);
		        	byte[] contentBytes = contentString.getBytes();
		        	String existMD5 = Utils.MD5(contentBytes);
		        	if(existMD5.equals(md5))
		        	{
		        		FileSavingThread saveThread = new FileSavingThread(type+"?"+md5+XingCloud.instance().appVersionCode,contentBytes);
						saveThread.start();
						applyService(contentString);
						is.close();
						
						return false;
		        	}
		        	else
		        	{
		        		checkOldCache();
		        		is.close();
		        		return true;
		        	}
				} catch (IOException e) {
					checkOldCache();
					return true;
				}
			}
		}
		else
		{
			checkOldCache();
			return true;
		}
	}

	protected String readTextFile(InputStream inputStream) {  
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
        return outputStream.toString();  
    }  
	
	protected void delFiles()
	{
		String[] files = XingCloud.instance().getContext().fileList();
		if(files == null || files.length <=0)
		{
			return;
		}
		else
		{
			for(int i=0; i < files.length; i++)
			{
				if(files[i].contains("items"))
				{
					try
					{
						XingCloud.instance().getContext().deleteFile(files[i]);
					}
					catch(Exception e)
					{
						XingCloudLogger.log(XingCloudLogger.ERROR, e.getMessage());
					}
				}
			}
		}
		
	}
	
	class FileSavingThread extends Thread
	{
		private byte[] content;
		private String fileName;

		public FileSavingThread(String fileName,byte[] content)
		{
			this.fileName = fileName;
			this.content = content;
		}
		@Override
		public void run()
		{
			FileHelper.save(fileName, content);
		}
	}

}
