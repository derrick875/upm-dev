package com.nets.nps.paynow.entity;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.nets.nps.core.entity.Response;

public class TransactionEnquiryResponse extends Response {
	
	@JsonProperty("response_code")
	private String responseCode;
	
	@JsonProperty("retrieval_ref")
	private String retrievalRef;

	@JsonProperty("transmission_time")
	private String transmissionTime;
	
	@JsonProperty("institution_code")
	private String institutionCode;
	
	@JsonProperty("transaction_enquiry_results")
	private List<TransactionEnquiryResults> transactionEnquiryResults;
	
	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("TransactionEnquiryResponse [");
		buffer.append("mti=" + mti);
		buffer.append(", processCode=" + processCode);
		buffer.append(", responseCode=" + responseCode);
		buffer.append(", retrievalRef=" + retrievalRef);
		buffer.append(", transmissionTime=" + transmissionTime);
		buffer.append(", institutionCode=" + institutionCode);
//		String transactionEnquiryResultsStr = (transactionEnquiryResults != null) ? transactionEnquiryResults.log() : null;
//		buffer.append(", transactionEnquiryResults[" + transactionEnquiryResultsStr + "]");
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
