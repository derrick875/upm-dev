package com.nets.nps.upi.service;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.stereotype.Service;

import com.nets.nps.paynow.utils.UtilComponents;
import com.nets.nps.upi.entity.AdditionalProcessingUpi;
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
	
	@Autowired
	private UpiAddProcessingService upiAddProcessingService;
	
	@ServiceActivator
	public String process(String additionalProcessingString) throws Exception {
		// 1.take in an additionalProcessing request
		logger.info("CvmCheckService process method start:" + additionalProcessingString);

		AdditionalProcessingUpiRequest addUPIRequest = (AdditionalProcessingUpiRequest) UtilComponents
				.getObjectFromString(additionalProcessingString, AdditionalProcessingUpiRequest.class);
		logger.info("additionalProcessingString cast to AdditionalProcessingUpiRequest  :" + addUPIRequest);
		
		//use incoming Additional Processing Request, save to Additional Processing Upi
		AdditionalProcessingUpi adObj = saveAdditionalProcessingUpi(addUPIRequest) ;

		// 2.  Detokenization of 2.52 Cvm Request
		CvmCheckRequest cvmCheckRequest=cvmCheckAdapter.convertToCvmCheckRequest(adObj) ;
		
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
		AdditionalProcessingUpiResponse addResponseObject = createAdditionalProcessingResponse(adObj) ;
		String addResponse = UtilComponents.getStringFromObject(addResponseObject) ;

		return addResponse;

	}

	private AdditionalProcessingUpiResponse createAdditionalProcessingResponse(AdditionalProcessingUpi adObj) {
//		final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
//		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		AdditionalProcessingUpiResponse addResponse = new AdditionalProcessingUpiResponse();
		MsgInfo msgInfo = new MsgInfo();
		msgInfo.setVersionNo(adObj.getVersionNo() );
		msgInfo.setMsgId(adObj.getMsgID() );
		msgInfo.setMsgType(adObj.getMsgType() );
		msgInfo.setTimeStamp(adObj.getTimeStamp() );
		msgInfo.setInsID(adObj.getInsID() );
		addResponse.setMsgInfo(msgInfo);
		MessageResponse msgResponse = new MessageResponse();
		msgResponse.setResponseCode("00");
		msgResponse.setResponseMsg("Approved");
		addResponse.setMsgResponse(msgResponse);
		logger.info("addResponse" + addResponse.toString() );

		return addResponse;
	}
	
	public AdditionalProcessingUpi saveAdditionalProcessingUpi(AdditionalProcessingUpiRequest addUPIRequest) {
		AdditionalProcessingUpi additionalProcessingUpi = new AdditionalProcessingUpi() ;
		additionalProcessingUpi.setBarcodeCpqrcPayload(" ");
		additionalProcessingUpi.setEmvCpqrcPayload(addUPIRequest.getTrxInfo().getEmvCpqrcPayload());
		additionalProcessingUpi.setInsID(addUPIRequest.getMsgInfo().getInsID());
		additionalProcessingUpi.setMsgID(addUPIRequest.getMsgInfo().getMsgId());
		additionalProcessingUpi.setTimeStamp(addUPIRequest.getMsgInfo().getTimeStamp());
		additionalProcessingUpi.setDeviceID(addUPIRequest.getTrxInfo().getDeviceID());
		additionalProcessingUpi.setMsgType(addUPIRequest.getMsgInfo().getMsgType());
		additionalProcessingUpi.setVersionNo(addUPIRequest.getMsgInfo().getVersionNo());
		additionalProcessingUpi.setMerchantName(addUPIRequest.getTrxInfo().getMerchantName());
		additionalProcessingUpi.setToken(addUPIRequest.getTrxInfo().getToken() );
		additionalProcessingUpi.setTrxAmt(addUPIRequest.getTrxInfo().getTrxAmt());
		additionalProcessingUpi.setTrxCurrency(addUPIRequest.getTrxInfo().getTrxCurrency() );
		upiAddProcessingService.save(additionalProcessingUpi) ;
		logger.info("additionalProcessingUpi:  "+ additionalProcessingUpi.log());
		
		return additionalProcessingUpi;
		
	}
}
