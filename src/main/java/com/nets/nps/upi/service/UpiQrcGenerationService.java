package com.nets.nps.upi.service;

import org.springframework.stereotype.Service;

import com.nets.nps.upi.entity.UpiQrcGeneration;

@Service
public interface UpiQrcGenerationService {
	
	public UpiQrcGeneration save(UpiQrcGeneration upiQrcGeneration);
}
