package com.nets.nps.paynow.upi.service.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nets.nps.paynow.utils.UtilComponents;
import com.nets.nps.upi.entity.DebitTransactionRequest;
import com.nets.nps.upi.entity.DebitTransactionRequestTrxInfo;
import com.nets.nps.upi.entity.DebitTransactionResponse;
import com.nets.nps.upi.entity.DiscountDetail;
import com.nets.nps.upi.entity.MerchantInfo;
import com.nets.nps.upi.entity.MessageResponse;
import com.nets.nps.upi.entity.MsgInfo;
import com.nets.nps.upi.entity.PullDebitRequest;
import com.nets.nps.upi.entity.PullDebitRequestTransactionDomainData;
import com.nets.nps.upi.entity.PullDebitResponse;
import com.nets.nps.upi.entity.UpiProxyRequest;
import com.nets.nps.upi.service.DebitTransactionService;
import com.nets.nps.upi.service.impl.PullDebitRequestAdapter;
import com.nets.nps.upi.service.impl.WalletAdapter;
import com.nets.upos.commons.constants.IDRegistryKeyTypes;
import com.nets.upos.commons.constants.TransactionTypes;
import com.nets.upos.commons.enums.ContextData;
import com.nets.upos.commons.enums.ForeignAcqAuthState;
import com.nets.upos.commons.enums.QrQueryState;
import com.nets.upos.commons.enums.QualificationState;
import com.nets.upos.commons.enums.TransactionState;
import com.nets.upos.commons.exception.BaseBusinessException;
import com.nets.upos.commons.exception.JsonFormatException;
import com.nets.upos.core.entity.AcquirerEntity;
import com.nets.upos.core.entity.CurrencyEntity;
import com.nets.upos.core.entity.DebitRequestDetails;
import com.nets.upos.core.entity.ForeignAcquirerAuthorizationEntity;
import com.nets.upos.core.entity.ForeignAcquirerEntity;
import com.nets.upos.core.entity.IDRegistryRequest;
import com.nets.upos.core.entity.IssuerPayAdviceEntity;
import com.nets.upos.core.entity.QrQueryDetails;
import com.nets.upos.core.entity.QualificationEntity;
import com.nets.upos.core.entity.SOFTypeEntity;
import com.nets.upos.core.entity.TransactionBasicsEntity;
import com.nets.upos.core.service.AcquirerService;
import com.nets.upos.core.service.CurrencyService;
import com.nets.upos.core.service.DebitRequestDetailService;
import com.nets.upos.core.service.ForeignAcquirerAuthorizationService;
import com.nets.upos.core.service.ForeignAcquirerService;
import com.nets.upos.core.service.IDRegistryService;
import com.nets.upos.core.service.IssuerPayAdviceService;
import com.nets.upos.core.service.QrQueryDetailsService;
import com.nets.upos.core.service.QualificationService;
import com.nets.upos.core.service.TransactionBasicsService;

@SpringBootTest(classes = {DebitTransactionService.class})
@TestPropertySource(locations = "classpath:paynow-local.properties")
public class DebitTransactionServiceTest {
	
	@Autowired
	DebitTransactionService unit;
	
	@MockBean
	PullDebitRequestAdapter mockPullDebitRequestAdapter;

	@MockBean
	WalletAdapter mockWalletAdapter;
	
	@MockBean
	CurrencyService mockCurrencyService;
	
	@MockBean
	AcquirerService mockAcquirerService;
	
	@MockBean
	TransactionBasicsService mockTransactionBasicsService;
	
	@MockBean
	IDRegistryService mockIDRegistryService;
	
	@MockBean
	ForeignAcquirerAuthorizationService mockForeignAcquirerAuthorizationService;
	
	@MockBean
	IssuerPayAdviceService mockIssuerPayAdviceService;
	
	@MockBean
	ForeignAcquirerService mockForeignAcquirerService;
	
	@MockBean
	QrQueryDetailsService mockQrQueryDetailsService;
	
	@MockBean
	QualificationService mockQualificationService;
	
	@MockBean
	DebitRequestDetailService mockDebitRequestDetailService;

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
		trxInfo.setTransDatetime("1212010203");
		trxInfo.setTraceNum("123456");
		List<DiscountDetail> discountList = new ArrayList<>();		
		DiscountDetail dc = new DiscountDetail();
		dc.setDiscountAmt("0.01");
		discountList.add(dc);
		trxInfo.setDiscountDetails(discountList);
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
	
	private String createMessageString(UpiProxyRequest upiProxyRequest) throws JsonProcessingException {
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
	
	private PullDebitResponse createPullDebitResponse(PullDebitRequest pullDebitReq) {
		PullDebitResponse pullDebitRes = new PullDebitResponse();
		pullDebitRes.setResponseCode("00");
		pullDebitRes.setRetrievalRef(pullDebitReq.getRetrievalRef());
		pullDebitRes.setTransmissionTime(pullDebitReq.getTransmissionTime());
		pullDebitRes.setInstitutionCode(pullDebitReq.getInstitutionCode());
		pullDebitRes.setDebitPassed("Y");
		pullDebitRes.setDebitApprovalRetrievalRef("debit approval retrieval ref");

		return pullDebitRes;
	}
	
	@Test
	public void validateDuplicateIdRegistry() throws BaseBusinessException, JsonProcessingException {
		DebitTransactionRequest debitTransactionRequest = createDebitTransactionRequest();
		UpiProxyRequest upiProxyRequest = createUpiProxyRequest(debitTransactionRequest);
		String message = createMessageString(upiProxyRequest);
		PullDebitRequest pullDebitRequest = createPullDebitRequest();
		
		when(mockPullDebitRequestAdapter.convertToPullDebitRequest(any(DebitTransactionRequest.class))).thenReturn(pullDebitRequest);
		
		//in create tbe
		Map<ContextData, Object> entityMap = new HashMap();
		AcquirerEntity acquirerEntity = new AcquirerEntity();
		acquirerEntity.setAcquirerId(104);
		entityMap.put(ContextData.ACQUIRER, acquirerEntity);
		
		SOFTypeEntity sofEntity = new SOFTypeEntity();
		sofEntity.setCardTypeId(0);
		entityMap.put(ContextData.SOF_TYPE, sofEntity);
		
		when(mockAcquirerService.getAcquirerSofDetails(any())).thenReturn(entityMap);
		
		CurrencyEntity mockCurrencyEntity = new CurrencyEntity();
		mockCurrencyEntity.setCurrencyId(111);
		mockCurrencyEntity.setUnits("123");
		when(mockCurrencyService.getCurrencyByCurrencyIsoNumber(any())).thenReturn(mockCurrencyEntity);
		
		when(mockIDRegistryService.saveIDRegistry(any(IDRegistryRequest.class))).thenThrow(DataIntegrityViolationException.class);
		
		BaseBusinessException e = assertThrows(BaseBusinessException.class, () -> unit.process(message));
		
		assertEquals("68", e.getCode());
		assertEquals("outbound payment inquiry reach first OR duplicate outbound paymentadvice for the same bank rrn.", e.getErrorMessage());
		
		verify(mockPullDebitRequestAdapter, times(1)).convertToPullDebitRequest(any(DebitTransactionRequest.class));
		verify(mockAcquirerService, times(1)).getAcquirerSofDetails(any());
		verify(mockCurrencyService, times(2)).getCurrencyByCurrencyIsoNumber(anyString());
		verify(mockIDRegistryService, times(1)).saveIDRegistry(any());
		
		ArgumentCaptor<TransactionBasicsEntity> argumentCaptorTbe = ArgumentCaptor.forClass(TransactionBasicsEntity.class);
		verify(mockTransactionBasicsService, times(2)).save(argumentCaptorTbe.capture());
		TransactionBasicsEntity tbe = argumentCaptorTbe.getValue();
		
		assertEquals(tbe.getState(), TransactionState.STATE_FAILED.getState());
	}
	
	
	@Test
	public void validateSuccessCase() throws JsonProcessingException, BaseBusinessException, JsonFormatException {
		DebitTransactionRequest debitTransactionRequest = createDebitTransactionRequest();
		UpiProxyRequest upiProxyRequest = createUpiProxyRequest(debitTransactionRequest);
		String message = createMessageString(upiProxyRequest);
		PullDebitRequest pullDebitRequest = createPullDebitRequest();
		
		when(mockPullDebitRequestAdapter.convertToPullDebitRequest(any(DebitTransactionRequest.class))).thenReturn(pullDebitRequest);
		
		//in create tbe
		Map<ContextData, Object> entityMap = new HashMap();
		AcquirerEntity acquirerEntity = new AcquirerEntity();
		acquirerEntity.setAcquirerId(104);
		entityMap.put(ContextData.ACQUIRER, acquirerEntity);
		
		SOFTypeEntity sofEntity = new SOFTypeEntity();
		sofEntity.setCardTypeId(0);
		entityMap.put(ContextData.SOF_TYPE, sofEntity);
		
		when(mockAcquirerService.getAcquirerSofDetails(any())).thenReturn(entityMap);
		
		CurrencyEntity mockCurrencyEntity = new CurrencyEntity();
		mockCurrencyEntity.setCurrencyId(111);
		mockCurrencyEntity.setUnits("123");
		when(mockCurrencyService.getCurrencyByCurrencyIsoNumber(any())).thenReturn(mockCurrencyEntity);
		
		//in foreign acq auth
		ForeignAcquirerEntity foreignAcquirer = new ForeignAcquirerEntity();
		foreignAcquirer.setId(1);
		
		when(mockForeignAcquirerService.getForeignAcquirer(anyString())).thenReturn(foreignAcquirer);
	
		PullDebitResponse pullDebitResponse = createPullDebitResponse(pullDebitRequest);
		String pullDebitResponseString = UtilComponents.getStringFromObject(pullDebitResponse);

		String debitTransactionResponseString = createDebitTransactionResponseString(debitTransactionRequest);
		
		when(mockWalletAdapter.sendAndReceiveFromBank(any(), any(HttpEntity.class))).thenReturn(new ResponseEntity<>(pullDebitResponseString, HttpStatus.OK));
		
		String responseString = unit.process(message);
		UpiProxyRequest response = (UpiProxyRequest) UtilComponents.getObjectFromString(responseString, UpiProxyRequest.class);
	
		verify(mockPullDebitRequestAdapter, times(1)).convertToPullDebitRequest(any(DebitTransactionRequest.class));
		verify(mockAcquirerService, times(1)).getAcquirerSofDetails(any());
		verify(mockCurrencyService, times(4)).getCurrencyByCurrencyIsoNumber(anyString());
		verify(mockForeignAcquirerService, times(1)).getForeignAcquirer(anyString());
		verify(mockWalletAdapter, times(1)).sendAndReceiveFromBank(any(), any(HttpEntity.class));
		
		assertEquals(response.getBankId(), upiProxyRequest.getBankId());
		assertEquals(response.getBankType(), upiProxyRequest.getBankType());
		assertNotNull(response.getCorrelationId());
		assertEquals(response.getInstCode(), upiProxyRequest.getInstCode());
		assertEquals(response.getTransactionType(), upiProxyRequest.getTransactionType());
		assertEquals(response.getUpiProxyRequestJsonData(), debitTransactionResponseString);	
	
		//verify saving db stuff argument captor
		ArgumentCaptor<TransactionBasicsEntity> argumentCaptorTbe = ArgumentCaptor.forClass(TransactionBasicsEntity.class);
		verify(mockTransactionBasicsService, times(1)).save(argumentCaptorTbe.capture());
		TransactionBasicsEntity tbe = argumentCaptorTbe.getValue();
		tbe.setId((long) 1234);
		assertEquals(debitTransactionRequest.getTrxInfo().getTraceNum(), tbe.getStan());
		assertNotNull(tbe.getAuthorizeDate());
		assertEquals(TransactionState.STATE_PENDING.getState(), tbe.getState());
		assertEquals(TransactionTypes.TYPE_OUTBOUND_PAYMENT_ADVICE, tbe.getTranType());
		assertEquals(debitTransactionRequest.getTrxInfo().getRetrivlRefNum(), tbe.getAuthResponseId());
		assertEquals(new BigDecimal(debitTransactionRequest.getTrxInfo().getTrxAmt()), tbe.getSourceAmount());
		assertEquals(mockCurrencyEntity.getCurrencyId(), tbe.getSourceCurrencyId());
		assertEquals(new BigDecimal(debitTransactionRequest.getTrxInfo().getBillAmt()), tbe.getTargetAmount());
		assertEquals(mockCurrencyEntity.getCurrencyId(), tbe.getTargetCurrencyId());
		assertEquals(sofEntity.getCardTypeId(), tbe.getCardTypeId());
		assertEquals(new Character('Y'), tbe.getIsDccTransaction());
		assertEquals(debitTransactionRequest.getTrxInfo().getMerchantInfo().getMid(), tbe.getMid());
		assertEquals(debitTransactionRequest.getTrxInfo().getMerchantInfo().getTermId(), tbe.getTid());
		assertNull(tbe.getAcqMerMappingId());
		assertEquals(acquirerEntity.getAcquirerId(), tbe.getAcquirerId());
		assertNotNull(tbe.getCreationDate());
		assertNotNull(tbe.getLastUpdatedDate());
		
		ArgumentCaptor<IDRegistryRequest> argumentCaptorIDRegistryRequest = ArgumentCaptor.forClass(IDRegistryRequest.class);
		verify(mockIDRegistryService, times(1)).saveIDRegistry(argumentCaptorIDRegistryRequest.capture());
		IDRegistryRequest irr = argumentCaptorIDRegistryRequest.getValue();
		
		assertEquals(IDRegistryKeyTypes.OUTBOUND_QR_PAYMENT, irr.getKeyType());
		assertEquals("30000000001_" + debitTransactionRequest.getTrxInfo().getRetrivlRefNum(), irr.getKeyData());
		assertNotNull(irr.getCreationDate());
		
		ArgumentCaptor<ForeignAcquirerAuthorizationEntity> argumentCaptorFaae = ArgumentCaptor.forClass(ForeignAcquirerAuthorizationEntity.class);
		verify(mockForeignAcquirerAuthorizationService, times(2)).save(argumentCaptorFaae.capture());
		ForeignAcquirerAuthorizationEntity faaeBeforeResponse = argumentCaptorFaae.getAllValues().get(0);
		
		assertEquals(foreignAcquirer.getId(), faaeBeforeResponse.getForeignAcquirerId());
		assertNotNull(faaeBeforeResponse.getCreationDate());
		
		ForeignAcquirerAuthorizationEntity faaeAfterResponse = argumentCaptorFaae.getAllValues().get(1);
		assertEquals(ForeignAcqAuthState.COMPLETED.getState().intValue(), faaeAfterResponse.getState());
		assertEquals(pullDebitResponse.getRetrievalRef(), faaeAfterResponse.getRetrievalRefNo());
		assertEquals(pullDebitResponse.getDebitApprovalRetrievalRef(), faaeAfterResponse.getAcquirerTransactionRef());
		assertEquals(pullDebitResponse.getTransmissionTime(), faaeAfterResponse.getAcquirerProcessingTime());
		assertEquals(pullDebitResponse.getDebitApprovalCode(), faaeAfterResponse.getApprovalCode());
		assertEquals(pullDebitResponse.getResponseCode(), faaeAfterResponse.getResponseCode());
		
		ArgumentCaptor<QrQueryDetails> argumentCaptorQqd = ArgumentCaptor.forClass(QrQueryDetails.class);
		verify(mockQrQueryDetailsService, times(1)).save(argumentCaptorQqd.capture());
		QrQueryDetails qqd = argumentCaptorQqd.getValue();
		
		assertEquals(debitTransactionRequest.getTrxInfo().getRetrivlRefNum(), qqd.getRetrievalRefNo());
		assertEquals(pullDebitRequest.getAcquirerInstitutionCode(), qqd.getForeignAcquirerInstitutionCode());
		assertEquals(QrQueryState.PROCESSING.getState(), qqd.getState());
		assertEquals(foreignAcquirer.getId(), qqd.getForeignAcquirerId());
		assertEquals(debitTransactionRequest.getTrxInfo().getTransDatetime(), qqd.getTransmissionTime());
		assertEquals(sofEntity.getCardTypeId(), qqd.getCardTypeId());
		assertEquals(acquirerEntity.getAcquirerId(), qqd.getAcquirerId());
		assertNotNull(qqd.getCreationDate());
		assertNotNull(qqd.getLastUpdatedDate());
		assertEquals(Integer.valueOf(300), qqd.getQueryTimeout());
		
		ArgumentCaptor<QualificationEntity> argumentCaptorQe = ArgumentCaptor.forClass(QualificationEntity.class);
		verify(mockQualificationService, times(1)).save(argumentCaptorQe.capture());
		QualificationEntity qe = argumentCaptorQe.getValue();
		
		assertEquals(debitTransactionRequest.getTrxInfo().getRetrivlRefNum(), qe.getRetrievalRefNo());
		assertEquals(QualificationState.PROCESSING.getState(), qe.getState());
		assertEquals(mockCurrencyEntity.getCurrencyId(), qe.getConvertCurrencyId());
		assertEquals(Integer.valueOf(mockCurrencyEntity.getUnits()), qe.getTargetCurrencyMinorUnit());
		assertEquals(new BigDecimal(debitTransactionRequest.getTrxInfo().getBillAmt()), qe.getAmount());
		assertEquals(debitTransactionRequest.getMsgInfo().getInsID(), qe.getInstitutionCode());
		assertEquals(sofEntity.getCardTypeId(), qe.getCardTypeId());
		assertEquals(acquirerEntity.getAcquirerId(), qe.getAcquirerId());
		assertEquals(debitTransactionRequest.getTrxInfo().getMerchantInfo().getTermId(), qe.getTid());
		assertEquals(debitTransactionRequest.getTrxInfo().getMerchantInfo().getMid(), qe.getMid());
		assertEquals(debitTransactionRequest.getTrxInfo().getTransDatetime(), qe.getTransmissionTime());
		assertNotNull(qe.getCreationDate());
		assertNotNull(qe.getLastUpdatedDate());
		
		ArgumentCaptor<IssuerPayAdviceEntity> argumentCaptorIpe = ArgumentCaptor.forClass(IssuerPayAdviceEntity.class);
		verify(mockIssuerPayAdviceService, times(1)).save(argumentCaptorIpe.capture());
		IssuerPayAdviceEntity ipe = argumentCaptorIpe.getValue();
		
		assertEquals(foreignAcquirer.getId(), ipe.getForeignAcquirerId());
		assertEquals(debitTransactionRequest.getTrxInfo().getRetrivlRefNum(), ipe.getRetrievalRefNo());
		assertEquals(debitTransactionRequest.getTrxInfo().getTraceNum(), ipe.getStan());
		assertEquals(debitTransactionRequest.getTrxInfo().getTransDatetime().substring(0, 4), ipe.getTransactionDate());
		assertEquals(debitTransactionRequest.getTrxInfo().getTransDatetime().substring(4, 10), ipe.getTransactionTime());
		assertEquals(debitTransactionRequest.getMsgInfo().getInsID(), ipe.getPaymentRequesterInstId());
		assertEquals(qqd.getForeignAcquirerInstitutionCode(), ipe.getForeignAcquirerInstitutionCode());
		assertNotNull(qqd.getCreationDate());
		assertNotNull(qqd.getLastUpdatedDate());
		
		ArgumentCaptor<DebitRequestDetails> argumentCaptorDrd = ArgumentCaptor.forClass(DebitRequestDetails.class);
		verify(mockDebitRequestDetailService, times(1)).save(argumentCaptorDrd.capture());
		DebitRequestDetails drd = argumentCaptorDrd.getValue();
		
		assertEquals(debitTransactionRequest.getMsgInfo().getMsgId(), drd.getMessageID());
		assertEquals("correlation id", drd.getCorrelationId());
		assertEquals(debitTransactionRequest.getMsgInfo().getMsgType(), drd.getMessageType());
		assertEquals(new BigDecimal(debitTransactionRequest.getTrxInfo().getBillAmt()), drd.getBillAmt());
		assertEquals(Integer.valueOf(debitTransactionRequest.getTrxInfo().getBillCurrency()), drd.getBillCurrency());
		assertEquals(new BigDecimal(debitTransactionRequest.getTrxInfo().getBillConvRate()), drd.getBillConvRate());
		assertEquals(debitTransactionRequest.getTrxInfo().getConvDate(), drd.getConvDate());
		assertEquals(debitTransactionRequest.getTrxInfo().getDebitAccountInfo(), drd.getDebitAccountInfo());
		assertEquals(new BigDecimal(debitTransactionRequest.getTrxInfo().getFeeAmt()), drd.getFeeAmt());
		assertEquals(debitTransactionRequest.getTrxInfo().getMerchantInfo().getFwdIIN(), drd.getFwdIIN());
		assertEquals(new BigDecimal(debitTransactionRequest.getTrxInfo().getMarkupAmt()), drd.getMarkupAmt());
		assertEquals(debitTransactionRequest.getTrxInfo().getMerchantInfo().getMcc(), drd.getMcc());
		assertEquals(debitTransactionRequest.getTrxInfo().getMerchantInfo().getMerchantCountry(), drd.getMerchantCountry());
		assertEquals(debitTransactionRequest.getTrxInfo().getMerchantInfo().getMerchantName(), drd.getMerchantName());
		assertEquals(debitTransactionRequest.getTrxInfo().getMerchantInfo().getMid(), drd.getMid());
		assertEquals(new BigDecimal(debitTransactionRequest.getTrxInfo().getTrxAmt()), drd.getTrxAmt());
		assertEquals(debitTransactionRequest.getTrxInfo().getTrxCurrency(), drd.getTrxCurrency());
		assertEquals(new BigDecimal(debitTransactionRequest.getTrxInfo().getSettAmt()), drd.getSettAmt());
		assertEquals(debitTransactionRequest.getTrxInfo().getSettDate(), drd.getSettDate());
		assertEquals(debitTransactionRequest.getTrxInfo().getPosEntryModeCode(), drd.getPosEntryModeCode());
		assertEquals(debitTransactionRequest.getTrxInfo().getRetrivlRefNum(), drd.getRetreiveRefNum());
		assertEquals(new BigDecimal(debitTransactionRequest.getTrxInfo().getDiscountDetails().get(0).getDiscountAmt()), drd.getDiscountAmt());
	}


}
