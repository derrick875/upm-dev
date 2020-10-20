package com.nets.nps.paynow.handlers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.nets.nps.MtiRequestMapper;
import com.nets.nps.core.entity.Request;
import com.nets.nps.core.entity.Response;
import com.nets.nps.paynow.RequestHandler;
import com.nets.nps.paynow.entity.ReversalRequest;
import com.nets.nps.paynow.entity.ReversalResponse;
import com.nets.nps.paynow.service.impl.PaynowReversalService;
import com.nets.nps.paynow.service.impl.ReversalRequestValidator;
import com.nets.upos.commons.exception.BaseBusinessException;
import com.nets.upos.commons.logger.ApsLogger;
import com.nets.upos.commons.validations.BusinessValidationErrorCodes;

@Component
public class ReversalHandler implements RequestHandler {

	private static final ApsLogger logger = new ApsLogger(RequestHandler.class);
	public static final String PAYNOW_CHANNEL = "PNT";

	@Autowired
	private ReversalRequestValidator reversalRequestValidator;

	@Autowired
	private PaynowReversalService paynowReversalService;

	@Override
	public Response handle(MtiRequestMapper mtiMapper, Request request) throws Exception {
		ReversalResponse response = null;

		logger.info("ReversalHandler service Started");
		ReversalRequest reversalReq = (ReversalRequest) request;
		reversalRequestValidator.validate(reversalReq);

		switch (reversalReq.getChannelIndicator()) {
		case PAYNOW_CHANNEL:
			response = paynowReversalService.process(reversalReq);

			break;
		default:
			throw new BaseBusinessException(BusinessValidationErrorCodes.CHANNEL_INDICATOR_NOT_SUPPORTED);
		}
		return response;
	}

}
