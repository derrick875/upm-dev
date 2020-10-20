package com.nets.nps.paynow.service.impl;

import static com.nets.upos.commons.validations.helper.StringValidationHelpers.isEqualLength;

import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.nets.nps.core.service.PaymentService;
import com.nets.nps.paynow.entity.OrderQueryPaynowOrderResult;
import com.nets.nps.paynow.entity.OrderQueryRequest;
import com.nets.nps.paynow.entity.OrderQueryResponse;
import com.nets.nps.paynow.service.PaynowTransactionDataService;
import com.nets.upos.commons.constants.ResponseCodes;
import com.nets.upos.commons.enums.RequestState;
import com.nets.upos.commons.enums.TransactionState;
import com.nets.upos.commons.exception.BaseBusinessException;
import com.nets.upos.commons.exception.JsonFormatException;
import com.nets.upos.commons.logger.ApsLogger;
import com.nets.upos.commons.validations.MessageFormatValidationErrorCodes;
import com.nets.upos.core.entity.PaynowTransactionData;
import com.nets.upos.core.entity.Request;
import com.nets.upos.core.entity.TransactionEntity;
import com.nets.upos.core.service.RequestService;
import com.nets.upos.core.service.TransactionService;

@Service
public class PaynowOrderQueryService implements PaymentService<OrderQueryRequest,OrderQueryResponse> {

	private static final ApsLogger logger = new ApsLogger(PaynowOrderQueryService.class);

	@Autowired
	private RequestService requestService;

	@Autowired
	private TransactionService transactionService;

	@Autowired
	private PaynowTransactionDataService paynowTransactionDataService;

	@Value("${paynow.maximum.searchcount}")
	private String maxSearchCount;

	public OrderQueryResponse process(OrderQueryRequest orderQueryReq)
			throws JsonFormatException, BaseBusinessException {
		logger.info("PaynowOrderQueryService has started");
		isEqualLength(25).test(orderQueryReq.getTransactionEnquiryData().getMerchantReferenceNumber())
				.throwIfInvalid(MessageFormatValidationErrorCodes.INVALID_MERCHANT_REFERENCE_NUMBER);

		Request orderRequest = requestService
				.findBySearchId(orderQueryReq.getTransactionEnquiryData().getMerchantReferenceNumber().substring(0, 16));
		OrderQueryResponse orderQueryResponse = orderQueryReq.createResponse();
		if (Objects.isNull(orderRequest)) {
			throw new BaseBusinessException("15", "Original order can't Find");
		}
		if (Objects.isNull(orderRequest.getTransactionId())) {
			if (Integer.parseInt(orderQueryReq.getTransactionEnquiryData().getRetryAttempt()) < Integer
					.parseInt(maxSearchCount) && orderRequest.getState() == (RequestState.WAITING.getState())) {
				throw new BaseBusinessException("03", "Order Status Unknown");
			}
			if (Integer.parseInt(orderQueryReq.getTransactionEnquiryData().getRetryAttempt()) == Integer
					.parseInt(maxSearchCount) && orderRequest.getState() == RequestState.WAITING.getState()) {
				throw new BaseBusinessException("03", "Order Status Unknown");
			}
			if (orderRequest.getState() == RequestState.TIMED_OUT.getState()) {
				throw new BaseBusinessException("68", "Timeout");
			}
			if (orderRequest.getState() == RequestState.REVERSED.getState()) {
				throw new BaseBusinessException("68", "Timeout");
			}

			else {
				throw new BaseBusinessException("06", "System Error");
			}
		} else {
			TransactionEntity transaction = transactionService.retrieve(orderRequest.getTransactionId());
			if (Objects.isNull(transaction)) {
				throw new BaseBusinessException("12", "Invalid Transaction");
			}

			PaynowTransactionData paynowTransactionData = paynowTransactionDataService
					.getPaynowTransactionData(orderRequest.getTransactionId());
			if (Objects.isNull(paynowTransactionData)) {
				throw new BaseBusinessException("12", "Invalid Transaction");
			}

			if (transaction.getState().equals(TransactionState.STATE_SETTLED.getState())|| transaction.getState().equals(TransactionState.STATE_OK.getState())) {
				orderQueryResponse.setResponseCode(ResponseCodes.SUCCESS);
				OrderQueryPaynowOrderResult orderQueryPaynowOrderResult = new OrderQueryPaynowOrderResult();
				orderQueryPaynowOrderResult.setStan(orderRequest.getStan());
				orderQueryPaynowOrderResult.setApprovalCode(transaction.getAcqApprovalCode());
				orderQueryPaynowOrderResult.setMerchantReferenceNumber(paynowTransactionData.getMerchantReferenceNumber());
				orderQueryPaynowOrderResult.setReceivingBankReference(paynowTransactionData.getAdditionalBankReference());
				orderQueryResponse.setPaynowOrderResult(orderQueryPaynowOrderResult);
				

			}

			// TODO change error code/msg
			if (transaction.getState().equals(TransactionState.STATE_REVERSED.getState())) {
				throw new BaseBusinessException("68", "Timeout");
			}
			// TODO change error code/msg
			if (transaction.getState().equals(TransactionState.STATE_REVERSAL_FAILED.getState())) {
				throw new BaseBusinessException("68", "Timeout");
			}
		}
		return orderQueryResponse;

	}
}
