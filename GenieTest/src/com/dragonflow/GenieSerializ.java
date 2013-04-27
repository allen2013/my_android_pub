package com.dragonflow;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OptionalDataException;
import java.io.StreamCorruptedException;
import java.util.HashMap;
import java.util.Map;

import com.netgear.genie.media.dlna.DLNAConfig;

import android.content.Context;

public class GenieSerializ 
{
	@SuppressWarnings("unchecked")
	public static HashMap<String, String> ReadMap(Context context,String filename)
	{	
		ObjectInputStream ois = null;
		try 
		{
			ois = new ObjectInputStream(context.openFileInput(filename));
		} 
		catch(StreamCorruptedException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		
		HashMap<String, String> map = null;
		try 
		{
			map = (HashMap)ois.readObject();
		}
		catch(OptionalDataException e)
		{
			return null;
		}
		catch (ClassNotFoundException e)
		{
			return null;
		}
		catch (Exception e) 
		{
			return null;
		}
		
		if(map != null)
		{
			return map;
		}
		return null;
	}
	public static void WriteMap(Context context, HashMap<String, String> map, String filename)
	{
		ObjectOutputStream oos = null;
		try 
		{
			oos = new ObjectOutputStream(context.openFileOutput(filename, Context.MODE_PRIVATE));
		} 
		catch (FileNotFoundException e) 
		{
			return ;
		} 
		catch(IOException e) 
		{
			return ;
		}
		//
		try 
		{
			oos.writeObject(map);
		} 
		catch(IOException e) 
		{
			return ;
		}
	}
	
	
	@SuppressWarnings("unchecked")
	public static DLNAConfig ReadDLNAConfig(Context context,String filename)
	{	
		ObjectInputStream ois = null;
		try 
		{
			ois = new ObjectInputStream(context.openFileInput(filename));
		} 
		catch(StreamCorruptedException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		
		DLNAConfig map = null;
		try 
		{
			map = (DLNAConfig)ois.readObject();
		}
		catch(OptionalDataException e)
		{
			return null;
		}
		catch (ClassNotFoundException e)
		{
			return null;
		}
		catch (Exception e) 
		{
			return null;
		}
		
		if(map != null)
		{
			return map;
		}
		return null;
	}
	public static void WriteDLNAConfig(Context context, DLNAConfig obj, String filename)
	{
		ObjectOutputStream oos = null;
		try 
		{
			oos = new ObjectOutputStream(context.openFileOutput(filename, Context.MODE_PRIVATE));
		} 
		catch (FileNotFoundException e) 
		{
			e.printStackTrace();
			return ;
		} 
		catch(IOException e) 
		{
			e.printStackTrace();
			return ;
		}
		//
		try 
		{
			oos.writeObject((Object)obj);
		} 
		catch(Exception e) 
		{
			e.printStackTrace();
			return ;
		}
	}
	
	
}
