package com.filebrowse;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.app.Activity;
import android.util.Xml;

public class ConnectionInfoUtil {
	
	private final static String filepath="ConnectionInfo.xml";
	
	public static List<ComputerConnInfo> getAllComputeConnInfo(Activity currentActivity){
		
		List<ComputerConnInfo> list=new ArrayList<ComputerConnInfo>();
		try {
			ComputerConnInfo computer=null;
			InputStream in=currentActivity.openFileInput(filepath);
			if(in!=null){
				XmlPullParser parser =Xml.newPullParser();
				parser.setInput(in,"UTF-8");
				int type =parser.getEventType();//产生第一个事件  
				while(type!=XmlPullParser.END_DOCUMENT){
					switch(type){
						case XmlPullParser.START_DOCUMENT: {
							break;
						}
						case XmlPullParser.START_TAG: {
							 String name=parser.getName().toLowerCase();//获取解析器当前指向的元素名称  
							 if("computer".equals(name)){
								 computer=new ComputerConnInfo();
								 computer.setName(parser.getAttributeValue(0));
							 }
							 if(computer!=null){
								 if("ip".equals(name)){
									 computer.setIp(parser.nextText());
								 }
								 if("username".equals(name)){
									 computer.setUsername(parser.nextText());
								 }
								 if("password".equals(name)){
									 computer.setPassword(parser.nextText());
								 }
							 }
							 break;
						}
						case XmlPullParser.END_TAG: {
							 if("computer".equals(parser.getName().toLowerCase())){
								 if(computer!=null){
									 list.add(computer);
									 computer=null;
								 }
							 }
							 break;
						}
					}
					type=parser.next();
				}
				
			}
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(list.size());
		return list;
		
	}
	
	public static ComputerConnInfo getComputeConnInfoById(String id,Activity currentActivity){
		
		if(id==null || (id!=null&&"".equals(id.trim()))){
			return null;
		}
		ComputerConnInfo computer=null;
		try {
			InputStream in=currentActivity.openFileInput(filepath);
			XmlPullParser parser =Xml.newPullParser();
			parser.setInput(in,"UTF-8");
			int type =parser.getEventType();//产生第一个事件  
			while(type!=XmlPullParser.END_DOCUMENT){
				switch(type){
					case XmlPullParser.START_DOCUMENT: {
						break;
					}
					case XmlPullParser.START_TAG: {
						 String name=parser.getName().toLowerCase();//获取解析器当前指向的元素名称  
						 if("computer".equals(name)){
							 String m_id=parser.getAttributeValue(0).trim();
							 if(id.equals(m_id)){
								 computer=new ComputerConnInfo();
								 computer.setId(m_id);
								 computer.setName(parser.getAttributeValue(1));
							 }
						 }
						 if(computer!=null){
							 if("ip".equals(name)){
								 computer.setIp(parser.nextText());
							 }
							 if("username".equals(name)){
								 computer.setUsername(parser.nextText());
							 }
							 if("password".equals(name)){
								 computer.setPassword(parser.nextText());
							 }
						 }
						 break;
					}
					case XmlPullParser.END_TAG: {
						 break;
					}
				}
				type=parser.next();
			}
			
			
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return computer;
		
	}
	
	public static boolean addComputer(ComputerConnInfo info,Activity currentActivity){
		
		boolean flag=true;
		OutputStreamWriter writer=null;
		try{
			if(info!=null){
				writer=new OutputStreamWriter(currentActivity.openFileOutput(filepath, currentActivity.MODE_PRIVATE),"UTF-8");
				writer.write(info.toXml());
				System.out.println(info.toXml());
				flag=true;
			}
		}catch(Exception ex){
			ex.printStackTrace();
			flag=false;
		}finally{
			if(writer!=null){
				try {
					writer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return flag;
		
	}
	
	public static boolean deleteComputer(String id,Activity currentActivity){
		
		boolean flag=true;
		OutputStreamWriter writer=null;
		try{
			List<ComputerConnInfo> list=getAllComputeConnInfo(currentActivity);
			writer=new OutputStreamWriter(currentActivity.openFileOutput(filepath, currentActivity.MODE_PRIVATE),"UTF-8");
			for(ComputerConnInfo info:list){
				if(!id.equals(info.getId())){
					writer.write(info.toXml());
				}
			}
		}catch(Exception ex){
			ex.printStackTrace();
			flag=false;
		}finally{
			if(writer!=null){
				try {
					writer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}	
		return flag;
	}

}
