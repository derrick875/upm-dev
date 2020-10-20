package com.nets.nps.paynow.service.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.nets.nps.paynow.entity.OrderInfo;
import com.nets.nps.paynow.entity.OrderRequest;
import com.nets.nps.paynow.entity.OrderTransactionDomainData;
import com.nets.nps.paynow.service.PaynowTransactionDataService;
import com.nets.nps.paynow.service.impl.PaynowOrderService;
import com.nets.upos.commons.constants.TransactionTypes;
import com.nets.upos.commons.enums.RequestState;
import com.nets.upos.commons.exception.BaseBusinessException;
import com.nets.upos.core.entity.AcqMerAccountEntity;
import com.nets.upos.core.entity.AcquirerMerchantMappingEntity;
import com.nets.upos.core.entity.BankCfgAttrEntity;
import com.nets.upos.core.entity.BankEntity;
import com.nets.upos.core.entity.CodesEntity;
import com.nets.upos.core.entity.MerchantCfgAttrEntity;
import com.nets.upos.core.entity.MerchantEntity;
import com.nets.upos.core.entity.PhysicalPosEntity;
import com.nets.upos.core.entity.Request;
import com.nets.upos.core.service.AcqMerAccountService;
import com.nets.upos.core.service.BankService;
import com.nets.upos.core.service.CodesService;
import com.nets.upos.core.service.IDRegistryService;
import com.nets.upos.core.service.MerchantService;
import com.nets.upos.core.service.PhysicalPosService;
import com.nets.upos.core.service.RequestService;

@SpringBootTest(classes = {PaynowOrderService.class})
public class PaynowOrderServiceTest {
	
	@Autowired
	PaynowOrderService unit;
	
	@MockBean
	private IDRegistryService mockIdRegistryService;

	@MockBean
	private CodesService mockCodesService;

	@MockBean
	private PhysicalPosService mockPosService;

	@MockBean
	private AcqMerAccountService mockAcqMerAccountService;

	@MockBean
	private PaynowTransactionDataService mockPaynowTransactionDataService;

	@MockBean
	private RequestService mockRequestService;

	@MockBean
	private BankService mockBankService;
	
	@MockBean
	private MerchantService mockMerchantService;
	

	/*
	 * Method to create a sample OrderRequest for testing purposes
	 */
	private OrderRequest createOrderRequest() {
		OrderTransactionDomainData transactionDomainData = new OrderTransactionDomainData();
		transactionDomainData.setPaymentAmount("000000000460");
		transactionDomainData.setPaymentCurrency("SGD");
		transactionDomainData.setTransactionTime("123456");
		transactionDomainData.setTransactionDate("0608");
		transactionDomainData.setValidityTime("30");
		transactionDomainData.setEntryMode("000");
		transactionDomainData.setConditionCode("00");
		transactionDomainData.setHostMid("11119860311");
		transactionDomainData.setHostTid("19860311");
		transactionDomainData.setNetsPaynowMpan("");
		transactionDomainData.setBankMerchantProxy("");
		transactionDomainData.setBankMerchantProxyType("");
		transactionDomainData.setMerchantReferenceNumber("ABCDEFGH1234567890");
		transactionDomainData.setInvoiceRef("");
		transactionDomainData.setNpxData(null);
		OrderRequest request = new OrderRequest();
		request.setRetrievalRef("12345678129");
		request.setInstitutionCode("30000000033");
		request.setTransmissionTime("0608123413");
		request.setChannelIndecator("PNT");
		request.setQrFormat("PAYNOW");
		request.setQrVersion("122");
		request.setTransactionDomainData(transactionDomainData);
		return request;	
	}
	
	@Test 
	public void validateDuplicates( ) {
		OrderRequest request = createOrderRequest();
		when(mockIdRegistryService.saveIDRegistry(any())).thenThrow(RuntimeException.class);

		BaseBusinessException e = assertThrows(BaseBusinessException.class, () -> unit.process(request));
		assertEquals("85", e.getCode());
		assertEquals("Order Was Previously Received.", e.getErrorMessage());
		
		verify(mockIdRegistryService, times(1)).saveIDRegistry(any());
	}
	
	@Test
	public void validateAcqMerMappingNotFound() throws Exception {
		OrderRequest request = createOrderRequest();
		PhysicalPosEntity posEntity = new PhysicalPosEntity();
		MerchantEntity merchant = new MerchantEntity();
		merchant.setMerchantId(11);
		posEntity.setPosId(19860311);
		posEntity.setMerchant(merchant);
		
		when(mockPosService.findAndValidatePos(any())).thenReturn(posEntity);
		
		CodesEntity codesEntity = new CodesEntity();
		List<CodesEntity> codesEntityList = new ArrayList<>();
		codesEntityList.add(codesEntity);
		when(mockCodesService.findByCodeName(any())).thenReturn(codesEntityList);
		when(mockAcqMerAccountService.findByAcquirerIdListandPosIdAndSourceCurrency(any(), anyInt(), any())).thenReturn(null);
		
		BaseBusinessException e = assertThrows(BaseBusinessException.class, () -> unit.process(request));
		assertEquals("15", e.getCode());
		assertEquals("Acquirer merchant mapping not found.", e.getErrorMessage());
		
		verify(mockPosService, times(1)).findAndValidatePos(any());
		verify(mockCodesService, times(1)).findByCodeName(any());
		verify(mockAcqMerAccountService, times(1)).findByAcquirerIdListandPosIdAndSourceCurrency(any(), anyInt(), any());
	}
	
	@Test
	public void validateAcqDissableforMerchant() throws Exception {
		OrderRequest request = createOrderRequest();
		PhysicalPosEntity posEntity = new PhysicalPosEntity();
		MerchantEntity merchant = new MerchantEntity();
		merchant.setMerchantId(11);
		posEntity.setPosId(19860311);
		posEntity.setMerchant(merchant);
		
		when(mockPosService.findAndValidatePos(any())).thenReturn(posEntity);
		
		CodesEntity codesEntity = new CodesEntity();
		List<CodesEntity> codesEntityList = new ArrayList<>();
		codesEntityList.add(codesEntity);
		when(mockCodesService.findByCodeName(any())).thenReturn(codesEntityList);
		AcqMerAccountEntity acqMerAccountEntity = new AcqMerAccountEntity();
		acqMerAccountEntity.setEcMid("1231232131001234");
		acqMerAccountEntity.setAcquirerMerchantMapping(new AcquirerMerchantMappingEntity());
		List<AcqMerAccountEntity> ama = new ArrayList<>();
		ama.add(acqMerAccountEntity);
		
		when(mockAcqMerAccountService.findByAcquirerIdListandPosIdAndSourceCurrency(any(), anyInt(), any())).thenReturn(ama);
		
		BaseBusinessException e = assertThrows(BaseBusinessException.class, () -> unit.process(request));
		assertEquals("58", e.getCode());
		assertEquals("SOF is disabled for the merchant.", e.getErrorMessage());
		
		verify(mockPosService, times(1)).findAndValidatePos(any());
		verify(mockCodesService, times(1)).findByCodeName(any());
		verify(mockAcqMerAccountService, times(1)).findByAcquirerIdListandPosIdAndSourceCurrency(any(), anyInt(), any());
	}
	
	@Test
	public void validateInvalidMerchantMpan() throws Exception {
		OrderRequest request = createOrderRequest();
		PhysicalPosEntity posEntity = new PhysicalPosEntity();
		MerchantEntity merchant = new MerchantEntity();
		merchant.setMerchantId(11);
		posEntity.setPosId(19860311);
		posEntity.setMerchant(merchant);
		
		when(mockPosService.findAndValidatePos(any())).thenReturn(posEntity);
		
		CodesEntity codesEntity = new CodesEntity();
		List<CodesEntity> codesEntityList = new ArrayList<>();
		codesEntityList.add(codesEntity);
		when(mockCodesService.findByCodeName(any())).thenReturn(codesEntityList);
		
		AcqMerAccountEntity acqMerAccountEntity = new AcqMerAccountEntity();
		acqMerAccountEntity.setEcMid("1231232131");
		List<AcqMerAccountEntity> ama = new ArrayList<>();
		ama.add(acqMerAccountEntity);
		
		when(mockAcqMerAccountService.findByAcquirerIdListandPosIdAndSourceCurrency(any(), anyInt(), any())).thenReturn(ama);
		
		BaseBusinessException e = assertThrows(BaseBusinessException.class, () -> unit.process(request));
		assertEquals("80", e.getCode());
		assertEquals("Invalid Merchant MPAN.", e.getErrorMessage());
		
		verify(mockPosService, times(1)).findAndValidatePos(any());
		verify(mockCodesService, times(1)).findByCodeName(any());
		verify(mockAcqMerAccountService, times(1)).findByAcquirerIdListandPosIdAndSourceCurrency(any(), anyInt(), any());
	}
	
	@Test
	public void validateInvalidMerchantShortName() throws Exception {
		OrderRequest request = createOrderRequest();
		PhysicalPosEntity posEntity = new PhysicalPosEntity();
		MerchantEntity merchant = new MerchantEntity();
		merchant.setMerchantId(11);
		posEntity.setPosId(19860311);
		posEntity.setMerchant(merchant);
		
		when(mockPosService.findAndValidatePos(any())).thenReturn(posEntity);
		
		CodesEntity codesEntity = new CodesEntity();
		List<CodesEntity> codesEntityList = new ArrayList<>();
		codesEntityList.add(codesEntity);
		when(mockCodesService.findByCodeName(any())).thenReturn(codesEntityList);
		
		AcqMerAccountEntity acqMerAccountEntity = new AcqMerAccountEntity();
		acqMerAccountEntity.setEcMid("1231231231231231");
		List<AcqMerAccountEntity> ama = new ArrayList<>();
		AcquirerMerchantMappingEntity amm=new AcquirerMerchantMappingEntity();
		amm.setStatus("2");
		acqMerAccountEntity.setAcquirerMerchantMapping(amm);
		ama.add(acqMerAccountEntity);
		
		when(mockAcqMerAccountService.findByAcquirerIdListandPosIdAndSourceCurrency(any(), anyInt(), any())).thenReturn(ama);
		
		when(mockMerchantService.getMerchantCfgAttrsByMerchantIdAndAttributeName(anyInt(), any())).thenReturn(null);
		
		BaseBusinessException e = assertThrows(BaseBusinessException.class, () -> unit.process(request));
		assertEquals("80", e.getCode());
		assertEquals("Invalid Merchant Shortname.", e.getErrorMessage());
		
		verify(mockPosService, times(1)).findAndValidatePos(any());
		verify(mockCodesService, times(1)).findByCodeName(any());
		verify(mockAcqMerAccountService, times(1)).findByAcquirerIdListandPosIdAndSourceCurrency(any(), anyInt(), any());
		verify(mockMerchantService, times(1)).getMerchantCfgAttrsByMerchantIdAndAttributeName(anyInt(), any());
	}
	
	@Test
	public void validateInvalidMerchantShortName2() throws Exception {
		OrderRequest request = createOrderRequest();
		PhysicalPosEntity posEntity = new PhysicalPosEntity();
		MerchantEntity merchant = new MerchantEntity();
		merchant.setMerchantId(11);
		posEntity.setPosId(19860311);
		posEntity.setMerchant(merchant);
		
		when(mockPosService.findAndValidatePos(any())).thenReturn(posEntity);
		
		CodesEntity codesEntity = new CodesEntity();
		List<CodesEntity> codesEntityList = new ArrayList<>();
		codesEntityList.add(codesEntity);
		when(mockCodesService.findByCodeName(any())).thenReturn(codesEntityList);
		
		AcqMerAccountEntity acqMerAccountEntity = new AcqMerAccountEntity();
		acqMerAccountEntity.setEcMid("1231231231231231");
		List<AcqMerAccountEntity> ama = new ArrayList<>();
		AcquirerMerchantMappingEntity amm=new AcquirerMerchantMappingEntity();
		amm.setStatus("2");
		acqMerAccountEntity.setAcquirerMerchantMapping(amm);
		ama.add(acqMerAccountEntity);
		
		when(mockAcqMerAccountService.findByAcquirerIdListandPosIdAndSourceCurrency(any(), anyInt(), any())).thenReturn(ama);
		
		MerchantCfgAttrEntity merchantCfgAttrEntity = new MerchantCfgAttrEntity();
		merchantCfgAttrEntity.setAttributeValue(null);
		List<MerchantCfgAttrEntity> merchantCfgAttrEntityList = new ArrayList<>();
		merchantCfgAttrEntityList.add(merchantCfgAttrEntity);
		
		when(mockMerchantService.getMerchantCfgAttrsByMerchantIdAndAttributeName(anyInt(), any())).thenReturn(merchantCfgAttrEntityList);
		
		BaseBusinessException e = assertThrows(BaseBusinessException.class, () -> unit.process(request));
		assertEquals("80", e.getCode());
		assertEquals("Invalid Merchant Shortname.", e.getErrorMessage());
		
		verify(mockPosService, times(1)).findAndValidatePos(any());
		verify(mockCodesService, times(1)).findByCodeName(any());
		verify(mockAcqMerAccountService, times(1)).findByAcquirerIdListandPosIdAndSourceCurrency(any(), anyInt(), any());
		verify(mockMerchantService, times(1)).getMerchantCfgAttrsByMerchantIdAndAttributeName(anyInt(), any());
	}
	
	@Test
	public void validateBankNotFound() throws Exception {
		OrderRequest request = createOrderRequest();
		PhysicalPosEntity posEntity = new PhysicalPosEntity();
		MerchantEntity merchant = new MerchantEntity();
		merchant.setMerchantId(11);
		posEntity.setPosId(19860311);
		posEntity.setMerchant(merchant);
		
		when(mockPosService.findAndValidatePos(any())).thenReturn(posEntity);
		
		CodesEntity codesEntity = new CodesEntity();
		List<CodesEntity> codesEntityList = new ArrayList<>();
		codesEntityList.add(codesEntity);
		when(mockCodesService.findByCodeName(any())).thenReturn(codesEntityList);
		
		AcqMerAccountEntity acqMerAccountEntity = new AcqMerAccountEntity();
		acqMerAccountEntity.setEcMid("1231231231231231");
		List<AcqMerAccountEntity> ama = new ArrayList<>();
		AcquirerMerchantMappingEntity amm=new AcquirerMerchantMappingEntity();
		amm.setStatus("2");
		acqMerAccountEntity.setAcquirerMerchantMapping(amm);
		
		ama.add(acqMerAccountEntity);
		
		when(mockAcqMerAccountService.findByAcquirerIdListandPosIdAndSourceCurrency(any(), anyInt(), any())).thenReturn(ama);
		
		MerchantCfgAttrEntity merchantCfgAttrEntity = new MerchantCfgAttrEntity();
		merchantCfgAttrEntity.setAttributeValue("abc");
		List<MerchantCfgAttrEntity> merchantCfgAttrEntityList = new ArrayList<>();
		merchantCfgAttrEntityList.add(merchantCfgAttrEntity);
		
		when(mockMerchantService.getMerchantCfgAttrsByMerchantIdAndAttributeName(anyInt(), any())).thenReturn(merchantCfgAttrEntityList);
		
		when(mockPaynowTransactionDataService.getSequenceNumber(any())).thenReturn("123456");
		
		when(mockBankService.findByAcquirerAndValidate(any())).thenReturn(null);
		
		BaseBusinessException e = assertThrows(BaseBusinessException.class, () -> unit.process(request));
		assertEquals("15", e.getCode());
		assertEquals("Bank not found", e.getErrorMessage());
		
		verify(mockPosService, times(1)).findAndValidatePos(any());
		verify(mockCodesService, times(1)).findByCodeName(any());
		verify(mockAcqMerAccountService, times(1)).findByAcquirerIdListandPosIdAndSourceCurrency(any(), anyInt(), any());
		verify(mockMerchantService, times(1)).getMerchantCfgAttrsByMerchantIdAndAttributeName(anyInt(), any());
		verify(mockPaynowTransactionDataService, times(2)).getSequenceNumber(any());
		verify(mockBankService, times(1)).findByAcquirerAndValidate(any());
	}
	
	@Test
	public void validateSuccessCase() throws Exception {
		OrderRequest orderRequest = createOrderRequest();
		PhysicalPosEntity posEntity = new PhysicalPosEntity();
		MerchantEntity merchant = new MerchantEntity();
		merchant.setMerchantId(11);
		posEntity.setPosId(19860311);
		posEntity.setMerchant(merchant);
		
		when(mockPosService.findAndValidatePos(any())).thenReturn(posEntity);
		
		CodesEntity codesEntity = new CodesEntity();
		List<CodesEntity> codesEntityList = new ArrayList<>();
		codesEntityList.add(codesEntity);
		when(mockCodesService.findByCodeName(any())).thenReturn(codesEntityList);
		
		AcqMerAccountEntity acqMerAccountEntity = new AcqMerAccountEntity();
		acqMerAccountEntity.setEcMid("1231231231231231");
		List<AcqMerAccountEntity> ama = new ArrayList<>();
		AcquirerMerchantMappingEntity amm=new AcquirerMerchantMappingEntity();
		amm.setStatus("2");
		acqMerAccountEntity.setAcquirerMerchantMapping(amm);
		
		ama.add(acqMerAccountEntity);
		
		when(mockAcqMerAccountService.findByAcquirerIdListandPosIdAndSourceCurrency(any(), anyInt(), any())).thenReturn(ama);
		
		MerchantCfgAttrEntity merchantCfgAttrEntity = new MerchantCfgAttrEntity();
		merchantCfgAttrEntity.setAttributeValue("abc");
		List<MerchantCfgAttrEntity> merchantCfgAttrEntityList = new ArrayList<>();
		merchantCfgAttrEntityList.add(merchantCfgAttrEntity);
		
		when(mockMerchantService.getMerchantCfgAttrsByMerchantIdAndAttributeName(anyInt(), any())).thenReturn(merchantCfgAttrEntityList);
		
		when(mockPaynowTransactionDataService.getSequenceNumber(any())).thenReturn("123456");
		
		BankEntity bank = new BankEntity();
		Set<BankCfgAttrEntity> bankCfgAttrSet = new HashSet<>();
		BankCfgAttrEntity bankCfgAttr = new BankCfgAttrEntity();
		bankCfgAttr.setCfgAttrId(22);
		bankCfgAttr.setCfgAttrName("PAYMENT_TYPE");
		bankCfgAttr.setCfgAttrValue("DBPN");
		bankCfgAttrSet.add(bankCfgAttr);
		bank.setBankCfgAttr(bankCfgAttrSet);
		
		when(mockBankService.findByAcquirerAndValidate(any())).thenReturn(bank);
		
		CodesEntity codesEntity2 = new CodesEntity();
		List<CodesEntity> codesEntityList2 = new ArrayList<>();
		codesEntityList2.add(codesEntity2);
		when(mockCodesService.findByCodeName(any())).thenReturn(codesEntityList2);
		
		Request savedRequest = new Request();
		when(mockRequestService.save(any())).thenReturn(savedRequest);
		
		OrderInfo orderInfo = unit.process(orderRequest);
		
		verify(mockPosService, times(1)).findAndValidatePos(any());
		verify(mockCodesService, times(2)).findByCodeName(any());
		verify(mockAcqMerAccountService, times(1)).findByAcquirerIdListandPosIdAndSourceCurrency(any(), anyInt(), any());
		verify(mockMerchantService, times(1)).getMerchantCfgAttrsByMerchantIdAndAttributeName(anyInt(), any());
		verify(mockPaynowTransactionDataService, times(2)).getSequenceNumber(any());
		verify(mockBankService, times(1)).findByAcquirerAndValidate(any());
		
		ArgumentCaptor<Request> argumentCaptorRequest = ArgumentCaptor.forClass(Request.class);
		verify(mockRequestService, times(1)).save(argumentCaptorRequest.capture());
		Request request = argumentCaptorRequest.getValue();
		
		assertEquals(orderRequest.getRetrievalRef(), request.getRetrievalRefNo());
		assertEquals(orderRequest.getTransactionDomainData().getTransactionDate(), request.getTransactionDate());
		assertEquals(orderRequest.getTransactionDomainData().getTransactionTime(), request.getTransactionTime());
		assertEquals(RequestState.WAITING.getState(), request.getState());
		assertEquals(orderRequest.getTransactionDomainData().getPaymentCurrency(), request.getTargetCurrency());
		assertEquals(orderInfo.getAmount(), request.getTargetAmount());
		assertEquals(orderInfo.getMerchantId(), request.getMerchantId());
		assertEquals(orderInfo.getPosId(), request.getPosId());
		assertEquals(TransactionTypes.TYPE_ORDER, request.getRequestType());
		assertEquals(orderInfo.getCreationDate(), request.getCreationDate());
		assertEquals(orderRequest.getInstitutionCode(), request.getOrderInstitutionId());
		assertEquals(orderRequest.getTransactionDomainData().getTransactionDate(), request.getCaptureDate());
		assertEquals(orderInfo.getQrValidityTime(), request.getOrderTimeout().intValue());
		assertEquals(orderRequest.getTransactionDomainData().getInvoiceRef(), request.getInvoiceRef());
		assertNotNull(orderInfo.getQrImage());
	}
	
	
	
	
	
	
	
	
	
}
