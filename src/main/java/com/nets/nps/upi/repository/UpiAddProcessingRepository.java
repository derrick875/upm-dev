package com.nets.nps.upi.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nets.nps.upi.entity.AdditionalProcessingUpi;


public interface UpiAddProcessingRepository extends JpaRepository<AdditionalProcessingUpi, Long>{
	
	public AdditionalProcessingUpi findByEmvCpqrcPayload(String emvCpqrcPayload) ;
	public AdditionalProcessingUpi findByBarcodeCpqrcPayload(String barcodeCpqrcPayload) ;

}
