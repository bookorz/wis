package com.innolux.common;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.log4j.Logger;

import com.innolux.common.base.IR_MessageBase;
import com.innolux.common.base.IR_Signal;
import com.innolux.common.base.ResponseBase;
import com.innolux.model.RF_Antenna_Setting;
import com.innolux.model.RF_Gate_Setting;
import com.innolux.model.RF_Tag_History;
import com.innolux.service.ReaderCmdService;

public class IRHandle {
	public static Logger logger = Logger.getLogger(IRHandle.class);
	public static IR_Signal T1_IR = new IR_Signal();
	public static IR_Signal T2_IR = new IR_Signal();

	public static ResponseBase<String> Data(IR_MessageBase ir) {
		ResponseBase<String> result = new ResponseBase<String>();
		List<RF_Tag_History> tmp = new ArrayList<RF_Tag_History>();
		RF_Tag_History IRhis = new RF_Tag_History();
		IRhis.setFab(ir.getFab());
		IRhis.setArea(ir.getArea());
		IRhis.setGate(ir.getGate());
		IRhis.setTimeStamp(Calendar.getInstance().getTime());

		logger.info("IR " + ir.toString());
		// String lastStatus = "";
		try {
			switch (ir.getFab()) {
			case "T1":
				if (ir.getType().equals(GlobleVar.RawDataReport)) {
					// lastStatus = T1_IR.GetLastSignal(ir.getFab(), ir.getArea(), ir.getGate(),
					// ir.getStatus());
					// if (!ir.getStatus().equals(lastStatus)) {
					// signal is change
					// String targetReaderIP = ToolUtility.GetReaderIP(ir.getFab(), ir.getArea(),
					// ir.getGate());

					RF_Gate_Setting gate = ToolUtility.GetGateSetting(ir.getFab(), ir.getArea(), ir.getGate(), "IR");

					// RF_Antenna_Setting antenna = ToolUtility.GetAntSetting(ir.getFab(),
					// ir.getArea(), ir.getGate(),
					// "DN", targetReaderIP);
					switch (ir.getStatus()) {
					case GlobleVar.On:
						// IR connect
						// Disable pallet antenna
						gate.setDirection_EndTime(ir.getTimeStamp());
						ToolUtility.UpdateGateSetting(gate, "IR");

						// Wait for few seconds ,if not receive on signal then disable antenna.
						Thread t = new Thread(new Runnable() {

							@Override
							public void run() {

								try {
									// RF_Gate_Setting oldgate = ToolUtility.GetGateSetting(ir.getFab(),
									// ir.getArea(),
									// ir.getGate(), targetReaderIP);
									RF_Gate_Setting oldGate = gate;
									Thread.sleep(3000);
									RF_Gate_Setting newgate = ToolUtility.GetGateSetting(oldGate.getFab(),
											oldGate.getArea(), oldGate.getGate(), "IR");
									
									if (oldGate.getDirection_EndTime() == newgate.getDirection_EndTime()) {
										// antenna.setActive(false);
										// ToolUtility.UpdateAntSetting(antenna, targetReaderIP);
										// ReaderCmdService.SetAntennaSequence(targetReaderIP);
										ToolUtility.SetAntActive(ir.getFab(), ir.getArea(), ir.getGate(), "DN", false,
												"IR");
										// Reset gate direction time
										newgate.setDirection_StartTime(0);
										newgate.setDirection_EndTime(0);
										ToolUtility.UpdateGateSetting(newgate, "IR");
										logger.info("IR" + " IR timeout, reset antenna.");
									}

								} catch (Exception e) {

									logger.error("IR" + " Exception:" + ToolUtility.StackTrace2String(e));

								}

							}
						});
						t.setDaemon(false);
						t.start();

						break;
					case GlobleVar.Off:
						// IR break
						// Active pallet antenna

						gate.setForkLift_Direction(GlobleVar.ForkLiftAll);
						gate.setDirection_ReportTime(ir.getTimeStamp());
						gate.setDirection_StartTime(ir.getTimeStamp());

						ToolUtility.SetAntActive(ir.getFab(), ir.getArea(), ir.getGate(), "DN", true, "IR");

						break;
					}

					// }
				}
				break;
			case "T2":

				if (ir.getType().equals(GlobleVar.DirectionReport)) {
					// Disable pallet antenna

					String targetReaderIP = ToolUtility.GetReaderIP(ir.getFab(), ir.getArea(), ir.getGate());

					RF_Gate_Setting gate = ToolUtility.GetGateSetting(ir.getFab(), ir.getArea(), ir.getGate(), "IR");

					RF_Antenna_Setting antenna = ToolUtility.GetAntSetting(ir.getFab(), ir.getArea(), ir.getGate(),
							"DN", "IR");
					IRhis.setFab(gate.getFab());
					IRhis.setArea(gate.getArea());
					IRhis.setGate(gate.getGate());
					if (gate.getDirection_StartTime() != 0) {
						gate.setForkLift_Direction(ir.getDirection());
						gate.setDirection_ReportTime(ir.getTimeStamp());
						gate.setDirection_EndTime(ir.getTimeStamp());
						ToolUtility.UpdateGateSetting(gate, "IR");
						TagHandle.Data(ToolUtility.GetTagHistory(gate.getFab(), gate.getArea(), gate.getGate(), "DN",
								gate.getDirection_StartTime(), "IR"));
						if (antenna != null) {
							antenna.setActive(false);
							ToolUtility.UpdateAntSetting(antenna, "IR");

							ReaderCmdService.SetAntennaSequence(targetReaderIP);
						} else {
							logger.error("IR antenna is not exist.");
						}
						// Reset gate direction time
						gate.setDirection_StartTime(0);
						// gate.setDirection_EndTime(0);
						ToolUtility.UpdateGateSetting(gate, "IR");
					}
				} else if (ir.getType().equals(GlobleVar.RawDataReport)) {

					// signal is change
					String targetReaderIP = ToolUtility.GetReaderIP(ir.getFab(), ir.getArea(), ir.getGate());

					RF_Gate_Setting gate = ToolUtility.GetGateSetting(ir.getFab(), ir.getArea(), ir.getGate(),
							targetReaderIP);

					IRhis.setFab(gate.getFab());
					IRhis.setArea(gate.getArea());
					IRhis.setGate(gate.getGate());
					// RF_Antenna_Setting antenna = ToolUtility.GetAntSetting(ir.getFab(),
					// ir.getArea(), ir.getGate(),
					// "DN", targetReaderIP);

					switch (ir.getStatus()) {
					case GlobleVar.On:
						// IR connect

						break;
					case GlobleVar.Off:
						// IR break
						// Active pallet antenna
//						if (gate.getDirection_StartTime() != 0) {
//							if (ir.getTimeStamp() - gate.getDirection_StartTime() > 15000) {
//								gate.setDirection_StartTime(0);
//							}
//						}

						if (gate.getDirection_StartTime() == 0) {
							gate.setDirection_StartTime(ir.getTimeStamp());
							gate.setDirection_EndTime(0);
							ToolUtility.UpdateGateSetting(gate, targetReaderIP);
							// antenna.setActive(true);
							// ToolUtility.UpdateAntSetting(antenna, targetReaderIP);
							// ReaderCmdService.SetAntennaSequence(targetReaderIP);
							ToolUtility.SetAntActive(ir.getFab(), ir.getArea(), ir.getGate(), "DN", true, "IR");

							// Wait for few seconds ,if not receive on signal then disable antenna.
							Thread t = new Thread(new Runnable() {

								@Override
								public void run() {

									try {
										// RF_Gate_Setting oldgate = ToolUtility.GetGateSetting(ir.getFab(),
										// ir.getArea(),
										// ir.getGate(), targetReaderIP);

										Thread.sleep(10000);
										RF_Gate_Setting newgate = ToolUtility.GetGateSetting(gate.getFab(),
												gate.getArea(), gate.getGate(), targetReaderIP);

										if (gate.getDirection_StartTime() == newgate.getDirection_StartTime()
												&& newgate.getDirection_EndTime() == 0) {
											// antenna.setActive(false);
											// ToolUtility.UpdateAntSetting(antenna, targetReaderIP);
											// ReaderCmdService.SetAntennaSequence(targetReaderIP);
											ToolUtility.SetAntActive(ir.getFab(), ir.getArea(), ir.getGate(), "DN",
													false, "IR");
											// Reset gate direction time
											newgate.setDirection_StartTime(0);
											newgate.setDirection_EndTime(0);
											ToolUtility.UpdateGateSetting(newgate, targetReaderIP);
											logger.info(" IR timeout, reset antenna.");
										}

									} catch (Exception e) {

										logger.error("IR Exception:" + ToolUtility.StackTrace2String(e));

									}

								}
							});
							t.setDaemon(false);
							t.start();
						}
						break;
					}
					// }
				}
				break;
			}

			result.setStatus("200");
			result.setMessage("Success");
			if (ir.getType().equals(GlobleVar.TimeSync)) {

			} else {

				IRhis.setAntenna_Type(GlobleVar.ANT_Pallet);
				IRhis.setCount(1);
				IRhis.setDiscover_Time(ir.getTimeStamp());
				IRhis.setRawData(ir.getType());
				IRhis.setTag_Type("IR");
				IRhis.setReceive_Time(ir.getTimeStamp());
				if (ir.getType().equals(GlobleVar.RawDataReport)) {
					IRhis.setTag_ID(ir.getSensorNo() + ":" + ir.getStatus());
				} else if (ir.getType().equals(GlobleVar.DirectionReport)) {
					IRhis.setTag_ID(ir.getDirection());
				}

				IRhis.setReader_IP("IR");
				IRhis.setRSSI("100");
				tmp.add(IRhis);
				ToolUtility.InsertLog(tmp, "IR");
			}
		} catch (

		Exception e) {
			logger.error("IR Exception:" + ToolUtility.StackTrace2String(e));
			result.setStatus("500");
			result.setMessage(ToolUtility.StackTrace2String(e));
		}
		return result;
	}
}
