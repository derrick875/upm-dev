package com.nets.nps.upi.service;

import org.springframework.stereotype.Service;

import com.nets.nps.upi.entity.AdditionalProcessingUpi;



@Service
public interface UpiAddProcessingService {
	public AdditionalProcessingUpi save(AdditionalProcessingUpi additionalProcessingUpi);
	
	public AdditionalProcessingUpi getByEmvCpqrcPayload(String emvCpqrcPayload) ;

	public AdditionalProcessingUpi getByBarcodeCpqrcPayload(String barcodeCpqrcPayload) ;
	


}
