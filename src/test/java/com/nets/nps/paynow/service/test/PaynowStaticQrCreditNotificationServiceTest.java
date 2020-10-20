package com.nets.nps.paynow.service.test;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;

import com.nets.nps.client.impl.FraudwallClient;
import com.nets.nps.client.impl.NonStopClient;
import com.nets.nps.client.impl.NotificationClient;
import com.nets.nps.client.impl.NotificationRequest;
import com.nets.nps.client.impl.TransactionStateMsg;
import com.nets.nps.paynow.client.bank.BankAdapterProcessingFactory;
import com.nets.nps.paynow.client.bank.ocbc.OcbcPaynowAdapter;
import com.nets.nps.paynow.entity.CreditNotificationInfo;
import com.nets.nps.paynow.entity.CreditNotificationRequest;
import com.nets.nps.paynow.entity.CreditNotificationResponse;
import com.nets.nps.paynow.entity.PaymentTransactionDomainData;
import com.nets.nps.paynow.exception.ExceptionHandler;
import com.nets.nps.paynow.service.PaynowTransactionDataService;
import com.nets.nps.paynow.service.impl.PaynowStaticQrCreditNotificationService;
import com.nets.nps.paynow.utils.PaynowUtil;
import com.nets.upos.commons.constants.AdditionalData;
import com.nets.upos.commons.constants.MtiRequestMap;
import com.nets.upos.commons.constants.ResponseCodes;
import com.nets.upos.commons.constants.TransactionTypes;
import com.nets.upos.commons.entity.APSRequest;
import com.nets.upos.commons.entity.APSRequestWrapper;
import com.nets.upos.commons.entity.JsonNotification;
import com.nets.upos.commons.enums.BankConfigAttributeName;
import com.nets.upos.commons.enums.ContextData;
import com.nets.upos.commons.enums.TransactionState;
import com.nets.upos.commons.exception.BaseBusinessException;
import com.nets.upos.core.entity.AcqMerAccountEntity;
import com.nets.upos.core.entity.AcquirerEntity;
import com.nets.upos.core.entity.AcquirerMerchantMappingEntity;
import com.nets.upos.core.entity.BankCfgAttrEntity;
import com.nets.upos.core.entity.BankEntity;
import com.nets.upos.core.entity.CodesEntity;
import com.nets.upos.core.entity.MerchantCfgAttrEntity;
import com.nets.upos.core.entity.MerchantEntity;
import com.nets.upos.core.entity.PaynowTransactionData;
import com.nets.upos.core.entity.PhysicalPosEntity;
import com.nets.upos.core.entity.PosNotificationEntity;
import com.nets.upos.core.entity.SOFTypeEntity;
import com.nets.upos.core.entity.StaticRequest;
import com.nets.upos.core.entity.TransactionEntity;
import com.nets.upos.core.repository.projection.PosInfoOnly;
import com.nets.upos.core.service.AcqMerAccountService;
import com.nets.upos.core.service.AcquirerService;
import com.nets.upos.core.service.BankService;
import com.nets.upos.core.service.CodesService;
import com.nets.upos.core.service.IDRegistryService;
import com.nets.upos.core.service.MDRService;
import com.nets.upos.core.service.MerchantService;
import com.nets.upos.core.service.PhysicalPosService;
import com.nets.upos.core.service.PosNotificationService;
import com.nets.upos.core.service.SOFTypeService;
import com.nets.upos.core.service.StaticRequestService;
import com.nets.upos.core.service.TransactionService;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@SpringBootTest(classes = {PaynowStaticQrCreditNotificationService.class})
@TestPropertySource(locations = "classpath:paynow-local.properties")
public class PaynowStaticQrCreditNotificationServiceTest {

	@Autowired
	PaynowStaticQrCreditNotificationService unit;

	@MockBean
	private IDRegistryService mockIdRegistryService;

	@MockBean
	private AcquirerService mockAcquirerService;

	@MockBean
	private AcqMerAccountService mockAcqMerAccountService;

	@MockBean
	private PhysicalPosService mockPosService;

	@MockBean
	private BankService mockBankService;

	@MockBean
	private StaticRequestService mockStaticRequestService;

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
	private BankAdapterProcessingFactory mockBankAdapterProcessingFactory;

	@MockBean
	private OcbcPaynowAdapter mockOcbcPaynowAdapter;
	
	@MockBean
	private ExceptionHandler exceptionHandler;
	
	@MockBean
	private MerchantService mockMerchantService;
	
	@MockBean
	private FraudwallClient mockFraudwallClient;

	public CreditNotificationRequest createCreditNotificationRequest() {
		PaymentTransactionDomainData transactionDomainData = new PaymentTransactionDomainData();
		transactionDomainData.setCreditAmount("000000000469");
		transactionDomainData.setCreditCurrency("SGD");
		transactionDomainData.setCreditingTime("1127235829");
		transactionDomainData.setReceivingAccountNumber("12345");
		transactionDomainData.setReceivingAccountType("OWN");
		transactionDomainData.setMerchantReferenceNumber("1986031100000001 DBSMerch");
		transactionDomainData.setSendingAccountBank(null);
		transactionDomainData.setSendingAccountNumber(null);
		transactionDomainData.setSendingAccountName(null);
		transactionDomainData.setAdditionalBankReference("abcdefghij");
		CreditNotificationRequest creditNotificationRequest = new CreditNotificationRequest();
		creditNotificationRequest.setRetrievalRef("12345678268");
		creditNotificationRequest.setInstitutionCode("30000000033");
		creditNotificationRequest.setTransmissionTime("0526235829");
		creditNotificationRequest.setTransactionType("PNC");
		creditNotificationRequest.setTransactionDomainData(transactionDomainData);
		creditNotificationRequest.setMti("8200");
		creditNotificationRequest.setProcessCode("420000");

		return creditNotificationRequest;
	}

	@Test
	public void validateDuplicates() {
		CreditNotificationRequest creditNotificationRequest = createCreditNotificationRequest();
		when(mockIdRegistryService.saveIDRegistry(any())).thenThrow(RuntimeException.class);

		BaseBusinessException e = assertThrows(BaseBusinessException.class, () -> unit.process(creditNotificationRequest));
		assertEquals("85", e.getCode());
		assertEquals("Credit Notification Was Previously Received.", e.getErrorMessage());

		verify(mockIdRegistryService, times(1)).saveIDRegistry(any());
	}

	@Test
	public void validateAcquirerNotFound() throws BaseBusinessException {
		CreditNotificationRequest creditNotificationRequest = createCreditNotificationRequest();

		Map<ContextData, Object> entityMap = new HashMap<>();
		when(mockAcquirerService.getAcquirerSofDetails(any())).thenReturn(entityMap);

		BaseBusinessException e = assertThrows(BaseBusinessException.class, () -> unit.process(creditNotificationRequest));
		assertEquals("15", e.getCode());
		assertEquals("Acquirer not found.", e.getErrorMessage());

		verify(mockAcquirerService, times(1)).getAcquirerSofDetails(any());
	}

	@Test
	public void validateMerchantRefNumCantBeMatched() throws BaseBusinessException {
		CreditNotificationRequest creditNotificationRequest = createCreditNotificationRequest();

		Map<ContextData, Object> entityMap = new HashMap<>();
		AcquirerEntity acquirer = new AcquirerEntity();
		acquirer.setAcquirerId(66);
		entityMap.put(ContextData.ACQUIRER, acquirer);

		SOFTypeEntity sofType = new SOFTypeEntity();
		sofType.setCardTypeId(57);
		entityMap.put(ContextData.SOF_TYPE, sofType);

		when(mockAcquirerService.getAcquirerSofDetails(any())).thenReturn(entityMap);

		when(mockAcqMerAccountService.getAcqMerAccountEntityByAcquirerIdAndEcMidAndSourceCurrency(anyInt(), any(), any())).thenReturn(null);

		BaseBusinessException e = assertThrows(BaseBusinessException.class, () -> unit.process(creditNotificationRequest));
		assertEquals("82", e.getCode());
		assertEquals("Merchant Reference Number Cannot Be Matched.", e.getErrorMessage());

		verify(mockAcquirerService, times(1)).getAcquirerSofDetails(any());
	}

	@Test
	public void validateMerchantNotFound() throws BaseBusinessException {
		CreditNotificationRequest creditNotificationRequest = createCreditNotificationRequest();

		Map<ContextData, Object> entityMap = new HashMap<>();
		AcquirerEntity acquirer = new AcquirerEntity();
		acquirer.setStatus("2");
		acquirer.setAcquirerId(66);
		entityMap.put(ContextData.ACQUIRER, acquirer);

		SOFTypeEntity sofType = new SOFTypeEntity();
		sofType.setCardTypeId(57);
		entityMap.put(ContextData.SOF_TYPE, sofType);
		when(mockAcquirerService.getAcquirerSofDetails(any())).thenReturn(entityMap);

		AcqMerAccountEntity ama = new AcqMerAccountEntity();
		AcquirerMerchantMappingEntity amm = new AcquirerMerchantMappingEntity();
		amm.setStatus("2");
		amm.setMerMappingId(14);
		ama.setAcquirerMerchantMapping(amm);
		ama.setPosId(17);
		ama.setTid("UOPNKADE");
		ama.setAcqMerAccountId(17);

		when(mockAcqMerAccountService.getAcqMerAccountEntityByAcquirerIdAndEcMidAndSourceCurrency(anyInt(), any(), any())).thenReturn(ama);

		when(mockPosService.getPosInfoOnlyByPosId(anyInt())).thenReturn(null);

		BaseBusinessException e = assertThrows(BaseBusinessException.class, () -> unit.process(creditNotificationRequest));
		assertEquals("81", e.getCode());
		assertEquals("Merchant Not Found.", e.getErrorMessage());

		verify(mockIdRegistryService, times(1)).saveIDRegistry(any());
		verify(mockAcquirerService, times(1)).getAcquirerSofDetails(any());
		verify(mockAcqMerAccountService, times(1)).getAcqMerAccountEntityByAcquirerIdAndEcMidAndSourceCurrency(anyInt(), any(), any());
		verify(mockPosService, times(1)).getPosInfoOnlyByPosId(anyInt());
	}

	@Test
	public void validateNoBankFound() throws BaseBusinessException {
		CreditNotificationRequest creditNotificationRequest = createCreditNotificationRequest();

		Map<ContextData, Object> entityMap = new HashMap<>();
		AcquirerEntity acquirer = new AcquirerEntity();
		acquirer.setStatus("2");
		acquirer.setAcquirerId(66);
		entityMap.put(ContextData.ACQUIRER, acquirer);

		SOFTypeEntity sofType = new SOFTypeEntity();
		sofType.setCardTypeId(57);
		entityMap.put(ContextData.SOF_TYPE, sofType);
		when(mockAcquirerService.getAcquirerSofDetails(any())).thenReturn(entityMap);

		AcqMerAccountEntity ama = new AcqMerAccountEntity();
		AcquirerMerchantMappingEntity amm = new AcquirerMerchantMappingEntity();
		amm.setStatus("2");
		amm.setMerMappingId(14);
		ama.setAcquirerMerchantMapping(amm);
		ama.setPosId(17);
		ama.setTid("UOPNKADE");
		ama.setAcqMerAccountId(17);

		when(mockAcqMerAccountService.getAcqMerAccountEntityByAcquirerIdAndEcMidAndSourceCurrency(anyInt(), any(), any())).thenReturn(ama);

		PosInfoOnly posInfo = mock(PosInfoOnly.class);

		when(mockPosService.getPosInfoOnlyByPosId(anyInt())).thenReturn(posInfo);

		when(mockBankService.findByAcquirerAndValidate(acquirer.getAcquirerId())).thenReturn(null);

		BaseBusinessException e = assertThrows(BaseBusinessException.class, () -> unit.process(creditNotificationRequest));
		assertEquals("15", e.getCode());
		assertEquals("No route found to bank.", e.getErrorMessage());
	}

	@Test
	public void validateInvalidAmount() throws BaseBusinessException {
		CreditNotificationRequest creditNotificationRequest = createCreditNotificationRequest();
		PaymentTransactionDomainData transactionDomainData = new PaymentTransactionDomainData();
		transactionDomainData.setCreditAmount("000000000000");
		transactionDomainData.setCreditCurrency("SGD");
		transactionDomainData.setCreditingTime("0527131532");
		transactionDomainData.setReceivingAccountNumber("12345");
		transactionDomainData.setReceivingAccountType("OWN");
		transactionDomainData.setMerchantReferenceNumber("1986031100000001 DBSMerch");
		transactionDomainData.setSendingAccountBank(null);
		transactionDomainData.setSendingAccountNumber(null);
		transactionDomainData.setSendingAccountName(null);
		transactionDomainData.setAdditionalBankReference("abcdefghij");
		creditNotificationRequest.setTransactionDomainData(transactionDomainData);

		Map<ContextData, Object> entityMap = new HashMap<>();
		AcquirerEntity acquirer = new AcquirerEntity();
		acquirer.setStatus("2");
		acquirer.setAcquirerId(66);
		entityMap.put(ContextData.ACQUIRER, acquirer);

		SOFTypeEntity sofType = new SOFTypeEntity();
		sofType.setCardTypeId(57);
		entityMap.put(ContextData.SOF_TYPE, sofType);
		when(mockAcquirerService.getAcquirerSofDetails(any())).thenReturn(entityMap);

		AcqMerAccountEntity ama = new AcqMerAccountEntity();
		AcquirerMerchantMappingEntity amm = new AcquirerMerchantMappingEntity();
		amm.setStatus("2");
		amm.setMerMappingId(14);
		ama.setAcquirerMerchantMapping(amm);
		ama.setPosId(17);
		ama.setTid("UOPNKADE");
		ama.setAcqMerAccountId(17);

		when(mockAcqMerAccountService.getAcqMerAccountEntityByAcquirerIdAndEcMidAndSourceCurrency(anyInt(), any(), any())).thenReturn(ama);

		PosInfoOnly pos = mock(PosInfoOnly.class);

		when(mockPosService.getPosInfoOnlyByPosId(anyInt())).thenReturn(pos);

		Set<BankCfgAttrEntity> bankCfgAttrSet = new HashSet<>();
		BankCfgAttrEntity bankCfgAttr = new BankCfgAttrEntity();
		bankCfgAttr.setCfgAttrId(22);
		bankCfgAttr.setCfgAttrName(BankConfigAttributeName.PAYMENT_TYPE.name());
		bankCfgAttr.setCfgAttrValue("DBPN");
		bankCfgAttrSet.add(bankCfgAttr);

		BankEntity bank = new BankEntity();
		bank.setBankId(62);
		bank.setBankCfgAttr(bankCfgAttrSet);

		when(mockBankService.findByAcquirerAndValidate(anyInt())).thenReturn(bank);

		BaseBusinessException e = assertThrows(BaseBusinessException.class, () -> unit.process(creditNotificationRequest));
		assertEquals("13", e.getCode());
		assertEquals("Invalid Amount", e.getErrorMessage());

		verify(mockIdRegistryService, times(1)).saveIDRegistry(any());
		verify(mockAcquirerService, times(1)).getAcquirerSofDetails(any());
		verify(mockAcqMerAccountService, times(1)).getAcqMerAccountEntityByAcquirerIdAndEcMidAndSourceCurrency(anyInt(), any(), any());
		verify(mockPosService, times(1)).getPosInfoOnlyByPosId(anyInt());
		verify(mockBankService, times(1)).findByAcquirerAndValidate(anyInt());
	}
	
	@Test
	public void validateEmptyMerchantCfgAttrEntityList() throws BaseBusinessException {
		CreditNotificationRequest creditNotificationRequest = createCreditNotificationRequest();

		Map<ContextData, Object> entityMap = new HashMap<>();
		AcquirerEntity acquirer = new AcquirerEntity();
		acquirer.setStatus("2");
		acquirer.setAcquirerId(66);
		entityMap.put(ContextData.ACQUIRER, acquirer);

		SOFTypeEntity sofType = new SOFTypeEntity();
		sofType.setCardTypeId(57);
		entityMap.put(ContextData.SOF_TYPE, sofType);
		when(mockAcquirerService.getAcquirerSofDetails(any())).thenReturn(entityMap);

		AcqMerAccountEntity ama = new AcqMerAccountEntity();
		AcquirerMerchantMappingEntity amm = new AcquirerMerchantMappingEntity();
		amm.setStatus("2");
		amm.setMerMappingId(14);
		ama.setAcquirerMerchantMapping(amm);
		ama.setPosId(17);
		ama.setTid("UOPNKADE");
		ama.setAcqMerAccountId(17);

		when(mockAcqMerAccountService.getAcqMerAccountEntityByAcquirerIdAndEcMidAndSourceCurrency(anyInt(), any(), any())).thenReturn(ama);

		PosInfoOnly posInfo = mock(PosInfoOnly.class);

		when(mockPosService.getPosInfoOnlyByPosId(anyInt())).thenReturn(posInfo);
		
		Set<BankCfgAttrEntity> bankCfgAttrSet = new HashSet<>();
		BankCfgAttrEntity bankCfgAttr = new BankCfgAttrEntity();
		bankCfgAttr.setCfgAttrId(22);
		bankCfgAttr.setCfgAttrName(BankConfigAttributeName.PAYMENT_TYPE.name());
		bankCfgAttr.setCfgAttrValue("DBPN");
		bankCfgAttrSet.add(bankCfgAttr);

		BankEntity bank = new BankEntity();
		bank.setBankId(62);
		bank.setBankCfgAttr(bankCfgAttrSet);

		when(mockBankService.findByAcquirerAndValidate(anyInt())).thenReturn(bank);
		
		Timestamp bankSettleDate = new Timestamp(System.currentTimeMillis());
		when(mockBankAdapterProcessingFactory.getBankAdapter(any())).thenReturn(mockOcbcPaynowAdapter);
		when(mockOcbcPaynowAdapter.getSettlementDate()).thenReturn(bankSettleDate);
		
		PhysicalPosEntity pos = new PhysicalPosEntity();
		MerchantEntity merchant = new MerchantEntity();
		merchant.setMerchantId(11);
		merchant.setStatus("2");
		pos.setPosId(19860311);
		pos.setStatus("2");
		pos.setName("");
		pos.setRid("");
		pos.setMerchant(merchant);

		when(mockPosService.findByNetsTid(any())).thenReturn(pos);
		
		when(mockMerchantService.getMerchantCfgAttrsByMerchantIdAndAttributeName(anyInt(), any())).thenReturn(null);
		
		BaseBusinessException e = assertThrows(BaseBusinessException.class, () -> unit.process(creditNotificationRequest));
		assertEquals("80", e.getCode());
		assertEquals("Invalid Merchant Shortname.", e.getErrorMessage());
		
		verify(mockIdRegistryService, times(1)).saveIDRegistry(any());
		verify(mockAcquirerService, times(1)).getAcquirerSofDetails(any());
		verify(mockAcqMerAccountService, times(1)).getAcqMerAccountEntityByAcquirerIdAndEcMidAndSourceCurrency(anyInt(), any(), any());
		verify(mockPosService, times(1)).getPosInfoOnlyByPosId(anyInt());
		verify(mockBankService, times(1)).findByAcquirerAndValidate(anyInt());
		verify(mockPosService, times(1)).findByNetsTid(any());
		//verify(mockBankAdapterProcessingFactory, times(1)).getBankAdapter(any());
		verify(mockOcbcPaynowAdapter, times(1)).getSettlementDate();
	}

	@Test
	public void validateEmptyMerchantCfgAttrEntity() throws BaseBusinessException {
		CreditNotificationRequest creditNotificationRequest = createCreditNotificationRequest();

		Map<ContextData, Object> entityMap = new HashMap<>();
		AcquirerEntity acquirer = new AcquirerEntity();
		acquirer.setStatus("2");
		acquirer.setAcquirerId(66);
		entityMap.put(ContextData.ACQUIRER, acquirer);

		SOFTypeEntity sofType = new SOFTypeEntity();
		sofType.setCardTypeId(57);
		entityMap.put(ContextData.SOF_TYPE, sofType);
		when(mockAcquirerService.getAcquirerSofDetails(any())).thenReturn(entityMap);

		AcqMerAccountEntity ama = new AcqMerAccountEntity();
		AcquirerMerchantMappingEntity amm = new AcquirerMerchantMappingEntity();
		amm.setStatus("2");
		amm.setMerMappingId(14);
		ama.setAcquirerMerchantMapping(amm);
		ama.setPosId(17);
		ama.setTid("UOPNKADE");
		ama.setAcqMerAccountId(17);

		when(mockAcqMerAccountService.getAcqMerAccountEntityByAcquirerIdAndEcMidAndSourceCurrency(anyInt(), any(), any())).thenReturn(ama);

		PosInfoOnly posInfo = mock(PosInfoOnly.class);

		when(mockPosService.getPosInfoOnlyByPosId(anyInt())).thenReturn(posInfo);
		
		Set<BankCfgAttrEntity> bankCfgAttrSet = new HashSet<>();
		BankCfgAttrEntity bankCfgAttr = new BankCfgAttrEntity();
		bankCfgAttr.setCfgAttrId(22);
		bankCfgAttr.setCfgAttrName(BankConfigAttributeName.PAYMENT_TYPE.name());
		bankCfgAttr.setCfgAttrValue("DBPN");
		bankCfgAttrSet.add(bankCfgAttr);

		BankEntity bank = new BankEntity();
		bank.setBankId(62);
		bank.setBankCfgAttr(bankCfgAttrSet);

		when(mockBankService.findByAcquirerAndValidate(anyInt())).thenReturn(bank);
		
		Timestamp bankSettleDate = new Timestamp(System.currentTimeMillis());
		when(mockBankAdapterProcessingFactory.getBankAdapter(any())).thenReturn(mockOcbcPaynowAdapter);
		when(mockOcbcPaynowAdapter.getSettlementDate()).thenReturn(bankSettleDate);
		
		PhysicalPosEntity pos = new PhysicalPosEntity();
		MerchantEntity merchant = new MerchantEntity();
		merchant.setMerchantId(11);
		merchant.setStatus("2");
		pos.setPosId(19860311);
		pos.setStatus("2");
		pos.setName("");
		pos.setRid("");
		pos.setMerchant(merchant);

		when(mockPosService.findByNetsTid(any())).thenReturn(pos);
		
		MerchantCfgAttrEntity merchantCfgAttrEntity = new MerchantCfgAttrEntity();
		List<MerchantCfgAttrEntity> merchantCfgAttrEntityList = new ArrayList<>();
		merchantCfgAttrEntityList.add(merchantCfgAttrEntity);
		
		when(mockMerchantService.getMerchantCfgAttrsByMerchantIdAndAttributeName(anyInt(), any())).thenReturn(merchantCfgAttrEntityList);
		
		BaseBusinessException e = assertThrows(BaseBusinessException.class, () -> unit.process(creditNotificationRequest));
		assertEquals("80", e.getCode());
		assertEquals("Invalid Merchant Shortname.", e.getErrorMessage());
		
		verify(mockIdRegistryService, times(1)).saveIDRegistry(any());
		verify(mockAcquirerService, times(1)).getAcquirerSofDetails(any());
		verify(mockAcqMerAccountService, times(1)).getAcqMerAccountEntityByAcquirerIdAndEcMidAndSourceCurrency(anyInt(), any(), any());
		verify(mockPosService, times(1)).getPosInfoOnlyByPosId(anyInt());
		verify(mockBankService, times(1)).findByAcquirerAndValidate(anyInt());
		verify(mockPosService, times(1)).findByNetsTid(any());
		//verify(mockBankAdapterProcessingFactory, times(1)).getBankAdapter(any());
		verify(mockOcbcPaynowAdapter, times(1)).getSettlementDate();
	}
	
	@Test
	public void validateMerchantShortNameNotMatch() throws BaseBusinessException {
		CreditNotificationRequest creditNotificationRequest = createCreditNotificationRequest();

		Map<ContextData, Object> entityMap = new HashMap<>();
		AcquirerEntity acquirer = new AcquirerEntity();
		acquirer.setStatus("2");
		acquirer.setAcquirerId(66);
		entityMap.put(ContextData.ACQUIRER, acquirer);

		SOFTypeEntity sofType = new SOFTypeEntity();
		sofType.setCardTypeId(57);
		entityMap.put(ContextData.SOF_TYPE, sofType);
		when(mockAcquirerService.getAcquirerSofDetails(any())).thenReturn(entityMap);

		AcqMerAccountEntity ama = new AcqMerAccountEntity();
		AcquirerMerchantMappingEntity amm = new AcquirerMerchantMappingEntity();
		amm.setStatus("2");
		amm.setMerMappingId(14);
		ama.setAcquirerMerchantMapping(amm);
		ama.setPosId(17);
		ama.setTid("UOPNKADE");
		ama.setAcqMerAccountId(17);

		when(mockAcqMerAccountService.getAcqMerAccountEntityByAcquirerIdAndEcMidAndSourceCurrency(anyInt(), any(), any())).thenReturn(ama);

		PosInfoOnly posInfo = mock(PosInfoOnly.class);

		when(mockPosService.getPosInfoOnlyByPosId(anyInt())).thenReturn(posInfo);
		
		Set<BankCfgAttrEntity> bankCfgAttrSet = new HashSet<>();
		BankCfgAttrEntity bankCfgAttr = new BankCfgAttrEntity();
		bankCfgAttr.setCfgAttrId(22);
		bankCfgAttr.setCfgAttrName(BankConfigAttributeName.PAYMENT_TYPE.name());
		bankCfgAttr.setCfgAttrValue("DBPN");
		bankCfgAttrSet.add(bankCfgAttr);

		BankEntity bank = new BankEntity();
		bank.setBankId(62);
		bank.setBankCfgAttr(bankCfgAttrSet);

		when(mockBankService.findByAcquirerAndValidate(anyInt())).thenReturn(bank);
		
		Timestamp bankSettleDate = new Timestamp(System.currentTimeMillis());
		when(mockBankAdapterProcessingFactory.getBankAdapter(any())).thenReturn(mockOcbcPaynowAdapter);
		when(mockOcbcPaynowAdapter.getSettlementDate()).thenReturn(bankSettleDate);
		
		PhysicalPosEntity pos = new PhysicalPosEntity();
		MerchantEntity merchant = new MerchantEntity();
		merchant.setMerchantId(11);
		merchant.setStatus("2");
		pos.setPosId(19860311);
		pos.setStatus("2");
		pos.setName("");
		pos.setRid("");
		pos.setMerchant(merchant);

		when(mockPosService.findByNetsTid(any())).thenReturn(pos);
		
		MerchantCfgAttrEntity merchantCfgAttrEntity = new MerchantCfgAttrEntity();
		merchantCfgAttrEntity.setAttributeValue("ABCDEFGHG");
		List<MerchantCfgAttrEntity> merchantCfgAttrEntityList = new ArrayList<>();
		merchantCfgAttrEntityList.add(merchantCfgAttrEntity);
		
		when(mockMerchantService.getMerchantCfgAttrsByMerchantIdAndAttributeName(anyInt(), any())).thenReturn(merchantCfgAttrEntityList);
		
		BaseBusinessException e = assertThrows(BaseBusinessException.class, () -> unit.process(creditNotificationRequest));
		assertEquals("82", e.getCode());
		assertEquals("Merchant Reference Number Cannot Be Matched.", e.getErrorMessage());
		
		verify(mockIdRegistryService, times(1)).saveIDRegistry(any());
		verify(mockAcquirerService, times(1)).getAcquirerSofDetails(any());
		verify(mockAcqMerAccountService, times(1)).getAcqMerAccountEntityByAcquirerIdAndEcMidAndSourceCurrency(anyInt(), any(), any());
		verify(mockPosService, times(1)).getPosInfoOnlyByPosId(anyInt());
		verify(mockBankService, times(1)).findByAcquirerAndValidate(anyInt());
		verify(mockPosService, times(1)).findByNetsTid(any());
		//verify(mockBankAdapterProcessingFactory, times(1)).getBankAdapter(any());
		verify(mockOcbcPaynowAdapter, times(1)).getSettlementDate();
	}
	
	@Test
	public void validateBankConfigDontMatchBankConfigAttrNameEnum() throws BaseBusinessException, ParseException {
		CreditNotificationRequest creditNotificationRequest = createCreditNotificationRequest();

		Map<ContextData, Object> entityMap = new HashMap<>();
		AcquirerEntity acquirer = new AcquirerEntity();
		acquirer.setStatus("2");
		acquirer.setAcquirerId(66);
		entityMap.put(ContextData.ACQUIRER, acquirer);

		SOFTypeEntity sofType = new SOFTypeEntity();
		sofType.setCardTypeId(57);
		entityMap.put(ContextData.SOF_TYPE, sofType);
		when(mockAcquirerService.getAcquirerSofDetails(any())).thenReturn(entityMap);

		AcqMerAccountEntity ama = new AcqMerAccountEntity();
		AcquirerMerchantMappingEntity amm = new AcquirerMerchantMappingEntity();
		amm.setStatus("2");
		amm.setMerMappingId(14);
		ama.setAcquirerMerchantMapping(amm);
		ama.setPosId(17);
		ama.setTid("UOPNKADE");
		ama.setAcqMerAccountId(17);

		when(mockAcqMerAccountService.getAcqMerAccountEntityByAcquirerIdAndEcMidAndSourceCurrency(anyInt(), any(), any())).thenReturn(ama);

		PosInfoOnly posInfo = mock(PosInfoOnly.class);

		when(mockPosService.getPosInfoOnlyByPosId(anyInt())).thenReturn(posInfo);

		Set<BankCfgAttrEntity> bankCfgAttrSet = new HashSet<>();
		BankCfgAttrEntity bankCfgAttr = new BankCfgAttrEntity();
		bankCfgAttr.setCfgAttrId(22);
		bankCfgAttr.setCfgAttrName("ABCD");
		bankCfgAttr.setCfgAttrValue("DPNN");
		bankCfgAttrSet.add(bankCfgAttr);

		BankEntity bank = new BankEntity();
		bank.setBankId(62);
		bank.setBankCfgAttr(bankCfgAttrSet);

		when(mockBankService.findByAcquirerAndValidate(anyInt())).thenReturn(bank);

		Timestamp bankSettleDate = new Timestamp(System.currentTimeMillis());
		when(mockBankAdapterProcessingFactory.getBankAdapter(any())).thenReturn(mockOcbcPaynowAdapter);
		when(mockOcbcPaynowAdapter.getSettlementDate()).thenReturn(bankSettleDate);
		
		PhysicalPosEntity pos = new PhysicalPosEntity();
		MerchantEntity merchant = new MerchantEntity();
		merchant.setMerchantId(11);
		merchant.setStatus("2");
		pos.setPosId(19860311);
		pos.setStatus("2");
		pos.setName("");
		pos.setRid("");
		pos.setMerchant(merchant);

		when(mockPosService.findByNetsTid(any())).thenReturn(pos);

		MerchantCfgAttrEntity merchantCfgAttrEntity = new MerchantCfgAttrEntity();
		merchantCfgAttrEntity.setAttributeValue("DBSMerchant");
		List<MerchantCfgAttrEntity> merchantCfgAttrEntityList = new ArrayList<>();
		merchantCfgAttrEntityList.add(merchantCfgAttrEntity);
		
		when(mockMerchantService.getMerchantCfgAttrsByMerchantIdAndAttributeName(anyInt(), any())).thenReturn(merchantCfgAttrEntityList);
		
		SOFTypeEntity sofTypeEntity = new SOFTypeEntity();

		when(mockSofTypeService.getCardTypeById(anyInt())).thenReturn(sofTypeEntity);

		CodesEntity codesEntity = new CodesEntity();
		codesEntity.setIntegRefValue(14);

		when(mockCodesService.findByCodeValue(any())).thenReturn(codesEntity);

		BigDecimal mdrPercent = new BigDecimal(0.5);
		when(mockMdrService.getMdrPercent(pos.getMerchant(), sofTypeEntity)).thenReturn(mdrPercent);

		TransactionEntity savedTransaction = mock(TransactionEntity.class);
		when(mockTransactionService.save(any())).thenReturn(savedTransaction);

		StaticRequest savedStaticRequest = mock(StaticRequest.class);
		when(mockStaticRequestService.save(any())).thenReturn(savedStaticRequest);

		PosNotificationEntity posNotification = new PosNotificationEntity();
		List<PosNotificationEntity> posNotifList = new ArrayList<PosNotificationEntity>();
		posNotifList.add(posNotification);
		when(mockPosNotificationService.findByPosId(any())).thenReturn(posNotifList);

		CreditNotificationResponse response = unit.process(creditNotificationRequest);

		ArgumentCaptor<StaticRequest> argumentCaptorStaticRequest = ArgumentCaptor.forClass(StaticRequest.class);
		verify(mockStaticRequestService, times(1)).save(argumentCaptorStaticRequest.capture());
		StaticRequest staticRequest = argumentCaptorStaticRequest.getValue();

		CreditNotificationInfo creditNotificationInfo = new CreditNotificationInfo();
		creditNotificationInfo.setPaymentType("");

		assertEquals(creditNotificationInfo.getPaymentType(), staticRequest.getPaymentType());
		
		verify(mockIdRegistryService, times(1)).saveIDRegistry(any());
		verify(mockAcquirerService, times(1)).getAcquirerSofDetails(any());
		verify(mockAcqMerAccountService, times(1)).getAcqMerAccountEntityByAcquirerIdAndEcMidAndSourceCurrency(anyInt(), any(), any());
		verify(mockPosService, times(1)).getPosInfoOnlyByPosId(anyInt());
		verify(mockBankService, times(1)).findByAcquirerAndValidate(anyInt());
		verify(mockPosService, times(2)).findByNetsTid(any());
		//verify(mockBankAdapterProcessingFactory, times(1)).getBankAdapter(any());
		verify(mockOcbcPaynowAdapter, times(1)).getSettlementDate();
	}
	
	@Test
	public void validateAcquirerInactive() throws BaseBusinessException {
		CreditNotificationRequest creditNotificationRequest = createCreditNotificationRequest();

		Map<ContextData, Object> entityMap = new HashMap<>();
		AcquirerEntity acquirer = new AcquirerEntity();
		acquirer.setStatus("1");
		acquirer.setAcquirerId(66);
		entityMap.put(ContextData.ACQUIRER, acquirer);

		SOFTypeEntity sofType = new SOFTypeEntity();
		sofType.setCardTypeId(57);
		entityMap.put(ContextData.SOF_TYPE, sofType);
		when(mockAcquirerService.getAcquirerSofDetails(any())).thenReturn(entityMap);

		AcqMerAccountEntity ama = new AcqMerAccountEntity();
		AcquirerMerchantMappingEntity amm = new AcquirerMerchantMappingEntity();
		amm.setStatus("2");
		amm.setMerMappingId(14);
		ama.setAcquirerMerchantMapping(amm);
		ama.setPosId(17);
		ama.setTid("UOPNKADE");
		ama.setAcqMerAccountId(17);

		when(mockAcqMerAccountService.getAcqMerAccountEntityByAcquirerIdAndEcMidAndSourceCurrency(anyInt(), any(), any())).thenReturn(ama);

		PosInfoOnly posInfo = mock(PosInfoOnly.class);

		when(mockPosService.getPosInfoOnlyByPosId(anyInt())).thenReturn(posInfo);

		Set<BankCfgAttrEntity> bankCfgAttrSet = new HashSet<>();
		BankCfgAttrEntity bankCfgAttr = new BankCfgAttrEntity();
		bankCfgAttr.setCfgAttrId(22);
		bankCfgAttr.setCfgAttrName(BankConfigAttributeName.PAYMENT_TYPE.name());
		bankCfgAttr.setCfgAttrValue("DBPN");
		bankCfgAttrSet.add(bankCfgAttr);

		BankEntity bank = new BankEntity();
		bank.setBankId(62);
		bank.setBankCfgAttr(bankCfgAttrSet);

		when(mockBankService.findByAcquirerAndValidate(anyInt())).thenReturn(bank);

		PhysicalPosEntity pos = new PhysicalPosEntity();
		MerchantEntity merchant = new MerchantEntity();
		merchant.setMerchantId(11);
		merchant.setStatus("2");
		pos.setPosId(19860311);
		pos.setStatus("2");
		pos.setName("");
		pos.setRid("");
		pos.setMerchant(merchant);

		when(mockPosService.findByNetsTid(any())).thenReturn(pos);

		MerchantCfgAttrEntity merchantCfgAttrEntity = new MerchantCfgAttrEntity();
		merchantCfgAttrEntity.setAttributeValue("DBSMerchant");
		List<MerchantCfgAttrEntity> merchantCfgAttrEntityList = new ArrayList<>();
		merchantCfgAttrEntityList.add(merchantCfgAttrEntity);
		
		when(mockMerchantService.getMerchantCfgAttrsByMerchantIdAndAttributeName(anyInt(), any())).thenReturn(merchantCfgAttrEntityList);
		
		SOFTypeEntity sofTypeEntity = new SOFTypeEntity();

		when(mockSofTypeService.getCardTypeById(anyInt())).thenReturn(sofTypeEntity);

		CodesEntity codesEntity = new CodesEntity();
		codesEntity.setIntegRefValue(14);

		when(mockCodesService.findByCodeValue(any())).thenReturn(codesEntity);

		BigDecimal mdrPercent = new BigDecimal(0.5);
		when(mockMdrService.getMdrPercent(pos.getMerchant(), sofTypeEntity)).thenReturn(mdrPercent);

		TransactionEntity savedTransaction = new TransactionEntity();
		savedTransaction.setTransactionId(123456);
		when(mockTransactionService.save(any())).thenReturn(savedTransaction);

		StaticRequest savedStaticRequest = mock(StaticRequest.class);
		when(mockStaticRequestService.save(any())).thenReturn(savedStaticRequest);

		Timestamp bankSettleDate = new Timestamp(System.currentTimeMillis());
		when(mockBankAdapterProcessingFactory.getBankAdapter(any())).thenReturn(mockOcbcPaynowAdapter);
		when(mockOcbcPaynowAdapter.getSettlementDate()).thenReturn(bankSettleDate);
		
		when(mockTransactionService.retrieve(anyInt())).thenReturn(savedTransaction);
		when(mockTransactionService.save(any())).thenReturn(savedTransaction);
		
		BaseBusinessException e = assertThrows(BaseBusinessException.class, () -> unit.process(creditNotificationRequest));
		assertEquals("58", e.getCode());
		assertEquals("Acquirer disabled.", e.getErrorMessage());
		
		ArgumentCaptor<TransactionEntity> argumentCaptorTransaction = ArgumentCaptor.forClass(TransactionEntity.class);
		verify(mockTransactionService, times(2)).save(argumentCaptorTransaction.capture());
		List<TransactionEntity> transactionEntity = argumentCaptorTransaction.getAllValues();
		
		assertEquals(TransactionState.STATE_REVERSED.getState(), transactionEntity.get(1).getState());
		
		verify(mockIdRegistryService, times(1)).saveIDRegistry(any());
		verify(mockAcquirerService, times(1)).getAcquirerSofDetails(any());
		verify(mockAcqMerAccountService, times(1)).getAcqMerAccountEntityByAcquirerIdAndEcMidAndSourceCurrency(anyInt(), any(), any());
		verify(mockPosService, times(1)).getPosInfoOnlyByPosId(anyInt());
		verify(mockBankService, times(1)).findByAcquirerAndValidate(anyInt());
		verify(mockPosService, times(2)).findByNetsTid(any());
		verify(mockSofTypeService, times(1)).getCardTypeById(anyInt());
		verify(mockCodesService, times(1)).findByCodeValue(any());
		verify(mockMdrService, times(1)).getMdrPercent(merchant, sofTypeEntity);
		//verify(mockBankAdapterProcessingFactory, times(2)).getBankAdapter(any());
		verify(mockOcbcPaynowAdapter, times(1)).getSettlementDate();
		verify(mockStaticRequestService, times(1)).save(any());
		
	}
	
	@Test
	public void validateAcquirerDisabledForMerchant() throws BaseBusinessException {
		CreditNotificationRequest creditNotificationRequest = createCreditNotificationRequest();

		Map<ContextData, Object> entityMap = new HashMap<>();
		AcquirerEntity acquirer = new AcquirerEntity();
		acquirer.setStatus("2");
		acquirer.setAcquirerId(66);
		entityMap.put(ContextData.ACQUIRER, acquirer);

		SOFTypeEntity sofType = new SOFTypeEntity();
		sofType.setCardTypeId(57);
		entityMap.put(ContextData.SOF_TYPE, sofType);
		when(mockAcquirerService.getAcquirerSofDetails(any())).thenReturn(entityMap);

		AcqMerAccountEntity ama = new AcqMerAccountEntity();
		AcquirerMerchantMappingEntity amm = new AcquirerMerchantMappingEntity();
		amm.setStatus("1");
		amm.setMerMappingId(14);
		ama.setAcquirerMerchantMapping(amm);
		ama.setPosId(17);
		ama.setTid("UOPNKADE");
		ama.setAcqMerAccountId(17);

		when(mockAcqMerAccountService.getAcqMerAccountEntityByAcquirerIdAndEcMidAndSourceCurrency(anyInt(), any(), any())).thenReturn(ama);

		PosInfoOnly posInfo = mock(PosInfoOnly.class);

		when(mockPosService.getPosInfoOnlyByPosId(anyInt())).thenReturn(posInfo);

		Set<BankCfgAttrEntity> bankCfgAttrSet = new HashSet<>();
		BankCfgAttrEntity bankCfgAttr = new BankCfgAttrEntity();
		bankCfgAttr.setCfgAttrId(22);
		bankCfgAttr.setCfgAttrName(BankConfigAttributeName.PAYMENT_TYPE.name());
		bankCfgAttr.setCfgAttrValue("DBPN");
		bankCfgAttrSet.add(bankCfgAttr);

		BankEntity bank = new BankEntity();
		bank.setBankId(62);
		bank.setBankCfgAttr(bankCfgAttrSet);

		when(mockBankService.findByAcquirerAndValidate(anyInt())).thenReturn(bank);

		PhysicalPosEntity pos = new PhysicalPosEntity();
		MerchantEntity merchant = new MerchantEntity();
		merchant.setMerchantId(11);
		merchant.setStatus("2");
		pos.setPosId(19860311);
		pos.setStatus("2");
		pos.setName("");
		pos.setRid("");
		pos.setMerchant(merchant);

		when(mockPosService.findByNetsTid(any())).thenReturn(pos);
		
		MerchantCfgAttrEntity merchantCfgAttrEntity = new MerchantCfgAttrEntity();
		merchantCfgAttrEntity.setAttributeValue("DBSMerchant");
		List<MerchantCfgAttrEntity> merchantCfgAttrEntityList = new ArrayList<>();
		merchantCfgAttrEntityList.add(merchantCfgAttrEntity);
		
		when(mockMerchantService.getMerchantCfgAttrsByMerchantIdAndAttributeName(anyInt(), any())).thenReturn(merchantCfgAttrEntityList);
		SOFTypeEntity sofTypeEntity = new SOFTypeEntity();

		when(mockSofTypeService.getCardTypeById(anyInt())).thenReturn(sofTypeEntity);

		CodesEntity codesEntity = new CodesEntity();
		codesEntity.setIntegRefValue(14);

		when(mockCodesService.findByCodeValue(any())).thenReturn(codesEntity);

		BigDecimal mdrPercent = new BigDecimal(0.5);
		when(mockMdrService.getMdrPercent(pos.getMerchant(), sofTypeEntity)).thenReturn(mdrPercent);

		TransactionEntity savedTransaction = new TransactionEntity();
		savedTransaction.setTransactionId(123456);
		when(mockTransactionService.save(any())).thenReturn(savedTransaction);

		StaticRequest savedStaticRequest = mock(StaticRequest.class);
		when(mockStaticRequestService.save(any())).thenReturn(savedStaticRequest);

		Timestamp bankSettleDate = new Timestamp(System.currentTimeMillis());
		when(mockBankAdapterProcessingFactory.getBankAdapter(any())).thenReturn(mockOcbcPaynowAdapter);
		when(mockOcbcPaynowAdapter.getSettlementDate()).thenReturn(bankSettleDate);
		
		when(mockTransactionService.retrieve(anyInt())).thenReturn(savedTransaction);
		when(mockTransactionService.save(any())).thenReturn(savedTransaction);
		
		BaseBusinessException e = assertThrows(BaseBusinessException.class, () -> unit.process(creditNotificationRequest));
		assertEquals("58", e.getCode());
		assertEquals("SOF is disabled for the merchant.", e.getErrorMessage());
		
		ArgumentCaptor<TransactionEntity> argumentCaptorTransaction = ArgumentCaptor.forClass(TransactionEntity.class);
		verify(mockTransactionService, times(2)).save(argumentCaptorTransaction.capture());
		List<TransactionEntity> transactionEntity = argumentCaptorTransaction.getAllValues();
		
		assertEquals(TransactionState.STATE_REVERSED.getState(), transactionEntity.get(1).getState());
		
		verify(mockIdRegistryService, times(1)).saveIDRegistry(any());
		verify(mockAcquirerService, times(1)).getAcquirerSofDetails(any());
		verify(mockAcqMerAccountService, times(1)).getAcqMerAccountEntityByAcquirerIdAndEcMidAndSourceCurrency(anyInt(), any(), any());
		verify(mockPosService, times(1)).getPosInfoOnlyByPosId(anyInt());
		verify(mockBankService, times(1)).findByAcquirerAndValidate(anyInt());
		verify(mockPosService, times(2)).findByNetsTid(any());
		verify(mockSofTypeService, times(1)).getCardTypeById(anyInt());
		verify(mockCodesService, times(1)).findByCodeValue(any());
		verify(mockMdrService, times(1)).getMdrPercent(merchant, sofTypeEntity);
		//verify(mockBankAdapterProcessingFactory, times(2)).getBankAdapter(any());
		verify(mockOcbcPaynowAdapter, times(1)).getSettlementDate();
		verify(mockStaticRequestService, times(1)).save(any());
	}
	
	@Test
	public void validatePosInactive() throws BaseBusinessException {
		CreditNotificationRequest creditNotificationRequest = createCreditNotificationRequest();

		Map<ContextData, Object> entityMap = new HashMap<>();
		AcquirerEntity acquirer = new AcquirerEntity();
		acquirer.setStatus("2");
		acquirer.setAcquirerId(66);
		entityMap.put(ContextData.ACQUIRER, acquirer);

		SOFTypeEntity sofType = new SOFTypeEntity();
		sofType.setCardTypeId(57);
		entityMap.put(ContextData.SOF_TYPE, sofType);
		when(mockAcquirerService.getAcquirerSofDetails(any())).thenReturn(entityMap);

		AcqMerAccountEntity ama = new AcqMerAccountEntity();
		AcquirerMerchantMappingEntity amm = new AcquirerMerchantMappingEntity();
		amm.setStatus("2");
		amm.setMerMappingId(14);
		ama.setAcquirerMerchantMapping(amm);
		ama.setPosId(17);
		ama.setTid("UOPNKADE");
		ama.setAcqMerAccountId(17);

		when(mockAcqMerAccountService.getAcqMerAccountEntityByAcquirerIdAndEcMidAndSourceCurrency(anyInt(), any(), any())).thenReturn(ama);

		PosInfoOnly posInfo = mock(PosInfoOnly.class);

		when(mockPosService.getPosInfoOnlyByPosId(anyInt())).thenReturn(posInfo);

		Set<BankCfgAttrEntity> bankCfgAttrSet = new HashSet<>();
		BankCfgAttrEntity bankCfgAttr = new BankCfgAttrEntity();
		bankCfgAttr.setCfgAttrId(22);
		bankCfgAttr.setCfgAttrName(BankConfigAttributeName.PAYMENT_TYPE.name());
		bankCfgAttr.setCfgAttrValue("DBPN");
		bankCfgAttrSet.add(bankCfgAttr);

		BankEntity bank = new BankEntity();
		bank.setBankId(62);
		bank.setBankCfgAttr(bankCfgAttrSet);

		when(mockBankService.findByAcquirerAndValidate(anyInt())).thenReturn(bank);

		PhysicalPosEntity pos = new PhysicalPosEntity();
		MerchantEntity merchant = new MerchantEntity();
		merchant.setMerchantId(11);
		merchant.setStatus("2");
		pos.setPosId(19860311);
		pos.setStatus("1");
		pos.setName("");
		pos.setRid("");
		pos.setMerchant(merchant);

		when(mockPosService.findByNetsTid(any())).thenReturn(pos);

		MerchantCfgAttrEntity merchantCfgAttrEntity = new MerchantCfgAttrEntity();
		merchantCfgAttrEntity.setAttributeValue("DBSMerchant");
		List<MerchantCfgAttrEntity> merchantCfgAttrEntityList = new ArrayList<>();
		merchantCfgAttrEntityList.add(merchantCfgAttrEntity);
		
		when(mockMerchantService.getMerchantCfgAttrsByMerchantIdAndAttributeName(anyInt(), any())).thenReturn(merchantCfgAttrEntityList);
		
		SOFTypeEntity sofTypeEntity = new SOFTypeEntity();

		when(mockSofTypeService.getCardTypeById(anyInt())).thenReturn(sofTypeEntity);

		CodesEntity codesEntity = new CodesEntity();
		codesEntity.setIntegRefValue(14);

		when(mockCodesService.findByCodeValue(any())).thenReturn(codesEntity);

		BigDecimal mdrPercent = new BigDecimal(0.5);
		when(mockMdrService.getMdrPercent(pos.getMerchant(), sofTypeEntity)).thenReturn(mdrPercent);

		TransactionEntity savedTransaction = new TransactionEntity();
		savedTransaction.setTransactionId(123456);
		when(mockTransactionService.save(any())).thenReturn(savedTransaction);

		StaticRequest savedStaticRequest = mock(StaticRequest.class);
		when(mockStaticRequestService.save(any())).thenReturn(savedStaticRequest);

		Timestamp bankSettleDate = new Timestamp(System.currentTimeMillis());
		when(mockBankAdapterProcessingFactory.getBankAdapter(any())).thenReturn(mockOcbcPaynowAdapter);
		when(mockOcbcPaynowAdapter.getSettlementDate()).thenReturn(bankSettleDate);
		
		when(mockTransactionService.retrieve(anyInt())).thenReturn(savedTransaction);
		when(mockTransactionService.save(any())).thenReturn(savedTransaction);
		
		BaseBusinessException e = assertThrows(BaseBusinessException.class, () -> unit.process(creditNotificationRequest));
		assertEquals("58", e.getCode());
		assertEquals("POS is not active.", e.getErrorMessage());
		
		ArgumentCaptor<TransactionEntity> argumentCaptorTransaction = ArgumentCaptor.forClass(TransactionEntity.class);
		verify(mockTransactionService, times(2)).save(argumentCaptorTransaction.capture());
		List<TransactionEntity> transactionEntity = argumentCaptorTransaction.getAllValues();
		
		assertEquals(TransactionState.STATE_REVERSED.getState(), transactionEntity.get(1).getState());
		
		verify(mockIdRegistryService, times(1)).saveIDRegistry(any());
		verify(mockAcquirerService, times(1)).getAcquirerSofDetails(any());
		verify(mockAcqMerAccountService, times(1)).getAcqMerAccountEntityByAcquirerIdAndEcMidAndSourceCurrency(anyInt(), any(), any());
		verify(mockPosService, times(1)).getPosInfoOnlyByPosId(anyInt());
		verify(mockBankService, times(1)).findByAcquirerAndValidate(anyInt());
		verify(mockPosService, times(2)).findByNetsTid(any());
		verify(mockSofTypeService, times(1)).getCardTypeById(anyInt());
		verify(mockCodesService, times(1)).findByCodeValue(any());
		verify(mockMdrService, times(1)).getMdrPercent(merchant, sofTypeEntity);
		//verify(mockBankAdapterProcessingFactory, times(2)).getBankAdapter(any());
		verify(mockOcbcPaynowAdapter, times(1)).getSettlementDate();
		verify(mockStaticRequestService, times(1)).save(any());
	}

	@Test
	public void validateMerchantInactive() throws BaseBusinessException {
		CreditNotificationRequest creditNotificationRequest = createCreditNotificationRequest();

		Map<ContextData, Object> entityMap = new HashMap<>();
		AcquirerEntity acquirer = new AcquirerEntity();
		acquirer.setStatus("2");
		acquirer.setAcquirerId(66);
		entityMap.put(ContextData.ACQUIRER, acquirer);

		SOFTypeEntity sofType = new SOFTypeEntity();
		sofType.setCardTypeId(57);
		entityMap.put(ContextData.SOF_TYPE, sofType);
		when(mockAcquirerService.getAcquirerSofDetails(any())).thenReturn(entityMap);

		AcqMerAccountEntity ama = new AcqMerAccountEntity();
		AcquirerMerchantMappingEntity amm = new AcquirerMerchantMappingEntity();
		amm.setStatus("2");
		amm.setMerMappingId(14);
		ama.setAcquirerMerchantMapping(amm);
		ama.setPosId(17);
		ama.setTid("UOPNKADE");
		ama.setAcqMerAccountId(17);

		when(mockAcqMerAccountService.getAcqMerAccountEntityByAcquirerIdAndEcMidAndSourceCurrency(anyInt(), any(), any())).thenReturn(ama);

		PosInfoOnly posInfo = mock(PosInfoOnly.class);

		when(mockPosService.getPosInfoOnlyByPosId(anyInt())).thenReturn(posInfo);

		Set<BankCfgAttrEntity> bankCfgAttrSet = new HashSet<>();
		BankCfgAttrEntity bankCfgAttr = new BankCfgAttrEntity();
		bankCfgAttr.setCfgAttrId(22);
		bankCfgAttr.setCfgAttrName(BankConfigAttributeName.PAYMENT_TYPE.name());
		bankCfgAttr.setCfgAttrValue("DBPN");
		bankCfgAttrSet.add(bankCfgAttr);

		BankEntity bank = new BankEntity();
		bank.setBankId(62);
		bank.setBankCfgAttr(bankCfgAttrSet);

		when(mockBankService.findByAcquirerAndValidate(anyInt())).thenReturn(bank);

		PhysicalPosEntity pos = new PhysicalPosEntity();
		MerchantEntity merchant = new MerchantEntity();
		merchant.setMerchantId(11);
		merchant.setStatus("1");
		pos.setPosId(19860311);
		pos.setStatus("2");
		pos.setName("");
		pos.setRid("");
		pos.setMerchant(merchant);

		when(mockPosService.findByNetsTid(any())).thenReturn(pos);

		MerchantCfgAttrEntity merchantCfgAttrEntity = new MerchantCfgAttrEntity();
		merchantCfgAttrEntity.setAttributeValue("DBSMerchant");
		List<MerchantCfgAttrEntity> merchantCfgAttrEntityList = new ArrayList<>();
		merchantCfgAttrEntityList.add(merchantCfgAttrEntity);
		
		when(mockMerchantService.getMerchantCfgAttrsByMerchantIdAndAttributeName(anyInt(), any())).thenReturn(merchantCfgAttrEntityList);
		
		SOFTypeEntity sofTypeEntity = new SOFTypeEntity();

		when(mockSofTypeService.getCardTypeById(anyInt())).thenReturn(sofTypeEntity);

		CodesEntity codesEntity = new CodesEntity();
		codesEntity.setIntegRefValue(14);

		when(mockCodesService.findByCodeValue(any())).thenReturn(codesEntity);

		BigDecimal mdrPercent = new BigDecimal(0.5);
		when(mockMdrService.getMdrPercent(pos.getMerchant(), sofTypeEntity)).thenReturn(mdrPercent);

		TransactionEntity savedTransaction = new TransactionEntity();
		savedTransaction.setTransactionId(123456);
		when(mockTransactionService.save(any())).thenReturn(savedTransaction);

		StaticRequest savedStaticRequest = mock(StaticRequest.class);
		when(mockStaticRequestService.save(any())).thenReturn(savedStaticRequest);

		Timestamp bankSettleDate = new Timestamp(System.currentTimeMillis());
		when(mockBankAdapterProcessingFactory.getBankAdapter(any())).thenReturn(mockOcbcPaynowAdapter);
		when(mockOcbcPaynowAdapter.getSettlementDate()).thenReturn(bankSettleDate);
		
		when(mockTransactionService.retrieve(anyInt())).thenReturn(savedTransaction);
		when(mockTransactionService.save(any())).thenReturn(savedTransaction);
		
		BaseBusinessException e = assertThrows(BaseBusinessException.class, () -> unit.process(creditNotificationRequest));
		assertEquals("58", e.getCode());
		assertEquals("Merchant is not active.", e.getErrorMessage());
		
		ArgumentCaptor<TransactionEntity> argumentCaptorTransaction = ArgumentCaptor.forClass(TransactionEntity.class);
		verify(mockTransactionService, times(2)).save(argumentCaptorTransaction.capture());
		List<TransactionEntity> transactionEntity = argumentCaptorTransaction.getAllValues();
		
		assertEquals(TransactionState.STATE_REVERSED.getState(), transactionEntity.get(1).getState());
		
		verify(mockIdRegistryService, times(1)).saveIDRegistry(any());
		verify(mockAcquirerService, times(1)).getAcquirerSofDetails(any());
		verify(mockAcqMerAccountService, times(1)).getAcqMerAccountEntityByAcquirerIdAndEcMidAndSourceCurrency(anyInt(), any(), any());
		verify(mockPosService, times(1)).getPosInfoOnlyByPosId(anyInt());
		verify(mockBankService, times(1)).findByAcquirerAndValidate(anyInt());
		verify(mockPosService, times(2)).findByNetsTid(any());
		verify(mockSofTypeService, times(1)).getCardTypeById(anyInt());
		verify(mockCodesService, times(1)).findByCodeValue(any());
		verify(mockMdrService, times(1)).getMdrPercent(merchant, sofTypeEntity);
		//verify(mockBankAdapterProcessingFactory, times(2)).getBankAdapter(any());
		verify(mockOcbcPaynowAdapter, times(1)).getSettlementDate();
		verify(mockStaticRequestService, times(1)).save(any());
	}
	
	@Test
	public void validateTimeout() throws BaseBusinessException {
		CreditNotificationRequest creditNotificationRequest = createCreditNotificationRequest();
		PaymentTransactionDomainData transactionDomainData = new PaymentTransactionDomainData();
		transactionDomainData.setCreditAmount("000000000469");
		transactionDomainData.setCreditCurrency("SGD");
		transactionDomainData.setCreditingTime("0527235831");
		transactionDomainData.setReceivingAccountNumber("12345");
		transactionDomainData.setReceivingAccountType("OWN");
		transactionDomainData.setMerchantReferenceNumber("1986031100000001 DBSMerch");
		transactionDomainData.setSendingAccountBank(null);
		transactionDomainData.setSendingAccountNumber(null);
		transactionDomainData.setSendingAccountName(null);
		transactionDomainData.setAdditionalBankReference("abcdefghij");
		creditNotificationRequest.setTransactionDomainData(transactionDomainData);
		
		Map<ContextData, Object> entityMap = new HashMap<>();
		AcquirerEntity acquirer = new AcquirerEntity();
		acquirer.setStatus("2");
		acquirer.setAcquirerId(66);
		entityMap.put(ContextData.ACQUIRER, acquirer);

		SOFTypeEntity sofType = new SOFTypeEntity();
		sofType.setCardTypeId(57);
		entityMap.put(ContextData.SOF_TYPE, sofType);
		when(mockAcquirerService.getAcquirerSofDetails(any())).thenReturn(entityMap);

		AcqMerAccountEntity ama = new AcqMerAccountEntity();
		AcquirerMerchantMappingEntity amm = new AcquirerMerchantMappingEntity();
		amm.setStatus("2");
		amm.setMerMappingId(14);
		ama.setAcquirerMerchantMapping(amm);
		ama.setPosId(17);
		ama.setTid("UOPNKADE");
		ama.setAcqMerAccountId(17);

		when(mockAcqMerAccountService.getAcqMerAccountEntityByAcquirerIdAndEcMidAndSourceCurrency(anyInt(), any(), any())).thenReturn(ama);

		PosInfoOnly posInfo = mock(PosInfoOnly.class);

		when(mockPosService.getPosInfoOnlyByPosId(anyInt())).thenReturn(posInfo);

		Set<BankCfgAttrEntity> bankCfgAttrSet = new HashSet<>();
		BankCfgAttrEntity bankCfgAttr = new BankCfgAttrEntity();
		bankCfgAttr.setCfgAttrId(22);
		bankCfgAttr.setCfgAttrName(BankConfigAttributeName.PAYMENT_TYPE.name());
		bankCfgAttr.setCfgAttrValue("DBPN");
		bankCfgAttrSet.add(bankCfgAttr);

		BankEntity bank = new BankEntity();
		bank.setBankId(62);
		bank.setBankCfgAttr(bankCfgAttrSet);

		when(mockBankService.findByAcquirerAndValidate(anyInt())).thenReturn(bank);

		PhysicalPosEntity pos = new PhysicalPosEntity();
		MerchantEntity merchant = new MerchantEntity();
		merchant.setMerchantId(11);
		merchant.setStatus("2");
		pos.setPosId(19860311);
		pos.setStatus("2");
		pos.setName("");
		pos.setRid("");
		pos.setMerchant(merchant);

		when(mockPosService.findByNetsTid(any())).thenReturn(pos);

		MerchantCfgAttrEntity merchantCfgAttrEntity = new MerchantCfgAttrEntity();
		merchantCfgAttrEntity.setAttributeValue("DBSMerchant");
		List<MerchantCfgAttrEntity> merchantCfgAttrEntityList = new ArrayList<>();
		merchantCfgAttrEntityList.add(merchantCfgAttrEntity);
		
		when(mockMerchantService.getMerchantCfgAttrsByMerchantIdAndAttributeName(anyInt(), any())).thenReturn(merchantCfgAttrEntityList);
		
		SOFTypeEntity sofTypeEntity = new SOFTypeEntity();

		when(mockSofTypeService.getCardTypeById(anyInt())).thenReturn(sofTypeEntity);

		CodesEntity codesEntity = new CodesEntity();
		codesEntity.setIntegRefValue(14);

		when(mockCodesService.findByCodeValue(any())).thenReturn(codesEntity);

		BigDecimal mdrPercent = new BigDecimal(0.5);
		when(mockMdrService.getMdrPercent(pos.getMerchant(), sofTypeEntity)).thenReturn(mdrPercent);

		TransactionEntity savedTransaction = new TransactionEntity();
		savedTransaction.setTransactionId(123456);
		when(mockTransactionService.save(any())).thenReturn(savedTransaction);

		StaticRequest savedStaticRequest = mock(StaticRequest.class);
		when(mockStaticRequestService.save(any())).thenReturn(savedStaticRequest);

		Timestamp bankSettleDate = new Timestamp(System.currentTimeMillis());
		when(mockBankAdapterProcessingFactory.getBankAdapter(any())).thenReturn(mockOcbcPaynowAdapter);
		when(mockOcbcPaynowAdapter.getSettlementDate()).thenReturn(bankSettleDate);
		
		when(mockTransactionService.retrieve(anyInt())).thenReturn(savedTransaction);
		when(mockTransactionService.save(any())).thenReturn(savedTransaction);
		
		BaseBusinessException e = assertThrows(BaseBusinessException.class, () -> unit.process(creditNotificationRequest));
		assertEquals("68", e.getCode());
		assertEquals("Credit Notification Timeout.", e.getErrorMessage());
		
		ArgumentCaptor<TransactionEntity> argumentCaptorTransaction = ArgumentCaptor.forClass(TransactionEntity.class);
		verify(mockTransactionService, times(2)).save(argumentCaptorTransaction.capture());
		List<TransactionEntity> transactionEntity = argumentCaptorTransaction.getAllValues();
		
		assertEquals(TransactionState.STATE_REVERSED.getState(), transactionEntity.get(1).getState());
		
		verify(mockIdRegistryService, times(1)).saveIDRegistry(any());
		verify(mockAcquirerService, times(1)).getAcquirerSofDetails(any());
		verify(mockAcqMerAccountService, times(1)).getAcqMerAccountEntityByAcquirerIdAndEcMidAndSourceCurrency(anyInt(), any(), any());
		verify(mockPosService, times(1)).getPosInfoOnlyByPosId(anyInt());
		verify(mockBankService, times(1)).findByAcquirerAndValidate(anyInt());
		verify(mockPosService, times(2)).findByNetsTid(any());
		verify(mockSofTypeService, times(1)).getCardTypeById(anyInt());
		verify(mockCodesService, times(1)).findByCodeValue(any());
		verify(mockMdrService, times(1)).getMdrPercent(merchant, sofTypeEntity);
		//verify(mockBankAdapterProcessingFactory, times(2)).getBankAdapter(any());
		verify(mockOcbcPaynowAdapter, times(1)).getSettlementDate();
		verify(mockStaticRequestService, times(1)).save(any());
	}
	
	@Test
	public void validateReverseTransactionCatchExceptionWithMessage() throws BaseBusinessException {
		CreditNotificationRequest creditNotificationRequest = createCreditNotificationRequest();
		PaymentTransactionDomainData transactionDomainData = new PaymentTransactionDomainData();
		transactionDomainData.setCreditAmount("000000000469");
		transactionDomainData.setCreditCurrency("SGD");
		transactionDomainData.setCreditingTime("0527235831");
		transactionDomainData.setReceivingAccountNumber("12345");
		transactionDomainData.setReceivingAccountType("OWN");
		transactionDomainData.setMerchantReferenceNumber("1986031100000001 DBSMerch");
		transactionDomainData.setSendingAccountBank(null);
		transactionDomainData.setSendingAccountNumber(null);
		transactionDomainData.setSendingAccountName(null);
		transactionDomainData.setAdditionalBankReference("abcdefghij");
		creditNotificationRequest.setTransactionDomainData(transactionDomainData);
		
		Map<ContextData, Object> entityMap = new HashMap<>();
		AcquirerEntity acquirer = new AcquirerEntity();
		acquirer.setStatus("2");
		acquirer.setAcquirerId(66);
		entityMap.put(ContextData.ACQUIRER, acquirer);

		SOFTypeEntity sofType = new SOFTypeEntity();
		sofType.setCardTypeId(57);
		entityMap.put(ContextData.SOF_TYPE, sofType);
		when(mockAcquirerService.getAcquirerSofDetails(any())).thenReturn(entityMap);

		AcqMerAccountEntity ama = new AcqMerAccountEntity();
		AcquirerMerchantMappingEntity amm = new AcquirerMerchantMappingEntity();
		amm.setStatus("2");
		amm.setMerMappingId(14);
		ama.setAcquirerMerchantMapping(amm);
		ama.setPosId(17);
		ama.setTid("UOPNKADE");
		ama.setAcqMerAccountId(17);

		when(mockAcqMerAccountService.getAcqMerAccountEntityByAcquirerIdAndEcMidAndSourceCurrency(anyInt(), any(), any())).thenReturn(ama);

		PosInfoOnly posInfo = mock(PosInfoOnly.class);

		when(mockPosService.getPosInfoOnlyByPosId(anyInt())).thenReturn(posInfo);

		Set<BankCfgAttrEntity> bankCfgAttrSet = new HashSet<>();
		BankCfgAttrEntity bankCfgAttr = new BankCfgAttrEntity();
		bankCfgAttr.setCfgAttrId(22);
		bankCfgAttr.setCfgAttrName(BankConfigAttributeName.PAYMENT_TYPE.name());
		bankCfgAttr.setCfgAttrValue("DBPN");
		bankCfgAttrSet.add(bankCfgAttr);

		BankEntity bank = new BankEntity();
		bank.setBankId(62);
		bank.setBankCfgAttr(bankCfgAttrSet);

		when(mockBankService.findByAcquirerAndValidate(anyInt())).thenReturn(bank);

		PhysicalPosEntity pos = new PhysicalPosEntity();
		MerchantEntity merchant = new MerchantEntity();
		merchant.setMerchantId(11);
		merchant.setStatus("2");
		pos.setPosId(19860311);
		pos.setStatus("2");
		pos.setName("");
		pos.setRid("");
		pos.setMerchant(merchant);

		when(mockPosService.findByNetsTid(any())).thenReturn(pos);

		MerchantCfgAttrEntity merchantCfgAttrEntity = new MerchantCfgAttrEntity();
		merchantCfgAttrEntity.setAttributeValue("DBSMerchant");
		List<MerchantCfgAttrEntity> merchantCfgAttrEntityList = new ArrayList<>();
		merchantCfgAttrEntityList.add(merchantCfgAttrEntity);
		
		when(mockMerchantService.getMerchantCfgAttrsByMerchantIdAndAttributeName(anyInt(), any())).thenReturn(merchantCfgAttrEntityList);
		
		SOFTypeEntity sofTypeEntity = new SOFTypeEntity();

		when(mockSofTypeService.getCardTypeById(anyInt())).thenReturn(sofTypeEntity);

		CodesEntity codesEntity = new CodesEntity();
		codesEntity.setIntegRefValue(14);

		when(mockCodesService.findByCodeValue(any())).thenReturn(codesEntity);

		BigDecimal mdrPercent = new BigDecimal(0.5);
		when(mockMdrService.getMdrPercent(pos.getMerchant(), sofTypeEntity)).thenReturn(mdrPercent);

		TransactionEntity savedTransaction = new TransactionEntity();
		savedTransaction.setTransactionId(123456);
		when(mockTransactionService.save(any())).thenReturn(savedTransaction);

		StaticRequest savedStaticRequest = mock(StaticRequest.class);
		when(mockStaticRequestService.save(any())).thenReturn(savedStaticRequest);

		Timestamp bankSettleDate = new Timestamp(System.currentTimeMillis());
		when(mockBankAdapterProcessingFactory.getBankAdapter(any())).thenReturn(mockOcbcPaynowAdapter);
		when(mockOcbcPaynowAdapter.getSettlementDate()).thenReturn(bankSettleDate);
		
		when(mockTransactionService.retrieve(anyInt())).thenReturn(savedTransaction);
		when(mockTransactionService.save(any())).thenReturn(savedTransaction);
		
		when(mockBankAdapterProcessingFactory.getBankAdapter(any())).thenReturn(mockOcbcPaynowAdapter);
		doThrow(new BaseBusinessException("06", "Static QR Exception Reversal Failed")).when(mockOcbcPaynowAdapter).processRefundReversalDuetoException(creditNotificationRequest);
		
		BaseBusinessException e = assertThrows(BaseBusinessException.class, () -> unit.process(creditNotificationRequest));
		
		ArgumentCaptor<TransactionEntity> argumentCaptorTransaction = ArgumentCaptor.forClass(TransactionEntity.class);
		verify(mockTransactionService, times(2)).save(argumentCaptorTransaction.capture());
		List<TransactionEntity> transactionEntity = argumentCaptorTransaction.getAllValues();
		
		assertEquals(TransactionState.STATE_REVERSED.getState(), transactionEntity.get(1).getState());
		
		verify(mockIdRegistryService, times(1)).saveIDRegistry(any());
		verify(mockAcquirerService, times(1)).getAcquirerSofDetails(any());
		verify(mockAcqMerAccountService, times(1)).getAcqMerAccountEntityByAcquirerIdAndEcMidAndSourceCurrency(anyInt(), any(), any());
		verify(mockPosService, times(1)).getPosInfoOnlyByPosId(anyInt());
		verify(mockBankService, times(1)).findByAcquirerAndValidate(anyInt());
		verify(mockPosService, times(2)).findByNetsTid(any());
		verify(mockSofTypeService, times(1)).getCardTypeById(anyInt());
		verify(mockCodesService, times(1)).findByCodeValue(any());
		verify(mockMdrService, times(1)).getMdrPercent(merchant, sofTypeEntity);
		//verify(mockBankAdapterProcessingFactory, times(2)).getBankAdapter(any());
		verify(mockOcbcPaynowAdapter, times(1)).getSettlementDate();
		verify(mockStaticRequestService, times(1)).save(any());
	}
	
	@Test
	public void validateReverseTransactionCatchExceptionWithoutMessage() throws BaseBusinessException {
		CreditNotificationRequest creditNotificationRequest = createCreditNotificationRequest();
		PaymentTransactionDomainData transactionDomainData = new PaymentTransactionDomainData();
		transactionDomainData.setCreditAmount("000000000469");
		transactionDomainData.setCreditCurrency("SGD");
		transactionDomainData.setCreditingTime("0527235831");
		transactionDomainData.setReceivingAccountNumber("12345");
		transactionDomainData.setReceivingAccountType("OWN");
		transactionDomainData.setMerchantReferenceNumber("1986031100000001 DBSMerch");
		transactionDomainData.setSendingAccountBank(null);
		transactionDomainData.setSendingAccountNumber(null);
		transactionDomainData.setSendingAccountName(null);
		transactionDomainData.setAdditionalBankReference("abcdefghij");
		creditNotificationRequest.setTransactionDomainData(transactionDomainData);
		
		Map<ContextData, Object> entityMap = new HashMap<>();
		AcquirerEntity acquirer = new AcquirerEntity();
		acquirer.setStatus("2");
		acquirer.setAcquirerId(66);
		entityMap.put(ContextData.ACQUIRER, acquirer);

		SOFTypeEntity sofType = new SOFTypeEntity();
		sofType.setCardTypeId(57);
		entityMap.put(ContextData.SOF_TYPE, sofType);
		when(mockAcquirerService.getAcquirerSofDetails(any())).thenReturn(entityMap);

		AcqMerAccountEntity ama = new AcqMerAccountEntity();
		AcquirerMerchantMappingEntity amm = new AcquirerMerchantMappingEntity();
		amm.setStatus("2");
		amm.setMerMappingId(14);
		ama.setAcquirerMerchantMapping(amm);
		ama.setPosId(17);
		ama.setTid("UOPNKADE");
		ama.setAcqMerAccountId(17);

		when(mockAcqMerAccountService.getAcqMerAccountEntityByAcquirerIdAndEcMidAndSourceCurrency(anyInt(), any(), any())).thenReturn(ama);

		PosInfoOnly posInfo = mock(PosInfoOnly.class);

		when(mockPosService.getPosInfoOnlyByPosId(anyInt())).thenReturn(posInfo);

		Set<BankCfgAttrEntity> bankCfgAttrSet = new HashSet<>();
		BankCfgAttrEntity bankCfgAttr = new BankCfgAttrEntity();
		bankCfgAttr.setCfgAttrId(22);
		bankCfgAttr.setCfgAttrName(BankConfigAttributeName.PAYMENT_TYPE.name());
		bankCfgAttr.setCfgAttrValue("DBPN");
		bankCfgAttrSet.add(bankCfgAttr);

		BankEntity bank = new BankEntity();
		bank.setBankId(62);
		bank.setBankCfgAttr(bankCfgAttrSet);

		when(mockBankService.findByAcquirerAndValidate(anyInt())).thenReturn(bank);

		PhysicalPosEntity pos = new PhysicalPosEntity();
		MerchantEntity merchant = new MerchantEntity();
		merchant.setMerchantId(11);
		merchant.setStatus("2");
		pos.setPosId(19860311);
		pos.setStatus("2");
		pos.setName("");
		pos.setRid("");
		pos.setMerchant(merchant);

		when(mockPosService.findByNetsTid(any())).thenReturn(pos);

		MerchantCfgAttrEntity merchantCfgAttrEntity = new MerchantCfgAttrEntity();
		merchantCfgAttrEntity.setAttributeValue("DBSMerchant");
		List<MerchantCfgAttrEntity> merchantCfgAttrEntityList = new ArrayList<>();
		merchantCfgAttrEntityList.add(merchantCfgAttrEntity);
		
		when(mockMerchantService.getMerchantCfgAttrsByMerchantIdAndAttributeName(anyInt(), any())).thenReturn(merchantCfgAttrEntityList);
		
		SOFTypeEntity sofTypeEntity = new SOFTypeEntity();

		when(mockSofTypeService.getCardTypeById(anyInt())).thenReturn(sofTypeEntity);

		CodesEntity codesEntity = new CodesEntity();
		codesEntity.setIntegRefValue(14);

		when(mockCodesService.findByCodeValue(any())).thenReturn(codesEntity);

		BigDecimal mdrPercent = new BigDecimal(0.5);
		when(mockMdrService.getMdrPercent(pos.getMerchant(), sofTypeEntity)).thenReturn(mdrPercent);

		TransactionEntity savedTransaction = new TransactionEntity();
		savedTransaction.setTransactionId(123456);
		when(mockTransactionService.save(any())).thenReturn(savedTransaction);

		StaticRequest savedStaticRequest = mock(StaticRequest.class);
		when(mockStaticRequestService.save(any())).thenReturn(savedStaticRequest);

		Timestamp bankSettleDate = new Timestamp(System.currentTimeMillis());
		when(mockBankAdapterProcessingFactory.getBankAdapter(any())).thenReturn(mockOcbcPaynowAdapter);
		when(mockOcbcPaynowAdapter.getSettlementDate()).thenReturn(bankSettleDate);
		
		when(mockBankAdapterProcessingFactory.getBankAdapter(any())).thenReturn(mockOcbcPaynowAdapter);
		doThrow(BaseBusinessException.class).when(mockOcbcPaynowAdapter).processRefundReversalDuetoException(creditNotificationRequest);
		
		when(mockTransactionService.retrieve(anyInt())).thenReturn(savedTransaction);
		when(mockTransactionService.save(any())).thenReturn(savedTransaction);
		
		BaseBusinessException e = assertThrows(BaseBusinessException.class, () -> unit.process(creditNotificationRequest));
		
		ArgumentCaptor<TransactionEntity> argumentCaptorTransaction = ArgumentCaptor.forClass(TransactionEntity.class);
		verify(mockTransactionService, times(2)).save(argumentCaptorTransaction.capture());
		List<TransactionEntity> transactionEntity = argumentCaptorTransaction.getAllValues();
		
		assertEquals(TransactionState.STATE_REVERSED.getState(), transactionEntity.get(1).getState());
		
		verify(mockIdRegistryService, times(1)).saveIDRegistry(any());
		verify(mockAcquirerService, times(1)).getAcquirerSofDetails(any());
		verify(mockAcqMerAccountService, times(1)).getAcqMerAccountEntityByAcquirerIdAndEcMidAndSourceCurrency(anyInt(), any(), any());
		verify(mockPosService, times(1)).getPosInfoOnlyByPosId(anyInt());
		verify(mockBankService, times(1)).findByAcquirerAndValidate(anyInt());
		verify(mockPosService, times(2)).findByNetsTid(any());
		verify(mockSofTypeService, times(1)).getCardTypeById(anyInt());
		verify(mockCodesService, times(1)).findByCodeValue(any());
		verify(mockMdrService, times(1)).getMdrPercent(merchant, sofTypeEntity);
		//verify(mockBankAdapterProcessingFactory, times(2)).getBankAdapter(any());
		verify(mockOcbcPaynowAdapter, times(1)).getSettlementDate();
		verify(mockStaticRequestService, times(1)).save(any());
	}
	
	@Test
	public void validateSuccessCase() throws BaseBusinessException, ParseException {
		CreditNotificationRequest creditNotificationRequest = createCreditNotificationRequest();

		Map<ContextData, Object> entityMap = new HashMap<>();
		AcquirerEntity acquirer = new AcquirerEntity();
		acquirer.setStatus("2");
		acquirer.setAcquirerId(66);
		entityMap.put(ContextData.ACQUIRER, acquirer);

		SOFTypeEntity sofType = new SOFTypeEntity();
		sofType.setCardTypeId(57);
		entityMap.put(ContextData.SOF_TYPE, sofType);
		when(mockAcquirerService.getAcquirerSofDetails(any())).thenReturn(entityMap);

		AcqMerAccountEntity ama = new AcqMerAccountEntity();
		AcquirerMerchantMappingEntity amm = new AcquirerMerchantMappingEntity();
		amm.setStatus("2");
		amm.setMerMappingId(14);
		ama.setAcquirerMerchantMapping(amm);
		ama.setPosId(17);
		ama.setTid("UOPNKADE");
		ama.setAcqMerAccountId(17);

		when(mockAcqMerAccountService.getAcqMerAccountEntityByAcquirerIdAndEcMidAndSourceCurrency(anyInt(), any(), any())).thenReturn(ama);

		PosInfoOnly posInfo = mock(PosInfoOnly.class);

		when(mockPosService.getPosInfoOnlyByPosId(anyInt())).thenReturn(posInfo);

		Set<BankCfgAttrEntity> bankCfgAttrSet = new HashSet<>();
		BankCfgAttrEntity bankCfgAttr = new BankCfgAttrEntity();
		bankCfgAttr.setCfgAttrId(22);
		bankCfgAttr.setCfgAttrName(BankConfigAttributeName.PAYMENT_TYPE.name());
		bankCfgAttr.setCfgAttrValue("DBPN");
		bankCfgAttrSet.add(bankCfgAttr);

		BankEntity bank = new BankEntity();
		bank.setBankId(62);
		bank.setBankCfgAttr(bankCfgAttrSet);

		when(mockBankService.findByAcquirerAndValidate(anyInt())).thenReturn(bank);

		PhysicalPosEntity pos = new PhysicalPosEntity();
		MerchantEntity merchant = new MerchantEntity();
		merchant.setMerchantId(11);
		merchant.setStatus("2");
		pos.setPosId(19860311);
		pos.setStatus("2");
		pos.setName("");
		pos.setRid("");
		pos.setMerchant(merchant);

		when(mockPosService.findByNetsTid(any())).thenReturn(pos);
		
		MerchantCfgAttrEntity merchantCfgAttrEntity = new MerchantCfgAttrEntity();
		merchantCfgAttrEntity.setAttributeValue("DBSMerchant");
		List<MerchantCfgAttrEntity> merchantCfgAttrEntityList = new ArrayList<>();
		merchantCfgAttrEntityList.add(merchantCfgAttrEntity);
		
		when(mockMerchantService.getMerchantCfgAttrsByMerchantIdAndAttributeName(anyInt(), any())).thenReturn(merchantCfgAttrEntityList);
		
		SOFTypeEntity sofTypeEntity = new SOFTypeEntity();

		when(mockSofTypeService.getCardTypeById(anyInt())).thenReturn(sofTypeEntity);

		CodesEntity codesEntity = new CodesEntity();
		codesEntity.setIntegRefValue(14);

		when(mockCodesService.findByCodeValue(any())).thenReturn(codesEntity);

		BigDecimal mdrPercent = new BigDecimal(0.5);
		when(mockMdrService.getMdrPercent(pos.getMerchant(), sofTypeEntity)).thenReturn(mdrPercent);

		TransactionEntity savedTransaction = mock(TransactionEntity.class);
		when(mockTransactionService.save(any())).thenReturn(savedTransaction);
		StaticRequest savedStaticRequest = new StaticRequest();
		savedStaticRequest.setStan("567890");
		
		when(mockStaticRequestService.save(any(StaticRequest.class))).then(new Answer<StaticRequest>() {
			@Override
			public StaticRequest answer(InvocationOnMock invocation) {
				return savedStaticRequest;
			}
		});
		
		Timestamp bankSettleDate = new Timestamp(System.currentTimeMillis());
		when(mockBankAdapterProcessingFactory.getBankAdapter(any())).thenReturn(mockOcbcPaynowAdapter);
		when(mockOcbcPaynowAdapter.getSettlementDate()).thenReturn(bankSettleDate);

		PosNotificationEntity posNotification = new PosNotificationEntity();
		List<PosNotificationEntity> posNotifList = new ArrayList<PosNotificationEntity>();
		posNotifList.add(posNotification);
		when(mockPosNotificationService.findByPosId(any())).thenReturn(posNotifList);

		CreditNotificationInfo creditNotificationInfo = new CreditNotificationInfo();
		creditNotificationInfo.setAcquirerActiveStatus(acquirer.isActive());
		creditNotificationInfo.setAcquirerId(acquirer.getAcquirerId());
		creditNotificationInfo.setCardTypeId(sofType.getCardTypeId());
		creditNotificationInfo.setAmaActiveStatus(ama.getAcquirerMerchantMapping().isActive());
		creditNotificationInfo.setEcMid(creditNotificationRequest.getTransactionDomainData().getMerchantReferenceNumber().substring(0, 16));
		creditNotificationInfo.setPosId(ama.getPosId());
		creditNotificationInfo.setAcqMerAccountId(ama.getAcqMerAccountId());
		creditNotificationInfo.setAcqMerMappingId(ama.getAcquirerMerchantMapping().getMerMappingId());
		creditNotificationInfo.setBankTid(ama.getTid());
		creditNotificationInfo.setAmount(new BigDecimal(creditNotificationRequest.getTransactionDomainData().getCreditAmount()).divide(new BigDecimal(100).setScale(2, BigDecimal.ROUND_HALF_DOWN)));
		creditNotificationInfo.setBankSettleDate(bankSettleDate);
		creditNotificationInfo.setPaymentType(bankCfgAttr.getCfgAttrValue());	

		CreditNotificationResponse response = unit.process(creditNotificationRequest);

		verify(mockIdRegistryService, times(1)).saveIDRegistry(any());
		verify(mockAcquirerService, times(1)).getAcquirerSofDetails(any());
		verify(mockAcqMerAccountService, times(1)).getAcqMerAccountEntityByAcquirerIdAndEcMidAndSourceCurrency(anyInt(), any(), any());
		verify(mockPosService, times(1)).getPosInfoOnlyByPosId(anyInt());
		verify(mockBankService, times(1)).findByAcquirerAndValidate(anyInt());
		verify(mockPosService, times(2)).findByNetsTid(any());
		verify(mockSofTypeService, times(1)).getCardTypeById(anyInt());
		verify(mockCodesService, times(1)).findByCodeValue(any());
		verify(mockMdrService, times(1)).getMdrPercent(merchant, sofTypeEntity);
		verify(mockPosNotificationService, times(1)).findByPosId(any());
		//verify(mockBankAdapterProcessingFactory, times(1)).getBankAdapter(any());
		verify(mockOcbcPaynowAdapter, times(1)).getSettlementDate();

		ArgumentCaptor<TransactionEntity> argumentCaptorTransaction = ArgumentCaptor.forClass(TransactionEntity.class);
		verify(mockTransactionService, times(1)).save(argumentCaptorTransaction.capture());
		TransactionEntity transaction = argumentCaptorTransaction.getValue();

		assertNotNull(transaction.getAcqApprovalCode());
		assertEquals(creditNotificationInfo.getAcqMerAccountId(), transaction.getAcqMerAcctId().intValue());
		assertEquals(creditNotificationInfo.getAcqMerMappingId(), transaction.getAcqMerMappingId().intValue());
		assertEquals(creditNotificationInfo.getAcquirerId(), transaction.getAcquirerId().intValue());
		assertNotNull(transaction.getAuthorizeDate());
		assertEquals(codesEntity.getIntegRefValue(), transaction.getCaptureType());
		assertEquals(2, transaction.getCreatedBy().intValue());
		assertNotNull(transaction.getCreationDate());
		assertEquals(creditNotificationInfo.getCardTypeId(), transaction.getCardTypeId().intValue());
		assertEquals('N', transaction.getIsRefund().charValue());
		assertEquals(creditNotificationRequest.getRetrievalRef(), transaction.getAcquirerAuthResponseId());
		assertEquals(creditNotificationInfo.getEcMid(), transaction.getEcMid());
		assertEquals(mdrPercent, transaction.getMdrPercent());
		assertEquals(merchant.getMerchantId(), transaction.getMerchantId());
		assertEquals(creditNotificationInfo.getAmount(), transaction.getOrigTargetAmount());
		assertEquals(pos.getPosId(), transaction.getPosId());
		assertEquals(creditNotificationInfo.getAmount(), transaction.getSourceAmount());
		assertEquals("SGD", transaction.getSourceCurrency());
		assertEquals(TransactionState.STATE_SETTLED.getState(), transaction.getState());
		assertEquals(creditNotificationInfo.getAmount(), transaction.getTargetAmount());
		assertEquals("SGD", transaction.getTargetCurrency());
		assertEquals(creditNotificationInfo.getBankTid(), transaction.getTid());
		assertEquals(TransactionTypes.TYPE_PAYNOW_STATIC_CREDIT_NOTIFICATION, transaction.getTranType());
		assertEquals("NA", transaction.getTransactionSource());
		assertNotNull(transaction.getWorkflowBizKey());
		assertEquals(creditNotificationInfo.getBankSettleDate(), transaction.getBankSettleDate());

		ArgumentCaptor<StaticRequest> argumentCaptorStaticRequest = ArgumentCaptor.forClass(StaticRequest.class);
		verify(mockStaticRequestService, times(1)).save(argumentCaptorStaticRequest.capture());
		StaticRequest staticRequest = argumentCaptorStaticRequest.getValue();

		assertEquals(creditNotificationInfo.getPosId(), staticRequest.getPosId());
		assertNotNull(staticRequest.getTransactionId());
		assertEquals(creditNotificationInfo.getTid(), staticRequest.getTid());
		assertEquals(creditNotificationInfo.getMid(), staticRequest.getMid());
		assertEquals(creditNotificationRequest.getTransactionDomainData().getCreditingTime().substring(0, 4), staticRequest.getTransactionDate());
		assertEquals(creditNotificationRequest.getTransactionDomainData().getCreditingTime().substring(4), staticRequest.getTransactionTime());
		assertEquals(creditNotificationRequest.getInstitutionCode(), staticRequest.getPaymentRequesterInstId());
		assertEquals(creditNotificationRequest.getRetrievalRef(), staticRequest.getRetrievalRefNo());
		assertEquals(creditNotificationInfo.getAcquirerId(), staticRequest.getAcquirerId().intValue());
		assertNotNull(staticRequest.getExternalSettlementDate());
		assertEquals(creditNotificationInfo.getPaymentType(), staticRequest.getPaymentType());
		
		ArgumentCaptor<PaynowTransactionData> argumentCaptorPaynowTransactionData = ArgumentCaptor.forClass(PaynowTransactionData.class);
		verify(mockPaynowTransactionDataService, times(1)).save(argumentCaptorPaynowTransactionData.capture());
		PaynowTransactionData paynowTransactionData = argumentCaptorPaynowTransactionData.getValue();

		assertEquals(creditNotificationRequest.getRetrievalRef(), paynowTransactionData.getRetrievalRef());
		assertEquals(creditNotificationRequest.getInstitutionCode(), paynowTransactionData.getInstitutionCode());
		assertEquals(creditNotificationInfo.getTid(), paynowTransactionData.getTid());
		assertNotNull(paynowTransactionData.getTransactionId());
		assertEquals(creditNotificationInfo.getAmount(), paynowTransactionData.getCreditAmount());
		assertEquals(creditNotificationRequest.getTransactionDomainData().getCreditCurrency(), paynowTransactionData.getCreditCurrency());
		assertEquals(creditNotificationRequest.getTransactionDomainData().getCreditingTime(), paynowTransactionData.getCreditingTime());
		assertEquals(creditNotificationRequest.getTransactionDomainData().getReceivingAccountNumber(), paynowTransactionData.getReceivingAccountNumber());
		assertEquals(creditNotificationRequest.getTransactionDomainData().getReceivingAccountType(), paynowTransactionData.getReceivingAccountType());
		assertEquals(creditNotificationRequest.getTransactionDomainData().getMerchantReferenceNumber(), paynowTransactionData.getMerchantReferenceNumber());
		assertEquals(creditNotificationRequest.getTransactionDomainData().getSendingAccountBank(), paynowTransactionData.getSendingAccountBank());
		assertEquals(creditNotificationRequest.getTransactionDomainData().getSendingAccountName(), paynowTransactionData.getSendingAccountName());
		assertEquals(creditNotificationRequest.getTransactionDomainData().getSendingAccountNumber(), paynowTransactionData.getSendingAccountNumber());
		assertEquals(creditNotificationRequest.getTransactionDomainData().getAdditionalBankReference(), paynowTransactionData.getAdditionalBankReference());

		assertEquals(ResponseCodes.SUCCESS, response.getResponseCode());
		assertNotNull(response.getTransactionResponseData());
		assertNotNull(response.getTransactionResponseData().getStan());
		assertNotNull(response.getTransactionResponseData().getApprovalCode());

		ArgumentCaptor<TransactionStateMsg> argumentCaptorTxnStateMsg = ArgumentCaptor.forClass(TransactionStateMsg.class);
		verify(mockNonStopClient, times(1)).put(argumentCaptorTxnStateMsg.capture());
		TransactionStateMsg txnStateMsg = argumentCaptorTxnStateMsg.getValue();
		
		assertEquals(creditNotificationRequest.getSofUri(), txnStateMsg.getSofUri());
		assertEquals(creditNotificationRequest.getTransactionDomainData().getCreditAmount(), txnStateMsg.getAmount());
		assertEquals(creditNotificationRequest.getTransmissionTime(), txnStateMsg.getTransmissionTime());
		assertNotNull(txnStateMsg.getStan());
		assertEquals(creditNotificationRequest.getTransactionDomainData().getCreditingTime().substring(4), txnStateMsg.getTransactionTime());
		assertEquals(creditNotificationRequest.getTransactionDomainData().getCreditingTime().substring(0, 4), txnStateMsg.getTransactionDate());
		assertEquals(PaynowUtil.getTandemSettlementDate(creditNotificationInfo.getBankSettleDate()), txnStateMsg.getSettlementDate());
		assertEquals(creditNotificationRequest.getInstitutionCode(), txnStateMsg.getInstitutionCode());
		assertEquals("00", txnStateMsg.getConditionCode());
		assertEquals(ResponseCodes.SUCCESS, txnStateMsg.getResponseCode());
		assertEquals(creditNotificationInfo.getTid(), txnStateMsg.getHostTid());
		assertEquals(creditNotificationInfo.getMid(), txnStateMsg.getHostMid());
		assertEquals("PNSTATICCN", txnStateMsg.getTransactionType());
		assertEquals(TransactionState.STATE_SETTLED.getState(), txnStateMsg.getStatus());
		assertEquals(creditNotificationInfo.getPaymentType(), txnStateMsg.getPaymentType());
		assertNotNull(txnStateMsg.getApprovalCode());
		assertEquals(creditNotificationRequest.getInstitutionCode(), txnStateMsg.getInstitutionCode());
		assertEquals(creditNotificationRequest.getRetrievalRef(), txnStateMsg.getRetrievalRef());
		assertNotNull(txnStateMsg.getTransactionId());
		
		ArgumentCaptor<JsonNotification> argumentCaptorNotificationResponse = ArgumentCaptor.forClass(JsonNotification.class);
		verify(mockNotificationClient, times(1)).put(argumentCaptorNotificationResponse.capture());
		JsonNotification notificationResponse = argumentCaptorNotificationResponse.getValue();
		NotificationRequest notification = (NotificationRequest) notificationResponse.getMessage();
		
		assertNotNull(notificationResponse.getCommunicationData());
		assertNotNull(notificationResponse.getMessage());
		assertEquals(creditNotificationRequest.getRetrievalRef(), notification.getRetrievalRef());
		assertEquals("0340", notification.getMti());
		assertEquals("000000", notification.getProcessCode());
		assertNotNull(notification.getStan());
		assertEquals(creditNotificationRequest.getTransactionDomainData().getCreditingTime().substring(4), notification.getTransactionTime());
		assertEquals(creditNotificationRequest.getTransactionDomainData().getCreditingTime().substring(0, 4), notification.getTransactionDate());
		assertEquals(creditNotificationInfo.getTid(), notification.getTid());
		assertEquals(creditNotificationInfo.getMid(), notification.getMid());
		assertEquals(ResponseCodes.SUCCESS, notification.getResponseCode());
		assertEquals(creditNotificationRequest.getTransactionDomainData().getCreditAmount(), notification.getTargetAmount());
		assertEquals(creditNotificationRequest.getSofUri(), notification.getSofUri());
		assertEquals(creditNotificationInfo.getPaymentType(), notification.getPaymentTypeId());
		assertNotNull(notification.getApprovalCode());
		assertNotNull(notification.getTransactionId());
		assertEquals(creditNotificationRequest.getTransactionDomainData().getCreditCurrency(), notification.getCurrencyCode());
		assertEquals(TransactionState.STATE_SETTLED.getState(), notification.getTransactionStatus());
		
		ArgumentCaptor<APSRequestWrapper> argumentCaptorAPSRequestWrapper = ArgumentCaptor.forClass(APSRequestWrapper.class);
		verify(mockFraudwallClient, times(1)).put(argumentCaptorAPSRequestWrapper.capture());
		APSRequestWrapper wrapper = argumentCaptorAPSRequestWrapper.getValue();
		APSRequest apsRequest = wrapper.getRequest();
		Map<String, String> additionalData = apsRequest.getAdditionalData();
		
		assertEquals(creditNotificationRequest.getTransactionDomainData().getHostTid(), apsRequest.getTid());
		assertNotNull(apsRequest.getTransactionId());
		assertNotNull(apsRequest.getStan());
		assertEquals(creditNotificationRequest.getTransactionDomainData().getHostMid(), apsRequest.getMid());
		assertEquals(new BigDecimal(creditNotificationRequest.getTransactionDomainData().getCreditAmount()).divide(new BigDecimal(100).setScale(2, BigDecimal.ROUND_HALF_DOWN)), apsRequest.getTargetAmount());
		assertNotNull(apsRequest.getRequestMap());
		assertNotNull(apsRequest.getApprovalCode());
		assertEquals("00", additionalData.get(AdditionalData.CONDITION_CODE));
		assertEquals(creditNotificationInfo.getPaymentType(), additionalData.get(AdditionalData.PAYMENT_TYPE));
		assertEquals("00", wrapper.getResponse().getStatusCode());
		
	}
}
