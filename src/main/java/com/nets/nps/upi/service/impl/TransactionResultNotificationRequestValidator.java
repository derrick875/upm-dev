package com.nets.nps.upi.service.impl;

import static com.nets.upos.commons.validations.helper.ObjectValidationHelpers.notNull;
import static com.nets.upos.commons.validations.helper.StringValidationHelpers.isEqualLength;
import static com.nets.upos.commons.validations.helper.StringValidationHelpers.lessThanOrEqualLength;
import static com.nets.upos.commons.validations.helper.StringValidationHelpers.notEmpty;
import static com.nets.upos.commons.validations.helper.StringValidationHelpers.numeric;
import static com.nets.upos.commons.validations.helper.StringValidationHelpers.fieldNotExist;
import static com.nets.upos.commons.validations.helper.StringValidationHelpers.between;

import org.springframework.stereotype.Service;

import com.nets.nps.upi.entity.MsgInfo;
import com.nets.nps.upi.entity.TransactionResultNotificationRequest;
import com.nets.nps.upi.entity.TransactionResultTrxInfo;
import com.nets.upos.commons.exception.JsonFormatException;
import com.nets.upos.commons.validations.MessageFormatValidationErrorCodes;

@Service
public class TransactionResultNotificationRequestValidator {
	
	public void validate(TransactionResultNotificationRequest request) throws JsonFormatException {
		
		MsgInfo msgInfo = request.getMsgInfo();
		notNull.test(msgInfo).throwIfInvalid(MessageFormatValidationErrorCodes.INVALID_AMOUNT); // add invalid msg info
		notEmpty.and(isEqualLength(5)).test(msgInfo.getVersionNo()).throwIfInvalid(MessageFormatValidationErrorCodes.INVALID_AMOUNT);// add invalid version number
		notEmpty.and(between(17, 49)).test(msgInfo.getMsgId()).throwIfInvalid(MessageFormatValidationErrorCodes.INVALID_AMOUNT); // add invalid msg id
		notEmpty.and(isEqualLength(14)).test(msgInfo.getTimeStamp()).throwIfInvalid(MessageFormatValidationErrorCodes.INVALID_TRANSMISSION_TIME); // add validate yyyyMMddhhmmss in date validation? add invalid timestamp?
		notEmpty.and(lessThanOrEqualLength(50)).test(msgInfo.getMsgType()).throwIfInvalid(MessageFormatValidationErrorCodes.INVALID_AMOUNT); //addd invalid msg type
		notEmpty.and(lessThanOrEqualLength(16)).test(msgInfo.getInsID()).throwIfInvalid(MessageFormatValidationErrorCodes.INVALID_INSTITUTION_CODE);
	
		TransactionResultTrxInfo trxInfo = request.getTrxInfo();
		notNull.test(trxInfo).throwIfInvalid(MessageFormatValidationErrorCodes.INVALID_AMOUNT); // add invalid trx info
		notEmpty.and(lessThanOrEqualLength(64)).test(trxInfo.getDeviceID()).throwIfInvalid(MessageFormatValidationErrorCodes.INVALID_AMOUNT); // add invalid device id
		notEmpty.and(between(16, 19)).test(trxInfo.getToken()).throwIfInvalid(MessageFormatValidationErrorCodes.INVALID_ADDITIONAL_BANK_REFERENCE); // add invalid token
		notEmpty.and(lessThanOrEqualLength(50)).test(trxInfo.getTrxMsgType()).throwIfInvalid(MessageFormatValidationErrorCodes.INVALID_TRANSACTION_TYPE); // add invalid transaction msg type?
//		fieldNotExist.and(between(17, 49)).test(trxInfo.getOrigMsgId()).throwIfInvalid(MessageFormatValidationErrorCodes.INVALID_ORIGINAL_AMOUNT); // add invalid original amount
//		fieldNotExist.and(lessThanOrEqualLength(50)).test(trxInfo.getOrigTrxType()).throwIfInvalid(MessageFormatValidationErrorCodes.INVALID_ORIGINAL_AMOUNT); // add invalid original trx type
//		fieldNotExist.test(trxInfo.getAccountInfo()).throwIfInvalid(MessageFormatValidationErrorCodes.INVALID_ADDITIONAL_BANK_REFERENCE); // add invalid account info
		notEmpty.and(lessThanOrEqualLength(13)).test(trxInfo.getTrxAmt()).throwIfInvalid(MessageFormatValidationErrorCodes.INVALID_AMOUNT); // add invalid trx amount?
		notEmpty.and(numeric.and(isEqualLength(3))).test(trxInfo.getTrxCurrency()).throwIfInvalid(MessageFormatValidationErrorCodes.INVALID_AMOUNT_CURRENCY); // add invalid trx currency
//		fieldNotExist.test(trxInfo.getDiscountDetails()).test
//		fieldNotExist.and(lessThanOrEqualLength(13)).test(trxInfo.getOriginalAmount()).throwIfInvalid(MessageFormatValidationErrorCodes.INVALID_ORIGINAL_AMOUNT);
//		fieldNotExist.and(lessThanOrEqualLength(120)).test(trxInfo.getTrxNote()).throwIfInvalid(MessageFormatValidationErrorCodes.INVALID_TARGET_AMOUNT); // add invalid trx note
//		fieldNotExist.and(lessThanOrEqualLength(40)).test(trxInfo.getMerchantName()).throwIfInvalid(MessageFormatValidationErrorCodes.INVALID_MERCHANT); // add invalid merchant name?
//		fieldNotExist.and(lessThanOrEqualLength(20)).test(trxInfo.getQrcVoucherNo()).throwIfInvalid(MessageFormatValidationErrorCodes.INVALID_QR_FORMAT); // add invalid qrc voucher
//		fieldNotExist.and(lessThanOrEqualLength(20)).test(trxInfo.getPaymentStatus()).throwIfInvalid(MessageFormatValidationErrorCodes.INVALID_PAYMENT_AMOUNT); // add invalid payment status
//		fieldNotExist.and(lessThanOrEqualLength(100)).test(trxInfo.getRejectionReason()).throwIfInvalid(MessageFormatValidationErrorCodes.INVALID_REBATE_AMOUNT); // add invalid rejection reason
//		fieldNotExist.and(lessThanOrEqualLength(50)).test(trxInfo.getReferNo()).throwIfInvalid(MessageFormatValidationErrorCodes.INVALID_REFUND_CURRENCY); // add invalid reference no	
	}
}
