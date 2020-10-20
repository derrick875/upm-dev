package com.nets.nps.paynow.service;

import com.nets.upos.core.entity.PaynowTransactionData;

public interface PaynowTransactionDataService {
	
	public Long save(PaynowTransactionData payNowTxnDataEntity);
	
	public PaynowTransactionData getPaynowTransactionData(String retrievalRef);
	
	public PaynowTransactionData getPaynowTransactionData(Integer transactionId);
	
	public String getSequenceNumber(String seqName);

}
