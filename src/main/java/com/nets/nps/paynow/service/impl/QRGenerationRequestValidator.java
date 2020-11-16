package com.nets.nps.paynow.service.impl;

import static com.nets.upos.commons.validations.helper.DateValidationHelpers.validateMMddhhmmss;
import static com.nets.upos.commons.validations.helper.ObjectValidationHelpers.notNull;
import static com.nets.upos.commons.validations.helper.StringValidationHelpers.isEqualLength;
import static com.nets.upos.commons.validations.helper.StringValidationHelpers.lessThanOrEqualLength;
import static com.nets.upos.commons.validations.helper.StringValidationHelpers.notEmpty;
import static com.nets.upos.commons.validations.helper.StringValidationHelpers.numeric;
import static com.nets.upos.commons.validations.helper.StringValidationHelpers.fieldNotExist;

import org.springframework.stereotype.Service;

import com.nets.nps.paynow.entity.QrcGenerationTransactionDomainData;
import com.nets.nps.paynow.entity.QrcGenerationRequest;
import com.nets.upos.commons.exception.JsonFormatException;
import com.nets.upos.commons.validations.MessageFormatValidationErrorCodes;

@Service
public class QRGenerationRequestValidator {
	
	public QrcGenerationRequest validate(QrcGenerationRequest request) throws JsonFormatException {
		
		notEmpty.and(lessThanOrEqualLength(20)).test(request.getRetrievalRef()).throwIfInvalid(MessageFormatValidationErrorCodes.INVALID_RETRIVAL_REFERENCE_NUMBER);
		notEmpty.and(numeric.and(isEqualLength(11))).test(request.getInstitutionCode()).throwIfInvalid(MessageFormatValidationErrorCodes.INVALID_INSTITUTION_CODE);
		notEmpty.and(numeric.and(isEqualLength(11))).test(request.getAcquirerInstitutionCode()).throwIfInvalid(MessageFormatValidationErrorCodes.INVALID_INSTITUTION_CODE);
		//need to add invalid acquirer institution code
		notEmpty.and(lessThanOrEqualLength(200)).test(request.getSofUri()).throwIfInvalid(MessageFormatValidationErrorCodes.INVALID_SOF_URI);
		notEmpty.and(validateMMddhhmmss.and(isEqualLength(10))).test(request.getTransmissionTime()).throwIfInvalid(MessageFormatValidationErrorCodes.INVALID_TRANSMISSION_TIME);
		notEmpty.and(lessThanOrEqualLength(80)).test(request.getSofAccountId()).throwIfInvalid(MessageFormatValidationErrorCodes.INVALID_SOF_ACCOUNT_ID);
		notNull.test(request.getTransactionDomainData()).throwIfInvalid(MessageFormatValidationErrorCodes.INVALID_TXN_DOMAIN_DATA); 

		// Transaction Domain Data check
		QrcGenerationTransactionDomainData transactionDomainData = request.getTransactionDomainData();
		notEmpty.and(numeric.and(isEqualLength(12))).test(transactionDomainData.getTxnLimitAmount()).throwIfInvalid(MessageFormatValidationErrorCodes.INVALID_AMOUNT);
		notEmpty.and(numeric.and(isEqualLength(12))).test(transactionDomainData.getCvmLimitAmount()).throwIfInvalid(MessageFormatValidationErrorCodes.INVALID_ADDITIONAL_BANK_REFERENCE);
		notEmpty.and(isEqualLength(3)).test(transactionDomainData.getAmountLimitCurrency()).throwIfInvalid(MessageFormatValidationErrorCodes.INVALID_ADDITIONAL_BANK_REFERENCE);
		//need to add invalid txn limit amount and cvm limit amount and amount limit currency
		notEmpty.and(lessThanOrEqualLength(2)).test(transactionDomainData.getTransactionType()).throwIfInvalid(MessageFormatValidationErrorCodes.INVALID_TRANSACTION_TYPE);
		fieldNotExist.or(lessThanOrEqualLength(30)).test(transactionDomainData.getDiscountCouponInfo()).throwIfInvalid(MessageFormatValidationErrorCodes.INVALID_ADDITIONAL_BANK_REFERENCE);
		//need to add invalid discount coupon info
		
		return request;
	}
}
