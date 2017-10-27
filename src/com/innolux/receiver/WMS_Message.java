package com.innolux.receiver;

import com.innolux.common.GlobleVar;
import com.innolux.interfaces.ITibcoRvListenService;
import com.innolux.service.TibcoRvListen;

public class WMS_Message implements ITibcoRvListenService{
	
	

	public WMS_Message(){
		TibcoRvListen rv = new TibcoRvListen("tcp:8585",GlobleVar.ListenFromWMS,"8585","");
		rv.setRvListener(this);
		rv.startService();
	}
			
	@Override
	public void onRvMsg(String msg) {
		// TODO Auto-generated method stub
		
	}

}
