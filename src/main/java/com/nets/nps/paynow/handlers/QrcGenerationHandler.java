package com.nets.nps.paynow.handlers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.nets.nps.MtiRequestMapper;
import com.nets.nps.core.entity.Request;
import com.nets.nps.core.entity.Response;
import com.nets.nps.paynow.RequestHandler;
import com.nets.nps.paynow.entity.QrcGenerationRequest;
import com.nets.nps.paynow.entity.QrcGenerationResponse;
import com.nets.nps.paynow.service.impl.QRGenerationRequestValidator;
import com.nets.nps.paynow.service.impl.QrcGenerationService;
import com.nets.upos.commons.logger.ApsLogger;

@Component
public class QrcGenerationHandler implements RequestHandler {

	private static final ApsLogger logger = new ApsLogger(QrcGenerationHandler.class);
	
	@Autowired
	private QRGenerationRequestValidator qrGenerationRequestValidator;
	
	@Autowired
	private QrcGenerationService qrcGenerationService;
	
	@Override
	public Response handle(MtiRequestMapper mtiMapper, Request request) throws Exception {
		
		logger.info("QrGenerationHandler Service Started");
		QrcGenerationRequest qrGenerationReq = (QrcGenerationRequest) request;
		qrGenerationRequestValidator.validate(qrGenerationReq);
		
		QrcGenerationResponse response = qrcGenerationService.process(qrGenerationReq);

		return response;
	}

}
