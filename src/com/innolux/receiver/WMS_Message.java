package com.innolux.receiver;

import java.util.List;

import org.apache.log4j.Logger;

import com.innolux.common.GlobleVar;
import com.innolux.common.MessageFormat;
import com.innolux.common.ToolUtility;
import com.innolux.interfaces.ITibcoRvListenService;
import com.innolux.model.RF_ContainerInfo;
import com.innolux.model.RF_Cylinder_Status;
import com.innolux.model.RF_Error_Pallet;
import com.innolux.model.RF_Gate_Error;
import com.innolux.model.RF_Gate_Setting;
import com.innolux.service.TibcoRvListen;

public class WMS_Message implements ITibcoRvListenService {

	private Logger logger = Logger.getLogger(this.getClass());

	public WMS_Message() {
		TibcoRvListen rv = new TibcoRvListen("172.20.8.13:8585", GlobleVar.ListenFromWMS, "8585", "");
		rv.setRvListener(this);
		rv.start();;
	}

	@Override
	public void onRvMsg(String msg) {
		
		int beginIdx = 0;
		int endIdx = 0;
		String searchText = "";
		String eventType = "";

		searchText = ">>L ";
		beginIdx = msg.indexOf(searchText) + searchText.length();
		endIdx = msg.indexOf(" ", beginIdx + 1);
		eventType = msg.substring(beginIdx, endIdx);

		switch (eventType) {
		case "WmsRfidErrorReset":
			String fab = msg.substring(msg.indexOf("<FAB>") + 5, msg.indexOf("</FAB>"));
			String area = msg.substring(msg.indexOf("<AREA>") + 6, msg.indexOf("</AREA>"));
			String gate = msg.substring(msg.indexOf("<GATEID>") + 8, msg.indexOf("</GATEID>"));
			String empno = msg.substring(msg.indexOf("<EMPNO>") + 7, msg.indexOf("</EMPNO>"));
			RF_Gate_Error gateErr =  ToolUtility.GetGateError(fab, area, gate, "", "RV");
			String palletStr = "";
			if(gateErr != null) {
				palletStr = gateErr.getError_Message();
			}else {
				palletStr = "No Error Exist";
			}
			ToolUtility.DeleteGateError(fab, area, gate, "", "RV");

			List<RF_Error_Pallet> errPallets = ToolUtility.GetErrorPalletList(fab, area, gate, "RV");
			for(RF_Error_Pallet each:errPallets) {
				ToolUtility.DeletePallet(each.getPallet_ID(), "RV");
			}
			
			ToolUtility.ClearErrorPallet(fab, area, gate, "RV");
			RF_ContainerInfo container = ToolUtility.GetContainerInfo(fab, area, gate, "RV");
			ToolUtility.Subtitle(fab, area, gate, ToolUtility.InitSubtitleStr(container, "RV"), "RV");

			ToolUtility.SignalTower(fab, area, gate, GlobleVar.RedOff, "RV");

			ToolUtility.MesDaemon.sendMessage(MessageFormat.ReplyRfidErrorReset(fab, area, gate, palletStr, empno,
					container.getContainer_ID(), "RV"), GlobleVar.SendToWMS);

			break;
		case "WmsRfidStatusChange":

			fab = msg.substring(msg.indexOf("<FAB>") + 5, msg.indexOf("</FAB>"));
			area = msg.substring(msg.indexOf("<AREA>") + 6, msg.indexOf("</AREA>"));
			gate = msg.substring(msg.indexOf("<GATEID>") + 8, msg.indexOf("</GATEID>"));
			String action = msg.substring(msg.indexOf("<ACTION>") + 8, msg.indexOf("</ACTION>"));
			String status = msg.substring(msg.indexOf("<STATUS>") + 8, msg.indexOf("</STATUS>"));

			container = ToolUtility.GetContainerInfo(fab, area, gate, "RV");
			container.setCurrentAction(action);
			container.setCurrentStatus(status);
			ToolUtility.UpdateContainerInfo(container, "RV");

			break;
		case "WmsReturn2CIMToxicity":
			String ERR_MSG = msg.substring(msg.indexOf("<ERR_MSG>") + 9, msg.indexOf("</ERR_MSG>")).trim();
			String Status = msg.substring(msg.indexOf("<STATUS>") + 8, msg.indexOf("</STATUS>")).trim();

			if (!ERR_MSG.equals("")) {
				String PalletID = msg.substring(msg.indexOf("<PALLET_ID>") + 5, msg.indexOf("</PALLET_ID>"));
				RF_Cylinder_Status cylinder = ToolUtility.GetCylinder(PalletID, "RV");

				if (cylinder == null) {

					logger.debug("WMS Set empty error: the pallet " + PalletID + " is not exist in db");
				} else {
					RF_Gate_Setting gateSetting = ToolUtility.GetGateSetting(cylinder.getFab(), cylinder.getArea(), "0", "RV");

					ToolUtility.VoiceSend(gateSetting.getVoice_Path(), ERR_MSG, "RV");

				}
			} else if (Status.toUpperCase().equals("EMPTY")) {
				String PalletID = msg.substring(msg.indexOf("<PALLET_ID>") + 5, msg.indexOf("</PALLET_ID>"));
				RF_Cylinder_Status cylinder = ToolUtility.GetCylinder(PalletID, "RV");
				if (cylinder == null) {

					logger.debug("WMS Set empty error: the pallet " + PalletID + " is not exist in db");
				} else {
					if (cylinder.getCylinder_Type().equals("small")) {
						cylinder.setPosition(GlobleVar.ANT_Small_Stock);
					} else {
						cylinder.setPosition(GlobleVar.ANT_Big_Stock);
					}
					cylinder.setNew_Position("");
					cylinder.setCheck_Times(0);
					cylinder.setStatus(GlobleVar.Cylinder_Empty);

					ToolUtility.SetCylinder(cylinder, "RV");
				}
			}
			break;
		case "OperationReport":
			fab = msg.substring(msg.indexOf("<FAB>") + 5, msg.indexOf("</FAB>"));
			area = msg.substring(msg.indexOf("<AREA>") + 6, msg.indexOf("</AREA>"));
			gate = msg.substring(msg.indexOf("<GATEID>") + 8, msg.indexOf("</GATEID>"));
			String operationType = msg.substring(msg.indexOf("<OPERATIONTYPE>") + 15, msg.indexOf("</OPERATIONTYPE>"));
			status = msg.substring(msg.indexOf("<STATUS>") + 8, msg.indexOf("</STATUS>"));

			container = ToolUtility.GetContainerInfo(fab, area, gate, "RV");
			if (container != null) {

				switch (operationType) {
				case "DN":
					container.setCurrent_Operation(GlobleVar.DeliveryLoad);
					break;
				case "PALLET":
					container.setCurrent_Operation(GlobleVar.EmptyWrapUnload);
					break;
				case "PLANTMOVE":
					container.setCurrent_Operation(GlobleVar.TransferIn);
					break;
				case "ASN":
					container.setCurrent_Operation(GlobleVar.ASNUnload);
					break;
				}

				switch (status) {
				case "BEGIN":
					
					container.setProcess_Start(ToolUtility.GetNowTimeStr());
					break;
				case "END":
					container.setProcess_End(ToolUtility.GetNowTimeStr());
			
					break;
				}

				ToolUtility.UpdateContainerInfo(container, "RV");
			} else {
				logger.debug("Container is not found.");
			}
			break;
		}

	}

}
