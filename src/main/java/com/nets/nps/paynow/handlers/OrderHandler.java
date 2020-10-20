package com.nets.nps.paynow.handlers;

import static com.nets.upos.commons.validations.helper.StringValidationHelpers.isEqual;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.nets.nps.MtiRequestMapper;
import com.nets.nps.core.entity.Request;
import com.nets.nps.core.entity.Response;
import com.nets.nps.paynow.RequestHandler;
import com.nets.nps.paynow.entity.OrderInfo;
import com.nets.nps.paynow.entity.OrderPayloadData;
import com.nets.nps.paynow.entity.OrderRequest;
import com.nets.nps.paynow.entity.OrderResponse;
import com.nets.nps.paynow.service.impl.OrderRequestValidator;
import com.nets.nps.paynow.service.impl.PaynowOrderService;
import com.nets.upos.commons.constants.ResponseCodes;
import com.nets.upos.commons.exception.BaseBusinessException;
import com.nets.upos.commons.logger.ApsLogger;
import com.nets.upos.commons.validations.BusinessValidationErrorCodes;

@Component
public class OrderHandler implements RequestHandler {

	private static final ApsLogger logger = new ApsLogger(RequestHandler.class);
	public static final String PAYNOW_CHANNEL = "PNT";
	@Autowired
	private OrderRequestValidator orderRequestValidator;

	@Autowired
	private PaynowOrderService paynowOrderService;

	@Override
	public Response handle(MtiRequestMapper mtiMapper, Request request) throws Exception {
		OrderResponse response = null;

		logger.info("OrderHandler service Started");
		OrderRequest orderReq = (OrderRequest) request;
		orderRequestValidator.validate(orderReq);

		switch (orderReq.getChannelIndecator()) {
		case PAYNOW_CHANNEL:
			isEqual("PAYNOW").test(orderReq.getQrFormat().toUpperCase())
					.throwIfInvalid(BusinessValidationErrorCodes.QRFORMAT_NOT_SUPPORTED_FOR_CURRENT_CHANNEL);
			isEqual("SGD").test(orderReq.getTransactionDomainData().getPaymentCurrency().toUpperCase())
					.throwIfInvalid(BusinessValidationErrorCodes.CURRENCY_NOT_SUPPORTED);
			OrderInfo orderInfo = paynowOrderService.process(orderReq);
			response=orderReq.createResponse();
			response.setResponseCode(ResponseCodes.SUCCESS);
			OrderPayloadData ordPayloadData=new OrderPayloadData();
			ordPayloadData.setQrpayloadImage(orderInfo.getQrImage());
			ordPayloadData.setQrFormat(orderReq.getQrFormat());
			ordPayloadData.setMerchantReferenceNumber(orderInfo.getMerchantRef());
			response.setOrderPayloadData(ordPayloadData);
			break;
		default:
			throw new BaseBusinessException(BusinessValidationErrorCodes.CHANNEL_INDICATOR_NOT_SUPPORTED);
		}
		return response;
	}

}
