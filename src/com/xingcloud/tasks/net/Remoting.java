package com.xingcloud.tasks.net;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Random;
import java.util.TreeMap;
import java.util.zip.GZIPInputStream;

import javax.net.ssl.SSLHandshakeException;

import org.apache.http.Header;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.NoHttpResponseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.ExecutionContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.os.Looper;
import android.util.Log;

import com.xingcloud.core.Config;
import com.xingcloud.core.SecurityKey;
import com.xingcloud.items.spec.AsObject;
import com.xingcloud.tasks.base.Task;
import com.xingcloud.utils.Base64;
import com.xingcloud.utils.XingCloudLogger;
import com.xingcloud.utils.MyCrypte;
import com.xingcloud.utils.Utils;

/**
 * 后台连接任务，通常游戏数据的后台交互用这个，可以指定默认的gateway，也可以使用多个gateway，他将保持一个gateway只会连接一个
 */
public class Remoting extends Task {
	public enum RemotingMethod {
		GET, POST, SFS;
	}

	public static int REMOTING_RETRY_COUNT = 3;
	/**
	 * 是否开启数据压缩
	 */
	public static boolean zipRemotingContent = true;
	/**
	 * Determines the timeout until a connection is established.
	 */
	public static int CONNECTION_TIMEOUT = 30000;
	/**
	 * Defines the default socket timeout (SO_TIMEOUT) in milliseconds which is the timeout for waiting for data.
	 */
	public static int SO_TIMEOUT = 30000;

	public static int ClientProtocolException = 1;
	public static int IOException = 2;

	private class RemotingTask extends AsyncTask<HttpRequestBase,Void, Void> {
		private WeakReference<HttpRequestBase> mHttpRequest;
		//private DefaultHttpClient client;

		protected Void doInBackground(HttpRequestBase... arg0) {
			HttpResponse httpResponse;
			try {
				mHttpRequest = new WeakReference<HttpRequestBase>(arg0[0]);
				HttpRequestBase httpRequest = mHttpRequest.get();
				DefaultHttpClient client =  new DefaultHttpClient();
				client.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, CONNECTION_TIMEOUT);
				client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, SO_TIMEOUT);
				client.setHttpRequestRetryHandler(myRetryHandler.get());
				HttpProtocolParams.setUseExpectContinue(client.getParams(), false);
				httpRequest.addHeader("Accept-Encoding", "gzip");
				if(zipRemotingContent)
					httpRequest.addHeader("Content-Encoding","gzip");
				httpResponse = client.execute(httpRequest);
				processResponse(httpResponse);
			} catch (ClientProtocolException e) {
				notifyError("Remoting->RemotingThread : ClientProtocolException, "+e.getMessage(),ClientProtocolException);
			} catch (IOException e) {
				notifyError("Remoting->RemotingThread : IOException, "+e.getMessage(),IOException);
			}
			// catch (Exception e) {
			// notifyError("Remoting->processResponse : "+e.getMessage());
			// }
			mHttpRequest = null;
			myRetryHandler = null;
			return null;
		}

		WeakReference<HttpRequestRetryHandler> myRetryHandler = new WeakReference<HttpRequestRetryHandler>(new HttpRequestRetryHandler() {
			public boolean retryRequest(
					IOException exception,
					int executionCount,
					HttpContext context) {
				if (executionCount >= REMOTING_RETRY_COUNT) {
					// Do not retry if over max retry count
					XingCloudLogger.log(XingCloudLogger.DEBUG,"Remoting->HttpRequestRetryHandler : Retry count limit exceeded.");
					return false;
				}

				XingCloudLogger.log(XingCloudLogger.DEBUG,"Remoting->HttpRequestRetryHandler : Retry count "+executionCount);

				if (exception instanceof NoHttpResponseException) {
					// Retry if the server dropped connection on us
					//XingCloudLogger.log(XingCloudLogger.DEBUG,"Remoting->HttpRequestRetryHandler : NoHttpResponseException");
					return true;
				}
				if (exception instanceof SSLHandshakeException) {
					// Do not retry on SSL handshake exception
					XingCloudLogger.log(XingCloudLogger.DEBUG,"Remoting->HttpRequestRetryHandler : SSLHandshakeException");
					return false;
				}
				HttpRequest request = (HttpRequest) context.getAttribute(ExecutionContext.HTTP_REQUEST);
				boolean idempotent = !(request instanceof HttpEntityEnclosingRequest);
				if (idempotent) {
					// Retry if the request is considered idempotent
					//XingCloudLogger.log(XingCloudLogger.DEBUG,"Remoting->HttpRequestRetryHandler : idempotent");
					return true;
				}
				return false;
			}
		});
	}

	/*
	class RemotingThread extends Thread {
		private HttpRequestBase httpRequest;
		private DefaultHttpClient client;

		public RemotingThread(HttpRequestBase request) {
			httpRequest = request;
		}

		public void run() {
			Looper.prepare();

			HttpResponse httpResponse;
			try {
				client =  new DefaultHttpClient();
				client.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, CONNECTION_TIMEOUT);
				client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, SO_TIMEOUT);
				//client.setHttpRequestRetryHandler(myRetryHandler);
				httpRequest.addHeader("Accept-Encoding", "gzip");
				if(zipRemotingContent)
					httpRequest.addHeader("Content-Encoding","gzip");
				httpResponse = client.execute(httpRequest);
				processResponse(httpResponse);
			} catch (ClientProtocolException e) {
				notifyError("Remoting->RemotingThread : ClientProtocolException, "+e.getMessage(),ClientProtocolException);
			} catch (IOException e) {
				notifyError("Remoting->RemotingThread : IOException, "+e.getMessage(),IOException);
			}
			httpRequest = null;
			client = null;
			// catch (Exception e) {
			// notifyError("Remoting->processResponse : "+e.getMessage());
			// }
		}
	 */

	/**
	 * 默认的gateway地址
	 */
	public static String defaultGateway = "";

	public static int remotingId=1;

	protected String _command = "";

	protected String _gateway;
	protected RemotingMethod _method;
	protected Boolean _needAuth = true;
	protected AsObject _params = null;
	protected int currentRemotingID;
	private String defaultContentEncoding;

	public RemotingResponse response;

	/*
	private IEventListener sfsEventHandler = new IEventListener() {

		@Override
		public void performEvent(XingCloudEvent evt) {
			SFSManager.instance().addEventListener(
					XingCloudSFSEvent.EXTENSION_SERVICE, this);
			XingCloudSFSEvent sfsEvent = (XingCloudSFSEvent) evt;
			Object obj = sfsEvent.getSFSEvent().getArguments().get("params");
			SFSObject sfsobj = (SFSObject) obj;
			AsObject asObj = new AsObject(sfsobj);
			response = RemotingResponse.fromAsObject(asObj);
			complete();
		}

	};
	 */

	public Remoting() {
		this.defaultContentEncoding = Charset.defaultCharset().name();
		_gateway = defaultGateway;
		_method = RemotingMethod.POST;
		currentRemotingID = remotingId++;
	}

	/**
	 * 与后台通信
	 * 
	 * @param command_name
	 *            方法名
	 * @param command_args
	 *            参数
	 * @param method
	 *            get/post
	 */
	public Remoting(String command_name, AsObject command_args,
			RemotingMethod method) {
		this.defaultContentEncoding = Charset.defaultCharset().name();
		_command = command_name;
		_gateway = defaultGateway;
		_method = method;
		_params = command_args;
		currentRemotingID = remotingId++;
	}

	/**
	 * @param command_name
	 * @param command_args
	 * @param method
	 *            get/post
	 * @param needAuth
	 *            是否需要安全验证，由后台配置决定
	 */
	public Remoting(String command_name, AsObject command_args,
			RemotingMethod method, Boolean needAuth) {
		this.defaultContentEncoding = Charset.defaultCharset().name();
		_gateway = defaultGateway;
		_command = command_name;
		_method = method;
		_params = command_args;
		_needAuth = needAuth;
		currentRemotingID = remotingId++;
	}

	/**
	 * 与后台通信
	 * 
	 * @param command_name
	 *            方法名
	 * @param command_args
	 *            参数
	 * @param gateWay
	 *            通信入口
	 * @param method
	 *            get/post
	 * @param needAuth
	 *            是否需要安全验证，由后台配置决定
	 */
	public Remoting(String command_name, AsObject command_args,
			RemotingMethod method, Boolean needAuth, String gateWay) {
		this.defaultContentEncoding = Charset.defaultCharset().name();
		if (gateWay == null)
			gateWay = defaultGateway;
		_command = command_name;
		_gateway = gateWay;
		_method = method;
		_params = command_args;
		_needAuth = needAuth;
		currentRemotingID = remotingId++;
	}

	/**
	 * 与后台通信
	 * 
	 * @param command_name
	 *            方法名
	 * @param command_args
	 *            参数
	 * @param gateWay
	 *            通信入口
	 * @param method
	 *            get/post
	 */
	public Remoting(String command_name, AsObject command_args,
			RemotingMethod method, String gateWay) {
		this.defaultContentEncoding = Charset.defaultCharset().name();
		if (gateWay == null)
			gateWay = defaultGateway;
		_command = command_name;
		_gateway = gateWay;
		_method = method;
		_params = command_args;
		currentRemotingID = remotingId++;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.xingcloud.tasks.base.Task#doExecute()
	 */
	protected void doExecute() {
		super.doExecute();
		if (_gateway == null) {
			this.notifyError("no gateway!",null);
			return;
		}

		_params.setProperty("id", currentRemotingID);
		_params.setProperty("info", Config.appInfo());

		if(_params.getProperty("data")==null)
			_params.setProperty("data", "");

		if (this._method == RemotingMethod.GET) {
			try {
				String cms = "";
				if (_command.length() > 0)
					cms = "/" + _command.replace(".", "/");
				this.sendGet(_gateway + cms +"/?"+Config.extraParams(), _params);
			} catch (IOException e) {
				e.printStackTrace();
				this.notifyError("Remoting->doExecute:throw IOException",IOException);
			}
		} else if (this._method == RemotingMethod.POST) {
			try {
				String cms = "";
				if (_command.length() > 0)
					cms = "/" + _command.replace(".", "/");
				this.sendPost(_gateway + cms +"/?"+Config.extraParams(), _params);
			} catch (IOException e) {
				e.printStackTrace();
				this.notifyError("Remoting->doExecute:throw IOException",IOException);
			}
		} else {
			/*
			if (!SFSManager.instance().isActive()) {
				this.notifyError("Remoting->doExecute:There is no active sfs connection now, please try later.");
			}

			SFSObject sfsparams = new SFSObject();
			sfsparams.putUtfString("api", _command);
			sfsparams.putSFSObject("data", _params.toSFSObject());
			sfsparams.putSFSObject("info", Config.appInfo().toSFSObject());
			ExtensionRequest extRequest = new ExtensionRequest("service",
					sfsparams);
			SFSManager.instance().addEventListener(
					XingCloudSFSEvent.EXTENSION_SERVICE, sfsEventHandler);
			extRequest.execute(SFSManager.instance().SFSClient);
			try {
				SFSManager.instance().SFSClient.getSocketEngine().send(
						extRequest.getMessage());
			} catch (SFSException e) {
				this.notifyError("Remoting->doExecute:" + e.getMessage());
			}
			 */
		}
	}

	private String generateHeaders(String urlString, AsObject params,
			RemotingMethod method) {
		try {
			// 加上认证数据
			TreeMap<String,String> oauth = new TreeMap<String,String>();
			oauth.put("realm", _gateway);
			oauth.put("oauth_consumer_key", SecurityKey.consumerKey);
			oauth.put("oauth_signature_method", "HMAC-SHA1");
			oauth.put("oauth_timestamp",
					String.valueOf(Config.getSystemTime() / 1000));
			oauth.put(
					"oauth_nonce",
					MyCrypte.messageDigest(
							String.valueOf(System.currentTimeMillis()
									+ new Random().nextInt(1000)), "MD5"));
			oauth.put("oauth_version", "1.0");
			if (method == RemotingMethod.GET) {
				Iterator iter = params.properties.entrySet().iterator();
				while (iter.hasNext()) {
					Entry entry = (Entry) iter.next();
					oauth.put(entry.getKey().toString(), entry.getValue().toString());
				}
			}
			String paramStr = "";
			String baseSha1 = "";
			byte[] baseBase64;
			String signature = "";
			String uri = urlString;
			String authHeader = "";
			// 按照oauth里边的key排序
			// Object[] key1=oauth.keySet().toArray();
			// Arrays.sort(key1);
			// 遍历oauth，把oauth组成一列字符串
			Iterator iterator = oauth.entrySet().iterator();
			while (iterator.hasNext()) {
				Entry entry = (Entry) iterator.next();
				paramStr += entry.getKey() + "=" + entry.getValue() + "&";
			}

			// 去掉最后一个多余的"&"符号
			paramStr = paramStr.substring(0, paramStr.length() - 1);
			// 把要编码的字符串接起来
			baseSha1 = method.toString() + "&"
					+ URLEncoder.encode(uri, "utf-8").replaceAll("\\+", "%20") + "&"
					+ URLEncoder.encode(paramStr, "utf-8").replaceAll("\\+", "%20");
			if (method == RemotingMethod.POST && params != null) {
				baseSha1 += ("&" + URLEncoder.encode(params.toJSONString(),
						"utf-8").replaceAll("\\+", "%20"));
			}
			// 用sha1的方法生成消息码
			baseBase64 = MyCrypte.messageCertify(baseSha1, "HmacSHA1",
					SecurityKey.secretKey, false);
			// 用base64的方法生成摘要
			// sun.misc.BASE64Encoder base64Encoder=new
			// sun.misc.BASE64Encoder();
			signature = Base64.encode(baseBase64);
			oauth.put("oauth_signature", signature);
			// 按照oauth里边的key排序
			// Object[] key2=oauth.keySet().toArray();
			// Arrays.sort(key2);

			// 遍历oauth生成Header，把oauth组成一列字符串
			iterator = oauth.entrySet().iterator();
			while (iterator.hasNext()) {
				Entry entry = (Entry) iterator.next();
				authHeader += entry.getKey() + "=" + entry.getValue() + ",";
			}

			// 去掉结尾最后一个逗号,并接上指定字符串
			authHeader = authHeader.substring(0, authHeader.length() - 1);

			return authHeader;
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}

	public int getCurrentRemotingID() {
		return currentRemotingID;
	}

	/**
	 * 默认的响应字符集
	 */
	public String getDefaultContentEncoding() {
		return this.defaultContentEncoding;
	}

	public RemotingMethod getMethod() {
		return _method;
	}

	public AsObject getParams() {
		return _params;
	}

	private void processResponse(HttpResponse resp) {
		if (resp == null) {
			this.notifyError("Remoting->processResponse : There is no result",null);
			return;
		}

		int statusCode = resp.getStatusLine().getStatusCode();

		if (statusCode == 401) {
			this.notifyError("Remoting->processResponse : Authorization error",null);
			return;
		} else if (statusCode != 200) {
			try {
				this.notifyError("Remoting->processResponse : Error - "
						+ resp.getStatusLine().getReasonPhrase()
						+ ", "
						+ new String(EntityUtils.toByteArray(resp.getEntity())
								.clone()),null);
			} catch (IOException e) {
				this.notifyError("Remoting->processResponse : Error - "
						+ resp.getStatusLine().getReasonPhrase(),null);
			}
			return;
		}

		try {
			if(zipRemotingContent)
			{
				response = RemotingResponse.fromBytes(Utils.inflate(EntityUtils.toByteArray(resp.getEntity())));
			}
			else
			{
				Header encodingHeader = resp.getEntity().getContentEncoding();
				long contentLength = resp.getEntity().getContentLength();
				if (encodingHeader != null && encodingHeader.getValue().equals("gzip")) 
				{
					InputStream ungzippedResponse = new GZIPInputStream(resp.getEntity().getContent());

					ByteArrayOutputStream out = new ByteArrayOutputStream();
					byte[] buf = new byte[8*1024];
					int i;
					while ((i = ungzippedResponse.read(buf)) >= 0) 
					{
						out.write(buf, 0, i);
					}
					response = RemotingResponse.fromBytes(out.toByteArray().clone());
					out.close();
					ungzippedResponse.close();

				} 
				else {
					response = RemotingResponse.fromBytes(EntityUtils.toByteArray(resp.getEntity()).clone());
				}
			}
		} catch (IOException e) {
			this.notifyError("Remoting->processResponse : error occured when parsing HttpResponse.",IOException);
			return;
		} catch (Exception e) {
			this.notifyError("Remoting->processResponse : "+e.getMessage(),null);
			return;
		} finally {
			if(response==null)
			{
				this.notifyError("Remoting->processResponse : finally error",null);
				return;
			}
		}

		XingCloudLogger.log(XingCloudLogger.DEBUG,"Remoting->processResponse : "+response.getContent());

		if(response.isSuccess())
		{
			this.complete();
		}
		else
		{
			this.notifyError("Remoting->processResponse : "+response.getMessage(),null);
		}
	}

	/**
	 * 发送GET请求
	 * 
	 * @param urlString
	 *            URL地址
	 * @param params
	 *            参数集合
	 * @return 响应对象
	 * @throws IOException
	 */
	private void sendGet(String urlString, AsObject params) throws IOException {
		String headers = "";

		if (_needAuth) {
			headers = generateHeaders(urlString, params, RemotingMethod.GET);
		}
		String content;
		if(params == null)
		{
			content="";
		}
		else
		{
			content = params.toURLString();
		}

		urlString += content;
		HttpGet httpGet = new HttpGet(urlString);
		if (_needAuth) {
			httpGet.setHeader("Authorization", headers);
		}

		XingCloudLogger.log(XingCloudLogger.DEBUG,"Remoting->sendGet: Start connect backend: "
				+ urlString);

		RemotingTask remTask = new RemotingTask();
		remTask.execute(httpGet);
	}

	/**
	 * 发送POST请求
	 * 
	 * @param urlString
	 *            URL地址
	 * @param params
	 *            参数集合
	 * @return 响应对象
	 * @throws IOException
	 */
	private void sendPost(String urlString, AsObject params) throws IOException {
		HttpPost httpPost = new HttpPost(urlString);

		JSONObject content = params.toJSON();
		if (_needAuth) {
			String headers = generateHeaders(urlString, params,
					RemotingMethod.POST);
			httpPost.setHeader("Authorization", headers);
		}
		String contentStr = content.toString();

		byte[] source = contentStr.getBytes("UTF-8");

		if(zipRemotingContent)
		{
			byte[] output = Utils.deflate(source);
			ByteArrayEntity bae = new ByteArrayEntity(output);
			bae.setContentEncoding("gzip");
			httpPost.setEntity(bae);
		}
		else
		{
			ByteArrayEntity se = new ByteArrayEntity(contentStr.getBytes());
			httpPost.setEntity(se);
		}


		XingCloudLogger.log(XingCloudLogger.DEBUG,"Remoting->sendPost: Start connect backend: "
				+ urlString + ", with content : "+contentStr);

		RemotingTask remTask = new RemotingTask();
		remTask.execute(httpPost);
	}

	/**
	 * 设置默认的响应字符集
	 */
	public void setDefaultContentEncoding(String defaultContentEncoding) {
		this.defaultContentEncoding = defaultContentEncoding;
	}

	public void setParams(AsObject params) {
		this._params = params;
	}

}