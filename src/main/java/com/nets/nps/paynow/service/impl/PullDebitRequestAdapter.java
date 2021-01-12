package com.nets.nps.paynow.service.impl;

import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
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
		pullDebitRequest.setTransmissionTime(debitTransactionRequest.getMsgInfo().getTimeStamp().substring(4));
//		pullDebitRequest.setSofAccountId(debitTransactionRequest.getTrxInfo()); need to add after tokenization and use the token 
		
		transactionDomainData.setAmount(StringUtils.leftPad(debitTransactionRequest.getTrxInfo().getTrxAmt(), 12, "0"));
		transactionDomainData.setAmountCurrency(debitTransactionRequest.getTrxInfo().getTrxCurrency());
//		transactionDomainData.setDiscount("");
		transactionDomainData.setFee(StringUtils.leftPad(debitTransactionRequest.getTrxInfo().getFeeAmt(), 12, "0"));
		transactionDomainData.setConvertedAmount(StringUtils.leftPad(debitTransactionRequest.getTrxInfo().getSettAmt(), 12, "0"));
		transactionDomainData.setConvertCurrency(debitTransactionRequest.getTrxInfo().getSettCurrency());
		transactionDomainData.setConversionRate(debitTransactionRequest.getTrxInfo().getSettConvRate());
		transactionDomainData.setTransactionType("1");
//		transactionDomainData.setCpmqrpaymentToken("dkwhatthis"); double check
		pullDebitRequest.setTransactionDomainData(transactionDomainData);
		
		return pullDebitRequest;
	}
}
