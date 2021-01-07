package com.nets.nps.paynow.service.impl;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.nets.nps.paynow.entity.DebitTransactionRequest;
import com.nets.nps.paynow.entity.PullDebitRequest;
import com.nets.nps.paynow.entity.PullDebitRequestTransactionDomainData;
import com.nets.upos.commons.logger.ApsLogger;

@Service
public class PullDebitRequestAdapter {

	private static final ApsLogger logger = new ApsLogger(PullDebitRequestAdapter.class);

	public PullDebitRequest convertToPullDebitRequest(DebitTransactionRequest debitTransactionRequest) {
		logger.info("Converting to PullDebitRequest");
		PullDebitRequest pullDebitRequest = new PullDebitRequest();
		PullDebitRequestTransactionDomainData transactionDomainData = new PullDebitRequestTransactionDomainData();
		
		pullDebitRequest.setRetrievalRef(UUID.randomUUID().toString());
		pullDebitRequest.setInstitutionCode("30000000001");
		pullDebitRequest.setAcquirerInstitutionCode("30000000033");
		pullDebitRequest.setSofUri("thisisthesofuri");
		
		transactionDomainData.setAmount(debitTransactionRequest.getTrxInfo().getTrxAmt());
		transactionDomainData.setAmountCurrency(debitTransactionRequest.getTrxInfo().getTrxCurrency());
		transactionDomainData.setDiscount("");
		transactionDomainData.setFee(debitTransactionRequest.getTrxInfo().getFeeAmt());
		transactionDomainData.setConvertedAmount(debitTransactionRequest.getTrxInfo().getSettAmt());
		transactionDomainData.setConvertCurrency(debitTransactionRequest.getTrxInfo().getSettCurrency());
		transactionDomainData.setConversionRate(debitTransactionRequest.getTrxInfo().getSettConvRate());
		transactionDomainData.setTransactionType("1");
		transactionDomainData.setCpmqrpaymentToken("dkwhatthis");
		pullDebitRequest.setTransactionDomainData(transactionDomainData);
		
		return pullDebitRequest;
	}
}
