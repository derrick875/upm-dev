package com.nets.nps.paynow.upi.service.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.UUID;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nets.nps.client.impl.MsgInfo;
import com.nets.nps.paynow.entity.DebitTransactionRequest;
import com.nets.nps.paynow.entity.DebitTransactionRequestTrxInfo;
import com.nets.nps.paynow.entity.DebitTransactionResponse;
import com.nets.nps.paynow.entity.MerchantInfo;
import com.nets.nps.paynow.entity.MessageResponse;
import com.nets.nps.paynow.entity.PullDebitRequest;
import com.nets.nps.paynow.entity.PullDebitRequestTransactionDomainData;
import com.nets.nps.paynow.entity.UpiProxyRequest;
import com.nets.nps.paynow.service.DebitTransactionService;
import com.nets.nps.paynow.service.impl.PullDebitRequestAdapter;
import com.nets.nps.paynow.service.impl.WalletAdapter;
import com.nets.nps.paynow.utils.UtilComponents;

@SpringBootTest(classes = {DebitTransactionService.class})

public class DebitTransactionServiceTest {
	
	@Autowired
	DebitTransactionService unit;
	
	@MockBean
	PullDebitRequestAdapter mockPullDebitRequestAdapter;

	@MockBean
	WalletAdapter mockWalletAdapter;
	
//	String message = {"transactionType":"DEBIT_TRANSACTION","upiProxyRequestJsonData":"{\"msgInfo\":{\"versionNo\":\"1.0.0\",\"msgID\":\"U0053034420190808110118000001\",\"timeStamp\":\"20190808110118\",\"msgType\":\"DEBIT_TRANSACTION\",\"insID\":\"39990029\"},\"trxInfo\":{\"merchantInfo\":{\"acquirerIIN\":\"47010344\",\"fwdIIN\":\"00020344\",\"mid\":\"701034453110010\",\"merchantName\":\"testing merchant Macau MAC\",\"mcc\":\"5311\",\"merchantCountry\":\"344\",\"termId\":\"00104001\"},\"debitAccountInfo\":\"eyJhbGciOiJSU0ExXzUiLCJlbmMiOiJBMTI4Q0JDLUhTMjU2Iiwia2lkIjoiMTU2MjAzMjg4NTk2MiJ9.IGVbmIFn7kqiSEQTYoCZDp7dxWHFKb gATceor14wOUjiTzjaq-BnKQ7UL8HEtT1K_OO2SmJf67-sREZ-widaS5gnjl_7vrDPMpRyitBnAsAaDcklcLq48CQ7jaTWrJg9te2m8U2VgyIdh7CVTpxS1yyon5CSTVSG94y qJm6O70Fh2zUDxqdPxXqfJj9bb1jMwofkOOwuHG-RhQNs6CP4zl22s7mnfg1h-tb-7J6BVD5Tu-zrt3VY6aAshooFDS63aesa9Xh7swQzBEIdBltSF-PHVMlok7_gnErTV MiiLRuonkPtZ801wvFaeH4Ppq9q3U1XGynSO7kbB6IlnHV2Hg.NDIxODViZDYyYzBkNGJjOA.Gt5I2aRcw0RL1YS860PDjXHoWQYtW9g6BT3snvFmCe0.gKc1fpZsZ3az 6jm7UpUoLA\",\"relTrxType\":\"CPQRC_PAYMENT\",\"onUsFlag\":\"N\",\"trxAmt\":\"2.22\",\"trxCurrency\":\"344\",\"billAmt\":\"2.22\",\"billCurrency\":\"344\",\"markupAmt\":\"0\",\"feeAmt\":\"0\",\"billConvRate\":\"1.000000\",\"settAmt\":\"0.28\",\"settCurrency\":\"840\",\"settConvRate\":\"0.1278459\",\"convDate\":\"0716\",\"settDate\":\"20190808\",\"posEntryModeCode\":\"042\",\"retrivlRefNum\":\"080887948744\"},\"correlationId\":\"529544fa-238a-44ae-b159-29a97d34eb76\"}","correlationId":"529544fa-238a-44ae-b159-29a97d34eb76"}"

	private DebitTransactionRequest createDebitTransactionRequest() {
		DebitTransactionRequest debitTransactionRequest = new DebitTransactionRequest();
		MsgInfo msgInfo = new MsgInfo();
		msgInfo.setVersionNo("1.0.0");
		msgInfo.setMsgId("U0053034420190808110118000001");
		msgInfo.setTimeStamp("20190808110118");
		msgInfo.setMsgType("DEBIT_TRANSACTION");
		msgInfo.setInsID("39990029");
		
		debitTransactionRequest.setMsgInfo(msgInfo);
		
		DebitTransactionRequestTrxInfo trxInfo = new DebitTransactionRequestTrxInfo();
		trxInfo.setRelTrxType("CPQRC_PAYMENT");
		MerchantInfo merchantInfo = new MerchantInfo();
		merchantInfo.setAcquirerIIN("47010344");
		merchantInfo.setFwdIIN("00020344");
		merchantInfo.setMid("701034453110010");
		merchantInfo.setMerchantName("testing merchant Macau MAC");
		merchantInfo.setMcc("5311");
		merchantInfo.setMerchantCountry("344");
		merchantInfo.setTermId("00104001");
		trxInfo.setMerchantInfo(merchantInfo);
		trxInfo.setOnUsFlag("N");
		trxInfo.setDebitAccountInfo("eyJhbGciOiJSU0ExXzUiLCJlbmMiOiJBMTI4Q0JDLUhTMjU2Iiwia2lkIjoiMTU2MjAzMjg4NTk2MiJ9.IGVbmIFn7kqiSEQTYoCZDp7dxWHFKb gATceor14wOUjiTzjaq-BnKQ7UL8HEtT1K_OO2SmJf67-sREZ-widaS5gnjl_7vrDPMpRyitBnAsAaDcklcLq48CQ7jaTWrJg9te2m8U2VgyIdh7CVTpxS1yyon5CSTVSG94y qJm6O70Fh2zUDxqdPxXqfJj9bb1jMwofkOOwuHG-RhQNs6CP4zl22s7mnfg1h-tb-7J6BVD5Tu-zrt3VY6aAshooFDS63aesa9Xh7swQzBEIdBltSF-PHVMlok7_gnErTV MiiLRuonkPtZ801wvFaeH4Ppq9q3U1XGynSO7kbB6IlnHV2Hg.NDIxODViZDYyYzBkNGJjOA.Gt5I2aRcw0RL1YS860PDjXHoWQYtW9g6BT3snvFmCe0.gKc1fpZsZ3az 6jm7UpUoLA");
		trxInfo.setTrxAmt("2.22");
		trxInfo.setTrxCurrency("344");
		trxInfo.setBillAmt("2.22");
		trxInfo.setBillCurrency("344");
		trxInfo.setMarkupAmt("0");
		trxInfo.setFeeAmt("0");
		trxInfo.setBillConvRate("1.000000");
		trxInfo.setSettAmt("0.28");
		trxInfo.setSettCurrency("840");
		trxInfo.setSettConvRate("0.1278459");
		trxInfo.setConvDate("0716");
		trxInfo.setSettDate("20190808");
		trxInfo.setPosEntryModeCode("042");
		trxInfo.setRetrivlRefNum("080887948744");
		debitTransactionRequest.setTrxInfo(trxInfo);
		
		return debitTransactionRequest;
	}

	private UpiProxyRequest createUpiProxyRequest(DebitTransactionRequest debitTransactionRequest) {
		
		String debitTransactonRequestString = UtilComponents.getStringFromObject(debitTransactionRequest);

		UpiProxyRequest upiProxyRequest = new UpiProxyRequest();
		upiProxyRequest.setTransactionType("DEBIT_TRANSACTION");
		upiProxyRequest.setUpiProxyRequestJsonData(debitTransactonRequestString);
		upiProxyRequest.setCorrelationId(UUID.randomUUID().toString());
		return upiProxyRequest;
	}
	
	private String createMessageString(DebitTransactionRequest debitTransactionRequest, UpiProxyRequest upiProxyRequest) throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		String message = mapper.writeValueAsString(upiProxyRequest);
		return message;
	}
	
	private PullDebitRequest createPullDebitRequest() {
		PullDebitRequest pullDebitRequest = new PullDebitRequest();
		PullDebitRequestTransactionDomainData transactionDomainData = new PullDebitRequestTransactionDomainData();
		pullDebitRequest.setRetrievalRef(UUID.randomUUID().toString());
		pullDebitRequest.setInstitutionCode("30000000001");
		pullDebitRequest.setAcquirerInstitutionCode("40000000001"); // 
		pullDebitRequest.setSofUri("http://dbs.com");
		pullDebitRequest.setTransmissionTime("2611111111");
//		pullDebitRequest.setSofAccountId(debitTransactionRequest.getTrxInfo()); need to add after tokenization and use the token 
		
		transactionDomainData.setAmount("000000000222");
		transactionDomainData.setAmountCurrency("344");
//		transactionDomainData.setDiscount("");
		transactionDomainData.setFee("000000000222");
		transactionDomainData.setConvertedAmount("000000000222");
		transactionDomainData.setConvertCurrency("344");
		transactionDomainData.setConversionRate("0.1278459");
		transactionDomainData.setTransactionType("1");
//		transactionDomainData.setCpmqrpaymentToken("dkwhatthis"); double check
		pullDebitRequest.setTransactionDomainData(transactionDomainData);
		
		return pullDebitRequest;
	}

	private String createDebitTransactionResponseString(DebitTransactionRequest debitTransactionRequest) {
		DebitTransactionResponse debitTransactionResponse = new DebitTransactionResponse();
		debitTransactionResponse.setMsgInfo(debitTransactionRequest.getMsgInfo());
		MessageResponse msgResponse = new MessageResponse();
		msgResponse.setResponseCode("00");
		msgResponse.setResponseMsg("Approved");
		debitTransactionResponse.setMsgResponse(msgResponse);

		String debitTransactionResponseString = UtilComponents.getStringFromObject(debitTransactionResponse);

		return debitTransactionResponseString;
	}
	@Test
	public void validateSuccessCase() throws JsonProcessingException {
		DebitTransactionRequest debitTransactionRequest = createDebitTransactionRequest();
		UpiProxyRequest upiProxyRequest = createUpiProxyRequest(debitTransactionRequest);
		String message = createMessageString(debitTransactionRequest, upiProxyRequest);
		PullDebitRequest pullDebitRequest = createPullDebitRequest();
		when(mockPullDebitRequestAdapter.convertToPullDebitRequest(any(DebitTransactionRequest.class))).thenReturn(pullDebitRequest);
		
		String debitTransactionResponseString = createDebitTransactionResponseString(debitTransactionRequest);
		
		when(mockWalletAdapter.sendAndReceiveFromBank(any(PullDebitRequest.class), any(DebitTransactionRequest.class))).thenReturn(debitTransactionResponseString);
		
		String responseString = unit.process(message);
		UpiProxyRequest response = (UpiProxyRequest) UtilComponents.getObjectFromString(responseString, UpiProxyRequest.class);
		verify(mockPullDebitRequestAdapter, times(1)).convertToPullDebitRequest(any(DebitTransactionRequest.class));
		verify(mockWalletAdapter, times(1)).sendAndReceiveFromBank(any(PullDebitRequest.class), any(DebitTransactionRequest.class));
		
		assertEquals(response.getBankId(), upiProxyRequest.getBankId());
		assertEquals(response.getBankType(), upiProxyRequest.getBankType());
		assertNotNull(response.getCorrelationId());
		assertEquals(response.getInstCode(), upiProxyRequest.getInstCode());
		assertEquals(response.getTransactionType(), upiProxyRequest.getTransactionType());
		assertEquals(response.getUpiProxyRequestJsonData(), debitTransactionResponseString);	
	}


}
