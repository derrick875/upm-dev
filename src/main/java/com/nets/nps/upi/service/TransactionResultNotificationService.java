package com.nets.nps.upi.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.stereotype.Service;

import com.nets.nps.paynow.utils.UtilComponents;
import com.nets.nps.upi.entity.CpmTransactionStatusNotificationRequest;
import com.nets.nps.upi.entity.CpmTransactionStatusNotificationResponse;
import com.nets.nps.upi.entity.MessageResponse;
import com.nets.nps.upi.entity.TransactionResultNotificationRequest;
import com.nets.nps.upi.entity.TransactionResultNotificationResponse;
import com.nets.nps.upi.service.impl.CpmTransactionStatusAdapter;
import com.nets.nps.upi.service.impl.TransactionResultNotificationRequestValidator;
import com.nets.nps.upi.service.impl.WalletAdapter;
import com.nets.upos.commons.exception.BaseBusinessException;
import com.nets.upos.commons.exception.JsonFormatException;
import com.nets.upos.commons.logger.ApsLogger;

@Service
public class TransactionResultNotificationService {
	
	private static final ApsLogger logger = new ApsLogger(TransactionResultNotificationService.class);
	
	@Autowired
	TransactionResultNotificationRequestValidator transactionResultNotificationRequestValidator;
	
	@Autowired
	CpmTransactionStatusAdapter cpmTransactionStatusAdapter;
	
	@Autowired
	WalletAdapter walletAdapter;
	
	@Value("${bank.service.base.url}")
	private String bankUrlPrefix;

	@Value("#{${bank.url}}")
	private Map<String, String> bankUrlSuffixMap;
	
	@ServiceActivator
	public String process(String message) throws BaseBusinessException, JsonFormatException {
		logger.info("TransactionResultNotif is: " +message);
		TransactionResultNotificationRequest trxResultNotifReq = (TransactionResultNotificationRequest) UtilComponents.getObjectFromString(message, TransactionResultNotificationRequest.class);
		transactionResultNotificationRequestValidator.validate(trxResultNotifReq);
		logger.info(trxResultNotifReq.toString());
		
		//fxTxnBooking
		
		//Convert to CPM Transaction Status object
		CpmTransactionStatusNotificationRequest cpmTransactionRequest = cpmTransactionStatusAdapter.convertToCpmTransactionStatusRequest(trxResultNotifReq);
		
		String postUrl = getPostUrl(trxResultNotifReq.getMsgInfo().getMsgType());
		HttpEntity<String> entity = convertRequestToHttpEntity(cpmTransactionRequest);
		ResponseEntity<String> bankResponse = null;
		bankResponse = walletAdapter.sendAndReceiveFromBank(postUrl, entity);
		// test not null for bankResponse
		CpmTransactionStatusNotificationResponse cpmTransactionResponse = (CpmTransactionStatusNotificationResponse) UtilComponents.getObjectFromString(bankResponse.getBody(), CpmTransactionStatusNotificationResponse.class);
		String trxResultNotifRespString = createTrxResultNotifResp(trxResultNotifReq, cpmTransactionResponse);
		logger.info(trxResultNotifRespString);
		
		return trxResultNotifRespString;
	}

	private String createTrxResultNotifResp(TransactionResultNotificationRequest trxResultNotifReq, CpmTransactionStatusNotificationResponse cpmTransactionResponse) {
		TransactionResultNotificationResponse trxResultNotifResp = new TransactionResultNotificationResponse();
		trxResultNotifResp.setMsgInfo(trxResultNotifReq.getMsgInfo());
		MessageResponse msgResponse = new MessageResponse();
		msgResponse.setResponseCode(cpmTransactionResponse.getResponseCode());
		if(cpmTransactionResponse.getResponseCode().equals("00")) {
			msgResponse.setResponseMsg("Approved");
		}
		trxResultNotifResp.setMsgResponse(msgResponse);
		String trxResultNotifRespString = UtilComponents.getStringFromObject(trxResultNotifResp);
		return trxResultNotifRespString;
	}

	private String getPostUrl(String msgType) throws BaseBusinessException {
		String bankSuffix = bankUrlSuffixMap.get(msgType);
//		notNull.test(bankSuffix).throwIfInvalid(BusinessValidationErrorCodes.INVALID_MERCHANT_ID); // add invalid msg type?? this not throwing??
		String postUrl = bankUrlPrefix + bankSuffix;
		logger.info("Sending to url: " + postUrl);
		return postUrl;
	}
	
	private HttpEntity<String> convertRequestToHttpEntity(CpmTransactionStatusNotificationRequest cpmTransactionRequest) {
		String cpmTransactionRequestString = UtilComponents.getStringFromObject(cpmTransactionRequest);
		HttpEntity<String> entity = new HttpEntity<String>(cpmTransactionRequestString);
		return entity;
	}
}
