package com.innolux.common;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.innolux.dao.GenericDao;
import com.innolux.dao.JdbcGenericDaoImpl;
import com.innolux.model.*;
import com.innolux.service.*;

public class ToolUtility {

	private static Logger logger = Logger.getLogger(ToolUtility.class);

	private static GenericDao<RF_SignalTower_Setting> RF_SignalTower_Setting_Dao = new JdbcGenericDaoImpl<RF_SignalTower_Setting>(
			GlobleVar.WIS_DB);
	private static GenericDao<RF_Subtitle_Setting> RF_Subtitle_Setting_Dao = new JdbcGenericDaoImpl<RF_Subtitle_Setting>(
			GlobleVar.WIS_DB);
	private static GenericDao<RF_Tag_Mapping> RF_Tag_Mapping_Dao = new JdbcGenericDaoImpl<RF_Tag_Mapping>(
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
			GlobleVar.T2WMS_DB);

	private static GenericDao<WMS_T2_ASN_Pallet> WMS_T2_ASN_Pallet_Dao = new JdbcGenericDaoImpl<WMS_T2_ASN_Pallet>(
			GlobleVar.T2WMS_DB);

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

	public static String getTxnID(String eqpID, String functionID) {
		StringBuffer sb = new StringBuffer();
		sb.append((int) (Math.random() * 100)).append("_").append(eqpID).append("_").append(functionID).append("_");
		sb.append(new SimpleDateFormat("HH:mm:ss.SSS").format(Calendar.getInstance().getTime()));
		return sb.toString();
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

	public static String SendDeliveryLoad(RF_Tag_History tag, RF_ContainerInfo container, String Action,
			String readerIP) {
		String RvFormat = ">>L WmsRfidPalletInfoXml USERID=\"DIS\" xml=\"<ZDIS01><HEADER><PALLET_ID>" + tag.getTag_ID()
				+ "</PALLET_ID><FAB>" + tag.getFab() + "</FAB><AREA>" + tag.getArea() + "</AREA><GATEID>"
				+ tag.getGate() + "</GATEID><CONTAINERID>" + container.getContainer_ID() + "</CONTAINERID><VEHICLE_NO>"
				+ container.getCar_ID() + "</VEHICLE_NO><ACTION>" + Action + "</ACTION></HEADER></ZDIS01>\"";
		logger.info(readerIP + " " + "send to WMS:" + RvFormat);
		return RvFormat;
	}

	public static WMS_T1_ASN_Pallet GetASNPallet(RF_Tag_History tag, String readerIP) {
		WMS_T1_ASN_Pallet result = null;

		try {

			switch (tag.getFab()) {
			case "T1":
				result = WMS_T1_ASN_Pallet_Dao.get(tag.getTag_ID(), WMS_T1_ASN_Pallet.class);
				break;
			case "T2":
				result = new WMS_T1_ASN_Pallet();
				WMS_T2_ASN_Pallet t2 = WMS_T2_ASN_Pallet_Dao.get(tag.getTag_ID(), WMS_T2_ASN_Pallet.class);
				result.setASN_NO(t2.getASN_NO());
				result.setCar_NO(t2.getCar_NO());
				result.setPallet_ID(t2.getPallet_ID());
				result.setRFID_Chk(t2.getRFID_Chk());
				break;
			}
		} catch (Exception e) {
			logger.error(tag.getReader_IP() + " " + "Exception:" + StackTrace2String(e));
		}

		return result;
	}

	public static String GetOpreation_Mode(RF_Tag_History tag) {
		String result = "";
		try {
			Map<String, Object> sqlWhereMap = new HashMap<String, Object>();

			sqlWhereMap.put("wh", tag.getFab());
			sqlWhereMap.put("area", tag.getArea());
			sqlWhereMap.put("gate", tag.getGate());

			switch (tag.getFab()) {
			case "T1":
				result = WMS_T1_Opreation_Mode_Dao.findAllByConditions(sqlWhereMap, WMS_T1_Opreation_Mode.class).get(0)
						.getOpreation_Type();
				break;
			case "T2":
				result = WMS_T2_Opreation_Mode_Dao.findAllByConditions(sqlWhereMap, WMS_T2_Opreation_Mode.class).get(0)
						.getOpreation_Type();
				break;
			}
		} catch (Exception e) {
			logger.error(tag.getReader_IP() + " " + "Exception:" + StackTrace2String(e));
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

	public static void SignalTowerAutoOff(RF_Gate_Setting gate, String cmd, long delay, String readerIP) {
		try {
			String cmdStr = "";

			RF_SignalTower_Setting signalToerSet = RF_SignalTower_Setting_Dao.get(gate.getSignalTower_Name(),
					RF_SignalTower_Setting.class);
			if (signalToerSet != null) {
				switch (cmd) {
				case GlobleVar.RedOn:
					cmdStr = signalToerSet.getRed_On_Cmd();
					// cmdStr2 = signalToerSet.getRed_Off_Cmd();
					// cmd2 = GlobleVar.RedOff;
					break;
				case GlobleVar.OrangeOn:
					cmdStr = signalToerSet.getOrange_On_Cmd();
					// cmdStr2 = signalToerSet.getOrange_Off_Cmd();
					// cmd2 = GlobleVar.OrangeOff;
					break;
				case GlobleVar.GreenOn:
					cmdStr = signalToerSet.getGreen_On_Cmd();
					// cmdStr2 = signalToerSet.getGreen_Off_Cmd();
					// cmd2 = GlobleVar.GreenOff;
					break;
				}

				if (ReaderCmdService.SendCmd(signalToerSet.getReader_IP(), cmdStr)) {
					signalToerSet.setState(cmd);
					RF_SignalTower_Setting_Dao.update(signalToerSet);

					Thread t = new Thread(new Runnable() {

						@Override
						public void run() {
							String cmd2 = "";
							String cmdStr2 = "";
							try {

								switch (cmd) {
								case GlobleVar.RedOn:
									// cmdStr = signalToerSet.getRed_On_Cmd();
									cmdStr2 = signalToerSet.getRed_Off_Cmd();
									cmd2 = GlobleVar.RedOff;
									break;
								case GlobleVar.OrangeOn:
									// cmdStr = signalToerSet.getOrange_On_Cmd();
									cmdStr2 = signalToerSet.getOrange_Off_Cmd();
									cmd2 = GlobleVar.OrangeOff;
									break;
								case GlobleVar.GreenOn:
									// cmdStr = signalToerSet.getGreen_On_Cmd();
									cmdStr2 = signalToerSet.getGreen_Off_Cmd();
									cmd2 = GlobleVar.GreenOff;
									break;
								}
								Thread.sleep(delay);
								if (ReaderCmdService.SendCmd(signalToerSet.getReader_IP(), cmdStr2)) {
									signalToerSet.setState(cmd2);
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
			// TODO Auto-generated catch block
			logger.error(readerIP + " " + "Exception:" + StackTrace2String(e));
		}

	}

	public static void SignalTower(RF_Gate_Setting gate, String cmd, String readerIP) {
		try {
			String cmdStr = "";

			RF_SignalTower_Setting signalToerSet = RF_SignalTower_Setting_Dao.get(gate.getSignalTower_Name(),
					RF_SignalTower_Setting.class);
			if (signalToerSet != null) {
				switch (cmd) {
				case GlobleVar.RedOn:
					cmdStr = signalToerSet.getRed_On_Cmd();
					break;
				case GlobleVar.RedOff:
					cmdStr = signalToerSet.getRed_Off_Cmd();
					break;
				case GlobleVar.OrangeOn:
					cmdStr = signalToerSet.getOrange_On_Cmd();
					break;
				case GlobleVar.OrangeOff:
					cmdStr = signalToerSet.getOrange_Off_Cmd();
					break;
				case GlobleVar.GreenOn:
					cmdStr = signalToerSet.getGreen_On_Cmd();
					break;
				case GlobleVar.GreenOff:
					cmdStr = signalToerSet.getGreen_Off_Cmd();
					break;

				}
				if (ReaderCmdService.SendCmd(signalToerSet.getReader_IP(), cmdStr)) {
					signalToerSet.setState(cmd);
					RF_SignalTower_Setting_Dao.update(signalToerSet);
				} else {
					logger.error(readerIP + " SignalTower send fail.");
				}
			} else {
				logger.error(readerIP + " SignalTower_Setting is not exist");
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error(readerIP + " " + "Exception:" + StackTrace2String(e));
		}

	}

	public static void Subtitle(RF_Gate_Setting gate, String showStr, String readerIP) {
		try {

			RF_Subtitle_Setting SubtitleSetting = RF_Subtitle_Setting_Dao.get(gate.getSubtitle_Name(),
					RF_Subtitle_Setting.class);

			if (SubtitleSetting != null) {

				if (SubtitleService.Show(SubtitleSetting.getSubtitle_IP(), showStr)) {
					logger.info(readerIP + " " + showStr);
					SubtitleSetting.setCurrent_Subtitle(showStr);
					RF_Subtitle_Setting_Dao.update(SubtitleSetting);
				} else {
					logger.error(readerIP + " SignalTower send fail.");
				}
			} else {
				logger.error(readerIP + " SubtitleSetting is not exist");
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error(readerIP + " " + "Exception:" + StackTrace2String(e));
		}

	}

	public static void VoiceSend(String path, String voiceStr, String readerIP) {
		try {

			if (!VoiceService.Send(path, voiceStr)) {

				logger.error(readerIP + " Voice send fail.");
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error(readerIP + " " + "Exception:" + StackTrace2String(e));
		}

	}

	public static RF_Tag_Mapping GetTagMapping(RF_Tag_History tag) {
		RF_Tag_Mapping result = null;
		try {

			result = RF_Tag_Mapping_Dao.get(tag.getTag_ID(), RF_Tag_Mapping.class);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error(tag.getReader_IP() + " " + "Exception:" + StackTrace2String(e));
		}
		return result;
	}

	public static void SetErrorPallet(RF_Tag_History tag, RF_ContainerInfo container, String opreation, String reason) {

		try {
			RF_Error_Pallet errorPallet = new RF_Error_Pallet();
			errorPallet.setContainer_ID(container.getContainer_ID());
			errorPallet.setOpreation_Mode(opreation);
			errorPallet.setPallet_ID(tag.getTag_ID());
			errorPallet.setReason(reason);
			errorPallet.setTimeStamp(Calendar.getInstance().getTime());

			RF_Error_Pallet_Dao.save(errorPallet);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error(tag.getReader_IP() + " " + "Exception:" + StackTrace2String(e));
		}

	}

	public static List<RF_Error_Pallet> GetErrorPalletList(RF_Tag_History tag) {
		List<RF_Error_Pallet> result = null;
		try {
			Map<String, Object> sqlWhereMap = new HashMap<String, Object>();

			sqlWhereMap.put("pallet_id", tag.getTag_ID());

			result = RF_Error_Pallet_Dao.findAllByConditions(sqlWhereMap, RF_Error_Pallet.class);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error(tag.getReader_IP() + " " + "Exception:" + StackTrace2String(e));
		}
		return result;
	}

	public static RF_Error_Pallet GetErrorPallet(RF_Tag_History tag, RF_ContainerInfo container, String opreation,
			String reason) {
		RF_Error_Pallet result = null;
		try {

			Map<String, Object> sqlWhereMap = new HashMap<String, Object>();

			sqlWhereMap.put("pallet_id", tag.getTag_ID());
			if (container != null) {
				sqlWhereMap.put("container_id", container.getContainer_ID());
			}
			if (opreation != "") {
				sqlWhereMap.put("opreation_mode", opreation);
			}
			if (reason != "") {
				sqlWhereMap.put("reason", reason);
			}
			result = RF_Error_Pallet_Dao.findAllByConditions(sqlWhereMap, RF_Error_Pallet.class).get(0);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error(tag.getReader_IP() + " " + "Exception:" + StackTrace2String(e));
		}
		return result;
	}

	public static String GetErrorPalletSummary(RF_ContainerInfo container, String opreation,String readerIP) {
		String result = "";
		String type1 = "";
		String type2 = "";
		try {

			Map<String, Object> sqlWhereMap = new HashMap<String, Object>();

			
			sqlWhereMap.put("container_id", container.getContainer_ID());
			sqlWhereMap.put("opreation_mode", opreation);

			List<RF_Error_Pallet> errorPalletList = RF_Error_Pallet_Dao.findAllByConditions(sqlWhereMap,
					RF_Error_Pallet.class);

			for (RF_Error_Pallet each : errorPalletList) {
				switch (each.getReason()) {
				case GlobleVar.ContainerMismatch:
					if (type1 == "") {
						type1 = "不屬於此貨櫃:";
					}
					type1 += each.getPallet_ID() + " ";
					break;
				case GlobleVar.WMSNotFound:
					if (type2 == "") {
						type2 = "不存在WMS資料庫:";
					}
					type2 += each.getPallet_ID() + " ";
					break;
				}
			}
			result = type1 + type2;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error(readerIP + " " + "Exception:" + StackTrace2String(e));
		}
		return result;
	}

	public static void DeleteErrorPallet(RF_Error_Pallet pallet, String readerIP) {

		try {

			RF_Error_Pallet_Dao.delete(pallet.getError_ID(), RF_Error_Pallet.class);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error(readerIP + " " + "Exception:" + StackTrace2String(e));
		}

	}

	public static void ClearErrorPallet(RF_ContainerInfo container, String readerIP) {

		try {

			Map<String, Object> sqlWhereMap = new HashMap<String, Object>();

			sqlWhereMap.put("container_id", container.getContainer_ID());

			RF_Error_Pallet_Dao.deleteAllByConditions(sqlWhereMap, RF_Error_Pallet.class);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error(readerIP + " " + "Exception:" + StackTrace2String(e));
		}

	}

	public static RF_Gate_Error GetGateError(String fab,String area,String gate, String errorType,String readerIP) {
		RF_Gate_Error result = null;
		try {

			Map<String, Object> sqlWhereMap = new HashMap<String, Object>();

			sqlWhereMap.put("fab", fab);
			sqlWhereMap.put("area", area);
			sqlWhereMap.put("gate", gate);
			if (!errorType.equals("")) {
				sqlWhereMap.put("error_type", errorType);
			}

			result = RF_Gate_Error_Dao.findAllByConditions(sqlWhereMap, RF_Gate_Error.class).get(0);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error(readerIP + " " + "Exception:" + StackTrace2String(e));
		}
		return result;
	}

	public static void DeleteGateError(RF_Tag_History tag) {

		try {

			Map<String, Object> sqlWhereMap = new HashMap<String, Object>();

			sqlWhereMap.put("fab", tag.getFab());
			sqlWhereMap.put("area", tag.getArea());
			sqlWhereMap.put("gate", tag.getGate());

			RF_Gate_Error_Dao.deleteAllByConditions(sqlWhereMap, RF_Gate_Error.class);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error(tag.getReader_IP() + " " + "Exception:" + StackTrace2String(e));
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
				// t.setError_ID(tag.getFab() + tag.getArea() + tag.getArea() + errType +
				// System.currentTimeMillis());
				t.setFab(tag.getFab());
				t.setArea(tag.getArea());
				t.setGate(tag.getArea());
				t.setError_Type(errType);
				t.setError_Message(errStr);
				t.setTimeStamp(Calendar.getInstance().getTime());

				RF_Gate_Error_Dao.save(t);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error(tag.getReader_IP() + " " + "Exception:" + StackTrace2String(e));
		}

	}

	public static boolean CheckManualMode(RF_Tag_History tag) {
		boolean result = false;
		try {

			Map<String, Object> sqlWhereMap = new HashMap<String, Object>();

			sqlWhereMap.put("fab", tag.getFab());
			sqlWhereMap.put("area", tag.getArea());
			sqlWhereMap.put("gate", tag.getGate());

			List<RF_ContainerInfo> containerInfo = RF_ContainerInfo_Dao.findAllByConditions(sqlWhereMap,
					RF_ContainerInfo.class);
			if (containerInfo.size() != 0) {
				if (containerInfo.get(0).getManual_Bind()) {
					result = true;
				}
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error(tag.getReader_IP() + " " + "Exception:" + StackTrace2String(e));
		}
		return result;
	}

	public static boolean CheckPortBinding(RF_Tag_History tag) {
		boolean result = false;
		try {

			Map<String, Object> sqlWhereMap = new HashMap<String, Object>();

			sqlWhereMap.put("fab", tag.getFab());
			sqlWhereMap.put("area", tag.getArea());
			sqlWhereMap.put("gate", tag.getGate());

			if (RF_ContainerInfo_Dao.findAllByConditions(sqlWhereMap, RF_ContainerInfo.class).size() != 0) {
				result = true;
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error(tag.getReader_IP() + " " + "Exception:" + StackTrace2String(e));
		}
		return result;
	}

	public static RF_ContainerInfo GetContainerInfo(String containerID) {
		RF_ContainerInfo result = null;
		try {

			result = RF_ContainerInfo_Dao.get(containerID, RF_ContainerInfo.class);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception:" + StackTrace2String(e));
		}
		return result;
	}

	public static RF_ContainerInfo GetContainerInfo(RF_Gate_Setting gate) {
		RF_ContainerInfo result = null;
		try {

			Map<String, Object> sqlWhereMap = new HashMap<String, Object>();

			sqlWhereMap.put("fab", gate.getFab());
			sqlWhereMap.put("area", gate.getArea());
			sqlWhereMap.put("gate", gate.getGate());

			result = RF_ContainerInfo_Dao.findAllByConditions(sqlWhereMap, RF_ContainerInfo.class).get(0);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception:" + StackTrace2String(e));
		}
		return result;
	}

	public static RF_ContainerInfo GetContainerInfo(RF_Tag_History tag) {
		RF_ContainerInfo result = null;
		try {

			result = RF_ContainerInfo_Dao.get(tag.getTag_ID(), RF_ContainerInfo.class);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error(tag.getReader_IP() + " " + "Exception:" + StackTrace2String(e));
		}
		return result;
	}

	public static void UpdateContainerInfo(RF_ContainerInfo t, String readerIP) {

		try {

			RF_ContainerInfo_Dao.update(t);
			SetMoveHistory(t, readerIP);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error(readerIP + " " + "Exception:" + StackTrace2String(e));
		}
	}

	public static void SetMoveHistory(RF_ContainerInfo t, String readerIP) {
		try {

			DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date today = Calendar.getInstance().getTime();
			String reportDate = df.format(today);

			RF_Move_History his = new RF_Move_History();

			his.setArea(t.getArea());
			his.setCar_ID(t.getCar_ID());
			his.setCar_Type(t.getCar_Type());
			his.setContainer_ID(t.getContainer_ID());
			his.setContainer_Status(t.getContainer_Status());
			his.setContainer_Type(t.getContainer_Type());
			his.setDriverName(t.getDriverName());
			his.setDriverPhone(t.getDriverPhone());
			his.setFab(t.getFab());
			his.setArea(t.getArea());
			his.setGate(t.getGate());
			his.setReason(t.getReason());
			his.setSource(t.getSource());
			his.setVendor_Name(t.getVendor_Name());
			his.setTimeStamp(reportDate);

			RF_Move_History_Dao.save(his);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error(readerIP + " " + "Exception:" + StackTrace2String(e));
		}
	}

	public static void SetGateSetting(RF_Gate_Setting t, String readerIP) {

		try {
			RF_Gate_Setting_Dao.update(t);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error(readerIP + " " + "Exception:" + StackTrace2String(e));
		}

	}
	
	public static RF_Gate_Setting GetGateSetting(RF_ContainerInfo container, String readerIP) {
		RF_Gate_Setting result = null;
		try {

			Map<String, Object> sqlWhereMap = new HashMap<String, Object>();

			sqlWhereMap.put("fab", container.getFab());
			sqlWhereMap.put("area", container.getArea());
			sqlWhereMap.put("gate", container.getGate());

			result = RF_Gate_Setting_Dao.findAllByConditions(sqlWhereMap, RF_Gate_Setting.class).get(0);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error(readerIP + " " + "Exception:" + StackTrace2String(e));
		}
		return result;
	}

	public static RF_Gate_Setting GetGateSetting(RF_Tag_History tag) {
		RF_Gate_Setting result = null;
		try {

			Map<String, Object> sqlWhereMap = new HashMap<String, Object>();

			sqlWhereMap.put("fab", tag.getFab());
			sqlWhereMap.put("area", tag.getArea());
			sqlWhereMap.put("gate", tag.getGate());

			result = RF_Gate_Setting_Dao.findAllByConditions(sqlWhereMap, RF_Gate_Setting.class).get(0);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error(tag.getReader_IP() + " " + "Exception:" + StackTrace2String(e));
		}
		return result;
	}

	public static void DeletePalletByCarID(String CarID, String readerIP) {
		try {

			Map<String, Object> sqlWhereMap = new HashMap<String, Object>();

			sqlWhereMap.put("container_id", CarID);

			RF_Pallet_Check_Dao.deleteAllByConditions(sqlWhereMap, RF_Pallet_Check.class);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error(readerIP + " " + "Exception:" + StackTrace2String(e));
		}

	}

	public static RF_Pallet_Check GetMarkPallet(String palletID, String containerID, String opreation, String readerIP) {
		RF_Pallet_Check result = new RF_Pallet_Check();
		try {
			Map<String, Object> sqlWhereMap = new HashMap<String, Object>();
			sqlWhereMap.put("pallet_id", palletID);
			if(containerID!="") {
				sqlWhereMap.put("container_id", containerID);
			}
			if(opreation!="") {
				sqlWhereMap.put("opreation_mode", opreation);
			}
			result = RF_Pallet_Check_Dao.findAllByConditions(sqlWhereMap, RF_Pallet_Check.class).get(0);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error(readerIP + " " + "Exception:" + StackTrace2String(e));
		}
		return result;
	}

	public static void MarkPallet(RF_Tag_History tag, String containerID, String Opreation) {
		try {
			RF_Pallet_Check pallet = new RF_Pallet_Check();
			pallet.setContainer_ID(containerID);
			pallet.setIn_Container(true);
			pallet.setOpreation_Mode(Opreation);
			pallet.setPallet_ID(tag.getTag_ID());
			pallet.setTimeStamp(Calendar.getInstance().getTime());

			RF_Pallet_Check_Dao.save(pallet);

		} catch (Exception e) {
			// TODO Auto-generated catch block
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
			// TODO Auto-generated catch block
			logger.error(readerIP + " Exception:" + StackTrace2String(e));
		}
		return result;
	}

	public static List<String> GetCompletePallet(RF_ContainerInfo container, String readerIP) {
		List<String> result = new ArrayList<String>();
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
			// TODO Auto-generated catch block
			logger.error(readerIP + " Exception:" + StackTrace2String(e));
		}
		return result;
	}

	public static List<String> GetAllPallet(RF_ContainerInfo container, String readerIP) {
		List<String> result = new ArrayList<String>();
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
			// TODO Auto-generated catch block
			logger.error(readerIP + " Exception:" + StackTrace2String(e));
		}
		return result;
	}

	public static WMS_T1_Check_Pallet GetPalletChk(RF_Tag_History tag) {
		WMS_T1_Check_Pallet result = null;
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
				}
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error(tag.getReader_IP() + " Exception:" + StackTrace2String(e));
		}
		return result;
	}

	public static WMS_T2_Check_Pallet GetT2Pallet(RF_Tag_History tag, String readerIP) {
		WMS_T2_Check_Pallet result = null;
		try {

			result = WMS_T2_Check_Pallet_Dao.get(tag.getTag_ID(), WMS_T2_Check_Pallet.class);

		} catch (Exception e) {
			// TODO Auto-generated catch block
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
			// TODO Auto-generated catch block
			logger.error(readerIP + " Exception:" + StackTrace2String(e));
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
			// TODO Auto-generated catch block
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
			// TODO Auto-generated catch block
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
			// TODO Auto-generated catch block
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
			// TODO Auto-generated catch block
			logger.error(readerIP + " Exception:" + StackTrace2String(e));
		}
		return result;
	}
}
