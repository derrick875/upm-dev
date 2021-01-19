package com.nets.nps.paynow.upi.service.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import com.nets.nps.upi.entity.DebitTransactionRequest;
import com.nets.nps.upi.entity.DebitTransactionRequestTrxInfo;
import com.nets.nps.upi.entity.MerchantInfo;
import com.nets.nps.upi.entity.MsgInfo;
import com.nets.nps.upi.entity.PullDebitRequest;
import com.nets.nps.upi.entity.PullDebitRequestTransactionDomainData;
import com.nets.nps.upi.service.impl.PullDebitRequestAdapter;

@SpringBootTest(classes = {PullDebitRequestAdapter.class})
@TestPropertySource(locations = "classpath:paynow-local.properties")
public class PullDebitRequestAdapterTest {

	@Autowired
	PullDebitRequestAdapter unit;

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
		trxInfo.setTransDatetime("2611111111");
		debitTransactionRequest.setTrxInfo(trxInfo);

		return debitTransactionRequest;
	}

	@Test
	public void validateSuccessCase() {
		DebitTransactionRequest debitTransactionRequest = createDebitTransactionRequest();
		DebitTransactionRequestTrxInfo trxInfo = debitTransactionRequest.getTrxInfo();
		
		PullDebitRequest pullDebitRequest = unit.convertToPullDebitRequest(debitTransactionRequest);
		PullDebitRequestTransactionDomainData trxDomainData = pullDebitRequest.getTransactionDomainData();
		
		assertNotNull(pullDebitRequest.getRetrievalRef());
		assertEquals("30000000001", pullDebitRequest.getInstitutionCode());
		assertEquals("40000000001", pullDebitRequest.getAcquirerInstitutionCode());
		assertEquals("http://dbs.com", pullDebitRequest.getSofUri());
		assertEquals(debitTransactionRequest.getMsgInfo().getTimeStamp().substring(4), pullDebitRequest.getTransmissionTime());
		assertNotNull(trxDomainData);
		assertEquals(StringUtils.leftPad(trxInfo.getTrxAmt(), 12, "0"), trxDomainData.getAmount());
		assertEquals(trxInfo.getTrxCurrency(), trxDomainData.getAmountCurrency());
		assertEquals(StringUtils.leftPad(trxInfo.getFeeAmt(), 12, "0"), trxDomainData.getFee());
		assertEquals(StringUtils.leftPad(trxInfo.getSettAmt(), 12, "0"), trxDomainData.getConvertedAmount());
		assertEquals(trxInfo.getSettCurrency(), trxDomainData.getConvertCurrency());
		assertEquals(trxInfo.getSettConvRate(), trxDomainData.getConversionRate());
		assertEquals("1", trxDomainData.getTransactionType());
	}
}
