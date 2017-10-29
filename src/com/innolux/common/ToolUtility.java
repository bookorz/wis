package com.innolux.common;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.innolux.WIS_Main;
import com.innolux.dao.GenericDao;
import com.innolux.dao.JdbcGenericDaoImpl;
import com.innolux.model.*;
import com.innolux.service.*;

public class ToolUtility {

	private Logger logger = Logger.getLogger(this.getClass());

	private GenericDao<RF_SignalTower_Setting> RF_SignalTower_Setting_Dao = new JdbcGenericDaoImpl<RF_SignalTower_Setting>(
			GlobleVar.WIS_DB);
	private GenericDao<RF_Subtitle_Setting> RF_Subtitle_Setting_Dao = new JdbcGenericDaoImpl<RF_Subtitle_Setting>(
			GlobleVar.WIS_DB);
	private GenericDao<RF_Tag_Mapping> RF_Tag_Mapping_Dao = new JdbcGenericDaoImpl<RF_Tag_Mapping>(GlobleVar.WIS_DB);
	private GenericDao<RF_Gate_Error> RF_Gate_Error_Dao = new JdbcGenericDaoImpl<RF_Gate_Error>(GlobleVar.WIS_DB);
	private GenericDao<RF_ContainerInfo> RF_ContainerInfo_Dao = new JdbcGenericDaoImpl<RF_ContainerInfo>(
			GlobleVar.WIS_DB);
	private GenericDao<RF_Move_History> RF_Move_History_Dao = new JdbcGenericDaoImpl<RF_Move_History>(GlobleVar.WIS_DB);
	private GenericDao<RF_Gate_Setting> RF_Gate_Setting_Dao = new JdbcGenericDaoImpl<RF_Gate_Setting>(GlobleVar.WIS_DB);
	private GenericDao<RF_Pallet_Check> RF_Pallet_Check_Dao = new JdbcGenericDaoImpl<RF_Pallet_Check>(GlobleVar.WIS_DB);
	private GenericDao<RF_Tag_History> RF_Tag_History_Dao = new JdbcGenericDaoImpl<RF_Tag_History>(GlobleVar.WIS_DB);
	private GenericDao<WMS_T1_Ship_Info> WMS_T1_Ship_Info_Dao = new JdbcGenericDaoImpl<WMS_T1_Ship_Info>(
			GlobleVar.T1WMS_DB);
	private GenericDao<WMS_T2_Ship_Info> WMS_T2_Ship_Info_Dao = new JdbcGenericDaoImpl<WMS_T2_Ship_Info>(
			GlobleVar.T2WMS_DB);
	private GenericDao<WMS_T1_Opreation_Mode> WMS_T1_Opreation_Mode_Dao = new JdbcGenericDaoImpl<WMS_T1_Opreation_Mode>(
			GlobleVar.T1WMS_DB);
	private GenericDao<WMS_T2_Opreation_Mode> WMS_T2_Opreation_Mode_Dao = new JdbcGenericDaoImpl<WMS_T2_Opreation_Mode>(
			GlobleVar.T2WMS_DB);

	public static String StackTrace2String(Exception e) {

		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		e.printStackTrace(pw);
		return sw.toString(); // stack trace as a string
	}

	public String getTxnID(String eqpID, String functionID) {
		StringBuffer sb = new StringBuffer();
		sb.append((int) (Math.random() * 100)).append("_").append(eqpID).append("_").append(functionID).append("_");
		sb.append(new SimpleDateFormat("HH:mm:ss.SSS").format(Calendar.getInstance().getTime()));
		return sb.toString();
	}

	public String GetOpreation_Mode(RF_Tag_History tag) {
		String result = "";
		try {
			Map<String, Object> sqlWhereMap = new HashMap<String, Object>();

			sqlWhereMap.put("wh", tag.getFab());
			sqlWhereMap.put("area", tag.getArea());
			sqlWhereMap.put("gate", tag.getGate());

			switch (tag.getFab()) {
			case "T1":
				result = WMS_T1_Opreation_Mode_Dao.findAllByConditions(sqlWhereMap, WMS_T1_Opreation_Mode.class).get(0).getOpreation_Type();
				break;
			case "T2":
				result = WMS_T2_Opreation_Mode_Dao.findAllByConditions(sqlWhereMap, WMS_T2_Opreation_Mode.class).get(0).getOpreation_Type();
				break;
			}
		} catch (Exception e) {
			logger.error(tag.getReader_IP() + " " + "Exception:" + StackTrace2String(e));
		}
		return result;
	}

	public void InsertLog(RF_Tag_History tag) {
		try {

			RF_Tag_History_Dao.save(tag);
		} catch (Exception e) {
			logger.error(tag.getReader_IP() + " " + "Exception:" + StackTrace2String(e));
		}
	}

	public void SignalTowerAutoOff(RF_Gate_Setting gate, String cmd, long delay, String readerIP) {
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

	public void SignalTower(RF_Gate_Setting gate, String cmd, String readerIP) {
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

	public void Subtitle(RF_Gate_Setting gate, String showStr, String readerIP) {
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

	public void VoiceSend(String path, String voiceStr, String readerIP) {
		try {

			if (!VoiceService.Send(path, voiceStr)) {

				logger.error(readerIP + " Voice send fail.");
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error(readerIP + " " + "Exception:" + StackTrace2String(e));
		}

	}

	public RF_Tag_Mapping GetTagMapping(RF_Tag_History tag) {
		RF_Tag_Mapping result = null;
		try {

			result = RF_Tag_Mapping_Dao.get(tag.getTag_ID(), RF_Tag_Mapping.class);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error(tag.getReader_IP() + " " + "Exception:" + StackTrace2String(e));
		}
		return result;
	}

	public RF_Gate_Error GetGateError(RF_Tag_History tag) {
		RF_Gate_Error result = null;
		try {

			Map<String, Object> sqlWhereMap = new HashMap<String, Object>();

			sqlWhereMap.put("fab", tag.getFab());
			sqlWhereMap.put("area", tag.getArea());
			sqlWhereMap.put("gate", tag.getGate());

			result = RF_Gate_Error_Dao.findAllByConditions(sqlWhereMap, RF_Gate_Error.class).get(0);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error(tag.getReader_IP() + " " + "Exception:" + StackTrace2String(e));
		}
		return result;
	}

	public void DeleteGateError(RF_Tag_History tag) {

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

	public void SetGateError(RF_Tag_History tag, String errType, String errStr) {

		try {

			GenericDao<RF_Gate_Error> RF_Gate_Error_Dao = new JdbcGenericDaoImpl<RF_Gate_Error>(GlobleVar.WIS_DB);
			RF_Gate_Error t = new RF_Gate_Error();
			t.setFab(tag.getFab());
			t.setArea(tag.getArea());
			t.setGate(tag.getArea());
			t.setError_Type(errType);
			t.setError_Message(errStr);
			t.setTimeStamp(Calendar.getInstance().getTime());

			RF_Gate_Error_Dao.save(t);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error(tag.getReader_IP() + " " + "Exception:" + StackTrace2String(e));
		}

	}

	public boolean CheckManualMode(RF_Tag_History tag) {
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

	public boolean CheckPortBinding(RF_Tag_History tag) {
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

	public RF_ContainerInfo GetContainerInfo(RF_Gate_Setting gate) {
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

	public RF_ContainerInfo GetContainerInfo(RF_Tag_History tag) {
		RF_ContainerInfo result = null;
		try {

			result = RF_ContainerInfo_Dao.get(tag.getTag_ID(), RF_ContainerInfo.class);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error(tag.getReader_IP() + " " + "Exception:" + StackTrace2String(e));
		}
		return result;
	}

	public void UpdateContainerInfo(RF_ContainerInfo t, String readerIP) {

		try {

			RF_ContainerInfo_Dao.update(t);
			SetMoveHistory(t, readerIP);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error(readerIP + " " + "Exception:" + StackTrace2String(e));
		}
	}

	public void SetMoveHistory(RF_ContainerInfo t, String readerIP) {
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

	public void SetGateSetting(RF_Gate_Setting t, String readerIP) {

		try {
			RF_Gate_Setting_Dao.update(t);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error(readerIP + " " + "Exception:" + StackTrace2String(e));
		}

	}

	public RF_Gate_Setting GetGateSetting(RF_Tag_History tag) {
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

	public void DeletePalletByCarID(RF_Tag_History tag) {
		try {

			Map<String, Object> sqlWhereMap = new HashMap<String, Object>();

			sqlWhereMap.put("container_id", tag.getTag_ID());

			RF_Pallet_Check_Dao.deleteAllByConditions(sqlWhereMap, RF_Pallet_Check.class);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error(tag.getReader_IP() + " " + "Exception:" + StackTrace2String(e));
		}

	}

	public List<RF_Pallet_Check> GetNotCompletePallet(RF_ContainerInfo container, String readerIP) {
		List<RF_Pallet_Check> result = null;
		try {
			Map<String, Object> sqlWhereMap = new HashMap<String, Object>();

			sqlWhereMap.put("container_id", container.getContainer_ID());
			sqlWhereMap.put("in_container", false);

			result = RF_Pallet_Check_Dao.findAllByConditions(sqlWhereMap, RF_Pallet_Check.class);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error(readerIP + " Exception:" + StackTrace2String(e));
		}
		return result;
	}
	
	public RF_Pallet_Check GetPallet(RF_Tag_History tag, String readerIP) {
		RF_Pallet_Check result = null;
		try {
			Map<String, Object> sqlWhereMap = new HashMap<String, Object>();

			sqlWhereMap.put("pallet_id", tag.getTag_ID());
			sqlWhereMap.put("in_container", false);

			result = RF_Pallet_Check_Dao.findAllByConditions(sqlWhereMap, RF_Pallet_Check.class);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error(readerIP + " Exception:" + StackTrace2String(e));
		}
		return result;
	}

	public String GetShipTo(RF_ContainerInfo container, String readerIP) {
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

	public String ConvertCarStr(RF_Tag_History tag) {
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

	public String ConvertGateStr(RF_Tag_History tag) {
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

	public String ConvertGateStr(RF_ContainerInfo containerInfo, String readerIP) {
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
