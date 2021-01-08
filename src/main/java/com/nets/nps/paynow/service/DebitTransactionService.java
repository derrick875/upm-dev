package com.nets.nps.paynow.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.stereotype.Service;

import com.nets.nps.paynow.entity.DebitTransactionRequest;
import com.nets.nps.paynow.entity.PullDebitRequest;
import com.nets.nps.paynow.entity.UpiProxyRequest;
import com.nets.nps.paynow.service.impl.PullDebitRequestAdapter;
import com.nets.nps.paynow.service.impl.WalletAdapter;
import com.nets.nps.paynow.utils.UtilComponents;
import com.nets.upos.commons.logger.ApsLogger;

@Service
public class DebitTransactionService {

	private static final ApsLogger logger = new ApsLogger(DebitTransactionService.class);
	
	@Autowired
	PullDebitRequestAdapter pullDebitRequestAdapter;
	
	@Autowired
	WalletAdapter walletAdapter;

	@ServiceActivator
	public String process(String message) {
		logger.info("DebitUpiRequest is: " +message);
		UpiProxyRequest upiProxyRequest = (UpiProxyRequest) UtilComponents.getObjectFromString(message, UpiProxyRequest.class);
		String debitTransactionRequestString = upiProxyRequest.getUpiProxyRequestJsonData();
		DebitTransactionRequest debitTransactionRequest = (DebitTransactionRequest) UtilComponents.getObjectFromString(debitTransactionRequestString, DebitTransactionRequest.class);
		
		//TODO tokenized
		
		// convert to pull debit req
		PullDebitRequest pullDebitRequest = pullDebitRequestAdapter.convertToPullDebitRequest(debitTransactionRequest);
		
		String debitTransactionResponseString = walletAdapter.sendAndReceiveFromBank(pullDebitRequest, debitTransactionRequest);
		
		String responseString = createResponseAndChangeToString(upiProxyRequest, debitTransactionResponseString);
	
		return responseString;
	}
	
	private String createResponseAndChangeToString(UpiProxyRequest upiProxyRequest, String debitTransactionResponseString) {
		UpiProxyRequest response = new UpiProxyRequest();
		response.setBankId(upiProxyRequest.getBankId());
		response.setBankType(upiProxyRequest.getBankType());
		response.setCorrelationId(upiProxyRequest.getCorrelationId());
		response.setInstCode(upiProxyRequest.getInstCode());
		response.setTransactionType(upiProxyRequest.getTransactionType());
		response.setUpiProxyRequestJsonData(debitTransactionResponseString);
		
		String responseString = UtilComponents.getStringFromObject(response);
		return responseString;
	}
}
