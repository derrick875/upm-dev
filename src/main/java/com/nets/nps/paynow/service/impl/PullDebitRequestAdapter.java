package com.nets.nps.paynow.service.impl;

import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.nets.nps.paynow.entity.DebitTransactionRequest;
import com.nets.nps.paynow.entity.PullDebitRequest;
import com.nets.nps.paynow.entity.PullDebitRequestTransactionDomainData;
import com.nets.nps.paynow.utils.UtilComponents;
import com.nets.upos.commons.logger.ApsLogger;

@Service
public class PullDebitRequestAdapter {

	private static final ApsLogger logger = new ApsLogger(PullDebitRequestAdapter.class);

	public static final String upiAcquirerInsCode = "40000000001";

	@Value("#{${nets.upi.appgateway.instId.map}}")
	private Map<String, String> instIdMap;
	
	@Value("#{${nets.upi.appgateway.uri.map}}")
	private Map<String, String> sofUriMap;
	
	public PullDebitRequest convertToPullDebitRequest(DebitTransactionRequest debitTransactionRequest) {
		logger.info("Converting to PullDebitRequest");
		
		String netsInsId = UtilComponents.getKey(instIdMap, debitTransactionRequest.getMsgInfo().getInsID());
		String sofUri = sofUriMap.get(netsInsId);
		//TODO check for null for these 2?
		
		PullDebitRequest pullDebitRequest = new PullDebitRequest();
		PullDebitRequestTransactionDomainData transactionDomainData = new PullDebitRequestTransactionDomainData();
		pullDebitRequest.setRetrievalRef(UUID.randomUUID().toString());
		pullDebitRequest.setInstitutionCode(netsInsId);
		pullDebitRequest.setAcquirerInstitutionCode(upiAcquirerInsCode); // 
		pullDebitRequest.setSofUri(sofUri);
		pullDebitRequest.setTransmissionTime(debitTransactionRequest.getTrxInfo().getTransDatetime());
//		pullDebitRequest.setSofAccountId(debitTransactionRequest.getTrxInfo()); need to add after tokenization and use the token 
		
		transactionDomainData.setAmount(debitTransactionRequest.getTrxInfo().getTrxAmt());
		transactionDomainData.setAmountCurrency(debitTransactionRequest.getTrxInfo().getTrxCurrency());
//		transactionDomainData.setDiscount("");
		transactionDomainData.setFee(debitTransactionRequest.getTrxInfo().getFeeAmt());
		transactionDomainData.setConvertedAmount(debitTransactionRequest.getTrxInfo().getSettAmt());
		transactionDomainData.setConvertCurrency(debitTransactionRequest.getTrxInfo().getSettCurrency());
		transactionDomainData.setConversionRate(debitTransactionRequest.getTrxInfo().getSettConvRate());
		transactionDomainData.setTransactionType("1");
//		transactionDomainData.setCpmqrpaymentToken("dkwhatthis"); double check
		pullDebitRequest.setTransactionDomainData(transactionDomainData);
		
		return pullDebitRequest;
	}
}
