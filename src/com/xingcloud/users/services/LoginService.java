package com.xingcloud.users.services;

import com.xingcloud.core.Config;
import com.xingcloud.core.XingCloud;
import com.xingcloud.event.IEventListener;
import com.xingcloud.event.XingCloudEvent;
import com.xingcloud.items.spec.AsObject;
import com.xingcloud.tasks.net.Remoting;
import com.xingcloud.tasks.net.Remoting.RemotingMethod;
import com.xingcloud.tasks.net.RemotingResponse;
import com.xingcloud.tasks.services.Service;
import com.xingcloud.utils.Utils;

public class LoginService extends Service {
	
	private String user;
	private boolean autoLoad;
	/**
	 * 登陆服务
	 * @param username 用户名
	 * @param password 密码
	 * @param onSuccess 成功回掉接口
	 * @param onFail 失败回掉接口
	 */
	public LoginService(String username,String password,IEventListener onSuccess,IEventListener onFail,boolean autoLoadData)
	{
		super(USER_LOGIN, onSuccess, onFail, RemotingMethod.POST);
		user = username;
		autoLoad = autoLoadData;
		this.command = Config.LOGIN_SERVICE;
		this.params.setProperty("data", new AsObject("{'username':'"+username+"','password':'"+Utils.generateProtectedPassword(password)+"'}"));
	}
	
	protected void handleSuccess(XingCloudEvent evt)
	{
		Config.setConfig("sig_user", user);
		if(autoLoad)
		{
			try
			{
				XingCloud.instance().setOwnerUser(XingCloud.instance().createOwnerUser());
			}
			catch(Exception except)
			{
				this.handleFail(evt);
				return;
			}
			RemotingResponse response = ((Remoting)(evt.getTarget())).response;
			XingCloud.getOwnerUser().updateUserData(((AsObject)response.getData()));
		}
		super.handleSuccess(evt);
	}
	
	protected void handleFail(XingCloudEvent evt)
	{
		super.handleFail(evt);
	}
}
