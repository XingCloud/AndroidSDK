package com.xingcloud.users.services;

import com.xingcloud.core.Config;
import com.xingcloud.core.XingCloud;
import com.xingcloud.event.IEventListener;
import com.xingcloud.event.XingCloudEvent;
import com.xingcloud.items.spec.AsObject;
import com.xingcloud.tasks.net.Remoting.RemotingMethod;
import com.xingcloud.tasks.services.Service;
import com.xingcloud.utils.Utils;

public class RegisterService extends Service {
	
	/**
	 * 注册服务
	 * @param userinfo 用户信息。包含全部用户的信息，必须的字段为username和password。password我们会进行一次md5后再传输给后台，并存储。
	 * @param onSuccess 成功回掉
	 * @param onFail 失败回掉
	 */
	public RegisterService(AsObject account,IEventListener onSuccess,IEventListener onFail)
	{
		super(USER_REGISTER, onSuccess, onFail, RemotingMethod.POST);
		this.command = Config.REGISTER_SERVICE;
		AsObject userinfoData = new AsObject();
		Object password = account.getProperty("password");
		Object username = account.getProperty("username");
		if(password==null || username==null)
		{
			throw new Error("RegisterService --> no username or password specified.");
		}
		XingCloud.instance().getSessionId(true);
		account.setProperty("password", Utils.generateProtectedPassword(password.toString()));
		userinfoData.setProperty("account", account);
		this.params.setProperty("data", userinfoData);
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
