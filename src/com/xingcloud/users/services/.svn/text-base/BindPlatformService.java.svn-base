package com.xingcloud.users.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import com.xingcloud.core.Config;
import com.xingcloud.event.IEventListener;
import com.xingcloud.event.XingCloudEvent;
import com.xingcloud.items.spec.AsObject;
import com.xingcloud.tasks.net.Remoting.RemotingMethod;
import com.xingcloud.tasks.services.Service;

public class BindPlatformService extends Service {
	/**
	 * 将某个用户与平台进行绑定
	 * @param platformAppId 平台AppID
	 * @param platformUserId 平台用户ID
	 * @param onSuccess 成功回掉接口
	 * @param onFail 失败回掉接口
	 */
	public BindPlatformService(String platformAppId,String platformUserId,IEventListener onSuccess,IEventListener onFail)
	{
		super(BIND_PLATFORM, onSuccess, onFail, RemotingMethod.POST);
		this.command = Config.BIND_PLATFORM_SERVICE;
		ArrayList data = new ArrayList();
		data.add(new AsObject("{platformAppId:"+platformAppId+",platformUserId:"+platformUserId+"}"));
		this.params.setProperty("data",data );
	}
	
	/**
	 * 将某个用户与多个平台进行绑定
	 * @param bindInfo 绑定信息，这个数据为一个数组。数组存储一系列的HashMap。
	 * 			HashMap包括全部的平台相关信息，目前包括平台AppID(platformAppId)和平台用户ID(platformUserId)
	 * @param onSuccess 成功回掉接口
	 * @param onFail 失败回掉接口
	 */
	public BindPlatformService(List<HashMap<String,String>> bindInfo,IEventListener onSuccess,IEventListener onFail)
	{
		super(BIND_PLATFORM, onSuccess, onFail, RemotingMethod.POST);
		this.command = Config.BIND_PLATFORM_SERVICE;
		
		ArrayList data = new ArrayList();
		
		int length = bindInfo.size();
		for(int i=0;i<length;i++)
		{
			AsObject asinfo = new AsObject();
			HashMap<String,String> info = bindInfo.get(i);
			Iterator it = info.entrySet().iterator();

			while(it.hasNext())
			{
				Entry ent = (Entry)it.next();
				String key = ent.getKey().toString();
				String value = ent.getValue().toString();
				asinfo.setProperty(key, value);
			}
			data.add(asinfo);
		}
		
		this.params.setProperty("data",data);
	}
	
	protected void handleSuccess(XingCloudEvent evt)
	{
		super.handleSuccess(evt);
	}
	
	protected void handleFail(XingCloudEvent evt)
	{
		super.handleFail(evt);
	}
}
