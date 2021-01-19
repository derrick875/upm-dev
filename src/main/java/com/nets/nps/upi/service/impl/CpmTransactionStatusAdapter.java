package com.nets.nps.upi.service.impl;

import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.nets.nps.paynow.utils.UtilComponents;
import com.nets.nps.upi.entity.CpmTransactionStatusNotificationRequest;
import com.nets.nps.upi.entity.CpmTransactionStatusNotificationRequestTransactionDomainData;
import com.nets.nps.upi.entity.TransactionResultNotificationRequest;
import com.nets.upos.commons.exception.BaseBusinessException;
import com.nets.upos.commons.logger.ApsLogger;

@Service
public class CpmTransactionStatusAdapter {
	
	private static final ApsLogger logger = new ApsLogger(CpmTransactionStatusAdapter.class);

	public static final String upiAcquirerInsCode = "40000000001";

	@Value("#{${nets.upi.appgateway.instId.map}}")
	private Map<String, String> instIdMap;
	
	@Value("#{${nets.upi.appgateway.uri.map}}")
	private Map<String, String> sofUriMap;
	
	public CpmTransactionStatusNotificationRequest convertToCpmTransactionStatusRequest(TransactionResultNotificationRequest transactionResultNotificationRequest) throws BaseBusinessException {
		logger.info("Converting to TransactionResultNotificationRequest");
		
		String netsInsId = UtilComponents.getKey(instIdMap, transactionResultNotificationRequest.getMsgInfo().getInsID());
		String sofUri = sofUriMap.get(netsInsId);
		
		CpmTransactionStatusNotificationRequest cpmTransactionRequest = new CpmTransactionStatusNotificationRequest();
		CpmTransactionStatusNotificationRequestTransactionDomainData transactionDomainData = new CpmTransactionStatusNotificationRequestTransactionDomainData();
		cpmTransactionRequest.setRetrievalRef(UUID.randomUUID().toString());
		cpmTransactionRequest.setInstitutionCode(netsInsId);
		cpmTransactionRequest.setAcquirerInstitutionCode(upiAcquirerInsCode);
		cpmTransactionRequest.setSofUri(sofUri);
		cpmTransactionRequest.setTransmissionTime(transactionResultNotificationRequest.getMsgInfo().getTimeStamp().substring(4));
//		cpmTransactionRequest.setSofAccountId(sofAccountId); ??
		
		transactionDomainData.setPaymentReceiptData(transactionResultNotificationRequest.getTrxInfo().getTrxNote()); //double check
		transactionDomainData.setCpmqrpaymentToken(transactionResultNotificationRequest.getTrxInfo().getToken());
		cpmTransactionRequest.setTransactionDomainData(transactionDomainData);
		
		return cpmTransactionRequest;
	}
}
