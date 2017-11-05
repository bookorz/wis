package com.innolux.receiver;

import java.util.Hashtable;
import java.util.List;

import org.apache.log4j.Logger;

import com.innolux.common.TagHandle;
import com.innolux.common.TagParser;
import com.innolux.common.ToolUtility;
import com.innolux.interfaces.ISocketService;
import com.innolux.model.RF_Antenna_Setting;
import com.innolux.model.RF_Reader_Setting;
import com.innolux.model.RF_Tag_History;
import com.innolux.service.SocketService;

public class AlienReaderBySocket implements ISocketService {

	private Logger logger = Logger.getLogger(this.getClass());
	private RF_Reader_Setting setting;
	private Hashtable<Integer, RF_Antenna_Setting> antSetting = new Hashtable<Integer, RF_Antenna_Setting>();

	public AlienReaderBySocket(RF_Reader_Setting _setting, int ListenPort) {
		setting = _setting;
		Initial();

		SocketService sc = new SocketService(setting.getReader_IP(), ListenPort);
		sc.setSocketListener(this);
		sc.startService();
	}

	@Override
	public void onSocketMsg(String msg) {
		// TODO Auto-generated method stub
		logger.debug(setting.getReader_IP() + " " + msg);
		List<RF_Tag_History> tagList = new TagParser().Parse(msg, antSetting);
		ToolUtility.InsertLog(tagList, setting.getReader_IP());
		if (tagList.size() != 0) {
			TagHandle.Data(tagList);
		}
	}

	private void Initial() {

		try {

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
