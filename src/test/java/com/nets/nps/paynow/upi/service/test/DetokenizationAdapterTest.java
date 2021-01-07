package com.nets.nps.paynow.upi.service.test;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.nets.nps.core.entity.Detokenization;
import com.nets.nps.paynow.entity.QrcGenerationRequest;
import com.nets.nps.paynow.entity.QrcGenerationTransactionDomainData;
import com.nets.nps.paynow.security.service.DetokenizationService;
import com.nets.nps.paynow.service.impl.DetokenizationAdapter;
import com.nets.upos.commons.exception.BaseBusinessException;

@SpringBootTest(classes = {DetokenizationAdapter.class})

public class DetokenizationAdapterTest {
	
	@Autowired
	DetokenizationAdapter unit;
	
	@MockBean
	private DetokenizationService mockDetokenizationService;
	
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

		BaseBusinessException e = assertThrows(BaseBusinessException.class, () -> unit.detokenizationQrcGenerationRequest(request));
		assertEquals("58", e.getCode());
		assertEquals("Invalid nets token ref id.", e.getErrorMessage());
	}
	
	@Test
	public void validateSuccessCase() throws BaseBusinessException {
		QrcGenerationRequest request = createQrcGenerationRequest();
		when(mockDetokenizationService.getTokenization(any(Detokenization.class))).thenReturn("abcd");
		
		QrcGenerationRequest qrGenReq = unit.detokenizationQrcGenerationRequest(request);
		assertEquals("abcd", qrGenReq.getSofAccountId());
	}
}
