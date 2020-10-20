package com.nets.nps.paynow.service.impl;

import static com.nets.upos.commons.validations.helper.DateValidationHelpers.validateMMddhhmmss;
import static com.nets.upos.commons.validations.helper.ObjectValidationHelpers.notNull;
import static com.nets.upos.commons.validations.helper.StringValidationHelpers.fieldNotExist;
import static com.nets.upos.commons.validations.helper.StringValidationHelpers.isEqualLength;
import static com.nets.upos.commons.validations.helper.StringValidationHelpers.lessThanOrEqualLength;
import static com.nets.upos.commons.validations.helper.StringValidationHelpers.notEmpty;
import static com.nets.upos.commons.validations.helper.StringValidationHelpers.numeric;
import org.springframework.stereotype.Service;
import com.nets.nps.paynow.entity.OrderRequest;
import com.nets.upos.commons.exception.JsonFormatException;
import com.nets.upos.commons.validations.MessageFormatValidationErrorCodes;

@Service
public class OrderRequestValidator {
	
	public OrderRequest validate(OrderRequest request) throws JsonFormatException {
		
		notEmpty.and(lessThanOrEqualLength(20)).test(request.getRetrievalRef()).throwIfInvalid(MessageFormatValidationErrorCodes.INVALID_RETRIVAL_REFERENCE_NUMBER);
		notEmpty.and(numeric.and(isEqualLength(11))).test(request.getInstitutionCode()).throwIfInvalid(MessageFormatValidationErrorCodes.INVALID_INSTITUTION_CODE);
		notEmpty.and(validateMMddhhmmss.and(isEqualLength(10))).test(request.getTransmissionTime()).throwIfInvalid(MessageFormatValidationErrorCodes.INVALID_TRANSMISSION_TIME);
		notEmpty.and(isEqualLength(3)).test(request.getChannelIndecator()).throwIfInvalid(MessageFormatValidationErrorCodes.INVALID_CHANNEL_INDICATOR);
		notEmpty.and(lessThanOrEqualLength(10)).test(request.getQrFormat()).throwIfInvalid(MessageFormatValidationErrorCodes.INVALID_QR_FORMAT);
		notEmpty.and(lessThanOrEqualLength(10)).test(request.getQrVersion()).throwIfInvalid(MessageFormatValidationErrorCodes.INVALID_QR_VERSION);
		
		notNull.test(request.getTransactionDomainData()).throwIfInvalid(MessageFormatValidationErrorCodes.INVALID_TXN_DOMAIN_DATA);
		numeric.and(isEqualLength(12)).test(request.getTransactionDomainData().getPaymentAmount()).throwIfInvalid(MessageFormatValidationErrorCodes.INVALID_PAYMENT_AMOUNT);
		notEmpty.and(isEqualLength(3)).test(request.getTransactionDomainData().getPaymentCurrency()).throwIfInvalid(MessageFormatValidationErrorCodes.INVALID_PAYMENT_CURRENCY);
		notEmpty.and(numeric.and(isEqualLength(6))).test(request.getTransactionDomainData().getTransactionTime()).throwIfInvalid(MessageFormatValidationErrorCodes.INVALID_TRANSACTION_TIME);
		notEmpty.and(numeric.and(isEqualLength(4))).test(request.getTransactionDomainData().getTransactionDate()).throwIfInvalid(MessageFormatValidationErrorCodes.INVALID_TRANSACTION_DATE);
		notEmpty.and(numeric.and(lessThanOrEqualLength(8))).test(request.getTransactionDomainData().getValidityTime()).throwIfInvalid(MessageFormatValidationErrorCodes.INVALID_VALIDITY_TIME);
		fieldNotExist.or(numeric.and(lessThanOrEqualLength(3))).test(request.getTransactionDomainData().getEntryMode()).throwIfInvalid(MessageFormatValidationErrorCodes.INVALID_ENTRY_MODE);
		fieldNotExist.or(numeric.and(isEqualLength(2))).test(request.getTransactionDomainData().getConditionCode()).throwIfInvalid(MessageFormatValidationErrorCodes.INVALID_CONDITION_CODE);
		
		fieldNotExist.or(isEqualLength(8)).test(request.getTransactionDomainData().getHostTid()).throwIfInvalid(MessageFormatValidationErrorCodes.INVALID_TID);
		fieldNotExist.or(lessThanOrEqualLength(15)).test(request.getTransactionDomainData().getHostMid()).throwIfInvalid(MessageFormatValidationErrorCodes.INVALID_MID);
		fieldNotExist.or(numeric.and(isEqualLength(16))).test(request.getTransactionDomainData().getNetsPaynowMpan()).throwIfInvalid(MessageFormatValidationErrorCodes.INVALID_NETS_PAYNOW_MPAN);
		fieldNotExist.or(lessThanOrEqualLength(100)).test(request.getTransactionDomainData().getBankMerchantProxy()).throwIfInvalid(MessageFormatValidationErrorCodes.INVALID_BANK_MERCHANT_PROXY);
		fieldNotExist.or(lessThanOrEqualLength(20)).test(request.getTransactionDomainData().getBankMerchantProxyType()).throwIfInvalid(MessageFormatValidationErrorCodes.INVALID_BANK_MERCHANT_PROXY_TYPE);
		fieldNotExist.or(lessThanOrEqualLength(40)).test(request.getTransactionDomainData().getMerchantReferenceNumber()).throwIfInvalid(MessageFormatValidationErrorCodes.INVALID_MERCHANT_REFERENCE_NUMBER);
		fieldNotExist.or(lessThanOrEqualLength(16)).test(request.getTransactionDomainData().getInvoiceRef()).throwIfInvalid(MessageFormatValidationErrorCodes.INVALID_INVOICE_REF);
		if(notNull.test(request.getTransactionDomainData().getNpxData()).isvalid()){
		notNull.test(request.getTransactionDomainData().getNpxData()).throwIfInvalid(MessageFormatValidationErrorCodes.MISSING_NPX_DATA);
		}
		return request;
	}


}
