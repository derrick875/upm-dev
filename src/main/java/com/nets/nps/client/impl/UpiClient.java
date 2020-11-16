package com.nets.nps.client.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.nets.nps.paynow.entity.QrcGenerationRequest;
import com.nets.nps.paynow.entity.UpiQrcGenerationRequest;
import com.nets.nps.paynow.entity.UpiQrcGenerationResponse;
import com.nets.nps.paynow.utils.UtilComponents;
import com.nets.upos.commons.logger.ApsLogger;
import com.nets.upos.commons.solace.UpiProxyReceiver;
import com.nets.upos.commons.solace.UpiProxySender;

@Service
public class UpiClient {
	
	private static final ApsLogger logger = new ApsLogger(UpiClient.class);

	@Autowired
	private UpiQrcGenerationRequestAdapter upiQrcGenerationRequestAdapter;
	
	@Qualifier("upiMessageProducer")
	@Autowired
	private UpiProxySender upiProxySender;
	
	@Qualifier("upiMessageReceiver")
	@Autowired
	private UpiProxyReceiver upiProxyReceiver;
	
	public UpiQrcGenerationResponse sendAndReceiveFromUpi(QrcGenerationRequest request) {
		
		logger.info("UpiClient Service Started");
		UpiQrcGenerationRequest upiQrcGenerationRequest = upiQrcGenerationRequestAdapter.handleQrGenReq(request);
		upiProxySender.put(upiQrcGenerationRequest);
		String upiProxyResp = upiProxyReceiver.receiveMessage();
		UpiQrcGenerationResponse upiQrcGenerationResponse = (UpiQrcGenerationResponse) UtilComponents.getObjectFromString(upiProxyResp, UpiQrcGenerationResponse.class);

		return upiQrcGenerationResponse;
	}	
}
