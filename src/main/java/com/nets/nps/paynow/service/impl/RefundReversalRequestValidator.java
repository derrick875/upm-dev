package com.nets.nps.paynow.service.impl;

import static com.nets.upos.commons.validations.helper.DateValidationHelpers.validateMMddhhmmss;
import static com.nets.upos.commons.validations.helper.StringValidationHelpers.isEqualLength;
import static com.nets.upos.commons.validations.helper.StringValidationHelpers.lessThanOrEqualLength;
import static com.nets.upos.commons.validations.helper.StringValidationHelpers.notEmpty;
import static com.nets.upos.commons.validations.helper.StringValidationHelpers.numeric;
import static com.nets.upos.commons.validations.helper.StringValidationHelpers.isEqual;
import static com.nets.upos.commons.validations.helper.StringValidationHelpers.fieldNotExist;
import static com.nets.upos.commons.validations.helper.ObjectValidationHelpers.notNull;

import org.springframework.stereotype.Service;

import com.nets.nps.paynow.entity.RefundReversalRequest;
import com.nets.nps.paynow.entity.RefundReversalTransactionDomainData;
import com.nets.upos.commons.exception.JsonFormatException;
import com.nets.upos.commons.validations.MessageFormatValidationErrorCodes;

@Service
public class RefundReversalRequestValidator {

	public RefundReversalRequest validate(RefundReversalRequest request) throws JsonFormatException {
		
		notEmpty.and(lessThanOrEqualLength(20)).test(request.getRetrievalRef()).throwIfInvalid(MessageFormatValidationErrorCodes.INVALID_RETRIVAL_REFERENCE_NUMBER);
		notEmpty.and(numeric.and(isEqualLength(11))).test(request.getInstitutionCode()).throwIfInvalid(MessageFormatValidationErrorCodes.INVALID_INSTITUTION_CODE);
		notEmpty.and(numeric.and(validateMMddhhmmss.and(isEqualLength(10)))).test(request.getTransmissionTime()).throwIfInvalid(MessageFormatValidationErrorCodes.INVALID_TRANSMISSION_TIME);
		notEmpty.and(isEqualLength(3).and(isEqual("REF").or(isEqual("REV")))).test(request.getTransactionType()).throwIfInvalid(MessageFormatValidationErrorCodes.INVALID_TRANSACTION_TYPE);
		notNull.test(request.getTransactionDomainData()).throwIfInvalid(MessageFormatValidationErrorCodes.INVALID_TXN_DOMAIN_DATA);
		
		RefundReversalTransactionDomainData transactionDomainData = request.getTransactionDomainData();
		notEmpty.and(numeric.and(isEqualLength(12))).test(transactionDomainData.getCreditAmount()).throwIfInvalid(MessageFormatValidationErrorCodes.INVALID_CREDIT_AMOUNT);
		notEmpty.and(isEqualLength(3)).test(transactionDomainData.getCreditCurrency()).throwIfInvalid(MessageFormatValidationErrorCodes.INVALID_CREDIT_CURRENCY);
		notEmpty.and(numeric.and(validateMMddhhmmss.and(isEqualLength(10)))).test(transactionDomainData.getCreditingTime()).throwIfInvalid(MessageFormatValidationErrorCodes.INVALID_CREDITING_TIME);
		notEmpty.and(lessThanOrEqualLength(40)).test(transactionDomainData.getReceivingAccountNumber()).throwIfInvalid(MessageFormatValidationErrorCodes.INVALID_RECEIVING_ACCOUNT_NUMBER);
		notEmpty.and(isEqualLength(3).and(isEqual("OWN").or(isEqual("OBO")))).test(transactionDomainData.getReceivingAccountType()).throwIfInvalid(MessageFormatValidationErrorCodes.INVALID_RECEIVING_ACCOUNT_TYPE);
		notEmpty.and(lessThanOrEqualLength(40)).test(transactionDomainData.getMerchantReferenceNumber()).throwIfInvalid(MessageFormatValidationErrorCodes.INVALID_MERCHANT_REFERENCE_NUMBER);
		notEmpty.and(numeric.and(isEqualLength(12))).test(transactionDomainData.getReversalRefundAmount()).throwIfInvalid(MessageFormatValidationErrorCodes.INVALID_REVERSAL_REFUND_AMOUNT);
		notEmpty.and(isEqualLength(3)).test(transactionDomainData.getReversalRefundCurrency()).throwIfInvalid(MessageFormatValidationErrorCodes.INVALID_REFUND_CURRENCY);
		notEmpty.and(isEqualLength(2).and(isEqual("AC").or(isEqual("PN")))).test(transactionDomainData.getReversalRefundTarget()).throwIfInvalid(MessageFormatValidationErrorCodes.INVALID_REVERSAL_REFUND_TARGET);
		fieldNotExist.or(numeric.and(lessThanOrEqualLength(40))).test(transactionDomainData.getSendingAccountBank()).throwIfInvalid(MessageFormatValidationErrorCodes.INVALID_SENDING_ACCOUNT_BANK);
		fieldNotExist.or(numeric.and(lessThanOrEqualLength(40))).test(transactionDomainData.getSendingAccountNumber()).throwIfInvalid(MessageFormatValidationErrorCodes.INVALID_SENDING_ACCOUNT_NUMBER);
		fieldNotExist.or(lessThanOrEqualLength(80)).test(transactionDomainData.getSendingAccountName()).throwIfInvalid(MessageFormatValidationErrorCodes.INVALID_SENDING_ACCOUNT_NAME);
		fieldNotExist.or(lessThanOrEqualLength(100)).test(transactionDomainData.getSendingProxy()).throwIfInvalid(MessageFormatValidationErrorCodes.INVALID_SENDING_PROXY);
		fieldNotExist.or(lessThanOrEqualLength(20).and(isEqual("MOBILE").or(isEqual("NRIC").or(isEqual("UEN"))))).test(transactionDomainData.getSendingProxyType()).throwIfInvalid(MessageFormatValidationErrorCodes.INVALID_SENDING_PROXY_TYPE);
		notEmpty.and(lessThanOrEqualLength(20)).test(transactionDomainData.getOriginalRetrievalReference()).throwIfInvalid(MessageFormatValidationErrorCodes.INVALID_ORIGINAL_RETRIEVAL_REFERENCE);
		notEmpty.and(lessThanOrEqualLength(200)).test(transactionDomainData.getAdditionalBankReference()).throwIfInvalid(MessageFormatValidationErrorCodes.INVALID_ADDITIONAL_BANK_REFERENCE);
		fieldNotExist.or(lessThanOrEqualLength(200)).test(transactionDomainData.getReversalRefundReference()).throwIfInvalid(MessageFormatValidationErrorCodes.INVALID_REVERSAL_REFUND_REFERENCE);

		return request;	
	}
}
