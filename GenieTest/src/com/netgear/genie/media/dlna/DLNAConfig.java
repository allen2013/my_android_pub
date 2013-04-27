package com.netgear.genie.media.dlna;

import java.io.IOException;
import java.io.Serializable;

import com.dragonflow.GenieDebug;

public class DLNAConfig implements Serializable{

	public byte[] configData;
	public String SaveRenderUUID;
	public String ServerSwitch;
	public String RenderSwitch;
	//private byte[] UUID;
	
	private void writeObject(java.io.ObjectOutputStream out)throws IOException
	{
		out.defaultWriteObject();
		GenieDebug.error("debug","DLNAConfig writeObject ServerSwitch = "+ServerSwitch);
		GenieDebug.error("debug","DLNAConfig writeObject RenderSwitch = "+RenderSwitch);
		GenieDebug.error("debug","DLNAConfig writeObject SaveRenderUUID = "+SaveRenderUUID);
		//out.writeBoolean(ServerSwitch);
		//out.writeBoolean(RenderSwitch);
		out.write(configData, 0,configData.length);
		out.defaultWriteObject();
		out.writeBytes(SaveRenderUUID);
		out.defaultWriteObject();
		out.writeBytes(ServerSwitch);
		out.defaultWriteObject();
		out.writeBytes(RenderSwitch);

	}
	private void readObject(java.io.ObjectInputStream in)throws IOException, ClassNotFoundException
	{
		in.defaultReadObject();
		in.read(configData);
		in.defaultReadObject();
		SaveRenderUUID = in.readLine();
		in.defaultReadObject();
		ServerSwitch = in.readLine();
		in.defaultReadObject();
		RenderSwitch = in.readLine();
		GenieDebug.error("debug","DLNAConfig readObject ServerSwitch = "+ServerSwitch);
		GenieDebug.error("debug","DLNAConfig readObject RenderSwitch = "+RenderSwitch);
		GenieDebug.error("debug","DLNAConfig readObject SaveRenderUUID = "+SaveRenderUUID);
	}
}
