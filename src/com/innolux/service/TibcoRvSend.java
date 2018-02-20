package com.innolux.service;

import org.apache.log4j.Logger;

import com.innolux.common.GlobleVar;
import com.innolux.common.MessageFormat;
import com.innolux.common.ToolUtility;
import com.tibco.tibrv.*;

public class TibcoRvSend {
	private Logger logger = Logger.getLogger(this.getClass());

	private TibrvTransport transport = null;
	private String daemon;
	private String service;
	private String network;

	public TibcoRvSend(String _daemon, String _service, String _network) {
		daemon = _daemon;
		service = _service;
		network = _network;
	}

	public synchronized void sendMessage(String data, String newSubject) {
		// open Tibrv in native implementation
		try {
			Tibrv.open(Tibrv.IMPL_NATIVE);
		} catch (TibrvException e) {
			logger.error("Failed to open Tibrv in native implementation: " + e);
			return;
		}

		// Create RVD transport
		try {
			transport = new TibrvRvdTransport(service, network, daemon);
		} catch (TibrvException e) {
			logger.error("Failed to create TibrvRvdTransport: " + e);
			return;
		}

		TibrvMsg msg = new TibrvMsg();

		try {
			String sendSubject = newSubject; // +".COMM_SRV."+eqpID;
			logger.debug("Send subject=" + sendSubject);

			msg.setSendSubject(sendSubject);
			// msg.setReplySubject(localSubject);
			msg.update("DATA", data);
			logger.info(msg);
			transport.send(msg);
			transport.destroy();
			Tibrv.close();
		} catch (TibrvException e) {
			logger.error(e.getMessage());
		}
	}

	public synchronized TibrvMsg sendReplyMessage(String data, String newSubject, int tryCount) {
		// open Tibrv in native implementation
		try {
			Tibrv.open(Tibrv.IMPL_NATIVE);
		} catch (TibrvException e) {
			logger.error("Failed to open Tibrv in native implementation: " + e);
			return null;
		}

		// Create RVD transport
		try {
			transport = new TibrvRvdTransport(service, network, daemon);
		} catch (TibrvException e) {
			logger.error("Failed to create TibrvRvdTransport: " + e);
			return null;
		}

		TibrvMsg msg = new TibrvMsg();
		TibrvMsg replyMsg = null;
		try {
			String sendSubject = newSubject; // +".COMM_SRV."+eqpID;
			logger.debug("Send subject=" + sendSubject);

			msg.setSendSubject(sendSubject);
			// msg.setReplySubject(localSubject);
			msg.update("DATA", data);
			logger.info(msg);

			while (replyMsg == null && tryCount != 0) {
				replyMsg = transport.sendRequest(msg, 5);
				tryCount--;
			}
			

			logger.info(replyMsg);
			transport.destroy();
			Tibrv.close();
			
			if(replyMsg==null) {
				ToolUtility.MesDaemon.sendMessage(MessageFormat.SendAms("T2",
						GlobleVar.Cylinder_NoReply, "WISCylinders", "WMS沒有回覆訊息", data, "sendReplyMessage"),
						GlobleVar.SendToAMS);
			}
		} catch (TibrvException e) {
			logger.error(e.getMessage());
		}
		return replyMsg;
	}
}
