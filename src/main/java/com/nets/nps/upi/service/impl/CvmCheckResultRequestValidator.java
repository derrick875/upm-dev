package com.nets.nps.upi.service.impl;

import static com.nets.upos.commons.validations.helper.DateValidationHelpers.validateMMddhhmmss;
import static com.nets.upos.commons.validations.helper.ObjectValidationHelpers.notNull;
import static com.nets.upos.commons.validations.helper.StringValidationHelpers.isEqualLength;
import static com.nets.upos.commons.validations.helper.StringValidationHelpers.lessThanOrEqualLength;
import static com.nets.upos.commons.validations.helper.StringValidationHelpers.notEmpty;
import static com.nets.upos.commons.validations.helper.StringValidationHelpers.numeric;
import static com.nets.upos.commons.validations.helper.StringValidationHelpers.fieldNotExist;
import org.springframework.stereotype.Service;
import static com.nets.upos.commons.validations.helper.StringValidationHelpers.alphanumeric;
import static com.nets.upos.commons.validations.helper.StringValidationHelpers.alpha;
import com.nets.nps.upi.entity.CvmCheckResultRequest;
import com.nets.nps.upi.entity.CvmCheckResultTransactionDomainData;
import com.nets.upos.commons.exception.BaseBusinessException;
import com.nets.upos.commons.exception.JsonFormatException;
import com.nets.upos.commons.validations.BusinessValidationErrorCodes;
import com.nets.upos.commons.validations.MessageFormatValidationErrorCodes;

@Service
public class CvmCheckResultRequestValidator {

	public CvmCheckResultRequest validate(CvmCheckResultRequest request) throws JsonFormatException, BaseBusinessException{
		notEmpty.and(lessThanOrEqualLength(20)).test(request.getRetrievalRef())
				.throwIfInvalid(MessageFormatValidationErrorCodes.INVALID_RETRIVAL_REFERENCE_NUMBER);
		notEmpty.and(numeric.and(isEqualLength(11))).test(request.getInstitutionCode()).throwIfInvalid(MessageFormatValidationErrorCodes.INVALID_INSTITUTION_CODE);
		notEmpty.and(numeric.and(isEqualLength(11))).test(request.getAcquirerInstitutionCode()).throwIfInvalid(MessageFormatValidationErrorCodes.INVALID_INSTITUTION_CODE);
		notEmpty.and(lessThanOrEqualLength(200)).test(request.getSofUri()).throwIfInvalid(MessageFormatValidationErrorCodes.INVALID_SOF_URI);
		notEmpty.and(validateMMddhhmmss.and(isEqualLength(10))).test(request.getTransmissionTime()).throwIfInvalid(MessageFormatValidationErrorCodes.INVALID_TRANSMISSION_TIME);
		notEmpty.and(lessThanOrEqualLength(80)).test(request.getSofAccountId()).throwIfInvalid(MessageFormatValidationErrorCodes.INVALID_SOF_ACCOUNT_ID);
		notNull.test(request.getTransactionDomainData()).throwIfInvalid(MessageFormatValidationErrorCodes.INVALID_TXN_DOMAIN_DATA); 

		CvmCheckResultTransactionDomainData transactionDomainData = request.getTransactionDomainData() ;
		String cvmPassedString = transactionDomainData.getCvmPassed() ;
		//test cvm_passed, ‘Y’ for accepted CVM, ‘N’ for rejected CVM
		notEmpty.and(alpha.and(isEqualLength(1))).test(cvmPassedString).throwIfInvalid();

		if(cvmPassedString =="Y") {
			notEmpty.and(lessThanOrEqualLength(80)).test(transactionDomainData.getCpmQrPaymentToken()).throwIfInvalid(BusinessValidationErrorCodes.VERIFICATION_TOKEN_TIMED_OUT);
//			notEmpty.and(lessThanOrEqualLength(80)).test(transactionDomainData.getCpmQrPaymentToken()).throwIfInvalid(BusinessValidationErrorCodes.QUALIFICATION_TOKEN_FAILED);
//			notEmpty.and(lessThanOrEqualLength(80)).test(transactionDomainData.getCpmQrPaymentToken()).throwIfInvalid(MessageFormatValidationErrorCodes.INVALID_QUALIFICATION_TOKEN);
//			notEmpty.and(lessThanOrEqualLength(80)).test(transactionDomainData.getCpmQrPaymentToken()).throwIfInvalid(MessageFormatValidationErrorCodes.INVALID_VERIFICATION_TOKEN);
		}
		else if(cvmPassedString =="N") {
			//test of cvm_rejection_reason - size 6, alphanumeric,  
			notEmpty.and(alphanumeric.and(isEqualLength(6))).test(transactionDomainData.getCvmRejectionReason()).throwIfInvalid();
			notEmpty.and(lessThanOrEqualLength(80)).test(transactionDomainData.getCpmQrPaymentToken()).throwIfInvalid(BusinessValidationErrorCodes.VERIFICATION_TOKEN_TIMED_OUT);
//			notEmpty.and(lessThanOrEqualLength(80)).test(transactionDomainData.getCpmQrPaymentToken()).throwIfInvalid(BusinessValidationErrorCodes.QUALIFICATION_TOKEN_FAILED);
//			notEmpty.and(lessThanOrEqualLength(80)).test(transactionDomainData.getCpmQrPaymentToken()).throwIfInvalid(MessageFormatValidationErrorCodes.INVALID_QUALIFICATION_TOKEN);
//			notEmpty.and(lessThanOrEqualLength(80)).test(transactionDomainData.getCpmQrPaymentToken()).throwIfInvalid(MessageFormatValidationErrorCodes.INVALID_VERIFICATION_TOKEN);
		}
			else {
				notEmpty.and(alpha.and(isEqualLength(1))).test(cvmPassedString).throwIfInvalid();
			}
		
		return request;
	}

}
