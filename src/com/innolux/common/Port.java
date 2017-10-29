package com.innolux.common;

import java.util.Hashtable;
import java.util.List;
import org.apache.log4j.Logger;

import com.innolux.model.RF_Antenna_Setting;
import com.innolux.model.RF_ContainerInfo;
import com.innolux.model.RF_Gate_Setting;
import com.innolux.model.RF_Pallet_Check;
import com.innolux.model.RF_Tag_History;
import com.innolux.model.RF_Tag_Mapping;

public class Port {
	public Logger logger = Logger.getLogger(this.getClass());
	private ToolUtility tools = new ToolUtility();

	public void Data(List<RF_Tag_History> tagList, Hashtable<Integer, RF_Antenna_Setting> antSetting) {
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

	private void AbnormalHandler(RF_Tag_History tag) {

	}

	private void DispatchHandler(RF_Tag_History tag) {

	}

	private void CenterParkingHandler(RF_Tag_History tag) {

	}

	private void CylinderHandler(RF_Tag_History tag) {

	}

	private void GateHandler(RF_Tag_History tag) {
		String Opreation = tools.GetOpreation_Mode(tag);

		RF_Gate_Setting gate = tools.GetGateSetting(tag);

		if (gate != null) {
			// Check forklift direction
			if (tag.getReceive_Time() - gate.geDirection_Time() <= GlobleVar.DirectionExpire) {
				// Check current wms opreation
				if (Opreation.equals("")) {
					tools.SignalTower(gate, GlobleVar.RedOn, tag.getReader_IP());
					tools.Subtitle(gate, "碼頭系統無作業，偵測到棧板" + tag.getTag_ID(), tag.getReader_IP());
					tools.SetGateError(tag, GlobleVar.NonOpreation,
							tools.ConvertGateStr(tag) + "系統無作業，偵測到棧板" + tag.getTag_ID());
				} else {

					switch (tag.getTag_Type()) {
					case GlobleVar.PalletTag:
						switch (Opreation) {
						case GlobleVar.ASNUnload:
							if (gate.getForkLift_Direction().equals(GlobleVar.ForkLiftIN)) {

							} else if (gate.getForkLift_Direction().equals(GlobleVar.ForkLiftOUT)) {

							} else {
								logger.debug(tag.getReader_IP() + " ForkLift_Direction is not define.");
							}
							break;
						case GlobleVar.EmptyWrapUnload:
							if (gate.getForkLift_Direction().equals(GlobleVar.ForkLiftIN)) {

							} else if (gate.getForkLift_Direction().equals(GlobleVar.ForkLiftOUT)) {

							} else {
								logger.debug(tag.getReader_IP() + " ForkLift_Direction is not define.");
							}
							break;
						case GlobleVar.TransferOut:
							if (gate.getForkLift_Direction().equals(GlobleVar.ForkLiftIN)) {

							} else if (gate.getForkLift_Direction().equals(GlobleVar.ForkLiftOUT)) {

							} else {
								logger.debug(tag.getReader_IP() + " ForkLift_Direction is not define.");
							}
							break;
						case GlobleVar.TransferIn:
							if (gate.getForkLift_Direction().equals(GlobleVar.ForkLiftIN)) {

							} else if (gate.getForkLift_Direction().equals(GlobleVar.ForkLiftOUT)) {

							} else {
								logger.debug(tag.getReader_IP() + " ForkLift_Direction is not define.");
							}
							break;
						case GlobleVar.DeliveryLoad:
							if (gate.getForkLift_Direction().equals(GlobleVar.ForkLiftIN)) {
								DeliveryLoad(tag);
							} else if (gate.getForkLift_Direction().equals(GlobleVar.ForkLiftOUT)) {

							} else {
								logger.debug(tag.getReader_IP() + " ForkLift_Direction is not define.");
							}
							break;
						default:

							break;
						}
						break;

					}
				}
			} else {
				logger.debug(tag.getReader_IP() + " Direction Expire");
				logger.debug(tag.getReader_IP() + " tagReceive_Time:" + tag.getReceive_Time() + "- gateDirection_Time:"
						+ gate.geDirection_Time() + " > DirectionExpire:" + GlobleVar.DirectionExpire);
			}
		} else {
			logger.debug(tag.getReader_IP() + " PortHandler : Fetch gate setting fail.");
		}
	}
	
	private void DeliveryLoad(RF_Tag_History tag) {
		
	}
	
	private void DeliveryUnLoad(RF_Tag_History tag) {
		
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
						logger.debug(tag.getReader_IP() + " PortHandler : Fetch gate setting fail.");
					}
				} else {
					logger.debug(tag.getReader_IP() + " PortHandler : This port was already binded car.");
				}
			} else {
				logger.debug(tag.getReader_IP() + " PortHandler : This tag is not in TagMapping table.");
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
			tools.Subtitle(gate, tools.ConvertCarStr(tag) + "進入:" + container.getContainer_ID(), tag.getReader_IP());

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
		RF_ContainerInfo container = tools.GetContainerInfo(tag);

		if (container != null) {

			// Check load complete?
			List<RF_Pallet_Check> notCompletePallets = tools.GetNotCompletePallet(container, tag.getReader_IP());

			if (notCompletePallets != null) {
				// There are some pallet is not complete.
				tools.SignalTower(gate, GlobleVar.RedOn, tag.getReader_IP());
				tools.Subtitle(gate, container.getContainer_ID() + "尚未裝櫃完成即離場", tag.getReader_IP());
				tools.SetGateError(tag, GlobleVar.LoadNotComplete, tools.ConvertGateStr(tag) + "，"
						+ tools.ConvertCarStr(tag) + container.getContainer_ID() + "，尚未裝櫃完成即離場");

				voiceText = "注意 注意 出貨" + tools.GetShipTo(container, tag.getReader_IP()) + " "
						+ container.getVendor_Name() + "運輸" + tools.ConvertGateStr(container, tag.getReader_IP())
						+ " 尚未裝櫃完成即離場 請同仁查明";
				tools.VoiceSend(gate.getVoice_Path(), voiceText, tag.getReader_IP());
			} else {
				// All pallet are complete, car exit normally.
				container.setArea("WH");
				container.setGate("0");
				tools.UpdateContainerInfo(container, tag.getReader_IP());
				tools.SignalTowerAutoOff(gate, GlobleVar.GreenOn, 5, tag.getReader_IP());
				tools.Subtitle(gate, container.getContainer_ID() + "已離開碼頭", tag.getReader_IP());
			}

		} else {
			logger.debug(tag.getReader_IP() + " This gate is not binding any car.");
		}

	}

}
