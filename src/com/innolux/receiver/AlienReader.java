package com.innolux.receiver;

import java.util.Hashtable;
import java.util.List;
import org.apache.log4j.Logger;

import com.alien.enterpriseRFID.notify.Message;
import com.alien.enterpriseRFID.notify.MessageListener;
import com.alien.enterpriseRFID.notify.MessageListenerService;
import com.innolux.common.TagHandle;
import com.innolux.common.TagParser;
import com.innolux.common.ToolUtility;
import com.innolux.model.RF_Antenna_Setting;
import com.innolux.model.RF_Reader_Setting;
import com.innolux.model.RF_Tag_History;

public class AlienReader implements MessageListener {

	private Logger logger = Logger.getLogger(this.getClass());
	private RF_Reader_Setting setting;
	private Hashtable<Integer, RF_Antenna_Setting> antSetting = new Hashtable<Integer, RF_Antenna_Setting>();

	public AlienReader(RF_Reader_Setting _setting) {
		setting = _setting;
		Initial();

		MessageListenerService service = new MessageListenerService(setting.getListen_Port());

		service.setMessageListener(this);
		try {
			service.startService();
		} catch (Exception e) {

			logger.error(setting.getReader_IP() + " " + "Exception:" + ToolUtility.StackTrace2String(e));
		}

	}

	@Override
	public void messageReceived(Message message) {
		String MessageRawData;
		MessageRawData = message.getRawData();
		logger.debug(setting.getReader_IP() + " " + MessageRawData);
		List<RF_Tag_History> tagList = new TagParser().Parse(MessageRawData, antSetting);
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
