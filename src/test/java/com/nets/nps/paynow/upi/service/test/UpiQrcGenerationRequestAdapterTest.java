package com.nets.nps.paynow.upi.service.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import com.nets.nps.client.impl.UpiQrcGenerationRequestAdapter;
import com.nets.nps.paynow.entity.QrcGenerationRequest;
import com.nets.nps.paynow.entity.QrcGenerationTransactionDomainData;
import com.nets.nps.paynow.entity.UpiQrcGenerationRequest;

@SpringBootTest(classes = {UpiQrcGenerationRequestAdapter.class})
@TestPropertySource(locations = "classpath:paynow-local.properties")
public class UpiQrcGenerationRequestAdapterTest {
	
	@Autowired
	UpiQrcGenerationRequestAdapter unit;
	
	private QrcGenerationRequest createQrcGenerationRequest() {
		QrcGenerationRequest request = new QrcGenerationRequest();
		request.setMti("0311");
		request.setProcessCode("700000");
		request.setRetrievalRef("123456781900");
		request.setInstitutionCode("30000000001");
		request.setAcquirerInstitutionCode("30000000044");
		request.setSofUri("thisisthesofuri");
		request.setTransmissionTime("0608123413");
		request.setSofAccountId("sof_account_id");
		QrcGenerationTransactionDomainData transactionDomainData = new QrcGenerationTransactionDomainData();
		transactionDomainData.setTxnLimitAmount("000000000460");
		transactionDomainData.setCvmLimitAmount("000000001000");
		transactionDomainData.setAmountLimitCurrency("SGD");
		transactionDomainData.setTransactionType("1");
		request.setTransactionDomainData(transactionDomainData);

		return request;
	}
	
	@Test
	public void validateUpiQrcGenerationRequestOutput() {
		
		QrcGenerationRequest request = createQrcGenerationRequest();
//		request.setInstitutionCode("1231321");
		UpiQrcGenerationRequest upiQrcGenerationRequest = unit.handleQrGenReq(request);
		
		assertNotNull(upiQrcGenerationRequest.getMsgInfo());
		assertEquals("1.0.0", upiQrcGenerationRequest.getMsgInfo().getVersionNo());
		assertNotNull(upiQrcGenerationRequest.getMsgInfo().getMsgId());
		assertNotNull(upiQrcGenerationRequest.getMsgInfo().getTimeStamp());
		assertEquals("QRC_GENERATION", upiQrcGenerationRequest.getMsgInfo().getMsgType());
		assertNotNull(upiQrcGenerationRequest.getMsgInfo().getInsID());
		assertNotNull(upiQrcGenerationRequest.getTrxInfo());
		assertNotNull(upiQrcGenerationRequest.getTrxInfo().getDeviceID());
		assertNotNull(upiQrcGenerationRequest.getTrxInfo().getUserID());
		assertEquals(request.getSofAccountId(), upiQrcGenerationRequest.getTrxInfo().getToken());
		assertEquals(request.getTransactionDomainData().getTxnLimitAmount(), upiQrcGenerationRequest.getTrxInfo().getTrxLimit());
		assertEquals(request.getTransactionDomainData().getCvmLimitAmount(), upiQrcGenerationRequest.getTrxInfo().getCvmLimit());
		assertEquals(request.getTransactionDomainData().getAmountLimitCurrency(), upiQrcGenerationRequest.getTrxInfo().getLimitCurrency());
		assertEquals("01", upiQrcGenerationRequest.getTrxInfo().getCpqrcNo());	
	}
	
	@Test
	public void validateUpiQrcGenerationRequestOutput2() {
		
		QrcGenerationRequest request = createQrcGenerationRequest();
		request.setInstitutionCode("1231321");
		UpiQrcGenerationRequest upiQrcGenerationRequest = unit.handleQrGenReq(request);
		
		assertNotNull(upiQrcGenerationRequest.getMsgInfo());
		assertEquals("1.0.0", upiQrcGenerationRequest.getMsgInfo().getVersionNo());
		assertNotNull(upiQrcGenerationRequest.getMsgInfo().getMsgId());
		assertNotNull(upiQrcGenerationRequest.getMsgInfo().getTimeStamp());
		assertEquals("QRC_GENERATION", upiQrcGenerationRequest.getMsgInfo().getMsgType());
		assertNotNull(upiQrcGenerationRequest.getMsgInfo().getInsID());
		assertNotNull(upiQrcGenerationRequest.getTrxInfo());
		assertNotNull(upiQrcGenerationRequest.getTrxInfo().getDeviceID());
		assertNotNull(upiQrcGenerationRequest.getTrxInfo().getUserID());
		assertEquals(request.getSofAccountId(), upiQrcGenerationRequest.getTrxInfo().getToken());
		assertEquals(request.getTransactionDomainData().getTxnLimitAmount(), upiQrcGenerationRequest.getTrxInfo().getTrxLimit());
		assertEquals(request.getTransactionDomainData().getCvmLimitAmount(), upiQrcGenerationRequest.getTrxInfo().getCvmLimit());
		assertEquals(request.getTransactionDomainData().getAmountLimitCurrency(), upiQrcGenerationRequest.getTrxInfo().getLimitCurrency());
		assertEquals("01", upiQrcGenerationRequest.getTrxInfo().getCpqrcNo());	
	}
}
