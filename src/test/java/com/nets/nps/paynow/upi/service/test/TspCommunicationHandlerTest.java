package com.nets.nps.paynow.upi.service.test;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.any;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;

import com.nets.nps.core.entity.Detokenization;
import com.nets.nps.core.entity.DetokenizationBody;
import com.nets.nps.core.entity.DetokenizationHeader;
import com.nets.nps.core.entity.DetokenizationSpecificData;
import com.nets.nps.paynow.entity.QrcGenerationRequest;
import com.nets.nps.paynow.entity.QrcGenerationTransactionDomainData;
import com.nets.nps.paynow.handlers.TspCommunicationHandler;
import com.nets.nps.paynow.utils.UtilComponents;
import com.nets.upos.commons.solace.SolaceMessageReceiver;
import com.nets.upos.commons.solace.SolaceMessageSender;
import com.nets.upos.commons.utils.DateUtil;

@SpringBootTest(classes = {TspCommunicationHandler.class})
@TestPropertySource(locations = "classpath:paynow-local.properties")
public class TspCommunicationHandlerTest {
	
	@Value("${product_indicator}")
	private String product_indicator;

	@Value("${release_number}")
	private String release_number;

	@Value("${status}")
	private String status;

	@Value("${originator_code}")
	private String originator_code;

	@Value("${responder_code}")
	private String responder_code;

	@Value("${mti}")
	private String mti;

	@Value("${nets_tag_1}")
	private String nets_tag_1;

	@Value("${nets_tag_2}")
	private String nets_tag_2;

	@Value("${processing_code}")
	private String processing_code;

	@Value("${sub_length}")
	private String sub_length;

	@Value("${scheme_network_id}")
	private String scheme_network_id;

	@Value("${entry_mode}")
	private String entry_mode;

	@Value("${condition_code}")
	private String condition_code;

	@Value("${terminal_id}")
	private String terminal_id;

	@Value("${retailer_id}")
	private String retailer_id;

	@Value("${nets_bank_fiid}")
	private String nets_bank_fiid;

	@Value("${retailer_info}")
	private String retailer_info;

	@Value("${sub_id}")
	private String sub_id;
	
	@Autowired
	TspCommunicationHandler unit;
	
	@MockBean
	SolaceMessageSender mockSolaceMessageSender;
	
	@Qualifier("tpsMessageReceiver")
	@MockBean
	SolaceMessageReceiver mockSolaceMessageReceiver;
	
	public String getRefWithPadding(String ref) {
		String amountStrWithLeftPad = String.format("%012d", Long.valueOf(ref));
		return amountStrWithLeftPad;
	}

	public static String addRightPadding(String tokenId) {
		return StringUtils.rightPad(tokenId, 20);
	}
	
	private Detokenization createDetokenization(QrcGenerationRequest request) {
		Detokenization detoken = new Detokenization();
		DetokenizationHeader header = new DetokenizationHeader();
		DetokenizationBody body = new DetokenizationBody();

		header.setMti(mti);
		header.setNets_tag_1(nets_tag_1);
		header.setOriginator_code(originator_code);
		header.setProduct_indicator(product_indicator);
		header.setRelease_number(release_number);
		header.setResponder_code(responder_code);
		header.setStatus(status);
		detoken.setHeader(header);
		body.setNets_tag_2(nets_tag_2);
		body.setProcessing_code(processing_code);
		LocalDateTime date = DateUtil.parseLocalDateTime(request.getTransmissionTime());	
		body.setStan(UtilComponents.getTraceNum());
		body.setTrxn_time(DateUtil.getDisplayTimeHHMMSS(date));
		body.setTrxn_date(DateUtil.getDisplayDateMMDD(date));
		body.setXmit_datetime(DateUtil.getDisplayDateMMDD(date)+DateUtil.getDisplayTimeHHMMSS(date));
		body.setEntry_mode(entry_mode);
		body.setCondition_code(condition_code);
		body.setRet_ref_num(getRefWithPadding(request.getRetrievalRef()));
		body.setRetailer_id(retailer_id);
		body.setRetailer_info(retailer_info);
		ArrayList<DetokenizationSpecificData> txn_specific_datas= new ArrayList<DetokenizationSpecificData>();  
		DetokenizationSpecificData txn_specific_data= new DetokenizationSpecificData();  
		txn_specific_data.setSub_id(sub_id);
		txn_specific_data.setSub_length(sub_length);
		txn_specific_data.setNets_bank_fiid(nets_bank_fiid);
		txn_specific_data.setNets_token_id(addRightPadding(request.getSofAccountId()));
		txn_specific_data.setScheme_network_id(scheme_network_id);
		txn_specific_datas.add(txn_specific_data);
		body.setTxn_specific_data(txn_specific_datas);
		detoken.setBody(body);

		return detoken;
	}
	
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
	public void testSuccessCase() throws IOException {
		
		QrcGenerationRequest request = createQrcGenerationRequest();
		Detokenization detoken = createDetokenization(request);
		
		String response = unit.sendAndReceive(detoken);
		
		verify(mockSolaceMessageSender, times(1)).put(any(), any());
		verify(mockSolaceMessageReceiver, times(1)).receiveMessage(any());
		
	}
	
	
	
}
