package com.nets.nps.upi.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nets.nps.upi.entity.UpiQrcGeneration;
import com.nets.nps.upi.repository.UpiQrcGenerationRepository;
import com.nets.nps.upi.service.UpiQrcGenerationService;
import com.nets.upos.commons.logger.ApsLogger;

@Service
public class UpiQrcGenerationServiceImpl implements UpiQrcGenerationService {
	
	private static final ApsLogger logger = new ApsLogger(UpiQrcGenerationServiceImpl.class);
	
	@Autowired
	public UpiQrcGenerationRepository upiQrcGenerationRepository;
	
	@Override
	public UpiQrcGeneration save(UpiQrcGeneration upiQrcGeneration) {
		UpiQrcGeneration savedUpiQrcGeneration = upiQrcGenerationRepository.save(upiQrcGeneration);
		
		logger.info("Saved UpiQrcGeneration: " + upiQrcGeneration.log());
		return savedUpiQrcGeneration;
	}

	@Override
	public UpiQrcGeneration getUpiQrcGeneration(String emvCpqrcPayload) {
		return upiQrcGenerationRepository.findByEmvCpqrcPayload(emvCpqrcPayload)  ;
	}
}
