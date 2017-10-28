package com.innolux.common;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.innolux.WIS_Main;
import com.innolux.dao.GenericDao;
import com.innolux.dao.JdbcGenericDaoImpl;
import com.innolux.model.RF_Antenna_Setting;
import com.innolux.model.RF_ContainerInfo;
import com.innolux.model.RF_Ext_Setting;
import com.innolux.model.RF_Gate_Error;
import com.innolux.model.RF_Gate_Setting;
import com.innolux.model.RF_Move_History;
import com.innolux.model.RF_Pallet_Check;
import com.innolux.model.RF_SignalTower_Setting;
import com.innolux.model.RF_Subtitle_Setting;
import com.innolux.model.RF_Tag_History;
import com.innolux.model.RF_Tag_Mapping;
import com.rfid.Type;
import com.rfid.base.AntennaBase;
import com.rfid.base.ContainerBase;
import com.rfid.base.TagBase;

public class Port {
	public Logger logger = Logger.getLogger(this.getClass());
	private ToolUtility tools = new ToolUtility();

	public void DataHandle(List<RF_Tag_History> tagList, Hashtable<Integer, RF_Antenna_Setting> antSetting) {
		try {

			for (RF_Tag_History tag : tagList) {

				if (tools.GetGateError(tag) != null) {
					// If the gate had error, do nothing.
					continue;
				}
				tools.InsertLog(tag);

				switch (tag.getAntenna_Type()) {
				case GlobleVar.ANT_Pallet:
					GateHandler(tag);
					break;
				case GlobleVar.ANT_Container:
					PortHandler(tag);
					break;
				case GlobleVar.ANT_Abnormal:
					AbnormalHandler(tag);
					break;
				case GlobleVar.ANT_T1_Dispatch:
				case GlobleVar.ANT_T2_Dispatch:
					DispatchHandler(tag);
					break;
				case GlobleVar.ANT_In_CenterParking:
				case GlobleVar.ANT_Out_CenterParking:
					CenterParkingHandler(tag);
					break;
				case GlobleVar.ANT_Big_Stock:
				case GlobleVar.ANT_Big_Use:
				case GlobleVar.ANT_Small_Stock:
				case GlobleVar.ANT_Small_Use:
					CylinderHandler(tag);

					break;
				}
			}

		} catch (Exception e) {
			logger.error(tagList.get(0).getReader_IP() + " " + "Exception:" + tools.StackTrace2String(e));
		}
	}

	private void PortHandler(RF_Tag_History tag) {
		// ClsContainer Container = null;

		switch (tag.getTag_Type()) {
		case GlobleVar.TruckTag:
		case GlobleVar.ContainerTag:
			RF_Tag_Mapping tagMapping = tools.GetTagMapping(tag);
			if (tagMapping != null) {
				tag.setTag_ID(tagMapping.getReal_id());
				tools.InsertLog(tag);
				if (tools.CheckPortBinding(tag)) {
					// get gateSetting object
					RF_Gate_Setting gate = tools.GetGateSetting(tag);
					if (gate != null) {
						gate.setLast_ContainerTag_Time(System.currentTimeMillis());
						if (tag.getReceive_Time() - gate.getLast_MarkTag_Time() > GlobleVar.TagMaskInTime) {
							PortBind(gate, tag);

						} else {
							logger.error(tag.getReader_IP() + " PortHandler error: gate setting not exist.");
						}
						// update gateSetting object
						tools.SetGateSetting(gate, tag.getReader_IP());
					} else {
						logger.debug(tag.getReader_IP() + " PortHandler : This tag is not in TagMapping table.");
					}
				} else {
					logger.debug(tag.getReader_IP() + " PortHandler : This port was already binded car.");
				}
			}
			break;
		case GlobleVar.MarkTag: // MarkTAG

			if (!tools.CheckManualMode(tag)) {
				RF_Gate_Setting gate = tools.GetGateSetting(tag);
				if (gate != null) {
					if (gate.getMark_Tag().equals(tag.getTag_ID())) {
						if (tag.getReceive_Time() - gate.getLast_ContainerTag_Time() > GlobleVar.TagMaskOutTime) {

							PortUnBind(gate, tag);

						} else {
							logger.debug(tag.getReader_IP() + " This markTag had read by the gate in 30s.");
						}
					} else {
						logger.debug(tag.getReader_IP() + " This markTag is not match for the gate.");
					}

				} else {
					logger.error(tag.getReader_IP() + " PortHandler error: gate setting not exist.");
				}

			}
			break;
		}

	}

	public void PortBind(RF_Gate_Setting gate, RF_Tag_History tag) {
		String voiceText = "";
		RF_ContainerInfo container = tools.GetContainerInfo(tag);

		if (container == null) { 

			tools.SignalTower(gate, GlobleVar.RedOn, tag.getReader_IP());
			tools.Subtitle(gate, "該車輛沒有進廠紀錄", tag.getReader_IP());
			tools.SetGateError(tag, GlobleVar.NonEntryRecord,
					tools.ConvertGateStr(tag) + "，" + tools.ConvertCarStr(tag) + tag.getTag_ID() + "沒有進廠紀錄");
		} else if (!container.getGate().equals("0")) {
			tools.SignalTower(gate, GlobleVar.RedOn, tag.getReader_IP());
			tools.Subtitle(gate, "靠碼頭失敗，該車輛目前被綁定在" + tools.ConvertGateStr(container, tag.getReader_IP()),
					tag.getReader_IP());
			tools.SetGateError(tag, GlobleVar.BindingError,
					tag.getTag_ID() + "無法綁定於" + tools.ConvertGateStr(tag) + "，因目前被綁定在" + tools.ConvertGateStr(tag));
		} else {
			// Bind start
			container.setFab(tag.getFab());
			container.setArea(tag.getArea());
			container.setGate(tag.getGate());
			container.setManual_Bind(false);
			// Update container object
			tools.UpdateContainerInfo(container, tag.getReader_IP());
			// Clear all pallet by this car ID
			tools.DeletePalletByCarID(tag);
			tools.SignalTowerAutoOff(gate, GlobleVar.GreenOn, 5, tag.getReader_IP());
			tools.Subtitle(gate, tools.ConvertCarStr(tag) + "進入:" + container.getContainer_ID(),
					tag.getReader_IP());

			if (container.getCar_Type().equals(GlobleVar.TruckStr)) {
				if (container.getReason() != null) {
					if (container.getReason().equals("Delivery")) {
						voiceText = "廠商" + container.getSource() + " " + container.getVendor_Name() + "運輸已停靠"
								+ tools.ConvertGateStr(container, tag.getReader_IP()) + " 請同仁收貨";
					} else if (container.getReason().equals("Receive")) {
						voiceText = container.getVendor_Name() + "運輸已停靠"
								+ tools.ConvertGateStr(container, tag.getReader_IP()) + " 請同仁出貨";
					}
				}
			} else if (container.getCar_Type().equals(GlobleVar.ContainerStr)) {
				if (container.getContainer_Status().equals("NonEmpty")) {
					voiceText = container.getVendor_Name() + "運輸重櫃已停靠"
							+ tools.ConvertGateStr(container, tag.getReader_IP()) + " 請同仁卸貨";
				} else {
					voiceText = container.getVendor_Name() + "運輸空櫃已停靠"
							+ tools.ConvertGateStr(container, tag.getReader_IP()) + " 請同仁出貨";
				}
			}
			tools.VoiceSend(gate.getVoice_Path(), voiceText, tag.getReader_IP());

			// Binded

		}

	}

	public void PortUnBind(RF_Gate_Setting gate, RF_Tag_History tag) {
		String voiceText = "";
		RF_ContainerInfo containerInfo = tools.GetContainerInfo(tag);

		if (containerInfo != null) {

			

			// Check load complete?
			List<RF_Pallet_Check> notCompletePallets = tools.GetNotCompletePallet(containerInfo, tag.getReader_IP());

			if (notCompletePallets != null) {
				// There are some pallet is not complete.
				tools.SignalTower(gate, GlobleVar.RedOn, tag.getReader_IP());
				tools.Subtitle(gate, containerInfo.getContainer_ID() + "尚未裝櫃完成即離場", tag.getReader_IP());
				tools.SetGateError(tag, GlobleVar.LoadNotComplete, tools.ConvertGateStr(tag) + "，"
						+ tools.ConvertCarStr(tag) + containerInfo.getContainer_ID() + "，尚未裝櫃完成即離場");

				voiceText = "注意 注意 出貨" + tools.GetShipTo(containerInfo, tag.getReader_IP()) + " "
						+ containerInfo.getVendor_Name() + "運輸"
						+ tools.ConvertGateStr(containerInfo, tag.getReader_IP()) + " 尚未裝櫃完成即離場 請同仁查明";
				tools.VoiceSend(gate.getVoice_Path(), voiceText, tag.getReader_IP());
			} else {
				// All pallet are complete, car exit normally.
				containerInfo.setArea("WH");
				containerInfo.setGate("0");
				tools.UpdateContainerInfo(containerInfo, tag.getReader_IP());
				tools.SignalTowerAutoOff(gate, GlobleVar.GreenOn, 5, tag.getReader_IP());
				tools.Subtitle(gate, containerInfo.getContainer_ID() + "已離開碼頭", tag.getReader_IP());
			}

		} else {
			logger.debug(tag.getReader_IP() + " This gate is not binding any car.");
		}

		if (!currentAnt.ContainerID.equals("")) {
			// 已綁定貨櫃
			ContainerBase tmpContainerInfo = dis.GetContainerInfo(currentAnt, currentAnt.ContainerID);
			dis.UpdateContainerInfo(currentAnt, currentAnt.ContainerID, currentAnt.fab, "WH", "0",
					currentAnt.CarTypeFullName);
			logger.debug(currentAnt.ReaderIP + " Fab:" + currentAnt.fab + " Area:" + currentAnt.area + " Gate:"
					+ currentAnt.gateID + " 更新貨櫃資訊");
			TagBase emptyTag = new TagBase();
			dis.UpdateContainerID4Gate(currentAnt, emptyTag);
			logger.debug(currentAnt.ReaderIP + " Fab:" + currentAnt.fab + " Area:" + currentAnt.area + " Gate:"
					+ currentAnt.gateID + " 更新天線資訊");
			// 確認是否為未裝載完成

			String progressStr = dis.GetPalletProgress(currentAnt, Type.DeliveryLoad);
			logger.debug(currentAnt.ReaderIP + " Fab:" + currentAnt.fab + " Area:" + currentAnt.area + " Gate:"
					+ currentAnt.gateID + " progressStr:" + progressStr);
			String[] progress = progressStr.split("/");

			if (progress.length == 2 && !progress[0].equals("")) {
				logger.debug(currentAnt.ReaderIP + " Fab:" + currentAnt.fab + " Area:" + currentAnt.area + " Gate:"
						+ currentAnt.gateID + " progress:" + progress[0] + " / " + progress[1]);
				if (Integer.parseInt(progress[0]) < Integer.parseInt(progress[1])) {

					// 為了解除完異常後讓字幕機顯示用
					currentAnt.CaptionStr = " " + currentAnt.CarTypeDesc + currentAnt.ContainerID + "已離開碼頭";
					dis.StoreErrorPallet(Type.LoadNotComplete, currentAnt, Type.LoadNotComplete);
					String voicePath = currentAnt.settings.getString(Type.VoicePath);
					if (!voicePath.equals("")) {
						if (!voicePath.substring(voicePath.length() - 1).equals("/")) {
							voicePath += "/";
						}
						voicePath += Type.LoadNotComplete + System.currentTimeMillis() + ".txt";
						String Text = "";
						String Area = "";
						switch (currentAnt.area) {
						case "Delivery":
							Area = "出";
							break;
						case "Receive":
							Area = "收";
							break;
						default:
							Area = "未知";
						}

						Text = "注意 注意 出貨" + wms.GetShipTo(currentAnt, tmpContainerInfo.ID) + " "
								+ tmpContainerInfo.VendorName + "運輸" + Area + currentAnt.gateID + "碼頭尚未裝櫃完成即離場 請同仁查明";

						voice.Send(currentAnt, voicePath, Text);

						dis.StoreErrorMessage(currentAnt, Type.LoadNotComplete, Text);
						// if(CheckDelayList(currentAnt.fab+currentAnt.area+currentAnt.gateID+Type.LoadNotComplete,currentAnt)){
						// wms.Send2AMS(currentAnt.ReaderIP, currentAnt.fab,
						// "WIS", Type.DeliveryError, "WISNotComplete",
						// "出貨異常", Text);
						// }
					}

				} else {
					// 裝載已完成
					currentAnt.CaptionStr = " " + currentAnt.CarTypeDesc + currentAnt.ContainerID + "已離開碼頭";
					logger.debug(currentAnt.ReaderIP + " Fab:" + currentAnt.fab + " Area:" + currentAnt.area + " Gate:"
							+ currentAnt.gateID + " " + "Fab:" + currentAnt.fab + " Area:" + currentAnt.area + " Gate:"
							+ String.valueOf(currentAnt.gateID) + " CarType:" + tmpContainerInfo.CarType + " Reason:"
							+ tmpContainerInfo.Reason);
					String voicePath = currentAnt.settings.getString(Type.VoicePath);
					if (!voicePath.equals("")) {
						if (!voicePath.substring(voicePath.length() - 1).equals("/")) {
							voicePath += "/";
						}
						voicePath += Type.ContainerOut + System.currentTimeMillis() + ".txt";
						String Area = "";
						switch (currentAnt.area) {
						case "Delivery":
							Area = "出";
							break;
						case "Receive":
							Area = "收";
							break;
						default:
							Area = "未知";
						}
						String Text = "";

						try {

							if (tmpContainerInfo.CarType.equals(Type.TruckStr)) {
								if (tmpContainerInfo.Reason != null) {
									if (tmpContainerInfo.Reason.equals("Delivery")) {
										Text = "廠商" + tmpContainerInfo.Source + " " + tmpContainerInfo.VendorName
												+ "運輸 卸貨完成已離開" + Area + currentAnt.gateID + "碼頭";
									} else if (tmpContainerInfo.Reason.equals("Receive")) {
										Text = "出貨" + wms.GetShipTo(currentAnt, tmpContainerInfo.ID) + " "
												+ tmpContainerInfo.VendorName + "運輸裝櫃完成已離開" + Area + currentAnt.gateID
												+ "碼頭";
									}
								}
							} else if (tmpContainerInfo.CarType.equals(Type.ContainerStr)) {
								if (tmpContainerInfo.Status.equals("NonEmpty")) {
									Text = "出貨" + wms.GetShipTo(currentAnt, tmpContainerInfo.ID) + " "
											+ tmpContainerInfo.VendorName + "運輸 裝櫃完成已離開" + Area + currentAnt.gateID
											+ "碼頭";
								} else {
									Text = tmpContainerInfo.VendorName + "運輸卸櫃完成已離開" + Area + currentAnt.gateID + "碼頭";
								}
							}
						} catch (Exception e1) {
							logger.debug(currentAnt.ReaderIP + " Fab:" + currentAnt.fab + " Area:" + currentAnt.area
									+ " Gate:" + currentAnt.gateID + " Exception:" + tool.StackTrace2String(e1));
						}
						// }
						voice.Send(currentAnt, voicePath, Text);
					}
				}
			} else {
				// 未進行裝載作業
				currentAnt.CaptionStr = " " + currentAnt.CarTypeDesc + currentAnt.ContainerID + " 已離開碼頭";
				String voicePath = currentAnt.settings.getString(Type.VoicePath);
				if (!voicePath.equals("")) {
					if (!voicePath.substring(voicePath.length() - 1).equals("/")) {
						voicePath += "/";
					}
					voicePath += Type.ContainerOut + System.currentTimeMillis() + ".txt";
					String AreaText = "";
					switch (currentAnt.area) {
					case "Delivery":
						AreaText = "出";
						break;
					case "Receive":
						AreaText = "收";
						break;
					default:
						AreaText = "未知";
					}
					String Text = "廠商" + tmpContainerInfo.VendorName + "已離開" + AreaText + currentAnt.gateID + "碼頭";
					voice.Send(currentAnt, voicePath, Text);
				}
			}

			// 清除棧板列表
			dis.ClearPalletList(currentAnt);
			logger.debug(currentAnt.ReaderIP + " Fab:" + currentAnt.fab + " Area:" + currentAnt.area + " Gate:"
					+ currentAnt.gateID + " 清除棧板列表");
			if (dis.GetErrorPallet(currentAnt, Type.LoadNotComplete, Type.LoadNotComplete) != null) {
				// 未裝載完成，發警報
				currentAnt.CaptionErrorStr = " " + currentAnt.CarTypeDesc + currentAnt.ContainerID + "未裝載完成離開";

			} else {
				currentAnt.CaptionErrorStr = "";
			}
			currentAnt.ContainerID = "";
			currentAnt.CarType = "";
			currentAnt.VendorName = "";
			currentAnt.ProcessStart = false;

			// }

		} else {
			logger.debug(currentAnt.ReaderIP + " Fab:" + currentAnt.fab + " Area:" + currentAnt.area + " Gate:"
					+ currentAnt.gateID + " 此門還未綁定任何貨櫃");
			// 空碼頭時更新最後讀到地上的時間
			currentAnt.LastSeeMarkTagTime = System.currentTimeMillis();
		}
	}

}
