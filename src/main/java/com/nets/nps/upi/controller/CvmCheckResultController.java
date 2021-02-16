package com.nets.nps.upi.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.nets.nps.paynow.utils.UtilComponents;
import com.nets.nps.upi.entity.AdditionalProcessingResultRequest;
import com.nets.nps.upi.entity.AdditionalProcessingResultTransactionInfo;
import com.nets.nps.upi.entity.AdditionalProcessingUpi;
import com.nets.nps.upi.entity.AdditionalProcessingUpiResponse;
import com.nets.nps.upi.entity.CvmCheckEntity;
import com.nets.nps.upi.entity.CvmCheckResponse;
import com.nets.nps.upi.entity.CvmCheckResultRequest;
import com.nets.nps.upi.entity.MsgInfo;
import com.nets.nps.upi.service.CvmCheckEntityService;
import com.nets.nps.upi.service.UpiAddProcessingService;
import com.nets.nps.upi.service.impl.CvmCheckResultService;


@RestController
public class CvmCheckResultController {
	
	@Autowired
	private CvmCheckResultService cvmCheckResultService ;
	
	@Autowired
	private UpiAddProcessingService upiAddProcessingService;
	
	@Autowired 
	private CvmCheckEntityService cvmCheckEntityService;
	
	Logger log = LoggerFactory.getLogger(CvmCheckResultController.class);
	
	@PostMapping("/cvmcheckresultrequest")
	public ResponseEntity<String> sendCvmResultResponse(@RequestBody CvmCheckResultRequest cvmCheckResultRequest) {
		// Take in CvmCheckResultRequest
		CvmCheckResponse cvmCheckResponse = new CvmCheckResponse();
		AdditionalProcessingResultRequest adReq = new AdditionalProcessingResultRequest() ;
		String cvmResultString = null;

		try {
			log.info("Start CvmCheckResult Controller:  " + cvmCheckResultRequest.log());
			
						
			// create a Cvm Check Result Response
			cvmCheckResponse = createCvmCheckResponse(cvmCheckResultRequest) ;
			log.info("Created CvmCheck Result Response:   " + cvmCheckResponse.log());
			
			
			//get cpm_qr_token from cvmcheckresultrequest and find from cvmcheck table(tbl_1_cvm_check_entity)
//			AdditionalProcessingUpi additionalProcessingUpi = 
			
			
			// create a Additional Processing Result Request 

					
			//create a  AdditionalProcessing Result Response  
			AdditionalProcessingUpiResponse response = cvmCheckResultService.process(adReq) ;
			log.info("Additional Processing Response sent back from Proxy:   " + response.toString() );
			
			cvmResultString = UtilComponents.getStringFromObject(cvmCheckResponse);
			log.debug("End CvmCheckResult Controller:  " + cvmResultString );

			return new ResponseEntity<>(cvmResultString, HttpStatus.OK);

		} catch (Exception e) {
			log.debug("error msg:" + e.toString());
			return new ResponseEntity<>(cvmResultString, HttpStatus.INTERNAL_SERVER_ERROR);

		}
		

	}
	
	
	public AdditionalProcessingResultRequest createAddProResultRequest(CvmCheckResultRequest cvmCheckRequest) {
		AdditionalProcessingResultRequest adRequest = new AdditionalProcessingResultRequest() ;
		log.debug("Creating a new Additional Processing Result Request  -");
		CvmCheckEntity cvmCheckEntity = cvmCheckEntityService.getCvmCheckEntity(cvmCheckRequest.getTransactionDomainData().getCpmQrPaymentToken() ) ;
		log.info("cvmCheckEntity"+ cvmCheckEntity.toString() );
		String cpmToken = cvmCheckEntity.getCpmQrPaymentToken() ;
		String payLoad = cvmCheckEntity.getSelectedQrPayLoad() ;
		AdditionalProcessingUpi addUpi = upiAddProcessingService.getByEmvCpqrcPayload(payLoad) ; //check either for emv or barcode 
		
		
		MsgInfo msgInfo = new MsgInfo() ;
		msgInfo.setMsgId(addUpi.getMsgID() );
		msgInfo.setVersionNo(addUpi.getVersionNo() );
		msgInfo.setInsID(addUpi.getInsID() );
		msgInfo.setTimeStamp(addUpi.getTimeStamp() );
		msgInfo.setMsgType("ADDITIONAL_PROCESSING_RESULT");
		adRequest.setMsgInfo(msgInfo);
		AdditionalProcessingResultTransactionInfo trxInfo = new AdditionalProcessingResultTransactionInfo() ;
//		trxInfo.setDeviceID("1b5ddc2562a8de5b4e175d418f5b7edf");
		trxInfo.setDeviceID(addUpi.getDeviceID() ) ;
		trxInfo.setUserID("userId");
		//tokenization- set nets identifier token before sending to UPI
		trxInfo.setToken(cvmCheckRequest.getSofAccountId());
		trxInfo.setEmvCpqrcPayload(cvmCheckRequest.getTransactionDomainData().getCpmQrPaymentToken());
		trxInfo.setPaymentStatus("CONTINUING");
		adRequest.setTrxInfo(trxInfo);
		return adRequest;
	}
	
	
	public CvmCheckResponse createCvmCheckResponse( CvmCheckResultRequest cvmCheckRequest) {
		CvmCheckResponse cvmCheckResponse = new CvmCheckResponse();
		cvmCheckResponse.setResponseCode("00");
		cvmCheckResponse.setInstitutionCode(cvmCheckRequest.getInstitutionCode());
		cvmCheckResponse.setRetrievalRef(cvmCheckRequest.getRetrievalRef());
		cvmCheckResponse.setTransmissionTime(cvmCheckRequest.getTransmissionTime());
		log.debug("End contoller CvmCheckResult Response " + cvmCheckResponse.log());
		return cvmCheckResponse ;
	}
	
	
	
}
