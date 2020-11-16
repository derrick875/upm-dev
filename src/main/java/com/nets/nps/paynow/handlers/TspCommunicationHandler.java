package com.nets.nps.paynow.handlers;

import java.io.IOException;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.nets.nps.core.entity.Detokenization;
import com.nets.nps.paynow.utils.UtilComponents;
import com.nets.upos.commons.logger.ApsLogger;
import com.nets.upos.commons.solace.SolaceMessageReceiver;
import com.nets.upos.commons.solace.SolaceMessageSender;

@Component
public class TspCommunicationHandler {

	private static final ApsLogger logger = new ApsLogger(TspCommunicationHandler.class);
	
	@Qualifier("tpsMessageProducer")
	@Autowired
	SolaceMessageSender solaceMessageSender;
	
	@Qualifier("tpsMessageReceiver")
	@Autowired
	SolaceMessageReceiver solaceMessageReceiver;

	public String correlationId;
	
	private static final String NS_CORRECLATION_ID_PREFIX = "J1";
		
	public String sendAndReceive(Detokenization detokenization) throws IOException {
		logger.info("Inside TspCommunicationHandler" + detokenization);
		correlationId = getNsCorrectionId();
		String requestString = UtilComponents.getStringFromObject(detokenization);
		solaceMessageSender.put(requestString, correlationId);
		String response = solaceMessageReceiver.receiveMessage(correlationId);
	
		logger.info("Detokenization response is" + response);
		return response;
	}
	
	private String getNsCorrectionId() {
		String uuid = UUID.randomUUID().toString().replace("-", "");
		uuid = NS_CORRECLATION_ID_PREFIX + uuid.substring(0, 20);
		return uuid;
	}
}
