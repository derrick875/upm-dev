package com.nets.nps.paynow.service.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.nets.nps.paynow.entity.DebitTransactionRequest;
import com.nets.nps.paynow.entity.DebitTransactionResponse;
import com.nets.nps.paynow.entity.MessageResponse;
import com.nets.nps.paynow.entity.PullDebitRequest;
import com.nets.nps.paynow.entity.PullDebitResponse;
import com.nets.nps.paynow.utils.UtilComponents;
import com.nets.upos.commons.logger.ApsLogger;

@Service
public class WalletAdapter {

	private static final ApsLogger logger = new ApsLogger(WalletAdapter.class);

	@Autowired
	@Qualifier("oneWay")
	private RestTemplate restTemplate;

	@Value("${bank.service.base.url}")
	private String bankUrlPrefix;

	@Value("#{${bank.url}}")
	private Map<String, String> bankUrlSuffixMap;

	ResponseEntity<String> bankResponse;

	public String sendAndReceiveFromBank(PullDebitRequest pullDebitRequest,DebitTransactionRequest debitTransactionRequest ) {

		String bankSuffix = bankUrlSuffixMap.get(pullDebitRequest.getInstitutionCode());
		String postUrl = bankUrlPrefix + bankSuffix;
		logger.info("Sending to url: " + postUrl);
		
		String pullDebitRequestString = UtilComponents.getStringFromObject(pullDebitRequest);
		HttpEntity<String> entity = new HttpEntity<String>(pullDebitRequestString);
		
		bankResponse = restTemplate.exchange(postUrl, HttpMethod.POST, entity, String.class);
		logger.info("Bank Response: " + bankResponse.getBody());

		PullDebitResponse pullDebitResponse = (PullDebitResponse) UtilComponents.getObjectFromString(bankResponse.getBody(), PullDebitResponse.class);
		String debitTransactionResponseString = createDebitTransactionResponse(debitTransactionRequest, pullDebitResponse);

		return debitTransactionResponseString;
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
