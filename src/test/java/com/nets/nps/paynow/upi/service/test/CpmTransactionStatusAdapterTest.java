package com.nets.nps.paynow.upi.service.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import com.nets.nps.upi.entity.CpmTransactionStatusNotificationRequest;
import com.nets.nps.upi.entity.MsgInfo;
import com.nets.nps.upi.entity.TransactionResultNotificationRequest;
import com.nets.nps.upi.entity.TransactionResultTrxInfo;
import com.nets.nps.upi.service.impl.CpmTransactionStatusAdapter;
import com.nets.upos.commons.exception.BaseBusinessException;

@SpringBootTest(classes = {CpmTransactionStatusAdapter.class})
@TestPropertySource(locations = "classpath:paynow-local.properties")
public class CpmTransactionStatusAdapterTest {
	
	@Autowired
	CpmTransactionStatusAdapter unit;
	
	private TransactionResultNotificationRequest createTransactionResultNotifReq() {
		TransactionResultNotificationRequest transactionResultNotifReq = new TransactionResultNotificationRequest();
		MsgInfo msgInfo = new MsgInfo();
		msgInfo.setVersionNo("1.0.0");
		msgInfo.setMsgId("U39990029000000000000479381");
		msgInfo.setTimeStamp("20190808110118");
		msgInfo.setMsgType("TRX_RESULT_NOTIFICATION");
		msgInfo.setInsID("39990029");

		transactionResultNotifReq.setMsgInfo(msgInfo);
		
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
		
		transactionResultNotifReq.setTrxInfo(trxInfo);
		
		return transactionResultNotifReq;
	}
	
	@Test
	public void validateSuccessCase() throws BaseBusinessException {
		TransactionResultNotificationRequest transactionResultNotifReq = createTransactionResultNotifReq();
		
		CpmTransactionStatusNotificationRequest cpmTransactionRequest = unit.convertToCpmTransactionStatusRequest(transactionResultNotifReq);
		
		assertNotNull(cpmTransactionRequest.getRetrievalRef());
		assertEquals("30000000001", cpmTransactionRequest.getInstitutionCode());
		assertEquals("40000000001", cpmTransactionRequest.getAcquirerInstitutionCode());
		assertEquals("http://dbs.com", cpmTransactionRequest.getSofUri());
		assertEquals(transactionResultNotifReq.getMsgInfo().getTimeStamp().substring(4), cpmTransactionRequest.getTransmissionTime());
		assertEquals(transactionResultNotifReq.getTrxInfo().getTrxNote(), cpmTransactionRequest.getTransactionDomainData().getPaymentReceiptData());
		assertEquals(transactionResultNotifReq.getTrxInfo().getToken(), cpmTransactionRequest.getTransactionDomainData().getCpmqrpaymentToken());
	}
}
