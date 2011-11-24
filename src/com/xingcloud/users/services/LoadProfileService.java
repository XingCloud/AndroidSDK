package com.xingcloud.users.services;

import java.util.ArrayList;
import java.util.List;

import android.util.Log;

import com.xingcloud.core.Config;
import com.xingcloud.core.XingCloud;
import com.xingcloud.event.IEventListener;
import com.xingcloud.event.XingCloudEvent;
import com.xingcloud.items.spec.AsObject;
import com.xingcloud.social.PlatformTypes;
import com.xingcloud.social.services.UserInfo;
import com.xingcloud.social.sgdp.GameConfig;
import com.xingcloud.tasks.net.Remoting;
import com.xingcloud.tasks.net.Remoting.RemotingMethod;
import com.xingcloud.tasks.net.RemotingResponse;
import com.xingcloud.tasks.services.Service;
import com.xingcloud.users.AbstractUserProfile;
import com.xingcloud.utils.XingCloudLogger;

public class LoadProfileService extends Service {

	private List<AbstractUserProfile> userProfiles;

	/**
	 * 获取用户信息服务
	 * @param username 用户名
	 * @param password 密码
	 * @param onSuccess 成功回掉接口。此回掉接口的参数XingCloudEvent中存储的data为AbstractUserProfile
	 * @param onFail 失败回掉接口
	 */
	public LoadProfileService(AbstractUserProfile prof,IEventListener onSuccess,IEventListener onFail)
	{
		super(USER_GET_PROFILE, onSuccess, onFail, RemotingMethod.POST);

		this.command = Config.USERPROFILE_SERVICE;
		ArrayList data = new ArrayList();
		data.add(new AsObject(getUserInfo(prof)));
		this.params.setProperty("data", data);
		
		userProfiles = new ArrayList<AbstractUserProfile>();
		userProfiles.add(prof);
	}

	/**
	 * 批量获取用户信息服务
	 * @param profs 用户列表
	 * @param onSuccess 成功回掉接口。此回掉接口的参数XingCloudEvent中存储的data为ArrayList，存储了一系列的AbstractUserProfile
	 * @param onFail 失败回掉接口
	 */
	public LoadProfileService(List<AbstractUserProfile> profs,IEventListener onSuccess,IEventListener onFail)
	{
		super(USER_GET_PROFILE, onSuccess, onFail, RemotingMethod.POST);
		this.command = Config.USERPROFILE_SERVICE;

		ArrayList data = new ArrayList();

		int length = profs.size();
		for(int i=0;i<length;i++)
		{
			AbstractUserProfile prof = profs.get(i);
			
			if(prof==null)
				continue;

			if(!(prof.getUid().equals("")))
			{
				data.add(new AsObject(getUserInfo(prof)));		
			}
			else
			{

				data.add(0,new AsObject(getUserInfo(prof)));
			}
		}

		this.params.setProperty("data", data);
		
		userProfiles = profs;

	}

	private String getUserInfo(AbstractUserProfile prof)
	{
		if(!(prof.getUid().equals("")))
		{
			return "{gameUserId:"+prof.getUid()+"}";
		}
		else
		{
			List<UserInfo> infos = prof.getUserInfo();
			if(infos==null || infos.size()==0)
			{
				XingCloudLogger.log(XingCloudLogger.ERROR,"LoadProfileService->getUserInfo : Try to load an UserProfile that has no account information.(None uid nor sns UserInfo)");
				return "{}";
			}
			else
			{
				UserInfo info = infos.get(0);
				String platformAppId = ""; 
				if(info.getPlatform()==PlatformTypes.XINGCLOUD_PASSPORT)
				{
					platformAppId = XingCloud.instance().getAppMetadata("XINGCLOUD_GAME_APPID");
				}
				else
				{
					platformAppId = GameConfig.instance().getSNSAppId(info.getPlatform());
					if(platformAppId==null)
						platformAppId = "";
				}
				
				//平台用户uid
				String platformUserId=info.getUid();

				return "{platformAppId:"+platformAppId+",platformUserId:"+platformUserId+"}";
			}
		}
	}

	protected void handleSuccess(XingCloudEvent evt)
	{
		RemotingResponse resp = ((Remoting)evt.getTarget()).response;

		Object data = (Object)resp.getData();
		ArrayList infos = (ArrayList) data;

		int length = userProfiles.size();
		for(int i=0;i<length;i++)
		{
			AbstractUserProfile prof = userProfiles.get(i);
			Object info = infos.get(i);
			if(info==null || !(info instanceof AsObject))
			{
				prof.updateUserData(null);
				continue;
			}
			else
				prof.updateUserData(((AsObject)infos.get(i)));
		}

		super.handleSuccess(evt);
	}

	protected void handleFail(XingCloudEvent evt)
	{
		super.handleFail(evt);
	}

}
