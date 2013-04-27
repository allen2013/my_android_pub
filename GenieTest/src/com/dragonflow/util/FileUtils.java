package com.dragonflow.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.http.util.EncodingUtils;

/**
 * <p>
 * Title: FileUtils
 * </p>
 * <p>
 * Description:文件工具类
 * </p>
 * <p>
 * Copyright: Copyright (c) 2013
 * </p>
 * <p>
 * Company: 北京游龙
 * </p>
 * 
 * @author <a href='mailto:allen_lhl@sina.cn'>allen </a>
 * @version 2.0
 * @since 2013-04-09
 */
public class FileUtils
{
	/**
	 * 删除文件
	 * @param fileName
	 * @return
	 */
	public static boolean delete(String fileName)
	{
		if(fileName == null || fileName.trim().equals(""))
		{
			return true;
		}
		File file = new File(fileName);
		if(file != null && file.exists())
		{
			return file.delete();
		}
		
		return true;
	}
	
	/**
	 * 生成文件
	 * @param fileName
	 * @return
	 */
	public static File getFile(String fileName)
	{
		File file = new File(fileName);
		if(file.exists())
		{
			return file;
		}
		else
		{
			try
            {
	            return file.createNewFile() ? file : null;
            }
            catch (IOException e)
            {
            	return null;
            }
		}
	}
	
	
	/**
	 * 写数据到指定的SD文件中 
	 * @param fileName
	 * @param data
	 * @param isAppend 是否附加
	 * @throws IOException
	 */
	public static void writeSdcardFile(String fileName, String data, boolean isAppend) throws IOException
	{
		try
		{
			FileOutputStream fout = new FileOutputStream(fileName, isAppend);
			byte[] bytes = data.getBytes();

			fout.write(bytes);
			fout.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * 写数据到指定的SD文件中 
	 * @param file
	 * @param data
	 * @param isAppend 是否附加
	 * @throws IOException
	 */
	public static void writeSdcardFile(File file, String data, boolean isAppend) throws IOException
	{
		try
		{
			FileOutputStream fout = new FileOutputStream(file, isAppend);
			byte[] bytes = data.getBytes();

			fout.write(bytes);
			fout.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * 读取文件
	 * @param fileName
	 * @return
	 * @throws IOException
	 */
	public static String readSdcardFile(String fileName) throws IOException
	{
		String res = "";
		try
		{
			FileInputStream fin = new FileInputStream(fileName);

			int length = fin.available();

			byte[] buffer = new byte[length];
			fin.read(buffer);

			res = EncodingUtils.getString(buffer, "UTF-8");

			fin.close();
		}

		catch (Exception e)
		{
			e.printStackTrace();
		}
		return res;
	}

}
