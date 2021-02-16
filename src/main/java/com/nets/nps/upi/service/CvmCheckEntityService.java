package com.nets.nps.upi.service;

import org.springframework.stereotype.Service;

import com.nets.nps.upi.entity.CvmCheckEntity;

@Service
public interface CvmCheckEntityService {
	public CvmCheckEntity save(CvmCheckEntity cvmCheckEntity) ;
	public CvmCheckEntity getCvmCheckEntity(String cpmQrPaymentToken) ;
}
