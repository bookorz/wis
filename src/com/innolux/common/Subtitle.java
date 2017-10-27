package com.innolux.common;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import org.apache.log4j.Logger;

import com.innolux.model.RF_Subtitle_Setting;

public class Subtitle {
	private RF_Subtitle_Setting SubtitleSet;
	public Logger logger = Logger.getLogger(this.getClass());
	private ToolUtility tools = new ToolUtility();
	Socket socket;

	byte ID_H = (byte) 'A'; // 站號 High byte
	byte ID_L = (byte) '1'; // 站號 Low byte
	byte checksum = 0; // 檢查碼
	byte intLinenum = 1; // 行號
	byte staysec = 0; // 停留秒數
	byte fcode = (byte) 'A'; // 前功能(A:由左移入)
	byte rcode = (byte) 'R'; // 後功能(S:續幕)
	Integer Row = 10; // 定義最多10行
	byte[][] MsgByteArray = new byte[Row][20]; // Show
												// Message:MsgIntArray[Row][col]
	byte[] MsgBuf;

	int m_port = 100;

	public Subtitle(RF_Subtitle_Setting _SubtitleSet) {

		SubtitleSet = _SubtitleSet;
	}

	private void SocketConnect(String servername, int port) throws UnknownHostException, IOException {

		// logger.debug(ReaderIP + " " + "SocketConnect start");
		socket = new Socket(InetAddress.getByName(servername), port);
		socket.setSoTimeout(1000);
		// logger.debug(ReaderIP + " " + "SocketConnect ok");
	}

	private String SocketClose() {
		String SocketCloseResult = "";
		try {
			logger.debug(SubtitleSet.getSubtitle_IP() + " " + "Socketclose start");
			socket.close();
			logger.debug(SubtitleSet.getSubtitle_IP() + " " + "Socketclose ok");
			// SocketCloseResult = "OK";
		} catch (Exception e) {
			SocketCloseResult = "NG[SocketClose]";
			logger.error(SubtitleSet.getSubtitle_IP() + " " + "Exception:" + tools.StackTrace2String(e));
		}
		return SocketCloseResult;

	}

	public synchronized boolean ShowData(String message) {
		
		boolean ShowDataResult = false;
		MsgBuf = new byte[31];
		byte[] ResultBuff = new byte[100];
		String send = "";
		String result = "";
		int retryCount = 0;
		Long lastCheckTime = System.currentTimeMillis();
		try {
			SocketConnect(SubtitleSet.getSubtitle_IP(), m_port);
			int Cols = 0;
			String ConvertMsgResult = "";
			String CombineMsgResult = "";
			DataOutputStream dataOut = new DataOutputStream(socket.getOutputStream());
			DataInputStream dataInput = new DataInputStream(socket.getInputStream());

			ConvertMsgResult = ConvertMsg(message);
			for (int t = 0; t < Row; t++) {
				Cols = t + 1;

				if (MsgByteArray[t][0] != 0)// 若MsgByteArray[t][0]=0表示此行無值，不需再做邏輯
				{
					CombineMsgResult = CombineMsg(MsgByteArray[t], (byte) 0xA1, Cols);// 0xA1
					send = "";
					for (int i1 = 0; i1 < MsgBuf.length; i1++) {
						if (send.equals("")) {
							send = String.valueOf(MsgBuf[i1]);
						} else {
							send += " " + String.valueOf(MsgBuf[i1]);
						}
					}
					// System.out.println("send:" + send); // 寫入
					logger.debug(SubtitleSet.getSubtitle_IP() + " " + "send:" + send);
					dataOut.write(MsgBuf);
					// System.out.println("send Complete"); // 寫入
					ResultBuff = new byte[30];
					retryCount = 0;
					lastCheckTime = System.currentTimeMillis();
					while (retryCount <= 3) {
						try {
							dataInput.read(ResultBuff);
						} catch (Exception e1) {
							// System.out.println("Exception:" + e1); // 寫入
							logger.error(SubtitleSet.getSubtitle_IP() + " " + "Exception:" + tools.StackTrace2String(e1));

							dataOut.write(MsgBuf);
							retryCount++;
							continue;
						}
						// System.out.println("read Complete"); //
						if (ResultBuff[0] == 2) {
							result = "";
							for (int i1 = 0; i1 < ResultBuff.length; i1++) {
								if (result.equals("")) {
									result = String.valueOf(ResultBuff[i1]);
								} else {
									result += " " + String.valueOf(ResultBuff[i1]);
								}
							}
							logger.debug(SubtitleSet.getSubtitle_IP() + " " + "Reply:" + result);
							// System.out.println("Reply:" + result);
							break;
						}
						// logger.debug(ReaderIP + " " +
						// "System.currentTimeMillis():" +
						// System.currentTimeMillis());
						// logger.debug(ReaderIP + " " + "lastCheckTime:" +
						// lastCheckTime);
						// System.out.println( "System.currentTimeMillis():" +
						// System.currentTimeMillis());
						// System.out.println( "lastCheckTime:" +
						// lastCheckTime);
						if ((System.currentTimeMillis() - lastCheckTime > 500) || ResultBuff[4] != 6) {
							send = "";
							for (int i1 = 0; i1 < MsgBuf.length; i1++) {
								if (send.equals("")) {
									send = String.valueOf(MsgBuf[i1]);
								} else {
									send += " " + String.valueOf(MsgBuf[i1]);
								}
							}
							retryCount++;
							// System.out.println("retry send" + retryCount +
							// ":" + send);
							logger.debug(SubtitleSet.getSubtitle_IP() + " " + "retry send" + retryCount + ":" + send);
							dataOut.write(MsgBuf);
							lastCheckTime = System.currentTimeMillis();
						}
					}
					// MsgBuf=null;
				} else {
					Cols = Cols - 1;
					CombineMsgResult = CombineMsg(MsgByteArray[t], (byte) 0xA8, (byte) Cols);// 0xA8
					send = "";
					for (int i1 = 0; i1 < MsgBuf.length; i1++) {
						if (send.equals("")) {
							send = String.valueOf(MsgBuf[i1]);
						} else {
							send += " " + String.valueOf(MsgBuf[i1]);
						}
					}
					// System.out.println("send:" + send); // 顯示行號
					logger.debug(SubtitleSet.getSubtitle_IP() + " " + "send:" + send);
					dataOut.write(MsgBuf);
					// System.out.println("send Complete"); // 寫入
					ResultBuff = new byte[30];
					retryCount = 0;
					lastCheckTime = System.currentTimeMillis();
					while (retryCount <= 3) {
						try {
							dataInput.read(ResultBuff);
						} catch (Exception e1) {
							// System.out.println("Exception:" + e1); // 寫入
							logger.error(SubtitleSet.getSubtitle_IP() + " " + "Exception:" + tools.StackTrace2String(e1));
							dataOut.write(MsgBuf);
							retryCount++;
							continue;
						}
						System.out.println("read Complete"); //
						if (ResultBuff[0] == 2) {
							result = "";
							for (int i1 = 0; i1 < ResultBuff.length; i1++) {
								if (result.equals("")) {
									result = String.valueOf(ResultBuff[i1]);
								} else {
									result += " " + String.valueOf(ResultBuff[i1]);
								}
							}
							logger.debug(SubtitleSet.getSubtitle_IP() + " " + "Reply:" + result);
							// System.out.println("Reply:" + result);
							break;
						}
						// System.out.println( "System.currentTimeMillis()222:"
						// + System.currentTimeMillis());
						// System.out.println( "lastCheckTime222:" +
						// lastCheckTime);
						if (System.currentTimeMillis() - lastCheckTime > 500 || ResultBuff[4] != 6) {
							send = "";
							for (int i1 = 0; i1 < MsgBuf.length; i1++) {
								if (send.equals("")) {
									send = String.valueOf(MsgBuf[i1]);
								} else {
									send += " " + String.valueOf(MsgBuf[i1]);
								}
							}
							retryCount++;
							logger.debug(SubtitleSet.getSubtitle_IP() + " " + "retry send" + retryCount + ":" + send);
							// System.out.println("retry send" + retryCount +
							// ":" + send);
							dataOut.write(MsgBuf);
							lastCheckTime = System.currentTimeMillis();
						}
					}
					// MsgBuf=null;
					break;// 若MsgByteArray[t][0]=0表示此行無值，跳出for迴圈
				}

			}
			// --------------
			// dataOut.flush();
			// dataOut.close();//取代socket.close();
			// ------------------
			// socket.close();
			// ---------------
			if (ConvertMsgResult != "OK" || CombineMsgResult != "OK") {
				ShowDataResult = false;

			} else {
				ShowDataResult = true;
			}
		} catch (Exception e) {
			logger.error(SubtitleSet.getSubtitle_IP() + " " + "Exception:" + tools.StackTrace2String(e));
			ShowDataResult = false;
		} finally {
			SocketClose();
		}
		MsgBuf = null;
		return ShowDataResult;
	}

	private String ConvertMsg(String Msg) {// 不足10字補32(20h),MsgIntArray[Row][col];
		int i = 0, j = 0, Row = 0;
		int count = 0;
		int TotalCount = 20;// 每行20個單位
		byte[] ArrayTemp;

		try {
			for (i = 0; i < Msg.length(); i++) {
				ArrayTemp = String.valueOf(Msg.charAt(i)).getBytes("BIG5");
				if ((TotalCount - count) < ArrayTemp.length) {
					while (count < TotalCount) {
						MsgByteArray[Row][count] = 0x20;// &h20(不足為補20h)
						count++;
					}
					count = 0;
					Row++;
				}
				for (j = 0; j < ArrayTemp.length; j++) {
					MsgByteArray[Row][count] = ArrayTemp[j];
					count++;
				}

			}
			while (count < TotalCount) {
				MsgByteArray[Row][count] = 0x20;// &h20(不足為補20h)
				count++;
			}
			return "OK";

		} catch (Exception e) {
			logger.error(SubtitleSet.getSubtitle_IP() + " " + "Exception:" + tools.StackTrace2String(e));
			return "NG" + "[ConvertMsg]:" + e;
		}

	}

	private String CombineMsg(byte[] byteArray, byte Type, int ColNo) {
		// when Type:A1-->ColNo(欲寫入的行號)
		// when Type:A8-->ColNo(欲想是的行數，1~ColNo行)
		String CombineMsgResult = null;
		int temp;
		MsgBuf = new byte[31];
		try {
			switch (Type) {
			case (byte) 0xA1:// A1 插入文字
				checksum = (byte) (ID_H ^ ID_L ^ Type ^ (byte) ColNo ^ staysec ^ fcode ^ (byte) 0xD0 ^ rcode);// "^"-->XOR
				MsgBuf[0] = (byte) 0x02; // 前導碼
				MsgBuf[1] = ID_H;
				MsgBuf[2] = ID_L;
				MsgBuf[3] = Type;
				MsgBuf[4] = (byte) ColNo;
				MsgBuf[5] = staysec;
				MsgBuf[6] = fcode;
				MsgBuf[7] = (byte) 0xD0;// 文字模式碼D0:半全形字
				temp = 8;
				for (int t = 0; t < byteArray.length; t++) {
					checksum = (byte) (checksum ^ byteArray[t]);
					MsgBuf[temp] = byteArray[t];
					temp++;

				}
				MsgBuf[28] = rcode;
				MsgBuf[29] = checksum;
				MsgBuf[30] = (byte) 0x03;
				break;
			case (byte) 0xA8:// 顯示行號
				checksum = (byte) (ID_H ^ ID_L ^ Type ^ 0x01 ^ (byte) ColNo);// "^"-->XOR
				MsgBuf[0] = (byte) 0x02; // 前導碼
				MsgBuf[1] = ID_H;
				MsgBuf[2] = ID_L;
				MsgBuf[3] = Type;
				MsgBuf[4] = 0x01;
				MsgBuf[5] = (byte) ColNo;
				MsgBuf[6] = checksum;
				MsgBuf[7] = (byte) 0x03;
				break;
			}

			CombineMsgResult = "OK";

		} catch (Exception e) {
			CombineMsgResult = "NG[CombineMsg-" + Type + "]:" + e;
			logger.error(SubtitleSet.getSubtitle_IP() + " " + "Exception:" + tools.StackTrace2String(e));
		}

		return CombineMsgResult;
	}
}
