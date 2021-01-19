package com.nets.nps.paynow.upi.service.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;

import com.nets.nps.paynow.utils.UtilComponents;
import com.nets.nps.upi.entity.CpmTransactionStatusNotificationRequest;
import com.nets.nps.upi.entity.CpmTransactionStatusNotificationRequestTransactionDomainData;
import com.nets.nps.upi.entity.CpmTransactionStatusNotificationResponse;
import com.nets.nps.upi.entity.MsgInfo;
import com.nets.nps.upi.entity.TransactionResultNotificationRequest;
import com.nets.nps.upi.entity.TransactionResultNotificationResponse;
import com.nets.nps.upi.entity.TransactionResultTrxInfo;
import com.nets.nps.upi.service.TransactionResultNotificationService;
import com.nets.nps.upi.service.impl.CpmTransactionStatusAdapter;
import com.nets.nps.upi.service.impl.WalletAdapter;
import com.nets.upos.commons.exception.BaseBusinessException;
import com.nets.upos.commons.exception.JsonFormatException;


@SpringBootTest(classes = {TransactionResultNotificationService.class})
@TestPropertySource(locations = "classpath:paynow-local.properties")
public class TransactionResultNotificationServiceTest {
	
	@Autowired
	TransactionResultNotificationService unit;
	
	@MockBean
	CpmTransactionStatusAdapter mockCpmTransactionStatusAdapter;
	
	@MockBean
	WalletAdapter mockWalletAdapter;
	
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
	
	private CpmTransactionStatusNotificationRequest createCpmTransactionStatusNotificationRequest(TransactionResultNotificationRequest transactionResultNotifReq) {
		CpmTransactionStatusNotificationRequest cpmTransactionStatusRequest = new CpmTransactionStatusNotificationRequest();
		CpmTransactionStatusNotificationRequestTransactionDomainData transactionDomainData = new CpmTransactionStatusNotificationRequestTransactionDomainData();
		cpmTransactionStatusRequest.setRetrievalRef(UUID.randomUUID().toString());
		cpmTransactionStatusRequest.setInstitutionCode("30000000001");
		cpmTransactionStatusRequest.setAcquirerInstitutionCode("40000000001");
		cpmTransactionStatusRequest.setSofUri("http://dbs.com.sg");
		cpmTransactionStatusRequest.setTransmissionTime(transactionResultNotifReq.getMsgInfo().getTimeStamp().substring(4));
//		cpmTransactionRequest.setSofAccountId(sofAccountId); ??
		
		transactionDomainData.setPaymentReceiptData(transactionResultNotifReq.getTrxInfo().getTrxNote()); //double check
		transactionDomainData.setCpmqrpaymentToken(transactionResultNotifReq.getTrxInfo().getToken());
		cpmTransactionStatusRequest.setTransactionDomainData(transactionDomainData);
		
		return cpmTransactionStatusRequest;
	}
	
	private String createCpmTransactionStatusNotificationResponseString(CpmTransactionStatusNotificationRequest cpmTransactionStatusNotificationReq) {
		CpmTransactionStatusNotificationResponse cpmTransactionResp = new CpmTransactionStatusNotificationResponse();
		cpmTransactionResp.setResponseCode("00");
		cpmTransactionResp.setRetrievalRef(cpmTransactionStatusNotificationReq.getRetrievalRef());
		cpmTransactionResp.setTransmissionTime(cpmTransactionStatusNotificationReq.getTransmissionTime());
		cpmTransactionResp.setInstitutionCode(cpmTransactionStatusNotificationReq.getInstitutionCode());
		
		String cpmTransactionRespString = UtilComponents.getStringFromObject(cpmTransactionResp);
		
		return cpmTransactionRespString;
	}
	@Test
	public void validateSuccessCase() throws BaseBusinessException, JsonFormatException {
		TransactionResultNotificationRequest transactionResultNotifReq = createTransactionResultNotifReq();
		String transactionResultNotifReqString = UtilComponents.getStringFromObject(transactionResultNotifReq);
		
		CpmTransactionStatusNotificationRequest cpmTransactionRequest = createCpmTransactionStatusNotificationRequest(transactionResultNotifReq);
		when(mockCpmTransactionStatusAdapter.convertToCpmTransactionStatusRequest(transactionResultNotifReq)).thenReturn(cpmTransactionRequest);
		
		String cpmTransactionRespString = createCpmTransactionStatusNotificationResponseString(cpmTransactionRequest);
		
		when(mockWalletAdapter.sendAndReceiveFromBank(any(), any(HttpEntity.class))).thenReturn(new ResponseEntity<>(cpmTransactionRespString, HttpStatus.OK));

		String responseString = unit.process(transactionResultNotifReqString);
		
		verify(mockCpmTransactionStatusAdapter, times(1)).convertToCpmTransactionStatusRequest(any(TransactionResultNotificationRequest.class));
		verify(mockWalletAdapter, times(1)).sendAndReceiveFromBank(any(), any(HttpEntity.class));
		
		TransactionResultNotificationResponse response = (TransactionResultNotificationResponse) UtilComponents.getObjectFromString(responseString, TransactionResultNotificationResponse.class);
		
		assertEquals(transactionResultNotifReq.getMsgInfo().getInsID(), response.getMsgInfo().getInsID());
		assertEquals(transactionResultNotifReq.getMsgInfo().getMsgId(), response.getMsgInfo().getMsgId());
		assertEquals(transactionResultNotifReq.getMsgInfo().getMsgType(), response.getMsgInfo().getMsgType());
		assertEquals(transactionResultNotifReq.getMsgInfo().getVersionNo(), response.getMsgInfo().getVersionNo());
		assertEquals(transactionResultNotifReq.getMsgInfo().getTimeStamp(), response.getMsgInfo().getTimeStamp());
		assertEquals("00", response.getMsgResponse().getResponseCode());
		assertEquals("Approved", response.getMsgResponse().getResponseMsg());
	}
	
	
	
	
	
	
	
	
}
