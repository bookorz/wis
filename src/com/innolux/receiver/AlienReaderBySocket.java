package com.innolux.receiver;

import java.util.Hashtable;
import java.util.List;

import org.apache.log4j.Logger;

import com.innolux.common.TagHandle;
import com.innolux.common.TagParser;
import com.innolux.common.ToolUtility;
import com.innolux.interfaces.ISocketService;
import com.innolux.model.RF_Antenna_Setting;
import com.innolux.model.RF_Gate_Setting;
import com.innolux.model.RF_Reader_Setting;
import com.innolux.model.RF_Tag_History;
import com.innolux.service.SocketService;

public class AlienReaderBySocket implements ISocketService {

	private Logger logger = Logger.getLogger(this.getClass());
	private RF_Reader_Setting setting;
	private Hashtable<Integer, RF_Antenna_Setting> antSetting = new Hashtable<Integer, RF_Antenna_Setting>();
	private Hashtable<String, RF_Gate_Setting> gateSetting = new Hashtable<String, RF_Gate_Setting>();

	public AlienReaderBySocket(RF_Reader_Setting _setting) {
		setting = _setting;
		Initial();

		SocketService sc = new SocketService(setting.getReader_IP(), setting.getListen_Port());
		sc.setSocketListener(this);
		sc.startService();
	}

	@Override
	public void onSocketMsg(String msg) {
		// TODO Auto-generated method stub
		logger.debug(setting.getReader_IP() + " " + msg);
		List<RF_Tag_History> tagList = new TagParser().Parse(msg, antSetting,gateSetting);
		ToolUtility.InsertLog(tagList, setting.getReader_IP());
		if (tagList.size() != 0) {
			TagHandle.Data(tagList);
		}
	}

	private void Initial() {

		try {
			for(RF_Gate_Setting eachGate:ToolUtility.GetAllGateSetting(setting.getReader_IP())) {
				String key = eachGate.getFab()+eachGate.getArea()+eachGate.getGate();
				if(!gateSetting.containsKey(key)) {
					gateSetting.put(key, eachGate);
				}
			}
			for (RF_Antenna_Setting eachAnt : ToolUtility.GetAntSettingList(setting.getReader_IP())) {
				if (!antSetting.containsKey(eachAnt.getAntenna_No())) {
					antSetting.put(eachAnt.getAntenna_No(), eachAnt);
				}
			}

		} catch (Exception e) {
			logger.error(setting.getReader_IP() + " " + "Exception:" + ToolUtility.StackTrace2String(e));
		}
	}

}
