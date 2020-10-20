package com.nets.upos.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nets.upos.core.entity.PaynowTransactionData;

public interface PaynowTransactionDataRepository extends JpaRepository<PaynowTransactionData, Long> {

	public PaynowTransactionData findByRetrievalRef(String retrievalRef);

	public PaynowTransactionData findByTransactionId(Integer transactionId);

}
