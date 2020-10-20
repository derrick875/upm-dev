package com.nets.nps.paynow.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.nets.nps.core.entity.Request;
import com.nets.upos.commons.logger.Loggable;

@JsonDeserialize(using = JsonDeserializer.None.class)
public class RefundReversalRequest extends Request implements Loggable {

	@JsonProperty("retrieval_ref")
	private String retrievalRef;
	
	@JsonProperty("institution_code")
	private String institutionCode;
	
	@JsonProperty("transmission_time")
	private String transmissionTime;
	
	@JsonProperty("transaction_type")
	private String transactionType;
	
	@JsonProperty("transaction_domain_data")
	private RefundReversalTransactionDomainData transactionDomainData;
			
	public String getRetrievalRef() {
		return retrievalRef;
	}

	public void setRetrievalRef(String retrievalRef) {
		this.retrievalRef = retrievalRef;
	}

	public String getInstitutionCode() {
		return institutionCode;
	}

	public void setInstitutionCode(String institutionCode) {
		this.institutionCode = institutionCode;
	}

	public String getTransmissionTime() {
		return transmissionTime;
	}

	public void setTransmissionTime(String transmissionTime) {
		this.transmissionTime = transmissionTime;
	}

	public String getTransactionType() {
		return transactionType;
	}

	public void setTransactionType(String transactionType) {
		this.transactionType = transactionType;
	}

	public RefundReversalTransactionDomainData getTransactionDomainData() {
		return transactionDomainData;
	}

	public void setTransactionDomainData(RefundReversalTransactionDomainData transactionDomainData) {
		this.transactionDomainData = transactionDomainData;
	}

	

	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("RefundReversalRequest [");
		buffer.append("retrievalRef=" + retrievalRef);
		buffer.append(", institutionCode=" + institutionCode);
		buffer.append(", transmissionTime=" + transmissionTime);
		buffer.append(", transactionType=" + transactionType);
		String transactionDomainDataLog = (transactionDomainData != null) ? transactionDomainData.log() : null;
		buffer.append(", transactionDomainData[" + transactionDomainDataLog + "]");
		buffer.append("]");
		return buffer.toString();
	}

	@Override
	public String log() {
		return this.toString();
	}

	@Override
	public String logKey() {
		return retrievalRef;
	}

}
