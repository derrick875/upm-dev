package com.nets.nps.paynow.upi.service.test;

import static org.mockito.Mockito.any;
import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.nets.nps.core.entity.Detokenization;
import com.nets.nps.paynow.entity.QrcGenerationRequest;
import com.nets.nps.paynow.entity.QrcGenerationTransactionDomainData;
import com.nets.nps.paynow.handlers.TspCommunicationHandler;
import com.nets.nps.paynow.security.service.DetokenizationService;
import com.nets.nps.paynow.utils.UtilComponents;
import com.nets.upos.commons.exception.BaseBusinessException;

@SpringBootTest(classes = {DetokenizationService.class})

public class DetokenizationServiceTest {

	@Autowired
	DetokenizationService unit;

	@MockBean
	UtilComponents mockUtilComponents;

	@MockBean
	TspCommunicationHandler mockTspCommunicationHandler;

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
	public void validateNullSofAccountId() {

		QrcGenerationRequest request = createQrcGenerationRequest();
		request.setSofAccountId(null);

		BaseBusinessException e = assertThrows(BaseBusinessException.class, () -> unit.getTokenization(request));
		assertEquals("58", e.getCode());
		assertEquals("Invalid nets token ref id.", e.getErrorMessage());
	}

	@Test
	public void validateNullResponseString() throws IOException {

		QrcGenerationRequest request = createQrcGenerationRequest();

		when(mockTspCommunicationHandler.sendAndReceive(any(Detokenization.class))).thenReturn(null);

		BaseBusinessException e = assertThrows(BaseBusinessException.class, () -> unit.getTokenization(request));
		assertEquals("06", e.getCode());
		assertEquals("Invalid UPI token response.", e.getErrorMessage());
		
		verify(mockTspCommunicationHandler, times(1)).sendAndReceive(any(Detokenization.class));
	}

	@Test
	public void validateIoException() throws IOException {

		QrcGenerationRequest request = createQrcGenerationRequest();		

		when(mockTspCommunicationHandler.sendAndReceive(any(Detokenization.class))).thenThrow(IOException.class);

		BaseBusinessException e = assertThrows(BaseBusinessException.class, () -> unit.getTokenization(request));
		assertEquals("91", e.getCode());
		assertEquals("Issuer unavilable or switch inoperative", e.getErrorMessage());
		
		verify(mockTspCommunicationHandler, times(1)).sendAndReceive(any(Detokenization.class));
	}

	@Test
	public void validateNullToken() throws IOException {

		QrcGenerationRequest request = createQrcGenerationRequest();
		String detokenizationStringResponse = UtilComponents.getRequestJsonString("/responseJson/EmptyTspResponse.json");

		when(mockTspCommunicationHandler.sendAndReceive(any(Detokenization.class))).thenReturn(detokenizationStringResponse);

		BaseBusinessException e = assertThrows(BaseBusinessException.class, () -> unit.getTokenization(request));
		assertEquals("06", e.getCode());
		assertEquals("Invalid UPI token response.", e.getErrorMessage());
		
		verify(mockTspCommunicationHandler, times(1)).sendAndReceive(any(Detokenization.class));
	}

	@Test
	public void validateNonAInactiveUpiStatus() throws IOException {

		QrcGenerationRequest request = createQrcGenerationRequest();
		String detokenizationStringResponse = UtilComponents.getRequestJsonString("/responseJson/InactiveUpiTspResponse.json");

		when(mockTspCommunicationHandler.sendAndReceive(any(Detokenization.class))).thenReturn(detokenizationStringResponse);

		BaseBusinessException e = assertThrows(BaseBusinessException.class, () -> unit.getTokenization(request));
		assertEquals("06", e.getCode());
		assertEquals("Inactive UPI token response.", e.getErrorMessage());
		
		verify(mockTspCommunicationHandler, times(1)).sendAndReceive(any(Detokenization.class));
	}
	
	@Test
	public void validateNullDetokenizationSpecificDatas() throws IOException {

		QrcGenerationRequest request = createQrcGenerationRequest();
		String detokenizationStringResponse = UtilComponents.getRequestJsonString("/responseJson/NullDetokenizationSpecificDataIdTspResponse.json");

		when(mockTspCommunicationHandler.sendAndReceive(any(Detokenization.class))).thenReturn(detokenizationStringResponse);

		BaseBusinessException e = assertThrows(BaseBusinessException.class, () -> unit.getTokenization(request));
		assertEquals("06", e.getCode());
		assertEquals("Inactive UPI token response.", e.getErrorMessage());
		
		verify(mockTspCommunicationHandler, times(1)).sendAndReceive(any(Detokenization.class));
	}

	@Test
	public void validateEmptyNetsTokenId() throws IOException {

		QrcGenerationRequest request = createQrcGenerationRequest();
		String detokenizationStringResponse = UtilComponents.getRequestJsonString("/responseJson/EmptyNetsTokenIdTspResponse.json");

		when(mockTspCommunicationHandler.sendAndReceive(any(Detokenization.class))).thenReturn(detokenizationStringResponse);

		BaseBusinessException e = assertThrows(BaseBusinessException.class, () -> unit.getTokenization(request));
		assertEquals("06", e.getCode());
		assertEquals("Invalid UPI token response.", e.getErrorMessage());
		
		verify(mockTspCommunicationHandler, times(1)).sendAndReceive(any(Detokenization.class));
	}

	@Test
	public void validateSuccess() throws IOException, BaseBusinessException {

		QrcGenerationRequest request = createQrcGenerationRequest();
		String detokenizationStringResponse = UtilComponents.getRequestJsonString("/responseJson/TspResponse.json");
		Detokenization token = (Detokenization) UtilComponents.getObjectFromString(detokenizationStringResponse, Detokenization.class);
		
		when(mockTspCommunicationHandler.sendAndReceive(any(Detokenization.class))).thenReturn(detokenizationStringResponse);

		QrcGenerationRequest tokenizeRequest = unit.getTokenization(request);
		
		assertEquals(token.getBody().getTxn_specific_data().get(0).getNets_token_id(), tokenizeRequest.getSofAccountId());
		
		verify(mockTspCommunicationHandler, times(1)).sendAndReceive(any(Detokenization.class));
	}
	

}
