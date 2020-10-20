package com.nets.nps.paynow.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.nets.nps.core.entity.Request;
import com.nets.upos.commons.logger.Loggable;

@JsonDeserialize(using = JsonDeserializer.None.class)
public class TransactionEnquiryRequest extends Request implements Loggable {
	
	@JsonProperty("retrieval_ref")
	private String retrievalRef;
	
	@JsonProperty("institution_code")
	private String institutionCode;
	
	@JsonProperty("transmission_time")
	private String transmissionTime;
	
	@JsonProperty("transaction_type")
	private String transactionType;
	
	@JsonProperty("transaction_enquiry_data")
	private TransactionEnquiryData transactionEnquiryData;
	
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

	public TransactionEnquiryData getTransactionEnquiryData() {
		return transactionEnquiryData;
	}

	public void setTransactionEnquiryData(TransactionEnquiryData transactionEnquiryData) {
		this.transactionEnquiryData = transactionEnquiryData;
	}

	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("TransactionEnquiryRequest [");
		buffer.append("retrievalRef=" + retrievalRef);
		buffer.append(", institutionCode=" + institutionCode);
		buffer.append(", transmissionTime=" + transmissionTime);
		buffer.append(", transactionType=" + transactionType);
		String transactionEnquiryDataLog = (transactionEnquiryData != null) ? transactionEnquiryData.log() : null;
		buffer.append(", transactionEnquiryData[" + transactionEnquiryDataLog + "]");
		buffer.append("]");
		return buffer.toString();
	}
	
	@Override
	public String log() {
		return toString();
	}

	@Override
	public String logKey() {
		return retrievalRef;
	}

}
