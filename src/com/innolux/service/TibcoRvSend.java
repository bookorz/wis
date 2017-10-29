package com.innolux.service;

import org.apache.log4j.Logger;

import com.tibco.tibrv.*;

public class TibcoRvSend {
	private Logger logger = Logger.getLogger(this.getClass());

	private TibrvTransport transport = null;

	public TibcoRvSend(String daemon, String service, String network) {

		// open Tibrv in native implementation
		try {
			Tibrv.open(Tibrv.IMPL_NATIVE);
		} catch (TibrvException e) {
			logger.error("Failed to open Tibrv in native implementation: " + e);
			// return false;
		}

		// Create RVD transport
		try {
			transport = new TibrvRvdTransport(service, network, daemon);
		} catch (TibrvException e) {
			logger.error("Failed to create TibrvRvdTransport: " + e);
			// return false;
		}

		
	}



	public synchronized void sendMessage(String data, String newSubject) {
		TibrvMsg msg = new TibrvMsg();

		try {
			String sendSubject = newSubject; // +".COMM_SRV."+eqpID;
			logger.debug("Send subject=" + sendSubject);

			msg.setSendSubject(sendSubject);
			// msg.setReplySubject(localSubject);
			msg.update("DATA", data);
			logger.info(msg);
			transport.send(msg);

		} catch (TibrvException e) {
			logger.error(e.getMessage());
		}
	}
}
