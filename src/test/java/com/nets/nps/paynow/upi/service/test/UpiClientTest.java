package com.nets.nps.paynow.upi.service.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;

import com.nets.nps.client.impl.MsgInfo;
import com.nets.nps.client.impl.UpiClient;
import com.nets.nps.client.impl.UpiQrcGenerationRequestAdapter;
import com.nets.nps.paynow.entity.MessageResponse;
import com.nets.nps.paynow.entity.QrcGenerationRequest;
import com.nets.nps.paynow.entity.QrcGenerationTransactionDomainData;
import com.nets.nps.paynow.entity.UpiQrcGenerationRequest;
import com.nets.nps.paynow.entity.UpiQrcGenerationRequestTransactionRequest;
import com.nets.nps.paynow.entity.UpiQrcGenerationResponse;
import com.nets.nps.paynow.entity.UpiQrcGenerationTransactionResponse;
import com.nets.nps.paynow.utils.UtilComponents;
import com.nets.upos.commons.exception.BaseBusinessException;
import com.nets.upos.commons.solace.UpiProxyReceiver;
import com.nets.upos.commons.solace.UpiProxySender;

@SpringBootTest(classes = {UpiClient.class})
@TestPropertySource(locations = "classpath:paynow-local.properties")
public class UpiClientTest {

	@Value("#{${nets.upi.appgateway.instId.map}}")
	private Map<String, String> instIdMap;

	@Value("${deviceId}")
	private String deviceId;

	@Value("${userId}")
	private String userId;

	@Autowired
	UpiClient unit;

	@Qualifier("upiMessageProducer")
	@MockBean
	UpiProxySender mockUpiProxySender;

	@Qualifier("upiMessageReceiver")
	@MockBean
	UpiProxyReceiver mockUpiProxyReceiver;

	@MockBean
	UpiQrcGenerationRequestAdapter mockUpiQrcGenerationRequestAdapter;

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

	private UpiQrcGenerationRequest createUpiQrcGenerationRequest(QrcGenerationRequest qrGenerationRequest) {
		UpiQrcGenerationRequest upiQrcGenerationRequest = new UpiQrcGenerationRequest();
		MsgInfo msgInfo = new MsgInfo();
		UpiQrcGenerationRequestTransactionRequest trxInfo = new UpiQrcGenerationRequestTransactionRequest();

		Timestamp currentTimeStamp = new Timestamp(Calendar.getInstance().getTimeInMillis());
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddhhmmss");

		String upiInsId=instIdMap.get(qrGenerationRequest.getInstitutionCode())==null?qrGenerationRequest.getInstitutionCode():instIdMap.get(qrGenerationRequest.getInstitutionCode());

		String msgId = UtilComponents.getMsgId(upiInsId);

		msgInfo.setVersionNo("1.0.0");
		msgInfo.setMsgId(msgId);
		msgInfo.setTimeStamp(formatter.format(currentTimeStamp));
		msgInfo.setMsgType("QRC_GENERATION");
		msgInfo.setInsID(upiInsId);

		trxInfo.setDeviceID(deviceId);
		trxInfo.setUserID(userId);
		trxInfo.setToken(qrGenerationRequest.getSofAccountId());
		trxInfo.setTrxLimit(qrGenerationRequest.getTransactionDomainData().getTxnLimitAmount());
		trxInfo.setCvmLimit(qrGenerationRequest.getTransactionDomainData().getCvmLimitAmount());
		trxInfo.setLimitCurrency(qrGenerationRequest.getTransactionDomainData().getAmountLimitCurrency());
		trxInfo.setCpqrcNo("01");

		upiQrcGenerationRequest.setMsgInfo(msgInfo);
		upiQrcGenerationRequest.setTrxInfo(trxInfo);

		return upiQrcGenerationRequest;

	}

	private UpiQrcGenerationResponse createUpiQrcGenerationResponse(UpiQrcGenerationRequest qrGenReq) {
		UpiQrcGenerationResponse qrGenResp = new UpiQrcGenerationResponse();
		qrGenResp.setMsgInfo(qrGenReq.getMsgInfo());

		UpiQrcGenerationTransactionResponse qrGenTrxRes = new UpiQrcGenerationTransactionResponse();
		qrGenTrxRes.setBarcodeCpqrcPayload("abcd");
		qrGenTrxRes.setCpqrcNo("12345");
		qrGenTrxRes.setEmvCpqrcPayload("2r23r23");

		MessageResponse msgResponse = new MessageResponse();
		msgResponse.setResponseCode("00");
		msgResponse.setResponseMsg("Approved");

		qrGenResp.setTrxInfo(qrGenTrxRes);
		qrGenResp.setMsgResponse(msgResponse);

		return qrGenResp;
	}
	
	@Test
	public void validateSuccessCase() throws BaseBusinessException {
		QrcGenerationRequest request = createQrcGenerationRequest();
		UpiQrcGenerationRequest upiQrcGenerationRequest = createUpiQrcGenerationRequest(request);

		when(mockUpiQrcGenerationRequestAdapter.handleQrGenReq(request)).thenReturn(upiQrcGenerationRequest);
		UpiQrcGenerationResponse upiQrcGenerationResponse = createUpiQrcGenerationResponse(upiQrcGenerationRequest);

		String proxyResponse = UtilComponents.getStringFromObject(upiQrcGenerationResponse);
		when(mockUpiProxyReceiver.receiveMessage()).thenReturn(proxyResponse);

		UpiQrcGenerationResponse response = unit.sendAndReceiveFromUpi(request);

		verify(mockUpiQrcGenerationRequestAdapter, times(1)).handleQrGenReq(request);
		verify(mockUpiProxySender, times(1)).put(upiQrcGenerationRequest);
		verify(mockUpiProxyReceiver, times(1)).receiveMessage();
	}
	
	@Test
	public void validateNullUpiProxyResponse() {
		
		QrcGenerationRequest request = createQrcGenerationRequest();
		UpiQrcGenerationRequest upiQrcGenerationRequest = createUpiQrcGenerationRequest(request);

		when(mockUpiQrcGenerationRequestAdapter.handleQrGenReq(request)).thenReturn(upiQrcGenerationRequest);

		when(mockUpiProxyReceiver.receiveMessage()).thenReturn(null);

		BaseBusinessException e = assertThrows(BaseBusinessException.class, () -> unit.sendAndReceiveFromUpi(request));
		
		assertEquals("68", e.getCode());
		assertEquals("Order Timeout.", e.getErrorMessage());
		
		verify(mockUpiQrcGenerationRequestAdapter, times(1)).handleQrGenReq(request);
		verify(mockUpiProxySender, times(1)).put(upiQrcGenerationRequest);
		verify(mockUpiProxyReceiver, times(1)).receiveMessage();
	}






}
