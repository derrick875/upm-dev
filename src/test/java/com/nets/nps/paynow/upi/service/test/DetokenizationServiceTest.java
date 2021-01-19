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

import com.nets.nps.paynow.utils.UtilComponents;
import com.nets.nps.upi.entity.Detokenization;
import com.nets.nps.upi.handlers.TspCommunicationHandler;
import com.nets.nps.upi.service.DetokenizationService;
import com.nets.upos.commons.exception.BaseBusinessException;

@SpringBootTest(classes = {DetokenizationService.class})

public class DetokenizationServiceTest {

	@Autowired
	DetokenizationService unit;

	@MockBean
	UtilComponents mockUtilComponents;

	@MockBean
	TspCommunicationHandler mockTspCommunicationHandler;

	@Test
	public void validateIoException() throws IOException {
		Detokenization detoken = new Detokenization();
		
		when(mockTspCommunicationHandler.sendAndReceive(detoken)).thenThrow(IOException.class);

		BaseBusinessException e = assertThrows(BaseBusinessException.class, () -> unit.getTokenization(detoken));
		assertEquals("91", e.getCode());
		assertEquals("Issuer unavilable or switch inoperative", e.getErrorMessage());
		
		verify(mockTspCommunicationHandler, times(1)).sendAndReceive(any(Detokenization.class));
	}
	
	@Test
	public void validateNullResponseString() throws IOException {
		Detokenization detoken = new Detokenization();
		when(mockTspCommunicationHandler.sendAndReceive(detoken)).thenReturn(null);

		BaseBusinessException e = assertThrows(BaseBusinessException.class, () -> unit.getTokenization(detoken));
		assertEquals("06", e.getCode());
		assertEquals("Invalid UPI token response.", e.getErrorMessage());
		
		verify(mockTspCommunicationHandler, times(1)).sendAndReceive(any(Detokenization.class));
	}

	@Test
	public void validateNullToken() throws IOException {
		Detokenization detoken = new Detokenization();
		String detokenizationStringResponse = UtilComponents.getRequestJsonString("/responseJson/EmptyTspResponse.json");

		when(mockTspCommunicationHandler.sendAndReceive(detoken)).thenReturn(detokenizationStringResponse);

		BaseBusinessException e = assertThrows(BaseBusinessException.class, () -> unit.getTokenization(detoken));
		assertEquals("06", e.getCode());
		assertEquals("Invalid UPI token response.", e.getErrorMessage());
		
		verify(mockTspCommunicationHandler, times(1)).sendAndReceive(any(Detokenization.class));
	}

	@Test
	public void validateNonAInactiveUpiStatus() throws IOException {
		Detokenization detoken = new Detokenization();
		String detokenizationStringResponse = UtilComponents.getRequestJsonString("/responseJson/InactiveUpiTspResponse.json");

		when(mockTspCommunicationHandler.sendAndReceive(detoken)).thenReturn(detokenizationStringResponse);

		BaseBusinessException e = assertThrows(BaseBusinessException.class, () -> unit.getTokenization(detoken));
		assertEquals("06", e.getCode());
		assertEquals("Inactive UPI token response.", e.getErrorMessage());
		
		verify(mockTspCommunicationHandler, times(1)).sendAndReceive(any(Detokenization.class));
	}
	
	@Test
	public void validateNullDetokenizationSpecificDatas() throws IOException {
		Detokenization detoken = new Detokenization();
		String detokenizationStringResponse = UtilComponents.getRequestJsonString("/responseJson/NullDetokenizationSpecificDataIdTspResponse.json");

		when(mockTspCommunicationHandler.sendAndReceive(detoken)).thenReturn(detokenizationStringResponse);

		BaseBusinessException e = assertThrows(BaseBusinessException.class, () -> unit.getTokenization(detoken));
		assertEquals("06", e.getCode());
		assertEquals("Inactive UPI token response.", e.getErrorMessage());
		
		verify(mockTspCommunicationHandler, times(1)).sendAndReceive(any(Detokenization.class));
	}

	@Test
	public void validateEmptyNetsTokenId() throws IOException {
		Detokenization detoken = new Detokenization();
		String detokenizationStringResponse = UtilComponents.getRequestJsonString("/responseJson/EmptyNetsTokenIdTspResponse.json");

		when(mockTspCommunicationHandler.sendAndReceive(detoken)).thenReturn(detokenizationStringResponse);

		BaseBusinessException e = assertThrows(BaseBusinessException.class, () -> unit.getTokenization(detoken));
		assertEquals("06", e.getCode());
		assertEquals("Invalid UPI token response.", e.getErrorMessage());
		
		verify(mockTspCommunicationHandler, times(1)).sendAndReceive(any(Detokenization.class));
	}

	@Test
	public void validateSuccess() throws IOException, BaseBusinessException {
		Detokenization detoken = new Detokenization();
		String detokenizationStringResponse = UtilComponents.getRequestJsonString("/responseJson/TspResponse.json");
		Detokenization token = (Detokenization) UtilComponents.getObjectFromString(detokenizationStringResponse, Detokenization.class);
		
		when(mockTspCommunicationHandler.sendAndReceive(detoken)).thenReturn(detokenizationStringResponse);

		String netsTokenId = unit.getTokenization(detoken);
		
		assertEquals(token.getBody().getTxn_specific_data().get(0).getNets_token_id(), netsTokenId);
		
		verify(mockTspCommunicationHandler, times(1)).sendAndReceive(any(Detokenization.class));
	}
	

}
