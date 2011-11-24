package com.xingcloud.utils;

import android.util.Log;

public class XingCloudLogger {
	public static final int VERBOSE = 1;
	public static final int DEBUG = 2;
	public static final int INFO = 3;
	public static final int WARN = 4;
	public static final int ERROR = 5;
	public static final int NONE = 6;

	/**
	 * 输出Log的级别，可以设置为XingCloudLogger.VERBOSE/DEBUG/INFO/WARN/ERROR/NONE之一。当log级别小于这个级别时，将不会输出log
	 */
	public static int OUTPUT_LEVEL = 1;
	
	/**
	 * 输出log
	 * @param level log的级别，可以设置为XingCloudLogger.VERBOSE/DEBUG/INFO/WARN/ERROR之一
	 * @param msg log内容
	 */
	public static void log(int level, String msg)
	{
		if(level<OUTPUT_LEVEL)
			return;
			
		String tag = "XingCloud";
		switch(level)
		{
		case VERBOSE:
			Log.v(tag, msg);
			break;
		case DEBUG:
			Log.d(tag, msg);
			break;
		case INFO:
			Log.i(tag, msg);
			break;
		case WARN:
			Log.w(tag, msg);
			break;
		case ERROR:
			Log.e(tag, msg);
			break;
		default:
			System.out.println(msg);
			break;
		}
	}
}
