package com.nets.nps.paynow.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nets.nps.client.impl.UpiClient;
import com.nets.nps.core.service.PaymentService;
import com.nets.nps.paynow.entity.QrcGenerationRequest;
import com.nets.nps.paynow.entity.QrcGenerationResponse;
import com.nets.nps.paynow.entity.UpiQrcGenerationResponse;
import com.nets.nps.paynow.security.service.DetokenizationService;
import com.nets.upos.commons.logger.ApsLogger;

@Service
public class QrcGenerationService implements PaymentService<QrcGenerationRequest, QrcGenerationResponse>{

	private static final ApsLogger logger = new ApsLogger(QrcGenerationService.class);

	@Autowired
	private DetokenizationService detokenizationService;

	@Autowired
	private UpiClient upiClient;

	@Override
	public QrcGenerationResponse process(QrcGenerationRequest request) throws Exception {

		logger.info("QrcGenerationService Service Started.");
		request = detokenizationService.getTokenization(request);
		UpiQrcGenerationResponse upiQrcGenerationResponse = upiClient.sendAndReceiveFromUpi(request);
		QrcGenerationResponse response = createQrcGenerationResponse(request, upiQrcGenerationResponse);
		
		return response;
	}

	private QrcGenerationResponse createQrcGenerationResponse(QrcGenerationRequest request, UpiQrcGenerationResponse upiQrcGenerationResponse) {
		logger.info("Creating QrcGenerationResponse...");
		QrcGenerationResponse response = new QrcGenerationResponse();
		response.setRetrievalRef(request.getRetrievalRef());
		response.setMti("0310");
		response.setProcessCode(request.getProcessCode());
		response.setTransmissionTime(request.getTransmissionTime());
		response.setInstitutionCode(request.getInstitutionCode());
		response.setQrpayloadDataEmv(upiQrcGenerationResponse.getTrxInfo().getEmvCpqrcPayload());
		response.setQrpayloadDataAlternate(upiQrcGenerationResponse.getTrxInfo().getBarcodeCpqrcPayload());
		response.setCpmqrpaymentToken(request.getSofAccountId());
		return response;
	}









}
