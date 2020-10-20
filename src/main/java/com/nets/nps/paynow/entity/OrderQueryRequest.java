package com.nets.nps.paynow.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.nets.nps.MtiRequestMapper;
import com.nets.nps.core.entity.Request;
import com.nets.upos.commons.logger.Loggable;

@JsonDeserialize(using = JsonDeserializer.None.class)
public class OrderQueryRequest extends Request implements Loggable{

	@JsonProperty("retrieval_ref")
	private String retrievalRef;
	
	@JsonProperty("institution_code")
	private String institutionCode;

	@JsonProperty("transmission_time")
	private String transmissionTime;
	
	@JsonProperty("channel_indicator")
	private String channelIndicator;
	
	@JsonProperty("transaction_enquiry_data")
	private OrderQueryTransactionEnquiryData transactionEnquiryData;
	
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

	public String getChannelIndicator() {
		return channelIndicator;
	}

	public void setChannelIndicator(String channelIndicator) {
		this.channelIndicator = channelIndicator;
	}

	public OrderQueryTransactionEnquiryData getTransactionEnquiryData() {
		return transactionEnquiryData;
	}

	public void setTransactionEnquiryData(OrderQueryTransactionEnquiryData transactionEnquiryData) {
		this.transactionEnquiryData = transactionEnquiryData;
	}
	
	@Override
	public OrderQueryResponse createResponse() {
		OrderQueryResponse response = new OrderQueryResponse();
		response.setMti(MtiRequestMapper.defaultReponseMti(this.getMti()));
		response.setProcessCode(this.getProcessCode());
		response.setInstitutionCode(this.getInstitutionCode());
		response.setRetrievalRef(this.getRetrievalRef());
		response.setTransmissionTime(this.getTransmissionTime());
		return response;
	}
	
	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("OrderQueryRequest [");
		buffer.append("retrievalRef=" + retrievalRef);
		buffer.append(", institutionCode=" + institutionCode);
		buffer.append(", transmissionTime=" + transmissionTime);
		buffer.append(", channelIndicator=" + channelIndicator);
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
