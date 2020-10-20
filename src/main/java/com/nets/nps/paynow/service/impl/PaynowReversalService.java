package com.nets.nps.paynow.service.impl;

import static com.nets.upos.commons.validations.helper.BooleanValidationHelpers.isAccepted;
import static com.nets.upos.commons.validations.helper.StringValidationHelpers.isEqualLength;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import com.nets.nps.client.impl.NonStopClient;
import com.nets.nps.client.impl.TransactionStateMsg;
import com.nets.nps.core.service.PaymentService;
import com.nets.nps.paynow.client.bank.BankAdapterProcessingFactory;
import com.nets.nps.paynow.entity.RefundReversalRequest;
import com.nets.nps.paynow.entity.RefundReversalTransactionDomainData;
import com.nets.nps.paynow.entity.ReversalInfo;
import com.nets.nps.paynow.entity.ReversalPaynowOrderResult;
import com.nets.nps.paynow.entity.ReversalRequest;
import com.nets.nps.paynow.entity.ReversalResponse;
import com.nets.nps.paynow.exception.ExceptionHandler;
import com.nets.nps.paynow.service.PaynowTransactionDataService;
import com.nets.nps.paynow.utils.PaynowUtil;
import com.nets.upos.commons.constants.ResponseCodes;
import com.nets.upos.commons.enums.RequestState;
import com.nets.upos.commons.enums.TransactionState;
import com.nets.upos.commons.exception.BaseBusinessException;
import com.nets.upos.commons.exception.JsonFormatException;
import com.nets.upos.commons.logger.ApsLogger;
import com.nets.upos.commons.validations.BusinessValidationErrorCodes;
import com.nets.upos.commons.validations.MessageFormatValidationErrorCodes;
import com.nets.upos.core.entity.PaynowTransactionData;
import com.nets.upos.core.entity.Request;
import com.nets.upos.core.entity.TransactionEntity;
import com.nets.upos.core.service.RequestService;
import com.nets.upos.core.service.TransactionService;

@Service
public class PaynowReversalService implements PaymentService<ReversalRequest, ReversalResponse> {

	private static final ApsLogger logger = new ApsLogger(PaynowReversalService.class);

	@Autowired
	private RequestService requestService;

	@Autowired
	private TransactionService transactionService;

	@Autowired
	private PaynowTransactionDataService paynowTransactionDataService;

	@Autowired
	private NonStopClient nonStopClient;

	@Autowired
	private BankAdapterProcessingFactory bankAdapterProcessingFactory;

	@Autowired
	ExceptionHandler exceptionHandler;

	@Value("${paynow.settlement.cutoff}")
	private String settlementCutoffTimeString;

	public ReversalResponse process(ReversalRequest request) throws BaseBusinessException, JsonFormatException {
		logger.info("PaynowReversalService has started");

		validateMerchantRef(request);
		ReversalInfo reversalInfo = loadOriginalOrder(request);
		if (!Objects.isNull(reversalInfo.getTransactionId())) {
			reversalInfo = loadTransactionAndCheckState(reversalInfo, request);
			reversalInfo = createRefundReversalRequestToBank(reversalInfo, request);
			createAndSendTransactionStateMessage(reversalInfo, request);
		}
		return generateResponse(request, reversalInfo);
	}

	private void validateMerchantRef(ReversalRequest request) throws JsonFormatException {

		isEqualLength(25).test(request.getTransactionEnquiryData().getMerchantReferenceNumber())
				.throwIfInvalid(MessageFormatValidationErrorCodes.INVALID_MERCHANT_REFERENCE_NUMBER);
	}

	private ReversalInfo loadOriginalOrder(ReversalRequest request) throws BaseBusinessException {

		ReversalInfo reversalInfo = new ReversalInfo();

		Request orderRequest = Optional
				.ofNullable(requestService.findBySearchId(
						request.getTransactionEnquiryData().getMerchantReferenceNumber().substring(0, 16)))
				.orElseThrow(() -> new BaseBusinessException(BusinessValidationErrorCodes.ORIGINAL_ORDER_CANNOT_FIND));

		reversalInfo.setTransactionId(orderRequest.getTransactionId());
		// reversalInfo.setOrderState(orderRequest.getState());
		reversalInfo.setPaymentType(orderRequest.getPaymentType());
		reversalInfo.setTid(orderRequest.getTid());
		reversalInfo.setMid(orderRequest.getMid());
		reversalInfo.setStan(orderRequest.getStan());
		reversalInfo.setTransactionDate(orderRequest.getTransactionDate());
		reversalInfo.setTransactionTime(orderRequest.getTransactionTime());

		if (Objects.isNull(orderRequest.getTransactionId())) {
			try {
				orderRequest = checkOrderStatus(orderRequest);
			} catch (OptimisticLockingFailureException e) {
				logger.info("Inside ObjectOptimisticLockingFailureException");
				orderRequest = requestService.refresh(orderRequest);
			}
		}
		return reversalInfo;
	}

	private Request checkOrderStatus(Request orderRequest) throws BaseBusinessException {

		if (orderRequest.isTimedOut()) {
			orderRequest.setState(RequestState.REVERSED.getState());
			requestService.save(orderRequest);
			throw new BaseBusinessException(BusinessValidationErrorCodes.ORIGINAL_ORDER_TIMEOUT);
		} else if (orderRequest.getState().equals(RequestState.REVERSED.getState())) {
			throw new BaseBusinessException(BusinessValidationErrorCodes.ORIGINAL_ORDER_TIMEOUT);

		} else if (orderRequest.getState().equals(RequestState.WAITING.getState())) {
			orderRequest.setState(RequestState.REVERSED.getState());
			requestService.save(orderRequest);
		} else {
			throw new BaseBusinessException("06", "Generic System Error.");
		}
		return orderRequest;
	}

	private ReversalInfo loadTransactionAndCheckState(ReversalInfo reversalInfo, ReversalRequest request)
			throws BaseBusinessException {

		TransactionEntity transaction = Optional
				.ofNullable(transactionService.retrieve(reversalInfo.getTransactionId()))
				.orElseThrow(() -> new BaseBusinessException(BusinessValidationErrorCodes.TRANSACTION_NOT_FOUND));

		PaynowTransactionData paynowTransactionData = Optional
				.ofNullable(paynowTransactionDataService.getPaynowTransactionData(reversalInfo.getTransactionId()))
				.orElseThrow(() -> new BaseBusinessException(BusinessValidationErrorCodes.TRANSACTION_NOT_FOUND));

		reversalInfo.setBankSettleDate(transaction.getBankSettleDate());
		Timestamp currentTimeStamp = new Timestamp(Calendar.getInstance().getTimeInMillis());
		isAccepted(false)
				.test(currentTimeStamp.toLocalDateTime()
						.isAfter((reversalInfo.getBankSettleDate().toLocalDateTime().plusDays(1))))
				.throwIfInvalid(BusinessValidationErrorCodes.ORDER_TIMED_OUT_BEFORE_REVERSAL);

		if (transaction.getState().equals(TransactionState.STATE_SETTLED.getState())
				|| transaction.getState().equals(TransactionState.STATE_OK.getState())) {

			transaction.setState(TransactionState.STATE_REVERSED.getState());
			transactionService.save(transaction);
			Request orderRequest = requestService
					.findBySearchId(request.getTransactionEnquiryData().getMerchantReferenceNumber().substring(0, 16));

			orderRequest.setState(RequestState.REVERSED.getState());
			try {
				requestService.save(orderRequest);
			} catch (OptimisticLockingFailureException e) {
				logger.info("Inside ObjectOptimisticLockingFailureException");
				orderRequest = requestService.refresh(orderRequest);
				orderRequest.setState(RequestState.REVERSED.getState());
				requestService.save(orderRequest);
			}

			reversalInfo.setApprovalCode(transaction.getAcqApprovalCode());
			reversalInfo.setOriginalRetrievalReference(transaction.getAcquirerAuthResponseId());
			reversalInfo.setCreditAmount(StringUtils.leftPad(
					paynowTransactionData.getCreditAmount().multiply(new BigDecimal(100)).toBigInteger().toString(), 12,
					'0'));
			reversalInfo.setCreditCurrency(paynowTransactionData.getCreditCurrency());
			reversalInfo.setCreditingTime(paynowTransactionData.getCreditingTime());
			reversalInfo.setReceivingAccountNumber(paynowTransactionData.getReceivingAccountNumber());
			reversalInfo.setReceivingAccountType(paynowTransactionData.getReceivingAccountType());
			reversalInfo.setSendingAccountBank(paynowTransactionData.getSendingAccountBank());
			reversalInfo.setSendingAccountName(paynowTransactionData.getSendingAccountName());
			reversalInfo.setSendingAccountNumber(paynowTransactionData.getSendingAccountNumber());
			reversalInfo.setMerchantReferenceNumber(paynowTransactionData.getMerchantReferenceNumber());
			reversalInfo.setAdditionalBankReference(paynowTransactionData.getAdditionalBankReference());
			reversalInfo.setBankSettleDate(transaction.getBankSettleDate());
			reversalInfo.setInstitutionCode(paynowTransactionData.getInstitutionCode());
		} else if (transaction.getState().equals(TransactionState.STATE_REVERSED.getState())) {
			throw new BaseBusinessException("68", "Transaction already reversed");

		} else {
			throw new BaseBusinessException("06", "Generic System Error.");
		}
		return reversalInfo;
	}

	private ReversalInfo createRefundReversalRequestToBank(ReversalInfo reversalInfo, ReversalRequest request)
			throws BaseBusinessException {

		DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("MMddHHmmss");

		Timestamp currentTimeStamp = new Timestamp(Calendar.getInstance().getTimeInMillis());

		RefundReversalRequest refundReversalRequest = new RefundReversalRequest();
		refundReversalRequest.setRetrievalRef(request.getRetrievalRef());
		refundReversalRequest.setMti("8400");
		refundReversalRequest.setProcessCode("440000");
		refundReversalRequest.setInstitutionCode(reversalInfo.getInstitutionCode());
		refundReversalRequest.setTransmissionTime(DATE_TIME_FORMATTER.format(currentTimeStamp.toLocalDateTime()));
		refundReversalRequest.setTransactionType("REV");
		RefundReversalTransactionDomainData reversalData = new RefundReversalTransactionDomainData();

		reversalData.setCreditAmount(reversalInfo.getCreditAmount());
		reversalData.setCreditCurrency(reversalInfo.getCreditCurrency());
		reversalData.setCreditingTime(reversalInfo.getCreditingTime());
		reversalData.setReceivingAccountNumber(reversalInfo.getReceivingAccountNumber());
		reversalData.setReceivingAccountType(reversalInfo.getReceivingAccountType());
		reversalData.setMerchantReferenceNumber(reversalInfo.getMerchantReferenceNumber());
		reversalData.setReversalRefundAmount(reversalInfo.getCreditAmount());
		reversalData.setReversalRefundCurrency(reversalInfo.getCreditCurrency());
		reversalData.setReversalRefundTarget("AC");
		reversalData.setSendingAccountBank(reversalInfo.getSendingAccountBank());
		reversalData.setSendingAccountNumber(reversalInfo.getSendingAccountNumber());
		reversalData.setSendingAccountName(reversalInfo.getSendingAccountName());
		// reversalData.setSendingProxy(sendingProxy);
		// reversalData.setSendingProxyType("UEN");
		reversalData.setOriginalRetrievalReference(reversalInfo.getOriginalRetrievalReference());
		reversalData.setAdditionalBankReference(reversalInfo.getAdditionalBankReference());
		// reversalData.setReversalRefundReference("");
		refundReversalRequest.setTransactionDomainData(reversalData);

		reverseTransaction(refundReversalRequest, reversalInfo);

		return reversalInfo;
	}

	private void createAndSendTransactionStateMessage(ReversalInfo reversalInfo, ReversalRequest request) {

		TransactionStateMsg txnStateMsg = new TransactionStateMsg();
		txnStateMsg.setAmount(reversalInfo.getCreditAmount());
		txnStateMsg.setTransmissionTime(request.getTransmissionTime());
		txnStateMsg.setStan(reversalInfo.getStan());
		txnStateMsg.setTransactionTime(reversalInfo.getTransactionTime());
		txnStateMsg.setTransactionDate(reversalInfo.getTransactionDate());
		txnStateMsg.setSettlementDate(PaynowUtil.getTandemSettlementDate(reversalInfo.getBankSettleDate()));
		txnStateMsg.setInstitutionCode(request.getInstitutionCode());
		txnStateMsg.setConditionCode("00");
		txnStateMsg.setResponseCode(ResponseCodes.SUCCESS);
		txnStateMsg.setHostTid(reversalInfo.getTid());
		txnStateMsg.setHostMid(reversalInfo.getMid());
		txnStateMsg.setTransactionType("PNDYNAMICREVERSAL");
		txnStateMsg.setPaymentType(reversalInfo.getPaymentType());
		txnStateMsg.setApprovalCode(reversalInfo.getApprovalCode());
		txnStateMsg.setStatus(TransactionState.STATE_REVERSED.getState());
		txnStateMsg.setInstitutionCode(reversalInfo.getInstitutionCode());
		txnStateMsg.setRetrievalRef(reversalInfo.getOriginalRetrievalReference());
		txnStateMsg.setTransactionId(String.valueOf(reversalInfo.getTransactionId()));
		nonStopClient.put(txnStateMsg);
	}

	private ReversalResponse generateResponse(ReversalRequest reversalReq, ReversalInfo reversalInfo) {
		ReversalResponse response = reversalReq.createResponse();
		response.setResponseCode(ResponseCodes.SUCCESS);
		ReversalPaynowOrderResult paynowOrderResult = new ReversalPaynowOrderResult();
		paynowOrderResult.setStan(reversalInfo.getStan());
		paynowOrderResult.setApprovalCode(reversalInfo.getApprovalCode());
		paynowOrderResult.setMerchantReferenceNumber(reversalInfo.getMerchantReferenceNumber());
		paynowOrderResult.setReceivingBankReference(reversalInfo.getAdditionalBankReference());
		response.setPaynowOrderResult(paynowOrderResult);
		return response;
	}

	private void reverseTransaction(RefundReversalRequest refundReversalRequest, ReversalInfo requestInfo) {
		CompletableFuture.runAsync(() -> {
			logger.info("Refund/Reversal Start for RRN:" + refundReversalRequest.getRetrievalRef()
					+ "  Institutioncode:" + refundReversalRequest.getInstitutionCode());
			try {

				bankAdapterProcessingFactory.getBankAdapter(refundReversalRequest.getInstitutionCode())
						.processRefundReversal(refundReversalRequest);
			} catch (Exception e) {
				logger.info("Refund/Reversal  Failed for RRN:" + refundReversalRequest.getRetrievalRef()
						+ "  Institutioncode:" + refundReversalRequest.getInstitutionCode());
				logger.error(e.getMessage());
				if (e.getMessage() != null) {
					exceptionHandler.handleBusinessException(refundReversalRequest, new BaseBusinessException("06",
							e.getMessage().substring(0, Math.min(e.getMessage().length(), 100))));
				} else {
					exceptionHandler.handleBusinessException(refundReversalRequest,
							new BaseBusinessException("06", "Dynamic QR refund ReversalRequest Failed"));

				}
			}
			logger.info("Refund/Reversal  End for RRN:" + refundReversalRequest.getRetrievalRef() + "  Institutioncode:"
					+ refundReversalRequest.getInstitutionCode());
		});
	}
}