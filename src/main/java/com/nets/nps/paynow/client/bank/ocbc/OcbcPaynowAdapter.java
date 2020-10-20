package com.nets.nps.paynow.client.bank.ocbc;

import java.sql.Timestamp;
import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.function.Consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nets.upos.commons.validations.BusinessValidationErrorCodes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.nets.nps.paynow.client.bank.PaynowBankAdapter;
import com.nets.nps.paynow.entity.CreditNotificationRequest;
import com.nets.nps.paynow.entity.PaymentTransactionDomainData;
import com.nets.nps.paynow.entity.RefundReversalRequest;
import com.nets.nps.paynow.entity.RefundReversalResponse;
import com.nets.nps.paynow.entity.RefundReversalTransactionDomainData;
import com.nets.nps.paynow.entity.TransactionEnquiryRequest;
import com.nets.nps.paynow.entity.TransactionEnquiryResponse;
import com.nets.nps.paynow.utils.PaynowUtil;
import com.nets.upos.commons.exception.BaseBusinessException;
import com.nets.upos.commons.logger.ApsLogger;

import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

@Service
public class OcbcPaynowAdapter implements PaynowBankAdapter {
	private static final ApsLogger logger = new ApsLogger(OcbcPaynowAdapter.class);

	public static final String TRANSACTION_REFUND_API_PATH = "/nets/refund/1.0/transactionrefund";

	public static final String HEADER_TIMESTAMP = "Timestamp";
	public static final String HEADER_SIGNATURE = "Signature";
	public static final String HEADER_CLIENT_ID = "Client-Id";
	public static final String HEADER_TEMP_PAYLOAD = "TEMP_PAYLOAD";

	@Autowired
	@Qualifier("ocbc-paynow-webclient")
	private WebClient webClient;

	@Value("${paynow.ocbc.retry.count}")
	private int retryLimit;

	@Value("${bank.ocbc.api.retry.waitperiod}")
	int waitPeriod;

	@Value("${bank.ocbc.api.retry.maxattemps}")
	int maxAttempts;

	@Value("${paynow.ocbc.settlement.cutoff}")
	private String settlementCutoffTimeString;

	@Autowired
	Signer signer;

	private DateTimeFormatter timeFormat = new DateTimeFormatterBuilder().appendPattern("hh:mm:ss a").toFormatter();

	@Override
	public RefundReversalResponse processRefundReversal(RefundReversalRequest refundReversalRequest)
			throws BaseBusinessException {

		logger.info("Reversal Start  for:" + refundReversalRequest.toString());

		try {
			ObjectMapper objectMapper = new ObjectMapper();
			String payload = objectMapper.writeValueAsString(refundReversalRequest);
			logger.info("Payload: " + payload);

			RefundReversalResponse respone =
					webClient.post().uri(TRANSACTION_REFUND_API_PATH)
							.body(Mono.just(payload), String.class)
							.headers(headers(payload))
							.retrieve().bodyToMono(RefundReversalResponse.class)
							.retryWhen(Retry.fixedDelay(maxAttempts, Duration.ofMillis(waitPeriod))).block();

			logger.info("Response: " + respone);
			return respone;
		} catch (Exception e) {
			logger.error("Failed to call OCBC Refund API.", e);
			throw new BaseBusinessException(BusinessValidationErrorCodes.REVERSAL_FAILED);
		}

	}

	protected Consumer<HttpHeaders> headers(String payload) {
		return headers -> {
			try {
				headers.add(HEADER_TEMP_PAYLOAD, payload);
			} catch (Exception e) {
				logger.error("Error when set the customized header", e);
				throw new RuntimeException(e);
			}
		};
	}

	@Override
	public RefundReversalResponse processRefundReversalDuetoException(
			CreditNotificationRequest creditNotificationRequest) throws BaseBusinessException {

		RefundReversalRequest rrr = convertCreditNotiReqToRefundRevReq(creditNotificationRequest);
		return processRefundReversal(rrr);
	}

	private RefundReversalRequest convertCreditNotiReqToRefundRevReq(
			CreditNotificationRequest creditNotificationRequest) {

		PaymentTransactionDomainData ptdd = creditNotificationRequest.getTransactionDomainData();

		RefundReversalRequest rrr = new RefundReversalRequest();
		RefundReversalTransactionDomainData rtdd = new RefundReversalTransactionDomainData();
		// set values
		rtdd.setCreditAmount(ptdd.getCreditAmount());
		rtdd.setCreditCurrency(ptdd.getCreditCurrency());
		rtdd.setCreditingTime(ptdd.getCreditingTime());
		rtdd.setReceivingAccountNumber(ptdd.getReceivingAccountNumber());
		rtdd.setReceivingAccountType(ptdd.getReceivingAccountType());
		rtdd.setMerchantReferenceNumber(ptdd.getMerchantReferenceNumber());
		rtdd.setReversalRefundAmount(ptdd.getCreditAmount());
		rtdd.setReversalRefundCurrency(ptdd.getCreditCurrency());
		rtdd.setReversalRefundTarget("AC");
		rtdd.setSendingAccountBank(ptdd.getSendingAccountBank());
		rtdd.setSendingAccountNumber(ptdd.getSendingAccountNumber());
		rtdd.setSendingAccountName(ptdd.getSendingAccountName());
		rtdd.setSendingProxy(ptdd.getBankMerchantProxy());
		// rtdd.setSendingProxyType("UEN");
		rtdd.setOriginalRetrievalReference(creditNotificationRequest.getRetrievalRef());
		rtdd.setAdditionalBankReference(ptdd.getAdditionalBankReference());

		rrr.setMti("8400");
		rrr.setProcessCode("440000");
		rrr.setRetrievalRef(creditNotificationRequest.getRetrievalRef());
		rrr.setInstitutionCode(creditNotificationRequest.getInstitutionCode());
		rrr.setTransmissionTime(creditNotificationRequest.getTransmissionTime());
		rrr.setTransactionType("REV");
		rrr.setTransactionDomainData(rtdd);
		return rrr;
	}

	@Override
	public Timestamp getSettlementDate() {
		Timestamp settlementDate = null;
		LocalTime settlementCutoffTime = LocalTime.parse(settlementCutoffTimeString, timeFormat);
		settlementDate = PaynowUtil.getSettlementDate(settlementCutoffTime);
		return settlementDate;
	}

	protected Consumer<HttpHeaders> headers(String resourcePath, String payload) {
		return headers -> {
			try {
				String authorization = headers.getFirst(HttpHeaders.AUTHORIZATION);
				String timestamp = String.valueOf(System.currentTimeMillis());

				// Refer OCBC API Gateway Specification 3.2.1
				String input = authorization + timestamp + resourcePath + payload;

				headers.add(HEADER_TIMESTAMP, timestamp);
				headers.add(HEADER_SIGNATURE, signer.sign(input).toString());
			} catch (Exception e) {
				logger.error("Error when set the customized header");
			}
		};
	}

}
