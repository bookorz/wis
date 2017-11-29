package com.innolux.common;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.log4j.Logger;

import com.innolux.model.RF_ContainerInfo;
import com.innolux.model.RF_Cylinder_Status;
import com.innolux.model.RF_Tag_History;
import com.innolux.model.WMS_T1_ASN_Pallet;
import com.innolux.model.WMS_T1_Check_Pallet;

public class MessageFormat {
	private static Logger logger = Logger.getLogger(MessageFormat.class);

	public static String SendAms(String fab_id, String eq_id, String alarm_id, String alarm_text, String alarm_comment,
			String readerIP) {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		DateFormat df1 = new SimpleDateFormat("HH:mm:ss.SSS");
		Date today = Calendar.getInstance().getTime();
		String issue_date = df.format(today);
		String tid = eq_id + "_EapReportAlarmSystemInt_" + df1.format(today);

		String RvFormat = ">>L FwEapComplexTxn msgTag=FwEapComplexTxn eqpId=\"" + eq_id
				+ "\" ruleSrvName=\"NT_RULEsrv\" userId=\"WIS\" eapAction={ class=EapReportAlarmSystemInt tId=\"" + tid
				+ "\" trx_id=\"AUTOREPORT\" type_id=\"I\" fab_id=\"" + fab_id + "\" sys_type=\"" + "WIS" + "\" eq_id=\""
				+ eq_id + "\" alarm_id=\"" + alarm_id + "\" alarm_text=\"" + alarm_text + ":" + alarm_comment
				+ "\" pc_ip=\"" + readerIP + "\" pc_name=\"WIS\" operator=\"WIS\" issue_date=\"" + issue_date + "\" }";
		logger.info(readerIP + " " + "send to AMS:" + RvFormat);
		return RvFormat;
	}

	public static String ReplyRfidErrorReset(String fab, String area, String gate, String PalletStr, String Empno,
			String containerID, String readerIP) {
		String Status;
		String Msg;
		if (PalletStr.equals("")) {
			Status = "NG";
			Msg = "該碼頭無異常棧板";
		} else {
			Status = "Success";
			Msg = "ResetErrorComplete";
		}

		String RvFormat = ">>L WmsRfidErrorReset USERID=\"DIS\" xml=\"<ZDIS01><HEADER><PALLET_ID>" + PalletStr
				+ "</PALLET_ID><FAB>" + fab + "</FAB><AREA>" + area + "</AREA><GATEID>" + gate
				+ "</GATEID><CONTAINERID>" + containerID + "</CONTAINERID><VEHICLE_NO>" + containerID
				+ "</VEHICLE_NO><STATUS>" + Status + "</STATUS><EMPNO>" + Empno + "</EMPNO><MSG>" + Msg
				+ "</MSG></HEADER></ZDIS01>\"";
		logger.info(readerIP + " " + "send to WMS:" + RvFormat);
		return RvFormat;
	}

	public static String SendCylinderStatus(RF_Cylinder_Status cylinder, String readerIP) {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String reportDate = df.format(cylinder.getUpdateTime());

		String RvFormat = ">>L WmsCylinderStatusInfoXml USERID=\"DIS\" xml=\"<ZDIS01><HEADER><PALLET_ID>"
				+ cylinder.getTag_ID() + "</PALLET_ID><TYPE>" + cylinder.getCylinder_Type() + "</TYPE><STATUS>"
				+ cylinder.getStatus() + "</STATUS><UPDATE_TIME>" + reportDate
				+ "</UPDATE_TIME><ERR_MSG></ERR_MSG></HEADER></ZDIS01>\"";
		logger.info(readerIP + " " + "send to WMS:" + RvFormat);
		return RvFormat;
	}

	public static String SendASNUnload(WMS_T1_ASN_Pallet pallet, RF_ContainerInfo container, String Action,
			String readerIP) {
		String RvFormat = ">>L WmsASNRfidPalletInfoXml USERID=\"DIS\" xml=\"<ZDIS01><HEADER><ASN_NO>"
				+ pallet.getASN_NO() + "</ASN_NO><PALLET_ID>" + pallet.getPallet_ID() + "</PALLET_ID><ACTION>" + Action
				+ "</ACTION><PLANT>" + container.getFab() + "</PLANT></HEADER></ZDIS01>\"";
		logger.info(readerIP + " " + "send to WMS:" + RvFormat);
		return RvFormat;
	}

	public static String SendEmptyWrapUnload(RF_Tag_History tag, RF_ContainerInfo container, String readerIP) {
		String RvFormat = ">>L WmsRfidEmptyWrapUnloadXml USERID=\"DIS\" xml=\"<ZDIS01><HEADER><PALLET_ID>"
				+ tag.getTag_ID() + "</PALLET_ID><FAB>" + tag.getFab() + "</FAB><AREA>" + tag.getArea()
				+ "</AREA><GATEID>" + tag.getGate() + "</GATEID><CONTAINERID>" + container.getContainer_ID()
				+ "</CONTAINERID><VEHICLE_NO>" + container.getCar_ID()
				+ "</VEHICLE_NO><ACTION></ACTION></HEADER></ZDIS01>\"";
		logger.info(readerIP + " " + "send to WMS:" + RvFormat);
		return RvFormat;
	}

	public static String SendTransfer(RF_Tag_History tag, RF_ContainerInfo container, String Action, String readerIP) {
		String RvFormat = ">>L WmsRfidTransferXml USERID=\"DIS\" xml=\"<ZDIS01><HEADER><PALLET_ID>" + tag.getTag_ID()
				+ "</PALLET_ID><FAB>" + tag.getFab() + "</FAB><AREA>" + tag.getArea() + "</AREA><GATEID>"
				+ tag.getGate() + "</GATEID><CONTAINERID>" + container.getContainer_ID() + "</CONTAINERID><VEHICLE_NO>"
				+ container.getCar_ID() + "</VEHICLE_NO><ACTION>" + Action + "</ACTION></HEADER></ZDIS01>\"";
		logger.info(readerIP + " " + "send to WMS:" + RvFormat);
		return RvFormat;
	}

	public static String SendDeliveryLoad(RF_Tag_History tag, WMS_T1_Check_Pallet wmsPallet, String Action,
			String readerIP) {
		String RvFormat = ">>L WmsRfidPalletInfoXml USERID=\"DIS\" xml=\"<ZDIS01><HEADER><PALLET_ID>" + tag.getTag_ID()
				+ "</PALLET_ID><FAB>" + tag.getFab() + "</FAB><AREA>" + tag.getArea() + "</AREA><GATEID>"
				+ tag.getGate() + "</GATEID><CONTAINERID>" + wmsPallet.getContainer_NO() + "</CONTAINERID><VEHICLE_NO>"
				+ wmsPallet.getTruck_NO() + "</VEHICLE_NO><ACTION>" + Action + "</ACTION></HEADER></ZDIS01>\"";
		logger.info(readerIP + " " + "send to WMS:" + RvFormat);
		return RvFormat;
	}
}
