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
import com.innolux.model.WMS_T1_ASN_Pallet;
import com.innolux.model.WMS_T1_Check_Pallet;
import com.innolux.model.WMS_T2_Check_Pallet;
import com.innolux.service.TibcoRvSend;

public class Port {
	public Logger logger = Logger.getLogger(this.getClass());
	private ToolUtility tools = new ToolUtility();
	public static TibcoRvSend MesDaemon = new TibcoRvSend(GlobleVar.TibDaemon, "8585", "");

	public void Data(List<RF_Tag_History> tagList, Hashtable<Integer, RF_Antenna_Setting> antSetting) {
		try {

			for (RF_Tag_History tag : tagList) {

				if (tools.GetGateError(tag) != null) {
					// If the gate had error, do nothing.
					continue;
				}
				

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
			logger.error(tagList.get(0).getReader_IP() + " " + "Exception:" + ToolUtility.StackTrace2String(e));
		}
	}

	

	private void DispatchHandler(RF_Tag_History tag) {

	}

	private void CenterParkingHandler(RF_Tag_History tag) {

	}

	private void CylinderHandler(RF_Tag_History tag) {

	}
	
	private void AbnormalHandler(RF_Tag_History tag) {
		
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
							if (gate.getForkLift_Direction().equals(GlobleVar.ForkLiftOUT)) {
								ASNUnload(tag, gate);
							} else {
								logger.debug(tag.getReader_IP() + " ForkLift_Direction is not define.");
							}
							break;
						case GlobleVar.EmptyWrapUnload:
							if (gate.getForkLift_Direction().equals(GlobleVar.ForkLiftOUT)) {
								EmptyWrapUnload(tag, gate);
							} else {
								logger.debug(tag.getReader_IP() + " ForkLift_Direction is not define.");
							}
							break;
						case GlobleVar.TransferOut:
							if (gate.getForkLift_Direction().equals(GlobleVar.ForkLiftOUT)) {
								TransferOut(tag, gate);
							} else {
								logger.debug(tag.getReader_IP() + " ForkLift_Direction is not define.");
							}
							break;
						case GlobleVar.TransferIn:
							if (gate.getForkLift_Direction().equals(GlobleVar.ForkLiftIN)) {
								TransferIn(tag, gate);
							} else {
								logger.debug(tag.getReader_IP() + " ForkLift_Direction is not define.");
							}
							break;
						case GlobleVar.DeliveryLoad:

							DeliveryLoad(tag, gate);

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

	private void ASNUnload(RF_Tag_History tag, RF_Gate_Setting gate) {
		RF_ContainerInfo container = tools.GetContainerInfo(gate);
		if (container != null) {
			RF_Pallet_Check pallet = tools.GetMarkPallet(tag.getTag_ID(), GlobleVar.ASNUnload, tag.getReader_IP());
			if (pallet != null) {
				logger.debug(tag.getReader_IP() + " the tag is already send to wms.");

			} else {

				WMS_T1_ASN_Pallet ANS_Pallet = tools.GetASNPallet(tag, tag.getReader_IP());

				if (!ANS_Pallet.getRFID_Chk().equals("TRUE")) {

					tools.MarkPallet(tag, container.getContainer_ID(), GlobleVar.ASNUnload);
					tools.SignalTowerAutoOff(gate, GlobleVar.GreenOn, 5, tag.getReader_IP());
					tools.Subtitle(gate, "棧板" + tag.getTag_ID() + "ASN:", tag.getReader_IP());
					MesDaemon.sendMessage(tools.SendTransfer(tag, container, "In", tag.getReader_IP()),
							GlobleVar.SendToWMS);
				}
			}
		} else {
			// 此碼頭沒有綁定任何車輛
			tools.SignalTower(gate, GlobleVar.RedOn, tag.getReader_IP());
			tools.Subtitle(gate, "沒有綁定車輛，偵測到棧板" + tag.getTag_ID(), tag.getReader_IP());
			tools.SetGateError(tag, GlobleVar.TransferError,
					tools.ConvertGateStr(tag) + "沒有綁定車輛，偵測到棧板" + tag.getTag_ID());
		}

	}

	private void EmptyWrapUnload(RF_Tag_History tag, RF_Gate_Setting gate) {

		RF_ContainerInfo container = tools.GetContainerInfo(gate);
		if (container != null) {
			RF_Pallet_Check pallet = tools.GetMarkPallet(tag.getTag_ID(), GlobleVar.EmptyWrapUnload,
					tag.getReader_IP());
			if (pallet != null) {
				logger.debug(tag.getReader_IP() + " the tag is already send to wms.");

			} else {
				tools.MarkPallet(tag, container.getContainer_ID(), GlobleVar.EmptyWrapUnload);
				tools.SignalTowerAutoOff(gate, GlobleVar.GreenOn, 5, tag.getReader_IP());
				tools.Subtitle(gate, "棧板" + tag.getTag_ID() + "卸包材", tag.getReader_IP());
				MesDaemon.sendMessage(tools.SendTransfer(tag, container, "In", tag.getReader_IP()),
						GlobleVar.SendToWMS);
			}
		} else {
			// 此碼頭沒有綁定任何車輛
			tools.SignalTower(gate, GlobleVar.RedOn, tag.getReader_IP());
			tools.Subtitle(gate, "沒有綁定車輛，偵測到棧板" + tag.getTag_ID(), tag.getReader_IP());
			tools.SetGateError(tag, GlobleVar.TransferError,
					tools.ConvertGateStr(tag) + "沒有綁定車輛，偵測到棧板" + tag.getTag_ID());
		}
	}

	private void TransferIn(RF_Tag_History tag, RF_Gate_Setting gate) {
		RF_ContainerInfo container = tools.GetContainerInfo(gate);
		if (container != null) {
			RF_Pallet_Check pallet = tools.GetMarkPallet(tag.getTag_ID(), GlobleVar.TransferIn, tag.getReader_IP());
			if (pallet != null) {
				logger.debug(tag.getReader_IP() + " the tag is already send to wms.");

			} else {
				tools.MarkPallet(tag, container.getContainer_ID(), GlobleVar.TransferIn);
				tools.SignalTowerAutoOff(gate, GlobleVar.GreenOn, 5, tag.getReader_IP());
				tools.Subtitle(gate, "棧板" + tag.getTag_ID() + "廠移出", tag.getReader_IP());
				MesDaemon.sendMessage(tools.SendTransfer(tag, container, "In", tag.getReader_IP()),
						GlobleVar.SendToWMS);
			}

		} else {
			// 此碼頭沒有綁定任何車輛
			tools.SignalTower(gate, GlobleVar.RedOn, tag.getReader_IP());
			tools.Subtitle(gate, "沒有綁定車輛，偵測到棧板" + tag.getTag_ID(), tag.getReader_IP());
			tools.SetGateError(tag, GlobleVar.TransferError,
					tools.ConvertGateStr(tag) + "沒有綁定車輛，偵測到棧板" + tag.getTag_ID());
		}
	}

	private void TransferOut(RF_Tag_History tag, RF_Gate_Setting gate) {
		RF_ContainerInfo container = tools.GetContainerInfo(gate);
		if (container != null) {
			RF_Pallet_Check pallet = tools.GetMarkPallet(tag.getTag_ID(), GlobleVar.TransferOut, tag.getReader_IP());
			if (pallet != null) {
				logger.debug(tag.getReader_IP() + " the tag is already send to wms.");

			} else {
				tools.MarkPallet(tag, container.getContainer_ID(), GlobleVar.TransferOut);
				tools.SignalTowerAutoOff(gate, GlobleVar.GreenOn, 5, tag.getReader_IP());
				tools.Subtitle(gate, "棧板" + tag.getTag_ID() + "廠移出", tag.getReader_IP());
				MesDaemon.sendMessage(tools.SendTransfer(tag, container, "Out", tag.getReader_IP()),
						GlobleVar.SendToWMS);
			}

		} else {
			// 此碼頭沒有綁定任何車輛
			tools.SignalTower(gate, GlobleVar.RedOn, tag.getReader_IP());
			tools.Subtitle(gate, "沒有綁定車輛，偵測到棧板" + tag.getTag_ID(), tag.getReader_IP());
			tools.SetGateError(tag, GlobleVar.TransferError,
					tools.ConvertGateStr(tag) + "沒有綁定車輛，偵測到棧板" + tag.getTag_ID());
		}
	}

	private void DeliveryLoad(RF_Tag_History tag, RF_Gate_Setting gate) {
		RF_ContainerInfo container = tools.GetContainerInfo(gate);
		if (container != null) {
			// Check pallet in the container
			WMS_T1_Check_Pallet pallet = null;
			WMS_T1_Check_Pallet t1 = tools.GetT1Pallet(tag, tag.getReader_IP());
			WMS_T2_Check_Pallet t2 = tools.GetT2Pallet(tag, tag.getReader_IP());
			if (t1 != null) {
				pallet = t1;
			} else if (t2 != null) {
				pallet = new WMS_T1_Check_Pallet();
				pallet.setContainer_NO(t2.getContainer_NO());
				pallet.setDN_No(t2.getDN_No());
				pallet.setFab(t2.getFab());
				pallet.setPallet_ID(t2.getPallet_ID());
				pallet.setStatus(t2.getStatus());
				pallet.setTruck_NO(t2.getTruck_NO());
			}
			if (pallet != null) {
				// is pallet match to container
				if (pallet.getContainer_NO().equals(container.getContainer_ID())
						|| pallet.getTruck_NO().equals(container.getCar_ID())) {
					// If not in container
					if (pallet.getStatus().equals("TRUE")) {
						if (gate.getForkLift_Direction().equals(GlobleVar.ForkLiftIN)) {
							logger.debug(tag.getReader_IP() + " Pallet is already in container.");
						} else if (gate.getForkLift_Direction().equals(GlobleVar.ForkLiftOUT)) {
							tools.SignalTowerAutoOff(gate, GlobleVar.GreenOn, 5, tag.getReader_IP());
							tools.Subtitle(gate,
									"目前進度" + (tools.GetCompletePallet(container, tag.getReader_IP()).size() - 1) + "/"
											+ tools.GetAllPallet(container, tag.getReader_IP()).size(),
									tag.getReader_IP());
							MesDaemon.sendMessage(tools.SendDeliveryLoad(tag, container, "Cancel", tag.getReader_IP()),
									GlobleVar.SendToWMS);

						} else {
							logger.debug(tag.getReader_IP() + " ForkLift_Direction is not define.");
						}

					} else {
						if (gate.getForkLift_Direction().equals(GlobleVar.ForkLiftIN)) {

							tools.SignalTowerAutoOff(gate, GlobleVar.GreenOn, 5, tag.getReader_IP());
							tools.Subtitle(gate,
									"目前進度" + (tools.GetCompletePallet(container, tag.getReader_IP()).size() + 1) + "/"
											+ tools.GetAllPallet(container, tag.getReader_IP()).size(),
									tag.getReader_IP());
							MesDaemon.sendMessage(tools.SendDeliveryLoad(tag, container, "Confirm", tag.getReader_IP()),
									GlobleVar.SendToWMS);
						} else if (gate.getForkLift_Direction().equals(GlobleVar.ForkLiftOUT)) {
							logger.debug(tag.getReader_IP() + " Pallet is not in container.");

						} else {
							logger.debug(tag.getReader_IP() + " ForkLift_Direction is not define.");
						}

					}
				} else {
					// 棧板不屬於此貨櫃
					tools.SignalTower(gate, GlobleVar.RedOn, tag.getReader_IP());
					tools.Subtitle(gate, "棧板" + tag.getTag_ID() + "不屬於" + container.getContainer_ID(),
							tag.getReader_IP());
					tools.SetGateError(tag, GlobleVar.DeliveryError,
							tools.ConvertGateStr(tag) + "棧板" + tag.getTag_ID() + "不屬於" + container.getContainer_ID());
				}
			} else {
				// WMS資料庫中找不到此棧板
				tools.SignalTower(gate, GlobleVar.RedOn, tag.getReader_IP());
				tools.Subtitle(gate, "棧板" + tag.getTag_ID() + "不存在WMS資料庫", tag.getReader_IP());
				tools.SetGateError(tag, GlobleVar.DeliveryError,
						tools.ConvertGateStr(tag) + "出貨棧板" + tag.getTag_ID() + "不存在WMS資料庫");
			}
		} else {
			// 此碼頭沒有綁定任何車輛
			tools.SignalTower(gate, GlobleVar.RedOn, tag.getReader_IP());
			tools.Subtitle(gate, "沒有綁定車輛，偵測到棧板" + tag.getTag_ID(), tag.getReader_IP());
			tools.SetGateError(tag, GlobleVar.DeliveryError,
					tools.ConvertGateStr(tag) + "沒有綁定車輛，偵測到棧板" + tag.getTag_ID());
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
			tools.DeletePalletByCarID(container.getContainer_ID(), tag.getReader_IP());
			// Update container object
			tools.UpdateContainerInfo(container, tag.getReader_IP());
			// Clear all pallet by this car ID

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
		RF_ContainerInfo container = tools.GetContainerInfo(gate);

		if (container != null) {

			// Check load complete?
			List<String> notCompletePallets = tools.GetNotCompletePallet(container, tag.getReader_IP());

			if (notCompletePallets.size() != 0) {
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
				tools.DeletePalletByCarID(container.getContainer_ID(), tag.getReader_IP());
				tools.UpdateContainerInfo(container, tag.getReader_IP());
				tools.SignalTowerAutoOff(gate, GlobleVar.GreenOn, 5, tag.getReader_IP());
				tools.Subtitle(gate, container.getContainer_ID() + "已離開碼頭", tag.getReader_IP());
			}

		} else {
			logger.debug(tag.getReader_IP() + " This gate is not binding any car.");
		}

	}

}
