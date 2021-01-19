package com.nets.nps.upi.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nets.nps.client.impl.UpiClient;
import com.nets.nps.core.service.PaymentService;
import com.nets.nps.upi.entity.QrcGenerationRequest;
import com.nets.nps.upi.entity.QrcGenerationResponse;
import com.nets.nps.upi.entity.UpiQrcGenerationResponse;
import com.nets.nps.upi.service.DetokenizationAdapter;
import com.nets.upos.commons.logger.ApsLogger;

@Service
public class QrcGenerationService implements PaymentService<QrcGenerationRequest, QrcGenerationResponse>{

	private static final ApsLogger logger = new ApsLogger(QrcGenerationService.class);

	@Autowired
	DetokenizationAdapter detokenizationAdapter;

	@Autowired
	private UpiClient upiClient;

	@Override
	public QrcGenerationResponse process(QrcGenerationRequest request) throws Exception {

		logger.info("QrcGenerationService Service Started.");
		request = detokenizationAdapter.detokenizationQrcGenerationRequest(request);
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
//		response.setCpmqrpaymentToken(UUID.); use uuid 
		return response;
	}









}
