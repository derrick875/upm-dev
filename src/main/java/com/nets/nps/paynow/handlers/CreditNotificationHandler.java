package com.nets.nps.paynow.handlers;

import java.text.ParseException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.nets.nps.MtiRequestMapper;
import com.nets.nps.core.entity.Request;
import com.nets.nps.core.entity.Response;
import com.nets.nps.paynow.RequestHandler;
import com.nets.nps.paynow.entity.CreditNotificationRequest;
import com.nets.nps.paynow.entity.CreditNotificationResponse;
import com.nets.nps.paynow.service.impl.CreditNotificationRequestValidator;
import com.nets.upos.commons.exception.BaseBusinessException;
import com.nets.upos.commons.exception.JsonFormatException;
import com.nets.upos.commons.logger.ApsLogger;
import com.nets.upos.commons.validations.BusinessValidationErrorCodes;

@Component
public class CreditNotificationHandler implements RequestHandler {
	
	public static final String PANOWQR = "PNC";
	private static final ApsLogger logger = new ApsLogger(CreditNotificationHandler.class);

	@Autowired
	private CreditNotificationRequestValidator creditNotificationRequestValidator;
	
	@Autowired
	private PaynowCreditNotificationHandler paynowQRHandler;

	@Override
	public Response handle(MtiRequestMapper mtiMapper, Request request)  throws JsonFormatException, BaseBusinessException, ParseException {
		
		logger.info("CreditNotificationHandler:Handle service Started");
		CreditNotificationRequest credNotiReq = (CreditNotificationRequest) request;
		creditNotificationRequestValidator.validate(credNotiReq);
		CreditNotificationResponse response;
		  switch (credNotiReq.getTransactionType()) {
          case PANOWQR:
        	  response=paynowQRHandler.handlePaynowQR(credNotiReq);
        	  break;
          default:
        	  throw new BaseBusinessException(BusinessValidationErrorCodes.TRANSACTION_TYPE_NOT_SUPPORTED);
              
      }
		return response;
	}
	
	
	
	
}
