package com.innolux.interfaces;

import com.innolux.service.base.RVMessage;

public interface ITibcoRvListenService {
	public void onRvMsg(RVMessage msg);
}
