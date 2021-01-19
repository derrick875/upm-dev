package com.nets.nps.upi.service.impl;

import static com.nets.upos.commons.validations.helper.DateValidationHelpers.validateMMddhhmmss;
import static com.nets.upos.commons.validations.helper.ObjectValidationHelpers.notNull;
import static com.nets.upos.commons.validations.helper.StringValidationHelpers.isEqualLength;
import static com.nets.upos.commons.validations.helper.StringValidationHelpers.lessThanOrEqualLength;
import static com.nets.upos.commons.validations.helper.StringValidationHelpers.notEmpty;
import static com.nets.upos.commons.validations.helper.StringValidationHelpers.numeric;
import static com.nets.upos.commons.validations.helper.StringValidationHelpers.fieldNotExist;


import org.springframework.stereotype.Service;

import com.nets.nps.upi.entity.PullDebitRequest;
import com.nets.nps.upi.entity.PullDebitRequestTransactionDomainData;
import com.nets.upos.commons.exception.JsonFormatException;
import com.nets.upos.commons.validations.MessageFormatValidationErrorCodes;

@Service
public class PullDebitRequestValidator {
	public void validate(PullDebitRequest request) throws JsonFormatException{
		notEmpty.and(lessThanOrEqualLength(20)).test(request.getRetrievalRef()).throwIfInvalid(MessageFormatValidationErrorCodes.INVALID_RETRIVAL_REFERENCE_NUMBER);// typo here
		notEmpty.and(numeric.and(isEqualLength(11))).test(request.getInstitutionCode()).throwIfInvalid(MessageFormatValidationErrorCodes.INVALID_INSTITUTION_CODE);
		notEmpty.and(numeric.and(isEqualLength(11))).test(request.getAcquirerInstitutionCode()).throwIfInvalid(MessageFormatValidationErrorCodes.INVALID_INSTITUTION_CODE); //add invalid acq ins code
		notEmpty.and(lessThanOrEqualLength(200)).test(request.getSofUri()).throwIfInvalid(MessageFormatValidationErrorCodes.INVALID_SOF_URI);
		notEmpty.and(validateMMddhhmmss.and(isEqualLength(10))).test(request.getTransmissionTime()).throwIfInvalid(MessageFormatValidationErrorCodes.INVALID_TRANSMISSION_TIME);
		notEmpty.and(lessThanOrEqualLength(80)).test(request.getSofAccountId()).throwIfInvalid(MessageFormatValidationErrorCodes.INVALID_SOF_ACCOUNT_ID);
		notNull.test(request.getTransactionDomainData()).throwIfInvalid(MessageFormatValidationErrorCodes.INVALID_TXN_DOMAIN_DATA);
		
		// Transaction domain data
		PullDebitRequestTransactionDomainData trxDomainData = request.getTransactionDomainData();
		notEmpty.and(isEqualLength(12)).test(trxDomainData.getAmount()).throwIfInvalid(MessageFormatValidationErrorCodes.INVALID_AMOUNT); //this have special character. does special character count in total length 12?
		notEmpty.and(numeric.and(isEqualLength(3))).test(trxDomainData.getAmountCurrency()).throwIfInvalid(MessageFormatValidationErrorCodes.INVALID_AMOUNT_CURRENCY);
		// discount
		fieldNotExist.and(isEqualLength(12)).test(trxDomainData.getFee()).throwIfInvalid(MessageFormatValidationErrorCodes.INVALID_AMOUNT); // add invalid fee
		fieldNotExist.and(isEqualLength(12)).test(trxDomainData.getConvertedAmount()).throwIfInvalid(MessageFormatValidationErrorCodes.INVALID_CONVERT_CURRENCY); // add invalid converted amount
		notEmpty.and(numeric.and(isEqualLength(3))).test(trxDomainData.getConvertCurrency()).throwIfInvalid(MessageFormatValidationErrorCodes.INVALID_CONVERT_CURRENCY);
		fieldNotExist.and(numeric.and(isEqualLength(15))).test(trxDomainData.getConversionRate()).throwIfInvalid(MessageFormatValidationErrorCodes.INVALID_CONVERT_CURRENCY); // add invalid conversion rate
		notEmpty.and(lessThanOrEqualLength(2)).test(trxDomainData.getTransactionType()).throwIfInvalid(MessageFormatValidationErrorCodes.INVALID_TRANSACTION_TYPE);
		notEmpty.and(lessThanOrEqualLength(80)).test(trxDomainData.getCpmqrpaymentToken()).throwIfInvalid(MessageFormatValidationErrorCodes.INVALID_AMOUNT); // add invalid cpmqr payment token
//		fieldNotExist additional pull debit data
	}
}
