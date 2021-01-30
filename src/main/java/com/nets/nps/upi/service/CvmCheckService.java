package com.nets.nps.upi.service;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.stereotype.Service;

import com.nets.nps.paynow.utils.UtilComponents;
import com.nets.nps.upi.entity.AdditionalProcessingUpiRequest;
import com.nets.nps.upi.entity.AdditionalProcessingUpiResponse;
import com.nets.nps.upi.entity.CvmCheckRequest;
import com.nets.nps.upi.entity.MessageResponse;
import com.nets.nps.upi.entity.MsgInfo;

@Service
public class CvmCheckService {
	
	private Logger logger = LoggerFactory.getLogger(CvmCheckService.class);
	
	@Autowired
	private WalletExchangeService walletExchangeService;
	
	@Autowired
	private CvmCheckAdapter cvmCheckAdapter ;
	
	@ServiceActivator
	public String process(String additionalProcessingString) throws Exception {
		// 1.take in an additionalProcessing request
		logger.info("CvmCheckService process method start:" + additionalProcessingString);

		AdditionalProcessingUpiRequest addUPIRequest = (AdditionalProcessingUpiRequest) UtilComponents
				.getObjectFromString(additionalProcessingString, AdditionalProcessingUpiRequest.class);
		logger.info("additionalProcessingString cast to AdditionalProcessingUpiRequest  :" + addUPIRequest);

		// 2.  Detokenization of 2.52 Cvm Request
		CvmCheckRequest cvmCheckRequest=cvmCheckAdapter.convertToCvmCheckRequest(addUPIRequest) ;
		
		String cvmReqString = UtilComponents.getStringFromObject(cvmCheckRequest);
		
		// 3. Htttp Rest Template send CVMCheck Request to Wallet Simulator and recieve
		// CVM Check Response //
		String bankInstitutionCode = cvmCheckRequest.getInstitutionCode() ;
		logger.info("bankInstitutionCode : "+bankInstitutionCode);
		String cvmResponse = walletExchangeService.getWalletResponse(cvmReqString, bankInstitutionCode);
		logger.info("cvmResponse : "+ cvmResponse);

		// 4. exchange Service to send back Additional Processing Response back to
		// Proxy(433)

		// create an addProcessingResponse object and convert back to string
		AdditionalProcessingUpiResponse addResponseObject = createAdditionalProcessingResponse() ;
		String addResponse = UtilComponents.getStringFromObject(addResponseObject) ;

		return addResponse;

	}

	private AdditionalProcessingUpiResponse createAdditionalProcessingResponse() {
		final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		AdditionalProcessingUpiResponse addResponse = new AdditionalProcessingUpiResponse();
		MsgInfo msgInfo = new MsgInfo();
		msgInfo.setVersionNo("1.0.0");
		msgInfo.setMsgId("U39999999000000000000479353");
		msgInfo.setMsgType("ADDITIONAL_PROCESSING");
		msgInfo.setTimeStamp(sdf.format(timestamp));
		msgInfo.setInsID("39999999");
		addResponse.setMsgInfo(msgInfo);
		MessageResponse msgResponse = new MessageResponse();
		msgResponse.setResponseCode("00");
		msgResponse.setResponseMsg("Approved");
		addResponse.setMsgResponse(msgResponse);
		return addResponse;
	}
	
}
