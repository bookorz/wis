package com.innolux.common;

import org.apache.log4j.Logger;

import com.innolux.common.base.IR_MessageBase;
import com.innolux.common.base.IR_Signal;
import com.innolux.common.base.ResponseBase;
import com.innolux.model.RF_Antenna_Setting;
import com.innolux.model.RF_Gate_Setting;
import com.innolux.service.ReaderCmdService;

public class IRHandle {
	public static Logger logger = Logger.getLogger(IRHandle.class);
	public static IR_Signal T1_IR = new IR_Signal();
	public static IR_Signal T2_IR = new IR_Signal();

	public static ResponseBase<String> Data(IR_MessageBase ir) {
		ResponseBase<String> result = new ResponseBase<String>();
		String lastStatus = "";
		try {
			switch (ir.getFab()) {
			case "T1":
				if (ir.getType().equals(GlobleVar.RawDataReport)) {
					lastStatus = T1_IR.GetLastSignal(ir.getFab(), ir.getArea(), ir.getGate(), ir.getStatus());
					if (!ir.getStatus().equals(lastStatus)) {
						// signal is change
						String targetReaderIP = ToolUtility.GetReaderIP(ir.getFab(), ir.getArea(), ir.getGate());
						RF_Antenna_Setting antenna = ToolUtility.GetAntSetting(ir.getFab(), ir.getArea(), ir.getGate(),
								"DN", targetReaderIP);
						RF_Gate_Setting gate = ToolUtility.GetGateSetting(ir.getFab(), ir.getArea(), ir.getGate(),
								targetReaderIP);
						switch (ir.getStatus()) {
						case GlobleVar.On:
							// IR connect
							// Disable pallet antenna
							if (gate.geDirection_StartTime() != 0) {
								gate.setForkLift_Direction(GlobleVar.ForkLiftAll);
								gate.setDirection_EndTime(ir.getTimeStamp());
								ToolUtility.UpdateGateSetting(gate, targetReaderIP);
								TagHandle.Data(ToolUtility.GetTagHistory(ir.getFab(), ir.getArea(), ir.getGate(), "DN",
										gate.geDirection_StartTime(), targetReaderIP));

								antenna.setActive(false);
								ToolUtility.UpdateAntSetting(antenna, targetReaderIP);
								ReaderCmdService.SetAntennaSequence(targetReaderIP);

								// Reset gate direction time
								gate.setDirection_StartTime(0);
								gate.setDirection_EndTime(0);
								ToolUtility.UpdateGateSetting(gate, targetReaderIP);
							}
							break;
						case GlobleVar.Off:
							// IR break
							// Active pallet antenna
							gate.setDirection_StartTime(ir.getTimeStamp());
							gate.setDirection_EndTime(0);
							ToolUtility.UpdateGateSetting(gate, targetReaderIP);
							antenna.setActive(true);
							ToolUtility.UpdateAntSetting(antenna, targetReaderIP);
							ReaderCmdService.SetAntennaSequence(targetReaderIP);

							// Wait for few seconds ,if not receive on signal then disable antenna.
							Thread t = new Thread(new Runnable() {

								@Override
								public void run() {

									try {
										RF_Gate_Setting oldgate = ToolUtility.GetGateSetting(ir.getFab(), ir.getArea(),
												ir.getGate(), targetReaderIP);

										Thread.sleep(60000);
										RF_Gate_Setting newgate = ToolUtility.GetGateSetting(ir.getFab(), ir.getArea(),
												ir.getGate(), targetReaderIP);

										if (oldgate.geDirection_StartTime() == newgate.geDirection_StartTime()
												&& gate.getDirection_EndTime() == 0) {
											antenna.setActive(false);
											ToolUtility.UpdateAntSetting(antenna, targetReaderIP);
											ReaderCmdService.SetAntennaSequence(targetReaderIP);
											// Reset gate direction time
											newgate.setDirection_StartTime(0);
											newgate.setDirection_EndTime(0);
											ToolUtility.UpdateGateSetting(newgate, targetReaderIP);
											logger.info(targetReaderIP + " IR timeout, reset antenna.");
										}

									} catch (Exception e) {

										logger.error(targetReaderIP + " Exception:" + ToolUtility.StackTrace2String(e));

									}

								}
							});
							t.setDaemon(false);
							t.start();
							break;
						}

					}
				}
				break;
			case "T2":
				if (ir.getType().equals(GlobleVar.DirectionReport)) {
					// Disable pallet antenna

					String targetReaderIP = ToolUtility.GetReaderIP(ir.getFab(), ir.getArea(), ir.getGate());
					RF_Antenna_Setting antenna = ToolUtility.GetAntSetting(ir.getFab(), ir.getArea(), ir.getGate(),
							"DN", "IR");
					RF_Gate_Setting gate = ToolUtility.GetGateSetting(ir.getFab(), ir.getArea(), ir.getGate(),
							"IR");
					//search for active operate gate
					if(!gate.getShare_Gate().equals("0")) {
						String Opreation = ToolUtility.GetOperation_Mode(gate.getFab(),gate.getArea(),gate.getGate(),"IR");
						if (Opreation.equals("")) {
							Opreation = ToolUtility.GetOperation_Mode(gate.getFab(),gate.getArea(),gate.getShare_Gate(),"IR");
							if (!Opreation.equals("")) {
								//Change to current operate gate
								
								gate = ToolUtility.GetGateSetting(gate.getFab(),gate.getArea(),gate.getShare_Gate(),
										"IR");
							}
						}
					}
					if (gate.geDirection_StartTime() != 0) {
						gate.setForkLift_Direction(ir.getDirection());
						gate.setDirection_EndTime(ir.getTimeStamp());
						ToolUtility.UpdateGateSetting(gate, "IR");
						TagHandle.Data(ToolUtility.GetTagHistory(ir.getFab(), ir.getArea(), ir.getGate(), "DN",
								gate.geDirection_StartTime(), "IR"));

						antenna.setActive(false);
						ToolUtility.UpdateAntSetting(antenna, "IR");
						ReaderCmdService.SetAntennaSequence(targetReaderIP);
						// Reset gate direction time
						gate.setDirection_StartTime(0);
						gate.setDirection_EndTime(0);
						ToolUtility.UpdateGateSetting(gate, "IR");
					}
				} else if (ir.getType().equals(GlobleVar.RawDataReport)) {
					lastStatus = T2_IR.GetLastSignal(ir.getFab(), ir.getArea(), ir.getGate(), ir.getStatus());

					if (!ir.getStatus().equals(lastStatus)) {
						// signal is change
						String targetReaderIP = ToolUtility.GetReaderIP(ir.getFab(), ir.getArea(), ir.getGate());
						RF_Antenna_Setting antenna = ToolUtility.GetAntSetting(ir.getFab(), ir.getArea(), ir.getGate(),
								"DN", targetReaderIP);
						RF_Gate_Setting gate = ToolUtility.GetGateSetting(ir.getFab(), ir.getArea(), ir.getGate(),
								targetReaderIP);
						//search for active operate gate
						if(!gate.getShare_Gate().equals("0")) {
							String Opreation = ToolUtility.GetOperation_Mode(gate.getFab(),gate.getArea(),gate.getGate(),"IR");
							if (Opreation.equals("")) {
								Opreation = ToolUtility.GetOperation_Mode(gate.getFab(),gate.getArea(),gate.getShare_Gate(),"IR");
								if (!Opreation.equals("")) {
									//Change to current operate gate
									
									gate = ToolUtility.GetGateSetting(gate.getFab(),gate.getArea(),gate.getShare_Gate(),
											"IR");
								}
							}
						}
						switch (ir.getStatus()) {
						case GlobleVar.On:
							// IR connect

							break;
						case GlobleVar.Off:
							// IR break
							// Active pallet antenna
							gate.setDirection_StartTime(ir.getTimeStamp());
							gate.setDirection_EndTime(0);
							ToolUtility.UpdateGateSetting(gate, targetReaderIP);
							antenna.setActive(true);
							ToolUtility.UpdateAntSetting(antenna, targetReaderIP);
							ReaderCmdService.SetAntennaSequence(targetReaderIP);

							// Wait for few seconds ,if not receive on signal then disable antenna.
							Thread t = new Thread(new Runnable() {

								@Override
								public void run() {

									try {
										RF_Gate_Setting oldgate = ToolUtility.GetGateSetting(ir.getFab(), ir.getArea(),
												ir.getGate(), targetReaderIP);

										Thread.sleep(60000);
										RF_Gate_Setting newgate = ToolUtility.GetGateSetting(ir.getFab(), ir.getArea(),
												ir.getGate(), targetReaderIP);

										if (oldgate.geDirection_StartTime() == newgate.geDirection_StartTime()
												&& newgate.getDirection_EndTime() == 0) {
											antenna.setActive(false);
											ToolUtility.UpdateAntSetting(antenna, targetReaderIP);
											ReaderCmdService.SetAntennaSequence(targetReaderIP);

											// Reset gate direction time
											newgate.setDirection_StartTime(0);
											newgate.setDirection_EndTime(0);
											ToolUtility.UpdateGateSetting(newgate, targetReaderIP);
											logger.info(targetReaderIP + " IR timeout, reset antenna.");
										}

									} catch (Exception e) {

										logger.error(targetReaderIP + " Exception:" + ToolUtility.StackTrace2String(e));

									}

								}
							});
							t.setDaemon(false);
							t.start();
							break;
						}
					}
				}
				break;
			}

			result.setStatus("200");
			result.setMessage("Success");
		} catch (Exception e) {
			logger.error("IRHandle.Data Exception:" + ToolUtility.StackTrace2String(e));
			result.setStatus("500");
			result.setMessage(ToolUtility.StackTrace2String(e));
		}
		return result;
	}
}
