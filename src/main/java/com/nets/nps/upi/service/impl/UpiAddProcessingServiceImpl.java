package com.nets.nps.upi.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nets.nps.upi.entity.AdditionalProcessingUpi;
import com.nets.nps.upi.repository.UpiAddProcessingRepository;
import com.nets.nps.upi.service.UpiAddProcessingService;
import com.nets.upos.commons.logger.ApsLogger;

@Service
public class UpiAddProcessingServiceImpl implements UpiAddProcessingService{
	
	private static final ApsLogger logger = new ApsLogger(UpiAddProcessingServiceImpl.class);

	@Autowired
	public UpiAddProcessingRepository upiAddProcessingRepo;

	@Override
	public AdditionalProcessingUpi save(AdditionalProcessingUpi additionalProcessingUpi) {
		AdditionalProcessingUpi savedAdditionalProcessingUpiRequest = upiAddProcessingRepo.save(additionalProcessingUpi) ;
		logger.info("Saved AdditionalProcessingUpiRequest: " + savedAdditionalProcessingUpiRequest.toString() );

		return savedAdditionalProcessingUpiRequest;
	}

	@Override
	public AdditionalProcessingUpi getByEmvCpqrcPayload(String emvCpqrcPayload) {
		
		return upiAddProcessingRepo.findByEmvCpqrcPayload(emvCpqrcPayload) ;
	}

	@Override
	public AdditionalProcessingUpi getByBarcodeCpqrcPayload(String barcodeCpqrcPayload) {
		return upiAddProcessingRepo.findByBarcodeCpqrcPayload(barcodeCpqrcPayload);
	}

}
