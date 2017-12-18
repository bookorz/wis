package com.innolux.common;

import java.util.List;
import org.apache.log4j.Logger;

import com.innolux.model.RF_ContainerInfo;
import com.innolux.model.RF_Cylinder_Status;
import com.innolux.model.RF_Error_Pallet;
import com.innolux.model.RF_Gate_Error;
import com.innolux.model.RF_Gate_Setting;
import com.innolux.model.RF_Pallet_Check;
import com.innolux.model.RF_Tag_History;
import com.innolux.model.WMS_T1_ASN_Pallet;
import com.innolux.model.WMS_T1_Check_Pallet;

public class TagHandle {
	public static Logger logger = Logger.getLogger(TagHandle.class);

	public static void Data(List<RF_Tag_History> tagList) {

		try {

			for (RF_Tag_History tag : tagList) {
				logger.info(tag.getReader_IP() + " Tag:"+tag);
				long startTime = System.currentTimeMillis();
				switch (tag.getAntenna_Type()) {
				case GlobleVar.ANT_Pallet:
					if (ToolUtility.GetGateError(tag.getFab(), tag.getArea(), tag.getGate(), GlobleVar.BindingError,
							tag.getReader_IP()) != null) {
						logger.info(tag.getReader_IP() + " Error exist, skip handle:" + GlobleVar.BindingError);
						continue;
					} else if (ToolUtility.GetGateError(tag.getFab(), tag.getArea(), tag.getGate(),
							GlobleVar.NonEntryRecord, tag.getReader_IP()) != null) {
						logger.info(tag.getReader_IP() + " Error exist, skip handle:" + GlobleVar.NonEntryRecord);
						continue;
						// } else if (ToolUtility.GetGateError(tag.getFab(), tag.getArea(),
						// tag.getGate(),
						// GlobleVar.NonStatusError, tag.getReader_IP()) != null) {
						// logger.info(tag.getReader_IP() + " Error exist, skip handle:" +
						// GlobleVar.NonStatusError);
						// continue;
					} else {
						if (tag.getTag_Type().equals(GlobleVar.PalletTag)) {
							GateHandler(tag);
						}
					}
					break;
				case GlobleVar.ANT_Container:
					RF_Gate_Error gateErr = ToolUtility.GetGateError(tag.getFab(), tag.getArea(), tag.getGate(), "",
							tag.getReader_IP());
					if (gateErr != null) {
						// If the gate had any error, do nothing.
						logger.info(tag.getReader_IP() + " Error exist, skip handle:" + gateErr.getError_Type() + " - "
								+ gateErr.getError_Message());
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

				logger.debug(tag.getReader_IP() + " Tag process time:" + (System.currentTimeMillis() - startTime));
			}

		} catch (Exception e) {
			logger.error(tagList.get(0).getReader_IP() + " " + "Exception:" + ToolUtility.StackTrace2String(e));
		}

	}

	private static void CylinderHandler(RF_Tag_History tag) {

		if (!tag.getTag_Type().equals(GlobleVar.PalletTag)) {
			return;
		}

		RF_Cylinder_Status cylinder = ToolUtility.GetCylinder(tag.getTag_ID(), tag.getReader_IP());
		if (cylinder != null) {

			switch (tag.getAntenna_Type()) {
			case GlobleVar.ANT_Big_Stock:
				// Only from big use can trigger.
				if (cylinder.getPosition().equals(GlobleVar.ANT_Big_Use)) {

					if (cylinder.getNew_Position().equals(GlobleVar.ANT_Big_Stock)) {
						if (cylinder.getCheck_Times() == 2) {
							cylinder.setPosition(GlobleVar.ANT_Big_Stock);
							cylinder.setNew_Position("");
							cylinder.setCheck_Times(0);
							cylinder.setStatus(GlobleVar.Cylinder_Empty);
							ToolUtility.MesDaemon.sendMessage(
									MessageFormat.SendCylinderStatus(cylinder, tag.getReader_IP()),
									GlobleVar.SendToWMS);

						} else {
							cylinder.setCheck_Times(cylinder.getCheck_Times() + 1);
						}
					} else {
						cylinder.setNew_Position(GlobleVar.ANT_Big_Stock);
						cylinder.setCheck_Times(1);
					}
					ToolUtility.SetCylinderHistory(cylinder, tag.getReader_IP());
				}

				break;
			case GlobleVar.ANT_Big_Use:

				// Only from big stock can trigger.
				if (cylinder.getPosition().equals(GlobleVar.ANT_Big_Stock)) {

					if (cylinder.getNew_Position().equals(GlobleVar.ANT_Big_Use)) {
						if (cylinder.getCheck_Times() == 2) {
							cylinder.setPosition(GlobleVar.ANT_Big_Use);
							cylinder.setNew_Position("");
							cylinder.setCheck_Times(0);
							cylinder.setStatus(GlobleVar.Cylinder_Used);
							ToolUtility.MesDaemon.sendMessage(
									MessageFormat.SendCylinderStatus(cylinder, tag.getReader_IP()),
									GlobleVar.SendToWMS);
						} else {
							cylinder.setCheck_Times(cylinder.getCheck_Times() + 1);
						}
					} else {
						cylinder.setNew_Position(GlobleVar.ANT_Big_Use);
						cylinder.setCheck_Times(1);
					}
					ToolUtility.SetCylinderHistory(cylinder, tag.getReader_IP());
				}

				break;
			case GlobleVar.ANT_Small_Stock:
				// Only from small stock can trigger.
				if (cylinder.getPosition().equals(GlobleVar.ANT_Small_Use)) {
					if (cylinder.getNew_Position().equals(GlobleVar.ANT_Small_Stock)) {
						if (cylinder.getCheck_Times() == 2) {
							cylinder.setPosition(GlobleVar.ANT_Small_Stock);
							cylinder.setNew_Position("");
							cylinder.setCheck_Times(0);
							cylinder.setStatus(GlobleVar.Cylinder_Empty);
							ToolUtility.MesDaemon.sendMessage(
									MessageFormat.SendCylinderStatus(cylinder, tag.getReader_IP()),
									GlobleVar.SendToWMS);
						} else {
							cylinder.setCheck_Times(cylinder.getCheck_Times() + 1);
						}
					} else {
						cylinder.setNew_Position(GlobleVar.ANT_Small_Stock);
						cylinder.setCheck_Times(1);
					}
					ToolUtility.SetCylinderHistory(cylinder, tag.getReader_IP());
				}
				break;
			case GlobleVar.ANT_Small_Use:
				// Only from small stock can trigger.
				if (cylinder.getPosition().equals(GlobleVar.ANT_Small_Stock)) {

					if (cylinder.getNew_Position().equals(GlobleVar.ANT_Small_Use)) {
						if (cylinder.getCheck_Times() == 2) {
							cylinder.setPosition(GlobleVar.ANT_Small_Use);
							cylinder.setNew_Position("");
							cylinder.setCheck_Times(0);
							cylinder.setStatus(GlobleVar.Cylinder_Used);
							ToolUtility.MesDaemon.sendMessage(
									MessageFormat.SendCylinderStatus(cylinder, tag.getReader_IP()),
									GlobleVar.SendToWMS);
						} else {
							cylinder.setCheck_Times(cylinder.getCheck_Times() + 1);
						}
					} else {
						cylinder.setNew_Position(GlobleVar.ANT_Small_Use);
						cylinder.setCheck_Times(1);
					}
					ToolUtility.SetCylinderHistory(cylinder, tag.getReader_IP());
				}

				break;
			}

		} else {
			// new tag found
			if (tag.getTag_Type().equals(GlobleVar.PalletTag)) {
				cylinder = new RF_Cylinder_Status();
				cylinder.setFab(tag.getFab());
				cylinder.setArea(tag.getArea());
				cylinder.setTag_ID(tag.getTag_ID());
				cylinder.setPosition(tag.getAntenna_Type());
				cylinder.setNew_Position("");
				switch (tag.getAntenna_Type()) {
				case GlobleVar.ANT_Big_Stock:
					cylinder.setStatus(GlobleVar.Cylinder_Create);
					break;
				case GlobleVar.ANT_Big_Use:
					cylinder.setStatus(GlobleVar.Cylinder_Used);
					break;
				case GlobleVar.ANT_Small_Stock:
					cylinder.setStatus(GlobleVar.Cylinder_Create);
					break;
				case GlobleVar.ANT_Small_Use:
					cylinder.setStatus(GlobleVar.Cylinder_Used);
					break;
				}
				ToolUtility.MesDaemon.sendMessage(MessageFormat.SendCylinderStatus(cylinder, tag.getReader_IP()),
						GlobleVar.SendToWMS);
				ToolUtility.SetCylinderHistory(cylinder, tag.getReader_IP());
			}
		}
		cylinder.setUpdateTime(System.currentTimeMillis());
		ToolUtility.SetCylinder(cylinder, tag.getReader_IP());
	}

	private static void CenterParkingHandler(RF_Tag_History tag) {
		RF_ContainerInfo container = ToolUtility.GetContainerInfo(tag);
		if (container.getGate().equals("0")) {
			if (container != null) {

				switch (tag.getAntenna_Type()) {
				case GlobleVar.ANT_In_CenterParking:
					container.setFab("T2");
					container.setArea("CP");
					container.setGate("0");
					ToolUtility.UpdateContainerInfo(container, tag.getReader_IP());
					break;
				case GlobleVar.ANT_Out_CenterParking:
					container.setFab("T2");
					container.setArea("SB");
					container.setGate("0");
					ToolUtility.UpdateContainerInfo(container, tag.getReader_IP());
					break;

				}
			}
		} else {
			logger.info(tag.getReader_IP() + " Change location fail: This container is binding for "
					+ ToolUtility.ConvertGateStr(tag));
		}
	}

	private static void DispatchHandler(RF_Tag_History tag) {

		RF_ContainerInfo container = ToolUtility.GetContainerInfo(tag);
		if (container.getGate().equals("0")) {
			if (container != null) {

				switch (tag.getAntenna_Type()) {
				case GlobleVar.ANT_T1_Dispatch:
					container.setFab("T1");
					container.setArea("SB");
					container.setGate("0");
					ToolUtility.UpdateContainerInfo(container, tag.getReader_IP());
					break;
				case GlobleVar.ANT_T2_Dispatch:
					container.setFab("T2");
					container.setArea("SB");
					container.setGate("0");
					ToolUtility.UpdateContainerInfo(container, tag.getReader_IP());
					break;

				}
			}
		} else {
			logger.info(tag.getReader_IP() + " Change location fail: This container is binding for "
					+ ToolUtility.ConvertGateStr(tag));
		}
	}

	private static void AbnormalHandler(RF_Tag_History tag) {
		List<RF_Error_Pallet> errorPalletList = ToolUtility.GetErrorPalletList(tag);
		if (errorPalletList != null) {
			// had error
			logger.info(tag.getReader_IP() + " This pallet is in errorPalletList.");
			for (RF_Error_Pallet eachErrorPallet : errorPalletList) {
				ToolUtility.DeleteErrorPallet(eachErrorPallet, tag.getReader_IP());

				RF_Gate_Setting gate = ToolUtility.GetGateSetting(eachErrorPallet.getFab(), eachErrorPallet.getArea(),
						eachErrorPallet.getGate(), tag.getReader_IP());
				String newErrorMsg = ToolUtility.GetErrorPalletSummary(eachErrorPallet.getFab(),
						eachErrorPallet.getArea(), eachErrorPallet.getGate(), eachErrorPallet.getOpreation_Mode(),
						tag.getReader_IP());
				if (newErrorMsg != "") {
					logger.info(tag.getReader_IP() + " There are error pallets in errorPalletList.");

					ToolUtility.Subtitle(gate.getFab(), gate.getArea(), gate.getGate(), newErrorMsg,
							tag.getReader_IP());

					ToolUtility.SignalTowerAutoOff(gate.getFab(), gate.getArea(), gate.getGate(), GlobleVar.GreenOn,
							5000, tag.getReader_IP());
					ToolUtility.SignalTower(gate.getFab(), gate.getArea(), gate.getGate(), GlobleVar.RedOn,
							tag.getReader_IP());
				} else {
					logger.info(tag.getReader_IP() + " All error pallet is clear.");
					// All error pallet is clear
					// Delete DeliveryError
					if (eachErrorPallet.getOpreation_Mode().equals(GlobleVar.DeliveryLoad)) {
						ToolUtility.DeleteGateError(tag.getFab(), tag.getArea(), tag.getGate(), GlobleVar.DeliveryError,
								tag.getReader_IP());
					} else if (eachErrorPallet.getOpreation_Mode().equals(GlobleVar.ASNUnload)) {
						ToolUtility.DeleteGateError(tag.getFab(), tag.getArea(), tag.getGate(), GlobleVar.ASNError,
								tag.getReader_IP());
					}

					// Gate error are not exist, reset gate information.
					RF_ContainerInfo container = ToolUtility.GetContainerInfo(gate.getFab(), gate.getArea(),
							gate.getGate(), tag.getReader_IP());
					ToolUtility.Subtitle(gate.getFab(), gate.getArea(), gate.getGate(),
							ToolUtility.InitSubtitleStr(container, tag.getReader_IP()), tag.getReader_IP());
					ToolUtility.SignalTower(gate.getFab(), gate.getArea(), gate.getGate(), GlobleVar.RedOff,
							tag.getReader_IP());

				}

			}
		}
		// cancel pallet from container
		logger.info(tag.getReader_IP() + " Cancel pallet from container.");
		RF_Pallet_Check targetPallet = ToolUtility.GetMarkPallet(tag.getTag_ID(), "", GlobleVar.DeliveryLoad, true,
				tag.getReader_IP());
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
					logger.info(tag.getReader_IP() + " Cancel pallet " + tag.getTag_ID() + ".");
					DeliveryUnLoad(pallet, gate);
				} else {
					logger.info(tag.getReader_IP() + " Gate is not found.");
				}
			} else {
				logger.info(tag.getReader_IP() + " " + ToolUtility.ConvertGateStr(tag) + " is not binding container.");
			}
		} else {
			logger.info(tag.getReader_IP() + " This pallet " + tag.getTag_ID() + " is not in container.");
		}

	}

	private static void GateHandler(RF_Tag_History tag) {
		String Opreation = "";

		RF_Gate_Setting gate = ToolUtility.GetGateSetting(tag.getFab(), tag.getArea(), tag.getGate(),
				tag.getReader_IP());

		if (gate != null) {

			Opreation = ToolUtility.GetOperation_Mode(gate.getFab(), gate.getArea(), gate.getGate(),
					tag.getReader_IP());

			// Check forklift direction (Only T2)
			if (tag.getFab().equals("T2")) {
				if (tag.getReceive_Time() - gate.getDirection_ReportTime() >= GlobleVar.DirectionExpire) {
					logger.info(tag.getReader_IP() + " Direction Expire");
					logger.info(tag.getReader_IP() + " tagReceive_Time:" + tag.getReceive_Time()
							+ "- getDirection_ReportTime:" + gate.getDirection_ReportTime() + " > DirectionExpire:"
							+ GlobleVar.DirectionExpire);
					return;
				}
			}
			// Check current wms opreation
			logger.info(tag.getReader_IP() + " Check current wms opreation.");
			if (Opreation.equals("")) {

				if (gate.getForkLift_Direction().equals(GlobleVar.ForkLiftOUT)) {
					logger.info(tag.getReader_IP() + " NoOperation:" + GlobleVar.ForkLiftOUT);
					ToolUtility.Subtitle(gate.getFab(), gate.getArea(), gate.getGate(),
							"碼頭系統無作業，偵測到棧板" + tag.getTag_ID(), tag.getReader_IP());
					ToolUtility.SignalTower(gate.getFab(), gate.getArea(), gate.getGate(), GlobleVar.RedOn,
							tag.getReader_IP());
					ToolUtility.SetGateError(tag, GlobleVar.NonOpreation,
							ToolUtility.ConvertGateStr(tag) + "系統無作業，偵測到棧板" + tag.getTag_ID());
				} else if (gate.getForkLift_Direction().equals(GlobleVar.ForkLiftIN)) {
					logger.info(tag.getReader_IP() + " NoOperation:" + GlobleVar.ForkLiftIN);
					ToolUtility.Subtitle(gate.getFab(), gate.getArea(), gate.getGate(),
							"碼頭系統無作業，偵測到棧板" + tag.getTag_ID(), tag.getReader_IP());
					ToolUtility.SignalTower(gate.getFab(), gate.getArea(), gate.getGate(), GlobleVar.RedOn,
							tag.getReader_IP());
					ToolUtility.SetGateError(tag, GlobleVar.NonOpreation,
							ToolUtility.ConvertGateStr(tag) + "系統無作業，偵測到棧板" + tag.getTag_ID());
				}else if (gate.getForkLift_Direction().equals(GlobleVar.ForkLiftAll)) {
					logger.info(tag.getReader_IP() + " NoOperation:" + GlobleVar.ForkLiftAll);
					ToolUtility.Subtitle(gate.getFab(), gate.getArea(), gate.getGate(),
							"碼頭系統無作業，偵測到棧板" + tag.getTag_ID(), tag.getReader_IP());
					ToolUtility.SignalTower(gate.getFab(), gate.getArea(), gate.getGate(), GlobleVar.RedOn,
							tag.getReader_IP());
					ToolUtility.SetGateError(tag, GlobleVar.NonOpreation,
							ToolUtility.ConvertGateStr(tag) + "系統無作業，偵測到棧板" + tag.getTag_ID());
				} else {
					logger.info(tag.getReader_IP() + " ForkLift_Direction is not define.");
				}

			} else {
				logger.info(tag.getReader_IP() + " Current wms opreation:" + Opreation);
				switch (tag.getTag_Type()) {
				case GlobleVar.PalletTag:
					switch (Opreation) {
					case GlobleVar.ASNUnload:
						if (gate.getFab().equals("T1")) {
							if (gate.getForkLift_Direction().equals(GlobleVar.ForkLiftAll)) {
								ASNUnload(tag, gate);
							} else {
								logger.info(tag.getReader_IP() + " ForkLift_Direction is not define.");
							}
						} else if (gate.getFab().equals("T2")) {
							if (gate.getForkLift_Direction().equals(GlobleVar.ForkLiftIN)) {
								ASNUnload(tag, gate);
							} else {
								logger.info(tag.getReader_IP() + " ForkLift_Direction is not define.");
							}
						}
						break;
					case GlobleVar.EmptyWrapUnload:
						if (gate.getFab().equals("T1")) {
							if (gate.getForkLift_Direction().equals(GlobleVar.ForkLiftAll)) {
								EmptyWrapUnload(tag, gate);
							} else {
								logger.info(tag.getReader_IP() + " ForkLift_Direction is not define.");
							}
						} else if (gate.getFab().equals("T2")) {
							if (gate.getForkLift_Direction().equals(GlobleVar.ForkLiftIN)) {
								EmptyWrapUnload(tag, gate);
							} else {
								logger.info(tag.getReader_IP() + " ForkLift_Direction is not define.");
							}
						}
						break;
					case GlobleVar.TransferOut:
						if (gate.getFab().equals("T1")) {
							if (gate.getForkLift_Direction().equals(GlobleVar.ForkLiftAll)) {
								TransferOut(tag, gate);
							} else {
								logger.info(tag.getReader_IP() + " ForkLift_Direction is not define.");
							}
						} else if (gate.getFab().equals("T2")) {
							if (gate.getForkLift_Direction().equals(GlobleVar.ForkLiftOUT)) {
								TransferOut(tag, gate);
							} else {
								logger.info(tag.getReader_IP() + " ForkLift_Direction is not define.");
							}
						}
						break;
					case GlobleVar.TransferIn:
						if (gate.getFab().equals("T1")) {
							if (gate.getForkLift_Direction().equals(GlobleVar.ForkLiftAll)) {
								TransferIn(tag, gate);
							} else {
								logger.info(tag.getReader_IP() + " ForkLift_Direction is not define.");
							}
						} else if (gate.getFab().equals("T2")) {
							if (gate.getForkLift_Direction().equals(GlobleVar.ForkLiftIN)) {
								TransferIn(tag, gate);
							} else {
								logger.info(tag.getReader_IP() + " ForkLift_Direction is not define.");
							}
						}
						break;
					case GlobleVar.DeliveryLoad:
						if (gate.getFab().equals("T1")) {
							if (gate.getForkLift_Direction().equals(GlobleVar.ForkLiftAll)) {
								logger.info(tag.getReader_IP() + " Delivery Load");
								DeliveryLoad(tag, gate);
							} else {
								logger.info(tag.getReader_IP() + " ForkLift_Direction is not define.");
							}
						} else if (gate.getFab().equals("T2")) {
							if (gate.getForkLift_Direction().equals(GlobleVar.ForkLiftOUT)) {
								logger.info(tag.getReader_IP() + " Delivery Load");
								DeliveryLoad(tag, gate);
							} else if (gate.getForkLift_Direction().equals(GlobleVar.ForkLiftIN)) {
								logger.info(tag.getReader_IP() + " Delivery UnLoad");
								DeliveryUnLoad(tag, gate);
							} else {
								logger.info(tag.getReader_IP() + " ForkLift_Direction is not define.");
							}
						}
						break;
					default:

						break;
					}
					break;

				}
			}

		} else {
			logger.info(tag.getReader_IP() + " PortHandler : Fetch gate setting fail.");
		}
	}

	private static void ASNUnload(RF_Tag_History tag, RF_Gate_Setting gate) {
		RF_ContainerInfo container = ToolUtility.GetContainerInfo(tag.getFab(), tag.getArea(), tag.getGate(),
				tag.getReader_IP());
		String voiceText = "";
		if (container != null) {
			RF_Pallet_Check pallet = ToolUtility.GetMarkPallet(tag.getTag_ID(), container.getContainer_ID(),
					GlobleVar.ASNUnload, false, tag.getReader_IP());
			if (pallet != null) {
				logger.debug(tag.getReader_IP() + " the tag is already send to wms.");

			} else {

				WMS_T1_ASN_Pallet ANS_Pallet = ToolUtility.GetASNPallet(tag);
				// test
				// if(ANS_Pallet==null) {
				// ANS_Pallet = new WMS_T1_ASN_Pallet();
				//
				// ANS_Pallet.setASN_NO("BOOK1234");
				// ANS_Pallet.setCar_NO("115522");
				// ANS_Pallet.setPallet_ID("NA539");
				// ANS_Pallet.setRFID_Chk("");
				// }
				// test
				if (ANS_Pallet == null) {

					ANS_Pallet = new WMS_T1_ASN_Pallet();
					ANS_Pallet.setASN_NO("N/A");
					ANS_Pallet.setPallet_ID(tag.getTag_ID());

					ToolUtility.SetErrorPallet(tag, GlobleVar.ASNUnload, GlobleVar.WMSNotFound);
					ToolUtility.Subtitle(gate.getFab(), gate.getArea(), gate.getGate(),
							"廠商" + container.getSource() + " 收貨棧板:" + tag.getTag_ID() + " 未存在ASN資料庫",
							tag.getReader_IP());
					ToolUtility.SignalTower(gate.getFab(), gate.getArea(), gate.getGate(), GlobleVar.RedOn,
							tag.getReader_IP());
					ToolUtility.SetGateError(tag, GlobleVar.ASNError,
							ToolUtility.ConvertGateStr(tag) + "廠商" + container.getSource() + " 收貨棧板:" + tag.getTag_ID()
									+ " 未存在ASN資料庫" + ToolUtility.GetErrorPalletSummary(gate.getFab(), gate.getArea(),
											gate.getGate(), GlobleVar.ASNUnload, tag.getReader_IP()));

					voiceText = "注意 注意 廠商" + container.getSource() + " 由" + container.getVendor_Name() + "運輸 棧板"
							+ ToolUtility.AddSpace(tag.getTag_ID()) + "未存在ASN資料庫 請同仁查明";
					ToolUtility.VoiceSend(gate.getVoice_Path(), voiceText, tag.getReader_IP());

					ToolUtility.MesDaemon.sendMessage(
							MessageFormat.SendASNUnload(ANS_Pallet, container, "Error", tag.getReader_IP()),
							GlobleVar.SendToWMS);
				} else {
					ToolUtility.MarkPallet(tag, container.getContainer_ID(), GlobleVar.ASNUnload, false);
					if (!ANS_Pallet.getRFID_Chk().equals("Y")) {

						logger.info(tag.getReader_IP() + " Pallet " + tag.getTag_ID() + " " + GlobleVar.ASNUnload);

						ToolUtility.Subtitle(gate.getFab(), gate.getArea(), gate.getGate(),
								"棧板" + ANS_Pallet.getPallet_ID() + " ASN:" + ANS_Pallet.getASN_NO(),
								tag.getReader_IP());
						ToolUtility.SignalTowerAutoOff(gate.getFab(), gate.getArea(), gate.getGate(), GlobleVar.GreenOn,
								5000, tag.getReader_IP());
						ToolUtility.MesDaemon.sendMessage(
								MessageFormat.SendASNUnload(ANS_Pallet, container, "Confirm", tag.getReader_IP()),
								GlobleVar.SendToWMS);

					} else {
						logger.debug(tag.getReader_IP() + " the tag is already send to wms, RFID_Chk is "
								+ ANS_Pallet.getRFID_Chk() + ".");
					}
				}
			}
		} else {
			// 此碼頭沒有綁定任何車輛
			logger.info(tag.getReader_IP() + "This gate is not bonding any car.");

			ToolUtility.Subtitle(gate.getFab(), gate.getArea(), gate.getGate(), "沒有綁定車輛，偵測到棧板" + tag.getTag_ID(),
					tag.getReader_IP());
			ToolUtility.SignalTower(gate.getFab(), gate.getArea(), gate.getGate(), GlobleVar.RedOn, tag.getReader_IP());
			ToolUtility.SetGateError(tag, GlobleVar.ASNUnload,
					ToolUtility.ConvertGateStr(tag) + "沒有綁定車輛，偵測到棧板" + tag.getTag_ID());
		}

	}

	private static void EmptyWrapUnload(RF_Tag_History tag, RF_Gate_Setting gate) {

		RF_ContainerInfo container = ToolUtility.GetContainerInfo(tag.getFab(), tag.getArea(), tag.getGate(),
				tag.getReader_IP());
		if (container != null) {
			RF_Pallet_Check pallet = ToolUtility.GetMarkPallet(tag.getTag_ID(), container.getContainer_ID(),
					GlobleVar.EmptyWrapUnload, false, tag.getReader_IP());
			if (pallet != null) {
				logger.debug(tag.getReader_IP() + " the tag is already send to wms.");

			} else {
				ToolUtility.MarkPallet(tag, container.getContainer_ID(), GlobleVar.EmptyWrapUnload, false);
				logger.info(tag.getReader_IP() + " Pallet " + tag.getTag_ID() + " " + GlobleVar.EmptyWrapUnload);

				ToolUtility.Subtitle(gate.getFab(), gate.getArea(), gate.getGate(), "棧板" + tag.getTag_ID() + "卸包材",
						tag.getReader_IP());
				ToolUtility.SignalTowerAutoOff(gate.getFab(), gate.getArea(), gate.getGate(), GlobleVar.GreenOn, 5000,
						tag.getReader_IP());
				ToolUtility.MesDaemon.sendMessage(MessageFormat.SendEmptyWrapUnload(tag, container, tag.getReader_IP()),
						GlobleVar.SendToWMS);
			}
		} else {
			// 此碼頭沒有綁定任何車輛
			logger.info(tag.getReader_IP() + "This gate is not bonding any car.");

			ToolUtility.Subtitle(gate.getFab(), gate.getArea(), gate.getGate(), "沒有綁定車輛，偵測到棧板" + tag.getTag_ID(),
					tag.getReader_IP());
			ToolUtility.SignalTower(gate.getFab(), gate.getArea(), gate.getGate(), GlobleVar.RedOn, tag.getReader_IP());
			ToolUtility.SetGateError(tag, GlobleVar.TransferError,
					ToolUtility.ConvertGateStr(tag) + "沒有綁定車輛，偵測到棧板" + tag.getTag_ID());
		}
	}

	private static void TransferIn(RF_Tag_History tag, RF_Gate_Setting gate) {
		RF_ContainerInfo container = ToolUtility.GetContainerInfo(tag.getFab(), tag.getArea(), tag.getGate(),
				tag.getReader_IP());
		if (container != null) {
			RF_Pallet_Check pallet = ToolUtility.GetMarkPallet(tag.getTag_ID(), container.getContainer_ID(),
					GlobleVar.TransferIn, true, tag.getReader_IP());
			if (pallet != null) {
				logger.debug(tag.getReader_IP() + " the tag is already send to wms.");

			} else {
				ToolUtility.MarkPallet(tag, container.getContainer_ID(), GlobleVar.TransferIn, true);
				logger.info(tag.getReader_IP() + " Pallet " + tag.getTag_ID() + " " + GlobleVar.TransferIn);

				ToolUtility.Subtitle(gate.getFab(), gate.getArea(), gate.getGate(), "棧板" + tag.getTag_ID() + "廠移入",
						tag.getReader_IP());
				ToolUtility.SignalTowerAutoOff(gate.getFab(), gate.getArea(), gate.getGate(), GlobleVar.GreenOn, 5000,
						tag.getReader_IP());
				ToolUtility.MesDaemon.sendMessage(MessageFormat.SendTransfer(tag, container, "In", tag.getReader_IP()),
						GlobleVar.SendToWMS);
			}

		} else {
			// 此碼頭沒有綁定任何車輛
			logger.info(tag.getReader_IP() + "This gate is not bonding any car.");

			ToolUtility.Subtitle(gate.getFab(), gate.getArea(), gate.getGate(), "沒有綁定車輛，偵測到棧板" + tag.getTag_ID(),
					tag.getReader_IP());
			ToolUtility.SignalTower(gate.getFab(), gate.getArea(), gate.getGate(), GlobleVar.RedOn, tag.getReader_IP());
			ToolUtility.SetGateError(tag, GlobleVar.TransferError,
					ToolUtility.ConvertGateStr(tag) + "沒有綁定車輛，偵測到棧板" + tag.getTag_ID());
		}
	}

	private static void TransferOut(RF_Tag_History tag, RF_Gate_Setting gate) {
		RF_ContainerInfo container = ToolUtility.GetContainerInfo(tag.getFab(), tag.getArea(), tag.getGate(),
				tag.getReader_IP());
		if (container != null) {
			RF_Pallet_Check pallet = ToolUtility.GetMarkPallet(tag.getTag_ID(), container.getContainer_ID(),
					GlobleVar.TransferOut, false, tag.getReader_IP());
			if (pallet != null) {
				logger.debug(tag.getReader_IP() + " the tag is already send to wms.");

			} else {
				ToolUtility.MarkPallet(tag, container.getContainer_ID(), GlobleVar.TransferOut, false);
				logger.info(tag.getReader_IP() + " Pallet " + tag.getTag_ID() + " " + GlobleVar.TransferOut);

				ToolUtility.Subtitle(gate.getFab(), gate.getArea(), gate.getGate(), "棧板" + tag.getTag_ID() + "廠移出",
						tag.getReader_IP());
				ToolUtility.SignalTowerAutoOff(gate.getFab(), gate.getArea(), gate.getGate(), GlobleVar.GreenOn, 5000,
						tag.getReader_IP());
				ToolUtility.MesDaemon.sendMessage(MessageFormat.SendTransfer(tag, container, "Out", tag.getReader_IP()),
						GlobleVar.SendToWMS);
			}

		} else {
			// 此碼頭沒有綁定任何車輛
			logger.info(tag.getReader_IP() + "This gate is not bonding any car.");

			ToolUtility.Subtitle(gate.getFab(), gate.getArea(), gate.getGate(), "沒有綁定車輛，偵測到棧板" + tag.getTag_ID(),
					tag.getReader_IP());
			ToolUtility.SignalTower(gate.getFab(), gate.getArea(), gate.getGate(), GlobleVar.RedOn, tag.getReader_IP());
			ToolUtility.SetGateError(tag, GlobleVar.TransferError,
					ToolUtility.ConvertGateStr(tag) + "沒有綁定車輛，偵測到棧板" + tag.getTag_ID());
		}
	}

	private static void DeliveryLoad(RF_Tag_History tag, RF_Gate_Setting gate) {
		String voiceText = "";
		RF_ContainerInfo container = ToolUtility.GetContainerInfo(tag.getFab(), tag.getArea(), tag.getGate(),
				tag.getReader_IP());
		if (container != null) {

			RF_Pallet_Check markPallet = ToolUtility.GetMarkPallet(tag.getTag_ID(), container.getContainer_ID(),
					GlobleVar.DeliveryLoad, true, tag.getReader_IP());
			if (markPallet != null) {
				logger.info(tag.getReader_IP() + " the tag " + tag.getTag_ID() + " is already send to wms.");

			} else {

				// Check pallet in the container
				logger.info(tag.getReader_IP() + " Check container " + container.getContainer_ID() + "pallet list.");
				WMS_T1_Check_Pallet pallet = ToolUtility.GetPalletChk(tag);
				if (pallet != null) {
					// is pallet match to container

					if (pallet.getContainer_NO().equals(container.getContainer_ID())
							|| pallet.getTruck_NO().equals(container.getCar_ID())) {
						logger.info(tag.getReader_IP() + " Pallet " + tag.getTag_ID() + " is in list.");
						ToolUtility.MarkPallet(tag, container.getContainer_ID(), GlobleVar.DeliveryLoad, true);
						// If not in container
						if (pallet.getStatus().equals("TRUE")) {

							logger.info(
									tag.getReader_IP() + " Pallet " + tag.getTag_ID() + " is already in container.");

						} else {

							logger.info(tag.getReader_IP() + " Pallet " + tag.getTag_ID() + " is not in container.");
							int completeCount = ToolUtility.GetCompletePallet(container, tag.getReader_IP()).size() + 1;
							int allCount = ToolUtility.GetAllPallet(container, tag.getReader_IP()).size();
							if (completeCount == allCount) {
								// Delivery load complete.

								ToolUtility.Subtitle(gate.getFab(), gate.getArea(), gate.getGate(),
										ToolUtility.ConvertCarStr(container, tag.getReader_IP())
												+ container.getContainer_ID() + "已裝載完成",
										tag.getReader_IP());
								ToolUtility.SignalTowerAutoOff(gate.getFab(), gate.getArea(), gate.getGate(),
										GlobleVar.OrangeOn, 5000, tag.getReader_IP());
								voiceText = "出貨" + ToolUtility.GetShipTo(container, tag.getReader_IP()) + " 由"
										+ container.getVendor_Name() + "運輸 "
										+ ToolUtility.ConvertGateStr(container, tag.getReader_IP()) + "已裝櫃完成";
								ToolUtility.VoiceSend(gate.getVoice_Path(), voiceText, tag.getReader_IP());
							} else {
								// Not complete yet.

								ToolUtility.Subtitle(gate.getFab(), gate.getArea(), gate.getGate(), "目前進度"
										+ (ToolUtility.GetCompletePallet(container, tag.getReader_IP()).size() + 1)
										+ "/" + ToolUtility.GetAllPallet(container, tag.getReader_IP()).size(),
										tag.getReader_IP());
								ToolUtility.SignalTowerAutoOff(gate.getFab(), gate.getArea(), gate.getGate(),
										GlobleVar.GreenOn, 5000, tag.getReader_IP());
							}
							ToolUtility.MesDaemon.sendMessage(
									MessageFormat.SendDeliveryLoad(tag, pallet, "Confirm", tag.getReader_IP()),
									GlobleVar.SendToWMS);

						}
					} else {
						// 棧板不屬於此貨櫃
						logger.info(
								tag.getReader_IP() + " Pallet " + tag.getTag_ID() + " is not match to this container.");
						if (ToolUtility.GetErrorPallet(tag, GlobleVar.DeliveryLoad,
								GlobleVar.ContainerMismatch) == null) {
							ToolUtility.SetErrorPallet(tag, GlobleVar.DeliveryLoad, GlobleVar.ContainerMismatch);

							String summary = ToolUtility.GetErrorPalletSummary(gate.getFab(), gate.getArea(),
									gate.getGate(), GlobleVar.DeliveryLoad, tag.getReader_IP());
							ToolUtility.Subtitle(gate.getFab(), gate.getArea(), gate.getGate(), summary,
									tag.getReader_IP());
							ToolUtility.SignalTower(gate.getFab(), gate.getArea(), gate.getGate(), GlobleVar.RedOn,
									tag.getReader_IP());
							ToolUtility.SetGateError(tag, GlobleVar.DeliveryError,
									ToolUtility.ConvertGateStr(tag) + summary);

						}
					}
				} else {
					// WMS資料庫中找不到此棧板
					logger.info(tag.getReader_IP() + " Pallet " + tag.getTag_ID() + " is not in wms database.");
					if (ToolUtility.GetErrorPallet(tag, GlobleVar.DeliveryLoad, GlobleVar.WMSNotFound) == null) {
						ToolUtility.SetErrorPallet(tag, GlobleVar.DeliveryLoad, GlobleVar.WMSNotFound);

						String summary = ToolUtility.GetErrorPalletSummary(gate.getFab(), gate.getArea(),
								gate.getGate(), GlobleVar.DeliveryLoad, tag.getReader_IP());
						ToolUtility.Subtitle(gate.getFab(), gate.getArea(), gate.getGate(), summary,
								tag.getReader_IP());
						ToolUtility.SignalTower(gate.getFab(), gate.getArea(), gate.getGate(), GlobleVar.RedOn,
								tag.getReader_IP());
						ToolUtility.SetGateError(tag, GlobleVar.DeliveryError,
								ToolUtility.ConvertGateStr(tag) + summary);

						voiceText = "注意 注意 出貨" + ToolUtility.GetShipTo(container, tag.getReader_IP()) + " 由"
								+ container.getVendor_Name() + "運輸  "
								+ ToolUtility.ConvertGateStr(container, tag.getReader_IP()) + " 棧板"
								+ ToolUtility.AddSpace(tag.getTag_ID()) + "未存在出貨資料庫 請同仁查明";
						ToolUtility.VoiceSend(gate.getVoice_Path(), voiceText, tag.getReader_IP());

					}
				}
			}
		} else {
			// 此碼頭沒有綁定任何車輛
			logger.info(tag.getReader_IP() + "This gate is not bonding any car.");

			ToolUtility.Subtitle(gate.getFab(), gate.getArea(), gate.getGate(), "沒有綁定車輛，偵測到棧板" + tag.getTag_ID(),
					tag.getReader_IP());
			ToolUtility.SignalTower(gate.getFab(), gate.getArea(), gate.getGate(), GlobleVar.RedOn, tag.getReader_IP());
			ToolUtility.SetGateError(tag, GlobleVar.DeliveryError,
					ToolUtility.ConvertGateStr(tag) + "沒有綁定車輛，偵測到棧板" + tag.getTag_ID());
		}

	}

	private static void DeliveryUnLoad(RF_Tag_History tag, RF_Gate_Setting gate) {
		RF_Error_Pallet errorPalle = ToolUtility.GetErrorPallet(tag, GlobleVar.DeliveryLoad, "");
		if (errorPalle != null) {
			// had error
			logger.info(tag.getReader_IP() + " This pallet is error pallet.");

			ToolUtility.DeleteErrorPallet(errorPalle, tag.getReader_IP());

			String newErrorMsg = ToolUtility.GetErrorPalletSummary(gate.getFab(), gate.getArea(), gate.getGate(),
					GlobleVar.DeliveryLoad, tag.getReader_IP());
			if (newErrorMsg != "") {
				logger.info(tag.getReader_IP() + " Another error pallet exist.");

				ToolUtility.Subtitle(gate.getFab(), gate.getArea(), gate.getGate(), newErrorMsg, tag.getReader_IP());
				ToolUtility.SignalTowerAutoOff(gate.getFab(), gate.getArea(), gate.getGate(), GlobleVar.GreenOn, 5000,
						tag.getReader_IP());
				ToolUtility.SignalTower(gate.getFab(), gate.getArea(), gate.getGate(), GlobleVar.RedOn,
						tag.getReader_IP());
			} else {
				// All error pallet is clear
				logger.info(tag.getReader_IP() + "All error pallet is clear.");
				// Delete DeliveryError
				ToolUtility.DeleteGateError(tag.getFab(), tag.getArea(), tag.getGate(), GlobleVar.DeliveryError,
						tag.getReader_IP());

				// If gate error are not exist, reset gate information.
				logger.info(tag.getReader_IP() + "All gate error is clear, reset gate information.");
				RF_ContainerInfo container = ToolUtility.GetContainerInfo(tag.getFab(), tag.getArea(), tag.getGate(),
						tag.getReader_IP());

				ToolUtility
						.Subtitle(gate.getFab(), gate.getArea(), gate.getGate(),
								"目前進度" + (ToolUtility.GetCompletePallet(container, tag.getReader_IP()).size()) + "/"
										+ ToolUtility.GetAllPallet(container, tag.getReader_IP()).size(),
								tag.getReader_IP());
				ToolUtility.SignalTower(gate.getFab(), gate.getArea(), gate.getGate(), GlobleVar.RedOff,
						tag.getReader_IP());
				ToolUtility.SignalTowerAutoOff(gate.getFab(), gate.getArea(), gate.getGate(), GlobleVar.GreenOn, 5000,
						tag.getReader_IP());
			}

		} else {
			// String voiceText = "";
			RF_ContainerInfo container = ToolUtility.GetContainerInfo(tag.getFab(), tag.getArea(), tag.getGate(),
					tag.getReader_IP());
			if (container != null) {
				logger.info(tag.getReader_IP() + "Check this pallet status.");
				RF_Pallet_Check markPallet = ToolUtility.GetMarkPallet(tag.getTag_ID(), container.getContainer_ID(),
						GlobleVar.DeliveryLoad, false, tag.getReader_IP());
				if (markPallet != null) {
					logger.info(tag.getReader_IP() + " the tag " + tag.getTag_ID() + " is already send to wms.");

				} else {
					ToolUtility.MarkPallet(tag, container.getContainer_ID(), GlobleVar.DeliveryLoad, false);

					// Check pallet in the container
					WMS_T1_Check_Pallet pallet = ToolUtility.GetPalletChk(tag);
					if (pallet != null) {

						// is pallet match to container
						if (pallet.getContainer_NO().equals(container.getContainer_ID())
								|| pallet.getTruck_NO().equals(container.getCar_ID())) {
							logger.info(tag.getReader_IP() + "Pallet " + pallet.getPallet_ID()
									+ " is found by wms database.");
							// If not in container
							// if (true) {//for test
							if (pallet.getStatus().equals("TRUE")) {
								logger.info(
										tag.getReader_IP() + "Pallet " + pallet + " is not yet enter into container.");

								ToolUtility.Subtitle(gate.getFab(), gate.getArea(), gate.getGate(), "目前進度"
										+ (ToolUtility.GetCompletePallet(container, tag.getReader_IP()).size() - 1)
										+ "/" + ToolUtility.GetAllPallet(container, tag.getReader_IP()).size(),
										tag.getReader_IP());
								ToolUtility.SignalTowerAutoOff(gate.getFab(), gate.getArea(), gate.getGate(),
										GlobleVar.GreenOn, 5000, tag.getReader_IP());

								ToolUtility.MesDaemon.sendMessage(
										MessageFormat.SendDeliveryLoad(tag, pallet, "Cancel", tag.getReader_IP()),
										GlobleVar.SendToWMS);

							} else {

								logger.debug(tag.getReader_IP() + " Pallet is not in container. ");

							}
						} else {
							logger.info(tag.getReader_IP() + "Pallet " + pallet.getPallet_ID()
									+ " is not match to this container.");
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
						logger.info(
								tag.getReader_IP() + "Pallet " + tag.getTag_ID() + " is not exist in wms database.");
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
				}
			} else {
				// 此碼頭沒有綁定任何車輛
				logger.info(tag.getReader_IP() + "This gate is not bonding any car.");

				// ToolUtility.Subtitle(gate.getFab(), gate.getArea(), gate.getGate(),
				// "沒有綁定車輛，偵測到棧板" + tag.getTag_ID(),
				// tag.getReader_IP());
				// ToolUtility.SignalTower(gate.getFab(), gate.getArea(), gate.getGate(),
				// GlobleVar.RedOn,
				// tag.getReader_IP());
				// ToolUtility.SetGateError(tag, GlobleVar.DeliveryError,
				// ToolUtility.ConvertGateStr(tag) + "沒有綁定車輛，偵測到棧板" + tag.getTag_ID());
			}
		}
	}

	private static void PortHandler(RF_Tag_History tag) {
		// ClsContainer Container = null;

		switch (tag.getTag_Type()) {
		case GlobleVar.TruckTag:
		case GlobleVar.ContainerTag:
			if (!ToolUtility.CheckManualMode(tag)) {
				if (!ToolUtility.CheckPortBinding(tag.getFab(), tag.getArea(), tag.getGate(), tag.getReader_IP())) {
					// get gateSetting object
					RF_Gate_Setting gate = ToolUtility.GetGateSetting(tag.getFab(), tag.getArea(), tag.getGate(),
							tag.getReader_IP());
					if (gate != null) {
						RF_ContainerInfo container = ToolUtility.GetContainerInfo(tag.getFab(), tag.getArea(),
								tag.getGate(), tag.getReader_IP());
						if (container != null) {
							if (container.getContainer_ID().equals(tag.getTag_ID())) {
								gate.setLast_ContainerTag_Time(System.currentTimeMillis());
								ToolUtility.UpdateGateSetting(gate, tag.getReader_IP());
							}
						}
						if (tag.getReceive_Time() - gate.getLast_MarkTag_Time() > GlobleVar.TagMaskInTime) {
							PortBind(gate, tag);

						} else {
							logger.error(tag.getReader_IP() + " PortHandler error: gate setting not exist.");
						}
						// update gateSetting object

					} else {
						logger.debug(tag.getReader_IP() + " PortHandler : Fetch gate setting fail.");
					}
				} else {
					logger.debug(tag.getReader_IP() + " PortHandler : This port is already binded car.");
				}
			}else {
				logger.debug(tag.getReader_IP() + " PortHandler : This port status is manual binded.");
			}
			break;
		case GlobleVar.MarkTag: // MarkTAG

			if (!ToolUtility.CheckManualMode(tag)) {
				RF_Gate_Setting gate = ToolUtility.GetGateSetting(tag.getFab(), tag.getArea(), tag.getGate(),
						tag.getReader_IP());
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

	public static void PortBind(RF_Gate_Setting gate, RF_Tag_History tag) {
		String voiceText = "";
		RF_ContainerInfo container = ToolUtility.GetContainerInfo(tag);

		if (container == null) {

			// ToolUtility.Subtitle(gate.getFab(), gate.getArea(), gate.getGate(),
			// "該車輛沒有進廠紀錄", tag.getReader_IP());
			// ToolUtility.SignalTower(gate.getFab(), gate.getArea(), gate.getGate(),
			// GlobleVar.RedOn, tag.getReader_IP());
			// ToolUtility.SetGateError(tag, GlobleVar.NonEntryRecord,
			// ToolUtility.ConvertGateStr(tag) + "，"
			// + ToolUtility.ConvertCarStr(tag) + tag.getTag_ID() + "沒有進廠紀錄");
			logger.info(tag.getReader_IP() + " " + ToolUtility.ConvertGateStr(tag) + " " + tag.getTag_ID() + "沒有進場紀錄");
		} else if (!container.getGate().equals("0")) {

			logger.info(tag.getReader_IP() + " " + tag.getTag_ID() + "無法綁定於" + ToolUtility.ConvertGateStr(tag)
					+ "，因目前被綁定在" + ToolUtility.ConvertGateStr(container, tag.getReader_IP()));
			// ToolUtility.Subtitle(gate.getFab(), gate.getArea(), gate.getGate(),
			// "綁定失敗，該車輛目前被綁定在" + ToolUtility.ConvertGateStr(container, tag.getReader_IP()),
			// tag.getReader_IP());
			// ToolUtility.SignalTower(gate.getFab(), gate.getArea(), gate.getGate(),
			// GlobleVar.RedOn, tag.getReader_IP());
			// ToolUtility.SetGateError(tag, GlobleVar.BindingError,
			// tag.getTag_ID() + "無法綁定於" + ToolUtility.ConvertGateStr(tag) + "，因目前被綁定在"
			// + ToolUtility.ConvertGateStr(container, tag.getReader_IP()));
		} else {
			// Bind start
			// gate.setManual_Bind(false);
			gate.setLast_ContainerTag_Time(System.currentTimeMillis());
			ToolUtility.UpdateGateSetting(gate, tag.getReader_IP());

			container.setFab(tag.getFab());
			container.setArea(tag.getArea());
			container.setGate(tag.getGate());

			ToolUtility.DeletePalletByCarID(container.getContainer_ID(), tag.getReader_IP());
			ToolUtility.ClearErrorPallet(gate.getFab(), gate.getArea(), gate.getGate(), tag.getReader_IP());
			// Update container object
			ToolUtility.UpdateContainerInfo(container, tag.getReader_IP());
			// Clear all pallet by this car ID

			ToolUtility.Subtitle(gate.getFab(), gate.getArea(), gate.getGate(),
					ToolUtility.ConvertCarStr(tag) + "進入:" + container.getContainer_ID(), tag.getReader_IP());
			ToolUtility.SignalTowerAutoOff(gate.getFab(), gate.getArea(), gate.getGate(), GlobleVar.GreenOn, 5000,
					tag.getReader_IP());
			if (container.getCar_Type().equals(GlobleVar.TruckStr)) {
				if (container.getReason() != null) {
					if (container.getReason().equals("Delivery")) {
						voiceText = "廠商" + container.getSource() + " 由" + container.getVendor_Name() + "運輸 已停靠"
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

	public static void PortUnBind(RF_Gate_Setting gate, RF_Tag_History tag) {
		String voiceText = "";
		RF_ContainerInfo container = ToolUtility.GetContainerInfo(tag.getFab(), tag.getArea(), tag.getGate(),
				tag.getReader_IP());

		if (container != null) {

			// Check load complete?
			List<String> notCompletePallets = ToolUtility.GetNotCompletePallet(container, tag.getReader_IP());

			if (notCompletePallets.size() != 0) {
				// There are some pallet is not complete.

				ToolUtility.Subtitle(gate.getFab(), gate.getArea(), gate.getGate(),
						container.getContainer_ID() + " 尚未裝櫃完成即離場", tag.getReader_IP());
				ToolUtility.SignalTower(gate.getFab(), gate.getArea(), gate.getGate(), GlobleVar.RedOn,
						tag.getReader_IP());
				ToolUtility.SetGateError(tag, GlobleVar.LoadNotComplete, ToolUtility.ConvertGateStr(tag) + "，"
						+ ToolUtility.ConvertCarStr(tag) + container.getContainer_ID() + "，尚未裝櫃完成即離場");

				voiceText = "注意 注意 出貨" + ToolUtility.GetShipTo(container, tag.getReader_IP()) + " 由"
						+ container.getVendor_Name() + "運輸 " + ToolUtility.ConvertGateStr(container, tag.getReader_IP())
						+ " 尚未裝櫃完成即離場 請同仁查明";
				ToolUtility.VoiceSend(gate.getVoice_Path(), voiceText, tag.getReader_IP());
			} else {
				// All pallet are complete, car exit normally.
				container.setArea("WH");
				container.setGate("0");

				ToolUtility.DeletePalletByCarID(container.getContainer_ID(), tag.getReader_IP());
				ToolUtility.ClearErrorPallet(gate.getFab(), gate.getArea(), gate.getGate(), tag.getReader_IP());
				ToolUtility.UpdateContainerInfo(container, tag.getReader_IP());

				ToolUtility.Subtitle(gate.getFab(), gate.getArea(), gate.getGate(),
						container.getContainer_ID() + " 已離開碼頭", tag.getReader_IP());
				ToolUtility.SignalTowerAutoOff(gate.getFab(), gate.getArea(), gate.getGate(), GlobleVar.GreenOn, 5000,
						tag.getReader_IP());
				if (container.getCar_Type().equals(GlobleVar.TruckStr)) {
					if (container.getReason() != null) {
						if (container.getReason().equals("Delivery")) {
							voiceText = "廠商" + container.getSource() + " 由" + container.getVendor_Name() + "運輸 卸貨完成已離開"
									+ ToolUtility.ConvertGateStr(container, tag.getReader_IP());
						} else if (container.getReason().equals("Receive")) {
							voiceText = "出貨" + ToolUtility.GetShipTo(container, tag.getReader_IP()) + " 由"
									+ container.getVendor_Name() + "運輸 裝櫃完成已離開"
									+ ToolUtility.ConvertGateStr(container, tag.getReader_IP());
						}
					}
				} else if (container.getCar_Type().equals(GlobleVar.ContainerStr)) {
					if (container.getContainer_Status().equals("NonEmpty")) {
						voiceText = "出貨" + ToolUtility.GetShipTo(container, tag.getReader_IP()) + " 由"
								+ container.getVendor_Name() + "運輸 裝櫃完成已離開"
								+ ToolUtility.ConvertGateStr(container, tag.getReader_IP());
					} else {
						voiceText = container.getVendor_Name() + "運輸卸櫃完成 已離開"
								+ ToolUtility.ConvertGateStr(container, tag.getReader_IP());
					}
				}
				ToolUtility.VoiceSend(gate.getVoice_Path(), voiceText, tag.getReader_IP());

			}
			gate.setManual_Bind(false);
		} else {
			logger.debug(tag.getReader_IP() + " This gate is not binding any car.");

		}
		gate.setLast_MarkTag_Time(System.currentTimeMillis());
		ToolUtility.UpdateGateSetting(gate, tag.getReader_IP());

	}

}
