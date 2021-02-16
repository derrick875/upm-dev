package com.nets.nps.client.impl;

import static com.nets.upos.commons.validations.helper.ObjectValidationHelpers.notNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.nets.nps.paynow.utils.UtilComponents;
import com.nets.nps.upi.entity.AdditionalProcessingResultRequest;
import com.nets.nps.upi.entity.AdditionalProcessingUpiResponse;
import com.nets.nps.upi.entity.QrcGenerationRequest;
import com.nets.nps.upi.entity.UpiQrcGenerationRequest;
import com.nets.nps.upi.entity.UpiQrcGenerationResponse;
import com.nets.upos.commons.exception.BaseBusinessException;
import com.nets.upos.commons.logger.ApsLogger;
import com.nets.upos.commons.solace.NpsProxyReceiver;
import com.nets.upos.commons.solace.NpsProxySender;
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
	
	@Qualifier("npsProxyMsgProducer")
	@Autowired
	private NpsProxySender npsProxySender;
	
	@Qualifier("npsProxyMsgReceiver")
	@Autowired
	private NpsProxyReceiver npsProxyReceiver;
	
	
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
	

	public AdditionalProcessingUpiResponse sendAndReceiveFromUpi( AdditionalProcessingResultRequest req ) {
		logger.info("UpiClient Service Started");
		npsProxySender.putAddRequest(req);
		String response = npsProxyReceiver.receiveAddProResponse() ;
		AdditionalProcessingUpiResponse responseObject = (AdditionalProcessingUpiResponse) UtilComponents.getObjectFromString(response , AdditionalProcessingUpiResponse.class ) ;
		logger.info("AdditionalProcessingUpiResponse's value is : " + responseObject.toString() );
		return responseObject;
		
	}
}
