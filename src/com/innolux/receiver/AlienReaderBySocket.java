package com.innolux.receiver;

import org.apache.log4j.Logger;

import com.innolux.common.ToolUtility;
import com.innolux.interfaces.ISocketService;
import com.innolux.service.SocketService;

public class AlienReaderBySocket implements ISocketService{
	
	public Logger logger = Logger.getLogger(this.getClass());
	private ToolUtility tools = new ToolUtility();
	
	public AlienReaderBySocket(String IP,int ListenPort){
		SocketService sc = new SocketService(IP,ListenPort);
		sc.setSocketListener(this);
		sc.startService();
	}

	@Override
	public void onSocketMsg(String msg) {
		// TODO Auto-generated method stub
		
	}

}
