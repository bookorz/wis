package com.innolux;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;

import com.innolux.common.GlobleVar;
import com.innolux.common.ToolUtility;
import com.innolux.common.MessageFormat;
import com.innolux.model.RF_ContainerInfo;
import com.innolux.model.RF_Cylinder_Status;
import com.innolux.model.RF_Gate_Error;
import com.innolux.model.RF_Gate_Setting;
import com.innolux.model.RF_Reader_Setting;
import com.innolux.model.RF_Subtitle_Setting;
import com.innolux.receiver.AlienReader;
import com.innolux.receiver.AlienReaderBySocket;
import com.innolux.receiver.WMS_Message;
import com.innolux.service.ReaderCmdService;
import com.innolux.service.SubtitleService;
import com.innolux.service.WebApiService;

public class WIS_Main {

	private static Logger logger = Logger.getLogger(WIS_Main.class);

	public static void main(String[] args) {

		SubtitleService.Initial();
		ToolUtility.Initial();
		InitialGates();
		new WebApiService().start();
		new WMS_Message();
		SetAliveNotify();
		GateErrorMonitor();
		InitialReaders();
		
		CustSubtileMonitor();
		CylinderMonitor();

	}

	private static void InitialGates() {
		long startTime = System.currentTimeMillis();

		List<RF_Gate_Setting> gateList = ToolUtility.GetAllGateSetting("InitialGates");
		for (RF_Gate_Setting each : gateList) {
			RF_ContainerInfo container = ToolUtility.GetContainerInfo(each.getFab(), each.getArea(), each.getGate(),
					"WIS_Main");
			if (container != null) {
				ToolUtility.Subtitle(each.getFab(), each.getArea(), each.getGate(),
						container.getCar_Type().replace("Container", "貨櫃").replace("Truck", "貨車") + "進入:"
								+ container.getContainer_ID(),
						"WIS_Main");
			} else {
				ToolUtility.Subtitle(each.getFab(), each.getArea(), each.getGate(), "無車輛進入", "WIS_Main");
			}
			each.setDirection_StartTime(0);
			each.setDirection_EndTime(0);
			each.setLast_MarkTag_Time(System.currentTimeMillis());
			ToolUtility.UpdateGateSetting(each, "InitialGates");
		}
		logger.debug("InitalGates process time:" + (System.currentTimeMillis() - startTime));
	}

	private static void InitialReaders() {
		long startTime = System.currentTimeMillis();

		List<RF_Reader_Setting> readerList = ToolUtility.GetAllReader();

		for (RF_Reader_Setting eachReader : readerList) {
			if (eachReader.getTest_Mode() == GlobleVar.TestMode && eachReader.getOn_Line()) {
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
		logger.debug("InitialReaders process time:" + (System.currentTimeMillis() - startTime));
		startTime = System.currentTimeMillis();
		ReaderCmdService.Initial();
		logger.debug("ReaderCmdServiceInitial process time:" + (System.currentTimeMillis() - startTime));
	}

	private static void SetAliveNotify() {
		TimerTask task = new TimerTask() {
			@Override
			public void run() {

				ToolUtility.MesDaemon.sendMessage(
						MessageFormat.SendAms("T2", "庫存訊息", "WISErrorPallet", "庫存訊息",
								ToolUtility.GetCylinderSummary("T2", "SetAliveNotify"), "SetAliveNotify"),
						GlobleVar.SendToAMS);
			}
		};

		Calendar calendar = Calendar.getInstance();
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH);
		int day = calendar.get(Calendar.DAY_OF_MONTH);

		calendar.set(year, month, day, 8, 00, 00);
		calendar.add(Calendar.DAY_OF_MONTH, 1);
		Date date = calendar.getTime();
		Timer timer = new Timer();

		long period = 86400 * 1000;

		timer.scheduleAtFixedRate(task, date, period);
	}

	private static void GateErrorMonitor() {

		Timer timer = new Timer();

		// 5min
		long period = 5 * 60 * 1000;
		TimerTask task = new TimerTask() {
			@Override
			public void run() {
				long startTime = System.currentTimeMillis();

				for (RF_Gate_Error each : ToolUtility.GetAllGateError("GateErrorMonitor")) {
					if (System.currentTimeMillis() - each.getTimeStamp() > 1800000) {
						ToolUtility.MesDaemon.sendMessage(
								MessageFormat.SendAms(each.getFab(), each.getError_Type(), "WISErrorPallet",
										each.getError_Type(), each.getError_Message(), "GateErrorMonitor"),
								GlobleVar.SendToAMS);
					}
				}

				logger.info("GateErrorMonitor process time:" + (System.currentTimeMillis() - startTime));
			}
		};

		timer.scheduleAtFixedRate(task, new Date(), period);
	}

	private static void CustSubtileMonitor() {

		Timer timer = new Timer();

		// 5min
		long period = 5 * 60 * 1000;
		TimerTask task = new TimerTask() {
			@Override
			public void run() {
				long startTime = System.currentTimeMillis();

				List<RF_Subtitle_Setting> subtitleList = ToolUtility.GetAllSubtitle();

				for (RF_Subtitle_Setting eachSubtitle : subtitleList) {
					if (eachSubtitle.getCust_Active()) {
						if (System.currentTimeMillis() - eachSubtitle.getUpdate_Time() > (5 * 60 * 1000)) {
							if (!eachSubtitle.getCurrent_Subtitle().equals(eachSubtitle.getCust_Subtitle())) {
								RF_ContainerInfo container = ToolUtility.GetContainerInfo(eachSubtitle.getFab(),
										eachSubtitle.getArea(), eachSubtitle.getGate(), "CustSubtileMonitor");
								if (container == null) {// When this port is not binding car
									ToolUtility.Subtitle(eachSubtitle.getFab(), eachSubtitle.getArea(),
											eachSubtitle.getGate(), eachSubtitle.getCust_Subtitle(),
											"CustSubtileMonitor");
								}
							}
						}

					}
				}
				logger.info("CustSubtitleMonitor process time:" + (System.currentTimeMillis() - startTime));
			}
		};

		timer.scheduleAtFixedRate(task, new Date(), period);
	}

	private static void CylinderMonitor() {
		try {
			Thread.sleep(50 * 60 * 1000);
		} catch (InterruptedException e) {

			logger.error("CylinderMonitor exception:" + ToolUtility.StackTrace2String(e));
		}
		Timer timer = new Timer();

		// 5min
		long period = 5 * 60 * 1000;
		TimerTask task = new TimerTask() {
			@Override
			public void run() {
				long startTime = System.currentTimeMillis();

				List<RF_Cylinder_Status> cylinderList = ToolUtility.GetAllCylinders("CylinderMonitor");

				for (RF_Cylinder_Status eachCylinder : cylinderList) {
					if (System.currentTimeMillis() - eachCylinder.getUpdateTime() > 2800000) {
						switch (eachCylinder.getPosition()) {
						case GlobleVar.ANT_Small_Stock:
						case GlobleVar.ANT_Big_Stock:
							if (eachCylinder.getStatus().equals(GlobleVar.Cylinder_Empty)) {
								ToolUtility.DeleteCylinder(eachCylinder, "CylinderMonitor");
								eachCylinder.setStatus(GlobleVar.Cylinder_Destroy);
								ToolUtility.SetCylinderHistory(eachCylinder, "CylinderMonitor");
								ToolUtility.MesDaemon.sendMessage(
										MessageFormat.SendCylinderStatus(eachCylinder, "CylinderMonitor"),
										GlobleVar.SendToAMS);
							} else {
								ToolUtility.DeleteCylinder(eachCylinder, "CylinderMonitor");
								eachCylinder.setStatus(GlobleVar.Cylinder_Disappear);
								ToolUtility.MesDaemon.sendMessage(
										MessageFormat.SendCylinderStatus(eachCylinder, "CylinderMonitor"),
										GlobleVar.SendToAMS);
								ToolUtility.SetCylinderHistory(eachCylinder, "CylinderMonitor");

								String Text = "鋼瓶" + ToolUtility.AddSpace(eachCylinder.getTag_ID()) + "無讀取資料，請進行檢查";
								RF_Gate_Setting gate = ToolUtility.GetGateSetting(eachCylinder.getFab(),
										eachCylinder.getArea(), "0", "CylinderMonitor");

								if (gate != null) {

									ToolUtility.VoiceSend(gate.getVoice_Path(), Text, "CylinderMonitor");
								}
								ToolUtility.MesDaemon.sendMessage(MessageFormat.SendAms(eachCylinder.getFab(),
										GlobleVar.Cylinder_Disappear, "WISCylinders", "鋼瓶不見", Text, "CylinderMonitor"),
										GlobleVar.SendToAMS);

							}
							break;
						case GlobleVar.ANT_Small_Use:
						case GlobleVar.ANT_Big_Use:
							ToolUtility.DeleteCylinder(eachCylinder, "CylinderMonitor");
							eachCylinder.setStatus(GlobleVar.Cylinder_Disappear);
							ToolUtility.MesDaemon.sendMessage(
									MessageFormat.SendCylinderStatus(eachCylinder, "CylinderMonitor"),
									GlobleVar.SendToWMS);
							ToolUtility.SetCylinderHistory(eachCylinder, "CylinderMonitor");

							String Text = "鋼瓶" + ToolUtility.AddSpace(eachCylinder.getTag_ID()) + "無讀取資料，請進行檢查";
							RF_Gate_Setting gate = ToolUtility.GetGateSetting(eachCylinder.getFab(),
									eachCylinder.getArea(), "0", "CylinderMonitor");

							if (gate != null) {

								ToolUtility.VoiceSend(gate.getVoice_Path(), Text, "CylinderMonitor");
							}
							ToolUtility.MesDaemon
									.sendMessage(
											MessageFormat.SendAms(eachCylinder.getFab(), GlobleVar.Cylinder_Disappear,
													"WISCylinders", "鋼瓶不見", Text, "CylinderMonitor"),
											GlobleVar.SendToAMS);
							break;
						}

					}
				}
				logger.info("CylinderMonitor process time:" + (System.currentTimeMillis() - startTime));
			}
		};

		timer.scheduleAtFixedRate(task, new Date(), period);
	}


}
