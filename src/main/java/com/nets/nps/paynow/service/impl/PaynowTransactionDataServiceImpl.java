package com.nets.nps.paynow.service.impl;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nets.nps.paynow.service.PaynowTransactionDataService;
import com.nets.upos.core.entity.PaynowTransactionData;
import com.nets.upos.core.repository.PaynowTransactionDataRepository;
import com.nets.upos.core.repository.SequenceRepository;

@Service
@Transactional
public class PaynowTransactionDataServiceImpl implements PaynowTransactionDataService {

	@Autowired
	private PaynowTransactionDataRepository paynowTransactionDataRepository;

	@Autowired
	private SequenceRepository sequenceRepository;

	@Override
	public Long save(PaynowTransactionData payNowTxnDataEntity) {
		payNowTxnDataEntity = paynowTransactionDataRepository.saveAndFlush(payNowTxnDataEntity);
		return payNowTxnDataEntity.getId();
	}

	@Override
	public PaynowTransactionData getPaynowTransactionData(String retrievalRef) {
		return paynowTransactionDataRepository.findByRetrievalRef(retrievalRef);
	}

	@Override
	public String getSequenceNumber(String seqName) {
		return sequenceRepository.geSequence(seqName);
	}

	@Override
	public PaynowTransactionData getPaynowTransactionData(Integer transactionId) {
		return paynowTransactionDataRepository.findByTransactionId(transactionId);
	}

}
