package com.innolux.service;

import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;

import com.innolux.common.GlobleVar;
import com.innolux.common.ToolUtility;
import com.innolux.common.base.ReaderCmd;
import com.innolux.model.RF_Reader_Setting;
import com.innolux.model.RF_Subtitle_Setting;

public class ReaderCmdService {

	static Hashtable<String, ReaderCmd> readerList = new Hashtable<String, ReaderCmd>();
	private static Logger logger = Logger.getLogger(ReaderCmdService.class);

	public static void Initial() {
		try {

			List<RF_Reader_Setting> result = ToolUtility.GetAllReader();
			for (RF_Reader_Setting eachReader : result) {
				if (eachReader.getTest_Mode() == GlobleVar.TestMode && eachReader.getOn_Line()) {
					if (!readerList.containsKey(eachReader.getReader_IP())) {
						readerList.put(eachReader.getReader_IP(), new ReaderCmd(eachReader));
					}
				}
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception:" + ToolUtility.StackTrace2String(e));
		}

		Timer timer = new Timer();

		// 30sec
		long period = 30 * 1000;
		TimerTask task = new TimerTask() {
			@Override
			public void run() {

				for(ReaderCmd each:readerList.values()) {
					each.InitialReader();
				}

			}
		};

		timer.scheduleAtFixedRate(task, new Date(), period);

	}

	public static boolean SendCmd(String readerIP, String cmdStr) {
		boolean result = false;
		if (readerList.containsKey(readerIP)) {
			ReaderCmd t = readerList.get(readerIP);

			result = t.Send(cmdStr);
			logger.info(readerIP + " SendCmd:" + cmdStr + " result:" + result);
		} else {
			logger.error(readerIP + " is not exist.");
		}
		return result;
	}

	public static boolean TimeSync(String readerIP) {
		boolean result = false;
		if (readerList.containsKey(readerIP)) {
			ReaderCmd t = readerList.get(readerIP);

			result = t.TimeSync();

		} else {
			logger.error(readerIP + " is not exist.");
		}
		return result;
	}

	public static boolean SetAttenuation(String readerIP) {
		boolean result = false;
		if (readerList.containsKey(readerIP)) {
			ReaderCmd t = readerList.get(readerIP);

			result = t.SetAttenuation();

		} else {
			logger.error(readerIP + " is not exist.");
		}
		return result;
	}

	public static boolean SetAntennaSequence(String readerIP) {
		boolean result = false;
		if (readerList.containsKey(readerIP)) {
			ReaderCmd t = readerList.get(readerIP);

			result = t.SetAntennaSequence();

		} else {
			logger.error(readerIP + " is not exist.");
		}
		return result;
	}
}
