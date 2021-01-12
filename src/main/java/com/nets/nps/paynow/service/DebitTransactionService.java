package com.nets.nps.paynow.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.stereotype.Service;

import com.nets.nps.paynow.entity.DebitTransactionRequest;
import com.nets.nps.paynow.entity.DebitTransactionResponse;
import com.nets.nps.paynow.entity.MessageResponse;
import com.nets.nps.paynow.entity.PullDebitRequest;
import com.nets.nps.paynow.entity.PullDebitResponse;
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

	@Value("${bank.service.base.url}")
	private String bankUrlPrefix;

	@Value("#{${bank.url}}")
	private Map<String, String> bankUrlSuffixMap;
	
	@ServiceActivator
	public String process(String message) {
		logger.info("DebitUpiRequest is: " +message);
		UpiProxyRequest upiProxyRequest = (UpiProxyRequest) UtilComponents.getObjectFromString(message, UpiProxyRequest.class);
		String debitTransactionRequestString = upiProxyRequest.getUpiProxyRequestJsonData();
		DebitTransactionRequest debitTransactionRequest = (DebitTransactionRequest) UtilComponents.getObjectFromString(debitTransactionRequestString, DebitTransactionRequest.class);
		
		//TODO tokenized
		
		// convert to pull debit req
		PullDebitRequest pullDebitRequest = pullDebitRequestAdapter.convertToPullDebitRequest(debitTransactionRequest);
		
		String postUrl = getPostUrl(debitTransactionRequest.getMsgInfo().getMsgType());
		HttpEntity<String> entity = convertRequestToHttpEntity(pullDebitRequest);
		ResponseEntity<String> bankResponse = walletAdapter.sendAndReceiveFromBank(postUrl, entity);
		
		PullDebitResponse pullDebitResponse = (PullDebitResponse) UtilComponents.getObjectFromString(bankResponse.getBody(), PullDebitResponse.class);
		String debitTransactionResponseString = createDebitTransactionResponse(debitTransactionRequest, pullDebitResponse);
		
		logger.info(debitTransactionResponseString);
		String responseString = createResponseAndChangeToString(upiProxyRequest, debitTransactionResponseString);
		logger.info(debitTransactionResponseString);
		return responseString;
	}
	
	private String getPostUrl(String msgType) {
		String bankSuffix = bankUrlSuffixMap.get(msgType);
		String postUrl = bankUrlPrefix + bankSuffix;
		logger.info("Sending to url: " + postUrl);
		return postUrl;
	}

	private HttpEntity<String> convertRequestToHttpEntity(PullDebitRequest pullDebitRequest) {
		String pullDebitRequestString = UtilComponents.getStringFromObject(pullDebitRequest);
		HttpEntity<String> entity = new HttpEntity<String>(pullDebitRequestString);
		return entity;
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
	
	private String createDebitTransactionResponse(DebitTransactionRequest debitTransactionRequest, PullDebitResponse pullDebitResponse) {
		DebitTransactionResponse debitTransactionResponse = new DebitTransactionResponse();
		debitTransactionResponse.setMsgInfo(debitTransactionRequest.getMsgInfo());
		MessageResponse msgResponse = new MessageResponse();
		msgResponse.setResponseCode(pullDebitResponse.getResponseCode());
		if(pullDebitResponse.getResponseCode().equals("00")) {
			msgResponse.setResponseMsg("Approved");
		}
		debitTransactionResponse.setMsgResponse(msgResponse);
		
		String debitTransactionResponseString = UtilComponents.getStringFromObject(debitTransactionResponse);
		
		return debitTransactionResponseString;
	}
}
