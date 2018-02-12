package com.innolux.service;

import java.util.Hashtable;
import java.util.List;
import org.apache.log4j.Logger;

import com.innolux.common.GlobleVar;
import com.innolux.common.ToolUtility;
import com.innolux.common.base.ReaderCmd;
import com.innolux.model.RF_Reader_Setting;

public class ReaderCmdService {

	static Hashtable<String, ReaderCmd> readerList = new Hashtable<String, ReaderCmd>();
	private static Logger logger = Logger.getLogger(ReaderCmdService.class);

	public static void Initial() {
		try {

			List<RF_Reader_Setting> result = ToolUtility.GetAllReader();
			for (RF_Reader_Setting eachReader : result) {
				if (eachReader.getTest_Mode() == GlobleVar.TestMode && eachReader.getOn_Line()
						&& eachReader.getReader_Type().equals(GlobleVar.AlienType)) {
					if (!readerList.containsKey(eachReader.getReader_IP())) {
						ReaderCmd cmdObj = new ReaderCmd(eachReader);
						readerList.put(eachReader.getReader_IP(), cmdObj);
					}
				}
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception:" + ToolUtility.StackTrace2String(e));
		}

	}
	
	public static boolean InitialReader(String readerIP) {
		boolean result = false;

		if (readerList.containsKey(readerIP)) {
			ReaderCmd t = readerList.get(readerIP);
			synchronized (t) {
				result = t.InitialReader();
			}
		} else {
			logger.error(readerIP + " is not exist.");
		}

		return result;
	
	}
	
	public static boolean Reconnet(String readerIP) {
		boolean result = false;

		if (readerList.containsKey(readerIP)) {
			ReaderCmd t = readerList.get(readerIP);
			synchronized (t) {
				logger.info("Reconnet start:"+readerIP);
				result = t.Reconnet();
				logger.info("Reconnet end:"+readerIP);
			}
		} else {
			logger.error(readerIP + " is not exist.");
		}

		return result;
	
	}

	public static boolean SendCmd(String readerIP, String cmdStr) {
		boolean result = false;

		if (readerList.containsKey(readerIP)) {
			ReaderCmd t = readerList.get(readerIP);
			synchronized (t) {
				logger.info("SendCmd start:"+readerIP);
				result = t.Send(cmdStr);
				logger.info("SendCmd end:"+readerIP);
			}
		} else {
			logger.error(readerIP + " is not exist.");
		}

		return result;
	}

	public static boolean TimeSync(String readerIP) {
		boolean result = false;
		if (readerList.containsKey(readerIP)) {
			ReaderCmd t = readerList.get(readerIP);
			synchronized (t) {
				logger.info("TimeSync start:"+readerIP);
				result = t.TimeSync();
				logger.info("TimeSync end:"+readerIP);
			}
		} else {
			logger.error(readerIP + " is not exist.");
		}
		return result;
	}

	public static boolean SetAttenuation(String readerIP) {
		boolean result = false;
		if (readerList.containsKey(readerIP)) {
			ReaderCmd t = readerList.get(readerIP);
			synchronized (t) {
				logger.info("SetAttenuation start:"+readerIP);
				result = t.SetAttenuation();
				logger.info("SetAttenuation end:"+readerIP);
			}
		} else {
			logger.error(readerIP + " is not exist.");
		}
		return result;
	}

	public static boolean SetAntennaSequence(String readerIP) {
		boolean result = false;
		if (readerList.containsKey(readerIP)) {
			ReaderCmd t = readerList.get(readerIP);
			synchronized (t) {
				logger.info("SetAntennaSequence start:"+readerIP);
				result = t.SetAntennaSequence();
				logger.info("SetAntennaSequence end:"+readerIP);
			}
		} else {
			logger.error(readerIP + " is not exist.");
		}
		return result;
	}
}
