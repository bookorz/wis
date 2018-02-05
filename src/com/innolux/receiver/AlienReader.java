package com.innolux.receiver;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import org.apache.log4j.Logger;

import com.alien.enterpriseRFID.notify.Message;
import com.alien.enterpriseRFID.notify.MessageListener;
import com.alien.enterpriseRFID.notify.MessageListenerService;
import com.innolux.common.GlobleVar;
import com.innolux.common.TagHandle;
import com.innolux.common.TagParser;
import com.innolux.common.ToolUtility;
import com.innolux.model.RF_Antenna_Setting;
import com.innolux.model.RF_Gate_Setting;
import com.innolux.model.RF_Reader_Setting;
import com.innolux.model.RF_Tag_History;
import com.innolux.model.RF_Tag_Setting;

public class AlienReader implements MessageListener {

	private Logger logger = Logger.getLogger(this.getClass());
	private RF_Reader_Setting setting;
	private Hashtable<Integer, RF_Antenna_Setting> antSetting = new Hashtable<Integer, RF_Antenna_Setting>();
	private Hashtable<String, RF_Tag_Setting> tagSetting = new Hashtable<String, RF_Tag_Setting>();
	private Hashtable<String, RF_Gate_Setting> gateSetting = new Hashtable<String, RF_Gate_Setting>();

	public AlienReader(RF_Reader_Setting _setting) {
		setting = _setting;
		Initial();

		MessageListenerService service = new MessageListenerService(setting.getListen_Port());
		logger.debug("setting.getListen_Port()" + setting.getListen_Port());
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
		List<RF_Tag_History> tagList = TagParser.Parse(MessageRawData, antSetting, gateSetting, setting.getReader_IP());
		ToolUtility.InsertLog(tagList, setting.getReader_IP());

		if (tagList.size() != 0) {

			try {
				TagHandle.Data(PreFilter(tagList));
			} catch (Exception e) {

				logger.error("messageReceived Exception:" + ToolUtility.StackTrace2String(e));

			}

		}

	}

	private List<RF_Tag_History> PreFilter(List<RF_Tag_History> tagList) {
		List<RF_Tag_History> result = new ArrayList<RF_Tag_History>();

		for (RF_Tag_History tag : tagList) {
			switch (tag.getAntenna_Type()) {
			case GlobleVar.ANT_Big_Stock:
			case GlobleVar.ANT_Big_Use:
			case GlobleVar.ANT_Small_Stock:
			case GlobleVar.ANT_Small_Use:
				if (CheckSetting(GlobleVar.CountLess, tag)) {
					result.add(tag);
				} else {
					logger.info("The tag is not pass by prefilter:" + tag);
				}

				break;
			default:
				result.add(tag);
				break;

			}

		}

		return result;
	}

	private boolean CheckSetting(String name, RF_Tag_History tag) {
		boolean result = false;
		if (antSetting.containsKey(tag.getAntenna_No())) {
			// Setting_ID+Tag_Type+Name
			RF_Antenna_Setting antSet = antSetting.get(tag.getAntenna_No());
			String key = antSet.getSetting_ID() + tag.getTag_Type() + name;
			if (tagSetting.containsKey(key)) {
				RF_Tag_Setting tagSet = tagSetting.get(key);
				switch (tagSet.getName()) {
				case GlobleVar.CountLess:
					int countLessSetting = Integer.parseInt(tagSet.getValue());
					if (tag.getCount() > countLessSetting) {
						result = true;
					}
					break;
				default:
					result = true;
					break;
				}

			} else {
				result = true;
			}
		} else {
			result = true;
		}

		return result;
	}

	private void Initial() {

		try {

			for (RF_Gate_Setting eachGate : ToolUtility.GetAllGateSetting(setting.getReader_IP())) {
				String key = eachGate.getFab() + eachGate.getArea() + eachGate.getGate();
				if (!gateSetting.containsKey(key)) {
					gateSetting.put(key, eachGate);
				}
			}

			for (RF_Antenna_Setting eachAnt : ToolUtility.GetAntSettingList(setting.getReader_IP())) {
				if (!antSetting.containsKey(eachAnt.getAntenna_No())) {
					if (eachAnt.getAntenna_Type().equals(GlobleVar.ANT_Pallet)) {
						eachAnt.setActive(false);
						eachAnt.setActive_Expire(0);
						ToolUtility.UpdateAntSetting(eachAnt, setting.getReader_IP());
					}
					antSetting.put(eachAnt.getAntenna_No(), eachAnt);
				}
			}

			for (RF_Tag_Setting eachTagSet : ToolUtility.GetTagSettingList(setting.getReader_IP())) {
				// Setting_ID+Tag_Type+Name
				String key = eachTagSet.getSetting_ID() + eachTagSet.getTag_Type() + eachTagSet.getName();
				if (!tagSetting.containsKey(key)) {
					tagSetting.put(key, eachTagSet);
				}
			}

		} catch (Exception e) {
			logger.error(setting.getReader_IP() + " " + "Exception:" + ToolUtility.StackTrace2String(e));
		}
	}

	// public static void main(String[] args) {
	// RF_Reader_Setting tes = new RF_Reader_Setting();
	// tes.setListen_Port(0);
	// tes.setLocation("Cylinder");
	// tes.setOn_Line(true);
	// tes.setReader_IP("10.56.229.212");
	// tes.setReader_Type("AlienType");
	// tes.setTest_Mode(false);
	// AlienReader tt = new AlienReader(tes);
	//
	// Message message = new Message();
	// message.setRawData("5047 4220 3036 E4D6 8F45 60FD
	// FBE7,1513997425519,1513997425519,0,1,69.0");
	// tt.messageReceived(message);
	// }

}
