package com.nets.nps.upi.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nets.nps.upi.entity.CvmCheckEntity;
import com.nets.nps.upi.repository.CvmCheckRepository;
import com.nets.nps.upi.service.CvmCheckEntityService;
import com.nets.upos.commons.logger.ApsLogger;

@Service
public class CvmCheckEntityServiceImpl implements CvmCheckEntityService{
	
	private static final ApsLogger logger = new ApsLogger(CvmCheckEntityServiceImpl.class);

	@Autowired
	public CvmCheckRepository cvmCheckRepo;

	@Override
	public CvmCheckEntity save(CvmCheckEntity cvmCheckEntity) {
		logger.info("Saved CvmCheckEntity: " + cvmCheckEntity.toString() );
		return cvmCheckRepo.save(cvmCheckEntity);
	}

	@Override
	public CvmCheckEntity getCvmCheckEntity(String cpmQrPaymentToken) {
		return cvmCheckRepo.findByCpmQrPaymentToken(cpmQrPaymentToken);
	}

}
