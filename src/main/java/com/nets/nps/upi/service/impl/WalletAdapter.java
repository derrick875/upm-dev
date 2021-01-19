package com.nets.nps.upi.service.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.nets.nps.paynow.utils.UtilComponents;
import com.nets.nps.upi.entity.DebitTransactionRequest;
import com.nets.nps.upi.entity.DebitTransactionResponse;
import com.nets.nps.upi.entity.MessageResponse;
import com.nets.nps.upi.entity.PullDebitRequest;
import com.nets.nps.upi.entity.PullDebitResponse;
import com.nets.upos.commons.logger.ApsLogger;

@Service
public class WalletAdapter {

	private static final ApsLogger logger = new ApsLogger(WalletAdapter.class);

	@Autowired
	@Qualifier("oneWay")
	private RestTemplate restTemplate;
	
	ResponseEntity<String> bankResponse;

	public ResponseEntity<String> sendAndReceiveFromBank(String postUrl, HttpEntity<String> entity) {
		
		bankResponse = restTemplate.exchange(postUrl, HttpMethod.POST, entity, String.class);
		logger.info("Bank Response: " + bankResponse.getBody());

		return bankResponse;
	}
}
