package com.nets.nps.paynow.service.impl;

import static com.nets.upos.commons.validations.helper.ObjectValidationHelpers.notNull;
import static com.nets.upos.commons.validations.helper.DateValidationHelpers.validateMMddhhmmss;
import static com.nets.upos.commons.validations.helper.StringValidationHelpers.isEqualLength;
import static com.nets.upos.commons.validations.helper.StringValidationHelpers.lessThanOrEqualLength;
import static com.nets.upos.commons.validations.helper.StringValidationHelpers.notEmpty;
import static com.nets.upos.commons.validations.helper.StringValidationHelpers.numeric;

import org.springframework.stereotype.Service;

import com.nets.nps.paynow.entity.TransactionEnquiryData;
import com.nets.nps.paynow.entity.TransactionEnquiryRequest;
import com.nets.upos.commons.exception.JsonFormatException;
import com.nets.upos.commons.validations.MessageFormatValidationErrorCodes;

@Service
public class TransactionEnquiryRequestValidator {
	
	public TransactionEnquiryRequest validate(TransactionEnquiryRequest request) throws JsonFormatException {
		
		notEmpty.and(lessThanOrEqualLength(20)).test(request.getRetrievalRef()).throwIfInvalid(MessageFormatValidationErrorCodes.INVALID_RETRIVAL_REFERENCE_NUMBER);
		notEmpty.and(numeric.and(isEqualLength(11))).test(request.getInstitutionCode()).throwIfInvalid(MessageFormatValidationErrorCodes.INVALID_INSTITUTION_CODE);
		notEmpty.and(validateMMddhhmmss.and(isEqualLength(10))).test(request.getTransmissionTime()).throwIfInvalid(MessageFormatValidationErrorCodes.INVALID_TRANSMISSION_TIME);
		notEmpty.and(isEqualLength(3)).test(request.getTransactionType()).throwIfInvalid(MessageFormatValidationErrorCodes.INVALID_TRANSACTION_TYPE);
		notNull.test(request.getTransactionEnquiryData()).throwIfInvalid(MessageFormatValidationErrorCodes.INVALID_TXN_ENQUIRY_DATA);
		
		//TransactionEnquiryData check
		TransactionEnquiryData transactionEnquiryData = request.getTransactionEnquiryData();
		notEmpty.and(lessThanOrEqualLength(40)).test(transactionEnquiryData.getReceivingAccountNumber()).throwIfInvalid(MessageFormatValidationErrorCodes.INVALID_RECEIVING_ACCOUNT_NUMBER);
		notEmpty.and(isEqualLength(3)).test(transactionEnquiryData.getReceivingAccountType()).throwIfInvalid(MessageFormatValidationErrorCodes.INVALID_RECEIVING_ACCOUNT_TYPE);
		notEmpty.and(lessThanOrEqualLength(40)).test(transactionEnquiryData.getMerchantReferenceNumber()).throwIfInvalid(MessageFormatValidationErrorCodes.INVALID_MERCHANT_REFERENCE_NUMBER);
		notEmpty.and(validateMMddhhmmss.and(isEqualLength(10))).test(transactionEnquiryData.getCreditingStartTime()).throwIfInvalid(MessageFormatValidationErrorCodes.INVALID_CREDITING_TIME);
		notEmpty.and(validateMMddhhmmss.and(isEqualLength(10))).test(transactionEnquiryData.getCreditingEndTime()).throwIfInvalid(MessageFormatValidationErrorCodes.INVALID_CREDITING_TIME);
		
		return request;
	}
	
}
