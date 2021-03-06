package com.innolux.common.base;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;

import com.alien.enterpriseRFID.reader.AlienClass1Reader;
import com.alien.enterpriseRFID.reader.AlienReaderConnectionException;
import com.alien.enterpriseRFID.reader.AlienReaderConnectionRefusedException;
import com.alien.enterpriseRFID.reader.AlienReaderException;
import com.alien.enterpriseRFID.reader.AlienReaderNotValidException;
import com.alien.enterpriseRFID.reader.AlienReaderTimeoutException;
import com.innolux.common.GlobleVar;
import com.innolux.common.MessageFormat;
import com.innolux.common.ToolUtility;
import com.innolux.dao.GenericDao;
import com.innolux.dao.JdbcGenericDaoImpl;
import com.innolux.model.RF_Antenna_Setting;
import com.innolux.model.RF_Reader_Setting;

public class ReaderCmd {
	private RF_Reader_Setting ReaderSet;
	private AlienClass1Reader reader = new AlienClass1Reader();
	private Logger logger = Logger.getLogger(this.getClass());
	private boolean isInitial = false;
	private boolean isError = false;

	public ReaderCmd(RF_Reader_Setting _ReaderSet) {
		
		ReaderSet = _ReaderSet;
		reader.setConnection(ReaderSet.getReader_IP(), 23);
		reader.setUsername("alien");
		reader.setPassword("password");
		


	}

	public boolean TimeSync() {
		boolean result = false;
		try {

			reader.open();

			reader.setTime();
			reader.close();
			result = true;
		} catch (Exception e) {

			logger.error(ReaderSet.getReader_IP() + " " + "Exception:" + ToolUtility.StackTrace2String(e));
		}
		return result;
	}

	public boolean NotifyNow() {
		boolean result = false;
		try {

			reader.open();
			reader.setAutoMode(AlienClass1Reader.ON);
			reader.setNotifyMode(AlienClass1Reader.ON);
			reader.notifyNow();
			reader.close();
			result = true;
			logger.debug(ReaderSet.getReader_IP() + " NotifyNow success.");
		} catch (Exception e) {

			logger.error(ReaderSet.getReader_IP() + " " + "Exception:" + ToolUtility.StackTrace2String(e));
		}
		return result;
	}

	public boolean Send(String cmdStr) {
		boolean result = false;
		long startTime = System.currentTimeMillis();
		try {
			logger.debug(ReaderSet.getReader_IP() + " Send cmd:" + cmdStr);

			reader.open();

			reader.macroRun(cmdStr);
			reader.close();
			result = true;
		} catch (Exception e) {

			logger.error(ReaderSet.getReader_IP() + " " + "Exception:" + ToolUtility.StackTrace2String(e));
		}
		logger.debug("macroRun process time:" + (System.currentTimeMillis() - startTime));
		return result;
	}

	public boolean SetAttenuation() {
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
				// reader.saveSettings();
				reader.close();
			}
			result = true;
		} catch (Exception e) {

			logger.error(ReaderSet.getReader_IP() + " " + "Exception:" + ToolUtility.StackTrace2String(e));
		}
		return result;
	}

	public boolean SetAntennaSequence() {
		long startTime = System.currentTimeMillis();
		boolean result = false;
		try {
			if (reader != null) {

				reader.open();

				reader.setAntennaSequence(ToolUtility.GetAntennaSequence(ReaderSet.getReader_IP()));
				reader.close();
				result = true;
			} else {
				logger.error(ReaderSet.getReader_IP() + " " + "reader is null");
			}
		} catch (Exception e) {
			logger.error(ReaderSet.getReader_IP() + " " + "Exception:" + ToolUtility.StackTrace2String(e));
		}
		logger.debug("SetAntennaSequence process time:" + (System.currentTimeMillis() - startTime));
		return result;
	}

	public boolean Reconnet() {
		long startTime = System.currentTimeMillis();
		boolean result = false;
		try {
			if (reader != null) {

				reader.open();

				reader.setNotifyAddress(InetAddress.getLocalHost().getHostAddress(), ReaderSet.getListen_Port());
				reader.setNotifyMode(AlienClass1Reader.ON);
				reader.close();
				isError = false;
				result = true;
			} else {
				logger.error(ReaderSet.getReader_IP() + " " + "reader is null");
			}
		} catch (AlienReaderTimeoutException e) {
			logger.error(ReaderSet.getReader_IP() + " " + "Exception:" + ToolUtility.StackTrace2String(e));
			if (!isError) {
				ToolUtility.MesDaemon.sendMessage(
						MessageFormat.SendAms(ReaderSet.getFab(), "Reader_Error", "WISErrorPallet", "Reader_Error",
								"Reader連線異常，請檢查Reader主機狀態，IP:" + ReaderSet.getReader_IP(), "ErrorMonitor"),
						GlobleVar.SendToAMS);
			}
			isError = true;
			// ToolUtility.ShowReaderInfoToSubtile("Reader連線異常，請檢查Reader主機狀態",
			// ReaderSet.getReader_IP());
		} catch (AlienReaderConnectionRefusedException e) {
			logger.error(ReaderSet.getReader_IP() + " " + "Exception:" + ToolUtility.StackTrace2String(e));
			// ToolUtility.ShowReaderInfoToSubtile("Reader連線異常，請檢查Reader主機狀態",
			// ReaderSet.getReader_IP());
		} catch (AlienReaderNotValidException e) {
			logger.error(ReaderSet.getReader_IP() + " " + "Exception:" + ToolUtility.StackTrace2String(e));
			// ToolUtility.ShowReaderInfoToSubtile("Reader連線異常，請檢查Reader主機狀態",
			// ReaderSet.getReader_IP());
		} catch (AlienReaderConnectionException e) {
			logger.error(ReaderSet.getReader_IP() + " " + "Exception:" + ToolUtility.StackTrace2String(e));
			// ToolUtility.ShowReaderInfoToSubtile("Reader連線異常，請檢查Reader主機狀態",
			// ReaderSet.getReader_IP());
		} catch (UnknownHostException e) {
			logger.error(ReaderSet.getReader_IP() + " " + "Exception:" + ToolUtility.StackTrace2String(e));
			// ToolUtility.ShowReaderInfoToSubtile("Reader連線異常，請檢查Reader主機狀態",
			// ReaderSet.getReader_IP());
		} catch (AlienReaderException e) {
			logger.error(ReaderSet.getReader_IP() + " " + "Exception:" + ToolUtility.StackTrace2String(e));
			// ToolUtility.ShowReaderInfoToSubtile("Reader連線異常，請檢查Reader主機狀態",
			// ReaderSet.getReader_IP());
		}
		logger.debug("Reconnet process time:" + (System.currentTimeMillis() - startTime));
		return result;
	}

	public boolean InitialReader() {
		boolean result = false;

		try {

			if (reader != null) {

				reader.open();
				if (!isInitial) {
					reader.setAutoMode(AlienClass1Reader.OFF);
					reader.setNotifyMode(AlienClass1Reader.OFF);
				}
				reader.setNotifyAddress(InetAddress.getLocalHost().getHostAddress(), ReaderSet.getListen_Port());

				if (ReaderSet.getLocation().equals("Cylinder")) {
					reader.setNetworkTimeout(65535);
					reader.setAntennaSequence("0 1 2 3");
					reader.setTagListAntennaCombine(AlienClass1Reader.ON);
					reader.setAutoStartPause(45000); // 停止40秒
					reader.setAutoStopTimer(15000); // 讀取20秒
					reader.setNotifyTime(300);
					reader.setNotifyHeader(AlienClass1Reader.OFF);
					reader.setPersistTime(-1);
					reader.setAutoMode(AlienClass1Reader.ON);
					reader.setNotifyKeepAliveTime(32767);
					reader.setMask(8, 0, "50");
					reader.setTime();
					reader.setTagListFormat(AlienClass1Reader.CUSTOM_FORMAT);
					reader.setNotifyFormat(AlienClass1Reader.CUSTOM_FORMAT);
					String CustomFormatStr;

					CustomFormatStr = "${TAGIDW},${MSEC1},${MSEC2},${TX},${COUNT},${RSSI_MAX}";

					reader.setTagListCustomFormat(CustomFormatStr);
					reader.setNotifyMode(AlienClass1Reader.OFF);
					reader.setNotifyMode(AlienClass1Reader.ON);
				} else {
					// reader.notifyNow();
					// reader.setAntennaSequence("1 3 1 0 3 1 3 2");
					reader.setNetworkTimeout(65535);
					reader.setAntennaSequence(ToolUtility.GetAntennaSequence(ReaderSet.getReader_IP()));
					reader.setNotifyTrigger("OFF");
					reader.setTagListAntennaCombine(AlienClass1Reader.OFF);
					reader.setAutoStartPause(0);
					reader.setAutoStopPause(0);
					reader.setNotifyTime(1);
					reader.setNotifyHeader(AlienClass1Reader.OFF);
					reader.setNotifyKeepAliveTime(32767);
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
					logger.debug(ReaderSet.getReader_IP() + " notify address" + reader.getNotifyAddress());
					reader.notifyNow();
				}
				if (!isInitial) {
					reader.saveSettings();
					isInitial = true;
				}
				reader.close();
			} else {
				logger.error(ReaderSet.getReader_IP() + " " + "reader is null");
			}
			result = true;
		} catch (Exception e) {
			logger.error(ReaderSet.getReader_IP() + " " + "Exception:" + ToolUtility.StackTrace2String(e));
		}
		return result;
	}
}
