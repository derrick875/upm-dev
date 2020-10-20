package com.nets.nps.paynow.service.impl;

import static com.nets.upos.commons.validations.helper.DateValidationHelpers.validateMMddhhmmss;
import static com.nets.upos.commons.validations.helper.ObjectValidationHelpers.notNull;
import static com.nets.upos.commons.validations.helper.StringValidationHelpers.isEqualLength;
import static com.nets.upos.commons.validations.helper.StringValidationHelpers.lessThanOrEqualLength;
import static com.nets.upos.commons.validations.helper.StringValidationHelpers.notEmpty;
import static com.nets.upos.commons.validations.helper.StringValidationHelpers.numeric;
import static com.nets.upos.commons.validations.helper.StringValidationHelpers.fieldNotExist;
import static com.nets.upos.commons.validations.helper.StringValidationHelpers.isEqual;

import org.springframework.stereotype.Service;

import com.nets.nps.paynow.entity.ReversalRequest;
import com.nets.nps.paynow.entity.ReversalTransactionEnquiryData;
import com.nets.upos.commons.exception.JsonFormatException;
import com.nets.upos.commons.validations.MessageFormatValidationErrorCodes;

@Service
public class ReversalRequestValidator {

	public ReversalRequest validate(ReversalRequest request) throws JsonFormatException {
		
		notEmpty.and(lessThanOrEqualLength(20)).test(request.getRetrievalRef()).throwIfInvalid(MessageFormatValidationErrorCodes.INVALID_RETRIVAL_REFERENCE_NUMBER);
		notEmpty.and(numeric.and(isEqualLength(11))).test(request.getInstitutionCode()).throwIfInvalid(MessageFormatValidationErrorCodes.INVALID_INSTITUTION_CODE);
		notEmpty.and(numeric.and(validateMMddhhmmss.and(isEqualLength(10)))).test(request.getTransmissionTime()).throwIfInvalid(MessageFormatValidationErrorCodes.INVALID_TRANSMISSION_TIME);
		notEmpty.and(isEqualLength(3).and(isEqual("PNT").or(isEqual("PNO").or(isEqual("PBC"))))).test(request.getChannelIndicator()).throwIfInvalid(MessageFormatValidationErrorCodes.INVALID_CHANNEL_INDICATOR);
		notNull.test(request.getTransactionEnquiryData()).throwIfInvalid(MessageFormatValidationErrorCodes.INVALID_TXN_ENQUIRY_DATA); 
		
		ReversalTransactionEnquiryData transactionEnquiryData = request.getTransactionEnquiryData();
		notEmpty.and(lessThanOrEqualLength(20)).test(transactionEnquiryData.getOriginalRetrievalReference()).throwIfInvalid(MessageFormatValidationErrorCodes.INVALID_ORIGINAL_RETRIEVAL_REFERENCE);
		fieldNotExist.or(lessThanOrEqualLength(15)).test(transactionEnquiryData.getHostMid()).throwIfInvalid(MessageFormatValidationErrorCodes.INVALID_MID);
		fieldNotExist.or(isEqualLength(8)).test(transactionEnquiryData.getHostTid()).throwIfInvalid(MessageFormatValidationErrorCodes.INVALID_TID);
		fieldNotExist.or(numeric.and(isEqualLength(16))).test(transactionEnquiryData.getNetsPaynowMpan()).throwIfInvalid(MessageFormatValidationErrorCodes.INVALID_NETS_PAYNOW_MPAN);
		fieldNotExist.or(lessThanOrEqualLength(100)).test(transactionEnquiryData.getBankMerchantProxy()).throwIfInvalid(MessageFormatValidationErrorCodes.INVALID_BANK_MERCHANT_PROXY);
		fieldNotExist.or(lessThanOrEqualLength(20)).test(transactionEnquiryData.getBankMerchantProxyType()).throwIfInvalid(MessageFormatValidationErrorCodes.INVALID_BANK_MERCHANT_PROXY_TYPE);
		fieldNotExist.or(lessThanOrEqualLength(40)).test(transactionEnquiryData.getMerchantReferenceNumber()).throwIfInvalid(MessageFormatValidationErrorCodes.INVALID_MERCHANT_REFERENCE_NUMBER);
		fieldNotExist.or(lessThanOrEqualLength(16)).test(transactionEnquiryData.getInvoiceRef()).throwIfInvalid(MessageFormatValidationErrorCodes.INVALID_INVOICE_REF);
		if(notNull.test(transactionEnquiryData.getNpxData()).isvalid()) {
			notNull.test(transactionEnquiryData.getNpxData()).throwIfInvalid(MessageFormatValidationErrorCodes.MISSING_NPX_DATA);
		}
		
		return request;
	}
}
