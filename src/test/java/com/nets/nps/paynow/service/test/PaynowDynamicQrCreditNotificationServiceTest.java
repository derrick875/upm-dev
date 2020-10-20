package com.nets.nps.paynow.service.test;

import static org.mockito.ArgumentMatchers.any;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.test.context.TestPropertySource;

import com.nets.upos.core.entity.Request;
import com.nets.upos.core.entity.SOFTypeEntity;
import com.nets.upos.core.entity.TransactionEntity;
import com.nets.upos.core.repository.projection.PosInfoOnly;
import com.nets.nps.client.impl.FraudwallClient;
import com.nets.nps.client.impl.NonStopClient;
import com.nets.nps.client.impl.NotificationClient;
import com.nets.nps.client.impl.NotificationRequest;
import com.nets.nps.client.impl.TransactionStateMsg;
import com.nets.nps.paynow.client.bank.BankAdapterProcessingFactory;
import com.nets.nps.paynow.client.bank.ocbc.OcbcPaynowAdapter;
import com.nets.nps.paynow.entity.CreditNotificationRequest;
import com.nets.nps.paynow.entity.PaymentTransactionDomainData;
import com.nets.nps.paynow.exception.ExceptionHandler;
import com.nets.nps.paynow.service.PaynowTransactionDataService;
import com.nets.nps.paynow.service.impl.PaynowDynamicQrCreditNotificationService;
import com.nets.nps.paynow.utils.PaynowUtil;
import com.nets.upos.commons.constants.AdditionalData;
import com.nets.upos.commons.constants.ResponseCodes;
import com.nets.upos.commons.constants.TransactionTypes;
import com.nets.upos.commons.entity.APSRequest;
import com.nets.upos.commons.entity.APSRequestWrapper;
import com.nets.upos.commons.entity.JsonNotification;
import com.nets.upos.commons.enums.BankConfigAttributeName;
import com.nets.upos.commons.enums.ContextData;
import com.nets.upos.commons.enums.RequestState;
import com.nets.upos.commons.enums.TransactionState;
import com.nets.upos.commons.exception.BaseBusinessException;
import com.nets.upos.core.entity.AcqMerAccountEntity;
import com.nets.upos.core.entity.AcquirerEntity;
import com.nets.upos.core.entity.AcquirerMerchantMappingEntity;
import com.nets.upos.core.entity.BankCfgAttrEntity;
import com.nets.upos.core.entity.BankEntity;
import com.nets.upos.core.entity.CardTypeEntity;
import com.nets.upos.core.entity.CodesEntity;
import com.nets.upos.core.entity.MerchantEntity;
import com.nets.upos.core.entity.PaynowTransactionData;
import com.nets.upos.core.entity.PhysicalPosEntity;
import com.nets.upos.core.entity.PosNotificationEntity;
import com.nets.upos.core.service.AcqMerAccountService;
import com.nets.upos.core.service.AcquirerService;
import com.nets.upos.core.service.BankService;
import com.nets.upos.core.service.CodesService;
import com.nets.upos.core.service.IDRegistryService;
import com.nets.upos.core.service.MDRService;
import com.nets.upos.core.service.PhysicalPosService;
import com.nets.upos.core.service.PosNotificationService;
import com.nets.upos.core.service.RequestService;
import com.nets.upos.core.service.SOFTypeService;
import com.nets.upos.core.service.TransactionService;

@SpringBootTest(classes = {PaynowDynamicQrCreditNotificationService.class})
@TestPropertySource(locations = "classpath:paynow-local.properties")
class PaynowDynamicQrCreditNotificationServiceTest {
	
	 static {
	        System.setProperty("PROPERTY_ENCRYPTION_PASSWORD", "test");
	    }

	@Value("${paynow.order.timeout.seconds:45}")
	private Integer payNowOrderTimeout;
	
	@Autowired
	private PaynowDynamicQrCreditNotificationService unit;
	
	@MockBean
	private IDRegistryService mockIdRegistryService;
	
	@MockBean
	private AcquirerService mockAcquirerService;
	
	@MockBean
	private PhysicalPosService mockPhysicalPosService;
	
	@MockBean
	private BankService mockBankService;
	
	@MockBean
	private MDRService mockMdrService;
	
	@MockBean
	private CodesService mockCodesService;
	
	@MockBean
	private SOFTypeService mockSofTypeService;
	
	@MockBean
	private TransactionService mockTransactionService;
	
	@MockBean
	private PaynowTransactionDataService mockPaynowTransactionDataService;
	
	@MockBean
	private NonStopClient mockNonStopClient;
	
	@MockBean
	private NotificationClient mockNotificationClient;
	
	@MockBean
	private PosNotificationService mockPosNotificationService;
	
	@MockBean
	private AcqMerAccountService mockAcqMerAccountService;
	
	@MockBean
	private RequestService mockRequestService;
		
	@MockBean
	private BankAdapterProcessingFactory mockBankAdapterProcessingFactory;
	
	@MockBean
	private OcbcPaynowAdapter mockOcbcPaynowAdapter;
	
	@MockBean
	private ExceptionHandler mockExceptionHandler;
	
	@MockBean
	private FraudwallClient mockFraudwallClient;
	
	private CreditNotificationRequest createCreditNotificationRequest() {
		
		CreditNotificationRequest payment = new CreditNotificationRequest();
		
		PaymentTransactionDomainData transactionDomainData = new PaymentTransactionDomainData();
		transactionDomainData.setMerchantReferenceNumber("ABCDEFGH1234567890");
		transactionDomainData.setCreditAmount("000000001200");
		transactionDomainData.setCreditCurrency("SGD");
		transactionDomainData.setCreditingTime("0000000000");
		payment.setTransactionDomainData(transactionDomainData);
		payment.setInstitutionCode("30000000033");
		payment.setRetrievalRef("12345678901234567890");
		payment.setMti("0000");
		return payment;
	}
	
	private Request createOrderRequest() {
		Request orderRequest =  new Request();
		orderRequest.setState(RequestState.WAITING.getState());
		orderRequest.setTargetAmount(new BigDecimal(12));
		orderRequest.setPosId(00);
		orderRequest.setStan("");
		orderRequest.setOrderTimeout(payNowOrderTimeout);
		return orderRequest;
	}
	
	private PosInfoOnly createPosInfo() {
		PosInfoOnly pos = new PosInfoOnly() {
			
			@Override
			public String getRid() {
				String Rid = "";
				return Rid;
			}
			
			@Override
			public String getPosId() {
				String posId = "";
				return posId;
			}
			
			@Override
			public String getName() {
				String name = "";
				return name;
			}
		};
		return pos;
	}
	
	private PhysicalPosEntity createPhysicalPosEntity() {
		PhysicalPosEntity pos = new PhysicalPosEntity();
		pos.setName("");
		pos.setRid("");
		MerchantEntity merchant = new MerchantEntity();
		merchant.setStatus("2");
		merchant.setMerchantId(00);
		pos.setMerchant(merchant);
		pos.setStatus("2");
		return pos;
	}
	
	private Map<ContextData,Object> createEntityMap() {
		
		Map <ContextData, Object> entityMap = new HashMap<ContextData, Object>();
		AcquirerEntity acquirer = new AcquirerEntity();
		acquirer.setAcquirerId(00);
		acquirer.setStatus("2");
		CardTypeEntity cardType = new CardTypeEntity();
		cardType.setCardTypeId(00);
		SOFTypeEntity sofType = new SOFTypeEntity();
		sofType.setCardTypeId(00);
		entityMap.put(ContextData.ACQUIRER, acquirer);
		entityMap.put(ContextData.ACQ_CARD_TYPE, cardType);
		entityMap.put(ContextData.SOF_TYPE, sofType);
 		return entityMap;
	}
	
	private AcqMerAccountEntity createAcqMerAccount() {
		AcqMerAccountEntity ama = new AcqMerAccountEntity();
		ama.setAcqMerAccountId(00);
		ama.setTid("");
		AcquirerMerchantMappingEntity amaMapping = new AcquirerMerchantMappingEntity();
		amaMapping.setMerMappingId(00);
		amaMapping.setStatus("2");
		ama.setAcquirerMerchantMapping(amaMapping);
		ama.setEcMid("");
		return ama;
	}
	
	private BankEntity createBank() {
		BankEntity bank = new BankEntity();
		bank.setBankId(00);
		BankCfgAttrEntity bankConfig = new BankCfgAttrEntity();
		bankConfig.setCfgAttrName(BankConfigAttributeName.PAYMENT_TYPE.toString());
		bankConfig.setCfgAttrValue("");
		Set<BankCfgAttrEntity> bankConfigs = new HashSet<BankCfgAttrEntity>();
		bankConfigs.add(bankConfig);
		bank.setBankCfgAttr(bankConfigs);
		return bank;
	}
	
	@Test
	public void process_CreditNotificationPreviouslyReceived() throws BaseBusinessException {
		
		CreditNotificationRequest payment = createCreditNotificationRequest();
		when(mockIdRegistryService.saveIDRegistry(any())).thenThrow(RuntimeException.class);
		
		BaseBusinessException e = assertThrows(BaseBusinessException.class, () -> unit.process(payment));
		
		verify(mockIdRegistryService, times(1)).saveIDRegistry(any());
		assertEquals("85", e.getCode());
		assertEquals("[85]Credit Notification Was Previously Received.", e.getMessage());
	}
	
	@Test
	public void process_OriginalOrderCannotFind() throws BaseBusinessException {
		
		CreditNotificationRequest payment = createCreditNotificationRequest();
		when(mockIdRegistryService.saveIDRegistry(any())).thenReturn(any());
		when(mockRequestService.findBySearchId(payment.getTransactionDomainData().getMerchantReferenceNumber().substring(0,16))).thenReturn(null);
		
		BaseBusinessException e = assertThrows(BaseBusinessException.class, () -> unit.process(payment));
		
		verify(mockIdRegistryService, times(1)).saveIDRegistry(any());
		verify(mockRequestService, times(1)).findBySearchId(payment.getTransactionDomainData().getMerchantReferenceNumber().substring(0,16));
		assertEquals("80", e.getCode());
		assertEquals("[80]Original Order can't find.", e.getMessage());
	}
	
	@Test
	public void process_InvalidPaymentCreditAmount() throws BaseBusinessException {
		
		CreditNotificationRequest payment = createCreditNotificationRequest();
		PaymentTransactionDomainData transactionDomainData = payment.getTransactionDomainData();
		transactionDomainData.setCreditAmount("0");
		payment.setTransactionDomainData(transactionDomainData);
		when(mockIdRegistryService.saveIDRegistry(any())).thenReturn(any());
		Request orderRequest = createOrderRequest();
		when(mockRequestService.findBySearchId(payment.getTransactionDomainData().getMerchantReferenceNumber().substring(0,16))).thenReturn(orderRequest);
		
		BaseBusinessException e = assertThrows(BaseBusinessException.class, () -> unit.process(payment));
		
		verify(mockIdRegistryService, times(1)).saveIDRegistry(any());
		verify(mockRequestService, times(1)).findBySearchId(payment.getTransactionDomainData().getMerchantReferenceNumber().substring(0,16));
		assertEquals("13", e.getCode());
		assertEquals("[13]Invalid Amount", e.getMessage());
	}
	
	@Test
	public void process_PaymentCreditAmountAndOriginalOrderAmountMismatch() throws BaseBusinessException {
		
		CreditNotificationRequest payment = createCreditNotificationRequest();
		PaymentTransactionDomainData transactionDomainData = payment.getTransactionDomainData();
		transactionDomainData.setCreditAmount("000000000");
		payment.setTransactionDomainData(transactionDomainData);
		when(mockIdRegistryService.saveIDRegistry(any())).thenReturn(any());
		Request orderRequest = createOrderRequest();
		orderRequest.setTargetAmount(new BigDecimal(13));
		when(mockRequestService.findBySearchId(payment.getTransactionDomainData().getMerchantReferenceNumber().substring(0,16))).thenReturn(orderRequest);
		
		BaseBusinessException e = assertThrows(BaseBusinessException.class, () -> unit.process(payment));
		
		verify(mockIdRegistryService, times(1)).saveIDRegistry(any());
		verify(mockRequestService, times(1)).findBySearchId(payment.getTransactionDomainData().getMerchantReferenceNumber().substring(0,16));
		assertEquals("13", e.getCode());
		assertEquals("[13]Invalid Amount", e.getMessage());
	}
	
	@Test
	public void process_MerchantNotFound() throws BaseBusinessException {
		
		CreditNotificationRequest payment = createCreditNotificationRequest();
		PaymentTransactionDomainData transactionDomainData = payment.getTransactionDomainData();
		payment.setTransactionDomainData(transactionDomainData);
		when(mockIdRegistryService.saveIDRegistry(any())).thenReturn(any());
		Request orderRequest = createOrderRequest();
		when(mockRequestService.findBySearchId(payment.getTransactionDomainData().getMerchantReferenceNumber().substring(0,16))).thenReturn(orderRequest);
		when(mockPhysicalPosService.getPosInfoOnlyByPosId(orderRequest.getPosId())).thenReturn(null);
		
		BaseBusinessException e = assertThrows(BaseBusinessException.class, () -> unit.process(payment));
		
		verify(mockIdRegistryService, times(1)).saveIDRegistry(any());
		verify(mockRequestService, times(1)).findBySearchId(payment.getTransactionDomainData().getMerchantReferenceNumber().substring(0,16));
		verify(mockPhysicalPosService, times(1)).getPosInfoOnlyByPosId(orderRequest.getPosId());
		assertEquals("81", e.getCode());
		assertEquals("[81]Merchant Not Found.", e.getMessage());
	}
	
	@Test
	public void process_AcquirerNotFound() throws BaseBusinessException {
		
		CreditNotificationRequest payment = createCreditNotificationRequest();
		PaymentTransactionDomainData transactionDomainData = payment.getTransactionDomainData();
		payment.setTransactionDomainData(transactionDomainData);
		when(mockIdRegistryService.saveIDRegistry(any())).thenReturn(any());
		Request orderRequest = createOrderRequest();
		when(mockRequestService.findBySearchId(payment.getTransactionDomainData().getMerchantReferenceNumber().substring(0,16))).thenReturn(orderRequest);
		PosInfoOnly pos = createPosInfo();
		when(mockPhysicalPosService.getPosInfoOnlyByPosId(orderRequest.getPosId())).thenReturn(pos);
		when(mockAcquirerService.getAcquirerSofDetails(payment.getSofUri())).thenReturn(null);
		
		BaseBusinessException e = assertThrows(BaseBusinessException.class, () -> unit.process(payment));
		
		verify(mockIdRegistryService, times(1)).saveIDRegistry(any());
		verify(mockRequestService, times(1)).findBySearchId(payment.getTransactionDomainData().getMerchantReferenceNumber().substring(0,16));
		verify(mockPhysicalPosService, times(1)).getPosInfoOnlyByPosId(orderRequest.getPosId());
		verify(mockAcquirerService, times(1)).getAcquirerSofDetails(payment.getSofUri());
		assertEquals("15", e.getCode());
		assertEquals("[15]Acquirer not found.", e.getMessage());
	}
	
	@Test
	public void process_AcquirerMappingNotFound() throws BaseBusinessException {
		
		CreditNotificationRequest payment = createCreditNotificationRequest();
		PaymentTransactionDomainData transactionDomainData = payment.getTransactionDomainData();
		payment.setTransactionDomainData(transactionDomainData);
		when(mockIdRegistryService.saveIDRegistry(any())).thenReturn(any());
		Request orderRequest = createOrderRequest();
		when(mockRequestService.findBySearchId(payment.getTransactionDomainData().getMerchantReferenceNumber().substring(0,16))).thenReturn(orderRequest);
		PosInfoOnly pos = createPosInfo();
		when(mockPhysicalPosService.getPosInfoOnlyByPosId(orderRequest.getPosId())).thenReturn((pos));
		Map<ContextData, Object> entityMap = createEntityMap();
		when(mockAcquirerService.getAcquirerSofDetails(payment.getSofUri())).thenReturn(entityMap);
		AcquirerEntity acquirer = (AcquirerEntity) entityMap.get(ContextData.ACQUIRER);
		entityMap.replace(ContextData.ACQUIRER, acquirer);
		List<AcqMerAccountEntity> amaList = new ArrayList<>();
		when(mockAcqMerAccountService.getAcqMerAccountsByPosIdAndAcquirerIdAndSourceCurrency(any(), eq(acquirer.getAcquirerId()) , eq(payment.getTransactionDomainData().getCreditCurrency().toUpperCase()))).thenReturn(amaList);
		
		BaseBusinessException e = assertThrows(BaseBusinessException.class, () -> unit.process(payment));
		
		verify(mockIdRegistryService, times(1)).saveIDRegistry(any());
		verify(mockRequestService, times(1)).findBySearchId(payment.getTransactionDomainData().getMerchantReferenceNumber().substring(0,16));
		verify(mockPhysicalPosService, times(1)).getPosInfoOnlyByPosId(orderRequest.getPosId());		
		verify(mockAcquirerService, times(1)).getAcquirerSofDetails(payment.getSofUri());
		assertEquals(0,amaList.size());
		verify(mockAcqMerAccountService, times(1)).getAcqMerAccountsByPosIdAndAcquirerIdAndSourceCurrency(any(), eq(acquirer.getAcquirerId()) , eq(payment.getTransactionDomainData().getCreditCurrency().toUpperCase()));
		assertEquals("15", e.getCode());
		assertEquals("[15]Acquirer merchant mapping not found.", e.getMessage());
	}
	
	@Test
	public void process_AcquirerDisabled() throws BaseBusinessException {
		
		CreditNotificationRequest payment = createCreditNotificationRequest();
		PaymentTransactionDomainData transactionDomainData = payment.getTransactionDomainData();
		payment.setTransactionDomainData(transactionDomainData);
		when(mockIdRegistryService.saveIDRegistry(any())).thenReturn(any());
		Request orderRequest = createOrderRequest();
		when(mockRequestService.findBySearchId(payment.getTransactionDomainData().getMerchantReferenceNumber().substring(0,16))).thenReturn(orderRequest);
		PosInfoOnly pos = createPosInfo();
		when(mockPhysicalPosService.getPosInfoOnlyByPosId(orderRequest.getPosId())).thenReturn(pos);	
		Map<ContextData, Object> entityMap = createEntityMap();
		when(mockAcquirerService.getAcquirerSofDetails(payment.getSofUri())).thenReturn(entityMap);
		AcquirerEntity acquirer = (AcquirerEntity) entityMap.get(ContextData.ACQUIRER);
		acquirer.setStatus("4");
		entityMap.replace(ContextData.ACQUIRER, acquirer);
		AcqMerAccountEntity ama = createAcqMerAccount();
		AcquirerMerchantMappingEntity amaMap = ama.getAcquirerMerchantMapping();
		amaMap.setStatus("2");
		ama.setAcquirerMerchantMapping(amaMap);
		List<AcqMerAccountEntity> amaList = new ArrayList<>();
		amaList.add(ama);
		when(mockAcqMerAccountService.getAcqMerAccountsByPosIdAndAcquirerIdAndSourceCurrency(any(), eq(acquirer.getAcquirerId()) , eq(payment.getTransactionDomainData().getCreditCurrency().toUpperCase()))).thenReturn(amaList);
		BankEntity bank = createBank();
		bank.setBankCfgAttr(new HashSet<BankCfgAttrEntity>());
		when(mockBankService.findByAcquirerAndValidate(acquirer.getAcquirerId())).thenReturn(bank);
		PhysicalPosEntity physicalPos = createPhysicalPosEntity();
		when(mockPhysicalPosService.findByNetsTid(any())).thenReturn(physicalPos);
		SOFTypeEntity sofType = new SOFTypeEntity();
		when(mockSofTypeService.getCardTypeById(any())).thenReturn(sofType);
		CodesEntity codes = new CodesEntity();
		codes.setIntegRefValue(00);
		when(mockCodesService.findByCodeValue("QR_CODE")).thenReturn(codes);
		TransactionEntity transaction = new TransactionEntity();
		transaction.setTransactionId(00);
		when(mockTransactionService.save(any())).thenReturn(transaction);
		PaynowTransactionData paynowTransactionData = new PaynowTransactionData();
		long id = 13123;
		paynowTransactionData.setId(id);
		when(mockPaynowTransactionDataService.save(any())).thenReturn(paynowTransactionData.getId());
		when(mockTransactionService.retrieve(anyInt())).thenReturn(transaction);
		when(mockTransactionService.save(any())).thenReturn(transaction);
		
		Timestamp settlementDate = new Timestamp(System.currentTimeMillis());
		when(mockBankAdapterProcessingFactory.getBankAdapter(any())).thenReturn(mockOcbcPaynowAdapter);
		when(mockOcbcPaynowAdapter.getSettlementDate()).thenReturn(settlementDate);
		when(mockOcbcPaynowAdapter.processRefundReversalDuetoException(any())).thenReturn(any());
		
		BaseBusinessException e = assertThrows(BaseBusinessException.class, () -> unit.process(payment));
		
		verify(mockIdRegistryService, times(1)).saveIDRegistry(any());
		verify(mockRequestService, times(1)).findBySearchId(payment.getTransactionDomainData().getMerchantReferenceNumber().substring(0,16));
		verify(mockPhysicalPosService, times(1)).getPosInfoOnlyByPosId(orderRequest.getPosId());
		verify(mockAcquirerService, times(1)).getAcquirerSofDetails(payment.getSofUri());
		assertEquals(1,amaList.size());
		verify(mockAcqMerAccountService, times(1)).getAcqMerAccountsByPosIdAndAcquirerIdAndSourceCurrency(any(), eq(acquirer.getAcquirerId()) , eq(payment.getTransactionDomainData().getCreditCurrency().toUpperCase()));
		verify(mockBankService, times(1)).findByAcquirerAndValidate(acquirer.getAcquirerId());
		verify(mockPhysicalPosService, times(1)).findByNetsTid(any());
		verify(mockCodesService, times(1)).findByCodeValue("QR_CODE");
		verify(mockTransactionService, times(2)).save(any());
		verify(mockPaynowTransactionDataService, times(1)).save(any());
		verify(mockTransactionService, times(1)).retrieve(anyInt());
		//verify(mockBankAdapterProcessingFactory, times(2)).getBankAdapter(any());
		verify(mockOcbcPaynowAdapter, times(1)).getSettlementDate();
		//verify(mockOcbcPaynowAdapter, times(1)).processRefundReversalDuetoException(any());
		assertEquals("58", e.getCode());
		assertEquals("[58]Acquirer disabled.", e.getMessage());
	}
	
	@Test
	public void process_AcquirerDisabledForMerchant() throws BaseBusinessException {
		
		CreditNotificationRequest payment = createCreditNotificationRequest();
		PaymentTransactionDomainData transactionDomainData = payment.getTransactionDomainData();
		payment.setTransactionDomainData(transactionDomainData);
		when(mockIdRegistryService.saveIDRegistry(any())).thenReturn(any());
		Request orderRequest = createOrderRequest();
		when(mockRequestService.findBySearchId(payment.getTransactionDomainData().getMerchantReferenceNumber().substring(0,16))).thenReturn(orderRequest);
		PosInfoOnly pos = createPosInfo();
		when(mockPhysicalPosService.getPosInfoOnlyByPosId(orderRequest.getPosId())).thenReturn(pos);	
		Map<ContextData, Object> entityMap = createEntityMap();
		when(mockAcquirerService.getAcquirerSofDetails(payment.getSofUri())).thenReturn(entityMap);
		AcquirerEntity acquirer = (AcquirerEntity) entityMap.get(ContextData.ACQUIRER);
		acquirer.setStatus("2");
		entityMap.replace(ContextData.ACQUIRER, acquirer);
		AcqMerAccountEntity ama = createAcqMerAccount();
		AcquirerMerchantMappingEntity amaMap = ama.getAcquirerMerchantMapping();
		amaMap.setStatus("4");
		ama.setAcquirerMerchantMapping(amaMap);
		List<AcqMerAccountEntity> amaList = new ArrayList<>();
		amaList.add(ama);
		when(mockAcqMerAccountService.getAcqMerAccountsByPosIdAndAcquirerIdAndSourceCurrency(any(), eq(acquirer.getAcquirerId()) , eq(payment.getTransactionDomainData().getCreditCurrency().toUpperCase()))).thenReturn(amaList);
		BankEntity bank = createBank();
		bank.setBankCfgAttr(new HashSet<BankCfgAttrEntity>());
		when(mockBankService.findByAcquirerAndValidate(acquirer.getAcquirerId())).thenReturn(bank);
		PhysicalPosEntity physicalPos = createPhysicalPosEntity();
		when(mockPhysicalPosService.findByNetsTid(any())).thenReturn(physicalPos);
		SOFTypeEntity sofType = new SOFTypeEntity();
		when(mockSofTypeService.getCardTypeById(any())).thenReturn(sofType);
		CodesEntity codes = new CodesEntity();
		codes.setIntegRefValue(00);
		when(mockCodesService.findByCodeValue("QR_CODE")).thenReturn(codes);
		TransactionEntity transaction = new TransactionEntity();
		transaction.setTransactionId(00);
		when(mockTransactionService.save(any())).thenReturn(transaction);
		PaynowTransactionData paynowTransactionData = new PaynowTransactionData();
		long id = 13123;
		paynowTransactionData.setId(id);
		when(mockPaynowTransactionDataService.save(any())).thenReturn(paynowTransactionData.getId());
		when(mockTransactionService.retrieve(anyInt())).thenReturn(transaction);
		when(mockTransactionService.save(any())).thenReturn(transaction);
		
		Timestamp settlementDate = new Timestamp(System.currentTimeMillis());
		when(mockBankAdapterProcessingFactory.getBankAdapter(any())).thenReturn(mockOcbcPaynowAdapter);
		when(mockOcbcPaynowAdapter.getSettlementDate()).thenReturn(settlementDate);
		when(mockOcbcPaynowAdapter.processRefundReversalDuetoException(any())).thenReturn(any());
		
		BaseBusinessException e = assertThrows(BaseBusinessException.class, () -> unit.process(payment));
		
		verify(mockIdRegistryService, times(1)).saveIDRegistry(any());
		verify(mockRequestService, times(1)).findBySearchId(payment.getTransactionDomainData().getMerchantReferenceNumber().substring(0,16));
		verify(mockPhysicalPosService, times(1)).getPosInfoOnlyByPosId(orderRequest.getPosId());
		verify(mockAcquirerService, times(1)).getAcquirerSofDetails(payment.getSofUri());
		assertEquals(1,amaList.size());
		verify(mockAcqMerAccountService, times(1)).getAcqMerAccountsByPosIdAndAcquirerIdAndSourceCurrency(any(), eq(acquirer.getAcquirerId()) , eq(payment.getTransactionDomainData().getCreditCurrency().toUpperCase()));
		verify(mockBankService, times(1)).findByAcquirerAndValidate(acquirer.getAcquirerId());
		verify(mockPhysicalPosService, times(1)).findByNetsTid(any());
		verify(mockCodesService, times(1)).findByCodeValue("QR_CODE");
		verify(mockTransactionService, times(2)).save(any());
		verify(mockPaynowTransactionDataService, times(1)).save(any());
		verify(mockTransactionService, times(1)).retrieve(anyInt());
		//verify(mockBankAdapterProcessingFactory, times(2)).getBankAdapter(any());
		verify(mockOcbcPaynowAdapter, times(1)).getSettlementDate();
		//verify(mockOcbcPaynowAdapter, times(1)).processRefundReversalDuetoException(any());
		assertEquals("58", e.getCode());
		assertEquals("[58]SOF is disabled for the merchant.", e.getMessage());
	}
	
	@Test
	public void process_PosIsNotActive() throws BaseBusinessException {
		
		CreditNotificationRequest payment = createCreditNotificationRequest();
		PaymentTransactionDomainData transactionDomainData = payment.getTransactionDomainData();
		payment.setTransactionDomainData(transactionDomainData);
		when(mockIdRegistryService.saveIDRegistry(any())).thenReturn(any());
		Request orderRequest = createOrderRequest();
		when(mockRequestService.findBySearchId(payment.getTransactionDomainData().getMerchantReferenceNumber().substring(0,16))).thenReturn(orderRequest);
		PosInfoOnly pos = createPosInfo();
		when(mockPhysicalPosService.getPosInfoOnlyByPosId(orderRequest.getPosId())).thenReturn(pos);	
		Map<ContextData, Object> entityMap = createEntityMap();
		when(mockAcquirerService.getAcquirerSofDetails(payment.getSofUri())).thenReturn(entityMap);
		AcquirerEntity acquirer = (AcquirerEntity) entityMap.get(ContextData.ACQUIRER);
		acquirer.setStatus("2");
		entityMap.replace(ContextData.ACQUIRER, acquirer);
		AcqMerAccountEntity ama = createAcqMerAccount();
		AcquirerMerchantMappingEntity amaMap = ama.getAcquirerMerchantMapping();
		amaMap.setStatus("2");
		ama.setAcquirerMerchantMapping(amaMap);
		List<AcqMerAccountEntity> amaList = new ArrayList<>();
		amaList.add(ama);
		when(mockAcqMerAccountService.getAcqMerAccountsByPosIdAndAcquirerIdAndSourceCurrency(any(), eq(acquirer.getAcquirerId()) , eq(payment.getTransactionDomainData().getCreditCurrency().toUpperCase()))).thenReturn(amaList);
		BankEntity bank = createBank();
		bank.setBankCfgAttr(new HashSet<BankCfgAttrEntity>());
		when(mockBankService.findByAcquirerAndValidate(acquirer.getAcquirerId())).thenReturn(bank);
		PhysicalPosEntity physicalPos = createPhysicalPosEntity();
		physicalPos.setStatus("4");
		when(mockPhysicalPosService.findByNetsTid(any())).thenReturn(physicalPos);
		SOFTypeEntity sofType = new SOFTypeEntity();
		when(mockSofTypeService.getCardTypeById(any())).thenReturn(sofType);
		CodesEntity codes = new CodesEntity();
		codes.setIntegRefValue(00);
		when(mockCodesService.findByCodeValue("QR_CODE")).thenReturn(codes);
		TransactionEntity transaction = new TransactionEntity();
		transaction.setTransactionId(00);
		when(mockTransactionService.save(any())).thenReturn(transaction);
		PaynowTransactionData paynowTransactionData = new PaynowTransactionData();
		long id = 13123;
		paynowTransactionData.setId(id);
		when(mockPaynowTransactionDataService.save(any())).thenReturn(paynowTransactionData.getId());
		when(mockTransactionService.retrieve(anyInt())).thenReturn(transaction);
		when(mockTransactionService.save(any())).thenReturn(transaction);
		
		Timestamp settlementDate = new Timestamp(System.currentTimeMillis());
		when(mockBankAdapterProcessingFactory.getBankAdapter(any())).thenReturn(mockOcbcPaynowAdapter);
		when(mockOcbcPaynowAdapter.getSettlementDate()).thenReturn(settlementDate);
		when(mockOcbcPaynowAdapter.processRefundReversalDuetoException(any())).thenReturn(any());
		
		BaseBusinessException e = assertThrows(BaseBusinessException.class, () -> unit.process(payment));
		
		verify(mockIdRegistryService, times(1)).saveIDRegistry(any());
		verify(mockRequestService, times(1)).findBySearchId(payment.getTransactionDomainData().getMerchantReferenceNumber().substring(0,16));
		verify(mockPhysicalPosService, times(1)).getPosInfoOnlyByPosId(orderRequest.getPosId());
		verify(mockAcquirerService, times(1)).getAcquirerSofDetails(payment.getSofUri());
		assertEquals(1,amaList.size());
		verify(mockAcqMerAccountService, times(1)).getAcqMerAccountsByPosIdAndAcquirerIdAndSourceCurrency(any(), eq(acquirer.getAcquirerId()) , eq(payment.getTransactionDomainData().getCreditCurrency().toUpperCase()));
		verify(mockBankService, times(1)).findByAcquirerAndValidate(acquirer.getAcquirerId());
		verify(mockPhysicalPosService, times(1)).findByNetsTid(any());
		verify(mockCodesService, times(1)).findByCodeValue("QR_CODE");
		verify(mockTransactionService, times(2)).save(any());
		verify(mockPaynowTransactionDataService, times(1)).save(any());
		verify(mockTransactionService, times(1)).retrieve(anyInt());
		//verify(mockBankAdapterProcessingFactory, times(2)).getBankAdapter(any());
		verify(mockOcbcPaynowAdapter, times(1)).getSettlementDate();
		//verify(mockOcbcPaynowAdapter, times(1)).processRefundReversalDuetoException(any());
		assertEquals("58", e.getCode());
		assertEquals("[58]POS is not active.", e.getMessage());
	}
	
	@Test
	public void process_MerchantIsNotActive() throws BaseBusinessException {
		
		CreditNotificationRequest payment = createCreditNotificationRequest();
		PaymentTransactionDomainData transactionDomainData = payment.getTransactionDomainData();
		payment.setTransactionDomainData(transactionDomainData);
		when(mockIdRegistryService.saveIDRegistry(any())).thenReturn(any());
		Request orderRequest = createOrderRequest();
		when(mockRequestService.findBySearchId(payment.getTransactionDomainData().getMerchantReferenceNumber().substring(0,16))).thenReturn(orderRequest);
		PosInfoOnly pos = createPosInfo();
		when(mockPhysicalPosService.getPosInfoOnlyByPosId(orderRequest.getPosId())).thenReturn(pos);	
		Map<ContextData, Object> entityMap = createEntityMap();
		when(mockAcquirerService.getAcquirerSofDetails(payment.getSofUri())).thenReturn(entityMap);
		AcquirerEntity acquirer = (AcquirerEntity) entityMap.get(ContextData.ACQUIRER);
		acquirer.setStatus("2");
		entityMap.replace(ContextData.ACQUIRER, acquirer);
		AcqMerAccountEntity ama = createAcqMerAccount();
		AcquirerMerchantMappingEntity amaMap = ama.getAcquirerMerchantMapping();
		amaMap.setStatus("2");
		ama.setAcquirerMerchantMapping(amaMap);
		List<AcqMerAccountEntity> amaList = new ArrayList<>();
		amaList.add(ama);
		when(mockAcqMerAccountService.getAcqMerAccountsByPosIdAndAcquirerIdAndSourceCurrency(any(), eq(acquirer.getAcquirerId()) , eq(payment.getTransactionDomainData().getCreditCurrency().toUpperCase()))).thenReturn(amaList);
		BankEntity bank = createBank();
		bank.setBankCfgAttr(new HashSet<BankCfgAttrEntity>());
		when(mockBankService.findByAcquirerAndValidate(acquirer.getAcquirerId())).thenReturn(bank);
		PhysicalPosEntity physicalPos = createPhysicalPosEntity();
		MerchantEntity merchant = physicalPos.getMerchant();
		merchant.setStatus("4");
		physicalPos.setMerchant(merchant);
		when(mockPhysicalPosService.findByNetsTid(any())).thenReturn(physicalPos);
		SOFTypeEntity sofType = new SOFTypeEntity();
		when(mockSofTypeService.getCardTypeById(any())).thenReturn(sofType);
		CodesEntity codes = new CodesEntity();
		codes.setIntegRefValue(00);
		when(mockCodesService.findByCodeValue("QR_CODE")).thenReturn(codes);
		TransactionEntity transaction = new TransactionEntity();
		transaction.setTransactionId(00);
		when(mockTransactionService.save(any())).thenReturn(transaction);
		PaynowTransactionData paynowTransactionData = new PaynowTransactionData();
		long id = 13123;
		paynowTransactionData.setId(id);
		when(mockPaynowTransactionDataService.save(any())).thenReturn(paynowTransactionData.getId());
		when(mockTransactionService.retrieve(anyInt())).thenReturn(transaction);
		when(mockTransactionService.save(any())).thenReturn(transaction);
		
		Timestamp settlementDate = new Timestamp(System.currentTimeMillis());
		when(mockBankAdapterProcessingFactory.getBankAdapter(any())).thenReturn(mockOcbcPaynowAdapter);
		when(mockOcbcPaynowAdapter.getSettlementDate()).thenReturn(settlementDate);
		when(mockOcbcPaynowAdapter.processRefundReversalDuetoException(any())).thenReturn(any());
		
		BaseBusinessException e = assertThrows(BaseBusinessException.class, () -> unit.process(payment));
		
		verify(mockIdRegistryService, times(1)).saveIDRegistry(any());
		verify(mockRequestService, times(1)).findBySearchId(payment.getTransactionDomainData().getMerchantReferenceNumber().substring(0,16));
		verify(mockPhysicalPosService, times(1)).getPosInfoOnlyByPosId(orderRequest.getPosId());
		verify(mockAcquirerService, times(1)).getAcquirerSofDetails(payment.getSofUri());
		assertEquals(1,amaList.size());
		verify(mockAcqMerAccountService, times(1)).getAcqMerAccountsByPosIdAndAcquirerIdAndSourceCurrency(any(), eq(acquirer.getAcquirerId()) , eq(payment.getTransactionDomainData().getCreditCurrency().toUpperCase()));
		verify(mockBankService, times(1)).findByAcquirerAndValidate(acquirer.getAcquirerId());
		verify(mockPhysicalPosService, times(1)).findByNetsTid(any());
		verify(mockCodesService, times(1)).findByCodeValue("QR_CODE");
		verify(mockTransactionService, times(2)).save(any());
		verify(mockPaynowTransactionDataService, times(1)).save(any());
		verify(mockTransactionService, times(1)).retrieve(anyInt());
		//verify(mockBankAdapterProcessingFactory, times(2)).getBankAdapter(any());
		verify(mockOcbcPaynowAdapter, times(1)).getSettlementDate();
		//verify(mockOcbcPaynowAdapter, times(1)).processRefundReversalDuetoException(any());
		assertEquals("58", e.getCode());
		assertEquals("[58]Merchant is not active.", e.getMessage());
	}
	
	@Test
	public void process_RequestStateTimedOut() throws BaseBusinessException {
		
		CreditNotificationRequest payment = createCreditNotificationRequest();
		PaymentTransactionDomainData transactionDomainData = payment.getTransactionDomainData();
		payment.setTransactionDomainData(transactionDomainData);
		when(mockIdRegistryService.saveIDRegistry(any())).thenReturn(any());
		Request orderRequest = createOrderRequest();
		orderRequest.setState(RequestState.TIMED_OUT.getState());
		when(mockRequestService.findBySearchId(payment.getTransactionDomainData().getMerchantReferenceNumber().substring(0,16))).thenReturn(orderRequest);
		PosInfoOnly pos = createPosInfo();
		when(mockPhysicalPosService.getPosInfoOnlyByPosId(orderRequest.getPosId())).thenReturn(pos);	
		Map<ContextData, Object> entityMap = createEntityMap();
		when(mockAcquirerService.getAcquirerSofDetails(payment.getSofUri())).thenReturn(entityMap);
		AcquirerEntity acquirer = (AcquirerEntity) entityMap.get(ContextData.ACQUIRER);
		acquirer.setStatus("2");
		entityMap.replace(ContextData.ACQUIRER, acquirer);
		AcqMerAccountEntity ama = createAcqMerAccount();
		AcquirerMerchantMappingEntity amaMap = ama.getAcquirerMerchantMapping();
		amaMap.setStatus("2");
		ama.setAcquirerMerchantMapping(amaMap);
		List<AcqMerAccountEntity> amaList = new ArrayList<>();
		amaList.add(ama);
		when(mockAcqMerAccountService.getAcqMerAccountsByPosIdAndAcquirerIdAndSourceCurrency(any(), eq(acquirer.getAcquirerId()) , eq(payment.getTransactionDomainData().getCreditCurrency().toUpperCase()))).thenReturn(amaList);
		BankEntity bank = createBank();
		bank.setBankCfgAttr(new HashSet<BankCfgAttrEntity>());
		when(mockBankService.findByAcquirerAndValidate(acquirer.getAcquirerId())).thenReturn(bank);
		PhysicalPosEntity physicalPos = createPhysicalPosEntity();
		MerchantEntity merchant = physicalPos.getMerchant();
		merchant.setStatus("2");
		physicalPos.setMerchant(merchant);
		when(mockPhysicalPosService.findByNetsTid(any())).thenReturn(physicalPos);
		SOFTypeEntity sofType = new SOFTypeEntity();
		when(mockSofTypeService.getCardTypeById(any())).thenReturn(sofType);
		CodesEntity codes = new CodesEntity();
		codes.setIntegRefValue(00);
		when(mockCodesService.findByCodeValue("QR_CODE")).thenReturn(codes);
		TransactionEntity transaction = new TransactionEntity();
		transaction.setTransactionId(00);
		when(mockTransactionService.save(any())).thenReturn(transaction);
		PaynowTransactionData paynowTransactionData = new PaynowTransactionData();
		long id = 13123;
		paynowTransactionData.setId(id);
		when(mockPaynowTransactionDataService.save(any())).thenReturn(paynowTransactionData.getId());
		when(mockTransactionService.retrieve(anyInt())).thenReturn(transaction);
		when(mockTransactionService.save(any())).thenReturn(transaction);
		
		Timestamp settlementDate = new Timestamp(System.currentTimeMillis());
		when(mockBankAdapterProcessingFactory.getBankAdapter(any())).thenReturn(mockOcbcPaynowAdapter);
		when(mockOcbcPaynowAdapter.getSettlementDate()).thenReturn(settlementDate);
		when(mockOcbcPaynowAdapter.processRefundReversalDuetoException(any())).thenReturn(any());
		
		BaseBusinessException e = assertThrows(BaseBusinessException.class, () -> unit.process(payment));
		
		verify(mockIdRegistryService, times(1)).saveIDRegistry(any());
		verify(mockRequestService, times(1)).findBySearchId(payment.getTransactionDomainData().getMerchantReferenceNumber().substring(0,16));
		verify(mockPhysicalPosService, times(1)).getPosInfoOnlyByPosId(orderRequest.getPosId());
		verify(mockAcquirerService, times(1)).getAcquirerSofDetails(payment.getSofUri());
		assertEquals(1,amaList.size());
		verify(mockAcqMerAccountService, times(1)).getAcqMerAccountsByPosIdAndAcquirerIdAndSourceCurrency(any(), eq(acquirer.getAcquirerId()) , eq(payment.getTransactionDomainData().getCreditCurrency().toUpperCase()));
		verify(mockBankService, times(1)).findByAcquirerAndValidate(acquirer.getAcquirerId());
		verify(mockPhysicalPosService, times(1)).findByNetsTid(any());
		verify(mockCodesService, times(1)).findByCodeValue("QR_CODE");
		verify(mockTransactionService, times(2)).save(any());
		verify(mockPaynowTransactionDataService, times(1)).save(any());
		verify(mockTransactionService, times(1)).retrieve(anyInt());
		//verify(mockBankAdapterProcessingFactory, times(2)).getBankAdapter(any());
		verify(mockOcbcPaynowAdapter, times(1)).getSettlementDate();
		//verify(mockOcbcPaynowAdapter, times(1)).processRefundReversalDuetoException(any());
		assertEquals("68", e.getCode());
		assertEquals("[68]Order Timeout.", e.getMessage());
	}
	
	@Test
	public void process_RequestOrderTimedOut() throws BaseBusinessException {
		CreditNotificationRequest payment = createCreditNotificationRequest();
		PaymentTransactionDomainData transactionDomainData = payment.getTransactionDomainData();
		payment.setTransactionDomainData(transactionDomainData);
		when(mockIdRegistryService.saveIDRegistry(any())).thenReturn(any());
		Request orderRequest = createOrderRequest();
		orderRequest.setCreationDate(Timestamp.valueOf("2019-03-21 04:44:01"));
		when(mockRequestService.findBySearchId(payment.getTransactionDomainData().getMerchantReferenceNumber().substring(0,16))).thenReturn(orderRequest);
		PosInfoOnly pos = createPosInfo();
		when(mockPhysicalPosService.getPosInfoOnlyByPosId(orderRequest.getPosId())).thenReturn(pos);	
		Map<ContextData, Object> entityMap = createEntityMap();
		when(mockAcquirerService.getAcquirerSofDetails(payment.getSofUri())).thenReturn(entityMap);
		AcquirerEntity acquirer = (AcquirerEntity) entityMap.get(ContextData.ACQUIRER);
		acquirer.setStatus("2");
		entityMap.replace(ContextData.ACQUIRER, acquirer);
		AcqMerAccountEntity ama = createAcqMerAccount();
		AcquirerMerchantMappingEntity amaMap = ama.getAcquirerMerchantMapping();
		amaMap.setStatus("2");
		ama.setAcquirerMerchantMapping(amaMap);
		List<AcqMerAccountEntity> amaList = new ArrayList<>();
		amaList.add(ama);
		when(mockAcqMerAccountService.getAcqMerAccountsByPosIdAndAcquirerIdAndSourceCurrency(any(), eq(acquirer.getAcquirerId()) , eq(payment.getTransactionDomainData().getCreditCurrency().toUpperCase()))).thenReturn(amaList);
		BankEntity bank = createBank();
		bank.setBankCfgAttr(new HashSet<BankCfgAttrEntity>());
		when(mockBankService.findByAcquirerAndValidate(acquirer.getAcquirerId())).thenReturn(bank);
		PhysicalPosEntity physicalPos = createPhysicalPosEntity();
		MerchantEntity merchant = physicalPos.getMerchant();
		merchant.setStatus("2");
		physicalPos.setMerchant(merchant);
		when(mockPhysicalPosService.findByNetsTid(any())).thenReturn(physicalPos);
		SOFTypeEntity sofType = new SOFTypeEntity();
		when(mockSofTypeService.getCardTypeById(any())).thenReturn(sofType);
		CodesEntity codes = new CodesEntity();
		codes.setIntegRefValue(00);
		when(mockCodesService.findByCodeValue("QR_CODE")).thenReturn(codes);
		TransactionEntity transaction = new TransactionEntity();
		transaction.setTransactionId(00);
		when(mockTransactionService.save(any())).thenReturn(transaction);
		PaynowTransactionData paynowTransactionData = new PaynowTransactionData();
		long id = 13123;
		paynowTransactionData.setId(id);
		when(mockPaynowTransactionDataService.save(any())).thenReturn(paynowTransactionData.getId());
		when(mockTransactionService.retrieve(anyInt())).thenReturn(transaction);
		when(mockTransactionService.save(any())).thenReturn(transaction);
		
		Timestamp settlementDate = new Timestamp(System.currentTimeMillis());
		when(mockBankAdapterProcessingFactory.getBankAdapter(any())).thenReturn(mockOcbcPaynowAdapter);
		when(mockOcbcPaynowAdapter.getSettlementDate()).thenReturn(settlementDate);
		when(mockOcbcPaynowAdapter.processRefundReversalDuetoException(any())).thenReturn(any());
		
		BaseBusinessException e = assertThrows(BaseBusinessException.class, () -> unit.process(payment));
		
		verify(mockIdRegistryService, times(1)).saveIDRegistry(any());
		verify(mockRequestService, times(1)).findBySearchId(payment.getTransactionDomainData().getMerchantReferenceNumber().substring(0,16));
		verify(mockPhysicalPosService, times(1)).getPosInfoOnlyByPosId(orderRequest.getPosId());
		verify(mockAcquirerService, times(1)).getAcquirerSofDetails(payment.getSofUri());
		assertEquals(1,amaList.size());
		verify(mockAcqMerAccountService, times(1)).getAcqMerAccountsByPosIdAndAcquirerIdAndSourceCurrency(any(), eq(acquirer.getAcquirerId()) , eq(payment.getTransactionDomainData().getCreditCurrency().toUpperCase()));
		verify(mockBankService, times(1)).findByAcquirerAndValidate(acquirer.getAcquirerId());
		verify(mockPhysicalPosService, times(1)).findByNetsTid(any());
		verify(mockCodesService, times(1)).findByCodeValue("QR_CODE");
		verify(mockTransactionService, times(2)).save(any());
		verify(mockPaynowTransactionDataService, times(1)).save(any());
		verify(mockTransactionService, times(1)).retrieve(anyInt());
		//verify(mockBankAdapterProcessingFactory, times(2)).getBankAdapter(any());
		verify(mockOcbcPaynowAdapter, times(1)).getSettlementDate();
		//verify(mockOcbcPaynowAdapter, times(1)).processRefundReversalDuetoException(any());
		assertEquals("68", e.getCode());
		assertEquals("[68]Order Timeout.", e.getMessage());
	}
	
	@Test
	public void process_RequestStateReversed() throws BaseBusinessException {
		
		CreditNotificationRequest payment = createCreditNotificationRequest();
		PaymentTransactionDomainData transactionDomainData = payment.getTransactionDomainData();
		payment.setTransactionDomainData(transactionDomainData);
		when(mockIdRegistryService.saveIDRegistry(any())).thenReturn(any());
		Request orderRequest = createOrderRequest();
		orderRequest.setState(RequestState.REVERSED.getState());
		when(mockRequestService.findBySearchId(payment.getTransactionDomainData().getMerchantReferenceNumber().substring(0,16))).thenReturn(orderRequest);
		PosInfoOnly pos = createPosInfo();
		when(mockPhysicalPosService.getPosInfoOnlyByPosId(orderRequest.getPosId())).thenReturn(pos);	
		Map<ContextData, Object> entityMap = createEntityMap();
		when(mockAcquirerService.getAcquirerSofDetails(payment.getSofUri())).thenReturn(entityMap);
		AcquirerEntity acquirer = (AcquirerEntity) entityMap.get(ContextData.ACQUIRER);
		acquirer.setStatus("2");
		entityMap.replace(ContextData.ACQUIRER, acquirer);
		AcqMerAccountEntity ama = createAcqMerAccount();
		AcquirerMerchantMappingEntity amaMap = ama.getAcquirerMerchantMapping();
		amaMap.setStatus("2");
		ama.setAcquirerMerchantMapping(amaMap);
		List<AcqMerAccountEntity> amaList = new ArrayList<>();
		amaList.add(ama);
		when(mockAcqMerAccountService.getAcqMerAccountsByPosIdAndAcquirerIdAndSourceCurrency(any(), eq(acquirer.getAcquirerId()) , eq(payment.getTransactionDomainData().getCreditCurrency().toUpperCase()))).thenReturn(amaList);
		BankEntity bank = createBank();
		bank.setBankCfgAttr(new HashSet<BankCfgAttrEntity>());
		when(mockBankService.findByAcquirerAndValidate(acquirer.getAcquirerId())).thenReturn(bank);
		PhysicalPosEntity physicalPos = createPhysicalPosEntity();
		MerchantEntity merchant = physicalPos.getMerchant();
		merchant.setStatus("2");
		physicalPos.setMerchant(merchant);
		when(mockPhysicalPosService.findByNetsTid(any())).thenReturn(physicalPos);
		SOFTypeEntity sofType = new SOFTypeEntity();
		when(mockSofTypeService.getCardTypeById(any())).thenReturn(sofType);
		CodesEntity codes = new CodesEntity();
		codes.setIntegRefValue(00);
		when(mockCodesService.findByCodeValue("QR_CODE")).thenReturn(codes);
		TransactionEntity transaction = new TransactionEntity();
		transaction.setTransactionId(00);
		when(mockTransactionService.save(any())).thenReturn(transaction);
		PaynowTransactionData paynowTransactionData = new PaynowTransactionData();
		long id = 13123;
		paynowTransactionData.setId(id);
		when(mockPaynowTransactionDataService.save(any())).thenReturn(paynowTransactionData.getId());
		when(mockTransactionService.retrieve(anyInt())).thenReturn(transaction);
		when(mockTransactionService.save(any())).thenReturn(transaction);
		
		Timestamp settlementDate = new Timestamp(System.currentTimeMillis());
		when(mockBankAdapterProcessingFactory.getBankAdapter(any())).thenReturn(mockOcbcPaynowAdapter);
		when(mockOcbcPaynowAdapter.getSettlementDate()).thenReturn(settlementDate);
		when(mockOcbcPaynowAdapter.processRefundReversalDuetoException(any())).thenReturn(any());
		
		BaseBusinessException e = assertThrows(BaseBusinessException.class, () -> unit.process(payment));
		
		verify(mockIdRegistryService, times(1)).saveIDRegistry(any());
		verify(mockRequestService, times(1)).findBySearchId(payment.getTransactionDomainData().getMerchantReferenceNumber().substring(0,16));
		verify(mockPhysicalPosService, times(1)).getPosInfoOnlyByPosId(orderRequest.getPosId());
		verify(mockAcquirerService, times(1)).getAcquirerSofDetails(payment.getSofUri());
		assertEquals(1,amaList.size());
		verify(mockAcqMerAccountService, times(1)).getAcqMerAccountsByPosIdAndAcquirerIdAndSourceCurrency(any(), eq(acquirer.getAcquirerId()) , eq(payment.getTransactionDomainData().getCreditCurrency().toUpperCase()));
		verify(mockBankService, times(1)).findByAcquirerAndValidate(acquirer.getAcquirerId());
		verify(mockPhysicalPosService, times(1)).findByNetsTid(any());
		verify(mockCodesService, times(1)).findByCodeValue("QR_CODE");
		verify(mockTransactionService, times(2)).save(any());
		verify(mockPaynowTransactionDataService, times(1)).save(any());
		verify(mockTransactionService, times(1)).retrieve(anyInt());
		//verify(mockBankAdapterProcessingFactory, times(2)).getBankAdapter(any());
		verify(mockOcbcPaynowAdapter, times(1)).getSettlementDate();
		////verify(mockOcbcPaynowAdapter, times(1)).processRefundReversalDuetoException(any());
		assertEquals("68", e.getCode());
		assertEquals("[68]Order Timeout.", e.getMessage());
	}
	
	
	@Test
	public void process_RequestStateCompleted() throws BaseBusinessException {
		
		CreditNotificationRequest payment = createCreditNotificationRequest();
		PaymentTransactionDomainData transactionDomainData = payment.getTransactionDomainData();
		payment.setTransactionDomainData(transactionDomainData);
		when(mockIdRegistryService.saveIDRegistry(any())).thenReturn(any());
		Request orderRequest = createOrderRequest();
		orderRequest.setState(RequestState.COMPLETED.getState());
		when(mockRequestService.findBySearchId(payment.getTransactionDomainData().getMerchantReferenceNumber().substring(0,16))).thenReturn(orderRequest);
		PosInfoOnly pos = createPosInfo();
		when(mockPhysicalPosService.getPosInfoOnlyByPosId(orderRequest.getPosId())).thenReturn(pos);	
		Map<ContextData, Object> entityMap = createEntityMap();
		when(mockAcquirerService.getAcquirerSofDetails(payment.getSofUri())).thenReturn(entityMap);
		AcquirerEntity acquirer = (AcquirerEntity) entityMap.get(ContextData.ACQUIRER);
		acquirer.setStatus("2");
		entityMap.replace(ContextData.ACQUIRER, acquirer);
		AcqMerAccountEntity ama = createAcqMerAccount();
		AcquirerMerchantMappingEntity amaMap = ama.getAcquirerMerchantMapping();
		amaMap.setStatus("2");
		ama.setAcquirerMerchantMapping(amaMap);
		List<AcqMerAccountEntity> amaList = new ArrayList<>();
		amaList.add(ama);
		when(mockAcqMerAccountService.getAcqMerAccountsByPosIdAndAcquirerIdAndSourceCurrency(any(), eq(acquirer.getAcquirerId()) , eq(payment.getTransactionDomainData().getCreditCurrency().toUpperCase()))).thenReturn(amaList);
		BankEntity bank = createBank();
		bank.setBankCfgAttr(new HashSet<BankCfgAttrEntity>());
		when(mockBankService.findByAcquirerAndValidate(acquirer.getAcquirerId())).thenReturn(bank);
		PhysicalPosEntity physicalPos = createPhysicalPosEntity();
		MerchantEntity merchant = physicalPos.getMerchant();
		merchant.setStatus("2");
		physicalPos.setMerchant(merchant);
		when(mockPhysicalPosService.findByNetsTid(any())).thenReturn(physicalPos);
		SOFTypeEntity sofType = new SOFTypeEntity();
		when(mockSofTypeService.getCardTypeById(any())).thenReturn(sofType);
		CodesEntity codes = new CodesEntity();
		codes.setIntegRefValue(00);
		when(mockCodesService.findByCodeValue("QR_CODE")).thenReturn(codes);
		TransactionEntity transaction = new TransactionEntity();
		transaction.setTransactionId(00);
		when(mockTransactionService.save(any())).thenReturn(transaction);
		PaynowTransactionData paynowTransactionData = new PaynowTransactionData();
		long id = 13123;
		paynowTransactionData.setId(id);
		when(mockPaynowTransactionDataService.save(any())).thenReturn(paynowTransactionData.getId());
		when(mockTransactionService.retrieve(anyInt())).thenReturn(transaction);
		when(mockTransactionService.save(any())).thenReturn(transaction);		
		Timestamp settlementDate = new Timestamp(System.currentTimeMillis());
		when(mockBankAdapterProcessingFactory.getBankAdapter(any())).thenReturn(mockOcbcPaynowAdapter);
		when(mockOcbcPaynowAdapter.getSettlementDate()).thenReturn(settlementDate);
		when(mockOcbcPaynowAdapter.processRefundReversalDuetoException(any())).thenReturn(any());
		
		BaseBusinessException e = assertThrows(BaseBusinessException.class, () -> unit.process(payment));
		
		verify(mockIdRegistryService, times(1)).saveIDRegistry(any());
		verify(mockRequestService, times(1)).findBySearchId(payment.getTransactionDomainData().getMerchantReferenceNumber().substring(0,16));
		verify(mockPhysicalPosService, times(1)).getPosInfoOnlyByPosId(orderRequest.getPosId());
		verify(mockAcquirerService, times(1)).getAcquirerSofDetails(payment.getSofUri());
		assertEquals(1,amaList.size());
		verify(mockAcqMerAccountService, times(1)).getAcqMerAccountsByPosIdAndAcquirerIdAndSourceCurrency(any(), eq(acquirer.getAcquirerId()) , eq(payment.getTransactionDomainData().getCreditCurrency().toUpperCase()));
		verify(mockBankService, times(1)).findByAcquirerAndValidate(acquirer.getAcquirerId());
		verify(mockPhysicalPosService, times(1)).findByNetsTid(any());
		verify(mockCodesService, times(1)).findByCodeValue("QR_CODE");
		verify(mockTransactionService, times(2)).save(any());
		verify(mockPaynowTransactionDataService, times(1)).save(any());
		verify(mockTransactionService, times(1)).retrieve(anyInt());
		//verify(mockBankAdapterProcessingFactory, times(2)).getBankAdapter(any());
		verify(mockOcbcPaynowAdapter, times(1)).getSettlementDate();
		////verify(mockOcbcPaynowAdapter, times(1)).processRefundReversalDuetoException(any());
		assertEquals("85", e.getCode());
		assertEquals("[85]Credit Notification Was Previously Received.", e.getMessage());
	}
	
	@Test
	public void process_ReverseTransaction_ExceptionNoMessage() throws BaseBusinessException {
		
		CreditNotificationRequest payment = createCreditNotificationRequest();
		PaymentTransactionDomainData transactionDomainData = payment.getTransactionDomainData();
		payment.setTransactionDomainData(transactionDomainData);
		when(mockIdRegistryService.saveIDRegistry(any())).thenReturn(any());
		Request orderRequest = createOrderRequest();
		orderRequest.setState(RequestState.COMPLETED.getState());
		when(mockRequestService.findBySearchId(payment.getTransactionDomainData().getMerchantReferenceNumber().substring(0,16))).thenReturn(orderRequest);
		PosInfoOnly pos = createPosInfo();
		when(mockPhysicalPosService.getPosInfoOnlyByPosId(orderRequest.getPosId())).thenReturn(pos);	
		Map<ContextData, Object> entityMap = createEntityMap();
		when(mockAcquirerService.getAcquirerSofDetails(payment.getSofUri())).thenReturn(entityMap);
		AcquirerEntity acquirer = (AcquirerEntity) entityMap.get(ContextData.ACQUIRER);
		acquirer.setStatus("2");
		entityMap.replace(ContextData.ACQUIRER, acquirer);
		AcqMerAccountEntity ama = createAcqMerAccount();
		AcquirerMerchantMappingEntity amaMap = ama.getAcquirerMerchantMapping();
		amaMap.setStatus("2");
		ama.setAcquirerMerchantMapping(amaMap);
		List<AcqMerAccountEntity> amaList = new ArrayList<>();
		amaList.add(ama);
		when(mockAcqMerAccountService.getAcqMerAccountsByPosIdAndAcquirerIdAndSourceCurrency(any(), eq(acquirer.getAcquirerId()) , eq(payment.getTransactionDomainData().getCreditCurrency().toUpperCase()))).thenReturn(amaList);
		BankEntity bank = createBank();
		bank.setBankCfgAttr(new HashSet<BankCfgAttrEntity>());
		when(mockBankService.findByAcquirerAndValidate(acquirer.getAcquirerId())).thenReturn(bank);
		PhysicalPosEntity physicalPos = createPhysicalPosEntity();
		MerchantEntity merchant = physicalPos.getMerchant();
		merchant.setStatus("2");
		physicalPos.setMerchant(merchant);
		when(mockPhysicalPosService.findByNetsTid(any())).thenReturn(physicalPos);
		SOFTypeEntity sofType = new SOFTypeEntity();
		when(mockSofTypeService.getCardTypeById(any())).thenReturn(sofType);
		CodesEntity codes = new CodesEntity();
		codes.setIntegRefValue(00);
		when(mockCodesService.findByCodeValue("QR_CODE")).thenReturn(codes);
		TransactionEntity transaction = new TransactionEntity();
		transaction.setTransactionId(00);
		when(mockTransactionService.save(any())).thenReturn(transaction);
		PaynowTransactionData paynowTransactionData = new PaynowTransactionData();
		long id = 13123;
		paynowTransactionData.setId(id);
		when(mockPaynowTransactionDataService.save(any())).thenReturn(paynowTransactionData.getId());
		when(mockTransactionService.retrieve(anyInt())).thenReturn(transaction);
		when(mockTransactionService.save(any())).thenReturn(transaction);
		
		Timestamp settlementDate = new Timestamp(System.currentTimeMillis());
		when(mockBankAdapterProcessingFactory.getBankAdapter(any())).thenReturn(mockOcbcPaynowAdapter);
		when(mockOcbcPaynowAdapter.getSettlementDate()).thenReturn(settlementDate);
		when(mockOcbcPaynowAdapter.processRefundReversalDuetoException(any())).thenThrow(BaseBusinessException.class);
		when(mockExceptionHandler.handleBusinessException(any(), any())).thenReturn(any());
		
		BaseBusinessException e = assertThrows(BaseBusinessException.class, () -> unit.process(payment));
		
		verify(mockIdRegistryService, times(1)).saveIDRegistry(any());
		verify(mockRequestService, times(1)).findBySearchId(payment.getTransactionDomainData().getMerchantReferenceNumber().substring(0,16));
		verify(mockPhysicalPosService, times(1)).getPosInfoOnlyByPosId(orderRequest.getPosId());
		verify(mockAcquirerService, times(1)).getAcquirerSofDetails(payment.getSofUri());
		assertEquals(1,amaList.size());
		verify(mockAcqMerAccountService, times(1)).getAcqMerAccountsByPosIdAndAcquirerIdAndSourceCurrency(any(), eq(acquirer.getAcquirerId()) , eq(payment.getTransactionDomainData().getCreditCurrency().toUpperCase()));
		verify(mockBankService, times(1)).findByAcquirerAndValidate(acquirer.getAcquirerId());
		verify(mockPhysicalPosService, times(1)).findByNetsTid(any());
		verify(mockCodesService, times(1)).findByCodeValue("QR_CODE");
		verify(mockTransactionService, times(2)).save(any());
		verify(mockPaynowTransactionDataService, times(1)).save(any());
		verify(mockTransactionService, times(1)).retrieve(anyInt());
		//verify(mockBankAdapterProcessingFactory, times(2)).getBankAdapter(any());
		verify(mockOcbcPaynowAdapter, times(1)).getSettlementDate();
		//verify(mockOcbcPaynowAdapter, times(1)).processRefundReversalDuetoException(any());
		//verify(mockExceptionHandler, times(1)).handleBusinessException(any(), any()); //this tests for the exception case
		assertEquals("85", e.getCode());
		assertEquals("[85]Credit Notification Was Previously Received.", e.getMessage());
	}
	
	@Test
	public void process_ReverseTransaction_ExceptionWithMessage() throws BaseBusinessException {
		
		CreditNotificationRequest payment = createCreditNotificationRequest();
		PaymentTransactionDomainData transactionDomainData = payment.getTransactionDomainData();
		payment.setTransactionDomainData(transactionDomainData);
		when(mockIdRegistryService.saveIDRegistry(any())).thenReturn(any());
		Request orderRequest = createOrderRequest();
		orderRequest.setState(RequestState.COMPLETED.getState());
		when(mockRequestService.findBySearchId(payment.getTransactionDomainData().getMerchantReferenceNumber().substring(0,16))).thenReturn(orderRequest);
		PosInfoOnly pos = createPosInfo();
		when(mockPhysicalPosService.getPosInfoOnlyByPosId(orderRequest.getPosId())).thenReturn(pos);	
		Map<ContextData, Object> entityMap = createEntityMap();
		when(mockAcquirerService.getAcquirerSofDetails(payment.getSofUri())).thenReturn(entityMap);
		AcquirerEntity acquirer = (AcquirerEntity) entityMap.get(ContextData.ACQUIRER);
		acquirer.setStatus("2");
		entityMap.replace(ContextData.ACQUIRER, acquirer);
		AcqMerAccountEntity ama = createAcqMerAccount();
		AcquirerMerchantMappingEntity amaMap = ama.getAcquirerMerchantMapping();
		amaMap.setStatus("2");
		ama.setAcquirerMerchantMapping(amaMap);
		List<AcqMerAccountEntity> amaList = new ArrayList<>();
		amaList.add(ama);
		when(mockAcqMerAccountService.getAcqMerAccountsByPosIdAndAcquirerIdAndSourceCurrency(any(), eq(acquirer.getAcquirerId()) , eq(payment.getTransactionDomainData().getCreditCurrency().toUpperCase()))).thenReturn(amaList);
		BankEntity bank = createBank();
		bank.setBankCfgAttr(new HashSet<BankCfgAttrEntity>());
		when(mockBankService.findByAcquirerAndValidate(acquirer.getAcquirerId())).thenReturn(bank);
		PhysicalPosEntity physicalPos = createPhysicalPosEntity();
		MerchantEntity merchant = physicalPos.getMerchant();
		merchant.setStatus("2");
		physicalPos.setMerchant(merchant);
		when(mockPhysicalPosService.findByNetsTid(any())).thenReturn(physicalPos);
		SOFTypeEntity sofType = new SOFTypeEntity();
		when(mockSofTypeService.getCardTypeById(any())).thenReturn(sofType);
		CodesEntity codes = new CodesEntity();
		codes.setIntegRefValue(00);
		when(mockCodesService.findByCodeValue("QR_CODE")).thenReturn(codes);
		TransactionEntity transaction = new TransactionEntity();
		transaction.setTransactionId(00);
		when(mockTransactionService.save(any())).thenReturn(transaction);
		PaynowTransactionData paynowTransactionData = new PaynowTransactionData();
		long id = 123123;
		paynowTransactionData.setId(id);
		when(mockPaynowTransactionDataService.save(any())).thenReturn(paynowTransactionData.getId());
		when(mockTransactionService.retrieve(anyInt())).thenReturn(transaction);
		when(mockTransactionService.save(any())).thenReturn(transaction);
		
		Timestamp settlementDate = new Timestamp(System.currentTimeMillis());
		when(mockBankAdapterProcessingFactory.getBankAdapter(any())).thenReturn(mockOcbcPaynowAdapter);
		when(mockOcbcPaynowAdapter.getSettlementDate()).thenReturn(settlementDate);
		BaseBusinessException exception = new BaseBusinessException("999", "TestingTesting");
		when(mockOcbcPaynowAdapter.processRefundReversalDuetoException(any())).thenThrow(exception);
		when(mockExceptionHandler.handleBusinessException(any(), any())).thenReturn(any());
		
		BaseBusinessException e = assertThrows(BaseBusinessException.class, () -> unit.process(payment));
		
		verify(mockIdRegistryService, times(1)).saveIDRegistry(any());
		verify(mockRequestService, times(1)).findBySearchId(payment.getTransactionDomainData().getMerchantReferenceNumber().substring(0,16));
		verify(mockPhysicalPosService, times(1)).getPosInfoOnlyByPosId(orderRequest.getPosId());
		verify(mockAcquirerService, times(1)).getAcquirerSofDetails(payment.getSofUri());
		assertEquals(1,amaList.size());
		verify(mockAcqMerAccountService, times(1)).getAcqMerAccountsByPosIdAndAcquirerIdAndSourceCurrency(any(), eq(acquirer.getAcquirerId()) , eq(payment.getTransactionDomainData().getCreditCurrency().toUpperCase()));
		verify(mockBankService, times(1)).findByAcquirerAndValidate(acquirer.getAcquirerId());
		verify(mockPhysicalPosService, times(1)).findByNetsTid(any());
		verify(mockCodesService, times(1)).findByCodeValue("QR_CODE");
		verify(mockTransactionService, times(2)).save(any());
		verify(mockPaynowTransactionDataService, times(1)).save(any());
		verify(mockTransactionService, times(1)).retrieve(anyInt());
		//verify(mockBankAdapterProcessingFactory, times(2)).getBankAdapter(any());
		verify(mockOcbcPaynowAdapter, times(1)).getSettlementDate();
		//verify(mockOcbcPaynowAdapter, times(1)).processRefundReversalDuetoException(any());
		//verify(mockExceptionHandler, times(1)).(any(), any()); //this tests for the exception case
		assertEquals("85", e.getCode());
		assertEquals("[85]Credit Notification Was Previously Received.", e.getMessage());
	}
	
	@Test
	public void process_SaveOrderRequest_OptimisticLockingFailureException() throws BaseBusinessException {
		
		CreditNotificationRequest payment = createCreditNotificationRequest();
		PaymentTransactionDomainData transactionDomainData = payment.getTransactionDomainData();
		payment.setTransactionDomainData(transactionDomainData);
		when(mockIdRegistryService.saveIDRegistry(any())).thenReturn(any());
		Request orderRequest = createOrderRequest();
		when(mockRequestService.findBySearchId(payment.getTransactionDomainData().getMerchantReferenceNumber().substring(0,16))).thenReturn(orderRequest);
		PosInfoOnly pos = createPosInfo();
		when(mockPhysicalPosService.getPosInfoOnlyByPosId(orderRequest.getPosId())).thenReturn(pos);		
		Map<ContextData, Object> entityMap = createEntityMap();
		when(mockAcquirerService.getAcquirerSofDetails(payment.getSofUri())).thenReturn(entityMap);
		AcquirerEntity acquirer = (AcquirerEntity) entityMap.get(ContextData.ACQUIRER);
		entityMap.replace(ContextData.ACQUIRER, acquirer);
		AcqMerAccountEntity ama = createAcqMerAccount();
		AcquirerMerchantMappingEntity amaMap = ama.getAcquirerMerchantMapping();
		amaMap.setStatus("2");
		ama.setAcquirerMerchantMapping(amaMap);
		List<AcqMerAccountEntity> amaList = new ArrayList<>();
		amaList.add(ama);
		when(mockAcqMerAccountService.getAcqMerAccountsByPosIdAndAcquirerIdAndSourceCurrency(any(), eq(acquirer.getAcquirerId()) , eq(payment.getTransactionDomainData().getCreditCurrency().toUpperCase()))).thenReturn(amaList);
		BankEntity bank = createBank();
		when(mockBankService.findByAcquirerAndValidate(acquirer.getAcquirerId())).thenReturn(bank);
		PhysicalPosEntity physicalPos = createPhysicalPosEntity();
		when(mockPhysicalPosService.findByNetsTid(any())).thenReturn(physicalPos);
		SOFTypeEntity sofType = new SOFTypeEntity();
		when(mockSofTypeService.getCardTypeById(any())).thenReturn(sofType);
		CodesEntity codes = new CodesEntity();
		codes.setIntegRefValue(00);
		when(mockCodesService.findByCodeValue("QR_CODE")).thenReturn(codes);
		TransactionEntity transaction = new TransactionEntity();
		transaction.setTransactionId(00);
		when(mockTransactionService.save(any())).thenReturn(transaction);
		when(mockPaynowTransactionDataService.save(any())).thenReturn(new Long(123123));
		when(mockRequestService.save(orderRequest)).thenThrow(OptimisticLockingFailureException.class);
		List<PosNotificationEntity> posNotificationList = new ArrayList<PosNotificationEntity>();
		when(mockPosNotificationService.findByPosId(any())).thenReturn(posNotificationList);
		Timestamp settlementDate = new Timestamp(System.currentTimeMillis());
		when(mockBankAdapterProcessingFactory.getBankAdapter(any())).thenReturn(mockOcbcPaynowAdapter);
		when(mockOcbcPaynowAdapter.getSettlementDate()).thenReturn(settlementDate);
		
		unit.process(payment);
		
		ArgumentCaptor<Request> argumentCaptorRequest = ArgumentCaptor.forClass(Request.class);
		verify(mockRequestService, times(1)).save(argumentCaptorRequest.capture());
		Request orderReq = argumentCaptorRequest.getValue();
		
		verify(mockIdRegistryService, times(1)).saveIDRegistry(any());
		verify(mockRequestService, times(3)).findBySearchId(payment.getTransactionDomainData().getMerchantReferenceNumber().substring(0,16)); //running 3rd time checks for optimisticlockingfailureexception
		verify(mockPhysicalPosService, times(1)).getPosInfoOnlyByPosId(orderRequest.getPosId());
		verify(mockAcquirerService, times(1)).getAcquirerSofDetails(payment.getSofUri());
		assertEquals(1,amaList.size());
		verify(mockAcqMerAccountService, times(1)).getAcqMerAccountsByPosIdAndAcquirerIdAndSourceCurrency(any(), eq(acquirer.getAcquirerId()) , eq(payment.getTransactionDomainData().getCreditCurrency().toUpperCase()));
		verify(mockBankService, times(1)).findByAcquirerAndValidate(acquirer.getAcquirerId());
		verify(mockPhysicalPosService, times(1)).findByNetsTid(any());
		verify(mockCodesService, times(1)).findByCodeValue("QR_CODE");
		verify(mockTransactionService, times(1)).save(any());
		verify(mockPaynowTransactionDataService, times(1)).save(any());
		//verify(mockBankAdapterProcessingFactory, times(1)).getBankAdapter(any());
		verify(mockOcbcPaynowAdapter, times(1)).getSettlementDate();
		verify(mockPosNotificationService, times(1)).findByPosId(anyInt());
		assertEquals(RequestState.COMPLETED.getState(),orderReq.getState());	
	}
	
	@Test
	public void process_SaveOrderRequest_OptimisticLockingFailureException_IsReversedTrue() throws BaseBusinessException {
		
		CreditNotificationRequest payment = createCreditNotificationRequest();
		PaymentTransactionDomainData transactionDomainData = payment.getTransactionDomainData();
		payment.setTransactionDomainData(transactionDomainData);
		when(mockIdRegistryService.saveIDRegistry(any())).thenReturn(any());
		Request orderRequest = createOrderRequest();
		Request reversedOrderRequest = new Request();
		reversedOrderRequest.setState(RequestState.REVERSED.getState());
		when(mockRequestService.findBySearchId(payment.getTransactionDomainData().getMerchantReferenceNumber().substring(0,16))).thenReturn(orderRequest).thenReturn(orderRequest).thenReturn(reversedOrderRequest);
		PosInfoOnly pos = createPosInfo();
		when(mockPhysicalPosService.getPosInfoOnlyByPosId(orderRequest.getPosId())).thenReturn(pos);		
		Map<ContextData, Object> entityMap = createEntityMap();
		when(mockAcquirerService.getAcquirerSofDetails(payment.getSofUri())).thenReturn(entityMap);
		AcquirerEntity acquirer = (AcquirerEntity) entityMap.get(ContextData.ACQUIRER);
		entityMap.replace(ContextData.ACQUIRER, acquirer);
		AcqMerAccountEntity ama = createAcqMerAccount();
		AcquirerMerchantMappingEntity amaMap = ama.getAcquirerMerchantMapping();
		amaMap.setStatus("2");
		ama.setAcquirerMerchantMapping(amaMap);
		List<AcqMerAccountEntity> amaList = new ArrayList<>();
		amaList.add(ama);
		when(mockAcqMerAccountService.getAcqMerAccountsByPosIdAndAcquirerIdAndSourceCurrency(any(), eq(acquirer.getAcquirerId()) , eq(payment.getTransactionDomainData().getCreditCurrency().toUpperCase()))).thenReturn(amaList);
		BankEntity bank = createBank();
		when(mockBankService.findByAcquirerAndValidate(acquirer.getAcquirerId())).thenReturn(bank);
		PhysicalPosEntity physicalPos = createPhysicalPosEntity();
		when(mockPhysicalPosService.findByNetsTid(any())).thenReturn(physicalPos);
		SOFTypeEntity sofType = new SOFTypeEntity();
		when(mockSofTypeService.getCardTypeById(any())).thenReturn(sofType);
		CodesEntity codes = new CodesEntity();
		codes.setIntegRefValue(00);
		when(mockCodesService.findByCodeValue("QR_CODE")).thenReturn(codes);
		TransactionEntity transaction = new TransactionEntity();
		transaction.setTransactionId(00);
		when(mockTransactionService.save(any())).thenReturn(transaction);
		when(mockPaynowTransactionDataService.save(any())).thenReturn(new Long(123123));
		when(mockRequestService.save(orderRequest)).thenThrow(OptimisticLockingFailureException.class);
		Timestamp settlementDate = new Timestamp(System.currentTimeMillis());
		when(mockBankAdapterProcessingFactory.getBankAdapter(any())).thenReturn(mockOcbcPaynowAdapter);
		when(mockOcbcPaynowAdapter.getSettlementDate()).thenReturn(settlementDate);
		
		BaseBusinessException e = assertThrows(BaseBusinessException.class, ()-> unit.process(payment));
		
		ArgumentCaptor<Request> argumentCaptorRequest = ArgumentCaptor.forClass(Request.class);
		verify(mockRequestService, times(1)).save(argumentCaptorRequest.capture());
		Request orderReq = argumentCaptorRequest.getValue();
		
		verify(mockIdRegistryService, times(1)).saveIDRegistry(any());
		verify(mockRequestService, times(3)).findBySearchId(payment.getTransactionDomainData().getMerchantReferenceNumber().substring(0,16)); //running 3rd time checks for optimisticlockingfailureexception
		verify(mockPhysicalPosService, times(1)).getPosInfoOnlyByPosId(orderRequest.getPosId());
		verify(mockAcquirerService, times(1)).getAcquirerSofDetails(payment.getSofUri());
		assertEquals(1,amaList.size());
		verify(mockAcqMerAccountService, times(1)).getAcqMerAccountsByPosIdAndAcquirerIdAndSourceCurrency(any(), eq(acquirer.getAcquirerId()) , eq(payment.getTransactionDomainData().getCreditCurrency().toUpperCase()));
		verify(mockBankService, times(1)).findByAcquirerAndValidate(acquirer.getAcquirerId());
		verify(mockPhysicalPosService, times(1)).findByNetsTid(any());
		verify(mockCodesService, times(1)).findByCodeValue("QR_CODE");
		verify(mockTransactionService, times(1)).save(any());
		verify(mockPaynowTransactionDataService, times(1)).save(any());
		//verify(mockBankAdapterProcessingFactory, times(1)).getBankAdapter(any());
		verify(mockOcbcPaynowAdapter, times(1)).getSettlementDate();
		assertEquals(RequestState.COMPLETED.getState(),orderReq.getState());
		assertEquals("68", e.getCode());
		assertEquals("[68]Order Timeout.", e.getMessage());		
	}
	
	@Test
	public void process_PosNotificationNotCreated() throws BaseBusinessException {
		
		CreditNotificationRequest payment = createCreditNotificationRequest();
		PaymentTransactionDomainData transactionDomainData = payment.getTransactionDomainData();
		payment.setTransactionDomainData(transactionDomainData);
		when(mockIdRegistryService.saveIDRegistry(any())).thenReturn(any());
		Request orderRequest = createOrderRequest();
		when(mockRequestService.findBySearchId(payment.getTransactionDomainData().getMerchantReferenceNumber().substring(0,16))).thenReturn(orderRequest);
		PosInfoOnly pos = createPosInfo();
		when(mockPhysicalPosService.getPosInfoOnlyByPosId(orderRequest.getPosId())).thenReturn(pos);		
		Map<ContextData, Object> entityMap = createEntityMap();
		when(mockAcquirerService.getAcquirerSofDetails(payment.getSofUri())).thenReturn(entityMap);
		AcquirerEntity acquirer = (AcquirerEntity) entityMap.get(ContextData.ACQUIRER);
		entityMap.replace(ContextData.ACQUIRER, acquirer);
		AcqMerAccountEntity ama = createAcqMerAccount();
		AcquirerMerchantMappingEntity amaMap = ama.getAcquirerMerchantMapping();
		amaMap.setStatus("2");
		ama.setAcquirerMerchantMapping(amaMap);
		List<AcqMerAccountEntity> amaList = new ArrayList<>();
		amaList.add(ama);
		when(mockAcqMerAccountService.getAcqMerAccountsByPosIdAndAcquirerIdAndSourceCurrency(any(), eq(acquirer.getAcquirerId()) , eq(payment.getTransactionDomainData().getCreditCurrency().toUpperCase()))).thenReturn(amaList);
		BankEntity bank = createBank();
		when(mockBankService.findByAcquirerAndValidate(acquirer.getAcquirerId())).thenReturn(bank);
		PhysicalPosEntity physicalPos = createPhysicalPosEntity();
		when(mockPhysicalPosService.findByNetsTid(any())).thenReturn(physicalPos);
		SOFTypeEntity sofType = new SOFTypeEntity();
		when(mockSofTypeService.getCardTypeById(any())).thenReturn(sofType);
		CodesEntity codes = new CodesEntity();
		codes.setIntegRefValue(00);
		when(mockCodesService.findByCodeValue("QR_CODE")).thenReturn(codes);
		TransactionEntity transaction = new TransactionEntity();
		transaction.setTransactionId(00);
		when(mockTransactionService.save(any())).thenReturn(transaction);
		when(mockPaynowTransactionDataService.save(any())).thenReturn(new Long(123123));
		when(mockRequestService.save(orderRequest)).thenReturn(orderRequest);
		List<PosNotificationEntity> posNotificationList = new ArrayList<PosNotificationEntity>();
		when(mockPosNotificationService.findByPosId(anyInt())).thenReturn(posNotificationList);
		
		Timestamp settlementDate = new Timestamp(System.currentTimeMillis());
		when(mockBankAdapterProcessingFactory.getBankAdapter(any())).thenReturn(mockOcbcPaynowAdapter);
		when(mockOcbcPaynowAdapter.getSettlementDate()).thenReturn(settlementDate);
		
		unit.process(payment);
		
		verify(mockIdRegistryService, times(1)).saveIDRegistry(any());
		verify(mockRequestService, times(2)).findBySearchId(payment.getTransactionDomainData().getMerchantReferenceNumber().substring(0,16));
		verify(mockPhysicalPosService, times(1)).getPosInfoOnlyByPosId(orderRequest.getPosId());
		verify(mockAcquirerService, times(1)).getAcquirerSofDetails(payment.getSofUri());
		assertEquals(1,amaList.size());
		verify(mockAcqMerAccountService, times(1)).getAcqMerAccountsByPosIdAndAcquirerIdAndSourceCurrency(any(), eq(acquirer.getAcquirerId()) , eq(payment.getTransactionDomainData().getCreditCurrency().toUpperCase()));
		verify(mockBankService, times(1)).findByAcquirerAndValidate(acquirer.getAcquirerId());
		verify(mockPhysicalPosService, times(1)).findByNetsTid(any());
		verify(mockCodesService, times(1)).findByCodeValue("QR_CODE");
		verify(mockTransactionService, times(1)).save(any());
		verify(mockPaynowTransactionDataService, times(1)).save(any());
		//verify(mockBankAdapterProcessingFactory, times(1)).getBankAdapter(any());
		verify(mockOcbcPaynowAdapter, times(1)).getSettlementDate();
		verify(mockPosNotificationService, times(1)).findByPosId(anyInt());
	}
	
	@Test
	public void process_SuccessCase() throws BaseBusinessException {
		
		CreditNotificationRequest payment = createCreditNotificationRequest();
		PaymentTransactionDomainData transactionDomainData = payment.getTransactionDomainData();
		payment.setTransactionDomainData(transactionDomainData);
		when(mockIdRegistryService.saveIDRegistry(any())).thenReturn(any());
		Request orderRequest = createOrderRequest();
		when(mockRequestService.findBySearchId(payment.getTransactionDomainData().getMerchantReferenceNumber().substring(0,16))).thenReturn(orderRequest);
		PosInfoOnly pos = createPosInfo();
		when(mockPhysicalPosService.getPosInfoOnlyByPosId(orderRequest.getPosId())).thenReturn(pos);
		Map<ContextData, Object> entityMap = createEntityMap();
		when(mockAcquirerService.getAcquirerSofDetails(payment.getSofUri())).thenReturn(entityMap);
		AcquirerEntity acquirer = (AcquirerEntity) entityMap.get(ContextData.ACQUIRER);
		entityMap.replace(ContextData.ACQUIRER, acquirer);
		AcqMerAccountEntity ama = createAcqMerAccount();
		AcquirerMerchantMappingEntity amaMap = ama.getAcquirerMerchantMapping();
		amaMap.setStatus("2");
		ama.setAcquirerMerchantMapping(amaMap);
		List<AcqMerAccountEntity> amaList = new ArrayList<>();
		amaList.add(ama);
		when(mockAcqMerAccountService.getAcqMerAccountsByPosIdAndAcquirerIdAndSourceCurrency(any(), eq(acquirer.getAcquirerId()) , eq(payment.getTransactionDomainData().getCreditCurrency().toUpperCase()))).thenReturn(amaList);
		BankEntity bank = createBank();
		when(mockBankService.findByAcquirerAndValidate(acquirer.getAcquirerId())).thenReturn(bank);
		PhysicalPosEntity physicalPos = createPhysicalPosEntity();
		when(mockPhysicalPosService.findByNetsTid(any())).thenReturn(physicalPos);
		SOFTypeEntity sofType = new SOFTypeEntity();
		when(mockSofTypeService.getCardTypeById(any())).thenReturn(sofType);
		CodesEntity codes = new CodesEntity();
		codes.setIntegRefValue(00);
		when(mockCodesService.findByCodeValue("QR_CODE")).thenReturn(codes);
		BigDecimal mdrPercent = new BigDecimal(0.5);
		when(mockMdrService.getMdrPercent((physicalPos).getMerchant(), sofType)).thenReturn(mdrPercent);
		TransactionEntity transaction = new TransactionEntity();
		transaction.setTransactionId(00);
		when(mockTransactionService.save(any())).thenReturn(transaction);
		PaynowTransactionData paynowTransactionData = new PaynowTransactionData();
		long id = 123123;
		paynowTransactionData.setId(id);
		when(mockPaynowTransactionDataService.save(any())).thenReturn(paynowTransactionData.getId());
		
		
		Timestamp settlementDate = new Timestamp(System.currentTimeMillis());
		when(mockBankAdapterProcessingFactory.getBankAdapter(any())).thenReturn(mockOcbcPaynowAdapter);
		when(mockOcbcPaynowAdapter.getSettlementDate()).thenReturn(settlementDate);
		
		PosNotificationEntity posNotification = new PosNotificationEntity();
		List<PosNotificationEntity> posNotifList = new ArrayList<PosNotificationEntity>();
		posNotifList.add(posNotification);
		when(mockPosNotificationService.findByPosId(any())).thenReturn(posNotifList);
		
		unit.process(payment);
		
		verify(mockIdRegistryService, times(1)).saveIDRegistry(any());
		verify(mockRequestService, times(2)).findBySearchId(payment.getTransactionDomainData().getMerchantReferenceNumber().substring(0,16));
		verify(mockPhysicalPosService, times(1)).getPosInfoOnlyByPosId(orderRequest.getPosId());
		verify(mockAcquirerService, times(1)).getAcquirerSofDetails(payment.getSofUri());
		assertEquals(1,amaList.size());
		verify(mockAcqMerAccountService, times(1)).getAcqMerAccountsByPosIdAndAcquirerIdAndSourceCurrency(any(), eq(acquirer.getAcquirerId()) , eq(payment.getTransactionDomainData().getCreditCurrency().toUpperCase()));
		verify(mockBankService, times(1)).findByAcquirerAndValidate(acquirer.getAcquirerId());
		verify(mockPhysicalPosService, times(1)).findByNetsTid(any());
		verify(mockCodesService, times(1)).findByCodeValue("QR_CODE");
		verify(mockMdrService, times(1)).getMdrPercent(physicalPos.getMerchant(), sofType);
		verify(mockTransactionService, times(1)).save(any());
		verify(mockPaynowTransactionDataService, times(1)).save(any());
		verify(mockPosNotificationService, times(1)).findByPosId(any());
		//verify(mockBankAdapterProcessingFactory, times(1)).getBankAdapter(any());
		verify(mockOcbcPaynowAdapter, times(1)).getSettlementDate();
		
		ArgumentCaptor<TransactionEntity> argumentCaptorTransaction = ArgumentCaptor.forClass(TransactionEntity.class);
		verify(mockTransactionService, times(1)).save(argumentCaptorTransaction.capture());
		TransactionEntity txn = argumentCaptorTransaction.getValue();
		
		assertNotNull(txn.getAcqApprovalCode());
		assertEquals(acquirer.getAcquirerId() ,txn.getAcquirerId());
		assertEquals(ama.getAcqMerAccountId(), txn.getAcqMerAcctId());
		assertEquals(ama.getAcquirerMerchantMapping().getMerMappingId(), txn.getAcqMerMappingId());
		assertNotNull(txn.getAuthorizeDate());
		assertEquals(00, txn.getCaptureType());
		assertEquals(2, txn.getCreatedBy());
		assertNotNull(txn.getCreationDate());
		assertEquals(((CardTypeEntity)entityMap.get(ContextData.ACQ_CARD_TYPE)).getCardTypeId(), txn.getCardTypeId());
		assertEquals('N', txn.getIsDccTransaction());
		assertEquals('N', txn.getIsRefund());
		assertEquals(payment.getRetrievalRef(), txn.getAcquirerAuthResponseId());
		assertEquals(ama.getEcMid(), txn.getEcMid());
		assertEquals(mdrPercent, txn.getMdrPercent());
		assertEquals((physicalPos).getMerchant().getMerchantId(), txn.getMerchantId());
		assertEquals(new BigDecimal(payment.getTransactionDomainData().getCreditAmount()).divide(new BigDecimal(100).setScale(2, BigDecimal.ROUND_HALF_DOWN)), txn.getOrigTargetAmount());
		assertEquals(physicalPos.getPosId(), txn.getPosId());
		assertEquals(new BigDecimal(payment.getTransactionDomainData().getCreditAmount()).divide(new BigDecimal(100).setScale(2, BigDecimal.ROUND_HALF_DOWN)), txn.getSourceAmount());
		assertEquals("SGD", txn.getSourceCurrency());
		assertEquals(TransactionState.STATE_SETTLED.getState(), txn.getState());
		assertEquals(new BigDecimal(payment.getTransactionDomainData().getCreditAmount()).divide(new BigDecimal(100).setScale(2, BigDecimal.ROUND_HALF_DOWN)), txn.getTargetAmount());
		assertEquals("SGD", txn.getTargetCurrency());
		assertEquals(ama.getTid(), txn.getTid());
		assertEquals(TransactionTypes.TYPE_PAYNOW_DYNAMIC_CREDIT_NOTIFICATION, txn.getTranType());
		assertEquals("NA", txn.getTransactionSource());
		assertNotNull(txn.getWorkflowBizKey());
		assertEquals(settlementDate, txn.getBankSettleDate());
		
		ArgumentCaptor<PaynowTransactionData> argumentCaptorPaynowTransactionData = ArgumentCaptor.forClass(PaynowTransactionData.class);
		verify(mockPaynowTransactionDataService, times(1)).save(argumentCaptorPaynowTransactionData.capture());
		PaynowTransactionData paynowTransactionData2 = argumentCaptorPaynowTransactionData.getValue();

		assertEquals(payment.getRetrievalRef(), paynowTransactionData2.getRetrievalRef());
		assertEquals(payment.getInstitutionCode(), paynowTransactionData2.getInstitutionCode());
		assertEquals(ama.getTid(), paynowTransactionData2.getTid());
		assertNotNull(paynowTransactionData2.getTransactionId());
		assertEquals(new BigDecimal(payment.getTransactionDomainData().getCreditAmount()).divide(new BigDecimal(100).setScale(2, BigDecimal.ROUND_HALF_DOWN)), paynowTransactionData2.getCreditAmount());
		assertEquals(payment.getTransactionDomainData().getCreditCurrency(), paynowTransactionData2.getCreditCurrency());
		assertEquals(payment.getTransactionDomainData().getCreditingTime(), paynowTransactionData2.getCreditingTime());
		assertEquals(payment.getTransactionDomainData().getReceivingAccountNumber(), paynowTransactionData2.getReceivingAccountNumber());
		assertEquals(payment.getTransactionDomainData().getReceivingAccountType(), paynowTransactionData2.getReceivingAccountType());
		assertEquals(payment.getTransactionDomainData().getMerchantReferenceNumber(), paynowTransactionData2.getMerchantReferenceNumber());
		assertEquals(payment.getTransactionDomainData().getSendingAccountBank(), paynowTransactionData2.getSendingAccountBank());
		assertEquals(payment.getTransactionDomainData().getSendingAccountName(), paynowTransactionData2.getSendingAccountName());
		assertEquals(payment.getTransactionDomainData().getSendingAccountNumber(), paynowTransactionData2.getSendingAccountNumber());
		assertEquals(payment.getTransactionDomainData().getAdditionalBankReference(), paynowTransactionData2.getAdditionalBankReference());
		
		ArgumentCaptor<Request> argumentCaptorRequest = ArgumentCaptor.forClass(Request.class);
		verify(mockRequestService, times(1)).save(argumentCaptorRequest.capture());
		Request requestEntity = argumentCaptorRequest.getValue();
		
		assertNotNull(requestEntity.getTransactionId());
		assertEquals(RequestState.COMPLETED.getState(), requestEntity.getState());
		assertEquals(bank.getBankCfgAttr().iterator().next().getCfgAttrValue(), requestEntity.getPaymentType());
		assertNotNull(requestEntity.getExternalSettlementDate());
		
		ArgumentCaptor<TransactionStateMsg> argumentCaptorTxnStateMsg = ArgumentCaptor.forClass(TransactionStateMsg.class);
		verify(mockNonStopClient, times(1)).put(argumentCaptorTxnStateMsg.capture());
		TransactionStateMsg txnStateMsg = argumentCaptorTxnStateMsg.getValue();
		
		assertEquals(payment.getSofUri(), txnStateMsg.getSofUri());
		assertEquals(payment.getTransactionDomainData().getCreditAmount(), txnStateMsg.getAmount());
		assertEquals(payment.getTransmissionTime(), txnStateMsg.getTransmissionTime());
		assertNotNull(txnStateMsg.getStan());
		assertEquals(orderRequest.getTransactionTime(), txnStateMsg.getTransactionTime());
		assertEquals(orderRequest.getTransactionDate(), txnStateMsg.getTransactionDate());
		assertEquals(PaynowUtil.getTandemSettlementDate(settlementDate), txnStateMsg.getSettlementDate());
		assertEquals(payment.getInstitutionCode(), txnStateMsg.getInstitutionCode());
		assertEquals("00", txnStateMsg.getConditionCode());
		assertEquals(ResponseCodes.SUCCESS, txnStateMsg.getResponseCode());
		assertEquals(ama.getTid(), txnStateMsg.getHostTid());
		assertEquals(ama.getEcMid(), txnStateMsg.getHostMid());
		assertEquals("PNDYNAMICCN", txnStateMsg.getTransactionType());
		assertEquals(TransactionState.STATE_SETTLED.getState(), txnStateMsg.getStatus());
		assertEquals(bank.getBankCfgAttr().iterator().next().getCfgAttrValue(), txnStateMsg.getPaymentType());
		assertNotNull(txnStateMsg.getApprovalCode());
		assertEquals(payment.getInstitutionCode(), txnStateMsg.getInstitutionCode());
		assertEquals(payment.getRetrievalRef(), txnStateMsg.getRetrievalRef());
		assertNotNull(txnStateMsg.getTransactionId());
		
		ArgumentCaptor<JsonNotification> argumentCaptorNotificationResponse = ArgumentCaptor.forClass(JsonNotification.class);
		verify(mockNotificationClient, times(1)).put(argumentCaptorNotificationResponse.capture());
		JsonNotification notificationResponse = argumentCaptorNotificationResponse.getValue();
		NotificationRequest notification = (NotificationRequest) notificationResponse.getMessage();
		
		assertNotNull(notificationResponse.getCommunicationData());
		assertNotNull(notificationResponse.getMessage());
		assertEquals(payment.getRetrievalRef(), notification.getRetrievalRef());
		assertEquals("8200", notification.getMti());
		assertEquals("420000", notification.getProcessCode());
		assertNotNull(notification.getStan());
		assertEquals(orderRequest.getTransactionTime(), notification.getTransactionTime());
		assertEquals(orderRequest.getTransactionDate(), notification.getTransactionDate());
		assertEquals(ama.getTid(), notification.getTid());
		assertEquals(ama.getEcMid(), notification.getMid());
		assertEquals(ResponseCodes.SUCCESS, notification.getResponseCode());
		assertEquals(payment.getTransactionDomainData().getCreditAmount(), notification.getTargetAmount());
		assertEquals(payment.getSofUri(), notification.getSofUri());
		assertEquals(bank.getBankCfgAttr().iterator().next().getCfgAttrValue(), notification.getPaymentTypeId());
		assertNotNull(notification.getApprovalCode());
		assertNotNull(notification.getTransactionId());
		assertEquals(payment.getTransactionDomainData().getCreditCurrency(), notification.getCurrencyCode());
		assertEquals(TransactionState.STATE_SETTLED.getState(), notification.getTransactionStatus());
		
		ArgumentCaptor<APSRequestWrapper> argumentCaptorAPSRequestWrapper = ArgumentCaptor.forClass(APSRequestWrapper.class);
		verify(mockFraudwallClient, times(1)).put(argumentCaptorAPSRequestWrapper.capture());
		APSRequestWrapper wrapper = argumentCaptorAPSRequestWrapper.getValue();
		APSRequest apsRequest = wrapper.getRequest();
		Map<String, String> additionalData = apsRequest.getAdditionalData();
		
		assertEquals(ama.getTid(), apsRequest.getTid());
		assertNotNull(apsRequest.getTransactionId());
		assertNotNull(apsRequest.getStan());
		assertEquals(ama.getEcMid(), apsRequest.getMid());
		assertEquals(new BigDecimal(payment.getTransactionDomainData().getCreditAmount()).divide(new BigDecimal(100).setScale(2, BigDecimal.ROUND_HALF_DOWN)), apsRequest.getTargetAmount());
		assertNotNull(apsRequest.getRequestMap());
		assertNotNull(apsRequest.getApprovalCode());
		assertEquals("00", additionalData.get(AdditionalData.CONDITION_CODE));
		assertEquals(bank.getBankCfgAttr().iterator().next().getCfgAttrValue(), additionalData.get(AdditionalData.PAYMENT_TYPE));
		assertEquals("00", wrapper.getResponse().getStatusCode());	
	}
}
