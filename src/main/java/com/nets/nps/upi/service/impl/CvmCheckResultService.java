package com.nets.nps.upi.service.impl;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.stereotype.Service;
import com.nets.nps.client.impl.UpiClient;
import com.nets.nps.upi.entity.AdditionalProcessingResultRequest;
import com.nets.nps.upi.entity.AdditionalProcessingUpiResponse;

@Service
public class CvmCheckResultService {
	
	private Logger logger = LoggerFactory.getLogger(CvmCheckResultService.class);
		
	@Autowired
	private UpiClient upiClient;
	
	@ServiceActivator
	public AdditionalProcessingUpiResponse process( AdditionalProcessingResultRequest adReq) {
		// 1. Take in a AdditionalProcessingResultRequest object 
		logger.info("AdditionalProcessingResultRequest object:  " + adReq.toString() );
		
		//2. Send by UpiClient(topic/queue) to Proxy
		AdditionalProcessingUpiResponse addResponse = upiClient.sendAndReceiveFromUpi(adReq) ;
		//3. Recieve back a 2.16 Additional Processing Result Response
		
		return addResponse;
	}
}

