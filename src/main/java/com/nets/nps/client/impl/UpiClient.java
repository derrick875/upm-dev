package com.nets.nps.client.impl;

import static com.nets.upos.commons.validations.helper.ObjectValidationHelpers.notNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.nets.nps.paynow.entity.QrcGenerationRequest;
import com.nets.nps.paynow.entity.UpiQrcGenerationRequest;
import com.nets.nps.paynow.entity.UpiQrcGenerationResponse;
import com.nets.nps.paynow.utils.UtilComponents;
import com.nets.upos.commons.exception.BaseBusinessException;
import com.nets.upos.commons.logger.ApsLogger;
import com.nets.upos.commons.solace.UpiProxyReceiver;
import com.nets.upos.commons.solace.UpiProxySender;
import com.nets.upos.commons.validations.BusinessValidationErrorCodes;

@Service
public class UpiClient {
	
	private static final ApsLogger logger = new ApsLogger(UpiClient.class);

	@Autowired
	private UpiQrcGenerationRequestAdapter upiQrcGenerationRequestAdapter;
	
	@Autowired
	private UpiProxySender upiProxySender;
	
	@Qualifier("upiMessageReceiver")
	@Autowired
	private UpiProxyReceiver upiProxyReceiver;
	
	public UpiQrcGenerationResponse sendAndReceiveFromUpi(QrcGenerationRequest request) throws BaseBusinessException {
		
		logger.info("UpiClient Service Started");
		UpiQrcGenerationRequest upiQrcGenerationRequest = upiQrcGenerationRequestAdapter.handleQrGenReq(request);
		upiProxySender.put(upiQrcGenerationRequest);
		String upiProxyResp = upiProxyReceiver.receiveMessage();
		//TODO Generate_QR_TIMEOUT add to commons and invalid upi qrc generation response
		notNull.test(upiProxyResp).throwIfInvalid(BusinessValidationErrorCodes.ORIGINAL_ORDER_TIMEOUT);
		UpiQrcGenerationResponse upiQrcGenerationResponse = (UpiQrcGenerationResponse) UtilComponents.getObjectFromString(upiProxyResp, UpiQrcGenerationResponse.class);
//		notNull.test(upiQrcGenerationResponse).throwIfInvalid(BusinessValidationErrorCodes.INVALID_NETS_TOKEN_REF_ID_RESPONSE);
		return upiQrcGenerationResponse;
	}	
}
