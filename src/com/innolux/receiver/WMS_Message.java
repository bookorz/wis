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
import com.innolux.service.base.RVMessage;

public class WMS_Message implements ITibcoRvListenService {

	private Logger logger = Logger.getLogger(this.getClass());

	public WMS_Message() {
		TibcoRvListen rv = new TibcoRvListen("172.20.8.13:8585", GlobleVar.ListenFromWMS, "8585", "");
		rv.setRvListener(this);
		rv.start();

	}

	@Override
	public void onRvMsg(RVMessage rvMsg) {
		try {
			String msg = rvMsg.MessageData;
			int beginIdx = 0;
			int endIdx = 0;
			String searchText = "";
			String eventType = "";
			if(rvMsg.ReplySubject==null) {
				rvMsg.ReplySubject=GlobleVar.SendToWMS;
			}

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
				RF_Gate_Error gateErr = ToolUtility.GetGateError(fab, area, gate, "", "RV");
				String palletStr = "";
				if (gateErr != null) {
					palletStr = gateErr.getError_Message();
				} else {
					palletStr = "No Error Exist";
				}
				ToolUtility.DeleteGateError(fab, area, gate, "", "RV");

				List<RF_Error_Pallet> errPallets = ToolUtility.GetErrorPalletList(fab, area, gate, "RV");
				for (RF_Error_Pallet each : errPallets) {
					ToolUtility.DeletePallet(each.getPallet_ID(), "RV");
				}

				ToolUtility.ClearErrorPallet(fab, area, gate, "RV");
				RF_ContainerInfo container = ToolUtility.GetContainerInfo(fab, area, gate, "RV");
				ToolUtility.Subtitle(fab, area, gate, ToolUtility.InitSubtitleStr(container, "RV"), "RV");

				ToolUtility.SignalTower(fab, area, gate, GlobleVar.RedOff, "RV");
				if (container != null) {
					ToolUtility.MesDaemon.sendMessage(MessageFormat.ReplyRfidErrorReset(fab, area, gate, palletStr,
							empno, container.getContainer_ID(), "RV"), rvMsg.ReplySubject);
				} else {
					ToolUtility.MesDaemon.sendMessage(MessageFormat.ReplyRfidErrorReset(fab, area, gate, palletStr,
							empno, "Container is not exist", "RV"), rvMsg.ReplySubject);
				}
				// String readerIP = ToolUtility.GetReaderIP(fab, area, gate);
				// if(!readerIP.equals("")) {
				// ToolUtility.SetReaderError(readerIP, false);
				// }

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
						RF_Gate_Setting gateSetting = ToolUtility.GetGateSetting(cylinder.getFab(), cylinder.getArea(),
								"0", "RV");

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
				String operationType = msg.substring(msg.indexOf("<OPERATIONTYPE>") + 15,
						msg.indexOf("</OPERATIONTYPE>"));
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
					case "PLANTMOVEIN":
						container.setCurrent_Operation(GlobleVar.TransferIn);
						break;
					case "PLANTMOVEOUT":
						container.setCurrent_Operation(GlobleVar.TransferOut);
						break;
					case "ASN":
						container.setCurrent_Operation(GlobleVar.ASNUnload);
						break;
					}

					switch (status) {
					case "BEGIN":

						container.setProcess_Start(ToolUtility.GetNowTimeStr());
						container.setProcess_End("");
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
			case "WmsCylinderInfoXml":
				String Pallet_ID = msg.substring(msg.indexOf("<PALLETID>") + 10, msg.indexOf("</PALLETID>"));
				Status = msg.substring(msg.indexOf("<STATUS>") + 8, msg.indexOf("</STATUS>"));
				
				
				RF_Cylinder_Status cylinderInfo = ToolUtility.GetCylinder(Pallet_ID, "RV");
				if(cylinderInfo!=null) {
					cylinderInfo.setStatus(Status);
					cylinderInfo.setStatus_ChangeTime(ToolUtility.GetNowTimeStr());
					ToolUtility.SetCylinder(cylinderInfo, "RV");
					ToolUtility.SetCylinderHistory(cylinderInfo, "RV");
					ToolUtility.MesDaemon.sendMessage(MessageFormat.SendReply("OK", "Update successfully.", ToolUtility.getTxnID("WIS", "CylinderInfo"), "RV"),
							rvMsg.ReplySubject);
				}else {
					ToolUtility.MesDaemon.sendMessage(MessageFormat.SendReply("ERROR", "The pallet is not exist.", ToolUtility.getTxnID("WIS", "CylinderInfo"), "RV"),
							rvMsg.ReplySubject);
				}
				
				//>>L WmsCylinderInfoXml USERID=\"WMS\" xml=\"<ZDIS01><HEADER><PALLETID>GS001</PALLETID><STATUS>create</STATUS>
				//<PART_NO_DESC>XXXXXXX</PART_NO_DESC><EXPIRE_DATE>20180101</EXPIRE_DATE><BATCH>9527</BATCH>
				//<TID>WIS_ReplyInfo_123500.734</TID></HEADER></ZDIS01>
			}
		} catch (Exception e) {
			logger.error(ToolUtility.StackTrace2String(e));
		}
	}

}
