package com.nets.nps.paynow.handlers;

import static com.nets.upos.commons.validations.helper.StringValidationHelpers.isEqual;
import static com.nets.upos.commons.validations.helper.StringValidationHelpers.moreThanOrEqualLength;
import static com.nets.upos.commons.validations.helper.StringValidationHelpers.notEmpty;

import java.text.ParseException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.nets.nps.paynow.entity.CreditNotificationRequest;
import com.nets.nps.paynow.entity.CreditNotificationResponse;
import com.nets.nps.paynow.service.impl.PaynowDynamicQrCreditNotificationService;
import com.nets.nps.paynow.service.impl.PaynowStaticQrCreditNotificationService;
import com.nets.upos.commons.exception.BaseBusinessException;
import com.nets.upos.commons.logger.ApsLogger;
import com.nets.upos.commons.validations.BusinessValidationErrorCodes;

@Component
public class PaynowCreditNotificationHandler {
	private static final ApsLogger logger = new ApsLogger(CreditNotificationHandler.class);
	public static final char SPACE = ' ';
	public static final char HYPHEN = '-';
	public static final char PERIOD = '.';

	@Autowired
	private PaynowStaticQrCreditNotificationService paynowStaticQrCreditNotificationService;

	@Autowired
	private PaynowDynamicQrCreditNotificationService paynowDynamicQrCreditNotificationService;

	public CreditNotificationResponse handlePaynowQR(CreditNotificationRequest request) throws BaseBusinessException, ParseException {

		CreditNotificationResponse response = null;

		notEmpty.and(moreThanOrEqualLength(22)).test(request.getTransactionDomainData().getMerchantReferenceNumber())
				.throwIfInvalid(BusinessValidationErrorCodes.INVALID_MERCHANT_REFERENCE);
		isEqual("SGD").test(request.getTransactionDomainData().getCreditCurrency().toUpperCase())
				.throwIfInvalid(BusinessValidationErrorCodes.CURRENCY_NOT_SUPPORTED);
		Character typeIndicator = request.getTransactionDomainData().getMerchantReferenceNumber().charAt(16);

		switch (typeIndicator) {
		case SPACE:
			response = paynowStaticQrCreditNotificationService.process(request);
			break;

		case HYPHEN:
			response = paynowDynamicQrCreditNotificationService.process(request);
			break;
		case PERIOD:
			break;

		default:
			throw new BaseBusinessException(BusinessValidationErrorCodes.INVALID_MERCHANT_REFERENCE);
		}
		return response;
	}

}
