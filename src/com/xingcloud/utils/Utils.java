package com.xingcloud.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.GZIPInputStream;
import java.util.zip.Inflater;

import android.util.Log;

import com.xingcloud.items.spec.AsObject;

public class Utils {

	protected static void parseProperty(AsObject item,String attrs)
	{
		//modified by 何松
		String[] attrArray = attrs.split(DbAssitant.dbItemAttributeSplit);
		if(null !=attrArray && attrArray.length > 0)
		{
			for(int i =0; i < attrArray.length;i++)
			{
				String key = attrArray[i].substring(0, attrArray[i].indexOf("="));
				String val =  attrArray[i].substring(attrArray[i].indexOf("=")+1, attrArray[i].length());
				item.setProperty(key, val);
			}
		}

	}

	public static String generateProtectedPassword(String originPassword)
	{
		return Utils.MD5((originPassword+"&XINGCLOUD").getBytes());
	}

	public static String MD5(byte[] source) {
		String s = null;
		char hexDigits[] = {       // 用来将字节转换成 16 进制表示的字符
				'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd',  'e', 'f'};
		try
		{
			java.security.MessageDigest md = java.security.MessageDigest.getInstance( "MD5" );
			md.update( source );
			byte tmp[] = md.digest();          // MD5 的计算结果是一个 128 位的长整数，
			// 用字节表示就是 16 个字节
			char str[] = new char[16 * 2];   // 每个字节用 16 进制表示的话，使用两个字符，
			// 所以表示成 16 进制需要 32 个字符
			int k = 0;                                // 表示转换结果中对应的字符位置
			for (int i = 0; i < 16; i++) {          // 从第一个字节开始，对 MD5 的每一个字节
				// 转换成 16 进制字符的转换
				byte byte0 = tmp[i];                 // 取第 i 个字节
				str[k++] = hexDigits[byte0 >>> 4 & 0xf];  // 取字节中高 4 位的数字转换, 
				// >>> 为逻辑右移，将符号位一起右移
				str[k++] = hexDigits[byte0 & 0xf];            // 取字节中低 4 位的数字转换
			}
			s = new String(str);                                 // 换后的结果转换为字符串

		}catch( Exception e )
		{
			e.printStackTrace();
		}
		return s;
	}

	public static byte[] deflate(byte[] source)
	{
		try {
			Deflater df = new Deflater();
			//df.setLevel(Deflater.BEST_SPEED);
			df.setInput(source);
			ByteArrayOutputStream baos = new ByteArrayOutputStream(source.length); 
			df.finish();
			byte[] buff = new byte[1024];
			while(!df.finished())
			{
				int count = df.deflate(buff);
				baos.write(buff, 0, count);
			}
			baos.close();
			byte[] output = baos.toByteArray();

			return output;
		} catch (IOException e) {
			XingCloudLogger.log(XingCloudLogger.DEBUG,"Utils->deflate : "+e.getMessage());
			return null;
		}
	}

	public static byte[] inflate(byte[] source)
	{
		try {
			Inflater ifl = new Inflater();
			//df.setLevel(Deflater.BEST_COMPRESSION);
			ifl.setInput(source);

			ByteArrayOutputStream baos = new ByteArrayOutputStream(source.length);
			
			while((!ifl.finished()))
			{
				byte[] buff = new byte[1024];
				int count = ifl.inflate(buff);
				baos.write(buff, 0, count);
			}
			byte[] output = baos.toByteArray();
			baos.close();
			ifl.end();
			return output;
		} 
		catch (IOException e) {
			XingCloudLogger.log(XingCloudLogger.DEBUG,"Utils->deflate IOException: "+e.getMessage());
			return null;
		}
		catch (DataFormatException e) {
			XingCloudLogger.log(XingCloudLogger.DEBUG,"Utils->deflate DataFormatException: "+e.getMessage());
			return null;
		}
	}
	
	public static byte[] ungzip(byte[] source)
	{
		ByteArrayInputStream input = new ByteArrayInputStream(source);
		try {
			InputStream ungzippedStream = new GZIPInputStream(input);
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			byte[] buf = new byte[8*1024];
			int i;
			while ((i = ungzippedStream.read(buf)) >= 0) 
			{
				out.write(buf, 0, i);
			}
			
			byte[] output = out.toByteArray().clone();
			out.close();
			input.close();
			ungzippedStream.close();
			return output;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}

}
