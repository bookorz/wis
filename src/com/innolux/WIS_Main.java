package com.innolux;

import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.innolux.common.GlobleVar;
import com.innolux.common.ToolUtility;
import com.innolux.model.RF_Reader_Setting;
import com.innolux.model.RF_Subtitle_Setting;
import com.innolux.receiver.AlienReader;
import com.innolux.receiver.AlienReaderBySocket;
import com.innolux.service.ReaderCmdService;
import com.innolux.service.SubtitleService;

public class WIS_Main {

	public static void main(String[] args) {

		InitialReaders();

		CustSubtileMonitor();
	}

	private static void InitialReaders() {
		List<RF_Reader_Setting> readerList = ToolUtility.GetAllReader();
		SubtitleService.Initial();
		for (RF_Reader_Setting eachReader : readerList) {
			if(eachReader.getTest_Mode()==GlobleVar.TestMode && eachReader.getOn_Line()) {
				switch (eachReader.getReader_Type()) {
				case GlobleVar.AlienType:
					new AlienReader(eachReader);
					break;
				case GlobleVar.SocketType:
					new AlienReaderBySocket(eachReader);
					break;

				}
			}
		}
		
		ReaderCmdService.Initial();
		
	}

	private static void CustSubtileMonitor() {

		Timer timer = new Timer();

		// 5min
		long period = 5 * 60 * 1000;
		TimerTask task = new TimerTask() {
			@Override
			public void run() {

				List<RF_Subtitle_Setting> subtitleList = ToolUtility.GetAllSubtitle();

				for (RF_Subtitle_Setting eachSubtitle : subtitleList) {
					if (eachSubtitle.getCust_Active()) {
						if (System.currentTimeMillis() - eachSubtitle.getUpdate_Time() > (5 * 60 * 1000)) {
							if (!eachSubtitle.getCurrent_Subtitle().equals(eachSubtitle.getCust_Subtitle())) {
								ToolUtility.Subtitle(eachSubtitle.getFab(), eachSubtitle.getArea(),
										eachSubtitle.getGate(), eachSubtitle.getCust_Subtitle(), "CustSubtileMonitor");
							}
						}

					}
				}

			}
		};

		timer.scheduleAtFixedRate(task, new Date(), period);
	}
	
	private static void CylinderMonitor() {

		Timer timer = new Timer();

		// 5min
		long period = 5 * 60 * 1000;
		TimerTask task = new TimerTask() {
			@Override
			public void run() {

				List<RF_Subtitle_Setting> subtitleList = ToolUtility.GetAllSubtitle();

				for (RF_Subtitle_Setting eachSubtitle : subtitleList) {
					if (eachSubtitle.getCust_Active()) {
						if (System.currentTimeMillis() - eachSubtitle.getUpdate_Time() > (5 * 60 * 1000)) {
							if (!eachSubtitle.getCurrent_Subtitle().equals(eachSubtitle.getCust_Subtitle())) {
								ToolUtility.Subtitle(eachSubtitle.getFab(), eachSubtitle.getArea(),
										eachSubtitle.getGate(), eachSubtitle.getCust_Subtitle(), "CustSubtileMonitor");
							}
						}

					}
				}

			}
		};

		timer.scheduleAtFixedRate(task, new Date(), period);
	}
}
