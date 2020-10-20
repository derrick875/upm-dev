package com.nets.nps.paynow.service.test;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;

import com.nets.nps.paynow.entity.OrderQueryRequest;
import com.nets.nps.paynow.entity.OrderQueryResponse;
import com.nets.nps.paynow.entity.OrderQueryTransactionEnquiryData;
import com.nets.nps.paynow.service.PaynowTransactionDataService;
import com.nets.nps.paynow.service.impl.PaynowOrderQueryService;
import com.nets.upos.commons.constants.ResponseCodes;
import com.nets.upos.commons.enums.RequestState;
import com.nets.upos.commons.enums.TransactionState;
import com.nets.upos.commons.exception.BaseBusinessException;
import com.nets.upos.commons.exception.JsonFormatException;
import com.nets.upos.core.entity.PaynowTransactionData;
import com.nets.upos.core.entity.Request;
import com.nets.upos.core.entity.TransactionEntity;
import com.nets.upos.core.service.RequestService;
import com.nets.upos.core.service.TransactionService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;

@SpringBootTest(classes = {PaynowOrderQueryService.class})
@TestPropertySource(locations = "classpath:paynow-local.properties")
public class PaynowOrderQueryServiceTest {

	@Autowired
	PaynowOrderQueryService unit;

	@MockBean
	private RequestService mockRequestService;

	@MockBean
	private TransactionService mockTransactionService;

	@MockBean
	private PaynowTransactionDataService mockPaynowTransactionDataService;

	@Value("${paynow.maximum.searchcount}")
	private String maxSearchCount;

	public OrderQueryRequest createOrderQueryRequest() {
		OrderQueryTransactionEnquiryData transactionEnquiryData = new OrderQueryTransactionEnquiryData();
		transactionEnquiryData.setOriginalRetrievalReference("12345678129");

		transactionEnquiryData.setRetryAttempt("1");
		transactionEnquiryData.setHostMid("11119860311");
		transactionEnquiryData.setHostTid("19860311");
		transactionEnquiryData.setNetsPaynowMpan("");
		transactionEnquiryData.setBankMerchantProxy("");
		transactionEnquiryData.setBankMerchantProxyType("");
		transactionEnquiryData.setMerchantReferenceNumber("DBPNKADE-00000003481d2245");
		transactionEnquiryData.setInvoiceRef("");
		transactionEnquiryData.setNpxData(null);
		OrderQueryRequest orderQueryRequest = new OrderQueryRequest();
		orderQueryRequest.setRetrievalRef("12345678128");
		orderQueryRequest.setInstitutionCode("30000000033");
		orderQueryRequest.setTransmissionTime("0608123413");
		orderQueryRequest.setChannelIndicator("PNT");
		orderQueryRequest.setTransactionEnquiryData(transactionEnquiryData);
		orderQueryRequest.setMti("0100");
		orderQueryRequest.setProcessCode("335000");
		return orderQueryRequest;
	}

	@Test
	public void validateMerchantRefNumLessThanLength25() {
		OrderQueryRequest orderQueryRequest = createOrderQueryRequest();
		OrderQueryTransactionEnquiryData transactionEnquiryData = new OrderQueryTransactionEnquiryData();

		transactionEnquiryData.setOriginalRetrievalReference("12345678129");

		transactionEnquiryData.setRetryAttempt("1");
		transactionEnquiryData.setHostMid("11119860311");
		transactionEnquiryData.setHostTid("19860311");
		transactionEnquiryData.setNetsPaynowMpan("");
		transactionEnquiryData.setBankMerchantProxy("");
		transactionEnquiryData.setBankMerchantProxyType("");
		transactionEnquiryData.setMerchantReferenceNumber("00000003481d22-DBPNKADE");;
		transactionEnquiryData.setInvoiceRef("");
		transactionEnquiryData.setNpxData(null);

		orderQueryRequest.setTransactionEnquiryData(transactionEnquiryData);

		JsonFormatException e = assertThrows(JsonFormatException.class, () -> unit.process(orderQueryRequest));
		assertEquals("30", e.getCode());
		assertEquals("Invalid Merchant Reference Number", e.getErrorMessage());
	}

	@Test
	public void validateMerchantRefNumMoreThanLength25() {
		OrderQueryRequest orderQueryRequest = createOrderQueryRequest();
		OrderQueryTransactionEnquiryData transactionEnquiryData = new OrderQueryTransactionEnquiryData();

		transactionEnquiryData.setOriginalRetrievalReference("12345678129");

		transactionEnquiryData.setRetryAttempt("1");
		transactionEnquiryData.setHostMid("11119860311");
		transactionEnquiryData.setHostTid("19860311");
		transactionEnquiryData.setNetsPaynowMpan("");
		transactionEnquiryData.setBankMerchantProxy("");
		transactionEnquiryData.setBankMerchantProxyType("");
		transactionEnquiryData.setMerchantReferenceNumber("DBPNKADE-00000003481d22433");
		transactionEnquiryData.setMerchantReferenceNumber("DBPNKADE-0000000");
		transactionEnquiryData.setInvoiceRef("");
		transactionEnquiryData.setNpxData(null);
		orderQueryRequest.setTransactionEnquiryData(transactionEnquiryData);

		JsonFormatException e = assertThrows(JsonFormatException.class, () -> unit.process(orderQueryRequest));
		assertEquals("30", e.getCode());
		assertEquals("Invalid Merchant Reference Number", e.getErrorMessage());
	}

	@Test
	public void validateNullOrderRequest() {
		OrderQueryRequest orderQueryRequest = createOrderQueryRequest();

		when(mockRequestService.findBySearchId(any())).thenReturn(null);

		BaseBusinessException e = assertThrows(BaseBusinessException.class, () -> unit.process(orderQueryRequest));
		assertEquals("15", e.getCode());
		assertEquals("Original order can't Find", e.getErrorMessage());

		verify(mockRequestService, times(1)).findBySearchId(any());
	}

	@Test
	public void validateNullOrderRequestTransactionIdRetryAttemptLessThanMax() {
		OrderQueryRequest orderQueryRequest = createOrderQueryRequest();

		Request orderRequest = new Request();
		orderRequest.setTransactionId(null);
		orderRequest.setState(RequestState.WAITING.getState());


		when(mockRequestService.findBySearchId(any())).thenReturn(orderRequest);

		BaseBusinessException e = assertThrows(BaseBusinessException.class, () -> unit.process(orderQueryRequest));
		assertEquals("03", e.getCode());
		assertEquals("Order Status Unknown", e.getErrorMessage());

		verify(mockRequestService, times(1)).findBySearchId(any());
	}

	@Test
	public void validateNullOrderRequestTransactionIdRetryAttemptEqualMax() {
		OrderQueryRequest orderQueryRequest = createOrderQueryRequest();
		OrderQueryTransactionEnquiryData transactionEnquiryData = new OrderQueryTransactionEnquiryData();
		transactionEnquiryData.setOriginalRetrievalReference("12345678129");

		transactionEnquiryData.setRetryAttempt("5");
		transactionEnquiryData.setHostMid("11119860311");
		transactionEnquiryData.setHostTid("19860311");
		transactionEnquiryData.setNetsPaynowMpan("");
		transactionEnquiryData.setBankMerchantProxy("");
		transactionEnquiryData.setBankMerchantProxyType("");
		transactionEnquiryData.setMerchantReferenceNumber("DBPNKADE-00000003481d2245");
		transactionEnquiryData.setInvoiceRef("");
		transactionEnquiryData.setNpxData(null);
		orderQueryRequest.setTransactionEnquiryData(transactionEnquiryData);

		Request orderRequest = new Request();
		orderRequest.setTransactionId(null);
		orderRequest.setState(RequestState.WAITING.getState());


		when(mockRequestService.findBySearchId(any())).thenReturn(orderRequest);

		BaseBusinessException e = assertThrows(BaseBusinessException.class, () -> unit.process(orderQueryRequest));
		assertEquals("03", e.getCode());
		assertEquals("Order Status Unknown", e.getErrorMessage());

		verify(mockRequestService, times(1)).findBySearchId(any());
	}

	@Test
	public void validateNullOrderRequestTransactionIdRequestStateTimeout() {
		OrderQueryRequest orderQueryRequest = createOrderQueryRequest();

		Request orderRequest = new Request();
		orderRequest.setTransactionId(null);
		orderRequest.setState(RequestState.TIMED_OUT.getState());

		when(mockRequestService.findBySearchId(any())).thenReturn(orderRequest);

		BaseBusinessException e = assertThrows(BaseBusinessException.class, () -> unit.process(orderQueryRequest));
		assertEquals("68", e.getCode());
		assertEquals("Timeout", e.getErrorMessage());

		verify(mockRequestService, times(1)).findBySearchId(any());
	}

	@Test
	public void validateNullOrderRequestTransactionIdRequestStateReversed() {
		OrderQueryRequest orderQueryRequest = createOrderQueryRequest();

		Request orderRequest = new Request();
		orderRequest.setTransactionId(null);
		orderRequest.setState(RequestState.REVERSED.getState());


		when(mockRequestService.findBySearchId(any())).thenReturn(orderRequest);

		BaseBusinessException e = assertThrows(BaseBusinessException.class, () -> unit.process(orderQueryRequest));
		assertEquals("68", e.getCode());
		assertEquals("Timeout", e.getErrorMessage());

		verify(mockRequestService, times(1)).findBySearchId(any());
	}

	@Test
	public void validateNullTransactionEntity() {
		OrderQueryRequest orderQueryRequest = createOrderQueryRequest();

		Request orderRequest = new Request();
		orderRequest.setTransactionId(32437086);


		when(mockRequestService.findBySearchId(any())).thenReturn(orderRequest);
		when(mockTransactionService.retrieve(anyInt())).thenReturn(null);

		BaseBusinessException e = assertThrows(BaseBusinessException.class, () -> unit.process(orderQueryRequest));
		assertEquals("12", e.getCode());
		assertEquals("Invalid Transaction", e.getErrorMessage());

		verify(mockRequestService, times(1)).findBySearchId(any());
		verify(mockTransactionService, times(1)).retrieve(anyInt());
	}

	@Test
	public void validateNullPaynowTransactionData() {
		OrderQueryRequest orderQueryRequest = createOrderQueryRequest();

		Request orderRequest = new Request();
		orderRequest.setTransactionId(32437086);

		TransactionEntity transaction = new TransactionEntity();

		when(mockRequestService.findBySearchId(any())).thenReturn(orderRequest);
		when(mockTransactionService.retrieve(anyInt())).thenReturn(transaction);
		when(mockPaynowTransactionDataService.getPaynowTransactionData(anyInt())).thenReturn(null);

		BaseBusinessException e = assertThrows(BaseBusinessException.class, () -> unit.process(orderQueryRequest));
		assertEquals("12", e.getCode());
		assertEquals("Invalid Transaction", e.getErrorMessage());

		verify(mockRequestService, times(1)).findBySearchId(any());
		verify(mockTransactionService, times(1)).retrieve(anyInt());
		verify(mockPaynowTransactionDataService, times(1)).getPaynowTransactionData(anyInt());
	}

	@Test
	public void validateTransactionStateReversed() throws JsonFormatException, BaseBusinessException {
		OrderQueryRequest orderQueryRequest = createOrderQueryRequest();

		Request orderRequest = new Request();
		orderRequest.setTransactionId(32437086);

		TransactionEntity transaction = new TransactionEntity();
		transaction.setState(TransactionState.STATE_REVERSED.getState());

		PaynowTransactionData paynowTransactionData = new PaynowTransactionData();

		when(mockRequestService.findBySearchId(any())).thenReturn(orderRequest);
		when(mockTransactionService.retrieve(anyInt())).thenReturn(transaction);
		when(mockPaynowTransactionDataService.getPaynowTransactionData(anyInt())).thenReturn(paynowTransactionData);

		BaseBusinessException e = assertThrows(BaseBusinessException.class, () -> unit.process(orderQueryRequest));
		assertEquals("68", e.getCode());
		assertEquals("Timeout", e.getErrorMessage());
		verify(mockRequestService, times(1)).findBySearchId(any());
		verify(mockTransactionService, times(1)).retrieve(anyInt());
		verify(mockPaynowTransactionDataService, times(1)).getPaynowTransactionData(anyInt());
	}

	@Test
	public void validateTransactionStateReversalFailed() throws JsonFormatException, BaseBusinessException {
		OrderQueryRequest orderQueryRequest = createOrderQueryRequest();

		Request orderRequest = new Request();
		orderRequest.setTransactionId(32437086);

		TransactionEntity transaction = new TransactionEntity();
		transaction.setState(TransactionState.STATE_REVERSAL_FAILED.getState());

		PaynowTransactionData paynowTransactionData = new PaynowTransactionData();

		when(mockRequestService.findBySearchId(any())).thenReturn(orderRequest);
		when(mockTransactionService.retrieve(anyInt())).thenReturn(transaction);
		when(mockPaynowTransactionDataService.getPaynowTransactionData(anyInt())).thenReturn(paynowTransactionData);

		BaseBusinessException e = assertThrows(BaseBusinessException.class, () -> unit.process(orderQueryRequest));
		assertEquals("68", e.getCode());
		assertEquals("Timeout", e.getErrorMessage());
		verify(mockRequestService, times(1)).findBySearchId(any());
		verify(mockTransactionService, times(1)).retrieve(anyInt());
		verify(mockPaynowTransactionDataService, times(1)).getPaynowTransactionData(anyInt());
	}

	@Test
	public void validateTransactionStateOk() throws JsonFormatException, BaseBusinessException {
		OrderQueryRequest orderQueryRequest = createOrderQueryRequest();

		Request orderRequest = new Request();
		orderRequest.setTransactionId(32437086);
		orderRequest.setStan("000003");

		TransactionEntity transaction = new TransactionEntity();
		transaction.setState(TransactionState.STATE_SETTLED.getState());
		transaction.setAcqApprovalCode("5KUEHX");

		PaynowTransactionData paynowTransactionData = new PaynowTransactionData();
		paynowTransactionData.setMerchantReferenceNumber("DBPNKADE 1986031100000001");
		paynowTransactionData.setAdditionalBankReference("abcdefghij");

		when(mockRequestService.findBySearchId(any())).thenReturn(orderRequest);
		when(mockTransactionService.retrieve(anyInt())).thenReturn(transaction);
		when(mockPaynowTransactionDataService.getPaynowTransactionData(anyInt())).thenReturn(paynowTransactionData);

		OrderQueryResponse response = unit.process(orderQueryRequest);
		assertEquals(orderRequest.getStan(), response.getPaynowOrderResult().getStan());
		assertEquals(transaction.getAcqApprovalCode(), response.getPaynowOrderResult().getApprovalCode());
		assertEquals(paynowTransactionData.getMerchantReferenceNumber(), response.getPaynowOrderResult().getMerchantReferenceNumber());
		assertEquals(paynowTransactionData.getAdditionalBankReference(), response.getPaynowOrderResult().getReceivingBankReference());
		assertEquals(ResponseCodes.SUCCESS,response.getResponseCode());
		verify(mockRequestService, times(1)).findBySearchId(any());
		verify(mockTransactionService, times(1)).retrieve(anyInt());
		verify(mockPaynowTransactionDataService, times(1)).getPaynowTransactionData(anyInt());
	}

}
