package com.innolux.common.base;

import java.util.Hashtable;

public class IR_Signal {

	private Hashtable<String, String> IR_Signal_status = new Hashtable<String, String>();

	public synchronized String GetLastSignal(String fab, String area, String gate, String status) {
		String result = "";
		String key = fab + area + gate;
		if (IR_Signal_status.containsKey(key)) {
			result = IR_Signal_status.get(key);
		}
		if (!result.equals(status)) {
			if (!IR_Signal_status.containsKey(key)) {
				IR_Signal_status.put(key, status);
			} else {
				IR_Signal_status.replace(key, status);
			}
		}

		return result;
	}

	public synchronized void SetLastSignal(String fab, String area, String gate, String status) {
		String key = fab + area + gate;
		if (!IR_Signal_status.containsKey(key)) {
			IR_Signal_status.put(key, status);
		} else {
			IR_Signal_status.replace(key, status);
		}
	}

}
