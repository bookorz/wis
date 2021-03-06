package com.innolux.service;

import java.util.Hashtable;
import java.util.List;

import org.apache.log4j.Logger;

import com.innolux.common.GlobleVar;
import com.innolux.common.Subtitle;
import com.innolux.common.ToolUtility;
import com.innolux.dao.GenericDao;
import com.innolux.dao.JdbcGenericDaoImpl;
import com.innolux.model.RF_Subtitle_Setting;

public class SubtitleService {
	static Hashtable<String, Subtitle> subtitleList = new Hashtable<String, Subtitle>();
	private static Logger logger = Logger.getLogger(SubtitleService.class);

	public static void Initial() {
		long startTime = System.currentTimeMillis();

		try {
			GenericDao<RF_Subtitle_Setting> RF_Subtitle_Setting_Dao = new JdbcGenericDaoImpl<RF_Subtitle_Setting>(
					GlobleVar.WIS_DB);

			List<RF_Subtitle_Setting> result = RF_Subtitle_Setting_Dao.findAllByConditions(null,
					RF_Subtitle_Setting.class);
			for (RF_Subtitle_Setting eachsubtitle : result) {
				if (!subtitleList.containsKey(eachsubtitle.getSubtitle_IP())) {
					subtitleList.put(eachsubtitle.getSubtitle_IP(), new Subtitle(eachsubtitle));
				}
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception:" + ToolUtility.StackTrace2String(e));
		}
		logger.debug("SubtitleService Initial process time:" + (System.currentTimeMillis() - startTime));
	}

	public static void Show(String subtitleIP, String showStr) {

		Thread t = new Thread(new Runnable() {

			@Override
			public void run() {
				if (subtitleList.containsKey(subtitleIP)) {
					Subtitle t = subtitleList.get(subtitleIP);

					t.ShowData(showStr);

				} else {
					logger.error(subtitleIP + " is not exist.");
				}
			}
		});
		t.setDaemon(false);
		t.start();

	}
}
