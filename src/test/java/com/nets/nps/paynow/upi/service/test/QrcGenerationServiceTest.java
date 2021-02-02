package com.nets.nps.paynow.upi.service.test;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.nets.nps.client.impl.UpiClient;
import com.nets.nps.upi.entity.MessageResponse;
import com.nets.nps.upi.entity.MsgInfo;
import com.nets.nps.upi.entity.QrcGenerationRequest;
import com.nets.nps.upi.entity.QrcGenerationResponse;
import com.nets.nps.upi.entity.QrcGenerationTransactionDomainData;
import com.nets.nps.upi.entity.UpiQrcGeneration;
import com.nets.nps.upi.entity.UpiQrcGenerationResponse;
import com.nets.nps.upi.entity.UpiQrcGenerationTransactionResponse;
import com.nets.nps.upi.service.DetokenizationAdapter;
import com.nets.nps.upi.service.UpiQrcGenerationService;
import com.nets.nps.upi.service.impl.QrcGenerationService;

@SpringBootTest(classes = {QrcGenerationService.class})

public class QrcGenerationServiceTest {
	
	@Autowired
	QrcGenerationService unit;
	
	@MockBean
	private DetokenizationAdapter mockDetokenizationAdapter;
	
	@MockBean
	private UpiClient mockUpiClient;
	
	@MockBean
	private UpiQrcGenerationService mockUpiQrcGenerationService;
	
	private QrcGenerationRequest createQrcGenerationRequest() {
		QrcGenerationRequest request = new QrcGenerationRequest();
		request.setMti("0300");
		request.setProcessCode("700000");
		request.setRetrievalRef("123456781900");
		request.setInstitutionCode("30000000001");
		request.setAcquirerInstitutionCode("30000000033");
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
	
	private UpiQrcGenerationResponse createUpiQrcGenerationResponse() {
		UpiQrcGenerationResponse upiResponse = new UpiQrcGenerationResponse();
		MsgInfo msgInfo = new MsgInfo();
		msgInfo.setVersionNo("1.0.0");
		msgInfo.setMsgId("A39990029202011251417530000011");
		msgInfo.setTimeStamp("20201125021753");
		msgInfo.setMsgType("QRC_GENERATION");
		msgInfo.setInsID("39990029");
		UpiQrcGenerationTransactionResponse trxInfo = new UpiQrcGenerationTransactionResponse();
		trxInfo.setCpqrcNo("12345");
		trxInfo.setEmvCpqrcPayload("2r23r23");
		trxInfo.setBarcodeCpqrcPayload("abcd");
		MessageResponse msgResponse = new MessageResponse();
		msgResponse.setResponseCode("00");
		msgResponse.setResponseMsg("Approved");
		upiResponse.setMsgInfo(msgInfo);
		upiResponse.setTrxInfo(trxInfo);
		upiResponse.setMsgResponse(msgResponse);
		
		return upiResponse;
	}
	
	@Test
	public void validateSuccessCase() throws Exception {
		
		QrcGenerationRequest request = createQrcGenerationRequest();
		UpiQrcGenerationResponse upiResponse = createUpiQrcGenerationResponse();

		when(mockDetokenizationAdapter.detokenizationQrcGenerationRequest(request)).thenReturn(request);
		when(mockUpiClient.sendAndReceiveFromUpi(request)).thenReturn(upiResponse);
		
		QrcGenerationResponse response = unit.process(request);
		
		verify(mockDetokenizationAdapter, times(1)).detokenizationQrcGenerationRequest(request);
		verify(mockUpiClient, times(1)).sendAndReceiveFromUpi(request);
		
		assertEquals(request.getRetrievalRef(), response.getRetrievalRef());
		assertEquals("0310", response.getMti());
		assertEquals(request.getProcessCode(), response.getProcessCode());
		assertEquals(request.getTransmissionTime(), response.getTransmissionTime());
		assertEquals(request.getInstitutionCode(), response.getInstitutionCode());
		assertEquals(upiResponse.getTrxInfo().getEmvCpqrcPayload(), response.getQrpayloadDataEmv());
		assertEquals(upiResponse.getTrxInfo().getBarcodeCpqrcPayload(), response.getQrpayloadDataAlternate());
		assertNotNull(response.getCpmqrpaymentToken());
		
		ArgumentCaptor<UpiQrcGeneration> argumentCaptorUpiQrcGeneration = ArgumentCaptor.forClass(UpiQrcGeneration.class);
		verify(mockUpiQrcGenerationService, times(1)).save(argumentCaptorUpiQrcGeneration.capture());
		UpiQrcGeneration upiQrcGeneration = argumentCaptorUpiQrcGeneration.getValue();
		
		assertEquals(upiResponse.getTrxInfo().getCpqrcNo(), upiQrcGeneration.getCpqrcNo());
		assertEquals(upiResponse.getTrxInfo().getEmvCpqrcPayload(), upiQrcGeneration.getEmvCpqrcPayload());
		assertEquals(upiResponse.getTrxInfo().getBarcodeCpqrcPayload(), upiQrcGeneration.getBarcodeCpqrcPayload());
		assertEquals(response.getCpmqrpaymentToken(), upiQrcGeneration.getCpmqrPaymentToken());
		assertEquals(response.getTransmissionTime(), upiQrcGeneration.getTransmissionTime());
		assertEquals(response.getInstitutionCode(), upiQrcGeneration.getInstitutionCode());
	}
}
