package com.nets.nps.client.impl;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.nets.nps.paynow.entity.QrcGenerationRequest;
import com.nets.nps.paynow.entity.UpiQrcGenerationRequestTransactionRequest;
import com.nets.nps.paynow.entity.UpiQrcGenerationRequest;
import com.nets.nps.paynow.utils.UtilComponents;
import com.nets.upos.commons.logger.ApsLogger;

@Service
public class UpiQrcGenerationRequestAdapter {
	
	 private static final ApsLogger logger = new ApsLogger(UpiQrcGenerationRequestAdapter.class);
	 
	 @Value("${deviceId}")
	 private String deviceId;
	 
	 @Value("${userId}")
	 private String userId;
	 
	 public UpiQrcGenerationRequest handleQrGenReq(QrcGenerationRequest qrcGenerationRequest) {
		 logger.info("UpiQrcGenerationRequestAdapter: handleQrcGenerationRequest service started");
		 UpiQrcGenerationRequest upiQrcGenerationRequest = createUpiQrcGenerationRequest(qrcGenerationRequest);
		return upiQrcGenerationRequest; 
	 }

	private UpiQrcGenerationRequest createUpiQrcGenerationRequest(QrcGenerationRequest qrGenerationRequest) {
		UpiQrcGenerationRequest upiQrcGenerationRequest = new UpiQrcGenerationRequest();
		MsgInfo msgInfo = new MsgInfo();
		UpiQrcGenerationRequestTransactionRequest trxInfo = new UpiQrcGenerationRequestTransactionRequest();
		
		Timestamp currentTimeStamp = new Timestamp(Calendar.getInstance().getTimeInMillis());
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddhhmmss");
        
        String msgId = UtilComponents.getMsgId(qrGenerationRequest.getInstitutionCode());
        
		msgInfo.setVersionNo("1.0.0");
		msgInfo.setMsgId(msgId);
		msgInfo.setTimeStamp(formatter.format(currentTimeStamp));
		msgInfo.setMsgType("QRC_GENERATION");
		msgInfo.setInsID(qrGenerationRequest.getInstitutionCode());
		
		trxInfo.setDeviceID(deviceId);
		trxInfo.setUserID(userId);
		trxInfo.setToken(qrGenerationRequest.getSofAccountId());
		trxInfo.setTrxLimit(qrGenerationRequest.getTransactionDomainData().getTxnLimitAmount());
		trxInfo.setCvmLimit(qrGenerationRequest.getTransactionDomainData().getCvmLimitAmount());
		trxInfo.setLimitCurrency(qrGenerationRequest.getTransactionDomainData().getAmountLimitCurrency());
		trxInfo.setCpqrcNo("01");

		upiQrcGenerationRequest.setMsgInfo(msgInfo);
		upiQrcGenerationRequest.setTrxInfo(trxInfo);
		
		return upiQrcGenerationRequest;
	}
	 
	 
	 
	 
	 
	 
	 
}
