package com.xingcloud.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/*
 * (non-Javadoc)
 */
public class MyCrypte
{
	//消息验证
	/* plainStr：要计算消息验证码的字符串
	 * algorithm：要使用的算法
	 * key：要使用的密钥
	 * isBinary：返回的子字符串是二进制串还是十六进制串
	 */
	public static byte[] messageCertify(String plainStr,String algorithm,String key,boolean isBinary)
	{
		byte[] keyByte=key.getBytes();
		byte[] output;
	
		//初始化mac对象
		Mac mac=null;
		SecretKeySpec SKS=new SecretKeySpec(keyByte,algorithm);
		try
		{
			mac=Mac.getInstance(algorithm);
		}
		catch(NoSuchAlgorithmException e)
		{
			e.printStackTrace();
		}
		
		//初始化密钥
		try 
		{
			mac.init(SKS);
		} 
		catch (java.security.InvalidKeyException e) 
		{
		// TODO Auto-generated catch block
			e.printStackTrace();
	    }
		
		//计算消息验证
		mac.update(plainStr.getBytes());
		byte[] certifyCode=mac.doFinal();
		return certifyCode;
		//将得到的验证码转换成该进制的字符串
//		if(isBinary)
//		{
//			for(int i=0;i<certifyCode.length;++i)
//			{
//				output+=Integer.toBinaryString(certifyCode[i]);
//			}
//		}
//		else
//		{
//			for(int i=0;i<certifyCode.length;++i)
//			{
//				rcertifyCode[i];
//			}
//		}
//		
//		return output;
	}
	
	//消息摘要
	public static String messageDigest(String plainStr,String algorithm)
	{
		MessageDigest md=null;
		try
		{
			md=MessageDigest.getInstance(algorithm);
		}
		catch(NoSuchAlgorithmException e) {      
			 e.printStackTrace();        
		}

		//获取摘要
		md.update(plainStr.getBytes());      
		byte[] b = md.digest();
		//将得到的摘要转换为32位的16进制字符串
		StringBuilder output = new StringBuilder(32);      
		for (int i = 0; i < b.length; i++) 
		{      
		    String temp = Integer.toHexString(b[i] & 0xff);      
		    if (temp.length() < 2) 
		    {      
		       output.append("0");      
		    }      
		    output.append(temp);      
		}      
		return output.toString();      
	}
	
	/*
	//将字节转换为二进制串
	public static String byteToBinaryString(byte b)
	{
		String output="";
		int[] bit=new int[8];
		int bit_len=0,i;
	
		//把byte转换成二进制组数
		while(b>0)
		{
			bit[bit_len++]=b%2;
			b/=2;
		}
		
		//在前边补0
		for(i=1;i<=8-bit_len;++i)
		{
			output+="0";
		}
		//接上后面的数字
		for(i=0;i<bit_len;++i)
		{
			output+=""+bit[i];
		}
		
		return output;
	}
	*/
}


 

