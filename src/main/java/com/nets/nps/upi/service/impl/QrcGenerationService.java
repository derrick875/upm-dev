package com.nets.nps.upi.service.impl;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nets.nps.client.impl.UpiClient;
import com.nets.nps.core.service.PaymentService;
import com.nets.nps.upi.entity.QrcGenerationRequest;
import com.nets.nps.upi.entity.QrcGenerationResponse;
import com.nets.nps.upi.entity.UpiQrcGeneration;
import com.nets.nps.upi.entity.UpiQrcGenerationResponse;
import com.nets.nps.upi.service.DetokenizationAdapter;
import com.nets.nps.upi.service.UpiQrcGenerationService;
import com.nets.upos.commons.logger.ApsLogger;

@Service
public class QrcGenerationService implements PaymentService<QrcGenerationRequest, QrcGenerationResponse>{

	private static final ApsLogger logger = new ApsLogger(QrcGenerationService.class);

	@Autowired
	DetokenizationAdapter detokenizationAdapter;

	@Autowired
	private UpiClient upiClient;
	
	@Autowired
	private UpiQrcGenerationService upiQrcGenerationService;

	@Override
	public QrcGenerationResponse process(QrcGenerationRequest request) throws Exception {

		logger.info("QrcGenerationService Service Started.");
		request = detokenizationAdapter.detokenizationQrcGenerationRequest(request);
		UpiQrcGenerationResponse upiQrcGenerationResponse = upiClient.sendAndReceiveFromUpi(request);
		QrcGenerationResponse response = createQrcGenerationResponse(request, upiQrcGenerationResponse);
		saveUpiQrcGeneration(response, upiQrcGenerationResponse);
		
		return response;
	}

	private UpiQrcGeneration saveUpiQrcGeneration(QrcGenerationResponse response, UpiQrcGenerationResponse upiQrcGenerationResponse) {
		UpiQrcGeneration upiQrcGeneration = new UpiQrcGeneration();
		upiQrcGeneration.setCpqrcNo(upiQrcGenerationResponse.getTrxInfo().getCpqrcNo());
		upiQrcGeneration.setEmvCpqrcPayload(upiQrcGenerationResponse.getTrxInfo().getEmvCpqrcPayload());
		upiQrcGeneration.setBarcodeCpqrcPayload(upiQrcGenerationResponse.getTrxInfo().getBarcodeCpqrcPayload());
		upiQrcGeneration.setCpmqrPaymentToken(response.getCpmqrpaymentToken());
		upiQrcGeneration.setTransmissionTime(response.getTransmissionTime());
		upiQrcGeneration.setInstitutionCode(response.getInstitutionCode());
		
		upiQrcGenerationService.save(upiQrcGeneration);
		return upiQrcGeneration;
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
		response.setCpmqrpaymentToken(UUID.randomUUID().toString());
		return response;
	}
}
