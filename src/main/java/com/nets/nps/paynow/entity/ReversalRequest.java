package com.nets.nps.paynow.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.nets.nps.MtiRequestMapper;
import com.nets.nps.core.entity.Request;
import com.nets.upos.commons.logger.Loggable;

@JsonDeserialize(using = JsonDeserializer.None.class)
public class ReversalRequest extends Request implements Loggable {

	@JsonProperty("retrieval_ref")
	private String retrievalRef;
	
	@JsonProperty("institution_code")
	private String institutionCode;

	@JsonProperty("transmission_time")
	private String transmissionTime;
	
	@JsonProperty("channel_indicator")
	private String channelIndicator;
	
	@JsonProperty("transaction_enquiry_data")
	private ReversalTransactionEnquiryData transactionEnquiryData;	
	
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

	public ReversalTransactionEnquiryData getTransactionEnquiryData() {
		return transactionEnquiryData;
	}

	public void setTransactionEnquiryData(ReversalTransactionEnquiryData transactionEnquiryData) {
		this.transactionEnquiryData = transactionEnquiryData;
	}
	
	public String getChannelIndicator() {
		return channelIndicator;
	}

	public void setChannelIndicator(String channelIndicator) {
		this.channelIndicator = channelIndicator;
	}

	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("ReversalRequest [");
		buffer.append("retrievalRef=" + retrievalRef);
		buffer.append(", institutionCode=" + institutionCode);
		buffer.append(", channelIndicator=" + channelIndicator);
		buffer.append(", transmissionTime=" + transmissionTime);
		String transactionEnquiryDataLog = (transactionEnquiryData != null) ? transactionEnquiryData.log() : null;
		buffer.append(", transactionEnquiryData[" + transactionEnquiryDataLog + "]");
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
	
	@Override
	public ReversalResponse createResponse() {
		ReversalResponse response = new ReversalResponse();
		response.setMti(MtiRequestMapper.defaultReponseMti(this.getMti()));
		response.setProcessCode(this.getProcessCode());
		response.setInstitutionCode(this.getInstitutionCode());
		response.setRetrievalRef(this.getRetrievalRef());
		response.setTransmissionTime(this.getTransmissionTime());
		return response;
	}

}
