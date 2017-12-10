package com.innolux.common;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import com.innolux.common.base.IR_MessageBase;
import com.innolux.common.base.ResponseBase;
import com.innolux.dao.GenericDao;
import com.innolux.dao.JdbcGenericDaoImpl;
import com.innolux.model.*;
import com.innolux.service.*;

public class ToolUtility {

	private static Logger logger = Logger.getLogger(ToolUtility.class);

	public static TibcoRvSend MesDaemon = new TibcoRvSend(GlobleVar.TibDaemon, "8585", "");

	private static boolean ReaderIPsInit = false;

	private static Hashtable<String, String> ReaderIPs = new Hashtable<String, String>();

	private static GenericDao<RF_SignalTower_Setting> RF_SignalTower_Setting_Dao = new JdbcGenericDaoImpl<RF_SignalTower_Setting>(
			GlobleVar.WIS_DB);
	private static GenericDao<RF_Subtitle_Setting> RF_Subtitle_Setting_Dao = new JdbcGenericDaoImpl<RF_Subtitle_Setting>(
			GlobleVar.WIS_DB);
	private static GenericDao<RF_Tag_Mapping> RF_Tag_Mapping_Dao = new JdbcGenericDaoImpl<RF_Tag_Mapping>(
			GlobleVar.WIS_DB);
	private static GenericDao<RF_Reader_Setting> RF_Reader_Setting_Dao = new JdbcGenericDaoImpl<RF_Reader_Setting>(
			GlobleVar.WIS_DB);
	private static GenericDao<RF_Gate_Error> RF_Gate_Error_Dao = new JdbcGenericDaoImpl<RF_Gate_Error>(
			GlobleVar.WIS_DB);
	private static GenericDao<RF_Error_Pallet> RF_Error_Pallet_Dao = new JdbcGenericDaoImpl<RF_Error_Pallet>(
			GlobleVar.WIS_DB);
	private static GenericDao<RF_ContainerInfo> RF_ContainerInfo_Dao = new JdbcGenericDaoImpl<RF_ContainerInfo>(
			GlobleVar.WIS_DB);
	private static GenericDao<RF_Move_History> RF_Move_History_Dao = new JdbcGenericDaoImpl<RF_Move_History>(
			GlobleVar.WIS_DB);
	private static GenericDao<RF_Gate_Setting> RF_Gate_Setting_Dao = new JdbcGenericDaoImpl<RF_Gate_Setting>(
			GlobleVar.WIS_DB);
	private static GenericDao<RF_Pallet_Check> RF_Pallet_Check_Dao = new JdbcGenericDaoImpl<RF_Pallet_Check>(
			GlobleVar.WIS_DB);
	private static GenericDao<RF_Tag_History> RF_Tag_History_Dao = new JdbcGenericDaoImpl<RF_Tag_History>(
			GlobleVar.WIS_DB);
	private static GenericDao<RF_Cylinder_Status> RF_Cylinder_Status_Dao = new JdbcGenericDaoImpl<RF_Cylinder_Status>(
			GlobleVar.WIS_DB);
	private static GenericDao<RF_Cylinder_History> RF_Cylinder_History_Dao = new JdbcGenericDaoImpl<RF_Cylinder_History>(
			GlobleVar.WIS_DB);
	private static GenericDao<RF_Antenna_Setting> RF_Antenna_Setting_Dao = new JdbcGenericDaoImpl<RF_Antenna_Setting>(
			GlobleVar.WIS_DB);
	private static GenericDao<RF_Tag_Setting> RF_Tag_Setting_Dao = new JdbcGenericDaoImpl<RF_Tag_Setting>(
			GlobleVar.WIS_DB);
	private static GenericDao<WMS_T1_Ship_Info> WMS_T1_Ship_Info_Dao = new JdbcGenericDaoImpl<WMS_T1_Ship_Info>(
			GlobleVar.T1WMS_DB);
	private static GenericDao<WMS_T2_Ship_Info> WMS_T2_Ship_Info_Dao = new JdbcGenericDaoImpl<WMS_T2_Ship_Info>(
			GlobleVar.T2WMS_DB);
	private static GenericDao<WMS_T1_Opreation_Mode> WMS_T1_Opreation_Mode_Dao = new JdbcGenericDaoImpl<WMS_T1_Opreation_Mode>(
			GlobleVar.T1WMS_DB);
	private static GenericDao<WMS_T2_Opreation_Mode> WMS_T2_Opreation_Mode_Dao = new JdbcGenericDaoImpl<WMS_T2_Opreation_Mode>(
			GlobleVar.T2WMS_DB);
	private static GenericDao<WMS_T1_Check_Pallet> WMS_T1_Check_Pallet_Dao = new JdbcGenericDaoImpl<WMS_T1_Check_Pallet>(
			GlobleVar.T1WMS_DB);
	private static GenericDao<WMS_T2_Check_Pallet> WMS_T2_Check_Pallet_Dao = new JdbcGenericDaoImpl<WMS_T2_Check_Pallet>(
			GlobleVar.T2WMS_DB);

	private static GenericDao<WMS_T1_ASN_Pallet> WMS_T1_ASN_Pallet_Dao = new JdbcGenericDaoImpl<WMS_T1_ASN_Pallet>(
			GlobleVar.T1WMS_DB);

	private static GenericDao<WMS_T2_ASN_Pallet> WMS_T2_ASN_Pallet_Dao = new JdbcGenericDaoImpl<WMS_T2_ASN_Pallet>(
			GlobleVar.T2WMS_DB);

	public static void Initial() {
		long startTime = System.currentTimeMillis();

		boolean init = false;
		List<RF_Antenna_Setting> result = null;
		String key = "";
		try {

			result = RF_Antenna_Setting_Dao.findAllByConditions(null, RF_Antenna_Setting.class);

			for (RF_Antenna_Setting eachAnt : result) {
				key = eachAnt.getFab() + eachAnt.getArea() + eachAnt.getGate();
				if (!ReaderIPs.containsKey(key)) {
					ReaderIPs.put(key, eachAnt.getReader_IP());
				}
			}

			init = true;
		} catch (Exception e) {
			logger.error("AntSetting init fail Exception:" + StackTrace2String(e));
		}
		logger.info("ToolUtility initial process time:" + (System.currentTimeMillis() - startTime));
		ReaderIPsInit = init;
	}

	public static ResponseBase<String> PortBinding(String msg) {
		ResponseBase<String> result = new ResponseBase<String>();
		try {
			result.setStatus("200");
			result.setMessage("Success");
			logger.debug("PortBinding msg:" + msg);

			JSONObject org = new JSONObject(msg);
			String fab = org.getString("Fab");
			String area = org.getString("Area");
			String gate = org.getString("Gate");
			String containerID = org.getString("ContainerID");
			String carType = org.getString("CarType");
			String Status = org.getString("Status");

			switch (Status) {
			case GlobleVar.BindContainer:
				if (!CheckPortBinding(fab, area, gate, "WebService")) {
					// get gateSetting object
					RF_Gate_Setting gateSetting = ToolUtility.GetGateSetting(fab, area, gate, "WebService");
					if (gateSetting != null) {
						gateSetting.setLast_ContainerTag_Time(System.currentTimeMillis());

						RF_Tag_History tag = new RF_Tag_History();
						tag.setAntenna_Type(GlobleVar.ANT_Container);
						tag.setFab(fab);
						tag.setArea(area);
						tag.setGate(gate);
						tag.setTag_ID(containerID);
						tag.setTag_Type(carType);
						tag.setReader_IP("WebService");
						tag.setRSSI("100");
						tag.setTimeStamp(Calendar.getInstance().getTime());
						List<RF_Tag_History> tmp = new ArrayList<RF_Tag_History>();
						tmp.add(tag);
						InsertLog(tmp, "WebService");

						TagHandle.PortBind(gateSetting, tag);
						gateSetting.setManual_Bind(true);
						UpdateGateSetting(gateSetting, "WebService");

						// update gateSetting object
						UpdateGateSetting(gateSetting, "WebService");
					} else {
						logger.debug("WebService" + " PortHandler : Fetch gate setting fail.");
						result.setStatus("500");
						result.setMessage("PortHandler error: Fetch gate setting fail.");
					}
				} else {
					logger.debug("WebService" + " PortHandler : This port was already binded car.");
					result.setStatus("500");
					result.setMessage("PortHandler error: This port was already binded car.");
				}
				break;
			case GlobleVar.UnBindContainer:
				if (CheckPortBinding(fab, area, gate, "WebService")) {
					// get gateSetting object
					RF_Gate_Setting gateSetting = ToolUtility.GetGateSetting(fab, area, gate, "WebService");
					if (gateSetting != null) {
						gateSetting.setLast_MarkTag_Time(System.currentTimeMillis());

						RF_Tag_History tag = new RF_Tag_History();
						tag.setAntenna_Type(GlobleVar.ANT_Container);
						tag.setFab(fab);
						tag.setArea(area);
						tag.setGate(gate);
						tag.setTag_ID(containerID);
						tag.setTag_Type(carType);
						tag.setReader_IP("WebService");

						List<RF_Tag_History> tmp = new ArrayList<RF_Tag_History>();
						tmp.add(tag);
						InsertLog(tmp, "WebService");

						TagHandle.PortUnBind(gateSetting, tag);
						gateSetting.setManual_Bind(false);
						UpdateGateSetting(gateSetting, "WebService");

						// update gateSetting object
						UpdateGateSetting(gateSetting, "WebService");
					} else {
						logger.debug("WebService" + " PortHandler : Fetch gate setting fail.");
						result.setStatus("500");
						result.setMessage("PortHandler error: Fetch gate setting fail.");
					}
				} else {
					logger.debug("WebService" + " PortHandler : This port is not binded car.");
					result.setStatus("500");
					result.setMessage("PortHandler error: This port is not binded car.");
				}
				break;
			}
		} catch (Exception e) {
			logger.error("SetSubtitle Exception:" + StackTrace2String(e));
			result.setStatus("500");
			result.setMessage(StackTrace2String(e));
		}
		return result;
	}

	public static ResponseBase<String> ResetAllError(String msg) {
		ResponseBase<String> result = new ResponseBase<String>();
		try {
			logger.debug("ResetAllError msg:" + msg);
			JSONObject orgJson = new JSONObject(msg);

			String fab = orgJson.getString("Fab");
			String area = orgJson.getString("Area");
			String gate = orgJson.getString("Gate");

			ToolUtility.DeleteGateError(fab, area, gate, "", "RV");
			List<RF_Error_Pallet> errPallets = ToolUtility.GetErrorPalletList(fab, area, gate, "RV");
			for (RF_Error_Pallet each : errPallets) {
				ToolUtility.DeletePallet(each.getPallet_ID(), "RV");
			}
			ToolUtility.ClearErrorPallet(fab, area, gate, "RV");
			RF_ContainerInfo container = ToolUtility.GetContainerInfo(fab, area, gate, "RV");

			ToolUtility.Subtitle(fab, area, gate, ToolUtility.InitSubtitleStr(container, "RV"), "RV");

			ToolUtility.SignalTower(fab, area, gate, GlobleVar.RedOff, "RV");

			result.setStatus("200");
			result.setMessage("Success");

		} catch (Exception e) {
			logger.error("ResetAllError Exception:" + StackTrace2String(e));
			result.setStatus("500");
			result.setMessage(StackTrace2String(e));
		}
		return result;
	}

	public static ResponseBase<String> SetAttenuation(String msg) {
		ResponseBase<String> result = new ResponseBase<String>();
		try {
			logger.debug("SetAttenuation msg:" + msg);
			JSONObject orgJson = new JSONObject(msg);

			String ReaderIP = orgJson.getString("ReaderIP");
			// String AntNumber = orgJson.getString("AntNumber");
			// String Value = orgJson.getString("Value");

			ReaderCmdService.SetAttenuation(ReaderIP);

			result.setStatus("200");
			result.setMessage("Success");
		} catch (Exception e) {
			logger.error("SetAttenuation Exception:" + StackTrace2String(e));
			result.setStatus("500");
			result.setMessage(StackTrace2String(e));
		}
		return result;
	}

	public static ResponseBase<String> SetSignalTower(String msg) {
		ResponseBase<String> result = new ResponseBase<String>();
		try {
			logger.debug("SetSignalTower msg:" + msg);
			JSONObject orgJson = new JSONObject(msg);

			String fab = orgJson.getString("Fab");
			String area = orgJson.getString("Area");
			String gate = orgJson.getString("Gate");
			String cmd = orgJson.getString("Cmd");

			SignalTower(fab, area, gate, cmd, "WebService");

			result.setStatus("200");
			result.setMessage("Success");
		} catch (Exception e) {
			logger.error("SetSignalTower Exception:" + StackTrace2String(e));
			result.setStatus("500");
			result.setMessage(StackTrace2String(e));
		}
		return result;
	}

	public static ResponseBase<String> SetSubtitle(String msg) {
		ResponseBase<String> result = new ResponseBase<String>();
		try {
			logger.debug("SetSubtitle msg:" + msg);
			JSONObject orgJson = new JSONObject(msg);
			JSONArray msgAry = new JSONArray(orgJson.getString("custSubtitleList"));

			for (int i = 0; i < msgAry.length(); i++) {
				JSONObject each = msgAry.getJSONObject(i);

				String fab = each.getString("Fab");
				String area = each.getString("Area");
				String gate = each.getString("Gate");
				String subtitleStr = each.getString("CustSubtitleStr");
				boolean Active = false;
				if (each.getString("Active").equals("T")) {
					Active = true;
				}
				RF_Subtitle_Setting sub = GetSubtitle(fab, area, gate, "WebService");

				sub.setCust_Subtitle(subtitleStr);
				sub.setCust_Active(Active);
				UpdateSubtitle(sub, "WebService");

				Subtitle(fab, area, gate, subtitleStr, "WebService");

			}
			result.setStatus("200");
			result.setMessage("Success");
		} catch (Exception e) {
			logger.error("SetSubtitle Exception:" + StackTrace2String(e));
			result.setStatus("500");
			result.setMessage(StackTrace2String(e));
		}
		return result;
	}

	public static String GetNowTimeStr() {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date today = Calendar.getInstance().getTime();
		return df.format(today);
	}

	public static String GetReaderIP(String fab, String area, String gate) {
		String result = "";
		String key = fab + area + gate;
		try {
			if (ReaderIPsInit) {
				if (ReaderIPs.containsKey(key)) {
					result = ReaderIPs.get(key);
				}
			}
		} catch (Exception e) {
			logger.error("GetReaderIP Exception:" + StackTrace2String(e));
			result = null;
		}
		return result;
	}

	public static String StackTrace2String(Exception e) {

		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		e.printStackTrace(pw);
		return sw.toString(); // stack trace as a string
	}

	public static String AddSpace(String input) {
		String output = " ";
		for (int i = 0; i < input.length(); i++) {
			output += input.substring(i, i + 1) + " ";
		}
		return output;

	}

	public static IR_MessageBase Parse_T1_IR(String msg) {
		IR_MessageBase result = new IR_MessageBase();
		logger.info("Parse_T1_IR " + msg);
		try {
			String startStr = "<commandXML>";
			String endStr = "</commandXML>";
			int StartIdx = msg.indexOf(startStr) + startStr.length();
			int EndIdx = msg.indexOf(endStr);

			msg.substring(StartIdx, EndIdx);
			String[] commandAry = msg.split(";");
			String[] infoAry = commandAry[2].split(",");
			result.setFab(infoAry[1]);
			result.setArea(infoAry[2]);
			result.setGate(infoAry[3]);
			result.setType(GlobleVar.RawDataReport);
			result.setSensorNo("1");
			// IR 1=connect 0=break
			if (infoAry[4].equals("1")) {
				result.setStatus(GlobleVar.On);
			} else {
				result.setStatus(GlobleVar.Off);
			}
			result.setTimeStamp(System.currentTimeMillis());

			logger.debug("Parse_T1_IR " + result.toString());

		} catch (Exception e) {
			logger.error("Parse_T1_IR Exception:" + StackTrace2String(e));
			result = null;
		}
		return result;
	}

	public static IR_MessageBase Parse_T2_IR(String msg) {
		IR_MessageBase result = new IR_MessageBase();
		logger.info("Parse_T2_IR " + msg);
		try {

			JSONObject org = new JSONObject(msg);
			result.setFab(org.getString("Fab"));
			result.setArea(org.getString("Area"));
			result.setGate(org.getString("Gate"));

			result.setType(org.getString("Type"));
			try {
				result.setDirection(org.getString("Direction"));
			} catch (Exception e11) {
				result.setDirection("NONE");
			}
			try {
				result.setSensorNo(org.getString("SensorNo"));
				result.setStatus(org.getString("Status"));
			} catch (Exception e12) {
				result.setSensorNo("");
				result.setStatus("");
			}

			result.setTimeStamp(System.currentTimeMillis());

			logger.debug("Parse_T2_IR " + result.toString());

		} catch (Exception e) {
			logger.error("Parse_T2_IR Exception:" + StackTrace2String(e));
			result = null;
		}
		return result;
	}

	public static String getTxnID(String eqpID, String functionID) {
		StringBuffer sb = new StringBuffer();
		sb.append((int) (Math.random() * 100)).append("_").append(eqpID).append("_").append(functionID).append("_");
		sb.append(new SimpleDateFormat("HH:mm:ss.SSS").format(Calendar.getInstance().getTime()));
		return sb.toString();
	}

	public static boolean SetAntActive(String fab, String area, String gate, String antType, boolean active,
			String readerIP) {
		boolean result = false;
		try {
			List<RF_Antenna_Setting> antList = null;
			Map<String, Object> sqlWhereMap = new HashMap<String, Object>();
			sqlWhereMap.put("fab", fab);
			sqlWhereMap.put("area", area);
			sqlWhereMap.put("gate", gate);
			sqlWhereMap.put("antenna_type", antType);

			antList = RF_Antenna_Setting_Dao.findAllByConditions(sqlWhereMap, RF_Antenna_Setting.class);
			if (antList.size() != 0) {
				for (RF_Antenna_Setting eachAnt : antList) {
					eachAnt.setActive(active);
					ToolUtility.UpdateAntSetting(eachAnt, readerIP);
				}

				ReaderCmdService.SetAntennaSequence(antList.get(0).getReader_IP());
			} else {
				logger.error(readerIP + " Antenna is not in config.");
			}
		} catch (Exception e) {
			logger.error(readerIP + " " + "Exception:" + StackTrace2String(e));
		}
		return result;
	}

	public static RF_Antenna_Setting UpdateAntSetting(RF_Antenna_Setting ant, String readerIP) {
		RF_Antenna_Setting result = null;
		try {

			RF_Antenna_Setting_Dao.update(ant);
		} catch (Exception e) {
			logger.error(readerIP + " " + "Exception:" + StackTrace2String(e));
		}
		return result;
	}

	public static RF_Antenna_Setting GetAntSetting(String fab, String area, String gate, String antenna_Type,
			String readerIP) {
		RF_Antenna_Setting result = null;
		try {
			Map<String, Object> sqlWhereMap = new HashMap<String, Object>();
			sqlWhereMap.put("fab", fab);
			sqlWhereMap.put("area", area);
			sqlWhereMap.put("gate", gate);
			sqlWhereMap.put("antenna_type", antenna_Type);
			List<RF_Antenna_Setting> resultList = RF_Antenna_Setting_Dao.findAllByConditions(sqlWhereMap,
					RF_Antenna_Setting.class);
			if (resultList.size() != 0) {
				result = resultList.get(0);
			} else {
				// Share gate use
				RF_Gate_Setting gateInfo = ToolUtility.GetGateSetting(fab, area, gate, readerIP);
				if (!gateInfo.getShare_Gate().equals("0")) {
					sqlWhereMap.clear();
					sqlWhereMap.put("fab", fab);
					sqlWhereMap.put("area", area);
					sqlWhereMap.put("gate", gateInfo.getShare_Gate());
					sqlWhereMap.put("antenna_type", antenna_Type);

					resultList = RF_Antenna_Setting_Dao.findAllByConditions(sqlWhereMap, RF_Antenna_Setting.class);

					if (resultList.size() != 0) {
						result = resultList.get(0);
					}

				}
			}

		} catch (Exception e) {
			logger.error(readerIP + " " + "Exception:" + StackTrace2String(e));
		}
		return result;
	}

	public static String GetAntennaSequence(String readerIP) {
		String AntennaSequence = "1 3 1 0 3 1 3 2";
		for (RF_Antenna_Setting eachAnt : GetAntSettingList(readerIP)) {

			if (eachAnt.getAntenna_No() == 0) {
				if (!eachAnt.getActive()) {
					AntennaSequence = AntennaSequence.replace("0", "");
				}
			}

			if (eachAnt.getAntenna_No() == 1) {
				if (!eachAnt.getActive()) {
					AntennaSequence = AntennaSequence.replace("1", "");
				}
			}
			if (eachAnt.getAntenna_No() == 2) {
				if (!eachAnt.getActive()) {
					AntennaSequence = AntennaSequence.replace("2", "");
				}
			}
			if (eachAnt.getAntenna_No() == 3) {
				if (!eachAnt.getActive()) {
					AntennaSequence = AntennaSequence.replace("3", "");
				}
			}
		}
		AntennaSequence = AntennaSequence.replace("  ", " ").trim();
		logger.debug(readerIP + " GetAntennaSequence " + AntennaSequence);
		return AntennaSequence;
	}

	public static List<RF_Antenna_Setting> GetAntSettingList(String readerIP) {
		List<RF_Antenna_Setting> result = null;
		try {
			Map<String, Object> sqlWhereMap = new HashMap<String, Object>();
			sqlWhereMap.put("reader_ip", readerIP);

			result = RF_Antenna_Setting_Dao.findAllByConditions(sqlWhereMap, RF_Antenna_Setting.class);
		} catch (Exception e) {
			logger.error(readerIP + " " + "Exception:" + StackTrace2String(e));
		}
		return result;
	}

	public static List<RF_Tag_Setting> GetTagSettingList(String readerIP) {
		List<RF_Tag_Setting> result = null;
		try {

			result = RF_Tag_Setting_Dao.findAllByConditions(null, RF_Tag_Setting.class);
		} catch (Exception e) {
			logger.error(readerIP + " " + "Exception:" + StackTrace2String(e));
		}
		return result;
	}

	public static void SetCylinderHistory(RF_Cylinder_Status cylinder, String readerIP) {
		RF_Cylinder_History history = new RF_Cylinder_History();
		try {
			history.setFab(cylinder.getFab() == null ? "" : cylinder.getFab());
			history.setArea(cylinder.getArea() == null ? "" : cylinder.getArea());
			history.setTag_ID(cylinder.getTag_ID() == null ? "" : cylinder.getTag_ID());
			history.setPosition(cylinder.getPosition() == null ? "" : cylinder.getPosition());
			history.setNew_Position(cylinder.getNew_Position() == null ? "" : cylinder.getNew_Position());
			history.setStatus(cylinder.getStatus() == null ? "" : cylinder.getStatus());
			history.setCheck_Times(cylinder.getCheck_Times());
			history.setCylinder_Type(cylinder.getCylinder_Type() == null ? "" : cylinder.getCylinder_Type());
			history.setUpdateTime(System.currentTimeMillis());

			RF_Cylinder_History_Dao.save(history);

		} catch (Exception e) {
			logger.error(readerIP + " " + "Exception:" + StackTrace2String(e));
		}

	}

	public static List<RF_Cylinder_Status> GetAllCylinders(String readerIP) {
		List<RF_Cylinder_Status> result = null;
		try {

			result = RF_Cylinder_Status_Dao.findAllByConditions(null, RF_Cylinder_Status.class);
		} catch (Exception e) {
			logger.error(readerIP + " " + "Exception:" + StackTrace2String(e));
		}
		return result;
	}

	public static RF_Cylinder_Status GetCylinder(String tagID, String readerIP) {
		RF_Cylinder_Status result = null;
		try {
			result = RF_Cylinder_Status_Dao.get(tagID, RF_Cylinder_Status.class);
		} catch (Exception e) {
			logger.error(readerIP + " " + "Exception:" + StackTrace2String(e));
		}
		return result;
	}

	public static void SetCylinder(RF_Cylinder_Status cylinder, String readerIP) {

		try {
			if (GetCylinder(cylinder.getTag_ID(), readerIP) != null) {
				RF_Cylinder_Status_Dao.update(cylinder);
			} else {
				if (cylinder.getPosition().equals(GlobleVar.ANT_Big_Stock)
						|| cylinder.getPosition().equals(GlobleVar.ANT_Big_Use)) {
					cylinder.setCylinder_Type("big");
				} else {
					cylinder.setCylinder_Type("small");
				}
				RF_Cylinder_Status_Dao.save(cylinder);
			}
		} catch (Exception e) {
			logger.error(readerIP + " " + "Exception:" + StackTrace2String(e));
		}

	}

	public static void DeleteCylinder(RF_Cylinder_Status cylinder, String readerIP) {

		try {

			RF_Cylinder_Status_Dao.delete(cylinder.getTag_ID(), RF_Cylinder_Status.class);

		} catch (Exception e) {
			logger.error(readerIP + " " + "Exception:" + StackTrace2String(e));
		}

	}

	public static WMS_T1_ASN_Pallet GetASNPallet(RF_Tag_History tag) {
		WMS_T1_ASN_Pallet result = null;

		try {

			switch (tag.getFab()) {
			case "T1":
				result = WMS_T1_ASN_Pallet_Dao.get(tag.getTag_ID(), WMS_T1_ASN_Pallet.class);
				break;
			case "T2":

				WMS_T2_ASN_Pallet t2 = WMS_T2_ASN_Pallet_Dao.get(tag.getTag_ID(), WMS_T2_ASN_Pallet.class);
				if (t2 != null) {
					result = new WMS_T1_ASN_Pallet();
					result.setASN_NO(t2.getASN_NO());
					result.setCar_NO(t2.getCar_NO());
					result.setPallet_ID(t2.getPallet_ID());
					result.setRFID_Chk(t2.getRFID_Chk());
				}
				break;
			}
		} catch (Exception e) {
			logger.error(tag.getReader_IP() + " " + "Exception:" + StackTrace2String(e));
		}

		return result;
	}

	public static String GetOperation_Mode(String fab, String area, String gate, String readerIP) {
		String result = "";
		// return GlobleVar.DeliveryLoad;
		try {
			Map<String, Object> sqlWhereMap = new HashMap<String, Object>();

			sqlWhereMap.put("wh", fab);
			sqlWhereMap.put("area", area);
			sqlWhereMap.put("gate_id", gate);

			List<WMS_T1_Opreation_Mode> resultList = WMS_T1_Opreation_Mode_Dao.findAllByConditions(sqlWhereMap,
					WMS_T1_Opreation_Mode.class);
			if (resultList.size() != 0) {
				result = resultList.get(0).getOperation_Type();
			} else {
				List<WMS_T2_Opreation_Mode> resultList2 = WMS_T2_Opreation_Mode_Dao.findAllByConditions(sqlWhereMap,
						WMS_T2_Opreation_Mode.class);
				if (resultList2.size() != 0) {
					result = resultList2.get(0).getOperation_Type();
				}
			}

		} catch (Exception e) {
			logger.error(readerIP + " " + "Exception:" + StackTrace2String(e));
		}
		return result;
	}

	public static void InsertLog(List<RF_Tag_History> tagList, String readerIP) {
		try {
			for (RF_Tag_History tag : tagList) {
				RF_Tag_History_Dao.save(tag);
			}
		} catch (Exception e) {
			logger.error(readerIP + " Exception:" + StackTrace2String(e));
		}
	}

	public static List<RF_Tag_History> GetTagHistory(String fab, String area, String gate, String antenna_Type,
			long startTime, String readerIP) {
		List<RF_Tag_History> result = null;
		try {
			Map<String, Object> sqlWhereMap = new HashMap<String, Object>();
			sqlWhereMap.put("fab", fab);
			sqlWhereMap.put("area", area);
			sqlWhereMap.put("gate", gate);
			sqlWhereMap.put("tag_type", "P");
			sqlWhereMap.put("antenna_type", antenna_Type);
			sqlWhereMap.put("receive_time,>=", startTime);

			result = RF_Tag_History_Dao.findAllByConditions(sqlWhereMap, RF_Tag_History.class);
		} catch (Exception e) {
			logger.error(readerIP + " Exception:" + StackTrace2String(e));
		}
		return result;
	}

	public static void SignalTowerAutoOff(String fab, String area, String gate, String cmd, long delay,
			String readerIP) {

		try {
			String cmdStr = "";

			Map<String, Object> sqlWhereMap = new HashMap<String, Object>();
			sqlWhereMap.put("fab", fab);
			sqlWhereMap.put("area", area);
			sqlWhereMap.put("gate", gate);

			RF_SignalTower_Setting signalToerSet = RF_SignalTower_Setting_Dao
					.findAllByConditions(sqlWhereMap, RF_SignalTower_Setting.class).get(0);
			if (signalToerSet != null) {
				switch (cmd) {
				case GlobleVar.RedOn:
					cmdStr = signalToerSet.getRed_On_Cmd();
					signalToerSet.setRed_State(true);
					// cmdStr2 = signalToerSet.getRed_Off_Cmd();
					// cmd2 = GlobleVar.RedOff;
					break;
				case GlobleVar.OrangeOn:
					cmdStr = signalToerSet.getOrange_On_Cmd();
					signalToerSet.setOrange_State(true);
					// cmdStr2 = signalToerSet.getOrange_Off_Cmd();
					// cmd2 = GlobleVar.OrangeOff;
					break;
				case GlobleVar.GreenOn:
					cmdStr = signalToerSet.getGreen_On_Cmd();
					signalToerSet.setGreen_State(true);
					// cmdStr2 = signalToerSet.getGreen_Off_Cmd();
					// cmd2 = GlobleVar.GreenOff;
					break;
				}

				if (ReaderCmdService.SendCmd(signalToerSet.getReader_IP(), cmdStr)) {

					RF_SignalTower_Setting_Dao.update(signalToerSet);

					Thread t = new Thread(new Runnable() {

						@Override
						public void run() {

							String cmdStr2 = "";
							try {

								switch (cmd) {
								case GlobleVar.RedOn:
									// cmdStr = signalToerSet.getRed_On_Cmd();
									cmdStr2 = signalToerSet.getRed_Off_Cmd();

									signalToerSet.setRed_State(false);
									break;
								case GlobleVar.OrangeOn:
									// cmdStr = signalToerSet.getOrange_On_Cmd();
									cmdStr2 = signalToerSet.getOrange_Off_Cmd();
									signalToerSet.setOrange_State(false);
									break;
								case GlobleVar.GreenOn:
									// cmdStr = signalToerSet.getGreen_On_Cmd();
									cmdStr2 = signalToerSet.getGreen_Off_Cmd();
									signalToerSet.setGreen_State(false);
									break;
								}
								Thread.sleep(delay);
								if (ReaderCmdService.SendCmd(signalToerSet.getReader_IP(), cmdStr2)) {

									RF_SignalTower_Setting_Dao.update(signalToerSet);
								}

							} catch (Exception e) {

								logger.error(readerIP + " Exception:" + StackTrace2String(e));

							}

						}
					});
					t.setDaemon(false);
					t.start();
				} else {
					logger.error(readerIP + " SignalTower send fail.");
				}
			} else {
				logger.error(readerIP + " SignalTower_Setting is not exist");
			}

		} catch (Exception e) {

			logger.error(readerIP + " " + "Exception:" + StackTrace2String(e));
		}

	}

	public static void SignalTower(String fab, String area, String gate, String cmd, String readerIP) {
		long StartTime = System.currentTimeMillis();
		try {
			String cmdStr = "";

			Map<String, Object> sqlWhereMap = new HashMap<String, Object>();
			sqlWhereMap.put("fab", fab);
			sqlWhereMap.put("area", area);
			sqlWhereMap.put("gate", gate);

			RF_SignalTower_Setting signalToerSet = RF_SignalTower_Setting_Dao
					.findAllByConditions(sqlWhereMap, RF_SignalTower_Setting.class).get(0);
			if (signalToerSet != null) {
				switch (cmd) {
				case GlobleVar.RedOn:
					cmdStr = signalToerSet.getRed_On_Cmd();
					signalToerSet.setRed_State(true);
					break;
				case GlobleVar.RedOff:
					cmdStr = signalToerSet.getRed_Off_Cmd();
					signalToerSet.setRed_State(false);
					break;
				case GlobleVar.OrangeOn:
					cmdStr = signalToerSet.getOrange_On_Cmd();
					signalToerSet.setOrange_State(true);
					break;
				case GlobleVar.OrangeOff:
					cmdStr = signalToerSet.getOrange_Off_Cmd();
					signalToerSet.setOrange_State(false);
					break;
				case GlobleVar.GreenOn:
					cmdStr = signalToerSet.getGreen_On_Cmd();
					signalToerSet.setGreen_State(true);
					break;
				case GlobleVar.GreenOff:
					cmdStr = signalToerSet.getGreen_Off_Cmd();
					signalToerSet.setGreen_State(false);
					break;

				}
				if (ReaderCmdService.SendCmd(signalToerSet.getReader_IP(), cmdStr)) {

					RF_SignalTower_Setting_Dao.update(signalToerSet);
				} else {
					logger.error(readerIP + " SignalTower send fail.");
				}
			} else {
				logger.error(readerIP + " SignalTower_Setting is not exist");
			}

		} catch (Exception e) {

			logger.error(readerIP + " " + "Exception:" + StackTrace2String(e));
		}
		logger.debug(readerIP + " SignalTower processTime:" + (System.currentTimeMillis() - StartTime));
	}

	public static List<RF_Reader_Setting> GetAllReader() {
		List<RF_Reader_Setting> result = null;
		try {

			result = RF_Reader_Setting_Dao.findAllByConditions(null, RF_Reader_Setting.class);

		} catch (Exception e) {

			logger.error("Exception:" + StackTrace2String(e));
		}
		return result;
	}

	public static List<RF_Subtitle_Setting> GetAllSubtitle() {
		List<RF_Subtitle_Setting> result = null;
		try {

			result = RF_Subtitle_Setting_Dao.findAllByConditions(null, RF_Subtitle_Setting.class);

		} catch (Exception e) {

			logger.error("Exception:" + StackTrace2String(e));
		}
		return result;
	}

	public static RF_Subtitle_Setting GetSubtitle(String fab, String area, String gate, String readerIP) {
		RF_Subtitle_Setting result = null;
		try {
			Map<String, Object> sqlWhereMap = new HashMap<String, Object>();
			sqlWhereMap.put("fab", fab);
			sqlWhereMap.put("area", area);
			sqlWhereMap.put("gate", gate);

			List<RF_Subtitle_Setting> tmp = RF_Subtitle_Setting_Dao.findAllByConditions(sqlWhereMap,
					RF_Subtitle_Setting.class);

			if (tmp.size() != 0) {
				result = tmp.get(0);
			}

		} catch (Exception e) {

			logger.error(readerIP + " Exception:" + StackTrace2String(e));
		}
		return result;
	}

	public static void UpdateSubtitle(RF_Subtitle_Setting sub, String readerIP) {

		try {

			RF_Subtitle_Setting_Dao.update(sub);

		} catch (Exception e) {

			logger.error(readerIP + " Exception:" + StackTrace2String(e));
		}

	}

	public static void Subtitle(String fab, String area, String gate, String showStr, String readerIP) {

		Thread t = new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					Map<String, Object> sqlWhereMap = new HashMap<String, Object>();
					sqlWhereMap.put("fab", fab);
					sqlWhereMap.put("area", area);
					sqlWhereMap.put("gate", gate);
					List<RF_Subtitle_Setting> SubtitleSettings = RF_Subtitle_Setting_Dao
							.findAllByConditions(sqlWhereMap, RF_Subtitle_Setting.class);

					if (SubtitleSettings.size() != 0) {
						RF_Subtitle_Setting SubtitleSetting = SubtitleSettings.get(0);
						if (SubtitleService.Show(SubtitleSetting.getSubtitle_IP(), showStr)) {
							logger.info(readerIP + " " + showStr);
							SubtitleSetting.setCurrent_Subtitle(showStr);
							SubtitleSetting.setUpdate_Time(System.currentTimeMillis());
							RF_Subtitle_Setting_Dao.update(SubtitleSetting);
						} else {
							logger.error(readerIP + " SignalTower send fail.");
						}
					} else {
						logger.error(readerIP + " SubtitleSetting is not exist");
					}
				} catch (Exception e) {

					logger.error(readerIP + " " + "Exception:" + StackTrace2String(e));
				}
			}
		});
		t.setDaemon(false);
		t.start();

	}

	public static void VoiceSend(String path, String voiceStr, String readerIP) {
		try {

			if (!VoiceService.Send(path, voiceStr)) {

				logger.error(readerIP + " Voice send fail.");
			}

		} catch (Exception e) {

			logger.error(readerIP + " " + "Exception:" + StackTrace2String(e));
		}

	}

	public static RF_Tag_Mapping GetTagMapping(RF_Tag_History tag) {
		RF_Tag_Mapping result = null;
		try {
			// For test
			// result = new RF_Tag_Mapping();
			// result.setTag_id(tag.getTag_ID());
			// result.setReal_id(tag.getTag_ID());
			result = RF_Tag_Mapping_Dao.get(tag.getTag_ID(), RF_Tag_Mapping.class);
		} catch (Exception e) {

			logger.error(tag.getReader_IP() + " " + "Exception:" + StackTrace2String(e));
		}
		return result;
	}

	public static void SetErrorPallet(RF_Tag_History tag, String opreation, String reason) {

		try {
			RF_Error_Pallet errorPallet = new RF_Error_Pallet();
			errorPallet.setFab(tag.getFab());
			errorPallet.setArea(tag.getArea());
			errorPallet.setGate(tag.getGate());
			errorPallet.setOpreation_Mode(opreation);
			errorPallet.setPallet_ID(tag.getTag_ID());
			errorPallet.setReason(reason);
			errorPallet.setTimeStamp(Calendar.getInstance().getTime());

			RF_Error_Pallet_Dao.save(errorPallet);

		} catch (Exception e) {

			logger.error(tag.getReader_IP() + " " + "Exception:" + StackTrace2String(e));
		}

	}

	public static List<RF_Error_Pallet> GetErrorPalletList(String fab, String area, String gate, String readerIP) {
		List<RF_Error_Pallet> result = null;
		try {
			Map<String, Object> sqlWhereMap = new HashMap<String, Object>();

			sqlWhereMap.put("fab", fab);
			sqlWhereMap.put("area", area);
			sqlWhereMap.put("gate", gate);

			result = RF_Error_Pallet_Dao.findAllByConditions(sqlWhereMap, RF_Error_Pallet.class);
		} catch (Exception e) {

			logger.error(readerIP + " " + "Exception:" + StackTrace2String(e));
		}
		return result;
	}

	public static List<RF_Error_Pallet> GetErrorPalletList(RF_Tag_History tag) {
		List<RF_Error_Pallet> result = null;
		try {
			Map<String, Object> sqlWhereMap = new HashMap<String, Object>();

			sqlWhereMap.put("pallet_id", tag.getTag_ID());

			result = RF_Error_Pallet_Dao.findAllByConditions(sqlWhereMap, RF_Error_Pallet.class);
		} catch (Exception e) {

			logger.error(tag.getReader_IP() + " " + "Exception:" + StackTrace2String(e));
		}
		return result;
	}

	public static RF_Error_Pallet GetErrorPallet(RF_Tag_History tag, String opreation, String reason) {
		RF_Error_Pallet result = null;
		try {

			Map<String, Object> sqlWhereMap = new HashMap<String, Object>();

			sqlWhereMap.put("pallet_id", tag.getTag_ID());
			sqlWhereMap.put("fab", tag.getFab());
			sqlWhereMap.put("area", tag.getArea());
			sqlWhereMap.put("gate", tag.getGate());
			if (opreation != "") {
				sqlWhereMap.put("opreation_mode", opreation);
			}
			if (reason != "") {
				sqlWhereMap.put("reason", reason);
			}
			List<RF_Error_Pallet> errPList = RF_Error_Pallet_Dao.findAllByConditions(sqlWhereMap,
					RF_Error_Pallet.class);
			if (errPList.size() != 0) {
				result = errPList.get(0);
			}
		} catch (Exception e) {

			logger.error(tag.getReader_IP() + " " + "Exception:" + StackTrace2String(e));
		}
		return result;
	}

	public static String GetErrorPalletSummary(String fab, String area, String gate, String opreation,
			String readerIP) {
		String result = "";

		try {

			Map<String, Object> sqlWhereMap = new HashMap<String, Object>();

			sqlWhereMap.put("fab", fab);
			sqlWhereMap.put("area", area);
			sqlWhereMap.put("gate", gate);
			sqlWhereMap.put("opreation_mode", opreation);

			List<RF_Error_Pallet> errorPalletList = RF_Error_Pallet_Dao.findAllByConditions(sqlWhereMap,
					RF_Error_Pallet.class);
			if (errorPalletList.size() != 0) {
				result += "異常棧板:";
			}

			for (RF_Error_Pallet each : errorPalletList) {
				result += " " + each.getPallet_ID();

			}

		} catch (Exception e) {

			logger.error(readerIP + " " + "Exception:" + StackTrace2String(e));
		}
		return result;
	}

	public static void DeleteErrorPallet(RF_Error_Pallet errPallet, String readerIP) {

		try {

			RF_Error_Pallet_Dao.delete(errPallet.getID(), RF_Error_Pallet.class);

		} catch (Exception e) {

			logger.error(readerIP + " " + "Exception:" + StackTrace2String(e));
		}

	}

	public static void ClearErrorPallet(String fab, String area, String gate, String readerIP) {

		try {

			Map<String, Object> sqlWhereMap = new HashMap<String, Object>();

			sqlWhereMap.put("fab", fab);
			sqlWhereMap.put("area", area);
			sqlWhereMap.put("gate", gate);

			RF_Error_Pallet_Dao.deleteAllByConditions(sqlWhereMap, RF_Error_Pallet.class);

		} catch (Exception e) {

			logger.error(readerIP + " " + "Exception:" + StackTrace2String(e));
		}

	}

	public static List<RF_Gate_Error> GetAllGateError(String readerIP) {
		List<RF_Gate_Error> result = null;
		try {

			result = RF_Gate_Error_Dao.findAllByConditions(null, RF_Gate_Error.class);

		} catch (Exception e) {

			logger.error(readerIP + " " + "Exception:" + StackTrace2String(e));
		}
		return result;
	}

	public static RF_Gate_Error GetGateError(String fab, String area, String gate, String errorType, String readerIP) {
		RF_Gate_Error result = null;
		try {

			Map<String, Object> sqlWhereMap = new HashMap<String, Object>();

			sqlWhereMap.put("fab", fab);
			sqlWhereMap.put("area", area);
			sqlWhereMap.put("gate", gate);
			if (!errorType.equals("")) {
				sqlWhereMap.put("error_type", errorType);
			}
			List<RF_Gate_Error> resultList = RF_Gate_Error_Dao.findAllByConditions(sqlWhereMap, RF_Gate_Error.class);
			if (resultList.size() != 0) {
				result = RF_Gate_Error_Dao.findAllByConditions(sqlWhereMap, RF_Gate_Error.class).get(0);
			}
		} catch (Exception e) {

			logger.error(readerIP + " " + "Exception:" + StackTrace2String(e));
		}
		return result;
	}

	public static void DeleteGateError(String fab, String area, String gate, String errorType, String readerIP) {

		try {

			Map<String, Object> sqlWhereMap = new HashMap<String, Object>();

			sqlWhereMap.put("fab", fab);
			sqlWhereMap.put("area", area);
			sqlWhereMap.put("gate", gate);
			if (!errorType.equals("")) {
				sqlWhereMap.put("error_type", errorType);
			}
			RF_Gate_Error_Dao.deleteAllByConditions(sqlWhereMap, RF_Gate_Error.class);

		} catch (Exception e) {

			logger.error(readerIP + " " + "Exception:" + StackTrace2String(e));
		}

	}

	public static void SetGateError(RF_Tag_History tag, String errType, String errStr) {

		try {
			Map<String, Object> sqlWhereMap = new HashMap<String, Object>();

			sqlWhereMap.put("fab", tag.getFab());
			sqlWhereMap.put("area", tag.getArea());
			sqlWhereMap.put("gate", tag.getGate());
			sqlWhereMap.put("error_type", errType);

			List<RF_Gate_Error> existErrors = RF_Gate_Error_Dao.findAllByConditions(sqlWhereMap, RF_Gate_Error.class);

			if (existErrors.size() != 0) {

				RF_Gate_Error error = existErrors.get(0);
				error.setError_Message(errStr);

				RF_Gate_Error_Dao.update(error);
			} else {

				RF_Gate_Error t = new RF_Gate_Error();

				t.setFab(tag.getFab());
				t.setArea(tag.getArea());
				t.setGate(tag.getGate());
				t.setError_Type(errType);
				t.setError_Message(errStr);
				t.setTimeStamp(System.currentTimeMillis());

				RF_Gate_Error_Dao.save(t);
			}
		} catch (Exception e) {

			logger.error(tag.getReader_IP() + " " + "Exception:" + StackTrace2String(e));
		}

	}

	public static boolean CheckPortBinding(String fab, String area, String gate, String readerIP) {
		boolean result = false;
		try {

			Map<String, Object> sqlWhereMap = new HashMap<String, Object>();

			sqlWhereMap.put("fab", fab);
			sqlWhereMap.put("area", area);
			sqlWhereMap.put("gate", gate);

			if (RF_ContainerInfo_Dao.findAllByConditions(sqlWhereMap, RF_ContainerInfo.class).size() != 0) {
				result = true;
			}

		} catch (Exception e) {

			logger.error(readerIP + " " + "Exception:" + StackTrace2String(e));
		}
		return result;
	}

	public static RF_ContainerInfo GetContainerInfo(String containerID) {
		RF_ContainerInfo result = null;
		try {

			result = RF_ContainerInfo_Dao.get(containerID, RF_ContainerInfo.class);
			if (result.getCar_ID() == null) {
				result.setCar_ID("");
			}
		} catch (Exception e) {

			logger.error("Exception:" + StackTrace2String(e));
		}
		return result;
	}

	public static RF_ContainerInfo GetContainerInfo(String fab, String area, String gate, String readerIP) {
		RF_ContainerInfo result = null;
		try {

			Map<String, Object> sqlWhereMap = new HashMap<String, Object>();

			sqlWhereMap.put("fab", fab);
			sqlWhereMap.put("area", area);
			sqlWhereMap.put("gate", gate);
			List<RF_ContainerInfo> resultList = RF_ContainerInfo_Dao.findAllByConditions(sqlWhereMap,
					RF_ContainerInfo.class);
			if (resultList.size() != 0) {
				result = resultList.get(0);
				if (result.getCar_ID() == null) {
					result.setCar_ID("");
				}
			}
		} catch (Exception e) {

			logger.error(readerIP + " Exception:" + StackTrace2String(e));
		}
		return result;
	}

	public static RF_ContainerInfo GetContainerInfo(RF_Tag_History tag) {
		RF_ContainerInfo result = null;
		try {

			result = RF_ContainerInfo_Dao.get(tag.getTag_ID(), RF_ContainerInfo.class);
			if (result.getCar_ID() == null) {
				result.setCar_ID("");
			}
		} catch (Exception e) {

			logger.error(tag.getReader_IP() + " " + "Exception:" + StackTrace2String(e));
		}
		return result;
	}

	public static void UpdateContainerInfo(RF_ContainerInfo t, String readerIP) {

		try {

			RF_ContainerInfo_Dao.update(t);
			SetMoveHistory(t, readerIP);

		} catch (Exception e) {

			logger.error(readerIP + " " + "Exception:" + StackTrace2String(e));
		}
	}

	public static void SetMoveHistory(RF_ContainerInfo t, String readerIP) {
		try {

			RF_Move_History his = new RF_Move_History();

			his.setArea(t.getArea() == null ? "" : t.getArea());
			his.setCar_ID(t.getCar_ID() == null ? "" : t.getCar_ID());
			his.setCar_Type(t.getCar_Type() == null ? "" : t.getCar_Type());
			his.setContainer_ID(t.getContainer_ID() == null ? "" : t.getContainer_ID());
			his.setContainer_Status(t.getContainer_Status() == null ? "" : t.getContainer_Status());
			his.setContainer_Type(t.getContainer_Type() == null ? "" : t.getContainer_Type());
			his.setDriverName(t.getDriverName() == null ? "" : t.getDriverName());
			his.setDriverPhone(t.getDriverPhone() == null ? "" : t.getDriverPhone());
			his.setFab(t.getFab() == null ? "" : t.getFab());
			his.setArea(t.getArea() == null ? "" : t.getArea());
			his.setGate(t.getGate() == null ? "" : t.getGate());
			his.setReason(t.getReason() == null ? "" : t.getReason());
			his.setSource(t.getSource() == null ? "" : t.getSource());
			his.setVendor_Name(t.getVendor_Name() == null ? "" : t.getVendor_Name());
			his.setTimeStamp(GetNowTimeStr() == null ? "" : GetNowTimeStr());
			his.setStartTime(t.getStartTime() == null ? "" : t.getStartTime());
			his.setEndTime(t.getEndTime() == null ? "" : t.getEndTime());
			his.setImpNo(t.getImpNo() == null ? "" : t.getImpNo());
			his.setVendorID(t.getVendorID() == null ? "" : t.getVendorID());
			his.setVendorCount(t.getVendorCount() == null ? "" : t.getVendorCount());
			his.setCurrentStatus(t.getCurrentStatus() == null ? "" : t.getCurrentStatus());
			his.setCurrentAction(t.getCurrentAction() == null ? "" : t.getCurrentAction());
			his.setProcess_Start(t.getProcess_Start() == null ? "" : t.getProcess_Start());
			his.setProcess_End(t.getProcess_End() == null ? "" : t.getProcess_End());
			his.setProcess_Count(t.getProcess_Count() == null ? "" : t.getProcess_Count());
			his.setCurrent_Operation(t.getCurrent_Operation() == null ? "" : t.getCurrent_Operation());

			RF_Move_History_Dao.save(his);

		} catch (Exception e) {

			logger.error(readerIP + " " + "Exception:" + StackTrace2String(e));
		}
	}

	public static void UpdateGateSetting(RF_Gate_Setting t, String readerIP) {

		try {
			RF_Gate_Setting_Dao.update(t);
		} catch (Exception e) {

			logger.error(readerIP + " " + "Exception:" + StackTrace2String(e));
		}

	}

	public static boolean CheckManualMode(RF_Tag_History tag) {
		boolean result = false;
		try {

			RF_Gate_Setting gate = GetGateSetting(tag.getFab(), tag.getArea(), tag.getGate(), tag.getReader_IP());

			if (gate.getManual_Bind()) {
				result = true;
			}

		} catch (Exception e) {

			logger.error(tag.getReader_IP() + " " + "Exception:" + StackTrace2String(e));
		}
		return result;
	}

	public static RF_Gate_Setting GetGateSetting(RF_ContainerInfo container, String readerIP) {
		RF_Gate_Setting result = null;
		try {

			Map<String, Object> sqlWhereMap = new HashMap<String, Object>();

			sqlWhereMap.put("fab", container.getFab());
			sqlWhereMap.put("area", container.getArea());
			sqlWhereMap.put("gate", container.getGate());
			List<RF_Gate_Setting> resultList = RF_Gate_Setting_Dao.findAllByConditions(sqlWhereMap,
					RF_Gate_Setting.class);
			if (resultList.size() != 0) {
				result = resultList.get(0);
			}

		} catch (Exception e) {

			logger.error(readerIP + " " + "Exception:" + StackTrace2String(e));
		}
		return result;
	}

	public static RF_Gate_Setting GetGateSetting(String fab, String area, String gate, String readerIP) {
		RF_Gate_Setting result = null;
		try {

			Map<String, Object> sqlWhereMap = new HashMap<String, Object>();

			sqlWhereMap.put("fab", fab);
			sqlWhereMap.put("area", area);
			sqlWhereMap.put("gate", gate);
			List<RF_Gate_Setting> resultList = RF_Gate_Setting_Dao.findAllByConditions(sqlWhereMap,
					RF_Gate_Setting.class);
			if (resultList.size() != 0) {
				result = resultList.get(0);
			}
			// For T2 Delivery 1&2
			if (!result.getShare_Gate().equals("0")) {
				logger.info(readerIP + " Looking for current operation gate");
				if (!ToolUtility.GetOperation_Mode(result.getFab(), result.getArea(), result.getShare_Gate(), readerIP)
						.equals("")) {
					result = GetGateSetting(result.getFab(), result.getArea(), result.getShare_Gate(), readerIP);

				}
				logger.info(readerIP + " Current operation gate:" + result.getGate() + " " + result.getFab()
						+ result.getArea());
			}

		} catch (Exception e) {

			logger.error(readerIP + " " + "Exception:" + StackTrace2String(e));
		}
		return result;
	}

	public static List<RF_Gate_Setting> GetAllGateSetting(String readerIP) {
		List<RF_Gate_Setting> result = null;
		try {

			result = RF_Gate_Setting_Dao.findAllByConditions(null, RF_Gate_Setting.class);

		} catch (Exception e) {

			logger.error(readerIP + " " + "Exception:" + StackTrace2String(e));
		}
		return result;
	}

	public static void DeletePalletByCarID(String CarID, String readerIP) {
		try {

			Map<String, Object> sqlWhereMap = new HashMap<String, Object>();

			sqlWhereMap.put("container_id", CarID);

			RF_Pallet_Check_Dao.deleteAllByConditions(sqlWhereMap, RF_Pallet_Check.class);

		} catch (Exception e) {

			logger.error(readerIP + " " + "Exception:" + StackTrace2String(e));
		}

	}

	public static void DeletePallet(String palletID, String readerIP) {
		try {

			Map<String, Object> sqlWhereMap = new HashMap<String, Object>();

			sqlWhereMap.put("pallet_id", palletID);

			RF_Pallet_Check_Dao.deleteAllByConditions(sqlWhereMap, RF_Pallet_Check.class);

		} catch (Exception e) {

			logger.error(readerIP + " " + "Exception:" + StackTrace2String(e));
		}

	}

	public static RF_Pallet_Check GetMarkPallet(String palletID, String containerID, String opreation,
			boolean in_Container, String readerIP) {
		RF_Pallet_Check result = null;
		try {
			Map<String, Object> sqlWhereMap = new HashMap<String, Object>();
			sqlWhereMap.put("pallet_id", palletID);
			sqlWhereMap.put("in_container", in_Container);
			if (containerID != "") {
				sqlWhereMap.put("container_id", containerID);
			}
			if (opreation != "") {
				sqlWhereMap.put("opreation_mode", opreation);
			}

			List<RF_Pallet_Check> resultList = RF_Pallet_Check_Dao.findAllByConditions(sqlWhereMap,
					RF_Pallet_Check.class);
			if (resultList.size() != 0) {
				result = resultList.get(0);
			}

		} catch (Exception e) {

			logger.error(readerIP + " " + "Exception:" + StackTrace2String(e));
		}
		return result;
	}

	public static void MarkPallet(RF_Tag_History tag, String containerID, String Opreation, boolean in_Container) {
		try {
			RF_Pallet_Check pallet = null;

			String reportDate = GetNowTimeStr();
			Map<String, Object> sqlWhereMap = new HashMap<String, Object>();
			sqlWhereMap.put("pallet_id", tag.getTag_ID());

			if (containerID != "") {
				sqlWhereMap.put("container_id", containerID);
			}
			if (Opreation != "") {
				sqlWhereMap.put("opreation_mode", Opreation);
			}

			List<RF_Pallet_Check> resultList = RF_Pallet_Check_Dao.findAllByConditions(sqlWhereMap,
					RF_Pallet_Check.class);
			if (resultList.size() != 0) {
				pallet = resultList.get(0);
			}
			if (pallet != null) {
				pallet.setIn_Container(in_Container);
				pallet.setUpdateTime(reportDate);
				RF_Pallet_Check_Dao.update(pallet);
			} else {

				RF_Pallet_Check tmp = new RF_Pallet_Check();
				tmp.setContainer_ID(containerID);
				tmp.setIn_Container(in_Container);
				tmp.setOpreation_Mode(Opreation);
				tmp.setPallet_ID(tag.getTag_ID());
				tmp.setUpdateTime(reportDate);
				tmp.setPosition(tag.getFab());
				tmp.setDN_No(" ");
				RF_Pallet_Check_Dao.save(tmp);
			}
		} catch (Exception e) {

			logger.error(tag.getReader_IP() + " " + "Exception:" + StackTrace2String(e));
		}

	}

	public static List<String> GetNotCompletePallet(RF_ContainerInfo container, String readerIP) {
		List<String> result = new ArrayList<String>();
		try {
			Map<String, Object> sqlWhereMap = new HashMap<String, Object>();
			if (container.getCar_Type().equals(GlobleVar.TruckStr)) {
				sqlWhereMap.put("truck_no", container.getContainer_ID());
			} else {
				sqlWhereMap.put("container_no", container.getContainer_ID());
			}
			sqlWhereMap.put("status", "");

			List<WMS_T1_Check_Pallet> tmp = WMS_T1_Check_Pallet_Dao.findAllByConditions(sqlWhereMap,
					WMS_T1_Check_Pallet.class);
			for (WMS_T1_Check_Pallet each : tmp) {
				result.add(each.getPallet_ID());
			}

			List<WMS_T2_Check_Pallet> tmp1 = WMS_T2_Check_Pallet_Dao.findAllByConditions(sqlWhereMap,
					WMS_T2_Check_Pallet.class);

			for (WMS_T2_Check_Pallet each : tmp1) {
				result.add(each.getPallet_ID());
			}
		} catch (Exception e) {

			logger.error(readerIP + " Exception:" + StackTrace2String(e));
		}
		return result;
	}

	public static List<String> GetCompletePallet(RF_ContainerInfo container, String readerIP) {
		List<String> result = new ArrayList<String>();
		// result.add("12344");
		try {
			Map<String, Object> sqlWhereMap = new HashMap<String, Object>();
			if (container.getCar_Type().equals(GlobleVar.TruckStr)) {
				sqlWhereMap.put("truck_no", container.getContainer_ID());
			} else {
				sqlWhereMap.put("container_no", container.getContainer_ID());
			}
			sqlWhereMap.put("status", "TRUE");

			List<WMS_T1_Check_Pallet> tmp = WMS_T1_Check_Pallet_Dao.findAllByConditions(sqlWhereMap,
					WMS_T1_Check_Pallet.class);
			for (WMS_T1_Check_Pallet each : tmp) {
				result.add(each.getPallet_ID());
			}

			List<WMS_T2_Check_Pallet> tmp1 = WMS_T2_Check_Pallet_Dao.findAllByConditions(sqlWhereMap,
					WMS_T2_Check_Pallet.class);

			for (WMS_T2_Check_Pallet each : tmp1) {
				result.add(each.getPallet_ID());
			}
		} catch (Exception e) {

			logger.error(readerIP + " Exception:" + StackTrace2String(e));
		}
		return result;
	}

	public static List<String> GetAllPallet(RF_ContainerInfo container, String readerIP) {
		List<String> result = new ArrayList<String>();
		// result.add("B2911");
		// result.add("B2912");
		// result.add("B2913");
		// result.add("B2914");
		try {
			Map<String, Object> sqlWhereMap = new HashMap<String, Object>();
			if (container.getCar_Type().equals(GlobleVar.TruckStr)) {
				sqlWhereMap.put("truck_no", container.getContainer_ID());
			} else {
				sqlWhereMap.put("container_no", container.getContainer_ID());
			}

			List<WMS_T1_Check_Pallet> tmp = WMS_T1_Check_Pallet_Dao.findAllByConditions(sqlWhereMap,
					WMS_T1_Check_Pallet.class);
			for (WMS_T1_Check_Pallet each : tmp) {
				result.add(each.getPallet_ID());
			}

			List<WMS_T2_Check_Pallet> tmp1 = WMS_T2_Check_Pallet_Dao.findAllByConditions(sqlWhereMap,
					WMS_T2_Check_Pallet.class);

			for (WMS_T2_Check_Pallet each : tmp1) {
				result.add(each.getPallet_ID());
			}
		} catch (Exception e) {

			logger.error(readerIP + " Exception:" + StackTrace2String(e));
		}
		return result;
	}

	public static WMS_T1_Check_Pallet GetPalletChk(RF_Tag_History tag) {
		WMS_T1_Check_Pallet result = null;

		// result = new WMS_T1_Check_Pallet();
		// result.setContainer_NO("NBYU9022717");
		// result.setDN_No("123456");
		// result.setFab("T2");
		// result.setPallet_ID("B2911");
		// result.setStatus("");
		// result.setTruck_NO("1234");
		try {

			result = WMS_T1_Check_Pallet_Dao.get(tag.getTag_ID(), WMS_T1_Check_Pallet.class);
			if (result == null) {
				WMS_T2_Check_Pallet t2 = WMS_T2_Check_Pallet_Dao.get(tag.getTag_ID(), WMS_T2_Check_Pallet.class);
				if (t2 != null) {
					result = new WMS_T1_Check_Pallet();
					result.setContainer_NO(t2.getContainer_NO());
					result.setDN_No(t2.getDN_No());
					result.setFab(t2.getFab());
					result.setPallet_ID(t2.getPallet_ID());
					result.setStatus(t2.getStatus());
					result.setTruck_NO(t2.getTruck_NO());
					if (result.getContainer_NO() == null) {
						result.setContainer_NO("");
					}
					if (result.getStatus() == null) {
						result.setStatus("");
					}
					if (result.getTruck_NO() == null) {
						result.setTruck_NO("");
					}
				}
			}

		} catch (Exception e) {

			logger.error(tag.getReader_IP() + " Exception:" + StackTrace2String(e));
		}
		return result;
	}

	public static WMS_T2_Check_Pallet GetT2Pallet(RF_Tag_History tag, String readerIP) {
		WMS_T2_Check_Pallet result = null;
		try {

			result = WMS_T2_Check_Pallet_Dao.get(tag.getTag_ID(), WMS_T2_Check_Pallet.class);

		} catch (Exception e) {

			logger.error(readerIP + " Exception:" + StackTrace2String(e));
		}
		return result;
	}

	public static String GetShipTo(RF_ContainerInfo container, String readerIP) {
		String result = "";

		try {
			Map<String, Object> sqlWhereMap = new HashMap<String, Object>();
			if (container.getCar_Type().equals(GlobleVar.ContainerStr)) {
				sqlWhereMap.put("container_no", container.getContainer_ID());
			} else {
				sqlWhereMap.put("truck_no", container.getCar_ID());
			}

			List<WMS_T1_Ship_Info> T1Ship = WMS_T1_Ship_Info_Dao.findAllByConditions(sqlWhereMap,
					WMS_T1_Ship_Info.class);

			if (T1Ship.size() != 0) {
				result = T1Ship.get(0).getShip_To();
			} else {
				List<WMS_T2_Ship_Info> T2Ship = WMS_T2_Ship_Info_Dao.findAllByConditions(sqlWhereMap,
						WMS_T2_Ship_Info.class);

				if (T2Ship.size() != 0) {
					result = T2Ship.get(0).getShip_To();
				}
			}

		} catch (Exception e) {

			logger.error(readerIP + " Exception:" + StackTrace2String(e));
		}

		return result;
	}

	public static String InitSubtitleStr(RF_ContainerInfo container, String readerIP) {
		String result = "";
		if (container != null) {

			result = container.getCar_Type().replace("Container", "貨櫃").replace("Truck", "貨車") + "進入:"
					+ container.getContainer_ID();
		} else {
			result = "無車輛進入";
		}
		return result;
	}

	public static String ConvertCarStr(RF_Tag_History tag) {
		String result = "";
		try {
			switch (tag.getTag_Type()) {
			case "C":
				result += "貨櫃";
				break;
			case "T":
				result += "貨車";
				break;
			}
		} catch (Exception e) {

			logger.error(tag.getReader_IP() + " Exception:" + StackTrace2String(e));
		}
		return result;
	}

	public static String ConvertCarStr(RF_ContainerInfo containerInfo, String readerIP) {
		String result = "";
		try {
			switch (containerInfo.getCar_Type()) {
			case GlobleVar.ContainerStr:
				result += "貨櫃";
				break;
			case GlobleVar.TruckStr:
				result += "貨車";
				break;
			}
		} catch (Exception e) {

			logger.error(readerIP + " Exception:" + StackTrace2String(e));
		}
		return result;
	}

	public static String ConvertGateStr(RF_Tag_History tag) {
		String result = tag.getFab();
		try {
			switch (tag.getArea()) {
			case "Receive":
				result += "收" + tag.getGate() + "碼頭";
				break;
			case "Delivery":
				result += "出" + tag.getGate() + "碼頭";
				break;
			}
		} catch (Exception e) {

			logger.error(tag.getReader_IP() + " Exception:" + StackTrace2String(e));
		}
		return result;
	}

	public static String ConvertGateStr(RF_ContainerInfo containerInfo, String readerIP) {
		String result = containerInfo.getFab();
		try {
			switch (containerInfo.getArea()) {
			case "Receive":
				result += "收" + containerInfo.getGate() + "碼頭";
				break;
			case "Delivery":
				result += "出" + containerInfo.getGate() + "碼頭";
				break;
			}
		} catch (Exception e) {

			logger.error(readerIP + " Exception:" + StackTrace2String(e));
		}
		return result;
	}

	public static String GetCylinderSummary(String fab, String readerIP) {
		String result = "";
		int stock = 0;
		int use = 0;
		int empty = 0;
		List<RF_Cylinder_Status> cylinders = GetAllCylinders(readerIP);
		for (RF_Cylinder_Status each : cylinders) {
			switch (each.getStatus()) {
			case GlobleVar.Cylinder_Create:
				stock++;
				break;
			case GlobleVar.Cylinder_Used:
				use++;
				break;
			case GlobleVar.Cylinder_Empty:
				empty++;
				break;
			}

		}
		result = "庫存" + stock + "、使用" + use + "、空瓶" + empty;
		return result;
	}
}
