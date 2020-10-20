package com.nets.nps.paynow.service.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.test.context.TestPropertySource;

import com.nets.nps.client.impl.NonStopClient;
import com.nets.nps.paynow.client.bank.BankAdapterProcessingFactory;
import com.nets.nps.paynow.client.bank.PaynowBankAdapter;
import com.nets.nps.paynow.entity.ReversalRequest;
import com.nets.nps.paynow.entity.ReversalTransactionEnquiryData;
import com.nets.nps.paynow.exception.ExceptionHandler;
import com.nets.nps.paynow.service.PaynowTransactionDataService;
import com.nets.nps.paynow.service.impl.PaynowReversalService;
import com.nets.upos.commons.enums.RequestState;
import com.nets.upos.commons.enums.TransactionState;
import com.nets.upos.commons.exception.BaseBusinessException;
import com.nets.upos.commons.exception.JsonFormatException;
import com.nets.upos.core.entity.PaynowTransactionData;
import com.nets.upos.core.entity.Request;
import com.nets.upos.core.entity.TransactionEntity;
import com.nets.upos.core.service.RequestService;
import com.nets.upos.core.service.TransactionService;


@SpringBootTest(classes = {PaynowReversalService.class})
@TestPropertySource(locations = "classpath:paynow-local.properties")
class PaynowReversalServiceTest {

	@Autowired
	private PaynowReversalService unit;
	
	@MockBean
	private RequestService mockRequestService;
	
	@MockBean
	private TransactionService mockTransactionService;
	
	@MockBean
	private PaynowTransactionDataService mockPaynowTransactionDataService;
	
	@MockBean
	private NonStopClient nonStopClient;
	
	@MockBean
	private BankAdapterProcessingFactory mockBankAdapterProcessingFactory;
	
	@MockBean 
	private ExceptionHandler mockExceptionHandler;

	@Value("${paynow.settlement.cutoff}")
	private String settlementCutoffTimeString;
	
	private ReversalRequest createReversalRequest() {
		ReversalTransactionEnquiryData transactionEnquiryData = new ReversalTransactionEnquiryData();
		transactionEnquiryData.setOriginalRetrievalReference(null);
		transactionEnquiryData.setHostMid("11119860311");
		transactionEnquiryData.setHostTid("19860311");
		transactionEnquiryData.setNetsPaynowMpan("");
		transactionEnquiryData.setBankMerchantProxy("");
		transactionEnquiryData.setBankMerchantProxyType("");
		transactionEnquiryData.setMerchantReferenceNumber("1234567890123456789012345");
		transactionEnquiryData.setInvoiceRef("");
		transactionEnquiryData.setNpxData(null);
		ReversalRequest request = new ReversalRequest();
		request.setRetrievalRef("12345678128");
		request.setMti("0400");
		request.setProcessCode("995000");
		request.setInstitutionCode("30000000033");
		request.setTransmissionTime("0608123413");
		request.setTransactionEnquiryData(transactionEnquiryData);
		return request;
	}
	
	@Test
	public void process_InvalidMerchantRef() throws BaseBusinessException, JsonFormatException {
		ReversalRequest request = createReversalRequest();
		String merchantRef = "123456789012345678901234567890";
		ReversalTransactionEnquiryData transactionEnquiryData = request.getTransactionEnquiryData();
		transactionEnquiryData.setMerchantReferenceNumber(merchantRef);
		request.setTransactionEnquiryData(transactionEnquiryData);
	
		JsonFormatException e = assertThrows(JsonFormatException.class, () -> unit.process(request));
		
		assertEquals("30", e.getCode());
		assertEquals("[30]Invalid Merchant Reference Number", e.getMessage());		
	}
	
	@Test
	public void process_NullOrderRequest() {
		ReversalRequest request = createReversalRequest();	
		when(mockRequestService.findBySearchId(any(String.class))).thenReturn(null);
		
		BaseBusinessException e = assertThrows(BaseBusinessException.class, () -> unit.process(request));
		
		assertEquals("80", e.getCode());
		assertEquals("[80]Original Order can't find.", e.getMessage());
		verify(mockRequestService, times(1)).findBySearchId(any(String.class));
	}
	
	@Test 
	public void process_RequestStateTimedOut() throws BaseBusinessException, JsonFormatException {
		ReversalRequest request = createReversalRequest();
		Request orderRequest = new Request();
		orderRequest.setTransactionId(null);
		orderRequest.setState(RequestState.TIMED_OUT.getState());
		when(mockRequestService.findBySearchId(any(String.class))).thenReturn(orderRequest);
		when(mockRequestService.save(any(Request.class))).thenReturn(any(Request.class));
		
		BaseBusinessException e = assertThrows(BaseBusinessException.class, () -> unit.process(request));
		
		ArgumentCaptor<Request> argumentCaptorRequest = ArgumentCaptor.forClass(Request.class);
		verify(mockRequestService, times(1)).save(argumentCaptorRequest.capture());
		Request orderReq = argumentCaptorRequest.getValue();
		
		assertEquals(RequestState.REVERSED.getState(), orderReq.getState());
		assertEquals("68", e.getCode());
		assertEquals("[68]Order Timeout.", e.getMessage());
		verify(mockRequestService, times(1)).findBySearchId(any(String.class));
		verify(mockRequestService, times(1)).save(any(Request.class));
	}
	
	@Test
	public void process_RequestSave_OptimisticLockException() throws BaseBusinessException, JsonFormatException {
		ReversalRequest request = createReversalRequest();
		Request orderRequest = new Request();
		orderRequest.setTransactionId(null);
		orderRequest.setCreationDate(Timestamp.valueOf("2019-03-21 04:44:01"));
		orderRequest.setOrderTimeout(30);
		orderRequest.setState(RequestState.WAITING.getState());
		when(mockRequestService.findBySearchId(any(String.class))).thenReturn(orderRequest);
		when(mockRequestService.save(any(Request.class))).thenThrow(OptimisticLockingFailureException.class);
		when(mockRequestService.refresh(any(Request.class))).thenReturn(any());

		unit.process(request);
		
		ArgumentCaptor<Request> argumentCaptorRequest = ArgumentCaptor.forClass(Request.class);
		verify(mockRequestService, times(1)).save(argumentCaptorRequest.capture());
		Request orderReq = argumentCaptorRequest.getValue();
		
		assertEquals(RequestState.REVERSED.getState(), orderReq.getState());
		verify(mockRequestService, times(1)).findBySearchId(any(String.class));
		verify(mockRequestService, times(1)).save(any(Request.class));
		verify(mockRequestService, times(1)).refresh(any(Request.class)); //this checks for the exception case
	}
	
	@Test
	public void process_RequestStateReversed() {
		ReversalRequest request = createReversalRequest();
		Request orderRequest = new Request();
		orderRequest.setTransactionId(null);
		orderRequest.setState(RequestState.REVERSED.getState());
		when(mockRequestService.findBySearchId(any(String.class))).thenReturn(orderRequest);
		
		BaseBusinessException e = assertThrows(BaseBusinessException.class, () -> unit.process(request));
		
		assertEquals("68", e.getCode());
		assertEquals("[68]Order Timeout.", e.getMessage());
		verify(mockRequestService, times(1)).findBySearchId(any(String.class));
	}
	
	@Test
	public void process_WrongRequestState_GenericSystemError() {
		ReversalRequest request = createReversalRequest();
		Request orderRequest = new Request();
		orderRequest.setTransactionId(null);
		orderRequest.setState(RequestState.NO_ORDER_ADVICE.getState());
		when(mockRequestService.findBySearchId(any(String.class))).thenReturn(orderRequest);
		
		BaseBusinessException e = assertThrows(BaseBusinessException.class, () -> unit.process(request));
		
		assertEquals("06", e.getCode());
		assertEquals("[06]Generic System Error.", e.getMessage());
		verify(mockRequestService, times(1)).findBySearchId(any(String.class));
		
	}
	
	@Test 
	public void process_ValidOrderRequest_TransactionIdNull() throws BaseBusinessException, JsonFormatException {
		ReversalRequest request = createReversalRequest();
		Request orderRequest = new Request();
		orderRequest.setTransactionId(null);
		orderRequest.setState(RequestState.WAITING.getState());
		when(mockRequestService.findBySearchId(any(String.class))).thenReturn(orderRequest);
		when(mockRequestService.save(any(Request.class))).thenReturn(any(Request.class));
		
		unit.process(request);
		
		ArgumentCaptor<Request> argumentCaptorRequest = ArgumentCaptor.forClass(Request.class);
		verify(mockRequestService, times(1)).save(argumentCaptorRequest.capture());
		Request orderReq = argumentCaptorRequest.getValue();
		
		verify(mockRequestService, times(1)).findBySearchId(any(String.class));
		verify(mockRequestService, times(1)).save(any(Request.class));
		assertEquals(RequestState.REVERSED.getState(),orderReq.getState());
	}
	
	@Test
	public void process_TransactionNotFound() {
		ReversalRequest request = createReversalRequest();
		Request orderRequest = new Request();
		orderRequest.setTransactionId(32434428);
		when(mockRequestService.findBySearchId(any(String.class))).thenReturn(orderRequest);
		when(mockTransactionService.retrieve(anyInt())).thenReturn(null);
		
		BaseBusinessException e = assertThrows(BaseBusinessException.class, () -> unit.process(request));
		
		assertEquals("12", e.getCode());
		assertEquals("[12]Transaction Not Found", e.getMessage());
		verify(mockRequestService, times(1)).findBySearchId(any(String.class));
		verify(mockTransactionService, times(1)).retrieve(anyInt());
	}
	
	@Test
	public void process_PaynowTransactionDataNotFound() {
		ReversalRequest request = createReversalRequest();
		Request orderRequest = new Request();
		orderRequest.setTransactionId(32434428);
		TransactionEntity transaction =  new TransactionEntity();
		transaction.setState(TransactionState.STATE_SETTLED.getState());
		when(mockRequestService.findBySearchId(any(String.class))).thenReturn(orderRequest);
		when(mockTransactionService.retrieve(anyInt())).thenReturn(transaction);
		when(mockPaynowTransactionDataService.getPaynowTransactionData(anyInt())).thenReturn(null);
		
		BaseBusinessException e = assertThrows(BaseBusinessException.class, () -> unit.process(request));
		
		assertEquals("12", e.getCode());
		assertEquals("[12]Transaction Not Found", e.getMessage());
		verify(mockRequestService, times(1)).findBySearchId(any(String.class));
		verify(mockTransactionService, times(1)).retrieve(anyInt());
		verify(mockPaynowTransactionDataService, times(1)).getPaynowTransactionData(anyInt());
	}
	
	@Test
	public void process_TransactionTypeNotSupported() {
		ReversalRequest request = createReversalRequest();
		Request orderRequest = new Request();
		orderRequest.setTransactionId(32434428);
		TransactionEntity transaction =  new TransactionEntity();
		transaction.setState(TransactionState.STATE_REVERSAL_PENDING.getState());
		Timestamp timestamp = new Timestamp(Calendar.getInstance().getTimeInMillis());
		transaction.setBankSettleDate(timestamp);
		PaynowTransactionData paynowTransactionData = new PaynowTransactionData();
		when(mockRequestService.findBySearchId(any(String.class))).thenReturn(orderRequest);
		when(mockTransactionService.retrieve(anyInt())).thenReturn(transaction);
		when(mockPaynowTransactionDataService.getPaynowTransactionData(anyInt())).thenReturn(paynowTransactionData);
		
		BaseBusinessException e = assertThrows(BaseBusinessException.class, () -> unit.process(request));
		
		assertEquals("06", e.getCode());
		assertEquals("[06]Generic System Error.", e.getMessage());
		verify(mockRequestService, times(1)).findBySearchId(any(String.class));
		verify(mockTransactionService, times(1)).retrieve(anyInt());
		verify(mockPaynowTransactionDataService, times(1)).getPaynowTransactionData(anyInt());
	}
	
	@Test
	public void process_TransactionStateSettled_OptimisticLockingFailureException() throws BaseBusinessException, JsonFormatException {
		ReversalRequest request = createReversalRequest();
		Request orderRequest = new Request();
		orderRequest.setState(RequestState.WAITING.getState());
		TransactionEntity transaction =  new TransactionEntity();
		orderRequest.setTransactionId(32434428);
		transaction.setAcqApprovalCode("123123");
		transaction.setAcquirerAuthResponseId("321321");
		transaction.setState(TransactionState.STATE_SETTLED.getState());
		Timestamp timestamp = new Timestamp(Calendar.getInstance().getTimeInMillis());
		transaction.setBankSettleDate(timestamp);
		PaynowTransactionData paynowTransactionData = new PaynowTransactionData();
		paynowTransactionData.setCreditAmount(new BigDecimal("111"));
		paynowTransactionData.setCreditCurrency("SGD");
		paynowTransactionData.setCreditingTime("121212");
		paynowTransactionData.setReceivingAccountNumber("112233");
		paynowTransactionData.setReceivingAccountType("OWN");
		paynowTransactionData.setSendingAccountBank("OCBC");
		paynowTransactionData.setSendingAccountName("abcd");
		paynowTransactionData.setSendingAccountNumber("44332211");
		paynowTransactionData.setMerchantReferenceNumber("1122334455667788990012345");
		paynowTransactionData.setAdditionalBankReference("additionalbankref");
		Request orderRequest2 = new Request();
		when(mockRequestService.findBySearchId(any(String.class))).thenReturn(orderRequest);
		when(mockTransactionService.retrieve(anyInt())).thenReturn(transaction);
		when(mockPaynowTransactionDataService.getPaynowTransactionData(anyInt())).thenReturn(paynowTransactionData);
		when(mockRequestService.findBySearchId(any(String.class))).thenReturn(orderRequest);
		when(mockRequestService.save(orderRequest)).thenThrow(OptimisticLockingFailureException.class);
		when(mockRequestService.refresh(orderRequest)).thenReturn(orderRequest2);
		when(mockRequestService.save(orderRequest2)).thenReturn(orderRequest2);
		
		unit.process(request);
		
		ArgumentCaptor<Request> argumentCaptorRequest = ArgumentCaptor.forClass(Request.class);
		verify(mockRequestService, times(2)).save(argumentCaptorRequest.capture());
		List<Request> orderReq = argumentCaptorRequest.getAllValues();
		
		assertEquals(RequestState.REVERSED.getState(),orderReq.get(0).getState());
		assertEquals(RequestState.REVERSED.getState(),orderReq.get(1).getState());
		verify(mockRequestService, times(2)).findBySearchId(any(String.class));
		verify(mockTransactionService, times(1)).retrieve(anyInt());
		verify(mockPaynowTransactionDataService, times(1)).getPaynowTransactionData(anyInt());
		verify(mockRequestService, times(1)).refresh(orderRequest);	
		verify(mockRequestService, times(1)).save(orderRequest2); //checks for the exception
	}
	
	
	@Test
	public void process_OrderTimedOutBeforeReversal() {
		ReversalRequest request = createReversalRequest();
		Request orderRequest = new Request();
		orderRequest.setState(RequestState.WAITING.getState());
		TransactionEntity transaction =  new TransactionEntity();
		orderRequest.setTransactionId(32434428);
		transaction.setAcqApprovalCode("123123");
		transaction.setAcquirerAuthResponseId("321321");
		transaction.setState(TransactionState.STATE_SETTLED.getState());
		Timestamp timestamp = Timestamp.valueOf("2020-05-17 10:10:10.0");
		transaction.setBankSettleDate(timestamp);
		PaynowTransactionData paynowTransactionData = new PaynowTransactionData();
		paynowTransactionData.setCreditAmount(new BigDecimal("111"));
		paynowTransactionData.setCreditCurrency("SGD");
		paynowTransactionData.setCreditingTime("121212");
		paynowTransactionData.setReceivingAccountNumber("112233");
		paynowTransactionData.setReceivingAccountType("OWN");
		paynowTransactionData.setSendingAccountBank("OCBC");
		paynowTransactionData.setSendingAccountName("abcd");
		paynowTransactionData.setSendingAccountNumber("44332211");
		paynowTransactionData.setMerchantReferenceNumber("1122334455667788990012345");
		paynowTransactionData.setAdditionalBankReference("additionalbankref");
		when(mockRequestService.findBySearchId(any(String.class))).thenReturn(orderRequest);
		when(mockTransactionService.retrieve(anyInt())).thenReturn(transaction);
		when(mockPaynowTransactionDataService.getPaynowTransactionData(anyInt())).thenReturn(paynowTransactionData);
		when(mockRequestService.findBySearchId(any(String.class))).thenReturn(orderRequest);
		
		BaseBusinessException e = assertThrows(BaseBusinessException.class,() -> unit.process(request));
		
		assertEquals("00", e.getCode());
		assertEquals("[00]Order Already Timed Out. No reversal needed.", e.getMessage());
		verify(mockRequestService, times(1)).findBySearchId(any(String.class));
		verify(mockTransactionService, times(1)).retrieve(anyInt());
		verify(mockPaynowTransactionDataService, times(1)).getPaynowTransactionData(anyInt());		
	}
	
	@Test
	public void process_reverseTransaction_ExceptionWithMessage() throws BaseBusinessException, JsonFormatException {
		
		ReversalRequest request = createReversalRequest();
		Request orderRequest = new Request();
		orderRequest.setState(RequestState.WAITING.getState());
		TransactionEntity transaction =  new TransactionEntity();
		orderRequest.setTransactionId(32434428);
		transaction.setAcqApprovalCode("123123");
		transaction.setAcquirerAuthResponseId("321321");
		transaction.setState(TransactionState.STATE_SETTLED.getState());
		Timestamp timestamp = new Timestamp(Calendar.getInstance().getTimeInMillis());
		transaction.setBankSettleDate(timestamp);
		PaynowTransactionData paynowTransactionData = new PaynowTransactionData();
		paynowTransactionData.setCreditAmount(new BigDecimal("111"));
		paynowTransactionData.setCreditCurrency("SGD");
		paynowTransactionData.setCreditingTime("121212");
		paynowTransactionData.setReceivingAccountNumber("112233");
		paynowTransactionData.setReceivingAccountType("OWN");
		paynowTransactionData.setSendingAccountBank("OCBC");
		paynowTransactionData.setSendingAccountName("abcd");
		paynowTransactionData.setSendingAccountNumber("44332211");
		paynowTransactionData.setMerchantReferenceNumber("1122334455667788990012345");
		paynowTransactionData.setAdditionalBankReference("additionalbankref");
		PaynowBankAdapter mockPaynowBankAdapter = mock(PaynowBankAdapter.class);
		when(mockRequestService.findBySearchId(any(String.class))).thenReturn(orderRequest);
		when(mockTransactionService.retrieve(anyInt())).thenReturn(transaction);
		when(mockPaynowTransactionDataService.getPaynowTransactionData(anyInt())).thenReturn(paynowTransactionData);
		when(mockRequestService.findBySearchId(any(String.class))).thenReturn(orderRequest);
		when(mockRequestService.save(orderRequest)).thenReturn(orderRequest);
		when(mockBankAdapterProcessingFactory.getBankAdapter(any())).thenReturn(mockPaynowBankAdapter);
		BaseBusinessException e = new BaseBusinessException("999", "TestingTesting");
		when(mockPaynowBankAdapter.processRefundReversal(any())).thenThrow(e);
		when(mockExceptionHandler.handleBusinessException(any(), any())).thenReturn(any());
		
		unit.process(request);
		
		ArgumentCaptor<Request> argumentCaptorRequest = ArgumentCaptor.forClass(Request.class);
		verify(mockRequestService, times(1)).save(argumentCaptorRequest.capture());
		Request orderReq = argumentCaptorRequest.getValue();
		
		assertEquals(RequestState.REVERSED.getState(),orderReq.getState());
		verify(mockRequestService, times(2)).findBySearchId(any(String.class));
		verify(mockTransactionService, times(1)).retrieve(anyInt());
		verify(mockPaynowTransactionDataService, times(1)).getPaynowTransactionData(anyInt());	
		verify(mockRequestService, times(1)).save(orderRequest);
	}
	
	@Test
	public void process_reverseTransaction_ExceptionNoMessage() throws BaseBusinessException, JsonFormatException {
		
		ReversalRequest request = createReversalRequest();
		Request orderRequest = new Request();
		orderRequest.setState(RequestState.WAITING.getState());
		TransactionEntity transaction =  new TransactionEntity();
		orderRequest.setTransactionId(32434428);
		transaction.setAcqApprovalCode("123123");
		transaction.setAcquirerAuthResponseId("321321");
		transaction.setState(TransactionState.STATE_SETTLED.getState());
		Timestamp timestamp = new Timestamp(Calendar.getInstance().getTimeInMillis());
		transaction.setBankSettleDate(timestamp);
		PaynowTransactionData paynowTransactionData = new PaynowTransactionData();
		paynowTransactionData.setCreditAmount(new BigDecimal("111"));
		paynowTransactionData.setCreditCurrency("SGD");
		paynowTransactionData.setCreditingTime("121212");
		paynowTransactionData.setReceivingAccountNumber("112233");
		paynowTransactionData.setReceivingAccountType("OWN");
		paynowTransactionData.setSendingAccountBank("OCBC");
		paynowTransactionData.setSendingAccountName("abcd");
		paynowTransactionData.setSendingAccountNumber("44332211");
		paynowTransactionData.setMerchantReferenceNumber("1122334455667788990012345");
		paynowTransactionData.setAdditionalBankReference("additionalbankref");
		PaynowBankAdapter mockPaynowBankAdapter = mock(PaynowBankAdapter.class);
		when(mockRequestService.findBySearchId(any(String.class))).thenReturn(orderRequest);
		when(mockTransactionService.retrieve(anyInt())).thenReturn(transaction);
		when(mockPaynowTransactionDataService.getPaynowTransactionData(anyInt())).thenReturn(paynowTransactionData);
		when(mockRequestService.findBySearchId(any(String.class))).thenReturn(orderRequest);
		when(mockRequestService.save(orderRequest)).thenReturn(orderRequest);
		when(mockBankAdapterProcessingFactory.getBankAdapter(any())).thenReturn(mockPaynowBankAdapter);
		when(mockPaynowBankAdapter.processRefundReversal(any())).thenThrow(BaseBusinessException.class);
		when(mockExceptionHandler.handleBusinessException(any(), any())).thenReturn(any());
		
		unit.process(request);
		
		ArgumentCaptor<Request> argumentCaptorRequest = ArgumentCaptor.forClass(Request.class);
		verify(mockRequestService, times(1)).save(argumentCaptorRequest.capture());
		Request orderReq = argumentCaptorRequest.getValue();
		
		assertEquals(RequestState.REVERSED.getState(),orderReq.getState());
		verify(mockRequestService, times(2)).findBySearchId(any(String.class));
		verify(mockTransactionService, times(1)).retrieve(anyInt());
		verify(mockPaynowTransactionDataService, times(1)).getPaynowTransactionData(anyInt());	
		verify(mockRequestService, times(1)).save(orderRequest);
		
	}
	
	@Test
	public void process_success() throws BaseBusinessException, JsonFormatException {
		
		ReversalRequest request = createReversalRequest();
		Request orderRequest = new Request();
		orderRequest.setState(RequestState.WAITING.getState());
		TransactionEntity transaction =  new TransactionEntity();
		orderRequest.setTransactionId(32434428);
		transaction.setAcqApprovalCode("123123");
		transaction.setAcquirerAuthResponseId("321321");
		transaction.setState(TransactionState.STATE_SETTLED.getState());
		Timestamp timestamp = new Timestamp(Calendar.getInstance().getTimeInMillis());
		transaction.setBankSettleDate(timestamp);
		PaynowTransactionData paynowTransactionData = new PaynowTransactionData();
		paynowTransactionData.setCreditAmount(new BigDecimal("111"));
		paynowTransactionData.setCreditCurrency("SGD");
		paynowTransactionData.setCreditingTime("121212");
		paynowTransactionData.setReceivingAccountNumber("112233");
		paynowTransactionData.setReceivingAccountType("OWN");
		paynowTransactionData.setSendingAccountBank("OCBC");
		paynowTransactionData.setSendingAccountName("abcd");
		paynowTransactionData.setSendingAccountNumber("44332211");
		paynowTransactionData.setMerchantReferenceNumber("1122334455667788990012345");
		paynowTransactionData.setAdditionalBankReference("additionalbankref");
		PaynowBankAdapter mockPaynowBankAdapter = mock(PaynowBankAdapter.class);
		when(mockRequestService.findBySearchId(any(String.class))).thenReturn(orderRequest);
		when(mockTransactionService.retrieve(anyInt())).thenReturn(transaction);
		when(mockPaynowTransactionDataService.getPaynowTransactionData(anyInt())).thenReturn(paynowTransactionData);
		when(mockRequestService.findBySearchId(any(String.class))).thenReturn(orderRequest);
		when(mockRequestService.save(orderRequest)).thenReturn(orderRequest);
		when(mockBankAdapterProcessingFactory.getBankAdapter(any())).thenReturn(mockPaynowBankAdapter);
		when(mockPaynowBankAdapter.processRefundReversal(any())).thenReturn(any());
		
		unit.process(request);
		
		ArgumentCaptor<Request> argumentCaptorRequest = ArgumentCaptor.forClass(Request.class);
		verify(mockRequestService, times(1)).save(argumentCaptorRequest.capture());
		Request orderReq = argumentCaptorRequest.getValue();
		
		assertEquals(RequestState.REVERSED.getState(),orderReq.getState());
		verify(mockRequestService, times(2)).findBySearchId(any(String.class));
		verify(mockTransactionService, times(1)).retrieve(anyInt());
		verify(mockPaynowTransactionDataService, times(1)).getPaynowTransactionData(anyInt());	
		verify(mockRequestService, times(1)).save(orderRequest);
		
		
	}
}