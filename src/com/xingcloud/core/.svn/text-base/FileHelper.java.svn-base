package com.xingcloud.core;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import com.xingcloud.event.EventDispatcher;

/**
 *  本地文件读写服务，默认会采用MODE_PRIVATE的方式写入文件
 * @author chuckzhang
 *
 */
public class FileHelper extends EventDispatcher {
	private static String extention=".xy";

	public static boolean delete(String name)
	{
		return XingCloud.instance().getContext().deleteFile(name);
	}

	/**
	 * 检查文件是否存在
	 * 
	 * @param fileName 文件名
	 * @return 是否存在
	 */
	public static boolean exist(String fileName){
		FileInputStream fis;
		try {
			fis = XingCloud.instance().getContext().openFileInput(fileName);
			return true;
		} catch (FileNotFoundException e) {
			return false;
		}  
	}  

	/** 
	 * 读取文件内容 
	 *  
	 * @param fileName 文件名 
	 * @return 文件内容 
	 */  
	public static byte[] read(String fileName){  
		FileInputStream fis;
		try {
			fis = XingCloud.instance().getContext().openFileInput(fileName);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}  
		ByteArrayOutputStream baos = new ByteArrayOutputStream();  

		byte[] buf = new byte[1024];  
		int len = 0;  

		//将读取后的数据放置在内存中---ByteArrayOutputStream  
		try {
			while ((len = fis.read(buf)) != -1) {  
				baos.write(buf, 0, len);  
			}
			
			fis.close();  
			baos.close();  
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}  

		//返回内存中存储的数据  
		return baos.toByteArray();  

	}  

	/** 
	 * 保存文件 
	 *  
	 * @param fileName 文件名 
	 * @param content  文件内容 
	 * @return 是否成功写入文件
	 */  
	public static boolean save(String fileName, byte[] content){  
		FileOutputStream fos;
		try {
			//fileName = fileName+"."+extention;
			fos = XingCloud.instance().getContext().openFileOutput(fileName, XingCloud.instance().getContext().MODE_PRIVATE);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		}  
		try {
			fos.write(content);
			fos.close();  
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		}  
		return false;
	}
	
	public FileHelper() {
	}
}
