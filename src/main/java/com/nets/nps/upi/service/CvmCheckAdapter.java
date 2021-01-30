package com.nets.nps.upi.service;

import org.springframework.stereotype.Service;
import com.nets.nps.paynow.utils.UtilComponents;
import com.nets.nps.upi.entity.AdditionalProcessingUpiRequest;
import com.nets.nps.upi.entity.CvmCheckRequest;
import com.nets.upos.commons.logger.ApsLogger;

@Service
public class CvmCheckAdapter {
	private static final ApsLogger logger = new ApsLogger(CvmCheckAdapter.class);

	public CvmCheckRequest convertToCvmCheckRequest(AdditionalProcessingUpiRequest addRequest) {
		logger.info("Converting to CvmCheckRequest");
		// test netsTokenId non null
		String netsTokenId = addRequest.getTrxInfo().getToken();

		CvmCheckRequest cvmCheckRequest = new CvmCheckRequest();
		String jsonString = "{ \"retrievalRef\": \"123456781900\", \"institutionCode\": \"30000000001\", \"acquirerInstitutionCode\": \"30000000033\", \"sofUri\": \"thisisthesofuri\", \"transmissionTime\": \"0608123413\", \"sofAccountId\": \"sof_account_id\", \"transaction_domain_data\": { \"selected_qrpayload\": \"000000000460\", \"transaction_amount\": \"000000001000\", \"amount_currency\": \"SGD\", \"merchant_name\": \"Testing\", \"cpmqrpayment_token\": \"2aytia\" } }";
		cvmCheckRequest = (CvmCheckRequest) UtilComponents.getObjectFromString(jsonString, CvmCheckRequest.class);
		logger.info("CvmCheckRequest before detoken: " + cvmCheckRequest.log());
		cvmCheckRequest.setSofAccountId(netsTokenId);
		logger.info("CvmCheckRequest after detoken: " + cvmCheckRequest.log());
		return cvmCheckRequest;
	}

}
