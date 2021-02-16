package com.nets.nps.upi.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nets.nps.paynow.utils.UtilComponents;
import com.nets.nps.upi.entity.AdditionalProcessingUpi;
import com.nets.nps.upi.entity.CvmCheckRequest;
import com.nets.nps.upi.entity.CvmCheckTransactionDomainData;
import com.nets.nps.upi.entity.UpiQrcGeneration;
import com.nets.upos.commons.logger.ApsLogger;

@Service
public class CvmCheckAdapter {
	private static final ApsLogger logger = new ApsLogger(CvmCheckAdapter.class);
	
	@Autowired
	public UpiQrcGenerationService upiQrcGenerationService ;

//find cpmtoken in upi_qrc_generation table by additionalProcessingUpi.getEmvCpqrcPayload()
	
	public CvmCheckRequest convertToCvmCheckRequest(AdditionalProcessingUpi additionalProcessingUpi) {
		logger.info("Converting to CvmCheckRequest");
		// test netsTokenId non null
		String netsTokenId = additionalProcessingUpi.getToken();
		logger.info("netsTokenId: " + netsTokenId) ;
		
			UpiQrcGeneration upiQrcGeneration = upiQrcGenerationService.getUpiQrcGeneration(additionalProcessingUpi.getEmvCpqrcPayload() ) ;
			logger.info("upiQrcGeneration"+ upiQrcGeneration.log() );

		CvmCheckRequest cvmCheckRequest = new CvmCheckRequest();
//		String jsonString = "{ \"retrievalRef\": \"123456781900\", \"institutionCode\": \"30000000001\", \"acquirerInstitutionCode\": \"30000000033\", \"sofUri\": \"thisisthesofuri\", \"transmissionTime\": \"0608123413\", \"sofAccountId\": \"sof_account_id\", \"transaction_domain_data\": { \"selected_qrpayload\": \"000000000460\", \"transaction_amount\": \"000000001000\", \"amount_currency\": \"SGD\", \"merchant_name\": \"Testing\", \"cpmqrpayment_token\": \"2aytia\" } }";
//		cvmCheckRequest = (CvmCheckRequest) UtilComponents.getObjectFromString(jsonString, CvmCheckRequest.class);
		
		cvmCheckRequest.setAcquirerInstitutionCode(upiQrcGeneration.getAcquirerInstitutionCode() );
//		cvmCheckRequest.setAcquirerInstitutionCode("30000000001"); //set value from upi_qrc_generation table
		cvmCheckRequest.setInstitutionCode(additionalProcessingUpi.getInsID());
		cvmCheckRequest.setRetrievalRef(upiQrcGeneration.getRetrievalRef() ); //set value from upi_qrc_generation table
		cvmCheckRequest.setSofAccountId(additionalProcessingUpi.getToken());
		cvmCheckRequest.setSofUri(upiQrcGeneration.getSofUri() ); //set value from upi_qrc_generation table
		cvmCheckRequest.setTransmissionTime(additionalProcessingUpi.getTimeStamp()); //set value from upi_qrc_generation table
		CvmCheckTransactionDomainData transactionDomainData = new CvmCheckTransactionDomainData();
		transactionDomainData.setAmountCurrency(additionalProcessingUpi.getTrxCurrency() );
		transactionDomainData.setCpmQrPaymentToken(upiQrcGeneration.getCpmqrPaymentToken() );  //set cpm token from upi_qrc_generation table
		transactionDomainData.setSelectedQrPayLoad(additionalProcessingUpi.getEmvCpqrcPayload());// either emv/ barcode format
		transactionDomainData.setTransactionAmount(additionalProcessingUpi.getTrxAmt() );
		cvmCheckRequest.setTransactionDomainData(transactionDomainData);
		
		
//		logger.info("CvmCheckRequest before detoken: " + cvmCheckRequest.log());
		cvmCheckRequest.setSofAccountId(netsTokenId);
		logger.info("CvmCheckRequest after detoken: " + cvmCheckRequest.log());
		return cvmCheckRequest;
	}

}
