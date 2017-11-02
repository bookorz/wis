package com.innolux.common;

import java.util.Hashtable;
import java.util.List;
import org.apache.log4j.Logger;

import com.innolux.model.RF_Antenna_Setting;
import com.innolux.model.RF_ContainerInfo;
import com.innolux.model.RF_Error_Pallet;
import com.innolux.model.RF_Gate_Error;
import com.innolux.model.RF_Gate_Setting;
import com.innolux.model.RF_Pallet_Check;
import com.innolux.model.RF_Tag_History;
import com.innolux.model.RF_Tag_Mapping;
import com.innolux.model.WMS_T1_ASN_Pallet;
import com.innolux.model.WMS_T1_Check_Pallet;
import com.innolux.service.TibcoRvSend;

public class Port {
	public Logger logger = Logger.getLogger(this.getClass());

	public static TibcoRvSend MesDaemon = new TibcoRvSend(GlobleVar.TibDaemon, "8585", "");

	public void Data(List<RF_Tag_History> tagList, Hashtable<Integer, RF_Antenna_Setting> antSetting) {
		try {

			for (RF_Tag_History tag : tagList) {

				switch (tag.getAntenna_Type()) {
				case GlobleVar.ANT_Pallet:
					if (ToolUtility.GetGateError(tag.getFab(), tag.getArea(), tag.getGate(), GlobleVar.BindingError,
							tag.getReader_IP()) != null
							|| ToolUtility.GetGateError(tag.getFab(), tag.getArea(), tag.getGate(),
									GlobleVar.NonEntryRecord, tag.getReader_IP()) != null
							|| ToolUtility.GetGateError(tag.getFab(), tag.getArea(), tag.getGate(),
									GlobleVar.NonStatusError, tag.getReader_IP()) != null) {

						continue;
					} else {
						GateHandler(tag);
					}
					break;
				case GlobleVar.ANT_Container:
					if (ToolUtility.GetGateError(tag.getFab(), tag.getArea(), tag.getGate(), "",
							tag.getReader_IP()) != null) {
						// If the gate had any error, do nothing.
						continue;
					} else {
						PortHandler(tag);
					}
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
		List<RF_Error_Pallet> errorPalletList = ToolUtility.GetErrorPalletList(tag);
		if (errorPalletList != null) {
			// had error
			for (RF_Error_Pallet eachErrorPallet : errorPalletList) {
				ToolUtility.DeleteErrorPallet(eachErrorPallet, tag.getReader_IP());
				RF_ContainerInfo container = ToolUtility.GetContainerInfo(eachErrorPallet.getContainer_ID());
				RF_Gate_Setting gate = ToolUtility.GetGateSetting(container, tag.getReader_IP());
				String newErrorMsg = ToolUtility.GetErrorPalletSummary(container, eachErrorPallet.getOpreation_Mode(),
						tag.getReader_IP());
				if (newErrorMsg != "") {
					ToolUtility.SignalTower(gate, GlobleVar.RedOn, tag.getReader_IP());
					ToolUtility.Subtitle(gate, newErrorMsg, tag.getReader_IP());
				} else {
					// All error pallet is clear
					ToolUtility.DeleteGateError(tag);
					// Check gate error
					RF_Gate_Error gateError = ToolUtility.GetGateError(tag.getFab(), tag.getArea(), tag.getGate(), "",
							tag.getReader_IP());
					if (gateError == null) {
						// If gate error are not exist, reset gate information.
						ToolUtility.SignalTower(gate, GlobleVar.RedOff, tag.getReader_IP());
						ToolUtility.SignalTowerAutoOff(gate, GlobleVar.GreenOn, 5, tag.getReader_IP());
						ToolUtility.Subtitle(gate, ToolUtility.ConvertCarStr(container, tag.getReader_IP()) + "進入:"
								+ container.getContainer_ID(), tag.getReader_IP());

					} else {
						// If any gate error exist
						ToolUtility.SignalTower(gate, GlobleVar.RedOn, tag.getReader_IP());
						ToolUtility.Subtitle(gate, gateError.getError_Message(), tag.getReader_IP());
					}
				}

			}
		} else {
			// cancel pallet from container

			RF_Pallet_Check targetPallet = ToolUtility.GetMarkPallet(tag.getTag_ID(), "", GlobleVar.DeliveryLoad,
					tag.getTag_ID());
			if (targetPallet != null) {
				RF_ContainerInfo container = ToolUtility.GetContainerInfo(targetPallet.getContainer_ID());
				if (container != null) {
					RF_Gate_Setting gate = ToolUtility.GetGateSetting(container, tag.getReader_IP());
					if (gate != null) {
						RF_Tag_History pallet = new RF_Tag_History();
						pallet.setReader_IP(tag.getReader_IP());
						pallet.setFab(gate.getFab());
						pallet.setArea(gate.getArea());
						pallet.setGate(gate.getGate());
						pallet.setTag_ID(tag.getTag_ID());

						DeliveryUnLoad(pallet, gate);
					}
				}
			}
		}

	}

	private void GateHandler(RF_Tag_History tag) {
		String Opreation = ToolUtility.GetOpreation_Mode(tag);

		RF_Gate_Setting gate = ToolUtility.GetGateSetting(tag);

		if (gate != null) {
			// Check forklift direction
			if (tag.getReceive_Time() - gate.geDirection_Time() <= GlobleVar.DirectionExpire) {
				// Check current wms opreation
				if (Opreation.equals("")) {
					ToolUtility.SignalTower(gate, GlobleVar.RedOn, tag.getReader_IP());
					ToolUtility.Subtitle(gate, "碼頭系統無作業，偵測到棧板" + tag.getTag_ID(), tag.getReader_IP());
					ToolUtility.SetGateError(tag, GlobleVar.NonOpreation,
							ToolUtility.ConvertGateStr(tag) + "系統無作業，偵測到棧板" + tag.getTag_ID());
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

							if (gate.getForkLift_Direction().equals(GlobleVar.ForkLiftIN)) {
								DeliveryLoad(tag, gate);
							} else if (gate.getForkLift_Direction().equals(GlobleVar.ForkLiftOUT)) {
								DeliveryUnLoad(tag, gate);
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

	private void ASNUnload(RF_Tag_History tag, RF_Gate_Setting gate) {
		RF_ContainerInfo container = ToolUtility.GetContainerInfo(gate);
		String voiceText = "";
		if (container != null) {
			RF_Pallet_Check pallet = ToolUtility.GetMarkPallet(tag.getTag_ID(), container.getContainer_ID(),
					GlobleVar.ASNUnload, tag.getReader_IP());
			if (pallet != null) {
				logger.debug(tag.getReader_IP() + " the tag is already send to wms.");

			} else {

				WMS_T1_ASN_Pallet ANS_Pallet = ToolUtility.GetASNPallet(tag, tag.getReader_IP());
				if (ANS_Pallet == null) {
					ANS_Pallet = new WMS_T1_ASN_Pallet();
					ANS_Pallet.setASN_NO("N/A");
					ANS_Pallet.setPallet_ID(tag.getTag_ID());

					ToolUtility.SignalTower(gate, GlobleVar.RedOn, tag.getReader_IP());
					ToolUtility.Subtitle(gate, "廠商" + container.getSource() + " 收貨棧板:" + tag.getTag_ID() + " 未存在ASN資料庫",
							tag.getReader_IP());
					ToolUtility.SetGateError(tag, GlobleVar.ASNError, ToolUtility.ConvertGateStr(tag) + "廠商"
							+ container.getSource() + " 收貨棧板:" + tag.getTag_ID() + " 未存在ASN資料庫" + tag.getTag_ID());
					ToolUtility.SetErrorPallet(tag, container, GlobleVar.ASNUnload, GlobleVar.WMSNotFound);
					voiceText = "注意 注意 廠商" + container.getSource() + " " + container.getVendor_Name() + "運輸 棧板"
							+ ToolUtility.AddSpace(tag.getTag_ID()) + "未存在ASN資料庫 請同仁查明";
					ToolUtility.VoiceSend(gate.getVoice_Path(), voiceText, tag.getReader_IP());

					MesDaemon.sendMessage(ToolUtility.SendASNUnload(ANS_Pallet, container, "Error", tag.getReader_IP()),
							GlobleVar.SendToWMS);
				} else {
					if (!ANS_Pallet.getRFID_Chk().equals("TRUE")) {

						ToolUtility.MarkPallet(tag, container.getContainer_ID(), GlobleVar.ASNUnload);
						ToolUtility.SignalTowerAutoOff(gate, GlobleVar.GreenOn, 5, tag.getReader_IP());
						ToolUtility.Subtitle(gate, "棧板" + tag.getTag_ID() + "ASN:", tag.getReader_IP());
						MesDaemon.sendMessage(
								ToolUtility.SendASNUnload(ANS_Pallet, container, "Confirm", tag.getReader_IP()),
								GlobleVar.SendToWMS);
					}
				}
			}
		} else {
			// 此碼頭沒有綁定任何車輛
			ToolUtility.SignalTower(gate, GlobleVar.RedOn, tag.getReader_IP());
			ToolUtility.Subtitle(gate, "沒有綁定車輛，偵測到棧板" + tag.getTag_ID(), tag.getReader_IP());
			ToolUtility.SetGateError(tag, GlobleVar.TransferError,
					ToolUtility.ConvertGateStr(tag) + "沒有綁定車輛，偵測到棧板" + tag.getTag_ID());
		}

	}

	private void EmptyWrapUnload(RF_Tag_History tag, RF_Gate_Setting gate) {

		RF_ContainerInfo container = ToolUtility.GetContainerInfo(gate);
		if (container != null) {
			RF_Pallet_Check pallet = ToolUtility.GetMarkPallet(tag.getTag_ID(), container.getContainer_ID(),
					GlobleVar.EmptyWrapUnload, tag.getReader_IP());
			if (pallet != null) {
				logger.debug(tag.getReader_IP() + " the tag is already send to wms.");

			} else {
				ToolUtility.MarkPallet(tag, container.getContainer_ID(), GlobleVar.EmptyWrapUnload);
				ToolUtility.SignalTowerAutoOff(gate, GlobleVar.GreenOn, 5, tag.getReader_IP());
				ToolUtility.Subtitle(gate, "棧板" + tag.getTag_ID() + "卸包材", tag.getReader_IP());
				MesDaemon.sendMessage(ToolUtility.SendTransfer(tag, container, "In", tag.getReader_IP()),
						GlobleVar.SendToWMS);
			}
		} else {
			// 此碼頭沒有綁定任何車輛
			ToolUtility.SignalTower(gate, GlobleVar.RedOn, tag.getReader_IP());
			ToolUtility.Subtitle(gate, "沒有綁定車輛，偵測到棧板" + tag.getTag_ID(), tag.getReader_IP());
			ToolUtility.SetGateError(tag, GlobleVar.TransferError,
					ToolUtility.ConvertGateStr(tag) + "沒有綁定車輛，偵測到棧板" + tag.getTag_ID());
		}
	}

	private void TransferIn(RF_Tag_History tag, RF_Gate_Setting gate) {
		RF_ContainerInfo container = ToolUtility.GetContainerInfo(gate);
		if (container != null) {
			RF_Pallet_Check pallet = ToolUtility.GetMarkPallet(tag.getTag_ID(), container.getContainer_ID(),
					GlobleVar.TransferIn, tag.getReader_IP());
			if (pallet != null) {
				logger.debug(tag.getReader_IP() + " the tag is already send to wms.");

			} else {
				ToolUtility.MarkPallet(tag, container.getContainer_ID(), GlobleVar.TransferIn);
				ToolUtility.SignalTowerAutoOff(gate, GlobleVar.GreenOn, 5, tag.getReader_IP());
				ToolUtility.Subtitle(gate, "棧板" + tag.getTag_ID() + "廠移出", tag.getReader_IP());
				MesDaemon.sendMessage(ToolUtility.SendTransfer(tag, container, "In", tag.getReader_IP()),
						GlobleVar.SendToWMS);
			}

		} else {
			// 此碼頭沒有綁定任何車輛
			ToolUtility.SignalTower(gate, GlobleVar.RedOn, tag.getReader_IP());
			ToolUtility.Subtitle(gate, "沒有綁定車輛，偵測到棧板" + tag.getTag_ID(), tag.getReader_IP());
			ToolUtility.SetGateError(tag, GlobleVar.TransferError,
					ToolUtility.ConvertGateStr(tag) + "沒有綁定車輛，偵測到棧板" + tag.getTag_ID());
		}
	}

	private void TransferOut(RF_Tag_History tag, RF_Gate_Setting gate) {
		RF_ContainerInfo container = ToolUtility.GetContainerInfo(gate);
		if (container != null) {
			RF_Pallet_Check pallet = ToolUtility.GetMarkPallet(tag.getTag_ID(), container.getContainer_ID(),
					GlobleVar.TransferOut, tag.getReader_IP());
			if (pallet != null) {
				logger.debug(tag.getReader_IP() + " the tag is already send to wms.");

			} else {
				ToolUtility.MarkPallet(tag, container.getContainer_ID(), GlobleVar.TransferOut);
				ToolUtility.SignalTowerAutoOff(gate, GlobleVar.GreenOn, 5, tag.getReader_IP());
				ToolUtility.Subtitle(gate, "棧板" + tag.getTag_ID() + "廠移出", tag.getReader_IP());
				MesDaemon.sendMessage(ToolUtility.SendTransfer(tag, container, "Out", tag.getReader_IP()),
						GlobleVar.SendToWMS);
			}

		} else {
			// 此碼頭沒有綁定任何車輛
			ToolUtility.SignalTower(gate, GlobleVar.RedOn, tag.getReader_IP());
			ToolUtility.Subtitle(gate, "沒有綁定車輛，偵測到棧板" + tag.getTag_ID(), tag.getReader_IP());
			ToolUtility.SetGateError(tag, GlobleVar.TransferError,
					ToolUtility.ConvertGateStr(tag) + "沒有綁定車輛，偵測到棧板" + tag.getTag_ID());
		}
	}

	private void DeliveryLoad(RF_Tag_History tag, RF_Gate_Setting gate) {
		String voiceText = "";
		RF_ContainerInfo container = ToolUtility.GetContainerInfo(gate);
		if (container != null) {

			RF_Pallet_Check markPallet = ToolUtility.GetMarkPallet(tag.getTag_ID(), container.getContainer_ID(),
					GlobleVar.DeliveryLoad, tag.getReader_IP());
			if (markPallet != null) {
				logger.debug(tag.getReader_IP() + " the tag is already send to wms.");

			} else {

				// Check pallet in the container
				WMS_T1_Check_Pallet pallet = ToolUtility.GetPalletChk(tag);
				if (pallet != null) {
					// is pallet match to container
					if (pallet.getContainer_NO().equals(container.getContainer_ID())
							|| pallet.getTruck_NO().equals(container.getCar_ID())) {
						// If not in container
						if (pallet.getStatus().equals("TRUE")) {

							logger.debug(tag.getReader_IP() + " Pallet is already in container.");

						} else {
							ToolUtility.MarkPallet(tag, container.getContainer_ID(), GlobleVar.DeliveryLoad);
							int completeCount = ToolUtility.GetCompletePallet(container, tag.getReader_IP()).size() + 1;
							int allCount = ToolUtility.GetAllPallet(container, tag.getReader_IP()).size();
							if (completeCount == allCount) {
								// Delivery load complete.
								ToolUtility.SignalTowerAutoOff(gate, GlobleVar.OrangeOn, 5, tag.getReader_IP());
								ToolUtility.Subtitle(gate, ToolUtility.ConvertCarStr(container, tag.getReader_IP())
										+ container.getContainer_ID() + "已裝載完成", tag.getReader_IP());

								voiceText = "出貨" + ToolUtility.GetShipTo(container, tag.getReader_IP()) + " "
										+ container.getVendor_Name() + "運輸 "
										+ ToolUtility.ConvertGateStr(container, tag.getReader_IP()) + "已裝櫃完成";
								ToolUtility.VoiceSend(gate.getVoice_Path(), voiceText, tag.getReader_IP());
							} else {
								// Not complete yet.
								ToolUtility.SignalTowerAutoOff(gate, GlobleVar.GreenOn, 5, tag.getReader_IP());
								ToolUtility.Subtitle(gate, "目前進度"
										+ (ToolUtility.GetCompletePallet(container, tag.getReader_IP()).size() + 1)
										+ "/" + ToolUtility.GetAllPallet(container, tag.getReader_IP()).size(),
										tag.getReader_IP());
							}
							MesDaemon.sendMessage(
									ToolUtility.SendDeliveryLoad(tag, container, "Confirm", tag.getReader_IP()),
									GlobleVar.SendToWMS);

						}
					} else {
						// 棧板不屬於此貨櫃
						if (ToolUtility.GetErrorPallet(tag, container, GlobleVar.DeliveryLoad,
								GlobleVar.ContainerMismatch) == null) {
							ToolUtility.SetErrorPallet(tag, container, GlobleVar.DeliveryLoad,
									GlobleVar.ContainerMismatch);
							ToolUtility.SignalTower(gate, GlobleVar.RedOn, tag.getReader_IP());
							ToolUtility.Subtitle(gate, ToolUtility.GetErrorPalletSummary(container,
									GlobleVar.DeliveryLoad, tag.getReader_IP()), tag.getReader_IP());
							ToolUtility.SetGateError(tag, GlobleVar.DeliveryError, ToolUtility.ConvertGateStr(tag)
									+ "棧板" + tag.getTag_ID() + "不屬於" + container.getContainer_ID());

						}
					}
				} else {
					// WMS資料庫中找不到此棧板
					if (ToolUtility.GetErrorPallet(tag, container, GlobleVar.DeliveryLoad,
							GlobleVar.WMSNotFound) == null) {
						ToolUtility.SetErrorPallet(tag, container, GlobleVar.DeliveryLoad, GlobleVar.WMSNotFound);
						ToolUtility.SignalTower(gate, GlobleVar.RedOn, tag.getReader_IP());
						ToolUtility.Subtitle(gate, ToolUtility.GetErrorPalletSummary(container, GlobleVar.DeliveryLoad,
								tag.getReader_IP()), tag.getReader_IP());
						ToolUtility.SetGateError(tag, GlobleVar.DeliveryError,
								ToolUtility.ConvertGateStr(tag) + "出貨棧板" + tag.getTag_ID() + "不存在WMS資料庫");

						voiceText = "注意 注意 出貨" + ToolUtility.GetShipTo(container, tag.getReader_IP()) + " "
								+ container.getVendor_Name() + "運輸 "
								+ ToolUtility.ConvertGateStr(container, tag.getReader_IP()) + "棧板"
								+ ToolUtility.AddSpace(tag.getTag_ID()) + "未存在出貨資料庫 請同仁查明";
						ToolUtility.VoiceSend(gate.getVoice_Path(), voiceText, tag.getReader_IP());

					}
				}
			}
		} else {
			// 此碼頭沒有綁定任何車輛
			ToolUtility.SignalTower(gate, GlobleVar.RedOn, tag.getReader_IP());
			ToolUtility.Subtitle(gate, "沒有綁定車輛，偵測到棧板" + tag.getTag_ID(), tag.getReader_IP());
			ToolUtility.SetGateError(tag, GlobleVar.DeliveryError,
					ToolUtility.ConvertGateStr(tag) + "沒有綁定車輛，偵測到棧板" + tag.getTag_ID());
		}

	}

	private void DeliveryUnLoad(RF_Tag_History tag, RF_Gate_Setting gate) {
		String voiceText = "";
		RF_ContainerInfo container = ToolUtility.GetContainerInfo(gate);
		if (container != null) {
			// Check pallet in the container
			WMS_T1_Check_Pallet pallet = ToolUtility.GetPalletChk(tag);
			if (pallet != null) {
				// is pallet match to container
				if (pallet.getContainer_NO().equals(container.getContainer_ID())
						|| pallet.getTruck_NO().equals(container.getCar_ID())) {
					// If not in container
					if (pallet.getStatus().equals("TRUE")) {

						ToolUtility.SignalTowerAutoOff(gate, GlobleVar.GreenOn, 5, tag.getReader_IP());
						ToolUtility.Subtitle(gate,
								"目前進度" + (ToolUtility.GetCompletePallet(container, tag.getReader_IP()).size() - 1) + "/"
										+ ToolUtility.GetAllPallet(container, tag.getReader_IP()).size(),
								tag.getReader_IP());
						MesDaemon.sendMessage(
								ToolUtility.SendDeliveryLoad(tag, container, "Cancel", tag.getReader_IP()),
								GlobleVar.SendToWMS);

					} else {

						logger.debug(tag.getReader_IP() + " Pallet is not in container.");

					}
				} else {
					// 棧板不屬於此貨櫃
					// ToolUtility.SignalTower(gate, GlobleVar.RedOn, tag.getReader_IP());
					// ToolUtility.Subtitle(gate, "棧板" + tag.getTag_ID() + "不屬於" +
					// container.getContainer_ID(),
					// tag.getReader_IP());
					// ToolUtility.SetGateError(tag, GlobleVar.DeliveryError,
					// ToolUtility.ConvertGateStr(tag) + "棧板" + tag.getTag_ID() + "不屬於" +
					// container.getContainer_ID());
				}
			} else {
				// WMS資料庫中找不到此棧板
				// ToolUtility.SignalTower(gate, GlobleVar.RedOn, tag.getReader_IP());
				// ToolUtility.Subtitle(gate, "棧板" + tag.getTag_ID() + "不存在WMS資料庫",
				// tag.getReader_IP());
				// ToolUtility.SetGateError(tag, GlobleVar.DeliveryError,
				// ToolUtility.ConvertGateStr(tag) + "出貨棧板" + tag.getTag_ID() + "不存在WMS資料庫");
				//
				// voiceText = "注意 注意 出貨" + ToolUtility.GetShipTo(container, tag.getReader_IP())
				// + " "
				// + container.getVendor_Name() + "運輸 " + ToolUtility.ConvertGateStr(container,
				// tag.getReader_IP())
				// + "棧板" + ToolUtility.AddSpace(tag.getTag_ID()) + "未存在出貨資料庫 請同仁查明";
				// ToolUtility.VoiceSend(gate.getVoice_Path(), voiceText, tag.getReader_IP());
			}
		} else {
			// 此碼頭沒有綁定任何車輛
			ToolUtility.SignalTower(gate, GlobleVar.RedOn, tag.getReader_IP());
			ToolUtility.Subtitle(gate, "沒有綁定車輛，偵測到棧板" + tag.getTag_ID(), tag.getReader_IP());
			ToolUtility.SetGateError(tag, GlobleVar.DeliveryError,
					ToolUtility.ConvertGateStr(tag) + "沒有綁定車輛，偵測到棧板" + tag.getTag_ID());
		}
	}

	private void PortHandler(RF_Tag_History tag) {
		// ClsContainer Container = null;

		switch (tag.getTag_Type()) {
		case GlobleVar.TruckTag:
		case GlobleVar.ContainerTag:
			RF_Tag_Mapping tagMapping = ToolUtility.GetTagMapping(tag);
			if (tagMapping != null) {
				tag.setTag_ID(tagMapping.getReal_id());

				if (ToolUtility.CheckPortBinding(tag)) {
					// get gateSetting object
					RF_Gate_Setting gate = ToolUtility.GetGateSetting(tag);
					if (gate != null) {
						gate.setLast_ContainerTag_Time(System.currentTimeMillis());
						if (tag.getReceive_Time() - gate.getLast_MarkTag_Time() > GlobleVar.TagMaskInTime) {
							PortBind(gate, tag);

						} else {
							logger.error(tag.getReader_IP() + " PortHandler error: gate setting not exist.");
						}
						// update gateSetting object
						ToolUtility.SetGateSetting(gate, tag.getReader_IP());
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

			if (!ToolUtility.CheckManualMode(tag)) {
				RF_Gate_Setting gate = ToolUtility.GetGateSetting(tag);
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
		RF_ContainerInfo container = ToolUtility.GetContainerInfo(tag);

		if (container == null) {

			ToolUtility.SignalTower(gate, GlobleVar.RedOn, tag.getReader_IP());
			ToolUtility.Subtitle(gate, "該車輛沒有進廠紀錄", tag.getReader_IP());
			ToolUtility.SetGateError(tag, GlobleVar.NonEntryRecord, ToolUtility.ConvertGateStr(tag) + "，"
					+ ToolUtility.ConvertCarStr(tag) + tag.getTag_ID() + "沒有進廠紀錄");
		} else if (!container.getGate().equals("0")) {
			ToolUtility.SignalTower(gate, GlobleVar.RedOn, tag.getReader_IP());
			ToolUtility.Subtitle(gate, "靠碼頭失敗，該車輛目前被綁定在" + ToolUtility.ConvertGateStr(container, tag.getReader_IP()),
					tag.getReader_IP());
			ToolUtility.SetGateError(tag, GlobleVar.BindingError, tag.getTag_ID() + "無法綁定於"
					+ ToolUtility.ConvertGateStr(tag) + "，因目前被綁定在" + ToolUtility.ConvertGateStr(tag));
		} else {
			// Bind start
			container.setFab(tag.getFab());
			container.setArea(tag.getArea());
			container.setGate(tag.getGate());
			container.setManual_Bind(false);
			ToolUtility.DeletePalletByCarID(container.getContainer_ID(), tag.getReader_IP());
			ToolUtility.ClearErrorPallet(container, tag.getReader_IP());
			// Update container object
			ToolUtility.UpdateContainerInfo(container, tag.getReader_IP());
			// Clear all pallet by this car ID

			ToolUtility.SignalTowerAutoOff(gate, GlobleVar.GreenOn, 5, tag.getReader_IP());
			ToolUtility.Subtitle(gate, ToolUtility.ConvertCarStr(tag) + "進入:" + container.getContainer_ID(),
					tag.getReader_IP());

			if (container.getCar_Type().equals(GlobleVar.TruckStr)) {
				if (container.getReason() != null) {
					if (container.getReason().equals("Delivery")) {
						voiceText = "廠商" + container.getSource() + " " + container.getVendor_Name() + "運輸已停靠"
								+ ToolUtility.ConvertGateStr(container, tag.getReader_IP()) + " 請同仁收貨";
					} else if (container.getReason().equals("Receive")) {
						voiceText = container.getVendor_Name() + "運輸已停靠"
								+ ToolUtility.ConvertGateStr(container, tag.getReader_IP()) + " 請同仁出貨";
					}
				}
			} else if (container.getCar_Type().equals(GlobleVar.ContainerStr)) {
				if (container.getContainer_Status().equals("NonEmpty")) {
					voiceText = container.getVendor_Name() + "運輸重櫃已停靠"
							+ ToolUtility.ConvertGateStr(container, tag.getReader_IP()) + " 請同仁卸貨";
				} else {
					voiceText = container.getVendor_Name() + "運輸空櫃已停靠"
							+ ToolUtility.ConvertGateStr(container, tag.getReader_IP()) + " 請同仁出貨";
				}
			}
			ToolUtility.VoiceSend(gate.getVoice_Path(), voiceText, tag.getReader_IP());

			// Binded

		}

	}

	public void PortUnBind(RF_Gate_Setting gate, RF_Tag_History tag) {
		String voiceText = "";
		RF_ContainerInfo container = ToolUtility.GetContainerInfo(gate);

		if (container != null) {

			// Check load complete?
			List<String> notCompletePallets = ToolUtility.GetNotCompletePallet(container, tag.getReader_IP());

			if (notCompletePallets.size() != 0) {
				// There are some pallet is not complete.
				ToolUtility.SignalTower(gate, GlobleVar.RedOn, tag.getReader_IP());
				ToolUtility.Subtitle(gate, container.getContainer_ID() + "尚未裝櫃完成即離場", tag.getReader_IP());
				ToolUtility.SetGateError(tag, GlobleVar.LoadNotComplete, ToolUtility.ConvertGateStr(tag) + "，"
						+ ToolUtility.ConvertCarStr(tag) + container.getContainer_ID() + "，尚未裝櫃完成即離場");

				voiceText = "注意 注意 出貨" + ToolUtility.GetShipTo(container, tag.getReader_IP()) + " "
						+ container.getVendor_Name() + "運輸" + ToolUtility.ConvertGateStr(container, tag.getReader_IP())
						+ " 尚未裝櫃完成即離場 請同仁查明";
				ToolUtility.VoiceSend(gate.getVoice_Path(), voiceText, tag.getReader_IP());
			} else {
				// All pallet are complete, car exit normally.
				container.setArea("WH");
				container.setGate("0");
				ToolUtility.DeletePalletByCarID(container.getContainer_ID(), tag.getReader_IP());
				ToolUtility.ClearErrorPallet(container, tag.getReader_IP());
				ToolUtility.UpdateContainerInfo(container, tag.getReader_IP());
				ToolUtility.SignalTowerAutoOff(gate, GlobleVar.GreenOn, 5, tag.getReader_IP());
				ToolUtility.Subtitle(gate, container.getContainer_ID() + "已離開碼頭", tag.getReader_IP());

				if (container.getCar_Type().equals(GlobleVar.TruckStr)) {
					if (container.getReason() != null) {
						if (container.getReason().equals("Delivery")) {
							voiceText = "廠商" + container.getSource() + " " + container.getVendor_Name() + "運輸 卸貨完成已離開"
									+ ToolUtility.ConvertGateStr(container, tag.getReader_IP());
						} else if (container.getReason().equals("Receive")) {
							voiceText = "出貨" + ToolUtility.GetShipTo(container, tag.getReader_IP()) + " "
									+ container.getVendor_Name() + "運輸裝櫃完成已離開"
									+ ToolUtility.ConvertGateStr(container, tag.getReader_IP());
						}
					}
				} else if (container.getCar_Type().equals(GlobleVar.ContainerStr)) {
					if (container.getContainer_Status().equals("NonEmpty")) {
						voiceText = "出貨" + ToolUtility.GetShipTo(container, tag.getReader_IP()) + " "
								+ container.getVendor_Name() + "運輸 裝櫃完成已離開"
								+ ToolUtility.ConvertGateStr(container, tag.getReader_IP());
					} else {
						voiceText = container.getVendor_Name() + "運輸卸櫃完成已離開"
								+ ToolUtility.ConvertGateStr(container, tag.getReader_IP());
					}
				}

			}

		} else {
			logger.debug(tag.getReader_IP() + " This gate is not binding any car.");
		}

	}

}
