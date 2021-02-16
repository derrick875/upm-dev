package com.nets.nps.upi.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nets.nps.upi.entity.CvmCheckEntity;

public interface CvmCheckRepository  extends JpaRepository<CvmCheckEntity, Long> {

	public CvmCheckEntity findByCpmQrPaymentToken(String cpmQrPaymentToken);
}
