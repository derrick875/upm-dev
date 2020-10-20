package com.nets.nps.paynow.service.impl;

import static com.nets.upos.commons.validations.helper.DateValidationHelpers.validateMMddhhmmss;
import static com.nets.upos.commons.validations.helper.ObjectValidationHelpers.notNull;
import static com.nets.upos.commons.validations.helper.StringValidationHelpers.fieldNotExist;
import static com.nets.upos.commons.validations.helper.StringValidationHelpers.isEqual;
import static com.nets.upos.commons.validations.helper.StringValidationHelpers.isEqualLength;
import static com.nets.upos.commons.validations.helper.StringValidationHelpers.lessThanOrEqualLength;
import static com.nets.upos.commons.validations.helper.StringValidationHelpers.notEmpty;
import static com.nets.upos.commons.validations.helper.StringValidationHelpers.numeric;

import org.springframework.stereotype.Service;

import com.nets.nps.paynow.entity.CreditNotificationRequest;
import com.nets.upos.commons.exception.JsonFormatException;
import com.nets.upos.commons.validations.MessageFormatValidationErrorCodes;

@Service
public class CreditNotificationRequestValidator {

	public CreditNotificationRequest validate(CreditNotificationRequest request) throws JsonFormatException {

		notEmpty.and(lessThanOrEqualLength(20)).test(request.getRetrievalRef()).throwIfInvalid(MessageFormatValidationErrorCodes.INVALID_RETRIVAL_REFERENCE_NUMBER);
		notEmpty.and(numeric.and(isEqualLength(11))).test(request.getInstitutionCode()).throwIfInvalid(MessageFormatValidationErrorCodes.INVALID_INSTITUTION_CODE);
		notEmpty.and(validateMMddhhmmss.and(isEqualLength(10))).test(request.getTransmissionTime()).throwIfInvalid(MessageFormatValidationErrorCodes.INVALID_TRANSMISSION_TIME);
		notEmpty.and(isEqualLength(3)).test(request.getTransactionType()).throwIfInvalid(MessageFormatValidationErrorCodes.INVALID_TRANSACTION_TYPE);
		notNull.test(request.getTransactionDomainData()).throwIfInvalid(MessageFormatValidationErrorCodes.INVALID_TXN_DOMAIN_DATA);
		numeric.and(isEqualLength(12)).test(request.getTransactionDomainData().getCreditAmount()).throwIfInvalid(MessageFormatValidationErrorCodes.INVALID_CREDIT_AMOUNT);
		notEmpty.and(isEqualLength(3)).test(request.getTransactionDomainData().getCreditCurrency()).throwIfInvalid(MessageFormatValidationErrorCodes.INVALID_CREDIT_CURRENCY);
		notEmpty.and(validateMMddhhmmss.and(isEqualLength(10))).test(request.getTransactionDomainData().getCreditingTime()).throwIfInvalid(MessageFormatValidationErrorCodes.INVALID_CREDITING_TIME);
		notEmpty.and(lessThanOrEqualLength(40)).test(request.getTransactionDomainData().getReceivingAccountNumber()).throwIfInvalid(MessageFormatValidationErrorCodes.INVALID_RECEIVING_ACCOUNT_NUMBER);
		notEmpty.and(isEqualLength(3).and(isEqual("OWN").or(isEqual("OBO").or(isEqual("NTY"))))).test(request.getTransactionDomainData().getReceivingAccountType()).throwIfInvalid(MessageFormatValidationErrorCodes.INVALID_RECEIVING_ACCOUNT_TYPE);
		notEmpty.and(lessThanOrEqualLength(40)).test(request.getTransactionDomainData().getMerchantReferenceNumber()).throwIfInvalid(MessageFormatValidationErrorCodes.INVALID_MERCHANT_REFERENCE_NUMBER);
		fieldNotExist.or(numeric.and(lessThanOrEqualLength(40))).test(request.getTransactionDomainData().getSendingAccountBank()).throwIfInvalid(MessageFormatValidationErrorCodes.INVALID_SENDING_ACCOUNT_BANK);
		fieldNotExist.or(numeric.and(lessThanOrEqualLength(40))).test(request.getTransactionDomainData().getSendingAccountNumber()).throwIfInvalid(MessageFormatValidationErrorCodes.INVALID_SENDING_ACCOUNT_NUMBER);
		fieldNotExist.or(lessThanOrEqualLength(80)).test(request.getTransactionDomainData().getSendingAccountName()).throwIfInvalid(MessageFormatValidationErrorCodes.INVALID_SENDING_ACCOUNT_NAME);
		notEmpty.and(lessThanOrEqualLength(35)).test(request.getTransactionDomainData().getAdditionalBankReference()).throwIfInvalid(MessageFormatValidationErrorCodes.INVALID_ADDITIONAL_BANK_REFERENCE);
		
		return request;

	}

}
