package com.xingcloud.sfs;

import com.xingcloud.core.Config;
import com.xingcloud.core.XingCloud;
import com.xingcloud.event.EventDispatcher;
import com.xingcloud.event.IEventListener;


public class SFSManager extends EventDispatcher implements IEventListener {
	
	
	private static SFSManager _instance;
	
	public static SFSManager instance()
	{
		if(_instance==null)
			_instance = new SFSManager();
		
		return _instance;
	}
	
	final public SmartFox SFSClient = new SmartFox();
	
	public SFSManager()
	{
		if(_instance!=null)
			throw new Error("Can not init SFSManager manully, use instance() instead.");
	}
	
	public void destroy()
	{
		if(SFSClient != null) {
			SFSClient.removeEventListener(SFSEvent.CONNECTION, this);
			SFSClient.removeEventListener(SFSEvent.CONNECTION_RESUME, this);
			SFSClient.removeEventListener(SFSEvent.EXTENSION_RESPONSE, this);
			SFSClient.disconnect();
		}
	}

	@Override
	public void dispatch(BaseEvent event) throws SFSException {
        if(event.getType().equals(SFSEvent.EXTENSION_RESPONSE))
        {
        	String extension = event.getArguments().get("cmd").toString();
        	if(extension.equals(XingCloud.SFS_XINGCLOUD_SERVICE_TOKEN))
        		this.dispatchEvent(new XingCloudSFSEvent(XingCloudSFSEvent.EXTENSION_SERVICE,event));
        }
        else if (event.getType().equals(SFSEvent.CONNECTION))
	    {
        	String account = "";
        	String password = "";
        	if(Config.getConfig("sfs_account")!=null)
        		account = Config.getConfig("sfs_account").toString();
        	if(Config.getConfig("sfs_password")!=null)
        		password = Config.getConfig("sfs_password").toString();
        	SFSClient.send(new LoginRequest(account,password, XingCloud.SFS_XINGCLOUD_ZONE));
	    }
        else if (event.getType().equals(SFSEvent.CONNECTION_RESUME))
        {
        	//TODO handle resume event
        }
	}

	public void init(final String ip, final int port)
	{	
		new Thread() {
			@Override
			public void run() {
				SFSClient.connect(ip,port);
			}
		}.start();
		
		SFSClient.addEventListener(SFSEvent.CONNECTION, this);
		SFSClient.addEventListener(SFSEvent.CONNECTION_RESUME, this);
		SFSClient.addEventListener(SFSEvent.EXTENSION_RESPONSE, this);
	}

	public boolean isActive()
	{
		return SFSClient.isConnected();
	}
}
