package com.nets.nps.paynow.handlers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.nets.nps.MtiRequestMapper;
import com.nets.nps.core.entity.Response;
import com.nets.nps.core.entity.Request;
import com.nets.nps.paynow.RequestHandler;
import com.nets.nps.paynow.entity.OrderQueryRequest;
import com.nets.nps.paynow.entity.OrderQueryResponse;
import com.nets.nps.paynow.service.impl.OrderQueryRequestValidator;
import com.nets.nps.paynow.service.impl.PaynowOrderQueryService;
import com.nets.upos.commons.exception.BaseBusinessException;
import com.nets.upos.commons.logger.ApsLogger;
import com.nets.upos.commons.validations.BusinessValidationErrorCodes;


@Component
public class OrderQueryHandler implements RequestHandler {

	private static final ApsLogger logger = new ApsLogger(OrderQueryHandler.class);
	public static final String PAYNOW_CHANNEL = "PNT";
	
	@Autowired
	private OrderQueryRequestValidator orderQueryRequestValidator;
	
	@Autowired
	private PaynowOrderQueryService paynowOrderQueryService;
	
	@Override
	public Response handle(MtiRequestMapper mtiMapper, Request request) throws Exception {
		OrderQueryResponse orderQueryResponse = null;
		
		logger.info("OrderQueryHandler service Started");
		OrderQueryRequest orderQueryReq = (OrderQueryRequest) request;
		orderQueryRequestValidator.validate(orderQueryReq);
		
		switch(orderQueryReq.getChannelIndicator()) {
		case PAYNOW_CHANNEL:
			orderQueryResponse = paynowOrderQueryService.process(orderQueryReq); 
			break;
		default:
			throw new BaseBusinessException(BusinessValidationErrorCodes.CHANNEL_INDICATOR_NOT_SUPPORTED);
		}
		

		return orderQueryResponse;
	}

}
