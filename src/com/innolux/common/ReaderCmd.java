package com.innolux.common;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.alien.enterpriseRFID.reader.AlienClass1Reader;
import com.innolux.dao.GenericDao;
import com.innolux.dao.JdbcGenericDaoImpl;
import com.innolux.model.RF_Antenna_Setting;
import com.innolux.model.RF_Reader_Setting;

public class ReaderCmd {
	private RF_Reader_Setting ReaderSet;
	private AlienClass1Reader reader = new AlienClass1Reader();
	private Logger logger = Logger.getLogger(this.getClass());
	

	public ReaderCmd(RF_Reader_Setting _ReaderSet) {
		ReaderSet = _ReaderSet;
		reader.setConnection(ReaderSet.getReader_IP(), 23);
		reader.setUsername("alien");
		reader.setPassword("password");
		if(!InitialReader()){
			logger.error(ReaderSet.getReader_IP() + " " + "InitialReader fail");
		}
		if(!SetAttenuation()){
			logger.error(ReaderSet.getReader_IP() + " " + "SetAttenuation fail");
		}
	}

	public synchronized boolean TimeSync() {
		boolean result = false;
		try {
			reader.open();
			reader.setTime();
			reader.close();
			result = true;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error(ReaderSet.getReader_IP() + " " + "Exception:" + ToolUtility.StackTrace2String(e));
		}
		return result;
	}

	public synchronized boolean Send(String cmdStr) {
		boolean result = false;
		try {
			reader.open();
			reader.macroRun(cmdStr);
			reader.close();
			result = true;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error(ReaderSet.getReader_IP() + " " + "Exception:" + ToolUtility.StackTrace2String(e));
		}
		return result;
	}

	public synchronized boolean SetAttenuation() {
		boolean result = false;
		try {
			GenericDao<RF_Antenna_Setting> RF_Antenna_Setting_Dao = new JdbcGenericDaoImpl<RF_Antenna_Setting>(
					GlobleVar.WIS_DB);
			Map<String, Object> sqlWhereMap = new HashMap<String, Object>();

			sqlWhereMap.put("reader_ip", ReaderSet.getReader_IP());

			List<RF_Antenna_Setting> antSets = RF_Antenna_Setting_Dao.findAllByConditions(sqlWhereMap,
					RF_Antenna_Setting.class);

			if (antSets.size() != 0) {
				reader.open();
				for (RF_Antenna_Setting eachSet : antSets) {
					reader.setRFAttenuation(eachSet.getAntenna_No(), eachSet.getRFAttenuation());
				}
				reader.saveSettings();
				reader.close();
			}
			result = true;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error(ReaderSet.getReader_IP() + " " + "Exception:" + ToolUtility.StackTrace2String(e));
		}
		return result;
	}

	public synchronized boolean InitialReader() {
		boolean result = false;
		try {
			if (reader != null) {

				reader.open();
				reader.setAutoMode(AlienClass1Reader.OFF);
				reader.setNotifyMode(AlienClass1Reader.OFF);
				reader.setNotifyAddress(InetAddress.getLocalHost().getHostAddress(), ReaderSet.getListen_Port());
				if (ReaderSet.getReader_Type().toUpperCase().equals("CYLINDER")) {

					reader.setAntennaSequence("0 1 2 3");
					reader.setTagListAntennaCombine(AlienClass1Reader.ON);
					reader.setAutoStartPause(40000); // 停止40秒
					reader.setAutoStopTimer(20000); // 讀取20秒
					reader.setNotifyTime(600);
					reader.setNotifyHeader(AlienClass1Reader.OFF);
					reader.setPersistTime(-1);
					reader.setAutoMode(AlienClass1Reader.ON);
					reader.setNotifyMode(AlienClass1Reader.ON);
					reader.setMask(8, 0, "50");
					reader.setTime();
					reader.setTagListFormat(AlienClass1Reader.CUSTOM_FORMAT);
					reader.setNotifyFormat(AlienClass1Reader.CUSTOM_FORMAT);
					String CustomFormatStr;

					CustomFormatStr = "${TAGIDW},${MSEC1},${MSEC2},${TX},${COUNT},${RSSI_MAX}";

					reader.setTagListCustomFormat(CustomFormatStr);

				} else {
					reader.notifyNow();
					reader.setAntennaSequence("1 3 1 0 3 1 3 2");
					reader.setNotifyTrigger("OFF");
					reader.setTagListAntennaCombine(AlienClass1Reader.OFF);
					reader.setAutoStartPause(0);
					reader.setAutoStopPause(0);
					reader.setNotifyTime(1);
					reader.setNotifyHeader(AlienClass1Reader.OFF);
					reader.setPersistTime(1);
					reader.setAutoMode(AlienClass1Reader.ON);
					reader.setNotifyMode(AlienClass1Reader.ON);
					reader.setTagListAntennaCombine(AlienClass1Reader.OFF);
					reader.setTime();
					reader.setTagListFormat(AlienClass1Reader.CUSTOM_FORMAT);
					reader.setNotifyFormat(AlienClass1Reader.CUSTOM_FORMAT);
					String CustomFormatStr;

					CustomFormatStr = "${TAGIDW},${MSEC1},${MSEC2},${TX},${COUNT},${RSSI_MAX}";

					reader.setTagListCustomFormat(CustomFormatStr);

				}
				reader.saveSettings();
				reader.close();
			}
			result = true;
		} catch (Exception e) {
			logger.error(ReaderSet.getReader_IP() + " " + "Exception:" + ToolUtility.StackTrace2String(e));
		}
		return result;
	}
}
