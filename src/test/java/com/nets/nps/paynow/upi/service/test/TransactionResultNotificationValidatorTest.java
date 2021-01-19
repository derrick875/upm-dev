package com.nets.nps.paynow.upi.service.test;

import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.junit.Before;
import org.junit.Test;

import com.nets.nps.upi.entity.MsgInfo;
import com.nets.nps.upi.entity.TransactionResultNotificationRequest;
import com.nets.nps.upi.entity.TransactionResultTrxInfo;
import com.nets.nps.upi.service.impl.TransactionResultNotificationRequestValidator;
import com.nets.upos.commons.exception.JsonFormatException;

@RunWith(JUnit4.class)
public class TransactionResultNotificationValidatorTest {
	
	private TransactionResultNotificationRequest request;
	private MsgInfo msgInfo = new MsgInfo();
	private TransactionResultTrxInfo trxInfo;
	private TransactionResultNotificationRequestValidator unit;
	
	@Before
	public void setUp() {
		request = createTransactionResultNotificationRequest();
		msgInfo = new MsgInfo();
		trxInfo = new TransactionResultTrxInfo();
		unit = new TransactionResultNotificationRequestValidator();
	}
	private TransactionResultNotificationRequest createTransactionResultNotificationRequest() {
		TransactionResultNotificationRequest request = new TransactionResultNotificationRequest();
		MsgInfo msgInfo = new MsgInfo();
		msgInfo.setVersionNo("1.0.0");
		msgInfo.setMsgId("U39990029000000000000479381");
		msgInfo.setTimeStamp("20190808110119");
		msgInfo.setMsgType("TRX_RESULT_NOTIFICATION");
		msgInfo.setInsID("39990029");
		request.setMsgInfo(msgInfo);
		
		TransactionResultTrxInfo trxInfo = new TransactionResultTrxInfo();
		trxInfo.setDeviceId("1b5ddc2562a8de5b4e175d418f5b7edf");
		trxInfo.setToken("6292610358444335641");
		trxInfo.setTrxMsgType("CPQRC_PAYMENT");
		trxInfo.setTrxAmt("2.22");
		trxInfo.setTrxCurrency("344");
		trxInfo.setMerchantName("testing merchant Macau MAC");
		trxInfo.setQrcVoucherNo("20196220228224364010");
		trxInfo.setPaymentStatus("APPROVED");
		trxInfo.setRejectionReason("APPROVED");
		request.setTrxInfo(trxInfo);
		
		return request;
	}
	@Test
	public void validateRetrievalRefNullValue() throws JsonFormatException{
		unit.validate(request);
	}
}
