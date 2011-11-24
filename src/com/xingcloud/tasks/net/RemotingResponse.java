package com.xingcloud.tasks.net;

import java.nio.charset.Charset;

import org.apache.http.HttpResponse;

import com.xingcloud.items.spec.AsObject;

public class RemotingResponse {

	public static RemotingResponse fromAsObject(AsObject resp)
	{
		RemotingResponse response=new RemotingResponse();
		if(resp==null)
			return response;
		response.analyzeContent(resp);

		return response;
	}
	public static RemotingResponse fromBytes(byte[] resp)
	{
		RemotingResponse response=new RemotingResponse();
		response.rawData = resp;
		response.content = new String(resp,Charset.forName("UTF-8"));
		return response;
	}

	public static RemotingResponse fromString(String resp)
	{
		RemotingResponse response=new RemotingResponse();
		response.rawData = resp.getBytes(Charset.forName("UTF-8"));
		response.content = resp;
		return response;
	}

	/**
	 * message在后台执行的状态代码，辨别错误
	 */
	private int code = 0;
	private String content;
	/**
	 * 执行一个message后可能会返回的数据（Object）
	 */
	private Object data;
	//public HttpResponse httpResponse;
	private int httpResponseCode = -1;
	
	public void setHttpStatusLineCode(int code)
	{
		httpResponseCode = code;
	}

	/**
	 * 执行此message的id
	 */
	private int id;

	/**
	 * message在后台执行返回的消息
	 */
	private String message;  

	private byte[] rawData;

	private RemotingResponse()
	{
	}

	private void analyzeContent(AsObject contentAsObj)
	{
		if(contentAsObj.toJSONString().equals("{}"))
		{
			id = -1;
			code = 200;
			message = "";
			data = null;
		}
		else
		{
			Object idObj = contentAsObj.getProperty("id");
			if(idObj!=null)
			{
				id=Integer.parseInt(idObj.toString());
			}
			else
			{
				id = -1;
			}
			
			Object codeObj = contentAsObj.getProperty("code");
			if(idObj!=null)
			{
				code=Integer.parseInt(codeObj.toString());
			}
			else
			{
				code = 501;
			}
			
			message=contentAsObj.getStringProperty("message");
			data = contentAsObj.getProperty("data");
		}
	}

	public int getCode() {
		if(data==null)
		{
			analyzeContent(new AsObject(content));
		}
		return code;
	}

	public String getContent() {  
		return content;  
	}

	public AsObject getContentAsObject() {

		if((content==null) || content.trim().length()==0)
			return null;

		AsObject contentAsObject = new AsObject(content);
		if(data==null)
		{
			analyzeContent(new AsObject(content));
		}
		return contentAsObject;
	}

	public Object getData() {

		if(data==null)
		{
			analyzeContent(new AsObject(content));
		}

		return data;
	}

	public int getHttpResponseStatusCode()
	{
		if(httpResponseCode==-1 && content!=null && content.length()>0)
			return 200;
		else if(httpResponseCode!=-1)
			return httpResponseCode;
		else
			return 500;
	}

	public int getID() {
		if(data==null)
		{
			analyzeContent(new AsObject(content));
		}
		return id;
	}


	public String getMessage() {
		if(data==null)
		{
			analyzeContent(new AsObject(content));
		}
		return message;
	}

	public byte[] getRawData ()
	{
		return rawData;
	}

	public boolean isSuccess()
	{
		return getCode()==200;
	}
}
